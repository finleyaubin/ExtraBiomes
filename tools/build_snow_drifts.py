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
POWDER_SNOW, GRAVEL, ANDESITE, CALCITE = "powder_snow", "gravel", "andesite", "calcite"
SUMMIT_CHEST = "summit_chest"
SUMMIT_LOOT_TABLE = "loot_tables/chests/snow_spire_summit.json"
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
    # appended after the originals so drifts 3-6 keep their exact palette order
    for key in (POWDER_SNOW, GRAVEL, ANDESITE, CALCITE):
        entries.append(palette_block(f"minecraft:{key}"))
        keys.append(key)
    entries.append(palette_block("minecraft:chest", {"minecraft:cardinal_direction": T_str("south")}))
    keys.append(SUMMIT_CHEST)
    return keys, entries


PALETTE_KEYS, PALETTE_ENTRIES = base_palette_entries()
PALETTE_INDEX = {k: i for i, k in enumerate(PALETTE_KEYS)}


def make_structure(sx, sy, sz, cells):
    total = sx * sy * sz

    def idx(x, y, z):
        return x * sy * sz + y * sz + z

    layer0 = [Tag(TAG_INT, -1) for _ in range(total)]
    layer1 = [Tag(TAG_INT, -1) for _ in range(total)]
    block_entities = {}
    for (x, y, z), key in cells.items():
        layer0[idx(x, y, z)] = Tag(TAG_INT, PALETTE_INDEX[key])
        if key == SUMMIT_CHEST:
            block_entities[str(idx(x, y, z))] = T_comp({"block_entity_data": T_comp({
                "id": T_str("Chest"),
                "LootTable": T_str(SUMMIT_LOOT_TABLE),
            })})
    return T_comp({
        "format_version": T_int(1),
        "size": T_list([T_int(sx), T_int(sy), T_int(sz)], TAG_INT),
        "structure": T_comp({
            "block_indices": T_list([T_list(layer0, TAG_INT), T_list(layer1, TAG_INT)], TAG_LIST),
            "entities": T_list([], TAG_END),
            "palette": T_comp({
                "default": T_comp({
                    "block_palette": T_list(PALETTE_ENTRIES, TAG_COMPOUND),
                    "block_position_data": T_comp(block_entities),
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


def voxelize_height(heights, seed, ice_chance=0.05, ice_from_t=0.0, powder_chance=0.0, scour_slope=None):
    """heights: dict (x, z) -> float height (in blocks, can be fractional).
    Fills a solid snow column per (x,z) up to the height, capping the top
    with a partial snow_layer (1..8) for the fractional remainder, and
    speckling a few ice/blue_ice/packed_ice blocks low in the column for
    the glassy-core look the originals have.

    powder_chance hides powder snow just under the crust of thick columns;
    scour_slope exposes packed/blue ice on faces steeper than that many
    blocks per block, where wind would strip the loose snow off."""
    cells = {}
    for (x, z), height in heights.items():
        if height <= 0:
            continue
        full = int(math.floor(height))
        frac = height - full
        scoured = scour_slope is not None and full >= 1 and steepest_drop(heights, x, z) > scour_slope
        for y in range(full):
            key = SNOW_BLOCK
            if y < full * 0.4 and rand01(seed, x * 131 + y * 977 + z * 7919) < ice_chance:
                r = rand01(seed, x * 733 + y * 331 + z * 991 + 17)
                key = PACKED_ICE if r < 0.5 else (BLUE_ICE if r < 0.8 else ICE)
            elif full >= 3 and y == full - 2 and rand01(seed, x * 419 + y * 613 + z * 283 + 5) < powder_chance:
                key = POWDER_SNOW
            cells[(x, y, z)] = key
        if scoured:
            cells[(x, full - 1, z)] = BLUE_ICE if rand01(seed, x * 97 + z * 389 + 23) < 0.3 else PACKED_ICE
            continue
        layer_h = max(1, min(8, round(frac * 8))) if frac > 0.02 else 0
        if layer_h > 0:
            cells[(x, full, z)] = snow_layer_key(layer_h - 1)
        elif full == 0:
            cells[(x, 0, z)] = snow_layer_key(0)
    return cells


def steepest_drop(heights, x, z):
    h = heights[(x, z)]
    return max(h - heights.get((x + dx, z + dz), 0.0) for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)))


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


def build_cornice_ridge(seed, length, width, peak_h, lip):
    """Ridge whose crest curls over its steep lee face as an overhanging
    cornice lip, like wind-loaded snow on a ridgeline."""
    crest_terms = make_wave_terms(seed, 3, base_freq=2.2 / max(1, length), amp=peak_h * 0.25)
    crest_z = int(width * 0.7)
    heights = {}
    for x in range(length):
        tx = x / max(1, length - 1)
        env = math.sin(math.pi * tx) ** 0.5
        local_peak = max(0.6, peak_h + sum_waves(crest_terms, x)) * env
        for z in range(width):
            if z <= crest_z:
                profile = (z / crest_z) ** 1.3
            else:
                profile = max(0.0, 1 - (z - crest_z) / (width - crest_z)) ** 3
            h = local_peak * profile
            if h > 0.15:
                heights[(x, z)] = h
    cells = voxelize_height(heights, seed, ice_chance=0.04, powder_chance=0.08, scour_slope=1.6)
    for x in range(length):
        top = int(heights.get((x, crest_z), 0))
        reach = round(lip * math.sin(math.pi * x / max(1, length - 1)))
        for dz in range(1, reach + 1):
            for y in (top - 1, top - 2):
                if y >= 1 and heights.get((x, crest_z + dz), 0) < y:
                    cells[(x, y, crest_z + dz)] = SNOW_BLOCK
    return finalize(cells)


def build_sastrugi_field(seed, length, width, peak_h, angle):
    """Low field of parallel knife-edged ridges carved by a steady wind,
    with scoured blue/packed ice showing in the troughs."""
    wobble = make_wave_terms(seed, 3, base_freq=2.0 / max(1, length), amp=1.4)
    ux, uz = math.cos(angle), math.sin(angle)
    heights = {}
    cx, cz = length / 2, width / 2
    for x in range(length):
        for z in range(width):
            ex, ez = (x - cx) / cx, (z - cz) / cz
            edge = max(0.0, 1 - (ex * ex + ez * ez)) ** 0.5
            if edge <= 0:
                continue
            t = x * ux + z * uz + sum_waves(wobble, x * uz - z * ux)
            crest = (1 - abs(math.sin(t * 0.45))) ** 3
            heights[(x, z)] = max(0.2, peak_h * crest * edge + 0.3 * edge)
    cells = voxelize_height(heights, seed, ice_chance=0.02, scour_slope=2.2)
    for (x, z), h in heights.items():
        if h < 0.35 and rand01(seed, x * 53 + z * 811) < 0.35:
            cells[(x, 0, z)] = BLUE_ICE if rand01(seed, x * 17 + z * 29 + 3) < 0.35 else PACKED_ICE
    return finalize(cells)


def build_barchan(seed, radius, peak_h):
    """Crescent dune: a rounded windward back with two horns trailing
    downwind around a steep hollow slip face."""
    size = radius * 3
    cx, cz = radius * 1.2, size / 2
    bump_terms = make_wave_terms(seed, 3, base_freq=2.5 / radius, amp=peak_h * 0.15)
    heights = {}
    for x in range(size):
        for z in range(size):
            dx, dz = (x - cx) / radius, (z - cz) / radius
            outer = math.sqrt(dx * dx + dz * dz)
            inner = math.sqrt((dx - 0.75) ** 2 + dz * dz) / 0.85
            if outer > 1.0 or inner < 1.0:
                horn = abs(dz) > 0.55 and dx > 0 and outer < 1.0 + 0.9 * dx and inner >= 1.0
                if not horn:
                    continue
            body = max(0.0, 1 - outer) ** 0.7 if outer <= 1.0 else 0.0
            horn_h = max(0.0, 0.35 - 0.2 * dx) * max(0.0, 1.3 - abs(abs(dz) - 0.8) * 3)
            rim = min(1.0, (inner - 1.0) * 2.5)
            h = (peak_h * max(body, horn_h) + sum_waves(bump_terms, x + z) * 0.4) * max(0.0, rim)
            if h > 0.15:
                heights[(x, z)] = h
    cells = voxelize_height(heights, seed, ice_chance=0.03, powder_chance=0.06, scour_slope=1.8)
    return finalize(cells)


def build_moraine_drift(seed, radius, peak_h, rocks):
    """Snow banked against glacial debris: a lumpy mound with half-buried
    andesite/calcite boulders and a gravel skirt at its toe."""
    sx, sy, sz, cells = build_rounded_mound(seed, radius, peak_h)
    cells = dict(cells)
    heights = {}
    for (x, y, z) in cells:
        heights[(x, z)] = max(heights.get((x, z), 0), y + 1)
    for (x, z), h in heights.items():
        if h <= 1 and rand01(seed, x * 71 + z * 887 + 9) < 0.45:
            cells[(x, 0, z)] = GRAVEL
    columns = sorted(heights)
    for i in range(rocks):
        bx, bz = columns[h32(seed, 900 + i) % len(columns)]
        r = 1.2 + rand01(seed, 910 + i) * 1.3
        stone = CALCITE if rand01(seed, 920 + i) < 0.35 else ANDESITE
        ir = int(math.ceil(r))
        for dx in range(-ir, ir + 1):
            for dz in range(-ir, ir + 1):
                for y in range(0, ir + 1):
                    if dx * dx + dz * dz + (y * 1.4) ** 2 <= r * r:
                        cells[(bx + dx, y, bz + dz)] = stone
    return finalize(cells)


def build_giant_swirl(seed, radius, peak_h, arms, twist):
    """Very large wind vortex: spiral snow arms coiling out from a scoured
    blue-ice eye, thinning and flattening toward the rim."""
    wobble = make_wave_terms(seed, 4, base_freq=3.0 / radius, amp=0.35)
    size = radius * 2 + 1
    heights = {}
    eye_r = radius * 0.14
    for x in range(size):
        for z in range(size):
            dx, dz = x - radius, z - radius
            r = math.sqrt(dx * dx + dz * dz)
            if r > radius:
                continue
            theta = math.atan2(dz, dx)
            phase = arms * (theta - twist * math.log(1 + r)) + sum_waves(wobble, r)
            arm = ((math.cos(phase) + 1) / 2) ** 1.8
            radial = math.sin(math.pi * min(1.0, max(0.0, (r - eye_r) / (radius - eye_r)))) ** 0.7
            rim_fade = max(0.0, 1 - r / radius) ** 0.35
            h = peak_h * arm * radial + 0.6 * rim_fade * (r > eye_r)
            if r <= eye_r:
                h = 0.0
            heights[(x, z)] = h
    cells = voxelize_height({k: v for k, v in heights.items() if v > 0.15}, seed,
                            ice_chance=0.05, powder_chance=0.1, scour_slope=1.5)
    for (x, z), h in heights.items():
        r = math.sqrt((x - radius) ** 2 + (z - radius) ** 2)
        if r <= eye_r + 1:
            roll = rand01(seed, x * 37 + z * 541 + 11)
            cells[(x, 0, z)] = BLUE_ICE if roll < 0.45 else (PACKED_ICE if roll < 0.85 else ICE)
    return finalize(cells)


def surface_cells(solid):
    """Cells of a solid set with at least one exposed face."""
    return {c for c in solid
            if any((c[0] + dx, c[1] + dy, c[2] + dz) not in solid
                   for dx, dy, dz in ((1, 0, 0), (-1, 0, 0), (0, 1, 0), (0, -1, 0), (0, 0, 1), (0, 0, -1)))}


def skin_solid(solid, seed, band=None):
    """Snow-block body with packed/blue ice speckled through the core and an
    optional ice band on the surface; exposed tops get a snow layer cap."""
    surface = surface_cells(solid)
    cells = {}
    for (x, y, z) in solid:
        key = SNOW_BLOCK
        if (x, y, z) in surface:
            if band is not None and band(x, y, z):
                key = BLUE_ICE if rand01(seed, x * 61 + y * 409 + z * 877) < 0.6 else PACKED_ICE
        elif rand01(seed, x * 131 + y * 977 + z * 7919) < 0.05:
            key = PACKED_ICE
        cells[(x, y, z)] = key
    for (x, y, z) in surface:
        if (x, y + 1, z) not in solid and cells[(x, y, z)] == SNOW_BLOCK:
            cells[(x, y + 1, z)] = snow_layer_key(int(rand01(seed, x * 7 + z * 13 + y) * 4))
    return cells


def build_twisting_spire(seed, height, radius, turns, lean, summit_chest=False):
    """Tall wind-twisted spire: elliptical slices that shrink, drift along a
    helix and rotate with height, wrapped in a spiral blue-ice band."""
    solid = set()
    size = radius * 2 + lean * 2 + 3
    c = size / 2
    for y in range(height):
        t = y / (height - 1)
        r = radius * (1 - t) ** 0.75 + 0.8
        spin = math.tau * turns * t
        cx, cz = c + lean * t * math.cos(spin), c + lean * t * math.sin(spin)
        a, b = r * 1.25, r * 0.8
        for x in range(size):
            for z in range(size):
                dx, dz = x - cx, z - cz
                u = dx * math.cos(spin) + dz * math.sin(spin)
                v = -dx * math.sin(spin) + dz * math.cos(spin)
                if (u / a) ** 2 + (v / b) ** 2 <= 1.0:
                    solid.add((x, y, z))

    def band(x, y, z):
        angle = math.atan2(z - c, x - c)
        phase = (angle - math.tau * turns * 1.5 * y / height) % math.tau
        return phase < 0.9

    cells = skin_solid(solid, seed, band)
    if summit_chest:
        top_y = max(y for (_, y, _) in solid)
        x, _, z = min((c for c in solid if c[1] == top_y), key=lambda c: (c[0], c[2]))
        cells[(x, top_y + 1, z)] = SUMMIT_CHEST
    return finalize(cells)


def build_breaking_wave(seed, length, height, depth):
    """Tall snow wave frozen mid-break: a steep face rising to a crest that
    curls forward over a hollow barrel, tapering at both ends."""
    crest_terms = make_wave_terms(seed, 3, base_freq=2.0 / length, amp=height * 0.15)
    solid = set()
    for x in range(length):
        env = math.sin(math.pi * x / (length - 1)) ** 0.6
        h = max(2.0, (height + sum_waves(crest_terms, x)) * env)
        crest_z = depth * 0.55
        curl_r = h * 0.35
        curl_cz, curl_cy = crest_z + curl_r * 0.2, h - curl_r
        thickness = max(1.2, curl_r * 0.45)
        for z in range(int(depth + curl_r + 2)):
            for y in range(int(h) + 1):
                back = z <= crest_z and y <= h * (z / crest_z) ** 0.7
                dz, dy = z - curl_cz, y - curl_cy
                dist = math.hypot(dz, dy)
                angle = math.atan2(dy, dz)
                lip = curl_r - thickness <= dist <= curl_r and -0.6 <= angle <= math.pi / 2 + 0.3
                if back or lip:
                    solid.add((x, y, z))
    return finalize(skin_solid(solid, seed, band=lambda x, y, z: y <= 1 and rand01(seed, x * 3 + z) < 0.3))


def build_spiral_cone(seed, radius, peak_h, arms, twist):
    """Tall cone of snow with spiral grooves winding up to its peak, like a
    frozen whirlwind."""
    size = radius * 2 + 1
    heights = {}
    for x in range(size):
        for z in range(size):
            dx, dz = x - radius, z - radius
            r = math.sqrt(dx * dx + dz * dz)
            if r > radius:
                continue
            theta = math.atan2(dz, dx)
            arm = (math.cos(arms * (theta - twist * r / radius * math.tau)) + 1) / 2
            cone = (1 - r / radius) ** 1.15
            h = peak_h * cone * (0.62 + 0.38 * arm) + 0.4
            heights[(x, z)] = h
    cells = voxelize_height(heights, seed, ice_chance=0.05, powder_chance=0.06, scour_slope=2.4)
    return finalize(cells)


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
    ("snow_drift_7", "cornice", 505, dict(length=22, width=11, peak_h=6, lip=3)),
    ("snow_drift_8", "sastrugi", 506, dict(length=24, width=18, peak_h=4, angle=0.5)),
    ("snow_drift_9", "barchan", 507, dict(radius=8, peak_h=6)),
    ("snow_drift_10", "moraine", 508, dict(radius=7, peak_h=5, rocks=4)),
    ("snow_drift_11", "swirl", 509, dict(radius=16, peak_h=7, arms=2, twist=1.6)),
    ("snow_drift_12", "spire", 510, dict(height=18, radius=6, turns=0.75, lean=3)),
    ("snow_drift_13", "wave", 511, dict(length=20, height=14, depth=9)),
    ("snow_drift_14", "cone", 512, dict(radius=11, peak_h=16, arms=3, twist=0.6)),
    ("snow_drift_15", "spire", 513, dict(height=40, radius=11, turns=1.25, lean=4)),
    ("snow_drift_16", "spire", 514, dict(height=150, radius=12, turns=3, lean=3, summit_chest=True)),
]

BUILDERS = {
    "ridge": build_ridge_dune,
    "mound": build_rounded_mound,
    "s_wave": build_s_wave_bank,
    "low_broad": build_low_broad_drift,
    "cornice": build_cornice_ridge,
    "sastrugi": build_sastrugi_field,
    "barchan": build_barchan,
    "moraine": build_moraine_drift,
    "swirl": build_giant_swirl,
    "spire": build_twisting_spire,
    "wave": build_breaking_wave,
    "cone": build_spiral_cone,
}

SELECT_WEIGHTS = [
    ("snow_drift_1", 20), ("snow_drift_2", 10), ("snow_drift_3", 20), ("snow_drift_4", 20),
    ("snow_drift_5", 10), ("snow_drift_6", 20), ("snow_drift_7", 20), ("snow_drift_8", 20),
    ("snow_drift_9", 20), ("snow_drift_10", 10), ("snow_drift_11", 10), ("snow_drift_12", 10),
    ("snow_drift_13", 10), ("snow_drift_14", 10), ("snow_drift_15", 6), ("snow_drift_16", 3),
]

JAVA_STRUCT_DIR = os.path.join(HERE, "..", "ExtraBiomes - Java", "common", "src", "main", "resources",
                               "data", NAMESPACE, "structure", "glacier")


def write_java_structure(name, sx, sy, sz, cells):
    import tempfile
    from collections import Counter
    import mc2java
    with tempfile.TemporaryDirectory() as tmp:
        src = os.path.join(tmp, f"{name}.mcstructure")
        save(src, make_structure(sx, sy, sz, cells), "")
        warnings = []
        mc2java.convert_one(src, os.path.join(JAVA_STRUCT_DIR, f"{name}.nbt"), warnings, Counter())
        for w in warnings:
            print(f"  warning: {w}")


if __name__ == "__main__":
    import sys
    # --java writes Java .nbt only, leaving the Bedrock pack untouched (Bedrock changes stay off Java branches)
    java_only = "--java" in sys.argv[1:]
    for name, kind, seed, kwargs in NEW_DRIFTS:
        sx, sy, sz, cells = BUILDERS[kind](seed, **kwargs)
        if java_only:
            write_java_structure(name, sx, sy, sz, cells)
        else:
            write_structure(name, sx, sy, sz, cells)
            write_structure_feature(name)
        print(f"{name}: {sx}x{sy}x{sz}, {len(cells)} blocks")

    if not java_only:
        write_select_feature(SELECT_WEIGHTS)
        print(f"\nupdated select_snow_drift_feature.json ({len(SELECT_WEIGHTS)} entries)")
    print("done")
