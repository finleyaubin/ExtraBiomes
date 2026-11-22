"""Generate the Larch Forest worldgen for Bedrock: tree and kettle lake templates, features and feature rules.

Usage:
    python tools/build_larch_forest.py

Ports the Java Larch Forest. Trees reuse Java's trunk and LarchFoliagePlacer maths to bake fixed variants (Bedrock
has no tree placer), with leaf shades picked per tree and per leaf to stand in for Java's position-based colour map.
Vanilla spruce, pine and ferns come from the `taiga` biome tag. Re-runnable: overwrites its own output.
"""
import math
import random

import build_floating_islands as bfi
from bedrock_features import FeatureWriter
from build_floating_islands import rand01
from mcstructure import T_byte, T_int, T_str

BIOME_TAG = "larch_forest"
WRITER = FeatureWriter(BIOME_TAG, BIOME_TAG, snap_range=12)
SURFACE_Y = "query.heightmap(variable.worldx, variable.worldz)"

# Leaves sit in a diamond-shaped crown; see Java LarchFoliagePlacer.
SPIRE_ROWS = 2
MIN_FOLIAGE_HEIGHT = 4
MIN_DROOPING_RADIUS = 2
RAGGED_EDGE_CHANCE = 0.3
DROOPING_TIP_CHANCE = 0.6
LEAF_SHADE_COUNT = 9
GREENNESS_BY_SHADE_ROW = 3

# name, trunk base height, extra random height, crown radius range, bare trunk range, variants
LARCH_TREES = [
    ("larch", 10, 4, (2, 2), (2, 3), 4),
    ("tall_larch", 16, 5, (3, 3), (3, 5), 3),
    ("young_larch", 4, 2, (1, 2), (1, 1), 3),
]
GREENNESS_WEIGHTS = [0.7, 0.2, 0.1]
LARCH_VARIANT_WEIGHT = 10
TALL_LARCH_VARIANT_WEIGHTS = [7, 7, 6]
YOUNG_SPRUCE_WEIGHTS = [22, 22, 21]
YOUNG_LARCH_WEIGHTS = [12, 12, 11]
BURIED_TRUNK_LAYERS = 3
CLEARING_NOISE_LEVEL = -0.1
TREES_PER_CHUNK_IN_CLEARING = 1
TREES_PER_CHUNK_IN_FOREST = 6
YOUNG_TREES_PER_CHUNK = 3
SHORT_DRY_GRASS_PATCHES_PER_CHUNK = 4
TALL_DRY_GRASS_PATCHES_PER_CHUNK = 1
SNOW_PATCHES_PER_CHUNK = 4
SNOW_PATCH_BLOCKS = 14
SNOW_PATCH_SPREAD = 3
CHUNKS_PER_MOSSY_ROCK = 3
CHUNKS_PER_ERRATIC = 4
CHUNKS_PER_KETTLE_LAKE = 10
KETTLE_LAKE_RADII = [3, 4, 5]
ERRATIC_RADII = [1.8, 2.6, 3.4]
ROCK_BURIED_LAYERS = 2
KETTLE_LAKE_GROUND_LEVEL = 3

LARCH_LEAVES = "extrabiomes:larch_leaves"
SOIL = ["minecraft:grass_block", "minecraft:dirt", "minecraft:podzol", "minecraft:coarse_dirt", "minecraft:snow",
        "minecraft:gravel", "minecraft:stone", "minecraft:sand"]
PLANTS = ["minecraft:short_grass", "minecraft:tall_grass", "minecraft:fern", "minecraft:large_fern",
          "minecraft:short_dry_grass", "minecraft:tall_dry_grass", "minecraft:snow_layer", "minecraft:sweet_berry_bush",
          "minecraft:red_flower", "minecraft:yellow_flower", "minecraft:double_plant"]
LEAVES = [LARCH_LEAVES, "minecraft:spruce_leaves"]
TREE_ALLOWLIST = ["minecraft:air"] + PLANTS + LEAVES
ROCK_ALLOWLIST = ["minecraft:air"] + PLANTS + SOIL
LAKE_ALLOWLIST = ["minecraft:air"] + PLANTS + SOIL + ["minecraft:andesite", "minecraft:granite", "minecraft:diorite", "minecraft:tuff", "minecraft:deepslate"]

