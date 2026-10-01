"""Generate the Glacier overhaul prefabs, features and feature rules for Bedrock (port of Java beta 9's Glacier).

Every template uses the ground_level convention: layer `ground_level` is the air layer just above the ground, so the
feature can be fed either a snapped cave floor/ceiling position or the surface heightmap.
Re-runnable: overwrites its own output. Does not touch the snow drift files.
"""
import math
import os

import build_floating_islands as bfi
from build_floating_islands import rand01, rim_scale, rim_terms, write_json
from mcstructure import T_int, T_str

BP = bfi.BP
NAMESPACE = "extrabiomes"
STRUCT_DIR = os.path.join(BP, "structures", "extrabiomes", "glacier")
FEATURE_DIR = os.path.join(BP, "features", "glacier")
RULE_DIR = os.path.join(BP, "feature_rules", "glacier")
LOOT_TABLE = "loot_tables/chests/ice_vault.json"
MAX_SPAN = 44

bfi.STRUCT_DIR = STRUCT_DIR
bfi.LOOT_TABLE = LOOT_TABLE
bfi.BLOCKS.update({
    "air": ("minecraft:air", {}),
    "basalt": ("minecraft:basalt", {"pillar_axis": T_str("y")}),
    "smooth_basalt": ("minecraft:smooth_basalt", {}),
    "magma": ("minecraft:magma", {}),
    "blue_ice": ("minecraft:blue_ice", {}),
    "ice": ("minecraft:ice", {}),
    "snow": ("minecraft:snow", {}),
    "lava": ("minecraft:lava", {"liquid_depth": T_int(0)}),
    "soul_sand": ("minecraft:soul_sand", {}),
})

ORES = [f"minecraft:{prefix}{ore}_ore" for prefix in ("", "deepslate_") for ore in
        ("coal", "iron", "copper", "gold", "redstone", "lapis", "diamond", "emerald")]
ROCK = ["minecraft:stone", "minecraft:andesite", "minecraft:diorite", "minecraft:granite", "minecraft:tuff",
        "minecraft:deepslate", "minecraft:dirt", "minecraft:gravel", "minecraft:sand", "minecraft:sandstone",
        "minecraft:calcite", "minecraft:ice", "minecraft:packed_ice", "minecraft:blue_ice", "minecraft:snow",
        "minecraft:basalt", "minecraft:smooth_basalt", "minecraft:magma"] + ORES
CAVE = ["minecraft:air"] + ROCK
SURFACE = CAVE + ["minecraft:snow_layer", "minecraft:powder_snow"]


def disc(cells, cy, radius, key, cx=0, cz=0, rng=None):
    limit = radius * radius + radius
    r = math.ceil(radius)
    for dx in range(-r, r + 1):
        for dz in range(-r, r + 1):
            d2 = dx * dx + dz * dz
            if d2 <= limit and not (rng and d2 > radius * radius and rng(dx, dz) < 0.25):
                cells[(cx + dx, cy, cz + dz)] = key


def emit(name, cells, ground, allowlist):
    xs, ys, zs = zip(*cells)
    x0, y0, z0 = min(xs), min(ys), min(zs)
    sx, sz = max(xs) - x0 + 1, max(zs) - z0 + 1
    sy = max(max(ys) - y0 + 1, ground - y0 + 1)
    shifted = {(x - x0, y - y0, z - z0): v for (x, y, z), v in cells.items()}
    assert sx <= MAX_SPAN and sz <= MAX_SPAN, f"{name} is {sx}x{sz}"
    bfi.write_bedrock_structure(name, sx, sy, sz, shifted)
    print(f"{name}: {sx}x{sy}x{sz}, {len(cells)} blocks, ground_level {ground - y0}")
    return {"name": name, "ground_level": ground - y0, "allowlist": allowlist}


