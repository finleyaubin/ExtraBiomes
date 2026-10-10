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
LINK_LINE = "Link in bio"
LINK_STEP = 40
HEAD_TOP = 190   # first heading line, just under the profile row Instagram draws over stories
HEAD_STEP = 118  # line pitch for a heading that wraps onto several lines

BAND_H = round((W - 2 * MARGIN) * 9 / 16)  # changelog screenshots are normally 16:9
STANDARD_ASPECT = 16 / 9
ASPECT_TOLERANCE = 0.12  # within this of 16:9 an image is cropped to the band, otherwise it keeps its own shape
MAX_IMAGE_H = round(BAND_H * 1.4)
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


def image_box(photo):
    """-> (width, height) of the frame a screenshot is drawn into. Near-16:9 shots fill the standard
    band; anything else keeps its own aspect ratio, full column width unless that would be taller than
    MAX_IMAGE_H, in which case it is height-limited and centred."""
    column = W - 2 * MARGIN
    aspect = photo.width / photo.height
    if abs(aspect / STANDARD_ASPECT - 1) <= ASPECT_TOLERANCE:
        return column, BAND_H
    height = round(column / aspect)
    if height > MAX_IMAGE_H:
        return round(MAX_IMAGE_H * aspect), MAX_IMAGE_H
    return column, height


