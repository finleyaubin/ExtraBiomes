"""Generate additional glacier snow-drift prefabs (smooth, wave-like, natural).

The two existing drifts (snow_drift_1/2) come from raw per-voxel hash noise,
which reads as spiky/jagged. These new drifts use a smooth height-field
built from a handful of summed sine waves (continuous in x/z, so no
per-voxel jaggedness) to get wind-sculpted dune/ridge/mound silhouettes:
asymmetric ramps, rippled crests, rounded mounds.

Follows the same technique as the other glacier structures: hand/procedurally
-authored .mcstructure prefabs, wrapped in minecraft:structure_template_feature,
picked from a weighted_random_feature pool (extrabiomes:glacier/select_snow_drift_feature),
scattered via feature_rules on the "frozen" biome tag.

Re-runnable: overwrites existing generated files. Does not touch snow_drift_1/2.
"""
import os, math
from mcstructure import (
    load, save, Tag, T_byte, T_int, T_str, T_comp, T_list,
    TAG_INT, TAG_LIST, TAG_COMPOUND, TAG_END,
)

HERE = os.path.dirname(os.path.abspath(__file__))
BP = os.path.join(HERE, "..", "ExtraBiomes - Bedrock", "packs", "BP")
STRUCT_DIR = os.path.join(BP, "structures", "extrabiomes")
FEATURE_DIR = os.path.join(BP, "features", "glacier")
NAMESPACE = "extrabiomes"

# borrow a palette 'version' int from the existing snow_drift_1 structure so blocks validate
_, _ref = load(os.path.join(STRUCT_DIR, "snow_drift_1.mcstructure"))
VERSION = (_ref.value["structure"].value["palette"].value["default"]
           .value["block_palette"].value[0].value["version"].value)


def palette_block(name, states=None):
    return T_comp({
        "name": T_str(name),
        "states": T_comp(states or {}),
        "version": T_int(VERSION),
    })


SNOW_BLOCK, ICE, BLUE_ICE, PACKED_ICE = "snow_block", "ice", "blue_ice", "packed_ice"
SNOW_LAYER_PREFIX = "layer"  # SNOW_LAYER_PREFIX + height(0..7) keys below


def snow_layer_key(height):
    return f"{SNOW_LAYER_PREFIX}{height}"


def base_palette_entries():
    entries = [
        palette_block("minecraft:snow"),        # SNOW_BLOCK -> index 0
        palette_block("minecraft:ice"),          # ICE -> index 1
        palette_block("minecraft:blue_ice"),     # BLUE_ICE -> index 2
        palette_block("minecraft:packed_ice"),   # PACKED_ICE -> index 3
    ]
    keys = [SNOW_BLOCK, ICE, BLUE_ICE, PACKED_ICE]
    for h in range(8):
        entries.append(palette_block("minecraft:snow_layer", {
            "covered_bit": T_byte(0),
            "height": T_int(h),
        }))
        keys.append(snow_layer_key(h))
    return keys, entries


PALETTE_KEYS, PALETTE_ENTRIES = base_palette_entries()
PALETTE_INDEX = {k: i for i, k in enumerate(PALETTE_KEYS)}


def make_structure(sx, sy, sz, cells):
    total = sx * sy * sz

    def idx(x, y, z):
        return x * sy * sz + y * sz + z

    layer0 = [Tag(TAG_INT, -1) for _ in range(total)]
    layer1 = [Tag(TAG_INT, -1) for _ in range(total)]
    for (x, y, z), key in cells.items():
        layer0[idx(x, y, z)] = Tag(TAG_INT, PALETTE_INDEX[key])
    return T_comp({
        "format_version": T_int(1),
        "size": T_list([T_int(sx), T_int(sy), T_int(sz)], TAG_INT),
        "structure": T_comp({
            "block_indices": T_list([T_list(layer0, TAG_INT), T_list(layer1, TAG_INT)], TAG_LIST),
            "entities": T_list([], TAG_END),
            "palette": T_comp({
                "default": T_comp({
                    "block_palette": T_list(PALETTE_ENTRIES, TAG_COMPOUND),
                    "block_position_data": T_comp({}),
                })
            }),
        }),
        "structure_world_origin": T_list([T_int(0), T_int(0), T_int(0)], TAG_INT),
    })


def finalize(cells):
    """Trim a sparse cells dict down to a tight bounding box starting at (0,0,0)."""
    xs = [c[0] for c in cells]
    ys = [c[1] for c in cells]
    zs = [c[2] for c in cells]
    x0, y0, z0 = min(xs), min(ys), min(zs)
    sx = max(xs) - x0 + 1
    sy = max(ys) - y0 + 1
    sz = max(zs) - z0 + 1
    out = {(x - x0, y - y0, z - z0): v for (x, y, z), v in cells.items()}
    return sx, sy, sz, out


