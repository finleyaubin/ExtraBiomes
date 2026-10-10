"""Convert Java Edition structure .nbt back to Bedrock .mcstructure.

Usage:
    python tools/java2mc.py <input.nbt> <output.mcstructure>

The inverse of mc2java.py, for backporting structures authored on the Java side.
Deliberately narrow: it maps only the block families the ported structures actually
use and raises on anything it doesn't know, so an unmapped block is a loud failure
rather than a silent hole in the structure.
"""
import json
import sys

import nbt_edit
from mcstructure import TAG_SHORT, Tag, T_byte, T_comp, T_int, T_list, T_str, save, TAG_COMPOUND, TAG_INT

BLOCK_VERSION = 18168865

AXIS_BLOCK_FACE = {"y": "up", "x": "east", "z": "north"}
STAIR_FACING_WEIRDO = {"east": 0, "west": 1, "south": 2, "north": 3}
FACING_DIRECTION = {"down": 0, "up": 1, "north": 2, "south": 3, "west": 4, "east": 5}
HORIZONTAL_DIRECTION = {"south": 0, "west": 1, "north": 2, "east": 3}
STAIR_NAMES = {"minecraft:cobblestone_stairs": "minecraft:stone_stairs"}
CONTAINER_BLOCK_ENTITIES = {"minecraft:chest": "Chest", "minecraft:dispenser": "Dispenser"}
WALL_CONNECTION = {"none": "none", "low": "short", "tall": "tall"}
# Inverse of block_map._ROT_TOP - the jigsaw `rotation` a vertical front encodes.
ROT_TOP = {"north": 0, "east": 1, "south": 2, "west": 3}

PILLARS = {"extrabiomes:gilded_sky_log", "extrabiomes:stripped_gilded_sky_log", "extrabiomes:stripped_gilded_sky_wood",
           "extrabiomes:sky_log", "extrabiomes:sky_wood", "extrabiomes:stripped_sky_log"}

CUSTOM_STAIRS = {"extrabiomes:dense_cloud_brick_stairs", "extrabiomes:dense_cloud_stairs",
                 "extrabiomes:gilded_sky_stairs", "extrabiomes:sky_stairs"}
# Inverse of block_map.STAIR_CORNER; a Java *_left shape is the *_right shape one quarter turn counter-clockwise.
STAIR_CORNER_DIRECTION = {("south", "outer_right"): 4, ("east", "outer_right"): 5, ("west", "outer_right"): 6,
                          ("north", "outer_right"): 7, ("south", "inner_right"): 8, ("east", "inner_right"): 9,
                          ("north", "inner_right"): 10, ("west", "inner_right"): 11}
COUNTER_CLOCKWISE = {"north": "west", "west": "south", "south": "east", "east": "north"}
FLOWER_POT_PLANTS = {
    "minecraft:potted_cornflower": ("minecraft:red_flower", {"flower_type": "cornflower"}),
    "minecraft:potted_blue_orchid": ("minecraft:red_flower", {"flower_type": "orchid"}),
    "minecraft:potted_torchflower": ("minecraft:torchflower", {}),
}

# Blocks whose id and (empty) state set are identical on both editions.
PLAIN = {"extrabiomes:dense_cloud", "extrabiomes:dense_cloud_brick", "minecraft:air",
         "extrabiomes:gilded_sky_planks", "minecraft:blue_ice",
         "minecraft:polished_tuff", "minecraft:chiseled_tuff", "minecraft:redstone_block",
         "minecraft:gold_block", "minecraft:white_concrete"}


def _bool(v):
    return 1 if v == "true" else 0


