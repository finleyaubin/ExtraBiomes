package net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluids;

/**
 * Vanilla's {@code VegetationPatchFeature} (used by lava_river_core/bank) sets its ground blocks
 * with an update flag that skips scheduling a fluid tick, so the lava/magma it places during
 * worldgen never starts flowing on its own - it just sits as static source blocks forever unless
 * something else happens to trigger a neighbor update nearby. This runs once per chunk right after
 * the river placements (registered with no placement modifiers, so it fires exactly once at the
 * chunk origin) and explicitly schedules a fluid tick on every lava block it finds near the surface,
 * kicking off normal fluid physics from there - the same as if a player had just placed it by hand.
 */
public class LavaFlowKickstartFeature implements Feature {
    // Generous on purpose: WORLD_SURFACE_WG's reported top can shift a little once later
    // LOCAL_MODIFICATIONS features (rock formations, volcanoes) dig into the same columns, and
    // missing a pocket here means that whole pocket never gets a scheduled tick at all - better to
    // over-scan than silently skip lava that's a few blocks deeper than expected.
    private static final int SEARCH_DEPTH = 12;

    public static final MapCodec<LavaFlowKickstartFeature> CODEC = MapCodec.unit(LavaFlowKickstartFeature::new);

    @Override
    public MapCodec<LavaFlowKickstartFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        boolean scheduledAny = false;

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int worldX = origin.getX() + dx;
                int worldZ = origin.getZ() + dz;
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ);
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(worldX, surfaceY, worldZ);
                for (int i = 0; i < SEARCH_DEPTH; i++) {
                    BlockState state = level.getBlockState(pos);
                    if (state.is(Blocks.LAVA)) {
                        level.scheduleTick(pos.immutable(), Fluids.LAVA, Fluids.LAVA.getTickDelay(level));
                        scheduledAny = true;
                    }
                    pos.move(0, -1, 0);
                }
            }
        }
        return scheduledAny;
    }
}
