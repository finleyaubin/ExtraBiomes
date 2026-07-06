import sys
from mcstructure import load, to_py

BASE = "../ExtraBiomes - Bedrock/packs/BP/structures/extrabiomes/sky_city"
for piece in ["paths/fountain", "paths/T", "paths/path_end"]:
    name, root = load(f"{BASE}/{piece}.mcstructure")
    r = to_py(root)
    sx, sy, sz = r["size"]
    pal = r["structure"]["palette"]["default"]["block_palette"]
    idx = r["structure"]["block_indices"][0]
    print(f"=== {piece} size {r['size']}")
    for y in range(min(sy, 3)):
        print(f" layer y={y}:")
        for z in range(sz):
            row = ""
            for x in range(sx):
                p = idx[x * sy * sz + y * sz + z]
                if p == -1:
                    ch = "."
                else:
                    n = pal[p]["name"]
                    ch = " " if n == "minecraft:air" else ("J" if n == "minecraft:jigsaw" else "#")
                row += ch
            print("   " + row)