def bowl(seed, radius, inner, shell, depth, shrink, frozen=False):
    top = depth
    terms = rim_terms(seed)
    half = math.ceil(radius * 1.4) + 2
    cells = {}
    for dx in range(-half, half + 1):
        for dz in range(-half, half + 1):
            blocks = math.hypot(dx, dz)
            scale = rim_scale(terms, math.atan2(dz, dx))
            d = blocks / (radius * scale)
            margin = 1.3 / (radius * scale)
            for y in range(0, top + 1):
                limit = 1 - shrink * (top - y)
                if y >= 1 and d <= limit:
                    cells[(dx, y, dz)] = "ice" if frozen and y == top else inner
                elif d <= limit + margin:
                    cells[(dx, y, dz)] = shell
    return cells, top + 1


def lava_pool(seed, radius):
    cells, ground = bowl(seed, radius, "lava", "basalt", 3, 0.18)
    return emit(f"lava_pool_{seed}", cells, ground, CAVE)


def meltwater_pool(seed, radius, frozen):
    cells, ground = bowl(seed, radius, "water", "smooth_basalt", 3, 0.18, frozen)
    prefix = "frozen_pool" if frozen else "meltwater_pool"
    return emit(f"{prefix}_{seed}", cells, ground, CAVE)


def pond(seed, radius):
    cells, ground = bowl(seed, radius, "water", "blue_ice", 2, 0.3)
    return emit(f"pond_{seed}", cells, ground, SURFACE)


def lavafall_cap():
    cells = {(0, 0, 0): "lava"}
    for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)):
        cells[(dx, 0, dz)] = "basalt"
    disc(cells, 1, 3, "basalt")
    disc(cells, 2, 3, "basalt")
    return emit("lavafall_cap", cells, 0, CAVE)


def lavafall_pad():
    cells = {}
    disc(cells, 0, 3, "basalt")
    disc(cells, 1, 4, "basalt")
    return emit("lavafall_pad", cells, 2, CAVE)


def pillar(seed, name, key, height, base_radius, hanging):
    cells = {}
    for layer in range(height):
        radius = 0 if layer == height - 1 else round(base_radius * (1 - 0.6 * layer / height))
        y = height - 1 - layer if hanging else layer
        disc(cells, y, radius, key, rng=lambda dx, dz, layer=layer: rand01(seed, dx * 131 + dz * 7919 + layer * 17))
    return emit(name, cells, height - 1 if hanging else 0, CAVE)


def ice_vault():
    cells = {(x, y, z): "ice" for x in range(3) for y in range(3) for z in range(3)}
    cells[(1, 1, 1)] = "chest"
    return emit("ice_vault", cells, 0, ROCK)


def boulder(seed, radius):
    g = bfi.Grid(seed)
    g.ball(0, 0, 0, radius, "andesite", 0, keep=0.93, flat=0.8)
    return emit(f"boulder_{seed}", g.cells, 1, SURFACE)


def crevasse(seed, half_length, depth):
    bridge_start = int(rand01(seed, 1) * half_length) - half_length // 2
    phase = rand01(seed, 2) * math.tau
    ground = depth
    carved, bridge = set(), {}
    for i in range(-half_length, half_length + 1):
        taper = abs(i) / half_length
        column_depth = int(depth * (1 - taper * taper))
        if column_depth <= 0:
            continue
        meander = round(math.sin(i * 0.4 + phase) * 1.5)
        is_bridge = i in (bridge_start, bridge_start + 1)
        reach = 1 if taper < 0.5 else 0
        for across in range(-reach, reach + 1):
            wall_depth = column_depth if across == 0 else column_depth * 6 // 10
            z = meander + across
            start = ground - 3 if is_bridge else ground + 2
            for y in range(start, ground - wall_depth - 1, -1):
                carved.add((i, y, z))
            if is_bridge:
                for y in (ground - 1, ground - 2):
                    bridge[(i, y, z)] = "snow"
    cells = {pos: "air" for pos in carved}
    cells.update(bridge)
    for (x, y, z) in carved:
        if y > ground - 1:
            continue
        for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            n = (x + dx, y, z + dz)
            if n not in cells and rand01(seed, x * 73 + y * 179 + z * 331 + dx * 7 + dz * 13) < 0.3:
                cells[n] = "blue_ice"
    return emit(f"crevasse_{seed}", cells, ground, SURFACE)


