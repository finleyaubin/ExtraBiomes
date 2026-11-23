"""Generate the Desert Bryce ground patches and packed mud stalagmites for Bedrock.

Usage:
    python tools/build_desert_bryce.py

Ports Java's packed mud and sandstone ground patches (ModSurfaceRules) and short and tall mud stalagmites
(BryceMesaPillarFeatures). The mesa surface builder ignores surface_material_adjustments, so the patches are painted
onto the top sand block column by column. Each stalagmite is a template, gated by placing its bottom block first so it
only grows out of packed mud. Re-runnable: overwrites its own output.
"""
import build_floating_islands as bfi
from bedrock_features import FeatureWriter
from mcstructure import T_byte, T_str

BIOME_TAG = "desert_bryce"
WRITER = FeatureWriter(BIOME_TAG, BIOME_TAG, snap_range=12)
SURFACE_Y = "query.heightmap(variable.worldx, variable.worldz)"
SHORT_ATTEMPTS_PER_CHUNK = 16
TALL_ATTEMPTS_PER_CHUNK = 8
ABOVE_SURFACE_Y = "query.above_top_solid(variable.worldx, variable.worldz)"
PATCH_NOISE = "query.noise(variable.worldx / 16, variable.worldz / 16)"
# Noise bands from Java's patchySandOverSandstone call for Desert Bryce.
GROUND_PATCHES = {"packed_mud": (-0.35, -0.15), "sandstone": (0.3, 0.6)}

# Bottom to top, one list per equally weighted variant.
SHORT_STALAGMITES = [["tip"], ["frustum", "tip"]]
TALL_STALAGMITES = [["base"] + ["middle"] * middles + ["frustum", "tip"] for middles in range(3)]

for thickness in ("tip", "frustum", "middle", "base"):
    bfi.BLOCKS[f"dripstone_{thickness}"] = ("minecraft:pointed_dripstone", {
        "dripstone_thickness": T_str(thickness), "hanging": T_byte(0)})


def stalagmite(name, layers):
    WRITER.write_template(name, {(0, y, 0): f"dripstone_{thickness}" for y, thickness in enumerate(layers)})
    column = WRITER.structure_feature(name, None, ["minecraft:air", "minecraft:pointed_dripstone"])
    bottom = WRITER.single_block_feature(
        f"{name}_bottom_feature",
        {"name": "minecraft:pointed_dripstone", "states": {"dripstone_thickness": layers[0], "hanging": False}},
        ["minecraft:packed_mud"])
    return WRITER.feature(f"{name}_on_mud_feature", "minecraft:aggregate_feature", "1.14.0",
                          {"early_out": "first_failure", "features": [bottom, column]})


def build_ground_patches():
    for block, (low, high) in GROUND_PATCHES.items():
        patch = WRITER.feature(f"{block}_patch_feature", "minecraft:single_block_feature", "1.17.0", {
            "places_block": f"minecraft:{block}",
            "enforce_survivability_rules": False,
            "enforce_placement_rules": False,
            "may_replace": ["minecraft:sand"],
        })
        # Outside the band the rule aims at the air above the surface, where the sand-only replace fails.
        in_band = f"({PATCH_NOISE} >= {low} && {PATCH_NOISE} < {high})"
        WRITER.feature_rule(f"{block}_patches", patch, "surface_pass", None,
                            f"{ABOVE_SURFACE_Y} - ({in_band} ? 1 : 0)", every_column=True)


def build_stalagmites(size, variants, attempts):
    columns = [(stalagmite(f"{size}_stalagmite_{n + 1}", layers), 1) for n, layers in enumerate(variants)]
    snapped = WRITER.snap_feature(f"{size}_stalagmite_snap_feature",
                                  WRITER.select_feature(f"select_{size}_stalagmite_feature", columns), "floor")
    WRITER.feature_rule(f"{size}_stalagmites", snapped, "after_surface_pass", attempts, SURFACE_Y)


if __name__ == "__main__":
    build_ground_patches()
    build_stalagmites("short", SHORT_STALAGMITES, SHORT_ATTEMPTS_PER_CHUNK)
    build_stalagmites("tall", TALL_STALAGMITES, TALL_ATTEMPTS_PER_CHUNK)
    print("done")
