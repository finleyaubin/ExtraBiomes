package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import org.slf4j.Logger;

import java.util.List;

@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class LogStrippingGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    private record Strip(Block log, Block stripped) {
    }

    @GameTest(template = "empty", batch = "extrabiomes")
    public static void everyModLogAndWoodStripsWithAnAxe(GameTestHelper helper) {
        List<Strip> strips = List.of(
                new Strip(ModBlocks.MYSTIC_LOG.get(), ModBlocks.STRIPPED_MYSTIC_LOG.get()),
                new Strip(ModBlocks.MYSTIC_WOOD.get(), ModBlocks.STRIPPED_MYSTIC_WOOD.get()),
                new Strip(ModBlocks.SKY_LOG.get(), ModBlocks.STRIPPED_SKY_LOG.get()),
                new Strip(ModBlocks.SKY_WOOD.get(), ModBlocks.STRIPPED_SKY_WOOD.get()),
                new Strip(ModBlocks.GILDED_SKY_LOG.get(), ModBlocks.STRIPPED_GILDED_SKY_LOG.get()),
                new Strip(ModBlocks.GILDED_SKY_WOOD.get(), ModBlocks.STRIPPED_GILDED_SKY_WOOD.get()),
                new Strip(ModBlocks.PALM_LOG.get(), ModBlocks.STRIPPED_PALM_LOG.get()),
                new Strip(ModBlocks.PALM_WOOD.get(), ModBlocks.STRIPPED_PALM_WOOD.get()));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_AXE));

        for (int i = 0; i < strips.size(); i++) {
            Strip strip = strips.get(i);
            BlockPos pos = new BlockPos(i, 2, 1);
            helper.setBlock(pos, strip.log());
            helper.useBlock(pos, player);
            helper.assertTrue(helper.getBlockState(pos).is(strip.stripped()),
                    "Axe did not strip " + strip.log().getDescriptionId() + ", got " + helper.getBlockState(pos).getBlock().getDescriptionId());
        }
        LOGGER.info("[LogStrippingGameTests] everyModLogAndWoodStripsWithAnAxe: passed");
        helper.succeed();
    }
}
