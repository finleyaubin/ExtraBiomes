package net.winepicfin.extrabiomes.worldgen.features.tallowhollows;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TallowPillarPlanTest {
    private static final int SEEDS = 500;
    private static final List<Integer> HEADROOMS = List.of(TallowPillarPlan.MIN_HEADROOM, 9, 15, 40);

    @Test
    void radiusNeverWidensGoingUp() {
        forEachPlan((seed, plan) -> {
            List<Integer> radii = plan.layerRadii();
            for (int layer = 1; layer < radii.size(); layer++) {
                assertTrue(radii.get(layer) <= radii.get(layer - 1), "seed " + seed + " widens at layer " + layer + ": " + radii);
            }
        });
    }

    @Test
    void topLayerIsAtMostOneBlockInRadius() {
        forEachPlan((seed, plan) -> assertTrue(plan.layerRadii().getLast() <= TallowPillarPlan.MAX_TOP_RADIUS, "seed " + seed + " top radius " + plan.layerRadii().getLast()));
    }

    @Test
    void heightStaysWithinFourToFourteen() {
        forEachPlan((seed, plan) -> {
            assertTrue(plan.height() >= 4 && plan.height() <= 14, "seed " + seed + " height " + plan.height());
            assertEquals(plan.height(), plan.layerRadii().size(), "seed " + seed + " has a radius per layer");
            assertEquals(plan.height() - 1, plan.body().stream().mapToInt(TallowPillarPlan.Cell::y).max().orElseThrow(), "seed " + seed + " body height");
        });
    }

    @Test
    void neverOutgrowsTheHeadroomItWasGiven() {
        for (int headroom : HEADROOMS) {
            for (int seed = 0; seed < SEEDS; seed++) {
                TallowPillarPlan plan = TallowPillarPlan.plan(seed, headroom);
                assertTrue(plan.wick().y() < headroom, "seed " + seed + " wick at " + plan.wick().y() + " under headroom " + headroom);
            }
        }
    }

    @Test
    void refusesACeilingTooLowForAPillar() {
        assertThrows(IllegalArgumentException.class, () -> TallowPillarPlan.plan(0, TallowPillarPlan.MIN_HEADROOM - 1));
    }

    @Test
    void wickSitsDirectlyOnTheTopBlock() {
        forEachPlan((seed, plan) -> {
            TallowPillarPlan.Cell top = new TallowPillarPlan.Cell(0, plan.height() - 1, 0);
            assertTrue(plan.body().contains(top), "seed " + seed + " has no top block under the wick");
            assertEquals(top.above(), plan.wick(), "seed " + seed + " wick is not directly above the top block");
            assertFalse(plan.body().contains(plan.wick()), "seed " + seed + " wick overlaps the body");
        });
    }

    @Test
    void everyDripHangsFromThePillarIntoOpenAir() {
        forEachPlan((seed, plan) -> {
            assertTrue(plan.drips().size() <= TallowPillarPlan.MAX_DRIPS, "seed " + seed + " has " + plan.drips().size() + " drips");
            for (TallowPillarPlan.Cell drip : plan.drips()) {
                assertTrue(plan.body().contains(drip.above()), "seed " + seed + " drip " + drip + " has no pillar block above it");
                assertFalse(plan.body().contains(drip), "seed " + seed + " drip " + drip + " is inside the pillar");
                assertFalse(plan.body().contains(drip.below()), "seed " + seed + " drip " + drip + " has pillar below it");
                assertTrue(drip.below().y() >= 0, "seed " + seed + " drip " + drip + " hangs into the floor");
            }
        });
    }

    @Test
    void someSeedsGrowDrips() {
        int withDrips = 0;
        for (int seed = 0; seed < SEEDS; seed++) {
            withDrips += TallowPillarPlan.plan(seed, 15).drips().isEmpty() ? 0 : 1;
        }
        assertTrue(withDrips > 0, "no seed out of " + SEEDS + " produced a drip");
    }

    @Test
    void sameSeedGivesSamePlan() {
        assertEquals(TallowPillarPlan.plan(42, 15), TallowPillarPlan.plan(42, 15));
    }

    private static void forEachPlan(PlanCheck check) {
        for (int headroom : HEADROOMS) {
            for (int seed = 0; seed < SEEDS; seed++) {
                check.accept(seed, TallowPillarPlan.plan(seed, headroom));
            }
        }
    }

    @FunctionalInterface
    private interface PlanCheck {
        void accept(int seed, TallowPillarPlan plan);
    }
}
