"""Generate the desert-temple structure templates plus their worldgen JSON (pool, structure, structure_set).

One temple per desert-like biome, each a re-skinned variation of the real vanilla desert pyramid saved from the
game (desert_temple_vanilla.nbt). Re-runnable: overwrites its outputs.
"""
import json
import os

import nbtlib
from nbtlib import Compound, Int, List, String

DATA = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "ExtraBiomes - Java",
                    "common", "src", "main", "resources", "data", "extrabiomes")
VANILLA = os.path.join(os.path.dirname(os.path.abspath(__file__)), "desert_temple_vanilla.nbt")
GROUND_Y = 16  # first air layer above ground in the vanilla template; floor is y=15
HALL_MIN, HALL_MAX = 1, 19
AIRS = {"minecraft:air", "minecraft:cave_air"}

SANDSTONE_FAMILY = {
    "minecraft:sandstone": "base", "minecraft:cut_sandstone": "cut", "minecraft:chiseled_sandstone": "chiseled",
    "minecraft:sandstone_stairs": "stairs", "minecraft:sandstone_slab": "slab",
    "minecraft:orange_terracotta": "accent1", "minecraft:blue_terracotta": "accent2",
}


def position_hash(x, y, z):
    return (x * 73856093 ^ y * 19349663 ^ z * 83492791) % 100


def bryce_mix(pos):
    roll = position_hash(*pos)
    return "minecraft:dripstone_block" if roll < 35 else "minecraft:packed_mud" if roll < 60 else "minecraft:sandstone"


def family(base, cut, chiseled, stairs, slab, accent1, accent2):
    n = lambda x: x if callable(x) or ":" in x else f"minecraft:{x}"
    return dict(base=n(base), cut=n(cut), chiseled=n(chiseled), stairs=n(stairs), slab=n(slab),
                accent1=n(accent1), accent2=n(accent2))


TEMPLATES = {
    "desert_bryce": dict(
        pal=family(bryce_mix, "cut_sandstone", "chiseled_sandstone", "sandstone_stairs", "cut_sandstone_slab",
                   "packed_mud", "dripstone_block"),
        tweaks=("back_towers", "dripstone_spikes")),
    "grand_oasis": dict(
        pal=family("red_sandstone", "cut_red_sandstone", "chiseled_red_sandstone", "red_sandstone_stairs",
                   "cut_red_sandstone_slab", "orange_terracotta", "cyan_terracotta"),
        tweaks=("pool",)),
    "future_desert": dict(
        pal=family("quartz_block", "quartz_bricks", "chiseled_quartz_block", "quartz_stairs", "quartz_slab",
                   "light_gray_concrete", "light_blue_concrete"),
        tweaks=("lanterns",)),
    "volcanic_moss_tundra": dict(
        pal=family("extrabiomes:black_sandstone", "extrabiomes:cut_black_sandstone",
                   "extrabiomes:chiseled_black_sandstone", "extrabiomes:black_sandstone_stairs",
                   "extrabiomes:cut_black_sandstone_slab", "moss_block", "pale_moss_block"),
        tweaks=("moss_carpet",)),
}


# (item, weight, min count, max count)
BIOME_LOOT = {
    "desert_bryce": [
        ("pointed_dripstone", 20, 4, 12), ("dripstone_block", 15, 4, 10), ("packed_mud", 15, 4, 10),
        ("mud_bricks", 10, 3, 8), ("clay_ball", 15, 3, 8), ("raw_copper", 10, 2, 6), ("amethyst_shard", 8, 1, 4),
        ("brick", 10, 2, 6), ("archer_pottery_sherd", 5, 1, 1), ("miner_pottery_sherd", 5, 1, 1),
        ("prize_pottery_sherd", 3, 1, 1)],
    "grand_oasis": [
        ("extrabiomes:palm_sapling", 20, 1, 3), ("melon_seeds", 15, 2, 6), ("sugar_cane", 15, 2, 6),
        ("cactus", 10, 2, 5), ("lapis_lazuli", 12, 3, 9), ("lily_pad", 10, 1, 4), ("pufferfish", 8, 1, 2),
        ("sea_pickle", 8, 1, 3), ("nautilus_shell", 5, 1, 1), ("heart_of_the_sea", 1, 1, 1)],
    "future_desert": [
        ("redstone", 20, 4, 12), ("quartz", 15, 3, 8), ("amethyst_shard", 10, 2, 6), ("glowstone_dust", 12, 2, 6),
        ("copper_ingot", 12, 2, 6), ("iron_nugget", 10, 4, 12), ("repeater", 6, 1, 2), ("comparator", 5, 1, 2),
        ("clock", 5, 1, 1), ("spyglass", 5, 1, 1), ("experience_bottle", 6, 1, 3), ("ender_pearl", 3, 1, 2),
        ("netherite_scrap", 1, 1, 1)],
    "volcanic_moss_tundra": [
        ("blackstone", 20, 4, 12), ("obsidian", 12, 2, 6), ("flint", 15, 2, 8), ("coal", 15, 3, 9),
        ("magma_cream", 8, 1, 3), ("blaze_powder", 8, 1, 3), ("fire_charge", 8, 1, 4), ("moss_block", 10, 2, 6),
        ("extrabiomes:mossy_pebble", 12, 2, 6), ("extrabiomes:pebble", 12, 2, 6), ("netherite_scrap", 1, 1, 1)],
}


