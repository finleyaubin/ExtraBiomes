package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.mesa.MesaFeatures;
import net.winepicfin.extrabiomes.worldgen.features.glacier.GlacierFeatures;

public class ColdMesa {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 4, 1, 2));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addExtraGold(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MesaFeatures.SELECT_TERRACOTTA_PLACED_KEY);
        BiomeDefaultFeatures.addBadlandExtraVegetation(biomeBuilder);
        BiomeDefaultFeatures.addBlueIce(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, GlacierFeatures.SELECT_SNOW_DRIFT_PLACED_KEY);
        // Bedrock's snow_accumulation on this biome is already handled globally as surface freezing.

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.COLD_MESA.downfall())
                .temperature(BiomeClimateTuning.COLD_MESA.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.COLD_MESA.waterColor())
                        .waterFogColor(0x113290)
                        .skyColor(BiomeAppearanceTuning.COLD_MESA.skyColor())
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(BiomeAppearanceTuning.COLD_MESA.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.COLD_MESA.grassColor())
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
