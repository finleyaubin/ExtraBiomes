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
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.util.ModTags;
import net.winepicfin.extrabiomes.worldgen.MobSpawnWeightTuning;
import net.winepicfin.extrabiomes.worldgen.ModPlacedFeatures;
import net.winepicfin.extrabiomes.worldgen.features.boulder.BoulderFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mushroom.MushroomFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;

import java.util.List;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_LUSH_GRASS = registerKey("add_lush_grass");
    public static final ResourceKey<BiomeModifier> ADD_UNDERGROUND_JUNGLE_VEGETATION = registerKey("add_underground_jungle_vegetation");
    public static final ResourceKey<BiomeModifier> ADD_UNDERGROUND_JUNGLE_CAVE_VINES = registerKey("add_underground_jungle_cave_vines");
    public static final ResourceKey<BiomeModifier> ADD_BOULDER = registerKey("add_boulder");
    public static final ResourceKey<BiomeModifier> ADD_STICK_PILE = registerKey("add_stick_pile");
    public static final ResourceKey<BiomeModifier> REMOVE_STICK_PILE_DARK_FOREST = registerKey("remove_stick_pile_dark_forest");
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
    public static final ResourceKey<BiomeModifier> ADD_SPAWN_PUCKOO_BEACH = registerKey("add_spawn_puckoo_beach");

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

        // Registered before the boulder/stick_pile block below - see the long comment there for
        // why the ORDER of these two blocks (not just their content) matters: JungleMarsh.java
        // bakes extrabiomes:swamp_huge_mushroom directly into its own VEGETAL_DECORATION list at
        // biome-registration time (i.e. always before any BiomeModifier runs at all), so on
        // vanilla Dark Forest - which gets both swamp_huge_mushroom and select_stick_pile purely
        // via modifiers - the mushroom modifier must also be registered (and therefore applied)
        // before the stick_pile-for-forest one, or the two biomes end up wanting opposite relative
        // orders for the same pair of features and vanilla's FeatureSorter crashes with
        // "Feature order cycle found" the moment a chunk needs both biomes' feature lists at once.
        // Tag-based (Tags.Biomes.IS_MUSHROOM, same convention tag ModBiomeTagProvider already folds
        // Biomes.MUSHROOM_FIELDS into) rather than a hardcoded biome list, matching
        // ADD_BOULDER/ADD_UNDERGROUND_JUNGLE_VEGETATION below - any biome (vanilla, this
        // mod's, or a third-party mod's) carrying the tag gets these, not just vanilla's own biome.
        context.register(ADD_MUSHROOM_FIELDS_HUGE_MUSHROOMS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_MUSHROOM),
                HolderSet.direct(placedFeatures.getOrThrow(MushroomFeatures.MUSHROOM_ISLAND_HUGE_MUSHROOM_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        // Small mod-added mushroom variants (via the mycelium-floor-patch mechanism -
        // MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY's vegetationFeature is SELECT_MUSHROOM_KEY) were
        // already wired into FungleJungle directly, but mushroom-tagged vanilla/modded biomes only
        // ever got the huge mushroom modifier above - they had no path to this mod's own small
        // mushroom colours at all. Same generation step FungleJungle uses this feature at (LOCAL_MODIFICATIONS).
        context.register(ADD_MUSHROOM_FIELDS_SMALL_MUSHROOMS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_MUSHROOM),
                HolderSet.direct(placedFeatures.getOrThrow(MushroomFeatures.MUSHROOM_SURFACE_MYCELIUM_FLOOR_PLACED_KEY)),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS));
        context.register(ADD_DARK_FOREST_HUGE_MUSHROOMS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(MushroomFeatures.SWAMP_HUGE_MUSHROOM_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        // Underground badlands terracotta banding (including the near-lava glazed band) is handled
        // by ModSurfaceRules' bandlands()/glazedTerracottaBand() surface rules, not a biome modifier
        // - a Feature-based approach here was both redundant with vanilla's own real terracotta
        // banding mechanism and, being a per-chunk full-volume block scan, too slow ("Can't keep up"
        // warnings during world generation). See ModSurfaceRules.makeRules() javadoc.

        // Bedrock's boulder_placer/stick_pile_placer feature_rules (packs/BP/feature_rules/boulder/)
        // gate on has_biome_tag alone (boulder: plains/forest/jungle, stick_pile: forest/jungle).
        // GETS_BOULDERS/GETS_STICK_PILES are this mod's own curated tags (see ModTags.Biomes'
        // javadoc for why - broad vanilla-tag membership repeatedly produced cross-mod "Feature
        // order cycle found" crashes once a third-party mod's biome shared one of these features),
        // so only vanilla + this mod's own biomes are covered, not every third-party mod's tagged
        // biome.
        context.register(ADD_BOULDER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.GETS_BOULDERS),
                HolderSet.direct(placedFeatures.getOrThrow(BoulderFeatures.SELECT_BOULDER_PLACED_KEY)),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS));
        context.register(ADD_STICK_PILE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.GETS_STICK_PILES),
                HolderSet.direct(placedFeatures.getOrThrow(BoulderFeatures.SELECT_STICK_PILE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        // Dark Forest specifically triggers vanilla's FeatureSorter "Feature order cycle found"
        // crash once it shares select_stick_pile with extrabiomes:jungle_marsh - bisected
        // empirically (see FabricBiomeModifiers' matching comment for the full story; reordering
        // this mod's own modifier registrations did not resolve it, so rather than continue
        // hunting a multi-hop contradiction through vanilla's own biome/feature graph, Dark Forest
        // is excluded here). Forge applies BiomeModifiers by Phase (ADD, then REMOVE), not
        // registration order, so this REMOVE always runs after every ADD above regardless of where
        // it's registered - every other GETS_STICK_PILES biome still gets stick piles from
        // ADD_STICK_PILE.
        context.register(REMOVE_STICK_PILE_DARK_FOREST, ForgeBiomeModifiers.RemoveFeaturesBiomeModifier.allSteps(
                HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(BoulderFeatures.SELECT_STICK_PILE_PLACED_KEY))));

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
                biomes.getOrThrow(Tags.Biomes.IS_SWAMP),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.PIRANHA.get(), MobSpawnWeightTuning.PIRANHA_SWAMP, 2, 5))));
        context.register(ADD_SPAWN_TREEFROG_JUNGLE, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.TREEFROG.get(), MobSpawnWeightTuning.TREEFROG_JUNGLE, 2, 3))));
        context.register(ADD_SPAWN_TREEFROG_SWAMP, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.IS_WETLAND),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.TREEFROG.get(), MobSpawnWeightTuning.TREEFROG_SWAMP, 2, 3))));
        // crimson / warped / mushroom + this mod's nether biomes
        context.register(ADD_SPAWN_HOPPLESHROOM, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.SPAWNS_HOPPLESHROOM),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.HOPPLESHROOM.get(), MobSpawnWeightTuning.HOPPLESHROOM, 1, 5))));
        // jellyfish: dense in JellyfishFields, rare on beaches
        context.register(ADD_SPAWN_JELLYFISH, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.SPAWNS_JELLYFISH),
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
        // puckoo: any beach-tagged biome, vanilla or modded
        context.register(ADD_SPAWN_PUCKOO_BEACH, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BEACH),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.PUCKOO.get(), MobSpawnWeightTuning.PUCKOO_BEACH,
                        MobSpawnWeightTuning.PUCKOO_BEACH_MIN_GROUP, MobSpawnWeightTuning.PUCKOO_BEACH_MAX_GROUP))));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }
}
