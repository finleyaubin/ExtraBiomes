package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.gameevent.GameEvent;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.entity.custom.projectile.PebbleProjectileEntity;
import net.winepicfin.extrabiomes.item.ModItems;

public class PebbleItem extends Item {
    private static final int CHARGE_TICKS = 16;
    private static final int MAX_USE_TICKS = 72000;

    public PebbleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (player.isCrouching()) {
            return InteractionResult.PASS;
        }
        player.startUsingItem(interactionHand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack itemstack, Level level, LivingEntity entity, int timeLeft) {
        boolean charged = getUseDuration(itemstack, entity) - timeLeft >= CHARGE_TICKS;
        if (!charged || !(entity instanceof Player player)) {
            return false;
        }
        if (!level.isClientSide()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            PebbleProjectileEntity pebbleEntity = new PebbleProjectileEntity(level, player);
            pebbleEntity.setItem(itemstack);
            pebbleEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(pebbleEntity);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        return true;
    }

    @Override
    public int getUseDuration(ItemStack itemstack, LivingEntity entity) {
        return MAX_USE_TICKS;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemstack) {
        return ItemUseAnimation.BOW;
    }

    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player.isCrouching()) {
            BlockPos blockpos = context.getClickedPos();
            BlockState blockstate = level.getBlockState(blockpos);
            BlockState blockstate1;
            BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
            if (player.getItemInHand(context.getHand()).getItem()== ModItems.PEBBLE.get()){
                blockstate1 = ModBlocks.PEBBLE.get().getStateForThrowing();
            }else {
                blockstate1 = ModBlocks.MOSSY_PEBBLE.get().getStateForThrowing();
            }
            BlockState existing = level.getBlockState(blockpos1);
            if (!(existing.isAir() || existing.getBlock() instanceof LiquidBlock) || !blockstate1.canSurvive(level, blockpos1)) {
                return InteractionResult.FAIL;
            }
            blockstate1 = blockstate1.setValue(BlockStateProperties.WATERLOGGED, existing.getFluidState().getType() == Fluids.WATER);
            level.playSound(player, blockpos1, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            level.setBlock(blockpos1, blockstate1, 11);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
            ItemStack itemstack = context.getItemInHand();
            if (player instanceof ServerPlayer) {
                itemstack.shrink(1);
                return level.isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }
        } else {
            return InteractionResult.PASS;
        }
    }
}
