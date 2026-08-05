package net.winepicfin.extrabiomes.block.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against ExtraBiomes - Bedrock/packs/BP/blocks/stick_pile.json, via
// StickPileTuning (kept free of Minecraft imports so this can run as a plain unit test without
// a bootstrapped registry - constructing an actual Block subclass needs one).
class StickPileBlockParityTest {

    @Test
    void destroySecondsMatchesBedrock_secondsToDestroy() {
        assertEquals(2.5f, StickPileTuning.DESTROY_SECONDS);
    }

    @Test
    void flammabilityMatchesBedrock_burnOdds() {
        assertEquals(50, StickPileTuning.FLAMMABILITY);
    }

    @Test
    void fireSpreadSpeedMatchesBedrock_flameOdds() {
        assertEquals(50, StickPileTuning.FIRE_SPREAD_SPEED);
    }
}
