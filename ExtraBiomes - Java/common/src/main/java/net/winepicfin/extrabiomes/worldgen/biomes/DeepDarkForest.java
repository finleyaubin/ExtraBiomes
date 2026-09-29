package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;

public class DeepDarkForest {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
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
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder);
        // Bedrock's 'has_structure_trail_ruins' tag is handled via structure_set/biome tags (see ModBiomeTagProvider), not here.

        // swamp_huge_mushroom is delivered by the ADD_DARK_FOREST_HUGE_MUSHROOMS modifier via ModTags.Biomes.GETS_SWAMP_HUGE_MUSHROOMS,
        // not baked in here: see that tag's javadoc for why a baked-in copy makes a feature order cycle with mods that append vegetal features.

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
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.DEEP_DARK_FOREST.waterColor())
                        .waterFogColor(0x050533)
                        .skyColor(BiomeAppearanceTuning.DEEP_DARK_FOREST.skyColor())
                        .fogColor(0x0d0d17)
                        .foliageColorOverride(BiomeAppearanceTuning.DEEP_DARK_FOREST.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.DEEP_DARK_FOREST.grassColor())
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
