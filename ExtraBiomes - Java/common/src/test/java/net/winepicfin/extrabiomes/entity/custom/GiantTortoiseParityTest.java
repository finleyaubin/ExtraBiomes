package net.winepicfin.extrabiomes.entity.custom;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against ExtraBiomes - Bedrock/packs/BP/entities/giant_tortoise.json, read live
// rather than copy-pasted, so the test breaks if the Bedrock source changes instead of silently
// going stale.
class GiantTortoiseParityTest {
    private static BedrockEntityJson bedrock;

    @BeforeAll
    static void loadBedrockSource() {
        bedrock = BedrockEntityJson.load("BP/entities/giant_tortoise.json");
    }

    @Test
    void healthMatchesBedrock() {
        assertEquals(bedrock.getDouble("minecraft:entity", "components", "minecraft:health", "value"),
                GiantTortoiseTuning.MAX_HEALTH);
    }

    @Test
    void attackDamageMatchesBedrock() {
        assertEquals(bedrock.getDouble("minecraft:entity", "component_groups", "minecraft:adult",
                        "minecraft:attack", "damage"),
                GiantTortoiseTuning.ATTACK_DAMAGE);
    }

    @Test
    void followRangeMatchesBedrock_nearestAttackableTargetWithinRadius() {
        assertEquals(bedrock.getDouble("minecraft:entity", "component_groups", "minecraft:adult",
                        "minecraft:behavior.nearest_attackable_target", "within_radius"),
                GiantTortoiseTuning.FOLLOW_RANGE);
    }

    @Test
    void movementSpeedMatchesBedrock() {
        // Java's Attributes.MOVEMENT_SPEED plays the same role as Bedrock's top-level
        // "minecraft:movement" component (both scale the entity's base walk speed the same way -
        // vanilla's own Bedrock/Java mob pairs, e.g. zombies, use matching values for it).
        // The Java port currently sets 0.15 against Bedrock's 0.3; pinned to the Bedrock source
        // value on purpose so this fails until it's reconciled (either bump the Java value or
        // document why half-speed is intentional).
        assertEquals(bedrock.getDouble("minecraft:entity", "components", "minecraft:movement", "value"),
                GiantTortoiseTuning.MOVEMENT_SPEED);
    }
}
