package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class TiagaSpikes {

    public Biome Register(BootstapContext<Biome> context)
    {


        // Get access to placed features registry
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);

        // Get vanilla ICE_SPIKE and ICE_PATCH features
        Holder<PlacedFeature> iceSpike = placed.getOrThrow(
                ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation("minecraft", "ice_spike"))
        );
        Holder<PlacedFeature> icePatch = placed.getOrThrow(
                ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation("minecraft", "ice_patch"))
        );
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 5, 2, 4));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);


        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        // RAW_GENERATION

        // LAKES

        // LOCAL_MODIFICATIONS

        // UNDERGROUND_STRUCTURES

        // SURFACE_STRUCTURES
        System.out.println("Adding ice spikes to TiagaSpikes");
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, iceSpike);
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, icePatch);
        // STRONGHOLDS

        // UNDERGROUND_ORES
        BiomeDefaultFeatures.addMossyStoneBlock(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
        // UNDERGROUND_DECORATION

        // FLUID_SPRINGS

        // VEGETAL_DECORATION
        BiomeDefaultFeatures.addBlueIce(biomeBuilder);
        BiomeDefaultFeatures.addTaigaTrees(biomeBuilder);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder);




        // TOP_LAYER_MODIFICATION


        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.5f)
                .temperature(0f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        //colours are ARGB not RGBA
                        .waterColor(0x5920441)
                        .waterFogColor(0x113290)
                        .skyColor(0x8103167)
                        .fogColor(0x84631263)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
