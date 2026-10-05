package net.winepicfin.extrabiomes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.winepicfin.extrabiomes.block.ModBlocks;

// Dense cloud buds outward around blue ice that sits above water heated by magma, at sky city altitude or higher.
public final class DenseCloudBudding {
    public static final int CLOUD_HORIZONTAL_RADIUS = 10;
    public static final int CLOUD_VERTICAL_RADIUS = 5;
    public static final int MAX_ICE_HEIGHT_ABOVE_WATER = 20;
    public static final int MIN_ICE_Y = 200;
    public static final int BUD_ONE_IN = 4;

    private DenseCloudBudding() {
    }

    public static boolean inCloudShape(int dx, int dy, int dz) {
        int h = CLOUD_HORIZONTAL_RADIUS * CLOUD_HORIZONTAL_RADIUS;
        int v = CLOUD_VERTICAL_RADIUS * CLOUD_VERTICAL_RADIUS;
        return dx * dx * v + dy * dy * h + dz * dz * v <= h * v;
    }

    public static boolean canBudAt(Level level, BlockPos target) {
        BlockPos.MutableBlockPos ice = new BlockPos.MutableBlockPos();
        for (int dx = -CLOUD_HORIZONTAL_RADIUS; dx <= CLOUD_HORIZONTAL_RADIUS; dx++) {
            for (int dz = -CLOUD_HORIZONTAL_RADIUS; dz <= CLOUD_HORIZONTAL_RADIUS; dz++) {
                for (int dy = -CLOUD_VERTICAL_RADIUS; dy <= CLOUD_VERTICAL_RADIUS; dy++) {
                    if (!inCloudShape(dx, dy, dz)) continue;
                    ice.set(target.getX() + dx, target.getY() + dy, target.getZ() + dz);
                    if (ice.getY() >= MIN_ICE_Y && level.getBlockState(ice).is(Blocks.BLUE_ICE) && sitsOverHeatedWater(level, ice)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean sitsOverHeatedWater(Level level, BlockPos icePos) {
        BlockPos.MutableBlockPos pos = icePos.mutable();
        for (int i = 1; i <= MAX_ICE_HEIGHT_ABOVE_WATER; i++) {
            pos.move(Direction.DOWN);
            BlockState state = level.getBlockState(pos);
            if (state.getFluidState().is(FluidTags.WATER)) {
                return waterEndsOnMagma(level, pos);
            }
            if (!state.isAir() && !state.is(ModBlocks.DENSE_CLOUD.get())) return false;
        }
        return false;
    }

    private static boolean waterEndsOnMagma(Level level, BlockPos.MutableBlockPos pos) {
        while (level.getFluidState(pos).is(FluidTags.WATER)) {
            pos.move(Direction.DOWN);
        }
        return level.getBlockState(pos).is(Blocks.MAGMA_BLOCK);
    }
}
