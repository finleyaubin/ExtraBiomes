package net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.function.Consumer;

/**
 * Java equivalent of Bedrock's {@code (query.heightmap(...) >= 75) ? query.heightmap(...) : 300}
 * pattern used by volcanic_moss_tundra_elevation_moss_feature.json and
 * volcanic_moss_tundra_high_elevation_moss_floor_feature.json, extended with a probability ramp:
 * below {@code minY} nothing places (as in Bedrock's formula), but instead of switching straight to
 * 100% at {@code minY}, the accept chance ramps linearly from 0 at {@code minY} to 100% at
 * {@code minY + rampBlocks} - moss starts sparse right at the cutoff and thickens with elevation,
 * rather than carpeting the whole "high ground" region uniformly the instant it crosses 75.
 */
public class MinYFilter implements PlacementModifier {
    public static final MapCodec<MinYFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("min_y").forGetter(MinYFilter::minY),
            Codec.INT.fieldOf("ramp_blocks").forGetter(MinYFilter::rampBlocks)
    ).apply(instance, MinYFilter::new));

    private final int minY;
    private final int rampBlocks;

    public MinYFilter(int minY) {
        this(minY, 0);
    }

    public MinYFilter(int minY, int rampBlocks) {
        this.minY = minY;
        this.rampBlocks = rampBlocks;
    }

    public int minY() {
        return minY;
    }

    public int rampBlocks() {
        return rampBlocks;
    }

    @Override
    public void modify(PlacementContext context, RandomSource random, BlockPos pos, Consumer<BlockPos> consumer) {
        int y = pos.getY();
        if (y < minY) {
            return;
        }
        if (rampBlocks <= 0) {
            consumer.accept(pos);
            return;
        }
        float chance = Math.min(1.0F, (y - minY) / (float) rampBlocks);
        if (random.nextFloat() < chance) {
            consumer.accept(pos);
        }
    }

    @Override
    public MapCodec<? extends PlacementModifier> codec() {
        return CODEC;
    }
}
