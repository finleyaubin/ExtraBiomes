"""Bedrock -> Java Edition 1.20.1 block + block-state mapping.

Given a Bedrock block id and its ``states`` dict (as produced by
``mcstructure.to_py``), :func:`map_block` returns ``(java_name, props)`` where

* ``java_name`` is a full registry id, e.g. ``"minecraft:oak_stairs"`` or
  ``"extrabiomes:sky_slab"``.
* ``props`` is a ``dict[str, str]`` of Java blockstate properties. Java state
  values are ALWAYS strings ("true"/"false"/"north"/"top"/"0"...).

If a property dict contains the key ``"waterlogged"`` the block is treated as
water-loggable by the converter (it will flip the value to "true" when the
Bedrock secondary layer holds a water source at that cell).

Unmapped block ids return ``(None, None)``; the converter turns those into
``minecraft:air`` and records a warning so nothing is silently dropped.
"""

# ---------------------------------------------------------------------------
# small helpers
# ---------------------------------------------------------------------------

# Bedrock facing_direction int -> Direction name (canonical, per spec).
FACING_DIRECTION = {0: "down", 1: "up", 2: "north", 3: "south", 4: "west", 5: "east"}

# Bedrock bed/`direction` int -> Java horizontal facing.
BED_DIRECTION = {0: "south", 1: "west", 2: "north", 3: "east"}

# Bedrock DyeColor byte order (bed color, and generally).
DYE_COLORS = [
    "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
    "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red",
    "black",
]

# minecraft:block_face axis string -> Java pillar axis.
BLOCK_FACE_AXIS = {
    "up": "y", "down": "y", "north": "z", "south": "z", "east": "x", "west": "x",
}

# The extrabiomes custom stair/trapdoor/door geometry (geometry.extrabiomes_stairs
# etc.) is hand-built with an EAST/WEST-mirrored convention relative to Java:
# a straight stair whose Bedrock `minecraft:cardinal_direction` == "east" has its
# tall step physically on the WEST (verified from the .geo cube coordinates), i.e.
# Java facing = west. North/south are unchanged. So converting a cardinal_direction
# to a Java horizontal `facing` for these CUSTOM blocks requires swapping E<->W.
# (Vanilla blocks that carry cardinal_direction -- chest/furnace/ender_chest -- are
# NOT mirrored and map identity.)
_EW_SWAP = {"north": "north", "south": "south", "east": "west", "west": "east"}


def _facing_from_cardinal(states, default="north"):
    """Java horizontal facing for a custom (mirrored-geometry) block."""
    return _EW_SWAP.get(_s(states.get("minecraft:cardinal_direction", default)), default)


# extrabiomes:direction int -> (Java stair facing, Java stair shape) for the
# corner variants. Derived from the visible geometry bone's physical quadrant
# (read from extrabiomes.stairs.geo cube coordinates) mapped to the vanilla Java
# stair model convention (facing=east,outer_right occupies the NE quadrant):
#   outer bone = the single filled quadrant; inner bone = the single notch quadrant.
# Every entry is canonicalised to the "_right" shape (each physical corner has an
# equivalent _left form at another facing; _right is picked for consistency).
STAIR_CORNER = {
    4: ("west", "outer_right"),   # southwest_outer  (filled SW)
    5: ("south", "outer_right"),  # southeast_outer  (filled SE)
    6: ("north", "outer_right"),  # northwest_outer  (filled NW)
    7: ("east", "outer_right"),   # northeast_outer  (filled NE)
    8: ("west", "inner_right"),   # northeast_inner  (notch NE)
    9: ("south", "inner_right"),  # northwest_inner  (notch NW)
    10: ("east", "inner_right"),  # southwest_inner  (notch SW)
    11: ("north", "inner_right"), # southeast_inner  (notch SE)
}

