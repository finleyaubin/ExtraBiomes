package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.shatteredswamp.ShatteredSwampFeatures;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.BryceMesaPillarFeatures;

public class ShatteredSwamp {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(EntityTypes.FROG, MobCategory.CREATURE, 4, UniformInt.of(2, 4));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addSwampVegetation(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        // shattered_swamp: bamboo feature
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ShatteredSwampFeatures.BAMBOO_PLACED_KEY);
        // swamp_huge_mushroom is delivered by the ADD_DARK_FOREST_HUGE_MUSHROOMS modifier via ModTags.Biomes.GETS_SWAMP_HUGE_MUSHROOMS,
        // not baked in here: see that tag's javadoc for why a baked-in copy makes a feature order cycle with mods that append vegetal features.
        // Reconstructs the pre-1.18 mesa surface builder's noise-gated pillar bumps; the mesa builder is reused just for the pillar shaping, not for a mesa-like look.
        biomeBuilder.addFeature(GenerationStep.Decoration.RAW_GENERATION, BryceMesaPillarFeatures.TUFF_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.SHATTERED_SWAMP.downfall())
                .temperature(BiomeClimateTuning.SHATTERED_SWAMP.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x2b5636))
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(BiomeAppearanceTuning.SHATTERED_SWAMP.skyColor()))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xC0D8FF))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.SHATTERED_SWAMP.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.SHATTERED_SWAMP.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.SHATTERED_SWAMP.grassColor()).build())
                .build();
    }
}
