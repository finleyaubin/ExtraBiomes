package net.winepicfin.extrabiomes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Port of Bedrock's "extrabiomes:stick_pile" (packs/BP/blocks/stick_pile.json). Bedrock rotates
 * the whole model in 90-degree steps based on the placed block_face, always identically within
 * each opposite-face pair (north/south, east/west, up/down) - i.e. it only ever depends on
 * {@link Direction#getAxis()}, exactly like a log. See tools/convert_stick_pile_model.py for the
 * three baked per-axis models this reuses ({@code stick_pile_x/y/z.json}, RotatedPillarBlock's
 * default {@code getStateForPlacement} already sets AXIS from the placement face).
 */
public class StickPileBlock extends RotatedPillarBlock {
    public StickPileBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return StickPileTuning.FLAMMABILITY;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return StickPileTuning.FIRE_SPREAD_SPEED;
    }
}
