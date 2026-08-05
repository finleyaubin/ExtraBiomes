package net.winepicfin.extrabiomes.entity.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against the "minecraft:adult" component group in
// ExtraBiomes - Bedrock/packs/BP/entities/giant_tortoise.json, via GiantTortoiseTuning (kept free
// of Minecraft imports so this can run as a plain unit test without a bootstrapped registry).
class GiantTortoiseParityTest {

    @Test
    void healthMatchesBedrock() {
        assertEquals(70.0, GiantTortoiseTuning.MAX_HEALTH);
    }

    @Test
    void attackDamageMatchesBedrock() {
        assertEquals(10.0, GiantTortoiseTuning.ATTACK_DAMAGE);
    }

    @Test
    void followRangeMatchesBedrock_nearestAttackableTargetWithinRadius() {
        assertEquals(25.0, GiantTortoiseTuning.FOLLOW_RANGE);
    }

    @Test
    void movementSpeedMatchesBedrock_adultMovementValue() {
        // Bedrock's adult "minecraft:movement" value is 0.3; the Java port currently sets 0.15.
        // Pinned to the Bedrock source value on purpose so this fails until it's reconciled
        // (either bump the Java value to 0.3 or document why half-speed is intentional).
        assertEquals(0.3, GiantTortoiseTuning.MOVEMENT_SPEED);
    }
}
