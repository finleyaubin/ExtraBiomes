package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.projectile.PebbleProjectileEntity;
import net.winepicfin.extrabiomes.item.ModItems;
import org.slf4j.Logger;

@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class PebbleChargeGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int USE_DURATION = 72000;

    @GameTest(template = "empty", batch = "extrabiomes")
    public static void pebbleThrowsOnlyAfterEightTenthsOfASecondCharge(GameTestHelper helper) {
        LOGGER.info("[PebbleChargeGameTests] pebbleThrowsOnlyAfterEightTenthsOfASecondCharge: starting");
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(helper.absoluteVec(new Vec3(1.5, 2.0, 1.5)));
        ItemStack pebbles = new ItemStack(ModItems.PEBBLE.get(), 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, pebbles);

        pebbles.getItem().releaseUsing(pebbles, helper.getLevel(), player, USE_DURATION - 15);
        helper.assertTrue(thrownPebbles(helper, player) == 0, "Released after 15 ticks but a pebble was thrown");
        helper.assertTrue(pebbles.getCount() == 2, "Released after 15 ticks but a pebble was used up");

        pebbles.getItem().releaseUsing(pebbles, helper.getLevel(), player, USE_DURATION - 16);
        helper.assertTrue(thrownPebbles(helper, player) == 1, "Released after 16 ticks but no pebble was thrown");
        helper.assertTrue(pebbles.getCount() == 1, "Expected one pebble used up but " + pebbles.getCount() + " were left");

        LOGGER.info("[PebbleChargeGameTests] pebbleThrowsOnlyAfterEightTenthsOfASecondCharge: passed");
        helper.succeed();
    }

    private static int thrownPebbles(GameTestHelper helper, Player player) {
        return helper.getLevel().getEntitiesOfClass(PebbleProjectileEntity.class, player.getBoundingBox().inflate(20.0)).size();
    }
}
