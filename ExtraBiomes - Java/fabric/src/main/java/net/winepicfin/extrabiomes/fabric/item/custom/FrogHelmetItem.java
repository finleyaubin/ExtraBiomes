package net.winepicfin.extrabiomes.fabric.item.custom;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.winepicfin.extrabiomes.fabric.entity.client.armour.FrogHelmetRenderer;
import net.winepicfin.extrabiomes.item.FrogHelmetEffects;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

// Fabric equivalent of forge/.../item/custom/FrogHelmetItem.java - identical inventoryTick/effect
// logic, but the armor-renderer hook uses GeckoLib's own loader-agnostic
// createGeoRenderer(Consumer<GeoRenderProvider>)/getRenderProvider() (from SingletonGeoAnimatable)
// instead of Forge's IClientItemExtensions.initializeClient, since plain vanilla Item (Fabric's
// compile target) has no such method to override.
//
// ArmorMaterial is a plain (non-registry) record and 1.21.5 removed ArmorItem (armor is now
// Item.Properties#humanoidArmor), so identifying "is this a frog helmet" goes through an
// instanceof check instead of comparing ArmorMaterial identity.
public final class FrogHelmetItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public FrogHelmetItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(properties.humanoidArmor(material, type));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (slot == EquipmentSlot.HEAD && entity instanceof Player player) {
            evaluateArmorEffects(player);
        }
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
            private GeoArmorRenderer<?, ?> renderer;

            @Override
            public <S extends HumanoidRenderState> GeoArmorRenderer<?, ?> getGeoArmorRenderer(@Nullable S renderState, ItemStack itemStack, EquipmentSlot equipmentSlot, EquipmentClientInfo.LayerType type, @Nullable HumanoidModel<S> original) {
                if (this.renderer == null)
                    this.renderer = new FrogHelmetRenderer<>();

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
