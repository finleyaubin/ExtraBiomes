package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

// Vanilla's own BoatItem is hardcoded to construct vanilla Boat/ChestBoat with EntityType.BOAT/
// EntityType.CHEST_BOAT - it can't be reused for a custom EntityType, so this is a copy of its
// use() body with getBoat() replaced by entityType.get().create(level), which invokes whatever
// EntityFactory that EntityType was registered with (see ModEntities.registerBoat/registerChestBoat).
public class ModBoatItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private final Supplier<? extends EntityType<? extends Boat>> entityType;

    public ModBoatItem(Supplier<? extends EntityType<? extends Boat>> entityType, Properties properties) {
        super(properties);
        this.entityType = entityType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        HitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack);
        }

        Vec3 viewVector = player.getViewVector(1.0F);
        List<Entity> nearby = level.getEntities(player, player.getBoundingBox().expandTowards(viewVector.scale(5.0)).inflate(1.0), ENTITY_PREDICATE);
        if (!nearby.isEmpty()) {
            Vec3 eyePosition = player.getEyePosition();
            for (Entity entity : nearby) {
                AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
                if (aabb.contains(eyePosition)) {
                    return InteractionResultHolder.pass(itemStack);
                }
            }
        }

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        }

        Boat boat = entityType.get().create(level);
        Vec3 location = hitResult.getLocation();
        boat.setPos(location.x, location.y, location.z);
        if (level instanceof ServerLevel serverLevel) {
            EntityType.<Boat>createDefaultStackConfig(serverLevel, itemStack, player).accept(boat);
        }
        boat.setYRot(player.getYRot());
        if (!level.noCollision(boat, boat.getBoundingBox())) {
            return InteractionResultHolder.fail(itemStack);
        }

        if (!level.isClientSide) {
            level.addFreshEntity(boat);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, hitResult.getLocation());
            itemStack.consume(1, player);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
