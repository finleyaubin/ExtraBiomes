"""Draw the Tallow Hollows block and item textures (16x16 pixel art).

Deterministic: every random choice comes from a fixed seed, so re-running the
script reproduces the committed PNGs byte for byte. The palette matches the
Tallow Hollows design brief so the Java and Bedrock editions share colours.

Usage: python tools/build_tallow_textures.py
"""
import random
from pathlib import Path

from PIL import Image

SIZE = 16
SEED = 20261111

TALLOW_BASE = (0xE8, 0xDC, 0xB8, 255)
TALLOW_SHADE = (0xCD, 0xBE, 0x94, 255)
TALLOW_HIGHLIGHT = (0xF6, 0xEE, 0xD6, 255)
TALLOW_CRACK = (0xA8, 0x96, 0x6A, 255)
WICK = (0x3B, 0x2A, 0x1A, 255)
FLAME_CORE = (0xFF, 0xF3, 0xB0, 255)
FLAME_MID = (0xFF, 0xB5, 0x2E, 255)
FLAME_EDGE = (0xE0, 0x66, 0x1A, 255)
LUMP_BASE = (0xEA, 0xD9, 0xA0, 255)
LUMP_SHADE = (0xC9, 0xB3, 0x6E, 255)
CLEAR = (0, 0, 0, 0)

JAVA_ROOT = Path(__file__).resolve().parent.parent / "ExtraBiomes - Java" / "common" / "src" / "main" / "resources" / "assets" / "extrabiomes" / "textures"
BLOCK_DIR = JAVA_ROOT / "block" / "tallow"
ITEM_DIR = JAVA_ROOT / "item"


def blank(fill=CLEAR):
    return Image.new("RGBA", (SIZE, SIZE), fill)


def smooth_noise(rng, cell):
    """Value noise on a coarse grid, bilinearly blended and tiled so block faces repeat seamlessly."""
    cells = SIZE // cell
    grid = [[rng.random() for _ in range(cells)] for _ in range(cells)]
    values = [[0.0] * SIZE for _ in range(SIZE)]
    for y in range(SIZE):
        for x in range(SIZE):
            gx, gy = x / cell, y / cell
            x0, y0 = int(gx) % cells, int(gy) % cells
            x1, y1 = (x0 + 1) % cells, (y0 + 1) % cells
            fx, fy = gx - int(gx), gy - int(gy)
            top = grid[y0][x0] * (1 - fx) + grid[y0][x1] * fx
            bottom = grid[y1][x0] * (1 - fx) + grid[y1][x1] * fx
            values[y][x] = top * (1 - fy) + bottom * fy
    return values


def tallowstone(rng):
    image = blank(TALLOW_BASE)
    pixels = image.load()
    noise = smooth_noise(rng, 4)
    for y in range(SIZE):
        for x in range(SIZE):
            value = noise[y][x] + rng.uniform(-0.12, 0.12)
            if value < 0.3:
                pixels[x, y] = TALLOW_SHADE
            elif value > 0.78:
                pixels[x, y] = TALLOW_HIGHLIGHT

    # Lighter wax that ran down the face and set: a thin streak with a rounded bead at its foot.
    for _ in range(3):
        x = rng.randrange(1, SIZE - 1)
        top = rng.randrange(0, SIZE)
        length = rng.randrange(4, 8)
        for step in range(length):
            pixels[x, (top + step) % SIZE] = TALLOW_HIGHLIGHT
        foot = (top + length) % SIZE
        pixels[x, foot] = TALLOW_HIGHLIGHT
        pixels[x + 1, foot] = TALLOW_HIGHLIGHT
        pixels[x + 1, (foot + 1) % SIZE] = TALLOW_SHADE

    for _ in range(4):
        x, y = rng.randrange(SIZE), rng.randrange(SIZE)
        pixels[x, y] = TALLOW_CRACK
        dx = rng.choice((-1, 1))
        pixels[(x + dx) % SIZE, (y + 1) % SIZE] = TALLOW_CRACK
    return image


