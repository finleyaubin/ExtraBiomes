"""Build jigsaw-native sky island + clouds for the sky city.

Generation chain:
  island (start piece, big flat-topped lumpy island with hanging cloud pouches)
    -> up jigsaw on its surface pulls in the cross plaza (hub)
      -> city expands via the existing horizontal connection jigsaws
        -> every piece has a down "cloud socket" that hangs a footprint-matched
           cloud pad under it (fails harmlessly over the island body)
          -> every pad has a down "puff socket" that hangs a cumulus / streak /
             mammatus / tower cloud under its deepest point (large pads get a
             second one; fails harmlessly where space is taken)
            -> every puff has down/side "satellite sockets" that pull small
               cloudlets out and down, so clouds trail off and thin out away
               from the city instead of stopping at a hard edge
          -> every pad also has side "filler sockets" along its edges that pull
             organic cloud slabs sideways into the gaps between paths; a
             multi-size filler pool means big slabs fill big gaps and small
             slabs squeeze into tight ones (colliding slabs fail harmlessly)

For every sky city piece this script:
  1. finds the piece's floor plan (solid columns in its bottom two layers),
  2. injects one downward-facing jigsaw block into the floor (the "cloud socket"),
  3. generates cloud pad .mcstructure templates (flat top, rounded bushy bottom)
     whose footprint mirrors the piece's floor plan,
  4. writes a template pool JSON per piece listing the pad variants.
It also generates the island start pieces, the shared puff/satellite/filler
pools, and the hub pool.

Shapes are built from soft-unioned ellipsoid lobes plus smooth value noise (not
per-block white noise), then tidied: lone blocks removed, pinholes filled, and
everything not connected to the piece's connector dropped.

Re-runnable: existing sockets are reused, generated files are overwritten.
"""
import os, json, math, random
from collections import deque

import numpy as np

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
SAT_SOCKET_NAME = "extrabiomes:sky_city_satellite_socket"  # down/side jigsaw in a puff
SAT_NAME = "extrabiomes:sky_city_satellite"                # up + side jigsaws in the satellite
SAT_POOL = "extrabiomes:sky_city_cloud_satellite"
SAT_SIDE_POOL = "extrabiomes:sky_city_cloud_satellite_side"  # side sockets: treed + plain cloudlets
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


# ---------------------------------------------------------------------------
# build-time noise and shape helpers (numpy, arrays indexed [x][y][z])
# ---------------------------------------------------------------------------

def _lattice(ix, iy, iz, seed):
    """Deterministic hash of integer lattice points -> floats in [0, 1)."""
    n = (ix.astype(np.int64) * 374761393 + iy.astype(np.int64) * 668265263
         + iz.astype(np.int64) * 2147483629 + seed * 2246822519) & 0xFFFFFFFF
    n = ((n ^ (n >> 13)) * 1274126177) & 0xFFFFFFFF
    n = n ^ (n >> 16)
    return (n & 0xFFFF) / 65535.0


def vnoise3(X, Y, Z, seed, scale):
    """Smooth value noise in [0, 1]; features are about `scale` blocks across."""
    x, y, z = X / scale, Y / scale, Z / scale
    x0, y0, z0 = np.floor(x), np.floor(y), np.floor(z)
    fx, fy, fz = x - x0, y - y0, z - z0
    sx, sy, sz = fx * fx * (3 - 2 * fx), fy * fy * (3 - 2 * fy), fz * fz * (3 - 2 * fz)
    out = 0.0
    for dx in (0, 1):
        for dy in (0, 1):
            for dz in (0, 1):
                v = _lattice(x0 + dx, y0 + dy, z0 + dz, seed)
                wx = sx if dx else 1 - sx
                wy = sy if dy else 1 - sy
                wz = sz if dz else 1 - sz
                out = out + v * wx * wy * wz
    return out


def fbm(X, Y, Z, seed, scale, octaves=2):
    """Fractal sum of value noise, normalised to [0, 1]."""
    total, amp, norm = 0.0, 1.0, 0.0
    for o in range(octaves):
        total = total + amp * vnoise3(X, Y, Z, seed + o * 131, scale / (2 ** o))
        norm += amp
        amp *= 0.5
    return total / norm


def grid_xyz(w, h, d):
    return np.meshgrid(np.arange(w, dtype=float), np.arange(h, dtype=float),
                       np.arange(d, dtype=float), indexing="ij")


def lobe_field(X, Y, Z, lobes):
    """Soft union of ellipsoids. lobes: (cx, cy, cz, rx, ry_up, ry_down, rz).
    Strongest-lobe union with a little blending: lobes keep their own rounded
    outline but fuse with a neck where they overlap.
    The field is > 0 inside a lobe; callers threshold it."""
    best = np.full_like(X, -1.0)
    total = np.zeros_like(X)
    for cx, cy, cz, rx, ru, rd, rz in lobes:
        ry = np.where(Y >= cy, ru, rd)
        q = 1.0 - (((X - cx) / rx) ** 2 + ((Y - cy) / ry) ** 2 + ((Z - cz) / rz) ** 2)
        q = np.clip(q, 0.0, None)
        best = np.maximum(best, q)
        total += q
    # mostly the strongest lobe (keeps creases between lobes crisp), with a
    # little blending so neighbours fuse instead of just touching
    return best + 0.3 * (total - best)