bfi.BLOCKS.update({
    "larch_log": ("extrabiomes:larch_log", {"minecraft:block_face": T_str("up")}),
    "spruce_log": ("minecraft:spruce_log", {"pillar_axis": T_str("y")}),
    "spruce_leaves": ("minecraft:spruce_leaves", {"persistent_bit": T_byte(0), "update_bit": T_byte(0)}),
    "lake_water": ("minecraft:water", {"liquid_depth": T_int(0)}),
    "lake_gravel": ("minecraft:gravel", {}),
    "lake_stone": ("minecraft:stone", {"stone_type": T_str("stone")}),
})
for shade in range(LEAF_SHADE_COUNT):
    bfi.BLOCKS[f"larch_leaves_{shade}"] = (LARCH_LEAVES, {
        "extrabiomes:shade": T_int(shade), "extrabiomes:persist": T_int(0),
        "extrabiomes:placed": T_byte(0), "extrabiomes:decay": T_int(0)})


def round_half_up(value):
    return math.floor(value + 0.5)


def whorl_radius_at(depth, foliage_height, max_radius):
    progress = (depth - SPIRE_ROWS) / max(1, foliage_height - SPIRE_ROWS)
    return 1 + round_half_up((max_radius - 1) * progress)


def pick_greenness_row(rng):
    roll = rng.random()
    for row, weight in enumerate(GREENNESS_WEIGHTS):
        roll -= weight
        if roll < 0:
            return row
    return len(GREENNESS_WEIGHTS) - 1


