package net.winepicfin.extrabiomes.entity.ai;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Parity checks against the "minecraft:behavior.ram_attack" component in
// ExtraBiomes - Bedrock/packs/BP/entities/giant_tortoise.json.
class GiantTortoiseChargeGoalParityTest {

    @Test
    void minRamDistanceMatchesBedrock() throws Exception {
        assertEquals(4.0, readStaticDouble("MIN_RAM_DISTANCE"));
    }

    @Test
    void ramDistanceMatchesBedrock() throws Exception {
        assertEquals(7.0, readStaticDouble("RAM_DISTANCE"));
    }

    @Test
    void ramSpeedMultiplierEncodesBedrocksDoubleSpeed() throws Exception {
        // Bedrock: run_speed 1, ram_speed 2 (i.e. 2x). The Java port applies this as a
        // MULTIPLY_TOTAL AttributeModifier, whose value is the ADDITIONAL fraction on top of the
        // base (1.0 == +100% == 2x) rather than the raw "2" — see applySpeedBoost().
        assertEquals(1.0, readStaticDouble("RAM_SPEED_MULTIPLIER"));
    }

    private static double readStaticDouble(String fieldName) throws Exception {
        Field field = GiantTortoiseChargeGoal.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getDouble(null);
    }
}