def stream(seed, length, vent):
    phase = rand01(seed, 1) * math.tau
    amp = 2 + rand01(seed, 2) * 2
    ground = 3
    path = []
    z = 0
    for x in range(length):
        target = round(math.sin(x * 0.45 + phase) * amp)
        while z != target:
            path.append((x, z))
            z += 1 if target > z else -1
        path.append((x, z))
    end = (length + 1, z)
    cells = {}
    pool = {(end[0] + dx, end[1] + dz) for dx in (-1, 0, 1) for dz in (-1, 0, 1)}
    for (x, z) in path:
        cells[(x, ground - 1, z)] = "water"
        cells[(x, ground - 2, z)] = "blue_ice"
        cells[(x, ground, z)] = "air"
        cells[(x, ground + 1, z)] = "air"
    for (x, z) in pool:
        cells[(x, ground - 1, z)] = "water"
        cells[(x, ground - 2, z)] = "water"
        cells[(x, ground - 3, z)] = "blue_ice"
        cells[(x, ground, z)] = "air"
        cells[(x, ground + 1, z)] = "air"
    if vent:
        cells[(end[0], ground - 3, end[1])] = "soul_sand"
    return emit(f"stream_{seed}", cells, ground, SURFACE)


def shaft(seed, radius, depth, chamber_radius, dogleg):
    ground = depth + 1
    cells = {}
    carved = set()
    wobble_phase = rand01(seed, 1) * math.tau
    turn_y = ground - depth // 2
    chamber_cx, chamber_cz = dogleg, 0
    chamber_floor = 1
    for y in range(chamber_floor, ground + 3):
        shift = dogleg * min(1, max(0, (turn_y - y) / 6))
        cx = shift + math.sin(y * 0.15 + wobble_phase) * 0.8
        for dx in range(-13, 14):
            for dz in range(-8, 9):
                if math.hypot(dx - cx, dz) <= radius:
                    carved.add((dx, y, dz))
    chamber_height = 5
    chamber_cy = chamber_floor + 3
    r = math.ceil(chamber_radius)
    for dx in range(-r, r + 1):
        for dz in range(-r, r + 1):
            for y in range(chamber_floor, chamber_cy + chamber_height + 1):
                dy = y - chamber_cy
                if ((dx * dx + dz * dz) / (chamber_radius * chamber_radius) + (dy * dy) / (chamber_height * chamber_height)) <= 1:
                    carved.add((chamber_cx + dx, y, chamber_cz + dz))
    cells.update({pos: "air" for pos in carved})
    for (x, y, z) in carved:
        if y < chamber_floor + 1 and (x - chamber_cx) ** 2 + (z - chamber_cz) ** 2 <= 9:
            cells[(x, y - 1, z)] = "blue_ice"
            cells[(x, y, z)] = "water"
        elif y == chamber_floor:
            cells[(x, y - 1, z)] = "blue_ice"
        elif y <= ground - 1:
            for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                n = (x + dx, y, z + dz)
                if n not in cells and rand01(seed, x * 73 + y * 179 + z * 331 + dx * 7 + dz * 13) < 0.2:
                    cells[n] = "blue_ice"
    inflow_x = -math.ceil(radius) - 1
    for step in range(9):
        x = inflow_x - step
        cells[(x, ground - 1, 0)] = "water"
        cells[(x, ground - 2, 0)] = "blue_ice"
        cells[(x, ground, 0)] = "air"
        cells[(x, ground + 1, 0)] = "air"
    return emit(f"shaft_{seed}", cells, ground, SURFACE)


def structure_feature(template, grounded=False):
    name = template["name"]
    body = {
        "description": {"identifier": f"{NAMESPACE}:glacier/{name}_feature"},
        "structure_name": f"{NAMESPACE}:glacier/{name}",
        "facing_direction": "random",
        "rotate_around_center": True,
        "constraints": {"block_intersection": {"block_allowlist": template["allowlist"]}},
    }
    if grounded:
        body["constraints"]["grounded"] = {}
    if template["ground_level"]:
        body["ground_level"] = template["ground_level"]
    write_json(os.path.join(FEATURE_DIR, f"{name}_feature.json"),
               {"format_version": "1.26.20", "minecraft:structure_template_feature": body})
    return f"{NAMESPACE}:glacier/{name}_feature"


