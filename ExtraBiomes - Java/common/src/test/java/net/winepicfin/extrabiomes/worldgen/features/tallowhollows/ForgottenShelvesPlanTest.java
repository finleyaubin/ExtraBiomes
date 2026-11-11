package net.winepicfin.extrabiomes.worldgen.features.tallowhollows;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForgottenShelvesPlanTest {
    private static final int SEEDS = 500;

    @Test
    void clusterHasTwoToSixColumns() {
        for (int seed = 0; seed < SEEDS; seed++) {
            int count = ForgottenShelvesPlan.plan(seed).columns().size();
            assertTrue(count >= 2 && count <= 6, "seed " + seed + " has " + count + " columns");
        }
    }

    @Test
    void columnsAreOneToThreeShelvesHigh() {
        for (int seed = 0; seed < SEEDS; seed++) {
            for (ForgottenShelvesPlan.Column column : ForgottenShelvesPlan.plan(seed).columns()) {
                assertTrue(column.height() >= 1 && column.height() <= 3, "seed " + seed + " column height " + column.height());
                assertTrue(column.bottomY() == 0 || column.bottomY() == -1, "seed " + seed + " column sunk " + column.bottomY() + " deep");
            }
        }
    }

    @Test
    void everyColumnSharesASideWithAnother() {
        for (int seed = 0; seed < SEEDS; seed++) {
            List<ForgottenShelvesPlan.Column> columns = ForgottenShelvesPlan.plan(seed).columns();
            Set<Long> footprint = new HashSet<>();
            for (ForgottenShelvesPlan.Column column : columns) {
                assertTrue(footprint.add(key(column.x(), column.z())), "seed " + seed + " stacks two columns at " + column.x() + "," + column.z());
            }
            for (ForgottenShelvesPlan.Column column : columns) {
                boolean touches = footprint.contains(key(column.x() + 1, column.z())) || footprint.contains(key(column.x() - 1, column.z()))
                        || footprint.contains(key(column.x(), column.z() + 1)) || footprint.contains(key(column.x(), column.z() - 1));
                assertTrue(touches, "seed " + seed + " column at " + column.x() + "," + column.z() + " stands alone");
            }
        }
    }

    @Test
    void candlesOnlyEverSitDirectlyOnTopOfAColumn() {
        for (int seed = 0; seed < SEEDS; seed++) {
            for (ForgottenShelvesPlan.Column column : ForgottenShelvesPlan.plan(seed).columns()) {
                assertEquals(column.topY() + 1, column.candleY(), "seed " + seed + " candle is not on the top shelf");
                assertTrue(column.candles() == 0 || (column.candles() >= 1 && column.candles() <= 3), "seed " + seed + " has " + column.candles() + " candles");
                assertTrue(column.candleY() < ForgottenShelvesPlan.MIN_HEADROOM, "seed " + seed + " candle above the headroom the feature checks");
            }
        }
    }

    @Test
    void roughlyHalfTheColumnsCarryCandles() {
        int columns = 0;
        int lit = 0;
        for (int seed = 0; seed < SEEDS; seed++) {
            for (ForgottenShelvesPlan.Column column : ForgottenShelvesPlan.plan(seed).columns()) {
                columns++;
                lit += column.hasCandles() ? 1 : 0;
            }
        }
        double share = (double) lit / columns;
        assertTrue(share > 0.35 && share < 0.65, "candle share " + share);
    }

    @Test
    void sameSeedGivesSamePlan() {
        assertEquals(ForgottenShelvesPlan.plan(42), ForgottenShelvesPlan.plan(42));
    }

    private static long key(int x, int z) {
        return ((long) x << 32) ^ (z & 0xFFFFFFFFL);
    }
}
