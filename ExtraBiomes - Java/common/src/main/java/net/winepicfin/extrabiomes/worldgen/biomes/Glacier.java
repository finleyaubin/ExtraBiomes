package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.attribute.AmbientAdditionsSettings;
import net.minecraft.world.attribute.modifier.FloatModifier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import java.util.List;
import java.util.Optional;
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

public class Glacier {
    // The clips run 10-23s, so the per-tick chance is low.
    private static final double ICE_CRACKING_TICK_CHANCE = 0.0005;
    private static final float WATER_CLARITY = 1.5F;
    private static AmbientSounds iceCrackingAmbience() {
        SoundEvent iceCracking = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "ambient.glacier.ice_cracking"));
        return new AmbientSounds(
                Optional.empty(),
                AmbientSounds.LEGACY_CAVE_SETTINGS.mood(),
                List.of(new AmbientAdditionsSettings(Holder.direct(iceCracking), ICE_CRACKING_TICK_CHANCE)));
    }

    public Biome Register(BootstrapContext<Biome> context)
    {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(EntityTypes.POLAR_BEAR, MobCategory.CREATURE, 1, UniformInt.of(1, 2));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CARVER));
        ModBiomes.globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addBlueIce(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GlacierFeatures.GLACIER_ICE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GlacierFeatures.GLACIER_PACKED_ICE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GlacierFeatures.GLACIER_BLUE_ICE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GlacierFeatures.GLACIER_MAGMA_FISSURE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LAKES, GlacierFeatures.GLACIER_LAVA_POOL_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LAKES, GlacierFeatures.GLACIER_SURFACE_POND_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, GlacierFeatures.GLACIER_MORAINE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, GlacierFeatures.GLACIER_ERRATIC_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, GlacierFeatures.GLACIER_TOP_ICE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, GlacierFeatures.SELECT_SNOW_DRIFT_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, GlacierFeatures.GLACIER_CREVASSE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, GlacierFeatures.GLACIER_CEILING_LAVAFALL_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, GlacierFeatures.GLACIER_ICE_ENCASED_LOOT_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, GlacierFeatures.GLACIER_BASALT_PILLAR_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, GlacierFeatures.GLACIER_SNOW_PILLAR_PLACED_KEY);
        // After the default lava springs so their lava gets a basalt rim, and before the meltwater pools so those stay liquid.
        biomeBuilder.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, GlacierFeatures.GLACIER_FINISH_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, GlacierFeatures.GLACIER_MELTWATER_POOL_PLACED_KEY);
        // Added after globalOverworldGeneration's surface freezing, so it runs after it within this step.
        biomeBuilder.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, GlacierFeatures.GLACIER_MELTWATER_STREAM_PLACED_KEY);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(BiomeClimateTuning.GLACIER.downfall())
                .temperature(BiomeClimateTuning.GLACIER.temperature())
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x8FDCF5))
                .modifyAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, FloatModifier.MULTIPLY, WATER_CLARITY)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(BiomeAppearanceTuning.GLACIER.skyColor()))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xC0D8FF))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, iceCrackingAmbience())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(BiomeAppearanceTuning.GLACIER.waterColor())
                        .foliageColorOverride(BiomeAppearanceTuning.GLACIER.foliageColor())
                        .grassColorOverride(BiomeAppearanceTuning.GLACIER.grassColor()).build())
                .build();
    }
}
