"""Generate the Tallow Hollows cave worldgen for Bedrock: structure templates, features and feature rules.

Usage:
    python tools/build_tallow_hollows.py

Bedrock has no surface rule for cave interiors, so the tallowstone shell, pillars, forgotten shelves, wick blooms,
candle clusters, wax drips and honeycomb veins are all features gated on the `tallow_hollows` biome tag.
Templates follow the Glacier convention (see build_glacier.py): layer `ground_level` is the air layer the snap
feature lands on, so floor templates sit on a sunk root layer and the ceiling caps reach up into the rock.
Re-runnable: overwrites its own output.
"""
import json
import math
import os

import build_floating_islands as bfi
from build_floating_islands import rand01
from mcstructure import T_byte, T_int

NAMESPACE = "extrabiomes"
BIOME_TAG = "tallow_hollows"
FOLDER = "tallow_hollows"
STRUCT_DIR = os.path.join(bfi.BP, "structures", NAMESPACE, FOLDER)
FEATURE_DIR = os.path.join(bfi.BP, "features", FOLDER)
RULE_DIR = os.path.join(bfi.BP, "feature_rules", FOLDER)
SNAP_RANGE = 12
CAVE_Y = {"distribution": "uniform", "extent": [-64, "query.heightmap(variable.worldx, variable.worldz) - 10"]}

TALLOWSTONE = f"{NAMESPACE}:tallowstone"
EVER_WICK = f"{NAMESPACE}:ever_wick"
WAX_DRIP = f"{NAMESPACE}:wax_drip"

bfi.STRUCT_DIR = STRUCT_DIR
bfi.BLOCKS.update({
    "tallowstone": (TALLOWSTONE, {}),
    "ever_wick": (EVER_WICK, {}),
    "wax_drip": (WAX_DRIP, {}),
    "bookshelf": ("minecraft:bookshelf", {}),
})
for count in range(1, 5):
    bfi.BLOCKS[f"candle_{count}"] = ("minecraft:candle", {"candles": T_int(count - 1), "lit": T_byte(1)})

ROCK = ["minecraft:stone", "minecraft:deepslate", "minecraft:andesite", "minecraft:diorite", "minecraft:granite",
        "minecraft:tuff", "minecraft:calcite", "minecraft:dripstone_block", "minecraft:dirt", "minecraft:gravel"]
ORES = [f"minecraft:{prefix}{ore}_ore" for prefix in ("", "deepslate_") for ore in
        ("coal", "iron", "copper", "gold", "redstone", "lapis", "diamond", "emerald")]
# Floor templates may only overwrite air and the biome's own soft blocks: a stone cell means the ceiling is too low
# (or the floor is not yet tallowstone), and water or lava rejects the placement outright.
FLOOR_ALLOWLIST = ["minecraft:air", TALLOWSTONE, EVER_WICK, WAX_DRIP, "minecraft:candle"]
CEILING_ALLOWLIST = ["minecraft:air", TALLOWSTONE] + ROCK + ORES

# (name, seed, height, base radius)
PILLARS = [
    ("tallow_pillar_1", 1, 4, 1),
    ("tallow_pillar_2", 2, 6, 2),
    ("tallow_pillar_3", 3, 9, 2),
    ("tallow_pillar_4", 4, 11, 3),
    ("tallow_pillar_5", 5, 14, 3),
]
MAX_PILLAR_DRIPS = 4
RIM_MELT_CHANCE = 0.4

# Each column: (x, z, shelves stacked, sunk one block into the floor, candles on top or 0).
SHELF_CLUSTERS = {
    "forgotten_shelves_1": [(0, 0, 2, False, 2), (1, 0, 3, True, 0)],
    "forgotten_shelves_2": [(0, 0, 1, False, 1), (1, 0, 3, True, 0), (2, 0, 2, False, 3)],
    "forgotten_shelves_3": [(0, 0, 3, True, 0), (1, 0, 2, False, 2), (2, 0, 1, False, 0), (2, 1, 2, True, 1)],
    "forgotten_shelves_4": [(0, 0, 2, False, 0), (1, 0, 3, True, 3), (2, 0, 3, False, 0), (3, 0, 1, False, 2),
                            (0, 1, 1, True, 0), (0, 2, 2, False, 1)],
}

# (name, seed, radius, depth)
CEILING_CAPS = [
    ("ceiling_cap_1", 1, 3.5, 2),
    ("ceiling_cap_2", 2, 4.5, 3),
    ("ceiling_cap_3", 3, 5.5, 3),
]


def disc(cells, y, radius, key, seed, layer):
    """Fill a horizontal disc, randomly dropping part of its outermost ring so edges look melted."""
    limit = radius * radius + radius
    reach = math.ceil(radius)
    for dx in range(-reach, reach + 1):
        for dz in range(-reach, reach + 1):
            d2 = dx * dx + dz * dz
            if d2 > limit:
                continue
            on_rim = d2 > radius * radius
            if on_rim and rand01(seed, dx * 131 + dz * 7919 + layer * 17) < RIM_MELT_CHANCE:
                continue
            cells[(dx, y, dz)] = key


