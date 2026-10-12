package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.custom.JellyfishEntity;
import org.slf4j.Logger;

public class JellyfishBreathingGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int PLANT_Y = 220;
    private static final int AREA_OFFSET_X = 400;
    private static final int BOX_SIZE = 5;
    private static final int TICKS_PAST_DROWNING = 600;

    public static void jellyfishDoesNotDrownInWaterOrOnLand(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        BlockPos pool = new BlockPos(origin.getX() + AREA_OFFSET_X, PLANT_Y, origin.getZ());
        BlockPos shore = pool.east(BOX_SIZE * 2);
        buildBox(level, pool, Blocks.WATER);
        buildBox(level, shore, Blocks.AIR);

        JellyfishEntity swimming = spawnJellyfish(level, pool.offset(2, 2, 2));
        JellyfishEntity washedUp = spawnJellyfish(level, shore.offset(2, 1, 2));
        for (int tick = 0; tick < TICKS_PAST_DROWNING; tick++) {
            swimming.baseTick();
            washedUp.baseTick();
        }

        helper.assertTrue(swimming.getHealth() == swimming.getMaxHealth(), Component.literal("Jellyfish drowned while swimming, health " + swimming.getHealth()));
        helper.assertTrue(washedUp.getHealth() == washedUp.getMaxHealth(), Component.literal("Jellyfish drowned on land, health " + washedUp.getHealth()));
        LOGGER.info("[JellyfishBreathingGameTests] jellyfishDoesNotDrownInWaterOrOnLand: passed");
        helper.succeed();
    }

    private static void buildBox(ServerLevel level, BlockPos corner, Block interior) {
        for (BlockPos pos : BlockPos.betweenClosed(corner, corner.offset(BOX_SIZE - 1, BOX_SIZE - 1, BOX_SIZE - 1))) {
            boolean shell = pos.getX() == corner.getX() || pos.getY() == corner.getY() || pos.getZ() == corner.getZ()
                    || pos.getX() == corner.getX() + BOX_SIZE - 1 || pos.getY() == corner.getY() + BOX_SIZE - 1 || pos.getZ() == corner.getZ() + BOX_SIZE - 1;
            level.setBlockAndUpdate(pos, (shell ? Blocks.STONE : interior).defaultBlockState());
        }
    }

    private static JellyfishEntity spawnJellyfish(ServerLevel level, BlockPos pos) {
        JellyfishEntity jellyfish = ModEntities.JELLYFISH.get().create(level, EntitySpawnReason.COMMAND);
        jellyfish.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        jellyfish.setNoAi(true);
        level.addFreshEntity(jellyfish);
        return jellyfish;
    }
}