def neighbor_count(s):
    p = np.pad(s.astype(np.int8), 1)
    return (p[:-2, 1:-1, 1:-1] + p[2:, 1:-1, 1:-1] + p[1:-1, :-2, 1:-1]
            + p[1:-1, 2:, 1:-1] + p[1:-1, 1:-1, :-2] + p[1:-1, 1:-1, 2:])


def tidy(s, passes=2):
    """Drop nubs (<=1 solid neighbour) and fill pinholes (>=5 solid neighbours)."""
    for _ in range(passes):
        n = neighbor_count(s)
        s = (s & (n >= 2)) | (~s & (n >= 5))
    return s


def keep_connected(s, start):
    """Keep only solid cells 6-connected to `start`; floaters are removed."""
    seen = np.zeros_like(s)
    if not s[start]:
        return seen
    q = deque([start])
    seen[start] = True
    while q:
        x, y, z = q.popleft()
        for dx, dy, dz in ((1, 0, 0), (-1, 0, 0), (0, 1, 0), (0, -1, 0), (0, 0, 1), (0, 0, -1)):
            nx, ny, nz = x + dx, y + dy, z + dz
            if (0 <= nx < s.shape[0] and 0 <= ny < s.shape[1] and 0 <= nz < s.shape[2]
                    and s[nx, ny, nz] and not seen[nx, ny, nz]):
                seen[nx, ny, nz] = True
                q.append((nx, ny, nz))
    return seen


def crop(s):
    """Trim to the solid bounding box. Returns (grid, (ox, oy, oz))."""
    xs = np.where(s.any(axis=(1, 2)))[0]
    ys = np.where(s.any(axis=(0, 2)))[0]
    zs = np.where(s.any(axis=(0, 1)))[0]
    o = (int(xs[0]), int(ys[0]), int(zs[0]))
    return s[xs[0]:xs[-1] + 1, ys[0]:ys[-1] + 1, zs[0]:zs[-1] + 1], o


def grid_cells(s):
    xs, ys, zs = np.nonzero(s)
    return {(int(x), int(y), int(z)): 0 for x, y, z in zip(xs, ys, zs)}


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
    """Euclidean distance from each mask column to the nearest non-mask column
    (or to outside the bounding box). Round, not the diamond a Chebyshev
    distance gives."""
    non = [(x, z) for x in range(sx) for z in range(sz) if not mask[x][z]]
    d = [[0.0] * sz for _ in range(sx)]
    for x in range(sx):
        for z in range(sz):
            if not mask[x][z]:
                continue
            best = float(min(x + 1, sx - x, z + 1, sz - z))
            for nx, nz in non:
                best = min(best, math.hypot(x - nx, z - nz))
            d[x][z] = best
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
    entries.append(palette_block(
        "extrabiomes:sky_leaves",
        {"extrabiomes:decay": T_int(1), "extrabiomes:persist": T_int(0),
         "extrabiomes:placed": T_byte(0)}, version))
    entries.append(palette_block(
        "extrabiomes:sky_log", {"minecraft:block_face": T_str("up")}, version))
    return entries


FACING_PIDX = {2: 3, 3: 4, 4: 5, 5: 6}  # facing_direction -> cloud_palette index
WATER_PIDX = 7
# Placed at the bottom of every generated water shaft: structure-placed water is
# not scheduled for a liquid tick, so it hangs frozen mid-air. An unsupported
# cave vine under the column breaks on its first random tick, and that block
# update kicks the water into flowing down to the ground.
VINE_PIDX = 8
LEAF_PIDX = 9   # natural sky leaves (placed=0, decay=1 like the stock sky tree): logs keep them alive, cutting the trunk lets them decay
LOG_PIDX = 10

# The small sky tree (same silhouette as structures/extrabiomes/sky_tree), one
# string per y layer from the trunk base up; rows are z, columns x, trunk at (2, 2).
SKY_TREE = [
    [".....", ".....", "..#..", ".....", "....."],
    [".....", ".....", "..#..", ".....", "....."],
    [".LLL.", "LLLLL", "LL#LL", "LLLLL", ".LLL."],
    [".LLL.", "LLLLL", "LL#LL", "LLLLL", ".LLL."],
    ["..L..", ".LLL.", "LL#LL", ".LLL.", "..L.."],
    ["..L..", ".LLL.", "LL#LL", ".LLL.", "..L.."],
    [".....", "..L..", ".L#L.", "..L..", "....."],
    [".....", "..L..", ".L#L.", "..L..", "....."],
    [".....", ".....", "..L..", ".....", "....."],
    [".....", ".....", "..L..", ".....", "....."],
]
TREE_HEIGHT = len(SKY_TREE)


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


