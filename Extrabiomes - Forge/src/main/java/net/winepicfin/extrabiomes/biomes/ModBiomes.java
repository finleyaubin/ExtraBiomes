package net.winepicfin.extrabiomes.biomes;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModBiomes
{
    public static final ResourceKey<Biome> CHARRED_FOREST = register("charred_forest");
    public static final ResourceKey<Biome> COLD_MESA = register("cold_mesa");
    public static final ResourceKey<Biome> COLD_ERODED_MESA = register("cold_eroded_mesa");

    private static ResourceKey<Biome> register(String name)
    {
        return ResourceKey.create(Registries.BIOME, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}