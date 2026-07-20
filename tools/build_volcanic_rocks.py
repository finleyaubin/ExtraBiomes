"""Generate blackstone rock-formation prefabs for the volcanic moss tundra.

Follows the same technique already used by the jungle_pillars biome
(structures/extrabiomes/stone_pillar_*.mcstructure): hand/procedurally-authored
.mcstructure prefabs, each wrapped in a minecraft:structure_template_feature,
picked from weighted_random_feature pools, and scattered via feature_rules
biome_filter'd on the shared "volcanic_moss_tundra" / "mountain" biome tags.

Produces 20 structures: 6 rocky pillars, 8 nobbly boulders, 4 elephant-rock
formations, 2 rare large volcanoes. All blackstone, with cracked/polished
speckle and a moss cap for weathering (ties into the biome's moss theme).

Re-runnable: overwrites existing generated files.
"""
import os, math
from mcstructure import (
    load, save, Tag, T_int, T_str, T_comp, T_list,
    TAG_INT, TAG_LIST, TAG_COMPOUND, TAG_END,
)

HERE = os.path.dirname(os.path.abspath(__file__))
BP = os.path.join(HERE, "..", "ExtraBiomes - Bedrock", "packs", "BP")
STRUCT_DIR = os.path.join(BP, "structures", "extrabiomes", "volcanic_moss_tundra")
FEATURE_DIR = os.path.join(BP, "features", "volcanic_moss_tundra")
RULES_DIR = os.path.join(BP, "feature_rules", "volcanic_moss_tundra")
NAMESPACE = "extrabiomes"
STRUCT_PREFIX = "volcanic_moss_tundra"  # extrabiomes:volcanic_moss_tundra/<name>

os.makedirs(STRUCT_DIR, exist_ok=True)
os.makedirs(FEATURE_DIR, exist_ok=True)
os.makedirs(RULES_DIR, exist_ok=True)

# borrow a palette 'version' int from an existing structure so blocks validate
_, _ref = load(os.path.join(BP, "structures", "extrabiomes", "stone_pillar_1.mcstructure"))
VERSION = (_ref.value["structure"].value["palette"].value["default"]
           .value["block_palette"].value[0].value["version"].value)


def h32(x, y, z, seed):
    n = (x * 374761393 + y * 668265263 + z * 2246822519 + seed * 3266489917) & 0xFFFFFFFF
    n = ((n ^ (n >> 13)) * 1274126177) & 0xFFFFFFFF
    return (n ^ (n >> 16)) & 0xFFFFFFFF


def noise(x, y, z, seed):
    """Deterministic build-time noise in [-0.5, 0.5)."""
    return (h32(x, y, z, seed) % 10000) / 10000.0 - 0.5


def palette_block(name, states=None):
    return T_comp({
        "name": T_str(name),
        "states": T_comp(states or {}),
        "version": T_int(VERSION),
    })


BLACKSTONE, POLISHED, CRACKED, MOSS, MAGMA, LAVA = range(6)


def base_palette():
    return [
        palette_block("minecraft:blackstone"),
        palette_block("minecraft:polished_blackstone"),
        palette_block("minecraft:cracked_polished_blackstone_bricks"),
        palette_block("minecraft:moss_block"),
        palette_block("minecraft:magma"),
        palette_block("minecraft:lava", {"liquid_depth": T_int(0)}),
    ]


