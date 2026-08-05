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
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.OasisPuddleFeature;
import net.winepicfin.extrabiomes.worldgen.features.oasis.OasisFossilFeatures;

public class GrandOasis {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CAMEL, 3, 1, 2));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDesertVegetation(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.PALM_PLACED_KEY);

        // oasis subsystem: scattered puddle structure_template (Bedrock oasis_puddle_placer)
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, OasisPuddleFeature.OASIS_PUDDLE_SCATTER_PLACED_KEY);
        // exposed surface fossils in the style of Soul Sand Valley's bone piles, rather than
        // vanilla desert's buried-underground Feature.FOSSIL (see OasisFossilFeatures)
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, OasisFossilFeatures.SELECT_FOSSIL_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .downfall(BiomeClimateTuning.GRAND_OASIS.downfall())
                .temperature(BiomeClimateTuning.GRAND_OASIS.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x20c8a0)
                        .waterFogColor(0x113290)
                        .skyColor(0xffe29a)
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(0x58b028)
                        .grassColorOverride(0x80c040).build())
                .build();
    }
}
