package net.winepicfin.extrabiomes.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Shared status-effect constants for the frog helmet, worn by either a player or a wolf. The
 * Forge and Fabric loaders each have their own copy of the frog-helmet item and tick handler
 * (see forge/.../item/custom/FrogHelmetItem.java, fabric/.../item/custom/FrogHelmetItem.java,
 * forge/.../event/WolfFrogHatHandler.java, fabric/.../event/FabricServerEvents.java) because the
 * armor-render hook and tick-event APIs differ per loader, but the water_breathing/jump_boost
 * effects themselves are the same gameplay feature on both loaders and must stay in sync. Centralizing
 * the constants and factory methods here means the two loader copies cannot silently drift apart.
 *
 * <p>{@link MobEffectInstance} is mutable and consumed by {@code LivingEntity#addEffect}, so this
 * class exposes factory methods that return a fresh instance per call rather than shared static
 * instances - handing the same instance to two different entities would be a bug.
 */
public final class FrogHelmetEffects {
    /** Effect duration, in ticks, for a player wearing the frog helmet. */
    public static final int PLAYER_EFFECT_DURATION_TICKS = 200;

    /** Effect duration, in ticks, for a wolf wearing the frog helmet. */
    public static final int WOLF_EFFECT_DURATION_TICKS = 220;

    private static final int AMPLIFIER = 1;
    private static final boolean AMBIENT = false;
    private static final boolean VISIBLE = false;
    private static final boolean SHOW_ICON = true;

    private FrogHelmetEffects() {
    }

    public static MobEffectInstance playerWaterBreathing() {
        return new MobEffectInstance(MobEffects.WATER_BREATHING, PLAYER_EFFECT_DURATION_TICKS, AMPLIFIER, AMBIENT, VISIBLE, SHOW_ICON);
    }

    public static MobEffectInstance playerJumpBoost() {
        return new MobEffectInstance(MobEffects.JUMP, PLAYER_EFFECT_DURATION_TICKS, AMPLIFIER, AMBIENT, VISIBLE, SHOW_ICON);
    }

    public static MobEffectInstance wolfWaterBreathing() {
        return new MobEffectInstance(MobEffects.WATER_BREATHING, WOLF_EFFECT_DURATION_TICKS, AMPLIFIER, AMBIENT, VISIBLE, SHOW_ICON);
    }

    public static MobEffectInstance wolfJumpBoost() {
        return new MobEffectInstance(MobEffects.JUMP, WOLF_EFFECT_DURATION_TICKS, AMPLIFIER, AMBIENT, VISIBLE, SHOW_ICON);
    }
}
