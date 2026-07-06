"""Build jigsaw-native sky island + clouds for the sky city.

Generation chain:
  island (start piece, big flat-topped bushy island)
    -> up jigsaw on its surface pulls in the cross plaza (hub)
      -> city expands via the existing horizontal connection jigsaws
        -> every piece has a down "cloud socket" that hangs a footprint-matched
           cloud pad under it (fails harmlessly over the island body)
          -> every pad has a down "puff socket" that hangs a wide cloud blob
             under its deepest point (fails harmlessly where space is taken)
          -> every pad also has side "filler sockets" along its edges that pull
             flat cloud slabs sideways into the gaps between paths; a multi-size
             filler pool means big slabs fill big gaps and small slabs squeeze
             into tight ones (colliding slabs fail harmlessly)

For every sky city piece this script:
  1. finds the piece's floor plan (solid columns in its bottom two layers),
  2. injects one downward-facing jigsaw block into the floor (the "cloud socket"),
  3. generates cloud pad .mcstructure templates (flat top, domed bushy bottom)
     whose footprint mirrors the piece's floor plan,
  4. writes a template pool JSON per piece listing the pad variants.
It also generates the island start pieces, the shared puff pool, and the hub pool.

Re-runnable: existing sockets are reused, generated files are overwritten.
"""
import os, json, math
from mcstructure import (
    load, save, Tag, T_byte, T_int, T_str, T_list, T_comp,
    TAG_INT, TAG_LIST, TAG_COMPOUND, TAG_END,
)

HERE = os.path.dirname(os.path.abspath(__file__))
BP = os.path.join(HERE, "..", "ExtraBiomes - Bedrock", "packs", "BP")
SC = os.path.join(BP, "structures", "extrabiomes", "sky_city")
CLOUD_DIR = os.path.join(SC, "clouds")
ISLAND_DIR = os.path.join(SC, "islands")
POOL_DIR = os.path.join(BP, "worldgen", "template_pools", "sky_city", "clouds")

CLOUD_BLOCK = "extrabiomes:dense_cloud"
SOCKET_NAME = "extrabiomes:sky_city_cloud_socket"     # down jigsaw in the piece
CONNECTOR_NAME = "extrabiomes:sky_city_cloud"         # up jigsaw in the pad
PUFF_SOCKET_NAME = "extrabiomes:sky_city_puff_socket" # down jigsaw in the pad
PUFF_NAME = "extrabiomes:sky_city_cloud_puff"         # up jigsaw in the puff
PUFF_POOL = "extrabiomes:sky_city_cloud_puff"
FILLER_SOCKET_NAME = "extrabiomes:sky_city_filler_socket"  # side jigsaw in the pad
FILLER_NAME = "extrabiomes:sky_city_filler"                # side jigsaws in the filler
FILLER_POOL = "extrabiomes:sky_city_cloud_filler"
HUB_POOL = "extrabiomes:sky_city_hub"
ISLAND_POOL = "extrabiomes:sky_city_island"
ISLAND_TOP_NAME = "extrabiomes:sky_city_island_top"
CONNECTION_NAME = "extrabiomes:sky_city_connection"  # horizontal city expansion
FLOOR_BLOCKS = {"extrabiomes:dense_cloud_brick", "extrabiomes:dense_cloud"}
VARIANTS = 3

# piece file (relative to sky_city/) -> short name used for cloud files/pools
PIECES = {
    "paths/path": "path",
    "paths/cross": "cross",
    "paths/T": "t",
    "paths/path_end": "path_end",
    "paths/fountain": "fountain",
    "paths/straight": "straight",
    "paths/curve": "curve",
    "paths/s_bend": "s_bend",
    "paths/roundabout": "roundabout",
    "buildings/house_1": "house_1",
    "buildings/sky_challet": "sky_challet",
    "buildings/tower_1": "tower_1",
    "buildings/tower_2": "tower_2",
}


