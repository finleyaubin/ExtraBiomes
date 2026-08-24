package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// `null` is passed to super() because DeferredRegister can't guarantee EntityType is resolved yet; the real type is resolved lazily via typeSupplier. getType()/requiredFeatures() are overridden because vanilla (unlike Forge's patched SpawnEggItem) reads the permanently-null `defaultType` field directly in requiredFeatures(), which otherwise NPEs whenever creative-tab contents rebuild.
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

    @Override
    public FeatureFlagSet requiredFeatures() {
        return typeSupplier.get().requiredFeatures();
    }
}
