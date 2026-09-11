"""Convert Java Edition structure .nbt back to Bedrock .mcstructure.

Usage:
    python tools/java2mc.py <input.nbt> <output.mcstructure>

The inverse of mc2java.py, for backporting structures authored on the Java side.
Deliberately narrow: it maps only the block families the ported structures actually
use and raises on anything it doesn't know, so an unmapped block is a loud failure
rather than a silent hole in the structure.
"""
import sys

import nbt_edit
from mcstructure import T_byte, T_comp, T_int, T_list, T_str, save, TAG_COMPOUND, TAG_INT

BLOCK_VERSION = 18168865

AXIS_BLOCK_FACE = {"y": "up", "x": "east", "z": "north"}
STAIR_FACING_WEIRDO = {"east": 0, "west": 1, "south": 2, "north": 3}
FACING_DIRECTION = {"down": 0, "up": 1, "north": 2, "south": 3, "west": 4, "east": 5}
WALL_CONNECTION = {"none": "none", "low": "short", "tall": "tall"}
# Inverse of block_map._ROT_TOP - the jigsaw `rotation` a vertical front encodes.
ROT_TOP = {"north": 0, "east": 1, "south": 2, "west": 3}

PILLARS = {"extrabiomes:gilded_sky_log", "extrabiomes:stripped_gilded_sky_log",
           "extrabiomes:sky_log", "extrabiomes:sky_wood", "extrabiomes:stripped_sky_log"}

# Blocks whose id and (empty) state set are identical on both editions.
PLAIN = {"extrabiomes:dense_cloud", "extrabiomes:dense_cloud_brick", "minecraft:air",
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

    if name.endswith("_stairs"):
        # Bedrock has no `shape` state - it derives corner geometry from neighbours,
        # so Java's outer_left/outer_right are dropped here rather than mapped.
        return name, {"weirdo_direction": T_int(STAIR_FACING_WEIRDO[props["facing"]]),
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
