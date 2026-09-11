package net.winepicfin.extrabiomes.block;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against standalone Bedrock blocks that don't have their own custom Block subclass
// (see MiscBlockTuning), read live rather than copy-pasted.
class MiscBlockParityTest {

    @Test
    void denseCloudDestroySecondsMatchesBedrock() {
        BedrockEntityJson bedrock = BedrockEntityJson.load("BP/blocks/dense_cloud.json");
        assertEquals(bedrock.getFloat("minecraft:block", "components", "minecraft:destructible_by_mining",
                        "seconds_to_destroy"),
                MiscBlockTuning.DENSE_CLOUD_DESTROY_SECONDS);
    }

    @Test
    void denseCloudBrickDestroySecondsMatchesBedrock() {
        BedrockEntityJson bedrock = BedrockEntityJson.load("BP/blocks/dense_cloud_brick.json");
        assertEquals(bedrock.getFloat("minecraft:block", "components", "minecraft:destructible_by_mining",
                        "seconds_to_destroy"),
                MiscBlockTuning.DENSE_CLOUD_BRICK_DESTROY_SECONDS);
    }

    @Test
    void glowMushroomBlockLightEmissionMatchesBedrock() {
        BedrockEntityJson bedrock = BedrockEntityJson.load("BP/blocks/mushrooms/glow_mushroom_block.json");
        assertEquals(bedrock.get("minecraft:block", "components", "minecraft:light_emission").getAsInt(),
                MiscBlockTuning.GLOW_MUSHROOM_BLOCK_LIGHT_EMISSION);
    }
}
