package net.winepicfin.extrabiomes.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModHangingSignBlockEntity extends HangingSignBlockEntity {
    public ModHangingSignBlockEntity(BlockPos pPos, BlockState pState) {
        super(pPos, pState);
    }

    @Override
    public BlockEntityType<?> getType(){
        return ModBlockEntities.MOD_HANGING_SIGN.get();
    }
}
