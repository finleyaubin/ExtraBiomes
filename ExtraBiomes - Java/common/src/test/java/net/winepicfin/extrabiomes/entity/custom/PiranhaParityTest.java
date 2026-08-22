package net.winepicfin.extrabiomes.entity.custom;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Parity checks against ExtraBiomes - Bedrock/packs/BP/entities/piranha.json, read live rather
// than copy-pasted, so the test breaks if the Bedrock source changes instead of silently going
// stale.
class PiranhaParityTest {
    private static BedrockEntityJson bedrock;

    @BeforeAll
    static void loadBedrockSource() {
        bedrock = BedrockEntityJson.load("BP/entities/piranha.json");
    }

    @Test
    void healthMatchesBedrock_scaleSmallVariant() {
        // Bedrock rolls one of three scale component groups on spawn (6/7/8 health); the Java port
        // has no size variants, so it pins the smallest.
        assertEquals(bedrock.getDouble("minecraft:entity", "component_groups", "scale_small",
                        "minecraft:health", "value"),
                PiranhaTuning.MAX_HEALTH);
    }

    @Test
    void meleeAttackSpeedMatchesBedrock() {
        assertEquals(bedrock.getDouble("minecraft:entity", "components", "minecraft:behavior.melee_attack",
                        "speed_multiplier"),
                PiranhaTuning.MELEE_ATTACK_SPEED);
    }

    @Test
    void swimsFasterThanVanillaDolphin() {
        // Bedrock's underwater_movement (0.3) is double a Bedrock dolphin's, and Java's swim
        // acceleration works out as goalSpeed * MOVEMENT_SPEED * inWaterSpeedModifier. Dolphin's
        // cruise is 1.0 * 1.2 * 0.02; a piranha must at least beat that.
        double piranhaCruise = PiranhaTuning.RANDOM_SWIM_SPEED * PiranhaTuning.MOVEMENT_SPEED
                * PiranhaTuning.IN_WATER_SPEED_MODIFIER;
        assertTrue(piranhaCruise > 1.0 * 1.2 * 0.02,
                "piranha cruise acceleration " + piranhaCruise + " should exceed vanilla Dolphin's 0.024");
    }
}
