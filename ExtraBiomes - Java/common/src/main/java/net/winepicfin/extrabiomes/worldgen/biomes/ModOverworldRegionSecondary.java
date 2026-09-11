package net.winepicfin.extrabiomes.worldgen.biomes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

/**
 * The other half of ExtraBiomes' primary-frequency biomes - see {@link ModOverworldRegion} for
 * the full rationale on why the primary set is split across two Regions (short version: it
 * removes climatically-similar biomes - bryce/spire siblings, biomes sharing a temperature bucket
 * and overlapping continentalness/erosion/weirdness - from competing for territory within a
 * single Region's {@code Climate.ParameterList} fitness resolution, and from TerraBlender's
 * cross-biome "adjacency" pass, entirely).
 * <p>
 * Registered alongside {@link ModOverworldRegion} with an equal weight by default (see
 * {@link ModTerrablender} / {@code Config.secondaryBiomeWeight}), so this region generates just as
 * often as the primary one - the split is purely about which biomes share a
 * {@code Climate.ParameterList} with which others, not about frequency (that's what
 * {@link ModOverworldRegionRare} is for).
 * <p>
 * Membership here: every bryce/spire sibling that would otherwise sit right next to its flatter
 * counterpart in climate space (Cold Mesa Bryce, Lush Mesa Bryce, Desert Bryce, Jungle Pillars,
 * Shattered Swamp - all sharing the same low-erosion/positive-weirdness "spire" recipe described
 * on {@link ModOverworldRegion}'s javadoc), plus the rest of the FROZEN-temperature cluster that
 * isn't already split off into {@link ModOverworldRegionRare} (Cold Mesa Plateau, Glacier), plus
 * Deep Dark Green (a cave-only biome with no surface climate box to speak of, kept here simply to
 * balance the two regions' biome counts rather than for any climate-overlap reason). Climate boxes
 * themselves are carried over unchanged from their previous home in {@link ModOverworldRegion}.
 */
public class ModOverworldRegionSecondary extends Region {
    public ModOverworldRegionSecondary(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        // Shared low-erosion band every bryce/spire biome below claims, on the positive-weirdness ("variant") half - see ModOverworldRegion's javadoc.
        Climate.Parameter lowErosion = ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_4);
        Climate.Parameter variantWeirdness = ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.LOW_SLICE_VARIANT_ASCENDING, ParameterUtils.Weirdness.MID_SLICE_VARIANT_DESCENDING);

        // Cold Mesa Bryce - eroded spire variant of Cold Mesa (ModOverworldRegion). Capped to MID_INLAND (not FAR_INLAND like Cold Mesa) to leave room exclusive of Taiga Spikes' COAST-NEAR_INLAND band.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.MID_INLAND)
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.COLD_MESA_BRYCE));

        // Cold Mesa Plateau - flat variant of Cold Mesa (replace_biomes amount 0.15).
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.FAR_INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.COLD_MESA_PLATEAU));

        // Deep Dark Green - a cave variant of vanilla's Deep Dark, matching its placement exactly except only half the weirdness range, so vanilla deep_dark still generates in the other half (mirrors bedrock's replace_biomes amount of 0.5).
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FULL_RANGE)
                .humidity(ParameterUtils.Humidity.FULL_RANGE)
                .continentalness(ParameterUtils.Continentalness.FULL_RANGE)
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1))
                .depth(ParameterUtils.Depth.FLOOR)
                .weirdness(Climate.Parameter.span(-1.0F, 0.0F))
                .build().forEach(point -> builder.add(point, ModBiomes.DEEP_DARK_GREEN));

        // Desert Bryce - bedrock temp=2, downfall=0, canyon desert (replace_biomes amount 0.2). Low erosion, positive-weirdness half only - vanilla's own Desert/Badlands reclaims the rest of this climate cell.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.NEUTRAL))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.NEAR_INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.DESERT_BRYCE));

        // Glacier - bedrock temp=0, downfall=1
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.GLACIER));

        // Jungle Pillars - bedrock temp=0.95, downfall=0.9, stone_pillars tag (replace_biomes amount 0.15). Low erosion, positive-weirdness half only.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.MID_INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.JUNGLE_PILLARS));

        // Shattered Swamp - bedrock temp=0.8, downfall=0.5. Low erosion, positive-weirdness half produces the tuff/stone spires poking out of the swamp, matching the Bedrock reference art.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.NEAR_INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.SHATTERED_SWAMP));

        // Lush Mesa Bryce - eroded spire variant of Lush Mesa (ModOverworldRegion). Temperature is HOT to match Lush Mesa - see that biome's comment in ModOverworldRegion.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.LUSH_MESA_BRYCE));

        // Volcanic Moss Tundra - moved here from ModOverworldRegion since its box overlapped Cold Mesa's there.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_2, ParameterUtils.Erosion.EROSION_4))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(Climate.Parameter.span(-1.0F, 0.0F))
                .build().forEach(point -> builder.add(point, ModBiomes.VOLCANIC_MOSS_TUNDRA));

        builder.build().forEach(mapper::accept);
    }
}
