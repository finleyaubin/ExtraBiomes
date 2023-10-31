package net.winepicfin.extrabiomes.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.winepicfin.extrabiomes.block.ModBlocks;

public class ModSignBlockEntity extends SignBlockEntity {
    public ModSignBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.MOD_SIGN.get(),pPos,pState);
    }

    @Override
    public BlockEntityType<?> getType(){
        return ModBlockEntities.MOD_SIGN.get();
    }
}
