package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

/**
 * Shared by every {@link SingleStructureFeature} placement: skips writing a template block
 * wherever the world already has real {@link Blocks#BEDROCK} at that exact position, instead of
 * overwriting it. {@link SingleStructureFeature} otherwise places its .nbt templates completely
 * unconditionally (see its own class javadoc) - it has no concept of "don't overwrite this" at
 * all, so any subsystem built on it (huge mushrooms, glacier snow drifts, and anything else
 * registered the same way) can carve straight through the world's bottom bedrock layer whenever
 * its randomized origin, or an {@code EnvironmentScanPlacement} floor search, happens to land near
 * y=-64. Unlike ore features' {@code RuleTest} (which only ever matches specific existing blocks
 * like stone/deepslate), structure placement has no equivalent gate by default - this processor
 * is that gate, added once here rather than duplicated per-subsystem.
 * <p>
 * This checks the real placed world, not an assumed Y threshold - it protects a column exactly
 * where the world's own randomized 1-5-block bedrock floor actually is, rather than guessing a
 * fixed margin the way a Y-range placement modifier would have to.
 * <p>
 * Runtime-only: constructed once and passed directly to {@link StructureTemplate#placeInWorld} at
 * generation time, never attached to anything Codec-serialized - so {@link #getType()} originally
 * just threw. That broke once Ars Nouveau's own StructureTemplate.placeInWorld mixin (its
 * preventAutoWaterlogging check) turned out to call {@code getType()} on every processor in the
 * active settings unconditionally, serialization or not - any structure placed alongside this
 * processor while Ars Nouveau is installed crashed world generation outright. Registering a real
 * {@link StructureProcessorType} to fix that (as this class used to) doesn't work either:
 * {@code BuiltInRegistries.STRUCTURE_PROCESSOR} freezes during vanilla's own bootstrap, before any
 * mod constructor has even run, so a mod can never register into it at all, at any point in its
 * lifecycle - "Registry is already frozen" fires immediately regardless of how early the
 * registration call is moved. Returning an existing vanilla type instead sidesteps that
 * entirely - {@link StructureProcessorType#NOP} was picked simply because it's the type vanilla
 * itself uses for its own no-op/identity processor, so its semantics ("this processor doesn't
 * need special handling") already match how this processor is actually used - Ars Nouveau's own
 * check only needs getType() to return *something* without throwing, not a value unique to this
 * processor.
 */
public final class PreserveBedrockProcessor extends StructureProcessor {
    public static final PreserveBedrockProcessor INSTANCE = new PreserveBedrockProcessor();

    private PreserveBedrockProcessor() {
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos, StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        // relativeBlockInfo.pos() is the real world position about to be written (blockInfo.pos() is still template-local space).
        return level.getBlockState(relativeBlockInfo.pos()).is(Blocks.BEDROCK) ? null : relativeBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.NOP;
    }
}
