package net.winepicfin.extrabiomes.worldgen.features.undergroundjungle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

/**
 * Runs every sub-{@link PlacedFeature} unconditionally at the same origin - a minimal, reusable
 * Java equivalent of Bedrock's {@code minecraft:aggregate_feature} for use in config slots that
 * only accept a single {@code PlacedFeature} (e.g. a {@code VegetationPatchFeature}'s
 * {@code vegetation_feature}) rather than wired directly into a biome via several independent
 * {@code addFeature} calls (the usual, simpler convention documented on {@code MossFeatures}).
 */
public record MultiFeature(List<Holder<PlacedFeature>> features) implements Feature {

    public static final MapCodec<MultiFeature> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    PlacedFeature.CODEC.listOf().fieldOf("features").forGetter(MultiFeature::features)
            ).apply(instance, MultiFeature::new));

    @Override
    public MapCodec<MultiFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        boolean placedAny = false;
        for (Holder<PlacedFeature> feature : features) {
            if (feature.value().place(level, generator, random, origin)) {
                placedAny = true;
            }
        }
        return placedAny;
    }
}
