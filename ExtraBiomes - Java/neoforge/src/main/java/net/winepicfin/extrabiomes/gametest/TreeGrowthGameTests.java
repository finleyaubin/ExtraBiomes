package net.winepicfin.extrabiomes.gametest;

import net.winepicfin.extrabiomes.gametest.shared.TreeGrowthChecks;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;

@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class TreeGrowthGameTests {
    @GameTest(template = "empty", batch = "extrabiomes")
    public static void everyTreeGrowsFromSaplingWithBoneMeal(GameTestHelper helper) {
        new TreeGrowthChecks(helper).everyTreeGrowsFromSaplingWithBoneMeal();
    }

    @GameTest(template = "empty", batch = "extrabiomes", timeoutTicks = 1200)
    public static void everyTreeGrowsNaturallyFromRandomTicks(GameTestHelper helper) {
        new TreeGrowthChecks(helper).everyTreeGrowsNaturallyFromRandomTicks();
    }

    @GameTest(template = "empty", batch = "extrabiomes")
    public static void everyTreeLeafDecaysOnceItsTrunkIsBroken(GameTestHelper helper) {
        new TreeGrowthChecks(helper).everyTreeLeafDecaysOnceItsTrunkIsBroken();
    }
}
