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
import net.winepicfin.extrabiomes.worldgen.features.boulder.BoulderFeatures;
import net.winepicfin.extrabiomes.worldgen.features.moorland.MoorlandFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsWindmillFeature;

public class Moorlands {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 8, 2, 4));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);
        ModSpawns.swampAndMoorland(spawnBuilder);
        ModSpawns.harpy(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        // boulder subsystem: boulders (LOCAL_MODIFICATIONS, matches vanilla's forest_rock step) and stick piles (VEGETAL_DECORATION)
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, BoulderFeatures.SELECT_BOULDER_PLACED_KEY);

        // moorland subsystem: podzol surface conversion (Bedrock after_surface_pass) -> LOCAL_MODIFICATIONS
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, MoorlandFeatures.MOORLAND_PODZOL_PLACED_KEY);

        BiomeDefaultFeatures.addPlainVegetation(biomeBuilder);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);

        // boulder subsystem: stick piles (Bedrock surface_pass)
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, BoulderFeatures.SELECT_STICK_PILE_PLACED_KEY);

        // moorland subsystem: select_grass_feature aggregate members (Bedrock surface_pass)
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MoorlandFeatures.MOORLAND_TALL_GRASS_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MoorlandFeatures.MOORLAND_DOUBLE_TALL_GRASS_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MoorlandFeatures.MOORLAND_SHORT_DRY_GRASS_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MoorlandFeatures.MOORLAND_TALL_DRY_GRASS_PLACED_KEY);

        // moorland subsystem: waterlily surface fixup (Bedrock surface_pass)
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MoorlandFeatures.MOORLAND_WATERLILY_PLACED_KEY);

        // the_netherlands subsystem: root-level windmill_feature.json also biome-tags "plains", which Moorlands matches
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, NetherlandsWindmillFeature.WINDMILL_PLAINS_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.5f)
                .temperature(0.5f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x2846ea)
                        .waterFogColor(0x113290)
                        .skyColor(0xb0c8e0)
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(0x28962a)
                        .grassColorOverride(0xbcbc2b)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
