package net.winepicfin.extrabiomes.worldgen.biomes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

/**
 * A small fourth region for the two "vast but spaced out" frozen biomes, Glacier and Volcanic Moss
 * Tundra. Registered with the Rare region's weight (see {@link ModTerrablender}), so each patch is
 * large but they turn up about as seldom as the Rare region's biomes.
 * <p>
 * Why its own region: both want wide, smooth-noise boxes (FROZEN temperature, COAST-MID_INLAND, every
 * erosion and weirdness) so each patch is big, and the frozen climate is already crowded - Cold Mesa
 * Plateau/Bryce in {@link ModOverworldRegionSecondary} and Shattered Taiga Spikes in
 * {@link ModOverworldRegionRare}. Putting them in either of those regions made unrelated biomes
 * unreachable in {@code BiomeGenerationGameTests} (Mystic Forest and Future Desert in the Rare region,
 * Jungle Pillars in the Secondary region): TerraBlender's per-region adjacency pass makes every edit to a
 * region's box set globally unpredictable (see the Rare region's javadoc). A region of its own leaves
 * those two region lists exactly as they were.
 * <p>
 * The two boxes are separated by humidity (Glacier NEUTRAL-HUMID, Volcanic Moss Tundra ARID-DRY), so
 * they never overlap, and by continentalness from Shattered Taiga Spikes (FAR_INLAND only).
 */
public class ModOverworldRegionFrozen extends Region {
    public ModOverworldRegionFrozen(Identifier name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        // Glacier - bedrock temp=0, downfall=1 (replace_biomes amount 0.7, the highest in the pack).
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.MID_INLAND))
                .erosion(ParameterUtils.Erosion.FULL_RANGE)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.GLACIER));

        // Volcanic Moss Tundra - replace_biomes amount 0.5; takes the drier half of the frozen humidity range.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.DRY))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.MID_INLAND))
                .erosion(ParameterUtils.Erosion.FULL_RANGE)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.VOLCANIC_MOSS_TUNDRA));

        builder.build().forEach(mapper::accept);
    }
}
