package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;

public class LushMesa {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addJungleTrees(biomeBuilder);
        // Matches vanilla's own jungle() convention (trees, then warm flowers, then jungle grass).
        // addDefaultFlowers's flower_default used to sit here instead: Terralith and Oh The Biomes
        // We've Gone each order flower_default relative to patch_grass_jungle in opposite directions
        // through their own biomes, so no placement of flower_default next to patch_grass_jungle is
        // safe - flower_warm avoids the shared node entirely and matches vanilla's real jungle order.
        BiomeDefaultFeatures.addWarmFlowers(biomeBuilder);
        BiomeDefaultFeatures.addJungleGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder, true);
        // boulder: weighted boulder selection (with pebble scatter), local modification step
        BiomeDefaultFeatures.addJungleVines(biomeBuilder);
        BiomeDefaultFeatures.addJungleMelons(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.LUSH_GRASS_PLACED_KEY);
        // boulder: weighted stick-pile selection, vegetal decoration step (per Bedrock surface_pass ordering)

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.LUSH_MESA.downfall())
                .temperature(BiomeClimateTuning.LUSH_MESA.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x1B9ED8)
                .setAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 60.0F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, BiomeAppearanceTuning.LUSH_MESA.skyColor())
                .setAttribute(EnvironmentAttributes.FOG_COLOR, 0xf8e6b4)
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.LUSH_MESA.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.LUSH_MESA.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.LUSH_MESA.grassColor()).build())
                .build();
    }
}
