"""Generate the Bedrock sign block/recipe JSON for every ExtraBiomes wood type.

Usage:
    python tools/gen_signs.py

Four blocks per wood (standing / wall / hanging / wall-hanging) times four woods is 16
near-identical files, so they are generated from the table below rather than hand-copied.
Re-run after editing SHARED or a per-shape entry. Text handling lives in the pack's
scripts/blocks/Components/sign_text.js - none of it is expressible in block JSON.
"""
import json
import os

WOODS = [
    ("mystic", "#5A3E6B"),
    ("palm", "#C49A5B"),
    ("sky", "#D9E6F2"),
    ("gilded_sky", "#E0C05A"),
]

ROTATIONS = {"south": 0, "west": 90, "north": 180, "east": 270}

PACK = "ExtraBiomes - Bedrock/packs"


def rotation_permutations(offset=0):
    return [
        {
            "condition": "query.block_state('minecraft:cardinal_direction') == '%s'" % facing,
            "components": {"minecraft:transformation": {"rotation": [0, (degrees + offset) % 360, 0]}},
        }
        for facing, degrees in ROTATIONS.items()
    ]


def block(wood, colour, suffix, geometry, texture, box, scale_pivot, in_menu):
    identifier = "extrabiomes:%s_%s" % (wood, suffix)
    description = {
        "identifier": identifier,
        "traits": {"minecraft:placement_direction": {"enabled_states": ["minecraft:cardinal_direction"]}},
    }
    if in_menu:
        description["menu_category"] = {"category": "construction", "group": "itemGroup.name.sign"}
    else:
        # Wall variants are only ever reached by placing the standing item against a
        # vertical face, so they stay out of the creative menu like vanilla's own.
        description["menu_category"] = {"category": "none"}

    components = {
        "minecraft:geometry": {"identifier": geometry},
        "minecraft:material_instances": {
            "*": {"texture": texture, "render_method": "alpha_test", "ambient_occlusion": False}
        },
        "minecraft:transformation": {"scale": [0.667, 0.667, 0.667], "scale_pivot": scale_pivot},
        "minecraft:collision_box": box,
        "minecraft:selection_box": box,
        "minecraft:destructible_by_mining": {"seconds_to_destroy": 1},
        "tag:minecraft:is_axe_item_destructible": {},
        "minecraft:flammable": {"destroy_chance_modifier": 45, "catch_chance_modifier": 80},
        "minecraft:map_color": colour,
        "minecraft:light_dampening": 0,
        "tag:wood": {},
        "tag:extrabiomes:no_fence_join": {},
        "extrabiomes:sign_text": {},
    }
    if not in_menu:
        # Break the wall variant and you get the item the player actually placed.
        components["minecraft:loot"] = "loot_tables/blocks/%s_%s.json" % (wood, "hanging_sign" if "hanging" in suffix else "sign")

    return {
        "format_version": "1.20.100",
        "minecraft:block": {
            "description": description,
            "components": components,
            "permutations": rotation_permutations(),
        },
    }


SHAPES = [
    # suffix, geometry, texture key, selection box, scale pivot, shown in creative
    ("sign", "geometry.extrabiomes_sign", "sign",
     {"size": [16, 16, 4], "origin": [-8, 0, -2]}, [8, 0, 8], True),
    ("wall_sign", "geometry.extrabiomes_wall_sign", "sign",
     {"size": [16, 8, 2], "origin": [-8, 4, 6]}, [8, 8, 16], False),
    ("hanging_sign", "geometry.extrabiomes_hanging_sign", "hanging_sign",
     {"size": [14, 16, 2], "origin": [-7, 0, -1]}, [8, 16, 8], True),
    ("wall_hanging_sign", "geometry.extrabiomes_wall_hanging_sign", "hanging_sign",
     {"size": [14, 16, 10], "origin": [-7, 0, -1]}, [8, 16, 8], False),
]


def sign_recipe(wood, hanging):
    if hanging:
        return {
            "format_version": "1.16.0",
            "minecraft:recipe_shaped": {
                "description": {"identifier": "extrabiomes:%s_hanging_sign" % wood},
                "tags": ["crafting_table"],
                "pattern": ["X X", "###", "###"],
                "key": {"X": {"item": "minecraft:chain"},
                        "#": {"item": "extrabiomes:stripped_%s_log" % wood}},
                "result": [{"item": "extrabiomes:%s_hanging_sign" % wood, "count": 6}],
            },
        }
    return {
        "format_version": "1.16.0",
        "minecraft:recipe_shaped": {
            "description": {"identifier": "extrabiomes:%s_sign" % wood},
            "tags": ["crafting_table"],
            "pattern": ["###", "###", " | "],
            "key": {"#": {"item": "extrabiomes:%s_planks" % wood},
                    "|": {"item": "minecraft:stick"}},
            "result": [{"item": "extrabiomes:%s_sign" % wood, "count": 3}],
        },
    }


def loot_table(wood, hanging):
    item = "extrabiomes:%s_%s" % (wood, "hanging_sign" if hanging else "sign")
    return {"pools": [{"rolls": 1, "entries": [{"type": "item", "name": item, "weight": 1}]}]}


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as handle:
        json.dump(data, handle, indent=4)
        handle.write("\n")
    print("wrote", path)


def main():
    for wood, colour in WOODS:
        block_folder = wood + "_wood"
        recipe_folder = {"mystic": "mystic wood", "palm": "palm wood", "sky": "sky wood",
                         "gilded_sky": "gilded_sky wood"}[wood]
        for suffix, geometry, texture_kind, box, pivot, in_menu in SHAPES:
            texture = "extrabiomes_%s_%s" % (wood, texture_kind)
            write(os.path.join(PACK, "BP/blocks", block_folder, "%s_%s.json" % (wood, suffix)),
                  block(wood, colour, suffix, geometry, texture, box, pivot, in_menu))
        for hanging in (False, True):
            name = "%s_%s" % (wood, "hanging_sign" if hanging else "sign")
            write(os.path.join(PACK, "BP/recipes", recipe_folder, name + ".json"), sign_recipe(wood, hanging))
            write(os.path.join(PACK, "BP/loot_tables/blocks", name + ".json"), loot_table(wood, hanging))


if __name__ == "__main__":
    main()
