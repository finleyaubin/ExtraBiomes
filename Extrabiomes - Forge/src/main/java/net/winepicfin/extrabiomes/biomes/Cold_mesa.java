package net.winepicfin.extrabiomes.biomes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

import static terrablender.api.ParameterUtils.*;

public class Cold_mesa extends Region{
    public Cold_mesa(ResourceLocation name, int weight)
    {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        // Overlap Vanilla's parameters with our own for our COLD_BLUE biome.
        // The parameters for this biome are chosen arbitrarily.
        new ParameterPointListBuilder()
                .temperature(Temperature.FROZEN)
                .humidity(Humidity.span(Humidity.DRY, Humidity.WET))
                .continentalness(Continentalness.span(Continentalness.INLAND,Continentalness.FAR_INLAND))
                .erosion(Erosion.FULL_RANGE)
                .depth(Depth.FULL_RANGE)
                .weirdness(Weirdness.VALLEY)
                .build().forEach(point -> builder.add(point, ModBiomes.COLD_MESA));

        // Add our points to the mapper
        builder.build().forEach(mapper::accept);

    }
    public static TagKey<Biome> COLD_MESA = TagKey.create(Registries.BIOME, new ResourceLocation( "forge:is_mesa"));
    public static TagKey<Biome> CHARRED = TagKey.create(Registries.BIOME, new ResourceLocation( "forge:is_cold"));

}
