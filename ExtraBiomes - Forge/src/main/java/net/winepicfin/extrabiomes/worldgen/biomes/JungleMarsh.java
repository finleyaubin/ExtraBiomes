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
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;

public class JungleMarsh {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 6, 2, 5));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addJungleTrees(biomeBuilder);
        BiomeDefaultFeatures.addSwampVegetation(biomeBuilder);
        BiomeDefaultFeatures.addJungleVines(biomeBuilder);
        // boulder: weighted boulder selection (with pebble scatter), local modification step
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, BoulderFeatures.SELECT_BOULDER_PLACED_KEY);
        // underground_jungle: cave grass floors + cave vines
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, UndergroundJungleFeatures.GRASS_FLOOR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, UndergroundJungleFeatures.GRASS_FLOOR_UPPER_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, UndergroundJungleFeatures.CAVE_VINE_PLACED_KEY);
        // boulder: weighted stick-pile selection, vegetal decoration step (per Bedrock surface_pass ordering)
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, BoulderFeatures.SELECT_STICK_PILE_PLACED_KEY);
        // shattered_swamp: swamp huge mushroom selection
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.9f)
                .temperature(0.95f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x2a6830)
                        .waterFogColor(0x2a6830)
                        .skyColor(0x4a7a3a)
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(0x386020)
                        .grassColorOverride(0x487030)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
