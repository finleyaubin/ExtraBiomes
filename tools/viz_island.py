import sys
from mcstructure import load, to_py

name, root = load(sys.argv[1])
r = to_py(root)
sx, sy, sz = r["size"]
pal = r["structure"]["palette"]["default"]["block_palette"]
idx = r["structure"]["block_indices"][0]

layers = [sy - 1, sy - 3, sy - 5, sy - 8, sy - 11, 1, 0]
for y in layers:
    filled = 0
    rows = []
    for z in range(sz):
        row = ""
        for x in range(sx):
            p = idx[x * sy * sz + y * sz + z]
            if p == -1:
                ch = "."
            elif pal[p]["name"] == "minecraft:jigsaw":
                ch = "J"
                filled += 1
            else:
                ch = "#"
                filled += 1
            row += ch
        rows.append(row)
    print(f"layer y={y} (top={sy-1}), {filled} blocks:")
    for row in rows:
        print("  " + row)

# vertical slice through the centre
print("\nvertical slice (x = centre), y top->bottom:")
cx = sx // 2
for y in range(sy - 1, -1, -1):
    row = ""
    for z in range(sz):
        p = idx[cx * sy * sz + y * sz + z]
        row += "." if p == -1 else "#"
    print("  " + row)
