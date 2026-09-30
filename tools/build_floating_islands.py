"""Generate the Floating Jungle sky-island prefabs.

Each prefab is a grass-topped island with an inverted stone cone underneath, jungle trees, hanging vines and roots,
cloud wisps beneath it, and (large ones) a waterfall over the rim. Archipelagos join a main island to satellite
islets with giant-log canopy bridges, and the ruin variant has a mossy brick ruin around a loot chest.

Java:    python tools/build_floating_islands.py --java   (writes .nbt only)
Bedrock: python tools/build_floating_islands.py          (writes .mcstructure, feature and feature_rule JSON)

Every template must stay <= MAX_SPAN wide: SingleStructureFeature only writes inside a 48-block window.
Re-runnable: overwrites its own output. Keep TEMPLATES weights in sync with FloatingJungleFeatures.java.
"""
import json
import math
import os
import sys

from mcstructure import load, save, Tag, T_byte, T_int, T_str, T_comp, T_list, TAG_INT, TAG_LIST, TAG_COMPOUND, TAG_END

HERE = os.path.dirname(os.path.abspath(__file__))
BP = os.path.join(HERE, "..", "ExtraBiomes - Bedrock", "packs", "BP")
STRUCT_DIR = os.path.join(BP, "structures", "extrabiomes", "floating_jungle")
FEATURE_DIR = os.path.join(BP, "features", "floating_jungle")
RULE_DIR = os.path.join(BP, "feature_rules", "floating_jungle")
JAVA_STRUCT_DIR = os.path.join(HERE, "..", "ExtraBiomes - Java", "common", "src", "main", "resources",
                               "data", "extrabiomes", "structure", "floating_jungle")
NAMESPACE = "extrabiomes"
LOOT_TABLE = "loot_tables/chests/floating_jungle_ruin.json"
MAX_SPAN = 44
RIM_NOISE = 0.18

# (name, weight, builder kwargs); "kind" picks the builder
TEMPLATES = [
    ("islet_small_1", 17, dict(kind="islet", seed=11, radius=6, thick=8)),
    ("islet_small_2", 17, dict(kind="islet", seed=12, radius=8, thick=10)),
    ("islet_small_3", 16, dict(kind="islet", seed=13, radius=9, thick=11)),
    ("islet_large_1", 12, dict(kind="islet", seed=21, radius=15, thick=16, waterfall=True, puffs=5)),
    ("islet_large_2", 13, dict(kind="islet", seed=22, radius=16, thick=18, waterfall=True, puffs=5)),
    ("archipelago_1", 10, dict(kind="archipelago", seed=31, radius=11, thick=12,
                               satellites=[(20, 20, 5, 7, 3), (70, 21, 5, 7, -2), (115, 19, 6, 8, 1)])),
    ("archipelago_2", 10, dict(kind="archipelago", seed=32, radius=12, thick=13,
                               satellites=[(10, 21, 6, 8, 2), (75, 20, 5, 7, -3)], chain=True)),
    ("archipelago_ruin", 5, dict(kind="archipelago", seed=41, radius=13, thick=14, ruin=True,
                                 satellites=[(15, 21, 5, 7, 2), (95, 20, 5, 7, -2)])),
]

BLOCKS = {
    "grass": ("minecraft:grass_block", {}),
    "dirt": ("minecraft:dirt", {}),
    "moss": ("minecraft:moss_block", {}),
    "moss_carpet": ("minecraft:moss_carpet", {}),
    "stone": ("minecraft:stone", {"stone_type": T_str("stone")}),
    "andesite": ("minecraft:andesite", {}),
    "mossy_cobble": ("minecraft:mossy_cobblestone", {}),
    "brick": ("minecraft:stonebrick", {"stone_brick_type": T_str("default")}),
    "brick_mossy": ("minecraft:stonebrick", {"stone_brick_type": T_str("mossy")}),
    "brick_cracked": ("minecraft:stonebrick", {"stone_brick_type": T_str("cracked")}),
    "log_y": ("minecraft:log", {"old_log_type": T_str("jungle"), "pillar_axis": T_str("y")}),
    "log_x": ("minecraft:log", {"old_log_type": T_str("jungle"), "pillar_axis": T_str("x")}),
    "log_z": ("minecraft:log", {"old_log_type": T_str("jungle"), "pillar_axis": T_str("z")}),
    "leaves": ("minecraft:jungle_leaves", {"persistent_bit": T_byte(1), "update_bit": T_byte(0)}),
    "roots": ("minecraft:hanging_roots", {}),
    "water": ("minecraft:water", {"liquid_depth": T_int(0)}),
    "cloud": ("extrabiomes:dense_cloud", {}),
    "chest": ("minecraft:chest", {"minecraft:cardinal_direction": T_str("south")}),
}
SOLID = {"grass", "dirt", "moss", "stone", "andesite", "mossy_cobble", "brick", "brick_mossy", "brick_cracked",
         "log_y", "log_x", "log_z"}
