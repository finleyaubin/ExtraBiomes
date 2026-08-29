package net.winepicfin.extrabiomes.worldgen.tree.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    private static final Codec<UniformInt> BRANCH_START_CODEC = UniformInt.CODEC.validate((pUniformInt) -> {
        return pUniformInt.getMaxValue() - pUniformInt.getMinValue() < 1 ? DataResult.error(() -> {
            return "Need at least 2 blocks variation for the branch starts to fit both branches";
        }) : DataResult.success(pUniformInt);
    }).codec();
public static final Codec<MysticTrunkPlacer> CODEC = RecordCodecBuilder.create((pInstance) -> {
    return trunkPlacerParts(pInstance).and(pInstance.group(IntProvider.codec(1, 3).fieldOf("branch_count").forGetter((pTrunkPlacer) -> {
        return pTrunkPlacer.branchCount;
    }), IntProvider.codec(2, 16).fieldOf("branch_horizontal_length").forGetter((pTrunkPlacer) -> {
        return pTrunkPlacer.branchHorizontalLength;
    }), IntProvider.validateCodec(-16, 0, BRANCH_START_CODEC).fieldOf("branch_start_offset_from_top").forGetter((pTrunkPlacer) -> {
        return pTrunkPlacer.branchStartOffsetFromTop;
    }), IntProvider.codec(-16, 16).fieldOf("branch_end_offset_from_top").forGetter((pTrunkPlacer) -> {
        return pTrunkPlacer.branchEndOffsetFromTop;
    }))).apply(pInstance, MysticTrunkPlacer::new);
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
        // Sampled once here (rather than inside generateBranch) so the trunk can grow tall enough to
        // meet whichever branch reaches highest - branches keep their height variation, but the trunk
        // is always at least as tall, instead of a branch tip poking up above the crown.
        int branchAEndHeight = pFreeTreeHeight - 1 + this.branchEndOffsetFromTop.sample(pRandom);
        int branchBEndHeight = flag1 ? pFreeTreeHeight - 1 + this.branchEndOffsetFromTop.sample(pRandom) : Integer.MIN_VALUE;
        l = Math.min(pFreeTreeHeight, Math.max(l, Math.max(branchAEndHeight, branchBEndHeight) + 1));
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
        // Always cap the central trunk with its own canopy - the trunk log column already runs up to
        // l regardless of branch_count, so without this the 2-in-3 rolls with fewer than 3 branches
        // left the trunk ending bare with only an off-center branch tuft for leaves.
        list.add(new FoliagePlacer.FoliageAttachment(pPos.above(l), 0, false));

        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        Direction directionA = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
        Direction directionB = pRandom.nextFloat() < 0.5F ? pickDiagonalPartner(pRandom, directionA) : null;
        list.addAll(this.generateBranch(pLevel, pBlockSetter, pRandom, pPos, pConfig, directionA, directionB, i, i < l - 1, branchAEndHeight, l, blockpos$mutableblockpos));
        if (flag1) {
            // The second branch isn't always sent exactly opposite the first any more - a perpendicular
            // or diagonal heading (still picked relative to directionA/B so it can't double back on the
            // first branch) makes multi-branch trees read as asymmetric instead of a mirrored pair.
            Direction secondA;
            Direction secondB;
            float roll = pRandom.nextFloat();
            if (roll < 0.4F) {
                secondA = directionA.getOpposite();
                secondB = directionB == null ? null : directionB.getOpposite();
            } else if (roll < 0.7F) {
                secondA = pRandom.nextBoolean() ? directionA.getClockWise() : directionA.getCounterClockWise();
                secondB = null;
            } else {
                secondA = directionA.getOpposite();
                secondB = pRandom.nextBoolean() ? directionA.getClockWise() : directionA.getCounterClockWise();
            }
            list.addAll(this.generateBranch(pLevel, pBlockSetter, pRandom, pPos, pConfig, secondA, secondB, j, j < l - 1, branchBEndHeight, l, blockpos$mutableblockpos));
        }

        return list;
    }

    /** Picks one of the two horizontal directions perpendicular to {@code pDirectionA}, so the pair forms a diagonal (e.g. NORTH+EAST). */
    private static Direction pickDiagonalPartner(RandomSource pRandom, Direction pDirectionA) {
        return pRandom.nextBoolean() ? pDirectionA.getClockWise() : pDirectionA.getCounterClockWise();
    }

    private static Function<BlockState, BlockState> axisSetter(Direction pDirection) {
        return (pState) -> pState.trySetValue(RotatedPillarBlock.AXIS, pDirection.getAxis());
    }

    /**
     * Walks a branch from the trunk out to a target point that may be offset diagonally
     * ({@code pDirectionB} non-null) as well as vertically, placing logs along the way and finishing
     * with a foliage attachment. Each step of the walk is chosen from whichever of the remaining X/Z/Y
     * deltas is largest (weighted by magnitude), which naturally reduces to the old single-axis +
     * vertical zig-zag when pDirectionB is null (its axis never accumulates any remaining delta).
     * Partway along the walk it may also sprout a short sub-branch and/or an extra foliage blob, so
     * a single main branch can read as several smaller leafy limbs rather than one bare rod with a
     * tuft on the end. The target height ({@code pEndHeight}) is capped at {@code pTrunkHeight - 1}
     * so a branch can never poke up above the central trunk's own canopy - placeTrunk already grows
     * the trunk to meet the branches' natural heights, so this only bites in the rare case that would
     * have pushed the trunk past the tree's overall height cap.
     */
    private List<FoliagePlacer.FoliageAttachment> generateBranch(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration pConfig, Direction pDirectionA, Direction pDirectionB, int pOffset, boolean pOffsetExtra, int pEndHeight, int pTrunkHeight, BlockPos.MutableBlockPos pPosMutable) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        pPosMutable.set(pPos).move(Direction.UP, pOffset);
        int i = Math.min(pEndHeight, pTrunkHeight - 1);
        boolean flag = pOffsetExtra || i < pOffset;
        int j = this.branchHorizontalLength.sample(pRandom) + (flag ? 1 : 0);
        BlockPos blockpos = pPos.relative(pDirectionA, j);
        if (pDirectionB != null) {
            blockpos = blockpos.relative(pDirectionB, j);
        }
        blockpos = blockpos.above(i);
        int k = flag ? 2 : 1;

        Function<BlockState, BlockState> propertySetterA = axisSetter(pDirectionA);
        for(int l = 0; l < k; ++l) {
            this.placeLog(pLevel, pBlockSetter, pRandom, pPosMutable.move(pDirectionA), pConfig, propertySetterA);
            if (pDirectionB != null) {
                this.placeLog(pLevel, pBlockSetter, pRandom, pPosMutable.move(pDirectionB), pConfig, axisSetter(pDirectionB));
            }
        }

        int totalSteps = pPosMutable.distManhattan(blockpos);
        int stepsTaken = 0;
        boolean forkedYet = false;

        while(true) {
            int dx = blockpos.getX() - pPosMutable.getX();
            int dy = blockpos.getY() - pPosMutable.getY();
            int dz = blockpos.getZ() - pPosMutable.getZ();
            int remaining = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
            if (remaining == 0) {
                attachments.add(new FoliagePlacer.FoliageAttachment(blockpos.above(), 0, false));
                return attachments;
            }

            float roll = pRandom.nextFloat() * (float)remaining;
            Direction step;
            Function<BlockState, BlockState> propertySetter;
            if (roll < (float)Math.abs(dx)) {
                step = dx > 0 ? Direction.EAST : Direction.WEST;
                propertySetter = axisSetter(step);
            } else if (roll < (float)(Math.abs(dx) + Math.abs(dz))) {
                step = dz > 0 ? Direction.SOUTH : Direction.NORTH;
                propertySetter = axisSetter(step);
            } else {
                step = dy > 0 ? Direction.UP : Direction.DOWN;
                propertySetter = Function.identity();
            }
            this.placeLog(pLevel, pBlockSetter, pRandom, pPosMutable.move(step), pConfig, propertySetter);
            ++stepsTaken;

            // Fork around the branch's midpoint, once, so the offshoot has enough of the branch left
            // ahead of it to read as a separate limb rather than sitting right on top of the tip.
            if (!forkedYet && totalSteps >= 4 && stepsTaken >= totalSteps / 2) {
                forkedYet = true;
                // Past 9 blocks a branch reads as too long and sparse without a blob to break it up.
                if (j > 9 || pRandom.nextFloat() < 0.45F) {
                    attachments.add(new FoliagePlacer.FoliageAttachment(pPosMutable.above().immutable(), 0, false));
                }
                if (pRandom.nextFloat() < 0.5F) {
                    attachments.addAll(this.generateSubBranch(pLevel, pBlockSetter, pRandom, pConfig, pPosMutable.immutable(), pDirectionA));
                }
            }
        }
    }

    /**
     * A shorter, un-forking offshoot from a point partway along a main branch: a couple of logs
     * heading off perpendicular to the parent branch, capped with their own foliage attachment.
     */
    private List<FoliagePlacer.FoliageAttachment> generateSubBranch(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, BlockPos pStart, Direction pParentDirection) {
        Direction direction = pRandom.nextBoolean() ? pParentDirection.getClockWise() : pParentDirection.getCounterClockWise();
        Function<BlockState, BlockState> propertySetter = axisSetter(direction);
        int length = 1 + pRandom.nextInt(3);
        BlockPos.MutableBlockPos cursor = pStart.mutable();
        for(int l = 0; l < length; ++l) {
            this.placeLog(pLevel, pBlockSetter, pRandom, cursor.move(direction), pConfig, propertySetter);
            if (pRandom.nextBoolean()) {
                this.placeLog(pLevel, pBlockSetter, pRandom, cursor.move(Direction.UP), pConfig, Function.identity());
            }
        }
        return List.of(new FoliagePlacer.FoliageAttachment(cursor.above().immutable(), 0, false));
    }
}
