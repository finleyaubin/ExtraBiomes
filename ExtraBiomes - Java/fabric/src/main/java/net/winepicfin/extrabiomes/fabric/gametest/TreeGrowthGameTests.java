package net.winepicfin.extrabiomes.fabric.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.gametest.shared.TreeGrowthScenarios;
import org.slf4j.Logger;

public class TreeGrowthGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void everyTreeGrowsFromSaplingWithBoneMeal(GameTestHelper helper) {
        TreeGrowthScenarios.assertEveryTreeGrowsWithBoneMeal(helper);
        LOGGER.info("[TreeGrowthGameTests] everyTreeGrowsFromSaplingWithBoneMeal: passed");
        helper.succeed();
    }

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty", timeoutTicks = 1200)
    public static void everyTreeGrowsNaturallyFromRandomTicks(GameTestHelper helper) {
        Runnable growthCheck = TreeGrowthScenarios.plantForNaturalGrowth(helper);
        helper.succeedWhen(() -> {
            growthCheck.run();
            LOGGER.info("[TreeGrowthGameTests] everyTreeGrowsNaturallyFromRandomTicks: passed");
        });
    }

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void everyTreeLeafDecaysOnceItsTrunkIsBroken(GameTestHelper helper) {
        TreeGrowthScenarios.assertEveryTreeLeafDecaysOnceItsTrunkIsBroken(helper);
        LOGGER.info("[TreeGrowthGameTests] everyTreeLeafDecaysOnceItsTrunkIsBroken: passed");
        helper.succeed();
    }
}
