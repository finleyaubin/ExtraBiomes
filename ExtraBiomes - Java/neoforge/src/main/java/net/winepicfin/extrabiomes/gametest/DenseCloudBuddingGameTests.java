package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.block.custom.DenseCloudBlock;
import net.winepicfin.extrabiomes.block.custom.DenseCloudBudding;
import org.slf4j.Logger;

public class DenseCloudBuddingGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TICKS = 150;
    private static final int WATER_DEPTH = 2;
    private static final int GAP = 5;

    public static void denseCloudBudsOnlyAroundIceOverMagmaHeatedWater(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        BlockPos heated = new BlockPos(origin.getX(), 220, origin.getZ());
        BlockPos unheated = heated.east(30);
        BlockPos iceHeated = buildStack(level, heated, Blocks.MAGMA_BLOCK, GAP);
        BlockPos iceUnheated = buildStack(level, unheated, Blocks.STONE, GAP);

        budFor(level, iceHeated.east());
        budFor(level, iceUnheated.east());

        helper.assertTrue(countCloud(level, iceHeated) > 1, Component.literal("Cloud did not bud around ice over magma-heated water"));
        helper.assertTrue(countCloud(level, iceUnheated) == 1, Component.literal("Cloud budded around ice over unheated water"));
        helper.assertTrue(everyCloudInShape(level, iceHeated), Component.literal("Cloud budded outside the allowed cloud shape"));
        LOGGER.info("[DenseCloudBuddingGameTests] {} cloud blocks grew around the heated ice", countCloud(level, iceHeated));
        LOGGER.info("[DenseCloudBuddingGameTests] denseCloudBudsOnlyAroundIceOverMagmaHeatedWater: passed");
        helper.succeed();
    }

    public static void iceMustBeWithinTwentyBlocksOfTheWater(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        BlockPos base = new BlockPos(origin.getX() + 60, 220, origin.getZ());
        BlockPos near = buildStack(level, base, Blocks.MAGMA_BLOCK, DenseCloudBudding.MAX_ICE_HEIGHT_ABOVE_WATER - 1);
        BlockPos far = buildStack(level, base.east(30), Blocks.MAGMA_BLOCK, DenseCloudBudding.MAX_ICE_HEIGHT_ABOVE_WATER);

        helper.assertTrue(DenseCloudBudding.sitsOverHeatedWater(level, near), Component.literal("Ice 20 blocks above the water was rejected"));
        helper.assertFalse(DenseCloudBudding.sitsOverHeatedWater(level, far), Component.literal("Ice 21 blocks above the water was accepted"));
        LOGGER.info("[DenseCloudBuddingGameTests] iceMustBeWithinTwentyBlocksOfTheWater: passed");
        helper.succeed();
    }

    public static void cloudCondenserBuildingDemonstratesTheMechanic(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        BlockPos corner = new BlockPos(origin.getX() + 120, 215, origin.getZ());
        Identifier id = Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "sky_city/buildings/cloud_condenser");
        StructureTemplate template = level.getStructureTemplateManager().getOrCreate(id);
        helper.assertTrue(template.getSize().getX() > 0, Component.literal("cloud_condenser structure failed to load"));
        template.placeInWorld(level, corner, corner, new StructurePlaceSettings(), level.getRandom(), 2);

        BlockPos ice = corner.offset(6, 14, 5);
        helper.assertTrue(level.getBlockState(ice).is(Blocks.BLUE_ICE), Component.literal("Condenser has no blue ice where expected"));
        helper.assertTrue(DenseCloudBudding.sitsOverHeatedWater(level, ice), Component.literal("Condenser ice does not sit over magma-heated water"));
        helper.assertTrue(corner.offset(6, 3, 5).equals(DenseCloudBudding.heatedWaterSurface(level, ice)), Component.literal("Condenser steam would not rise from the top of its water"));
        WrittenBookContent book =level.getBlockEntity(corner.offset(2, 1, 5)) instanceof LecternBlockEntity lectern
                ? lectern.getBook().get(DataComponents.WRITTEN_BOOK_CONTENT) : null;
        String bookText = book == null ? "<no book>" : book.title().raw() + " | " + book.pages().get(0).raw().getString();
        helper.assertTrue(bookText.startsWith("Cloud Condenser | Cloud Condenser"), Component.literal("Condenser lectern book is wrong: " + bookText));

        int before = countCloud(level, ice);
        budFor(level, ice.east());
        helper.assertTrue(countCloud(level, ice) > before, Component.literal("Condenser cloud did not bud"));
        LOGGER.info("[DenseCloudBuddingGameTests] condenser cloud grew from {} to {} blocks", before, countCloud(level, ice));
        LOGGER.info("[DenseCloudBuddingGameTests] cloudCondenserBuildingDemonstratesTheMechanic: passed");
        helper.succeed();
    }

    private static BlockPos buildStack(ServerLevel level, BlockPos base, Block heatSource, int airGap) {
        level.setBlockAndUpdate(base, heatSource.defaultBlockState());
        for (int i = 1; i <= WATER_DEPTH; i++) {
            level.setBlockAndUpdate(base.above(i), Blocks.WATER.defaultBlockState());
        }
        for (int i = 1; i <= airGap; i++) {
            level.setBlockAndUpdate(base.above(WATER_DEPTH + i), Blocks.AIR.defaultBlockState());
        }
        BlockPos ice = base.above(WATER_DEPTH + airGap + 1);
        level.setBlockAndUpdate(ice, Blocks.BLUE_ICE.defaultBlockState());
        level.setBlockAndUpdate(ice.east(), ModBlocks.DENSE_CLOUD.get().defaultBlockState());
        return ice;
    }

    private static void budFor(ServerLevel level, BlockPos seed) {
        DenseCloudBlock cloud = (DenseCloudBlock) ModBlocks.DENSE_CLOUD.get();
        for (int i = 0; i < TICKS; i++) {
            for (BlockPos pos : BlockPos.betweenClosed(seed.offset(-12, -6, -12), seed.offset(12, 6, 12))) {
                if (level.getBlockState(pos).is(cloud)) {
                    cloud.randomTick(cloud.defaultBlockState(), level, pos.immutable(), level.getRandom());
                }
            }
        }
    }

    private static int countCloud(ServerLevel level, BlockPos ice) {
        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(ice.offset(-12, -6, -12), ice.offset(12, 6, 12))) {
            if (level.getBlockState(pos).is(ModBlocks.DENSE_CLOUD.get())) count++;
        }
        return count;
    }

    private static boolean everyCloudInShape(ServerLevel level, BlockPos ice) {
        for (BlockPos pos : BlockPos.betweenClosed(ice.offset(-12, -6, -12), ice.offset(12, 6, 12))) {
            if (level.getBlockState(pos).is(ModBlocks.DENSE_CLOUD.get())
                    && !DenseCloudBudding.inCloudShape(pos.getX() - ice.getX(), pos.getY() - ice.getY(), pos.getZ() - ice.getZ())) {
                return false;
            }
        }
        return true;
    }
}
