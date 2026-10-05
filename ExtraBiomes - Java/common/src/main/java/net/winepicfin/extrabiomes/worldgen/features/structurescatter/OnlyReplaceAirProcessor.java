package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

// Runtime-only like PreserveBedrockProcessor: STRUCTURE_PROCESSOR is frozen before mod init, so NOP stands in for a real type.
public final class OnlyReplaceAirProcessor extends StructureProcessor {
    public static final OnlyReplaceAirProcessor INSTANCE = new OnlyReplaceAirProcessor();

    private OnlyReplaceAirProcessor() {
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos, StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        return level.getBlockState(relativeBlockInfo.pos()).isAir() ? relativeBlockInfo : null;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.NOP;
    }
}
