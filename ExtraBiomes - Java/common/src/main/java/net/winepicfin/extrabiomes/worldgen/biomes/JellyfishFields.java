package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.ARGB;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.jellycoral.JellyCoralFeatures;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;

public class JellyfishFields {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        // Jellyfish is added via the add_spawn_jellyfish biome modifier; no vanilla water spawns here so they can't crowd it out.
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

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
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x02C7D5))
                .setAttribute(EnvironmentAttributes.WATER_FOG_START_DISTANCE, 30.0F)
                .setAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 90.0F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(BiomeAppearanceTuning.JELLYFISH_FIELDS.skyColor()))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0x8fe0e8))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.JELLYFISH_FIELDS.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.JELLYFISH_FIELDS.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.JELLYFISH_FIELDS.grassColor()).build())
                .build();
    }
}
