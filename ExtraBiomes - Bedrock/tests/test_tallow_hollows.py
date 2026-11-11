"""Static validation of the Bedrock add-on's Tallow Hollows content.

Run from the repository root:
    python3 -m unittest discover -s "ExtraBiomes - Bedrock/tests" -v

The indexes below walk the whole pack so references can be resolved anywhere, but only Tallow Hollows content is
asserted on (except JSON parsing), so pre-existing quirks elsewhere in the pack do not fail this suite.
"""
import json
import os
import sys
import unittest

HERE = os.path.dirname(os.path.abspath(__file__))
PACKS = os.path.join(HERE, "..", "packs")
BP = os.path.join(PACKS, "BP")
RP = os.path.join(PACKS, "RP")
sys.path.insert(0, os.path.join(HERE, "..", "..", "tools"))

from mcstructure import load as load_structure  # noqa: E402

BIOME_ID = "extrabiomes:tallow_hollows"
BIOME_TAG = "tallow_hollows"
SHAPE_PARENTS = {"extrabiomes:tallowstone": "extrabiomes:tallowstone", "extrabiomes:tallowstone_brick": "extrabiomes:tallowstone_bricks"}
SHAPE_RECIPES = {"slab": (["###"], 6), "stairs": (["#  ", "## ", "###"], 4), "wall": (["###", "###"], 6)}
SHAPE_BLOCKS = [f"{stem}_{shape}" for stem in SHAPE_PARENTS for shape in SHAPE_RECIPES]
BLOCKS = ["extrabiomes:tallowstone", "extrabiomes:tallowstone_bricks", "extrabiomes:wax_drip",
          "extrabiomes:ever_wick"] + SHAPE_BLOCKS
FUEL_SECONDS = {"extrabiomes:tallow_lump": 20}
ITEMS = ["extrabiomes:tallow_lump"]
FEATURE_RULES = [f"extrabiomes:tallow_hollows_{name}" for name in (
    "floor_shell", "ceiling_shell", "pillars", "forgotten_shelves", "wick_blooms", "candle_clusters", "wax_drips",
    "honeycomb_veins")]
LANGUAGE = "en_US"
CLIMATE = {"temperature": 0.7, "downfall": 0.3}
CLIENT_COLOURS = {
    "minecraft:water_appearance": ("surface_color", "#D9A441"),
    "minecraft:foliage_appearance": ("color", "#B8A04A"),
    "minecraft:grass_appearance": ("color", "#C4AE5A"),
}
FOG_COLOUR = "#3A2410"
TEXTURE_EXTENSIONS = (".png", ".tga")


def strip_comments(text):
    """Remove // and /* */ comments outside strings, as Bedrock's JSON reader allows them."""
    out = []
    i, in_string = 0, False
    while i < len(text):
        c = text[i]
        if in_string:
            out.append(c)
            if c == "\\":
                out.append(text[i + 1])
                i += 1
            elif c == '"':
                in_string = False
        elif c == '"':
            in_string = True
            out.append(c)
        elif text.startswith("//", i):
            end = text.find("\n", i)
            i = len(text) if end == -1 else end
            continue
        elif text.startswith("/*", i):
            end = text.find("*/", i + 2)
            if end == -1:
                raise ValueError("unterminated block comment")
            i = end + 2
            continue
        else:
            out.append(c)
        i += 1
    return "".join(out)


def read_json(path):
    with open(path, encoding="utf-8-sig") as f:
        return json.loads(strip_comments(f.read()))


def json_files(root):
    for directory, _, files in os.walk(root):
        for name in sorted(files):
            if name.endswith(".json"):
                yield os.path.join(directory, name)


def body_of(document):
    """The single top-level object whose key starts with `minecraft:` (feature, block, item, recipe...)."""
    keys = [k for k in document if k.startswith("minecraft:")]
    if len(keys) != 1:
        raise ValueError(f"expected one minecraft: key, found {keys}")
    return keys[0], document[keys[0]]


def index_by_identifier(root):
    index = {}
    for path in json_files(root):
        kind, body = body_of(read_json(path))
        index[body["description"]["identifier"]] = (kind, body, path)
    return index


