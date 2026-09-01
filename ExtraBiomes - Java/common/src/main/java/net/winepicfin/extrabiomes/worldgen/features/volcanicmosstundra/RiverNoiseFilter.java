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
 * Multi-sine "river shore" formula adapted from Bedrock's
 * volcanic_moss_tundra_{basalt_bank,lava_river_bank,lava_river_core}_feature.json
 * (feature_rules/volcanic_moss_tundra/*): each column's world x/z is fed through
 * {@code |sin(x*0.078) + sin(z*0.102) + sin((x+z)*0.042) + sin((x-z)*0.066)| / 8} and only kept
 * when the result falls in that feature's band.
 * <p>
 * Bedrock's own {@code iterations: 30, scatter_chance: 90} random per-chunk sampling (and Java
 * equivalents of it up to {@code CountPlacement.of(80)}) were tested in-game and never render as a
 * continuous river - the core band is well under one hit per chunk on average, so most chunks get
 * nothing and the ones that do get an isolated speck. This modifier is instead the first placement
 * modifier in its chains (it receives the raw chunk origin) and exhaustively enumerates all 256
 * columns of the chunk, which is what actually reproduces Bedrock's rendered look given the same
 * formula. The real height is left to whatever placement modifier runs after this (typically
 * {@link net.minecraft.world.level.levelgen.placement.HeightmapPlacement}).
 * <p>
 * Exhaustive per-chunk coverage crashed live worldgen ({@code IllegalStateException: Requested chunk
 * unavailable during world generation}) under this mod's normal decoration load - the crash report
 * pinned it on {@link net.minecraft.world.level.levelgen.placement.BiomeFilter}'s {@code getBiome}
 * call, but that survived even after {@link VolcanicMossTundraFeatures#bootstrapPlaced} was changed
 * to no longer chain {@code BiomeFilter.biome()} after this modifier for lava_river_core/bank/
 * basalt_bank (confirmed via decompiling the actual built class - those three registrations truly
 * have no BiomeFilter call), so the crash report's attribution is not reliable evidence of the exact
 * cause under this much concurrent chunk generation. {@code EDGE_MARGIN} trims the scan away from
 * the two outermost columns on each side of the chunk as a defensive mitigation (less total work per
 * chunk, and avoids the columns most likely to need a neighboring chunk's data for anything that
 * does resolve near an edge) - if crashes recur, that is the next thing to narrow down, not a reason
 * to fall back to random sampling (that renders as disconnected specks - see above).
 * The three bands partition the same noise field into concentric rings - core (&lt;0.003, the
 * narrowest/most central band) is the lava channel itself, bank (0.003-0.006) is the magma rim
 * around it, and basalt_bank (0.006-0.01) is the next ring out where basalt columns cluster.
 */
public class RiverNoiseFilter extends PlacementModifier {
    private static final int EDGE_MARGIN = 2;

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

    // Halved from Bedrock's original 0.078/0.102/0.042/0.066 to double the pattern's wavelength -
    // the 1:1 frequencies produced a tight crisscrossing lattice of many rivers; this spreads it
    // into fewer, larger meanders across the same area.
    private static final double SCALE_X = 0.039;
    private static final double SCALE_Z = 0.051;
    private static final double SCALE_SUM = 0.021;
    private static final double SCALE_DIFF = 0.033;

    public static double noise(int worldX, int worldZ) {
        return Math.abs((Math.sin(worldX * SCALE_X) + Math.sin(worldZ * SCALE_Z)
                + Math.sin((worldX + worldZ) * SCALE_SUM) + Math.sin((worldX - worldZ) * SCALE_DIFF)) / 8.0);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        int originX = pos.getX();
        int originZ = pos.getZ();
        int y = pos.getY();
        Stream.Builder<BlockPos> matches = Stream.builder();
        for (int dx = EDGE_MARGIN; dx < 16 - EDGE_MARGIN; dx++) {
            for (int dz = EDGE_MARGIN; dz < 16 - EDGE_MARGIN; dz++) {
                int worldX = originX + dx;
                int worldZ = originZ + dz;
                double v = noise(worldX, worldZ);
                if (v >= min && v < max) matches.add(new BlockPos(worldX, y, worldZ));
            }
        }
        return matches.build();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModVolcanicPlacementModifiers.RIVER_NOISE_FILTER.get();
    }
}
