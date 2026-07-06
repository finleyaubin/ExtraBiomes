"""Dump size, palette and jigsaw block data from a .mcstructure file."""
import sys, json
from mcstructure import load, to_py

path = sys.argv[1]
name, root = load(path)
r = to_py(root)
size = r["size"]
print("file:", path)
print("format_version:", r.get("format_version"))
print("size (x,y,z):", size)
print("structure_world_origin:", r.get("structure_world_origin"))

palette = r["structure"]["palette"]["default"]["block_palette"]
print("\npalette:")
for i, entry in enumerate(palette):
    print(f"  [{i}] {entry['name']} states={json.dumps(entry.get('states', {}))}")

indices = r["structure"]["block_indices"][0]
sx, sy, sz = size

def coord(flat):
    x = flat // (sy * sz)
    y = (flat // sz) % sy
    z = flat % sz
    return x, y, z

# count blocks per palette index, find jigsaw positions
from collections import Counter
c = Counter(indices)
print("\nblock counts:", {palette[k]["name"] if k >= 0 else "VOID(-1)": v for k, v in c.items()})

jigsaw_idx = [i for i, p in enumerate(palette) if p["name"] == "minecraft:jigsaw"]
if jigsaw_idx:
    print("\njigsaw block positions:")
    for flat, pidx in enumerate(indices):
        if pidx in jigsaw_idx:
            print(f"  flat={flat} xyz={coord(flat)} states={json.dumps(palette[pidx].get('states', {}))}")

bpd = r["structure"]["palette"]["default"].get("block_position_data", {})
print("\nblock_position_data:")
for k, v in bpd.items():
    print(f"  key={k} xyz={coord(int(k))}")
    print("   ", json.dumps(v))
