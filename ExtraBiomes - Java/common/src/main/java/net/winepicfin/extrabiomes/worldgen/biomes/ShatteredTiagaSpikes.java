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
import net.winepicfin.extrabiomes.worldgen.features.glacier.GlacierFeatures;
import net.winepicfin.extrabiomes.worldgen.features.taigaspike.TaigaSpikeFeatures;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.BryceMesaPillarFeatures;

public class ShatteredTiagaSpikes {

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(EntityTypes.WOLF, MobCategory.CREATURE, 4, UniformInt.of(2, 3));
        spawnBuilder.addSpawn(EntityTypes.FOX, MobCategory.CREATURE, 4, UniformInt.of(1, 3));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addMossyStoneBlock(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
        BiomeDefaultFeatures.addBlueIce(biomeBuilder);
        BiomeDefaultFeatures.addTaigaTrees(biomeBuilder);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder, true);
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TaigaSpikeFeatures.TAIGA_SPIKE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, GlacierFeatures.SELECT_SNOW_DRIFT_PLACED_KEY);
        // Reconstructs the pre-1.18 mesa surface builder's noise-gated pillar bumps - the "spikes" this biome is named for, separate from the taiga_spike structure feature above.
        biomeBuilder.addFeature(GenerationStep.Decoration.RAW_GENERATION, BryceMesaPillarFeatures.TUFF_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.SHATTERED_TAIGA_SPIKES.downfall())
                .temperature(BiomeClimateTuning.SHATTERED_TAIGA_SPIKES.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x113290))
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(BiomeAppearanceTuning.SHATTERED_TAIGA_SPIKES.skyColor()))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xC0D8FF))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.SHATTERED_TAIGA_SPIKES.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.SHATTERED_TAIGA_SPIKES.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.SHATTERED_TAIGA_SPIKES.grassColor()).build())
                .build();
    }
}
