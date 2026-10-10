package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.ARGB;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.stonepillars.StonePillarsFeature;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.BryceMesaPillarFeatures;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;

public class JunglePillars {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addJungleTrees(biomeBuilder);
        BiomeDefaultFeatures.addJungleGrass(biomeBuilder);
        // boulder: weighted boulder selection (with pebble scatter), local modification step
        // boulder: weighted stick-pile selection, vegetal decoration step (per Bedrock surface_pass ordering)
        // stone_pillars: single pillar structure placed once per chunk (Bedrock feature_rules/stone_pillars.json, first_pass)
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, StonePillarsFeature.STONE_PILLARS_PLACED_KEY);
        // Bedrock layers two independent pillar mechanics here: the hand-authored stone_pillar structures above, and its "mesa" surface builder's own bryce_pillars noise bumps, reconstructed below.
        biomeBuilder.addFeature(GenerationStep.Decoration.RAW_GENERATION, BryceMesaPillarFeatures.STONE_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.JUNGLE_PILLARS.downfall())
                .temperature(BiomeClimateTuning.JUNGLE_PILLARS.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x113290))
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(BiomeAppearanceTuning.JUNGLE_PILLARS.skyColor()))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xC0D8FF))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.JUNGLE_PILLARS.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.JUNGLE_PILLARS.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.JUNGLE_PILLARS.grassColor()).build())
                .build();
    }
}
