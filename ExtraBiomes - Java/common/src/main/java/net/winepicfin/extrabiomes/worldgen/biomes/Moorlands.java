package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.moorland.MoorlandFeatures;

public class Moorlands {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 2, 4));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        // boulder subsystem: boulders (LOCAL_MODIFICATIONS, matches vanilla's forest_rock step) and stick piles (VEGETAL_DECORATION)

        // addPlainVegetation() already adds flower_plains alongside trees_plains/patch_grass_plain,
        // matching vanilla's plains biomes exactly. A second addDefaultFlowers() call used to sit here
        // too, forcing minecraft:flower_default directly next to minecraft:patch_grass_plain - a pair
        // Terralith/Oh The Biomes We've Gone and vanilla's own windswept_savanna order in conflicting
        // directions through their own biomes, so no placement of that call here is safe.
        BiomeDefaultFeatures.addPlainVegetation(biomeBuilder);

        // boulder subsystem: stick piles (Bedrock surface_pass)

        // moorland subsystem: tall grass field (Bedrock surface_pass)
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MoorlandFeatures.MOORLAND_DOUBLE_TALL_GRASS_PLACED_KEY);

        // moorland subsystem: waterlily surface fixup (Bedrock surface_pass)
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MoorlandFeatures.MOORLAND_WATERLILY_PLACED_KEY);

        // the_netherlands subsystem: windmill generation moved off this biome-features list entirely -
        // it's now a real jigsaw Structure (WindmillStructures.WINDMILL_PLAINS_KEY) that attaches to this
        // biome via its own Structure.biomes() HolderSet instead of an addFeature call here.

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.MOORLANDS.downfall())
                .temperature(BiomeClimateTuning.MOORLANDS.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x113290)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, BiomeAppearanceTuning.MOORLANDS.skyColor())
                .setAttribute(EnvironmentAttributes.FOG_COLOR, 0xC0D8FF)
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.MOORLANDS.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.MOORLANDS.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.MOORLANDS.grassColor()).build())
                .build();
    }
}