def h32(*vals):
    n = 0
    mults = (374761393, 668265263, 2246822519, 3266489917, 668265263)
    for v, m in zip(vals, mults):
        n = (n + v * m) & 0xFFFFFFFF
    n = ((n ^ (n >> 13)) * 1274126177) & 0xFFFFFFFF
    return (n ^ (n >> 16)) & 0xFFFFFFFF


def rand01(seed, tag):
    return (h32(seed, tag) % 100000) / 100000.0


# ---------------------------------------------------------------------------
# smooth height field: sums of a few sine waves per structure (continuous in
# x/z, so surfaces are naturally smooth/wavy instead of per-voxel jagged)
# ---------------------------------------------------------------------------

def make_wave_terms(seed, n, base_freq, amp):
    """n deterministic sine terms: (freq, phase, amp) tuned around base_freq/amp."""
    terms = []
    for i in range(n):
        freq = base_freq * (0.6 + 1.4 * rand01(seed, 10 * i + 1))
        phase = rand01(seed, 10 * i + 2) * math.tau
        a = amp * (0.4 + 0.9 * rand01(seed, 10 * i + 3)) / n
        terms.append((freq, phase, a))
    return terms


def sum_waves(terms, t):
    return sum(a * math.sin(t * freq + phase) for freq, phase, a in terms)


def voxelize_height(heights, seed, ice_chance=0.05, ice_from_t=0.0):
    """heights: dict (x, z) -> float height (in blocks, can be fractional).
    Fills a solid snow column per (x,z) up to the height, capping the top
    with a partial snow_layer (1..8) for the fractional remainder, and
    speckling a few ice/blue_ice/packed_ice blocks low in the column for
    the glassy-core look the originals have."""
    cells = {}
    for (x, z), height in heights.items():
        if height <= 0:
            continue
        full = int(math.floor(height))
        frac = height - full
        for y in range(full):
            key = SNOW_BLOCK
            if y < full * 0.4 and rand01(seed, x * 131 + y * 977 + z * 7919) < ice_chance:
                r = rand01(seed, x * 733 + y * 331 + z * 991 + 17)
                key = PACKED_ICE if r < 0.5 else (BLUE_ICE if r < 0.8 else ICE)
            cells[(x, y, z)] = key
        layer_h = max(1, min(8, round(frac * 8))) if frac > 0.02 else 0
        if layer_h > 0:
            cells[(x, full, z)] = snow_layer_key(layer_h - 1)
        elif full == 0:
            cells[(x, 0, z)] = snow_layer_key(0)
    return cells


# ---------------------------------------------------------------------------
# shape generators
# ---------------------------------------------------------------------------

def build_ridge_dune(seed, length, width, peak_h):
    """Elongated wind-swept ridge: gentle windward ramp, steeper lee face,
    smooth rippled crest running along its length (sastrugi-style wave)."""
    crest_terms = make_wave_terms(seed, 3, base_freq=2.4 / max(1, length), amp=peak_h * 0.22)
    heights = {}
    for x in range(length):
        tx = x / max(1, length - 1)
        crest_wave = sum_waves(crest_terms, x)
        local_peak = max(0.6, peak_h + crest_wave)
        # asymmetric envelope along the length: gentle rise, gentle fall, broad middle plateau-ish
        env = math.sin(math.pi * tx) ** 0.6
        for z in range(width):
            tz = z / max(1, width - 1)
            # windward (tz small) gentle ramp; lee (tz large) steep drop-off
            if tz < 0.55:
                profile = (tz / 0.55) ** 0.8
            else:
                profile = (1 - (tz - 0.55) / 0.45) ** 1.6
            profile = max(0.0, profile)
            h = local_peak * env * profile
            if h > 0.15:
                heights[(x, z)] = h
    cells = voxelize_height(heights, seed, ice_chance=0.06)
    sx, sy, sz, cells = finalize(cells)
    return sx, sy, sz, cells


def build_rounded_mound(seed, radius, peak_h):
    """Big smooth rounded natural snow pile - dome profile with gentle
    organic undulation, minimal ice exposure (mostly banked snow)."""
    bump_terms = make_wave_terms(seed, 4, base_freq=3.0 / max(1, radius), amp=peak_h * 0.12)
    size = radius * 2 + 1
    heights = {}
    for x in range(size):
        for z in range(size):
            dx, dz = x - radius, z - radius
            d = math.sqrt(dx * dx + dz * dz) / radius
            if d > 1.05:
                continue
            base = peak_h * max(0.0, math.cos(min(1.0, d) * math.pi / 2)) ** 0.85
            bump = sum_waves(bump_terms, dx) * 0.5 + sum_waves(bump_terms, dz) * 0.5
            h = max(0.0, base + bump * (1 - d))
            if h > 0.15:
                heights[(x, z)] = h
    cells = voxelize_height(heights, seed, ice_chance=0.03)
    sx, sy, sz, cells = finalize(cells)
    return sx, sy, sz, cells


