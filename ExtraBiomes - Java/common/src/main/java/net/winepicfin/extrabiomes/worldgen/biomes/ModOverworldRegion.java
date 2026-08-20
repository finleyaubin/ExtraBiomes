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
 * Overworld climate placement for every ExtraBiomes biome.
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
 * shape instead - rugged but not spired. Each bryce/pillar biome below is therefore split from its
 * flatter sibling (or, where there is no mod-registered sibling, from whatever else would
 * otherwise occupy that spot) purely by weirdness sign at a shared low-erosion band, exactly how
 * vanilla itself distinguishes Badlands from Eroded Badlands.
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
 * been split out into {@link ModOverworldRegionRare}, a second, lower-weight TerraBlender Region
 * registered alongside this one (see {@link ModTerrablender}). Every other biome - including ones
 * tagged "rare" in Bedrock but with mid-to-high "amount" values, like Jungle Marsh (0.5) - stays
 * here in the primary region, with four exceptions kept for worldgen-stability rather than
 * frequency reasons: Jungle Marsh, Shattered Taiga Spikes, Charred Forest and Moorlands also
 * live in {@link ModOverworldRegionRare} - see that class's javadoc for why.
 */
public class ModOverworldRegion extends Region {
    public ModOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        // Shared low-erosion band bryce/pillar biomes claim, split by weirdness sign - see class
        // javadoc. normalWeirdness = vanilla's negative-weirdness half (rugged, not spired).
        // variantWeirdness = vanilla's positive-weirdness half (the half vanilla renders as spires).
        Climate.Parameter lowErosion = ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_4);
        Climate.Parameter normalWeirdness = ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.MID_SLICE_NORMAL_ASCENDING, ParameterUtils.Weirdness.LOW_SLICE_NORMAL_DESCENDING);
        Climate.Parameter variantWeirdness = ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.LOW_SLICE_VARIANT_ASCENDING, ParameterUtils.Weirdness.MID_SLICE_VARIANT_DESCENDING);

        // Cold Mesa - bedrock temp=0, downfall=1 (frozen mesa). Covers the flat/rolling erosion
        // band (3-6) at any weirdness, plus the negative-weirdness half of the low-erosion band
        // (0-2) - the same low erosion Cold Mesa Bryce claims for its positive-weirdness half.
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

        // Cold Mesa Bryce - eroded spire variant of Cold Mesa; same low-erosion band as Cold Mesa
        // above, but only the positive-weirdness half, which is what renders as spires (see class
        // javadoc). Capped to MID_INLAND (rather than reaching from INLAND to FAR_INLAND like Cold
        // Mesa) so it has room that's exclusive of Taiga Spikes' (COAST-NEAR_INLAND) - Shattered
        // Taiga Spikes (previously FAR_INLAND) now lives in ModOverworldRegionRare (a separate
        // Region, see that class's javadoc), so it no longer competes for continentalness here at
        // all, but the MID_INLAND cap is left in place since it still fits Cold Mesa Bryce's theme.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.MID_INLAND)
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.COLD_MESA_BRYCE));

        // Cold Mesa Plateau - flat variant of Cold Mesa (tagged "rare"; replace_biomes amount
        // 0.15 - see class javadoc note on "rare" not implying frequency)
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.FAR_INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.COLD_MESA_PLATEAU));

        // Deep Dark Forest - bedrock temp=0.3, downfall=0.8, roofed/mega forest (tagged "rare";
        // replace_biomes amount 0.15 - see class javadoc)
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.COOL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.DEEP_DARK_FOREST));

        // Deep Dark Green - bedrock tags: caves, deep_dark, jungle; a cave variant of vanilla's Deep
        // Dark, not a surface biome. Matches OverworldBiomeBuilder.addUndergroundBiomes' exact Deep Dark
        // placement (full temp/humidity/continentalness range, erosion bands 0-1, depth=FLOOR i.e. the
        // bottom-of-world point vanilla uses for Deep Dark), but only takes half of the weirdness range
        // so vanilla deep_dark still generates in the other half - mirroring bedrock's
        // "replace_biomes" amount of 0.5 rather than fully replacing vanilla Deep Dark everywhere.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FULL_RANGE)
                .humidity(ParameterUtils.Humidity.FULL_RANGE)
                .continentalness(ParameterUtils.Continentalness.FULL_RANGE)
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1))
                .depth(ParameterUtils.Depth.FLOOR)
                .weirdness(Climate.Parameter.span(-1.0F, 0.0F))
                .build().forEach(point -> builder.add(point, ModBiomes.DEEP_DARK_GREEN));

        // Desert Bryce - bedrock temp=2, downfall=0, canyon desert (tagged "rare"; replace_biomes
        // amount 0.2). Low erosion, positive-weirdness half only (see class javadoc) - vanilla's
        // own Desert/Badlands reclaims the rest of this climate cell.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.NEUTRAL))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.NEAR_INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.DESERT_BRYCE));

        // Floating Jungle - bedrock temp=0.95, downfall=0.9 (tagged "rare"; replace_biomes
        // amount 0.2)
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.MID_INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_4, ParameterUtils.Erosion.EROSION_6))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.PEAK_NORMAL)
                .build().forEach(point -> builder.add(point, ModBiomes.FLOATING_JUNGLE));

        // Fungle Jungle - bedrock temp=0.95, downfall=0.9, mushroom_island tag (also tagged
        // "rare", but replace_biomes amount is 0.4 - one of the higher values in the set; see
        // class javadoc note on "rare" not implying frequency). Capped to NEAR_INLAND rather than
        // the wider INLAND alias (COAST=[-0.19,-0.11], NEAR_INLAND=[-0.11,0.03], but
        // INLAND=[-0.11,0.55] - a "numerically wide alias", not a synonym for NEAR_INLAND; see the
        // Charred Forest comment above) - spanning to INLAND left Jungle Pillars (which claims
        // INLAND..FAR_INLAND = [-0.11,1.0]) with only the sliver above continentalness 0.55 as
        // exclusive territory, too narrow to reliably generate within a normal search radius.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.NEAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.FUNGLE_JUNGLE));

        // Glacier - bedrock temp=0, downfall=1
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.GLACIER));

        // Grand Oasis - bedrock temp=2, downfall=0, oasis/warm tags
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_3, ParameterUtils.Erosion.EROSION_5))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.GRAND_OASIS));

        // Jungle Pillars - bedrock temp=0.95, downfall=0.9, stone_pillars tag (tagged "rare";
        // replace_biomes amount 0.15). Low erosion, positive-weirdness half only (see class
        // javadoc).
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.JUNGLE_PILLARS));

        // Lush Mesa - bedrock temp=2, downfall=0.7. Temperature is HOT, not WARM, to match every
        // other temp=2 biome across the two Regions (Charred Forest, Desert Bryce, Grand Oasis) -
        // it was
        // previously miscoded as WARM, which put its box (COAST..INLAND, erosion 3-6, HUMID..WET,
        // full weirdness) in the same temperature bucket as, and as a strict geometric superset
        // of, Jungle Marsh's box (COAST..NEAR_INLAND, erosion 4-6, WET..HUMID, full weirdness).
        // Climate.ParameterList resolves points with tied fitness (i.e. a query location that
        // falls inside both boxes) in favor of whichever point wins its internal tie-break -
        // consistently Lush Mesa here - so Jungle Marsh had no reachable territory at all despite
        // its own box looking reasonably sized in isolation. Moving Lush Mesa to HOT removes the
        // overlap entirely rather than trying to carve out exclusive territory by hand. Covers the
        // flat/rolling erosion band (3-6) at any weirdness, plus the negative-weirdness half of
        // the low-erosion band (0-2) - the same low erosion Lush Mesa Bryce claims for its
        // positive-weirdness half.
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

        // Lush Mesa Bryce - eroded spire variant of Lush Mesa; same low-erosion band as Lush Mesa
        // above, but only the positive-weirdness half, which is what renders as spires (see class
        // javadoc). Temperature is HOT to match Lush Mesa - see that comment.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.LUSH_MESA_BRYCE));

        // Shattered Swamp - bedrock temp=0.8, downfall=0.5. Low erosion, positive-weirdness half
        // only (see class javadoc) - this is what actually produces the tuff/stone spires poking
        // out of the swamp, matching the Bedrock reference art.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.NEAR_INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.SHATTERED_SWAMP));

        // Tiaga Spikes - bedrock temp=0, downfall=1. Capped to COAST-NEAR_INLAND (rather than
        // reaching to MID_INLAND) so it leaves room for Cold Mesa Bryce (MID_INLAND) - see Cold
        // Mesa Bryce comment above. (Shattered Taiga Spikes, formerly FAR_INLAND, now lives in
        // ModOverworldRegionRare - see that class's javadoc.)
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.NEAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_3))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.TAIGA_SPIKES));

        // Tropical Island - bedrock temp=1, downfall=0.9, beach/ocean/warm tags
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.COAST)
                .erosion(ParameterUtils.Erosion.FULL_RANGE)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.TROPICAL_ISLAND));

        // Volcanic Moss Tundra - bedrock temp=0.2, downfall=0.85, replacement of vanilla's
        // ice_plains (Snowy Plains). Not actually tagged "rare" in Bedrock, and its
        // replace_biomes amount (0.5) is the same top-frequency tier as Cold Mesa/Deep Dark
        // Green/Jungle Marsh - see class javadoc note on "rare" not implying frequency. Modeled
        // the same way as Deep Dark Green above regardless: restricted to half the weirdness
        // range rather than claiming the whole climate cell, so vanilla Snowy Plains still
        // generates in the other half.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_2, ParameterUtils.Erosion.EROSION_4))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(Climate.Parameter.span(-1.0F, 0.0F))
                .build().forEach(point -> builder.add(point, ModBiomes.VOLCANIC_MOSS_TUNDRA));

        // Add our points to the mapper
        builder.build().forEach(mapper::accept);
    }
}
