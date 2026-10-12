package net.winepicfin.extrabiomes.fabric.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.gametest.shared.TreeGrowthChecks;

public class TreeGrowthGameTests {
    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void everyTreeGrowsFromSaplingWithBoneMeal(GameTestHelper helper) {
        new TreeGrowthChecks(helper).everyTreeGrowsFromSaplingWithBoneMeal();
    }

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty", timeoutTicks = 1200)
    public static void everyTreeGrowsNaturallyFromRandomTicks(GameTestHelper helper) {
        new TreeGrowthChecks(helper).everyTreeGrowsNaturallyFromRandomTicks();
    }

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty")
    public static void everyTreeLeafDecaysOnceItsTrunkIsBroken(GameTestHelper helper) {
        new TreeGrowthChecks(helper).everyTreeLeafDecaysOnceItsTrunkIsBroken();
    }
}
