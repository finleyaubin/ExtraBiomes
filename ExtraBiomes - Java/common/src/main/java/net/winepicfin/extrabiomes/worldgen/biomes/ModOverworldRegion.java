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
 * Overworld climate placement for about half of ExtraBiomes' primary-frequency biomes - see
 * {@link ModOverworldRegionSecondary} for the other half and why the primary set is split across
 * two Regions in the first place.
 *
 * The temperature/humidity band chosen for each biome below is derived from that biome's
 * "minecraft:climate" component in the Bedrock BP (temperature/downfall). Continentalness,
 * erosion, depth and weirdness are not present in the Bedrock data in a directly-portable
 * form (Bedrock uses "minecraft:overworld_height" noise params instead), so those spans are
 * best-effort placements based on each biome's theme (mesa/plateau -> inland, ocean/island ->
 * coast, etc.) and should be play-tested and tuned.
 * <p>
 * Biomes whose Bedrock "minecraft:surface_builder" has "bryce_pillars": true (ColdMesaBryce,
 * LushMesaBryce, DesertBryce, JunglePillars, ShatteredTiagaSpikes, ShatteredSwamp) need to land
 * on the specific erosion/weirdness combination vanilla's own terrain-shaping splines render as
 * tall eroded spires - this is a property of the shared overworld noise router, not something a
 * biome can request directly, so getting the shape right means reusing vanilla's own recipe for
 * it (see vanilla's OverworldBiomeBuilder#pickBadlandsBiome): LOW erosion (index 0-2 - vanilla
 * puts its flattest biomes like Swamp at the HIGH end of the erosion range, e.g. index 6, and its
 * roughest/spikiest terrain at the low end) combined with positive ("variant") weirdness. Negative
 * ("normal") weirdness at that same low erosion produces vanilla's plain (non-eroded) Badlands
 * shape instead - rugged but not spired. Cold Mesa and Lush Mesa below are each split from their
 * bryce/spire sibling (now in {@link ModOverworldRegionSecondary}) purely by weirdness sign at a
 * shared low-erosion band, exactly how vanilla itself distinguishes Badlands from Eroded Badlands
 * - the two siblings no longer need to additionally avoid each other geometrically since they're
 * governed by different Regions (see below).
 * <p>
 * A note on Bedrock's "rare" tag: it does not correlate with generation frequency and should not
 * be read as such. Cross-referencing every biome's Bedrock "minecraft:replace_biomes" "amount"
 * (the actual noise-blend frequency Bedrock uses when swapping a custom biome in over its vanilla
 * target) shows no relationship to the "rare" tag - e.g. Jungle Marsh is tagged "rare" yet has
 * amount 0.5, the same top-frequency tier as untagged biomes like Cold Mesa and Deep Dark Green,
 * while Mystic Forest (untagged) sits at amount 0.05, the lowest value in the whole set. "rare" is
 * best treated as a Bedrock content/gameplay tag (loot, advancement categorisation, etc.), not a
 * worldgen rarity signal - so it is not used below to size climate boxes or set TerraBlender
 * region weight, and comments that previously implied otherwise have been corrected to cite the
 * actual "amount" value instead.
 * <p>
 * The genuinely lowest-frequency biomes by that same "amount" value (Mystic Forest 0.05,
 * Jellyfish Fields 0.08, Future Desert / The Netherlands / The Netherlands Mutated 0.10) have
 * been split out into {@link ModOverworldRegionRare}, a third, lower-weight TerraBlender Region
 * registered alongside this one (see {@link ModTerrablender}). Every other biome - including ones
 * tagged "rare" in Bedrock but with mid-to-high "amount" values, like Jungle Marsh (0.5) - stays
 * in one of the two primary-frequency regions, with four exceptions kept for worldgen-stability
 * rather than frequency reasons: Jungle Marsh, Shattered Taiga Spikes, Charred Forest and
 * Moorlands also live in {@link ModOverworldRegionRare} - see that class's javadoc for why.
 * <p>
 * <b>Why this region is split in two:</b> with roughly 20 biomes' worth of climate boxes packed
 * into one Region, TerraBlender's {@code VanillaParameterOverlayBuilder} runs a global "adjacency"
 * pass once per Region that pairs up ANY two of that Region's climate points sharing identical
 * values on 5 of 6 axes - even points from biomes with no thematic relationship to each other -
 * and synthesizes extra boundary points from those pairings (see {@link ModOverworldRegionRare}'s
 * javadoc for the specific reachability regressions that behavior caused). Biomes that are
 * climatically similar (same temperature bucket, overlapping continentalness/erosion/weirdness -
 * e.g. every bryce/spire sibling pair, or Fungle Jungle vs. Jungle Pillars) are the ones most
 * likely to trip that adjacency pass against each other or crowd out one another's territory
 * within a single Region's {@code Climate.ParameterList} fitness resolution. Splitting the
 * primary set across two Regions removes that pressure entirely for any pair now on opposite
 * sides of the split - two different Regions don't compete for the same point via
 * {@code ParameterList} fitness or the adjacency pass at all, TerraBlender picks which Region
 * governs a given point in the world (weighted by each Region's configured weight - see
 * {@link ModTerrablender} / {@code Config.biomeWeight} / {@code Config.secondaryBiomeWeight}, both
 * equal by default) before it ever looks at either Region's own climate boxes. Concretely: every
 * bryce/spire sibling pair (Cold Mesa/Cold Mesa Bryce, Lush Mesa/Lush Mesa Bryce) and the
 * previously-documented Fungle Jungle/Jungle Pillars overlap now sit on opposite sides of the
 * split, along with the rest of the FROZEN-temperature cluster (Glacier, Cold Mesa Plateau) and
 * one HOT-temperature biome (Desert Bryce) redistributed to balance the two regions' biome counts.
 * Climate boxes themselves are otherwise unchanged from before the split.
 */
