package net.winepicfin.extrabiomes.worldgen.tree.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.CaveVineFeature;

/**
 * Hangs {@link Blocks#CAVE_VINES}/{@link Blocks#CAVE_VINES_PLANT} chains beneath a mystic tree's
 * logs and leaves, matching the Bedrock structures (mystic_tree.mcstructure /
 * Large_mystic_tree.mcstructure) whose palette uses only cave vine variants for vine decoration -
 * no regular {@code minecraft:vine} block appears anywhere in either structure - unlike vanilla's
 * {@link net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator}/
 * {@link net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator} this
 * replaces, which both place regular side-attached vine blocks.
 * <p>
 * Age/berries weighting mirrors {@link CaveVineFeature}: head blocks get a 50/50 berries roll and a
 * uniform 0-{@link GrowingPlantHeadBlock#MAX_AGE} age, body blocks get a 1-in-5 berries roll.
 */
public class CaveVineTreeDecorator extends TreeDecorator {
    public static final MapCodec<CaveVineTreeDecorator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(d -> d.probability),
                    Codec.intRange(1, 32).fieldOf("max_length").forGetter(d -> d.maxLength)
            ).apply(instance, CaveVineTreeDecorator::new));

    private final float probability;
    private final int maxLength;

    public CaveVineTreeDecorator(float probability, int maxLength) {
        this.probability = probability;
        this.maxLength = maxLength;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.CAVE_VINE_TREE_DECORATOR.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        RandomSource random = context.random();
        context.logs().forEach(pos -> tryHang(context, random, pos));
        context.leaves().forEach(pos -> tryHang(context, random, pos));
    }

    private void tryHang(TreeDecorator.Context context, RandomSource random, BlockPos anchor) {
        if (random.nextFloat() >= this.probability) return;

        BlockPos.MutableBlockPos pos = anchor.below().mutable();
        if (!context.level().isStateAtPosition(pos, BlockBehaviour.BlockStateBase::isAir)) return;

        int length = 1 + random.nextInt(this.maxLength);
        for (int i = 0; i < length; i++) {
            if (!context.level().isStateAtPosition(pos, BlockBehaviour.BlockStateBase::isAir)) break;

            boolean isHead = i == length - 1;
            BlockState state;
            if (isHead) {
                state = Blocks.CAVE_VINES.defaultBlockState()
                        .setValue(CaveVines.BERRIES, random.nextInt(2) == 0)
                        .setValue(GrowingPlantHeadBlock.AGE, random.nextInt(GrowingPlantHeadBlock.MAX_AGE + 1));
            } else {
                state = Blocks.CAVE_VINES_PLANT.defaultBlockState().setValue(CaveVines.BERRIES, random.nextInt(5) == 0);
            }

            context.setBlock(pos, state);
            pos.move(Direction.DOWN);
        }
    }
}
