"""Render a release changelog into Instagram story slides (1080x1920 JPEGs)."""

import argparse
import io
import re
import sys
import urllib.request
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

W, H = 1080, 1920
MARGIN = 96
# Instagram draws its progress bar and profile row over roughly the top 250px of a story, so
# the heading starts just under that rather than at the top of the canvas.
BODY_TOP, BODY_BOTTOM = 380, H - 240

# Sampled from the biome screenshots in README.md - glacier bone and mesa rust over deep
# forest ink, sky-blue accent (scratchpad/palette.py did the extraction).
THEMES = {
    "glacier": {"ink": "#dfe7db", "muted": "#7f9da7", "accent": "#93bfd6",
                "top": (17, 39, 30), "bottom": (9, 5, 17)},
    "bryce": {"ink": "#11271e", "muted": "#6c7b83", "accent": "#7f3c31",
              "top": (223, 231, 219), "bottom": (199, 187, 171)},
}
THEME = THEMES["glacier"]
EDITION = "Java"

BAND_H = round((W - 2 * MARGIN) * 9 / 16)  # changelog screenshots are always 16:9
LOGO = Path(__file__).parent / "title.png"
LOGO_W = W - 2 * MARGIN  # spans the text column exactly, so the wordmark shares its left edge
IMAGE_CACHE = {}

FONT_DIRS = ["/usr/share/fonts/truetype/dejavu", "/usr/share/fonts/TTF",
             "/usr/share/fonts/liberation", "/usr/share/fonts/truetype/liberation",
             "/System/Library/Fonts/Supplemental"]
FONT_FILES = {"bold": ["DejaVuSans-Bold.ttf", "LiberationSans-Bold.ttf", "Arial Bold.ttf"],
              "regular": ["DejaVuSans.ttf", "LiberationSans-Regular.ttf", "Arial.ttf"]}


def font(weight, size):
    for directory in FONT_DIRS:
        for name in FONT_FILES[weight]:
            path = Path(directory, name)
            if path.exists():
                return ImageFont.truetype(str(path), size)
    return ImageFont.load_default(size)


IMAGE_SRC = re.compile(r'<img[^>]+src="([^"]+)"|!\[[^\]]*\]\(([^)\s]+)')


def images_in(line):
    return [tag.group(1) or tag.group(2) for tag in IMAGE_SRC.finditer(line)]


def fetch(url):
    """A screenshot that won't download must not fail a release - the slide just loses it."""
    if url not in IMAGE_CACHE:
        request = urllib.request.Request(url, headers={"User-Agent": "extrabiomes-slides"})
        try:
            with urllib.request.urlopen(request, timeout=30) as response:
                IMAGE_CACHE[url] = Image.open(io.BytesIO(response.read())).convert("RGB")
        except Exception as error:
            print(f"skipping image {url}: {error}", file=sys.stderr)
            IMAGE_CACHE[url] = None
    return IMAGE_CACHE[url]


def paste_image(slide, photo, y, radius=40):
    width = W - 2 * MARGIN
    mask = Image.new("L", (width, BAND_H), 0)
    ImageDraw.Draw(mask).rounded_rectangle([0, 0, width - 1, BAND_H - 1], radius, fill=255)
    slide.paste(photo.resize((width, BAND_H), Image.LANCZOS), (MARGIN, y), mask)


def clean(text):
    text = re.sub(r"<[^>]+>", "", text)
    text = re.sub(r"!?\[([^\]]*)\]\([^)]*\)", r"\1", text)
    return re.sub(r"[*`_]", "", text).strip()


def parse(markdown):
    """-> [(section heading, [("sub"|"bullet"|"image", text)])] - the "# " title is dropped as it only
    restates the version the slide chrome already shows."""
    sections = []
    for raw in markdown.splitlines():
        line = raw.strip()
        if sections:
            sections[-1][1].extend(("image", url) for url in images_in(line))
        if line.startswith("## "):
            sections.append((clean(line[3:]), []))
        elif line.startswith("### ") and sections:
            sections[-1][1].append(("sub", clean(line[4:])))
        elif line.startswith(("- ", "* ")) and sections:
            body = clean(line[2:])
            if body:
                sections[-1][1].append(("bullet", body))
    return [s for s in sections if s[1]]


