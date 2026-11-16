package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.resources.Identifier;
import net.winepicfin.extrabiomes.Config;
import net.winepicfin.extrabiomes.ExtraBiomes;
import terrablender.api.Regions;

import java.util.concurrent.atomic.AtomicBoolean;

public class ModTerrablender {
    // Config.load() can fire more than once (even concurrently, off Forge's config watcher thread), and calling Regions.register twice has caused TerraBlender crashes and non-deterministic biome placement, so registration is guarded to run at most once.
    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

    public static void registerBiomes(){
        if (!REGISTERED.compareAndSet(false, true)) return;
        Regions.register(new ModOverworldRegion(Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"overworld"), Config.biomeWeight));
        Regions.register(new ModOverworldRegionSecondary(Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"overworld_secondary"), Config.secondaryBiomeWeight));
        Regions.register(new ModOverworldRegionRare(Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"overworld_rare"), Config.rareBiomeWeight));
        Regions.register(new ModOverworldRegionFrozen(Identifier.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"overworld_frozen"), Config.frozenBiomeWeight));
    }
}
