package net.winepicfin.extrabiomes.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.EntityTypes;
import net.winepicfin.extrabiomes.entity.ModBlockEntities;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.client.BaitModel;
import net.winepicfin.extrabiomes.entity.client.BaitRenderer;
import net.winepicfin.extrabiomes.entity.client.GiantTortoiseModel;
import net.winepicfin.extrabiomes.entity.client.GiantTortoiseRenderer;
import net.winepicfin.extrabiomes.entity.client.HarpyModel;
import net.winepicfin.extrabiomes.entity.client.HarpyRenderer;
import net.winepicfin.extrabiomes.entity.client.HoppleshroomModel;
import net.winepicfin.extrabiomes.entity.client.HoppleshroomRenderer;
import net.winepicfin.extrabiomes.entity.client.JellyfishModel;
import net.winepicfin.extrabiomes.entity.client.JellyfishRenderer;
import net.winepicfin.extrabiomes.entity.client.ModModelLayers;
import net.winepicfin.extrabiomes.entity.client.PiranhaModel;
import net.winepicfin.extrabiomes.entity.client.PiranhaRenderer;
import net.winepicfin.extrabiomes.entity.client.PuckooModel;
import net.winepicfin.extrabiomes.entity.client.PuckooRenderer;
import net.winepicfin.extrabiomes.entity.client.RazorFeatherRenderer;
import net.winepicfin.extrabiomes.entity.client.TreefrogModel;
import net.winepicfin.extrabiomes.entity.client.TreefrogRenderer;
import net.winepicfin.extrabiomes.entity.client.WormModel;
import net.winepicfin.extrabiomes.entity.client.WormRenderer;
import net.winepicfin.extrabiomes.entity.client.layers.PuckooBaseModelLayers;
import net.winepicfin.extrabiomes.fabric.entity.client.layers.WolfFrogHatLayer;
import net.winepicfin.extrabiomes.fabric.fluid.GooFluid;
import net.winepicfin.extrabiomes.fabric.fluid.ModFluids;