# vine_direction_bits: south=1 (+z), west=2 (-x), north=4 (-z), east=8 (+x)
VINE_FACES = [((0, 1), 1), ((-1, 0), 2), ((0, -1), 4), ((1, 0), 8)]


def block_def(key):
    if isinstance(key, tuple):
        return "minecraft:vine", {"vine_direction_bits": T_int(key[1])}
    return BLOCKS[key]


def _reference_version():
    for root, _, files in os.walk(os.path.join(BP, "structures")):
        for f in files:
            if f.endswith(".mcstructure") and root != STRUCT_DIR:
                _, ref = load(os.path.join(root, f))
                return ref.value["structure"].value["palette"].value["default"].value["block_palette"].value[0].value["version"].value
    raise SystemExit("no reference .mcstructure found to copy the palette version from")


VERSION = _reference_version()


def h32(*vals):
    n = 0
    for v, m in zip(vals, (374761393, 668265263, 2246822519, 3266489917, 668265263)):
        n = (n + v * m) & 0xFFFFFFFF
    n = ((n ^ (n >> 13)) * 1274126177) & 0xFFFFFFFF
    return (n ^ (n >> 16)) & 0xFFFFFFFF


def rand01(seed, tag):
    return (h32(seed, tag) % 100000) / 100000.0


def rim_terms(seed):
    """Integer-frequency angular sine terms, so the island outline closes without a seam."""
    return [(k, rand01(seed, k) * math.tau, RIM_NOISE * a) for k, a in ((2, 0.5), (3, 0.3), (5, 0.2))]


def rim_scale(terms, theta):
    return 1 + sum(a * math.sin(k * theta + ph) for k, ph, a in terms)


class Grid:
    def __init__(self, seed):
        self.cells = {}
        self.seed = seed
        self.columns = {}

    def put(self, x, y, z, key, overwrite=False):
        if overwrite or (x, y, z) not in self.cells:
            self.cells[(x, y, z)] = key

    def ball(self, cx, cy, cz, r, key, salt, keep=0.9, flat=1.0):
        R = math.ceil(r)
        for dx in range(-R, R + 1):
            for dy in range(-R, R + 1):
                for dz in range(-R, R + 1):
                    if dx * dx + (dy / flat) ** 2 + dz * dz <= r * r and rand01(self.seed + salt, dx * 73 + dy * 179 + dz * 331) < keep:
                        self.put(cx + dx, cy + dy, cz + dz, key)


def add_island(g, seed, cx, cz, radius, thick, top_y):
    terms = rim_terms(seed)
    ph1, ph2 = rand01(seed, 901) * math.tau, rand01(seed, 902) * math.tau
    span = int(radius * (1 + RIM_NOISE)) + 2
    for x in range(cx - span, cx + span + 1):
        for z in range(cz - span, cz + span + 1):
            dx, dz = x - cx, z - cz
            d = math.hypot(dx, dz) / (radius * rim_scale(terms, math.atan2(dz, dx)))
            if d >= 1:
                continue
            bump = 0.5 + 0.5 * math.sin(0.35 * dx + ph1) * math.sin(0.3 * dz + ph2)
            top = top_y + int(bump * 1.6 * (1 - d) ** 0.7)
            depth = int(thick * (1 - d) ** 1.4 * (0.85 + 0.3 * rand01(seed, x * 7919 + z * 104729)))
            g.columns[(x, z)] = (top, top - depth)
            for y in range(top - depth, top + 1):
                k = top - y
                r = rand01(seed, x * 31 + y * 137 + z * 719)
                if k == 0:
                    key = "moss" if r < 0.12 else "grass"
                elif k <= 2:
                    key = "dirt"
                else:
                    key = "andesite" if r < 0.3 else ("mossy_cobble" if r < 0.36 else "stone")
                g.put(x, y, z, key, overwrite=True)


