#!/usr/bin/env python3
"""Convert Bedrock RTX PBR texture sets (color + metalness/emissive/roughness[/subsurface]
+ optional heightmap, described by each *.texture_set.json) into LabPBR-format textures
for the Java resource pack, so Iris/Oculus shaders pick up matching materials.

Requires Pillow + numpy (pip install pillow numpy). Run with any python3:
    python3 tools/pbr2java.py --list                 # show Bedrock/Java name matches
    python3 tools/pbr2java.py black_sandstone_top     # convert one texture (test case)
    python3 tools/pbr2java.py --all                   # convert every matched texture

Output: for a matched Java texture assets/extrabiomes/textures/block/**/<name>.png, writes
<name>_n.png (LabPBR normal+AO+height) and <name>_s.png (LabPBR specular) next to it.

--- Bedrock source format (from *.texture_set.json) ---
  color:                                   diffuse texture (already mirrored in the Java pack)
  metalness_emissive_roughness (RGB)  or
  metalness_emissive_roughness_subsurface (RGBA, usually a .tga):
      R = metalness [0-255], G = emissive [0-255], B = roughness [0-255], A = subsurface [0-255]
  heightmap (L, optional):                grayscale bump height, 255 = highest point

--- LabPBR 1.3 output format ---
  <name>_n.png (normal map):
      R,G = tangent-space normal X,Y (encoded 0-255, 127/128 = flat), derived from the
            Bedrock heightmap via a Sobel filter (Bedrock ships no true per-pixel normal
            data for these blocks, only height)
      B   = ambient occlusion (255 = no occlusion; we have no AO source, so always 255)
      A   = height/POM value, passed through directly from the Bedrock heightmap
            (255 = no displacement .. 0 = deepest), or 255 (flat) when no heightmap exists
  <name>_s.png (specular map):
      R = smoothness = 255 - roughness
      G = F0/metalness: metalness < 0.5 -> dielectric F0 = metalness byte, clamped to 229;
                         metalness >= 0.5 -> metal, encoded as generic Iron (230) since
                         Bedrock does not tell us which metal it should be. Tune per-block
                         via METAL_OVERRIDES below if a specific block deserves a real metal ID.
      B = porosity/SSS: subsurface byte (if any) remapped into the SSS band 65-255, else 0
      A = emissive: emissive byte, clamped to 0-254 (255 is reserved by the spec)

This is a first-pass approximation (see conversation) meant to be checked visually in a
LabPBR shader pack (Complementary/BSL/Photon) before being trusted across the whole pack.
"""
import json
import os
import sys

import numpy as np
from PIL import Image

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BEDROCK_BLOCKS = os.path.join(REPO_ROOT, "ExtraBiomes - Bedrock", "packs", "RP", "textures", "blocks")
JAVA_BLOCKS = os.path.join(REPO_ROOT, "ExtraBiomes - Java", "common", "src", "main", "resources",
                            "assets", "extrabiomes", "textures", "block")

# Bedrock texture_set base name -> Java texture base name, for one-off cases that don't
# fit the generic wood-naming rule below (e.g. Bedrock's default/undecorated sandstone
# variant is suffixed "_normal", Java just calls it the plain name).
NAME_ALIASES = {
    "black_sandstone_normal": "black_sandstone",
    "black_sandstone_carved": "chiseled_black_sandstone",
    "log_gilded_sky_stripped": "stripped_gilded_sky_log",
}

# Bedrock wood textures are named "<part>_<wood>[_top]" (log_ash, log_ash_top, planks_ash,
# leaves_ash, sapling_ash); Java names them "<wood>_<part>[_top]" (ash_log, ash_log_top, ...).
import re as _re
_WOOD_PART_RE = _re.compile(r"^(log|planks|leaves|sapling)_(.+?)(_top)?$")


def _generic_alias(bedrock_base):
    m = _WOOD_PART_RE.match(bedrock_base)
    if not m:
        return None
    part, wood, top = m.group(1), m.group(2), m.group(3) or ""
    return f"{wood}_{part}{top}"

# Explicit LabPBR metal IDs (230-255 band) for blocks that are unambiguously a specific
# metal, keyed by Java texture base name. Anything metallic not listed here falls back to
# 230 (Iron) as a generic default -- see module docstring.
METAL_OVERRIDES = {
    # "some_gold_block": 231,
}


def find_texture_set_jsons():
    for root, _dirs, files in os.walk(BEDROCK_BLOCKS):
        for f in files:
            if f.endswith(".texture_set.json"):
                yield os.path.join(root, f)


def _first_existing(root, base, suffixes):
    for suf, ext in suffixes:
        p = os.path.join(root, base + suf + ext)
        if os.path.isfile(p):
            return p
    return None


def load_bedrock_set(json_path):
    with open(json_path, "r", encoding="utf-8-sig") as fh:
        data = json.load(fh)["minecraft:texture_set"]
    root = os.path.dirname(json_path)

    mer_key = next((k for k in data if k.startswith("metalness_emissive_roughness")), None)
    has_subsurface = mer_key == "metalness_emissive_roughness_subsurface"

    mer_path = _first_existing(root, data[mer_key], [("", ".png"), ("", ".tga")]) if mer_key else None
    height_path = None
    if "heightmap" in data:
        height_path = _first_existing(root, data["heightmap"], [("", ".png"), ("", ".tga")])

    bedrock_base = os.path.splitext(os.path.basename(json_path))[0].removesuffix(".texture_set")
    return {
        "base": bedrock_base,
        "mer_path": mer_path,
        "has_subsurface": has_subsurface,
        "height_path": height_path,
    }


