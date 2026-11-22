"""Generate the Bedrock wooden shelf blocks, recipes and names (Mystic, Sky, Palm, Gilded Sky and Larch).

Usage:
    python tools/build_shelves.py

Shelves hold up to three item stacks (see scripts/blocks/Components/shelf.js). Java's redstone hotbar swapping and comparator output
are not ported. Re-runnable: overwrites its own output.
"""
import json
import os
import shutil

from build_larch_wood import JAVA_ASSETS, JAVA_LANG, PACKS, write_json, write_text

BP = os.path.join(PACKS, "BP")
RP = os.path.join(PACKS, "RP")
WOODS = ["mystic", "palm", "sky", "gilded_sky", "larch"]
SHELF_TEXTURE_SIZE = 32
JAVA_TEXTURES = os.path.join(JAVA_ASSETS, "textures", "block", "wood")
# Vanilla's shelf model, in Java units (16 per block, uv on a 16x16 grid scaled onto the 32x32 texture). Front faces north.
JAVA_ELEMENTS = [
    ([0, 0, 13], [16, 16, 16], {"east": [8, 0, 9.5, 8], "south": [8, 0, 16, 8], "west": [14.5, 0, 16, 8],
                                "up": [16, 5, 8, 3.5], "down": [16, 6, 8, 4.5]}),
    ([0, 0, 11], [16, 4, 13], {"north": [0, 6, 8, 8], "east": [1.5, 6, 2.5, 8], "west": [5.5, 6, 6.5, 8],
                               "up": [8, 3.5, 16, 4.5], "down": [16, 4.5, 8, 3.5]}),
    ([0, 12, 11], [16, 16, 13], {"north": [0, 0, 8, 2], "east": [1.5, 0, 2.5, 2], "west": [5.5, 0, 6.5, 2],
                                 "up": [16, 6, 8, 5], "down": [8, 5, 16, 6]}),
    ([0, 4, 13], [16, 12, 13], {"north": [0, 2, 8, 6]}),
]
# Keyed by the placement trait's cardinal_direction, the way the player faced; the shelf's back is on that side and
# its front points back at the player. Each maps the model's +z back onto the facing's back as a quarter turn of (x, z).
# Bedrock reports east and west the opposite way round to north and south, so those two are swapped.
FACING_TURN = {
    "south": lambda x, z: (x, z),
    "east": lambda x, z: (-z, x),
    "north": lambda x, z: (-x, -z),
    "west": lambda x, z: (z, -x),
}
FACE_DIRECTION = {"north": (0, -1), "south": (0, 1), "east": (1, 0), "west": (-1, 0)}
BACK_BOX = ([-8, 0, 3], [16, 16, 5])
UV_SCALE = SHELF_TEXTURE_SIZE / 16


def turn_box(facing, origin, size):
    turn = FACING_TURN[facing]
    corners = [turn(origin[0] + dx * size[0], origin[2] + dz * size[2]) for dx in (0, 1) for dz in (0, 1)]
    xs, zs = zip(*corners)
    return [min(xs), origin[1], min(zs)], [max(xs) - min(xs), size[1], max(zs) - min(zs)]


# Bedrock's cube faces are mirrored along x: the face named east is drawn on the -x side.
BEDROCK_FACE_NAME = {"north": "north", "south": "south", "east": "west", "west": "east"}


def turn_face(facing, face):
    if face not in FACE_DIRECTION:
        return face
    turned = FACING_TURN[facing](*FACE_DIRECTION[face])
    world_face = next(name for name, direction in FACE_DIRECTION.items() if direction == turned)
    return BEDROCK_FACE_NAME[world_face]


def java_element_to_cube(facing, java_from, java_to, faces):
    origin = [java_from[0] - 8, java_from[1], java_from[2] - 8]
    size = [java_to[i] - java_from[i] for i in range(3)]
    turned_origin, turned_size = turn_box(facing, origin, size)
    return {"origin": turned_origin, "size": turned_size, "uv": {
        turn_face(facing, face): face_uv(facing, face, uv) for face, uv in faces.items()}}


def face_uv(facing, face, uv):
    region = [uv[0] * UV_SCALE, uv[1] * UV_SCALE]
    extent = [(uv[2] - uv[0]) * UV_SCALE, (uv[3] - uv[1]) * UV_SCALE]
    quarter_turned = facing in ("east", "west") and face in ("up", "down")
    return {"uv": region, "uv_size": extent[::-1] if quarter_turned else extent}


def geometry_id(facing):
    return f"geometry.extrabiomes_shelf_{facing}"


