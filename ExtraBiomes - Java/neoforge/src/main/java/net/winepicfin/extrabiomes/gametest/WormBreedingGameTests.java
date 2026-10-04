package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.custom.WormEntity;
import org.slf4j.Logger;

public class WormBreedingGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void wormsBreedInsideCompostingComposter(GameTestHelper helper) {
        LOGGER.info("[WormBreedingGameTests] wormsBreedInsideCompostingComposter: starting");
        BlockPos composter = new BlockPos(2, 2, 2);
        helper.setBlock(composter.below(), Blocks.STONE);
        for (BlockPos side : new BlockPos[]{composter.north(), composter.east(), composter.south(), composter.west()}) {
            helper.setBlock(side, Blocks.AIR);
        }
        helper.setBlock(composter, Blocks.COMPOSTER.defaultBlockState().setValue(ComposterBlock.LEVEL, 3));
        Vec3 inside = new Vec3(2.5, 2.125, 2.5);
        helper.spawnWithNoFreeWill(ModEntities.WORM.get(), inside);
        helper.spawnWithNoFreeWill(ModEntities.WORM.get(), inside);

        helper.succeedWhen(() -> {
            helper.assertBlockProperty(composter, ComposterBlock.LEVEL, 2);
            int worms = helper.getLevel().getEntitiesOfClass(WormEntity.class, helper.getBounds().inflate(8.0)).size();
            helper.assertTrue(worms == 3, Component.literal("Expected 3 worms after breeding but found " + worms));
            int northWorms = helper.getLevel().getEntitiesOfClass(WormEntity.class, new AABB(helper.absolutePos(composter.north()))).size();
            helper.assertTrue(northWorms == 1, Component.literal("Expected the baby on the north side (bottom is covered) but found " + northWorms));
            LOGGER.info("[WormBreedingGameTests] wormsBreedInsideCompostingComposter: passed");
        });
    }
}
