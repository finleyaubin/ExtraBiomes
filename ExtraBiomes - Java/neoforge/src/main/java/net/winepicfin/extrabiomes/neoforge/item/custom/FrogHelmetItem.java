package net.winepicfin.extrabiomes.neoforge.item.custom;

import com.google.common.collect.ImmutableMap;
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
import net.winepicfin.extrabiomes.entity.client.armour.FrogHelmetRenderer;
import net.winepicfin.extrabiomes.item.FrogHelmetEffects;
import net.winepicfin.extrabiomes.item.ModItemMaterials;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.function.Consumer;

// Lives in neoforge/ (not common/) for the same reason as forge/'s FrogHelmetItem - see
// platform/ExtraBiomesExpectPlatform#createFrogHelmetItem for how common constructs one. Unlike
// forge's IClientItemExtensions.initializeClient, this uses GeckoLib's own loader-agnostic
// GeoItem#createGeoRenderer/GeoRenderProvider hook, which NeoForge's GeckoLib build also
// consults (see GeckoLibClientNeoForge#getArmorModelForItem upstream).
public final class FrogHelmetItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ArmorMaterial material;
    public static final Map<ArmorMaterial, MobEffectInstance> MATERIAL_MOB_EFFECT_INSTANCE_MAP = (new ImmutableMap.Builder<ArmorMaterial, MobEffectInstance>()).put(ModItemMaterials.FROG, FrogHelmetEffects.playerWaterBreathing())
            .build();

    public FrogHelmetItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(material, type, properties);
        this.material = material;
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
        ItemStack helmetStack = player.getInventory().getArmor(3);
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
            private GeoArmorRenderer<?> renderer;

            @Override
            public <E extends LivingEntity, S extends HumanoidRenderState> @Nullable HumanoidModel<?> getGeoArmorRenderer(@Nullable E livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, EquipmentModel.LayerType type, HumanoidModel<S> original) {
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
