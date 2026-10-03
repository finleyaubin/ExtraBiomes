package net.winepicfin.extrabiomes.worldgen.features.moorland;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

// Walks every column of the chunk so almost the whole biome floor is tall grass, replacing the short grass vanilla's plains vegetation already placed there.
public class DoubleTallGrassFeature extends Feature<NoneFeatureConfiguration> {
    private static final float COVERAGE = 0.95F;

    public DoubleTallGrassFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkPos chunkPos = new ChunkPos(context.origin());
        boolean placedAny = false;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkPos.getMinBlockX() + x;
                int worldZ = chunkPos.getMinBlockZ() + z;
                BlockPos lower = new BlockPos(worldX, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ), worldZ);
                if (context.random().nextFloat() < COVERAGE && plantTallGrass(level, lower)) {
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
