package net.winepicfin.extrabiomes.forge.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.util.ModTags;
import net.winepicfin.extrabiomes.worldgen.MobSpawnWeightTuning;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;

import java.util.List;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_LUSH_GRASS = registerKey("add_lush_grass");
    public static final ResourceKey<BiomeModifier> ADD_UNDERGROUND_JUNGLE_VEGETATION = registerKey("add_underground_jungle_vegetation");
    public static final ResourceKey<BiomeModifier> ADD_UNDERGROUND_JUNGLE_CAVE_VINES = registerKey("add_underground_jungle_cave_vines");
    public static final ResourceKey<BiomeModifier> ADD_MUSHROOM_FIELDS_HUGE_MUSHROOMS = registerKey("add_mushroom_fields_huge_mushrooms");
    public static final ResourceKey<BiomeModifier> ADD_MUSHROOM_FIELDS_SMALL_MUSHROOMS = registerKey("add_mushroom_fields_small_mushrooms");
    public static final ResourceKey<BiomeModifier> ADD_DARK_FOREST_HUGE_MUSHROOMS = registerKey("add_dark_forest_huge_mushrooms");

    // Mob spawns ported from the Bedrock spawn_rules biome tags, applied to the matching vanilla
    // (and mod) biomes. Uses the vanilla biome tags, which this mod's datagen already folds its own
    // jungle/beach/etc. biomes into, so these cover both vanilla and ExtraBiomes biomes at once.
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_GIANT_TORTOISE = registerKey("add_spawn_giant_tortoise");
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_PIRANHA_JUNGLE = registerKey("add_spawn_piranha_jungle");
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_PIRANHA_SWAMP = registerKey("add_spawn_piranha_swamp");
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_TREEFROG_JUNGLE = registerKey("add_spawn_treefrog_jungle");
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_TREEFROG_SWAMP = registerKey("add_spawn_treefrog_swamp");
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_HOPPLESHROOM = registerKey("add_spawn_hoppleshroom");
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_JELLYFISH = registerKey("add_spawn_jellyfish");
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_JELLYFISH_BEACH = registerKey("add_spawn_jellyfish_beach");
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_HARPY = registerKey("add_spawn_harpy");
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_WORM = registerKey("add_spawn_worm");

    private static final TagKey<Biome> IS_OVERWORLD = TagKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:is_overworld"));

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_LUSH_GRASS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(ModTags.Biomes.LUSH_MESA),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LUSH_GRASS_PLACED_KEY)),
                GenerationStep.Decoration.TOP_LAYER_MODIFICATION));

        // Same underground_jungle recipe used for FloatingJungle/FungleJungle/etc (see
        // UndergroundJungleFeatures' class javadoc "Biome wiring" section), applied to every vanilla
        // jungle biome (jungle, sparse_jungle, bamboo_jungle) instead of one of this mod's own biomes.
        context.register(ADD_UNDERGROUND_JUNGLE_VEGETATION, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                HolderSet.direct(
                        placedFeatures.getOrThrow(UndergroundJungleFeatures.GRASS_FLOOR_PLACED_KEY),
                        placedFeatures.getOrThrow(UndergroundJungleFeatures.GRASS_FLOOR_UPPER_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_UNDERGROUND_JUNGLE_CAVE_VINES, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                HolderSet.direct(placedFeatures.getOrThrow(UndergroundJungleFeatures.CAVE_VINE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_DECORATION));

        // Custom huge mushroom variants added to vanilla's own mushroom-themed biomes, mirroring how
        // FungleJungle/DeepDarkForest already use these same placed features for this mod's biomes.
        context.register(ADD_MUSHROOM_FIELDS_HUGE_MUSHROOMS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.MUSHROOM_FIELDS)),
                HolderSet.direct(placedFeatures.getOrThrow(MushroomFeatures.MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        // Small mod-added mushroom variants (via the mycelium-floor-patch mechanism -
        // MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY's vegetationFeature is SELECT_MUSHROOM_KEY) were
        // already wired into FungleJungle directly, but vanilla mushroom fields only ever got the huge
        // mushroom modifier above - it had no path to this mod's own small mushroom colours at all.
        // Same generation step FungleJungle uses this feature at (LOCAL_MODIFICATIONS).
        context.register(ADD_MUSHROOM_FIELDS_SMALL_MUSHROOMS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.MUSHROOM_FIELDS)),
                HolderSet.direct(placedFeatures.getOrThrow(MushroomFeatures.MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY)),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS));
        context.register(ADD_DARK_FOREST_HUGE_MUSHROOMS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        // Mob spawns (Bedrock spawn_rules -> vanilla + mod biomes)
        // jungle tag: giant_tortoise, piranha, treefrog
        context.register(ADD_SPAWN_GIANT_TORTOISE, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.GIANT_TORTOISE.get(), MobSpawnWeightTuning.GIANT_TORTOISE, 1, 2))));
        context.register(ADD_SPAWN_PIRANHA_JUNGLE, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.PIRANHA.get(), MobSpawnWeightTuning.PIRANHA_JUNGLE,
                        MobSpawnWeightTuning.PIRANHA_JUNGLE_MIN_GROUP, MobSpawnWeightTuning.PIRANHA_JUNGLE_MAX_GROUP))));
        context.register(ADD_SPAWN_PIRANHA_SWAMP, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.MANGROVE_SWAMP), biomes.getOrThrow(ModBiomes.SHATTERED_SWAMP)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.PIRANHA.get(), MobSpawnWeightTuning.PIRANHA_SWAMP, 2, 5))));
        context.register(ADD_SPAWN_TREEFROG_JUNGLE, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.TREEFROG.get(), MobSpawnWeightTuning.TREEFROG_JUNGLE, 2, 3))));
        context.register(ADD_SPAWN_TREEFROG_SWAMP, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP), biomes.getOrThrow(Biomes.MANGROVE_SWAMP),
                        biomes.getOrThrow(ModBiomes.SHATTERED_SWAMP), biomes.getOrThrow(ModBiomes.MOORLANDS)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.TREEFROG.get(), MobSpawnWeightTuning.TREEFROG_SWAMP, 2, 3))));
        // crimson / warped / mushroom + this mod's nether biomes
        context.register(ADD_SPAWN_HOPPLESHROOM, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.MUSHROOM_FIELDS), biomes.getOrThrow(Biomes.CRIMSON_FOREST),
                        biomes.getOrThrow(Biomes.WARPED_FOREST)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.HOPPLESHROOM.get(), MobSpawnWeightTuning.HOPPLESHROOM, 1, 5))));
        // jellyfish: dense in JellyfishFields, rare on beaches
        context.register(ADD_SPAWN_JELLYFISH, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(ModBiomes.JELLYFISH_FIELDS)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.JELLYFISH.get(), MobSpawnWeightTuning.JELLYFISH, 3, 8))));
        context.register(ADD_SPAWN_JELLYFISH_BEACH, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BEACH),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.JELLYFISH.get(), MobSpawnWeightTuning.JELLYFISH_BEACH, 1, 1))));
        // harpy (no Bedrock biome filter) and worm ("animal" tag): overworld-wide
        context.register(ADD_SPAWN_HARPY, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.HARPY.get(), MobSpawnWeightTuning.HARPY, 1, 1))));
        context.register(ADD_SPAWN_WORM, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.WORM.get(), MobSpawnWeightTuning.WORM, 1, 3))));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }
}