def pillar_radius(layer, height, base_radius):
    """Never widens with height and is at most 1 on the top layer."""
    taper = round(base_radius * (1 - layer / (height - 1)))
    return min(taper, 1) if layer == height - 1 else taper


def pillar_drip_spots(body, seed):
    """Cells under an overhanging pillar block with air below them too, chosen deterministically."""
    candidates = sorted(
        (x, y - 1, z) for (x, y, z) in body
        if y - 1 >= 2 and (x, y - 1, z) not in body and (x, y - 2, z) not in body
    )
    picked = sorted(candidates, key=lambda pos: rand01(seed, pos[0] * 37 + pos[1] * 101 + pos[2] * 211))
    return picked[:min(MAX_PILLAR_DRIPS, int(rand01(seed, 999) * (MAX_PILLAR_DRIPS + 1)))]


def tallow_pillar(seed, height, base_radius):
    cells = {}
    disc(cells, 0, base_radius, "tallowstone", seed, -1)
    for layer in range(height):
        disc(cells, layer + 1, pillar_radius(layer, height, base_radius), "tallowstone", seed, layer)
    cells[(0, height + 1, 0)] = "ever_wick"
    body = {pos for pos, key in cells.items() if key == "tallowstone"}
    for spot in pillar_drip_spots(body, seed):
        cells[spot] = "wax_drip"
    return cells


def forgotten_shelves(columns):
    cells = {}
    for x, z, stacked, sunk, candles in columns:
        bottom = 0 if sunk else 1
        if not sunk:
            cells[(x, 0, z)] = "tallowstone"
        for y in range(bottom, bottom + stacked):
            cells[(x, y, z)] = "bookshelf"
        if candles:
            cells[(x, bottom + stacked, z)] = f"candle_{candles}"
    return cells


def ceiling_cap(seed, radius, depth):
    cells = {}
    for layer in range(depth):
        disc(cells, layer + 1, radius + layer * 0.5, "tallowstone", seed, layer)
    return cells


def write_template(name, cells):
    """Writes cells (y already relative to the snapped layer's floor) and returns the template's size."""
    xs, ys, zs = zip(*cells)
    x0, z0 = min(xs), min(zs)
    assert min(ys) >= 0, f"{name} has cells below layer 0"
    sx, sy, sz = max(xs) - x0 + 1, max(ys) + 1, max(zs) - z0 + 1
    shifted = {(x - x0, y, z - z0): key for (x, y, z), key in cells.items()}
    bfi.write_bedrock_structure(name, sx, sy, sz, shifted, pad=False)
    print(f"{name}: {sx}x{sy}x{sz}, {len(cells)} blocks")


def _format(value, indent):
    pad = " " * indent
    if isinstance(value, dict):
        if not value:
            return "{}"
        items = [f'{pad}    {json.dumps(k)}: {_format(v, indent + 4)}' for k, v in value.items()]
        return "{\n" + ",\n".join(items) + f"\n{pad}}}"
    if isinstance(value, list):
        inline = "[" + ", ".join(json.dumps(v) for v in value) + "]"
        if all(not isinstance(v, (dict, list)) for v in value) and len(inline) + indent <= 80:
            return inline
        items = [f"{pad}    {_format(v, indent + 4)}" for v in value]
        return "[\n" + ",\n".join(items) + f"\n{pad}]"
    return json.dumps(value)


def write_json(directory, name, data):
    """Writes 4-space JSON matching the pack's prettier style."""
    os.makedirs(directory, exist_ok=True)
    with open(os.path.join(directory, f"{name}.json"), "w", encoding="utf-8", newline="\n") as f:
        f.write(_format(data, 0) + "\n")


def feature_id(name):
    return f"{NAMESPACE}:{FOLDER}/{name}"


def feature(name, feature_type, format_version, body):
    write_json(FEATURE_DIR, name, {
        "format_version": format_version,
        feature_type: {"description": {"identifier": feature_id(name)}, **body},
    })
    return feature_id(name)


def structure_feature(name, ground_level, allowlist):
    body = {
        "structure_name": f"{NAMESPACE}:{FOLDER}/{name}",
        "facing_direction": "random",
        "rotate_around_center": True,
        "constraints": {"block_intersection": {"block_allowlist": allowlist}},
    }
    if ground_level:
        body["ground_level"] = ground_level
    return feature(f"{name}_feature", "minecraft:structure_template_feature", "1.26.20", body)


def select_feature(name, weighted):
    return feature(name, "minecraft:weighted_random_feature", "1.14.0",
                   {"features": [[member, weight] for member, weight in weighted]})


def snap_feature(name, target, surface):
    return feature(name, "minecraft:snap_to_surface_feature", "1.20.30",
                   {"feature_to_snap": target, "vertical_search_range": SNAP_RANGE, "surface": surface})


