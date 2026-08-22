package net.winepicfin.extrabiomes.fabric.event;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.winepicfin.extrabiomes.item.ModItems;

// Fabric equivalent of the interact half of forge/.../event/WolfFrogHatHandler.java - Fabric API's
// UseEntityCallback is the direct replacement for Forge's PlayerInteractEvent.EntityInteract.
public class WolfFrogHatInteractHandler {
    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!(entity instanceof Wolf wolf)) return InteractionResult.PASS;
            if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            if (!wolf.isTame() || !wolf.isOwnedBy(player) || player.isShiftKeyDown()) return InteractionResult.PASS;

            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack headItem = wolf.getItemBySlot(EquipmentSlot.HEAD);

            boolean equipping = heldItem.getItem() == ModItems.FROG_HELMET.get() && headItem.isEmpty();
            boolean shearing = heldItem.getItem() == Items.SHEARS && headItem.getItem() == ModItems.FROG_HELMET.get();
            if (!equipping && !shearing) return InteractionResult.PASS;

            if (level.isClientSide) return InteractionResult.SUCCESS;

            if (equipping) {
                wolf.setItemSlot(EquipmentSlot.HEAD, heldItem.copy());
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                wolf.level().playSound(null, wolf, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.NEUTRAL, 1.0F, 1.0F);
            } else {
                wolf.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                wolf.level().addFreshEntity(new ItemEntity(wolf.level(), wolf.getX(), wolf.getY() + 0.5D, wolf.getZ(), headItem));
                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                wolf.level().playSound(null, wolf, SoundEvents.SHEEP_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        });
    }
}