# golden_rail rail_direction int -> Java rail shape.
RAIL_SHAPE = {
    0: "north_south", 1: "east_west", 2: "ascending_east", 3: "ascending_west",
    4: "ascending_north", 5: "ascending_south",
}


def _b(v):
    """Bedrock int/bool -> Java boolean string."""
    return "true" if v in (1, True, "1", "true") else "false"


def _s(v):
    """Any value -> Java string state value."""
    return str(v)


# ---------------------------------------------------------------------------
# main entry point
# ---------------------------------------------------------------------------

def map_block(name, states, be=None):
    """Map one Bedrock block to Java. ``be`` is the optional block-entity dict.

    Returns ``(java_name, props)`` or ``(None, None)`` if unmapped.
    Jigsaw blocks are handled by the converter (orientation needs the block
    entity too), but are still mapped here for completeness / final_state.
    """
    states = states or {}

    # -- vanilla identity / simple ------------------------------------------
    if name == "minecraft:air":
        return "minecraft:air", {}
    if name in ("minecraft:black_concrete", "minecraft:white_concrete",
                "minecraft:cyan_carpet", "minecraft:crafting_table"):
        return name, {}
    if name == "minecraft:glass_pane":
        # north/south/east/west default to false; Java recomputes on placement.
        return "minecraft:glass_pane", {"waterlogged": "false"}
    if name == "minecraft:warped_fence":
        return "minecraft:warped_fence", {"waterlogged": "false"}
    if name == "minecraft:mob_spawner":
        return "minecraft:spawner", {}
    if name == "minecraft:ender_chest":
        return "minecraft:ender_chest", {
            "facing": _s(states.get("minecraft:cardinal_direction", "north")),
            "waterlogged": "false",
        }
    if name == "minecraft:flower_pot":
        # Emit an empty pot (per spec).
        return "minecraft:flower_pot", {}
    if name == "minecraft:iron_chain":
        return "minecraft:chain", {"axis": _s(states.get("pillar_axis", "y"))}

    if name in ("minecraft:ice", "minecraft:blue_ice", "minecraft:packed_ice"):
        return name, {}

    if name == "minecraft:stone":
        # Bedrock pre-flattening "stone_type" state selects the actual variant.
        STONE_TYPE = {
            "stone": "minecraft:stone",
            "granite": "minecraft:granite",
            "granite_smooth": "minecraft:polished_granite",
            "diorite": "minecraft:diorite",
            "diorite_smooth": "minecraft:polished_diorite",
            "andesite": "minecraft:andesite",
            "andesite_smooth": "minecraft:polished_andesite",
        }
        return STONE_TYPE.get(_s(states.get("stone_type", "stone")), "minecraft:stone"), {}
    if name == "minecraft:snow":
        # Bedrock "minecraft:snow" is the full solid snow block.
        return "minecraft:snow_block", {}
    if name == "minecraft:snow_layer":
        # Bedrock height 0..7 -> Java "layers" 1..8.
        height = int(states.get("height", 0))
        return "minecraft:snow", {"layers": _s(max(1, min(8, height + 1)))}

    if name == "minecraft:water":
        depth = int(states.get("liquid_depth", 0))
        return "minecraft:water", {"level": _s(max(0, min(15, depth)))}

    if name == "minecraft:bed":
        color = DYE_COLORS[be["color"]] if be and "color" in be else "red"
        return "minecraft:%s_bed" % color, {
            "facing": BED_DIRECTION.get(int(states.get("direction", 0)), "south"),
            "part": "head" if states.get("head_piece_bit") in (1, True) else "foot",
            "occupied": _b(states.get("occupied_bit", 0)),
        }

    if name in ("minecraft:chest", "minecraft:trapped_chest"):
        return name, {
            "facing": _s(states.get("minecraft:cardinal_direction", "north")),
            "type": "single",
            "waterlogged": "false",
        }
    if name == "minecraft:furnace":
        return "minecraft:furnace", {
            "facing": _s(states.get("minecraft:cardinal_direction", "north")),
            "lit": "false",
        }

    if name == "minecraft:cave_vines":
        return "minecraft:cave_vines", {
            "age": _s(int(states.get("growing_plant_age", 0))),
            "berries": "false",
        }

    if name.endswith("_candle") and name.startswith("minecraft:"):
        return name, {
            "candles": _s(int(states.get("candles", 0)) + 1),  # Bedrock 0..3 -> Java 1..4
            "lit": _b(states.get("lit", 0)),
            "waterlogged": "false",
        }

    if name == "minecraft:golden_rail":
        return "minecraft:powered_rail", {
            "powered": _b(states.get("rail_data_bit", 0)),
            "shape": RAIL_SHAPE.get(int(states.get("rail_direction", 0)), "north_south"),
        }

    if name == "minecraft:ladder":
        return "minecraft:ladder", {
            "facing": FACING_DIRECTION.get(int(states.get("facing_direction", 2)), "north"),
            "waterlogged": "false",
        }
    if name == "minecraft:lantern":
        return "minecraft:lantern", {
            "hanging": _b(states.get("hanging", 0)),
            "waterlogged": "false",
        }
    if name == "minecraft:lightning_rod":
        return "minecraft:lightning_rod", {
            "facing": FACING_DIRECTION.get(int(states.get("facing_direction", 1)), "up"),
            "powered": _b(states.get("powered_bit", 0)),
            "waterlogged": "false",
        }
    if name == "minecraft:scaffolding":
        return "minecraft:scaffolding", {
            "distance": "0", "waterlogged": "false", "bottom": "false",
        }
    if name == "minecraft:warped_wall_sign":
        return "minecraft:warped_wall_sign", {
            "facing": FACING_DIRECTION.get(int(states.get("facing_direction", 3)), "north"),
            "waterlogged": "false",
        }

    if name == "minecraft:jigsaw":
        # Orientation is computed by the converter from facing_direction+rotation.
        return "minecraft:jigsaw", {"orientation": "north_up"}

    if name == "minecraft:azalea_leaves":
        return "minecraft:azalea_leaves", {
            "persistent": _b(states.get("persistent_bit", 0)),
            "distance": "7",
            "waterlogged": "false",
        }
    if name == "minecraft:end_rod":
        return "minecraft:end_rod", {
            "facing": FACING_DIRECTION.get(int(states.get("facing_direction", 1)), "up"),
        }

    # -- extrabiomes custom blocks ------------------------------------------
    if name in ("extrabiomes:dense_cloud", "extrabiomes:dense_cloud_brick",
                "extrabiomes:gilded_sky_planks", "extrabiomes:sky_planks"):
        return name, {}

    if name in ("extrabiomes:dense_cloud_brick_slab", "extrabiomes:gilded_sky_slab",
                "extrabiomes:sky_slab"):
        if states.get("extrabiomes:is_double") in (1, True):
            stype = "double"
        else:
            stype = _s(states.get("minecraft:vertical_half", "bottom"))
        return name, {"type": stype, "waterlogged": "false"}

    if name in ("extrabiomes:dense_cloud_brick_stairs", "extrabiomes:gilded_sky_stairs",
                "extrabiomes:sky_stairs"):
        direction = int(states.get("extrabiomes:direction", 0))
        half = _s(states.get("minecraft:vertical_half", "bottom"))
        if direction in STAIR_CORNER:
            facing, shape = STAIR_CORNER[direction]
        else:
            # straight: facing from cardinal_direction with the E<->W mirror.
            facing, shape = _facing_from_cardinal(states), "straight"
        return name, {
            "facing": facing,
            "half": half,
            "shape": shape,
            "waterlogged": "false",
        }

    if name == "extrabiomes:gilded_sky_trapdoor":
        return "extrabiomes:gilded_sky_trapdoor", {
            "facing": _facing_from_cardinal(states),
            "half": _s(states.get("minecraft:vertical_half", "bottom")),
            "open": _b(states.get("extrabiomes:is_open", 0)),
            "powered": "false",
            "waterlogged": "false",
        }

    if name in ("extrabiomes:gilded_sky_door_bottom", "extrabiomes:gilded_sky_door_top"):
        half = "upper" if name.endswith("_top") else "lower"
        return "extrabiomes:gilded_sky_door", {
            "facing": _facing_from_cardinal(states),
            "half": half,
            # doubledoor is only present on the bottom half; upper defaults left.
            "hinge": "right" if states.get("extrabiomes:doubledoor") in (1, True) else "left",
            "open": _b(states.get("extrabiomes:is_open", 0)),
            "powered": "false",
        }

    if name == "extrabiomes:sky_door":
        return "extrabiomes:sky_door", {
            "facing": BED_DIRECTION.get(int(states.get("direction", 0)), "south"),
            "half": "upper" if states.get("extrabiomes:upper_bit") in (1, True) else "lower",
            "hinge": "left",
            "open": _b(states.get("extrabiomes:open_bit", 0)),
            "powered": "false",
        }

    # logs / wood -> RotatedPillarBlock. Bedrock stripped_ -> Java striped_.
    if name in ("extrabiomes:gilded_sky_log", "extrabiomes:sky_log",
                "extrabiomes:sky_wood", "extrabiomes:stripped_sky_log"):
        java_name = "extrabiomes:striped_sky_log" if name == "extrabiomes:stripped_sky_log" else name
        face = states.get("minecraft:block_face", "up")
        return java_name, {"axis": BLOCK_FACE_AXIS.get(face, "y")}

    if name == "extrabiomes:sky_fence":
        return "extrabiomes:sky_fence", {
            "north": _b(states.get("extrabiomes:north", 0)),
            "south": _b(states.get("extrabiomes:south", 0)),
            "east": _b(states.get("extrabiomes:east", 0)),
            "west": _b(states.get("extrabiomes:west", 0)),
            "waterlogged": "false",
        }

    if name == "extrabiomes:sky_leaves":
        return "extrabiomes:sky_leaves", {
            "distance": "7",
            "persistent": _b(states.get("extrabiomes:persist", 0)),
            "waterlogged": "false",
        }

    # boulder/pebble subsystem: extrabiomes:{small,medium,large}[_mossy]_pebble ->
    # extrabiomes:pebble_block / extrabiomes:mossy_pebble_block with integer "size" (1-3).
    PEBBLE_SIZE = {"small": 1, "medium": 2, "large": 3}
    if name in ("extrabiomes:small_pebble", "extrabiomes:medium_pebble", "extrabiomes:large_pebble"):
        size = name.split(":")[1].split("_")[0]
        return "extrabiomes:pebble_block", {"size": _s(PEBBLE_SIZE[size])}
    if name in ("extrabiomes:small_mossy_pebble", "extrabiomes:medium_mossy_pebble", "extrabiomes:large_mossy_pebble"):
        size = name.split(":")[1].split("_")[0]
        return "extrabiomes:mossy_pebble_block", {"size": _s(PEBBLE_SIZE[size])}

    # boulder/stick_pile subsystem: extrabiomes:stick_pile has no Java-side block yet
    # (no StickPileBlock has been implemented in the Forge port). Drop it to air rather
    # than emit a reference to a nonexistent registry name; see worldgen boulder subsystem
    # notes for the follow-up needed to restore the stick visuals.
    if name == "extrabiomes:stick_pile":
        return "minecraft:air", {}

    # plain (unpowered) rail -> Java minecraft:rail. Unlike golden/detector/activator
    # rails, plain rails also support diagonal "shape" values, but this converter only
    # ever sees straight/ascending forms (0-5) in practice, so the same table applies.
    if name == "minecraft:rail":
        return "minecraft:rail", {"shape": RAIL_SHAPE.get(int(states.get("rail_direction", 0)), "north_south")}

    # huge mushroom blocks (red/brown vanilla + extrabiomes colored variants).
    # Bedrock's "huge_mushroom_bits" (0-15) is the pre-flattening Java data
    # value for these blocks; Java 1.20.1 exposes the same information as six
    # per-face booleans (up/down/north/south/east/west = show cap texture).
    if name in ("minecraft:red_mushroom_block", "minecraft:brown_mushroom_block") or (
        name.startswith("extrabiomes:") and name.endswith("_mushroom_block")
    ):
        return name, _mushroom_bits(int(states.get("huge_mushroom_bits", 14)))

    if name == "minecraft:mushroom_stem":
        return name, _mushroom_bits(int(states.get("huge_mushroom_bits", 10)))

    # -- unmapped -----------------------------------------------------------
    return None, None


