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
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsCaveCarver;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsOreFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsTulipFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsWindmillFeature;

public class TheNetherlands {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.BEE, 4, 2, 3));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
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
        // NOTE: despite bedrock's 'nether'/'nether_wastes' spawn-category tags, this biome is
        // themed after the real-world Netherlands (tulip fields, windmills, wheat, canals -
        // see bedrock feature_rules/the_netherlands/*). It generates in the OVERWORLD.
        // Base (non-mutated) TheNetherlands gets tulip fields (feature_rules gate on
        // has_biome_tag != "mutated"); TheNetherlandsMutated gets wheat/canal instead.
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsTulipFeatures.ORANGE_TULIP_FLOOR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsTulipFeatures.PINK_TULIP_FLOOR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsTulipFeatures.RED_TULIP_FLOOR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherlandsTulipFeatures.WHITE_TULIP_FLOOR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, NetherlandsWindmillFeature.WINDMILL_NETHERLANDS_PLACED_KEY);
        biomeBuilder.addCarver(GenerationStep.Carving.AIR, NetherlandsCaveCarver.NETHERLANDS_CAVE_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.5f)
                .temperature(0.5f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x90ADAD)
                        .waterFogColor(0x113290)
                        .skyColor(0xb0d0e0)
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(0xD4912C)
                        .grassColorOverride(0x7AAB35)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
