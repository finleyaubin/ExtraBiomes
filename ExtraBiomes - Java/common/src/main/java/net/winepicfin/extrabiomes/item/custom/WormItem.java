package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.custom.WormEntity;

// Bedrock parity: extrabiomes:worm is directly placeable from the item, not just via spawn egg - mirrors vanilla's bucket-of-fish placement flow.
public class WormItem extends Item {
    public WormItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        Direction clickedFace = context.getClickedFace();
        BlockPos spawnPos = clickedState.getCollisionShape(level, clickedPos).isEmpty()
                ? clickedPos
                : clickedPos.relative(clickedFace);

        WormEntity worm = ModEntities.WORM.get().create(serverLevel);
        if (worm == null) {
            return InteractionResult.FAIL;
        }

        float yaw = Mth.wrapDegrees(level.random.nextFloat() * 360.0F);
        worm.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, yaw, 0.0F);
        worm.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.BUCKET, null, null);
        serverLevel.addFreshEntity(worm);

        Player player = context.getPlayer();
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
