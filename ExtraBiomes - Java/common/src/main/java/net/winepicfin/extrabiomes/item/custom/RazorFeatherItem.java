package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.entity.custom.projectile.RazorFeatherProjectileEntity;

// Base "razor feather" variant. Diamond/Netherite reuse this by overriding createProjectile().
public class RazorFeatherItem extends Item {
    public RazorFeatherItem(Properties properties){
        super(properties);
    }

    protected RazorFeatherProjectileEntity createProjectile(Level level, Player player) {
        return new RazorFeatherProjectileEntity(level, player);
    }

    public InteractionResult use(Level level, Player player, InteractionHand hand){
        ItemStack itemStack = player.getItemInHand(hand);
        level.playSound(null, player.getX(),player.getY(),player.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.5F,0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            RazorFeatherProjectileEntity razorFeatherProjectile = createProjectile(level, player);
            razorFeatherProjectile.setItem(itemStack);
            razorFeatherProjectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(razorFeatherProjectile);
        }
            player.awardStat(Stats.ITEM_USED.get(this));
            if(!player.getAbilities().instabuild){
                itemStack.shrink(1);
            }

        return level.isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
    }
}
