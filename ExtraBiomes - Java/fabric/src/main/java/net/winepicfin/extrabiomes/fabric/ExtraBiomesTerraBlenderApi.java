package net.winepicfin.extrabiomes.fabric;

import terrablender.api.TerraBlenderApi;

// Fabric's flat ModInitializer entrypoint gives no ordering guarantee between mods - unlike
// Forge, where TerraBlender's own mod constructor (which sets terrablender.core.TerraBlender.CONFIG)
// always runs for every mod before any @Mod.EventBusSubscriber/ModConfigEvent code fires, Fabric
// mods' "main" entrypoints can run in either order. TerraBlender-fabric's own onInitialize sets
// CONFIG *then* invokes every mod's "terrablender" entrypoint (TerraBlenderApi) synchronously
// right after - registering here instead of calling FabricConfig.load() directly from
// ExtraBiomesFabric.onInitialize() guarantees TerraBlender.CONFIG is non-null before
// Config.load() -> ModTerrablender.registerBiomes() touches terrablender.api.Regions (whose
// <clinit> reads that field and threw a NullPointerException when this ran too early).
public class ExtraBiomesTerraBlenderApi implements TerraBlenderApi {
    @Override
    public void onTerraBlenderInitialized() {
        FabricConfig.load();
    }
}
