package net.winepicfin.extrabiomes.worldgen.features.tallowhollows;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Stands a {@link ForgottenShelvesPlan} of bookshelves on the cave floor, as if a library had been swallowed by the wax. */
public class ForgottenShelvesFeature implements Feature {
    public static final MapCodec<ForgottenShelvesFeature> CODEC = MapCodec.unit(ForgottenShelvesFeature::new);

    @Override
    public MapCodec<ForgottenShelvesFeature> codec() {
        return CODEC;
    }

    // origin is the first air block above the floor. The whole cluster is placed or none of it, so what generates is always the planned shape.
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        ForgottenShelvesPlan plan = ForgottenShelvesPlan.plan(random.nextLong());
        for (ForgottenShelvesPlan.Column column : plan.columns()) {
            if (!fits(level, origin, column)) {
                return false;
            }
        }
        BlockState bookshelf = Blocks.BOOKSHELF.defaultBlockState();
        for (ForgottenShelvesPlan.Column column : plan.columns()) {
            for (int y = column.bottomY(); y <= column.topY(); y++) {
                level.setBlock(origin.offset(column.x(), y, column.z()), bookshelf, 2);
            }
            if (column.hasCandles()) {
                BlockState candles = Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, column.candles()).setValue(CandleBlock.LIT, true);
                level.setBlock(origin.offset(column.x(), column.candleY(), column.z()), candles, 2);
            }
        }
        return true;
    }

    // A column needs sturdy ground under its bottom shelf and open air (never water or lava) from the floor up to its candles.
    private static boolean fits(WorldGenLevel level, BlockPos origin, ForgottenShelvesPlan.Column column) {
        BlockPos ground = origin.offset(column.x(), column.bottomY() - 1, column.z());
        if (!level.getBlockState(ground).isFaceSturdy(level, ground, Direction.UP)) {
            return false;
        }
        if (column.sunk() && !isSolidRock(level.getBlockState(ground.above()))) {
            return false;
        }
        for (int y = 0; y < ForgottenShelvesPlan.MIN_HEADROOM; y++) {
            if (!level.getBlockState(origin.offset(column.x(), y, column.z())).isAir()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSolidRock(BlockState state) {
        return !state.isAir() && state.getFluidState().isEmpty();
    }
}
