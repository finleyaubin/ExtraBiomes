package net.winepicfin.extrabiomes.block.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MossyPebbleBlock extends Block {
    public static final VoxelShape SHAPE1=Block.box(0,0,0,16,2,16);
    public static final VoxelShape SHAPE2=Block.box(0,0,0,16,3,16);
    public static final VoxelShape SHAPE3=Block.box(0,0,0,16,4,16);
    public static IntegerProperty SIZE=IntegerProperty.create("size", 1,3);

    public MossyPebbleBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.getStateDefinition().any().setValue(SIZE, 1));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pGetter, BlockPos pBlockPos, CollisionContext pCollission) {
        if (pState.getValue(SIZE)==1){
            return SHAPE1;
        }else if (pState.getValue(SIZE)==2){
            return SHAPE2;
        }else {
            return SHAPE3;
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIZE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext p_49820_) {
        return this.defaultBlockState().setValue(SIZE,1);
    }

    public BlockState getStateForThrowing() {
        return this.defaultBlockState().setValue(SIZE,1);
    }


    @Override
    public @NotNull InteractionResult use(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);

    if (pState.getValue(SIZE) < 3 && heldItem.getItem() == ModItems.MOSSY_PEBBLE.get()) {
            // Increase the block size
            pLevel.setBlockAndUpdate(pPos, pState.setValue(SIZE, pState.getValue(SIZE) + 1));

            if (!pPlayer.isCreative()) {
                // Decrease the item stack size if the player is not in creative mode
                heldItem.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;

    }
}