def lang_keys(language):
    keys = set()
    with open(os.path.join(RP, "texts", f"{language}.lang"), encoding="utf-8-sig") as f:
        for line in f:
            if "=" in line and not line.startswith("##"):
                keys.add(line.split("=", 1)[0].strip())
    return keys


def texture_on_disk(relative_path):
    return any(os.path.isfile(os.path.join(RP, relative_path + ext)) for ext in TEXTURE_EXTENSIONS)


def texture_paths(entry):
    textures = entry["textures"]
    if isinstance(textures, str):
        return [textures]
    if isinstance(textures, dict):
        return [variation["path"] for variation in textures["variations"]]
    return list(textures)


def block_name(value):
    return value if isinstance(value, str) else value["name"]


def component_sets(block_body):
    """The block's base components followed by each permutation's components."""
    return [block_body["components"]] + [p["components"] for p in block_body.get("permutations", [])]


def geometry_identifiers(block_body):
    ids = set()
    for components in component_sets(block_body):
        geometry = components.get("minecraft:geometry")
        if geometry is not None:
            ids.add(geometry if isinstance(geometry, str) else geometry["identifier"])
    return ids


def model_geometry_identifiers():
    ids = set()
    for path in json_files(os.path.join(RP, "models")):
        for geometry in read_json(path).get("minecraft:geometry", []):
            ids.add(geometry["description"]["identifier"])
    return ids


def structure_palette(structure_name):
    namespace, path = structure_name.split(":", 1)
    file_path = os.path.join(BP, "structures", namespace, path + ".mcstructure")
    _, root = load_structure(file_path)
    palette = root.value["structure"].value["palette"].value["default"].value["block_palette"].value
    return {entry.value["name"].value for entry in palette}


class FeatureGraph:
    """Resolves feature references and the blocks they place, walking every feature file in the pack."""

    def __init__(self):
        self.features = index_by_identifier(os.path.join(BP, "features"))

    def children(self, kind, body):
        refs = []
        for key in ("places_feature", "feature_to_snap", "vegetation_feature"):
            if key in body:
                refs.append(body[key])
        for member in body.get("features", []):
            refs.append(member if isinstance(member, str) else member[0])
        return refs

    def placed_blocks(self, kind, body):
        if kind == "minecraft:single_block_feature":
            return {block_name(body["places_block"])}
        if kind == "minecraft:ore_feature":
            return {block_name(rule["places_block"]) for rule in body["replace_rules"]}
        if kind == "minecraft:vegetation_patch_feature":
            return {block_name(body["ground_block"])}
        if kind == "minecraft:structure_template_feature":
            return structure_palette(body["structure_name"])
        return set()

    def resolve(self, root_id):
        """Returns (features reached, missing feature ids, blocks placed)."""
        reached, missing, blocks = set(), set(), set()
        pending = [root_id]
        while pending:
            feature_id = pending.pop()
            if feature_id in reached or feature_id in missing:
                continue
            if feature_id not in self.features:
                missing.add(feature_id)
                continue
            reached.add(feature_id)
            kind, body, _ = self.features[feature_id]
            blocks |= self.placed_blocks(kind, body)
            pending.extend(self.children(kind, body))
        return reached, missing, blocks


class JsonParsingTest(unittest.TestCase):
    def test_every_pack_json_file_parses(self):
        failures = []
        for path in json_files(PACKS):
            try:
                read_json(path)
            except (ValueError, UnicodeDecodeError) as error:
                failures.append(f"{os.path.relpath(path, PACKS)}: {error}")
        self.assertEqual(failures, [])


