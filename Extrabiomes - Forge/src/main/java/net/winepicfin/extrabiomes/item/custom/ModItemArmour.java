package net.winepicfin.extrabiomes.item.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.item.ModItemMaterials;

import java.util.Map;


public class ModItemArmour extends ArmorItem {
    public static final Map<ArmorMaterial, MobEffectInstance> MATERIAL_MOB_EFFECT_INSTANCE_MAP = (new ImmutableMap.Builder<ArmorMaterial,MobEffectInstance>()).put(ModItemMaterials.FROG, new MobEffectInstance(MobEffects.WATER_BREATHING,200,1,false,false,true)).build();
    public ModItemArmour(ArmorMaterial p_40386_, Type p_266831_, Properties p_40388_) {
        super(p_40386_, p_266831_, p_40388_);
    }

    @Override
    public void onArmourTick(ItemStack stack, Level world, Player player){

        if (!world.isClientSide()){
            if(hasHelmetOn(player)){

            }
        }
    }
private boolean hasHelmetOn(Player player){
        ItemStack helmet = player.getInventory().getArmor(3);
        return !helmet.isEmpty();
    }
}
