package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsOreFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsWaterFeature;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsWheatFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsWindmillFeature;

public class TheNetherlandsMutated {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.BEE, 4, 2, 3));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NetherlandsOreFeatures.COAL_ORE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NetherlandsOreFeatures.COPPER_ORE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NetherlandsOreFeatures.DIAMOND_ORE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NetherlandsOreFeatures.EMERALD_ORE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NetherlandsOreFeatures.GOLD_ORE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NetherlandsOreFeatures.IRON_ORE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NetherlandsOreFeatures.LAPIS_ORE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NetherlandsOreFeatures.QUARTZ_ORE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NetherlandsOreFeatures.REDSTONE_ORE_PLACED_KEY);
        BiomeDefaultFeatures.addPlainVegetation(biomeBuilder);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        // 'mutated' variant gets wheat + the canal/water feature instead of tulips (tulips are base TheNetherlands only)
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsWheatFeatures.WHEAT_FLOOR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsWheatFeatures.HYDRATION_WATER_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsWaterFeature.WATER_FEATURE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, NetherlandsWindmillFeature.WINDMILL_NETHERLANDS_PLACED_KEY);
        // No custom cave carver on Java - see TheNetherlands/ModSurfaceRules for why.
        // Bedrock's top material here is plain dirt (no grass) rather than grass_block, reflected in the surface rules.

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.THE_NETHERLANDS_MUTATED.downfall())
                .temperature(BiomeClimateTuning.THE_NETHERLANDS_MUTATED.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.THE_NETHERLANDS_MUTATED.waterColor())
                        .waterFogColor(0x113290)
                        .skyColor(BiomeAppearanceTuning.THE_NETHERLANDS_MUTATED.skyColor())
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(BiomeAppearanceTuning.THE_NETHERLANDS_MUTATED.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.THE_NETHERLANDS_MUTATED.grassColor())
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
