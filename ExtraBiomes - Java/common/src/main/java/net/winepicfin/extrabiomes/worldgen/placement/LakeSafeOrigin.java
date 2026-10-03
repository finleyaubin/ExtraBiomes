package net.winepicfin.extrabiomes.worldgen.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra.ModVolcanicPlacementModifiers;

// A lake spans 16 blocks from its origin and its water pools read biomes across that span; an origin in the last two columns of a chunk reads past the 3x3 chunks world generation can access and crashes.
public class LakeSafeOrigin extends PlacementFilter {
    private static final int MAX_LOCAL_COORDINATE = 13;

    public static final LakeSafeOrigin INSTANCE = new LakeSafeOrigin();
    public static final MapCodec<LakeSafeOrigin> CODEC = MapCodec.unit(INSTANCE);

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        return (pos.getX() & 15) <= MAX_LOCAL_COORDINATE && (pos.getZ() & 15) <= MAX_LOCAL_COORDINATE;
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModVolcanicPlacementModifiers.LAKE_SAFE_ORIGIN.get();
    }
}
