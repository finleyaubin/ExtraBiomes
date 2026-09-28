package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityTypes;
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

        spawnBuilder.addSpawn(EntityTypes.FROG, MobCategory.CREATURE, 5, UniformInt.of(4, 4));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        // swampVegetation < defaultMushrooms is the order ShatteredSwamp already establishes for that
        // pair - FeatureSorter shares one global per-step order across all biomes, so this has to
        // agree or world load throws a "Feature order cycle" crash (confirmed live: a CI run crashed
        // with exactly that cycle between mystic_forest and shattered_swamp before this fix, from
        // addSwampVegetation originally being placed after addDefaultMushrooms here).
        //
        // The old addForestFlowers/addDefaultFlowers/addForestGrass/addDefaultMushrooms calls
        // (vanilla's shared forest_flowers/flower_default/patch_grass_forest/brown_mushroom_normal/
        // red_mushroom_normal features) are gone - see MysticFeatures.MYSTIC_FLOWERS_KEY's javadoc
        // for why: sharing any of these with any other mod's biome pulled mystic_forest into a
        // global ordering graph that eventually cycled against Biomes We've Gone's biomes, and no
        // amount of reordering these calls could fix a contradiction that wasn't even between this
        // biome and theirs directly. Swapping flowers alone didn't clear the cycle, so grass and
        // both mushroom patches got the same treatment.
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MysticFeatures.MYSTIC_FLOWERS_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MysticFeatures.MYSTIC_GRASS_PLACED_KEY);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        // mystic_forest.biome.json carries the "swamp" tag alongside "mystic" (same as ShatteredSwamp/
        // JungleMarsh), which on Bedrock pulls in vanilla's swamp-tagged vegetation/mushroom feature_rules
        // - these were never ported to Java, so this biome was missing them entirely.
        BiomeDefaultFeatures.addSwampVegetation(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MysticFeatures.MYSTIC_BROWN_MUSHROOM_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MysticFeatures.MYSTIC_RED_MUSHROOM_PLACED_KEY);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder, true);
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
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x3A5F78))
                .setAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, 12.0F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(BiomeAppearanceTuning.MYSTIC_FOREST.skyColor()))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0x4A3C2D))
                .setAttribute(EnvironmentAttributes.FOG_START_DISTANCE, 32.0F)
                .setAttribute(EnvironmentAttributes.FOG_END_DISTANCE, 1200.0F)
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.PORTAL, 0.100193334F))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_CHERRY_GROVE)))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.MYSTIC_FOREST.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.MYSTIC_FOREST.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.MYSTIC_FOREST.grassColor()).build())
                .build();
    }
}
