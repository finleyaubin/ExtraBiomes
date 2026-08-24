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
 * Companion region to {@link ModOverworldRegion}, holding the biomes that are genuinely
 * low-frequency in the Bedrock source data - i.e. a low "minecraft:replace_biomes" "amount", not
 * a "rare" tag (see the "rare" tag note on {@link ModOverworldRegion}'s javadoc for why the two
 * are not the same thing). Membership here is cut at amount <= 0.10:
 * <p>
 * Mystic Forest (0.05), Jellyfish Fields (0.08), Future Desert / The Netherlands / The
 * Netherlands Mutated (0.10 each).
 * <p>
 * This region is registered alongside the primary one with its own, lower weight
 * (see {@link ModTerrablender} / {@code Config.rareBiomeWeight}), so these five generate less
 * often across the world without needing an artificially shrunk climate box the way "rare"-tagged
 * but higher-amount biomes (e.g. Jungle Marsh at 0.5) do in the primary region.
 * <p>
 * Climate boxes below are carried over unchanged from their previous home in
 * {@link ModOverworldRegion} - moving regions doesn't by itself require re-tuning continentalness/
 * erosion/weirdness, since TerraBlender picks which Region governs a given point in the world
 * before it ever looks at that Region's own climate boxes, so these five no longer compete
 * against anything left behind in either primary-frequency region. Future Desert's box in
 * particular used to need a weirdness-half restriction purely to avoid swallowing Desert Bryce
 * (now in {@link ModOverworldRegionSecondary}, having originally stayed behind in
 * {@link ModOverworldRegion} itself) - that restriction is no longer load-bearing now that the two
 * are in separate Regions, but it's left as-is here rather than widened, since widening it is a
 * generation-behaviour change that deserves its own playtested patch, not a side effect of
 * moving files around.
 * <p>
 * Four more biomes live here for a different reason entirely: Jungle Marsh (replace_biomes amount
 * 0.5), Shattered Taiga Spikes, Charred Forest and Moorlands are NOT low-frequency by that metric
 * and would otherwise belong in the primary region, but {@code BiomeGenerationGameTests} (the
 * gametest that searches a live generated world for every mod biome within 15000 blocks of spawn)
 * found them consistently unreachable there, across multiple random seeds, despite each having a
 * reasonably-sized climate box in isolation. The cause: TerraBlender's
 * {@code VanillaParameterOverlayBuilder} runs a global "adjacency" pass once per Region that pairs
 * up ANY two of that Region's climate points sharing identical values on 5 of 6 axes (even points
 * from unrelated biomes, even across different temperature buckets) and synthesizes extra boundary
 * points from those pairings - confirmed by observing that an unrelated, same-Region edit (Lush
 * Mesa's temperature bucket) flipped Shattered Taiga Spikes' (a FROZEN-temp biome, geometrically
 * nowhere near Lush Mesa) reachability, and that moving Jungle Marsh and Shattered Taiga Spikes out
 * of the primary region in turn flipped Charred Forest and Moorlands (again in unrelated
 * temperature buckets from each other) from reachable to unreachable. With ~20 biomes densely
 * packed into the primary region, this made per-biome climate-box tuning globally unpredictable -
 * edits that should have been local kept causing regressions elsewhere, and removing biomes from
 * the region reliably surfaced a new pair of casualties rather than settling. Moving all four here,
 * to this much smaller, mostly-disjoint-climate region, sidesteps the problem by removing them from
 * that adjacency computation entirely, rather than continuing to chase it. If a future biome
 * addition to the primary region reintroduces this failure mode, the fix is the same: identify
 * whichever biome(s) {@code BiomeGenerationGameTests} reports as unreachable and move them here
 * too - this is a workaround for TerraBlender's overlay-builder behavior at this region's current
 * biome count, not a one-time fix that rules out recurrence. Their climate boxes are unchanged
 * from the primary region.
 */
public class ModOverworldRegionRare extends Region {
    public ModOverworldRegionRare(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        // Used by the Future Desert box below; kept unchanged from ModOverworldRegion even though it no longer pairs against Desert Bryce there - see class javadoc.
        Climate.Parameter normalWeirdness = ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.MID_SLICE_NORMAL_ASCENDING, ParameterUtils.Weirdness.LOW_SLICE_NORMAL_DESCENDING);

        // Mystic Forest - bedrock temp=0.95, downfall=0.9. Lowest replace_biomes amount (0.05) in the whole biome set.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.HUMID, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_4))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.MYSTIC_FOREST));

        // Jellyfish Fields - bedrock temp=0.5, downfall=0.5, ocean/warm tags; replace_biomes amount 0.08. Confined to DEEP_OCEAN..OCEAN so it doesn't bleed into the COAST (land/beach) band.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.DEEP_OCEAN, ParameterUtils.Continentalness.OCEAN))
                .erosion(ParameterUtils.Erosion.FULL_RANGE)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.JELLYFISH_FIELDS));

        // Future Desert - bedrock temp=2, downfall=0; replace_biomes amount 0.1. Weirdness restriction and continentalness band are historical (see class javadoc) - kept unchanged rather than widened.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.NEUTRAL))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.MID_INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.FULL_RANGE)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(normalWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.FUTURE_DESERT));

        // The Netherlands - bedrock temp=0.5, downfall=0.5 (Dutch tulip fields/windmills, overworld); replace_biomes amount 0.1.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.NEAR_INLAND, ParameterUtils.Continentalness.MID_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.THE_NETHERLANDS));

        // The Netherlands Mutated - replace_biomes amount 0.1.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.NEAR_INLAND, ParameterUtils.Continentalness.MID_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_3, ParameterUtils.Erosion.EROSION_4))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.THE_NETHERLANDS_MUTATED));

        // Jungle Marsh - bedrock temp=0.95, downfall=0.9, jungle+swamp tags; replace_biomes amount 0.5, NOT low-frequency - lives here for worldgen-stability reasons, not rarity. See class javadoc.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.NEAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_4, ParameterUtils.Erosion.EROSION_6))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.JUNGLE_MARSH));

        // Shattered Tiaga Spikes - mutated variant of Tiaga Spikes (which stays in ModOverworldRegion); NOT low-frequency - lives here for worldgen-stability reasons, not rarity. See class javadoc.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.FAR_INLAND)
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_4))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.LOW_SLICE_VARIANT_ASCENDING, ParameterUtils.Weirdness.MID_SLICE_VARIANT_DESCENDING))
                .build().forEach(point -> builder.add(point, ModBiomes.SHATTERED_TAIGA_SPIKES));

        // Charred Forest - bedrock temp=2, downfall=0.5 (hot, replaces pale garden/birch forest). NOT low-frequency - lives here for worldgen-stability reasons, not rarity. See class javadoc.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.NEUTRAL))
                .continentalness(ParameterUtils.Continentalness.NEAR_INLAND)
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_3))
                .depth(ParameterUtils.Depth.span(ParameterUtils.Depth.SURFACE, ParameterUtils.Depth.FLOOR))
                .weirdness(ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.PEAK_NORMAL, ParameterUtils.Weirdness.MID_SLICE_NORMAL_DESCENDING))
                .build().forEach(point -> builder.add(point, ModBiomes.CHARRED_FOREST));

        // Moorlands - bedrock temp=0.5, downfall=0.5, plains/river tags. NOT low-frequency - lives here for worldgen-stability reasons, not rarity. See class javadoc.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.NEAR_INLAND, ParameterUtils.Continentalness.MID_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_5, ParameterUtils.Erosion.EROSION_6))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.MOORLANDS));

        builder.build().forEach(mapper::accept);
    }
}
