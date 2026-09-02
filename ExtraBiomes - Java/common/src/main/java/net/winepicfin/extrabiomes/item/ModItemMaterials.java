package net.winepicfin.extrabiomes.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.util.ModTags;

import java.util.EnumMap;
import java.util.Map;

public class ModItemMaterials {
    // Durability/toughness/knockback match leather tier (the closest vanilla analog for this
    // cosmetic frog-leg armor); repair ingredient moved from an Ingredient supplier to a TagKey.
    // ArmorMaterial is a plain record in 1.21.3, no longer registry-backed.
    public static final ArmorMaterial FROG = buildFrogMaterial();

    private static ArmorMaterial buildFrogMaterial() {
        Map<ArmorType, Integer> defense = new EnumMap<>(ArmorType.class);
        defense.put(ArmorType.HELMET, 2);
        defense.put(ArmorType.CHESTPLATE, 6);
        defense.put(ArmorType.LEGGINGS, 5);
        defense.put(ArmorType.BOOTS, 2);
        defense.put(ArmorType.BODY, 4);
        return new ArmorMaterial(
                5,
                defense,
                15,
                Holder.direct(SoundEvents.FROGLIGHT_PLACE),
                0f,
                0f,
                ModTags.Items.REPAIRS_FROG_ARMOR,
                ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "frog")
        );
    }

    public static void register() {
    }
}
