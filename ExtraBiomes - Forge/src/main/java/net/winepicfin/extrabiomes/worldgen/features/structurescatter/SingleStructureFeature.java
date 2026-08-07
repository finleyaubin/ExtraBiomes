package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

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
                .setIgnoreEntities(false);

        BlockPos anchor = context.origin().offset(0, config.groundOffset(), 0);
        BlockPos origin = anchor;
        if (config.anchor().isPresent()) {
            // Same rotation-pivot trick as the centered case below, but for an arbitrary local
            // point instead of the footprint center - lets a structure's actual focal point (e.g.
            // a leaning tree's trunk base, which isn't at the bounding box's corner OR center) be
            // what lands on `anchor` regardless of rotation.
            BlockPos rotatedPoint = StructureTemplate.transform(config.anchor().get(), Mirror.NONE, rotation, BlockPos.ZERO);
            origin = anchor.subtract(rotatedPoint);
        } else if (config.centered()) {
            // StructurePlaceSettings' rotation pivot defaults to the template's local (0,0,0)
            // corner, not its footprint center, and rotation is applied in that local space
            // BEFORE translating by `origin` - so the footprint's center must be rotated the
            // same way here to find how far it lands from that corner, then subtracted back out
            // so the center (not the corner) ends up sitting on `anchor` regardless of rotation.
            Vec3i size = template.getSize();
            BlockPos localCenter = new BlockPos(size.getX() / 2, 0, size.getZ() / 2);
            BlockPos rotatedCenter = StructureTemplate.transform(localCenter, Mirror.NONE, rotation, BlockPos.ZERO);
            origin = anchor.subtract(rotatedCenter);
        }

        if (!fitsWithinSafeWriteArea(template, settings, origin, context.origin())) {
            return false;
        }

        return template.placeInWorld(level, origin, anchor, settings, random, Block.UPDATE_CLIENTS);
    }

    /**
     * Checks the template's true rotated/mirrored footprint (via {@link StructureTemplate#getBoundingBox})
     * against the chunk-column window vanilla actually allows Feature writes into, so oversized
     * templates skip cleanly instead of getting clipped at the edge.
     */
    private static boolean fitsWithinSafeWriteArea(StructureTemplate template, StructurePlaceSettings settings, BlockPos origin, BlockPos decoratingColumn) {
        BoundingBox structureBox = template.getBoundingBox(settings, origin);
        ChunkPos chunk = new ChunkPos(decoratingColumn);
        int minX = chunk.getMinBlockX() - (WRITE_RADIUS_CHUNKS * 16);
        int maxX = chunk.getMaxBlockX() + (WRITE_RADIUS_CHUNKS * 16);
        int minZ = chunk.getMinBlockZ() - (WRITE_RADIUS_CHUNKS * 16);
        int maxZ = chunk.getMaxBlockZ() + (WRITE_RADIUS_CHUNKS * 16);
        return structureBox.minX() >= minX && structureBox.maxX() <= maxX
                && structureBox.minZ() >= minZ && structureBox.maxZ() <= maxZ;
    }
}
