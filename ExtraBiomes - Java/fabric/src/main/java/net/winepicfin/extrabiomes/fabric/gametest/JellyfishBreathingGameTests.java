package net.winepicfin.extrabiomes.fabric.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.gametest.JellyfishBreathingChecks;

public class JellyfishBreathingGameTests {
    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void jellyfishDoesNotDrownInWaterOrOnLand(GameTestHelper helper) {
        new JellyfishBreathingChecks(helper).jellyfishDoesNotDrownInWaterOrOnLand();
    }
}