def pick_extra_puffs(mask, dist, sx, sz, anchor, water_cols, min_gap=11, limit=2):
    """Extra puff socket columns for long pads, spread out so the puffs hung
    from them (about 11-17 wide) don't overlap each other or the anchor's."""
    chosen = [anchor]
    cands = [(x, z) for x in range(sx) for z in range(sz)
             if mask[x][z] and dist[x][z] >= 2 and (x, z) not in water_cols]
    extras = []
    while len(extras) < limit:
        best = None
        for x, z in cands:
            gap = min(math.hypot(x - cx, z - cz) for cx, cz in chosen)
            if gap >= min_gap and (best is None or gap > best[0]):
                best = (gap, x, z)
        if best is None:
            break
        chosen.append((best[1], best[2]))
        extras.append((best[1], best[2]))
    return extras


# ---------------------------------------------------------------------------
# pads: footprint-matched cloud hung under a city piece
# ---------------------------------------------------------------------------

def build_pad(short, variant, seed, mask, anchor, sx, sz, water_cols=()):
    """One cloud pad: flat top, rounded bushy bottom, puff socket at the deepest point.

    water_cols: piece floor columns holding water — the pad carries them through
    as an enclosed water shaft so the stream exits its underside and falls to the
    ground (climbable from below). Pads with a shaft skip the puff socket so no
    blob spawns under the outlet and intercepts the stream."""
    rnd = random.Random(seed)
    H = cloud_height(sx, sz)
    ax, az = anchor
    dist = edge_distance(mask, sx, sz)
    water_cols = set(water_cols)

    X, Y, Z = grid_xyz(sx, 1, sz)
    n1 = fbm(X, Y, Z, seed, 4.5, 2)[:, 0, :] - 0.5       # broad swell
    n2 = fbm(X, Y, Z, seed + 77, 2.5, 2)[:, 0, :] - 0.5   # fine lumps

    puffs = [] if water_cols else [(ax, az)] + pick_extra_puffs(
        mask, dist, sx, sz, (ax, az), water_cols)
    # hanging bulges: soft downward lumps scattered over the interior
    interior = [(x, z) for x in range(sx) for z in range(sz)
                if mask[x][z] and dist[x][z] >= 1.5]
    bulges = []
    for _ in range(min(len(interior), 2 + (sx * sz) // 30)):
        px, pz = rnd.choice(interior)
        bulges.append((px, pz, rnd.uniform(2.8, 5.2), rnd.uniform(0.5, 0.95) * H))

    # underside = a thin slab plus hanging hemispherical lobes, so the bottom
    # reads as a few rounded masses rather than concentric steps
    Rrim = max(3.5, H * 0.75)
    hang = [(px, pz, max(3.5, H * 0.8), H) for px, pz in puffs]
    for bx, bz, rad, amp in bulges:
        hang.append((bx, bz, rad, amp))
    depth = [[0] * sz for _ in range(sx)]
    for x in range(sx):
        for z in range(sz):
            if not mask[x][z]:
                continue
            d = 2.0 + n2[x][z] * 1.2
            for hx, hz, rad, amp in hang:
                r = math.hypot(x - hx, z - hz) / rad
                if r < 1.0:
                    d = max(d, amp * math.sqrt(1.0 - r * r) + n2[x][z])
            wob = dist[x][z] + n1[x][z] * 3.0                   # wavy, so contours aren't squares
            t = min(1.0, max(0.0, (wob - 0.5) / Rrim))
            d = min(d, 1.0 + (H - 1) * math.sqrt(1.0 - (1.0 - t) ** 2))  # round lip at the rim
            depth[x][z] = max(1, min(H, int(round(d))))
    for px, pz in puffs:
        depth[px][pz] = H  # puff socket lives at the bottom of its column

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
    for px, pz in puffs:
        cells[(px, 0, pz)] = 2  # down socket for the puff below
        bpd[str(flat_index(px, 0, pz, H, sz))] = jigsaw_entity(
            PUFF_SOCKET_NAME, PUFF_NAME, PUFF_POOL,
            CLOUD_BLOCK, px, 0, pz, joint="rollable")

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


# ---------------------------------------------------------------------------
# fillers: organic slabs that close the gaps between paths
# ---------------------------------------------------------------------------

def build_filler(variant, seed, w, d, h):
    """Gap-filler slab: lobed oval outline, rounded bushy bottom.
    The connector jigsaws sit one layer BELOW the top: they align with the pad
    side sockets (one under the piece floor), so the filler's flat top rises
    level with the road surface and paths/buildings read as cutting through a
    continuous cloud field."""
    cx, cz = (w - 1) / 2.0, (d - 1) / 2.0
    top = h - 1
    joint = h - 2  # connector layer, level with the pad top
    X, Y, Z = grid_xyz(w, 1, d)
    n1 = fbm(X, Y, Z, seed, 4.5, 2)[:, 0, :]
    n2 = fbm(X, Y, Z, seed + 53, 2.5, 2)[:, 0, :] - 0.5

    conns = [(int(cx), 0, 2), (int(cx), d - 1, 3), (0, int(cz), 4), (w - 1, int(cz), 5)]
    rnd = random.Random(seed)
    hang = []
    for _ in range(1 + (w * d) // 45):
        hang.append((rnd.uniform(0.25, 0.75) * w, rnd.uniform(0.25, 0.75) * d,
                     rnd.uniform(0.28, 0.45) * min(w, d) + 1.5, rnd.uniform(0.6, 1.0) * h))
    solid = np.zeros((w, h, d), dtype=bool)
    for x in range(w):
        for z in range(d):
            ex, ez = (x - cx) / (w / 2.0), (z - cz) / (d / 2.0)
            r = math.hypot(ex, ez)
            ang = math.atan2(ez, ex)
            # outline holds at the four side midpoints (where the connectors sit)
            # and indents between them by a smooth noise amount
            indent = 0.34 * n1[x][z] * math.sin(2 * ang) ** 2
            R = 1.0 - indent
            if r >= R:
                continue
            t = 1.0 - r / R                      # 0 at the edge, 1 at the centre
            depth = 2.0 + n2[x][z]
            for hx, hz, rad, amp in hang:
                rr = math.hypot(x - hx, z - hz) / rad
                if rr < 1.0:
                    depth = max(depth, amp * math.sqrt(1.0 - rr * rr))
            depth = min(depth, 2.0 + 6.0 * t)     # slope off toward the rim
            depth = max(2, min(h, int(round(depth))))
            top_y = top
            for i in range(depth):
                solid[x, top_y - i, z] = True
    for px, pz, _ in conns:
        for y in (joint, top):
            solid[px, y, pz] = True
        inx = 1 if px == 0 else (-1 if px == w - 1 else 0)
        inz = 1 if pz == 0 else (-1 if pz == d - 1 else 0)
        solid[px + inx, joint, pz + inz] = True
    solid = tidy(solid, 1)
    for px, pz, _ in conns:
        solid[px, joint, pz] = True
    solid = keep_connected(solid, (int(cx), top, int(cz)))
    for px, pz, _ in conns:
        solid[px, joint, pz] = True

    cells = grid_cells(solid)
    bpd = {}
    for x, z, fd in conns:
        cells[(x, joint, z)] = FACING_PIDX[fd]
        bpd[str(flat_index(x, joint, z, h, d))] = jigsaw_entity(
            FILLER_NAME, "minecraft:empty", "minecraft:empty",
            CLOUD_BLOCK, x, joint, z)
    root = make_structure(w, h, d, cells, cloud_palette(VERSION_FROM_PIECE), bpd)
    save(os.path.join(CLOUD_DIR, f"filler_{variant}.mcstructure"), root, "")


# ---------------------------------------------------------------------------
# puffs and satellites: free-hanging clouds built from lobes
# ---------------------------------------------------------------------------

def shape_cumulus(rnd, w, h, d):
    """Round billowing cumulus: a core with a ring of smaller lobes."""
    cx, cz = (w - 1) / 2.0, (d - 1) / 2.0
    lobes = [(cx, h * 0.56, cz, 0.30 * w, h * 0.42, h * 0.52, 0.30 * d)]
    n = rnd.randint(5, 7)
    for k in range(n):
        ang = 2 * math.pi * k / n + rnd.uniform(-0.35, 0.35)
        reach = rnd.uniform(0.6, 1.0)
        s = rnd.uniform(0.8, 1.2)
        lobes.append((cx + math.cos(ang) * 0.30 * w * reach * 1.15,
                      h * rnd.uniform(0.36, 0.6),
                      cz + math.sin(ang) * 0.30 * d * reach * 1.15,
                      0.19 * w * s, h * rnd.uniform(0.26, 0.38) * s,
                      h * rnd.uniform(0.38, 0.52) * s, 0.19 * d * s))
    return lobes


def shape_streak(rnd, w, h, d):
    """Long, low stratus streak: a row of overlapping flat lobes, bowed in z."""
    cz = (d - 1) / 2.0
    lobes = []
    n = rnd.randint(5, 7)
    bow = rnd.uniform(-0.15, 0.15) * d
    for k in range(n):
        u = k / (n - 1)
        s = rnd.uniform(0.85, 1.2) * (0.7 + 0.5 * math.sin(math.pi * u))
        lobes.append((0.1 * w + 0.8 * w * u + rnd.uniform(-0.04, 0.04) * w,
                      h * rnd.uniform(0.45, 0.62),
                      cz + bow * math.sin(math.pi * u) + rnd.uniform(-0.12, 0.12) * d,
                      0.17 * w * s, h * 0.34 * s, h * 0.5 * s, 0.30 * d * s))
    return lobes


def shape_mammatus(rnd, w, h, d):
    """Flat deck with rounded pouches hanging from its underside."""
    cx, cz = (w - 1) / 2.0, (d - 1) / 2.0
    lobes = [(cx, h - 2.0, cz, 0.5 * w, 1.9, 2.4, 0.5 * d)]
    cols = 3 if w >= 12 else 2
    for i in range(cols):
        for j in range(cols):
            if rnd.random() < 0.2:
                continue
            px = w * (i + 0.5 + rnd.uniform(-0.25, 0.25)) / cols
            pz = d * (j + 0.5 + rnd.uniform(-0.25, 0.25)) / cols
            reach = rnd.uniform(0.45, 1.0)
            rad = 0.16 * (w + d) / 2 * rnd.uniform(0.85, 1.2)
            lobes.append((px, h * 0.62, pz, rad, 2.2, h * 0.62 * reach + 0.6, rad))
    return lobes


def shape_tower(rnd, w, h, d):
    """Heaped cauliflower column, widest at the top and narrowing downward."""
    cx, cz = (w - 1) / 2.0, (d - 1) / 2.0
    lobes = []
    n = 4
    for k in range(n):
        u = k / (n - 1)
        s = 1.0 - 0.5 * u
        lobes.append((cx + rnd.uniform(-0.12, 0.12) * w, h * (0.78 - 0.55 * u),
                      cz + rnd.uniform(-0.12, 0.12) * d,
                      0.36 * w * s, h * 0.2, h * 0.26, 0.36 * d * s))
    return lobes


PUFF_SHAPES = {"cumulus": shape_cumulus, "streak": shape_streak,
               "mammatus": shape_mammatus, "tower": shape_tower}


def finish_cloud(name, solid, conn_xz, rnd, sockets):
    """Tidy a lobe grid, crop it, add the up connector (and optional outgoing
    satellite sockets) and write it as a structure.

    conn_xz: (x, z) of the up connector on the top layer before cropping.
    sockets: how many satellite sockets to add (0 for satellites)."""
    h = solid.shape[1]
    px, pz = conn_xz
    # solid cap around the connector so the cloud merges with whatever hangs above
    solid[max(0, px - 1):px + 2, h - 1, max(0, pz - 1):pz + 2] = True
    solid = tidy(solid, 2)
    solid[px, h - 1, pz] = True
    solid = keep_connected(solid, (px, h - 1, pz))
    solid, (ox, oy, oz) = crop(solid)
    px, pz = px - ox, pz - oz
    w, h, d = solid.shape
    top = h - 1
    cells = grid_cells(solid)
    cells[(px, top, pz)] = 1
    bpd = {str(flat_index(px, top, pz, h, d)): jigsaw_entity(
        PUFF_NAME if sockets else SAT_NAME, "minecraft:empty", "minecraft:empty",
        CLOUD_BLOCK, px, top, pz)}
    return solid, cells, bpd, (w, h, d), (px, top, pz)


def add_sat_sockets(solid, cells, bpd, dims, conn, rnd, count):
    """Down socket on the bottom layer plus side sockets on the bounding-box
    faces, each on a solid cell with solid behind it."""
    w, h, d = dims
    taken = {conn}
    options = []
    # down: farthest solid bottom cell from the connector, backed by cloud above
    bottoms = [(x, z) for x in range(w) for z in range(d)
               if solid[x, 0, z] and h > 1 and solid[x, 1, z]]
    if bottoms:
        bx, bz = max(bottoms, key=lambda c: math.hypot(c[0] - conn[0], c[1] - conn[2]))
        options.append(("down", (bx, 0, bz), 0))
    mid = (h - 1) * 0.45
    faces = [(4, lambda y, z: (0, y, z), lambda y, z: (1, y, z)),
             (5, lambda y, z: (w - 1, y, z), lambda y, z: (w - 2, y, z)),
             (2, lambda y, x: (x, y, 0), lambda y, x: (x, y, 1)),
             (3, lambda y, x: (x, y, d - 1), lambda y, x: (x, y, d - 2))]
    rnd.shuffle(faces)
    for fd, face, inward in faces:
        span = d if fd in (4, 5) else w
        best = None
        for y in range(1, h - 1):
            for u in range(span):
                c, i = face(y, u), inward(y, u)
                if solid[c] and solid[i]:
                    score = abs(y - mid) + rnd.uniform(0, 1.5) - 0.15 * abs(u - span / 2.0)
                    if best is None or score < best[0]:
                        best = (score, c)
        if best is not None:
            options.append((fd, best[1], fd))
    chosen = []
    if options and options[0][0] == "down":
        chosen.append(options.pop(0))
    rnd.shuffle(options)
    chosen += options[:max(0, count - len(chosen))]
    for kind, (x, y, z), fd in chosen:
        if (x, y, z) in taken:
            continue
        taken.add((x, y, z))
        cells[(x, y, z)] = 2 if kind == "down" else FACING_PIDX[fd]
        # a satellite hung below a puff has the puff over its head, so only the
        # side-attached ones (open sky above) draw from the pool with trees
        bpd[str(flat_index(x, y, z, h, d))] = jigsaw_entity(
            SAT_SOCKET_NAME, SAT_NAME, SAT_POOL if kind == "down" else SAT_SIDE_POOL,
            CLOUD_BLOCK, x, y, z, joint="rollable")


def build_puff(variant, seed, kind, w, h, d, satellites=2):
    """A free-hanging cloud that hangs below a pad's puff socket. The connector
    sits off-centre, so the body spreads sideways rather than piling up under
    the pad."""
    rnd = random.Random(seed)
    X, Y, Z = grid_xyz(w, h, d)
    lobes = PUFF_SHAPES[kind](rnd, w, h, d)
    px = int(round((w - 1) / 2.0 + rnd.uniform(-0.2, 0.2) * w))
    pz = int(round((d - 1) / 2.0 + rnd.uniform(-0.2, 0.2) * d))
    lobes.append((px, h - 1.0, pz, 3.6, 2.0, 2.8, 3.6))   # neck up to the pad
    F = lobe_field(X, Y, Z, lobes) + (fbm(X, Y, Z, seed, 5.0, 2) - 0.5) * 0.3
    solid, cells, bpd, dims, conn = finish_cloud(
        f"puff_{variant}", F > 0.18, (px, pz), rnd, sockets=True)
    add_sat_sockets(solid, cells, bpd, dims, conn, rnd, satellites)
    w, h, d = dims
    root = make_structure(w, h, d, cells, cloud_palette(VERSION_FROM_PIECE), bpd)
    save(os.path.join(CLOUD_DIR, f"puff_{variant}.mcstructure"), root, "")
    return dims


def sat_lobes(rnd, kind, w, h, d):
    cx, cz = (w - 1) / 2.0, (d - 1) / 2.0
    top = h - 1.0
    if kind == "cumulus":
        lobes = [(cx, top - 1.2, cz, 0.36 * w, 1.6, h * 0.6, 0.36 * d)]
        for _ in range(3):
            ang = rnd.uniform(0, 2 * math.pi)
            lobes.append((cx + math.cos(ang) * 0.22 * w, top - rnd.uniform(1.2, 2.2),
                          cz + math.sin(ang) * 0.22 * d, 0.24 * w, 1.4, h * 0.45, 0.24 * d))
    elif kind == "streak":
        lobes = []
        for k in range(4):
            u = k / 3.0
            lobes.append((0.15 * w + 0.7 * w * u, top - 1.0 - rnd.uniform(0, 0.5),
                          cz + rnd.uniform(-0.1, 0.1) * d, 0.22 * w, 1.4, 2.4, 0.4 * d))
    elif kind == "pouch":
        lobes = [(cx, top - 1.0, cz, 0.42 * w, 1.5, h * 0.9, 0.42 * d),
                 (cx + 0.18 * w, top - 1.5, cz - 0.1 * d, 0.26 * w, 1.2, h * 0.6, 0.26 * d)]
    else:  # wisp: two flat lobes, barely there
        lobes = [(cx - 0.18 * w, top - 0.8, cz, 0.3 * w, 1.3, 2.0, 0.4 * d),
                 (cx + 0.2 * w, top - 1.0, cz + 0.1 * d, 0.26 * w, 1.2, 1.8, 0.36 * d)]
    return lobes


def build_satellite(variant, seed, kind, w, h, d, trees=0, prefix="satellite"):
    """A small cloudlet. One up connector plus outward side connectors at two
    heights on every face, so it can hang below a socket or hook onto one from
    the side at a varied height. trees > 0 plants that many sky trees on the top
    surface (only used where the top is open sky, see SAT_SIDE_POOL)."""
    rnd = random.Random(seed)
    X, Y, Z = grid_xyz(w, h, d)
    lobes = sat_lobes(rnd, kind, w, h, d)
    F = lobe_field(X, Y, Z, lobes) + (fbm(X, Y, Z, seed, 4.0, 2) - 0.5) * 0.25
    solid = F > 0.15
    cxz = (int(round((w - 1) / 2.0)), int(round((d - 1) / 2.0)))
    solid, cells, bpd, dims, conn = finish_cloud(
        f"{prefix}_{variant}", solid, cxz, rnd, sockets=False)
    w, h, d = dims
    if trees:
        # level the crown into a plateau so trunks have flat footing: any column
        # whose surface is within 2 of the top is raised to the top layer
        for x in range(w):
            for z in range(d):
                ys = np.nonzero(solid[x, :, z])[0]
                if len(ys) and ys.max() >= h - 3:
                    for y in range(int(ys.max()) + 1, h):
                        solid[x, y, z] = True
                        cells.setdefault((x, y, z), 0)
    top_solid = solid[:, h - 1, :].copy()
    # side connectors, outward-facing, on the middle of each bounding-box face
    mx, mz = (w - 1) // 2, (d - 1) // 2
    layers = sorted({max(0, h - 2), min(1, h - 1)}) if h >= 3 else [0]
    for jl in layers:
        for x, z, fd, ix, iz in ((0, mz, 4, 1, mz), (w - 1, mz, 5, w - 2, mz),
                                 (mx, 0, 2, mx, 1), (mx, d - 1, 3, mx, d - 2)):
            if (x, jl, z) in bpd or (x, jl, z) == conn:
                continue
            cells[(x, jl, z)] = FACING_PIDX[fd]
            # carve inward from the face until the connector meets the body, so
            # it is never a floating block
            sx_, sz_ = ix - x, iz - z
            cx_, cz_ = ix, iz
            for _ in range(6):
                if not (0 <= cx_ < w and 0 <= cz_ < d) or (cx_, jl, cz_) in cells:
                    break
                cells[(cx_, jl, cz_)] = 0
                cx_, cz_ = cx_ + sx_, cz_ + sz_
            bpd[str(flat_index(x, jl, z, h, d))] = jigsaw_entity(
                SAT_NAME, "minecraft:empty", "minecraft:empty", CLOUD_BLOCK, x, jl, z)
    planted = plant_trees(cells, top_solid, conn, h, rnd, trees) if trees else 0
    if trees and planted == 0:
        raise ValueError(f"{prefix}_{variant}: no flat spot for a tree")
    if planted:
        # re-key jigsaw entities for the taller bounding box
        new_h = h + TREE_HEIGHT
        rekeyed = {}
        for key, ent in bpd.items():
            f = int(key)
            x, y, z = f // (h * d), (f // d) % h, f % d
            rekeyed[str(flat_index(x, y, z, new_h, d))] = ent
        bpd, h = rekeyed, new_h
        dims = (w, h, d)
    root = make_structure(w, h, d, cells, cloud_palette(VERSION_FROM_PIECE), bpd)
    save(os.path.join(CLOUD_DIR, f"{prefix}_{variant}.mcstructure"), root, "")
    return dims


def plant_trees(cells, top_solid, conn, top_layer_h, rnd, count):
    """Plant up to `count` sky trees on the satellite's top surface. A trunk
    needs a mostly flat 3x3 patch of cloud and room for the 5-wide canopy, and
    keeps clear of the up connector and of other trunks. Returns trees planted."""
    w, d = top_solid.shape
    top = top_layer_h - 1
    cands = []
    for x in range(2, w - 2):
        for z in range(2, d - 2):
            patch = top_solid[x - 1:x + 2, z - 1:z + 2]
            if top_solid[x, z] and patch.sum() >= 8 and cells.get((x, top, z)) == 0 \
                    and math.hypot(x - conn[0], z - conn[2]) >= 3:
                edge = min(x, w - 1 - x, z, d - 1 - z)
                cands.append((edge + rnd.uniform(0, 1.2), x, z))
    cands.sort(reverse=True)
    trunks = []
    for _, x, z in cands:
        if all(math.hypot(x - tx, z - tz) >= 6 for tx, tz in trunks):
            trunks.append((x, z))
            if len(trunks) == count:
                break
    for tx, tz in trunks:
        for ly, layer in enumerate(SKY_TREE):
            for lz, row in enumerate(layer):
                for lx, ch in enumerate(row):
                    if ch == ".":
                        continue
                    pos = (tx - 2 + lx, top + 1 + ly, tz - 2 + lz)
                    if ch == "#" or pos not in cells:
                        cells[pos] = LOG_PIDX if ch == "#" else LEAF_PIDX
    return len(trunks)


# ---------------------------------------------------------------------------
# islands
# ---------------------------------------------------------------------------

def build_island(variant, seed, w, h, as_puff=False):
    """Start piece: flat top the city sits on, lumpy overhanging rim, and a
    cumulus-like underbelly of hanging pouches.

    as_puff: write an island_puff variant whose jigsaw is a plain puff connector
    (name must match the pad puff socket's target) with no further expansion, so
    the same island shape can hang below cloud pads without spawning a second city.
    """
    rnd = random.Random(seed)
    c = (w - 1) / 2.0
    cx = int(c)
    top = h - 1
    X, Y, Z = grid_xyz(w, h, w)
    i = top - Y                       # 0 = surface layer
    mult = np.where(i == 0, 1.0,
                    np.where(i <= 3, 1.0 + 0.055 * i,
                             1.16 * (1.0 - np.clip((i - 3) / (h - 3.0), 0, 1) ** 1.35)))
    r_top = c - 2.6
    body = h - 4                       # bowl layers; the pouches hang below it
    ex = 1.0 + rnd.uniform(-0.08, 0.08)   # slightly oval, never a perfect disc
    dx, dz = (X - c) / ex, (Z - c) * ex
    rho2 = (dx * dx + dz * dz) / (r_top * r_top)
    n = fbm(X, Y * 1.3, Z, seed, 8.0, 3) - 0.5
    bowl = rho2 + (np.clip(i, 0, None) / body) ** 2.0
    # implicit ellipsoid with strong 3D noise: lumps instead of concentric rings;
    # the surface layer only gets mild noise so the rim stays tidy
    solid = np.where(i == 0, rho2 <= 1.0 + n * 0.9, bowl <= 1.0 + n * 1.1) & (i < body)
    solid &= (dx * dx + dz * dz) <= (c - 0.5) ** 2

    lobes = []                                           # hanging pouches below
    for _ in range(rnd.randint(11, 15)):
        ang = rnd.uniform(0, 2 * math.pi)
        rad = rnd.uniform(0.05, 0.78) * r_top * (1.0 - 0.0)
        top_y = rnd.uniform(4.0, 6.5)
        rad_xz = rnd.uniform(2.6, 6.0)
        lobes.append((c + math.cos(ang) * rad, top_y, c + math.sin(ang) * rad,
                      rad_xz, 2.5, top_y - rnd.uniform(0.0, 0.7), rad_xz))
    solid |= lobe_field(X, Y, Z, lobes) > 0.2
    solid = tidy(solid, 2)

    # enclosed core around the fountain's water shaft (one block east of the
    # jigsaw column) so the stream can't leak sideways out of the island
    solid[cx:cx + 3, :, cx - 1:cx + 2] = True
    solid = keep_connected(solid, (cx, top, cx))
    cells = grid_cells(solid)
    if not as_puff:
        # water shaft below the fountain hub's outlet: carries the stream through
        # the island so it falls out the underside and players can swim up from
        # the ground
        shaft_ys = [y for y in range(h) if (cx + 1, y, cx) in cells]
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
        json.dump(pool, f, indent=4)


def clear_generated(directory, prefixes):
    """Drop stale templates whose count or naming changed between runs."""
    for name in os.listdir(directory):
        if name.endswith(".mcstructure") and any(name.startswith(p) for p in prefixes):
            os.remove(os.path.join(directory, name))


# (kind, w, h, d, weight) - big, off-centre clouds that spread out from the pad
PUFFS = [
    ("cumulus", 17, 9, 15, 3), ("cumulus", 15, 8, 17, 3), ("cumulus", 19, 9, 17, 2),
    ("streak", 23, 6, 10, 2), ("streak", 19, 5, 9, 2),
    ("mammatus", 15, 10, 15, 3), ("mammatus", 17, 9, 13, 2),
    ("tower", 13, 12, 13, 1),
]
# (kind, w, h, d, weight) - small trailing cloudlets
SATELLITES = [
    ("cumulus", 15, 9, 13, 3), ("cumulus", 12, 7, 11, 3), ("pouch", 11, 10, 10, 2),
    ("streak", 17, 5, 9, 2), ("wisp", 13, 5, 9, 3), ("wisp", 10, 4, 8, 2),
]
# (w, d, h, weight) - gap fillers, from wide slabs down to pocket-sized
FILLERS = [
    (15, 9, 6, 3), (13, 13, 6, 3), (11, 7, 5, 3), (9, 9, 5, 2),
    (7, 5, 4, 2), (12, 8, 5, 2), (5, 5, 4, 1),
]
# (kind, w, h, d, weight, trees) - wide, flat-topped cloudlets that carry sky trees
TREE_SATELLITES = [
    ("cumulus", 15, 8, 13, 3, 2), ("cumulus", 13, 7, 11, 3, 1), ("streak", 19, 5, 11, 2, 2),
]
ISLANDS = [(40, 14), (38, 13), (42, 14)]


def main():
    os.makedirs(CLOUD_DIR, exist_ok=True)
    os.makedirs(ISLAND_DIR, exist_ok=True)
    os.makedirs(POOL_DIR, exist_ok=True)
    clear_generated(CLOUD_DIR, ("puff_", "satellite_", "filler_"))  # "satellite_" also covers satellite_tree_

    # grab a palette 'version' value from an existing piece so new blocks match
    global VERSION_FROM_PIECE
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

    puff_dims = []
    for v, (kind, w, h, d, _) in enumerate(PUFFS):
        puff_dims.append(build_puff(v, seed=8000 + v * 37, kind=kind, w=w, h=h, d=d))
    # weighted puff shapes, plus the occasional huge island hanging below a pad
    write_pool("puff.json", PUFF_POOL,
               [(f"extrabiomes/sky_city/clouds/puff_{v}", p[4]) for v, p in enumerate(PUFFS)]
               + [(f"extrabiomes/sky_city/islands/island_puff_{v}", 1)
                  for v in range(len(ISLANDS))])
    print(f"puffs: {puff_dims}")

    sat_dims = []
    for v, (kind, w, h, d, _) in enumerate(SATELLITES):
        sat_dims.append(build_satellite(v, seed=3000 + v * 41, kind=kind, w=w, h=h, d=d))
    write_pool("satellite.json", SAT_POOL,
               [(f"extrabiomes/sky_city/clouds/satellite_{v}", s[4])
                for v, s in enumerate(SATELLITES)])
    print(f"satellites: {sat_dims}")

    tree_dims = []
    for v, (kind, w, h, d, _, n) in enumerate(TREE_SATELLITES):
        tree_dims.append(build_satellite(v, seed=3500 + v * 43, kind=kind, w=w, h=h, d=d,
                                         trees=n, prefix="satellite_tree"))
    # side sockets: treed cloudlets first choice, plain ones as the fallback where
    # the taller treed shape doesn't fit
    write_pool("satellite_side.json", SAT_SIDE_POOL,
               [(f"extrabiomes/sky_city/clouds/satellite_tree_{v}", t[4])
                for v, t in enumerate(TREE_SATELLITES)]
               + [(f"extrabiomes/sky_city/clouds/satellite_{v}", 1)
                  for v in range(len(SATELLITES))])
    print(f"tree satellites: {tree_dims}")

    for v, (w, d, h, _) in enumerate(FILLERS):
        build_filler(v, seed=5000 + v * 61, w=w, d=d, h=h)
    write_pool("filler.json", FILLER_POOL,
               [(f"extrabiomes/sky_city/clouds/filler_{v}", f[3])
                for v, f in enumerate(FILLERS)])
    print(f"fillers: {FILLERS}")

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


if __name__ == "__main__":
    main()
