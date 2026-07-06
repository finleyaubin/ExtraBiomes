"""Generate extra sky city path pieces: lamp-lined straight, L-curve, roundabout.

Style follows the hand-built cross piece: 3-wide dense_cloud_brick roads set in a
dense_cloud lawn (full-square y0 floor), gilded_sky_log + sky_leaves corner trees
with flower pots, rollable connection jigsaws at y1 on road-centre edge cells.
Block palette entries for the custom blocks are lifted verbatim from the cross so
states (leaf persistence, log orientation, pot update bit) match exactly.

The cloud socket jigsaw is NOT added here — build_clouds.py injects it and builds
each piece's cloud pads, so run build_clouds.py after this script.
"""
import os
from mcstructure import (
    load, save, Tag, T_int, T_str, T_byte, T_list, T_comp,
    TAG_INT, TAG_LIST, TAG_COMPOUND, TAG_END,
)

HERE = os.path.dirname(os.path.abspath(__file__))
SC = os.path.join(HERE, "..", "ExtraBiomes - Bedrock", "packs", "BP",
                  "structures", "extrabiomes", "sky_city")
CONNECTION = "extrabiomes:sky_city_connection"


def jigsaw_entity(name, target, target_pool, final_state, x, y, z, joint="rollable"):
    return T_comp({
        "block_entity_data": T_comp({
            "final_state": T_str(final_state),
            "id": T_str("JigsawBlock"),
            "isMovable": T_byte(1),
            "joint": T_str(joint),
            "name": T_str(name),
            "placement_priority": T_int(0),
            "selection_priority": T_int(0),
            "target": T_str(target),
            "target_pool": T_str(target_pool),
            "x": T_int(x),
            "y": T_int(y),
            "z": T_int(z),
        })
    })


def make_structure(sx, sy, sz, cells, palette_entries, bpd_entries):
    total = sx * sy * sz
    layer0 = [Tag(TAG_INT, -1) for _ in range(total)]
    layer1 = [Tag(TAG_INT, -1) for _ in range(total)]
    for (x, y, z), pidx in cells.items():
        layer0[x * sy * sz + y * sz + z] = Tag(TAG_INT, pidx)
    return T_comp({
        "format_version": T_int(1),
        "size": T_list([T_int(sx), T_int(sy), T_int(sz)], TAG_INT),
        "structure": T_comp({
            "block_indices": T_list([
                T_list(layer0, TAG_INT),
                T_list(layer1, TAG_INT),
            ], TAG_LIST),
            "entities": T_list([], TAG_END),
            "palette": T_comp({
                "default": T_comp({
                    "block_palette": T_list(palette_entries, TAG_COMPOUND),
                    "block_position_data": T_comp(bpd_entries),
                })
            }),
        }),
        "structure_world_origin": T_list([T_int(0), T_int(0), T_int(0)], TAG_INT),
    })


# ---- palette lifted from the cross piece so custom block states match ----
_, cross = load(os.path.join(SC, "paths", "cross.mcstructure"))
cross_palette = cross.value["structure"].value["palette"].value["default"].value["block_palette"].value
VERSION = cross_palette[0].value["version"].value


def from_cross(name, want_state=None):
    for entry in cross_palette:
        if entry.value["name"].value != name:
            continue
        if want_state:
            k, v = want_state
            states = entry.value["states"].value
            if k not in states or states[k].value != v:
                continue
        return entry
    raise ValueError(f"{name} not in cross palette")


def fresh(name, states=None):
    return T_comp({
        "name": T_str(name),
        "states": T_comp(states or {}),
        "version": T_int(VERSION),
    })