# Pre-flattening data value -> which faces show the "cap"/bark texture.
# (Same table used by red_mushroom_block, brown_mushroom_block, mushroom_stem,
# and every extrabiomes:<color>_mushroom_block, which all share this shape.)
_MUSHROOM_BITS_FACES = {
    0: (),
    1: ("up", "north", "west"),
    2: ("up", "north"),
    3: ("up", "north", "east"),
    4: ("up", "west"),
    5: ("up",),
    6: ("up", "east"),
    7: ("up", "west", "south"),
    8: ("up", "south"),
    9: ("up", "south", "east"),
    10: ("north", "south", "east", "west"),
    11: ("up", "down", "north", "south", "east", "west"),
    12: ("up", "down", "north", "south", "east", "west"),
    13: ("up", "down", "north", "south", "east", "west"),
    14: ("up", "down", "north", "south", "east", "west"),
    15: (),
}


def _mushroom_bits(bits):
    faces = _MUSHROOM_BITS_FACES.get(int(bits) & 0xF, ())
    return {
        face: ("true" if face in faces else "false")
        for face in ("up", "down", "north", "south", "east", "west")
    }


def map_final_state(bare_id):
    """Convert a Bedrock jigsaw ``final_state`` bare id to a Java block id.

    ``final_state`` in Java may be a plain id (no brackets). "air" -> the
    fully-qualified "minecraft:air"; namespaced ids pass through.
    """
    if not bare_id:
        return "minecraft:air"
    if ":" not in bare_id:
        return "minecraft:" + bare_id
    return bare_id


