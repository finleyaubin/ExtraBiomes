package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

// See ModBoatEntity for why this never touches the closed vanilla Boat.Type enum, including the
// same checkFallDamage() gap (inherited unchanged from Boat here).
public class ModChestBoatEntity extends ChestBoat {
    private final Supplier<Item> dropItem;

    public ModChestBoatEntity(EntityType<? extends Boat> type, Level level, Supplier<Item> dropItem) {
        super(type, level);
        this.dropItem = dropItem;
    }

    @Override
    public Item getDropItem() {
        return dropItem.get();
    }
}
