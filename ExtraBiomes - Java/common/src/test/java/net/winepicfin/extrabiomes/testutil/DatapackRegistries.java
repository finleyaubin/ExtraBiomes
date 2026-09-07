package net.winepicfin.extrabiomes.testutil;

import com.mojang.serialization.MapCodec;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.advancements.BaitLureTrigger;
import net.winepicfin.extrabiomes.block.custom.MossyPebbleBlock;
import net.winepicfin.extrabiomes.block.custom.PebbleBlock;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.BrycePillarsConfiguration;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.BrycePillarsFeature;
import net.winepicfin.extrabiomes.worldgen.features.moorland.DoubleTallGrassFeature;
import net.winepicfin.extrabiomes.worldgen.features.moorland.PodzolConversionFeature;
import net.winepicfin.extrabiomes.worldgen.features.moorland.WaterLilyFixupFeature;
import net.winepicfin.extrabiomes.worldgen.features.mystic.GooConversionFeature;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsWheatFieldFeature;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureConfiguration;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.SingleStructureFeature;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.CaveVineFeature;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.FallenJungleTreeFeature;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.MultiFeature;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.MultiFeatureConfiguration;
import net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra.BasaltBankFeature;
import net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra.LavaFlowKickstartFeature;
import net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra.MinYFilter;
import net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra.RiverNoiseFilter;
import net.winepicfin.extrabiomes.worldgen.structure.windmill.WindmillStructure;
import net.winepicfin.extrabiomes.worldgen.tree.custom.CaveVineTreeDecorator;
import net.winepicfin.extrabiomes.worldgen.tree.custom.MysticTrunkPlacer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

// Loads this mod's dynamic registries (biome, configured/placed feature, structure*, noise, ...)
// the same way a real server does when it loads a datapack from disk: RegistryDataLoader reads
// the raw JSON straight out of a ResourceManager, resolving Holder/tag cross-references between
// files as it goes.
//
// This mod's own custom registry entries (Block/Item/Feature/PlacementModifierType/StructureType/
// TreeDecoratorType/TrunkPlacerType ids that configured_feature/placed_feature/biome JSON
// reference) are normally added via Architectury's DeferredRegister from each platform module's
// mod entrypoint (fabric/ExtraBiomesFabric, neoforge/ExtraBiomesForge) - not available here, since
// DeferredRegister needs a real platform's RegistrarManager (throws the same unimplemented-
// @ExpectPlatform-style AssertionError on common's plain JUnit classpath, and merely loading a
// class like ModBlocks/ModItems trips it too, since their DeferredRegister.register("id", ...)
// calls run as static field initializers). Codecs for these types resolve "type": "extrabiomes:..."
// fields via BuiltInRegistries' own static singletons directly (not through whatever RegistryAccess
// is passed to RegistryDataLoader.load), so the only way to make them resolve is registering into
// those same live singletons - see registerModEntries().
//
// Block/Item ids are discovered dynamically (scanModIdReferences(), matching every
// "extrabiomes:..." string the datapack JSON itself references) rather than hand-listed, so new
// blocks/items need no update here - only their id needs to exist, not a specific Block/Item
// subclass or behavior. The custom Feature/PlacementModifierType/StructureType/TreeDecoratorType/
// TrunkPlacerType entries can't be discovered the same way: their JSON "config" bodies need the
// real Java class's real Codec to validate (a placeholder object's codec would happily accept any
// shape, defeating the point), so that short, rarely-changing list stays explicit below - adding a
// new one of *those* is a deliberate code change anyway.
public final class DatapackRegistries {
    private static final List<Path> DATA_ROOTS = List.of(
            Paths.get("src/main/resources"),
            Paths.get("src/generated/resources"));

    // Matches any "extrabiomes:some/id" appearing as a full quoted JSON string value, e.g.
    // "extrabiomes:sky_log" or "extrabiomes:the_netherlands/windmill" - not a tag reference
    // (those are written "#extrabiomes:...", excluded by the negative lookbehind on '#').
    private static final Pattern MOD_ID_REFERENCE = Pattern.compile("(?<!#)\"extrabiomes:([a-z0-9_./\\-]+)\"");

    private static volatile HolderLookup.Provider instance;

    public static HolderLookup.Provider get() {
        HolderLookup.Provider result = instance;
        if (result == null) {
            synchronized (DatapackRegistries.class) {
                result = instance;
                if (result == null) {
                    SharedConstants.tryDetectVersion();
                    Bootstrap.bootStrap();
                    registerModEntries();
                    result = load();
                    instance = result;
                }
            }
        }
        return result;
    }