def select_feature(name, members):
    write_json(os.path.join(FEATURE_DIR, f"{name}.json"), {
        "format_version": "1.14.0",
        "minecraft:weighted_random_feature": {
            "description": {"identifier": f"{NAMESPACE}:glacier/{name}"},
            "features": [[feature, weight] for feature, weight in members],
        },
    })
    return f"{NAMESPACE}:glacier/{name}"


def snap_feature(name, target, surface, search_range):
    write_json(os.path.join(FEATURE_DIR, f"{name}.json"), {
        "format_version": "1.20.30",
        "minecraft:snap_to_surface_feature": {
            "description": {"identifier": f"{NAMESPACE}:glacier/{name}"},
            "feature_to_snap": target,
            "vertical_search_range": search_range,
            "surface": surface,
        },
    })
    return f"{NAMESPACE}:glacier/{name}"


def ore_feature(name, count, places, replaces):
    write_json(os.path.join(FEATURE_DIR, f"{name}.json"), {
        "format_version": "1.17.0",
        "minecraft:ore_feature": {
            "description": {"identifier": f"{NAMESPACE}:glacier/{name}"},
            "count": count,
            "replace_rules": [{"places_block": places, "may_replace": replaces}],
        },
    })
    return f"{NAMESPACE}:glacier/{name}"


def aggregate_feature(name, members):
    write_json(os.path.join(FEATURE_DIR, f"{name}.json"), {
        "format_version": "1.13.0",
        "minecraft:aggregate_feature": {
            "description": {"identifier": f"{NAMESPACE}:glacier/{name}"},
            "features": members,
        },
    })
    return f"{NAMESPACE}:glacier/{name}"


def feature_rule(name, places, pass_name, iterations, y, scatter_chance=100):
    write_json(os.path.join(RULE_DIR, f"glacier_{name}.json"), {
        "format_version": "1.14.0",
        "minecraft:feature_rules": {
            "description": {"identifier": f"{NAMESPACE}:glacier_{name}", "places_feature": places},
            "conditions": {
                "placement_pass": pass_name,
                "minecraft:biome_filter": [{"test": "has_biome_tag", "operator": "==", "value": "glacier"}],
            },
            "distribution": {
                "iterations": iterations,
                "scatter_chance": scatter_chance,
                "x": {"distribution": "uniform", "extent": [0, 16]},
                "y": y,
                "z": {"distribution": "uniform", "extent": [0, 16]},
            },
        },
    })


def between(low, high):
    return {"distribution": "uniform", "extent": [low, high]}


HEIGHT = "query.heightmap(variable.worldx, variable.worldz)"
ABOVE_SEA = f"{HEIGHT} > 63 ? {HEIGHT} : 300"
STONE_FAMILY = ["minecraft:stone", "minecraft:andesite", "minecraft:diorite", "minecraft:granite",
                "minecraft:dirt", "minecraft:grass_block", "minecraft:sand", "minecraft:gravel",
                "minecraft:sandstone", "minecraft:deepslate"]


def build_ores():
    blue = ore_feature("blue_ice_feature", 64, "minecraft:blue_ice",
                       STONE_FAMILY + ["minecraft:packed_ice", "minecraft:ice"])
    magma = ore_feature("magma_fissure_feature", 6, "minecraft:magma",
                        ["minecraft:packed_ice", "minecraft:blue_ice", "minecraft:stone", "minecraft:deepslate"])
    moraine = ore_feature("moraine_feature", 12, "minecraft:gravel",
                          ["minecraft:ice", "minecraft:packed_ice", "minecraft:blue_ice", "minecraft:snow"])
    feature_rule("blue_ice", blue, "underground_pass", 25, between(-64, 8))
    feature_rule("magma_fissure", magma, "underground_pass", 14, between(-64, 40))
    feature_rule("moraine", moraine, "after_surface_pass", 8,
                 between(f"{HEIGHT} - 2", f"{HEIGHT} - 1"))


