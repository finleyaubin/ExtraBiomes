package net.winepicfin.extrabiomes.worldgen.features.tallowhollows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

// Plans a tapered wax column as offsets from the first air block above the floor (y = 0). No Minecraft imports, so TallowPillarPlanTest runs without a bootstrapped registry.
public record TallowPillarPlan(int height, List<Integer> layerRadii, Set<Cell> body, Cell wick, Set<Cell> drips) {
    public static final int MIN_HEIGHT = 4;
    public static final int MAX_HEIGHT = 14;
    public static final int MAX_BASE_RADIUS = 3;
    public static final int MAX_TOP_RADIUS = 1;
    public static final int MAX_DRIPS = 4;
    // The column plus the wick on top of it must fit under the ceiling.
    public static final int MIN_HEADROOM = 6;

    private static final int MIN_BASE_RADIUS = 1;
    private static final float RIM_GAP_CHANCE = 0.3F;

    public record Cell(int x, int y, int z) {
        public Cell above() {
            return new Cell(x, y + 1, z);
        }

        public Cell below() {
            return new Cell(x, y - 1, z);
        }
    }

    // headroom is the number of air blocks above the floor; the wick takes the last one.
    public static TallowPillarPlan plan(long seed, int headroom) {
        if (headroom < MIN_HEADROOM) {
            throw new IllegalArgumentException("A tallow pillar needs " + MIN_HEADROOM + " blocks of headroom, got " + headroom);
        }
        Random random = new Random(seed);
        int tallestFit = Math.min(MAX_HEIGHT, headroom - 1);
        int height = MIN_HEIGHT + random.nextInt(tallestFit - MIN_HEIGHT + 1);
        int baseRadius = MIN_BASE_RADIUS + random.nextInt(MAX_BASE_RADIUS - MIN_BASE_RADIUS + 1);
        int topRadius = Math.min(baseRadius, random.nextInt(MAX_TOP_RADIUS + 1));

        List<Integer> layerRadii = new ArrayList<>();
        Set<Cell> body = new HashSet<>();
        for (int layer = 0; layer < height; layer++) {
            int radius = taperedRadius(baseRadius, topRadius, layer, height);
            layerRadii.add(radius);
            addLayer(body, random, layer, radius);
        }
        Cell wick = new Cell(0, height, 0);
        return new TallowPillarPlan(height, List.copyOf(layerRadii), Set.copyOf(body), wick, Set.copyOf(pickDrips(body, random)));
    }

    private static int taperedRadius(int baseRadius, int topRadius, int layer, int height) {
        double progress = (double) layer / (height - 1);
        return (int) Math.round(baseRadius + (topRadius - baseRadius) * progress);
    }

    // Rim cells go missing at random, which leaves the ragged, melted outline and the overhangs drips hang from.
    private static void addLayer(Set<Cell> body, Random random, int y, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int distanceSquared = dx * dx + dz * dz;
                boolean onRim = distanceSquared > radius * radius;
                if (distanceSquared > radius * radius + radius || (onRim && random.nextFloat() < RIM_GAP_CHANCE)) {
                    continue;
                }
                body.add(new Cell(dx, y, dz));
            }
        }
    }

    // A drip hangs under an overhanging body cell and needs a further air block below it to hang into.
    private static List<Cell> pickDrips(Set<Cell> body, Random random) {
        List<Cell> candidates = body.stream()
                .filter(cell -> cell.y() >= 2)
                .map(Cell::below)
                .filter(drip -> !body.contains(drip) && !body.contains(drip.below()))
                .sorted(Comparator.comparingInt(Cell::y).thenComparingInt(Cell::x).thenComparingInt(Cell::z))
                .toList();
        List<Cell> shuffled = new ArrayList<>(candidates);
        Collections.shuffle(shuffled, random);
        return shuffled.subList(0, Math.min(random.nextInt(MAX_DRIPS + 1), shuffled.size()));
    }
}
