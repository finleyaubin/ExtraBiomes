import sys
import math
from mcstructure import load, to_py


def classify(name):
    n = name.lower()
    if "_log" in n:
        return "log"
    if "_leaves" in n:
        return "leaves"
    if n == "minecraft:air" or n == "void":
        return "air"
    return "other"


def main(path):
    name, root = load(path)
    r = to_py(root)
    sx, sy, sz = r["size"]
    pal = r["structure"]["palette"]["default"]["block_palette"]
    idx = r["structure"]["block_indices"][0]

    kinds = [None] * len(pal)
    for i, p in enumerate(pal):
        kinds[i] = classify(p["name"])

    def kind_at(x, y, z):
        p = idx[x * sy * sz + y * sz + z]
        if p == -1:
            return "air"
        return kinds[p]

    # find trunk column (x,z with most logs stacked) to use as center
    log_xz_counts = {}
    for x in range(sx):
        for z in range(sz):
            c = sum(1 for y in range(sy) if kind_at(x, y, z) == "log")
            if c:
                log_xz_counts[(x, z)] = c
    if log_xz_counts:
        cx, cz = max(log_xz_counts, key=log_xz_counts.get)
    else:
        cx, cz = sx // 2, sz // 2

    print(f"=== {path} ===")
    print(f"size (x,y,z): [{sx}, {sy}, {sz}]  trunk column guess: ({cx},{cz})")

    print("\nper-Y profile (top->bottom): y | logs | leaves | leaf radius (max dist from trunk column)")
    for y in range(sy - 1, -1, -1):
        log_count = 0
        leaf_count = 0
        max_r = 0.0
        for x in range(sx):
            for z in range(sz):
                k = kind_at(x, y, z)
                if k == "log":
                    log_count += 1
                elif k == "leaves":
                    leaf_count += 1
                    r_ = math.hypot(x - cx, z - cz)
                    if r_ > max_r:
                        max_r = r_
        if log_count or leaf_count:
            print(f"  y={y:3d} | logs={log_count:3d} | leaves={leaf_count:3d} | radius={max_r:.1f}")

    print("\nvertical slice (through trunk column), L=log #=leaves v=other .=air, top->bottom:")
    for y in range(sy - 1, -1, -1):
        row = ""
        for z in range(sz):
            k = kind_at(cx, y, z)
            row += {"log": "L", "leaves": "#", "air": ".", "other": "v"}[k]
        print(f"  y={y:3d} " + row)


if __name__ == "__main__":
    for path in sys.argv[1:]:
        main(path)
        print()
