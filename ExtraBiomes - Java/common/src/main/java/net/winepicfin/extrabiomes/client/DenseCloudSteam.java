package net.winepicfin.extrabiomes.client;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.winepicfin.extrabiomes.block.custom.DenseCloudBudding;

import java.util.ArrayList;
import java.util.List;

// Steam rises off the water of any blue ice stack that satisfies the dense cloud rules, whether or not any cloud has been seeded yet.
public final class DenseCloudSteam {
    private static final int SCAN_INTERVAL_TICKS = 20;
    private static final int SCAN_RADIUS = 16;

    private static List<BlockPos> waterSurfaces = List.of();

    private DenseCloudSteam() {
    }

    public static void init() {
        ClientTickEvent.CLIENT_LEVEL_POST.register(DenseCloudSteam::tick);
    }

    private static void tick(ClientLevel level) {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.getY() + SCAN_RADIUS < DenseCloudBudding.MIN_ICE_Y) {
            waterSurfaces = List.of();
            return;
        }
        if (level.getGameTime() % SCAN_INTERVAL_TICKS == 0) {
            waterSurfaces = scan(level, player.blockPosition());
        }
        for (BlockPos surface : waterSurfaces) {
            if (level.getRandom().nextInt(2) == 0) {
                level.addParticle(ParticleTypes.CLOUD,
                        surface.getX() + 0.2 + level.getRandom().nextDouble() * 0.6,
                        surface.getY() + 1.0,
                        surface.getZ() + 0.2 + level.getRandom().nextDouble() * 0.6,
                        0.0, 0.05, 0.0);
            }
        }
    }

    private static List<BlockPos> scan(ClientLevel level, BlockPos center) {
        List<BlockPos> found = new ArrayList<>();
        int minX = center.getX() - SCAN_RADIUS;
        int maxX = center.getX() + SCAN_RADIUS;
        int minZ = center.getZ() - SCAN_RADIUS;
        int maxZ = center.getZ() + SCAN_RADIUS;
        int minY = Math.max(DenseCloudBudding.MIN_ICE_Y, center.getY() - SCAN_RADIUS);
        int maxY = center.getY() + SCAN_RADIUS;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) continue;
                for (int sectionY = minY >> 4; sectionY <= maxY >> 4; sectionY++) {
                    int index = level.getSectionIndexFromSectionY(sectionY);
                    if (index < 0 || index >= chunk.getSectionsCount()) continue;
                    LevelChunkSection section = chunk.getSection(index);
                    if (!section.maybeHas(state -> state.is(Blocks.BLUE_ICE))) continue;
                    for (int x = Math.max(minX, chunkX << 4); x <= Math.min(maxX, (chunkX << 4) + 15); x++) {
                        for (int z = Math.max(minZ, chunkZ << 4); z <= Math.min(maxZ, (chunkZ << 4) + 15); z++) {
                            for (int y = Math.max(minY, sectionY << 4); y <= Math.min(maxY, (sectionY << 4) + 15); y++) {
                                pos.set(x, y, z);
                                if (!level.getBlockState(pos).is(Blocks.BLUE_ICE)) continue;
                                BlockPos surface = DenseCloudBudding.heatedWaterSurface(level, pos);
                                if (surface != null) found.add(surface);
                            }
                        }
                    }
                }
            }
        }
        return found;
    }
}