def build_caves():
    pools = [structure_feature(lava_pool(seed, radius)) for seed, radius in ((1, 2.5), (2, 3.5), (3, 4.5))]
    feature_rule("lava_pool", snap_feature("lava_pool_snap_feature", select_feature("select_lava_pool_feature",
                 [(f, 1) for f in pools]), "floor", 12), "after_surface_pass", 10, between(-48, 48), 100)

    water = [structure_feature(meltwater_pool(seed, radius, frozen))
             for seed, radius, frozen in ((1, 2.5, False), (2, 3.5, False), (3, 3.5, True), (4, 4.5, True))]
    feature_rule("meltwater_pool", snap_feature("meltwater_pool_snap_feature", select_feature(
        "select_meltwater_pool_feature", [(f, 1) for f in water]), "floor", 12),
        "after_surface_pass", 8, between(-32, 40), 100)

    cap = snap_feature("lavafall_cap_snap_feature", structure_feature(lavafall_cap()), "ceiling", 12)
    pad = snap_feature("lavafall_pad_snap_feature", structure_feature(lavafall_pad()), "floor", 20)
    feature_rule("lavafall", aggregate_feature("lavafall_feature", [cap, pad]), "after_surface_pass", 60,
                 between(-56, 48), 100)

    snow = [structure_feature(pillar(seed, f"snow_pillar_{seed}", "snow", height, radius, False))
            for seed, height, radius in ((1, 4, 1), (2, 6, 2), (3, 8, 2), (4, 9, 2))]
    feature_rule("snow_pillar", snap_feature("snow_pillar_snap_feature", select_feature(
        "select_snow_pillar_feature", [(f, 1) for f in snow]), "floor", 12),
        "after_surface_pass", 30, between(-56, 48), 100)

    basalt = [structure_feature(pillar(seed, f"basalt_pillar_{seed}", "basalt", height, radius, True))
              for seed, height, radius in ((1, 3, 1), (2, 5, 2), (3, 6, 2), (4, 8, 2))]
    feature_rule("basalt_pillar", snap_feature("basalt_pillar_snap_feature", select_feature(
        "select_basalt_pillar_feature", [(f, 1) for f in basalt]), "ceiling", 12),
        "after_surface_pass", 30, between(-56, 48), 100)

    vault = structure_feature(ice_vault())
    feature_rule("ice_vault", vault, "after_surface_pass", 1, between(-56, 40), 17)


def build_surface():
    crevasses = [structure_feature(crevasse(seed, half_length, depth), grounded=True)
                 for seed, half_length, depth in ((1, 6, 14), (2, 8, 18), (3, 10, 22), (4, 12, 28), (5, 13, 33))]
    feature_rule("crevasse", select_feature("select_crevasse_feature", [(f, 1) for f in crevasses]),
                 "after_surface_pass", 1, ABOVE_SEA, 17)

    streams = [structure_feature(stream(seed, length, vent), grounded=True)
               for seed, length, vent in ((1, 12, True), (2, 14, False), (3, 16, True), (4, 13, False))]
    feature_rule("meltwater_stream", select_feature("select_meltwater_stream_feature", [(f, 1) for f in streams]),
                 "after_surface_pass", 1, ABOVE_SEA, 40)

    shafts = [structure_feature(shaft(seed, radius, depth, chamber, dogleg), grounded=True)
              for seed, radius, depth, chamber, dogleg in ((1, 2.5, 32, 8, 3), (2, 3.5, 38, 9.5, 0), (3, 5.5, 44, 9.5, -4))]
    feature_rule("erosion_shaft", select_feature("select_erosion_shaft_feature", [(f, 1) for f in shafts]),
                 "after_surface_pass", 1, ABOVE_SEA, 8)

    ponds = [structure_feature(pond(seed, radius), grounded=True) for seed, radius in ((1, 3), (2, 4.5), (3, 6))]
    feature_rule("pond", select_feature("select_pond_feature", [(f, 1) for f in ponds]),
                 "after_surface_pass", 1, ABOVE_SEA, 8)

    boulders = [structure_feature(boulder(seed, radius), grounded=True) for seed, radius in ((1, 1.8), (2, 2.4), (3, 3.2))]
    feature_rule("erratic", select_feature("select_erratic_feature", [(f, 1) for f in boulders]),
                 "after_surface_pass", 1, ABOVE_SEA, 30)


if __name__ == "__main__":
    build_ores()
    build_caves()
    build_surface()
    print("done")
