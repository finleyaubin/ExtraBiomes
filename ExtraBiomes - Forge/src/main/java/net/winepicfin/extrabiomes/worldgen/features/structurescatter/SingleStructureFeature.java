package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
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

        BlockPos origin = context.origin().offset(0, config.groundOffset(), 0);

        return template.placeInWorld(level, origin, origin, settings, random, Block.UPDATE_CLIENTS);
    }
}
