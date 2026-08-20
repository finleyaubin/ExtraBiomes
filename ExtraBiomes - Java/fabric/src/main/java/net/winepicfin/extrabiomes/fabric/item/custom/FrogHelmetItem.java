package net.winepicfin.extrabiomes.fabric.item.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.fabric.entity.client.armour.FrogHelmetRenderer;
import net.winepicfin.extrabiomes.item.ModItemMaterials;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

// Fabric equivalent of forge/.../item/custom/FrogHelmetItem.java - identical inventoryTick/effect
// logic, but the armor-renderer hook uses GeckoLib's own loader-agnostic
// createRenderer(Consumer<Object>)/getRenderProvider() (see GeoItem.makeRenderer) instead of
// Forge's IClientItemExtensions.initializeClient, since plain vanilla Item (Fabric's compile
// target) has no such method to override.
public final class FrogHelmetItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    public static final Map<ArmorMaterial, MobEffectInstance> MATERIAL_MOB_EFFECT_INSTANCE_MAP = (new ImmutableMap.Builder<ArmorMaterial, MobEffectInstance>()).put(ModItemMaterials.FROG, new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 1, false, false, true))
            .build();

    public FrogHelmetItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slotId, boolean isSelected) {
        if (!world.isClientSide() && slotId == 39 && entity instanceof Player player) {
            if (hasHelmetOn(player)) {
                evaluateArmorEffects(player);
            }
        }
    }

    private boolean hasHelmetOn(Player player) {
        ItemStack helmet = player.getInventory().getArmor(3);
        return !helmet.isEmpty();
    }

    private void evaluateArmorEffects(Player player) {
        for (Map.Entry<ArmorMaterial, MobEffectInstance> entry : MATERIAL_MOB_EFFECT_INSTANCE_MAP.entrySet()) {
            ArmorMaterial mapArmourMaterial = entry.getKey();
            MobEffectInstance mapStatusEffect = entry.getValue();
            if (hasFrogHelmetOn(mapArmourMaterial, player)) {
                addStatusEffectForMaterial(player, mapArmourMaterial, mapStatusEffect);
            }
        }
    }

    private boolean hasFrogHelmetOn(ArmorMaterial material, Player player) {
        ArmorItem helmet = ((ArmorItem) player.getInventory().getArmor(3).getItem());
        return helmet.getMaterial() == material;
    }

    private void addStatusEffectForMaterial(Player player, ArmorMaterial mapArmourMaterial, MobEffectInstance mapStatusEffect) {
        boolean hasPlayerEffect = player.hasEffect(mapStatusEffect.getEffect());
        if (hasFrogHelmetOn(mapArmourMaterial, player) && !hasPlayerEffect) {
            player.addEffect(new MobEffectInstance(mapStatusEffect));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 1, false, false, true));
        }
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public net.minecraft.client.model.HumanoidModel<net.minecraft.world.entity.LivingEntity> getHumanoidArmorModel(net.minecraft.world.entity.LivingEntity livingEntity, ItemStack itemStack, net.minecraft.world.entity.EquipmentSlot equipmentSlot, net.minecraft.client.model.HumanoidModel<net.minecraft.world.entity.LivingEntity> original) {
                if (this.renderer == null)
                    this.renderer = new FrogHelmetRenderer();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return (net.minecraft.client.model.HumanoidModel<net.minecraft.world.entity.LivingEntity>) this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