public class ModOverworldRegion extends Region {
    public ModOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        // Shared with the bryce/spire siblings in ModOverworldRegionSecondary, which claim the positive-weirdness half of this same band.
        Climate.Parameter lowErosion = ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_4);
        Climate.Parameter normalWeirdness = ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.MID_SLICE_NORMAL_ASCENDING, ParameterUtils.Weirdness.LOW_SLICE_NORMAL_DESCENDING);

        // Cold Mesa - bedrock temp=0, downfall=1 (frozen mesa).
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_3, ParameterUtils.Erosion.EROSION_6))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.COLD_MESA));
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(normalWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.COLD_MESA));

        // Deep Dark Forest - bedrock temp=0.3, downfall=0.8, roofed/mega forest (replace_biomes amount 0.15).
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.COOL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.DEEP_DARK_FOREST));

        // Floating Jungle - bedrock temp=0.95, downfall=0.9 (replace_biomes amount 0.2). Placed on
        // vanilla's actual Peaks/Slopes recipe (see OverworldBiomeBuilder#pickPeakBiome/
        // pickSlopeBiome): erosion index 0-1 is what produces the jagged mountain terrain shape,
        // not just the PEAK_NORMAL weirdness point alone - the original EROSION_4-6 band put this
        // in smooth/flat terrain instead. EROSION_0 alone was confirmed placed correctly but landed
        // right at the edge of (and on some seeds outside) a 15000-block search radius, same
        // ballpark rarity as the accepted Cold Mesa family/Deep Dark Forest/Green precedent above -
        // widening to EROSION_0-1 roughly doubles the qualifying climate area while staying within
        // vanilla's own peak/near-peak erosion range.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.MID_INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.PEAK_NORMAL)
                .build().forEach(point -> builder.add(point, ModBiomes.FLOATING_JUNGLE));

        // Fungle Jungle - bedrock temp=0.95, downfall=0.9, mushroom_island tag (replace_biomes amount 0.4). Capped to NEAR_INLAND rather than the wider INLAND alias (INLAND=[-0.11,0.55] is not a synonym for NEAR_INLAND=[-0.11,0.03]).
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.NEAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.FUNGLE_JUNGLE));

        // Grand Oasis - bedrock temp=2, downfall=0, oasis/warm tags
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_3, ParameterUtils.Erosion.EROSION_5))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.GRAND_OASIS));

        // Lush Mesa - bedrock temp=2, downfall=0.7. Temperature is HOT, not WARM: as WARM its box was a strict superset of Jungle Marsh's, and ParameterList's tied-fitness tie-break always favored Lush Mesa, leaving Jungle Marsh unreachable.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.HUMID, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_3, ParameterUtils.Erosion.EROSION_6))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.LUSH_MESA));
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.HUMID, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(normalWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.LUSH_MESA));

        // Tiaga Spikes - bedrock temp=0, downfall=1. Capped to COAST-NEAR_INLAND to leave room for Cold Mesa Bryce (MID_INLAND, in ModOverworldRegionSecondary).
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.NEAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_3))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.TAIGA_SPIKES));

        // Tropical Island - bedrock temp=1, downfall=0.9, beach/ocean/warm tags.
        // Widened from a single COAST point (vanilla's own beach-strip band) to COAST-NEAR_INLAND so
        // the biome has real interior landmass instead of rendering as a thin shoreline; DEEP_OCEAN/OCEAN
        // are left unclaimed so vanilla warm_ocean (same WARM temperature bucket) surrounds it as open water.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.NEAR_INLAND))
                .erosion(ParameterUtils.Erosion.FULL_RANGE)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.TROPICAL_ISLAND));

        // Volcanic Moss Tundra moved to ModOverworldRegionSecondary - its box overlapped Cold Mesa's above.

        builder.build().forEach(mapper::accept);
    }
}
