package net.winepicfin.extrabiomes.worldgen.features.glacier;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MeltwaterStreamFeature extends Feature<NoneFeatureConfiguration> {
    public MeltwaterStreamFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }


    private static final int MIN_LENGTH = 4;
    private static final int MAX_STEPS = 20;
    // Plunge pools and moulins reach one block past the path, so this keeps every write inside the 3x3 chunk window.
    private static final int MAX_REACH = 13;
    // The conduit is planned relative to the feature origin, so a cavern stream must end close to it to leave room for a wide shaft, tunnels and chamber inside the write window.
    private static final int CAVERN_START_REACH = 3;
    private static final int MIN_CAVERN_LENGTH = 3;
    private static final int CAVERN_ONE_IN = 3;
    private static final float MEANDER_CHANCE = 0.25F;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkGenerator generator = context.chunkGenerator();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        if (origin.getY() <= generator.getSeaLevel()) {
            return false;
        }

        List<BlockPos> path = walkDownhill(level, random, origin);
        if (path.size() < MIN_LENGTH) {
            return false;
        }

        // A cavern needs the stream's end near the origin so its chamber stays inside the chunk write window.
        List<BlockPos> shortPath = path.stream().takeWhile(pos -> withinReach(pos, origin, CAVERN_START_REACH)).toList();
        boolean cavern = random.nextInt(CAVERN_ONE_IN) == 0 && shortPath.size() >= MIN_CAVERN_LENGTH;
        if (cavern) {
            path = shortPath;
        }

        path.forEach(pos -> placeWater(level, pos));
        BlockPos end = path.get(path.size() - 1);
        BlockPos upstream = path.get(Math.max(0, path.size() - 2));
        if (!cavern || !digCavern(level, random, end, end.getX() - upstream.getX(), end.getZ() - upstream.getZ(), origin)) {
            carvePlungePool(level, end);
        }
        return true;
    }

    private static List<BlockPos> walkDownhill(WorldGenLevel level, RandomSource random, BlockPos origin) {
        List<BlockPos> path = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        int x = origin.getX();
        int z = origin.getZ();
        for (int step = 0; step < MAX_STEPS; step++) {
            int ground = groundY(level, x, z);
            if (!level.getBlockState(new BlockPos(x, ground + 1, z)).getFluidState().isEmpty()) {
                break;
            }
            path.add(new BlockPos(x, ground, z));
            visited.add(columnKey(x, z));

            int[] next = pickDownhill(level, random, origin, visited, x, z, ground);
            if (next == null) {
                break;
            }
            x = next[0];
            z = next[1];
        }
        return path;
    }

    private static int[] pickDownhill(WorldGenLevel level, RandomSource random, BlockPos origin, Set<Long> visited, int x, int z, int ground) {
        List<int[]> candidates = new ArrayList<>();
        int lowest = Integer.MAX_VALUE;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int nextX = x + direction.getStepX();
            int nextZ = z + direction.getStepZ();
            boolean outOfReach = Math.abs(nextX - origin.getX()) > MAX_REACH || Math.abs(nextZ - origin.getZ()) > MAX_REACH;
            if (outOfReach || visited.contains(columnKey(nextX, nextZ))) {
                continue;
            }
            int nextGround = groundY(level, nextX, nextZ);
            if (nextGround <= ground) {
                candidates.add(new int[]{nextX, nextZ, nextGround});
                lowest = Math.min(lowest, nextGround);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }

        if (random.nextFloat() >= MEANDER_CHANCE) {
            int steepest = lowest;
            candidates.removeIf(candidate -> candidate[2] > steepest);
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private static void carvePlungePool(WorldGenLevel level, BlockPos end) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                boolean corner = dx != 0 && dz != 0;
                BlockPos column = end.offset(dx, 0, dz);
                if (corner || Math.abs(groundY(level, column.getX(), column.getZ()) - end.getY()) > 1) {
                    continue;
                }
                placeWater(level, column);
                placeWater(level, column.below());
            }
        }
    }

    // The meltwater bores an open shaft down through the ice; where it meets hard rock it turns and runs sideways along the contact, and it ends in a large chamber. A single one-block stream hugs one wall throughout.
    private static boolean digCavern(WorldGenLevel level, RandomSource random, BlockPos end, int flowX, int flowZ, BlockPos origin) {
        CavernPlan plan = CavernPlan.plan(random, end, flowX, flowZ, origin, pos -> hardness(level.getBlockState(pos)));
        if (touchesLava(level, plan)) {
            return false;
        }

        for (BlockPos cell : plan.cells()) {
            BlockState state = level.getBlockState(cell);
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                level.setBlock(cell, Blocks.AIR.defaultBlockState(), 2);
            }
        }
        // Sources go in after the carving, so a channel cell that was also carved still ends up as water.
        for (BlockPos cell : plan.water()) {
            BlockState state = level.getBlockState(cell);
            if (state.getFluidState().isEmpty() && !state.hasBlockEntity() && !state.is(Blocks.BEDROCK)) {
                setSource(level, cell);
            }
        }
        return true;
    }

    // Ice, snow, loose sediment and soluble rock erode readily; everything else, including ores and bedrock, resists.
    private static int hardness(BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty()) {
            return 0;
        }
        if (state.hasBlockEntity() || state.is(Blocks.BEDROCK)) {
            return CavernPlan.HARD + 1;
        }
        boolean erodible = state.is(BlockTags.ICE) || state.is(BlockTags.SNOW) || state.is(BlockTags.SAND) || state.is(BlockTags.DIRT)
                || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY) || state.is(Blocks.CALCITE) || state.is(Blocks.TUFF) || state.is(Blocks.SANDSTONE);
        return erodible ? 1 : CavernPlan.HARD;
    }

    private static boolean touchesLava(WorldGenLevel level, CavernPlan plan) {
        return Stream.concat(plan.cells().stream(), plan.water().stream()).anyMatch(cell -> isLavaAt(level, cell) || Arrays.stream(Direction.values()).anyMatch(direction -> isLavaAt(level, cell.relative(direction))));
    }

    private static boolean isLavaAt(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.LAVA);
    }

    private static void placeWater(WorldGenLevel level, BlockPos pos) {
        if (!isCarvable(level.getBlockState(pos))) {
            return;
        }
        setSource(level, pos);
        BlockPos bed = pos.below();
        if (isCarvable(level.getBlockState(bed))) {
            level.setBlock(bed, Blocks.BLUE_ICE.defaultBlockState(), 2);
        }
    }

    // Snow cover from the surface-freezing pass would otherwise be left floating over the new water.
    private static void setSource(WorldGenLevel level, BlockPos pos) {
        level.setBlock(pos, Blocks.WATER.defaultBlockState(), 2);
        // A source placed with flag 2 never starts flowing on its own, so cascades need an explicit tick.
        level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        if (level.getBlockState(pos.above()).is(Blocks.SNOW)) {
            level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 2);
        }
    }

    private static boolean isCarvable(BlockState state) {
        return !state.isAir() && state.getFluidState().isEmpty() && !state.hasBlockEntity();
    }

    private static int groundY(WorldGenLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
    }

    private static boolean withinReach(BlockPos pos, BlockPos origin, int reach) {
        return Math.abs(pos.getX() - origin.getX()) <= reach && Math.abs(pos.getZ() - origin.getZ()) <= reach;
    }

    private static long columnKey(int x, int z) {
        return BlockPos.asLong(x, 0, z);
    }
}
