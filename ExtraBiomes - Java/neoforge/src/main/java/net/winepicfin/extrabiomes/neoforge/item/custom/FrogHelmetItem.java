package net.winepicfin.extrabiomes.neoforge.item.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.winepicfin.extrabiomes.entity.client.armour.FrogHelmetRenderer;
import net.winepicfin.extrabiomes.item.FrogHelmetEffects;
import net.winepicfin.extrabiomes.item.ModItemMaterials;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.function.Consumer;

// Lives in neoforge/ (not common/) for the same reason as forge/'s FrogHelmetItem - see
// platform/ExtraBiomesExpectPlatform#createFrogHelmetItem for how common constructs one. Unlike
// forge's IClientItemExtensions.initializeClient, this uses GeckoLib's own loader-agnostic
// GeoItem#createGeoRenderer/GeoRenderProvider hook, which NeoForge's GeckoLib build also
// consults (see GeckoLibClientNeoForge#getArmorModelForItem upstream).
public final class FrogHelmetItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ArmorMaterial material;
    public static final Map<ArmorMaterial, MobEffectInstance> MATERIAL_MOB_EFFECT_INSTANCE_MAP = (new ImmutableMap.Builder<ArmorMaterial, MobEffectInstance>()).put(ModItemMaterials.FROG, FrogHelmetEffects.playerWaterBreathing())
            .build();

    public FrogHelmetItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(properties.humanoidArmor(material, type));
        this.material = material;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (slot == EquipmentSlot.HEAD && entity instanceof Player player) {
            if (hasHelmetOn(player)) {
                evaluateArmorEffects(player);
            }
        }
    }

    private boolean hasHelmetOn(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
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
        ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);
        return helmetStack.getItem() instanceof FrogHelmetItem frogHelmet && frogHelmet.material == material;
    }

    private void addStatusEffectForMaterial(Player player, ArmorMaterial mapArmourMaterial, MobEffectInstance mapStatusEffect) {
        boolean hasPlayerEffect = player.hasEffect(mapStatusEffect.getEffect());
        if (hasFrogHelmetOn(mapArmourMaterial, player) && !hasPlayerEffect) {
            player.addEffect(new MobEffectInstance(mapStatusEffect));
            player.addEffect(FrogHelmetEffects.playerJumpBoost());
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?, ?> renderer;

            @Override
            public <S extends HumanoidRenderState> @Nullable GeoArmorRenderer<?, ?> getGeoArmorRenderer(@Nullable S renderState, ItemStack itemStack, EquipmentSlot equipmentSlot, EquipmentClientInfo.LayerType type, @Nullable HumanoidModel<S> original) {
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
