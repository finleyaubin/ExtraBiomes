"""Ask an Ollama vision model what each colour in a texture represents and how high it sits.

level_map(path) -> float array in 0-1 (one value per pixel), cached in height_palettes.json so
reruns are deterministic and the model's decisions can be reviewed and hand-edited.
Raises on any Ollama or parsing failure; nothing falls back to a guess.
"""
import base64
import hashlib
import io
import json
import os

import numpy as np
import requests
from PIL import Image, ImageDraw

OLLAMA_URL = os.environ.get("OLLAMA_URL", "http://localhost:11434/api/chat")
MODEL = os.environ.get("HEIGHT_VISION_MODEL", "qwen3.8:latest")
MAX_COLOURS = 12
LEVELS = 6
CACHE_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "height_palettes.json")

SCHEMA = {
    "type": "object",
    "properties": {
        "subject": {"type": "string"},
        "entries": {
            "type": "array",
            "items": {
                "type": "object",
                "properties": {
                    "index": {"type": "integer"},
                    "role": {"type": "string"},
                    "level": {"type": "integer", "minimum": 0, "maximum": LEVELS - 1},
                },
                "required": ["index", "role", "level"],
            },
        },
    },
    "required": ["subject", "entries"],
}

PROMPT = f"""This is a small Minecraft pixel-art texture named "{{name}}" (left) with a numbered colour legend (right).
First say what the texture depicts. Then, for EVERY numbered colour, say what that colour represents
(e.g. grout, brick face, bark ridge, bark crevice, leaf, eye, skin, ore vein, stone) and give it a
physical surface height from 0 (deepest: gaps, grout, crevices, eye sockets, holes) to {LEVELS - 1}
(highest: raised ridges, ore nuggets, bolts, studs). Colours that are only lighter or darker shades of
the same material and the same depth MUST share the same level. Use the full range only if the
texture really has that much relief. Do not give every colour the same level: unless a colour's role says
otherwise, darker colours sit deeper and lighter colours sit higher.
The legend has {{count}} colours, numbered 0 to {{last}}; return exactly one entry for each."""


def _load_cache():
    if not os.path.exists(CACHE_PATH):
        return {}
    with open(CACHE_PATH, encoding="utf-8") as fh:
        return json.load(fh)


def _save_cache(cache):
    with open(CACHE_PATH, "w", encoding="utf-8") as fh:
        json.dump(cache, fh, indent=1, sort_keys=True)
        fh.write("\n")


def _quantize(path):
    """Return (palette[(r,g,b)], index array HxW, opaque mask HxW)."""
    rgba = Image.open(path).convert("RGBA")
    opaque = np.asarray(rgba)[:, :, 3] > 127
    rgb = rgba.convert("RGB")
    q = rgb.quantize(colors=MAX_COLOURS, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE)
    raw = q.getpalette()
    used = int(np.asarray(q).max()) + 1
    palette = [tuple(raw[i * 3:i * 3 + 3]) for i in range(used)]
    return palette, np.asarray(q), opaque


def _legend_image(path, palette):
    tex = Image.open(path).convert("RGBA")
    scale = max(1, 256 // max(tex.size))
    backdrop = Image.new("RGBA", tex.size, (128, 128, 128, 255))
    tex = Image.alpha_composite(backdrop, tex).resize((tex.width * scale, tex.height * scale), Image.NEAREST)
    row = 28
    legend = Image.new("RGB", (190, max(tex.height, row * len(palette))), (255, 255, 255))
    draw = ImageDraw.Draw(legend)
    for i, colour in enumerate(palette):
        draw.rectangle([4, i * row + 2, 40, i * row + row - 2], fill=colour, outline=(0, 0, 0))
        draw.text((50, i * row + 8), f"{i}  #{colour[0]:02x}{colour[1]:02x}{colour[2]:02x}", fill=(0, 0, 0))
    sheet = Image.new("RGB", (tex.width + legend.width + 8, max(tex.height, legend.height)), (255, 255, 255))
    sheet.paste(tex.convert("RGB"), (0, 0))
    sheet.paste(legend, (tex.width + 8, 0))
    return sheet


def _ask_model(path, palette):
    buf = io.BytesIO()
    _legend_image(path, palette).save(buf, "PNG")
    reply = requests.post(OLLAMA_URL, json={
        "model": MODEL,
        "stream": False,
        "think": False,
        "format": SCHEMA,
        "options": {"temperature": 0},
        "messages": [{"role": "user", "content": PROMPT.format(name=os.path.splitext(os.path.basename(path))[0], count=len(palette), last=len(palette) - 1),
                      "images": [base64.b64encode(buf.getvalue()).decode()]}],
    }, timeout=600)
    reply.raise_for_status()
    result = json.loads(reply.json()["message"]["content"])
    levels = {e["index"]: e["level"] for e in result["entries"]}
    missing = set(range(len(palette))) - set(levels)
    if missing:
        raise ValueError(f"{path}: model gave no level for palette indexes {sorted(missing)}")
    return {"subject": result["subject"], "model": MODEL, "palette": [
        {"index": i, "colour": "#%02x%02x%02x" % palette[i], "role": next(e["role"] for e in result["entries"] if e["index"] == i),
         "level": levels[i]} for i in range(len(palette))]}


def _spread_levels(levels):
    """Space the distinct levels evenly; two depths give half relief, three or more full relief."""
    distinct = sorted(set(levels))
    if len(distinct) == 1:
        return {distinct[0]: 0.0}
    relief = min(1.0, (len(distinct) - 1) / 2)
    return {level: relief * rank / (len(distinct) - 1) for rank, level in enumerate(distinct)}


def level_map(path):
    digest = hashlib.sha1(open(path, "rb").read()).hexdigest()
    palette, indexes, opaque = _quantize(path)
    cache = _load_cache()
    entry = cache.get(digest)
    if entry is None:
        entry = {"example": os.path.basename(path), **_ask_model(path, palette)}
        cache[digest] = entry
        _save_cache(cache)
        print(f"  vision: {entry['example']}: {entry['subject']}")
    spread = _spread_levels([p["level"] for p in entry["palette"]])
    by_index = np.array([spread[p["level"]] for p in entry["palette"]], dtype=np.float32)
    return np.where(opaque, by_index[indexes], 0.0)
