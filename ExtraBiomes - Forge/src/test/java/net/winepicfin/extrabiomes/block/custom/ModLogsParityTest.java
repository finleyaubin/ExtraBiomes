package net.winepicfin.extrabiomes.block.custom;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against every ModLogs-based wood type's log block
// (ExtraBiomes - Bedrock/packs/BP/blocks/<wood>_wood/<wood>_log.json), read live rather than
// copy-pasted. All four share the same "minecraft:destructible_by_mining".seconds_to_destroy (2).
class ModLogsParityTest {

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"mystic_wood/mystic_log.json", "sky_wood/sky_log.json",
            "palm_wood/palm_log.json", "gilded_sky_wood/gilded_sky_log.json"})
    void destroySecondsMatchesBedrock_secondsToDestroy(String relativePath) {
        // Vanilla's own oak log (Java and Bedrock both use hardness/seconds_to_destroy 2) is the
        // baseline this mod's Bedrock logs also use; the Java port currently sets 5, well above
        // both. Pinned to the Bedrock source value on purpose so this fails until it's
        // reconciled (either bump ModLogsTuning.DESTROY_SECONDS to 2 or document why these woods
        // are intentionally tougher than vanilla/Bedrock).
        BedrockEntityJson bedrock = BedrockEntityJson.load("BP/blocks/" + relativePath);
        assertEquals(bedrock.getFloat("minecraft:block", "components", "minecraft:destructible_by_mining",
                        "seconds_to_destroy"),
                ModLogsTuning.DESTROY_SECONDS);
    }
}
