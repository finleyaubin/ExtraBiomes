package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.jellycoral.JellyCoralFeatures;

public class JellyfishFields {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        // Ocean biome - no land farm animals. Jellyfish itself is added via the
        // add_spawn_jellyfish biome modifier; these mirror vanilla's warm ocean ambience.
        BiomeDefaultFeatures.warmOceanSpawns(spawnBuilder, 10, 4);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        //BiomeDefaultFeatures.addWarmOceanVegetation(biomeBuilder);

        // bedrock feature_rules/jellycoral.json
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, JellyCoralFeatures.JELLYCORAL_1_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, JellyCoralFeatures.JELLYCORAL_2_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, JellyCoralFeatures.JELLYCORAL_3_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, JellyCoralFeatures.JELLYCORAL_4_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.JELLYFISH_FIELDS.downfall())
                .temperature(BiomeClimateTuning.JELLYFISH_FIELDS.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.JELLYFISH_FIELDS.waterColor())
                        .waterFogColor(0x50D8CE)
                        .skyColor(BiomeAppearanceTuning.JELLYFISH_FIELDS.skyColor())
                        .fogColor(0x8fe0e8)
                        .foliageColorOverride(BiomeAppearanceTuning.JELLYFISH_FIELDS.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.JELLYFISH_FIELDS.grassColor())
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