def build_s_wave_bank(seed, length, width, peak_h):
    """Snaking S-curve snowbank: two linked wave crests along a curved
    spine, natural drift-against-terrain look."""
    spine_terms = make_wave_terms(seed, 2, base_freq=2.0 / max(1, length), amp=width * 0.28)
    height_terms = make_wave_terms(seed + 1, 3, base_freq=3.2 / max(1, length), amp=peak_h * 0.25)
    half_w = width
    size_z = width * 2 + int(width * 0.6)
    heights = {}
    for x in range(length):
        tx = x / max(1, length - 1)
        spine_z = size_z / 2 + sum_waves(spine_terms, x)
        env = math.sin(math.pi * tx) ** 0.5
        local_peak = max(0.6, peak_h + sum_waves(height_terms, x)) * env
        for z in range(size_z):
            dz = (z - spine_z) / half_w
            if abs(dz) > 1.1:
                continue
            profile = max(0.0, math.cos(min(1.0, abs(dz)) * math.pi / 2)) ** 0.9
            h = local_peak * profile
            if h > 0.15:
                heights[(x, z)] = h
    cells = voxelize_height(heights, seed, ice_chance=0.05)
    sx, sy, sz, cells = finalize(cells)
    return sx, sy, sz, cells


def build_low_broad_drift(seed, size, peak_h):
    """Wide, low, gently rippled snow field - drifted snow banked thin
    across open ground rather than piled into a mound."""
    ripple_terms = make_wave_terms(seed, 5, base_freq=2.6 / max(1, size), amp=peak_h * 0.4)
    heights = {}
    cx = cz = size / 2
    for x in range(size):
        for z in range(size):
            dx, dz = x - cx, z - cz
            d = math.sqrt(dx * dx + dz * dz) / (size / 2)
            if d > 1.0:
                continue
            edge = max(0.0, 1 - d) ** 0.6
            ripple = peak_h * 0.55 + sum_waves(ripple_terms, x * 0.7 + z * 0.3)
            h = max(0.0, edge * ripple)
            if h > 0.15:
                heights[(x, z)] = h
    cells = voxelize_height(heights, seed, ice_chance=0.02)
    sx, sy, sz, cells = finalize(cells)
    return sx, sy, sz, cells


# ---------------------------------------------------------------------------
# emit .mcstructure + feature json
# ---------------------------------------------------------------------------

def write_structure(name, sx, sy, sz, cells):
    root = make_structure(sx, sy, sz, cells)
    save(os.path.join(STRUCT_DIR, f"{name}.mcstructure"), root, "")


def write_structure_feature(name):
    ident = f"{NAMESPACE}:glacier/{name}_feature"
    data = {
        "format_version": "1.14.0",
        "minecraft:structure_template_feature": {
            "description": {"identifier": ident},
            "structure_name": f"{NAMESPACE}:{name}",
            "constraints": {
                "unburied": {},
                "block_intersection": {
                    "block_allowlist": ["minecraft:air", "minecraft:snow_layer"]
                },
                "grounded": {},
            },
        },
    }
    import json
    with open(os.path.join(FEATURE_DIR, f"{name}_feature.json"), "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)


def write_select_feature(entries):
    """entries: list of (structure_name, weight); overwrites select_snow_drift_feature.json."""
    import json
    ident = f"{NAMESPACE}:glacier/select_snow_drift_feature"
    data = {
        "format_version": "1.14.0",
        "minecraft:weighted_random_feature": {
            "description": {"identifier": ident},
            "features": [[f"{NAMESPACE}:glacier/{n}_feature", w] for n, w in entries],
        },
    }
    with open(os.path.join(FEATURE_DIR, "select_snow_drift_feature.json"), "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)


# ---------------------------------------------------------------------------
# build everything
# ---------------------------------------------------------------------------

NEW_DRIFTS = [
    ("snow_drift_3", "ridge", 501, dict(length=24, width=10, peak_h=6)),
    ("snow_drift_4", "mound", 502, dict(radius=7, peak_h=6)),
    ("snow_drift_5", "s_wave", 503, dict(length=22, width=5, peak_h=5)),
    ("snow_drift_6", "low_broad", 504, dict(size=17, peak_h=4)),
]

BUILDERS = {
    "ridge": build_ridge_dune,
    "mound": build_rounded_mound,
    "s_wave": build_s_wave_bank,
    "low_broad": build_low_broad_drift,
}

if __name__ == "__main__":
    for name, kind, seed, kwargs in NEW_DRIFTS:
        sx, sy, sz, cells = BUILDERS[kind](seed, **kwargs)
        write_structure(name, sx, sy, sz, cells)
        write_structure_feature(name)
        print(f"{name}: {sx}x{sy}x{sz}, {len(cells)} blocks")

    # original ratio was snow_drift_1:2, snow_drift_2:1 (spiky originals);
    # new smooth/wave shapes get the majority share of the weighted pool.
    write_select_feature([
        ("snow_drift_1", 2),
        ("snow_drift_2", 1),
        ("snow_drift_3", 2),
        ("snow_drift_4", 2),
        ("snow_drift_5", 1),
        ("snow_drift_6", 2),
    ])
    print("\nupdated select_snow_drift_feature.json (6 entries)")
    print("done")
