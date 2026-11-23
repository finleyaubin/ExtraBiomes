#!/usr/bin/env python3
"""Convert Bedrock RTX PBR texture sets (color + metalness/emissive/roughness[/subsurface]
+ optional heightmap, described by each *.texture_set.json) into LabPBR-format textures
for the Java resource pack, so Iris/Oculus shaders pick up matching materials.

Requires Pillow + numpy (pip install pillow numpy). Run with any python3:
    python3 tools/pbr2java.py --list                 # show Bedrock/Java name matches
    python3 tools/pbr2java.py black_sandstone_top     # convert one texture (test case)
    python3 tools/pbr2java.py --all                   # convert every matched texture
    add --vision to any mode to let an Ollama vision model assign each colour's height (tools/vision_heights.py)

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
BEDROCK_RP = os.path.join(REPO_ROOT, "ExtraBiomes - Bedrock", "packs", "RP", "textures")
BEDROCK_BLOCKS = os.path.join(BEDROCK_RP, "blocks")
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


def java_color_path(java_base):
    return os.path.join(java_dir_for(java_base), java_base + ".png")


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


HEIGHT_LEVELS = 6
HEIGHT_FLOOR = 0.25
USE_VISION = False


def _shift(a, step, axis, tileable):
    if tileable:
        return np.roll(a, step, axis)
    edge = np.take(a, [0 if step > 0 else -1], axis)
    body = np.take(a, range(a.shape[axis] - 1) if step > 0 else range(1, a.shape[axis]), axis)
    return np.concatenate([edge, body] if step > 0 else [body, edge], axis)


def _tent_blur(a, tileable=True):
    for axis in (0, 1):
        a = (_shift(a, 1, axis, tileable) + 2 * a + _shift(a, -1, axis, tileable)) / 4
    return a


def clean_height(color_path, height_path=None, tileable=True, levels=HEIGHT_LEVELS):
    """Denoised 0-1 height: Bedrock heightmap (or colour luminance when there is none),
    posterized to a few plateaus, then blurred so edges stay soft."""
    if USE_VISION:
        from vision_heights import level_map
        h = level_map(color_path)
        if h.max() - h.min() < 1e-3:
            return np.ones_like(h)
        return HEIGHT_FLOOR + (1 - HEIGHT_FLOOR) * _tent_blur(h, tileable)
    color = Image.open(color_path).convert("RGBA")
    if height_path is not None:
        src = np.asarray(Image.open(height_path).convert("L").resize(color.size, Image.NEAREST), dtype=np.float32) / 255
    else:
        rgba = np.asarray(color, dtype=np.float32) / 255
        src = (0.299 * rgba[:, :, 0] + 0.587 * rgba[:, :, 1] + 0.114 * rgba[:, :, 2]) * (rgba[:, :, 3] > 0.5)
    h = src
    span = h.max() - h.min()
    if span < 1e-3:
        return np.ones_like(h)
    h = np.round((h - h.min()) / span * (levels - 1)) / (levels - 1)
    return HEIGHT_FLOOR + (1 - HEIGHT_FLOOR) * _tent_blur(h, tileable)


def build_normal(height, strength=1.5, tileable=True):
    h, w = height.shape

    hL, hR = _shift(height, 1, 1, tileable), _shift(height, -1, 1, tileable)
    hU, hD = _shift(height, 1, 0, tileable), _shift(height, -1, 0, tileable)
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
    norm = build_normal(clean_height(java_color_path(java_base), bset["height_path"]))

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


SKIP_NAME = _re.compile(r"(_mer|_sign|_hanging_sign)$")


def derive_missing_java_normals():
    """Write _n for Java textures that have none, deriving height from the colour."""
    n = 0
    for root, _dirs, files in os.walk(JAVA_BLOCKS):
        for f in sorted(files):
            base = f[:-4]
            if not f.endswith(".png") or base.endswith(("_n", "_s")) or SKIP_NAME.search(base):
                continue
            if os.path.exists(os.path.join(root, base + "_n.png")):
                continue
            build_normal(clean_height(os.path.join(root, f))).save(os.path.join(root, base + "_n.png"))
            print(f"  {base}_n.png (from colour)")
            n += 1
    print(f"\nDerived {n} normals.")


ENTITY_LEVELS = 4
ENTITY_STRENGTH = 1.0


def derive_entity_normals(root):
    """Write _n for every entity texture under root that has none (UV layouts, so no edge wrapping)."""
    n = 0
    for dirpath, _dirs, files in os.walk(root):
        for f in sorted(files):
            base = f[:-4]
            if not f.endswith(".png") or base.endswith(("_n", "_s")) or os.path.exists(os.path.join(dirpath, base + "_n.png")):
                continue
            height = clean_height(os.path.join(dirpath, f), tileable=False, levels=ENTITY_LEVELS)
            build_normal(height, ENTITY_STRENGTH, tileable=False).save(os.path.join(dirpath, base + "_n.png"))
            n += 1
    print(f"Derived {n} entity normals.")


def write_bedrock_entity_heightmaps():
    """Add a heightmap and texture set beside every Bedrock entity texture that has none."""
    root = os.path.join(BEDROCK_RP, "entity")
    n = 0
    for dirpath, _dirs, files in os.walk(root):
        for f in sorted(files):
            base = f[:-4]
            if not f.endswith(".png") or base.endswith(("_heightmap", "_mer")) or os.path.exists(os.path.join(dirpath, base + ".texture_set.json")):
                continue
            save_heightmap(clean_height(os.path.join(dirpath, f), tileable=False, levels=ENTITY_LEVELS),
                           os.path.join(dirpath, base + "_heightmap.png"))
            with open(os.path.join(dirpath, base + ".texture_set.json"), "w", encoding="utf-8") as fh:
                json.dump({"format_version": "1.21.30",
                           "minecraft:texture_set": {"color": base, "heightmap": base + "_heightmap"}}, fh, indent=4)
                fh.write("\n")
            n += 1
    print(f"Added {n} entity heightmaps.")


def write_bedrock_heightmaps():
    """Replace every Bedrock heightmap with the cleaned one; add one (and a texture set) where missing."""
    for root, _dirs, files in os.walk(BEDROCK_BLOCKS):
        for f in sorted(files):
            if not f.endswith(".texture_set.json"):
                continue
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8-sig") as fh:
                data = json.load(fh)["minecraft:texture_set"]
            color = os.path.join(root, data["color"] + ".png")
            height = _first_existing(root, data["heightmap"], [("", ".png")]) if "heightmap" in data else None
            out = os.path.join(root, data.get("heightmap", data["color"] + "_heightmap") + ".png")
            save_heightmap(clean_height(color, height), out)
            print(f"  {os.path.relpath(out, BEDROCK_BLOCKS)}")
    for root, _dirs, files in os.walk(BEDROCK_BLOCKS):
        for f in sorted(files):
            base = f[:-4]
            if not f.endswith(".png") or base.endswith(("_mer", "_heightmap", "_height", "_normal")) or base == "air":
                continue
            if os.path.exists(os.path.join(root, base + ".texture_set.json")):
                continue
            if "_mer" in base or base.endswith("_door"):
                continue
            save_heightmap(clean_height(os.path.join(root, f)), os.path.join(root, base + "_heightmap.png"))
            with open(os.path.join(root, base + ".texture_set.json"), "w", encoding="utf-8") as fh:
                json.dump({"format_version": "1.21.30",
                           "minecraft:texture_set": {"color": base, "heightmap": base + "_heightmap"}}, fh, indent=4)
                fh.write("\n")
            print(f"  NEW {base}_heightmap.png + texture set")


def save_heightmap(height, path):
    Image.fromarray(np.round(height * 255).astype(np.uint8), mode="L").save(path)


def main(argv):
    global USE_VISION
    if "--vision" in argv:
        USE_VISION = True
        argv = [a for a in argv if a != "--vision"]
    if argv and argv[0] == "--java-missing":
        return derive_missing_java_normals()
    if argv and argv[0] == "--java-entities":
        return derive_entity_normals(os.path.join(os.path.dirname(JAVA_BLOCKS), "entity"))
    if argv and argv[0] == "--bedrock-entities":
        return write_bedrock_entity_heightmaps()
    if argv and argv[0] == "--bedrock-heights":
        return write_bedrock_heightmaps()

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
