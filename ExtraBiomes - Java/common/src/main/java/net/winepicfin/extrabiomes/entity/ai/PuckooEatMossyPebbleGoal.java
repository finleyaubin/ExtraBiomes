package net.winepicfin.extrabiomes.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.block.custom.MossyPebbleBlock;
import net.winepicfin.extrabiomes.entity.custom.PuckooEntity;
import net.winepicfin.extrabiomes.item.ModItems;

// Ported from Bedrock extrabiomes:puckoo's "minecraft:behavior.eat_block", which chews a mossy
// pebble down a size tier at a time (large -> medium -> small -> air) and fires the
// extrabiomes:drop event to spit out a pebble item. Bedrock's component only fires on a block the
// mob is already standing at; this extends MoveToBlockGoal so a puckoo actually goes looking for
// one, the way vanilla mobs seek out sand/crops.
//
// Bedrock's three separate mossy-pebble blocks are one block with a SIZE property here, so
// "replace_block" is a decrement rather than a block swap.
public class PuckooEatMossyPebbleGoal extends MoveToBlockGoal {
    private static final double SPEED_MODIFIER = 1.0;
    private static final int SEARCH_RANGE = 12;
    private static final int VERTICAL_SEARCH_RANGE = 4;
    // Bedrock nibbles at a 1-2% per-tick success_chance; a flat chew time reads the same without
    // being able to strip a whole pile in a second once the puckoo is standing on it.
    private static final int CHEW_TICKS = 60;

    private final PuckooEntity puckoo;
    private int chewTicks;

    public PuckooEatMossyPebbleGoal(PuckooEntity puckoo) {
        super(puckoo, SPEED_MODIFIER, SEARCH_RANGE, VERTICAL_SEARCH_RANGE);
        this.puckoo = puckoo;
    }

    @Override
    public boolean canUse() {
        return !this.puckoo.isVehicle() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.puckoo.isVehicle() && super.canContinueToUse();
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.MOSSY_PEBBLE.get());
    }

    @Override
    public void start() {
        super.start();
        this.chewTicks = CHEW_TICKS;
    }

    @Override
    public void tick() {
        super.tick();
        this.puckoo.getLookControl().setLookAt(this.blockPos.getX() + 0.5, this.blockPos.getY(),
                this.blockPos.getZ() + 0.5);
        if (!this.isReachedTarget() || this.chewTicks-- > 0) {
            return;
        }
        this.chewTicks = CHEW_TICKS;
        this.eatOneTier();
    }

    private void eatOneTier() {
        Level level = this.puckoo.level();
        BlockState state = level.getBlockState(this.blockPos);
        if (!state.is(ModBlocks.MOSSY_PEBBLE.get())) {
            return;
        }
        int size = state.getValue(MossyPebbleBlock.SIZE);
        if (size > 1) {
            level.setBlockAndUpdate(this.blockPos, state.setValue(MossyPebbleBlock.SIZE, size - 1));
        } else {
            level.removeBlock(this.blockPos, false);
        }
        level.gameEvent(GameEvent.BLOCK_CHANGE, this.blockPos, GameEvent.Context.of(this.puckoo, state));
        this.puckoo.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
        this.puckoo.spawnAtLocation(new ItemStack(ModItems.PEBBLE.get()));
    }
}
