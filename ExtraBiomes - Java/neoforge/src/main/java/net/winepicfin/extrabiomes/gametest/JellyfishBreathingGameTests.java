package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.gametest.shared.JellyfishBreathingScenario;
import org.slf4j.Logger;

@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class JellyfishBreathingGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest(template = "empty", batch = "extrabiomes")
    public static void jellyfishDoesNotDrownInWaterOrOnLand(GameTestHelper helper) {
        new JellyfishBreathingScenario(helper).assertJellyfishDoesNotDrownInWaterOrOnLand();
        LOGGER.info("[JellyfishBreathingGameTests] jellyfishDoesNotDrownInWaterOrOnLand: passed");
        helper.succeed();
    }
}
