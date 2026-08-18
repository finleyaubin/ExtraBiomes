package net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

/**
 * Java equivalent of Bedrock's {@code (query.heightmap(...) >= 75) ? query.heightmap(...) : 300}
 * pattern used by volcanic_moss_tundra_elevation_moss_feature.json and
 * volcanic_moss_tundra_high_elevation_moss_floor_feature.json: keeps a candidate position only
 * when its Y (already resolved onto the heightmap by an earlier
 * {@link net.minecraft.world.level.levelgen.placement.HeightmapPlacement} modifier) is at or above
 * {@code minY} - i.e. moss only grows on high ground, matching the biome's summit-only moss theme.
 */
public class MinYFilter extends PlacementModifier {
    public static final Codec<MinYFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("min_y").forGetter(MinYFilter::minY)
    ).apply(instance, MinYFilter::new));

    private final int minY;

    public MinYFilter(int minY) {
        this.minY = minY;
    }

    public int minY() {
        return minY;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        return pos.getY() >= minY ? Stream.of(pos) : Stream.of();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModVolcanicPlacementModifiers.MIN_Y_FILTER.get();
    }
}
