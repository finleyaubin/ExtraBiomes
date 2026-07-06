import sys
from mcstructure import load, to_py

name, root = load(sys.argv[1])
r = to_py(root)
sx, sy, sz = r["size"]
pal = r["structure"]["palette"]["default"]["block_palette"]
idx = r["structure"]["block_indices"][0]
for y in range(sy - 1, -1, -1):
    print(f"layer y={y} (top={sy-1}):")
    for z in range(sz):
        row = ""
        for x in range(sx):
            p = idx[x * sy * sz + y * sz + z]
            if p == -1:
                ch = "."
            elif pal[p]["name"] == "minecraft:jigsaw":
                ch = "J"
            else:
                ch = "#"
            row += ch
        print("  " + row)
