package net.winepicfin.extrabiomes.worldgen.tree.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MysticTrunkPlacer extends TrunkPlacer {
    private static final Codec<UniformInt> BRANCH_START_CODEC = ExtraCodecs.validate(UniformInt.CODEC, (p_275181_) -> {
        return p_275181_.getMaxValue() - p_275181_.getMinValue() < 1 ? DataResult.error(() -> {
            return "Need at least 2 blocks variation for the branch starts to fit both branches";
        }) : DataResult.success(p_275181_);
    });
public static final Codec<MysticTrunkPlacer> CODEC = RecordCodecBuilder.create((p_273579_) -> {
    return trunkPlacerParts(p_273579_).and(p_273579_.group(IntProvider.codec(1, 3).fieldOf("branch_count").forGetter((p_272644_) -> {
        return p_272644_.branchCount;
    }), IntProvider.codec(2, 16).fieldOf("branch_horizontal_length").forGetter((p_273612_) -> {
        return p_273612_.branchHorizontalLength;
    }), IntProvider.codec(-16, 0, BRANCH_START_CODEC).fieldOf("branch_start_offset_from_top").forGetter((p_272705_) -> {
        return p_272705_.branchStartOffsetFromTop;
    }), IntProvider.codec(-16, 16).fieldOf("branch_end_offset_from_top").forGetter((p_273633_) -> {
        return p_273633_.branchEndOffsetFromTop;
    }))).apply(p_273579_, MysticTrunkPlacer::new);
});
    public MysticTrunkPlacer(int pBaseHeight, int pHeightRandomA, int pHeightRandomB, IntProvider pBranchCount, IntProvider pBranchLength, UniformInt pBranchOffsetFromTop, IntProvider branchEndOffsetFromTop) {
        super(pBaseHeight, pHeightRandomA, pHeightRandomB);
        this.branchCount = pBranchCount;
        this.branchHorizontalLength = pBranchLength;
        this.branchStartOffsetFromTop = pBranchOffsetFromTop;
        this.secondBranchStartOffsetFromTop = UniformInt.of(pBranchOffsetFromTop.getMinValue(), pBranchOffsetFromTop.getMaxValue() - 1);
        this.branchEndOffsetFromTop = branchEndOffsetFromTop;
    }
    private final IntProvider branchCount;
    private final IntProvider branchHorizontalLength;
    private final UniformInt branchStartOffsetFromTop;
    private final UniformInt secondBranchStartOffsetFromTop;
    private final IntProvider branchEndOffsetFromTop;
    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.MYSTIC_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        BlockPos blockpos = pPos.below();
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos, pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.north(), pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.east(), pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.south(), pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.west(), pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.north().east(), pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.north().west(), pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.south().east(), pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.south().west(), pConfig);

        int i = Math.max(0, pFreeTreeHeight - 1 + this.branchStartOffsetFromTop.sample(pRandom));
        int j = Math.max(0, pFreeTreeHeight - 1 + this.secondBranchStartOffsetFromTop.sample(pRandom));
        if (j >= i) {
            ++j;
        }
        int k = this.branchCount.sample(pRandom);
        boolean flag = k == 3;
        boolean flag1 = k >= 2;
        int l;
        if (flag) {
            l = pFreeTreeHeight;
        } else if (flag1) {
            l = Math.max(i, j) + 1;
        } else {
            l = i + 1;
        }
        this.placeLog(pLevel, pBlockSetter, pRandom, pPos.north().east(), pConfig);
        this.placeLog(pLevel, pBlockSetter, pRandom, pPos.north().west(), pConfig);
        this.placeLog(pLevel, pBlockSetter, pRandom, pPos.south().east(), pConfig);
        this.placeLog(pLevel, pBlockSetter, pRandom, pPos.south().west(), pConfig);
        for(int i1 = 0; i1 < l; ++i1) {
            this.placeLog(pLevel, pBlockSetter, pRandom, pPos.above(i1), pConfig);
            this.placeLog(pLevel, pBlockSetter, pRandom, pPos.north().above(i1), pConfig);
            this.placeLog(pLevel, pBlockSetter, pRandom, pPos.east().above(i1), pConfig);
            this.placeLog(pLevel, pBlockSetter, pRandom, pPos.south().above(i1), pConfig);
            this.placeLog(pLevel, pBlockSetter, pRandom, pPos.west().above(i1), pConfig);
        }

        List<FoliagePlacer.FoliageAttachment> list = new ArrayList<>();
        if (flag) {
            list.add(new FoliagePlacer.FoliageAttachment(pPos.above(l), 0, false));
        }

        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
        Function<BlockState, BlockState> function = (p_273382_) -> {
            return p_273382_.trySetValue(RotatedPillarBlock.AXIS, direction.getAxis());
        };
        list.add(this.generateBranch(pLevel, pBlockSetter, pRandom, pFreeTreeHeight, pPos, pConfig, function, direction, i, i < l - 1, blockpos$mutableblockpos));
        if (flag1) {
            list.add(this.generateBranch(pLevel, pBlockSetter, pRandom, pFreeTreeHeight, pPos, pConfig, function, direction.getOpposite(), j, j < l - 1, blockpos$mutableblockpos));
        }

        return list;
    }

    private FoliagePlacer.FoliageAttachment generateBranch(LevelSimulatedReader p_272736_, BiConsumer<BlockPos, BlockState> p_273092_, RandomSource p_273449_, int p_272659_, BlockPos p_273743_, TreeConfiguration p_273027_, Function<BlockState, BlockState> p_273558_, Direction p_273712_, int p_272980_, boolean p_272719_, BlockPos.MutableBlockPos p_273496_) {
        p_273496_.set(p_273743_).move(Direction.UP, p_272980_);
        int i = p_272659_ - 1 + this.branchEndOffsetFromTop.sample(p_273449_);
        boolean flag = p_272719_ || i < p_272980_;
        int j = this.branchHorizontalLength.sample(p_273449_) + (flag ? 1 : 0);
        BlockPos blockpos = p_273743_.relative(p_273712_, j).above(i);
        int k = flag ? 2 : 1;

        for(int l = 0; l < k; ++l) {
            this.placeLog(p_272736_, p_273092_, p_273449_, p_273496_.move(p_273712_), p_273027_, p_273558_);
        }

        Direction direction = blockpos.getY() > p_273496_.getY() ? Direction.UP : Direction.DOWN;

        while(true) {
            int i1 = p_273496_.distManhattan(blockpos);
            if (i1 == 0) {
                return new FoliagePlacer.FoliageAttachment(blockpos.above(), 0, false);
            }

            float f = (float)Math.abs(blockpos.getY() - p_273496_.getY()) / (float)i1;
            boolean flag1 = p_273449_.nextFloat() < f;
            p_273496_.move(flag1 ? direction : p_273712_);
            this.placeLog(p_272736_, p_273092_, p_273449_, p_273496_, p_273027_, flag1 ? Function.identity() : p_273558_);
        }
    }
}
