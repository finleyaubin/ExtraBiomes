package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.entity.custom.projectile.BaitProjectileEntity;

// Ported from Bedrock extrabiomes:bait — thrown to draw piranhas away from the player.
public class BaitItem extends Item {
    private static final int COOLDOWN_TICKS = 10;

    public BaitItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            BaitProjectileEntity bait = new BaitProjectileEntity(level, player);
            bait.setItem(itemStack);
            // A bait picked back up via BaitProjectileEntity.interact() carries its remaining health over
            // as this item's damage value - re-throwing it should start from that same health, not full.
            bait.setHealth(BaitProjectileEntity.MAX_HEALTH - itemStack.getDamageValue());
            bait.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(bait);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
