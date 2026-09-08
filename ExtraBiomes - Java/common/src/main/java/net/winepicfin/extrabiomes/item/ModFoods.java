package net.winepicfin.extrabiomes.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class ModFoods {
    public static final FoodProperties FROGS_LEGS = new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build();
    public static final Consumable FROGS_LEGS_CONSUMABLE = Consumable.builder()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.JUMP, 600, 1), 0.8F))
            .build();
    public static final FoodProperties COOKED_FROGS_LEGS = new FoodProperties.Builder().nutrition(7).saturationModifier(0.6f).build();
    public static final FoodProperties PIRANHA = new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build();
    public static final FoodProperties COOKED_PIRANHA = new FoodProperties.Builder().nutrition(5).saturationModifier(0.6f).build();
    public static final FoodProperties JELLYFISH_JAM = new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f).build();
    public static final Consumable JELLYFISH_JAM_CONSUMABLE = Consumable.builder()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 100, 0), 0.3F))
            .build();

}
