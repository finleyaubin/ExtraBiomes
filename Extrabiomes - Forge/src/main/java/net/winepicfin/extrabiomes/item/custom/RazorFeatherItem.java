package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class RazorFeatherItem extends Item {
    public RazorFeatherItem(Properties properties){
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
        ItemStack itemStack = player.getItemInHand(hand);
        level.playSound((Player) null, player.getX(),player.getY(),player.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.5F,0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {       //TODO change to razor feather
            ThrownEgg thrownEgg = new ThrownEgg(level, player);
            thrownEgg.setItem(itemStack);
            thrownEgg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(thrownEgg);
        }
            player.awardStat(Stats.ITEM_USED.get(this));
            if(!player.getAbilities().instabuild){
                itemStack.shrink(1);
            }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
