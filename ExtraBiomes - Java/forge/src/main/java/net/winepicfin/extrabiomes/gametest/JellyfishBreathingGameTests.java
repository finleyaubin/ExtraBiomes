package net.winepicfin.extrabiomes.gametest;

import net.winepicfin.extrabiomes.gametest.shared.JellyfishBreathingScenarios;
import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import org.slf4j.Logger;

@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class JellyfishBreathingGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest(template = "empty")
    public static void jellyfishDoesNotDrownInWaterOrOnLand(GameTestHelper helper) {
        JellyfishBreathingScenarios.assertJellyfishDoesNotDrownInWaterOrOnLand(helper);
        LOGGER.info("[JellyfishBreathingGameTests] jellyfishDoesNotDrownInWaterOrOnLand: passed");
        helper.succeed();
    }
}
