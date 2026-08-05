package net.winepicfin.extrabiomes.entity.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against ExtraBiomes - Bedrock/packs/BP/entities/treefrog.json, via TreefrogTuning
// (kept free of Minecraft imports so this can run as a plain unit test without a bootstrapped
// registry).
class TreefrogParityTest {

    @Test
    void healthMatchesBedrock() {
        assertEquals(4.0, TreefrogTuning.MAX_HEALTH);
    }

    @Test
    void jumpStrengthMatchesBedrock_jumpStaticJumpPower() {
        // Bedrock's "minecraft:jump.static" component sets jump_power: 0.8, applied directly as
        // Y velocity — the same role TreefrogEntity#getJumpPower() gives JUMP_STRENGTH here. The
        // Java port currently uses 0.7; pinned to the Bedrock source value on purpose so this
        // fails until it's reconciled.
        assertEquals(0.8, TreefrogTuning.JUMP_STRENGTH);
    }
}
