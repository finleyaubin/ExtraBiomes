package net.winepicfin.extrabiomes.worldgen.features.glacier;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class CeilingLavafallFeature extends Feature<NoneFeatureConfiguration> {
    public CeilingLavafallFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }


    // Lava is light level 15 and regular ice melts near light, then the water meets the lava and makes cobblestone, so everything the lava touches or lights is shielded in basalt.
    private static final int MAX_FALL = 48;
    private static final int CEILING_RADIUS = 3;
    private static final int COLUMN_RADIUS = 2;
    private static final int LANDING_RADIUS = 4;
    private static final int LANDING_WALL_RADIUS = 5;
    private static final int LANDING_WALL_HEIGHT = 4;
    private static final int NO_FLOOR = Integer.MIN_VALUE;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkGenerator generator = context.chunkGenerator();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        if (!level.getBlockState(origin.below()).isAir() || !isSealedRock(level, origin)) {
            return false;
        }
        int floorY = findFloor(level, origin);
        if (floorY == NO_FLOOR) {
            return false;
        }

        for (Direction direction : Direction.values()) {
            if (direction != Direction.DOWN) {
                level.setBlock(origin.relative(direction), Blocks.BASALT.defaultBlockState(), 2);
            }
        }
        level.setBlock(origin, Blocks.LAVA.defaultBlockState(), 2);
        // A source placed with flag 2 never starts flowing on its own, so the fall needs an explicit tick.
        level.scheduleTick(origin, Fluids.LAVA, Fluids.LAVA.getTickDelay(level));

        shieldCeiling(level, origin);
        shieldColumn(level, origin, floorY);
        shieldLanding(level, origin.getX(), floorY, origin.getZ());
        return true;
    }

    // The first solid block below the source; a fall into water or with no floor in reach is skipped, since lava meeting water only makes a mess.
    private static int findFloor(WorldGenLevel level, BlockPos origin) {
        for (int drop = 1; drop <= MAX_FALL; drop++) {
            BlockState state = level.getBlockState(origin.below(drop));
            if (!state.getFluidState().isEmpty()) {
                return NO_FLOOR;
            }
            if (!state.isAir()) {
                return origin.getY() - drop;
            }
        }
        return NO_FLOOR;
    }

    private static void shieldCeiling(WorldGenLevel level, BlockPos origin) {
        for (int up = 0; up <= 1; up++) {
            for (int dx = -CEILING_RADIUS; dx <= CEILING_RADIUS; dx++) {
                for (int dz = -CEILING_RADIUS; dz <= CEILING_RADIUS; dz++) {
                    if (withinRadius(dx, dz, CEILING_RADIUS)) {
                        toBasalt(level, origin.offset(dx, up, dz), false);
                    }
                }
            }
        }
    }

    private static void shieldColumn(WorldGenLevel level, BlockPos origin, int floorY) {
        for (int y = origin.getY() - 1; y > floorY; y--) {
            for (int dx = -COLUMN_RADIUS; dx <= COLUMN_RADIUS; dx++) {
                for (int dz = -COLUMN_RADIUS; dz <= COLUMN_RADIUS; dz++) {
                    if (withinRadius(dx, dz, COLUMN_RADIUS)) {
                        toBasalt(level, new BlockPos(origin.getX() + dx, y, origin.getZ() + dz), false);
                    }
                }
            }
        }
    }

    // Lava spreads a few blocks across the floor where it lands, so the floor and the walls it can reach are basalt too.
    private static void shieldLanding(WorldGenLevel level, int x, int floorY, int z) {
        for (int dx = -LANDING_WALL_RADIUS; dx <= LANDING_WALL_RADIUS; dx++) {
            for (int dz = -LANDING_WALL_RADIUS; dz <= LANDING_WALL_RADIUS; dz++) {
                if (withinRadius(dx, dz, LANDING_RADIUS)) {
                    toBasalt(level, new BlockPos(x + dx, floorY, z + dz), false);
                }
                if (withinRadius(dx, dz, LANDING_WALL_RADIUS)) {
                    for (int up = 1; up <= LANDING_WALL_HEIGHT; up++) {
                        toBasalt(level, new BlockPos(x + dx, floorY + up, z + dz), true);
                    }
                }
            }
        }
    }

    // Walls are only shielded where they face open air, so the basalt lines the cave instead of hollowing out the rock behind it.
    private static void toBasalt(WorldGenLevel level, BlockPos pos, boolean onlyIfExposed) {
        BlockState state = level.getBlockState(pos);
        boolean rock = !state.isAir() && state.getFluidState().isEmpty() && !state.hasBlockEntity() && !state.is(Blocks.BEDROCK) && !state.is(Blocks.BASALT);
        if (rock && (!onlyIfExposed || isExposed(level, pos))) {
            level.setBlock(pos, Blocks.BASALT.defaultBlockState(), 2);
        }
    }

    private static boolean isExposed(WorldGenLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).isAir()) {
                return true;
            }
        }
        return false;
    }

    private static boolean withinRadius(int dx, int dz, int radius) {
        return dx * dx + dz * dz <= radius * radius + radius;
    }

    private static boolean isSealedRock(WorldGenLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (direction != Direction.DOWN && !isSolid(level.getBlockState(pos.relative(direction)))) {
                return false;
            }
        }
        return isSolid(level.getBlockState(pos));
    }

    private static boolean isSolid(BlockState state) {
        return !state.isAir() && state.getFluidState().isEmpty();
    }
}
