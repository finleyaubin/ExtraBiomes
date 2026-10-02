package net.winepicfin.extrabiomes.worldgen.features.glacier;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

// Plans a meltwater conduit as erosion would carve it: it bores down through soft material, and where it meets hard rock it scours a basin and runs sideways along the contact until it can drop again. A single one-block stream hugs one wall the whole way.
public record CavernPlan(Set<BlockPos> cells, Set<BlockPos> water, BlockPos landing) {
    // Hardness levels a probe returns: 0 air or fluid, 1 erodible, HARD or more resists water.
    public static final int HARD = 2;
    // A feature may only write this far from its origin (the origin chunk plus one on every side).
    public static final int WINDOW_REACH = 15;
    public static final int MIN_DEPTH = 40;
    public static final int MAX_DEPTH = 70;
    public static final int FLOOR_LIMIT_Y = -50;

    // Half-widths: a round cross-section of radius r is 2r+1 blocks across, so shafts are 5-9 wide, tunnels 5-11 and chambers 11-19.
    private static final int MIN_SHAFT_RADIUS = 2;
    private static final int SHAFT_RADIUS_SPREAD = 3;
    private static final int MIN_TUNNEL_RADIUS = 2;
    private static final int TUNNEL_RADIUS_SPREAD = 4;
    private static final int MIN_TUNNEL_HEIGHT = 3;
    private static final int TUNNEL_HEIGHT_SPREAD = 4;
    private static final int MIN_CHAMBER_RADIUS = 5;
    private static final int CHAMBER_RADIUS_SPREAD = 5;
    private static final int MIN_CHAMBER_HEIGHT = 4;
    private static final int CHAMBER_HEIGHT_SPREAD = 5;
    private static final int MIN_CHAMBER_ROOM = 3;
    private static final int MAX_CROSS_SECTION_RADIUS = MIN_TUNNEL_RADIUS + TUNNEL_RADIUS_SPREAD - 1;

    private static final int MIN_WALL_STREAM = 4;
    private static final int WALL_STREAM_SPREAD = 5;
    private static final int LATERAL_MAX = 18;
    private static final int LOOKAHEAD = 12;
    private static final int DROP_BONUS = 1000;
    private static final int MAX_STEPS = 400;
    private static final double WALL_ROUGHNESS = 0.3;
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    // flowX/flowZ is the unit direction the surface stream was travelling when it reached start.
    public static CavernPlan plan(RandomSource random, BlockPos start, int flowX, int flowZ, BlockPos origin, ToIntFunction<BlockPos> hardness) {
        return new Planner(random, start, flowX, flowZ, origin, hardness).run();
    }

