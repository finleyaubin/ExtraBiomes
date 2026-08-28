package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
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
public class SingleStructureFeature extends Feature<SingleStructureConfiguration> {
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

    public SingleStructureFeature(Codec<SingleStructureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SingleStructureConfiguration> context) {
        SingleStructureConfiguration config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        ServerLevel serverLevel = level.getLevel();

        StructureTemplateManager structureManager = serverLevel.getStructureManager();
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

        BlockPos anchor = context.origin().offset(0, config.groundOffset(), 0);
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

        if (!fitsWithinSafeWriteArea(structureBox, context.origin())) {
            return false;
        }

        // Skip the whole placement rather than letting PreserveBedrockProcessor drop individual blocks - block-by-block skipping produced a floating-cap/clipped-through-walls look.
        if (structureBox.minY() < BEDROCK_MARGIN_Y) {
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
        if (config.requireGroundedFloor() && !hasSolidFloor(level, structureBox, context.origin().getY() - 1, config.requiredFloorBlocks())) {
            return false;
        }

        return template.placeInWorld(level, origin, anchor, settings, random, Block.UPDATE_CLIENTS);
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
                while (y > level.getMinBuildHeight()) {
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
        return minStoneTopY == Integer.MAX_VALUE ? level.getMinBuildHeight() : minStoneTopY;
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
        ChunkPos chunk = new ChunkPos(decoratingColumn);
        int minX = chunk.getMinBlockX() - (WRITE_RADIUS_CHUNKS * 16);
        int maxX = chunk.getMaxBlockX() + (WRITE_RADIUS_CHUNKS * 16);
        int minZ = chunk.getMinBlockZ() - (WRITE_RADIUS_CHUNKS * 16);
        int maxZ = chunk.getMaxBlockZ() + (WRITE_RADIUS_CHUNKS * 16);
        return structureBox.minX() >= minX && structureBox.maxX() <= maxX
                && structureBox.minZ() >= minZ && structureBox.maxZ() <= maxZ;
    }
}
