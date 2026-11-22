"""Generate the Bedrock larch wood set by cloning the palm set and swapping in the Java larch textures and names.

Usage:
    python tools/build_larch_wood.py

Palm is the template because it already has every piece Bedrock supports (blocks, items, recipes, loot, boats,
sapling). Larch leaves get nine pre-tinted texture variants selected by the `extrabiomes:shade` block state, because
Bedrock custom blocks cannot sample a colour map the way Java's position-based tint does.
Re-runnable: overwrites its own output.
"""
import json
import os
import re
import shutil

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.join(HERE, "..")
PACKS = os.path.join(ROOT, "ExtraBiomes - Bedrock", "packs")
BP = os.path.join(PACKS, "BP")
RP = os.path.join(PACKS, "RP")
JAVA_ASSETS = os.environ.get("JAVA_ASSETS") or os.path.join(
    ROOT, "ExtraBiomes - Java", "common", "src", "main", "resources", "assets", "extrabiomes")
JAVA_TEXTURES = os.path.join(JAVA_ASSETS, "textures")
JAVA_LANG = os.path.join(JAVA_ASSETS, "lang")

NOT_CLONED = ("features/tree/", "feature_rules/tree/", "structures/", "/textures/blocks/", "trading/")
GREENNESS_STEPS = (0.2, 0.5, 0.9)
SHADE_STEPS = (0.1, 0.5, 0.9)
DEFAULT_SHADE = 1
LEAF_SHADE_COUNT = len(GREENNESS_STEPS) * len(SHADE_STEPS)
LEAF_MAP_COLOR = "#d9902a"
SAPLING_MAP_COLOR = "#5f7a3a"

BLOCK_TEXTURES = {
    "log_larch": "larch_log",
    "log_larch_top": "larch_log_top",
    "planks_larch": "larch_planks",
    "sapling_larch": "larch_sapling",
    "stripped_larch_log": "stripped_larch_log",
    "stripped_larch_log_top": "stripped_larch_log_top",
    "larch_door_bottom": "larch_door_bottom",
    "larch_door_top": "larch_door_top",
    "larch_trapdoor": "larch_trapdoor",
}
ITEM_TEXTURES = {
    "boat_larch": "item/boat/boat_larch",
    "boat_larch_chest": "item/boat/boat_larch_chest",
}
ENTITY_TEXTURES = {
    "boat/boat_larch": "entity/boat/boat_larch",
    "boat/chest_boat_larch": "entity/chest_boat/boat_larch",
}


def larchify(text):
    return text.replace("palm", "larch").replace("Palm", "Larch").replace("PALM", "LARCH")


