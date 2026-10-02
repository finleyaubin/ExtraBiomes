"""Render isometric previews of the generated sky city clouds.

Usage:
    python tools/preview_clouds.py <out_dir>

Writes contact sheets (one per template group), each template shown from above
and from below, plus a mock composite of pads, puffs, satellites and fillers
hung under a small cross-shaped city. Needs only Pillow and numpy. The images
are for eyeballing silhouettes while tuning build_clouds.py; they are not
committed.

Colours: cloud = white-blue, water = blue, jigsaw = magenta, cave vine and
sky leaves = green, sky log = brown, gilded log = gold, sapling = light green.
"""
import math
import os
import sys

import numpy as np
from PIL import Image, ImageChops, ImageDraw

from mcstructure import load, to_py

HERE = os.path.dirname(os.path.abspath(__file__))
SC = os.path.join(HERE, "..", "ExtraBiomes - Bedrock", "packs", "BP",
                  "structures", "extrabiomes", "sky_city")
CLOUD_DIR = os.path.join(SC, "clouds")
ISLAND_DIR = os.path.join(SC, "islands")

EMPTY, CLOUD, WATER, JIGSAW, VINE, LEAF, LOG, GILDED, SAPLING = 0, 1, 2, 3, 4, 5, 6, 7, 8
SLAB_B, SLAB_T = 9, 10          # half blocks
STAIR0 = 20                     # 20 + 4 * (half == top) + index in DIRS: a stair, tall back toward DIRS[i]
DIRS = ["north", "south", "west", "east"]
COLORS = {
    CLOUD: ((244, 247, 252), (208, 218, 234), (172, 188, 210)),
    WATER: ((90, 150, 235), (70, 125, 215), (55, 100, 190)),
    JIGSAW: ((235, 60, 200), (200, 40, 170), (160, 30, 140)),
    VINE: ((70, 190, 80), (55, 160, 65), (45, 130, 55)),
    LEAF: ((96, 190, 92), (70, 150, 70), (52, 120, 56)),
    LOG: ((150, 108, 68), (118, 82, 50), (92, 64, 40)),
    GILDED: ((236, 200, 90), (205, 168, 64), (170, 136, 48)),
    SAPLING: ((140, 230, 120), (110, 200, 95), (85, 165, 75)),
}
BG = (36, 44, 66)
for _k in [SLAB_B, SLAB_T] + list(range(STAIR0, STAIR0 + 8)):
    COLORS[_k] = COLORS[CLOUD]


def boxes(kind):
    """Sub-boxes (x0, x1, y0, y1, z0, z1), in cell fractions, a block kind occupies."""
    if kind == SLAB_B:
        return [(0, 1, 0, .5, 0, 1)]
    if kind == SLAB_T:
        return [(0, 1, .5, 1, 0, 1)]
    if kind >= STAIR0:
        top, di = divmod(kind - STAIR0, 4)
        base = (0, 1, .5, 1, 0, 1) if top else (0, 1, 0, .5, 0, 1)
        y0, y1 = (0, .5) if top else (.5, 1)
        back = {"north": (0, 1, y0, y1, 0, .5), "south": (0, 1, y0, y1, .5, 1),
                "west": (0, .5, y0, y1, 0, 1), "east": (.5, 1, y0, y1, 0, 1)}[DIRS[di]]
        return [base, back]
    return [(0, 1, 0, 1, 0, 1)]


def mirror_y(kind):
    if kind == SLAB_B:
        return SLAB_T
    if kind == SLAB_T:
        return SLAB_B
    if kind >= STAIR0:
        top, di = divmod(kind - STAIR0, 4)
        return STAIR0 + 4 * (1 - top) + di
    return kind


