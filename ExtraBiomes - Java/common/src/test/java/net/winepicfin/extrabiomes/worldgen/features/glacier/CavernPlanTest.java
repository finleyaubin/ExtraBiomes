package net.winepicfin.extrabiomes.worldgen.features.glacier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CavernPlanTest {
    private static final BlockPos START = new BlockPos(100, 80, -40);
    private static final int SEEDS = 300;
    private static final int SOFT = 1;
    private static final int STONE = CavernPlan.HARD;

    private static final ToIntFunction<BlockPos> ALL_SOFT = pos -> SOFT;
    private static final ToIntFunction<BlockPos> FLAT_SLAB = pos -> pos.getY() < 50 ? STONE : SOFT;
    // The rock surface drops one block for every block east of the start.
    private static final ToIntFunction<BlockPos> SLOPE = pos -> pos.getY() < 60 - (pos.getX() - START.getX()) ? STONE : SOFT;
    private static final ToIntFunction<BlockPos> ROCK_BELOW_START = pos -> pos.getY() < START.getY() ? STONE : SOFT;

    private static CavernPlan plan(int seed, ToIntFunction<BlockPos> terrain) {
        return CavernPlan.plan(RandomSource.create(seed), START, 1, 0, START, terrain);
    }

    @Test
    void neverStraysOutsideTheWriteWindow() {
        for (ToIntFunction<BlockPos> terrain : List.of(ALL_SOFT, FLAT_SLAB, SLOPE, ROCK_BELOW_START)) {
            for (int seed = 0; seed < SEEDS; seed++) {
                CavernPlan plan = plan(seed, terrain);
                for (BlockPos pos : java.util.stream.Stream.concat(plan.cells().stream(), plan.water().stream()).toList()) {
                    assertTrue(Math.abs(pos.getX() - START.getX()) <= CavernPlan.WINDOW_REACH, "seed " + seed + " x " + pos);
                    assertTrue(Math.abs(pos.getZ() - START.getZ()) <= CavernPlan.WINDOW_REACH, "seed " + seed + " z " + pos);
                }
            }
        }
    }

    @Test
    void neverErodesHardRock() {
        for (ToIntFunction<BlockPos> terrain : List.of(FLAT_SLAB, SLOPE, ROCK_BELOW_START)) {
            for (int seed = 0; seed < SEEDS; seed++) {
                for (BlockPos cell : plan(seed, terrain).cells()) {
                    assertTrue(terrain.applyAsInt(cell) < CavernPlan.HARD, "seed " + seed + " carved hard rock at " + cell);
                }
            }
        }
    }

    @Test
    void boresAnOpenShaftDownThroughSoftMaterial() {
        for (int seed = 0; seed < SEEDS; seed++) {
            CavernPlan plan = plan(seed, ALL_SOFT);
            int[] extent = layerExtent(plan.cells(), START.getY() - 10);
            assertTrue(extent[0] >= 5 && extent[0] <= 9, "seed " + seed + " shaft x width " + extent[0]);
            assertTrue(extent[1] >= 5 && extent[1] <= 9, "seed " + seed + " shaft z width " + extent[1]);
            assertTrue(START.getY() - plan.cells().stream().mapToInt(BlockPos::getY).min().orElseThrow() >= CavernPlan.MIN_DEPTH, "seed " + seed + " too shallow");
        }
    }

    @Test
    void opensAChamberBetweenTenAndTwentyBlocksWide() {
        for (int seed = 0; seed < SEEDS; seed++) {
            CavernPlan plan = plan(seed, ALL_SOFT);
            int[] widest = new int[]{0, 0};
            for (int y = plan.cells().stream().mapToInt(BlockPos::getY).min().orElseThrow(); y <= START.getY(); y++) {
                int[] extent = layerExtent(plan.cells(), y);
                if (extent[0] + extent[1] > widest[0] + widest[1]) {
                    widest = extent;
                }
            }
            assertTrue(widest[0] >= 9 && widest[0] <= 20, "seed " + seed + " chamber x width " + widest[0]);
            assertTrue(widest[1] >= 9 && widest[1] <= 20, "seed " + seed + " chamber z width " + widest[1]);
        }
    }

    @Test
    void keepsToASingleStreamOfWater() {
        for (ToIntFunction<BlockPos> terrain : List.of(ALL_SOFT, FLAT_SLAB, SLOPE, ROCK_BELOW_START)) {
            for (int seed = 0; seed < SEEDS; seed++) {
                CavernPlan plan = plan(seed, terrain);
                assertTrue(plan.water().contains(plan.landing()), "seed " + seed + " landing is not water");
                assertTrue(plan.water().size() < 100, "seed " + seed + " has " + plan.water().size() + " water blocks");
            }
        }
    }

    @Test
    void theStreamLandsAgainstAWallNotInOpenSpace() {
        for (int seed = 0; seed < SEEDS; seed++) {
            CavernPlan plan = plan(seed, ALL_SOFT);
            BlockPos above = plan.landing().above();
            assertTrue(plan.cells().contains(above), "seed " + seed + " landing is not under open air");
            boolean touchesWall = false;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                touchesWall |= !plan.cells().contains(above.relative(direction));
            }
            assertTrue(touchesWall, "seed " + seed + " stream lands in the middle of open space");
        }
    }

    @Test
    void runsSidewaysAlongRockItCannotBoreThrough() {
        for (int seed = 0; seed < SEEDS; seed++) {
            CavernPlan plan = plan(seed, FLAT_SLAB);
            int reach = plan.cells().stream().mapToInt(cell -> Math.max(Math.abs(cell.getX() - START.getX()), Math.abs(cell.getZ() - START.getZ()))).max().orElseThrow();
            assertTrue(reach >= 9, "seed " + seed + " only reached " + reach);
            assertTrue(plan.cells().stream().mapToInt(BlockPos::getY).min().orElseThrow() >= 50, "seed " + seed + " went below the slab");
            assertTrue(plan.water().stream().anyMatch(cell -> cell.getY() == 49), "seed " + seed + " no stream channel on the contact");
        }
    }

    @Test
    void dropsAgainDownASlopingContact() {
        for (int seed = 0; seed < SEEDS; seed++) {
            CavernPlan plan = plan(seed, SLOPE);
            assertTrue(plan.cells().stream().mapToInt(BlockPos::getY).min().orElseThrow() <= 56, "seed " + seed + " never followed the slope down");
            assertTrue(plan.cells().stream().mapToInt(cell -> cell.getX() - START.getX()).max().orElseThrow() >= 6, "seed " + seed + " did not travel downslope");
        }
    }

    @Test
    void sameSeedGivesSamePlan() {
        assertEquals(plan(7, SLOPE), plan(7, SLOPE));
    }

    private static int[] layerExtent(Set<BlockPos> cells, int y) {
        Set<BlockPos> layer = cells.stream().filter(cell -> cell.getY() == y).collect(Collectors.toSet());
        int minX = layer.stream().mapToInt(BlockPos::getX).min().orElseThrow();
        int maxX = layer.stream().mapToInt(BlockPos::getX).max().orElseThrow();
        int minZ = layer.stream().mapToInt(BlockPos::getZ).min().orElseThrow();
        int maxZ = layer.stream().mapToInt(BlockPos::getZ).max().orElseThrow();
        return new int[]{maxX - minX + 1, maxZ - minZ + 1};
    }
}
