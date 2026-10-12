package net.winepicfin.extrabiomes.gametest.shared;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.winepicfin.extrabiomes.block.ModBlocks;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class TreeGrowthScenarios {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int PLANT_Y = 220;
    private static final int FIRST_TREE_OFFSET_X = 200;
    private static final int TREE_SPACING = 40;
    private static final int LANE_SPACING = 100;
    private static final int TREE_RADIUS = 16;
    private static final int TREE_HEIGHT = 30;
    private static final int MAX_BONE_MEAL_USES = 300;
    private static final int MAX_DECAY_PASSES = 60;

    private record Tree(String name, Block sapling, Block ground, Block log, Block leaves) {
    }

    private TreeGrowthScenarios() {
    }

    private static List<Tree> everyTree() {
        return List.of(
                new Tree("mystic", ModBlocks.MYSTIC_SAPLING.get(), Blocks.DIRT, ModBlocks.MYSTIC_LOG.get(), ModBlocks.MYSTIC_LEAVES.get()),
                new Tree("sky", ModBlocks.SKY_SAPLING.get(), Blocks.DIRT, ModBlocks.SKY_LOG.get(), ModBlocks.SKY_LEAVES.get()),
                new Tree("palm", ModBlocks.PALM_SAPLING.get(), Blocks.SAND, ModBlocks.PALM_LOG.get(), ModBlocks.PALM_LEAVES.get()));
    }

    public static void assertEveryTreeGrowsWithBoneMeal(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);

        List<Tree> trees = everyTree();
        for (int i = 0; i < trees.size(); i++) {
            Tree tree = trees.get(i);
            BlockPos sapling = plantSapling(level, origin, 0, i, tree);
            int uses = 0;
            while (level.getBlockState(sapling).is(tree.sapling()) && uses < MAX_BONE_MEAL_USES) {
                BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), level, sapling);
                uses++;
            }
            assertGrewIntoTree(helper, level, tree, sapling);
            LOGGER.info("[TreeGrowthGameTests] {} sapling grew after {} bone meal uses", tree.name(), uses);
        }
    }

    /** Plants and force-loads one sapling per tree; the returned check random-ticks them and unforces the chunks once every tree has grown. */
    public static Runnable plantForNaturalGrowth(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        List<Tree> trees = everyTree();
        List<BlockPos> saplings = new ArrayList<>();
        for (int i = 0; i < trees.size(); i++) {
            BlockPos sapling = plantSapling(level, origin, 1, i, trees.get(i));
            level.setChunkForced(sapling.getX() >> 4, sapling.getZ() >> 4, true);
            saplings.add(sapling);
        }

        return () -> {
            for (int i = 0; i < trees.size(); i++) {
                BlockState state = level.getBlockState(saplings.get(i));
                if (state.is(trees.get(i).sapling())) {
                    state.randomTick(level, saplings.get(i), level.getRandom());
                }
            }
            for (int i = 0; i < trees.size(); i++) {
                assertGrewIntoTree(helper, level, trees.get(i), saplings.get(i));
            }
            saplings.forEach(sapling -> level.setChunkForced(sapling.getX() >> 4, sapling.getZ() >> 4, false));
        };
    }

    public static void assertEveryTreeLeafDecaysOnceItsTrunkIsBroken(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);

        List<Tree> trees = everyTree();
        for (int i = 0; i < trees.size(); i++) {
            Tree tree = trees.get(i);
            BlockPos sapling = plantSapling(level, origin, 2, i, tree);
            growImmediately(level, tree, sapling);
            assertGrewIntoTree(helper, level, tree, sapling);

            findBlocks(level, sapling, state -> state.is(tree.log())).forEach(log -> level.destroyBlock(log, false));
            decayLeaves(level, tree, sapling);

            int leavesLeft = findBlocks(level, sapling, state -> state.is(tree.leaves())).size();
            helper.assertTrue(leavesLeft == 0, leavesLeft + " " + tree.name() + " leaves did not decay after the trunk was broken");
            LOGGER.info("[TreeGrowthGameTests] {} leaves all decayed", tree.name());
        }
    }

    private static BlockPos plantSapling(ServerLevel level, BlockPos origin, int lane, int treeIndex, Tree tree) {
        BlockPos ground = new BlockPos(origin.getX() + FIRST_TREE_OFFSET_X + treeIndex * TREE_SPACING, PLANT_Y, origin.getZ() + lane * LANE_SPACING);
        clearAirAround(level, ground);
        level.setBlockAndUpdate(ground.below(), Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(ground, tree.ground().defaultBlockState());
        level.setBlockAndUpdate(ground.east(), Blocks.LIGHT.defaultBlockState());
        BlockPos sapling = ground.above();
        level.setBlockAndUpdate(sapling, tree.sapling().defaultBlockState());
        return sapling;
    }

    private static void clearAirAround(ServerLevel level, BlockPos ground) {
        BlockPos from = ground.offset(-TREE_RADIUS, -1, -TREE_RADIUS);
        BlockPos to = ground.offset(TREE_RADIUS, TREE_HEIGHT, TREE_RADIUS);
        for (BlockPos pos : BlockPos.betweenClosed(from, to)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private static void growImmediately(ServerLevel level, Tree tree, BlockPos sapling) {
        SaplingBlock block = (SaplingBlock) tree.sapling();
        for (int stage = 0; stage < 2; stage++) {
            BlockState state = level.getBlockState(sapling);
            if (state.is(block)) {
                block.advanceTree(level, sapling, state, level.getRandom());
            }
        }
    }

    private static void assertGrewIntoTree(GameTestHelper helper, ServerLevel level, Tree tree, BlockPos sapling) {
        helper.assertTrue(!level.getBlockState(sapling).is(tree.sapling()), tree.name() + " sapling never grew");
        helper.assertTrue(!findBlocks(level, sapling, state -> state.is(tree.log())).isEmpty(), tree.name() + " tree grew no logs");
        helper.assertTrue(!findBlocks(level, sapling, state -> state.is(tree.leaves())).isEmpty(), tree.name() + " tree grew no leaves");
    }

    private static void decayLeaves(ServerLevel level, Tree tree, BlockPos sapling) {
        for (int pass = 0; pass < MAX_DECAY_PASSES; pass++) {
            List<BlockPos> leaves = findBlocks(level, sapling, state -> state.is(tree.leaves()));
            if (leaves.isEmpty()) {
                return;
            }
            for (BlockPos pos : leaves) {
                tickLeaf(level, pos, tree.leaves());
            }
        }
    }

    private static void tickLeaf(ServerLevel level, BlockPos pos, Block leaves) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(leaves)) {
            return;
        }
        state.tick(level, pos, level.getRandom());
        state = level.getBlockState(pos);
        if (state.is(leaves)) {
            state.randomTick(level, pos, level.getRandom());
        }
    }

    private static List<BlockPos> findBlocks(ServerLevel level, BlockPos sapling, Predicate<BlockState> match) {
        List<BlockPos> found = new ArrayList<>();
        BlockPos from = sapling.offset(-TREE_RADIUS, 0, -TREE_RADIUS);
        BlockPos to = sapling.offset(TREE_RADIUS, TREE_HEIGHT, TREE_RADIUS);
        for (BlockPos pos : BlockPos.betweenClosed(from, to)) {
            if (match.test(level.getBlockState(pos))) {
                found.add(pos.immutable());
            }
        }
        return found;
    }
}
