package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

// Runtime-only like PreserveBedrockProcessor: STRUCTURE_PROCESSOR is frozen before mod init, so codec() is never invoked.
public final class OnlyReplaceAirProcessor implements StructureProcessor {
    public static final OnlyReplaceAirProcessor INSTANCE = new OnlyReplaceAirProcessor();

    private OnlyReplaceAirProcessor() {
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos, BlockPos originalPos, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        return level.getBlockState(relativeBlockInfo.pos()).isAir() ? relativeBlockInfo : null;
    }

    @Override
    public MapCodec<OnlyReplaceAirProcessor> codec() {
        return MapCodec.unit(INSTANCE);
    }
}
