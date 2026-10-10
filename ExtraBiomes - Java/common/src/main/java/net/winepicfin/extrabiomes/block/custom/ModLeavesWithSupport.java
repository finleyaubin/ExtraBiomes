package net.winepicfin.extrabiomes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// IForgeShearable was dropped as a marker interface here since it added no override and its default behavior matches vanilla's; removing it keeps this class Forge-import-free for Fabric (see scripts/verify_common_isolation.py).
public class ModLeavesWithSupport extends TintedParticleLeavesBlock implements SimpleWaterloggedBlock {
    public static final int DECAY_DISTANCE = 7;
    private static final int TICK_DELAY = 1;

    public ModLeavesWithSupport(BlockBehaviour.Properties properties) {
        super(0.0F, properties);
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

}