def add_trees(g, seed, cx, cz, radius, top_y, avoid=()):
    placed = []
    trees = int(radius * radius / 45)
    for i in range(trees * 4 + int(radius / 3) * 3):
        if len(placed) >= trees + int(radius / 3):
            break
        ang, dist = rand01(seed, 1000 + i * 3) * math.tau, radius * 0.75 * rand01(seed, 1001 + i * 3)
        x, z = cx + round(dist * math.cos(ang)), cz + round(dist * math.sin(ang))
        col = g.columns.get((x, z))
        if col is None or (x, z) in avoid or any(math.hypot(x - px, z - pz) < 4 for px, pz, _ in placed):
            continue
        is_tree = len([p for p in placed if p[2]]) < trees
        placed.append((x, z, is_tree))
        top = col[0]
        if is_tree:
            h = 4 + int(rand01(seed, 1002 + i) * 4)
            for y in range(top + 1, top + h + 1):
                g.put(x, y, z, "log_y", overwrite=True)
            g.ball(x, top + h, z, 2.4, "leaves", i, flat=0.7)
            g.ball(x, top + h + 2, z, 1.6, "leaves", i + 50)
        else:
            g.ball(x, top + 1, z, 1.7, "leaves", i, keep=0.85, flat=0.6)


def add_waterfall(g, seed, cx, cz, radius):
    ang = rand01(seed, 950) * math.tau
    dx, dz = math.cos(ang), math.sin(ang)
    r = radius * 1.4
    while r > 0 and (round(cx + dx * r), round(cz + dz * r)) not in g.columns:
        r -= 0.5
    for step in range(3):
        x, z = round(cx + dx * (r - step)), round(cz + dz * (r - step))
        col = g.columns.get((x, z))
        if col:
            g.put(x, col[0], z, "water", overwrite=True)
            for y in (col[0] - 1,):
                g.put(x, y, z, "dirt")


def add_clouds(g, seed, cx, cz, radius, thick, top_y, count):
    for i in range(count):
        ang = rand01(seed, 1500 + i) * math.tau
        dist = radius * (0.5 + 0.6 * rand01(seed, 1510 + i))
        add_cloud_puff(g, seed + i, cx + round(dist * math.cos(ang)), cz + round(dist * math.sin(ang)),
                       top_y - int(thick * (0.35 + 0.6 * rand01(seed, 1520 + i))))


def add_cloud_puff(g, seed, px, pz, base_y):
    """Flat-bottomed cloud: every column starts at base_y and its height follows a low ripple, so the top is wavy."""
    rx, rz = 3 + 3 * rand01(seed, 1530), 3 + 3 * rand01(seed, 1531)
    ph1, ph2 = rand01(seed, 1532) * math.tau, rand01(seed, 1533) * math.tau
    for x in range(px - math.ceil(rx), px + math.ceil(rx) + 1):
        for z in range(pz - math.ceil(rz), pz + math.ceil(rz) + 1):
            d2 = ((x - px) / rx) ** 2 + ((z - pz) / rz) ** 2
            if d2 >= 1:
                continue
            ripple = 0.5 + 0.3 * math.sin(0.9 * (x - px) + ph1) + 0.2 * math.sin(1.3 * (z - pz) + ph2)
            for y in range(base_y, base_y + 1 + int(3.6 * (1 - d2) ** 0.7 * ripple)):
                g.put(x, y, z, "cloud")


def add_roots(g, seed):
    for (x, z), (top, bottom) in g.columns.items():
        if rand01(seed, x * 41 + z * 97) < 0.06 and (x, bottom - 1, z) not in g.cells and bottom < top - 2:
            g.put(x, bottom - 1, z, "roots")


def add_vines(g, seed):
    for (x, y, z), key in sorted(g.cells.items()):
        if key not in SOLID:
            continue
        chance = 0.14 if key.startswith("log") else 0.06 if key.startswith("brick") else 0.05
        for (dx, dz), _ in VINE_FACES:
            nx, nz = x + dx, z + dz
            if (nx, y, nz) in g.cells or rand01(seed, x * 13 + y * 29 + z * 53 + dx * 7 + dz * 11) >= chance:
                continue
            mask = sum(bit for (fx, fz), bit in VINE_FACES if g.cells.get((nx + fx, y, nz + fz)) in SOLID)
            length = 2 + int(rand01(seed, x * 3 + y * 5 + z * 7 + dx) * 9)
            for i in range(length):
                if (nx, y - i, nz) in g.cells:
                    break
                g.put(nx, y - i, nz, ("vine", mask))