def java_dir_for(java_base):
    for root, _, files in os.walk(JAVA_BLOCKS):
        if java_base + ".png" in files:
            return root
    return JAVA_BLOCKS


def java_name_for(bedrock_base):
    if bedrock_base in NAME_ALIASES:
        return NAME_ALIASES[bedrock_base]
    return _generic_alias(bedrock_base) or bedrock_base


def match_all():
    """Return {java_base: bedrock_set_dict} for every Bedrock set with a Java texture match."""
    matched, unmatched = {}, []
    for jp in find_texture_set_jsons():
        s = load_bedrock_set(jp)
        java_base = java_name_for(s["base"])
        java_png = os.path.join(java_dir_for(java_base), java_base + ".png")
        if os.path.isfile(java_png):
            matched[java_base] = s
        else:
            unmatched.append(s["base"])
    return matched, unmatched


def load_mer(mer_path):
    im = Image.open(mer_path).convert("RGBA")
    arr = np.asarray(im).astype(np.float32)
    metalness = arr[:, :, 0]
    emissive = arr[:, :, 1]
    roughness = arr[:, :, 2]
    subsurface = arr[:, :, 3]
    return metalness, emissive, roughness, subsurface


def build_specular(java_base, metalness, emissive, roughness, subsurface, has_subsurface):
    h, w = roughness.shape
    out = np.zeros((h, w, 4), dtype=np.uint8)

    smoothness = np.clip(255 - roughness, 0, 255)
    out[:, :, 0] = smoothness.astype(np.uint8)

    is_metal = metalness >= 128
    metal_id = METAL_OVERRIDES.get(java_base, 230)
    f0 = np.where(is_metal, metal_id, np.clip(metalness, 0, 229))
    out[:, :, 1] = f0.astype(np.uint8)

    if has_subsurface:
        sss = np.where(subsurface > 0, 65 + (subsurface / 255.0) * 190.0, 0)
        out[:, :, 2] = np.clip(sss, 0, 255).astype(np.uint8)
    else:
        out[:, :, 2] = 0

    out[:, :, 3] = np.clip(emissive, 0, 254).astype(np.uint8)
    return Image.fromarray(out, mode="RGBA")


def build_normal(height_path, shape, strength=1.5):
    h, w = shape
    if height_path is not None:
        height_img = Image.open(height_path).convert("L").resize((w, h), Image.NEAREST)
        height = np.asarray(height_img).astype(np.float32) / 255.0
    else:
        height = np.full((h, w), 1.0, dtype=np.float32)

    # Sobel gradient (wrap at edges -- these are tileable block textures).
    hL = np.roll(height, 1, axis=1)
    hR = np.roll(height, -1, axis=1)
    hU = np.roll(height, 1, axis=0)
    hD = np.roll(height, -1, axis=0)
    dx = (hR - hL) * strength
    dy = (hD - hU) * strength

    nx, ny, nz = -dx, -dy, np.ones_like(height)
    length = np.sqrt(nx * nx + ny * ny + nz * nz)
    nx, ny, nz = nx / length, ny / length, nz / length

    out = np.zeros((h, w, 4), dtype=np.uint8)
    out[:, :, 0] = np.clip((nx * 0.5 + 0.5) * 255, 0, 255).astype(np.uint8)
    out[:, :, 1] = np.clip((ny * 0.5 + 0.5) * 255, 0, 255).astype(np.uint8)
    out[:, :, 2] = 255  # AO: no source data, assume unoccluded
    out[:, :, 3] = np.clip(height * 255, 0, 255).astype(np.uint8)
    return Image.fromarray(out, mode="RGBA")


def convert_one(java_base, bset, dry_run=False):
    if bset["mer_path"] is None:
        print(f"  SKIP {java_base}: no MER texture found for Bedrock base {bset['base']!r}")
        return False

    metalness, emissive, roughness, subsurface = load_mer(bset["mer_path"])
    spec = build_specular(java_base, metalness, emissive, roughness, subsurface, bset["has_subsurface"])
    norm = build_normal(bset["height_path"], roughness.shape)

    out_dir = java_dir_for(java_base)
    s_path = os.path.join(out_dir, java_base + "_s.png")
    n_path = os.path.join(out_dir, java_base + "_n.png")
    print(f"  {java_base}: mer={os.path.basename(bset['mer_path'])} "
          f"height={os.path.basename(bset['height_path']) if bset['height_path'] else '(none, flat)'} "
          f"-> {os.path.basename(s_path)}, {os.path.basename(n_path)}")
    if not dry_run:
        spec.save(s_path)
        norm.save(n_path)
    return True


def main(argv):
    matched, unmatched = match_all()

    if not argv or argv[0] == "--list":
        print(f"Matched {len(matched)} Bedrock texture sets to Java textures:")
        for base in sorted(matched):
            print(f"  {base}")
        print(f"\nUnmatched Bedrock bases ({len(unmatched)}) -- no Java texture of that name, "
              f"add to NAME_ALIASES if they should map somewhere:")
        for base in sorted(unmatched):
            print(f"  {base}")
        return

    if argv[0] == "--all":
        n = 0
        for java_base, bset in sorted(matched.items()):
            n += convert_one(java_base, bset)
        print(f"\nConverted {n}/{len(matched)} matched textures.")
        return

    for name in argv:
        if name not in matched:
            print(f"  SKIP {name}: not in matched set (run --list to see matches)")
            continue
        convert_one(name, matched[name])


if __name__ == "__main__":
    main(sys.argv[1:])
