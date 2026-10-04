package net.winepicfin.extrabiomes.worldgen.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.function.Consumer;

// Puts the position back on its chunk's corner for features that scan the whole chunk from their origin.
public class ChunkOriginSnap implements PlacementModifier {
    public static final ChunkOriginSnap INSTANCE = new ChunkOriginSnap();
    public static final MapCodec<ChunkOriginSnap> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void modify(PlacementContext context, RandomSource random, BlockPos pos, Consumer<BlockPos> consumer) {
        consumer.accept(new BlockPos(pos.getX() & ~15, pos.getY(), pos.getZ() & ~15));
    }

    @Override
    public MapCodec<ChunkOriginSnap> codec() {
        return CODEC;
    }
}
