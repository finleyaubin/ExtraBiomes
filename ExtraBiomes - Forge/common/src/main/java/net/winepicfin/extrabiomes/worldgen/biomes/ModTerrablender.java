package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.Config;
import net.winepicfin.extrabiomes.ExtraBiomes;
import terrablender.api.Regions;

public class ModTerrablender {
    public static void registerBiomes(){
        Regions.register(new ModOverworldRegion(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"overworld"), Config.biomeWeight));
        Regions.register(new ModOverworldRegionRare(ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID,"overworld_rare"), Config.rareBiomeWeight));
        System.out.println(Config.biomeWeight+" is the biome weight On registerBiomes");
        System.out.println(Config.rareBiomeWeight+" is the rare biome weight On registerBiomes");
    }
}