def write_geometry():
    write_json(os.path.join(RP, "models", "blocks", "extrabiomes_shelf.geo.json"), {
        "format_version": "1.12.0",
        "minecraft:geometry": [{
            "description": {"identifier": geometry_id(facing), "texture_width": SHELF_TEXTURE_SIZE,
                            "texture_height": SHELF_TEXTURE_SIZE, "visible_bounds_width": 2, "visible_bounds_height": 2,
                            "visible_bounds_offset": [0, 0.5, 0]},
            "bones": [{"name": "shelf", "pivot": [0, 0, 0],
                       "cubes": [java_element_to_cube(facing, *element) for element in JAVA_ELEMENTS]}],
        } for facing in FACING_TURN],
    })


def copy_textures():
    folder_name = lambda wood: f"{wood.replace('_', ' ')} wood"
    for wood in WOODS:
        folder = os.path.join(RP, "textures", "blocks", folder_name(wood))
        os.makedirs(folder, exist_ok=True)
        shutil.copyfile(os.path.join(JAVA_TEXTURES, wood, f"{wood}_shelf.png"), os.path.join(folder, f"{wood}_shelf.png"))


def register_textures():
    path = os.path.join(RP, "textures", "terrain_texture.json")
    with open(path, encoding="utf-8") as f:
        atlas = json.load(f)
    for wood in WOODS:
        atlas["texture_data"][f"extrabiomes_{wood}_shelf"] = {
            "textures": [f"textures/blocks/{wood.replace('_', ' ')} wood/{wood}_shelf"]}
    write_json(path, atlas)


def write_block(wood):
    write_json(os.path.join(BP, "blocks", f"{wood}_wood", f"{wood}_shelf.json"), {
        "format_version": "1.26.0",
        "minecraft:block": {
            "description": {
                "identifier": f"extrabiomes:{wood}_shelf",
                "menu_category": {"category": "construction"},
                "traits": {"minecraft:placement_direction": {"enabled_states": ["minecraft:cardinal_direction"]}},
            },
            "permutations": [
                {"condition": f"q.block_state('minecraft:cardinal_direction') == '{facing}'",
                 "components": {
                     "minecraft:geometry": geometry_id(facing),
                     "minecraft:collision_box": dict(zip(("origin", "size"), turn_box(facing, *BACK_BOX))),
                     "minecraft:selection_box": dict(zip(("origin", "size"), turn_box(facing, *BACK_BOX))),
                 }}
                for facing in FACING_TURN
            ],
            "components": {
                "minecraft:geometry": geometry_id("south"),
                "minecraft:material_instances": {"*": {"texture": f"extrabiomes_{wood}_shelf", "render_method": "opaque"}},
                "minecraft:flammable": {"destroy_chance_modifier": 45, "catch_chance_modifier": 80},
                "minecraft:destructible_by_mining": {"seconds_to_destroy": 2},
                "minecraft:destructible_by_explosion": True,
                "tag:minecraft:is_axe_item_destructible": {},
                "extrabiomes:shelf": {},
            },
        },
    })


def write_recipe(wood):
    wood_folder = wood.replace("_", " ")
    write_json(os.path.join(BP, "recipes", f"{wood_folder} wood", f"{wood}_shelf.json"), {
        "format_version": "1.16.0",
        "minecraft:recipe_shaped": {
            "description": {"identifier": f"extrabiomes:{wood}_shelf"},
            "tags": ["crafting_table"],
            "pattern": ["###", "   ", "###"],
            "key": {"#": {"item": f"extrabiomes:stripped_{wood}_log"}},
            "result": [{"item": f"extrabiomes:{wood}_shelf", "count": 6}],
        },
    })


def update_lang_files():
    for name in sorted(os.listdir(os.path.join(RP, "texts"))):
        if not name.endswith(".lang"):
            continue
        with open(os.path.join(JAVA_LANG, name.lower().replace(".lang", ".json")), encoding="utf-8") as f:
            java = json.load(f)
        path = os.path.join(RP, "texts", name)
        with open(path, encoding="utf-8") as f:
            lines = [line for line in f.read().splitlines() if "_shelf." not in line]
        lines += [f"tile.extrabiomes:{wood}_shelf.name={java[f'block.extrabiomes.{wood}_shelf']}" for wood in WOODS]
        write_text(path, "\n".join(lines) + "\n")


def register_shelf_sounds():
    path = os.path.join(RP, "blocks.json")
    with open(path, encoding="utf-8") as f:
        registry = json.load(f)
    for wood in WOODS:
        registry[f"extrabiomes:{wood}_shelf"] = {"sound": "wood"}
    write_json(path, registry)


if __name__ == "__main__":
    write_geometry()
    copy_textures()
    register_textures()
    for wood in WOODS:
        write_block(wood)
        write_recipe(wood)
    register_shelf_sounds()
    update_lang_files()
