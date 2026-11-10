package net.winepicfin.extrabiomes.worldgen.features.larchforest;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Dusts a ragged blob of ground with snow layers, for the first-snowfall look below the biome's real snowline. */
public class SnowPatchFeature implements Feature {
    private static final int MIN_RADIUS = 2;
    private static final int MAX_RADIUS = 4;

    public static final MapCodec<SnowPatchFeature> CODEC = MapCodec.unit(SnowPatchFeature::new);

    @Override
    public MapCodec<SnowPatchFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int radius = Mth.nextInt(random, MIN_RADIUS, MAX_RADIUS);
        boolean placedAnySnow = false;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (isOutsidePatch(dx, dz, radius, random)) continue;

                BlockPos aboveGround = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin.offset(dx, 0, dz));
                placedAnySnow |= dustWithSnow(level, aboveGround);
            }
        }
        return placedAnySnow;
    }

    private static boolean isOutsidePatch(int dx, int dz, int radius, RandomSource random) {
        int distanceSquared = dx * dx + dz * dz;
        if (distanceSquared > radius * radius) return true;

        boolean onRim = distanceSquared > (radius - 1) * (radius - 1);
        return onRim && random.nextBoolean();
    }

    private static boolean dustWithSnow(WorldGenLevel level, BlockPos pos) {
        BlockState snow = Blocks.SNOW.defaultBlockState();
        if (!level.getBlockState(pos).isAir() || !snow.canSurvive(level, pos)) return false;

        level.setBlock(pos, snow, 2);
        BlockState ground = level.getBlockState(pos.below());
        if (ground.hasProperty(BlockStateProperties.SNOWY)) {
            level.setBlock(pos.below(), ground.setValue(BlockStateProperties.SNOWY, true), 2);
        }
        return true;
    }
}
