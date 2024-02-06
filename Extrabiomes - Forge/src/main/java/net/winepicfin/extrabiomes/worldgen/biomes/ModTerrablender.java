package net.winepicfin.extrabiomes.worldgen.biomes;

import net.minecraft.resources.ResourceLocation;
import net.winepicfin.extrabiomes.Config;
import net.winepicfin.extrabiomes.ExtraBiomes;
import terrablender.api.Regions;

public class ModTerrablender {
    public static void registerBiomes(){
        Regions.register(new ModOverworldRegion(new ResourceLocation(ExtraBiomes.MOD_ID,"overworld"), Config.biomeWeight));
        System.out.println(Config.biomeWeight+" is the biome weight On registerBiomes");
    }
}
