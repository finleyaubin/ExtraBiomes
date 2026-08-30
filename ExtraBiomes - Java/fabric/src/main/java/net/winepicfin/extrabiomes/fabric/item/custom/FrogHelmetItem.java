package net.winepicfin.extrabiomes.fabric.item.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.fabric.entity.client.armour.FrogHelmetRenderer;
import net.winepicfin.extrabiomes.item.FrogHelmetEffects;
import net.winepicfin.extrabiomes.item.ModItemMaterials;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.function.Consumer;

// Fabric equivalent of forge/.../item/custom/FrogHelmetItem.java - identical inventoryTick/effect
// logic, but the armor-renderer hook uses GeckoLib's own loader-agnostic
// createGeoRenderer(Consumer<GeoRenderProvider>)/getRenderProvider() (from SingletonGeoAnimatable)
// instead of Forge's IClientItemExtensions.initializeClient, since plain vanilla Item (Fabric's
// compile target) has no such method to override.
public final class FrogHelmetItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final Map<Holder<ArmorMaterial>, MobEffectInstance> MATERIAL_MOB_EFFECT_INSTANCE_MAP = (new ImmutableMap.Builder<Holder<ArmorMaterial>, MobEffectInstance>()).put(BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(ModItemMaterials.FROG.get()), FrogHelmetEffects.playerWaterBreathing())
            .build();

    public FrogHelmetItem(ArmorMaterial material, Type type, Properties properties) {
        super(BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(material), type, properties);
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
        for (Map.Entry<Holder<ArmorMaterial>, MobEffectInstance> entry : MATERIAL_MOB_EFFECT_INSTANCE_MAP.entrySet()) {
            Holder<ArmorMaterial> mapArmourMaterial = entry.getKey();
            MobEffectInstance mapStatusEffect = entry.getValue();
            if (hasFrogHelmetOn(mapArmourMaterial, player)) {
                addStatusEffectForMaterial(player, mapArmourMaterial, mapStatusEffect);
            }
        }
    }

    private boolean hasFrogHelmetOn(Holder<ArmorMaterial> material, Player player) {
        ArmorItem helmet = ((ArmorItem) player.getInventory().getArmor(3).getItem());
        return helmet.getMaterial() == material;
    }

    private void addStatusEffectForMaterial(Player player, Holder<ArmorMaterial> mapArmourMaterial, MobEffectInstance mapStatusEffect) {
        boolean hasPlayerEffect = player.hasEffect(mapStatusEffect.getEffect());
        if (hasFrogHelmetOn(mapArmourMaterial, player) && !hasPlayerEffect) {
            player.addEffect(new MobEffectInstance(mapStatusEffect));
            player.addEffect(FrogHelmetEffects.playerJumpBoost());
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends net.minecraft.world.entity.LivingEntity> net.minecraft.client.model.HumanoidModel<?> getGeoArmorRenderer(T livingEntity, ItemStack itemStack, net.minecraft.world.entity.EquipmentSlot equipmentSlot, net.minecraft.client.model.HumanoidModel<T> original) {
                if (this.renderer == null)
                    this.renderer = new FrogHelmetRenderer();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
