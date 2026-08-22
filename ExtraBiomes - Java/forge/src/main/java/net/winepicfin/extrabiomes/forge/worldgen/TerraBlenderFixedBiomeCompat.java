package net.winepicfin.extrabiomes.forge.worldgen;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.winepicfin.extrabiomes.ExtraBiomes;

// Forge's "run once when the server is about to start" hook for the loader-agnostic core in
// net.winepicfin.extrabiomes.worldgen.TerraBlenderFixedBiomeCompat (common) - see that class's
// javadoc for why this is needed. See fabric/.../FabricTerraBlenderFixedBiomeCompat for the
// Fabric equivalent hook, which calls the same shared logic.
@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TerraBlenderFixedBiomeCompat {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        net.winepicfin.extrabiomes.worldgen.TerraBlenderFixedBiomeCompat.applyToFixedBiomeSourceLevels(
                event.getServer().registryAccess());
    }
}
