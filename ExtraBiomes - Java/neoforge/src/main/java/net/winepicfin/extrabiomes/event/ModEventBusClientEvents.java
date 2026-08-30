package net.winepicfin.extrabiomes.event;

import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.winepicfin.extrabiomes.ExtraBiomes;
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

@EventBusSubscriber(modid = ExtraBiomes.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_SIGN.get(), SignRenderer::new);
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
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        EntityRenderer<?> wolfRenderer = event.getRenderer(EntityType.WOLF);
        if (wolfRenderer instanceof WolfRenderer renderer) {
            renderer.addLayer(new WolfFrogHatLayer(renderer));
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