def add_bridge(g, seed, a, b, salt):
    (x1, z1, y1), (x2, z2, y2) = a, b
    length = max(abs(x2 - x1), abs(z2 - z1), 1)
    axis = "log_x" if abs(x2 - x1) >= abs(z2 - z1) else "log_z"
    for i in range(length + 1):
        t = i / length
        x, z = round(x1 + (x2 - x1) * t), round(z1 + (z2 - z1) * t)
        y = round(y1 + (y2 - y1) * t + 1.6 * math.sin(math.pi * t))
        for ox in (0, 1):
            for oz in (0, 1):
                g.put(x + ox, y, z + oz, axis, overwrite=True)
                g.put(x + ox, y - 1, z + oz, axis, overwrite=True)
        if i % 3 == 1 and rand01(seed + salt, i) < 0.75:
            side = 1 if rand01(seed + salt, i + 500) < 0.5 else -2
            if axis == "log_x":
                g.ball(x, y + 1, z + side, 1.7, "leaves", salt + i, keep=0.85, flat=0.7)
            else:
                g.ball(x + side, y + 1, z, 1.7, "leaves", salt + i, keep=0.85, flat=0.7)


def add_ruin(g, seed, cx, cz, floor_y):
    half = 4
    footprint = set()
    for dx in range(-half, half + 1):
        for dz in range(-half, half + 1):
            x, z = cx + dx, cz + dz
            footprint.add((x, z))
            for y in range(floor_y + 1, floor_y + 8):
                g.cells.pop((x, y, z), None)
            r = rand01(seed, dx * 17 + dz * 31)
            g.put(x, floor_y, z, "brick_mossy" if r < 0.4 else "brick_cracked" if r < 0.6 else "brick", overwrite=True)
            g.put(x, floor_y - 1, z, "dirt")
            edge = max(abs(dx), abs(dz))
            corner = abs(dx) == half and abs(dz) == half
            pillar = abs(dx) == 2 and abs(dz) == 2
            if corner or pillar:
                height = (5 + int(rand01(seed, dx + dz * 9) * 2)) if corner else (2 + int(rand01(seed, dx * 5 + dz) * 3))
            elif edge == half and not (dz == half and abs(dx) <= 1):
                height = int(rand01(seed, dx * 23 + dz * 41) * 4)
            else:
                height = 0
            for h in range(1, height + 1):
                w = rand01(seed, dx * 11 + dz * 19 + h)
                g.put(x, floor_y + h, z, "brick_mossy" if w < 0.5 else "brick_cracked" if w < 0.65 else "brick", overwrite=True)
            if height == 0 and edge < half and rand01(seed, dx * 7 + dz * 3 + 1) < 0.25:
                g.put(x, floor_y + 1, z, "moss_carpet")
    g.put(cx, floor_y + 1, cz, "chest", overwrite=True)
    return footprint


def build(seed, radius, thick, kind, waterfall=False, puffs=3, satellites=(), ruin=False, chain=False):
    g = Grid(seed)
    add_island(g, seed, 0, 0, radius, thick, 0)
    avoid = add_ruin(g, seed, 0, 0, 0) if ruin else set()
    add_trees(g, seed, 0, 0, radius, 0, avoid)
    if waterfall:
        add_waterfall(g, seed, 0, 0, radius)
    add_clouds(g, seed, 0, 0, radius, thick, 0, puffs)

    prev = (0, 0, radius, 0)
    for i, (deg, dist, sat_r, sat_thick, dy) in enumerate(satellites):
        sx, sz = round(dist * math.cos(math.radians(deg))), round(dist * math.sin(math.radians(deg)))
        sat_seed = seed * 7 + i
        add_island(g, sat_seed, sx, sz, sat_r, sat_thick, dy)
        add_trees(g, sat_seed, sx, sz, sat_r, dy)
        add_clouds(g, sat_seed, sx, sz, sat_r, sat_thick, dy, 2)
        px, pz, pr, py = prev if chain else (0, 0, radius, 0)
        ang = math.atan2(sz - pz, sx - px)
        start = (px + round(math.cos(ang) * (pr - 1)), pz + round(math.sin(ang) * (pr - 1)), py)
        end = (sx - round(math.cos(ang) * (sat_r - 1)), sz - round(math.sin(ang) * (sat_r - 1)), dy)
        add_bridge(g, seed, start, end, 100 + i * 200)
        prev = (sx, sz, sat_r, dy)

    add_roots(g, seed)
    add_vines(g, seed)
    return finalize(g.cells)


def finalize(cells):
    xs, ys, zs = zip(*cells)
    x0, y0, z0 = min(xs), min(ys), min(zs)
    sx, sy, sz = max(xs) - x0 + 1, max(ys) - y0 + 1, max(zs) - z0 + 1
    return sx, sy, sz, {(x - x0, y - y0, z - z0): v for (x, y, z), v in cells.items()}


