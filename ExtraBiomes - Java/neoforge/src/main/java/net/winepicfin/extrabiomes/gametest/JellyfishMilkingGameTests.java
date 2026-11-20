package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.custom.JellyfishEntity;
import net.winepicfin.extrabiomes.entity.custom.JellyfishTuning;
import net.winepicfin.extrabiomes.item.ModItems;
import org.slf4j.Logger;

// Mirrors the "milk_available"/"milk_cooldown" component-group swap in
// ExtraBiomes - Bedrock/packs/BP/entities/jellyfish.json: one jellyfish yields jam once per
// thirty seconds, and a second bottle pressed before the timer expires is simply ignored.
public class JellyfishMilkingGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void milkingIsOnAThirtySecondCooldown(GameTestHelper helper) {
        JellyfishEntity jellyfish = helper.spawn(ModEntities.JELLYFISH.get(), 2, 3, 2);
        jellyfish.setNoAi(true);
        // Far enough that the jellyfish's contact damage can't kill the mock player while the
        // cooldown ticks out, and mobInteract needs no reach check anyway.
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        BlockPos standOff = helper.absolutePos(new BlockPos(0, 3, 0));
        player.teleportTo(standOff.getX() + 0.5, standOff.getY(), standOff.getZ() + 0.5);

        milkOnce(player, jellyfish);
        helper.assertTrue(jellyfish.milkCooldownRemaining() == JellyfishTuning.MILK_COOLDOWN_TICKS,
                Component.literal("Milking did not arm the cooldown, remaining " + jellyfish.milkCooldownRemaining()));

        giveBottle(player);
        jellyfish.mobInteract(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.GLASS_BOTTLE),
                Component.literal("Jellyfish was milked again during its cooldown, hand holds " + player.getItemInHand(InteractionHand.MAIN_HAND).getItem().getDescriptionId()));
        helper.assertTrue(jellyfish.milkCooldownRemaining() > 0,
                Component.literal("Cooldown did not survive a blocked milking attempt, remaining " + jellyfish.milkCooldownRemaining()));

        helper.runAfterDelay(JellyfishTuning.MILK_COOLDOWN_TICKS + 1, () -> {
            milkOnce(player, jellyfish);
            LOGGER.info("[JellyfishMilkingGameTests] milkingIsOnAThirtySecondCooldown: passed");
            helper.succeed();
        });
    }

    private static void milkOnce(ServerPlayer player, JellyfishEntity jellyfish) {
        giveBottle(player);
        InteractionResult result = jellyfish.mobInteract(player, InteractionHand.MAIN_HAND);
        if (result != InteractionResult.SUCCESS) {
            throw new AssertionError("First milking attempt returned " + result);
        }
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!held.is(ModItems.JELLYFISH_JAM_BOTTLE.get())) {
            throw new AssertionError("Milking did not produce a jellyfish jam bottle, hand holds " + held.getItem().getDescriptionId());
        }
    }

    private static void giveBottle(ServerPlayer player) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE));
    }
}
