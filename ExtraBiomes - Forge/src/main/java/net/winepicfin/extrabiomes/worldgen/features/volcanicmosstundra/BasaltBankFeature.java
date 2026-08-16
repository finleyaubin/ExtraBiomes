package net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Port of Bedrock's extrabiomes:volcanic_moss_tundra/basalt_bank aggregate_feature: a fixed
 * diagonal-tapering grid of 30 basalt_pillar_N growing_plant_feature placements
 * (features/volcanic_moss_tundra/basalt_bank/c{2-7}_r{0-5}.json), each a plain basalt column of a
 * fixed height (basalt_pillar_1..5 = height 3/4/5/6/7 respectively) at a fixed (x, z) offset from
 * the aggregate's origin, with its own Y independently resolved from
 * {@code query.heightmap(worldx, worldz) - 2} at that column's own world position (not the
 * origin's). Reproduced here as one hardcoded offset table rather than 30 separate
 * ConfiguredFeature/PlacedFeature registrations, since none of the 30 need independent
 * registration, rotation, or reuse elsewhere.
 */
public class BasaltBankFeature extends Feature<NoneFeatureConfiguration> {
    // {dx, dz, height} - see basalt_bank/c{col}_r{row}.json (dx=col, dz=row) and each entry's
    // "places_feature" -> basalt_pillar_1..5 height (3,4,5,6,7 respectively).
    private static final int[][] OFFSETS = {
            {2, 0, 7}, {2, 1, 5}, {2, 2, 7}, {2, 3, 5}, {2, 4, 7}, {2, 5, 5},
            {3, 0, 4}, {3, 1, 6}, {3, 2, 4}, {3, 3, 6}, {3, 4, 4}, {3, 5, 6},
            {4, 0, 5}, {4, 1, 3}, {4, 2, 5}, {4, 3, 3}, {4, 4, 5}, {4, 5, 3},
            {5, 0, 3}, {5, 1, 5}, {5, 2, 3}, {5, 3, 5}, {5, 4, 3}, {5, 5, 5},
            {6, 1, 3}, {6, 3, 3}, {6, 5, 3},
            {7, 0, 3}, {7, 2, 3}, {7, 4, 3},
    };

    public BasaltBankFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        boolean placedAny = false;
        for (int[] offset : OFFSETS) {
            int worldX = origin.getX() + offset[0];
            int worldZ = origin.getZ() + offset[1];
            int height = offset[2];
            int baseY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ) - 2;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(worldX, baseY, worldZ);
            for (int i = 0; i < height; i++) {
                level.setBlock(pos, Blocks.BASALT.defaultBlockState(), 2);
                pos.move(0, 1, 0);
                placedAny = true;
            }
        }
        return placedAny;
    }
}
