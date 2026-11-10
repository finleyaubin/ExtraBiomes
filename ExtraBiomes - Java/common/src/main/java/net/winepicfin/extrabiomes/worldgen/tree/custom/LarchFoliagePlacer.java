package net.winepicfin.extrabiomes.worldgen.tree.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * A slender larch crown: a bare leader spire, then diamond-shaped branch whorls that widen towards
 * the base, separated by thinner rows so the tiers read from a distance, with tips that droop.
 */
public class LarchFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<LarchFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance)
                    .and(IntProviders.codec(0, 24).fieldOf("trunk_height").forGetter(placer -> placer.trunkHeight))
                    .apply(instance, LarchFoliagePlacer::new));

    private static final int SPIRE_ROWS = 2;
    private static final int MIN_FOLIAGE_HEIGHT = 4;
    private static final int MIN_DROOPING_RADIUS = 2;
    private static final float RAGGED_EDGE_CHANCE = 0.3F;
    private static final float DROOPING_TIP_CHANCE = 0.6F;

    private final IntProvider trunkHeight;

    public LarchFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight) {
        super(radius, offset);
        this.trunkHeight = trunkHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacerTypes.LARCH_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(WorldGenLevel level, FoliageSetter foliageSetter, RandomSource random, TreeFeature config, int treeHeight, FoliageAttachment attachment, int foliageHeight, int leafRadius, int offset) {
        BlockPos crownTop = attachment.pos();
        boolean doubleTrunk = attachment.doubleTrunk();

        for (int depth = 0; depth <= foliageHeight; depth++) {
            int y = offset - depth;
            if (depth < SPIRE_ROWS) {
                placeLeavesRow(level, foliageSetter, random, config, crownTop, 0, y, doubleTrunk);
                continue;
            }

            int whorlRadius = whorlRadiusAt(depth, foliageHeight, leafRadius);
            if (isWhorlRow(depth)) {
                placeLeavesRow(level, foliageSetter, random, config, crownTop, whorlRadius, y, doubleTrunk);
                hangDroopingTips(level, foliageSetter, random, config, crownTop.offset(0, y - 1, 0), whorlRadius);
            } else {
                placeLeavesRow(level, foliageSetter, random, config, crownTop, whorlRadius - 1, y, doubleTrunk);
            }
        }
    }

    private static boolean isWhorlRow(int depth) {
        return depth % 2 == 0;
    }

    private static int whorlRadiusAt(int depth, int foliageHeight, int maxRadius) {
        float progressToBase = (depth - SPIRE_ROWS) / (float) Math.max(1, foliageHeight - SPIRE_ROWS);
        return 1 + Math.round((maxRadius - 1) * progressToBase);
    }

    private static void hangDroopingTips(WorldGenLevel level, FoliageSetter foliageSetter, RandomSource random, TreeFeature config, BlockPos centerBelowWhorl, int whorlRadius) {
        if (whorlRadius < MIN_DROOPING_RADIUS) return;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < DROOPING_TIP_CHANCE) {
                tryPlaceLeaf(level, foliageSetter, random, config, centerBelowWhorl.relative(direction, whorlRadius));
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int treeHeight, TreeFeature config) {
        return Math.max(MIN_FOLIAGE_HEIGHT, treeHeight - this.trunkHeight.sample(random));
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
        int distanceFromTrunk = dx + dz;
        if (distanceFromTrunk > currentRadius) return true;

        boolean diagonalEdge = distanceFromTrunk == currentRadius && dx > 0 && dz > 0;
        return diagonalEdge && random.nextFloat() < RAGGED_EDGE_CHANCE;
    }
}
