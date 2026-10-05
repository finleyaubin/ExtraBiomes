package net.winepicfin.extrabiomes.block.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DenseCloudBuddingTest {

    @Test
    void cloudReachesTenBlocksSidewaysAndFiveVertically() {
        assertTrue(DenseCloudBudding.inCloudShape(10, 0, 0));
        assertTrue(DenseCloudBudding.inCloudShape(0, 0, -10));
        assertTrue(DenseCloudBudding.inCloudShape(0, 5, 0));
        assertTrue(DenseCloudBudding.inCloudShape(0, -5, 0));
    }

    @Test
    void cloudStopsBeyondTheRadii() {
        assertFalse(DenseCloudBudding.inCloudShape(11, 0, 0));
        assertFalse(DenseCloudBudding.inCloudShape(0, 6, 0));
        assertFalse(DenseCloudBudding.inCloudShape(8, 4, 0));
    }
}
