package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.custom.JellyfishEntity;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class JellyfishingNetItem extends Item {
    public JellyfishingNetItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            JellyfishEntity jellyfish = ModEntities.JELLYFISH.get().create(serverLevel);
            if (jellyfish != null) {
                jellyfish.moveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                        context.getClickedFace() == Direction.UP ? 0.0F : 0.0F, 0.0F);
                jellyfish.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(pos), MobSpawnType.BUCKET, null);
                serverLevel.addFreshEntity(jellyfish);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
                if (player != null) {
                    player.awardStat(Stats.ITEM_USED.get(this));
                    if (!player.getAbilities().instabuild) {
                        context.getItemInHand().shrink(1);
                        ItemStack emptyNet = new ItemStack(ModItems.JELLYFISHING_NET_EMPTY.get());
                        if (!player.getInventory().add(emptyNet)) {
                            player.drop(emptyNet, false);
                        }
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, net.minecraft.world.entity.LivingEntity target, InteractionHand hand) {
        return InteractionResult.PASS;
    }
}