def item_id(name):
    return name if ":" in name else f"minecraft:{name}"


def loot_entries(biome):
    return [{"type": "minecraft:item", "name": item_id(name), "weight": weight,
             "functions": [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": low, "max": high}}]}
            for name, weight, low, high in BIOME_LOOT[biome]]


def write_loot_tables(biome):
    name = f"desert_temple_{biome}"
    write_data("loot_table/chests", name, {"type": "minecraft:chest", "pools": [
        {"rolls": 1, "entries": [{"type": "minecraft:loot_table", "value": "minecraft:chests/desert_pyramid"}]},
        {"rolls": {"type": "minecraft:uniform", "min": 1, "max": 3}, "entries": loot_entries(biome)}]})
    archaeology_entries = [{"type": "minecraft:loot_table", "value": "minecraft:archaeology/desert_pyramid", "weight": 8}]
    archaeology_entries += [{**e, "weight": 1, "functions": []} for e in loot_entries(biome)]
    write_data("loot_table/archaeology", name, {"type": "minecraft:archaeology", "pools": [
        {"rolls": 1, "entries": archaeology_entries}], "random_sequence": f"extrabiomes:archaeology/{name}"})


def retarget_loot(nbts, biome):
    name = f"extrabiomes:{{}}/desert_temple_{biome}"
    for tag in nbts.values():
        kind = "chests" if str(tag["id"]) == "minecraft:chest" else "archaeology"
        tag["LootTable"] = String(name.format(kind))


def load_vanilla():
    f = nbtlib.load(VANILLA)
    palette = [(str(p["id"]), dict(p.get("properties", {}))) for p in f["palette"]]
    grid, nbts = {}, {}
    for b in f["blocks"]:
        pos = tuple(int(v) for v in b["pos"])
        name, props = palette[int(b["state"])]
        grid[pos] = (name, props)
        if "nbt" in b:
            tag = Compound({k: v for k, v in b["nbt"].items() if k != "LootTableSeed"})
            nbts[pos] = tag
    return tuple(int(v) for v in f["size"]), grid, nbts


def in_hall_hull(x, y, z):
    layer = y - GROUND_Y
    return layer >= 0 and HALL_MIN + layer <= x <= HALL_MAX - layer and z >= HALL_MIN + layer


def drop_outside_air(size, grid):
    """Remove air captured around the temple so placing it doesn't carve terrain (hall interior is kept)."""
    sx, sy, sz = size
    outside, stack = set(), [(-1, -1, -1)]
    while stack:
        p = stack.pop()
        if p in outside:
            continue
        x, y, z = p
        if not (-1 <= x <= sx and -1 <= y <= sy and -1 <= z <= sz):
            continue
        if p in grid and grid[p][0] not in AIRS:
            continue
        outside.add(p)
        stack.extend(((x + 1, y, z), (x - 1, y, z), (x, y + 1, z), (x, y - 1, z), (x, y, z + 1), (x, y, z - 1)))
    for p in outside:
        if p in grid and not in_hall_hull(*p):
            del grid[p]


def fill_buried_footprint(size, grid):
    """Solid stone where the capture was air, so a natural cave under the temple can't show through."""
    for x in range(size[0]):
        for z in range(size[2]):
            for y in range(GROUND_Y):
                grid.setdefault((x, y, z), ("minecraft:stone", {}))


def apply_palette(grid, pal):
    for pos, (name, props) in grid.items():
        role = SANDSTONE_FAMILY.get(name)
        if role:
            block = pal[role]
            grid[pos] = (block(pos) if callable(block) else block, props)


def mirror_front_towers_to_back(grid, nbts):
    flip = {"north": "south", "south": "north"}
    for (x, y, z), (name, props) in list(grid.items()):
        if y >= GROUND_Y and z >= 16 and (x <= 4 or x >= 16):
            props = {k: (flip.get(v, v) if k == "facing" else v) for k, v in props.items()}
            grid[(x, y, 20 - z)] = (name, props)


SPIKE_THICKNESS = {1: ["tip"], 2: ["frustum", "tip"], 3: ["base", "frustum", "tip"]}
SPIKE_HOSTS = {"minecraft:dripstone_block", "minecraft:packed_mud", "minecraft:sandstone"}


def place_spike(grid, pos, direction, length):
    step = 1 if direction == "up" else -1
    for i, thickness in enumerate(SPIKE_THICKNESS[length]):
        cell = (pos[0], pos[1] + step * (i + 1), pos[2])
        grid[cell] = ("minecraft:pointed_dripstone", {"thickness": thickness, "vertical_direction": direction,
                                                      "waterlogged": "false"})


def add_dripstone_spikes(grid, pal):
    max_y = max(y for _, y, _ in grid)
    for (x, y, z), (name, _) in sorted(grid.items()):
        if name not in SPIKE_HOSTS or y <= GROUND_Y:
            continue
        roll = position_hash(x + 5, y, z + 11)
        length = 1 + roll % 3
        if roll < 14:
            cells = [(x, y + i, z) for i in range(1, length + 1)]
            if y + length <= max_y and all(c not in grid for c in cells):
                place_spike(grid, (x, y, z), "up", length)
        elif roll < 30:
            cells = [(x, y - i, z) for i in range(1, length + 1)]
            if all(grid.get(c, ("",))[0] in AIRS for c in cells):
                place_spike(grid, (x, y, z), "down", length)


def add_pool(grid, pal):
    for x in range(8, 13):
        for z in range(3, 6):
            grid[(x, GROUND_Y - 1, z)] = ("minecraft:water", {"level": "0"})


def add_lanterns(grid, pal):
    for pos, (name, _) in list(grid.items()):
        if pos[1] == GROUND_Y + 1 and name == pal["chiseled"] and pos[0] in (4, 16):
            grid[pos] = ("minecraft:sea_lantern", {})


def add_moss_carpet(grid, pal):
    for x in range(3, 18):
        for z in range(3, 14):
            above = (x, GROUND_Y, z)
            if (x * 7 + z * 13) % 5 == 0 and grid.get(above, ("",))[0] in AIRS | {None} and (x, GROUND_Y - 1, z) in grid:
                grid[above] = ("minecraft:moss_carpet", {})


TWEAKS = {"back_towers": lambda grid, pal: mirror_front_towers_to_back(grid, None),
          "dripstone_spikes": add_dripstone_spikes, "pool": add_pool, "lanterns": add_lanterns, "moss_carpet": add_moss_carpet}


def build(biome, spec):
    size, grid, nbts = load_vanilla()
    drop_outside_air(size, grid)
    fill_buried_footprint(size, grid)
    apply_palette(grid, spec["pal"])
    retarget_loot(nbts, biome)
    for tweak in spec["tweaks"]:
        TWEAKS[tweak](grid, spec["pal"])
    return size, grid, nbts


def write_template(name, size, grid, nbts):
    names, blocks = {}, []
    for pos in sorted(grid, key=lambda p: (p[1], p[2], p[0])):
        name_props = (grid[pos][0], tuple(sorted(grid[pos][1].items())))
        idx = names.setdefault(name_props, len(names))
        entry = Compound({"pos": List[Int]([Int(v) for v in pos]), "state": Int(idx)})
        if pos in nbts:
            entry["nbt"] = nbts[pos]
        blocks.append(entry)
    pal_list = []
    for (bname, props), _ in sorted(names.items(), key=lambda kv: kv[1]):
        c = Compound({"id": String(bname)})
        if props:
            c["properties"] = Compound({k: String(v) for k, v in props})
        pal_list.append(c)
    root = nbtlib.File({"DataVersion": Int(5023), "size": List[Int]([Int(v) for v in size]),
                        "palette": List[Compound](pal_list), "blocks": List[Compound](blocks),
                        "entities": List[Compound]([])}, gzipped=True)
    path = os.path.join(DATA, "structure", "desert_temple", f"{name}.nbt")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    root.save(path)


def write_json(kind, name, content):
    write_data(f"worldgen/{kind}", name, content)


def write_data(folder, name, content):
    path = os.path.join(DATA, folder, f"{name}.json")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(content, f, indent=2)
        f.write("\n")


def write_worldgen(biome, salt):
    name = f"desert_temple_{biome}"
    write_json("template_pool", name, {
        "name": f"extrabiomes:{name}", "fallback": "minecraft:empty",
        "elements": [{"weight": 1, "element": {
            "element_type": "minecraft:single_pool_element",
            "location": f"extrabiomes:desert_temple/{biome}",
            "processors": "minecraft:empty", "projection": "rigid"}}]})
    write_json("structure", name, {
        "type": "minecraft:jigsaw", "biomes": f"extrabiomes:{biome}", "step": "surface_structures",
        "terrain_adaptation": "beard_thin", "start_pool": f"extrabiomes:{name}",
        "start_height": {"absolute": -GROUND_Y}, "project_start_to_heightmap": "OCEAN_FLOOR_WG",
        "size": 1, "max_distance_from_center": 80, "use_expansion_hack": False, "spawn_overrides": {}})
    write_json("structure_set", name, {
        "structures": [{"structure": f"extrabiomes:{name}", "weight": 1}],
        "placement": {"type": "minecraft:random_spread", "spacing": 32, "separation": 8, "salt": salt,
                      "spread_type": "linear"}})


for i, (biome, spec) in enumerate(TEMPLATES.items()):
    size, grid, nbts = build(biome, spec)
    write_template(biome, size, grid, nbts)
    write_worldgen(biome, 14357617 + i)
    write_loot_tables(biome)
