package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Optional;

/**
 * Reusable Feature that places a single converted (Bedrock -> Java) .nbt structure template
 * unconditionally at the feature's origin, honoring {@link SingleStructureConfiguration}'s fixed
 * or random rotation and vertical ground offset.
 * <p>
 * This is infrastructure only - do not add subsystem-specific logic here. Every subsystem that
 * needs to scatter a single raw structure (as opposed to a jigsaw assembly, an ore vein, etc.)
 * should register its own ConfiguredFeature backed by this Feature class, each pointing at its
 * own converted .nbt and its own {@link SingleStructureConfiguration}. See
 * {@link OasisPuddleFeature} for a complete worked example.
 */
public record SingleStructureFeature(Identifier structure, Optional<Rotation> rotation, int groundOffset, boolean centered, Optional<BlockPos> anchor, float minClearFraction, boolean requireGroundedFloor, List<Block> requiredFloorBlocks, float minSubmergedFraction, boolean embedInStone, boolean weatheredVariation, boolean followTerrain) implements Feature {

    public SingleStructureFeature(Identifier structure) {
        this(structure, Optional.empty(), 0, false, Optional.empty(), 0.0F, false, List.of(), 0.0F, false, false, false);
    }

    public SingleStructureFeature(Identifier structure, Rotation fixedRotation) {
        this(structure, Optional.of(fixedRotation), 0, false, Optional.empty(), 0.0F, false, List.of(), 0.0F, false, false, false);
    }

    public SingleStructureFeature(Identifier structure, int groundOffset) {
        this(structure, Optional.empty(), groundOffset, false, Optional.empty(), 0.0F, false, List.of(), 0.0F, false, false, false);
    }

    // Jellycoral-style use: random rotation + a required submerged (water/waterlogged) fraction,
    // for templates that no longer bundle their own explicit water fill.
    public SingleStructureFeature(Identifier structure, int groundOffset, float minSubmergedFraction) {
        this(structure, Optional.empty(), groundOffset, false, Optional.empty(), 0.0F, false, List.of(), minSubmergedFraction, false, false, false);
    }

    public SingleStructureFeature(Identifier structure, Optional<Rotation> rotation, int groundOffset) {
        this(structure, rotation, groundOffset, false, Optional.empty(), 0.0F, false, List.of(), 0.0F, false, false, false);
    }

    public SingleStructureFeature(Identifier structure, Optional<Rotation> rotation, int groundOffset, boolean centered) {
        this(structure, rotation, groundOffset, centered, Optional.empty(), 0.0F, false, List.of(), 0.0F, false, false, false);
    }

    // Mushroom-style use: fixed/random rotation + centered + a required clear-space fraction.
    public SingleStructureFeature(Identifier structure, Optional<Rotation> rotation, int groundOffset, boolean centered, float minClearFraction) {
        this(structure, rotation, groundOffset, centered, Optional.empty(), minClearFraction, false, List.of(), 0.0F, false, false, false);
    }

    // Stick-pile-style use: fixed/random rotation + a required clear-space fraction + a required solid floor.
    public SingleStructureFeature(Identifier structure, Optional<Rotation> rotation, int groundOffset, float minClearFraction, boolean requireGroundedFloor) {
        this(structure, rotation, groundOffset, false, Optional.empty(), minClearFraction, requireGroundedFloor, List.of(), 0.0F, false, false, false);
    }

    // Oasis-puddle-style use: required solid floor restricted to a specific set of blocks.
    public SingleStructureFeature(Identifier structure, Optional<Rotation> rotation, int groundOffset, boolean requireGroundedFloor, List<Block> requiredFloorBlocks) {
        this(structure, rotation, groundOffset, false, Optional.empty(), 0.0F, requireGroundedFloor, requiredFloorBlocks, 0.0F, false, false, false);
    }

    // Stone-pillar-style use: required solid floor restricted to a specific set of blocks, sunk into real stone rather than anchored to the dirt/grass surface, with noise-based weathering/vegetation.
    public SingleStructureFeature(Identifier structure, Optional<Rotation> rotation, int groundOffset, boolean requireGroundedFloor, List<Block> requiredFloorBlocks, boolean embedInStone, boolean weatheredVariation) {
        this(structure, rotation, groundOffset, false, Optional.empty(), 0.0F, requireGroundedFloor, requiredFloorBlocks, 0.0F, embedInStone, weatheredVariation, false);
    }