    private static void registerModEntries() {
        // Every "extrabiomes:..." id this mod's own datapack JSON references anywhere - not just
        // the ones meant as Block/Item ids (feature/structure/tag ids match the same pattern too),
        // but registering a harmless placeholder Block/Item under an id nothing ever looks up as a
        // Block/Item costs nothing. This is what keeps this file from needing hand-maintained id
        // lists that drift out of sync as ModBlocks/ModItems grow - it derives straight from
        // whatever the datapack on disk currently references, the same source of truth
        // DatapackSchemaTest itself validates against.
        Set<String> modIds = scanModIdReferences();

        // Block's constructor self-registers an "intrusive" Holder into BuiltInRegistries.BLOCK
        // (see Block.<init>), but that registry's one-time intrusive-holder bookkeeping map is
        // nulled out once vanilla's own bootstrap finishes populating it - reopen it so late
        // (test-only) Block construction can still register itself, same as the frozen flag above.
        reopenIntrusiveHolders(BuiltInRegistries.BLOCK);
        setFrozen(BuiltInRegistries.BLOCK, false);
        for (String id : modIds) {
            // Block's own constructor performs the actual registry write (it self-registers its
            // intrusive holder) - stays unfrozen for the whole loop rather than using the shared
            // register() helper (which re-freezes after each call, and `new Block(...)` needs
            // frozen=false at construction time, before register() ever runs).
            Block block = placeholderBlock(id);
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, id), block);
        }
        BuiltInRegistries.BLOCK.freeze();

        // Item works the same self-registering-intrusive-holder way as Block (see above).
        reopenIntrusiveHolders(BuiltInRegistries.ITEM);
        setFrozen(BuiltInRegistries.ITEM, false);
        for (String id : modIds) {
            net.minecraft.world.item.Item item = new net.minecraft.world.item.Item(new net.minecraft.world.item.Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, id), item);
        }
        BuiltInRegistries.ITEM.freeze();

        // EntityType self-registers an intrusive holder too (same as Block/Item above).
        // createNothing() is vanilla's own pattern for a marker/placeholder entity type (used for
        // e.g. LIGHTNING_BOLT).
        reopenIntrusiveHolders(BuiltInRegistries.ENTITY_TYPE);
        setFrozen(BuiltInRegistries.ENTITY_TYPE, false);
        for (String id : modIds) {
            net.minecraft.world.entity.EntityType<?> entityType =
                    net.minecraft.world.entity.EntityType.Builder.createNothing(net.minecraft.world.entity.MobCategory.MISC).build(id);
            register(BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE, id, entityType);
        }
        BuiltInRegistries.ENTITY_TYPE.freeze();

        setFrozen(BuiltInRegistries.FEATURE, false);
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "single_structure", new SingleStructureFeature(SingleStructureConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "bryce_pillars", new BrycePillarsFeature(BrycePillarsConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "moorland_podzol_conversion", new PodzolConversionFeature(NoneFeatureConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "moorland_double_tall_grass", new DoubleTallGrassFeature(NoneFeatureConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "moorland_waterlily_fixup", new WaterLilyFixupFeature(NoneFeatureConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "mystic_goo_conversion", new GooConversionFeature(NoneFeatureConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "netherlands_wheat_field", new NetherlandsWheatFieldFeature(NoneFeatureConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "underground_jungle_cave_vine", new CaveVineFeature(NoneFeatureConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "underground_jungle_fallen_jungle_tree", new FallenJungleTreeFeature(NoneFeatureConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "underground_jungle_multi", new MultiFeature(MultiFeatureConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "basalt_bank", new BasaltBankFeature(NoneFeatureConfiguration.CODEC));
        register(BuiltInRegistries.FEATURE, Registries.FEATURE, "lava_flow_kickstart", new LavaFlowKickstartFeature(NoneFeatureConfiguration.CODEC));
        BuiltInRegistries.FEATURE.freeze();

        setFrozen(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, false);
        register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, Registries.PLACEMENT_MODIFIER_TYPE, "river_noise_filter", (PlacementModifierType<RiverNoiseFilter>) () -> RiverNoiseFilter.CODEC);
        register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, Registries.PLACEMENT_MODIFIER_TYPE, "min_y_filter", (PlacementModifierType<MinYFilter>) () -> MinYFilter.CODEC);
        BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.freeze();

        setFrozen(BuiltInRegistries.STRUCTURE_TYPE, false);
        register(BuiltInRegistries.STRUCTURE_TYPE, Registries.STRUCTURE_TYPE, "windmill", (StructureType<WindmillStructure>) () -> WindmillStructure.CODEC);
        BuiltInRegistries.STRUCTURE_TYPE.freeze();

        setFrozen(BuiltInRegistries.TREE_DECORATOR_TYPE, false);
        register(BuiltInRegistries.TREE_DECORATOR_TYPE, Registries.TREE_DECORATOR_TYPE, "cave_vine_tree_decorator",
                newTreeDecoratorType(CaveVineTreeDecorator.CODEC));
        BuiltInRegistries.TREE_DECORATOR_TYPE.freeze();

        setFrozen(BuiltInRegistries.TRUNK_PLACER_TYPE, false);
        register(BuiltInRegistries.TRUNK_PLACER_TYPE, Registries.TRUNK_PLACER_TYPE, "mystic_trunk_placer",
                newTrunkPlacerType(MysticTrunkPlacer.CODEC));
        BuiltInRegistries.TRUNK_PLACER_TYPE.freeze();

        setFrozen(BuiltInRegistries.TRIGGER_TYPES, false);
        register(BuiltInRegistries.TRIGGER_TYPES, Registries.TRIGGER_TYPE, "lured_piranha_with_bait", new BaitLureTrigger());
        BuiltInRegistries.TRIGGER_TYPES.freeze();
    }

    // TreeDecoratorType/TrunkPlacerType's constructors are private in vanilla - real mods reach
    // them via an access-transformed/mixin-widened platform call
    // (ExtraBiomesExpectPlatform.createTreeDecoratorType/createTrunkPlacerType), which itself needs
    // a real platform module. Reflection reaches the same private constructor directly.
    @SuppressWarnings("unchecked")
    private static <P extends net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator> TreeDecoratorType<P> newTreeDecoratorType(MapCodec<P> codec) {
        try {
            Constructor<TreeDecoratorType> ctor = TreeDecoratorType.class.getDeclaredConstructor(MapCodec.class);
            ctor.setAccessible(true);
            return (TreeDecoratorType<P>) ctor.newInstance(codec);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <P extends net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer> TrunkPlacerType<P> newTrunkPlacerType(MapCodec<P> codec) {
        try {
            Constructor<TrunkPlacerType> ctor = TrunkPlacerType.class.getDeclaredConstructor(MapCodec.class);
            ctor.setAccessible(true);
            return (TrunkPlacerType<P>) ctor.newInstance(codec);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    // Caller is responsible for unfreezing (setFrozen(registry, false)) before a whole batch of
    // these and calling the registry's real public .freeze() (not setFrozen) after - freeze() also
    // finalizes read-side state a register() call alone doesn't, and skipping it (or reflectively
    // flipping the frozen flag back on instead) leaves entries readable via containsKey() but with
    // their Holder.Reference value still null, surfacing later as "Trying to access unbound value".
    private static <T> void register(Registry<T> registry, ResourceKey<? extends Registry<T>> registryKey, String path, T value) {
        ((net.minecraft.core.WritableRegistry<T>) registry).register(
                ResourceKey.create(registryKey, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, path)), value, RegistrationInfo.BUILT_IN);
    }

    // A plain Block has no BlockState properties at all, which is fine for most loot
    // table/recipe/tag references (they only need the id to exist) but not for the handful of
    // loot tables with a block_state_property condition (slabs' "type", doors' "half", this mod's
    // own pebble blocks' "size") - those need a placeholder with the real shape, inferred from the
    // same naming convention ModBlocks itself already follows for every block of that shape
    // (see ModBlocks.registerStandardWoodSet/registerBlock call sites), not a per-id hardcoded list.
    private static Block placeholderBlock(String id) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
        if (id.equals("pebble_block")) {
            return new PebbleBlock(properties);
        }
        if (id.equals("mossy_pebble_block")) {
            return new MossyPebbleBlock(properties);
        }
        if (id.endsWith("_slab")) {
            return new SlabBlock(properties);
        }
        if (id.endsWith("_door")) {
            return new DoorBlock(BlockSetType.OAK, properties);
        }
        return new Block(properties);
    }

    private static Set<String> scanModIdReferences() {
        Set<String> ids = new java.util.HashSet<>();
        for (Path root : DATA_ROOTS) {
            if (!java.nio.file.Files.isDirectory(root)) {
                continue;
            }
            try (java.util.stream.Stream<Path> walk = java.nio.file.Files.walk(root)) {
                for (Path file : (Iterable<Path>) walk.filter(java.nio.file.Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().endsWith(".json"))::iterator) {
                    String content = java.nio.file.Files.readString(file, java.nio.charset.StandardCharsets.UTF_8);
                    java.util.regex.Matcher matcher = MOD_ID_REFERENCE.matcher(content);
                    while (matcher.find()) {
                        ids.add(matcher.group(1));
                    }
                }
            } catch (java.io.IOException e) {
                throw new java.io.UncheckedIOException(e);
            }
        }
        return ids;
    }

    private static void reopenIntrusiveHolders(Registry<?> registry) {
        try {
            Field field = net.minecraft.core.MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
            field.setAccessible(true);
            if (field.get(registry) == null) {
                field.set(registry, new java.util.IdentityHashMap<>());
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void setFrozen(Registry<?> registry, boolean frozen) {
        try {
            Field field = net.minecraft.core.MappedRegistry.class.getDeclaredField("frozen");
            field.setAccessible(true);
            field.set(registry, frozen);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static HolderLookup.Provider load() {
        List<PackResources> packs = new ArrayList<>();
        // Vanilla's own built-in datapack (data/minecraft/**, read straight off the merged
        // Minecraft jar this test classpath already resolves) - without it, vanilla's own
        // required-non-empty dynamic registries (e.g. minecraft:painting_variant) come back empty
        // and RegistryDataLoader.load() throws. VanillaPackResourcesBuilder#pushJarResources
        // can't locate this jar reliably from a plain Gradle test JVM (no dev-launch classpath
        // marker), so open it directly instead via the jar backing a known vanilla class.
        packs.add(new FilePackResources.FileResourcesSupplier(vanillaJarPath())
                .openPrimary(new PackLocationInfo("vanilla", Component.literal("vanilla"), PackSource.BUILT_IN, Optional.empty())));
        for (Path root : DATA_ROOTS) {
            packs.add(new PathPackResources(
                    new PackLocationInfo(root.toString(), Component.literal(root.toString()), PackSource.BUILT_IN, Optional.empty()),
                    root));
        }
        try (MultiPackResourceManager resources = new MultiPackResourceManager(PackType.SERVER_DATA, packs)) {
            RegistryAccess base = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            RegistryAccess.Frozen loaded = RegistryDataLoader.load(resources, base, registriesToLoad());
            // RegistryDataLoader.load()'s result only carries the registries it just loaded
            // (registriesToLoad()) - callers (DatapackSchemaTest) need item/block/enchantment/...
            // lookups too (e.g. loot table/advancement/recipe predicates), so merge base back in.
            return HolderLookup.Provider.create(java.util.stream.Stream.concat(
                    base.registries().map(entry -> entry.value().asLookup()),
                    loaded.registries().map(entry -> entry.value().asLookup())));
        }
    }

    // The worldgen registries this mod actually authors data for. Deliberately narrower than
    // RegistryDataLoader.WORLDGEN_REGISTRIES as a whole - the full vanilla list also loads
    // registries like enchantment/trim_pattern/wolf_variant whose own vanilla JSON leans on
    // item/entity_type tags for HolderSet fields, which need a full ReloadableServerResources-
    // style tag pass this test doesn't set up. None of that is relevant to validating this mod's
    // own worldgen JSON, so only load the registries this mod (and its cross-references) touch.
    //
    // Computed lazily (not a static field) - touching RegistryDataLoader's own class before
    // Bootstrap.bootStrap() has run trips the same "Not bootstrapped" failure this method's
    // caller is careful to avoid.
    private static List<RegistryDataLoader.RegistryData<?>> registriesToLoad() {
        Set<ResourceKey<?>> keys = Set.of(
                Registries.CONFIGURED_FEATURE,
                Registries.PLACED_FEATURE,
                Registries.STRUCTURE,
                Registries.STRUCTURE_SET,
                Registries.TEMPLATE_POOL,
                Registries.PROCESSOR_LIST,
                Registries.BIOME,
                Registries.NOISE,
                Registries.DENSITY_FUNCTION,
                Registries.NOISE_SETTINGS,
                Registries.CONFIGURED_CARVER,
                // Not authored by this mod, but every vanilla nether biome this test also loads
                // as base content (basalt_deltas, crimson_forest, ...) references its ambient
                // sound presets through this registry.
                Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST,
                // Not authored by this mod, but this mod's own loot tables/recipes/advancements
                // reference vanilla enchantments (looting, fortune, silk_touch, ...) by id, and
                // vanilla's own enchantment JSON in turn references damage types (thorns, ...).
                Registries.ENCHANTMENT,
                Registries.DAMAGE_TYPE);
        return RegistryDataLoader.WORLDGEN_REGISTRIES.stream()
                .filter(data -> keys.contains(data.key()))
                .toList();
    }

    private static Path vanillaJarPath() {
        try {
            return Paths.get(RegistryDataLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private DatapackRegistries() {
    }
}
