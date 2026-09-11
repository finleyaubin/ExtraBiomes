package net.winepicfin.extrabiomes.block.custom;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against ExtraBiomes - Bedrock/packs/BP/blocks/stick_pile.json, read live rather
// than copy-pasted, so the test breaks if the Bedrock source changes instead of silently going
// stale.
class StickPileBlockParityTest {
    private static BedrockEntityJson bedrock;

    @BeforeAll
    static void loadBedrockSource() {
        bedrock = BedrockEntityJson.load("BP/blocks/stick_pile.json");
    }

    @Test
    void destroySecondsMatchesBedrock_secondsToDestroy() {
        assertEquals(bedrock.getFloat("minecraft:block", "components", "minecraft:destructible_by_mining",
                        "seconds_to_destroy"),
                StickPileTuning.DESTROY_SECONDS);
    }

    @Test
    void flammabilityMatchesBedrock_burnOdds() {
        assertEquals(bedrock.get("minecraft:block", "components", "minecraft:flammable", "burn_odds").getAsInt(),
                StickPileTuning.FLAMMABILITY);
    }

    @Test
    void fireSpreadSpeedMatchesBedrock_flameOdds() {
        assertEquals(bedrock.get("minecraft:block", "components", "minecraft:flammable", "flame_odds").getAsInt(),
                StickPileTuning.FIRE_SPREAD_SPEED);
    }
}
