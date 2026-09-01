package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mystic.MysticFeatures;

public class MysticForest {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 5, 4, 4));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        // forestFlowers < defaultFlowers < forestGrass < defaultMushrooms < defaultExtraVegetation is the
        // relative VEGETAL_DECORATION order CharredForest/DeepDarkForest already establish for these same
        // vanilla features - FeatureSorter shares one global per-step order across all biomes, so this has
        // to agree with theirs or world load throws a "Feature order cycle" crash.
        BiomeDefaultFeatures.addForestFlowers(biomeBuilder);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addForestGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder);
        // mystic_forest.biome.json carries the "swamp" tag alongside "mystic" (same as ShatteredSwamp/
        // JungleMarsh), which on Bedrock pulls in vanilla's swamp-tagged vegetation/mushroom feature_rules
        // - these were never ported to Java, so this biome was missing them entirely.
        BiomeDefaultFeatures.addSwampVegetation(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MYSTIC_PLACED_KEY);
        // This biome's "sea_material" is goo, which needs a TOP_LAYER_MODIFICATION feature run after lakes/aquifers exist, rather than a direct fluid swap (see GooConversionFeature).
        biomeBuilder.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MysticFeatures.MYSTIC_GOO_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.MYSTIC_FOREST.downfall())
                .temperature(BiomeClimateTuning.MYSTIC_FOREST.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.MYSTIC_FOREST.waterColor())
                        .waterFogColor(0x113290)
                        .skyColor(BiomeAppearanceTuning.MYSTIC_FOREST.skyColor())
                        .fogColor(0x4F126384)
                        .foliageColorOverride(BiomeAppearanceTuning.MYSTIC_FOREST.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.MYSTIC_FOREST.grassColor())
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.PORTAL, 0.100193334F))
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_CHERRY_GROVE)).build())
                .build();
    }
}
