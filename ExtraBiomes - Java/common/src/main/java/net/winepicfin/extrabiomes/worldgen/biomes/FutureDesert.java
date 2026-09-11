package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.future.FutureTreeFeatures;

public class FutureDesert {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDesertVegetation(biomeBuilder);
        // Bedrock's top/mid material is an unspecified 'concretepowder' (defaults to white); the surface rule below assumes LIGHT_GRAY_CONCRETE_POWDER to match the RP's cool grey tint.
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FutureTreeFeatures.FUTURE_TREE_2_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FutureTreeFeatures.FUTURE_TREE_3_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .downfall(BiomeClimateTuning.FUTURE_DESERT.downfall())
                .temperature(BiomeClimateTuning.FUTURE_DESERT.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.FUTURE_DESERT.waterColor())
                        .waterFogColor(0x113290)
                        .skyColor(BiomeAppearanceTuning.FUTURE_DESERT.skyColor())
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(BiomeAppearanceTuning.FUTURE_DESERT.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.FUTURE_DESERT.grassColor()).build())
                .build();
    }
}
