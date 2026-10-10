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
 * Java port of Bedrock's "minecraft:fixup_waterlily_position_feature", placed by
 * "moorlands_surface_waterlily_feature". Bedrock samples a broad y range and then "fixes up" the
 * position to the nearest valid spot; here we start at the placement origin (from a heightmap
 * placement modifier, see {@link MoorlandFeatures#bootstrapPlaced}) and scan downward for the
 * first water block topped by air, placing a lily pad there. Fails (no-op) if no such spot is
 * found within the search depth, mirroring Bedrock's "optional" fixup behavior.
 */
public class WaterLilyFixupFeature implements Feature {

    private static final int SEARCH_DEPTH = 8;

    public static final MapCodec<WaterLilyFixupFeature> CODEC = MapCodec.unit(WaterLilyFixupFeature::new);

    @Override
    public MapCodec<WaterLilyFixupFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        BlockPos.MutableBlockPos pos = origin.mutable();

        for (int i = 0; i < SEARCH_DEPTH; i++) {
            BlockState state = level.getBlockState(pos);
            BlockState above = level.getBlockState(pos.above());
            if (state.is(Blocks.WATER) && above.isAir()) {
                level.setBlock(pos.above(), Blocks.LILY_PAD.defaultBlockState(), 2);
                return true;
            }
            pos.move(0, -1, 0);
        }
        return false;
    }
}
