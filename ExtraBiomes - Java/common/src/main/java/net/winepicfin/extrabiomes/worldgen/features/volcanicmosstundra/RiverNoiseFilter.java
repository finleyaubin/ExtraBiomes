package net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

/**
 * Java equivalent of the hand-rolled multi-sine "river shore" formula shared by Bedrock's
 * volcanic_moss_tundra_{basalt_bank,lava_river_bank,lava_river_core}_feature.json
 * (feature_rules/volcanic_moss_tundra/*): each attempt's world x/z is fed through
 * {@code |sin(x*0.078) + sin(z*0.102) + sin((x+z)*0.042) + sin((x-z)*0.066)| / 8} and only kept
 * when the result falls in that feature's band - Bedrock does this by conditionally returning the
 * "distribution.y" value or the sentinel 300 (above the build limit, i.e. "don't place here");
 * this modifier reproduces the same gate as a keep/reject filter instead, leaving the real height
 * to whatever placement modifier runs after it (typically {@link net.minecraft.world.level.levelgen.placement.HeightmapPlacement}).
 * The three bands partition the same noise field into concentric rings - core (&lt;0.003, the
 * narrowest/most central band) is the lava channel itself, bank (0.003-0.006) is the magma rim
 * around it, and basalt_bank (0.006-0.01) is the next ring out where basalt columns cluster.
 */
public class RiverNoiseFilter extends PlacementModifier {
    public static final MapCodec<RiverNoiseFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("min").forGetter(RiverNoiseFilter::min),
            Codec.DOUBLE.fieldOf("max").forGetter(RiverNoiseFilter::max)
    ).apply(instance, RiverNoiseFilter::new));

    private final double min;
    private final double max;

    public RiverNoiseFilter(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public static double noise(int worldX, int worldZ) {
        return Math.abs((Math.sin(worldX * 0.078) + Math.sin(worldZ * 0.102)
                + Math.sin((worldX + worldZ) * 0.042) + Math.sin((worldX - worldZ) * 0.066)) / 8.0);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        double v = noise(pos.getX(), pos.getZ());
        return (v >= min && v < max) ? Stream.of(pos) : Stream.of();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModVolcanicPlacementModifiers.RIVER_NOISE_FILTER.get();
    }
}