// Fabric equivalent of forge/.../forge/ExtraBiomesForge.java's ClientModEvents inner class. See
// that class for the Forge-side registration list this mirrors. The fluid render layer/textures
// are the one genuinely different piece - Forge's ItemBlockRenderTypes.setRenderLayer(Fluid,...)
// and IClientFluidTypeExtensions (see forge/.../fluid/BaseFluidType.java) have no Fabric
// equivalent; BlockRenderLayerMap + FluidRenderHandlerRegistry are Fabric API's replacements (no
// per-fluid fog customization equivalent exists on Fabric, so that part of BaseFluidType is
// dropped here).
public class ExtraBiomesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // No Fabric equivalent of Forge's Sheets.addWoodType is needed here: Fabric API's
        // WoodTypeRegistry-backed registration (see platform/fabric/ExtraBiomesExpectPlatformImpl)
        // runs during mod init, before Sheets' own static sign/hanging-sign material maps are
        // built (they're populated lazily off WoodType.values() the first time Sheets is touched,
        // which happens no earlier than resource/model reload), so our wood types are already
        // present by then.

        // BlockRenderLayerMap (both the fluid-translucency and block-cutout calls formerly here)
        // is gone in 26.1 - chunk render layer is now derived automatically from sprite
        // transparency, so there's nothing left to register.
        FluidRenderingRegistry.register(ModFluids.SOURCE_GOO.get(), ModFluids.FLOWING_GOO.get(),
                new FluidModel.Unbaked(
                        new Material(GooFluid.STILL_TEXTURE, true),
                        new Material(GooFluid.FLOWING_TEXTURE, true),
                        new Material(GooFluid.OVERLAY_TEXTURE, true),
                        BlockTintSources.constant(0xFFFFFFFF)));

        registerBlockEntityRenderer(ModBlockEntities.MOD_SIGN.get(), StandingSignRenderer::new);
        registerBlockEntityRenderer(ModBlockEntities.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);

        ModelLayerRegistry.registerModelLayer(PuckooBaseModelLayers.PUCKOO_BASE_LAYER, PuckooModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.WORM, WormModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.TREEFROG, TreefrogModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.HOPPLESHROOM, HoppleshroomModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.GIANT_TORTOISE, GiantTortoiseModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.JELLYFISH, JellyfishModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.PIRANHA, PiranhaModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.HARPY, HarpyModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.BAIT, BaitModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.MYSTIC_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.MYSTIC_CHEST_BOAT, BoatModel::createChestBoatModel);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.PALM_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.PALM_CHEST_BOAT, BoatModel::createChestBoatModel);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.SKY_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.SKY_CHEST_BOAT, BoatModel::createChestBoatModel);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.GILDED_SKY_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.GILDED_SKY_CHEST_BOAT, BoatModel::createChestBoatModel);

        EntityRendererRegistry.register(ModEntities.PUCKOO.get(), PuckooRenderer::new);
        EntityRendererRegistry.register(ModEntities.WORM.get(), WormRenderer::new);
        EntityRendererRegistry.register(ModEntities.TREEFROG.get(), TreefrogRenderer::new);
        EntityRendererRegistry.register(ModEntities.HOPPLESHROOM.get(), HoppleshroomRenderer::new);
        EntityRendererRegistry.register(ModEntities.GIANT_TORTOISE.get(), GiantTortoiseRenderer::new);
        EntityRendererRegistry.register(ModEntities.JELLYFISH.get(), JellyfishRenderer::new);
        EntityRendererRegistry.register(ModEntities.PIRANHA.get(), PiranhaRenderer::new);
        EntityRendererRegistry.register(ModEntities.HARPY.get(), HarpyRenderer::new);
        EntityRendererRegistry.register(ModEntities.PEBBLE_PROJECTILE.get(), ThrownItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.MOSSY_PEBBLE_PROJECTILE.get(), ThrownItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.RAZOR_FEATHER.get(), RazorFeatherRenderer::new);
        EntityRendererRegistry.register(ModEntities.DIAMOND_RAZOR_FEATHER.get(), RazorFeatherRenderer::new);
        EntityRendererRegistry.register(ModEntities.NETHERITE_RAZOR_FEATHER.get(), RazorFeatherRenderer::new);
        EntityRendererRegistry.register(ModEntities.BAIT_PROJECTILE.get(), BaitRenderer::new);
        EntityRendererRegistry.register(ModEntities.MYSTIC_BOAT.get(), ctx -> new BoatRenderer(ctx, ModModelLayers.MYSTIC_BOAT));
        EntityRendererRegistry.register(ModEntities.MYSTIC_CHEST_BOAT.get(), ctx -> new BoatRenderer(ctx, ModModelLayers.MYSTIC_CHEST_BOAT));
        EntityRendererRegistry.register(ModEntities.PALM_BOAT.get(), ctx -> new BoatRenderer(ctx, ModModelLayers.PALM_BOAT));
        EntityRendererRegistry.register(ModEntities.PALM_CHEST_BOAT.get(), ctx -> new BoatRenderer(ctx, ModModelLayers.PALM_CHEST_BOAT));
        EntityRendererRegistry.register(ModEntities.SKY_BOAT.get(), ctx -> new BoatRenderer(ctx, ModModelLayers.SKY_BOAT));
        EntityRendererRegistry.register(ModEntities.SKY_CHEST_BOAT.get(), ctx -> new BoatRenderer(ctx, ModModelLayers.SKY_CHEST_BOAT));
        EntityRendererRegistry.register(ModEntities.GILDED_SKY_BOAT.get(), ctx -> new BoatRenderer(ctx, ModModelLayers.GILDED_SKY_BOAT));
        EntityRendererRegistry.register(ModEntities.GILDED_SKY_CHEST_BOAT.get(), ctx -> new BoatRenderer(ctx, ModModelLayers.GILDED_SKY_CHEST_BOAT));

        LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityType == EntityTypes.WOLF && entityRenderer instanceof WolfRenderer wolfRenderer) {
                registrationHelper.register(new WolfFrogHatLayer(wolfRenderer));
            }
        });

        // createSpawnEggItem eagerly resolves a real EntityType and constructs a plain vanilla
        // SpawnEggItem (see platform/fabric/ExtraBiomesExpectPlatformImpl#createSpawnEggItem), so
        // these 8 items are in SpawnEggItem.eggs() and vanilla's own ItemColors.createDefault()
        // tints them correctly (it wraps in ARGB32.opaque() - our background/highlight colors are
        // written as bare 0xRRGGBB literals with a zero alpha byte, so a manual registration here
        // that just forwards getColor(layer) without forcing alpha opaque overrides vanilla's
        // correct mapping with a fully-transparent one, rendering the eggs invisible).
    }

    // Fabric API's BlockEntityRendererRegistry.register requires the renderer's own type parameter
    // to be a supertype of the block entity type parameter (BlockEntityRendererProvider<? super E>)
    // - javac can't resolve that wildcard directly against a SignRenderer/HangingSignRenderer
    // (typed to the vanilla SignBlockEntity/HangingSignBlockEntity superclass) method reference in
    // one inference step, so this narrows it manually. Safe at runtime: ModSignBlockEntity/
    // ModHangingSignBlockEntity are plain subclasses adding no new rendered state, so a renderer
    // built for the vanilla supertype works unchanged on ours.
    @SuppressWarnings("unchecked")
    private static <E extends net.minecraft.world.level.block.entity.BlockEntity, S extends net.minecraft.world.level.block.entity.BlockEntity,
            RS extends net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState> void registerBlockEntityRenderer(
            net.minecraft.world.level.block.entity.BlockEntityType<E> type,
            net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider<S, RS> provider) {
        BlockEntityRendererRegistry.register(type, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider<E, RS>) provider);
    }
}