def map_block(name, props):
    """Return (bedrock_name, states dict of Tag) or raise for an unmapped block."""
    if name in PLAIN:
        return name, {}

    if name in PILLARS:
        face = AXIS_BLOCK_FACE[props.get("axis", "y")]
        return name, {"minecraft:block_face": T_str(face)}

    if name.endswith("_leaves") and name.startswith("extrabiomes:"):
        # Bedrock's leaf states are this pack's own; `placed` keeps the leaves from
        # decaying the way every other hand-placed sky-city leaf block does.
        return name, {"extrabiomes:decay": T_byte(0),
                      "extrabiomes:persist": T_byte(_bool(props.get("persistent", "false"))),
                      "extrabiomes:placed": T_byte(1)}

    if name in ("minecraft:cobblestone", "minecraft:mossy_cobblestone"):
        return name, {}

    if name == "minecraft:chiseled_stone_bricks":
        return "minecraft:stonebrick", {"stone_brick_type": T_str("chiseled")}

    # The vanilla jungle temple's parts (floating_jungle/islet_temple). Redstone, tripwire, hook, lever and
    # repeater directions are a best-effort mapping that has not been loaded in Bedrock yet.
    if name == "minecraft:chest":
        return name, {"minecraft:cardinal_direction": T_str(props.get("facing", "north"))}

    if name == "minecraft:dispenser":
        return name, {"facing_direction": T_int(FACING_DIRECTION[props["facing"]]),
                      "triggered_bit": T_byte(_bool(props.get("triggered", "false")))}

    if name == "minecraft:sticky_piston":
        return name, {"facing_direction": T_int(FACING_DIRECTION[props["facing"]])}

    if name == "minecraft:tripwire":
        return "minecraft:trip_wire", {"attached_bit": T_byte(_bool(props.get("attached", "false"))),
                                       "disarmed_bit": T_byte(_bool(props.get("disarmed", "false"))),
                                       "powered_bit": T_byte(_bool(props.get("powered", "false"))),
                                       "suspended_bit": T_byte(0)}

    if name == "minecraft:tripwire_hook":
        return name, {"attached_bit": T_byte(_bool(props.get("attached", "false"))),
                      "powered_bit": T_byte(_bool(props.get("powered", "false"))),
                      "direction": T_int(HORIZONTAL_DIRECTION[props["facing"]])}

    if name == "minecraft:redstone_wire":
        return name, {"redstone_signal": T_int(int(props.get("power", "0")))}

    if name == "minecraft:lever":
        return name, {"lever_direction": T_str(props["facing"]),
                      "open_bit": T_byte(_bool(props.get("powered", "false")))}

    if name == "minecraft:repeater":
        return "minecraft:unpowered_repeater", {"direction": T_int(HORIZONTAL_DIRECTION[props["facing"]]),
                                                "repeater_delay": T_int(int(props.get("delay", "1")) - 1)}

    if name == "minecraft:vine":
        bits = sum(bit for side, bit in (("south", 1), ("west", 2), ("north", 4), ("east", 8)) if props.get(side) == "true")
        return name, {"vine_direction_bits": T_int(bits)}

    if name in CUSTOM_STAIRS:
        facing, shape = props["facing"], props["shape"]
        if shape == "straight":
            direction = 0
        else:
            kind, side = shape.split("_")
            if side == "left":
                facing = COUNTER_CLOCKWISE[facing]
            direction = STAIR_CORNER_DIRECTION[(facing, kind + "_right")]
        top = props.get("half") == "top"
        return name, {"extrabiomes:direction": T_int(direction),
                      "extrabiomes:is_upside_down": T_byte(1 if top else 0),
                      "minecraft:cardinal_direction": T_str(props["facing"]),
                      "minecraft:vertical_half": T_str("top" if top else "bottom")}

    if name.endswith("_stairs"):
        # Bedrock has no `shape` state - it derives corner geometry from neighbours,
        # so Java's outer_left/outer_right are dropped here rather than mapped.
        return STAIR_NAMES.get(name, name), {"weirdo_direction": T_int(STAIR_FACING_WEIRDO[props["facing"]]),
                      "upside_down_bit": T_byte(1 if props.get("half") == "top" else 0)}

    if name.endswith("_wall"):
        states = {"wall_post_bit": T_byte(_bool(props.get("up", "false")))}
        for side in ("north", "south", "east", "west"):
            states["wall_connection_type_" + side] = T_str(WALL_CONNECTION[props.get(side, "none")])
        return name, states

    if name.endswith("_button"):
        face = props.get("face", "wall")
        direction = {"floor": "up", "ceiling": "down"}.get(face) or props["facing"]
        return name, {"facing_direction": T_int(FACING_DIRECTION[direction]),
                      "button_pressed_bit": T_byte(_bool(props.get("powered", "false")))}

    if name == "minecraft:lantern":
        return name, {"hanging": T_byte(_bool(props.get("hanging", "false")))}

    if name == "minecraft:water":
        return name, {"liquid_depth": T_int(int(props.get("level", "0")))}

    if name == "minecraft:magma_block":
        return "minecraft:magma", {}

    if name == "extrabiomes:sky_fence":
        return name, {"extrabiomes:" + side: T_byte(_bool(props.get(side, "false")))
                      for side in ("north", "south", "east", "west")}

    if name == "minecraft:lectern":
        return name, {"minecraft:cardinal_direction": T_str(props["facing"]),
                      "powered_bit": T_byte(_bool(props.get("powered", "false")))}

    if name in FLOWER_POT_PLANTS:
        return "minecraft:flower_pot", {"update_bit": T_byte(0)}

    if name == "minecraft:jigsaw":
        front, top = props["orientation"].rsplit("_", 1)
        rotation = 0 if front in ("north", "south", "east", "west") else ROT_TOP[top]
        return name, {"facing_direction": T_int(FACING_DIRECTION[front]), "rotation": T_int(rotation)}

    raise ValueError("unmapped block: %s %s" % (name, props))


