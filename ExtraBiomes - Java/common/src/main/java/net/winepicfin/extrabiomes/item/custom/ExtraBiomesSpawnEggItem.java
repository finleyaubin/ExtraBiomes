package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// Loader-agnostic stand-in for Forge's ForgeSpawnEggItem: vanilla's own SpawnEggItem constructor
// needs a resolved EntityType at construction time, which architectury's DeferredRegister can't
// guarantee (items and entities are independent deferred registries with no fixed firing order
// relative to each other). Overriding getType(CompoundTag) to resolve the entity type lazily -
// exactly what vanilla's own tag-based override hook exists for - sidesteps that ordering problem
// without needing any platform-specific type at all. The `null` passed to super() is safe because
// SpawnEggItem's own internals (spawnsCreature/dispenser behavior/etc.) all go through
// getType(CompoundTag), never the raw defaultType field.
public class ExtraBiomesSpawnEggItem extends SpawnEggItem {
    private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

    public ExtraBiomesSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> typeSupplier, int backgroundColor, int highlightColor, Item.Properties properties) {
        super(null, backgroundColor, highlightColor, properties);
        this.typeSupplier = typeSupplier;
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag tag) {
        if (tag != null && tag.contains("EntityTag", 10)) {
            CompoundTag entityTag = tag.getCompound("EntityTag");
            if (entityTag.contains("id", 8)) {
                return EntityType.byString(entityTag.getString("id")).orElseGet(typeSupplier::get);
            }
        }
        return typeSupplier.get();
    }
}