def make_structure(sx, sy, sz, cells):
    keys = sorted({k for k in cells.values()}, key=str)
    index = {k: i for i, k in enumerate(keys)}
    palette = []
    for k in keys:
        name, states = block_def(k)
        palette.append(T_comp({"name": T_str(name), "states": T_comp(dict(states)), "version": T_int(VERSION)}))
    total = sx * sy * sz
    layer0 = [Tag(TAG_INT, -1) for _ in range(total)]
    layer1 = [Tag(TAG_INT, -1) for _ in range(total)]
    block_entities = {}
    for (x, y, z), key in cells.items():
        flat = x * sy * sz + y * sz + z
        layer0[flat] = Tag(TAG_INT, index[key])
        if key == "chest":
            block_entities[str(flat)] = T_comp({"block_entity_data": T_comp({
                "id": T_str("Chest"),
                "LootTable": T_str(LOOT_TABLE),
            })})
    return T_comp({
        "format_version": T_int(1),
        "size": T_list([T_int(sx), T_int(sy), T_int(sz)], TAG_INT),
        "structure": T_comp({
            "block_indices": T_list([T_list(layer0, TAG_INT), T_list(layer1, TAG_INT)], TAG_LIST),
            "entities": T_list([], TAG_END),
            "palette": T_comp({"default": T_comp({
                "block_palette": T_list(palette, TAG_COMPOUND),
                "block_position_data": T_comp(block_entities),
            })}),
        }),
        "structure_world_origin": T_list([T_int(0), T_int(0), T_int(0)], TAG_INT),
    })


def write_bedrock_structure(name, sx, sy, sz, cells):
    os.makedirs(STRUCT_DIR, exist_ok=True)
    save(os.path.join(STRUCT_DIR, f"{name}.mcstructure"), make_structure(sx, sy, sz, cells), "")


def write_java_structure(name, sx, sy, sz, cells):
    import tempfile
    from collections import Counter
    import mc2java
    os.makedirs(JAVA_STRUCT_DIR, exist_ok=True)
    with tempfile.TemporaryDirectory() as tmp:
        src = os.path.join(tmp, f"{name}.mcstructure")
        save(src, make_structure(sx, sy, sz, cells), "")
        warnings = []
        mc2java.convert_one(src, os.path.join(JAVA_STRUCT_DIR, f"{name}.nbt"), warnings, Counter())
        for w in warnings:
            print(f"  warning: {w}")


def write_json(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)
        f.write("\n")


def write_bedrock_features():
    for name, _, _ in TEMPLATES:
        write_json(os.path.join(FEATURE_DIR, f"{name}_feature.json"), {
            "format_version": "1.14.0",
            "minecraft:structure_template_feature": {
                "description": {"identifier": f"{NAMESPACE}:floating_jungle/{name}_feature"},
                "structure_name": f"{NAMESPACE}:floating_jungle/{name}",
                "constraints": {"block_intersection": {"block_allowlist": ["minecraft:air"]}},
            },
        })
    write_json(os.path.join(FEATURE_DIR, "select_island_feature.json"), {
        "format_version": "1.14.0",
        "minecraft:weighted_random_feature": {
            "description": {"identifier": f"{NAMESPACE}:floating_jungle/select_island_feature"},
            "features": [[f"{NAMESPACE}:floating_jungle/{name}_feature", weight] for name, weight, _ in TEMPLATES],
        },
    })
    surface = "query.heightmap(variable.worldx, variable.worldz)"
    write_json(os.path.join(RULE_DIR, "island.json"), {
        "format_version": "1.14.0",
        "minecraft:feature_rules": {
            "description": {
                "identifier": f"{NAMESPACE}:floating_jungle_island",
                "places_feature": f"{NAMESPACE}:floating_jungle/select_island_feature",
            },
            "conditions": {
                "placement_pass": "surface_pass",
                "minecraft:biome_filter": [{"test": "has_biome_tag", "operator": "==", "value": "floating_jungle"}],
            },
            "distribution": {
                "iterations": 1,
                "scatter_chance": 12,
                "x": {"distribution": "uniform", "extent": [0, 16]},
                "y": {"distribution": "uniform", "extent": [f"{surface} + 30", f"{surface} + 70"]},
                "z": {"distribution": "uniform", "extent": [0, 16]},
            },
        },
    })


if __name__ == "__main__":
    java_only = "--java" in sys.argv[1:]
    for name, _, kwargs in TEMPLATES:
        sx, sy, sz, cells = build(**kwargs)
        assert sx <= MAX_SPAN and sz <= MAX_SPAN, f"{name} is {sx}x{sz}, over the {MAX_SPAN}-block write window"
        (write_java_structure if java_only else write_bedrock_structure)(name, sx, sy, sz, cells)
        print(f"{name}: {sx}x{sy}x{sz}, {len(cells)} blocks")
    if not java_only:
        write_bedrock_features()
    print("done")
