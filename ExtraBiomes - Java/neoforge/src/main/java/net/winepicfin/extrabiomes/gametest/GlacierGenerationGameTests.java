package net.winepicfin.extrabiomes.gametest;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.Biome;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;
import org.slf4j.Logger;

// Generates real Glacier chunks so runtime-only failures (far-chunk writes, loot table setup) surface, one chunk per step to stay under the tick watchdog.
public class GlacierGenerationGameTests {
    private static final Logger LOGGER = LogUtils.getLogger();
    // Lava, blue ice and chests are patchy per chunk, so the sample is wide enough that a working generator almost never misses them.
    private static final int CHUNK_RADIUS = 6;
    private static final int MAX_SCAN_Y = 120;
    private static final int BIOME_SAMPLE_Y = 80;
    // Snow-pillar placements start at most at y=48, and natural snow blocks only form near the surface, so snow blocks below this are pillars.
    private static final int SURFACE_CUTOFF_Y = 50;

    public static void glacierGeothermalFeaturesGenerate(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Pair<BlockPos, Holder<Biome>> glacier = level.findClosestBiome3d(holder -> holder.is(ModBiomes.GLACIER), new BlockPos(0, 80, 0), 15_000, 32, 128);
        helper.assertTrue(glacier != null, Component.literal("no glacier within 15000 blocks of spawn"));

        int centerChunkX = SectionPos.blockToSectionCoord(glacier.getFirst().getX());
        int centerChunkZ = SectionPos.blockToSectionCoord(glacier.getFirst().getZ());
        GameTestSequence sequence = helper.startSequence();
        for (int dx = -CHUNK_RADIUS; dx <= CHUNK_RADIUS; dx++) {
            for (int dz = -CHUNK_RADIUS; dz <= CHUNK_RADIUS; dz++) {
                int chunkX = centerChunkX + dx;
                int chunkZ = centerChunkZ + dz;
                sequence = sequence.thenExecute(() -> level.getChunk(chunkX, chunkZ)).thenIdle(1);
            }
        }
        sequence.thenExecute(() -> {
                    BlockPos found = glacier.getFirst();
                    int centreSolid = 0;
                    for (int y = level.getMinY(); y < MAX_SCAN_Y; y++) {
                        if (!level.getBlockState(new BlockPos(found.getX(), y, found.getZ())).isAir()) {
                            centreSolid++;
                        }
                    }
                    LOGGER.info("[GlacierGenerationGameTests] centre column nonAir={} chunkStatus={}", centreSolid,
                            level.getChunk(centerChunkX, centerChunkZ).getPersistedStatus());
                    int[] counts = scan(level, centerChunkX, centerChunkZ);
                    int unrimmedLava = counts[0];
                    LOGGER.info("[GlacierGenerationGameTests] glacier at {}: columns={} lava={} unrimmedLava={} basalt={} magma={} blueIce={} chests={} snowBlocks={} ice={} undergroundSnow={} hangingBasalt={} waterTops={} gravelTops={} andesiteTops={}",
                            glacier.getFirst(), counts[8], counts[1], unrimmedLava, counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[9], counts[10], counts[11], counts[12], counts[13]);
                    boolean scannedGlacier = counts[8] > 0 && counts[4] + counts[7] > 0;
                    if (unrimmedLava == 0 && scannedGlacier) {
                        LOGGER.info("[GlacierGenerationGameTests] glacierGeothermalFeaturesGenerate: passed");
                    } else {
                        LOGGER.error("[GlacierGenerationGameTests] glacierGeothermalFeaturesGenerate: failed");
                    }
                    helper.assertTrue(scannedGlacier, Component.literal("scanned " + counts[8] + " glacier columns with no ice in them"));
                    helper.assertTrue(unrimmedLava == 0, Component.literal(unrimmedLava + " lava blocks touch non-basalt rock in glacier chunks"));
                })
                .thenSucceed();
    }

    // Returns {unrimmedLava, lava, basalt, magma, blueIce, chests, snowBlocks, ice, glacierColumns, undergroundSnow, hangingBasalt, waterTops, gravelTops, andesiteTops}.
    private static int[] scan(ServerLevel level, int centerChunkX, int centerChunkZ) {
        int[] counts = new int[14];
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        // Only the inner chunks: the outer ring's own neighbours were never generated, so lava spilling in from beyond it can't have been rimmed.
        int scanRadius = CHUNK_RADIUS - 1;
        int minX = SectionPos.sectionToBlockCoord(centerChunkX - scanRadius);
        int minZ = SectionPos.sectionToBlockCoord(centerChunkZ - scanRadius);
        int size = (scanRadius * 2 + 1) * 16;
        for (int x = minX; x < minX + size; x++) {
            for (int z = minZ; z < minZ + size; z++) {
                if (!level.getBiome(pos.set(x, BIOME_SAMPLE_Y, z)).is(ModBiomes.GLACIER)) {
                    continue;
                }
                counts[8]++;
                tallyTopBlock(level, pos, x, z, counts);
                for (int y = level.getMinY(); y < MAX_SCAN_Y; y++) {
                    tally(level, pos.set(x, y, z), counts);
                }
            }
        }
        return counts;
    }

    private static void tallyTopBlock(ServerLevel level, BlockPos.MutableBlockPos pos, int x, int z, int[] counts) {
        int top = MAX_SCAN_Y - 1;
        while (top > level.getMinY() && level.getBlockState(pos.set(x, top, z)).isAir()) {
            top--;
        }
        BlockState topState = level.getBlockState(pos.set(x, top, z));
        // Surface freezing lays a snow layer over everything, so look through it.
        if (topState.is(Blocks.SNOW)) {
            topState = level.getBlockState(pos.set(x, top - 1, z));
        }
        if (topState.is(Blocks.WATER)) {
            counts[11]++;
        } else if (topState.is(Blocks.GRAVEL)) {
            counts[12]++;
        } else if (topState.is(Blocks.ANDESITE)) {
            counts[13]++;
        }
    }

    private static void tally(ServerLevel level, BlockPos.MutableBlockPos pos, int[] counts) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.SNOW_BLOCK) && pos.getY() < SURFACE_CUTOFF_Y) {
            counts[9]++;
        }
        if (state.is(Blocks.BASALT) && level.getBlockState(pos.below()).isAir()) {
            counts[10]++;
        }
        if (state.is(Blocks.LAVA)) {
            counts[1]++;
            if (level.getBiome(pos).is(ModBiomes.GLACIER) && touchesNonBasaltRock(level, pos)) {
                counts[0]++;
            }
        } else if (state.is(Blocks.BASALT)) {
            counts[2]++;
        } else if (state.is(Blocks.MAGMA_BLOCK)) {
            counts[3]++;
        } else if (state.is(Blocks.BLUE_ICE)) {
            counts[4]++;
        } else if (state.is(Blocks.CHEST)) {
            counts[5]++;
        } else if (state.is(Blocks.SNOW_BLOCK)) {
            counts[6]++;
        } else if (state.is(Blocks.ICE)) {
            counts[7]++;
        }
    }

    private static boolean touchesNonBasaltRock(ServerLevel level, BlockPos lava) {
        for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.values()) {
            BlockState neighbor = level.getBlockState(lava.relative(direction));
            boolean rock = !neighbor.isAir() && neighbor.getFluidState().isEmpty() && !neighbor.hasBlockEntity();
            if (rock && !neighbor.is(Blocks.BASALT) && !neighbor.is(Blocks.BEDROCK)) {
                return true;
            }
        }
        return false;
    }
}
