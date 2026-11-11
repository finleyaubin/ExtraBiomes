package net.winepicfin.extrabiomes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/** A wick that burns straight out of the rock: snuffed by an empty hand, relit like a candle. */
public class EverWickBlock extends AbstractCandleBlock {
    public static final int LIT_LIGHT_LEVEL = 14;
    private static final double FLAME_HEIGHT = 0.6;
    private static final float SMOKE_CHANCE = 0.15F;
    private static final VoxelShape SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);
    private static final List<Vec3> FLAME_POSITIONS = List.of(new Vec3(0.5, FLAME_HEIGHT, 0.5));

    public EverWickBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LIT, true));
    }

    public static int lightLevel(BlockState state) {
        return state.getValue(LIT) ? LIT_LIGHT_LEVEL : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState state) {
        return FLAME_POSITIONS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        boolean lit = state.getValue(LIT);
        if (lit && stack.isEmpty() && player.getAbilities().mayBuild) {
            extinguish(player, state, level, pos);
            return InteractionResult.SUCCESS;
        }
        if (!lit && isIgniter(stack)) {
            relight(stack, state, level, pos, player, hand);
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    private static boolean isIgniter(ItemStack stack) {
        return stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE);
    }

    // Flint and steel's own useOn only lights blocks CandleBlock.canLight accepts, so the wick lights itself and spends the igniter the same way.
    private static void relight(ItemStack igniter, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        boolean fireCharge = igniter.is(Items.FIRE_CHARGE);
        SoundEvent sound = fireCharge ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL_IMMEDIATE);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        if (fireCharge) {
            igniter.consume(1, player);
        } else {
            igniter.hurtAndBreak(1, player, hand.asEquipmentSlot());
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY() + FLAME_HEIGHT;
        double z = pos.getZ() + 0.5;
        level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
        if (random.nextFloat() < SMOKE_CHANCE) {
            level.addParticle(ParticleTypes.SMOKE, x, y + 0.1, z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }
}
