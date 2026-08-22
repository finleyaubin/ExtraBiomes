package net.winepicfin.extrabiomes.worldgen.features.moorland;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Java port of the Bedrock "minecraft:grass_double_plant_patch_feature" placed by
 * "moorlands_scatter_double_tall_grass_feature". Vanilla's own Feature.SIMPLE_BLOCK only ever
 * sets a single block, so double-tall plants (tall grass) need both halves set explicitly.
 */
public class DoubleTallGrassFeature extends Feature<NoneFeatureConfiguration> {

    public DoubleTallGrassFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos lower = context.origin();
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
