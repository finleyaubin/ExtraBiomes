package net.winepicfin.extrabiomes.worldgen.features.moorland;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

// Walks every column of the chunk so almost the whole biome floor is tall grass, replacing the short grass vanilla's plains vegetation already placed there.
public class DoubleTallGrassFeature implements Feature {
    private static final float COVERAGE = 0.95F;

    public static final MapCodec<DoubleTallGrassFeature> CODEC = MapCodec.unit(DoubleTallGrassFeature::new);

    @Override
    public MapCodec<DoubleTallGrassFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        ChunkPos chunkPos = ChunkPos.containing(origin);
        boolean placedAny = false;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkPos.getMinBlockX() + x;
                int worldZ = chunkPos.getMinBlockZ() + z;
                BlockPos lower = new BlockPos(worldX, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ), worldZ);
                if (random.nextFloat() < COVERAGE && plantTallGrass(level, lower)) {
                    placedAny = true;
                }
            }
        }
        return placedAny;
    }

    private static boolean plantTallGrass(WorldGenLevel level, BlockPos lower) {
        BlockPos upper = lower.above();
        BlockState lowerState = level.getBlockState(lower);
        if (!level.getBiome(lower).is(ModBiomes.MOORLANDS)
                || !(lowerState.isAir() || lowerState.is(Blocks.SHORT_GRASS))
                || !level.getBlockState(upper).isAir()
                || !level.getBlockState(lower.below()).is(BlockTags.DIRT)) {
            return false;
        }
        level.setBlock(lower, Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 2);
        level.setBlock(upper, Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
        return true;
    }
}
