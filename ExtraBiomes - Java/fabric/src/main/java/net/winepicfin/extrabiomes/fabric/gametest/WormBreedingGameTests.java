package net.winepicfin.extrabiomes.fabric.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.ChunkPos;
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

    @GameTest(template = ExtraBiomes.MOD_ID + ":empty", timeoutTicks = 600)
    public static void wormsBreedInsideCompostingComposter(GameTestHelper helper) {
        LOGGER.info("[WormBreedingGameTests] wormsBreedInsideCompostingComposter: starting");
        BlockPos composter = new BlockPos(2, 2, 2);
        Vec3 inside = new Vec3(2.5, 2.125, 2.5);

        ChunkPos chunk = new ChunkPos(helper.absolutePos(composter));
        // A worm only ticks once its chunk is entity-ticking, and the test area's own ticket can lapse on a lagging server.
        helper.getLevel().setChunkForced(chunk.x, chunk.z, true);
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(helper.getLevel().isPositionEntityTicking(helper.absolutePos(composter)),
                        "Test chunk is not entity-ticking yet"))
                .thenExecute(() -> {
                    helper.setBlock(composter.below(), Blocks.STONE);
                    helper.setBlock(composter, Blocks.COMPOSTER.defaultBlockState().setValue(ComposterBlock.LEVEL, 3));
                    helper.spawnWithNoFreeWill(ModEntities.WORM.get(), inside);
                    helper.spawnWithNoFreeWill(ModEntities.WORM.get(), inside);
                })
                .thenWaitUntil(() -> {
                    helper.assertBlockProperty(composter, ComposterBlock.LEVEL, 2);
                    int worms = helper.getLevel().getEntitiesOfClass(WormEntity.class, helper.getBounds().inflate(8.0)).size();
                    helper.assertTrue(worms == 3, "Expected 3 worms after breeding but found " + worms);
                    int northWorms = helper.getLevel().getEntitiesOfClass(WormEntity.class, new AABB(helper.absolutePos(composter.north()))).size();
                    helper.assertTrue(northWorms == 1, "Expected the baby on the north side (bottom is covered) but found " + northWorms);
                    helper.getLevel().setChunkForced(chunk.x, chunk.z, false);
                    LOGGER.info("[WormBreedingGameTests] wormsBreedInsideCompostingComposter: passed");
                })
                .thenSucceed();
    }
}
