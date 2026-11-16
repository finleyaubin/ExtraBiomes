package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.BryceMesaPillarFeatures;
import net.minecraft.world.attribute.EnvironmentAttributes;

public class DesertBryce {
    private static final int DUSTY_HAZE_FOG_COLOR = 0xD8CFB4;

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.desertSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDesertVegetation(biomeBuilder);
        // Reconstructs the pre-1.18 mesa surface builder's noise-gated pillar bumps.
        biomeBuilder.addFeature(GenerationStep.Decoration.RAW_GENERATION, BryceMesaPillarFeatures.SAND_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, BryceMesaPillarFeatures.SHORT_MUD_STALAGMITE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, BryceMesaPillarFeatures.TALL_MUD_STALAGMITE_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .downfall(BiomeClimateTuning.DESERT_BRYCE.downfall())
                .temperature(BiomeClimateTuning.DESERT_BRYCE.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x113290)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, BiomeAppearanceTuning.DESERT_BRYCE.skyColor())
                .setAttribute(EnvironmentAttributes.FOG_COLOR, DUSTY_HAZE_FOG_COLOR)
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DESERT)))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.DESERT_BRYCE.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.DESERT_BRYCE.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.DESERT_BRYCE.grassColor()).build())
                .build();
    }
}
