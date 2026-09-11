package net.winepicfin.extrabiomes.entity.custom;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against ExtraBiomes - Bedrock/packs/BP/entities/jellyfish.json, read live rather
// than copy-pasted, so the test breaks if the Bedrock source changes instead of silently going
// stale.
//
// Not covered here:
// - "minecraft:area_attack" damage_per_tick (2) and "minecraft:mob_effect" effect_time
//   (10s / 200 ticks): JellyfishEntity#tick() applies these as inline literals rather than named
//   constants, so there's nothing to assert against without spinning up a live entity/level.
// - The gray_amount/scale_y transition rate: Bedrock defines it as a Molang pre_animation lerp in
//   the client resource-pack .entity.json (script, not a plain numeric field in this behavior
//   pack), so it can't be read the same way; JellyfishTuning's 160-tick (~8s) value is kept as a
//   documented match by inspection instead.
class JellyfishParityTest {
    private static BedrockEntityJson bedrock;

    @BeforeAll
    static void loadBedrockSource() {
        bedrock = BedrockEntityJson.load("BP/entities/jellyfish.json");
    }

    @Test
    void healthMatchesBedrock() {
        assertEquals(bedrock.getDouble("minecraft:entity", "components", "minecraft:health", "value"),
                JellyfishTuning.MAX_HEALTH);
    }

    @Test
    void movementSpeedMatchesBedrock() {
        assertEquals(bedrock.getDouble("minecraft:entity", "components", "minecraft:movement", "value"),
                JellyfishTuning.MOVEMENT_SPEED);
    }
}
