package net.winepicfin.extrabiomes.event;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
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

    // NeoForge 21.4 removed the datagen-time "render_type" blockstate/model JSON field along with
    // the BlockStateProvider that wrote it (see ModBlockStateProvider's header comment) - the
    // runtime-registration replacement is this same ItemBlockRenderTypes API Fabric's
    // BlockRenderLayerMap already wraps (see ExtraBiomesFabricClient for the identical block list).
    // Without this, saplings/mushrooms/leaves/doors/trapdoors default to RenderType.solid() and their
    // texture's transparent pixels render as opaque black instead of being cut out.
    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var block : new net.minecraft.world.level.block.Block[]{
                    ModBlocks.MYSTIC_SAPLING.get(), ModBlocks.SKY_SAPLING.get(), ModBlocks.PALM_SAPLING.get(),
                    ModBlocks.BLACK_MUSHROOM.get(), ModBlocks.BLUE_MUSHROOM.get(), ModBlocks.CYAN_MUSHROOM.get(),
                    ModBlocks.GREEN_MUSHROOM.get(), ModBlocks.ORANGE_MUSHROOM.get(), ModBlocks.PURPLE_MUSHROOM.get(),
                    ModBlocks.WHITE_MUSHROOM.get(), ModBlocks.YELLOW_MUSHROOM.get(), ModBlocks.GLOW_MUSHROOM.get(),
                    ModBlocks.BLACK_MUSHROOM_BLOCK.get(), ModBlocks.BLUE_MUSHROOM_BLOCK.get(), ModBlocks.CYAN_MUSHROOM_BLOCK.get(),
                    ModBlocks.GREEN_MUSHROOM_BLOCK.get(), ModBlocks.ORANGE_MUSHROOM_BLOCK.get(), ModBlocks.PURPLE_MUSHROOM_BLOCK.get(),
                    ModBlocks.WHITE_MUSHROOM_BLOCK.get(), ModBlocks.YELLOW_MUSHROOM_BLOCK.get(), ModBlocks.GLOW_MUSHROOM_BLOCK.get(),
                    ModBlocks.MYSTIC_LEAVES.get(), ModBlocks.SKY_LEAVES.get(), ModBlocks.PALM_LEAVES.get(),
                    ModBlocks.MYSTIC_DOOR.get(), ModBlocks.SKY_DOOR.get(), ModBlocks.PALM_DOOR.get(), ModBlocks.GILDED_SKY_DOOR.get(),
                    ModBlocks.MYSTIC_TRAPDOOR.get(), ModBlocks.SKY_TRAPDOOR.get(), ModBlocks.PALM_TRAPDOOR.get(), ModBlocks.GILDED_SKY_TRAPDOOR.get()}) {
                ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
            }
        });
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
