package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra.VolcanicMossTundraFeatures;

/**
 * Java port of extrabiomes:volcanic_moss_tundra ("ExtraBiomes - Bedrock/packs/BP/biomes/
 * volcanic_moss_tundra.biome.json", added in Bedrock 3.1.0-beta-2). A rare, cold biome that
 * replaces vanilla ice_plains: black sand/sandstone ground (see {@link net.winepicfin.extrabiomes.worldgen.biomes.surface.ModSurfaceRules}),
 * a winding lava river with magma banks and basalt-column clusters along its shore, moss patches
 * on high ground, and rare basalt/boulder/elephant-rock formations plus rarer volcano structures
 * (see {@link VolcanicMossTundraFeatures}).
 */
public class VolcanicMossTundra {

    public Biome Register(BootstapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, VolcanicMossTundraFeatures.LAVA_RIVER_CORE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, VolcanicMossTundraFeatures.LAVA_RIVER_BANK_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, VolcanicMossTundraFeatures.SELECT_ROCK_FORMATION_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, VolcanicMossTundraFeatures.SELECT_VOLCANO_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, VolcanicMossTundraFeatures.BASALT_BANK_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, VolcanicMossTundraFeatures.HIGH_ELEVATION_MOSS_FLOOR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VolcanicMossTundraFeatures.ELEVATION_MOSS_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.VOLCANIC_MOSS_TUNDRA.downfall())
                .temperature(BiomeClimateTuning.VOLCANIC_MOSS_TUNDRA.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.VOLCANIC_MOSS_TUNDRA.waterColor())
                        .waterFogColor(0x113290)
                        .skyColor(BiomeAppearanceTuning.VOLCANIC_MOSS_TUNDRA.skyColor())
                        .fogColor(0xC0D8FF)
                        .foliageColorOverride(BiomeAppearanceTuning.VOLCANIC_MOSS_TUNDRA.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.VOLCANIC_MOSS_TUNDRA.grassColor())
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .build();
    }
}
