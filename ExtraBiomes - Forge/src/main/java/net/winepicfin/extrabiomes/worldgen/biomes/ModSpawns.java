package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.winepicfin.extrabiomes.entity.ModEntities;

// Central place for the mob spawns of the entities ported from Bedrock, grouped by the
// Bedrock spawn-rule biome tags (jungle / jellyfish / swamp+moorlands / nether / etc.).
public class ModSpawns {

    // jungle tag: giant_tortoise, piranha (water), treefrog
    public static void jungle(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.GIANT_TORTOISE.get(), 15, 1, 2));
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.TREEFROG.get(), 25, 2, 4));
        builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.PIRANHA.get(), 35, 4, 8));
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.WORM.get(), 20, 1, 3));
    }

    // jellyfish tag
    public static void jellyfish(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.JELLYFISH.get(), 25, 3, 8));
    }

    // swamp / moorlands tags: treefrog + worms
    public static void swampAndMoorland(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.TREEFROG.get(), 25, 2, 3));
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.WORM.get(), 25, 1, 3));
    }

    // crimson / warped / mooshroom tags
    public static void nether(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.HOPPLESHROOM.get(), 35, 1, 5));
    }

    // harpy: a surface monster with no biome filter in Bedrock — sprinkled into open/exposed biomes.
    public static void harpy(MobSpawnSettings.Builder builder) {
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.HARPY.get(), 20, 1, 1));
    }
}
