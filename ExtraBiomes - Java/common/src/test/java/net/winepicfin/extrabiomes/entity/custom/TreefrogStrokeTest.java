package net.winepicfin.extrabiomes.entity.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreefrogStrokeTest {
    private static final float STEP = 0.001f;
    private static final float LARGEST_SMOOTH_STEP = 0.03f;

    @Test
    void legsNeverSnapBetweenFramesIncludingAcrossTheCycleWrap() {
        for (float phase = 0f; phase < 1f; phase += STEP) {
            float next = TreefrogStroke.phaseAt((phase + STEP) * TreefrogStroke.CYCLE_TICKS);
            assertTrue(Math.abs(TreefrogStroke.kickAt(next) - TreefrogStroke.kickAt(phase)) < LARGEST_SMOOTH_STEP, "kick snaps at " + phase);
            assertTrue(Math.abs(TreefrogStroke.reachAt(next) - TreefrogStroke.reachAt(phase)) < LARGEST_SMOOTH_STEP, "reach snaps at " + phase);
            assertTrue(Math.abs(TreefrogStroke.sweepAt(next) - TreefrogStroke.sweepAt(phase)) < LARGEST_SMOOTH_STEP, "sweep snaps at " + phase);
        }
    }

    @Test
    void hindLegsStayStretchedUntilTheFrontLegsHaveFinishedTheirPull() {
        for (float phase = 0f; phase < 1f; phase += STEP) {
            boolean frontLegsBusy = TreefrogStroke.reachAt(phase) < 1f || TreefrogStroke.sweepAt(phase) > 0f;
            if (frontLegsBusy) {
                assertEquals(1f, TreefrogStroke.kickAt(phase), "hind legs moved during the pull at " + phase);
            }
        }
    }

    @Test
    void glideHoldsEveryLegStretchedAndThrustPeaksOnTheKick() {
        float midGlide = 0.8f;
        float midKick = 0.5f;
        assertEquals(1f, TreefrogStroke.kickAt(midGlide));
        assertEquals(1f, TreefrogStroke.reachAt(midGlide));
        for (float phase = 0f; phase < 1f; phase += STEP) {
            assertTrue(TreefrogStroke.thrustAt(phase) <= TreefrogStroke.thrustAt(midKick));
        }
    }
}