def paste_image(slide, photo, y, radius=40):
    width, height = image_box(photo)
    if (width, height) == (W - 2 * MARGIN, BAND_H):
        # Crop to the band's shape rather than stretching a slightly-off screenshot.
        crop_h = min(photo.height, round(photo.width * BAND_H / width))
        crop_w = min(photo.width, round(photo.height * width / BAND_H))
        left, top = (photo.width - crop_w) // 2, (photo.height - crop_h) // 2
        photo = photo.crop((left, top, left + crop_w, top + crop_h))
    # Small sources are pixel art (spawn egg strips, icons); smoothing them to blur is worse than blocky.
    method = Image.NEAREST if photo.width * 2 < width else Image.LANCZOS
    mask = Image.new("L", (width, height), 0)
    ImageDraw.Draw(mask).rounded_rectangle([0, 0, width - 1, height - 1], min(radius, height // 2), fill=255)
    left = MARGIN + (W - 2 * MARGIN - width) // 2
    slide.paste(photo.resize((width, height), method), (left, y), mask)


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


def intro_paragraphs(markdown):
    """The prose between the "# " title and the next heading, one cleaned string per line of text."""
    paragraphs, seen_title = [], False
    for raw in markdown.splitlines():
        line = raw.strip()
        if line.startswith("#"):
            if seen_title:
                break
            seen_title = line.startswith("# ")
            continue
        if seen_title and line:
            text = clean(line)
            if text:
                paragraphs.append(text)
    return paragraphs


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


def heading_layout(draw, heading, size=108, smallest=32):
    """-> (font, lines). Wraps on word boundaries; if a single word is still wider than the text
    column the point size shrinks until it fits, so a long heading can never run off the slide."""
    width = W - 2 * MARGIN
    for pt in range(size, smallest - 1, -4):
        fnt = font("bold", pt)
        if all(draw.textlength(word, font=fnt) <= width for word in heading.split()):
            break
    return fnt, wrap(draw, heading, fnt, width)


def background():
    image = Image.new("RGB", (W, H), THEME["top"])
    draw = ImageDraw.Draw(image)
    for y in range(H):
        blend = y / H
        draw.line([(0, y), (W, y)], fill=tuple(
            round(top + (bottom - top) * blend) for top, bottom in zip(THEME["top"], THEME["bottom"])))
    return image


def links_height():
    return LINK_STEP


def draw_links(draw, top):
    # Stories posted through the API can't carry link stickers, and a printed URL can't be tapped
    # anyway, so the slides point at the profile's bio link instead.
    draw.text((MARGIN, top), LINK_LINE, font=font("bold", 32), fill=THEME["accent"])


def chrome(image, version, footer):
    """The section name is the headline on its own slide, so brand, version and edition sit
    quietly in the footer instead of competing with it from the top of every slide."""
    draw = ImageDraw.Draw(image)
    top = H - 190 - links_height()
    draw.text((MARGIN, top), f"ExtraBiomes {version} · {EDITION} Edition",
              font=font("bold", 32), fill=THEME["accent"])
    draw.text((MARGIN, top + 46), footer, font=font("regular", 32), fill=THEME["muted"])
    draw_links(draw, top + 96)
    return draw


def intro_block(draw, paragraphs, max_height):
    """-> (font, line pitch, lines) for the largest text size at which the intro fits max_height, or
    None when even the smallest size can't fit it."""
    for size in range(46, 27, -2):
        fnt = font("regular", size)
        pitch = round(size * 1.3)
        lines = []
        for paragraph in paragraphs:
            lines += wrap(draw, paragraph, fnt, W - 2 * MARGIN)
        if len(lines) * pitch <= max_height:
            return fnt, pitch, lines
    return None


def cover_slide(sections, version, footer, intro=()):
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
    hero = next((fetch(url) for _, entries in sections for kind, url in entries if kind == "image"), None)
    hero_h = image_box(hero)[1] if hero is not None else 0
    # The text under the image is bottom-anchored above the footer and the hero takes whatever is
    # left, so a longer version or edition string can't push either off the slide. The changelog's
    # intro goes there when it has one and fits; otherwise the section names do.
    text_bottom = H - 300 - links_height()
    text_room = text_bottom - (y + 110 + hero_h + 24) if hero is not None else text_bottom - y
    block = intro_block(draw, list(intro), text_room) if intro else None
    if block is None and intro:
        block = intro_block(draw, list(intro)[:1], text_room)
    if block is not None:
        entry_font, pitch, lines = block
    else:
        entry_font, pitch, lines = font("regular", 46), 64, [heading for heading, _ in sections]
    text_top = text_bottom - len(lines) * pitch
    if hero is not None:
        paste_image(image, hero, y + 110 + (text_top - y - 170 - hero_h) // 2)
    for offset, line in enumerate(lines):
        draw.text((MARGIN, text_top + offset * pitch), line, font=entry_font, fill=THEME["muted"])
    draw.text((MARGIN, H - 190 - links_height()), footer, font=font("regular", 34), fill=THEME["muted"])
    draw_links(draw, H - 140 - links_height())
    return image


def section_slides(heading, entries, version, footer):
    """One slide per screenful of entries - long sections continue onto further slides."""
    sub_font, body_font = font("bold", 46), font("regular", 44)
    scratch = ImageDraw.Draw(Image.new("RGB", (1, 1)))
    heading_font, heading_lines = heading_layout(scratch, heading)
    # Every extra heading line pushes the rule and the body down by one line pitch.
    body_top = BODY_TOP + (len(heading_lines) - 1) * HEAD_STEP
    image, draw, y = None, None, 0
    slides = []
    for index, (kind, text) in enumerate(entries):
        photo = fetch(text) if kind == "image" else None
        if kind == "image" and photo is None:
            continue
        entry_font = sub_font if kind == "sub" else body_font
        indent = 0 if kind == "sub" else 46
        lines = [] if kind == "image" else wrap(scratch, text, entry_font, W - 2 * MARGIN - indent)
        needed = image_box(photo)[1] + 48 if kind == "image" else len(lines) * 58 + (46 if kind == "sub" else 34)
        # A subheading alone at the foot of a slide reads as an orphan, so it has to fit
        # whatever follows it too - that only affects the fit test, never the cursor.
        required = needed
        if kind == "sub" and index + 1 < len(entries):
            following = entries[index + 1]
            next_photo = fetch(following[1]) if following[0] == "image" else None
            required += image_box(next_photo)[1] + 48 if next_photo is not None else 120
        if image is None or y + required > BODY_BOTTOM - links_height():
            image = background()
            draw = chrome(image, version, footer)
            for number, line in enumerate(heading_lines):
                draw.text((MARGIN, HEAD_TOP + number * HEAD_STEP), line, font=heading_font, fill=THEME["ink"])
            # The rule runs the width of the widest heading line, not a fixed stub.
            widest = max(draw.textlength(line, font=heading_font) for line in heading_lines)
            draw.line([(MARGIN, body_top - 50), (widest + MARGIN, body_top - 50)],
                      fill=THEME["accent"], width=8)
            slides.append(image)
            y = body_top
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
    slides = [cover_slide(sections, version, footer, intro_paragraphs(markdown))]
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
    assert intro_paragraphs("# Title\nA **bold** intro.\n\n<img src=\"x.png\" />\nSecond line\n# Changes\nnot intro\n") == \
        ["A bold intro.", "Second line"]
    assert intro_paragraphs("# Title\n# Changes\n## Biomes\n- x\n") == []

    class Shape:
        def __init__(self, width, height):
            self.width, self.height = width, height

    column = W - 2 * MARGIN
    assert image_box(Shape(2560, 1440)) == (column, BAND_H)
    assert image_box(Shape(1920, 1080)) == (column, BAND_H)
    strip = image_box(Shape(431, 58))
    assert strip[0] == column and strip[1] < BAND_H // 2, strip
    tall = image_box(Shape(600, 900))
    assert tall[1] == MAX_IMAGE_H and tall[0] < column, tall
    assert pretty_version("3.10.0-beta-7") == "v3.10.0 Beta 7", pretty_version("3.10.0-beta-7")
    assert pretty_version("v3.10.0") == "v3.10.0"
    assert images_in("![shot](https://e/a.png) and <img width=\"9\" src=\"https://e/b.png\" />") == \
        ["https://e/a.png", "https://e/b.png"]
    draw = ImageDraw.Draw(Image.new("RGB", (1, 1)))
    assert len(wrap(draw, "word " * 60, font("regular", 44), 500)) > 1
    for heading in ("World Generation", "Floating Jungle", "Sky City", "Extraordinarilylongheadingword"):
        fnt, lines = heading_layout(draw, heading)
        assert " ".join(lines) == heading, (heading, lines)
        assert all(draw.textlength(line, font=fnt) <= W - 2 * MARGIN for line in lines), (heading, lines)
    assert len(heading_layout(draw, "World Generation")[1]) == 2
    print("ok")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("changelog", nargs="?", type=Path)
    parser.add_argument("--version", default="")
    parser.add_argument("--footer", default=None)
    parser.add_argument("--out", type=Path, default=Path("slides"))
    parser.add_argument("--theme", choices=sorted(THEMES),
                        help="defaults to bryce for Bedrock, glacier for Java")
    parser.add_argument("--edition", choices=["Java", "Bedrock"],
                        help="defaults to the changelog filename's prefix")
    parser.add_argument("--selftest", action="store_true")
    args = parser.parse_args()
    if args.selftest:
        return selftest()
    global THEME, EDITION
    prefix = args.changelog.name.split("-")[0] if args.changelog else ""
    EDITION = args.edition or (prefix if prefix in ("Java", "Bedrock") else "Java")
    THEME = THEMES[args.theme or ("bryce" if EDITION == "Bedrock" else "glacier")]
    if not args.changelog:
        parser.error("changelog path required")
    footer = args.footer or (
        "Out now on mcpedl & CurseForge" if EDITION == "Bedrock" else "Out now on Modrinth & CurseForge")
    for path in render(args.changelog.read_text(), args.version, footer, args.out):
        print(path)


if __name__ == "__main__":
    sys.exit(main())
