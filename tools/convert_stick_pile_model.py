"""
Converts the Bedrock stick_pile geometry (packs/RP/models/blocks/stick_pile.geo.json) into
three baked Java block models, one per Direction.Axis (X/Y/Z), for the new
extrabiomes:stick_pile block (a RotatedPillarBlock).

The Bedrock geometry has no bone rotations (only pivots), so it is really just a flat list
of ~70 axis-aligned cuboids. The Bedrock block's permutations rotate the whole geometry
depending on the placed `extrabiomes:facing`/block_face, always in 90-degree steps, so a
plain per-axis rotation of the raw cuboid list reproduces the same three distinct shapes
(one per axis bucket: north/south, east/west, up/down) that Bedrock produces.

Face textures are simplified rather than porting the original per-face UV rects: bones
named "core*" (the log end/rings faces in the source model) get the oak_log_top texture,
all other ("stick*") cuboids get the oak_log side texture, with UV auto-derived by the
Minecraft model loader (uv omitted). This mirrors the level of fidelity already used for
PebbleBlock/MossyPebbleBlock rather than a pixel-perfect port of the Bedrock UV atlas.

Run: python tools/convert_stick_pile_model.py
"""
import json
import os

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
GEO_PATH = os.path.join(REPO, "ExtraBiomes - Bedrock", "packs", "RP", "models", "blocks", "stick_pile.geo.json")
MODEL_DIR = os.path.join(REPO, "ExtraBiomes - Java", "common", "src", "main", "resources", "assets", "extrabiomes", "models", "block")


def load_cubes():
    data = json.load(open(GEO_PATH, encoding="utf-8"))
    bones = data["minecraft:geometry"][0]["bones"]
    cubes = []
    for bone in bones:
        is_core = bone["name"].startswith("core")
        for cube in bone.get("cubes", []):
            ox, oy, oz = cube["origin"]
            sx, sy, sz = cube["size"]
            cubes.append({
                "from": [ox, oy, oz],
                "to": [ox + sx, oy + sy, oz + sz],
                "core": is_core,
            })
    return cubes


def to_java_space(cube):
    # Bedrock model origin is centered on X/Z (spans roughly -8..8); Java element
    # coordinates are 0..16 (and beyond, for overflowing geometry), so shift X/Z by +8.
    (x0, y0, z0) = cube["from"]
    (x1, y1, z1) = cube["to"]
    return (x0 + 8, y0, z0 + 8), (x1 + 8, y1, z1 + 8)


def rotate_x90(p, center=(8, 8, 8)):
    # +90 degrees about the X axis, around the given center: (x, y, z) -> (x, 16-z, y)
    cx, cy, cz = center
    x, y, z = p
    return (x, cy - (z - cz), cz + (y - cy))


def rotate_z90(p, center=(8, 8, 8)):
    # +90 degrees about the Z axis, around the given center: (x, y, z) -> (16-y, x, z)
    cx, cy, cz = center
    x, y, z = p
    return (cx - (y - cy), cy + (x - cx), z)


def rotate_y90(p, center=(8, 8, 8)):
    # +90 degrees about the Y axis, around the given center: (x, y, z) -> (x, y, 16-x) (mirrored)
    cx, cy, cz = center
    x, y, z = p
    return (cx + (z - cz), y, cz - (x - cx))


def aabb_from_corners(p0, p1):
    xs = sorted([p0[0], p1[0]])
    ys = sorted([p0[1], p1[1]])
    zs = sorted([p0[2], p1[2]])
    return [xs[0], ys[0], zs[0]], [xs[1], ys[1], zs[1]]


def build_elements(cubes, rotate):
    elements = []
    for cube in cubes:
        p0, p1 = to_java_space(cube)
        if rotate is not None:
            p0 = rotate(p0)
            p1 = rotate(p1)
        frm, to = aabb_from_corners(p0, p1)
        texture = "#core" if cube["core"] else "#stick"
        faces = {face: {"texture": texture} for face in ("north", "south", "east", "west", "up", "down")}
        elements.append({"from": frm, "to": to, "faces": faces})
    return elements


def write_model(path, elements):
    model = {
        # Without a parent, this custom-elements model has no gui/firstperson/thirdperson display
        # transforms at all, so the item icon (stick_pile_y, used directly as the block item's
        # model - see ModBlockStateProvider.stickPileBlock()) renders with identity transforms
        # instead of the standard scaled/rotated block-item framing. That's what made the item
        # icon look broken after the axis-rotation fix: the geometry itself changed shape/
        # orientation, and with no display block to normalize it for GUI/inventory rendering, the
        # new orientation no longer happens to look reasonable by chance the way the old (wrong)
        # one incidentally did. "minecraft:block/block" supplies the same default display
        # transforms every vanilla block model gets; it doesn't affect in-world rendering (only
        # item/gui contexts use "display"), so it's safe to add to all three axis variants.
        "parent": "minecraft:block/block",
        "ambientocclusion": False,
        "textures": {
            "stick": "minecraft:block/oak_log",
            "core": "minecraft:block/oak_log_top",
            "particle": "minecraft:block/oak_log",
        },
        "elements": elements,
    }
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(model, f, indent=2)
        f.write("\n")


def main():
    cubes = load_cubes()

    # Bucket <-> rotation mapping taken directly from packs/BP/blocks/stick_pile.json's three
    # permutations, keyed by q.block_state('minecraft:block_face') - NOT guessed: west/east ->
    # rotation [90,0,0] (X), up/down -> rotation [0,0,90] (Z), north/south -> rotation [0,90,0]
    # (Y). A prior version of this script paired each Java axis with the wrong Bedrock bucket
    # (cyclically off-by-one), which is why placed stick piles rotated incorrectly in-game.
    variants = {
        # axis=Y (placed on the up/down face of a block): Bedrock's up/down bucket, rotation
        # [0,0,90] about Z.
        "stick_pile_y": rotate_z90,
        # axis=X (placed on the west/east face of a block): Bedrock's west/east bucket, rotation
        # [90,0,0] about X.
        "stick_pile_x": rotate_x90,
        # axis=Z (placed on the north/south face of a block): Bedrock's north/south bucket,
        # rotation [0,90,0] about Y.
        "stick_pile_z": rotate_y90,
    }

    for name, rotate in variants.items():
        elements = build_elements(cubes, rotate)
        write_model(os.path.join(MODEL_DIR, name + ".json"), elements)
        print(f"wrote {name}.json ({len(elements)} elements)")

    # The item model (parented to block/stick_pile_y) is generated by datagen -
    # see ModBlockStateProvider.stickPileBlock() - not written statically here,
    # to avoid a duplicate-resource clash with the generated one at build time.


if __name__ == "__main__":
    main()
