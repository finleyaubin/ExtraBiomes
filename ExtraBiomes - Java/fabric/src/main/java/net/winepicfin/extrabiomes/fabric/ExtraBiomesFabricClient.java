package net.winepicfin.extrabiomes.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.entity.ModBlockEntities;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
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

        BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.SOURCE_GOO.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.FLOWING_GOO.get(), RenderType.translucent());

        // Without this, saplings/mushrooms/leaves default to RenderType.solid() and their
        // texture's transparent pixels render as opaque black instead of being cut out.
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                ModBlocks.MYSTIC_SAPLING.get(), ModBlocks.SKY_SAPLING.get(), ModBlocks.PALM_SAPLING.get(),
                ModBlocks.BLACK_MUSHROOM.get(), ModBlocks.BLUE_MUSHROOM.get(), ModBlocks.CYAN_MUSHROOM.get(),
                ModBlocks.GREEN_MUSHROOM.get(), ModBlocks.ORANGE_MUSHROOM.get(), ModBlocks.PURPLE_MUSHROOM.get(),
                ModBlocks.WHITE_MUSHROOM.get(), ModBlocks.YELLOW_MUSHROOM.get(), ModBlocks.GLOW_MUSHROOM.get(),
                ModBlocks.MYSTIC_LEAVES.get(), ModBlocks.SKY_LEAVES.get(), ModBlocks.PALM_LEAVES.get());
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.SOURCE_GOO.get(), ModFluids.FLOWING_GOO.get(),
                new SimpleFluidRenderHandler(GooFluid.STILL_TEXTURE, GooFluid.FLOWING_TEXTURE, GooFluid.OVERLAY_TEXTURE, 0xFFFFFFFF));

        registerBlockEntityRenderer(ModBlockEntities.MOD_SIGN.get(), SignRenderer::new);
        registerBlockEntityRenderer(ModBlockEntities.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(PuckooBaseModelLayers.PUCKOO_BASE_LAYER, PuckooModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WORM, WormModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.TREEFROG, TreefrogModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.HOPPLESHROOM, HoppleshroomModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.GIANT_TORTOISE, GiantTortoiseModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.JELLYFISH, JellyfishModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PIRANHA, PiranhaModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.HARPY, HarpyModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.BAIT, BaitModel::createBodyLayer);

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

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityType == EntityType.WOLF && entityRenderer instanceof WolfRenderer wolfRenderer) {
                registrationHelper.register(new WolfFrogHatLayer(wolfRenderer));
            }
        });

        // Vanilla's own ItemColors.createDefault() tints every spawn egg by iterating
        // SpawnEggItem.eggs() (backed by SpawnEggItem's private static BY_ID map), and
        // SpawnEggItem's constructor only adds itself to that map when its EntityType argument is
        // non-null. ExtraBiomesSpawnEggItem always passes null to super() (see that class's own
        // doc comment - architectury's DeferredRegister can't guarantee a resolved EntityType at
        // construction time), so none of this mod's 8 spawn eggs are ever in SpawnEggItem.eggs(),
        // and vanilla never registers a tint for them - they render fully untinted (plain white)
        // instead of their background/highlight colors. Registering each one's color directly
        // here bypasses SpawnEggItem.eggs() entirely and fixes this regardless of that map. See
        // forge/.../event/ModEventBusClientEvents.java for the Forge-side equivalent fix.
        ColorProviderRegistry.ITEM.register((stack, layer) -> ((SpawnEggItem) stack.getItem()).getColor(layer),
                ModItems.PUCKOO_SPAWN_EGG.get(),
                ModItems.WORM_SPAWN_EGG.get(),
                ModItems.TREEFROG_SPAWN_EGG.get(),
                ModItems.HOPPLESHROOM_SPAWN_EGG.get(),
                ModItems.GIANT_TORTOISE_SPAWN_EGG.get(),
                ModItems.JELLYFISH_SPAWN_EGG.get(),
                ModItems.PIRANHA_SPAWN_EGG.get(),
                ModItems.HARPY_SPAWN_EGG.get());
    }

    // Fabric API's BlockEntityRendererRegistry.register requires the renderer's own type parameter
    // to be a supertype of the block entity type parameter (BlockEntityRendererProvider<? super E>)
    // - javac can't resolve that wildcard directly against a SignRenderer/HangingSignRenderer
    // (typed to the vanilla SignBlockEntity/HangingSignBlockEntity superclass) method reference in
    // one inference step, so this narrows it manually. Safe at runtime: ModSignBlockEntity/
    // ModHangingSignBlockEntity are plain subclasses adding no new rendered state, so a renderer
    // built for the vanilla supertype works unchanged on ours.
    @SuppressWarnings("unchecked")
    private static <E extends net.minecraft.world.level.block.entity.BlockEntity, S extends net.minecraft.world.level.block.entity.BlockEntity> void registerBlockEntityRenderer(
            net.minecraft.world.level.block.entity.BlockEntityType<E> type,
            net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider<S> provider) {
        BlockEntityRendererRegistry.register(type, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider<E>) provider);
    }
}
