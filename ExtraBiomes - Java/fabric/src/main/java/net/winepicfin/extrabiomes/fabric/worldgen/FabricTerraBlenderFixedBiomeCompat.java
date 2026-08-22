package net.winepicfin.extrabiomes.fabric.worldgen;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

// Fabric's "run once when the server is about to start" hook for the loader-agnostic core in
// net.winepicfin.extrabiomes.worldgen.TerraBlenderFixedBiomeCompat (common) - see that class's
// javadoc for why this is needed (Single Biome worlds use a FixedBiomeSource, which TerraBlender
// doesn't patch surface rules for on its own). ServerLifecycleEvents.SERVER_STARTING is Fabric
// API's equivalent of Forge's ServerAboutToStartEvent - see forge/.../TerraBlenderFixedBiomeCompat
// for that hook, which calls the same shared logic. Note: unlike Forge's LOWEST-priority
// subscription, Fabric API's event has no priority levels, so ordering relative to other mods'
// listeners isn't guaranteed here.
public class FabricTerraBlenderFixedBiomeCompat {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                net.winepicfin.extrabiomes.worldgen.TerraBlenderFixedBiomeCompat.applyToFixedBiomeSourceLevels(
                        server.registryAccess()));
    }
}
