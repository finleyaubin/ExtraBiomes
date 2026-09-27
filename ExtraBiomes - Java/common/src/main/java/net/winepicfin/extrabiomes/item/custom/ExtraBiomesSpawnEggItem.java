package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// vanilla's SpawnEggItem constructor no longer takes an EntityType at all, so getType()/requiredFeatures() are overridden to resolve typeSupplier lazily instead.
public class ExtraBiomesSpawnEggItem extends SpawnEggItem {
    // vanilla's SpawnEggItem.byId() (which Mob#getPickResult() calls) only indexes eggs registered
    // through Item.Properties#spawnEgg(EntityType), which this class deliberately doesn't use since
    // typeSupplier.get() is unsafe during construction. ALL lets Fabric's MobPickResultMixin resolve
    // entity type -> egg item on demand, well after registration has finished.
    private static final List<ExtraBiomesSpawnEggItem> ALL = new ArrayList<>();

    private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

    public ExtraBiomesSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> typeSupplier, int backgroundColor, int highlightColor, Item.Properties properties) {
        // backgroundColor/highlightColor no longer go through the Item itself in 1.21.4 - egg colors
        // are now item-model tints (see the generated spawn egg item model JSON), so they're unused here.
        super(properties);
        this.typeSupplier = typeSupplier;
        ALL.add(this);
    }

    @Nullable
    public static ExtraBiomesSpawnEggItem byType(EntityType<?> type) {
        for (ExtraBiomesSpawnEggItem egg : ALL) {
            if (egg.typeSupplier.get() == type) {
                return egg;
            }
        }
        return null;
    }

    @Override
    public EntityType<?> getType(ItemStack stack) {
        if (stack == null) {
            return typeSupplier.get();
        }
        TypedEntityData<EntityType<?>> entityData = stack.get(DataComponents.ENTITY_DATA);
        if (entityData != null) {
            return entityData.type();
        }
        return typeSupplier.get();
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return typeSupplier.get().requiredFeatures();
    }
}
