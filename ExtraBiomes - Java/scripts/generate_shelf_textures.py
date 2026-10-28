"""Recolours the vanilla oak shelf texture to each wood set's stripped-log palette.

Colours the oak shelf shares with the oak stripped log are mapped through that log:
exactly (pixel for pixel) when the wood's stripped log is a straight recolour of oak's,
otherwise by luminance rank. Shelf-only colours are darker than anything in the log, so
they keep their ratio to the log's median colour.
"""
import struct
import sys
import zlib
from pathlib import Path

WOOD_DIR = Path(__file__).resolve().parent.parent / "common/src/main/resources/assets/extrabiomes/textures/block/wood"
# Gilded sky borrows sky's palette; its gold is painted on by hand afterwards.
PALETTE_SOURCE = {"larch": "larch", "mystic": "mystic", "sky": "sky", "palm": "palm", "gilded_sky": "sky"}


def luminance(rgb):
    return 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]


def read_png(path):
    data = path.read_bytes()
    pos, idat, palette, trns = 8, b"", None, None
    while pos < len(data):
        length, kind = struct.unpack(">I4s", data[pos:pos + 8])
        body = data[pos + 8:pos + 8 + length]
        pos += 12 + length
        if kind == b"IHDR":
            width, height, depth, color_type, _, _, interlace = struct.unpack(">IIBBBBB", body[:13])
        elif kind == b"PLTE":
            palette = [tuple(body[i:i + 3]) for i in range(0, length, 3)]
        elif kind == b"tRNS":
            trns = body
        elif kind == b"IDAT":
            idat += body
    assert interlace == 0, f"{path} is interlaced"
    channels = {0: 1, 2: 3, 3: 1, 4: 2, 6: 4}[color_type]
    stride = (width * channels * depth + 7) // 8
    bpp = max(1, channels * depth // 8)
    raw = zlib.decompress(idat)
    rows, prev = [], bytearray(stride)
    for y in range(height):
        filter_type = raw[y * (stride + 1)]
        line = bytearray(raw[y * (stride + 1) + 1:(y + 1) * (stride + 1)])
        for x in range(stride):
            a = line[x - bpp] if x >= bpp else 0
            b = prev[x]
            c = prev[x - bpp] if x >= bpp else 0
            if filter_type == 1:
                line[x] = (line[x] + a) & 255
            elif filter_type == 2:
                line[x] = (line[x] + b) & 255
            elif filter_type == 3:
                line[x] = (line[x] + (a + b) // 2) & 255
            elif filter_type == 4:
                pa, pb, pc = abs(b - c), abs(a - c), abs(a + b - 2 * c)
                line[x] = (line[x] + (a if pa <= pb and pa <= pc else b if pb <= pc else c)) & 255
        rows.append(line)
        prev = line
    pixels = []
    for row in rows:
        for x in range(width):
            if color_type == 3:
                per_byte = 8 // depth
                index = row[x] if depth == 8 else (row[x // per_byte] >> (8 - depth * (x % per_byte + 1))) & ((1 << depth) - 1)
                alpha = trns[index] if trns and index < len(trns) else 255
                pixels.append(palette[index] + (alpha,))
            elif color_type == 6:
                pixels.append(tuple(row[x * 4:x * 4 + 4]))
            elif color_type == 2:
                pixels.append(tuple(row[x * 3:x * 3 + 3]) + (255,))
            else:
                raise ValueError(f"{path}: unsupported color type {color_type}")
    return width, height, pixels


def write_png(path, width, height, pixels):
    def chunk(kind, body):
        return struct.pack(">I", len(body)) + kind + body + struct.pack(">I", zlib.crc32(kind + body) & 0xFFFFFFFF)

    raw = b"".join(b"\0" + bytes(v for p in pixels[y * width:(y + 1) * width] for v in p) for y in range(height))
    path.write_bytes(b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
                     + chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b""))


def exact_mapping(oak_log, wood_log):
    mapping = {}
    for source, target in zip(oak_log, wood_log):
        if mapping.setdefault(source, target) != target:
            return None
    return mapping


def luminance_rank_mapping(oak_log, wood_log):
    oak_sorted = sorted(oak_log, key=luminance)
    wood_sorted = sorted(wood_log, key=luminance)
    ranks = {}
    for rank, colour in enumerate(oak_sorted):
        ranks.setdefault(colour, []).append(rank)
    return {colour: wood_sorted[ranks_of[len(ranks_of) // 2]] for colour, ranks_of in ranks.items()}


def median_colour(pixels):
    return sorted(pixels, key=luminance)[len(pixels) // 2]


def build_colour_mapping(oak_log, wood_log):
    mapping = exact_mapping(oak_log, wood_log)
    if mapping is None:
        mapping = luminance_rank_mapping(oak_log, wood_log)
    return mapping


def scale_like_oak(colour, oak_median, wood_median):
    return tuple(min(255, round(wood_median[i] * colour[i] / oak_median[i])) for i in range(3)) + (colour[3],)


def recolour_shelf(oak_shelf, oak_log, wood_log):
    mapping = build_colour_mapping(oak_log, wood_log)
    oak_median = median_colour(oak_log)
    wood_median = median_colour(wood_log)
    log_floor = min(luminance(c) for c in oak_log)
    cache = {}
    for colour in set(oak_shelf):
        if colour in mapping:
            cache[colour] = mapping[colour]
        elif luminance(colour) < log_floor:
            cache[colour] = scale_like_oak(colour, oak_median, wood_median)
        else:
            nearest = min(mapping, key=lambda source: abs(luminance(source) - luminance(colour)))
            cache[colour] = mapping[nearest]
    return [cache[p] for p in oak_shelf]


def main():
    ref = WOOD_DIR / "ref"
    shelf_w, shelf_h, oak_shelf = read_png(ref / "oak_shelf.png")
    _, _, oak_log = read_png(ref / "stripped_oak_log.png")
    for wood, palette_wood in PALETTE_SOURCE.items():
        _, _, wood_log = read_png(WOOD_DIR / palette_wood / f"stripped_{palette_wood}_log.png")
        write_png(WOOD_DIR / wood / f"{wood}_shelf.png", shelf_w, shelf_h, recolour_shelf(oak_shelf, oak_log, wood_log))
        print(f"wrote {wood}_shelf.png")


if __name__ == "__main__":
    sys.exit(main())
