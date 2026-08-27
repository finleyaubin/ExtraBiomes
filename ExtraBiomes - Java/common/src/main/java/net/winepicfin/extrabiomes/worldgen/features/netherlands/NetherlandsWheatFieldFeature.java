package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Deterministic replacement for a scattered wheat patch: vanilla's CountPlacement/InSquarePlacement
 * combo samples columns independently with replacement, so no attempt count ever guarantees every
 * column gets hit (and most attempts past the first ~256 just overwrite a column already set). Since
 * ModSurfaceRules already paints this biome's entire floor as FARMLAND, this feature is invoked once
 * per chunk (see NetherlandsWheatFeatures - no CountPlacement/InSquarePlacement on its PlacedFeature)
 * and iterates all 256 columns directly, so every farmland column gets exactly one wheat block with
 * no wasted re-placement and no probabilistic gaps.
 * <p>
 * Hydration pockets are rolled in this SAME pass rather than as a separate PlacedFeature: a second
 * placed feature running after this one would have to re-locate the farmland surface via a heightmap
 * query that now also has to see past whatever this feature just placed on top of it, which is a
 * needless source of bugs for no benefit - this feature already visits every column once, so a
 * fraction of them becoming water instead of wheat costs nothing extra.
 */
public class NetherlandsWheatFieldFeature extends Feature<NoneFeatureConfiguration> {
    private static final float HYDRATION_CHANCE = 1f / 12f;

    public NetherlandsWheatFieldFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        ChunkPos chunkPos = new ChunkPos(context.origin());
        boolean placedAny = false;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkPos.getMinBlockX() + x;
                int worldZ = chunkPos.getMinBlockZ() + z;
                BlockPos target = new BlockPos(worldX, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ), worldZ);
                BlockPos farmland = target.below();

                if (!level.getBlockState(target).isAir() || !level.getBlockState(farmland).is(Blocks.FARMLAND)) {
                    continue;
                }

                if (random.nextFloat() < HYDRATION_CHANCE && isHydratable(level, farmland)) {
                    level.setBlock(farmland, Blocks.WATER.defaultBlockState(), 2);
                    placedAny = true;
                    continue;
                }

                int age = random.nextFloat() < 0.625F ? 7 : 4;
                level.setBlock(target, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, age), 2);
                placedAny = true;
            }
        }
        return placedAny;
    }

    // All 4 horizontal neighbours must still be farmland and the block below still netherrack, so
    // turning this column into an open pond is contained on every side it could flow out of - the
    // farmland/netherrack itself is the wall, no separate liner needed.
    private static boolean isHydratable(WorldGenLevel level, BlockPos farmland) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!level.getBlockState(farmland.relative(direction)).is(Blocks.FARMLAND)) {
                return false;
            }
        }
        return level.getBlockState(farmland.below()).is(Blocks.NETHERRACK);
    }
}
