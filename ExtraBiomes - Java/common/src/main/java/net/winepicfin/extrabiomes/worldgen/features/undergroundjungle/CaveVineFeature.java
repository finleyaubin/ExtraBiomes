package net.winepicfin.extrabiomes.worldgen.features.undergroundjungle;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Java port of Bedrock's {@code features/underground_jungle/cave_vine_feature.json}
 * ({@code minecraft:growing_plant_feature}, growth_direction DOWN) combined with its wrapping
 * {@code cave_vine_snap_to_ceiling_feature.json} ({@code minecraft:snap_to_surface_feature},
 * vertical_search_range 12, surface "ceiling"). Per project convention the snap-to-surface wrapper
 * is folded directly into this single Java {@link Feature} rather than becoming a second feature:
 * {@link #place} first searches outward (up to {@link #CEILING_SEARCH_RANGE} blocks, alternating
 * up/down) from the placement origin for a ceiling attachment point (an empty block with a
 * face-sturdy block directly above it), then grows the vine chain downward from there.
 * <p>
 * Bedrock field mapping:
 * <ul>
 *   <li>{@code height_distribution} (three weighted uniform ranges: [1,13] weight 2, [1,2] weight 3,
 *       [1,7] weight 10) -&gt; {@link WeightedListInt} of {@link UniformInt} providers - the closest
 *       direct Java equivalent of a "weighted list of ranges" and used the same way vanilla itself
 *       composes such distributions (see e.g. {@code TreeFeatures}).</li>
 *   <li>{@code age} (range 17-26) has no faithful Java equivalent: {@code CaveVinesBlock}'s
 *       {@link GrowingPlantHeadBlock#AGE} property only spans 0-{@link GrowingPlantHeadBlock#MAX_AGE}
 *       (25) and is a growth-mechanics value (whether the vine can still lengthen via bonemeal/random
 *       tick), not a visual length like in Bedrock. It is approximated with a uniform 0-25 pick so the
 *       property is still populated with a plausible in-range value.</li>
 *   <li>{@code body_blocks} ({@code minecraft:cave_vines} weight 4 vs
 *       {@code minecraft:cave_vines_body_with_berries} weight 1) and {@code head_blocks}
 *       ({@code minecraft:cave_vines} weight 4 vs {@code minecraft:cave_vines_head_with_berries}
 *       weight 4) map directly onto vanilla's single {@link Blocks#CAVE_VINES_PLANT} (body) /
 *       {@link Blocks#CAVE_VINES} (head) blocks, since Bedrock's "with_berries" permutations are
 *       simply the {@link CaveVines#BERRIES} boolean state of those same two Java blocks - no
 *       separate blocks are needed.</li>
 * </ul>
 */
public class CaveVineFeature extends Feature<NoneFeatureConfiguration> {

    private static final int CEILING_SEARCH_RANGE = 12;

    /** cave_vine_feature.json's height_distribution, ported verbatim as a weighted list of ranges. */
    private static final IntProvider HEIGHT_DISTRIBUTION = new WeightedListInt(
            SimpleWeightedRandomList.<IntProvider>builder()
                    .add(UniformInt.of(1, 13), 2)
                    .add(UniformInt.of(1, 2), 3)
                    .add(UniformInt.of(1, 7), 10)
                    .build());

    public CaveVineFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        BlockPos attachPoint = findCeiling(level, context.origin());
        if (attachPoint == null) {
            return false;
        }

        int height = HEIGHT_DISTRIBUTION.sample(random);
        if (height <= 0) {
            return false;
        }

        int age = random.nextInt(GrowingPlantHeadBlock.MAX_AGE + 1);
        BlockPos.MutableBlockPos pos = attachPoint.mutable();
        boolean placedAny = false;

        for (int i = 0; i < height; i++) {
            if (!level.isEmptyBlock(pos)) {
                break;
            }

            boolean isHead = i == height - 1;
            BlockState state;
            if (isHead) {
                // head_blocks: cave_vines(4) : cave_vines_head_with_berries(4) -> 50/50
                boolean berries = random.nextInt(8) < 4;
                state = Blocks.CAVE_VINES.defaultBlockState()
                        .setValue(CaveVines.BERRIES, berries)
                        .setValue(GrowingPlantHeadBlock.AGE, age);
            } else {
                // body_blocks: cave_vines(4) : cave_vines_body_with_berries(1) -> 1/5 chance of berries
                boolean berries = random.nextInt(5) == 0;
                state = Blocks.CAVE_VINES_PLANT.defaultBlockState().setValue(CaveVines.BERRIES, berries);
            }

            level.setBlock(pos, state, 2);
            placedAny = true;
            pos.move(Direction.DOWN);
        }

        return placedAny;
    }

    /**
     * Approximates {@code minecraft:snap_to_surface_feature}'s {@code surface: "ceiling"} search:
     * scans outward from {@code origin} (alternating up/down) within {@link #CEILING_SEARCH_RANGE}
     * blocks for an empty position with a face-sturdy block directly above it.
     */
    private static BlockPos findCeiling(WorldGenLevel level, BlockPos origin) {
        if (isCeiling(level, origin)) {
            return origin;
        }
        for (int offset = 1; offset <= CEILING_SEARCH_RANGE; offset++) {
            BlockPos up = origin.above(offset);
            if (isCeiling(level, up)) {
                return up;
            }
            BlockPos down = origin.below(offset);
            if (isCeiling(level, down)) {
                return down;
            }
        }
        return null;
    }

    private static boolean isCeiling(WorldGenLevel level, BlockPos pos) {
        return level.isEmptyBlock(pos) && level.getBlockState(pos.above()).isFaceSturdy(level, pos.above(), Direction.DOWN);
    }
}
