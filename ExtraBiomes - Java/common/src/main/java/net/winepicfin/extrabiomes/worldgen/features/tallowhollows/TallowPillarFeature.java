package net.winepicfin.extrabiomes.worldgen.features.tallowhollows;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.winepicfin.extrabiomes.block.ModBlocks;

/** Raises a {@link TallowPillarPlan} from the cave floor: a tapered tallowstone candle crowned with an ever-wick. */
public class TallowPillarFeature implements Feature {
    public static final MapCodec<TallowPillarFeature> CODEC = MapCodec.unit(TallowPillarFeature::new);

    @Override
    public MapCodec<TallowPillarFeature> codec() {
        return CODEC;
    }

    // origin is the first air block above the floor.
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int headroom = airAbove(level, origin);
        if (headroom < TallowPillarPlan.MIN_HEADROOM) {
            return false;
        }
        TallowPillarPlan plan = TallowPillarPlan.plan(random.nextLong(), headroom);

        BlockState tallowstone = ModBlocks.TALLOWSTONE.get().defaultBlockState();
        boolean placedAny = false;
        for (TallowPillarPlan.Cell cell : plan.body()) {
            placedAny |= placeInAir(level, at(origin, cell), tallowstone);
        }
        BlockPos wick = at(origin, plan.wick());
        if (level.getBlockState(wick.below()).is(ModBlocks.TALLOWSTONE.get())) {
            placeInAir(level, wick, ModBlocks.EVER_WICK.get().defaultBlockState());
        }
        BlockState drip = ModBlocks.WAX_DRIP.get().defaultBlockState();
        for (TallowPillarPlan.Cell cell : plan.drips()) {
            BlockPos pos = at(origin, cell);
            if (level.getBlockState(pos.above()).is(ModBlocks.TALLOWSTONE.get())) {
                placeInAir(level, pos, drip);
            }
        }
        return placedAny;
    }

    private static int airAbove(WorldGenLevel level, BlockPos origin) {
        int headroom = 0;
        while (headroom <= TallowPillarPlan.MAX_HEIGHT && level.getBlockState(origin.above(headroom)).isAir()) {
            headroom++;
        }
        return headroom;
    }

    // Only ever fills air, so the pillar never displaces rock, water or lava.
    private static boolean placeInAir(WorldGenLevel level, BlockPos pos, BlockState state) {
        if (!level.getBlockState(pos).isAir()) {
            return false;
        }
        level.setBlock(pos, state, 2);
        return true;
    }

    private static BlockPos at(BlockPos origin, TallowPillarPlan.Cell cell) {
        return origin.offset(cell.x(), cell.y(), cell.z());
    }
}