def load_grid(path):
    """Structure -> int array [x][y][z] of EMPTY/CLOUD/WATER/JIGSAW/VINE."""
    _, root = load(path)
    r = to_py(root)
    sx, sy, sz = r["size"]
    pal = r["structure"]["palette"]["default"]["block_palette"]
    idx = np.array(r["structure"]["block_indices"][0]).reshape(sx, sy, sz)
    kinds = []
    for p in pal:
        n = p["name"]
        kinds.append(JIGSAW if n == "minecraft:jigsaw" else WATER if n == "minecraft:water"
                     else VINE if n == "minecraft:cave_vines" else LEAF if n == "extrabiomes:sky_leaves"
                     else LOG if n == "extrabiomes:sky_log" else GILDED if n == "extrabiomes:gilded_sky_log"
                     else SAPLING if n == "extrabiomes:sky_sapling_block" else CLOUD)
    for pi, p in enumerate(pal):
        st = p["states"]
        if p["name"] == "extrabiomes:dense_cloud_slab":
            kinds[pi] = SLAB_T if st.get("minecraft:vertical_half") == "top" else SLAB_B
        elif p["name"] == "extrabiomes:dense_cloud_stairs":
            kinds[pi] = (STAIR0 + (4 if st.get("minecraft:vertical_half") == "top" else 0)
                         + DIRS.index(st.get("minecraft:cardinal_direction", "north")))
    grid = np.zeros((sx, sy, sz), dtype=np.int8)
    for pi, k in enumerate(kinds):
        grid[idx == pi] = k
    return grid


def render_iso(grid, u=6, flip=False):
    """Painter's-algorithm voxel render with half blocks. flip=True mirrors y
    first, which shows the underside as seen from below."""
    if flip:
        grid = np.vectorize(mirror_y)(grid[:, ::-1, :]).astype(np.int8)
    sx, sy, sz = grid.shape
    solid = grid != EMPTY
    full = solid & ((grid < SLAB_B) | ((grid > SLAB_T) & (grid < STAIR0)))   # cubes that hide neighbours' faces
    pad = np.pad(full, 1)

    def proj(x, y, z):
        return ((x - z) * u * 0.866, (x + z) * u * 0.5 - y * u)

    xs, ys, zs = np.nonzero(solid)
    if len(xs) == 0:
        return Image.new("RGB", (8, 8), BG)
    corners = [proj(x, y, z) for x in (0, sx) for y in (0, sy) for z in (0, sz)]
    minx = min(c[0] for c in corners) - 4
    miny = min(c[1] for c in corners) - 4
    w = int(max(c[0] for c in corners) - minx + 4)
    h = int(max(c[1] for c in corners) - miny + 4)
    img = Image.new("RGB", (w, h), BG)
    dr = ImageDraw.Draw(img)
    order = np.argsort(xs + ys + zs, kind="stable")
    for k in order:
        x, y, z = int(xs[k]), int(ys[k]), int(zs[k])
        kind = int(grid[x, y, z])
        top, left, right = COLORS[kind]
        for (x0, x1, y0, y1, z0, z1) in sorted(boxes(kind), key=lambda b_: b_[2]):
            X0, X1, Y0, Y1, Z0, Z1 = x + x0, x + x1, y + y0, y + y1, z + z0, z + z1
            faces = []
            if not (y1 == 1 and pad[x + 1, y + 2, z + 1]):
                faces.append((top, [(X0, Y1, Z0), (X1, Y1, Z0), (X1, Y1, Z1), (X0, Y1, Z1)]))
            if not (x1 == 1 and pad[x + 2, y + 1, z + 1]):
                faces.append((right, [(X1, Y0, Z0), (X1, Y0, Z1), (X1, Y1, Z1), (X1, Y1, Z0)]))
            if not (z1 == 1 and pad[x + 1, y + 1, z + 2]):
                faces.append((left, [(X0, Y0, Z1), (X1, Y0, Z1), (X1, Y1, Z1), (X0, Y1, Z1)]))
            for color, quad in faces:
                pts = [(proj(*p_)[0] - minx, proj(*p_)[1] - miny) for p_ in quad]
                dr.polygon(pts, fill=color)
    box = ImageChops.difference(img, Image.new("RGB", img.size, BG)).getbbox()
    return img.crop((max(0, box[0] - 4), max(0, box[1] - 4), box[2] + 4, box[3] + 4)) if box else img


def sheet(items, out_path, cols=3, u=6):
    """items: list of (label, grid). Each is drawn from above and from below."""
    tiles = []
    for label, grid in items:
        a = render_iso(grid, u)
        b = render_iso(grid, u, flip=True)
        tw, th = a.width + b.width + 12, max(a.height, b.height) + 18
        tile = Image.new("RGB", (tw, th), BG)
        tile.paste(a, (4, 16))
        tile.paste(b, (a.width + 8, 16))
        ImageDraw.Draw(tile).text((4, 2), f"{label}  {grid.shape[0]}x{grid.shape[1]}x{grid.shape[2]}",
                                  fill=(255, 255, 255))
        tiles.append(tile)
    rows = [tiles[i:i + cols] for i in range(0, len(tiles), cols)]
    widths = [max(sum(t.width for t in row), 1) for row in rows]
    W = max(widths)
    H = sum(max(t.height for t in row) + 6 for row in rows)
    img = Image.new("RGB", (W, H), (20, 24, 36))
    y = 0
    for row in rows:
        x = 0
        for t in row:
            img.paste(t, (x, y))
            x += t.width
        y += max(t.height for t in row) + 6
    img.save(out_path)


