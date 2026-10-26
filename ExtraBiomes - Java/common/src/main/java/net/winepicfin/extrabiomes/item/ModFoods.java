package net.winepicfin.extrabiomes.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

// All nutrition/saturation values below mirror the matching Bedrock behaviour pack item in
// ExtraBiomes - Bedrock/packs/BP/items/ so the two platforms stay in parity.
public class ModFoods {
    public static final FoodProperties FROGS_LEGS = new FoodProperties.Builder().nutrition(2).saturationModifier(0.2f).effect(new MobEffectInstance(MobEffects.JUMP,600,1),0.8F).build();
    public static final FoodProperties COOKED_FROGS_LEGS = new FoodProperties.Builder().nutrition(6).saturationModifier(0.7f).build();
    public static final FoodProperties PIRANHA = new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f).build();
    public static final FoodProperties COOKED_PIRANHA = new FoodProperties.Builder().nutrition(8).saturationModifier(0.7f).build();
    // Drinking the jam clears poison instead of causing it (see JellyfishJamBottleItem), matching Bedrock.
    public static final FoodProperties JELLYFISH_JAM = new FoodProperties.Builder().nutrition(8).saturationModifier(0.8f).build();

}