# Bedrock facing_direction + rotation -> Java jigsaw `orientation` (FrontAndTop).
#
# The 12 vanilla values: down_east down_north down_south down_west
# up_east up_north up_south up_west west_up east_up north_up south_up.
#
# For a HORIZONTAL front (n/s/e/w) the value is "<front>_up" -- this is what
# drives sky-city expansion, so it must be exact. For a VERTICAL front (up/down)
# the value is "<front>_<toptowards>", where the "top towards" horizontal
# direction is derived from `rotation`. Every observed sky_city jigsaw uses
# rotation 0, so rotation-0 correctness is prioritised; the rot->direction
# convention (0->north, 1->east, 2->south, 3->west) is documented here so it
# can be corrected easily if the in-game result disagrees.
_ROT_TOP = {0: "north", 1: "east", 2: "south", 3: "west"}


def jigsaw_orientation(facing_direction, rotation):
    """Return (orientation_string, warning_or_None)."""
    front = FACING_DIRECTION.get(int(facing_direction), "north")
    rot = int(rotation) % 4
    warning = None
    if front in ("north", "south", "east", "west"):
        # Horizontal sockets: top always points up. Rotation should be 0.
        if rot != 0:
            warning = ("horizontal jigsaw facing %s has non-zero rotation %d "
                       "(kept as %s_up)" % (front, rot, front))
        return "%s_up" % front, warning
    # Vertical front (up/down): top component derived from rotation.
    return "%s_%s" % (front, _ROT_TOP[rot]), warning
