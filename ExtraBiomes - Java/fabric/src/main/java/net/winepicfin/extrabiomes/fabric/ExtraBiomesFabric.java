package net.winepicfin.extrabiomes.fabric;

import net.fabricmc.api.ModInitializer;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.entity.ModBlockEntities;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.fabric.event.FabricModEvents;
import net.winepicfin.extrabiomes.fabric.event.FabricServerEvents;
import net.winepicfin.extrabiomes.fabric.event.WolfFrogHatInteractHandler;
import net.winepicfin.extrabiomes.fabric.fluid.ModFluids;
import net.winepicfin.extrabiomes.fabric.util.FabricVanillaCompat;
import net.winepicfin.extrabiomes.fabric.worldgen.FabricBiomeModifiers;
import net.winepicfin.extrabiomes.fabric.worldgen.FabricSpawnCaps;
import net.winepicfin.extrabiomes.fabric.worldgen.FabricTerraBlenderFixedBiomeCompat;
import net.winepicfin.extrabiomes.item.ModCreativeModeTabs;
import net.winepicfin.extrabiomes.item.ModItems;
import net.winepicfin.extrabiomes.sound.ModSounds;
import net.winepicfin.extrabiomes.worldgen.biomes.surface.ModSurfaceRules;
import net.winepicfin.extrabiomes.worldgen.features.brycepillars.ModBrycePillarsFeatures;
import net.winepicfin.extrabiomes.worldgen.features.moorland.MoorlandFeatures;
import net.winepicfin.extrabiomes.worldgen.features.mystic.MysticFeatures;
import net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures;
import net.winepicfin.extrabiomes.worldgen.features.undergroundjungle.UndergroundJungleFeatures;
import net.winepicfin.extrabiomes.worldgen.features.volcanicmosstundra.ModVolcanicPlacementModifiers;
import net.winepicfin.extrabiomes.worldgen.tree.custom.ModTreeDecoratorTypes;
import net.winepicfin.extrabiomes.worldgen.tree.custom.ModTrunkPlacerTypes;
import terrablender.api.SurfaceRuleManager;

// Fabric bootstrap entry point - see forge/.../forge/ExtraBiomesForge.java for the Forge
// equivalent. Registration order matters here in a way it doesn't on Forge: architectury's Fabric
// backend registers each DeferredRegister's entries immediately when .register() is called
// (Forge instead defers everything to RegisterEvent, whose firing order per-registry is decided
// by Forge/vanilla's own registry dependency graph, not call order) - so fluids must be
// registered before blocks/items (ModBlocks.GOO/ModItems.BUCKET_OF_GOO resolve the concrete Fluid
// via ExtraBiomesExpectPlatform at registration time), and entities before the biome modifiers/
// attribute/spawn-placement registration below (all resolve concrete EntityTypes immediately,
// unlike Forge's SpawnPlacementRegisterEvent/EntityAttributeCreationEvent).
public class ExtraBiomesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        startDatagenExitWatchdogIfRunningDataGen();

        ModCreativeModeTabs.register();
        ModFluids.register();
        ModBlocks.register();
        ModItems.register();
        ModEntities.register();
        ModBlockEntities.register();
        ModSounds.register();
        ModTrunkPlacerTypes.register();
        ModTreeDecoratorTypes.register();
        ModStructureScatterFeatures.register();
        ModVolcanicPlacementModifiers.register();
        ModBrycePillarsFeatures.register();
        MoorlandFeatures.register();
        MysticFeatures.register();
        UndergroundJungleFeatures.register();

        FabricVanillaCompat.register();
        FabricModEvents.register();
        FabricBiomeModifiers.register();
        FabricSpawnCaps.register();
        FabricTerraBlenderFixedBiomeCompat.register();
        WolfFrogHatInteractHandler.register();
        FabricServerEvents.register();

        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, ExtraBiomes.MOD_ID, ModSurfaceRules.makeRules());

        // FabricConfig.load() (which triggers ModTerrablender.registerBiomes()) is NOT called
        // here - see ExtraBiomesTerraBlenderApi for why it has to run from the "terrablender"
        // entrypoint instead.
    }

    // Fabric equivalent of forge/.../ExtraBiomesForge.java's own watchdog (see its comment for the
    // full explanation) - Architectury Loom's dev.architectury.transformer.TransformerRuntime
    // wrapper never calls System.exit()/equivalent after a "run" JVM's real work finishes, so
    // :fabric:runDatagen hangs forever once FabricDataGenHelper's providers are done instead of
    // returning control to Gradle. "fabric-api.datagen" is the same system property
    // FabricDataGenHelper itself checks to decide whether datagen should run at all.
    private void startDatagenExitWatchdogIfRunningDataGen() {
        if (System.getProperty("fabric-api.datagen") == null) {
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
}
