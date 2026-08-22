package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// Loader-agnostic stand-in for Forge's ForgeSpawnEggItem: vanilla's own SpawnEggItem constructor
// needs a resolved EntityType at construction time, which architectury's DeferredRegister can't
// guarantee (items and entities are independent deferred registries with no fixed firing order
// relative to each other). `null` is passed to super() and the real type is resolved lazily via
// typeSupplier, by which point mod loading has finished and every registry has settled.
//
// Forge patches vanilla SpawnEggItem with a protected getDefaultType() override point for exactly
// this (see ForgeSpawnEggItem); plain vanilla - what this common module compiles against, so also
// what Fabric runs - has no such hook: SpawnEggItem#requiredFeatures() reads the private final
// `defaultType` field directly (confirmed via javap on the vanilla jar), which stays permanently
// null for every instance built through this class. That's a real crash, not a hypothetical: it
// NPEs in FeatureElement#isEnabled -> requiredFeatures -> null.requiredFeatures(), which fires for
// every item while building creative-tab contents (both at startup and whenever a player opens
// their inventory), because CreativeModeTab's contents rebuild calls requiredFeatures() on every
// registered item regardless of whether it's actually in that tab.
//
// Since the field itself can't be overridden, override requiredFeatures() as a method instead
// (vanilla's own version isn't final) so it never touches the null field at all - same fix as
// getType(CompoundTag) below, applied to the other vanilla entry point that needs a real type.
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