def h32(x, z, seed):
    """Deterministic 32-bit hash for build-time noise."""
    n = (x * 374761393 + z * 668265263 + seed * 2246822519) & 0xFFFFFFFF
    n = ((n ^ (n >> 13)) * 1274126177) & 0xFFFFFFFF
    return (n ^ (n >> 16)) & 0xFFFFFFFF


def flat_index(x, y, z, sy, sz):
    return x * sy * sz + y * sz + z


def piece_info(root):
    v = root.value
    size = [t.value for t in v["size"].value]
    origin = [t.value for t in v["structure_world_origin"].value]
    structure = v["structure"].value
    palette_tag = structure["palette"].value["default"].value["block_palette"]
    indices_tag = structure["block_indices"].value[0]
    bpd_tag = structure["palette"].value["default"].value["block_position_data"]
    return size, origin, structure, palette_tag, indices_tag, bpd_tag


def compute_mask(size, palette_tag, indices_tag):
    """Columns that have a solid block in the bottom two layers."""
    sx, sy, sz = size
    names = [p.value["name"].value for p in palette_tag.value]
    skip = {"minecraft:air", "minecraft:jigsaw"}
    mask = [[False] * sz for _ in range(sx)]
    for x in range(sx):
        for z in range(sz):
            for y in range(min(2, sy)):
                p = indices_tag.value[flat_index(x, y, z, sy, sz)].value
                if p >= 0 and names[p] not in skip:
                    mask[x][z] = True
                    break
    return mask


def pick_anchor(size, palette_tag, indices_tag, mask):
    """Floor block (y=0) from FLOOR_BLOCKS nearest the mask centroid."""
    sx, sy, sz = size
    names = [p.value["name"].value for p in palette_tag.value]
    cols = [(x, z) for x in range(sx) for z in range(sz) if mask[x][z]]
    cx = sum(c[0] for c in cols) / len(cols)
    cz = sum(c[1] for c in cols) / len(cols)
    best = None
    for x in range(sx):
        for z in range(sz):
            p = indices_tag.value[flat_index(x, 0, z, sy, sz)].value
            if p >= 0 and names[p] in FLOOR_BLOCKS:
                d = (x - cx) ** 2 + (z - cz) ** 2
                if best is None or d < best[0]:
                    best = (d, x, z, names[p])
    if best is None:
        raise ValueError("no floor block found for anchor")
    return best[1], best[2], best[3]


def jigsaw_entity(name, target, target_pool, final_state, x, y, z, joint="aligned"):
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


def palette_block(name, states, version):
    return T_comp({
        "name": T_str(name),
        "states": T_comp(states),
        "version": T_int(version),
    })


def ensure_fountain_openings():
    """The hand-built fountain has one path opening (east). As the hub piece it
    needs all four, so carve a 3-wide doorway through each remaining bench
    (brick floor at y1, clear y2) and add a connection jigsaw matching the east
    one. Idempotent: skips if the extra jigsaws are already present."""
    path = os.path.join(SC, "paths", "fountain.mcstructure")
    root_name, root = load(path)
    size, origin, structure, palette_tag, indices_tag, bpd_tag = piece_info(root)
    sx, sy, sz = size
    conns = [e for e in bpd_tag.value.values()
             if e.value.get("block_entity_data") is not None
             and e.value["block_entity_data"].value["name"].value == CONNECTION_NAME]
    if len(conns) >= 4:
        return
    names = [p.value["name"].value for p in palette_tag.value]
    version = palette_tag.value[0].value["version"].value
    brick = names.index("extrabiomes:dense_cloud_brick")
    air = names.index("minecraft:air")

    # (jigsaw x, z, facing_direction, flanking doorway cells)
    sides = [
        (0, 5, 4, [(0, 4), (0, 6)]),     # west
        (5, 0, 2, [(4, 0), (6, 0)]),     # north
        (5, 10, 3, [(4, 10), (6, 10)]),  # south
    ]
    for jx, jz, fd, flanks in sides:
        for x, z in [(jx, jz)] + flanks:
            indices_tag.value[flat_index(x, 1, z, sy, sz)] = Tag(TAG_INT, brick)
            indices_tag.value[flat_index(x, 2, z, sy, sz)] = Tag(TAG_INT, air)
        palette_tag.value.append(palette_block(
            "minecraft:jigsaw",
            {"facing_direction": T_int(fd), "rotation": T_int(0)}, version))
        f = flat_index(jx, 2, jz, sy, sz)
        indices_tag.value[f] = Tag(TAG_INT, len(palette_tag.value) - 1)
        bpd_tag.value[str(f)] = jigsaw_entity(
            CONNECTION_NAME, CONNECTION_NAME, CONNECTION_NAME, "air",
            origin[0] + jx, origin[1] + 2, origin[2] + jz, joint="rollable")
    save(path, root, root_name)
    print("fountain: carved west/north/south doorways + connection jigsaws")


