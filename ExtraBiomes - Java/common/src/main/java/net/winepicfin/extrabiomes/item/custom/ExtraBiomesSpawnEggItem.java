package net.winepicfin.extrabiomes.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// `null` is passed to super() because DeferredRegister can't guarantee EntityType is resolved yet; the real type is resolved lazily via typeSupplier. getType()/requiredFeatures() are overridden because vanilla (unlike Forge's patched SpawnEggItem) reads the permanently-null `defaultType` field directly in requiredFeatures(), which otherwise NPEs whenever creative-tab contents rebuild.
public class ExtraBiomesSpawnEggItem extends SpawnEggItem {
    // Same null-super() consequence as above, but for pick-block instead of requiredFeatures():
    // vanilla's SpawnEggItem.byId() (which Mob#getPickResult() calls) only indexes eggs whose
    // super() type was non-null, so none of these are ever found that way either. ALL lets
    // Fabric's MobPickResultMixin resolve entity type -> egg item on demand, well after
    // registration has finished (typeSupplier.get() is unsafe only during construction).
    private static final List<ExtraBiomesSpawnEggItem> ALL = new ArrayList<>();

    private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

    public ExtraBiomesSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> typeSupplier, int backgroundColor, int highlightColor, Item.Properties properties) {
        super(null, backgroundColor, highlightColor, properties);
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
        CustomData customData = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
        if (!customData.isEmpty()) {
            CompoundTag entityTag = customData.copyTag();
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
