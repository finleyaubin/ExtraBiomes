package net.winepicfin.extrabiomes.worldgen.features.glacier;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

public class GlacierFinishFeature implements Feature {
    public static final MapCodec<GlacierFinishFeature> CODEC = MapCodec.unit(GlacierFinishFeature::new);

    // Lava lakes spill up to ~9 blocks into neighbouring chunks whose own pass already ran, so lava is rimmed across a margin; the rim stays inside the 3x3 chunk write window.
    private static final int LAVA_MARGIN = 10;

    @Override
    public MapCodec<GlacierFinishFeature> codec() {
        return CODEC;
    }

    // Runs once per chunk at its origin, after every other Glacier feature, so it also catches vanilla lava lakes and aquifer lava.
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        boolean changed = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -LAVA_MARGIN; dx < 16 + LAVA_MARGIN; dx++) {
            for (int dz = -LAVA_MARGIN; dz < 16 + LAVA_MARGIN; dz++) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                boolean inChunk = dx >= 0 && dx < 16 && dz >= 0 && dz < 16;
                int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                for (int y = top; y >= level.getMinY(); y--) {
                    // Surface water is left alone: vanilla surface freezing handles ponds, and meltwater streams must keep flowing.
                    boolean underground = y + 1 < top;
                    changed |= finishBlock(level, pos.set(x, y, z), inChunk && underground);
                }
            }
        }
        return changed;
    }

    private static boolean finishBlock(WorldGenLevel level, BlockPos.MutableBlockPos pos, boolean freezeAllowed) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.LAVA) && level.getBiome(pos).is(ModBiomes.GLACIER)) {
            return rimWithBasalt(level, pos);
        }
        boolean exposedWater = state.is(Blocks.WATER) && state.getFluidState().isSource() && level.getBlockState(pos.above()).isAir();
        if (freezeAllowed && exposedWater && level.getBiome(pos).is(ModBiomes.GLACIER)) {
            level.setBlock(pos, Blocks.ICE.defaultBlockState(), 2);
            return true;
        }
        return false;
    }

    private static boolean rimWithBasalt(WorldGenLevel level, BlockPos lava) {
        boolean changed = false;
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = lava.relative(direction);
            BlockState state = level.getBlockState(neighbor);
            boolean rock = !state.isAir() && state.getFluidState().isEmpty() && !state.hasBlockEntity();
            if (rock && !state.is(Blocks.BASALT) && !state.is(Blocks.BEDROCK)) {
                level.setBlock(neighbor, Blocks.BASALT.defaultBlockState(), 2);
                changed = true;
            }
        }
        return changed;
    }
}
