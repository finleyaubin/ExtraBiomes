package net.winepicfin.extrabiomes.event;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.HarpySpawner;

// See HarpySpawner's own class comment for why this is a separate per-tick routine instead of
// the standard NaturalSpawner category loop.
@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID)
public class HarpySpawnerHandler {
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide()) return;
        if (event.level instanceof ServerLevel serverLevel) {
            HarpySpawner.tick(serverLevel);
        }
    }
}
