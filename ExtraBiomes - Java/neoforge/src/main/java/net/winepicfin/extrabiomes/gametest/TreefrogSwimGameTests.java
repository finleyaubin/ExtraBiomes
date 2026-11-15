package net.winepicfin.extrabiomes.gametest;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Blocks;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.custom.TreefrogEntity;
import org.slf4j.Logger;

@GameTestHolder(ExtraBiomes.MOD_ID)
@PrefixGameTestTemplate(false)
public class TreefrogSwimGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    // Raised clear of the test structure, which sits in the chunks the framework keeps ticking.
    private static final BlockPos POOL_CENTRE = new BlockPos(2, 12, 2);
    private static final int SHORE_WIDTH = 2;
    private static final int POOL_RADIUS = 1;
    private static final int POOL_DEPTH = 2;
    // Vanilla's bank climb lifts a swimmer at 0.3 blocks a tick; a treefrog's leap is far faster.
    private static final double LEAP_SPEED = 0.5;

    @GameTest(template = "empty", batch = "extrabiomes")
    public static void treefrogSwimsOutOfWaterWithoutLeapingOffIt(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos poolCentre = helper.absolutePos(POOL_CENTRE);
        buildPool(level, poolCentre);

        TreefrogEntity treefrog = ModEntities.TREEFROG.get().create(level, EntitySpawnReason.COMMAND);
        treefrog.setPos(poolCentre.getX() + 0.5, poolCentre.getY(), poolCentre.getZ() + 0.5);
        level.addFreshEntity(treefrog);

        boolean[] leaptOffWater = {false};
        helper.succeedWhen(() -> {
            double strayedX = Math.abs(treefrog.getX() - (poolCentre.getX() + 0.5));
            double strayedZ = Math.abs(treefrog.getZ() - (poolCentre.getZ() + 0.5));
            boolean leftPool = Math.max(strayedX, strayedZ) > POOL_RADIUS + 0.5;
            leaptOffWater[0] |= treefrog.isInWater() && !treefrog.onGround() && treefrog.getDeltaMovement().y > LEAP_SPEED;
            helper.assertTrue(!leaptOffWater[0], "Treefrog leapt off the water's surface");
            helper.assertTrue(leftPool, "Treefrog is still in the pool at " + treefrog.position());
            LOGGER.info("[TreefrogSwimGameTests] treefrogSwimsOutOfWaterWithoutLeapingOffIt: passed after {} ticks", helper.getTick());
        });
    }

    private static void buildPool(ServerLevel level, BlockPos centre) {
        int reach = POOL_RADIUS + SHORE_WIDTH;
        for (BlockPos pos : BlockPos.betweenClosed(centre.offset(-reach, -POOL_DEPTH, -reach), centre.offset(reach, 0, reach))) {
            boolean water = pos.getY() > centre.getY() - POOL_DEPTH
                    && Math.abs(pos.getX() - centre.getX()) <= POOL_RADIUS
                    && Math.abs(pos.getZ() - centre.getZ()) <= POOL_RADIUS;
            level.setBlockAndUpdate(pos, (water ? Blocks.WATER : Blocks.STONE).defaultBlockState());
        }
    }
}
