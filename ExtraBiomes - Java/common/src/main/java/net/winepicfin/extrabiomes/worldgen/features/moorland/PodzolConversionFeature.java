package net.winepicfin.extrabiomes.worldgen.features.moorland;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Java port of Bedrock's "extrabiomes:moorland/moorlands_podzol_feature", an aggregate that
 * unconditionally wraps a single "minecraft:optional_podzol_feature". That vanilla Bedrock
 * feature converts the surface block beneath the placement position into podzol when the
 * ground is a valid target, and is a no-op otherwise (mirroring the aggregate's
 * "early_out": "first_failure" semantics - since it wraps only one feature, that just means
 * "succeed or fail based on whether the ground was convertible").
 * <p>
 * The placement position ({@link FeaturePlaceContext#origin()}) is expected to come from a
 * heightmap placement modifier (see {@link MoorlandFeatures#bootstrapPlaced}), which lands on
 * the first free (air) position above the ground - so the actual surface block to convert is
 * one below the origin.
 */
public class PodzolConversionFeature implements Feature {

    public static final MapCodec<PodzolConversionFeature> CODEC = MapCodec.unit(PodzolConversionFeature::new);

    @Override
    public MapCodec<PodzolConversionFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        BlockPos ground = origin.below();
        BlockState state = level.getBlockState(ground);

        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)) {
            level.setBlock(ground, Blocks.PODZOL.defaultBlockState(), 2);
            return true;
        }
        return false;
    }
}
