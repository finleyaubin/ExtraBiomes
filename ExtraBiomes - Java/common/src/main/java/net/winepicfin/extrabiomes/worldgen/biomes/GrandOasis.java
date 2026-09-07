package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.OasisPuddleFeature;
import net.winepicfin.extrabiomes.worldgen.features.oasis.OasisFossilFeatures;

public class GrandOasis {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CAMEL, 3, 1, 2));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDesertVegetation(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.GRAND_OASIS_PALM_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.GRAND_OASIS_DEAD_BUSH_PLACED_KEY);

        // oasis subsystem: scattered puddle structure_template (Bedrock oasis_puddle_placer)
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, OasisPuddleFeature.OASIS_PUDDLE_SCATTER_PLACED_KEY);
        // Exposed surface fossils like Soul Sand Valley's bone piles, rather than vanilla desert's buried-underground Feature.FOSSIL.
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, OasisFossilFeatures.SELECT_FOSSIL_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .downfall(BiomeClimateTuning.GRAND_OASIS.downfall())
                .temperature(BiomeClimateTuning.GRAND_OASIS.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.GRAND_OASIS.waterColor())
                        .waterFogColor(0x113290)
                        .skyColor(BiomeAppearanceTuning.GRAND_OASIS.skyColor())
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(BiomeAppearanceTuning.GRAND_OASIS.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.GRAND_OASIS.grassColor()).build())
                .build();
    }
}
