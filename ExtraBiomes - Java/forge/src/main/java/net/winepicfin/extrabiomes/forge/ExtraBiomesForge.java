package net.winepicfin.extrabiomes.forge;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.advancements.ModCriteriaTriggers;
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
import net.winepicfin.extrabiomes.forge.fluid.ModFluidTypes;
import net.winepicfin.extrabiomes.forge.fluid.ModFluids;
import net.winepicfin.extrabiomes.item.ModCreativeModeTabs;
import net.winepicfin.extrabiomes.item.ModItems;
import net.winepicfin.extrabiomes.forge.util.ModVanillaCompat;
import net.winepicfin.extrabiomes.forge.worldgen.ModSpawnCaps;
import net.winepicfin.extrabiomes.util.ModWoodTypes;
import net.winepicfin.extrabiomes.worldgen.biomes.surface.ModSurfaceRules;
import net.winepicfin.extrabiomes.worldgen.features.moorland.MoorlandFeatures;
import net.winepicfin.extrabiomes.worldgen.features.netherlands.NetherlandsWheatFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mystic.MysticFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.structure.windmill.ModStructureTypes;
import net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra.ModVolcanicPlacementModifiers;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.ModBrycePillarsFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;
import net.winepicfin.extrabiomes.worldgen.tree.custom.ModTrunkPlacerTypes;
import net.winepicfin.extrabiomes.worldgen.tree.custom.ModTreeDecoratorTypes;
import org.slf4j.Logger;
import terrablender.api.SurfaceRuleManager;

