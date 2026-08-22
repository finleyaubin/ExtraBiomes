package net.winepicfin.extrabiomes.entity.ai;

import net.winepicfin.extrabiomes.testutil.BedrockEntityJson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against the "minecraft:behavior.ram_attack" component in
// ExtraBiomes - Bedrock/packs/BP/entities/giant_tortoise.json, read live rather than copy-pasted,
// so the test breaks if the Bedrock source changes instead of silently going stale.
class GiantTortoiseChargeGoalParityTest {
    private static BedrockEntityJson bedrock;

    @BeforeAll
    static void loadBedrockSource() {
        bedrock = BedrockEntityJson.load("BP/entities/giant_tortoise.json");
    }

    private static double ramAttack(String field) {
        return bedrock.getDouble("minecraft:entity", "component_groups", "minecraft:adult",
                "minecraft:behavior.ram_attack", field);
    }

    @Test
    void minRamDistanceMatchesBedrock() throws Exception {
        assertEquals(ramAttack("min_ram_distance"), readStaticDouble("MIN_RAM_DISTANCE"));
    }

    @Test
    void ramDistanceMatchesBedrock() throws Exception {
        assertEquals(ramAttack("ram_distance"), readStaticDouble("RAM_DISTANCE"));
    }

    @Test
    void ramSpeedMultiplierEncodesBedrocksSpeedRatio() throws Exception {
        // Bedrock's run_speed/ram_speed are absolute speeds; the Java port applies the ratio
        // between them as a MULTIPLY_TOTAL AttributeModifier, whose value is the ADDITIONAL
        // fraction on top of the base (e.g. ram_speed:2 vs run_speed:1 -> +100% -> 2x) rather
        // than either raw number — see applySpeedBoost().
        double expectedMultiplier = ramAttack("ram_speed") / ramAttack("run_speed") - 1.0;
        assertEquals(expectedMultiplier, readStaticDouble("RAM_SPEED_MULTIPLIER"));
    }

    private static double readStaticDouble(String fieldName) throws Exception {
        Field field = GiantTortoiseChargeGoal.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getDouble(null);
    }
}
