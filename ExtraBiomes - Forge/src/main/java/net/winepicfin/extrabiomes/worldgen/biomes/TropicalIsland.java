package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.winepicfin.extrabiomes.worldgen.features.moss.MossFeatures;
import net.winepicfin.extrabiomes.worldgen.features.tropical.TropicalIslandFeatures;

public class TropicalIsland {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 5, 1, 2));
        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.TURTLE, 5, 2, 3));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);
        ModSpawns.jungle(spawnBuilder);
        ModSpawns.harpy(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        //BiomeDefaultFeatures.addWarmOceanVegetation(biomeBuilder);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.PALM_PLACED_KEY);
        // island_grass_floor_feature.json (sand -> grass floor, no vegetation - see class docs)
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TropicalIslandFeatures.GRASS_FLOOR_PLACED_KEY);
        // moss/growth chain (moorlands_scatter_tall_grass_feature.json, moss/scatter_carpet_feature.json, jungle_bush)
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MossFeatures.TALL_GRASS_SCATTER_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MossFeatures.MOSS_CARPET_SCATTER_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MossFeatures.JUNGLE_BUSH_PLACED_KEY);
        // tropical_melon_feature.json
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TropicalIslandFeatures.MELON_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.9f)
                .temperature(1.0f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x28c8e0)
                        .waterFogColor(0x50D8CE)
                        .skyColor(0x9fe8f0)
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(0x40b020)
                        .grassColorOverride(0x60c840)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
