package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.larchforest.LarchForestFeatures;

/**
 * Autumn larch forest on the mountain slopes vanilla gives to Grove (issue #388). Java-only: there is
 * no Bedrock counterpart yet.
 */
public class LarchForest {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(EntityTypes.WOLF, MobCategory.CREATURE, 8, UniformInt.of(4, 4));
        spawnBuilder.addSpawn(EntityTypes.RABBIT, MobCategory.CREATURE, 4, UniformInt.of(2, 3));
        spawnBuilder.addSpawn(EntityTypes.FOX, MobCategory.CREATURE, 8, UniformInt.of(2, 4));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.LAKES, LarchForestFeatures.KETTLE_LAKE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, LarchForestFeatures.MOSSY_ROCK_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, LarchForestFeatures.ERRATIC_PLACED_KEY);
        BiomeDefaultFeatures.addFerns(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
        // Trees stay ahead of flower_default: BiomeRegistrationParityTest explains the feature-order cycle that guards against.
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, LarchForestFeatures.TREES_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, LarchForestFeatures.YOUNG_TREES_PLACED_KEY);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addTaigaGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder, true);
        BiomeDefaultFeatures.addRareBerryBushes(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, LarchForestFeatures.SHORT_DRY_GRASS_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, LarchForestFeatures.TALL_DRY_GRASS_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, LarchForestFeatures.SNOW_PATCH_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.LARCH_FOREST.downfall())
                .temperature(BiomeClimateTuning.LARCH_FOREST.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x113290))
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(BiomeAppearanceTuning.LARCH_FOREST.skyColor()))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xC0D8FF))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.LARCH_FOREST.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.LARCH_FOREST.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.LARCH_FOREST.grassColor()).build())
                .build();
    }
}
