package net.winepicfin.extrabiomes.block.custom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// IForgeShearable used to be implemented here as a marker, but with no onSheared/isShearable
// override its Forge-side default behavior is identical to vanilla's own loot-table-driven leaf
// shearing - dropping it is behavior-neutral on Forge and removes the last Forge-only import from
// common (see scripts/verify_common_isolation.py), letting this class work unmodified on Fabric.
public class ModLeavesWithSupport extends LeavesBlock implements SimpleWaterloggedBlock {
    public static final int DECAY_DISTANCE = 7;
    private static final int TICK_DELAY = 1;

    public ModLeavesWithSupport(BlockBehaviour.Properties p_54422_) {
        super(p_54422_);
    }

    public VoxelShape getBlockSupportShape(BlockState p_54456_, BlockGetter p_54457_, BlockPos p_54458_) {
        return Shapes.block();
    }

}
