package net.winepicfin.extrabiomes.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.winepicfin.extrabiomes.entity.custom.HarpyEntity;

// Vanilla's NaturalSpawner picks each chunk's spawn-attempt Y uniformly between the world's min
// build height and that column's own terrain surface height (NaturalSpawner.getRandomPosWithin) -
// it can never roll above the real surface. HarpyEntity.checkHarpySpawnRules requires y >=
// HarpyEntity.MIN_SPAWN_Y (192), which almost no overworld column's surface ever reaches, so the
// standard per-chunk spawn loop can essentially never produce one. This mirrors vanilla's own fix
// for the identical problem with Phantoms: PhantomSpawner runs as a separate per-tick routine
// driven by online players instead of NaturalSpawner's category loop, sampling a position near
// each player directly instead of a uniform sub-surface roll. The roll frequency approximates
// Phantom's own cadence (a fresh attempt roughly every 60-180 ticks per level, driven by a
// per-level countdown field) via a stateless per-player-per-tick probability of comparable
// magnitude instead, since nothing else in this codebase keeps that kind of per-level tick state.
// TICK_CHANCE_DENOMINATOR was raised from an initial 1800 (~90s average) after playtesting found
// harpies too rare even once they could spawn at all.
public final class HarpySpawner {
    private static final int TICK_CHANCE_DENOMINATOR = 600;
    private static final int SEARCH_RADIUS = 20;
    private static final double MIN_DISTANCE_BETWEEN_HARPIES = 64.0;

    public static void tick(ServerLevel level) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) || level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }

        RandomSource random = level.getRandom();
        for (ServerPlayer player : level.players()) {
            if (!player.isSpectator() && random.nextInt(TICK_CHANCE_DENOMINATOR) == 0) {
                attemptSpawnNear(level, player, random);
            }
        }
    }

    private static void attemptSpawnNear(ServerLevel level, ServerPlayer player, RandomSource random) {
        BlockPos playerPos = player.blockPosition();
        int x = playerPos.getX() + random.nextInt(SEARCH_RADIUS * 2 + 1) - SEARCH_RADIUS;
        int z = playerPos.getZ() + random.nextInt(SEARCH_RADIUS * 2 + 1) - SEARCH_RADIUS;
        if (!level.hasChunkAt(new BlockPos(x, 0, z))) {
            return;
        }

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (surfaceY < HarpyEntity.MIN_SPAWN_Y) {
            return;
        }

        BlockPos pos = new BlockPos(x, surfaceY, z);
        if (!HarpyEntity.checkHarpySpawnRules(ModEntities.HARPY.get(), level, MobSpawnType.NATURAL, pos, random)) {
            return;
        }
        AABB aabb = ModEntities.HARPY.get().getAABB(x + 0.5, surfaceY, z + 0.5);
        if (!level.noCollision(aabb) || !level.getEntitiesOfClass(HarpyEntity.class, aabb.inflate(MIN_DISTANCE_BETWEEN_HARPIES)).isEmpty()) {
            return;
        }

        HarpyEntity harpy = ModEntities.HARPY.get().create(level);
        if (harpy == null) {
            return;
        }
        harpy.moveTo(x + 0.5, surfaceY, z + 0.5, random.nextFloat() * 360.0F, 0.0F);
        harpy.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
        level.addFreshEntity(harpy);
    }

    private HarpySpawner() {
    }
}
