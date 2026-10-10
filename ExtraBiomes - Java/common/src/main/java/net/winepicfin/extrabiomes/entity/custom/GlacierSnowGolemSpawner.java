package net.winepicfin.extrabiomes.entity.custom;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

// Vanilla swaps a MISC mob like the snow golem for a pig in any biome spawn list, so Glacier's underground golems come from this small spawner instead.
public final class GlacierSnowGolemSpawner {
    private static final int INTERVAL_TICKS = 200;
    private static final int ATTEMPTS = 12;
    private static final int MIN_DISTANCE = 24;
    private static final int MAX_DISTANCE = 56;
    private static final int MIN_DEPTH_BELOW_SURFACE = 8;
    private static final int FLOOR_MARGIN = 4;
    private static final int NEARBY_RADIUS = 48;
    private static final int MAX_NEARBY = 3;

    public static void register() {
        TickEvent.SERVER_LEVEL_POST.register(GlacierSnowGolemSpawner::tick);
    }

    private static void tick(ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD || level.getGameTime() % INTERVAL_TICKS != 0) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            if (!player.isSpectator()) {
                trySpawnNear(level, player);
            }
        }
    }

    private static void trySpawnNear(ServerLevel level, ServerPlayer player) {
        if (nearbyGolems(level, player.blockPosition()) >= MAX_NEARBY) {
            return;
        }
        RandomSource random = level.getRandom();
        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            int distance = MIN_DISTANCE + random.nextInt(MAX_DISTANCE - MIN_DISTANCE + 1);
            int x = player.getBlockX() + (int) (Math.cos(angle) * distance);
            int z = player.getBlockZ() + (int) (Math.sin(angle) * distance);
            int ceiling = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - MIN_DEPTH_BELOW_SURFACE;
            int floor = level.getMinY() + FLOOR_MARGIN;
            if (ceiling <= floor) {
                continue;
            }

            BlockPos pos = new BlockPos(x, floor + random.nextInt(ceiling - floor), z);
            if (isValidSpot(level, pos) && nearbyGolems(level, pos) < MAX_NEARBY) {
                spawn(level, pos, random);
                return;
            }
        }
    }

    private static boolean isValidSpot(ServerLevel level, BlockPos pos) {
        if (!level.isPositionEntityTicking(pos) || !level.getBiome(pos).is(ModBiomes.GLACIER) || level.canSeeSky(pos)) {
            return false;
        }
        boolean roomToStand = level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir();
        boolean solidFloor = level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
        return roomToStand && solidFloor && level.players().stream().noneMatch(player -> player.distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) < MIN_DISTANCE * MIN_DISTANCE);
    }

    private static int nearbyGolems(ServerLevel level, BlockPos pos) {
        return level.getEntitiesOfClass(SnowGolem.class, new AABB(pos).inflate(NEARBY_RADIUS)).size();
    }

    private static void spawn(ServerLevel level, BlockPos pos, RandomSource random) {
        SnowGolem golem = EntityTypes.SNOW_GOLEM.create(level, EntitySpawnReason.NATURAL);
        if (golem == null) {
            return;
        }
        golem.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        golem.setYRot(random.nextFloat() * 360.0F);
        golem.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.NATURAL, null);
        level.addFreshEntity(golem);
    }

    private GlacierSnowGolemSpawner() {
    }
}
