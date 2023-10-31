package net.winepicfin.extrabiomes.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.winepicfin.extrabiomes.block.custom.ModHangingSignBlock;

public class ModHangingSignBlockEntity extends SignBlockEntity {
    public ModHangingSignBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.MOD_HANGING_SIGN.get(), pPos,pState);
    }

    @Override
    public BlockEntityType<?> getType(){
        return ModBlockEntities.MOD_HANGING_SIGN.get();
    }
}
