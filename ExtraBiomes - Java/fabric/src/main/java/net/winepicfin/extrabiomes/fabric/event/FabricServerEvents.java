package net.winepicfin.extrabiomes.fabric.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

// Fabric equivalent of forge/.../event/DenseCloudSlowFallingHandler.java and the per-tick half of
// forge/.../event/WolfFrogHatHandler.java. Fabric API has no generic "PlayerTickEvent"/
// "LivingTickEvent" - iterating players/wolves once per server/world tick is the direct
// replacement (see class javadocs on the Forge originals for the Bedrock-parity behavior being
// mirrored here).
public class FabricServerEvents {
    private static final int MIN_SCAN_DISTANCE = 4;
    private static final int MAX_SCAN_DISTANCE = 40;
    private static final double MIN_FALL_SPEED = 0.5;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                applyDenseCloudSlowFalling(player);
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(FabricServerEvents::tickWolves);
    }

    private static void applyDenseCloudSlowFalling(Player player) {
        Level level = player.level();
        double fallSpeed = -player.getDeltaMovement().y;
        if (fallSpeed <= MIN_FALL_SPEED) return;

        int scanDistance = Mth.clamp((int) Math.floor(fallSpeed * 3), MIN_SCAN_DISTANCE, MAX_SCAN_DISTANCE);
        BlockPos playerPos = player.blockPosition();
        int lowestY = Math.max(level.getMinBuildHeight(), playerPos.getY() - scanDistance);

        for (int y = playerPos.getY(); y >= lowestY; y--) {
            BlockState state = level.getBlockState(new BlockPos(playerPos.getX(), y, playerPos.getZ()));
            if (isDenseCloudBlock(state)) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 30, 1, false, false));
                return;
            }
        }
    }

    private static boolean isDenseCloudBlock(BlockState state) {
        return state.is(ModBlocks.DENSE_CLOUD.get())
                || state.is(ModBlocks.DENSE_CLOUD_BRICK.get())
                || state.is(ModBlocks.DENSE_CLOUD_BRICK_SLAB.get())
                || state.is(ModBlocks.DENSE_CLOUD_BRICK_STAIRS.get());
    }

    private static void tickWolves(ServerLevel level) {
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof Wolf wolf)) continue;
            if (wolf.getItemBySlot(EquipmentSlot.HEAD).getItem() != ModItems.FROG_HELMET.get()) continue;
            if (wolf.isInWaterOrBubble()) continue;

            wolf.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 220, 1, false, false, true));
            wolf.addEffect(new MobEffectInstance(MobEffects.JUMP, 220, 1, false, false, true));
        }
    }
}
