package net.winepicfin.extrabiomes.item.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.winepicfin.extrabiomes.entity.client.armour.FrogHelmetRenderer;
import net.winepicfin.extrabiomes.item.ModItemMaterials;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.function.Consumer;


public class FrogHelmetItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final Map<ArmorMaterial, MobEffectInstance> MATERIAL_MOB_EFFECT_INSTANCE_MAP = (new ImmutableMap.Builder<ArmorMaterial, MobEffectInstance>()).put(ModItemMaterials.FROG, new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 1, false, false, true ))
             .build();

    public FrogHelmetItem(ArmorMaterial p_40386_, Type p_266831_, Properties p_40388_) {
        super(p_40386_, p_266831_, p_40388_);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (!world.isClientSide()) {
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
            MobEffectInstance mapStatusEffect= entry.getValue();
            if (hasFrogHelmetOn(mapArmourMaterial, player) ){
                addStatusEffectForMaterial(player, mapArmourMaterial, mapStatusEffect);
            }
        }
    }
    private boolean hasFrogHelmetOn(ArmorMaterial material, Player player) {
        ArmorItem helmet = ((ArmorItem) player.getInventory().getArmor(3).getItem());
        return helmet.getMaterial() == material;

    }

    private void addStatusEffectForMaterial(Player player, ArmorMaterial mapArmourMaterial, MobEffectInstance mapStatusEffect){
        boolean hasPlayerEffect=player.hasEffect(mapStatusEffect.getEffect());
        if(hasFrogHelmetOn(mapArmourMaterial,player)&&!hasPlayerEffect){
            player.addEffect(new MobEffectInstance(mapStatusEffect));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP,200, 1, false, false, true ));
        }
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private FrogHelmetRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new ExampleArmorRenderer();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}


