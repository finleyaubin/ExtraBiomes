"""Generate the Tallow Hollows 16x16 textures for the Bedrock resource pack.

Usage:
    python tools/make_tallow_hollows_textures.py

Deterministic: every pixel comes from a hash of its coordinates, so re-running rewrites identical files.
The palette is shared with the Java edition's copy of this script; keep the two in step.
"""
import os

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
RP_TEXTURES = os.path.join(HERE, "..", "ExtraBiomes - Bedrock", "packs", "RP", "textures")
BLOCK_DIR = os.path.join(RP_TEXTURES, "blocks", "tallow_hollows")
ITEM_DIR = os.path.join(RP_TEXTURES, "items")
SIZE = 16

TALLOW_BASE = (0xE8, 0xDC, 0xB8)
TALLOW_SHADE = (0xCD, 0xBE, 0x94)
TALLOW_HIGHLIGHT = (0xF6, 0xEE, 0xD6)
TALLOW_CRACK = (0xA8, 0x96, 0x6A)
WICK = (0x3B, 0x2A, 0x1A)
FLAME_CORE = (0xFF, 0xF3, 0xB0)
FLAME_MID = (0xFF, 0xB5, 0x2E)
FLAME_EDGE = (0xE0, 0x66, 0x1A)
LUMP = (0xEA, 0xD9, 0xA0)
LUMP_SHADE = (0xC9, 0xB3, 0x6E)
CLEAR = (0, 0, 0, 0)


def hash01(seed, x, y):
    n = (seed * 374761393 + x * 668265263 + y * 2246822519) & 0xFFFFFFFF
    n = ((n ^ (n >> 13)) * 1274126177) & 0xFFFFFFFF
    return ((n ^ (n >> 16)) & 0xFFFF) / 0xFFFF


def opaque(rgb):
    return rgb + (255,)


def mottled(seed, x, y):
    roll = hash01(seed, x, y)
    if roll < 0.18:
        return TALLOW_SHADE
    if roll > 0.9:
        return TALLOW_HIGHLIGHT
    return TALLOW_BASE


def tallowstone():
    img = Image.new("RGBA", (SIZE, SIZE))
    for y in range(SIZE):
        for x in range(SIZE):
            img.putpixel((x, y), opaque(mottled(1, x, y)))
    # Drip streaks: a lighter run down from the top edge, ending in a one-pixel bead.
    for column, length in ((3, 6), (9, 4), (13, 8)):
        for y in range(length):
            img.putpixel((column, y), opaque(TALLOW_HIGHLIGHT))
        img.putpixel((column, length), opaque(TALLOW_SHADE))
    for x, y in ((6, 11), (7, 12), (11, 13)):
        img.putpixel((x, y), opaque(TALLOW_CRACK))
    return img


def tallowstone_bricks():
    img = Image.new("RGBA", (SIZE, SIZE))
    for y in range(SIZE):
        for x in range(SIZE):
            img.putpixel((x, y), opaque(mottled(2, x, y)))
    brick_height, brick_width = 4, 8
    for y in range(SIZE):
        row = y // brick_height
        offset = 0 if row % 2 == 0 else brick_width // 2
        for x in range(SIZE):
            local_x = (x + offset) % brick_width
            local_y = y % brick_height
            if local_y == brick_height - 1 or local_x == brick_width - 1:
                img.putpixel((x, y), opaque(TALLOW_CRACK))
            elif local_y == 0 and local_x < brick_width - 2:
                img.putpixel((x, y), opaque(TALLOW_HIGHLIGHT))
    # Melted corners: soften where a mortar line meets a brick corner.
    for y in range(SIZE):
        row = y // brick_height
        offset = 0 if row % 2 == 0 else brick_width // 2
        for x in range(SIZE):
            local_x = (x + offset) % brick_width
            local_y = y % brick_height
            corner = local_y in (0, brick_height - 2) and local_x in (0, brick_width - 2)
            if corner:
                img.putpixel((x, y), opaque(TALLOW_SHADE))
    return img


def wax_drip():
    img = Image.new("RGBA", (SIZE, SIZE), CLEAR)
    for center, length in ((4, 12), (8, 6), (12, 9)):
        for y in range(length):
            half_width = 1 if y < length * 0.6 else 0
            for x in range(center - half_width, center + half_width + 1):
                colour = TALLOW_SHADE if x == center + half_width and half_width else TALLOW_BASE
                img.putpixel((x, y), opaque(colour))
        img.putpixel((center, length - 1), opaque(TALLOW_HIGHLIGHT))
        img.putpixel((center, length), opaque(TALLOW_SHADE))
    return img


def ever_wick():
    img = Image.new("RGBA", (SIZE, SIZE), CLEAR)
    flame_rows = [
        (4, (FLAME_EDGE,)),
        (5, (FLAME_EDGE, FLAME_MID, FLAME_EDGE)),
        (6, (FLAME_EDGE, FLAME_MID, FLAME_MID, FLAME_EDGE)),
        (7, (FLAME_MID, FLAME_CORE, FLAME_CORE, FLAME_MID)),
        (8, (FLAME_MID, FLAME_CORE, FLAME_CORE, FLAME_MID)),
        (9, (FLAME_EDGE, FLAME_CORE, FLAME_EDGE)),
    ]
    for y, colours in flame_rows:
        start = 8 - len(colours) // 2
        for i, colour in enumerate(colours):
            img.putpixel((start + i, y), opaque(colour))
    for y in range(10, 16):
        img.putpixel((7, y), opaque(WICK))
        img.putpixel((8, y), opaque(WICK))
    img.putpixel((8, 10), opaque(FLAME_EDGE))
    return img


def tallow_lump():
    img = Image.new("RGBA", (SIZE, SIZE), CLEAR)
    cx, cy, rx, ry = 7.5, 9.0, 5.5, 4.5
    for y in range(SIZE):
        for x in range(SIZE):
            d = ((x - cx) / rx) ** 2 + ((y - cy) / ry) ** 2
            if d > 1:
                continue
            lower_right = (x - cx) + (y - cy) > 2.5
            colour = LUMP_SHADE if d > 0.7 or lower_right else LUMP
            if d < 0.25 and x < cx and y < cy:
                colour = TALLOW_HIGHLIGHT
            img.putpixel((x, y), opaque(colour))
    for x, y in ((6, 5), (7, 4), (8, 5)):
        img.putpixel((x, y), opaque(LUMP))
    return img


OUTPUTS = {
    os.path.join(BLOCK_DIR, "tallowstone.png"): tallowstone,
    os.path.join(BLOCK_DIR, "tallowstone_bricks.png"): tallowstone_bricks,
    os.path.join(BLOCK_DIR, "wax_drip.png"): wax_drip,
    os.path.join(BLOCK_DIR, "ever_wick.png"): ever_wick,
    os.path.join(ITEM_DIR, "tallow_lump.png"): tallow_lump,
}


if __name__ == "__main__":
    os.makedirs(BLOCK_DIR, exist_ok=True)
    for path, draw in OUTPUTS.items():
        draw().save(path)
        print(f"wrote {os.path.relpath(path, HERE)}")
