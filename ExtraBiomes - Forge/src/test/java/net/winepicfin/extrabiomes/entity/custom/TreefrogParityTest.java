package net.winepicfin.extrabiomes.entity.custom;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against ExtraBiomes - Bedrock/packs/BP/entities/treefrog.json, read live rather
// than copy-pasted, so the test breaks if the Bedrock source changes instead of silently going
// stale.
class TreefrogParityTest {
    private static BedrockEntityJson bedrock;

    @BeforeAll
    static void loadBedrockSource() {
        bedrock = BedrockEntityJson.load("BP/entities/treefrog.json");
    }

    @Test
    void healthMatchesBedrock() {
        assertEquals(bedrock.getDouble("minecraft:entity", "components", "minecraft:health", "value"),
                TreefrogTuning.MAX_HEALTH);
    }

    @Test
    void jumpStrengthMatchesBedrock_jumpStaticJumpPower() {
        // Bedrock's "minecraft:jump.static" jump_power is applied directly as Y velocity — the
        // same role TreefrogEntity#getJumpPower() gives JUMP_STRENGTH here. The Java port
        // currently uses 0.7 against Bedrock's 0.8; pinned to the Bedrock source value on
        // purpose so this fails until it's reconciled.
        assertEquals(bedrock.getDouble("minecraft:entity", "components", "minecraft:jump.static", "jump_power"),
                TreefrogTuning.JUMP_STRENGTH);
    }
}
