"""Shared writers for generated Bedrock worldgen: structure templates, features and feature rules for one biome tag."""
import json
import os

import build_floating_islands as bfi

NAMESPACE = "extrabiomes"


def _format(value, indent):
    pad = " " * indent
    if isinstance(value, dict):
        if not value:
            return "{}"
        items = [f'{pad}    {json.dumps(k)}: {_format(v, indent + 4)}' for k, v in value.items()]
        return "{\n" + ",\n".join(items) + f"\n{pad}}}"
    if isinstance(value, list):
        inline = "[" + ", ".join(json.dumps(v) for v in value) + "]"
        if all(not isinstance(v, (dict, list)) for v in value) and len(inline) + indent <= 80:
            return inline
        items = [f"{pad}    {_format(v, indent + 4)}" for v in value]
        return "[\n" + ",\n".join(items) + f"\n{pad}]"
    return json.dumps(value)


def write_json(directory, name, data):
    """Writes 4-space JSON matching the pack's prettier style."""
    os.makedirs(directory, exist_ok=True)
    with open(os.path.join(directory, f"{name}.json"), "w", encoding="utf-8", newline="\n") as f:
        f.write(_format(data, 0) + "\n")


class FeatureWriter:
    def __init__(self, folder, biome_tag, snap_range=12):
        self.folder = folder
        self.biome_tag = biome_tag
        self.snap_range = snap_range
        self.struct_dir = os.path.join(bfi.BP, "structures", NAMESPACE, folder)
        self.feature_dir = os.path.join(bfi.BP, "features", folder)
        self.rule_dir = os.path.join(bfi.BP, "feature_rules", folder)

    def write_template(self, name, cells):
        """Writes cells (y already relative to the snapped layer's floor) and returns the template's size."""
        xs, ys, zs = zip(*cells)
        x0, z0 = min(xs), min(zs)
        assert min(ys) >= 0, f"{name} has cells below layer 0"
        sx, sy, sz = max(xs) - x0 + 1, max(ys) + 1, max(zs) - z0 + 1
        shifted = {(x - x0, y, z - z0): key for (x, y, z), key in cells.items()}
        bfi.STRUCT_DIR = self.struct_dir
        bfi.write_bedrock_structure(name, sx, sy, sz, shifted, pad=False)
        print(f"{name}: {sx}x{sy}x{sz}, {len(cells)} blocks")

    def feature_id(self, name):
        return f"{NAMESPACE}:{self.folder}/{name}"

    def feature(self, name, feature_type, format_version, body):
        write_json(self.feature_dir, name, {
            "format_version": format_version,
            feature_type: {"description": {"identifier": self.feature_id(name)}, **body},
        })
        return self.feature_id(name)

    def structure_feature(self, name, ground_level, allowlist, extra_constraints=None):
        body = {
            "structure_name": f"{NAMESPACE}:{self.folder}/{name}",
            "facing_direction": "random",
            "rotate_around_center": True,
            "constraints": {"block_intersection": {"block_allowlist": allowlist}, **(extra_constraints or {})},
        }
        if ground_level:
            body["ground_level"] = ground_level
        return self.feature(f"{name}_feature", "minecraft:structure_template_feature", "1.26.20", body)

    def select_feature(self, name, weighted):
        assert all(isinstance(weight, int) and weight > 0 for _, weight in weighted), f"{name} needs positive integer weights"
        return self.feature(name, "minecraft:weighted_random_feature", "1.14.0",
                            {"features": [[member, weight] for member, weight in weighted]})

    def snap_feature(self, name, target, surface):
        return self.feature(name, "minecraft:snap_to_surface_feature", "1.20.30",
                            {"feature_to_snap": target, "vertical_search_range": self.snap_range, "surface": surface})

    def scatter_feature(self, name, target, iterations, spread):
        gaussian = {"distribution": "gaussian", "extent": [-spread, spread]}
        return self.feature(name, "minecraft:scatter_feature", "1.14.0", {
            "iterations": iterations,
            "x": gaussian,
            "y": {"distribution": "gaussian", "extent": [-4, 4]},
            "z": gaussian,
            "coordinate_eval_order": "zyx",
            "places_feature": target,
        })

    def single_block_feature(self, name, block, attach_bottom=None):
        body = {
            "places_block": block,
            "enforce_survivability_rules": True,
            "enforce_placement_rules": True,
            "may_replace": ["minecraft:air"],
        }
        if attach_bottom:
            body["may_attach_to"] = {"bottom": attach_bottom}
        return self.feature(name, "minecraft:single_block_feature", "1.17.0", body)

    def feature_rule(self, name, places, pass_name, iterations, y, scatter_chance=100):
        rule_name = f"{self.biome_tag}_{name}"
        write_json(self.rule_dir, rule_name, {
            "format_version": "1.20.20",
            "minecraft:feature_rules": {
                "description": {"identifier": f"{NAMESPACE}:{rule_name}", "places_feature": places},
                "conditions": {
                    "placement_pass": pass_name,
                    "minecraft:biome_filter": [{"test": "has_biome_tag", "operator": "==", "value": self.biome_tag}],
                },
                "distribution": {
                    "iterations": iterations,
                    "scatter_chance": scatter_chance,
                    "x": {"distribution": "uniform", "extent": [0, 16]},
                    "y": y,
                    "z": {"distribution": "uniform", "extent": [0, 16]},
                },
            },
        })
