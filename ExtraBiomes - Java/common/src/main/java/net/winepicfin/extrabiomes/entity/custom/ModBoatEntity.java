package net.winepicfin.extrabiomes.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

// Vanilla (pre-1.21.2) keys a boat's wood to the closed Boat.Type enum, which mods can't extend -
// this subclass never touches Boat.Type (it stays at its default OAK value) and instead supplies
// its own drop item directly, overriding the one vanilla method that reads it on normal pickup/break.
//
// Known gap: Boat#checkFallDamage() (fall damage > 3 on land) drops this.getVariant().getPlanks()
// directly rather than going through getDropItem(), so a boat destroyed that way still drops oak
// planks instead of this wood's planks. checkFallDamage() touches a private Boat field (lastYd)
// and a private status getter that a subclass can't reach without copying vanilla's whole method
// body, which isn't worth the risk of drifting from vanilla behavior for this rare edge case.
public class ModBoatEntity extends Boat {
    private final Supplier<Item> dropItem;

    public ModBoatEntity(EntityType<? extends Boat> type, Level level, Supplier<Item> dropItem) {
        super(type, level);
        this.dropItem = dropItem;
    }

    @Override
    public Item getDropItem() {
        return dropItem.get();
    }
}
