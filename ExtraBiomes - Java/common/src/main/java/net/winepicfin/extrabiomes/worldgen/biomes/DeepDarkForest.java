package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.ARGB;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;

public class DeepDarkForest {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addMossyStoneBlock(biomeBuilder);
        // Bedrock's deep_dark_forest carries both "taiga"+"mega" (old-growth taiga trees) and "roofed" (dark oak) tags at once, so both are added rather than picking just one.
        // Each feature's position here must match its position in the vanilla biome it's borrowed from, or FeatureSorter throws Feature order cycle since it shares one global per-step order across biomes.
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.DARK_FOREST_VEGETATION);
        BiomeDefaultFeatures.addForestFlowers(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addForestGrass(biomeBuilder);
        BiomeDefaultFeatures.addGiantTaigaVegetation(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder, true);
        // Bedrock's 'has_structure_trail_ruins' tag is handled via structure_set/biome tags (see ModBiomeTagProvider), not here.

        // shattered_swamp/swamp_huge_mushroom_feature.json applies to swamp OR roofed-forest tagged biomes
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY);

        // Same sculk vein + deep dark sculk patch vanilla's underground deep_dark biome uses (BiomeDefaultFeatures.addSculk).
        // Sculk vein is a multiface growth that clings to any solid surface it can spread onto, including the trunks of this
        // biome's trees, matching Bedrock's sculk-covered deep dark forest (https://minecraft.wiki/w/Deep_Dark).
        BiomeDefaultFeatures.addSculk(biomeBuilder);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.DEEP_DARK_FOREST.downfall())
                .temperature(BiomeClimateTuning.DEEP_DARK_FOREST.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x000000))
                .setAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 12.0F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(BiomeAppearanceTuning.DEEP_DARK_FOREST.skyColor()))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0x000000))
                .setAttribute(EnvironmentAttributes.FOG_START_DISTANCE, 4.0F)
                .setAttribute(EnvironmentAttributes.FOG_END_DISTANCE, 32.0F)
                .setAttribute(EnvironmentAttributes.SKY_FOG_END_DISTANCE, 32.0F)
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.DEEP_DARK_FOREST.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.DEEP_DARK_FOREST.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.DEEP_DARK_FOREST.grassColor()).build())
                .build();
    }
}
