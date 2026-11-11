package net.winepicfin.extrabiomes.worldgen.features.tallowhollows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Plans a buried library's last bookshelves as columns offset from the first air block above the floor (y = 0). No Minecraft imports, so ForgottenShelvesPlanTest runs without a bootstrapped registry.
public record ForgottenShelvesPlan(List<Column> columns) {
    public static final int MIN_COLUMNS = 2;
    public static final int MAX_COLUMNS = 6;
    public static final int MIN_COLUMN_HEIGHT = 1;
    public static final int MAX_COLUMN_HEIGHT = 3;
    public static final int MIN_CANDLES = 1;
    public static final int MAX_CANDLES = 3;
    // The tallest unsunk column plus its candles.
    public static final int MIN_HEADROOM = 4;

    private static final float CANDLE_CHANCE = 0.5F;
    private static final float SUNK_CHANCE = 0.4F;
    private static final float L_SHAPE_CHANCE = 0.5F;
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    // sunk lowers the bottom shelf one block into the floor; candles is 0 for a bare column.
    public record Column(int x, int z, int height, boolean sunk, int candles) {
        public int bottomY() {
            return sunk ? -1 : 0;
        }

        public int topY() {
            return bottomY() + height - 1;
        }

        public int candleY() {
            return topY() + 1;
        }

        public boolean hasCandles() {
            return candles > 0;
        }
    }

    public static ForgottenShelvesPlan plan(long seed) {
        Random random = new Random(seed);
        int count = MIN_COLUMNS + random.nextInt(MAX_COLUMNS - MIN_COLUMNS + 1);
        int[] direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        // An L turns a quarter-turn after at least two columns, and keeps at least one column on the new leg.
        int cornerAfter = count >= 3 && random.nextFloat() < L_SHAPE_CHANCE ? 2 + random.nextInt(count - 2) : count;
        int[] turned = random.nextBoolean() ? new int[]{-direction[1], direction[0]} : new int[]{direction[1], -direction[0]};

        List<Column> columns = new ArrayList<>();
        int x = 0;
        int z = 0;
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                int[] step = i < cornerAfter ? direction : turned;
                x += step[0];
                z += step[1];
            }
            columns.add(column(random, x, z));
        }
        return new ForgottenShelvesPlan(List.copyOf(columns));
    }

    private static Column column(Random random, int x, int z) {
        int height = MIN_COLUMN_HEIGHT + random.nextInt(MAX_COLUMN_HEIGHT - MIN_COLUMN_HEIGHT + 1);
        boolean sunk = random.nextFloat() < SUNK_CHANCE;
        int candles = random.nextFloat() < CANDLE_CHANCE ? MIN_CANDLES + random.nextInt(MAX_CANDLES - MIN_CANDLES + 1) : 0;
        return new Column(x, z, height, sunk, candles);
    }
}