def wrap(draw, text, fnt, width):
    lines, current = [], ""
    for word in text.split():
        candidate = f"{current} {word}".strip()
        if draw.textlength(candidate, font=fnt) <= width or not current:
            current = candidate
        else:
            lines.append(current)
            current = word
    return lines + [current] if current else lines


def background():
    image = Image.new("RGB", (W, H), THEME["top"])
    draw = ImageDraw.Draw(image)
    for y in range(H):
        blend = y / H
        draw.line([(0, y), (W, y)], fill=tuple(
            round(top + (bottom - top) * blend) for top, bottom in zip(THEME["top"], THEME["bottom"])))
    return image


def chrome(image, version, footer):
    """The section name is the headline on its own slide, so brand, version and edition sit
    quietly in the footer instead of competing with it from the top of every slide."""
    draw = ImageDraw.Draw(image)
    draw.text((MARGIN, H - 190), f"ExtraBiomes {version} · {EDITION} Edition",
              font=font("bold", 32), fill=THEME["accent"])
    draw.text((MARGIN, H - 144), footer, font=font("regular", 32), fill=THEME["muted"])
    return draw


def cover_slide(sections, version, footer):
    image = background()
    draw = ImageDraw.Draw(image)
    y = 170
    if LOGO.exists():
        logo = Image.open(LOGO).convert("RGBA")
        logo = logo.resize((LOGO_W, round(LOGO_W * logo.height / logo.width)), Image.LANCZOS)
        image.paste(logo, ((W - logo.width) // 2, y), logo)
        y += logo.height + 60
    else:
        draw.text((MARGIN, y), "EXTRABIOMES", font=font("bold", 40), fill=THEME["accent"])
        y += 80
    # The version always sets on one line, so it runs to a tighter margin than the body and
    # the point size shrinks to whatever fits that width.
    size = next(size for size in range(96, 47, -4)
                if draw.textlength(version, font=font("bold", size)) <= W - 2 * MARGIN)
    draw.text((MARGIN, y), version, font=font("bold", size), fill=THEME["ink"])
    y += size + 40
    draw.text((MARGIN, y + 10), f"{EDITION} Edition", font=font("bold", 58), fill=THEME["accent"])
    y += 100
    # The contents line is bottom-anchored above the footer and the hero takes whatever is
    # left, so a longer version or edition string can't push either off the slide.
    entry_font = font("regular", 46)
    lines = [heading for heading, _ in sections]
    contents_top = H - 300 - len(lines) * 64
    hero = next((fetch(url) for _, entries in sections for kind, url in entries if kind == "image"), None)
    if hero is not None:
        paste_image(image, hero, y + 110 + (contents_top - y - 170 - BAND_H) // 2)
    for offset, line in enumerate(lines):
        draw.text((MARGIN, contents_top + offset * 64), line, font=entry_font, fill=THEME["muted"])
    draw.text((MARGIN, H - 190), footer, font=font("regular", 34), fill=THEME["muted"])
    return image


def section_slides(heading, entries, version, footer):
    """One slide per screenful of entries - long sections continue onto further slides."""
    heading_font, sub_font, body_font = font("bold", 108), font("bold", 46), font("regular", 44)
    scratch = ImageDraw.Draw(Image.new("RGB", (1, 1)))
    image, draw, y = None, None, 0
    slides = []
    for index, (kind, text) in enumerate(entries):
        photo = fetch(text) if kind == "image" else None
        if kind == "image" and photo is None:
            continue
        entry_font = sub_font if kind == "sub" else body_font
        indent = 0 if kind == "sub" else 46
        lines = [] if kind == "image" else wrap(scratch, text, entry_font, W - 2 * MARGIN - indent)
        needed = BAND_H + 48 if kind == "image" else len(lines) * 58 + (46 if kind == "sub" else 34)
        # A subheading alone at the foot of a slide reads as an orphan, so it has to fit
        # whatever follows it too - that only affects the fit test, never the cursor.
        required = needed
        if kind == "sub" and index + 1 < len(entries):
            required += BAND_H + 48 if entries[index + 1][0] == "image" else 120
        if image is None or y + required > BODY_BOTTOM:
            image = background()
            draw = chrome(image, version, footer)
            draw.text((MARGIN, BODY_TOP - 190), heading, font=heading_font, fill=THEME["ink"])
            # The rule runs the width of the heading itself, not a fixed stub.
            draw.line([(MARGIN, BODY_TOP - 50), (draw.textlength(heading, font=heading_font) + MARGIN, BODY_TOP - 50)],
                      fill=THEME["accent"], width=8)
            slides.append(image)
            y = BODY_TOP
        if kind == "image":
            paste_image(image, photo, y)
            y += needed
            continue
        if kind == "bullet":
            draw.ellipse([MARGIN, y + 20, MARGIN + 12, y + 32], fill=THEME["accent"])
        for offset, line in enumerate(lines):
            draw.text((MARGIN + indent, y + offset * 58), line, font=entry_font,
                      fill=THEME["ink"] if kind == "bullet" else THEME["accent"])
        y += needed
    return slides


def pretty_version(version):
    """gradle's mod_version ("3.10.0-beta-7") reads as a filename, not a headline."""
    return "v" + re.sub(r"-(alpha|beta|rc)-?(\d+)", lambda m: f" {m[1].title()} {m[2]}",
                        version.lstrip("vV"))


def render(markdown, version, footer, out_dir):
    sections = parse(markdown)
    version = pretty_version(version)
    slides = [cover_slide(sections, version, footer)]
    for heading, entries in sections:
        slides += section_slides(heading, entries, version, footer)
    out_dir.mkdir(parents=True, exist_ok=True)
    paths = []
    for index, slide in enumerate(slides, start=1):
        path = out_dir / f"slide-{index}.jpg"
        slide.save(path, "JPEG", quality=90, optimize=True)
        paths.append(path)
    return paths


def selftest():
    sections = parse("# ExtraBiomes v3 Beta 7\n"
                     "## Structures\n### Sky City\n"
                     '<img src="x.png" />\n'
                     "- made **bigger**\n\n## Empty\n")
    assert sections == [("Structures", [("sub", "Sky City"), ("image", "x.png"),
                                        ("bullet", "made bigger")])], sections
    assert pretty_version("3.10.0-beta-7") == "v3.10.0 Beta 7", pretty_version("3.10.0-beta-7")
    assert pretty_version("v3.10.0") == "v3.10.0"
    assert images_in("![shot](https://e/a.png) and <img width=\"9\" src=\"https://e/b.png\" />") == \
        ["https://e/a.png", "https://e/b.png"]
    draw = ImageDraw.Draw(Image.new("RGB", (1, 1)))
    assert len(wrap(draw, "word " * 60, font("regular", 44), 500)) > 1
    print("ok")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("changelog", nargs="?", type=Path)
    parser.add_argument("--version", default="")
    parser.add_argument("--footer", default="Out now on Modrinth & CurseForge")
    parser.add_argument("--out", type=Path, default=Path("slides"))
    parser.add_argument("--theme", choices=sorted(THEMES), default="glacier")
    parser.add_argument("--edition", choices=["Java", "Bedrock"],
                        help="defaults to the changelog filename's prefix")
    parser.add_argument("--selftest", action="store_true")
    args = parser.parse_args()
    if args.selftest:
        return selftest()
    global THEME, EDITION
    THEME = THEMES[args.theme]
    prefix = args.changelog.name.split("-")[0] if args.changelog else ""
    EDITION = args.edition or (prefix if prefix in ("Java", "Bedrock") else "Java")
    if not args.changelog:
        parser.error("changelog path required")
    for path in render(args.changelog.read_text(), args.version, args.footer, args.out):
        print(path)


if __name__ == "__main__":
    sys.exit(main())