def group(prefix, directory=CLOUD_DIR):
    names = sorted(f[:-len(".mcstructure")] for f in os.listdir(directory)
                   if f.startswith(prefix) and f.endswith(".mcstructure"))
    key = lambda n: (len(n), n)
    return [(n, load_grid(os.path.join(directory, n + ".mcstructure"))) for n in sorted(names, key=key)]


def jigsaws(grid):
    return [tuple(int(v) for v in c) for c in np.argwhere(grid == JIGSAW)]


class Canvas:
    """A big voxel field that templates are stamped into for the mock composite."""

    def __init__(self, w, h, d):
        self.g = np.zeros((w, h, d), dtype=np.int8)
        self.o = (0, 0, 0)

    def stamp(self, grid, ox, oy, oz, as_cloud=True):
        sx, sy, sz = grid.shape
        for x, y, z in np.argwhere(grid != EMPTY):
            X, Y, Z = x + ox, y + oy, z + oz
            if (0 <= X < self.g.shape[0] and 0 <= Y < self.g.shape[1]
                    and 0 <= Z < self.g.shape[2] and self.g[X, Y, Z] == EMPTY):
                v = grid[x, y, z]
                self.g[X, Y, Z] = CLOUD if as_cloud and v in (WATER, VINE, JIGSAW) else v


