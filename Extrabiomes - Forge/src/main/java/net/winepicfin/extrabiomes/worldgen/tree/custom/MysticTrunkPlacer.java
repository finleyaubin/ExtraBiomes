package net.winepicfin.extrabiomes.worldgen.tree.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class MysticTrunkPlacer extends TrunkPlacer {
public static final Codec<MysticTrunkPlacer> CODEC = RecordCodecBuilder.create(mysticTrunkPlacerInstance -> trunkPlacerParts(mysticTrunkPlacerInstance).apply(mysticTrunkPlacerInstance, MysticTrunkPlacer::new));
    public MysticTrunkPlacer(int p_70268_, int p_70269_, int p_70270_) {
        super(p_70268_, p_70269_, p_70270_);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.MYSTIC_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader p_226157_, BiConsumer<BlockPos, BlockState> p_226158_, RandomSource p_226159_, int p_226160_, BlockPos p_226161_, TreeConfiguration p_226162_) {
        return null;
    }
}
