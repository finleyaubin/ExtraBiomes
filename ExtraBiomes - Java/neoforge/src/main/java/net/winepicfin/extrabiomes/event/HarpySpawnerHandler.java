package net.winepicfin.extrabiomes.event;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.HarpySpawner;

// See HarpySpawner's own class comment for why this is a separate per-tick routine instead of
// the standard NaturalSpawner category loop.
@EventBusSubscriber(modid = ExtraBiomes.MOD_ID)
public class HarpySpawnerHandler {
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            HarpySpawner.tick(serverLevel);
        }
    }
}
