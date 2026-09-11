package net.winepicfin.extrabiomes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

// Placed under the sky city fountain's water column in the structure NBT. Blocks placed by
// structure templates skip the neighbour/onPlace updates that would normally schedule a fluid's
// first tick, so a source block dropped straight into the NBT just sits there instead of falling
// to the ground below. This block's random tick kicks off that first fluid tick, then removes itself.
public class FountainSpoutBlock extends Block {
    public FountainSpoutBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        BlockPos above = pos.above();
        FluidState fluid = level.getFluidState(above);
        if (!fluid.isEmpty()) {
            level.scheduleTick(above, fluid.getType(), fluid.getType().getTickDelay(level));
        }
        level.removeBlock(pos, false);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }
}