def tallowstone_bricks(rng):
    """Bricks twice as wide as tall, rows offset by half a brick, with corners rounded off like softened wax."""
    brick_width, brick_height = 8, 4
    image = blank(TALLOW_CRACK)
    pixels = image.load()
    for row in range(SIZE // brick_height):
        offset = (brick_width // 2) * (row % 2)
        top = row * brick_height
        for left in range(-brick_width, SIZE, brick_width):
            x0 = left + offset
            for y in range(top, top + brick_height - 1):
                for x in range(x0, x0 + brick_width - 1):
                    if 0 <= x < SIZE:
                        pixels[x, y] = brick_pixel(rng, x - x0, y - top, brick_width - 1, brick_height - 1)
    return image


def brick_pixel(rng, x, y, width, height):
    if (x in (0, width - 1)) and (y in (0, height - 1)):
        return TALLOW_SHADE
    if y == 0:
        return TALLOW_HIGHLIGHT
    if y == height - 1 or x == width - 1:
        return TALLOW_SHADE
    return TALLOW_BASE if rng.random() > 0.15 else TALLOW_HIGHLIGHT


def wax_drip(rng):
    image = blank()
    pixels = image.load()
    for x in range(3, 13):
        pixels[x, 0] = TALLOW_SHADE
        pixels[x, 1] = TALLOW_BASE
    drips = [(4, rng.randrange(9, 12)), (8, rng.randrange(12, 15)), (11, rng.randrange(6, 9))]
    for left, length in drips:
        for y in range(1, length):
            # Three wide near the ceiling, two in the middle, one at the tip.
            width = 3 if y < length // 3 else 2 if y < 2 * length // 3 else 1
            for dx in range(width):
                colour = TALLOW_HIGHLIGHT if dx == 0 else TALLOW_SHADE if dx == width - 1 and width > 1 else TALLOW_BASE
                pixels[left + dx, y] = colour
        pixels[left, length] = TALLOW_SHADE
    return image


def wick_stub(pixels):
    for y in range(10, 15):
        pixels[7, y] = WICK
        pixels[8, y] = WICK
    for x in range(6, 10):
        pixels[x, 15] = TALLOW_SHADE


def ever_wick(_rng):
    image = blank()
    pixels = image.load()
    wick_stub(pixels)
    # A teardrop flame six pixels tall resting on the wick tip.
    flame = [
        "...E....",
        "..EME...",
        ".EMME...",
        ".EMCME..",
        ".EMCCE..",
        "..EMME..",
    ]
    colours = {"E": FLAME_EDGE, "M": FLAME_MID, "C": FLAME_CORE}
    for row, line in enumerate(flame):
        for column, key in enumerate(line):
            if key in colours:
                pixels[4 + column, 4 + row] = colours[key]
    return image


def ever_wick_unlit(_rng):
    image = blank()
    pixels = image.load()
    wick_stub(pixels)
    pixels[7, 9] = WICK
    pixels[8, 9] = FLAME_EDGE
    return image


def tallow_lump(rng):
    image = blank()
    pixels = image.load()
    centre_x, centre_y, radius_x, radius_y = 7.5, 8.5, 5.6, 4.6
    for y in range(SIZE):
        for x in range(SIZE):
            distance = ((x - centre_x) / radius_x) ** 2 + ((y - centre_y) / radius_y) ** 2
            if distance > 1.0:
                continue
            lower_right = (x - centre_x) + (y - centre_y) > 2.0
            if distance > 0.72:
                pixels[x, y] = LUMP_SHADE
            elif lower_right:
                pixels[x, y] = LUMP_SHADE if rng.random() > 0.3 else LUMP_BASE
            else:
                pixels[x, y] = LUMP_BASE
    for x, y in ((5, 6), (6, 6), (5, 7), (8, 5)):
        pixels[x, y] = TALLOW_HIGHLIGHT
    for x in range(4, 12):
        if pixels[x, 13][3] and not pixels[x, 14][3]:
            pixels[x, 13] = TALLOW_CRACK
    return image


TEXTURES = [
    (BLOCK_DIR / "tallowstone.png", tallowstone),
    (BLOCK_DIR / "tallowstone_bricks.png", tallowstone_bricks),
    (BLOCK_DIR / "wax_drip.png", wax_drip),
    (BLOCK_DIR / "ever_wick.png", ever_wick),
    (BLOCK_DIR / "ever_wick_unlit.png", ever_wick_unlit),
    (ITEM_DIR / "tallow_lump.png", tallow_lump),
]


def main():
    BLOCK_DIR.mkdir(parents=True, exist_ok=True)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    for index, (path, draw) in enumerate(TEXTURES):
        draw(random.Random(SEED + index)).save(path)
        print(f"wrote {path}")


if __name__ == "__main__":
    main()
