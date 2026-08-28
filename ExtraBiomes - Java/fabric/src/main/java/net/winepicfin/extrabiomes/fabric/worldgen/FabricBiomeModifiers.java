package net.winepicfin.extrabiomes.fabric.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.util.ModTags;
import net.winepicfin.extrabiomes.worldgen.MobSpawnWeightTuning;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.winepicfin.extrabiomes.worldgen.features.boulder.BoulderFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;

// Fabric equivalent of forge/.../worldgen/ModBiomeModifiers.java. Forge's datapack-driven
// BiomeModifier registry (ForgeBiomeModifiers.AddFeaturesBiomeModifier/AddSpawnsBiomeModifier,
// built at datagen bootstrap time) has no Fabric equivalent - Fabric API's BiomeModifications is
// code-driven instead, called once from the mod entrypoint, and resolves biome/feature/tag
// references lazily via BiomeSelectionContext, so no BootstapContext/registry lookups are needed
// here at all (simpler than the Forge version, not just a straight port). Must run after
// ModEntities.register() since addSpawn needs already-registered EntityTypes.
public class FabricBiomeModifiers {
    public static void register() {
        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.Biomes.LUSH_MESA),
                GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModPlacedFeatures.LUSH_GRASS_PLACED_KEY);

        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE),
                GenerationStep.Decoration.VEGETAL_DECORATION, UndergroundJungleFeatures.GRASS_FLOOR_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE),
                GenerationStep.Decoration.VEGETAL_DECORATION, UndergroundJungleFeatures.GRASS_FLOOR_UPPER_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE),
                GenerationStep.Decoration.UNDERGROUND_DECORATION, UndergroundJungleFeatures.CAVE_VINE_PLACED_KEY);

        // Registered before the boulder/stick_pile block below - see the long comment there for
        // why the ORDER of these two blocks (not just their content) matters: JungleMarsh.java
        // bakes extrabiomes:swamp_huge_mushroom directly into its own VEGETAL_DECORATION list at
        // biome-registration time (i.e. always before any BiomeModifications run at all), so on
        // vanilla Dark Forest - which gets both swamp_huge_mushroom and select_stick_pile purely
        // via modifiers, in whatever order these calls run - the mushroom modifier must also run
        // before the stick_pile-for-forest one, or the two biomes end up wanting opposite relative
        // orders for the same pair of features and vanilla's FeatureSorter crashes with
        // "Feature order cycle found" the moment a chunk needs both biomes' feature lists at once.
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.MUSHROOM_FIELDS),
                GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.MUSHROOM_FIELDS),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS, MushroomFeatures.MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.DARK_FOREST),
                GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY);

        // Underground badlands terracotta banding (including the near-lava glazed band) is handled
        // by ModSurfaceRules' bandlands()/glazedTerracottaBand() surface rules, not a biome modifier
        // - a Feature-based approach here was both redundant with vanilla's own real terracotta
        // banding mechanism and, being a per-chunk full-volume block scan, too slow ("Can't keep up"
        // warnings during world generation). See ModSurfaceRules.makeRules() javadoc.

        // Bedrock's boulder_placer/stick_pile_placer feature_rules gate on has_biome_tag alone
        // (boulder: plains/forest/jungle, stick_pile: forest/jungle) - see ModBiomeModifiers (forge)
        // for the full rationale. ModTags.Biomes.IS_PLAINS is this mod's own tag since vanilla has
        // no BiomeTags.IS_PLAINS equivalent.
        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.Biomes.IS_PLAINS),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS, BoulderFeatures.SELECT_BOULDER_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_FOREST),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS, BoulderFeatures.SELECT_BOULDER_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS, BoulderFeatures.SELECT_BOULDER_PLACED_KEY);
        // Dark Forest specifically (bisected empirically: IS_FOREST alone reproduces "Feature
        // order cycle found [dark_forest, jungle_marsh]"; the same call with IS_JUNGLE, or with
        // Dark Forest excluded from IS_FOREST, does not) ends up in a feature-order cycle with
        // jungle_marsh once select_stick_pile links the two into the same global ordering graph -
        // vanilla's FeatureSorter computes one global topological order across every biome's
        // VEGETAL_DECORATION list at once, so this isn't necessarily a *direct* disagreement
        // between these two biomes' own lists, but some multi-hop contradiction elsewhere in the
        // huge vanilla+mod feature graph that this new shared edge happens to close into a cycle.
        // Reordering (e.g. the swamp_huge_mushroom-before-stick_pile fix elsewhere in this method)
        // did not resolve it, so rather than continue hunting a knock-on chain through vanilla's
        // own biome list, Dark Forest specifically is excluded from getting stick piles - every
        // other IS_FOREST biome (forest, flower_forest, birch_forest, etc.) still gets them.
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_FOREST).and(BiomeSelectors.excludeByKey(Biomes.DARK_FOREST)),
                GenerationStep.Decoration.VEGETAL_DECORATION, BoulderFeatures.SELECT_STICK_PILE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE),
                GenerationStep.Decoration.VEGETAL_DECORATION, BoulderFeatures.SELECT_STICK_PILE_PLACED_KEY);

        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), MobCategory.MONSTER,
                ModEntities.GIANT_TORTOISE.get(), MobSpawnWeightTuning.GIANT_TORTOISE, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), MobCategory.WATER_AMBIENT,
                ModEntities.PIRANHA.get(), MobSpawnWeightTuning.PIRANHA_JUNGLE,
                MobSpawnWeightTuning.PIRANHA_JUNGLE_MIN_GROUP, MobSpawnWeightTuning.PIRANHA_JUNGLE_MAX_GROUP);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.SWAMP), MobCategory.WATER_AMBIENT,
                ModEntities.PIRANHA.get(), MobSpawnWeightTuning.PIRANHA_SWAMP, 2, 5);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), MobCategory.CREATURE,
                ModEntities.TREEFROG.get(), MobSpawnWeightTuning.TREEFROG_JUNGLE, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.IS_WETLAND), MobCategory.CREATURE,
                ModEntities.TREEFROG.get(), MobSpawnWeightTuning.TREEFROG_SWAMP, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.SPAWNS_HOPPLESHROOM), MobCategory.CREATURE,
                ModEntities.HOPPLESHROOM.get(), MobSpawnWeightTuning.HOPPLESHROOM, 1, 5);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.SPAWNS_JELLYFISH), MobCategory.WATER_CREATURE,
                ModEntities.JELLYFISH.get(), MobSpawnWeightTuning.JELLYFISH, 3, 8);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_BEACH), MobCategory.WATER_CREATURE,
                ModEntities.JELLYFISH.get(), MobSpawnWeightTuning.JELLYFISH_BEACH, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.MONSTER,
                ModEntities.HARPY.get(), MobSpawnWeightTuning.HARPY, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.CREATURE,
                ModEntities.WORM.get(), MobSpawnWeightTuning.WORM, 1, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_BEACH), MobCategory.CREATURE,
                ModEntities.PUCKOO.get(), MobSpawnWeightTuning.PUCKOO_BEACH,
                MobSpawnWeightTuning.PUCKOO_BEACH_MIN_GROUP, MobSpawnWeightTuning.PUCKOO_BEACH_MAX_GROUP);
    }
}
