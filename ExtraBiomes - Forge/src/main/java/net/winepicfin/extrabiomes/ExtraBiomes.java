package net.winepicfin.extrabiomes;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.entity.ModBlockEntities;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.sound.ModSounds;
import net.winepicfin.extrabiomes.entity.client.BaitRenderer;
import net.winepicfin.extrabiomes.entity.client.GiantTortoiseRenderer;
import net.winepicfin.extrabiomes.entity.client.HarpyRenderer;
import net.winepicfin.extrabiomes.entity.client.HoppleshroomRenderer;
import net.winepicfin.extrabiomes.entity.client.JellyfishRenderer;
import net.winepicfin.extrabiomes.entity.client.PiranhaRenderer;
import net.winepicfin.extrabiomes.entity.client.PuckooRenderer;
import net.winepicfin.extrabiomes.entity.client.RazorFeatherRenderer;
import net.winepicfin.extrabiomes.entity.client.TreefrogRenderer;
import net.winepicfin.extrabiomes.entity.client.WormRenderer;
import net.winepicfin.extrabiomes.fluid.ModFluidTypes;
import net.winepicfin.extrabiomes.fluid.ModFluids;
import net.winepicfin.extrabiomes.item.ModCreativeModeTabs;
import net.winepicfin.extrabiomes.item.ModItems;
import net.winepicfin.extrabiomes.util.ModVanillaCompat;
import net.winepicfin.extrabiomes.util.ModWoodTypes;
import net.winepicfin.extrabiomes.worldgen.biomes.surface.ModSurfaceRules;
import net.winepicfin.extrabiomes.worldgen.features.moorland.MoorlandFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.ModBrycePillarsFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;
import net.winepicfin.extrabiomes.worldgen.tree.custom.ModTrunkPlacerTypes;
import net.winepicfin.extrabiomes.worldgen.tree.custom.ModTreeDecoratorTypes;
import org.slf4j.Logger;
import terrablender.api.SurfaceRuleManager;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExtraBiomes.MOD_ID)
public class ExtraBiomes
{
    public static final String MOD_ID = "extrabiomes";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static void LoadConfig(){


}

    public ExtraBiomes() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModFluids.register(modEventBus);
        ModFluidTypes.register(modEventBus);
        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModTrunkPlacerTypes.register(modEventBus);
        ModTreeDecoratorTypes.register(modEventBus);
        ModStructureScatterFeatures.register(modEventBus);
        ModBrycePillarsFeatures.register(modEventBus);
        MoorlandFeatures.register(modEventBus);
        UndergroundJungleFeatures.register(modEventBus);
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            // Register our surface rules
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModSurfaceRules.makeRules());
            ModVanillaCompat.register();
        });
    }


private void addCreative(BuildCreativeModeTabContentsEvent event){
}
    
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            Sheets.addWoodType(ModWoodTypes.MYSTIC);
            Sheets.addWoodType(ModWoodTypes.PALM);
            Sheets.addWoodType(ModWoodTypes.SKY);
            Sheets.addWoodType(ModWoodTypes.GILDED_SKY);
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_GOO.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_GOO.get(), RenderType.translucent());
            EntityRenderers.register(ModEntities.PUCKOO.get(), PuckooRenderer::new);
            EntityRenderers.register(ModEntities.WORM.get(), WormRenderer::new);
            EntityRenderers.register(ModEntities.TREEFROG.get(), TreefrogRenderer::new);
            EntityRenderers.register(ModEntities.HOPPLESHROOM.get(), HoppleshroomRenderer::new);
            EntityRenderers.register(ModEntities.GIANT_TORTOISE.get(), GiantTortoiseRenderer::new);
            EntityRenderers.register(ModEntities.JELLYFISH.get(), JellyfishRenderer::new);
            EntityRenderers.register(ModEntities.PIRANHA.get(), PiranhaRenderer::new);
            EntityRenderers.register(ModEntities.HARPY.get(), HarpyRenderer::new);
            EntityRenderers.register(ModEntities.PEBBLE_PROJECTILE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.MOSSY_PEBBLE_PROJECTILE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.RAZOR_FEATHER.get(), RazorFeatherRenderer::new);
            EntityRenderers.register(ModEntities.DIAMOND_RAZOR_FEATHER.get(), RazorFeatherRenderer::new);
            EntityRenderers.register(ModEntities.NETHERITE_RAZOR_FEATHER.get(), RazorFeatherRenderer::new);
            EntityRenderers.register(ModEntities.BAIT_PROJECTILE.get(), BaitRenderer::new);
        }
    }
}
