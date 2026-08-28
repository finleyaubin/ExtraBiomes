package net.winepicfin.extrabiomes.event;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;

/**
 * Java port of Bedrock's "packs/BP/scripts/blocks/dense_cloud_effect.js": every tick, a player
 * falling fast enough gets a brief Slow Falling effect if one of the dense cloud block variants
 * is somewhere in the column below them, so they touch down softly instead of taking fall damage.
 * <p>
 * Bedrock computes fallSpeed from -velocity.y, skips players not falling faster than 0.5, scans a
 * BlockVolume straight down by clamp(4, 40, floor(fallSpeed * 3)) blocks (bounded by world height),
 * and applies {@code slow_falling} (duration 30, amplifier 1, no particles) if any of
 * {@code extrabiomes:dense_cloud}/{@code dense_cloud_brick}/{@code _slab}/{@code _stairs} is found.
 * This mirrors that exactly, re-applying the 30-tick effect every tick the condition holds so it
 * persists smoothly through the landing.
 */
@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID)
public class DenseCloudSlowFallingHandler {
    private static final int MIN_SCAN_DISTANCE = 4;
    private static final int MAX_SCAN_DISTANCE = 40;
    private static final double MIN_FALL_SPEED = 0.5;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) return;

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
}
