package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
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
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;

public class DeepDarkForest {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addMossyStoneBlock(biomeBuilder);
        // Bedrock's deep_dark_forest carries both the "taiga"+"mega" tags (large/old-growth taiga
        // trees) and the "roofed" tag (dark oak trees) at once - both are added here rather than
        // picking just one, matching that hybrid tag set.
        //
        // FeatureSorter builds one global per-step feature order across every biome that shares
        // a placed feature, so a feature's position here must stay consistent with its position
        // in whichever vanilla biome it's borrowed from (dark_forest for DARK_FOREST_VEGETATION,
        // old-growth taiga for TREES_OLD_GROWTH_SPRUCE_TAIGA/addGiantTaigaVegetation) or two
        // biomes disagreeing on the order produces an IllegalStateException: Feature order cycle.
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
        // NOTE: bedrock tags include 'has_structure_trail_ruins' - Java Trail Ruins structure placement
        // is handled via structure_set/biome tags (see ModBiomeTagProvider), not here.

        // shattered_swamp/swamp_huge_mushroom_feature.json applies to swamp OR roofed-forest tagged biomes
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY);

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