def write_text(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(text)


def _format(value, indent):
    pad = " " * indent
    if isinstance(value, dict):
        if not value:
            return "{}"
        items = [f"{pad}    {json.dumps(k, ensure_ascii=False)}: {_format(v, indent + 4)}" for k, v in value.items()]
        return "{\n" + ",\n".join(items) + f"\n{pad}}}"
    if isinstance(value, list):
        inline = "[" + ", ".join(json.dumps(v, ensure_ascii=False) for v in value) + "]"
        if all(not isinstance(v, (dict, list)) for v in value) and len(inline) + indent <= 80:
            return inline
        items = [f"{pad}    {_format(v, indent + 4)}" for v in value]
        return "[\n" + ",\n".join(items) + f"\n{pad}]"
    return json.dumps(value, ensure_ascii=False)


def write_json(path, data):
    write_text(path, _format(data, 0) + "\n")


def clone_palm_files():
    cloned = []
    for pack in (BP, RP):
        for folder, _, names in os.walk(pack):
            for name in names:
                path = os.path.join(folder, name)
                rel = os.path.relpath(path, PACKS).replace(os.sep, "/")
                if "palm" not in rel.lower() or any(skip in "/" + rel for skip in NOT_CLONED):
                    continue
                if not name.endswith((".json", ".js")):
                    continue
                with open(path, encoding="utf-8") as f:
                    content = f.read()
                write_text(os.path.join(PACKS, larchify(rel)), larchify(content))
                cloned.append(larchify(rel))
    return cloned


def tint_for(greenness, shade):
    colormap = Image.open(os.path.join(JAVA_TEXTURES, "colormap", "larch_leaves.png")).convert("RGB")
    size = colormap.width
    return colormap.getpixel((round(greenness * (size - 1)), round(shade * (size - 1))))


def leaf_texture_name(shade):
    return f"extrabiomes_larch_leaves_{shade}"


def bake_leaf_variants():
    """Java larch_leaves.png is greyscale and tinted at runtime, so multiply it by nine colour-map samples."""
    grey = Image.open(os.path.join(JAVA_TEXTURES, "block", "wood", "larch", "larch_leaves.png")).convert("RGBA")
    folder = os.path.join(RP, "textures", "blocks", "larch wood")
    os.makedirs(folder, exist_ok=True)
    for greenness_index, greenness in enumerate(GREENNESS_STEPS):
        for shade_index, shade in enumerate(SHADE_STEPS):
            red, green, blue = tint_for(greenness, shade)
            tinted = grey.copy()
            tinted.putdata([(p[0] * red // 255, p[1] * green // 255, p[2] * blue // 255, p[3]) for p in grey.getdata()])
            tinted.save(os.path.join(folder, f"leaves_larch_{greenness_index * len(SHADE_STEPS) + shade_index}.png"))


def write_leaves_block():
    path = os.path.join(BP, "blocks", "larch_wood", "larch_leaves.json")
    with open(path, encoding="utf-8") as f:
        block = json.load(f)
    inner = block["minecraft:block"]
    shades = [DEFAULT_SHADE] + [s for s in range(LEAF_SHADE_COUNT) if s != DEFAULT_SHADE]
    inner["description"]["states"]["extrabiomes:shade"] = shades
    inner["components"]["minecraft:map_color"] = LEAF_MAP_COLOR
    leaf_material = inner["components"]["minecraft:material_instances"]["*"]
    inner["components"]["minecraft:material_instances"]["*"] = {**leaf_material, "texture": leaf_texture_name(DEFAULT_SHADE)}
    inner["permutations"] = [
        {
            "condition": f"q.block_state('extrabiomes:shade') == {shade}",
            "components": {"minecraft:material_instances": {"*": {**leaf_material, "texture": leaf_texture_name(shade)}}},
        }
        for shade in range(LEAF_SHADE_COUNT) if shade != DEFAULT_SHADE
    ]
    write_json(path, block)


def write_sapling_block_color():
    path = os.path.join(BP, "blocks", "larch_wood", "larch_sapling.json")
    with open(path, encoding="utf-8") as f:
        text = f.read()
    write_text(path, re.sub(r'"minecraft:map_color": "#[0-9a-fA-F]+"', f'"minecraft:map_color": "{SAPLING_MAP_COLOR}"', text))


def copy_java_textures():
    block_folder = os.path.join(RP, "textures", "blocks", "larch wood")
    os.makedirs(block_folder, exist_ok=True)
    for bedrock_name, java_name in BLOCK_TEXTURES.items():
        shutil.copyfile(os.path.join(JAVA_TEXTURES, "block", "wood", "larch", f"{java_name}.png"),
                        os.path.join(block_folder, f"{bedrock_name}.png"))
    shutil.copyfile(os.path.join(JAVA_TEXTURES, "item", "wood", "larch_door.png"), os.path.join(block_folder, "larch_door.png"))
    for bedrock_name, java_path in ITEM_TEXTURES.items():
        shutil.copyfile(os.path.join(JAVA_TEXTURES, java_path + ".png"), os.path.join(RP, "textures", "items", f"{bedrock_name}.png"))
    for bedrock_path, java_path in ENTITY_TEXTURES.items():
        shutil.copyfile(os.path.join(JAVA_TEXTURES, java_path + ".png"), os.path.join(RP, "textures", "entity", f"{bedrock_path}.png"))


def clone_registry_entries(path, section=None):
    with open(path, encoding="utf-8") as f:
        data = json.load(f)
    target = data[section] if section else data
    for key in [k for k in target if "palm" in k]:
        target[larchify(key)] = json.loads(larchify(json.dumps(target[key])))
    write_json(path, data)
    return target


def register_leaf_textures():
    path = os.path.join(RP, "textures", "terrain_texture.json")
    with open(path, encoding="utf-8") as f:
        data = json.load(f)
    texture_data = data["texture_data"]
    texture_data.pop("extrabiomes_larch_leaves", None)
    for shade in range(LEAF_SHADE_COUNT):
        texture_data[leaf_texture_name(shade)] = {"textures": [f"textures/blocks/larch wood/leaves_larch_{shade}"]}
    write_json(path, data)


def register_blocks_json():
    path = os.path.join(RP, "blocks.json")
    with open(path, encoding="utf-8") as f:
        data = json.load(f)
    data["extrabiomes:larch_leaves"] = {"sound": "grass", "isotropic": True, "textures": leaf_texture_name(DEFAULT_SHADE)}
    write_json(path, data)


def java_larch_names(lang_file):
    """Maps Bedrock lang keys to the Java larch translations for one language."""
    with open(os.path.join(JAVA_LANG, lang_file), encoding="utf-8") as f:
        java = json.load(f)
    names = {}
    for key, value in java.items():
        kind, _, rest = key.partition(".extrabiomes.")
        if "larch" not in rest:
            continue
        if kind == "block":
            names[f"tile.extrabiomes:{rest}.name"] = value
        elif kind == "item":
            names[f"item.extrabiomes:{rest}"] = value
        elif kind == "entity":
            names[f"entity.extrabiomes:{rest}.name"] = value
    return names


BEDROCK_ONLY_LARCH_KEYS = {
    "tile.extrabiomes:larch_sapling_block.name": "item.extrabiomes:larch_sapling",
}


def update_lang_files():
    for name in sorted(os.listdir(os.path.join(RP, "texts"))):
        if not name.endswith(".lang"):
            continue
        path = os.path.join(RP, "texts", name)
        with open(path, encoding="utf-8") as f:
            lines = f.read().splitlines()
        java_names = java_larch_names(name.lower().replace(".lang", ".json"))
        kept = [line for line in lines if "larch" not in line.lower()]
        added = []
        for line in lines:
            key, sep, value = line.partition("=")
            if "palm" not in key:
                continue
            larch_key = larchify(key)
            if key.startswith("action.hint"):
                added.append(f"{larch_key}={value}")
                continue
            source_key = BEDROCK_ONLY_LARCH_KEYS.get(larch_key, larch_key)
            if source_key not in java_names:
                raise KeyError(f"{name}: no Java translation for {source_key}")
            added.append(f"{larch_key}={java_names[source_key]}")
        write_text(path, "\n".join(kept).rstrip("\n") + "\n" + "\n".join(added) + "\n")


def main():
    cloned = clone_palm_files()
    print(f"cloned {len(cloned)} palm files")
    write_leaves_block()
    write_sapling_block_color()
    bake_leaf_variants()
    copy_java_textures()
    clone_registry_entries(os.path.join(RP, "blocks.json"))
    clone_registry_entries(os.path.join(RP, "textures", "item_texture.json"), "texture_data")
    clone_registry_entries(os.path.join(RP, "textures", "terrain_texture.json"), "texture_data")
    register_blocks_json()
    register_leaf_textures()
    update_lang_files()


if __name__ == "__main__":
    main()