def water_columns(size, palette_tag, indices_tag):
    """Columns whose floor block (y=0) is water — the pad must pass these through."""
    sx, sy, sz = size
    names = [p.value["name"].value for p in palette_tag.value]
    cols = set()
    for x in range(sx):
        for z in range(sz):
            p = indices_tag.value[flat_index(x, 0, z, sy, sz)].value
            if p >= 0 and names[p] == "minecraft:water":
                cols.add((x, z))
    return cols


def inject_socket(path, pool_id):
    """Add a down-facing jigsaw to the piece floor.

    Returns (anchor_x, anchor_z, size, mask, water_cols)."""
    root_name, root = load(path)
    size, origin, structure, palette_tag, indices_tag, bpd_tag = piece_info(root)
    sx, sy, sz = size
    mask = compute_mask(size, palette_tag, indices_tag)
    water = water_columns(size, palette_tag, indices_tag)

    # already injected? reuse the existing socket position (keep target_pool fresh)
    for key, entry in bpd_tag.value.items():
        bed = entry.value.get("block_entity_data")
        if bed is not None and bed.value.get("name") and bed.value["name"].value == SOCKET_NAME:
            bed.value["target_pool"] = T_str(pool_id)
            save(path, root, root_name)
            f = int(key)
            ax, az = f // (sy * sz), f % sz
            mask[ax][az] = True  # socket replaced the floor block; still a cloud column
            return ax, az, size, mask, water

    ax, az, floor_name = pick_anchor(size, palette_tag, indices_tag, mask)

    version = palette_tag.value[0].value["version"].value
    palette_tag.value.append(palette_block(
        "minecraft:jigsaw",
        {"facing_direction": T_int(0), "rotation": T_int(0)},
        version,
    ))
    jigsaw_pidx = len(palette_tag.value) - 1

    f = flat_index(ax, 0, az, sy, sz)
    indices_tag.value[f] = Tag(TAG_INT, jigsaw_pidx)
    bpd_tag.value[str(f)] = jigsaw_entity(
        SOCKET_NAME, CONNECTOR_NAME, pool_id, floor_name,
        origin[0] + ax, origin[1], origin[2] + az,
    )
    save(path, root, root_name)
    return ax, az, size, mask, water


def edge_distance(mask, sx, sz):
    """Chebyshev distance to the nearest non-mask column (or outside)."""
    INF = 10 ** 6
    d = [[INF if mask[x][z] else 0 for z in range(sz)] for x in range(sx)]
    changed = True
    while changed:
        changed = False
        for x in range(sx):
            for z in range(sz):
                if not mask[x][z]:
                    continue
                nb = []
                for dx in (-1, 0, 1):
                    for dz in (-1, 0, 1):
                        if dx == 0 and dz == 0:
                            continue
                        nx, nz = x + dx, z + dz
                        if 0 <= nx < sx and 0 <= nz < sz:
                            nb.append(d[nx][nz])
                        else:
                            nb.append(0)
                v = min(nb) + 1
                if v < d[x][z]:
                    d[x][z] = v
                    changed = True
    return d


