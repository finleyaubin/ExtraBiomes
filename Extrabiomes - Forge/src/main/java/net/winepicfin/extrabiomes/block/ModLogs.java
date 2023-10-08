package net.winepicfin.extrabiomes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;

public class ModLogs extends RotatedPillarBlock {

    public ModLogs(Properties p_55926_) {
        super(p_55926_);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction){
        return true;
    }
    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction){
        return 5;
    }
    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction){
        return 5;
    }
    @Override
   public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate){
    if(context.getItemInHand().getItem() instanceof AxeItem){
            if(state.is(ModBlocks.MYSTIC_LOG.get())){
                return ModBlocks.STRIPED_MYSTIC_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
            if(state.is(ModBlocks.MYSTIC_WOOD.get())){
                return ModBlocks.STRIPED_MYSTIC_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        if(state.is(ModBlocks.SKY_LOG.get())){
            return ModBlocks.STRIPED_SKY_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }
        if(state.is(ModBlocks.SKY_WOOD.get())){
            return ModBlocks.STRIPED_SKY_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }
        }
        return super.getToolModifiedState(state, context,toolAction, simulate);
    }
}