def scatter_feature(name, target, iterations, spread):
    gaussian = {"distribution": "gaussian", "extent": [-spread, spread]}
    return feature(name, "minecraft:scatter_feature", "1.14.0", {
        "iterations": iterations,
        "x": gaussian,
        "y": {"distribution": "gaussian", "extent": [-4, 4]},
        "z": gaussian,
        "coordinate_eval_order": "zyx",
        "places_feature": target,
    })


def single_block_feature(name, block, attach_bottom=None):
    body = {
        "places_block": block,
        "enforce_survivability_rules": True,
        "enforce_placement_rules": True,
        "may_replace": ["minecraft:air"],
    }
    if attach_bottom:
        body["may_attach_to"] = {"bottom": attach_bottom}
    return feature(name, "minecraft:single_block_feature", "1.17.0", body)


def feature_rule(name, places, pass_name, iterations, scatter_chance=100):
    rule_name = f"{BIOME_TAG}_{name}"
    write_json(RULE_DIR, rule_name, {
        "format_version": "1.20.20",
        "minecraft:feature_rules": {
            "description": {"identifier": f"{NAMESPACE}:{rule_name}", "places_feature": places},
            "conditions": {
                "placement_pass": pass_name,
                "minecraft:biome_filter": [{"test": "has_biome_tag", "operator": "==", "value": BIOME_TAG}],
            },
            "distribution": {
                "iterations": iterations,
                "scatter_chance": scatter_chance,
                "x": {"distribution": "uniform", "extent": [0, 16]},
                "y": CAVE_Y,
                "z": {"distribution": "uniform", "extent": [0, 16]},
            },
        },
    })


def build_shell():
    floor = feature("floor_shell_feature", "minecraft:vegetation_patch_feature", "1.20.20", {
        "replaceable_blocks": ROCK,
        "ground_block": TALLOWSTONE,
        "vegetation_feature": f"{NAMESPACE}:null",
        "surface": "floor",
        "depth": {"range_min": 2, "range_max": 3},
        "vertical_range": 5,
        "vegetation_chance": 0,
        "horizontal_radius": {"range_min": 4, "range_max": 7},
        "extra_edge_column_chance": 0.3,
    })
    feature_rule("floor_shell", snap_feature("floor_shell_snap_feature", floor, "floor"), "underground_pass", 24)

    caps = []
    for name, seed, radius, depth in CEILING_CAPS:
        write_template(name, ceiling_cap(seed, radius, depth))
        caps.append((structure_feature(name, 0, CEILING_ALLOWLIST), 1))
    feature_rule("ceiling_shell", snap_feature("ceiling_shell_snap_feature",
                 select_feature("select_ceiling_cap_feature", caps), "ceiling"), "underground_pass", 24)


def build_honeycomb():
    veins = feature("honeycomb_vein_feature", "minecraft:ore_feature", "1.17.0", {
        "count": 6,
        "replace_rules": [{"places_block": "minecraft:honeycomb_block", "may_replace": [TALLOWSTONE] + ROCK}],
    })
    feature_rule("honeycomb_veins", veins, "underground_pass", 10)


def build_pillars():
    pillars = []
    for name, seed, height, base_radius in PILLARS:
        write_template(name, tallow_pillar(seed, height, base_radius))
        pillars.append((structure_feature(name, 1, FLOOR_ALLOWLIST), 1))
    feature_rule("pillars", snap_feature("pillar_snap_feature",
                 select_feature("select_pillar_feature", pillars), "floor"), "after_surface_pass", 6)


def build_forgotten_shelves():
    clusters = []
    for name, columns in SHELF_CLUSTERS.items():
        write_template(name, forgotten_shelves(columns))
        clusters.append((structure_feature(name, 1, FLOOR_ALLOWLIST), 1))
    feature_rule("forgotten_shelves", snap_feature("forgotten_shelves_snap_feature",
                 select_feature("select_forgotten_shelves_feature", clusters), "floor"), "after_surface_pass", 2, 20)


def build_decorations():
    wick = single_block_feature("wick_bloom_feature", EVER_WICK, [TALLOWSTONE])
    feature_rule("wick_blooms", scatter_feature("scatter_wick_bloom_feature",
                 snap_feature("wick_bloom_snap_feature", wick, "floor"), 4, 3), "after_surface_pass", 6)

    candles = []
    for count in range(1, 5):
        block = {"name": "minecraft:candle", "states": {"candles": count - 1, "lit": True}}
        candles.append((single_block_feature(f"candle_{count}_feature", block, [TALLOWSTONE]), 1))
    feature_rule("candle_clusters", scatter_feature("scatter_candle_cluster_feature",
                 snap_feature("candle_cluster_snap_feature", select_feature("select_candle_feature", candles), "floor"),
                 5, 2), "after_surface_pass", 3, 50)

    drip = single_block_feature("wax_drip_feature", WAX_DRIP)
    feature_rule("wax_drips", snap_feature("wax_drip_snap_feature", drip, "ceiling"), "after_surface_pass", 96)


if __name__ == "__main__":
    build_shell()
    build_honeycomb()
    build_pillars()
    build_forgotten_shelves()
    build_decorations()
    print("done")
