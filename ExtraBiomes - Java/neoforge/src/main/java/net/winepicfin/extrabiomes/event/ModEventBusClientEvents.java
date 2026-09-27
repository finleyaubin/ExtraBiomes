package net.winepicfin.extrabiomes.event;

import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSources;
import net.winepicfin.extrabiomes.neoforge.fluid.ModFluids;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.neoforge.fluid.BaseFluidType;
import net.winepicfin.extrabiomes.neoforge.fluid.ModFluidTypes;
import net.winepicfin.extrabiomes.entity.ModBlockEntities;
import net.winepicfin.extrabiomes.entity.client.BaitModel;
import net.winepicfin.extrabiomes.entity.client.GiantTortoiseModel;
import net.winepicfin.extrabiomes.entity.client.HarpyModel;
import net.winepicfin.extrabiomes.entity.client.HoppleshroomModel;
import net.winepicfin.extrabiomes.entity.client.JellyfishModel;
import net.winepicfin.extrabiomes.entity.client.ModModelLayers;
import net.winepicfin.extrabiomes.entity.client.PiranhaModel;
import net.winepicfin.extrabiomes.entity.client.TreefrogModel;
import net.winepicfin.extrabiomes.entity.client.WormModel;
import net.winepicfin.extrabiomes.entity.client.layers.PuckooBaseModelLayers;
import net.winepicfin.extrabiomes.entity.client.PuckooModel;
import net.winepicfin.extrabiomes.neoforge.entity.client.layers.WolfFrogHatLayer;

@EventBusSubscriber(modid = ExtraBiomes.MOD_ID, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_SIGN.get(), StandingSignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PuckooBaseModelLayers.PUCKOO_BASE_LAYER, PuckooModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.WORM, WormModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.TREEFROG, TreefrogModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.HOPPLESHROOM, HoppleshroomModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.GIANT_TORTOISE, GiantTortoiseModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.JELLYFISH, JellyfishModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.PIRANHA, PiranhaModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.HARPY, HarpyModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.BAIT, BaitModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.MYSTIC_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(ModModelLayers.MYSTIC_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(ModModelLayers.PALM_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(ModModelLayers.PALM_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(ModModelLayers.SKY_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(ModModelLayers.SKY_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(ModModelLayers.GILDED_SKY_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(ModModelLayers.GILDED_SKY_CHEST_BOAT, BoatModel::createChestBoatModel);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        WolfRenderer wolfRenderer = event.getRenderer(EntityType.WOLF);
        if (wolfRenderer != null) {
            wolfRenderer.addLayer(new WolfFrogHatLayer(wolfRenderer));
        }
    }

    @SubscribeEvent
    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(WolfRenderer.class, (Wolf wolf, WolfRenderState state) -> state.setRenderData(WolfFrogHatLayer.WOLF, wolf));
    }

    @SubscribeEvent
    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        if (ModFluidTypes.GOO_FLUID_TYPE.get() instanceof BaseFluidType goo) {
            event.register(new FluidModel.Unbaked(
                    new Material(goo.getStillTextureId(), true),
                    new Material(goo.getFlowingTextureId(), true),
                    new Material(goo.getOverlayTextureId(), true),
                    FluidTintSources.constant(goo.getTintColour())), ModFluids.SOURCE_GOO, ModFluids.FLOWING_GOO);
        }
    }

    // FluidType lost its own initializeClient(Consumer) hook - client extensions for fluid types
    // (as well as blocks/items/mob effects) all register centrally here instead.
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        if (ModFluidTypes.GOO_FLUID_TYPE.get() instanceof BaseFluidType gooFluidType) {
            event.registerFluidType(gooFluidType, gooFluidType);
        }
    }

    // createSpawnEggItem eagerly resolves a real EntityType and constructs a plain vanilla
    // SpawnEggItem (see platform/neoforge/ExtraBiomesExpectPlatformImpl#createSpawnEggItem), so
    // these 8 items are in SpawnEggItem.eggs() and vanilla's own ItemColors.createDefault()
    // tints them correctly (it wraps in ARGB32.opaque() - our background/highlight colors are
    // written as bare 0xRRGGBB literals with a zero alpha byte, so a manual registration here
    // that just forwards getColor(layer) without forcing alpha opaque overrides vanilla's
    // correct mapping with a fully-transparent one, rendering the eggs invisible).
}
