package net.winepicfin.extrabiomes.worldgen.features.undergroundjungle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

/**
 * Configuration for {@link MultiFeature}: a fixed list of sub-{@link PlacedFeature}s that are all
 * placed unconditionally at the same origin. This is the closest Java equivalent of Bedrock's
 * {@code minecraft:aggregate_feature} for the (rare) case where an aggregate needs to be nested
 * inside a single-slot config field (e.g. a {@code VegetationPatchConfiguration}'s
 * {@code vegetation_feature}) rather than wired directly into a biome via several independent
 * {@code addFeature} calls (the usual, simpler convention documented on {@code MossFeatures}).
 */
public record MultiFeatureConfiguration(List<Holder<PlacedFeature>> features) implements FeatureConfiguration {
    public static final Codec<MultiFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    PlacedFeature.CODEC.listOf().fieldOf("features").forGetter(MultiFeatureConfiguration::features)
            ).apply(instance, MultiFeatureConfiguration::new));
}
