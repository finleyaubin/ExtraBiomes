package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.boulder.BoulderFeatures;
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
        // boulder: weighted stick-pile selection, vegetal decoration step (per Bedrock surface_pass ordering)
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, BoulderFeatures.SELECT_STICK_PILE_PLACED_KEY);
        // shattered_swamp: swamp huge mushroom selection
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.JUNGLE_MARSH.downfall())
                .temperature(BiomeClimateTuning.JUNGLE_MARSH.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.JUNGLE_MARSH.waterColor())
                        .waterFogColor(0x2a6830)
                        .skyColor(BiomeAppearanceTuning.JUNGLE_MARSH.skyColor())
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(BiomeAppearanceTuning.JUNGLE_MARSH.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.JUNGLE_MARSH.grassColor())
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
