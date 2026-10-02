package net.winepicfin.extrabiomes.worldgen.features.glacier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.HashSet;
import java.util.Set;

public class CavePillarFeature extends Feature<CavePillarFeature.Configuration> {
    public record Configuration(BlockState state, boolean hanging, int minHeight, int maxHeight) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.fieldOf("state").forGetter(Configuration::state),
                Codec.BOOL.fieldOf("hanging").forGetter(Configuration::hanging),
                Codec.INT.fieldOf("min_height").forGetter(Configuration::minHeight),
                Codec.INT.fieldOf("max_height").forGetter(Configuration::maxHeight)
        ).apply(instance, Configuration::new));
    }

    // Pillars of the same block within this horizontal distance block a new one, so density stays low however open the caves are.
    private static final int SPACING = 6;
    private static final int SPACING_DEPTH = 3;
    private static final float EDGE_SKIP_CHANCE = 0.25F;

    public CavePillarFeature(Codec<Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        Configuration config = context.config();
        BlockState state = config.state();
        boolean hanging = config.hanging();
        int minHeight = config.minHeight();
        int maxHeight = config.maxHeight();
        int growth = hanging ? -1 : 1;
        if (!level.getBlockState(origin).isAir() || !isSolid(level.getBlockState(origin.offset(0, -growth, 0))) || crowded(level, state, origin, growth)) {
            return false;
        }

        int height = minHeight + random.nextInt(maxHeight - minHeight + 1);
        int baseRadius = 1 + random.nextInt(2);
        Set<BlockPos> placed = new HashSet<>();
        for (int layer = 0; layer < height; layer++) {
            int radius = layer == height - 1 ? 0 : Math.round(baseRadius * (1 - 0.6F * layer / height));
            growLayer(level, random, state, origin.offset(0, layer * growth, 0), radius, growth, placed);
        }
        return !placed.isEmpty();
    }

    private void growLayer(WorldGenLevel level, RandomSource random, BlockState state, BlockPos center, int radius, int growth, Set<BlockPos> placed) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int distance = dx * dx + dz * dz;
                if (distance > radius * radius + radius || (distance > radius * radius && random.nextFloat() < EDGE_SKIP_CHANCE)) {
                    continue;
                }
                BlockPos pos = center.offset(dx, 0, dz);
                BlockPos support = pos.offset(0, -growth, 0);
                boolean supported = placed.contains(support) || isSolid(level.getBlockState(support));
                if (supported && level.getBlockState(pos).isAir()) {
                    level.setBlock(pos, state, 2);
                    placed.add(pos);
                }
            }
        }
    }

    private boolean crowded(WorldGenLevel level, BlockState state, BlockPos origin, int growth) {
        for (int dx = -SPACING; dx <= SPACING; dx++) {
            for (int dz = -SPACING; dz <= SPACING; dz++) {
                for (int depth = 0; depth < SPACING_DEPTH; depth++) {
                    if (level.getBlockState(origin.offset(dx, depth * growth, dz)).is(state.getBlock())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isSolid(BlockState blockState) {
        return !blockState.isAir() && blockState.getFluidState().isEmpty();
    }
}