def cloud_height(sx, sz):
    return max(5, min(8, 4 + min(sx, sz) // 3))


def make_structure(sx, sy, sz, cells, palette_entries, bpd_entries):
    """cells: dict (x,y,z)->palette index. Everything else is structure void."""
    total = sx * sy * sz
    layer0 = [Tag(TAG_INT, -1) for _ in range(total)]
    layer1 = [Tag(TAG_INT, -1) for _ in range(total)]
    for (x, y, z), pidx in cells.items():
        layer0[flat_index(x, y, z, sy, sz)] = Tag(TAG_INT, pidx)
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


def cloud_palette(version):
    """0 = dense cloud, 1 = up jigsaw, 2 = down jigsaw, 3-6 = N/S/W/E jigsaws."""
    entries = [
        palette_block(CLOUD_BLOCK, {}, version),
        palette_block("minecraft:jigsaw",
                      {"facing_direction": T_int(1), "rotation": T_int(0)}, version),
        palette_block("minecraft:jigsaw",
                      {"facing_direction": T_int(0), "rotation": T_int(0)}, version),
    ]
    for fd in (2, 3, 4, 5):  # north, south, west, east
        entries.append(palette_block(
            "minecraft:jigsaw",
            {"facing_direction": T_int(fd), "rotation": T_int(0)}, version))
    entries.append(palette_block(
        "minecraft:water", {"liquid_depth": T_int(0)}, version))
    entries.append(palette_block(
        "minecraft:cave_vines", {"growing_plant_age": T_int(1)}, version))
    return entries


FACING_PIDX = {2: 3, 3: 4, 4: 5, 5: 6}  # facing_direction -> cloud_palette index
WATER_PIDX = 7
# Placed at the bottom of every generated water shaft: structure-placed water is
# not scheduled for a liquid tick, so it hangs frozen mid-air. An unsupported
# cave vine under the column breaks on its first random tick, and that block
# update kicks the water into flowing down to the ground.
VINE_PIDX = 8


def side_sockets(mask, sx, sz, ax, az, spacing=6):
    """Outward-facing socket positions (x, z, facing) along the mask boundary,
    one pair per `spacing` rows/columns so long pieces get several."""
    out = []
    for z in range(sz):
        xs = [x for x in range(sx) if mask[x][z]]
        if xs and z % spacing == az % spacing:
            out.append((min(xs), z, 4))  # west edge
            out.append((max(xs), z, 5))  # east edge
    for x in range(sx):
        zs = [z for z in range(sz) if mask[x][z]]
        if zs and x % spacing == ax % spacing:
            out.append((x, min(zs), 2))  # north edge
            out.append((x, max(zs), 3))  # south edge
    return out


def build_pad(short, variant, seed, mask, anchor, sx, sz, water_cols=()):
    """One cloud pad: flat top, domed bushy bottom, puff socket at the deepest point.

    water_cols: piece floor columns holding water — the pad carries them through
    as an enclosed water shaft so the stream exits its underside and falls to the
    ground (climbable from below). Pads with a shaft skip the puff socket so no
    blob spawns under the outlet and intercepts the stream."""
    H = cloud_height(sx, sz)
    ax, az = anchor
    dist = edge_distance(mask, sx, sz)
    water_cols = set(water_cols)

    depth = [[0] * sz for _ in range(sx)]
    for x in range(sx):
        for z in range(sz):
            if not mask[x][z]:
                continue
            d = dist[x][z] + h32(x, z, seed) % 2
            if h32(x * 7 + 3, z * 5 + 1, seed) % 5 == 0:
                d += 2  # hanging tuft
            # dome towards the anchor so the underside bellies out to full depth
            dome = H - max(abs(x - ax), abs(z - az))
            d = max(d, dome)
            depth[x][z] = max(1, min(H, d))
    depth[ax][az] = H  # puff socket lives at the bottom of the anchor column

    # water shafts run full depth, walled by full-depth cloud so they only exit below
    for wx, wz in water_cols:
        for dx in (-1, 0, 1):
            for dz in (-1, 0, 1):
                nx, nz = wx + dx, wz + dz
                if 0 <= nx < sx and 0 <= nz < sz and mask[nx][nz]:
                    depth[nx][nz] = H

    cells = {}
    top = H - 1
    for x in range(sx):
        for z in range(sz):
            if not mask[x][z]:
                continue
            fill = WATER_PIDX if (x, z) in water_cols else 0
            for i in range(depth[x][z]):
                cells[(x, top - i, z)] = fill
    for wx, wz in water_cols:
        cells[(wx, 0, wz)] = VINE_PIDX  # breaks on random tick -> water flows
    cells[(ax, top, az)] = 1   # up connector to the piece

    bpd = {
        str(flat_index(ax, top, az, H, sz)): jigsaw_entity(
            CONNECTOR_NAME, "minecraft:empty", "minecraft:empty",
            CLOUD_BLOCK, ax, top, az),
    }
    if not water_cols:
        cells[(ax, 0, az)] = 2  # down socket for the puff below
        bpd[str(flat_index(ax, 0, az, H, sz))] = jigsaw_entity(
            PUFF_SOCKET_NAME, PUFF_NAME, PUFF_POOL,
            CLOUD_BLOCK, ax, 0, az, joint="rollable")

    # side sockets pull filler slabs into the gaps between paths
    for x, z, fd in side_sockets(mask, sx, sz, ax, az):
        if (x, z) == (ax, az) or (x, z) in water_cols or cells.get((x, top, z), 0) != 0:
            continue  # keep the up connector and water shafts, don't stack sockets
        cells[(x, top, z)] = FACING_PIDX[fd]
        bpd[str(flat_index(x, top, z, H, sz))] = jigsaw_entity(
            FILLER_SOCKET_NAME, FILLER_NAME, FILLER_POOL,
            CLOUD_BLOCK, x, top, z)
    root = make_structure(sx, H, sz, cells, cloud_palette(VERSION_FROM_PIECE), bpd)
    save(os.path.join(CLOUD_DIR, f"{short}_{variant}.mcstructure"), root, "")


def build_filler(variant, seed, w, h):
    """Gap-filler slab with a domed bushy bottom. The connector jigsaws sit one
    layer BELOW the top: they align with the pad side sockets (one under the
    piece floor), so the filler's flat top rises level with the road surface and
    paths/buildings read as cutting through a continuous cloud field."""
    mask = [[True] * w for _ in range(w)]
    dist = edge_distance(mask, w, w)
    c = (w - 1) // 2
    top = h - 1
    joint = h - 2  # connector layer, level with the pad top
    cells = {}
    for x in range(w):
        for z in range(w):
            d = dist[x][z] + h32(x, z, seed) % 2
            dome = h - max(abs(x - c), abs(z - c))
            d = max(2, min(h, max(d, dome)))  # min 2 so the joint layer is backed
            for i in range(d):
                cells[(x, top - i, z)] = 0
    bpd = {}
    for x, z, fd in ((c, 0, 2), (c, w - 1, 3), (0, c, 4), (w - 1, c, 5)):
        cells[(x, joint, z)] = FACING_PIDX[fd]
        bpd[str(flat_index(x, joint, z, h, w))] = jigsaw_entity(
            FILLER_NAME, "minecraft:empty", "minecraft:empty",
            CLOUD_BLOCK, x, joint, z)
    root = make_structure(w, h, w, cells, cloud_palette(VERSION_FROM_PIECE), bpd)
    save(os.path.join(CLOUD_DIR, f"filler_{variant}.mcstructure"), root, "")


# vertical radius profile of a puffy blob, index 0 = top layer
def blob_profile(h):
    prof = []
    for i in range(h):
        t = i / max(1, h - 1)
        # small at top, widest ~1/3 down, wispy at the bottom
        if t <= 0.35:
            prof.append(0.75 + 0.25 * (t / 0.35))
        else:
            prof.append(1.0 - 0.85 * ((t - 0.35) / 0.65) ** 1.2)
    return prof


def build_puff(variant, seed, w, h):
    """A wide free-floating blob that hangs below a pad's puff socket."""
    r = (w - 1) / 2.0
    c = (w - 1) / 2.0
    prof = blob_profile(h)
    cells = {}
    for y in range(h):
        i = h - 1 - y  # 0 at top
        ry = r * prof[i]
        for x in range(w):
            for z in range(w):
                dx, dz = x - c, z - c
                n = (h32(x, z, seed + i * 97) % 1000) / 1000.0 - 0.5
                if dx * dx + dz * dz <= (ry + n * 1.4) ** 2:
                    cells[(x, y, z)] = 0
    cx = int(c)
    cells[(cx, h - 1, cx)] = 1
    bpd = {
        str(flat_index(cx, h - 1, cx, h, w)): jigsaw_entity(
            PUFF_NAME, "minecraft:empty", "minecraft:empty",
            CLOUD_BLOCK, cx, h - 1, cx),
    }
    root = make_structure(w, h, w, cells, cloud_palette(VERSION_FROM_PIECE), bpd)
    save(os.path.join(CLOUD_DIR, f"puff_{variant}.mcstructure"), root, "")


def build_island(variant, seed, w, h, as_puff=False):
    """Start piece: flat top the city sits on, overhung bulging sides, wispy bottom.

    as_puff: write an island_puff variant whose jigsaw is a plain puff connector
    (name must match the pad puff socket's target) with no further expansion, so
    the same island shape can hang below cloud pads without spawning a second city.
    """
    c = (w - 1) / 2.0
    r_top = c - 2.5
    cells = {}
    top = h - 1
    for y in range(h):
        i = top - y  # 0 = surface layer
        t = i / (h - 1)
        if i == 0:
            mult = 1.0
        elif i <= 3:
            mult = 1.0 + 0.06 * i          # belly out just below the rim
        else:
            mult = 1.18 * (1.0 - ((i - 3) / (h - 3)) ** 1.35)
        ry = r_top * mult
        for x in range(w):
            for z in range(w):
                dx, dz = x - c, z - c
                ang = int((math.atan2(dz, dx) + math.pi) * 8 / (2 * math.pi))
                lump = (h32(ang, i // 2, seed) % 1000) / 1000.0 - 0.5
                n = (h32(x, z, seed + i * 131) % 1000) / 1000.0 - 0.5
                rr = ry + lump * 2.2 + n * 1.2
                if i == 0:
                    rr = ry + lump * 1.2   # keep the surface edge tidier
                if rr > c:
                    rr = c
                if dx * dx + dz * dz <= rr * rr:
                    cells[(x, y, z)] = 0
    cx = int(c)
    if not as_puff:
        # water shaft below the fountain hub's outlet, which sits one block east
        # (+x) of the jigsaw column: carries the stream through the island so it
        # falls out the underside and players can swim up from the ground
        shaft_ys = [y for y in range(h) if cells.get((cx + 1, y, cx)) == 0]
        for y in shaft_ys:
            cells[(cx + 1, y, cx)] = WATER_PIDX
        if shaft_ys:
            cells[(cx + 1, min(shaft_ys), cx)] = VINE_PIDX  # water updater
    cells[(cx, top, cx)] = 1  # up jigsaw: city hub spawner, or puff connector
    if as_puff:
        entity = jigsaw_entity(PUFF_NAME, "minecraft:empty", "minecraft:empty",
                               CLOUD_BLOCK, cx, top, cx)
        name = f"island_puff_{variant}"
    else:
        entity = jigsaw_entity(ISLAND_TOP_NAME, SOCKET_NAME, HUB_POOL,
                               CLOUD_BLOCK, cx, top, cx)
        name = f"island_{variant}"
    bpd = {str(flat_index(cx, top, cx, h, w)): entity}
    root = make_structure(w, h, w, cells, cloud_palette(VERSION_FROM_PIECE), bpd)
    save(os.path.join(ISLAND_DIR, f"{name}.mcstructure"), root, "")


def write_pool(filename, pool_id, locations):
    """locations: iterable of location strings or (location, weight) tuples."""
    elements = []
    for loc in locations:
        weight = 1
        if isinstance(loc, tuple):
            loc, weight = loc
        elements.append({
            "element": {
                "element_type": "minecraft:single_pool_element",
                "location": loc,
                "projection": "rigid",
            },
            "weight": weight,
        })
    pool = {
        "format_version": "1.26.10",
        "minecraft:template_pool": {
            "description": {"identifier": pool_id},
            "elements": elements,
        },
    }
    with open(os.path.join(POOL_DIR, filename), "w", encoding="utf-8") as f:
        json.dump(pool, f, indent=2)


os.makedirs(CLOUD_DIR, exist_ok=True)
os.makedirs(ISLAND_DIR, exist_ok=True)
os.makedirs(POOL_DIR, exist_ok=True)

# grab a palette 'version' value from an existing piece so new blocks match
_, _ref = load(os.path.join(SC, "paths", "path.mcstructure"))
VERSION_FROM_PIECE = (
    _ref.value["structure"].value["palette"].value["default"]
    .value["block_palette"].value[0].value["version"].value
)

ensure_fountain_openings()

for rel, short in PIECES.items():
    piece_path = os.path.join(SC, rel.replace("/", os.sep) + ".mcstructure")
    pool_id = f"extrabiomes:sky_city_cloud_{short}"
    ax, az, size, mask, water = inject_socket(piece_path, pool_id)
    sx, sy, sz = size
    for v in range(VARIANTS):
        build_pad(short, v, seed=v * 1000 + len(short), mask=mask,
                  anchor=(ax, az), sx=sx, sz=sz, water_cols=water)
    write_pool(f"{short}.json", pool_id,
               [f"extrabiomes/sky_city/clouds/{short}_{v}" for v in range(VARIANTS)])
    note = f", water shafts {sorted(water)}" if water else ""
    print(f"{rel}: socket ({ax},0,{az}), footprint {sx}x{sz}, pad height {cloud_height(sx, sz)}{note}")

PUFFS = [(7, 4), (9, 5), (11, 5), (13, 6), (11, 6), (9, 4)]
for v, (w, h) in enumerate(PUFFS):
    build_puff(v, seed=8000 + v * 37, w=w, h=h)
# only the biggest puffs, plus the occasional huge island hanging below a pad
write_pool("puff.json", PUFF_POOL, [
    ("extrabiomes/sky_city/clouds/puff_2", 2),
    ("extrabiomes/sky_city/clouds/puff_3", 4),
    ("extrabiomes/sky_city/clouds/puff_4", 3),
    ("extrabiomes/sky_city/islands/island_puff_0", 1),
    ("extrabiomes/sky_city/islands/island_puff_1", 1),
    ("extrabiomes/sky_city/islands/island_puff_2", 1),
])
print(f"puffs: {PUFFS}")

FILLERS = [(14, 6, 3), (12, 6, 3), (10, 6, 3), (8, 5, 2), (6, 5, 2), (4, 4, 1)]  # (w, h, weight)
for v, (w, h, _) in enumerate(FILLERS):
    build_filler(v, seed=5000 + v * 61, w=w, h=h)
write_pool("filler.json", FILLER_POOL,
           [(f"extrabiomes/sky_city/clouds/filler_{v}", wt)
            for v, (_, _, wt) in enumerate(FILLERS)])
print(f"fillers: {FILLERS}")

ISLANDS = [(40, 14), (38, 13), (42, 14)]
for v, (w, h) in enumerate(ISLANDS):
    build_island(v, seed=91 + v * 53, w=w, h=h)
    build_island(v, seed=91 + v * 53, w=w, h=h, as_puff=True)
write_pool("island.json", ISLAND_POOL,
           [f"extrabiomes/sky_city/islands/island_{v}" for v in range(len(ISLANDS))])
# the fountain is the guaranteed start piece: its water shaft runs down through
# the island so players can always swim up into the city
write_pool("hub.json", HUB_POOL, ["extrabiomes/sky_city/paths/fountain"])
print(f"islands: {ISLANDS}")
print("done")
