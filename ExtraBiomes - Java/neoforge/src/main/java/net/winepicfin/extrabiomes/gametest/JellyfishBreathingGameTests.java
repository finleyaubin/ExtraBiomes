package net.winepicfin.extrabiomes.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;

@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class JellyfishBreathingGameTests {
    @GameTest(template = "empty", batch = "extrabiomes")
    public static void jellyfishDoesNotDrownInWaterOrOnLand(GameTestHelper helper) {
        new JellyfishBreathingChecks(helper).jellyfishDoesNotDrownInWaterOrOnLand();
    }
}
