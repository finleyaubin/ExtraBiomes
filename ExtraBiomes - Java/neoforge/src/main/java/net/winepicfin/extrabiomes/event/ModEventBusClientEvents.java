package net.winepicfin.extrabiomes.event;

import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.ModBlockEntities;
import net.winepicfin.extrabiomes.item.ModItems;
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

@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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

    // Vanilla's own ItemColors.createDefault() tints every spawn egg by iterating
    // SpawnEggItem.eggs() (backed by SpawnEggItem's private static BY_ID map), and
    // SpawnEggItem's constructor only adds itself to that map when its EntityType argument is
    // non-null. ExtraBiomesSpawnEggItem always passes null to super() (see that class's own doc
    // comment - architectury's DeferredRegister can't guarantee a resolved EntityType at
    // construction time), so none of this mod's 8 spawn eggs are ever in SpawnEggItem.eggs(),
    // and vanilla never registers a tint for them - they render fully untinted (plain white)
    // instead of their background/highlight colors. Registering each one's ItemColor directly
    // here bypasses SpawnEggItem.eggs() entirely and fixes this regardless of that map.
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        net.minecraft.client.color.item.ItemColor spawnEggColor = (stack, layer) -> ((SpawnEggItem) stack.getItem()).getColor(layer);
        event.register(spawnEggColor,
                ModItems.PUCKOO_SPAWN_EGG.get(),
                ModItems.WORM_SPAWN_EGG.get(),
                ModItems.TREEFROG_SPAWN_EGG.get(),
                ModItems.HOPPLESHROOM_SPAWN_EGG.get(),
                ModItems.GIANT_TORTOISE_SPAWN_EGG.get(),
                ModItems.JELLYFISH_SPAWN_EGG.get(),
                ModItems.PIRANHA_SPAWN_EGG.get(),
                ModItems.HARPY_SPAWN_EGG.get());
    }
}
