package net.winepicfin.extrabiomes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;

public class DenseCloudBlock extends TransparentBlock {
    public DenseCloudBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (random.nextInt(DenseCloudBudding.BUD_ONE_IN) != 0) return;
        BlockPos target = pos.relative(Direction.getRandom(random));
        if (level.getBlockState(target).isAir() && level.isUnobstructed(defaultBlockState(), target, CollisionContext.empty())
                && DenseCloudBudding.canBudAt(level, target)) {
            level.setBlockAndUpdate(target, defaultBlockState());
        }
    }
}
