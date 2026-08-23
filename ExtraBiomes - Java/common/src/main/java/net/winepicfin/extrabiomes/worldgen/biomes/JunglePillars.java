package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.stonepillars.StonePillarsFeature;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.BryceMesaPillarFeatures;

public class JunglePillars {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addJungleTrees(biomeBuilder);
        BiomeDefaultFeatures.addJungleGrass(biomeBuilder);
        // boulder: weighted boulder selection (with pebble scatter), local modification step
        // boulder: weighted stick-pile selection, vegetal decoration step (per Bedrock surface_pass ordering)
        // stone_pillars: single pillar structure placed once per chunk (Bedrock feature_rules/stone_pillars.json, first_pass)
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, StonePillarsFeature.STONE_PILLARS_PLACED_KEY);
        // Bedrock layers TWO independent pillar mechanics on jungle_pillars: the hand-authored
        // stone_pillar_{1,2,3} structures above, AND its "minecraft:mesa" surface builder's own
        // "bryce_pillars": true terrain-noise bumps (packs/BP/biomes/jungle_pillars.biome.json) -
        // this reconstructs that second mechanic.
        biomeBuilder.addFeature(GenerationStep.Decoration.RAW_GENERATION, BryceMesaPillarFeatures.STONE_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.JUNGLE_PILLARS.downfall())
                .temperature(BiomeClimateTuning.JUNGLE_PILLARS.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.JUNGLE_PILLARS.waterColor())
                        .waterFogColor(0x113290)
                        .skyColor(BiomeAppearanceTuning.JUNGLE_PILLARS.skyColor())
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(BiomeAppearanceTuning.JUNGLE_PILLARS.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.JUNGLE_PILLARS.grassColor())
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
