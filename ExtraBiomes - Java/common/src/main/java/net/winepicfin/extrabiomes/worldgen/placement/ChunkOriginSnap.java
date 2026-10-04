package net.winepicfin.extrabiomes.worldgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra.ModVolcanicPlacementModifiers;

import java.util.stream.Stream;

// Puts the position back on its chunk's corner for features that scan the whole chunk from their origin.
public class ChunkOriginSnap extends PlacementModifier {
    public static final ChunkOriginSnap INSTANCE = new ChunkOriginSnap();
    public static final Codec<ChunkOriginSnap> CODEC = Codec.unit(INSTANCE);

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        return Stream.of(new BlockPos(pos.getX() & ~15, pos.getY(), pos.getZ() & ~15));
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModVolcanicPlacementModifiers.CHUNK_ORIGIN_SNAP.get();
    }
}