def _jigsaw_entity(nbt, x, y, z):
    final_state = nbt.get("final_state", "minecraft:air")
    return T_comp({"block_entity_data": T_comp({
        "final_state": T_str(final_state.split(":", 1)[1] if final_state.startswith("minecraft:") else final_state),
        "id": T_str("JigsawBlock"),
        "isMovable": T_byte(1),
        "joint": T_str(nbt.get("joint", "rollable")),
        "name": T_str(nbt.get("name", "minecraft:empty")),
        "placement_priority": T_int(int(nbt.get("placement_priority", 0))),
        "selection_priority": T_int(int(nbt.get("selection_priority", 0))),
        "target": T_str(nbt.get("target", "minecraft:empty")),
        "target_pool": T_str(nbt.get("pool", "minecraft:empty")),
        "x": T_int(x), "y": T_int(y), "z": T_int(z),
    })})


def _container_entity(nbt):
    entity = {"id": T_str(CONTAINER_BLOCK_ENTITIES[nbt["id"]]), "isMovable": T_byte(1)}
    if "LootTable" in nbt:
        entity["LootTable"] = T_str("loot_tables/" + nbt["LootTable"].split(":", 1)[1] + ".json")
    return T_comp({"block_entity_data": T_comp(entity)})


def _lectern_entity(nbt):
    book = nbt["Book"]["components"]["minecraft:written_book_content"]
    pages = [json.loads(page["raw"]) for page in book["pages"]]
    written_book = {
        "Count": T_byte(1), "Damage": Tag(TAG_SHORT, 0), "Name": T_str("minecraft:written_book"), "WasPickedUp": T_byte(0),
        "tag": T_comp({
            "author": T_str(book["author"]), "generation": T_int(0), "title": T_str(book["title"]["raw"]),
            "pages": T_list([T_comp({"photoname": T_str(""), "text": T_str(text)}) for text in pages], TAG_COMPOUND),
        }),
    }
    return T_comp({"block_entity_data": T_comp({
        "book": T_comp(written_book), "hasBook": T_byte(1), "id": T_str("Lectern"), "isMovable": T_byte(1),
        "page": T_int(nbt.get("Page", 0)), "totalPages": T_int(len(pages)),
    })})


def _flower_pot_entity(java_name):
    plant, states = FLOWER_POT_PLANTS[java_name]
    return T_comp({"block_entity_data": T_comp({
        "PlantBlock": T_comp({"name": T_str(plant), "states": T_comp({k: T_str(v) for k, v in states.items()}),
                              "version": T_int(BLOCK_VERSION)}),
        "id": T_str("FlowerPot"), "isMovable": T_byte(1),
    })})


def _py(tag):
    if tag.type == nbt_edit.TAG_COMPOUND:
        return {k: _py(t) for k, t in tag.value}
    if tag.type == nbt_edit.TAG_LIST:
        return [_py(t) for t in tag.value[1]]
    return tag.value


def convert(src):
    _, root = nbt_edit.load(src)
    d = _py(root)
    sx, sy, sz = d["size"]

    palette = []
    index_of = {}

    def palette_id(java_name, props):
        bedrock_name, states = map_block(java_name, props)
        key = (bedrock_name, tuple(sorted((k, t.value) for k, t in states.items())))
        if key not in index_of:
            index_of[key] = len(palette)
            palette.append(T_comp({"name": T_str(bedrock_name),
                                   "states": T_comp(states),
                                   "version": T_int(BLOCK_VERSION)}))
        return index_of[key]

    java_palette = [(e["Name"], e.get("Properties", {})) for e in d["palette"]]

    layer0 = [-1] * (sx * sy * sz)
    position_data = {}
    for blk in d["blocks"]:
        x, y, z = blk["pos"]
        flat = (x * sy + y) * sz + z
        java_name, props = java_palette[blk["state"]]
        layer0[flat] = palette_id(java_name, props)
        if java_name == "minecraft:jigsaw":
            position_data[str(flat)] = _jigsaw_entity(blk["nbt"], x, y, z)
        elif java_name == "minecraft:lectern":
            position_data[str(flat)] = _lectern_entity(blk["nbt"])
        elif java_name in FLOWER_POT_PLANTS:
            position_data[str(flat)] = _flower_pot_entity(java_name)
        elif java_name in CONTAINER_BLOCK_ENTITIES and "nbt" in blk:
            position_data[str(flat)] = _container_entity(blk["nbt"])

    return T_comp({
        "format_version": T_int(1),
        "size": T_list([T_int(sx), T_int(sy), T_int(sz)], TAG_INT),
        "structure": T_comp({
            "block_indices": T_list([
                T_list([T_int(i) for i in layer0], TAG_INT),
                T_list([T_int(-1)] * (sx * sy * sz), TAG_INT),
            ], 9),
            "entities": T_list([], TAG_COMPOUND),
            "palette": T_comp({"default": T_comp({
                "block_palette": T_list(palette, TAG_COMPOUND),
                "block_position_data": T_comp(position_data),
            })}),
        }),
        "structure_world_origin": T_list([T_int(0), T_int(0), T_int(0)], TAG_INT),
    })


if __name__ == "__main__":
    save(sys.argv[2], convert(sys.argv[1]))
