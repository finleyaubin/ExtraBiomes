package net.winepicfin.extrabiomes.worldgen.features.glacier;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.HashSet;
import java.util.Set;

public class CrevasseFeature extends Feature<NoneFeatureConfiguration> {
    public CrevasseFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }


    // Kept inside the 3x3 chunk write window: an origin at a chunk edge reaches 13 + 1 lining block either way.
    private static final int MIN_HALF_LENGTH = 6;
    private static final int MAX_HALF_LENGTH = 13;
    private static final int BRIDGE_THICKNESS = 2;
    private static final float WALL_ICE_CHANCE = 0.3F;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkGenerator generator = context.chunkGenerator();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        if (origin.getY() <= generator.getSeaLevel()) {
            return false;
        }

        boolean alongX = random.nextBoolean();
        int halfLength = MIN_HALF_LENGTH + random.nextInt(MAX_HALF_LENGTH - MIN_HALF_LENGTH + 1);
        int depth = 14 + random.nextInt(20);
        int bridgeStart = random.nextInt(halfLength) - halfLength / 2;
        double phase = random.nextDouble() * Math.PI * 2;

        Set<BlockPos> carved = new HashSet<>();
        for (int i = -halfLength; i <= halfLength; i++) {
            double taper = Math.abs(i) / (double) halfLength;
            int columnDepth = (int) (depth * (1 - taper * taper));
            int meander = (int) Math.round(Math.sin(i * 0.4 + phase) * 1.5);
            boolean bridge = i == bridgeStart || i == bridgeStart + 1;
            int reach = taper < 0.5 ? 1 : 0;
            for (int across = -reach; across <= reach; across++) {
                int x = origin.getX() + (alongX ? i : meander + across);
                int z = origin.getZ() + (alongX ? meander + across : i);
                int wallDepth = across == 0 ? columnDepth : columnDepth * 6 / 10;
                carveColumn(level, x, z, wallDepth, bridge, carved);
            }
        }

        lineWalls(level, random, carved);
        return !carved.isEmpty();
    }

    private static void carveColumn(WorldGenLevel level, int x, int z, int depth, boolean bridge, Set<BlockPos> carved) {
        int top = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
        if (!level.getBlockState(new BlockPos(x, top + 1, z)).getFluidState().isEmpty()) {
            return;
        }

        int start = bridge ? top - BRIDGE_THICKNESS : top;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = start; y > top - depth; y--) {
            BlockState state = level.getBlockState(pos.set(x, y, z));
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                carved.add(pos.immutable());
            }
        }

        if (bridge) {
            for (int y = top; y > start; y--) {
                level.setBlock(pos.set(x, y, z), Blocks.SNOW_BLOCK.defaultBlockState(), 2);
            }
        }
    }

    private static void lineWalls(WorldGenLevel level, RandomSource random, Set<BlockPos> carved) {
        for (BlockPos pos : carved) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos neighbor = pos.relative(direction);
                if (carved.contains(neighbor) || random.nextFloat() >= WALL_ICE_CHANCE) {
                    continue;
                }
                BlockState state = level.getBlockState(neighbor);
                if (!state.isAir() && state.getFluidState().isEmpty()) {
                    level.setBlock(neighbor, Blocks.BLUE_ICE.defaultBlockState(), 2);
                }
            }
        }
    }
}
