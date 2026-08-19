package net.winepicfin.extrabiomes.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties FROGS_LEGS = new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).meat().effect(()->new MobEffectInstance(MobEffects.JUMP,600,1),0.8F).build();
    public static final FoodProperties COOKED_FROGS_LEGS = new FoodProperties.Builder().nutrition(7).saturationMod(0.6f).meat().build();
    public static final FoodProperties PIRANHA = new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build();
    public static final FoodProperties COOKED_PIRANHA = new FoodProperties.Builder().nutrition(5).saturationMod(0.6f).build();
    public static final FoodProperties JELLYFISH_JAM = new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).effect(()->new MobEffectInstance(MobEffects.POISON,100,0),0.3F).build();

}