// The value here should match an entry in the META-INF/mods.toml file. This is the Forge
// bootstrap entry point; ExtraBiomes (common) holds only the loader-agnostic MOD_ID constant.
@Mod(ExtraBiomes.MOD_ID)
public class ExtraBiomesForge
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public ExtraBiomesForge(net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext context) {
        var modEventBus = context.getModEventBus();
        // Must happen before any DeferredRegister .register() call below - architectury's
        // RegistrarManager looks up the mod's event bus by mod id, which it only knows about
        // once this runs (Forge's own ModList entry for this mod isn't usable for that lookup
        // yet, since we're still inside this mod's own constructor).
        dev.architectury.platform.forge.EventBuses.registerModEventBus(ExtraBiomes.MOD_ID, modEventBus);

        startDatagenExitWatchdogIfRunningDataGen();
        startGameTestExitWatchdogIfRunningGameTestServer();

        ModCreativeModeTabs.register();
        ModItems.register();
        ModBlocks.register();
        ModFluids.register();
        ModFluidTypes.register(modEventBus);
        ModEntities.register();
        ModBlockEntities.register();
        ModSounds.register();
        ModCriteriaTriggers.register();
        ModTrunkPlacerTypes.register();
        ModTreeDecoratorTypes.register();
        ModStructureScatterFeatures.register();
        ModStructureTypes.register();
        ModVolcanicPlacementModifiers.register();
        ModBrycePillarsFeatures.register();
        MoorlandFeatures.register();
        NetherlandsWheatFeatures.register();
        MysticFeatures.register();
        UndergroundJungleFeatures.register();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener((ModConfigEvent event) -> ForgeConfig.load());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us.
        // FMLJavaModLoadingContext#registerConfig was removed in Forge 48.x (1.20.2) - this is now
        // only on ModLoadingContext.
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ForgeConfig.SPEC);
    }

    // The pre-Loom (ForgeGradle) version of this project's runData exited on its own -
    // ForgeGradle's own dev launcher must call System.exit()/equivalent after the datagen JVM
    // finishes. Architectury Loom replaces that with its own
    // dev.architectury.transformer.TransformerRuntime wrapper, which doesn't, so whatever
    // background executors datagen leaves running (Forge/vanilla's own, not this mod's) keep
    // the JVM alive forever once GatherDataEvent's providers finish. The JVM's real "main"
    // thread runs that whole synchronous datagen pipeline and terminates the moment it's
    // genuinely done, so waiting on it (from a daemon thread, so this itself never blocks a
    // real exit) and then forcing the process down is a safe, deterministic way to get
    // :forge:runData to actually return control to Gradle under Loom's launcher.
    private void startDatagenExitWatchdogIfRunningDataGen() {
        if (!net.minecraftforge.data.loading.DatagenModLoader.isRunningDataGen()) {
            return;
        }
        Thread mainThread = Thread.getAllStackTraces().keySet().stream()
                .filter(t -> t.getName().equals("main"))
                .findFirst().orElse(null);
        if (mainThread == null) {
            return;
        }
        Thread watchdog = new Thread(() -> {
            try {
                mainThread.join();
            } catch (InterruptedException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }, "extrabiomes-datagen-exit-watchdog");
        watchdog.setDaemon(true);
        watchdog.start();
    }

    // Same root cause and fix as startDatagenExitWatchdogIfRunningDataGen() above (see its
    // comment) - Loom's TransformerRuntime wrapper doesn't call System.exit() after the JVM's
    // real work is done, so :forge:runGameTestServer's console "stop" command shuts the
    // DedicatedServer down cleanly (its "Server thread" - the non-daemon thread actually keeping
    // the JVM alive, unlike "main" which just launches it and returns - finishes and logs "All
    // dimensions are saved") but Gradle never gets control back, hanging until CI's outer
    // `timeout` wrapper kills it. forge.enabledGameTestNamespaces is the vmArg
    // forge/build.gradle's gameTestServer run sets, so this only applies there.
    //
    // This watchdog's only job is to make sure the JVM always exits - actual pass/fail is
    // determined by CI grepping the console log for TestCommand's "All required tests passed"/
    // "required tests failed" summary lines (see gradle-build.yml's "Run GameTests" step), not
    // by this process's exit code. Waiting for "Server thread" to appear used to be unbounded:
    // if the server crashed during startup (e.g. a registry/worldgen bootstrap exception) before
    // DedicatedServer's constructor ever created that thread, the loop spun forever and this
    // watchdog never fired - the JVM then hung until CI's outer `timeout` killed it 15 minutes
    // later with no diagnostics. Bounding the wait means a startup crash now exits promptly
    // instead of silently eating the whole timeout budget.
    private void startGameTestExitWatchdogIfRunningGameTestServer() {
        if (System.getProperty("forge.enabledGameTestNamespaces") == null) {
            return;
        }
        Thread watchdog = new Thread(() -> {
            Thread serverThread = null;
            long deadline = System.currentTimeMillis() + java.util.concurrent.TimeUnit.MINUTES.toMillis(2);
            while (serverThread == null && System.currentTimeMillis() < deadline) {
                serverThread = Thread.getAllStackTraces().keySet().stream()
                        .filter(t -> t.getName().equals("Server thread"))
                        .findFirst().orElse(null);
                if (serverThread == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                        return;
                    }
                }
            }
            if (serverThread == null) {
                LOGGER.error("extrabiomes-gametest-exit-watchdog: \"Server thread\" never appeared within 2 minutes - the server likely crashed during startup. Exiting.");
                Runtime.getRuntime().halt(1);
                return;
            }
            try {
                serverThread.join();
            } catch (InterruptedException ignored) {
            }
            Runtime.getRuntime().halt(0);
        }, "extrabiomes-gametest-exit-watchdog");
        watchdog.setDaemon(true);
        watchdog.start();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            // Register our surface rules
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, ExtraBiomes.MOD_ID, ModSurfaceRules.makeRules());
            // addSurfaceRules above only reaches biomes namespaced "extrabiomes" - this instead
            // injects into the shared default ruleset every other namespace (including vanilla's
            // own badlands/eroded_badlands/wooded_badlands) falls back to, so those get the same
            // depth-banded terracotta too. See ModSurfaceRules.makeVanillaBadlandsAdditions() javadoc.
            SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD,
                    SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 0, ModSurfaceRules.makeVanillaBadlandsAdditions());
            ModVanillaCompat.register();
            ModSpawnCaps.register();
        });
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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

            // Saplings/mushrooms/leaves get their cutout render type from their block model's
            // "render_type" field (set in ModBlockStateProvider's datagen) instead of here -
            // the runtime ItemBlockRenderTypes.setRenderLayer(Block, RenderType) overloads are
            // deprecated for removal in favor of setting render_type on the model itself.
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