def leaf_key(seed, greenness_row, x, y, z):
    """Shade changes roughly every few blocks sideways and up, like Java's position noise."""
    shade_column = int(rand01(seed, (x // 3) * 7919 + (y // 4) * 104729 + (z // 3) * 1299709) * GREENNESS_BY_SHADE_ROW)
    return f"larch_leaves_{greenness_row * GREENNESS_BY_SHADE_ROW + shade_column}"


def place_leaf(cells, rng_key, x, y, z):
    if cells.get((x, y, z), "").endswith("log"):
        return
    cells[(x, y, z)] = rng_key(x, y, z)


def foliage_row(cells, rng, rng_key, radius, y):
    for dx in range(-radius, radius + 1):
        for dz in range(-radius, radius + 1):
            distance = abs(dx) + abs(dz)
            if distance > radius:
                continue
            if distance == radius and dx != 0 and dz != 0 and rng.random() < RAGGED_EDGE_CHANCE:
                continue
            place_leaf(cells, rng_key, dx, y, dz)


def larch_tree(seed, base_height, extra_height, radius_range, bare_range):
    rng = random.Random(seed)
    height = base_height + rng.randint(0, extra_height)
    radius = rng.randint(*radius_range)
    foliage_height = max(MIN_FOLIAGE_HEIGHT, height - rng.randint(*bare_range))
    greenness_row = pick_greenness_row(rng)
    rng_key = lambda x, y, z: leaf_key(seed, greenness_row, x, y, z)

    cells = {(0, y, 0): "larch_log" for y in range(height)}
    for depth in range(foliage_height + 1):
        y = height + 1 - depth
        if depth < SPIRE_ROWS:
            place_leaf(cells, rng_key, 0, y, 0)
            continue
        whorl_radius = whorl_radius_at(depth, foliage_height, radius)
        if depth % 2 == 0:
            foliage_row(cells, rng, rng_key, whorl_radius, y)
            if whorl_radius >= MIN_DROOPING_RADIUS:
                for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    if rng.random() < DROOPING_TIP_CHANCE:
                        place_leaf(cells, rng_key, dx * whorl_radius, y - 1, dz * whorl_radius)
        else:
            foliage_row(cells, rng, rng_key, whorl_radius - 1, y)
    return cells


def young_spruce(seed):
    rng = random.Random(seed)
    height = 2 + rng.randint(0, 1)
    cells = {(0, y, 0): "spruce_log" for y in range(height)}
    cells[(0, height, 0)] = "spruce_leaves"
    for y, radius in ((height - 1, 1), (height - 2, 1)):
        for dx in range(-radius, radius + 1):
            for dz in range(-radius, radius + 1):
                if (dx, dz) != (0, 0) and (abs(dx) + abs(dz) <= radius or rng.random() < 0.5):
                    cells[(dx, y, dz)] = "spruce_leaves"
    return cells


def kettle_lake(seed, radius):
    """Layer 2 is the floor block the water replaces, so the template needs ground_level 3."""
    cells = {}
    reach = radius + 2
    for dx in range(-reach, reach + 1):
        for dz in range(-reach, reach + 1):
            distance = math.hypot(dx, dz)
            edge = radius + (rand01(seed, dx * 131 + dz * 7919) - 0.5)
            if distance <= edge:
                cells[(dx, 2, dz)] = "lake_water"
            if distance <= edge - 0.7:
                cells[(dx, 1, dz)] = "lake_water"
            elif distance <= edge + 1:
                cells[(dx, 1, dz)] = "lake_stone"
            if distance <= edge + 1:
                cells[(dx, 0, dz)] = "lake_gravel"
    return cells


def bury_trunk(cells):
    """Extends the trunk underground so a tree snapped on a slope never hangs in the air."""
    lifted = {(x, y + BURIED_TRUNK_LAYERS, z): key for (x, y, z), key in cells.items()}
    trunk_key = lifted[(0, BURIED_TRUNK_LAYERS, 0)]
    lifted.update({(0, y, 0): trunk_key for y in range(BURIED_TRUNK_LAYERS)})
    return lifted


def tree_feature(name, cells):
    WRITER.write_template(name, bury_trunk(cells))
    return WRITER.structure_feature(name, BURIED_TRUNK_LAYERS, TREE_ALLOWLIST)


def weighted(features, weights):
    return list(zip(features, weights))


def build_trees():
    larches = {}
    for name, base, extra, radii, bare, variants in LARCH_TREES:
        larches[name] = [tree_feature(f"{name}_{n + 1}", larch_tree(1000 * len(name) + n, base, extra, radii, bare))
                         for n in range(variants)]

    forest_mix = (weighted(larches["larch"], [LARCH_VARIANT_WEIGHT] * len(larches["larch"]))
                  + weighted(larches["tall_larch"], TALL_LARCH_VARIANT_WEIGHTS))
    trees = WRITER.snap_feature("tree_snap_feature", WRITER.select_feature("select_tree_feature", forest_mix), "floor")
    clearing = f"(query.noise(math.trunc(variable.originx / 80), math.trunc(variable.originz / 80)) < {CLEARING_NOISE_LEVEL})"
    WRITER.feature_rule("trees", trees, "after_surface_pass",
                        f"{clearing} ? {TREES_PER_CHUNK_IN_CLEARING} : {TREES_PER_CHUNK_IN_FOREST}", SURFACE_Y)

    spruces = [tree_feature(f"young_spruce_{n + 1}", young_spruce(n + 1)) for n in range(len(YOUNG_SPRUCE_WEIGHTS))]
    young_mix = weighted(spruces, YOUNG_SPRUCE_WEIGHTS) + weighted(larches["young_larch"], YOUNG_LARCH_WEIGHTS)
    young = WRITER.snap_feature("young_tree_snap_feature",
                                WRITER.select_feature("select_young_tree_feature", young_mix), "floor")
    WRITER.feature_rule("young_trees", young, "after_surface_pass", YOUNG_TREES_PER_CHUNK, SURFACE_Y)


def build_ground_cover():
    patch_y = {"distribution": "uniform", "extent": [f"{SURFACE_Y}-4", f"{SURFACE_Y}+4"]}
    WRITER.feature_rule("short_dry_grass", "extrabiomes:moorland/moorlands_scatter_small_dry_grass_feature",
                        "surface_pass", SHORT_DRY_GRASS_PATCHES_PER_CHUNK, patch_y)
    WRITER.feature_rule("tall_dry_grass", "extrabiomes:moorland/moorlands_scatter_tall_dry_grass_feature",
                        "surface_pass", TALL_DRY_GRASS_PATCHES_PER_CHUNK, patch_y)

    snow = WRITER.single_block_feature("snow_layer_feature", {"name": "minecraft:snow_layer", "states": {"height": 0}},
                                       ["minecraft:grass_block", "minecraft:podzol", "minecraft:coarse_dirt", "minecraft:dirt"])
    snow_patch = WRITER.scatter_feature("snow_patch_feature", WRITER.snap_feature("snow_snap_feature", snow, "floor"),
                                        SNOW_PATCH_BLOCKS, SNOW_PATCH_SPREAD)
    WRITER.feature_rule("snow_patches", snow_patch, "after_surface_pass", SNOW_PATCHES_PER_CHUNK, SURFACE_Y)

    patch_noise = "math.clamp(math.trunc(math.ceil((query.noise(math.trunc(variable.originx / 80), math.trunc(variable.originz / 80)) + 0.3) * 120)), 15, 160)"
    podzol = WRITER.feature("podzol_feature", "minecraft:aggregate_feature", "1.14.0",
                            {"early_out": "first_failure", "features": ["minecraft:optional_podzol_feature"]})
    WRITER.feature_rule("podzol", podzol, "after_surface_pass", patch_noise, SURFACE_Y)


def rock_blob(seed, radius):
    """A lumpy ellipsoid whose lower half is sunk into the ground, so it can never float."""
    cells = {}
    reach = math.ceil(radius) + 1
    for dx in range(-reach, reach + 1):
        for dy in range(-ROCK_BURIED_LAYERS, reach + 1):
            for dz in range(-reach, reach + 1):
                lump = 0.75 + 0.5 * rand01(seed, dx * 131 + dy * 7919 + dz * 104729)
                if (dx / radius) ** 2 + (dy / (radius * 0.8)) ** 2 + (dz / radius) ** 2 <= lump:
                    cells[(dx, dy + ROCK_BURIED_LAYERS, dz)] = "rock"
    return cells


def build_boulders():
    rocks = {"mossy_rock": ("minecraft:mossy_cobblestone", [1.4], CHUNKS_PER_MOSSY_ROCK),
             "granite_erratic": ("minecraft:granite", ERRATIC_RADII, CHUNKS_PER_ERRATIC / 0.4),
             "diorite_erratic": ("minecraft:diorite", ERRATIC_RADII, CHUNKS_PER_ERRATIC / 0.3),
             "andesite_erratic": ("minecraft:andesite", ERRATIC_RADII, CHUNKS_PER_ERRATIC / 0.3)}
    for name, (block, radii, chunks_per) in rocks.items():
        bfi.BLOCKS["rock"] = (block, {})
        variants = []
        for n, radius in enumerate(radii):
            template = f"{name}_{n + 1}"
            WRITER.write_template(template, rock_blob(n + 1, radius))
            variants.append((WRITER.structure_feature(template, ROCK_BURIED_LAYERS, ROCK_ALLOWLIST), 1))
        rock = WRITER.snap_feature(f"{name}_snap_feature", WRITER.select_feature(f"select_{name}_feature", variants), "floor")
        WRITER.feature_rule(name, rock, "after_surface_pass", 1, SURFACE_Y, scatter_chance=round(100 / chunks_per))


def build_kettle_lakes():
    lakes = []
    for n, radius in enumerate(KETTLE_LAKE_RADII):
        name = f"kettle_lake_{n + 1}"
        WRITER.write_template(name, kettle_lake(n + 1, radius))
        lakes.append((WRITER.structure_feature(name, KETTLE_LAKE_GROUND_LEVEL, LAKE_ALLOWLIST), 1))
    pool = WRITER.snap_feature("kettle_lake_snap_feature", WRITER.select_feature("select_kettle_lake_feature", lakes), "floor")
    WRITER.feature_rule("kettle_lakes", pool, "after_surface_pass", 1, SURFACE_Y, scatter_chance=round(100 / CHUNKS_PER_KETTLE_LAKE))


if __name__ == "__main__":
    build_trees()
    build_ground_cover()
    build_boulders()
    build_kettle_lakes()
    print("done")
