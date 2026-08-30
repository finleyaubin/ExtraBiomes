package net.winepicfin.extrabiomes.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.winepicfin.extrabiomes.ExtraBiomes;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ModItemMaterials {
    public static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.ARMOR_MATERIAL);

    public static final RegistrySupplier<ArmorMaterial> FROG = MATERIALS.register("frog", () -> {
        Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.HELMET, 2);
        defense.put(ArmorItem.Type.CHESTPLATE, 6);
        defense.put(ArmorItem.Type.LEGGINGS, 5);
        defense.put(ArmorItem.Type.BOOTS, 2);
        defense.put(ArmorItem.Type.BODY, 4);
        return new ArmorMaterial(
                defense,
                15,
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.FROGLIGHT_PLACE),
                () -> Ingredient.of(ModItems.FROGS_LEGS.get()),
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "frog"))),
                0f,
                0f
        );
    });

    public static void register() {
        MATERIALS.register();
    }
}
