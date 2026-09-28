package net.winepicfin.extrabiomes.worldgen.features.undergroundjungle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Approximation of Bedrock's built-in {@code minecraft:optional_fallen_jungle_tree_feature}
 * (referenced by {@code select_moss_or_jungle_tree_feature.json} / {@code _upper}), which has no
 * JSON body shipped with the game to port exactly (it is one of vanilla Bedrock's internal hard-coded
 * features). This lays down a short horizontal line of jungle logs starting at the placement origin,
 * choosing a random horizontal direction and length, and stops early (an "optional"/partial fallen
 * log) if a step would not be fully supported by solid ground or would overlap a non-air block -
 * mirroring the "optional" naming's implication that the feature gracefully no-ops/truncates rather
 * than failing outright.
 */
public class FallenJungleTreeFeature implements Feature {

    private static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 6;

    public static final MapCodec<FallenJungleTreeFeature> CODEC = MapCodec.unit(FallenJungleTreeFeature::new);

    @Override
    public MapCodec<FallenJungleTreeFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        Direction direction = HORIZONTAL_DIRECTIONS[random.nextInt(HORIZONTAL_DIRECTIONS.length)];
        Direction.Axis axis = direction.getAxis();
        int length = MIN_LENGTH + random.nextInt(MAX_LENGTH - MIN_LENGTH + 1);

        boolean placedAny = false;
        BlockPos.MutableBlockPos pos = origin.mutable();
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                pos.move(direction);
            }
            if (!level.isEmptyBlock(pos) || !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
                break;
            }
            level.setBlock(pos, Blocks.JUNGLE_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis), 2);
            placedAny = true;
        }

        return placedAny;
    }
}
