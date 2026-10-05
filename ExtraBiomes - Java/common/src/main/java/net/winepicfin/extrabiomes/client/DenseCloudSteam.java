package net.winepicfin.extrabiomes.client;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.winepicfin.extrabiomes.block.custom.DenseCloudBudding;
import net.winepicfin.extrabiomes.particle.ModParticles;

import java.util.ArrayList;
import java.util.List;

// Steam rises off the water of any blue ice stack that satisfies the dense cloud rules, whether or not any cloud has been seeded yet.
public final class DenseCloudSteam {
    private static final int SCAN_INTERVAL_TICKS = 20;
    private static final int SCAN_RADIUS = 16;
    private static final double RIBBON_PUFFS_PER_BLOCK = 1.2;
    private static final int STRANDS = 3;
    private static final int BILLOW_PUFFS = 3;

    private record Plume(BlockPos waterSurface, int capY) {
    }

    private static List<Plume> plumes = List.of();

    private DenseCloudSteam() {
    }

    public static void init() {
        ParticleProviderRegistry.register(ModParticles.STEAM, SteamParticle.Provider::new);
        ClientTickEvent.CLIENT_LEVEL_POST.register(DenseCloudSteam::tick);
    }

    private static void tick(ClientLevel level) {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.getY() + SCAN_RADIUS < DenseCloudBudding.MIN_ICE_Y) {
            plumes = List.of();
            return;
        }
        if (level.getGameTime() % SCAN_INTERVAL_TICKS == 0) {
            plumes = scan(level, player.blockPosition());
        }
        for (Plume plume : plumes) {
            emit(level, level.getRandom(), plume);
        }
    }

    // Puffs are placed along a few swaying strands that widen as they rise, so the steam reads as wavy ribbons up to the first block in the way, where more puffs drift outward and billow under it.
    private static void emit(ClientLevel level, RandomSource random, Plume plume) {
        double x = plume.waterSurface().getX() + 0.5;
        double z = plume.waterSurface().getZ() + 0.5;
        double bottom = plume.waterSurface().getY() + 1.0;
        double top = plume.capY();
        double height = Math.max(top - bottom, 0.0);
        double time = level.getGameTime();
        int ribbonPuffs = (int) Math.ceil(height * RIBBON_PUFFS_PER_BLOCK) + 1;
        for (int i = 0; i < ribbonPuffs; i++) {
            double above = random.nextDouble() * height;
            double rise = height > 0.0 ? above / height : 0.0;
            double phase = random.nextInt(STRANDS) * 2.1;
            double wave = above * 1.6 + time * 0.12 + phase;
            double amplitude = 0.08 + 0.3 * rise;
            level.addParticle(ModParticles.STEAM.get(),
                    x + Math.sin(wave) * amplitude,
                    bottom + above,
                    z + Math.cos(wave * 0.8 + phase) * amplitude * 0.8,
                    0.0, 0.03, 0.0);
        }
        for (int i = 0; i < BILLOW_PUFFS; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double speed = 0.02 + random.nextDouble() * 0.04;
            level.addParticle(ModParticles.STEAM.get(),
                    x + (random.nextDouble() - 0.5) * 0.3,
                    top - 0.5,
                    z + (random.nextDouble() - 0.5) * 0.3,
                    Math.cos(angle) * speed, 0.0, Math.sin(angle) * speed);
        }
    }

    private static List<Plume> scan(ClientLevel level, BlockPos center) {
        List<Plume> found = new ArrayList<>();
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
                                if (surface != null) found.add(new Plume(surface, firstBlockAbove(level, surface, pos.getY())));
                            }
                        }
                    }
                }
            }
        }
        return found;
    }

    private static int firstBlockAbove(ClientLevel level, BlockPos waterSurface, int iceY) {
        BlockPos.MutableBlockPos pos = waterSurface.mutable();
        for (int y = waterSurface.getY() + 1; y < iceY; y++) {
            pos.setY(y);
            if (!level.getBlockState(pos).isAir()) return y;
        }
        return iceY;
    }
}
