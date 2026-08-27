package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsOreFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsTulipFeatures;

public class TheNetherlands {

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
        // Despite Bedrock's 'nether'/'nether_wastes' spawn-category tags, this biome generates in the OVERWORLD and is themed after the real-world Netherlands (tulips, windmills, wheat, canals).
        // Base (non-mutated) TheNetherlands gets tulip fields; TheNetherlandsMutated gets wheat/canal instead.
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsTulipFeatures.ORANGE_TULIP_FLOOR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsTulipFeatures.PINK_TULIP_FLOOR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsTulipFeatures.RED_TULIP_FLOOR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsTulipFeatures.WHITE_TULIP_FLOOR_PLACED_KEY);
        // Windmill generation moved off this biome-features list entirely - it's now a real jigsaw
        // Structure (see WindmillStructures), which attaches to biomes via its own Structure.biomes()
        // HolderSet rather than a per-biome addFeature call.
        // No custom cave carver on Java: the netherrack band (see ModSurfaceRules) is capped low enough that the vanilla carver already reaches plain stone before bedrock, so Bedrock's netherrack-aware carver isn't needed.

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.THE_NETHERLANDS.downfall())
                .temperature(BiomeClimateTuning.THE_NETHERLANDS.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.THE_NETHERLANDS.waterColor())
                        .waterFogColor(0x113290)
                        .skyColor(BiomeAppearanceTuning.THE_NETHERLANDS.skyColor())
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(BiomeAppearanceTuning.THE_NETHERLANDS.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.THE_NETHERLANDS.grassColor())
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