# symbolic indices into the shared piece palette
PALETTE = [
    from_cross("extrabiomes:dense_cloud_brick"),                              # 0 road
    from_cross("extrabiomes:dense_cloud"),                                    # 1 lawn
    from_cross("extrabiomes:gilded_sky_log", ("minecraft:block_face", "up")), # 2 trunk
    from_cross("extrabiomes:sky_leaves"),                                     # 3 canopy
    from_cross("minecraft:flower_pot"),                                       # 4 pot
    fresh("minecraft:lantern"),                                               # 5 lamp
    fresh("minecraft:jigsaw", {"facing_direction": T_int(2), "rotation": T_int(0)}),  # 6 N
    fresh("minecraft:jigsaw", {"facing_direction": T_int(3), "rotation": T_int(0)}),  # 7 S
    fresh("minecraft:jigsaw", {"facing_direction": T_int(4), "rotation": T_int(0)}),  # 8 W
    fresh("minecraft:jigsaw", {"facing_direction": T_int(5), "rotation": T_int(0)}),  # 9 E
]
ROAD, LAWN, LOG, LEAF, POT, LANTERN = 0, 1, 2, 3, 4, 5
JIG = {2: 6, 3: 7, 4: 8, 5: 9}  # facing_direction -> palette index


def connection(cells, bpd, sy, sz, x, z, fd):
    cells[(x, 1, z)] = JIG[fd]
    bpd[str(x * sy * sz + 1 * sz + z)] = jigsaw_entity(
        CONNECTION, CONNECTION, CONNECTION, "air", x, 1, z)


FLOWERS = ("minecraft:cornflower", "minecraft:torchflower")


def pot(cells, bpd, sy, sz, x, z):
    """Flower pot with a plant: Bedrock keeps the plant in the tile entity."""
    cells[(x, 1, z)] = POT
    plant = FLOWERS[(x + 2 * z) % 2]
    bpd[str(x * sy * sz + 1 * sz + z)] = T_comp({
        "block_entity_data": T_comp({
            "id": T_str("FlowerPot"),
            "isMovable": T_byte(1),
            "PlantBlock": T_comp({
                "name": T_str(plant),
                "states": T_comp({}),
                "version": T_int(VERSION),
            }),
            "x": T_int(x),
            "y": T_int(1),
            "z": T_int(z),
        })
    })


def tree(cells, bpd, sy, sz, x, z, with_pots=True):
    """Corner tree copied from the cross: trunk, leaf ring, 3x3 cap, and a
    + shaped crown on top; potted flowers around the base."""
    for y in (1, 2, 3):
        cells[(x, y, z)] = LOG
    for dx, dz in ((-1, 0), (1, 0), (0, -1), (0, 1)):
        cells[(x + dx, 3, z + dz)] = LEAF
        if with_pots:
            pot(cells, bpd, sy, sz, x + dx, z + dz)
    for dx in (-1, 0, 1):
        for dz in (-1, 0, 1):
            if (dx, dz) != (0, 0):
                cells[(x + dx, 4, z + dz)] = LEAF
    cells[(x, 4, z)] = LEAF
    for dx, dz in ((0, 0), (-1, 0), (1, 0), (0, -1), (0, 1)):
        cells[(x + dx, 5, z + dz)] = LEAF


def lamp(cells, x, z):
    cells[(x, 1, z)] = LOG
    cells[(x, 2, z)] = LOG
    cells[(x, 3, z)] = LANTERN


def build_straight():
    """12-long lamp-lined straight road: brick centre, lawn verges, two lamps."""
    sx, sy, sz = 12, 4, 5
    cells, bpd = {}, {}
    for x in range(sx):
        for z in range(sz):
            cells[(x, 0, z)] = ROAD if 1 <= z <= 3 else LAWN
    lamp(cells, 3, 0)
    lamp(cells, 8, 4)
    for x, z in ((2, 0), (4, 0), (7, 4), (9, 4)):
        pot(cells, bpd, sy, sz, x, z)
    connection(cells, bpd, sy, sz, 0, 2, 4)
    connection(cells, bpd, sy, sz, 11, 2, 5)
    return sx, sy, sz, cells, bpd


