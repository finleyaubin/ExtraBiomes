package net.winepicfin.extrabiomes.fabric.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.gametest.shared.JellyfishBreathingScenario;
import org.slf4j.Logger;

public class JellyfishBreathingGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void jellyfishDoesNotDrownInWaterOrOnLand(GameTestHelper helper) {
        new JellyfishBreathingScenario(helper).assertJellyfishDoesNotDrownInWaterOrOnLand();
        LOGGER.info("[JellyfishBreathingGameTests] jellyfishDoesNotDrownInWaterOrOnLand: passed");
        helper.succeed();
    }
}
