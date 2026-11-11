package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.block.custom.EverWickBlock;
import org.slf4j.Logger;

public class TallowHollowsBlockGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void everWickSnuffsByHandAndRelightsWithFlintAndSteel(GameTestHelper helper) {
        LOGGER.info("[TallowHollowsBlockGameTests] everWickSnuffsByHandAndRelightsWithFlintAndSteel: starting");
        BlockPos wick = new BlockPos(1, 2, 1);
        helper.setBlock(wick.below(), Blocks.STONE);
        helper.setBlock(wick, ModBlocks.EVER_WICK.get());
        helper.assertBlockProperty(wick, EverWickBlock.LIT, true);
        assertLight(helper, wick, EverWickBlock.LIT_LIGHT_LEVEL);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.useBlock(wick, player);
        helper.assertBlockProperty(wick, EverWickBlock.LIT, false);
        assertLight(helper, wick, 0);

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FLINT_AND_STEEL));
        helper.useBlock(wick, player);
        helper.assertBlockProperty(wick, EverWickBlock.LIT, true);
        assertLight(helper, wick, EverWickBlock.LIT_LIGHT_LEVEL);

        LOGGER.info("[TallowHollowsBlockGameTests] everWickSnuffsByHandAndRelightsWithFlintAndSteel: passed");
        helper.succeed();
    }

    public static void waxDripFallsWhenTheBlockAboveIsRemoved(GameTestHelper helper) {
        LOGGER.info("[TallowHollowsBlockGameTests] waxDripFallsWhenTheBlockAboveIsRemoved: starting");
        BlockPos drip = new BlockPos(1, 2, 1);
        helper.setBlock(drip.above(), Blocks.STONE);
        helper.setBlock(drip, ModBlocks.WAX_DRIP.get());
        helper.assertBlockPresent(ModBlocks.WAX_DRIP.get(), drip);

        helper.setBlock(drip.above(), Blocks.AIR);
        helper.assertBlockNotPresent(ModBlocks.WAX_DRIP.get(), drip);

        LOGGER.info("[TallowHollowsBlockGameTests] waxDripFallsWhenTheBlockAboveIsRemoved: passed");
        helper.succeed();
    }

    private static void assertLight(GameTestHelper helper, BlockPos pos, int expected) {
        int emitted = helper.getBlockState(pos).getLightEmission();
        helper.assertTrue(emitted == expected, Component.literal("Ever-wick emits light " + emitted + ", expected " + expected));
    }
}
