package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.ListModifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.winepicfin.extrabiomes.worldgen.features.tallowhollows.TallowHollowsFeatures;

/**
 * A cave biome of pale waxy stone lit by its own ever-burning wicks, carved out of a third of
 * vanilla's dripstone caves (see ModOverworldRegionSecondary). Java-first: its Bedrock files live on
 * the Bedrock-Dev branch.
 */
public class TallowHollows {
    private static final int WATER_FOG_COLOR = 0x5A3C10;
    private static final int FOG_COLOR = 0x3A2410;
    private static final float WAX_MOTE_DENSITY = 0.004F;

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(EntityTypes.BAT, MobCategory.AMBIENT, 10, UniformInt.of(4, 8));
        spawnBuilder.addSpawn(EntityTypes.GLOW_SQUID, MobCategory.UNDERGROUND_WATER_CREATURE, 5, UniformInt.of(2, 4));
        // commonSpawns()'s monster weights without its cave spawns, which this biome sets itself above.
        BiomeDefaultFeatures.monsters(spawnBuilder, 95, 5, 0, 100, false);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TallowHollowsFeatures.HONEYCOMB_VEIN_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, TallowHollowsFeatures.PILLAR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, TallowHollowsFeatures.FORGOTTEN_SHELVES_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, TallowHollowsFeatures.WAX_DRIP_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, TallowHollowsFeatures.WICK_BLOOM_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, TallowHollowsFeatures.CANDLE_CLUSTER_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.TALLOW_HOLLOWS.downfall())
                .temperature(BiomeClimateTuning.TALLOW_HOLLOWS.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(WATER_FOG_COLOR))
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(BiomeAppearanceTuning.TALLOW_HOLLOWS.skyColor()))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(FOG_COLOR))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .modifyAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, ListModifier.append(), AmbientParticle.of(ParticleTypes.WAX_ON, WAX_MOTE_DENSITY))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.TALLOW_HOLLOWS.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.TALLOW_HOLLOWS.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.TALLOW_HOLLOWS.grassColor()).build())
                .build();
    }
}
