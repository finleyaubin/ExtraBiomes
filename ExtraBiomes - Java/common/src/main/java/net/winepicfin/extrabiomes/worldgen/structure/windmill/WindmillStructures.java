package net.winepicfin.extrabiomes.worldgen.structure.windmill;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

import java.util.List;
import java.util.Map;

/**
 * Converts the old {@code NetherlandsWindmillFeature} scatter-feature placement into a real jigsaw
 * Structure (shows up under {@code /locate}, has its own structure/structure_set/template_pool datapack
 * files - see this class's three bootstrap methods, wired into the RegistrySetBuilder in
 * forge/datagen/ModWorldGenProvider and fabric/datagen/FabricDataGenerators exactly like every other
 * dynamic registry in this mod). Two templates: "the_netherlands/windmill" (the converted-from-Bedrock
 * original) and "the_netherlands/windmill_create" (a hand-built variant with a real Create windmill
 * bearing/kinetic contraption baked in, captured via structure block - see WindmillStructure#findGenerationPoint
 * for how the choice between them is made at worldgen time). Both are one-piece {@link StructureTemplatePool}s
 * placed via {@link WindmillStructure} (this mod's own thin re-implementation of vanilla's JigsawStructure,
 * needed for its fixed-rotation + pool-choice + afterPlace behaviour - see that class's javadoc).
 * <p>
 * Two independent Structures/StructureSets (netherlands + plains) rather than one, mirroring the old
 * two-PlacedFeature split exactly: a Structure's biome list can't vary placement odds per biome, so
 * matching the old differentiated rarity between "the netherlands" biomes and Moorlands needs two
 * separate StructureSets, each with only its own biomes and its own spacing/separation.
 */
public class WindmillStructures {
    private static final ResourceLocation WINDMILL_TEMPLATE = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "the_netherlands/windmill");
    private static final ResourceLocation WINDMILL_CREATE_TEMPLATE = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "the_netherlands/windmill_create");

    public static final ResourceKey<StructureTemplatePool> WINDMILL_POOL_KEY = poolKey("windmill");
    public static final ResourceKey<StructureTemplatePool> WINDMILL_CREATE_POOL_KEY = poolKey("windmill_create");
    public static final ResourceKey<Structure> WINDMILL_NETHERLANDS_KEY = structureKey("windmill_netherlands");
    public static final ResourceKey<Structure> WINDMILL_PLAINS_KEY = structureKey("windmill_plains");
    public static final ResourceKey<StructureSet> WINDMILL_NETHERLANDS_SET_KEY = setKey("windmill_netherlands");
    public static final ResourceKey<StructureSet> WINDMILL_PLAINS_SET_KEY = setKey("windmill_plains");

    // minecraft:structure_void placed literally is just another (invisible, no-collision) block - vanilla's
    // placeInWorld doesn't skip it on its own, so without this the void shell both windmill.nbt and
    // windmill_create.nbt use around their exterior (see the flood-fill cleanup that produced them) would
    // overwrite whatever terrain is already there instead of leaving it untouched.
    private static final Holder<StructureProcessorList> IGNORE_STRUCTURE_VOID =
            Holder.direct(new StructureProcessorList(List.of(new BlockIgnoreProcessor(List.of(Blocks.STRUCTURE_VOID)))));

    public static void bootstrapTemplatePool(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> poolLookup = context.lookup(net.minecraft.core.registries.Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> fallback = poolLookup.getOrThrow(Pools.EMPTY);
        context.register(WINDMILL_POOL_KEY, new StructureTemplatePool(
                fallback,
                List.of(Pair.of(StructurePoolElement.single(WINDMILL_TEMPLATE.toString(), IGNORE_STRUCTURE_VOID), 1)),
                StructureTemplatePool.Projection.RIGID
        ));
        context.register(WINDMILL_CREATE_POOL_KEY, new StructureTemplatePool(
                fallback,
                List.of(Pair.of(StructurePoolElement.single(WINDMILL_CREATE_TEMPLATE.toString(), IGNORE_STRUCTURE_VOID), 1)),
                StructureTemplatePool.Projection.RIGID
        ));
    }

    public static void bootstrapStructure(BootstrapContext<Structure> context) {
        HolderGetter<StructureTemplatePool> poolLookup = context.lookup(net.minecraft.core.registries.Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> pool = poolLookup.getOrThrow(WINDMILL_POOL_KEY);
        Holder<StructureTemplatePool> createPool = poolLookup.getOrThrow(WINDMILL_CREATE_POOL_KEY);
        HolderGetter<Biome> biomeLookup = context.lookup(net.minecraft.core.registries.Registries.BIOME);

        HolderSet<Biome> netherlandsBiomes = HolderSet.direct(
                biomeLookup.getOrThrow(ModBiomes.THE_NETHERLANDS),
                biomeLookup.getOrThrow(ModBiomes.THE_NETHERLANDS_MUTATED)
        );
        HolderSet<Biome> plainsBiomes = HolderSet.direct(biomeLookup.getOrThrow(ModBiomes.MOORLANDS));

        context.register(WINDMILL_NETHERLANDS_KEY, new WindmillStructure(
                new Structure.StructureSettings(netherlandsBiomes, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE),
                pool, createPool, Heightmap.Types.OCEAN_FLOOR_WG
        ));
        context.register(WINDMILL_PLAINS_KEY, new WindmillStructure(
                new Structure.StructureSettings(plainsBiomes, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE),
                pool, createPool, Heightmap.Types.OCEAN_FLOOR_WG
        ));
    }

    public static void bootstrapStructureSet(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structureLookup = context.lookup(net.minecraft.core.registries.Registries.STRUCTURE);
        // spacing/separation is a chunk-grid average, not directly comparable to the old RarityFilter's
        // per-chunk-column odds - these are hand-picked to feel about as (in)frequent in-game as the old
        // onAverageOnceEvery(30)/(50) did, not a mathematical conversion of them.
        context.register(WINDMILL_NETHERLANDS_SET_KEY, new StructureSet(
                structureLookup.getOrThrow(WINDMILL_NETHERLANDS_KEY),
                new RandomSpreadStructurePlacement(24, 8, RandomSpreadType.LINEAR, 194772013)
        ));
        context.register(WINDMILL_PLAINS_SET_KEY, new StructureSet(
                structureLookup.getOrThrow(WINDMILL_PLAINS_KEY),
                new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 194772017)
        ));
    }

    private static ResourceKey<StructureTemplatePool> poolKey(String name) {
        return ResourceKey.create(net.minecraft.core.registries.Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<Structure> structureKey(String name) {
        return ResourceKey.create(net.minecraft.core.registries.Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<StructureSet> setKey(String name) {
        return ResourceKey.create(net.minecraft.core.registries.Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }
}