    // Snow-drift-style use: low, wide template draped over the terrain column by column.
    public SingleStructureFeature(Identifier structure, int groundOffset, boolean centered, boolean followTerrain) {
        this(structure, Optional.empty(), groundOffset, centered, Optional.empty(), 0.0F, false, List.of(), 0.0F, false, false, followTerrain);
    }

    public SingleStructureFeature(Identifier structure, BlockPos anchor) {
        this(structure, Optional.empty(), 0, false, Optional.of(anchor), 0.0F, false, List.of(), 0.0F, false, false, false);
    }

    public static final MapCodec<SingleStructureFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("structure").forGetter(SingleStructureFeature::structure),
            Codec.STRING.xmap(Rotation::valueOf, Rotation::name).optionalFieldOf("rotation").forGetter(SingleStructureFeature::rotation),
            Codec.INT.optionalFieldOf("ground_offset", 0).forGetter(SingleStructureFeature::groundOffset),
            Codec.BOOL.optionalFieldOf("centered", false).forGetter(SingleStructureFeature::centered),
            BlockPos.CODEC.optionalFieldOf("anchor").forGetter(SingleStructureFeature::anchor),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("min_clear_fraction", 0.0F).forGetter(SingleStructureFeature::minClearFraction),
            Codec.BOOL.optionalFieldOf("require_grounded_floor", false).forGetter(SingleStructureFeature::requireGroundedFloor),
            Codec.list(BuiltInRegistries.BLOCK.byNameCodec()).optionalFieldOf("required_floor_blocks", List.of()).forGetter(SingleStructureFeature::requiredFloorBlocks),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("min_submerged_fraction", 0.0F).forGetter(SingleStructureFeature::minSubmergedFraction),
            Codec.BOOL.optionalFieldOf("embed_in_stone", false).forGetter(SingleStructureFeature::embedInStone),
            Codec.BOOL.optionalFieldOf("weathered_variation", false).forGetter(SingleStructureFeature::weatheredVariation),
            Codec.BOOL.optionalFieldOf("follow_terrain", false).forGetter(SingleStructureFeature::followTerrain)
    ).apply(instance, SingleStructureFeature::new));

    @Override
    public MapCodec<SingleStructureFeature> codec() {
        return CODEC;
    }

    /**
     * Vanilla's FEATURES chunk status only lets a Feature safely write into the chunk currently
     * decorating plus one chunk of buffer on every side (a 3x3-chunk / 48-block-wide window). Any
     * template wider than that in X/Z (e.g. the 22x30x16 windmill) can have its randomized origin
     * land close enough to a chunk edge that part of it falls outside that window; vanilla then
     * silently drops those blocks (logged as "Detected setBlock in a far chunk"), producing a
     * visibly clipped structure instead of a clean skip.
     */
    private static final int WRITE_RADIUS_CHUNKS = 1;

    // One block above the max thickness of Java's randomized 1-5-block bedrock floor (y=-64); matches MesaFeatures/ModSurfaceRules' own margin.
    private static final int BEDROCK_MARGIN_Y = -59;

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos placementOrigin) {
        SingleStructureFeature config = this;

        ServerLevel serverLevel = level.getLevel();

        StructureTemplateManager structureManager = serverLevel.getStructureTemplateManager();
        Optional<StructureTemplate> templateOpt = structureManager.get(config.structure());
        if (templateOpt.isEmpty()) {
            return false;
        }
        StructureTemplate template = templateOpt.get();

        Rotation rotation = config.rotation().orElseGet(() -> Rotation.getRandom(random));
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false)
                // Prevents this otherwise-unconditional placement from carving through the bottom bedrock layer near y=-64 - see PreserveBedrockProcessor's javadoc.
                .addProcessor(PreserveBedrockProcessor.INSTANCE)
                // minecraft:structure_void placed literally is just another (invisible, no-collision) block - vanilla's
                // placeInWorld doesn't skip it on its own, so without this a converted template using structure_void as
                // a "leave this position alone" marker (e.g. jellycoral relying on the surrounding ocean rather than its
                // own explicit water fill) would overwrite whatever's already there instead of leaving it untouched.
                .addProcessor(new BlockIgnoreProcessor(List.of(Blocks.STRUCTURE_VOID)));
        if (config.weatheredVariation()) {
            settings.addProcessor(net.winepicfin.extrabiomes.worldgen.features.stonepillars.PillarWeatheringProcessor.INSTANCE);
        }
        if (config.followTerrain()) {
            settings.addProcessor(new GravityProcessor(Heightmap.Types.OCEAN_FLOOR_WG, config.groundOffset()));
        }

        BlockPos anchor = placementOrigin.offset(0, config.groundOffset(), 0);
        BlockPos origin = anchor;
        if (config.anchor().isPresent()) {
            // Same rotation-pivot trick as the centered case below, but for an arbitrary local point (e.g. a leaning tree's trunk base) instead of the footprint center.
            BlockPos rotatedPoint = StructureTemplate.transform(config.anchor().get(), Mirror.NONE, rotation, BlockPos.ZERO);
            origin = anchor.subtract(rotatedPoint);
        } else if (config.centered()) {
            // StructurePlaceSettings' rotation pivot is the template's local (0,0,0) corner, applied before translating by `origin`, so the center must be rotated the same way to find its offset from that corner.
            Vec3i size = template.getSize();
            BlockPos localCenter = new BlockPos(size.getX() / 2, 0, size.getZ() / 2);
            BlockPos rotatedCenter = StructureTemplate.transform(localCenter, Mirror.NONE, rotation, BlockPos.ZERO);
            origin = anchor.subtract(rotatedCenter);
        }

        BoundingBox structureBox = template.getBoundingBox(settings, origin);

        // Re-anchor to the shallowest real stone under the whole footprint instead of the origin
        // column's dirt/grass surface - see SingleStructureConfiguration#embedInStone.
        if (config.embedInStone()) {
            int minStoneTopY = findMinStoneTopY(level, structureBox.minX(), structureBox.maxX(), structureBox.minZ(), structureBox.maxZ());
            int deltaY = (minStoneTopY + config.groundOffset()) - anchor.getY();
            if (deltaY != 0) {
                anchor = anchor.offset(0, deltaY, 0);
                origin = origin.offset(0, deltaY, 0);
                structureBox = template.getBoundingBox(settings, origin);
            }
        }

        if (!fitsWithinSafeWriteArea(structureBox, placementOrigin)) {
            return false;
        }

        // Skip the whole placement rather than letting PreserveBedrockProcessor drop individual blocks - block-by-block skipping produced a floating-cap/clipped-through-walls look.
        if (structureBox.minY() < BEDROCK_MARGIN_Y) {
            return false;
        }

        // Skip rather than let the world silently drop everything above the build limit (e.g. the 150-tall snow spire on high ground).
        if (structureBox.maxY() > level.getMaxY()) {
            return false;
        }

        // Opt-in check (minClearFraction 0.0F by default keeps unrelated subsystems placing unconditionally) - currently only used by huge mushrooms to avoid landing on an already-placed neighbour.
        if (config.minClearFraction() > 0.0F && !hasEnoughClearSpace(level, structureBox, config.minClearFraction())) {
            return false;
        }

        // Opt-in check (minSubmergedFraction 0.0F by default keeps unrelated subsystems unaffected) - for templates
        // (e.g. jellycoral) that no longer bundle their own explicit water fill and so need a placement-time
        // guarantee that they're actually landing underwater.
        if (config.minSubmergedFraction() > 0.0F && !isSubmergedEnough(level, structureBox, config.minSubmergedFraction())) {
            return false;
        }

        // Checked at the un-offset heightmap origin so a negative groundOffset doesn't probe underground.
        if (config.requireGroundedFloor() && !hasSolidFloor(level, structureBox, placementOrigin.getY() - 1, config.requiredFloorBlocks())) {
            return false;
        }

        boolean placed = template.placeInWorld(level, origin, anchor, settings, random, Block.UPDATE_CLIENTS);
        // Real exterior faces are only knowable once the structure is actually in the world - see PillarWeatheringProcessor's javadoc for why this can't be done as a processBlock transform.
        if (placed && config.weatheredVariation()) {
            net.winepicfin.extrabiomes.worldgen.features.stonepillars.PillarWeatheringProcessor.growVegetation(level, structureBox, random);
        }
        return placed;
    }

    /**
     * Fraction of {@code box} that's currently air (covers regular air, cave air, and void air -
     * see {@link net.minecraft.world.level.block.state.BlockState#isAir}). Anything already
     * non-air here is either natural terrain the structure is expected to partially embed into
     * (its own base/floor row) or - the case this exists to catch - solid blocks from a previous
     * structure placement that's already sitting in this exact space.
     */
    private static boolean hasEnoughClearSpace(WorldGenLevel level, BoundingBox box, float minClearFraction) {
        int total = 0;
        int clear = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minY(); y <= box.maxY(); y++) {
                for (int z = box.minZ(); z <= box.maxZ(); z++) {
                    total++;
                    if (level.getBlockState(pos.set(x, y, z)).isAir()) {
                        clear++;
                    }
                }
            }
        }
        return total == 0 || (float) clear / total >= minClearFraction;
    }

    /**
     * Fraction of {@code box} that's currently water, checked via fluid state rather than block
     * state so waterlogged blocks (sea pickles, coral fans, kelp) count as submerged just like a
     * plain water block does - matching how the structure's own waterlogged pieces hold their
     * water without needing an adjacent explicit water block.
     */
    private static boolean isSubmergedEnough(WorldGenLevel level, BoundingBox box, float minSubmergedFraction) {
        int total = 0;
        int submerged = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minY(); y <= box.maxY(); y++) {
                for (int z = box.minZ(); z <= box.maxZ(); z++) {
                    total++;
                    if (level.getFluidState(pos.set(x, y, z)).is(net.minecraft.tags.FluidTags.WATER)) {
                        submerged++;
                    }
                }
            }
        }
        return total == 0 || (float) submerged / total >= minSubmergedFraction;
    }

    /**
     * The shallowest stone-like top across every column of {@code [minX,maxX] x [minZ,maxZ]} -
     * per column, walks down from that column's own {@code WORLD_SURFACE_WG} height through any
     * {@link BlockTags#DIRT} blocks (grass/dirt/podzol/coarse dirt/mycelium/rooted dirt) until it
     * hits stone or bedrock. Taking the shallowest (highest) result across the whole footprint,
     * rather than just the origin column, is what keeps a wide structure from having part of its
     * base still floating over a dip once it's re-anchored - see embedInStone's javadoc.
     */
    private static int findMinStoneTopY(WorldGenLevel level, int minX, int maxX, int minZ, int maxZ) {
        int minStoneTopY = Integer.MAX_VALUE;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
                pos.set(x, y, z);
                while (y > level.getMinY()) {
                    BlockState state = level.getBlockState(pos);
                    if (!state.isAir() && !state.is(BlockTags.DIRT)) {
                        break;
                    }
                    y--;
                    pos.set(x, y, z);
                }
                minStoneTopY = Math.min(minStoneTopY, y);
            }
        }
        return minStoneTopY == Integer.MAX_VALUE ? level.getMinY() : minStoneTopY;
    }

    // Catches wide structures hanging over a ledge that a single-column HeightmapPlacement wouldn't.
    private static boolean hasSolidFloor(WorldGenLevel level, BoundingBox box, int floorY, java.util.List<Block> requiredFloorBlocks) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos.set(x, floorY, z));
                if (requiredFloorBlocks.isEmpty()) {
                    if (state.isAir()) {
                        return false;
                    }
                } else if (!requiredFloorBlocks.contains(state.getBlock())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks the template's true rotated/mirrored footprint against the chunk-column window
     * vanilla actually allows Feature writes into, so oversized templates skip cleanly instead of
     * getting clipped at the edge.
     */
    private static boolean fitsWithinSafeWriteArea(BoundingBox structureBox, BlockPos decoratingColumn) {
        ChunkPos chunk = new ChunkPos(decoratingColumn.getX() >> 4, decoratingColumn.getZ() >> 4);
        int minX = chunk.getMinBlockX() - (WRITE_RADIUS_CHUNKS * 16);
        int maxX = chunk.getMaxBlockX() + (WRITE_RADIUS_CHUNKS * 16);
        int minZ = chunk.getMinBlockZ() - (WRITE_RADIUS_CHUNKS * 16);
        int maxZ = chunk.getMaxBlockZ() + (WRITE_RADIUS_CHUNKS * 16);
        return structureBox.minX() >= minX && structureBox.maxX() <= maxX
                && structureBox.minZ() >= minZ && structureBox.maxZ() <= maxZ;
    }
}