class BiomeTest(unittest.TestCase):
    def setUp(self):
        self.biome = read_json(os.path.join(BP, "biomes", "tallow_hollows.biome.json"))["minecraft:biome"]
        self.client = read_json(os.path.join(RP, "biomes", "tallow_hollows.client_biome.json"))["minecraft:client_biome"]

    def test_identifiers_match(self):
        self.assertEqual(self.biome["description"]["identifier"], BIOME_ID)
        self.assertEqual(self.client["description"]["identifier"], BIOME_ID)

    def test_climate(self):
        self.assertEqual(self.biome["components"]["minecraft:climate"], CLIMATE)

    def test_replaces_a_third_of_dripstone_caves(self):
        replacements = self.biome["components"]["minecraft:replace_biomes"]["replacements"]
        self.assertEqual(replacements, [{
            "dimension": "minecraft:overworld",
            "targets": ["minecraft:dripstone_caves"],
            "amount": 0.33,
            "noise_frequency_scale": 0.2,
        }])

    def test_tagged_for_its_features_but_not_as_dripstone_caves(self):
        tags = self.biome["components"]["minecraft:tags"]["tags"]
        self.assertIn(BIOME_TAG, tags)
        self.assertNotIn("dripstone_caves", tags)

    def test_client_colours(self):
        components = self.client["components"]
        for component, (field, colour) in CLIENT_COLOURS.items():
            self.assertEqual(components[component][field].upper(), colour, component)

    def test_fog_exists_with_the_warm_colour(self):
        fog_id = self.client["components"]["minecraft:fog_appearance"]["fog_identifier"]
        fogs = {}
        for path in json_files(os.path.join(RP, "fogs")):
            settings = read_json(path)["minecraft:fog_settings"]
            fogs[settings["description"]["identifier"]] = settings
        self.assertTrue(fog_id in fogs, f"{fog_id} missing from RP/fogs")
        self.assertEqual(fogs[fog_id]["distance"]["air"]["fog_color"].upper(), FOG_COLOUR)


class WorldgenTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.rules = index_by_identifier(os.path.join(BP, "feature_rules"))
        cls.graph = FeatureGraph()
        cls.defined_blocks = set(index_by_identifier(os.path.join(BP, "blocks")))

    def tallow_rules(self):
        return {rule_id: body for rule_id, (_, body, _) in self.rules.items()
                if BIOME_TAG in json.dumps(body["conditions"]["minecraft:biome_filter"])}

    def test_every_tallow_feature_rule_exists_and_is_scoped_to_the_biome(self):
        self.assertEqual(sorted(self.tallow_rules()), sorted(FEATURE_RULES))

    def test_rules_resolve_to_existing_features(self):
        for rule_id, body in self.tallow_rules().items():
            _, missing, _ = self.graph.resolve(body["description"]["places_feature"])
            self.assertEqual(missing, set(), rule_id)

    def test_placed_blocks_are_vanilla_or_defined_in_the_pack(self):
        for rule_id, body in self.tallow_rules().items():
            _, _, blocks = self.graph.resolve(body["description"]["places_feature"])
            unknown = {b for b in blocks if not b.startswith("minecraft:") and b not in self.defined_blocks}
            self.assertEqual(unknown, set(), rule_id)

    def blocks_placed_by(self, rule_suffix):
        body = self.rules[f"extrabiomes:tallow_hollows_{rule_suffix}"][1]
        return self.graph.resolve(body["description"]["places_feature"])[2]

    def test_each_rule_places_its_signature_block(self):
        expectations = {
            "floor_shell": "extrabiomes:tallowstone",
            "ceiling_shell": "extrabiomes:tallowstone",
            "pillars": "extrabiomes:ever_wick",
            "forgotten_shelves": "minecraft:bookshelf",
            "wick_blooms": "extrabiomes:ever_wick",
            "candle_clusters": "minecraft:candle",
            "wax_drips": "extrabiomes:wax_drip",
            "honeycomb_veins": "minecraft:honeycomb_block",
        }
        for rule_suffix, block in expectations.items():
            self.assertIn(block, self.blocks_placed_by(rule_suffix), rule_suffix)

    def test_forgotten_shelves_place_bookshelves(self):
        rule = self.rules["extrabiomes:tallow_hollows_forgotten_shelves"][1]
        reached, missing, blocks = self.graph.resolve(rule["description"]["places_feature"])
        self.assertEqual(missing, set())
        templates = [f for f in reached if self.graph.features[f][0] == "minecraft:structure_template_feature"]
        self.assertGreaterEqual(len(templates), 2)
        for template in templates:
            structure = self.graph.features[template][1]["structure_name"]
            self.assertIn("minecraft:bookshelf", structure_palette(structure), template)

    def test_no_dripstone_is_placed(self):
        for rule_id, body in self.tallow_rules().items():
            reached, _, blocks = self.graph.resolve(body["description"]["places_feature"])
            self.assertFalse({b for b in blocks if "dripstone" in b and b != "minecraft:dripstone_block"}, rule_id)
            self.assertFalse({f for f in reached if "dripstone" in f}, rule_id)


class BlockAndItemAssetsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.blocks = index_by_identifier(os.path.join(BP, "blocks"))
        cls.items = index_by_identifier(os.path.join(BP, "items"))
        cls.blocks_json = read_json(os.path.join(RP, "blocks.json"))
        cls.terrain = read_json(os.path.join(RP, "textures", "terrain_texture.json"))["texture_data"]
        cls.item_textures = read_json(os.path.join(RP, "textures", "item_texture.json"))["texture_data"]
        cls.lang = lang_keys(LANGUAGE)
        cls.models = model_geometry_identifiers()

    def test_blocks_have_sound_textures_and_names(self):
        for block_id in BLOCKS:
            self.assertTrue(block_id in self.blocks, f"{block_id} missing from BP/blocks")
            self.assertTrue(block_id in self.blocks_json, f"{block_id} missing from blocks.json")
            components = self.blocks[block_id][1]["components"]
            for instance in components["minecraft:material_instances"].values():
                texture = instance["texture"]
                self.assertTrue(texture in self.terrain, f"{block_id}: {texture} missing from terrain_texture.json")
                for path in texture_paths(self.terrain[texture]):
                    self.assertTrue(texture_on_disk(path), f"{block_id}: {path}")
            self.assertTrue(f"tile.{block_id}.name" in self.lang, f"{block_id} missing from {LANGUAGE}.lang")

    def test_items_have_icons_and_names(self):
        for item_id in ITEMS:
            self.assertTrue(item_id in self.items, f"{item_id} missing from BP/items")
            icon = self.items[item_id][1]["components"]["minecraft:icon"]
            icon = icon if isinstance(icon, str) else icon["texture"]
            self.assertTrue(icon in self.item_textures, f"{item_id}: {icon} missing from item_texture.json")
            for path in texture_paths(self.item_textures[icon]):
                self.assertTrue(texture_on_disk(path), f"{item_id}: {path}")
            self.assertTrue(f"item.{item_id}" in self.lang, f"{item_id} missing from {LANGUAGE}.lang")

    def test_custom_geometry_exists_in_the_resource_pack(self):
        for block_id in BLOCKS:
            custom = {g for g in geometry_identifiers(self.blocks[block_id][1]) if not g.startswith("minecraft:")}
            self.assertEqual(custom - self.models, set(), block_id)

    def test_loot_tables_referenced_by_blocks_exist(self):
        for block_id in BLOCKS:
            for components in component_sets(self.blocks[block_id][1]):
                loot = components.get("minecraft:loot")
                if loot is not None:
                    self.assertTrue(os.path.isfile(os.path.join(BP, loot)), f"{block_id}: {loot}")

    def test_double_slabs_drop_two_of_themselves(self):
        for block_id in (b for b in SHAPE_BLOCKS if b.endswith("_slab")):
            [loot] = [c["minecraft:loot"] for c in component_sets(self.blocks[block_id][1]) if "minecraft:loot" in c]
            [entry] = [e for pool in read_json(os.path.join(BP, loot))["pools"] for e in pool["entries"]]
            self.assertEqual(entry["name"], block_id)
            self.assertEqual([f["count"] for f in entry["functions"] if f["function"] == "set_count"], [2])

    def test_shapes_match_their_parent_block(self):
        for stem, parent_id in SHAPE_PARENTS.items():
            parent = self.blocks[parent_id][1]["components"]
            parent_texture = parent["minecraft:material_instances"]["*"]["texture"]
            for shape in SHAPE_RECIPES:
                components = self.blocks[f"{stem}_{shape}"][1]["components"]
                self.assertEqual(components["minecraft:destructible_by_mining"],
                                 parent["minecraft:destructible_by_mining"], f"{stem}_{shape}")
                self.assertEqual(components["minecraft:destructible_by_explosion"],
                                 parent["minecraft:destructible_by_explosion"], f"{stem}_{shape}")
                self.assertIn("tag:minecraft:is_pickaxe_item_destructible", components, f"{stem}_{shape}")
                self.assertEqual({i["texture"] for i in components["minecraft:material_instances"].values()},
                                 {parent_texture}, f"{stem}_{shape}")
                self.assertEqual(self.blocks_json[f"{stem}_{shape}"], self.blocks_json[parent_id])

    def test_fuel_durations(self):
        for item_id, seconds in FUEL_SECONDS.items():
            self.assertEqual(self.items[item_id][1]["components"]["minecraft:fuel"], {"duration": seconds})

    def test_wax_drip_drops_a_tallow_lump(self):
        loot = self.blocks["extrabiomes:wax_drip"][1]["components"]["minecraft:loot"]
        entries = [e for pool in read_json(os.path.join(BP, loot))["pools"] for e in pool["entries"]]
        self.assertEqual([e["name"] for e in entries], ["extrabiomes:tallow_lump"])


class RecipeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        recipes = index_by_identifier(os.path.join(BP, "recipes"))
        cls.shaped = {rid: body for rid, (kind, body, _) in recipes.items() if kind == "minecraft:recipe_shaped"}
        cls.stonecutter = {rid: body for rid, (kind, body, _) in recipes.items()
                           if kind == "minecraft:recipe_shapeless" and body["tags"] == ["stonecutter"]}
        cls.known_ids = (set(index_by_identifier(os.path.join(BP, "blocks")))
                         | set(index_by_identifier(os.path.join(BP, "items"))))

    def result_of(self, body):
        result = body["result"]
        return result[0] if isinstance(result, list) else result

    def recipes_making(self, item_id, ingredient):
        return [body for body in self.shaped.values()
                if self.result_of(body)["item"] == item_id
                and ingredient in {key["item"] for key in body["key"].values()}]

    def test_candle_from_string_above_tallow_lump(self):
        [recipe] = self.recipes_making("minecraft:candle", "extrabiomes:tallow_lump")
        symbols = {key["item"]: symbol for symbol, key in recipe["key"].items()}
        self.assertEqual(recipe["pattern"], [symbols["minecraft:string"], symbols["extrabiomes:tallow_lump"]])
        self.assertEqual(self.result_of(recipe)["count"], 1)

    def test_four_lumps_make_one_tallowstone(self):
        [recipe] = self.recipes_making("extrabiomes:tallowstone", "extrabiomes:tallow_lump")
        self.assertEqual(recipe["pattern"], ["##", "##"])
        self.assertEqual(self.result_of(recipe)["count"], 1)

    def test_four_tallowstone_make_four_bricks(self):
        [recipe] = self.recipes_making("extrabiomes:tallowstone_bricks", "extrabiomes:tallowstone")
        self.assertEqual(recipe["pattern"], ["##", "##"])
        self.assertEqual(self.result_of(recipe)["count"], 4)

    def test_shapes_craft_from_their_parent(self):
        for stem, parent_id in SHAPE_PARENTS.items():
            for shape, (pattern, count) in SHAPE_RECIPES.items():
                [recipe] = self.recipes_making(f"{stem}_{shape}", parent_id)
                self.assertEqual(recipe["pattern"], pattern, f"{stem}_{shape}")
                self.assertEqual(self.result_of(recipe)["count"], count, f"{stem}_{shape}")

    def test_shapes_have_stonecutter_recipes_like_black_sandstone(self):
        cut = {(body["ingredients"][0]["item"], self.result_of(body)["item"]): self.result_of(body)["count"]
               for body in self.stonecutter.values()}
        for stem, parent_id in SHAPE_PARENTS.items():
            for shape in SHAPE_RECIPES:
                expected = 2 if shape == "slab" else 1
                self.assertEqual(cut.get((parent_id, f"{stem}_{shape}")), expected, f"{stem}_{shape}")

    def test_tallow_recipes_reference_known_items(self):
        for recipe_id, body in self.shaped.items():
            ids = {key["item"] for key in body["key"].values()} | {self.result_of(body)["item"]}
            if not any(i in BLOCKS + ITEMS for i in ids):
                continue
            unknown = {i for i in ids if not i.startswith("minecraft:") and i not in self.known_ids}
            self.assertEqual(unknown, set(), recipe_id)


if __name__ == "__main__":
    unittest.main()