def build_curve():
    """13x13 rounded bend (west -> south): a gentle quarter-circle arc of road
    swept around the inner corner, trees lining the inside and outside."""
    sx, sy, sz = 13, 6, 13
    cells, bpd = {}, {}
    for x in range(sx):
        for z in range(sz):
            d2 = x * x + (z - 12) ** 2  # distance from the arc centre (0, 12)
            road = 30.25 <= d2 <= 72.25  # radius 5.5..8.5 -> 3-wide arc
            cells[(x, 0, z)] = ROAD if road else LAWN
    tree(cells, bpd, sy, sz, 2, 10)              # inside the bend
    for tx, tz in ((5, 1), (10, 2), (10, 7)):    # arc of trees along the outside
        tree(cells, bpd, sy, sz, tx, tz)
    connection(cells, bpd, sy, sz, 0, 5, 4)
    connection(cells, bpd, sy, sz, 7, 12, 3)
    return sx, sy, sz, cells, bpd


def build_s_bend():
    """10x15 S-curve (west -> east with a sideways shift): two opposite
    quarter-circle arcs meeting at the inflection, trees in the outer corners."""
    sx, sy, sz = 10, 6, 15
    cells, bpd = {}, {}
    for x in range(sx):
        for z in range(sz):
            dA = x * x + (z - 7.5) ** 2          # arc centre (0, 7.5)
            dB = (x - 9) ** 2 + (z - 7.5) ** 2   # arc centre (9, 7.5)
            road = (z < 7.5 and 9 <= dA <= 36) or (z > 7.5 and 9 <= dB <= 36)
            cells[(x, 0, z)] = ROAD if road else LAWN
    tree(cells, bpd, sy, sz, 8, 2)    # outer corner of the first arc
    tree(cells, bpd, sy, sz, 1, 12)   # outer corner of the second arc
    connection(cells, bpd, sy, sz, 0, 3, 4)
    connection(cells, bpd, sy, sz, 9, 12, 5)
    return sx, sy, sz, cells, bpd


def build_roundabout():
    """13x13 ring road with 4 exits, centre lantern monument, corner trees."""
    sx, sy, sz = 13, 6, 13
    c = 6
    cells, bpd = {}, {}
    for x in range(sx):
        for z in range(sz):
            d2 = (x - c) ** 2 + (z - c) ** 2
            ring = 6.25 <= d2 <= 30.25
            stub = (5 <= z <= 7 or 5 <= x <= 7) and d2 > 30.25
            cells[(x, 0, z)] = ROAD if (ring or stub) else LAWN
    # centre monument: pot ring, log column, lantern on top
    for y in (1, 2, 3):
        cells[(c, y, c)] = LOG
    cells[(c, 4, c)] = LANTERN
    for dx, dz in ((-1, 0), (1, 0), (0, -1), (0, 1)):
        pot(cells, bpd, sy, sz, c + dx, c + dz)
    for x, z in ((1, 1), (11, 1), (1, 11), (11, 11)):
        tree(cells, bpd, sy, sz, x, z, with_pots=False)
    connection(cells, bpd, sy, sz, 0, c, 4)
    connection(cells, bpd, sy, sz, 12, c, 5)
    connection(cells, bpd, sy, sz, c, 0, 2)
    connection(cells, bpd, sy, sz, c, 12, 3)
    return sx, sy, sz, cells, bpd


for name, builder in (("straight", build_straight),
                      ("curve", build_curve),
                      ("s_bend", build_s_bend),
                      ("roundabout", build_roundabout)):
    sx, sy, sz, cells, bpd = builder()
    root = make_structure(sx, sy, sz, cells, PALETTE, bpd)
    out = os.path.join(SC, "paths", f"{name}.mcstructure")
    save(out, root, "")
    print(f"{name}: {sx}x{sy}x{sz}, {len(bpd)} block entities (connections + pots)")
print("done — now run build_clouds.py to inject cloud sockets and build pads")
