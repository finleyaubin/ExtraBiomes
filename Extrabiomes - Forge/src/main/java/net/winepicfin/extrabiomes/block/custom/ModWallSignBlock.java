package net.winepicfin.extrabiomes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.winepicfin.extrabiomes.entity.ModHangingSignBlockEntity;
import net.winepicfin.extrabiomes.entity.ModSignBlockEntity;

public class ModWallSignBlock extends WallSignBlock {
    public ModWallSignBlock(Properties p_251606_, WoodType p_252140_) {
        super(p_251606_, p_252140_);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ModSignBlockEntity(pPos, pState);
    }
}
