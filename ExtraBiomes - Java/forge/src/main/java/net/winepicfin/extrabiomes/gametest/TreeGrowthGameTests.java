package net.winepicfin.extrabiomes.gametest;

import net.winepicfin.extrabiomes.gametest.shared.TreeGrowthScenarios;
import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import org.slf4j.Logger;

@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class TreeGrowthGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    @GameTest(template = "empty")
    public static void everyTreeGrowsFromSaplingWithBoneMeal(GameTestHelper helper) {
        TreeGrowthScenarios.assertEveryTreeGrowsWithBoneMeal(helper);
        LOGGER.info("[TreeGrowthGameTests] everyTreeGrowsFromSaplingWithBoneMeal: passed");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 1200)
    public static void everyTreeGrowsNaturallyFromRandomTicks(GameTestHelper helper) {
        Runnable growthCheck = TreeGrowthScenarios.plantForNaturalGrowth(helper);
        helper.succeedWhen(() -> {
            growthCheck.run();
            LOGGER.info("[TreeGrowthGameTests] everyTreeGrowsNaturallyFromRandomTicks: passed");
        });
    }

    @GameTest(template = "empty")
    public static void everyTreeLeafDecaysOnceItsTrunkIsBroken(GameTestHelper helper) {
        TreeGrowthScenarios.assertEveryTreeLeafDecaysOnceItsTrunkIsBroken(helper);
        LOGGER.info("[TreeGrowthGameTests] everyTreeLeafDecaysOnceItsTrunkIsBroken: passed");
        helper.succeed();
    }
}
