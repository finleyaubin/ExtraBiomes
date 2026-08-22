package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.Config;
import net.winepicfin.extrabiomes.ExtraBiomes;
import terrablender.api.Regions;

import java.util.concurrent.atomic.AtomicBoolean;

public class ModTerrablender {
    // Config.load() (this method's only caller) runs from a ModConfigEvent listener that reacts
    // to every ModConfigEvent, not just the initial ModConfigEvent.Loading - Forge's config file
    // watcher can fire a second ModConfigEvent.Reloading off a background watcher thread shortly
    // after the config file's first write-to-disk-with-defaults (which the watcher notices as an
    // "external" change), so this can run more than once, including concurrently with itself from
    // a different thread than the one initializing the world. Regions.register isn't safe to call
    // twice for the same name under that condition: it produced a captured
    // ArrayIndexOutOfBoundsException crash inside TerraBlender's own Regions.get() (a classic
    // concurrent-modification symptom - the backing map's snapshot size and its live size
    // disagreeing mid-copy) and, even without crashing outright, a repeat/racing registration
    // landing before vs. after TerraBlender bakes a level stem's region-selection layer changes
    // what region state that layer captures - observed directly as the same pinned world seed
    // placing this mod's biomes at genuinely different coordinates (or not finding several of
    // them at all) between otherwise-identical runs. Guarding registration to happen at most once
    // removes the repeat/race at its source regardless of why Config.load() got invoked again.
    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

    public static void registerBiomes(){
        if (!REGISTERED.compareAndSet(false, true)) return;
        Regions.register(new ModOverworldRegion(new ResourceLocation(ExtraBiomes.MOD_ID,"overworld"), Config.biomeWeight));
        Regions.register(new ModOverworldRegionRare(new ResourceLocation(ExtraBiomes.MOD_ID,"overworld_rare"), Config.rareBiomeWeight));
    }
}
