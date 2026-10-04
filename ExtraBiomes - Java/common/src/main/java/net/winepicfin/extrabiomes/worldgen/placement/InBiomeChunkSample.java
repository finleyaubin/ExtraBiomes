package net.winepicfin.extrabiomes.worldgen.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.function.Consumer;

// Moves a chunk-corner origin to a random column of that chunk whose biome carries the feature, so a following BiomeFilter doesn't skip chunks whose corner is in another biome; pair with ChunkOriginSnap.
public class InBiomeChunkSample implements PlacementModifier {
    private static final int SAMPLES = 16;

    public static final InBiomeChunkSample INSTANCE = new InBiomeChunkSample();
    public static final MapCodec<InBiomeChunkSample> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void modify(PlacementContext context, RandomSource random, BlockPos pos, Consumer<BlockPos> consumer) {
        PlacedFeature feature = context.topFeature().orElseThrow(() -> new IllegalStateException("InBiomeChunkSample needs a placed feature"));
        for (int i = 0; i < SAMPLES; i++) {
            BlockPos sample = pos.offset(random.nextInt(16), 0, random.nextInt(16));
            if (context.generator().getBiomeGenerationSettings(context.getLevel().getBiome(sample)).hasFeature(feature)) {
                consumer.accept(sample);
                return;
            }
        }
    }

    @Override
    public MapCodec<InBiomeChunkSample> codec() {
        return CODEC;
    }
}
