#!/usr/bin/env python3
"""Give every gold pixel in the Gilded Sky textures the same LabPBR specular (shiny gold, not a mirror).

    python3 tools/gilded_gold.py

Existing *_s.png files keep their non-gold pixels; missing ones are created with a matte base.
"""
import glob
import os

import numpy as np
from PIL import Image

TEXTURES = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                        "ExtraBiomes - Java", "common", "src", "main", "resources", "assets", "extrabiomes", "textures")
GOLD_SHADES = ["fffd90", "ffec4f", "fee048", "ffd83e", "f5cc27", "f9bd23", "d39632", "cc8e27", "feffbd"]
GOLD_SPECULAR = (160, 231, 0, 0)  # smoothness (255 = mirror), LabPBR gold metal ID, porosity, emissive
MATTE_SPECULAR = (0, 0, 0, 0)

GOLD_RGB = np.array([[int(h[i:i + 2], 16) for i in (0, 2, 4)] for h in GOLD_SHADES], dtype=np.uint8)


def gilded_textures():
    patterns = ["block/wood/gilded_sky/*gilded_sky*.png", "entity/**/*gilded_sky*.png"]
    for pattern in patterns:
        for path in sorted(glob.glob(os.path.join(TEXTURES, pattern), recursive=True)):
            if not path.endswith(("_n.png", "_s.png")):
                yield path


def paint_gold_specular(color_path):
    color = np.asarray(Image.open(color_path).convert("RGBA"))
    is_gold = (color[:, :, 3] > 127) & (color[:, :, None, :3] == GOLD_RGB).all(axis=3).any(axis=2)
    spec_path = color_path[:-4] + "_s.png"
    if os.path.exists(spec_path):
        spec = np.asarray(Image.open(spec_path).convert("RGBA")).copy()
    else:
        spec = np.tile(np.array(MATTE_SPECULAR, dtype=np.uint8), (*is_gold.shape, 1))
    spec[is_gold] = GOLD_SPECULAR
    Image.fromarray(spec, mode="RGBA").save(spec_path)
    return int(is_gold.sum())


if __name__ == "__main__":
    for path in gilded_textures():
        print(f"{paint_gold_specular(path):4d} gold px  {os.path.relpath(path, TEXTURES)}")
