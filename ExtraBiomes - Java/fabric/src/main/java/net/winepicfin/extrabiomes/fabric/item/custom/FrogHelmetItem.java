package net.winepicfin.extrabiomes.fabric.item.custom;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.level.Level;
import net.winepicfin.extrabiomes.fabric.entity.client.armour.FrogHelmetRenderer;
import net.winepicfin.extrabiomes.item.FrogHelmetEffects;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

// Fabric equivalent of forge/.../item/custom/FrogHelmetItem.java - identical inventoryTick/effect
// logic, but the armor-renderer hook uses GeckoLib's own loader-agnostic
// createGeoRenderer(Consumer<GeoRenderProvider>)/getRenderProvider() (from SingletonGeoAnimatable)
// instead of Forge's IClientItemExtensions.initializeClient, since plain vanilla Item (Fabric's
// compile target) has no such method to override.
//
// ArmorMaterial is a plain (non-registry) record in 1.21.3 and ArmorItem no longer exposes its
// material, so identifying "is this a frog helmet" now goes through an instanceof check instead
// of comparing ArmorMaterial identity.
public final class FrogHelmetItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public FrogHelmetItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slotId, boolean isSelected) {
        if (!world.isClientSide() && slotId == 39 && entity instanceof Player player) {
            if (hasFrogHelmetOn(player)) {
                evaluateArmorEffects(player);
            }
        }
    }

    private boolean hasFrogHelmetOn(Player player) {
        return player.getInventory().getArmor(3).getItem() instanceof FrogHelmetItem;
    }

    private void evaluateArmorEffects(Player player) {
        MobEffectInstance waterBreathing = FrogHelmetEffects.playerWaterBreathing();
        if (!player.hasEffect(waterBreathing.getEffect())) {
            player.addEffect(waterBreathing);
            player.addEffect(FrogHelmetEffects.playerJumpBoost());
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <E extends LivingEntity, S extends HumanoidRenderState> HumanoidModel<?> getGeoArmorRenderer(E livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, EquipmentModel.LayerType type, HumanoidModel<S> original) {
                if (this.renderer == null)
                    this.renderer = new FrogHelmetRenderer();

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
