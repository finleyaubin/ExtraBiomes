package net.winepicfin.extrabiomes.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.item.FrogHelmetEffects;
import net.winepicfin.extrabiomes.item.ModItems;

/**
 * Java port of Bedrock's wolf.json "minecraft:wolf_armorable" interact behavior: a tamed wolf's
 * owner can right-click it with extrabiomes:frog_helmet (mainhand, not sneaking) to equip it on
 * the wolf's head, mirroring vanilla wolf armor, and remove it again with shears. While worn and
 * the wolf isn't in water, the wolf gets the same water_breathing/jump_boost effects the item
 * grants a player wearing it (Bedrock's environment_sensor -> add_frog/remove_frog component
 * groups on skin_id).
 */
@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID)
public class WolfFrogHatHandler {
    @SubscribeEvent
    public static void onWolfInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Wolf wolf)) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Player player = event.getEntity();
        if (!wolf.isTame() || !wolf.isOwnedBy(player) || player.isShiftKeyDown()) return;

        ItemStack heldItem = event.getItemStack();
        ItemStack headItem = wolf.getItemBySlot(EquipmentSlot.HEAD);

        boolean equipping = heldItem.getItem() == ModItems.FROG_HELMET.get() && headItem.isEmpty();
        boolean shearing = heldItem.getItem() == Items.SHEARS && headItem.getItem() == ModItems.FROG_HELMET.get();
        if (!equipping && !shearing) return;

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (wolf.level().isClientSide) return;

        if (equipping) {
            wolf.setItemSlot(EquipmentSlot.HEAD, heldItem.copy());
            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }
            wolf.level().playSound(null, wolf, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else {
            wolf.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            wolf.level().addFreshEntity(new ItemEntity(wolf.level(), wolf.getX(), wolf.getY() + 0.5D, wolf.getZ(), headItem));
            heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            wolf.level().playSound(null, wolf, SoundEvents.SHEEP_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onWolfTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Wolf wolf) || wolf.level().isClientSide) return;
        if (wolf.getItemBySlot(EquipmentSlot.HEAD).getItem() != ModItems.FROG_HELMET.get()) return;
        if (wolf.isInWaterOrBubble()) return;

        wolf.addEffect(FrogHelmetEffects.wolfWaterBreathing());
        wolf.addEffect(FrogHelmetEffects.wolfJumpBoost());
    }
}
