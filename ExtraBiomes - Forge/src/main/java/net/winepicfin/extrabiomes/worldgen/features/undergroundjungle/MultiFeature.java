package net.winepicfin.extrabiomes.worldgen.features.undergroundjungle;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Runs every {@link PlacedFeature} in its {@link MultiFeatureConfiguration} unconditionally at the
 * same origin - a minimal, reusable Java equivalent of Bedrock's {@code minecraft:aggregate_feature}
 * for use in config slots that only accept a single {@code PlacedFeature} (see
 * {@link MultiFeatureConfiguration}'s javadoc for why this exists instead of the usual "just register
 * each sub-feature separately" convention).
 */
public class MultiFeature extends Feature<MultiFeatureConfiguration> {

    public MultiFeature(Codec<MultiFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<MultiFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkGenerator generator = context.chunkGenerator();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        boolean placedAny = false;
        for (Holder<PlacedFeature> feature : context.config().features()) {
            if (feature.value().place(level, generator, random, origin)) {
                placedAny = true;
            }
        }
        return placedAny;
    }
}