def make_structure(sx, sy, sz, cells):
    total = sx * sy * sz

    def idx(x, y, z):
        return x * sy * sz + y * sz + z

    layer0 = [Tag(TAG_INT, -1) for _ in range(total)]
    layer1 = [Tag(TAG_INT, -1) for _ in range(total)]
    for (x, y, z), pidx in cells.items():
        layer0[idx(x, y, z)] = Tag(TAG_INT, pidx)
    return T_comp({
        "format_version": T_int(1),
        "size": T_list([T_int(sx), T_int(sy), T_int(sz)], TAG_INT),
        "structure": T_comp({
            "block_indices": T_list([T_list(layer0, TAG_INT), T_list(layer1, TAG_INT)], TAG_LIST),
            "entities": T_list([], TAG_END),
            "palette": T_comp({
                "default": T_comp({
                    "block_palette": T_list(base_palette(), TAG_COMPOUND),
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


def add_embed(cells, embed):
    """Solid blackstone plug under the shape's footprint (y==0 layer) so it
    merges into whatever terrain sits beneath it, regardless of ground shape."""
    footprint = {(x, z) for (x, y, z) in cells if y == 0}
    shifted = {(x, y + embed, z): v for (x, y, z), v in cells.items()}
    for x, z in footprint:
        for y in range(embed):
            shifted[(x, y, z)] = BLACKSTONE
    return shifted


def weather(cells, seed, moss_from_t=0.75, moss_chance=0.35, crack_chance=0.05):
    """Speckle in cracked_polished_blackstone and cap the upper surface in moss,
    matching the biome's moss theme without changing the silhouette."""
    ys = [y for (_, y, _) in cells]
    y0, y1 = min(ys), max(ys)
    span = max(1, y1 - y0)
    out = dict(cells)
    for (x, y, z), v in cells.items():
        if v != BLACKSTONE:
            continue
        t = (y - y0) / span
        n1 = noise(x, y, z, seed + 91)
        if t >= moss_from_t and n1 + 0.5 < moss_chance:
            out[(x, y, z)] = MOSS
        elif noise(x, y, z, seed + 173) + 0.5 < crack_chance:
            out[(x, y, z)] = CRACKED
    return out


# ---------------------------------------------------------------------------
# shape generators
# ---------------------------------------------------------------------------

def build_pillar(seed, height, base_r, top_r):
    """Tapering rock spire, rugged silhouette, occasional bulge/ledge, jagged tip."""
    cells = {}
    for y in range(height):
        t = y / max(1, height - 1)
        r = base_r + (top_r - base_r) * t
        r += 0.9 * math.sin(t * math.pi * (2 + (seed % 3))) * (1 - t * 0.6)  # necking/bulges
        ir = int(math.ceil(r)) + 2
        for dx in range(-ir, ir + 1):
            for dz in range(-ir, ir + 1):
                n = noise(dx, 0, dz, seed) * 1.1 + noise(dx, y, dz, seed + 3) * 0.4
                if dx * dx + dz * dz <= (max(0.6, r) + n) ** 2:
                    cells[(dx, y, dz)] = BLACKSTONE
        # occasional stubby side ledge
        if height > 15 and y == int(height * 0.4) + (seed % 3):
            lx = int(r) + 1
            for dz in range(-1, 2):
                cells[(lx, y, dz)] = BLACKSTONE
                cells[(lx + 1, y, dz)] = BLACKSTONE
    cells = weather(cells, seed, moss_from_t=0.8, moss_chance=0.3)
    sx, sy, sz, cells = finalize(cells)
    return sx, sy, sz, cells


def build_boulder(seed, w, h):
    """Squashed, nobbly free-standing rock mass."""
    cells = {}
    r = (w - 1) / 2.0
    c = r
    for y in range(h):
        t = y / max(1, h - 1)
        prof = math.sin(math.pi * min(1.0, t * 1.15)) ** 0.7
        ry = r * (0.35 + 0.65 * prof)
        for x in range(w):
            for z in range(w):
                dx, dz = x - c, z - c
                n = noise(x, y, z, seed)
                if dx * dx + dz * dz <= (ry + n * 1.6) ** 2:
                    cells[(x, y, z)] = BLACKSTONE
    cells = weather(cells, seed, moss_from_t=0.7, moss_chance=0.4)
    sx, sy, sz, cells = finalize(cells)
    return sx, sy, sz, cells


def build_elephant(seed, scale=1.0):
    """Big rounded body, a raised head, and a curved trunk tapering to the ground —
    the classic 'elephant rock' sea-stack silhouette."""
    cells = {}
    bw = max(6, int(13 * scale))
    bd = max(5, int(10 * scale))
    bh = max(5, int(9 * scale))
    bcx, bcz = bw // 2, bd // 2 + 4

    for y in range(bh):
        t = y / max(1, bh - 1)
        squash = math.sqrt(max(0.0, 1 - ((t - 0.55) / 0.75) ** 2))
        rx = (bw / 2) * max(0.2, squash)
        rz = (bd / 2) * max(0.2, squash)
        for x in range(bw + 4):
            for z in range(bd + 8):
                dx, dz = (x - bcx) / rx, (z - bcz) / rz
                n = noise(x, y, z, seed) * 0.14
                if dx * dx + dz * dz <= (1 + n) ** 2:
                    cells[(x, y, z)] = BLACKSTONE

    hr = 3.3 * scale
    hcx = bcx + int(bw * 0.28)
    hcz = max(6, int(3 * hr))  # keep enough room in front for the trunk to descend without going negative
    hcy = bh - 1
    for y in range(int(hcy - hr), int(hcy + hr * 1.2) + 1):
        for x in range(int(hcx - hr - 2), int(hcx + hr + 2)):
            for z in range(int(hcz - hr - 2), int(hcz + hr + 2)):
                if x < 0 or z < 0 or y < 0:
                    continue
                dx, dy, dz = x - hcx, y - hcy, z - hcz
                n = noise(x, y, z, seed + 5) * 0.16
                if (dx * dx) / (hr * hr) + (dy * dy) / ((hr * 0.9) ** 2) + (dz * dz) / (hr * hr) <= (1 + n) ** 2:
                    cells[(x, y, z)] = BLACKSTONE

    # trunk: tapering tube arcing from the head down to the ground out front.
    # Explicit parametric curve (not accumulated steps) so it always lands
    # exactly at y=0 and never drifts past z=0 into dropped negative coords.
    trunk_top_y = hcy - hr * 0.3
    trunk_top_z = hcz - hr * 0.4
    lean = 1.6 * scale + hr * 0.3       # total forward curve, bounded well under hcz
    curl = 0.5 * scale                  # tip curls back in slightly near the ground
    trunk_r = 1.7 * scale
    steps = int(trunk_top_y) + 8
    for i in range(steps + 1):
        t = i / steps
        py = trunk_top_y * (1 - t)
        pz = trunk_top_z - lean * math.sin(t * math.pi / 2) + curl * (t ** 3)
        r = max(0.6, trunk_r * (1 - 0.5 * t))
        cxi, cyi, czi = int(round(hcx)), int(round(py)), int(round(pz))
        ir = int(math.ceil(r)) + 1
        for dx in range(-ir, ir + 1):
            for dy in range(-ir, ir + 1):
                for dz in range(-ir, ir + 1):
                    x, y, z = cxi + dx, cyi + dy, czi + dz
                    if x < 0 or y < 0 or z < 0:
                        continue
                    if dx * dx + dy * dy + dz * dz <= r * r + 0.4:
                        cells[(x, y, z)] = BLACKSTONE

    cells = weather(cells, seed, moss_from_t=0.75, moss_chance=0.35)
    sx, sy, sz, cells = finalize(cells)
    return sx, sy, sz, cells


def build_volcano(seed, base_r, height, crater_r, crater_depth):
    """Large cone with a magma-filled crater — the rare landmark structure."""
    cells = {}
    off = base_r + 2
    for y in range(height):
        t = y / max(1, height - 1)
        r = base_r * (1 - t) ** 0.8 + 1.5 * t
        r = max(r, crater_r + 1.5)
        ir = int(math.ceil(r)) + 2
        for dx in range(-ir, ir + 1):
            for dz in range(-ir, ir + 1):
                n = noise(dx, y, dz, seed) * 1.5
                if dx * dx + dz * dz <= (r + n) ** 2:
                    cells[(dx + off, y, dz + off)] = BLACKSTONE

    top = height - 1
    for x in range(-crater_r - 1, crater_r + 2):
        for z in range(-crater_r - 1, crater_r + 2):
            d = math.sqrt(x * x + z * z)
            if d <= crater_r:
                depth = int(crater_depth * (1 - (d / crater_r) ** 2))
                for y in range(top, top - depth - 1, -1):
                    cells.pop((off + x, y, off + z), None)

    floor_y = max(0, top - crater_depth)
    for x in range(-crater_r + 1, crater_r):
        for z in range(-crater_r + 1, crater_r):
            if x * x + z * z <= (crater_r - 1) ** 2:
                cells[(off + x, floor_y, off + z)] = MAGMA if (x + z) % 3 else LAVA

    cells = weather(cells, seed, moss_from_t=1.1, moss_chance=0.0)  # no moss above the treeline
    # low-slope moss instead, applied directly since weather() only handles the top
    ys = [y for (_, y, _) in cells]
    y0, y1 = min(ys), max(ys)
    span = max(1, y1 - y0)
    for (x, y, z), v in list(cells.items()):
        if v == BLACKSTONE and (y - y0) / span < 0.32 and noise(x, y, z, seed + 61) > 0.15:
            cells[(x, y, z)] = MOSS
    sx, sy, sz, cells = finalize(cells)
    return sx, sy, sz, cells


# ---------------------------------------------------------------------------
# emit .mcstructure + feature json
# ---------------------------------------------------------------------------

def write_structure(name, sx, sy, sz, cells):
    root = make_structure(sx, sy, sz, cells)
    save(os.path.join(STRUCT_DIR, f"{name}.mcstructure"), root, "")


def write_structure_feature(name):
    ident = f"{NAMESPACE}:{STRUCT_PREFIX}/{name}"
    data = {
        "format_version": "1.14.0",
        "minecraft:structure_template_feature": {
            "description": {"identifier": ident},
            "structure_name": ident,
            "constraints": {
                "block_intersection": {
                    "block_allowlist": [
                        "minecraft:air",
                        "minecraft:grass_block",
                        "minecraft:blackstone",
                        "minecraft:moss_block",
                        "minecraft:gravel",
                        "minecraft:dirt",
                        "minecraft:stone",
                        "minecraft:water",
                    ]
                }
            },
        },
    }
    import json
    with open(os.path.join(FEATURE_DIR, f"{name}.json"), "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)


def write_weighted_feature(name, entries):
    """entries: list of (structure_name, weight)."""
    import json
    ident = f"{NAMESPACE}:{STRUCT_PREFIX}/{name}"
    data = {
        "format_version": "1.14.0",
        "minecraft:weighted_random_feature": {
            "description": {"identifier": ident},
            "features": [[f"{NAMESPACE}:{STRUCT_PREFIX}/{n}", w] for n, w in entries],
        },
    }
    with open(os.path.join(FEATURE_DIR, f"{name}.json"), "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)


def write_feature_rules(name, places_feature, biome_tag, scatter_chance, embed):
    import json
    ident = f"{NAMESPACE}:{STRUCT_PREFIX}_{name}"
    data = {
        "format_version": "1.16.0",
        "minecraft:feature_rules": {
            "description": {
                "identifier": ident,
                "places_feature": f"{NAMESPACE}:{STRUCT_PREFIX}/{places_feature}",
            },
            "conditions": {
                "placement_pass": "before_underground_pass",
                "minecraft:biome_filter": [
                    {"test": "has_biome_tag", "operator": "==", "value": biome_tag}
                ],
            },
            "distribution": {
                "iterations": 1,
                "scatter_chance": scatter_chance,
                "x": {"distribution": "uniform", "extent": [0, 16]},
                "y": f"query.above_top_solid(variable.worldx, variable.worldz)-{embed}",
                "z": {"distribution": "uniform", "extent": [0, 16]},
            },
        },
    }
    with open(os.path.join(RULES_DIR, f"{ident.split(':')[1]}.json"), "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)


# ---------------------------------------------------------------------------
# build everything
# ---------------------------------------------------------------------------

PILLARS = [
    ("pillar_1", 101, 16, 3, 1),
    ("pillar_2", 102, 22, 4, 1),
    ("pillar_3", 103, 14, 2, 1),
    ("pillar_4", 104, 26, 4, 2),
    ("pillar_5", 105, 19, 3, 1),
    ("pillar_6", 106, 24, 5, 2),
]
BOULDERS = [
    ("boulder_1", 201, 5, 4),
    ("boulder_2", 202, 7, 5),
    ("boulder_3", 203, 4, 3),
    ("boulder_4", 204, 9, 6),
    ("boulder_5", 205, 6, 4),
    ("boulder_6", 206, 8, 5),
    ("boulder_7", 207, 5, 5),
    ("boulder_8", 208, 10, 7),
]
ELEPHANTS = [
    ("elephant_rock_1", 301, 1.0),
    ("elephant_rock_2", 302, 1.2),
    ("elephant_rock_3", 303, 0.85),
    ("elephant_rock_4", 304, 1.1),
]
VOLCANOES = [
    ("volcano_1", 401, 14, 24, 4, 6),
    ("volcano_2", 402, 12, 20, 3, 5),
]

names_common = []

for name, seed, height, base_r, top_r in PILLARS:
    sx, sy, sz, cells = build_pillar(seed, height, base_r, top_r)
    cells = add_embed(cells, 3)
    sx, sy, sz, cells = finalize(cells)
    write_structure(name, sx, sy, sz, cells)
    write_structure_feature(name)
    names_common.append((name, 3))
    print(f"{name}: {sx}x{sy}x{sz}, {len(cells)} blocks")

for name, seed, w, h in BOULDERS:
    sx, sy, sz, cells = build_boulder(seed, w, h)
    cells = add_embed(cells, 3)
    sx, sy, sz, cells = finalize(cells)
    write_structure(name, sx, sy, sz, cells)
    write_structure_feature(name)
    names_common.append((name, 4))
    print(f"{name}: {sx}x{sy}x{sz}, {len(cells)} blocks")

for name, seed, scale in ELEPHANTS:
    sx, sy, sz, cells = build_elephant(seed, scale)
    cells = add_embed(cells, 3)
    sx, sy, sz, cells = finalize(cells)
    write_structure(name, sx, sy, sz, cells)
    write_structure_feature(name)
    names_common.append((name, 2))
    print(f"{name}: {sx}x{sy}x{sz}, {len(cells)} blocks")

names_volcano = []
for name, seed, base_r, height, crater_r, crater_depth in VOLCANOES:
    sx, sy, sz, cells = build_volcano(seed, base_r, height, crater_r, crater_depth)
    cells = add_embed(cells, 6)
    sx, sy, sz, cells = finalize(cells)
    write_structure(name, sx, sy, sz, cells)
    write_structure_feature(name)
    names_volcano.append((name, 1))
    print(f"{name}: {sx}x{sy}x{sz}, {len(cells)} blocks")

write_weighted_feature("select_rock_formation", names_common)
write_weighted_feature("select_volcano", names_volcano)

write_feature_rules("rock_formations_feature", "select_rock_formation",
                     "volcanic_moss_tundra", 15, 3)
write_feature_rules("volcano_feature", "select_volcano",
                     "mountain", 1, 6)

print(f"\ntotal structures: {len(names_common) + len(names_volcano)}")
print("done")
