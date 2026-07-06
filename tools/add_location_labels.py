"""Stamp a movie-style location title onto each biome screenshot.

Walks an input tree like:
    Raw/Overworld Surface/Charred Forest.png
and writes labelled JPEGs to the output tree, preserving structure.
The filename becomes the title ("CHARRED FOREST") and the parent folder
the subtitle ("OVERWORLD SURFACE"), centered near the bottom of frame
in wide-tracked Century Gothic caps.

The title colour is derived per image: the complement (hue + 180deg) of
the image's average colour, lifted to a bright pastel so it pops without
going muddy.

Usage:
    python add_location_labels.py [input_root] [output_root]
Defaults:
    input_root  = Y:/Media/3.0.0 Images/Raw
    output_root = Y:/Media/3.0.0 Images/Output
"""

import colorsys
import sys
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont, ImageStat

DEFAULT_INPUT = Path(r"Y:\Media\3.0.0 Images\Raw")
DEFAULT_OUTPUT = Path(r"Y:\Media\3.0.0 Images\Output")

FONTS_DIR = Path(r"C:\Windows\Fonts")
TITLE_FONT = "GOTHICB.TTF"   # Century Gothic Bold
SUB_FONT = "GOTHIC.TTF"      # Century Gothic Regular

WHITE = (245, 245, 242, 255)
JPEG_QUALITY = 95


def complementary_pastel(img: Image.Image) -> tuple:
    """Complement of the image's average colour, as a bright pastel."""
    r, g, b = ImageStat.Stat(img.convert("RGB")).mean
    h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
    h = (h + 0.5) % 1.0
    s = min(max(s, 0.35), 0.6)   # enough chroma to read as a colour
    v = max(v, 0.9)              # always bright enough for a title
    cr, cg, cb = colorsys.hsv_to_rgb(h, s, v)
    return (int(cr * 255), int(cg * 255), int(cb * 255), 255)


def tracked(draw, pos, text, font, fill, tr):
    x, y = pos
    for ch in text:
        draw.text((x, y), ch, font=font, fill=fill)
        x += draw.textlength(ch, font=font) + tr


def tracked_w(draw, text, font, tr):
    return sum(draw.textlength(ch, font=font) for ch in text) + tr * (len(text) - 1)


def label_image(src: Path, dst: Path, title: str, subtitle: str):
    img = Image.open(src).convert("RGBA")
    w, h = img.size
    title_col = complementary_pastel(img)

    tf = ImageFont.truetype(str(FONTS_DIR / TITLE_FONT), w // 20)
    sf = ImageFont.truetype(str(FONTS_DIR / SUB_FONT), w // 66)
    d = ImageDraw.Draw(img)
    t_up, s_up = title.upper(), subtitle.upper()
    tr_t, tr_s = w // 130, w // 140
    tb = d.textbbox((0, 0), t_up, font=tf)
    sb = d.textbbox((0, 0), s_up, font=sf)
    t_h, s_h = tb[3] - tb[1], sb[3] - sb[1]
    gap = t_h // 2
    y0 = h - h // 9 - (t_h + gap + s_h)

    def render(dd, shadow_fill):
        col_t = shadow_fill or title_col
        col_s = shadow_fill or WHITE
        twid = tracked_w(dd, t_up, tf, tr_t)
        sw = tracked_w(dd, s_up, sf, tr_s)
        tracked(dd, ((w - twid) / 2, y0 - tb[1]), t_up, tf, col_t, tr_t)
        tracked(dd, ((w - sw) / 2, y0 + t_h + gap), s_up, sf, col_s, tr_s)

    # soft drop shadow, then crisp text on top
    off = max(2, w // 900)
    shadow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    render(ImageDraw.Draw(shadow), (0, 0, 0, 190))
    shadow = shadow.filter(ImageFilter.GaussianBlur(off))
    img.alpha_composite(shadow, (off, off))
    top = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    render(ImageDraw.Draw(top), None)
    img.alpha_composite(top)

    dst.parent.mkdir(parents=True, exist_ok=True)
    img.convert("RGB").save(dst, quality=JPEG_QUALITY)
    return title_col


def main():
    input_root = Path(sys.argv[1]) if len(sys.argv) > 1 else DEFAULT_INPUT
    output_root = Path(sys.argv[2]) if len(sys.argv) > 2 else DEFAULT_OUTPUT

    images = sorted(p for p in input_root.rglob("*") if p.suffix.lower() in (".png", ".jpg", ".jpeg"))
    if not images:
        sys.exit(f"No images found under {input_root}")

    for src in images:
        rel = src.relative_to(input_root)
        subtitle = " ".join(rel.parent.parts) if rel.parent.parts else "Overworld"
        dst = (output_root / rel).with_suffix(".jpeg")
        col = label_image(src, dst, src.stem, subtitle)
        print(f"labelled: {rel}  (title colour #{col[0]:02x}{col[1]:02x}{col[2]:02x})")

    print(f"\n{len(images)} images written to {output_root}")


if __name__ == "__main__":
    main()
