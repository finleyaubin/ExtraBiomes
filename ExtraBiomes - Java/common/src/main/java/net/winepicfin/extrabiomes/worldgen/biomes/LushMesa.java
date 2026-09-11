package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;

public class LushMesa {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addJungleTrees(biomeBuilder);
        BiomeDefaultFeatures.addJungleGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder);
        // boulder: weighted boulder selection (with pebble scatter), local modification step
        BiomeDefaultFeatures.addJungleVines(biomeBuilder);
        BiomeDefaultFeatures.addJungleMelons(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.LUSH_GRASS_PLACED_KEY);
        // boulder: weighted stick-pile selection, vegetal decoration step (per Bedrock surface_pass ordering)

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.LUSH_MESA.downfall())
                .temperature(BiomeClimateTuning.LUSH_MESA.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.LUSH_MESA.waterColor())
                        .waterFogColor(0x113290)
                        .skyColor(BiomeAppearanceTuning.LUSH_MESA.skyColor())
                        .fogColor(0xf8e6b4)
                        .foliageColorOverride(BiomeAppearanceTuning.LUSH_MESA.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.LUSH_MESA.grassColor())
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
