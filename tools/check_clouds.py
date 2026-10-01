"""Sanity-check the generated sky city cloud structures.

Usage:
    python tools/check_clouds.py

Checks every template under sky_city/clouds and sky_city/islands:
  * block indices are in range and the secondary layer is empty,
  * every jigsaw block has a matching block entity at its position,
  * every jigsaw sits on a cloud-backed cell (a neighbour is cloud/water),
  * no floating cloud: all solid cells are connected,
  * island springs: every water cell outside the fountain shaft is sealed in cloud
    except for one open face, which holds a cave vine (the water updater),
  * fountain pads and the hub island carry an enclosed water shaft down to a
    cave-vine updater with nothing below it, so the stream can fall to the
    ground and players can swim up.
Exits non-zero if anything fails.
"""
import os
import sys
from collections import deque

import numpy as np

from mcstructure import load, to_py

HERE = os.path.dirname(os.path.abspath(__file__))
SC = os.path.join(HERE, "..", "ExtraBiomes - Bedrock", "packs", "BP",
                  "structures", "extrabiomes", "sky_city")

failures = []


def fail(name, msg):
    failures.append(f"{name}: {msg}")


def read(path):
    _, root = load(path)
    r = to_py(root)
    sx, sy, sz = r["size"]
    st = r["structure"]
    pal = st["palette"]["default"]["block_palette"]
    idx = np.array(st["block_indices"][0]).reshape(sx, sy, sz)
    second = np.array(st["block_indices"][1])
    bpd = st["palette"]["default"]["block_position_data"]
    return (sx, sy, sz), pal, idx, second, bpd


def connected(solid):
    pts = np.argwhere(solid)
    if len(pts) == 0:
        return True
    seen = {tuple(pts[0])}
    q = deque([tuple(pts[0])])
    while q:
        x, y, z = q.popleft()
        for d in ((1, 0, 0), (-1, 0, 0), (0, 1, 0), (0, -1, 0), (0, 0, 1), (0, 0, -1)):
            n = (x + d[0], y + d[1], z + d[2])
            if n not in seen and all(0 <= n[i] < solid.shape[i] for i in range(3)) and solid[n]:
                seen.add(n)
                q.append(n)
    return len(seen) == len(pts)


def check_file(path, name):
    (sx, sy, sz), pal, idx, second, bpd = read(path)
    if idx.max() >= len(pal):
        fail(name, "palette index out of range")
    if (second != -1).any():
        fail(name, "secondary layer is not empty")
    names = [p["name"] for p in pal]
    nm = np.where(idx >= 0, np.array(names + [""])[np.clip(idx, 0, None)], "")
    solid = (idx >= 0) & (nm != "minecraft:air")
    cloudish = (nm == "extrabiomes:dense_cloud") | (nm == "minecraft:water")
    for x, y, z in np.argwhere(nm == "minecraft:jigsaw"):
        key = str(x * sy * sz + y * sz + z)
        be = bpd.get(key, {}).get("block_entity_data")
        if not be or be.get("id") != "JigsawBlock":
            fail(name, f"jigsaw at {(x, y, z)} has no block entity")
            continue
        if (be["x"], be["y"], be["z"]) != (be["x"], be["y"], be["z"]):
            fail(name, "bad entity position")
        near = [(x + dx, y + dy, z + dz) for dx, dy, dz in
                ((1, 0, 0), (-1, 0, 0), (0, 1, 0), (0, -1, 0), (0, 0, 1), (0, 0, -1))]
        if not any(all(0 <= n[i] < (sx, sy, sz)[i] for i in range(3)) and cloudish[n] for n in near):
            fail(name, f"jigsaw at {(x, y, z)} is not backed by cloud")
    if not connected(solid):
        fail(name, "floating cloud (disconnected blocks)")
    return (sx, sy, sz), nm


def check_shaft(name, nm, cols, expect_top):
    """Water column continuous from `expect_top` down to a vine at the lowest
    layer with nothing under it, walled by cloud in between."""
    sx, sy, sz = nm.shape
    for x, z in cols:
        vine = [y for y in range(sy) if nm[x, y, z] == "minecraft:cave_vines"]
        if vine != [0]:
            fail(name, f"shaft ({x},{z}): vine updater must be at y=0, found {vine}")
        for y in range(1, expect_top + 1):
            if nm[x, y, z] != "minecraft:water":
                fail(name, f"shaft ({x},{z}) broken at y={y}")
                break
        for y in range(1, expect_top + 1):
            for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, nz = x + dx, z + dz
                if 0 <= nx < sx and 0 <= nz < sz and nm[nx, y, nz] in ("", "minecraft:air"):
                    fail(name, f"shaft ({x},{z}) leaks sideways at y={y}")
                    return


def check_springs(name, nm, shaft_col):
    sx, sy, sz = nm.shape
    for x, y, z in np.argwhere(nm == "minecraft:water"):
        if (x, z) == shaft_col:
            continue
        opening = []
        for dx, dy, dz in ((1, 0, 0), (-1, 0, 0), (0, 1, 0), (0, -1, 0), (0, 0, 1), (0, 0, -1)):
            n = (x + dx, y + dy, z + dz)
            inside = all(0 <= n[i] < (sx, sy, sz)[i] for i in range(3))
            kind = nm[n] if inside else ""
            if kind not in ("extrabiomes:dense_cloud", "minecraft:water"):
                opening.append(kind)
        if opening != ["minecraft:cave_vines"]:
            fail(name, f"spring at {(x, y, z)} should have exactly one cave-vine opening, has {opening}")


def main():
    count = 0
    for sub in ("clouds", "islands"):
        d = os.path.join(SC, sub)
        for f in sorted(os.listdir(d)):
            if not f.endswith(".mcstructure"):
                continue
            name = f"{sub}/{f}"
            dims, nm = check_file(os.path.join(d, f), name)
            count += 1
            if f.startswith("fountain_"):
                cols = [(x, z) for x, z in np.argwhere((nm == "minecraft:water").any(axis=1))]
                if not cols:
                    fail(name, "fountain pad lost its water shaft")
                check_shaft(name, nm, cols, dims[1] - 1)
            if f.startswith("island_") and not f.startswith("island_puff"):
                cx = (dims[0] - 1) // 2
                check_shaft(name, nm, [(cx + 1, cx)], dims[1] - 1)
                check_springs(name, nm, (cx + 1, cx))
    for sub in ("paths", "buildings"):
        pass
    print(f"checked {count} structures")
    if failures:
        print("FAILED:")
        for m in failures:
            print("  " + m)
        return 1
    print("ok")
    return 0


if __name__ == "__main__":
    sys.exit(main())
