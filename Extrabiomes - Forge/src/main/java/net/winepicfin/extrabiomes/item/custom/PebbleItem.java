package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.entity.custom.projectile.PebbleProjectileEntity;
import net.winepicfin.extrabiomes.item.ModItems;

public class PebbleItem extends Item {
    public PebbleItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (!level.isClientSide) {
            if (!player.isCrouching()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
                PebbleProjectileEntity pebbleEntity = new PebbleProjectileEntity(level, player);
                pebbleEntity.setItem(itemstack);
                pebbleEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(pebbleEntity);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    public InteractionResult useOn(UseOnContext p_41297_) {
        Player player = p_41297_.getPlayer();
        Level level = p_41297_.getLevel();
        if (player.isCrouching()) {
            BlockPos blockpos = p_41297_.getClickedPos();
            BlockState blockstate = level.getBlockState(blockpos);
            BlockState blockstate1;
            BlockPos blockpos1 = blockpos.relative(p_41297_.getClickedFace());
            level.playSound(player, blockpos1, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            if (player.getItemInHand(p_41297_.getHand()).getItem()== ModItems.PEBBLE.get()){
                blockstate1 = ModBlocks.PEBBLE.get().getStateForThrowing();
            }else {
                blockstate1 = ModBlocks.MOSSY_PEBBLE.get().getStateForThrowing();
            }
            level.setBlock(blockpos1, blockstate1, 11);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
            ItemStack itemstack = p_41297_.getItemInHand();
            if (player instanceof ServerPlayer) {
                itemstack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.FAIL;
            }
        } else {
            return InteractionResult.FAIL;
        }
    }
}