def mock_city(out_path, seed=3):
    """Hang pads, puffs, satellites and fillers under a plus-shaped layout using
    the real generated templates. Collisions are voxel-level (a stand-in for the
    game's bounding-box test), so it approximates what generation produces."""
    import random
    rnd = random.Random(seed)
    pads = {n: g for n, g in group("cross_") + group("straight_") + group("t_") + group("curve_")}
    puffs = [g for _, g in group("puff_")]
    sats = [g for n, g in group("satellite_") if not n.startswith("satellite_tree") and "virga" not in n]
    tree_sats = [g for _, g in group("satellite_tree_")]
    fills = [g for _, g in group("filler_")]
    # (pad name, offset x, z) - a plus of pieces laid edge to edge
    layout = [("cross_0", 40, 40), ("straight_0", 49, 42), ("straight_1", 28, 42),
              ("t_0", 61, 40), ("curve_0", 15, 36), ("cross_2", 40, 49),
              ("straight_2", 42, 28)]
    cv = Canvas(120, 60, 120)
    top_y = 40
    occupied = np.zeros(cv.g.shape, dtype=bool)

    def free(grid, ox, oy, oz):
        for x, y, z in np.argwhere(grid != EMPTY):
            X, Y, Z = x + ox, y + oy, z + oz
            if not (0 <= X < 120 and 0 <= Y < 60 and 0 <= Z < 120) or occupied[X, Y, Z]:
                return False
        return True

    def commit(grid, ox, oy, oz):
        for x, y, z in np.argwhere(grid != EMPTY):
            occupied[x + ox, y + oy, z + oz] = True
        cv.stamp(grid, ox, oy, oz)

    for name, px, pz in layout:
        g = pads[name]
        oy = top_y - (g.shape[1] - 1)
        if not free(g, px, oy, pz):
            continue
        commit(g, px, oy, pz)
        for (jx, jy, jz) in jigsaws(g):
            facing_down = jy == 0
            if facing_down:
                # puff hangs directly below, spun a random quarter turn about the socket
                p = rnd.choice(puffs)
                k = rnd.randint(0, 3)
                p = np.rot90(p, k, axes=(0, 2))
                up = [c for c in jigsaws(p) if c[1] == p.shape[1] - 1]
                if not up:
                    continue
                ux, uy, uz = up[0]
                ox, oy2, oz = px + jx - ux, oy - 1 - uy, pz + jz - uz
                if free(p, ox, oy2, oz):
                    commit(p, ox, oy2, oz)
                    # satellites below / beside the puff
                    for (sx_, sy_, sz_) in jigsaws(p):
                        if sy_ == p.shape[1] - 1 and (sx_, sz_) == (ux, uz):
                            continue
                        pw, ph, pd = p.shape
                        face = ("w" if sx_ == 0 else "e" if sx_ == pw - 1
                                else "n" if sz_ == 0 else "s" if sz_ == pd - 1 else None)
                        if sy_ > 0 and face:
                            # side socket: hook a treed (or plain) cloudlet on from the side
                            s = rnd.choice(tree_sats + sats[:2])
                            sw, sh, sd = s.shape
                            want = {"w": lambda c: c[0] == sw - 1, "e": lambda c: c[0] == 0,
                                    "n": lambda c: c[2] == sd - 1, "s": lambda c: c[2] == 0}[face]
                            cj = sorted((c for c in jigsaws(s) if want(c) and c[1] > 0),
                                        key=lambda c: -c[1])
                            if cj:
                                cx, cy, cz = cj[0]
                                dx, dz = {"w": (-1, 0), "e": (1, 0), "n": (0, -1), "s": (0, 1)}[face]
                                sox = ox + sx_ + dx - cx
                                soy = oy2 + sy_ - cy
                                soz = oz + sz_ + dz - cz
                                if free(s, sox, soy, soz):
                                    commit(s, sox, soy, soz)
                            continue
                        s = rnd.choice(sats)
                        sup = [c for c in jigsaws(s) if c[1] == s.shape[1] - 1]
                        if sy_ == 0 and sup:
                            cx, cy, cz = sup[0]
                            sox, soy, soz = ox + sx_ - cx, oy2 - 1 - cy, oz + sz_ - cz
                            if free(s, sox, soy, soz):
                                commit(s, sox, soy, soz)
            elif jy == g.shape[1] - 1:
                f = rnd.choice(fills)
                # side socket: find the filler connector on the opposite face
                fx = 0 if jx == 0 else (g.shape[0] - 1 if jx == g.shape[0] - 1 else None)
                if fx is None:
                    continue
                jl = max(c[1] for c in jigsaws(f))
                cand = [c for c in jigsaws(f) if c[1] == jl
                        and ((jx == 0 and c[0] == f.shape[0] - 1) or (jx != 0 and c[0] == 0))]
                if not cand:
                    continue
                cx, cy, cz = cand[0]
                dx = -1 if jx == 0 else 1
                fox, foy, foz = px + jx + dx - cx, oy + jy - cy, pz + jz - cz
                if free(f, fox, foy, foz):
                    commit(f, fox, foy, foz)
    tile_a = render_iso(cv.g, 7)
    tile_b = render_iso(cv.g, 7, flip=True)
    img = Image.new("RGB", (tile_a.width + tile_b.width + 12, max(tile_a.height, tile_b.height)), BG)
    img.paste(tile_a, (4, 0))
    img.paste(tile_b, (tile_a.width + 8, 0))
    img.save(out_path)


def main(argv):
    if len(argv) != 1:
        print(__doc__)
        return 2
    out = argv[0]
    os.makedirs(out, exist_ok=True)
    pads = []
    for short in ("cross", "path_end", "fountain", "roundabout", "sky_challet"):
        pads += group(short + "_")[:2]
    sheet(pads, os.path.join(out, "pads.png"), cols=2)
    sheet(group("puff_"), os.path.join(out, "puffs.png"), cols=2)
    plain = [g for g in group("satellite_") if not g[0].startswith("satellite_tree")]
    treed = group("satellite_tree_")
    sheet([g for g in plain if "virga" not in g[0]], os.path.join(out, "satellites.png"), cols=3, u=9)
    sheet([g for g in plain if "virga" in g[0]], os.path.join(out, "virga.png"), cols=3, u=8)
    sheet(group("bank_"), os.path.join(out, "banks.png"), cols=1, u=6)
    sheet(treed, os.path.join(out, "satellites_trees.png"), cols=1, u=9)
    fillers = group("filler_")
    sheet([g for g in fillers if "tree" not in g[0]], os.path.join(out, "fillers.png"), cols=2, u=8)
    sheet([g for g in fillers if "tree" in g[0]], os.path.join(out, "fillers_trees.png"), cols=1, u=8)
    sheet(group("island_", ISLAND_DIR)[:3], os.path.join(out, "islands.png"), cols=1, u=7)
    mock_city(os.path.join(out, "mock_city.png"), seed=3)
    print("wrote previews to", out)
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
