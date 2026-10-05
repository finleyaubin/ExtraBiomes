package net.winepicfin.extrabiomes.block.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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

public class MossyPebbleBlock extends Block implements SimpleWaterloggedBlock {
    public static final VoxelShape SHAPE1=Block.box(0,0,0,16,2,16);
    public static final VoxelShape SHAPE2=Block.box(0,0,0,16,3,16);
    public static final VoxelShape SHAPE3=Block.box(0,0,0,16,4,16);
    public static IntegerProperty SIZE=IntegerProperty.create("size", 1,3);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public MossyPebbleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(SIZE, 1).setValue(WATERLOGGED, false));
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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIZE, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.getStateForThrowing().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    public BlockState getStateForThrowing() {
        return this.defaultBlockState().setValue(SIZE,1);
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, @NotNull LevelReader level, @NotNull ScheduledTickAccess ticks, @NotNull BlockPos pos, @NotNull Direction direction, @NotNull BlockPos neighborPos, @NotNull BlockState neighborState, @NotNull RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    // Pick-block hands back the placeable mossy pebble item, not the auto-registered "mossy_pebble_block" BlockItem.
    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(ModItems.MOSSY_PEBBLE.get());
    }


    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack heldItem, BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pState.getValue(SIZE) < 3 && heldItem.getItem() == ModItems.MOSSY_PEBBLE.get()) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(SIZE, pState.getValue(SIZE) + 1));

            if (!pPlayer.isCreative()) {
                heldItem.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;

    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState below = pLevel.getBlockState(pPos.below());
        return !below.isAir() && !(below.getBlock() instanceof PebbleBlock) && !(below.getBlock() instanceof MossyPebbleBlock);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, net.minecraft.world.level.redstone.Orientation pOrientation, boolean pIsMoving) {
        if (!pIsMoving && !this.canSurvive(pState, pLevel, pPos)) {
            pLevel.destroyBlock(pPos, true);
        }
        super.neighborChanged(pState, pLevel, pPos, pBlock, pOrientation, pIsMoving);
    }
}