    private static List<int[]> disc(int radius) {
        List<int[]> offsets = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius + radius) {
                    offsets.add(new int[]{dx, dz});
                }
            }
        }
        return offsets;
    }

    private static final class Planner {
        private final RandomSource random;
        private final BlockPos start;
        private final int flowX;
        private final int flowZ;
        private final BlockPos origin;
        private final ToIntFunction<BlockPos> hardness;
        private final Set<BlockPos> cells = new HashSet<>();
        private final Set<BlockPos> water = new HashSet<>();
        private final int shaftRadius;
        private final int tunnelRadius;
        private final int tunnelHeight;
        private final List<int[]> shaftDisc;
        private final List<int[]> tunnelDisc;
        // The shaft centre, and the unit direction from it to the wall the stream clings to.
        private int centerX;
        private int centerZ;
        private int wallX;
        private int wallZ;
        private int y;
        private int lateralSteps;
        private int lastStreamX;
        private int lastStreamZ;

        Planner(RandomSource random, BlockPos start, int flowX, int flowZ, BlockPos origin, ToIntFunction<BlockPos> hardness) {
            this.random = random;
            this.start = start;
            this.flowX = flowX;
            this.flowZ = flowZ;
            this.origin = origin;
            this.hardness = hardness;
            this.shaftRadius = MIN_SHAFT_RADIUS + random.nextInt(SHAFT_RADIUS_SPREAD);
            this.tunnelRadius = MIN_TUNNEL_RADIUS + random.nextInt(TUNNEL_RADIUS_SPREAD);
            this.tunnelHeight = MIN_TUNNEL_HEIGHT + random.nextInt(TUNNEL_HEIGHT_SPREAD);
            this.shaftDisc = disc(shaftRadius);
            this.tunnelDisc = disc(tunnelRadius);
        }

        CavernPlan run() {
            int targetY = Math.max(start.getY() - MIN_DEPTH - random.nextInt(MAX_DEPTH - MIN_DEPTH + 1), FLOOR_LIMIT_Y);
            // The shaft lies downstream of the stream's last block, which sits on the shaft's upstream wall so the water falls down that wall.
            centerX = start.getX() + flowX * shaftRadius;
            centerZ = start.getZ() + flowZ * shaftRadius;
            wallX = -flowX;
            wallZ = -flowZ;
            y = start.getY();
            addCell(start);

            for (int step = 0; step < MAX_STEPS && y > targetY && lateralSteps < LATERAL_MAX; step++) {
                if (!blocked(centerX, y - 1, centerZ, shaftDisc)) {
                    y--;
                    carveShaftLayer(y);
                } else if (!deflect()) {
                    break;
                }
            }
            BlockPos landing = addChamber();
            return new CavernPlan(cells, water, landing);
        }

        private int streamColumnX() {
            return centerX + wallX * shaftRadius;
        }

        private int streamColumnZ() {
            return centerZ + wallZ * shaftRadius;
        }

        // Falling water lands on rock it cannot bore through: it scours a basin, then follows the contact toward wherever it can drop again.
        private boolean deflect() {
            lastStreamX = streamColumnX();
            lastStreamZ = streamColumnZ();
            addTrench(lastStreamX, lastStreamZ);
            scour();
            int[] direction = chooseDirection();
            return direction != null && runLateral(direction[0], direction[1]);
        }

        private void scour() {
            int radius = shaftRadius + 1;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (dx * dx + dy * dy + dz * dz <= radius * radius + 1) {
                            addCell(new BlockPos(centerX + dx, y + dy, centerZ + dz));
                        }
                    }
                }
            }
        }

        private int[] chooseDirection() {
            int bestScore = 0;
            List<int[]> best = new ArrayList<>();
            for (int[] direction : DIRECTIONS) {
                int score = scoreDirection(direction);
                if (score > bestScore) {
                    bestScore = score;
                    best.clear();
                    best.add(direction);
                } else if (score == bestScore && score > 0) {
                    best.add(direction);
                }
            }
            return best.isEmpty() ? null : best.get(random.nextInt(best.size()));
        }

        // A direction that reaches a drop soonest wins; otherwise the one with the longest passable run.
        private int scoreDirection(int[] direction) {
            int passable = 0;
            for (int step = 1; step <= LOOKAHEAD; step++) {
                int nextX = centerX + direction[0] * step;
                int nextZ = centerZ + direction[1] * step;
                if (!hasRoom(nextX, nextZ) || blocked(nextX, y, nextZ, tunnelDisc)) {
                    break;
                }
                passable = step;
                if (!blocked(nextX, y - 1, nextZ, shaftDisc)) {
                    return DROP_BONUS - step;
                }
            }
            return passable;
        }

        private boolean runLateral(int startDirX, int startDirZ) {
            boolean moved = false;
            int dirX = startDirX;
            int dirZ = startDirZ;
            int[] side = sideFor(dirX, dirZ, wallX, wallZ);
            while (lateralSteps < LATERAL_MAX) {
                int nextX = centerX + dirX;
                int nextZ = centerZ + dirZ;
                if (!hasRoom(nextX, nextZ) || blocked(nextX, y, nextZ, tunnelDisc)) {
                    int[] turn = pickTurn(dirX, dirZ);
                    if (turn == null) {
                        break;
                    }
                    dirX = turn[0];
                    dirZ = turn[1];
                    side = sideFor(dirX, dirZ, side[0], side[1]);
                    continue;
                }
                centerX = nextX;
                centerZ = nextZ;
                lateralSteps++;
                moved = true;
                carveTunnel();
                connectStream(centerX + side[0] * tunnelRadius, centerZ + side[1] * tunnelRadius);
                if (!blocked(centerX, y - 1, centerZ, shaftDisc)) {
                    break;
                }
            }
            if (moved) {
                // However the run ended, the stream now hugs the wall on its side: it falls down the next shaft's wall there, or keeps to it in the chamber.
                wallX = side[0];
                wallZ = side[1];
                connectStream(streamColumnX(), streamColumnZ());
            }
            return moved;
        }

        // Keeps the stream on the wall it was already hugging when that is still a side wall, otherwise picks one.
        private int[] sideFor(int dirX, int dirZ, int refX, int refZ) {
            if ((refX != 0 || refZ != 0) && refX * dirX + refZ * dirZ == 0) {
                return new int[]{refX, refZ};
            }
            return random.nextBoolean() ? new int[]{-dirZ, dirX} : new int[]{dirZ, -dirX};
        }

        private int[] pickTurn(int dirX, int dirZ) {
            int[][] turns = {{-dirZ, dirX}, {dirZ, -dirX}};
            int first = random.nextInt(2);
            for (int i = 0; i < 2; i++) {
                int[] turn = turns[(first + i) % 2];
                if (hasRoom(centerX + turn[0], centerZ + turn[1]) && !blocked(centerX + turn[0], y, centerZ + turn[1], tunnelDisc)) {
                    return turn;
                }
            }
            return null;
        }

        private void carveShaftLayer(int level) {
            for (int[] offset : shaftDisc) {
                addCell(new BlockPos(centerX + offset[0], level, centerZ + offset[1]));
            }
        }

        // An open passage; the stream is a one-block channel cut into the floor along one wall, not water across the whole width.
        private void carveTunnel() {
            for (int[] offset : tunnelDisc) {
                BlockPos floor = new BlockPos(centerX + offset[0], y, centerZ + offset[1]);
                for (int up = 0; up < tunnelHeight; up++) {
                    addCell(floor.above(up));
                }
            }
        }

        // The channel is one block below the floor, so sources in it cannot spread sideways onto the floor.
        private void addTrench(int trenchX, int trenchZ) {
            if (Math.abs(trenchX - origin.getX()) <= WINDOW_REACH && Math.abs(trenchZ - origin.getZ()) <= WINDOW_REACH) {
                water.add(new BlockPos(trenchX, y - 1, trenchZ));
            }
        }

        private void connectStream(int targetX, int targetZ) {
            int x = lastStreamX;
            int z = lastStreamZ;
            while (x != targetX) {
                x += Integer.signum(targetX - x);
                addTrench(x, z);
            }
            while (z != targetZ) {
                z += Integer.signum(targetZ - z);
                addTrench(x, z);
            }
            lastStreamX = targetX;
            lastStreamZ = targetZ;
        }

        // A flat-floored dome with the shaft set against its wall, and the stream running a short way along that wall.
        private BlockPos addChamber() {
            int landingX = streamColumnX();
            int landingZ = streamColumnZ();
            lastStreamX = landingX;
            lastStreamZ = landingZ;
            addTrench(landingX, landingZ);
            BlockPos landing = new BlockPos(landingX, y - 1, landingZ);

            int radiusX = MIN_CHAMBER_RADIUS + random.nextInt(CHAMBER_RADIUS_SPREAD);
            int radiusZ = MIN_CHAMBER_RADIUS + random.nextInt(CHAMBER_RADIUS_SPREAD);
            int height = MIN_CHAMBER_HEIGHT + random.nextInt(CHAMBER_HEIGHT_SPREAD);
            int chamberX = centerX;
            int chamberZ = centerZ;
            for (int attempt = 0; attempt < 4; attempt++) {
                int wallRadius = wallX != 0 ? radiusX : radiusZ;
                int shift = Math.max(0, wallRadius - shaftRadius);
                chamberX = centerX - wallX * shift;
                chamberZ = centerZ - wallZ * shift;
                int room = WINDOW_REACH - Math.max(Math.abs(chamberX - origin.getX()), Math.abs(chamberZ - origin.getZ())) - 1;
                if (radiusX <= room && radiusZ <= room) {
                    break;
                }
                radiusX = Math.min(radiusX, room);
                radiusZ = Math.min(radiusZ, room);
            }
            if (radiusX < MIN_CHAMBER_ROOM || radiusZ < MIN_CHAMBER_ROOM) {
                return landing;
            }

            for (int dx = -radiusX; dx <= radiusX; dx++) {
                for (int dy = 0; dy <= height; dy++) {
                    for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                        double distance = (double) (dx * dx) / (radiusX * radiusX) + (double) (dy * dy) / (height * height) + (double) (dz * dz) / (radiusZ * radiusZ);
                        if (distance <= 1.0 + (random.nextDouble() - 0.5) * WALL_ROUGHNESS) {
                            addCell(new BlockPos(chamberX + dx, y + dy, chamberZ + dz));
                        }
                    }
                }
            }

            int tangentX = random.nextBoolean() ? -wallZ : wallZ;
            int tangentZ = tangentX == 0 ? (random.nextBoolean() ? wallX : -wallX) : 0;
            int length = MIN_WALL_STREAM + random.nextInt(WALL_STREAM_SPREAD);
            for (int step = 1; step <= length; step++) {
                int trenchX = landingX + tangentX * step;
                int trenchZ = landingZ + tangentZ * step;
                if (!cells.contains(new BlockPos(trenchX, y, trenchZ))) {
                    break;
                }
                addTrench(trenchX, trenchZ);
            }
            return landing;
        }

        // Shaft and tunnel keep their whole cross-section inside the window and leave room for a chamber of at least MIN_CHAMBER_ROOM.
        private boolean hasRoom(int anchorX, int anchorZ) {
            int offset = Math.max(Math.abs(anchorX - origin.getX()), Math.abs(anchorZ - origin.getZ()));
            return offset <= Math.min(WINDOW_REACH - MAX_CROSS_SECTION_RADIUS - 1, WINDOW_REACH - MIN_CHAMBER_ROOM);
        }

        // Water cannot bore through a cross-section that is mostly hard rock.
        private boolean blocked(int anchorX, int level, int anchorZ, List<int[]> crossSection) {
            int hard = 0;
            for (int[] offset : crossSection) {
                if (isHard(new BlockPos(anchorX + offset[0], level, anchorZ + offset[1]))) {
                    hard++;
                }
            }
            return hard * 2 >= crossSection.size();
        }

        private boolean isHard(BlockPos pos) {
            return hardness.applyAsInt(pos) >= HARD;
        }

        // Erosion only ever removes erodible material, and nothing outside the write window.
        private boolean addCell(BlockPos pos) {
            if (Math.abs(pos.getX() - origin.getX()) > WINDOW_REACH || Math.abs(pos.getZ() - origin.getZ()) > WINDOW_REACH || isHard(pos)) {
                return false;
            }
            cells.add(pos);
            return true;
        }
    }
}
