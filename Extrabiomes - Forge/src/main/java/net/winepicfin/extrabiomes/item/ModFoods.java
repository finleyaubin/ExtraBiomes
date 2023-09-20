package net.winepicfin.extrabiomes.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties FROGS_LEGS = new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).meat().effect(()->new MobEffectInstance(MobEffects.JUMP,600,1),0.8F).build();
    public static final FoodProperties COOKED_FROGS_LEGS = new FoodProperties.Builder().nutrition(7).saturationMod(0.6f).meat().build();

}
