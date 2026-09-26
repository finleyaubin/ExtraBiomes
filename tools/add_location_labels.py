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
    python add_location_labels.py [input_root] [output_root] \
        [--fonts-dir DIR] [--title-font NAME] [--sub-font NAME]
input_root/output_root and the two fonts can all be given as flags/arguments;
whatever is left unspecified, or can't be found (e.g. a font name that isn't
installed), pops up a picker (folder chooser / font list with a Browse button)
via tkinter instead of failing outright. Needs tkinter (Linux: `apt install
python3-tk` or your distro's equivalent) and a display; without either, or
in a headless environment, missing values fail with an error telling you
which flag to pass instead.
"""

import colorsys
import os
import sys
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont, ImageStat

DEFAULT_TITLE_FONT = "GOTHICB.TTF"   # Century Gothic Bold
DEFAULT_SUB_FONT = "GOTHIC.TTF"      # Century Gothic Regular

# Common per-OS font install locations, searched in order when a font is given as a bare
# filename rather than a path. Override with --fonts-dir or the EXTRABIOMES_FONTS_DIR env
# var instead of editing this.
CANDIDATE_FONT_DIRS = [
    os.environ.get("WINDIR", r"C:\Windows") + r"\Fonts",
    str(Path.home() / "AppData/Local/Microsoft/Windows/Fonts"),
    "/Library/Fonts",
    str(Path.home() / "Library/Fonts"),
    "/System/Library/Fonts/Supplemental",
    str(Path.home() / ".local/share/fonts"),
    str(Path.home() / ".fonts"),
    "/usr/share/fonts/truetype",
    "/usr/local/share/fonts",
]


def _tk_root():
    """A hidden root window, or exit with a clear message if tkinter/a display isn't available."""
    try:
        import tkinter as tk
        root = tk.Tk()
    except Exception as e:
        sys.exit(f"No picker window available ({e}). "
                  "Pass the missing value explicitly as a flag/argument instead.")
    root.withdraw()
    return tk, root


def pick_directory(title: str) -> Path:
    tk, root = _tk_root()
    from tkinter import filedialog
    chosen = filedialog.askdirectory(title=title, parent=root)
    root.destroy()
    if not chosen:
        sys.exit(f"No folder selected for: {title}")
    return Path(chosen)


def pick_font(role: str, search_dirs: list) -> Path:
    """List every .ttf/.otf found under search_dirs and let the user pick one, or browse
    to an arbitrary file."""
    tk, root = _tk_root()
    from tkinter import filedialog

    found, seen = [], set()
    for d in search_dirs:
        if not d.is_dir():
            continue
        for p in d.rglob("*"):
            if p.suffix.lower() in (".ttf", ".otf") and p.name.lower() not in seen:
                seen.add(p.name.lower())
                found.append(p)
    found.sort(key=lambda p: p.name.lower())

    result = {"path": None}
    win = tk.Toplevel(root)
    win.title(f"Choose {role} font")
    win.geometry("440x440")

    tk.Label(win, text=f"No {role} font found/specified.\n"
                        f"Pick one below, or browse for a font file:").pack(pady=8)

    listbox = tk.Listbox(win, width=55, height=16)
    for p in found:
        listbox.insert(tk.END, f"{p.name}   ({p.parent})")
    listbox.pack(padx=10, pady=6, fill="both", expand=True)
    if found:
        listbox.selection_set(0)

    def use_selected():
        sel = listbox.curselection()
        if sel:
            result["path"] = found[sel[0]]
            win.destroy()

    def browse():
        chosen = filedialog.askopenfilename(
            title=f"Choose {role} font file", parent=win,
            filetypes=[("Fonts", "*.ttf *.otf *.TTF *.OTF"), ("All files", "*.*")])
        if chosen:
            result["path"] = Path(chosen)
            win.destroy()

    btns = tk.Frame(win)
    btns.pack(pady=8)
    tk.Button(btns, text="Use selected", command=use_selected).pack(side="left", padx=4)
    tk.Button(btns, text="Browse for a file...", command=browse).pack(side="left", padx=4)
    tk.Button(btns, text="Cancel", command=win.destroy).pack(side="left", padx=4)
    listbox.bind("<Double-Button-1>", lambda _e: use_selected())

    win.protocol("WM_DELETE_WINDOW", win.destroy)
    win.wait_visibility()  # grab_set fails if the window manager hasn't mapped the window yet
    try:
        win.grab_set()
    except tk.TclError:
        pass  # not modal, but still fully usable -- better than crashing (seen under WSLg)
    root.wait_window(win)
    root.destroy()

    if result["path"] is None:
        sys.exit(f"No font selected for: {role}")
    return result["path"]


def resolve_font(name_or_path: str, fonts_dir_cli: str | None, role: str) -> Path:
    """A font given as an existing path is used as-is; a bare filename is searched for in
    --fonts-dir, EXTRABIOMES_FONTS_DIR, then the common per-OS font folders above. If it
    can't be found anywhere, fall back to a picker window instead of failing outright."""
    direct = Path(name_or_path)
    if direct.is_file():
        return direct

    search_dirs = []
    if fonts_dir_cli:
        search_dirs.append(Path(fonts_dir_cli))
    if os.environ.get("EXTRABIOMES_FONTS_DIR"):
        search_dirs.append(Path(os.environ["EXTRABIOMES_FONTS_DIR"]))
    search_dirs += [Path(c) for c in CANDIDATE_FONT_DIRS]

    for d in search_dirs:
        if (d / name_or_path).is_file():
            return d / name_or_path

    print(f"Could not find font {name_or_path!r} as a path or in any of the usual font "
          f"folders -- opening a picker for the {role} font.")
    return pick_font(role, search_dirs)

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


def label_image(src: Path, dst: Path, title: str, subtitle: str, title_font: Path, sub_font: Path):
    img = Image.open(src).convert("RGBA")
    w, h = img.size
    title_col = complementary_pastel(img)

    tf = ImageFont.truetype(str(title_font), w // 20)
    sf = ImageFont.truetype(str(sub_font), w // 66)
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
    import argparse

    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("input_root", type=Path, nargs="?", default=None,
                         help="folder of raw screenshots to label (else a folder picker pops up)")
    parser.add_argument("output_root", type=Path, nargs="?", default=None,
                         help="folder to write labelled JPEGs into (else a folder picker pops up)")
    parser.add_argument("--fonts-dir", help="folder to search for --title-font/--sub-font "
                                             "when given as bare filenames (else "
                                             "EXTRABIOMES_FONTS_DIR env var, else auto-detected)")
    parser.add_argument("--title-font", default=DEFAULT_TITLE_FONT,
                         help=f"bold/title font, filename or path (default: {DEFAULT_TITLE_FONT})")
    parser.add_argument("--sub-font", default=DEFAULT_SUB_FONT,
                         help=f"regular/subtitle font, filename or path (default: {DEFAULT_SUB_FONT})")
    args = parser.parse_args()

    title_font = resolve_font(args.title_font, args.fonts_dir, "title")
    sub_font = resolve_font(args.sub_font, args.fonts_dir, "subtitle")
    input_root = args.input_root or pick_directory("Select the folder of raw screenshots")
    output_root = args.output_root or pick_directory("Select the folder to write labelled JPEGs into")

    images = sorted(p for p in input_root.rglob("*") if p.suffix.lower() in (".png", ".jpg", ".jpeg"))
    if not images:
        sys.exit(f"No images found under {input_root}")

    for src in images:
        rel = src.relative_to(input_root)
        subtitle = " ".join(rel.parent.parts) if rel.parent.parts else "Overworld"
        dst = (output_root / rel).with_suffix(".jpeg")
        col = label_image(src, dst, src.stem, subtitle, title_font, sub_font)
        print(f"labelled: {rel}  (title colour #{col[0]:02x}{col[1]:02x}{col[2]:02x})")

    print(f"\n{len(images)} images written to {output_root}")


if __name__ == "__main__":
    main()
