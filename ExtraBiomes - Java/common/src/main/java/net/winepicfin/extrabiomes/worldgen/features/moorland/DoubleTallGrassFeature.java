package net.winepicfin.extrabiomes.worldgen.features.moorland;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Java port of the Bedrock "minecraft:grass_double_plant_patch_feature" placed by
 * "moorlands_scatter_double_tall_grass_feature". Vanilla's own Feature.SIMPLE_BLOCK only ever
 * sets a single block, so double-tall plants (tall grass) need both halves set explicitly.
 */
public class DoubleTallGrassFeature implements Feature {

    public static final MapCodec<DoubleTallGrassFeature> CODEC = MapCodec.unit(DoubleTallGrassFeature::new);

    @Override
    public MapCodec<DoubleTallGrassFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos lower) {
        BlockPos upper = lower.above();
        BlockPos ground = lower.below();

        if (!level.getBlockState(lower).isAir() || !level.getBlockState(upper).isAir()) {
            return false;
        }
        if (!level.getBlockState(ground).is(BlockTags.DIRT)) {
            return false;
        }

        level.setBlock(lower, Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 2);
        level.setBlock(upper, Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
        return true;
    }
}
