package net.winepicfin.extrabiomes.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;

// All nutrition/saturation values below mirror the matching Bedrock behaviour pack item in
// ExtraBiomes - Bedrock/packs/BP/items/ so the two platforms stay in parity.
public class ModFoods {
    public static final FoodProperties FROGS_LEGS = new FoodProperties.Builder().nutrition(2).saturationModifier(0.2f).build();
    public static final Consumable FROGS_LEGS_CONSUMABLE = Consumable.builder()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 600, 1), 0.8F))
            .build();
    public static final FoodProperties COOKED_FROGS_LEGS = new FoodProperties.Builder().nutrition(6).saturationModifier(0.7f).build();
    public static final FoodProperties PIRANHA = new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f).build();
    public static final FoodProperties COOKED_PIRANHA = new FoodProperties.Builder().nutrition(8).saturationModifier(0.7f).build();
    // Parity with the Bedrock pack (items/jellyfish_jam_bottle.json + scripts/items/drink_jellyfish_jam.js):
    // jam is a fairly filling drink, and drinking it clears poison instead of causing it.
    public static final FoodProperties JELLYFISH_JAM = new FoodProperties.Builder().nutrition(8).saturationModifier(0.8f).build();
    public static final Consumable JELLYFISH_JAM_CONSUMABLE = Consumable.builder()
            .onConsume(new RemoveStatusEffectsConsumeEffect(MobEffects.POISON))
            .build();

}
