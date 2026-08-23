package net.winepicfin.extrabiomes.fabric.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.util.ModTags;
import net.winepicfin.extrabiomes.worldgen.MobSpawnWeightTuning;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;
import net.winepicfin.extrabiomes.worldgen.features.boulder.BoulderFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;
import net.winepicfin.extrabiomes.util.ModTags;

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
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_FOREST),
                GenerationStep.Decoration.VEGETAL_DECORATION, BoulderFeatures.SELECT_STICK_PILE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE),
                GenerationStep.Decoration.VEGETAL_DECORATION, BoulderFeatures.SELECT_STICK_PILE_PLACED_KEY);

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.MUSHROOM_FIELDS),
                GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.MUSHROOM_FIELDS),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS, MushroomFeatures.MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.DARK_FOREST),
                GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY);

        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), MobCategory.MONSTER,
                ModEntities.GIANT_TORTOISE.get(), MobSpawnWeightTuning.GIANT_TORTOISE, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), MobCategory.WATER_AMBIENT,
                ModEntities.PIRANHA.get(), MobSpawnWeightTuning.PIRANHA_JUNGLE,
                MobSpawnWeightTuning.PIRANHA_JUNGLE_MIN_GROUP, MobSpawnWeightTuning.PIRANHA_JUNGLE_MAX_GROUP);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.MANGROVE_SWAMP, ModBiomes.SHATTERED_SWAMP), MobCategory.WATER_AMBIENT,
                ModEntities.PIRANHA.get(), MobSpawnWeightTuning.PIRANHA_SWAMP, 2, 5);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), MobCategory.CREATURE,
                ModEntities.TREEFROG.get(), MobSpawnWeightTuning.TREEFROG_JUNGLE, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP, ModBiomes.SHATTERED_SWAMP, ModBiomes.MOORLANDS), MobCategory.CREATURE,
                ModEntities.TREEFROG.get(), MobSpawnWeightTuning.TREEFROG_SWAMP, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.MUSHROOM_FIELDS, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST), MobCategory.CREATURE,
                ModEntities.HOPPLESHROOM.get(), MobSpawnWeightTuning.HOPPLESHROOM, 1, 5);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(ModBiomes.JELLYFISH_FIELDS), MobCategory.WATER_CREATURE,
                ModEntities.JELLYFISH.get(), MobSpawnWeightTuning.JELLYFISH, 3, 8);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_BEACH), MobCategory.WATER_CREATURE,
                ModEntities.JELLYFISH.get(), MobSpawnWeightTuning.JELLYFISH_BEACH, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.CREATURE,
                ModEntities.HARPY.get(), MobSpawnWeightTuning.HARPY, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.CREATURE,
                ModEntities.WORM.get(), MobSpawnWeightTuning.WORM, 1, 3);
    }
}
