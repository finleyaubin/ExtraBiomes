package net.winepicfin.extrabiomes.entity.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against ExtraBiomes - Bedrock/packs/BP/entities/jellyfish.json, via
// JellyfishTuning (kept free of Minecraft imports so this can run as a plain unit test without a
// bootstrapped registry).
//
// Not covered here: "minecraft:area_attack" damage_per_tick (2) and "minecraft:mob_effect"
// effect_time (10s / 200 ticks) — JellyfishEntity#tick() applies these as inline literals
// (2.0F, 200) rather than named constants, so there's nothing to assert against without
// spinning up a live entity/level. Extract them to JellyfishTuning if that coverage is wanted.
class JellyfishParityTest {

    @Test
    void healthMatchesBedrock() {
        assertEquals(6.0, JellyfishTuning.MAX_HEALTH);
    }

    @Test
    void movementSpeedMatchesBedrock() {
        assertEquals(0.15, JellyfishTuning.MOVEMENT_SPEED);
    }

    @Test
    void grayAmountLerpMatchesBedrock_eightSecondTransition() {
        // Bedrock's pre_animation gray_amount lerp fully transitions over ~8 seconds (160 ticks)
        // when entering/leaving water.
        assertEquals(1.0F / 160.0F, JellyfishTuning.GRAY_STEP_PER_TICK);
    }
}
