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
        Climate.Parameter lowErosion = ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2);
        Climate.Parameter normalWeirdness = ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.MID_SLICE_NORMAL_ASCENDING, ParameterUtils.Weirdness.LOW_SLICE_NORMAL_DESCENDING);
        Climate.Parameter variantWeirdness = ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.LOW_SLICE_VARIANT_ASCENDING, ParameterUtils.Weirdness.MID_SLICE_VARIANT_DESCENDING);

        // Charred Forest - bedrock temp=2, downfall=0.5 (hot, replaces pale garden/birch forest)
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.DRY))
                .continentalness(ParameterUtils.Continentalness.INLAND)
                .erosion(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1)
                .depth(ParameterUtils.Depth.span(ParameterUtils.Depth.SURFACE, ParameterUtils.Depth.FLOOR))
                .weirdness(ParameterUtils.Weirdness.span(ParameterUtils.Weirdness.PEAK_NORMAL, ParameterUtils.Weirdness.MID_SLICE_NORMAL_DESCENDING))
                .build().forEach(point -> builder.add(point, ModBiomes.CHARRED_FOREST));

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
        // Mesa) so it has room that's neither Taiga Spikes' (COAST-NEAR_INLAND) nor Shattered Taiga
        // Spikes' (FAR_INLAND) - all four frozen/dry-wet low-erosion biomes previously fought over
        // the same territory; each now gets its own continentalness slice.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.MID_INLAND)
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.COLD_MESA_BRYCE));

        // Cold Mesa Plateau - "rare"/flat variant of Cold Mesa
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.FAR_INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.COLD_MESA_PLATEAU));

        // Deep Dark Forest - bedrock temp=0.3, downfall=0.8 ("rare", roofed/mega forest)
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

        // Desert Bryce - bedrock temp=2, downfall=0 ("rare" canyon desert). Low erosion,
        // positive-weirdness half only (see class javadoc) - vanilla's own Desert/Badlands
        // reclaims the rest of this climate cell, which fits its Bedrock "rare" tag.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.ARID)
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.DESERT_BRYCE));

        // Floating Jungle - bedrock temp=0.95, downfall=0.9 ("rare")
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.HUMID)
                .continentalness(ParameterUtils.Continentalness.FAR_INLAND)
                .erosion(ParameterUtils.Erosion.EROSION_6, ParameterUtils.Erosion.EROSION_6)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.FLOATING_JUNGLE));

        // Fungle Jungle - bedrock temp=0.95, downfall=0.9, mushroom_island tag ("rare")
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.HUMID)
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.FUNGLE_JUNGLE));

        // Future Desert - bedrock temp=2, downfall=0 ("rare"). Restricted to the negative-weirdness
        // half of its erosion band so it doesn't fully swallow Desert Bryce, which shares this exact
        // temp/humidity/continentalness/erosion cell and claims the positive-weirdness half (see
        // class javadoc) - the two boxes were previously identical except for weirdness, and since
        // Future Desert's was FULL_RANGE it entirely contained Desert Bryce's, making Desert Bryce
        // unreachable.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.ARID)
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(normalWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.FUTURE_DESERT));

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

        // Jellyfish Fields - bedrock temp=0.5, downfall=0.5, ocean/warm tags
        // Confined to DEEP_OCEAN..OCEAN so it doesn't bleed into the COAST band (which is land/beach transition).
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.DEEP_OCEAN, ParameterUtils.Continentalness.OCEAN))
                .erosion(ParameterUtils.Erosion.FULL_RANGE)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.JELLYFISH_FIELDS));

        // Jungle Marsh - bedrock temp=0.95, downfall=0.9, jungle+swamp tags ("rare")
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.HUMID)
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.NEAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_4, ParameterUtils.Erosion.EROSION_6))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.JUNGLE_MARSH));

        // Jungle Pillars - bedrock temp=0.95, downfall=0.9, stone_pillars tag ("rare"). Low
        // erosion, positive-weirdness half only (see class javadoc).
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.HUMID)
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.JUNGLE_PILLARS));

        // Lush Mesa - bedrock temp=2, downfall=0.7. Covers the flat/rolling erosion band (3-6)
        // at any weirdness, plus the negative-weirdness half of the low-erosion band (0-2) - the
        // same low erosion Lush Mesa Bryce claims for its positive-weirdness half.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.HUMID, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_3, ParameterUtils.Erosion.EROSION_6))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.LUSH_MESA));
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.HUMID, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(normalWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.LUSH_MESA));

        // Lush Mesa Bryce - eroded spire variant of Lush Mesa; same low-erosion band as Lush Mesa
        // above, but only the positive-weirdness half, which is what renders as spires (see class
        // javadoc)
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.HUMID, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.INLAND))
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.LUSH_MESA_BRYCE));

        // Moorlands - bedrock temp=0.5, downfall=0.5, plains/river tags. Moved to EROSION_5-6
        // (rather than EROSION_0-3) so it doesn't overlap The Netherlands (EROSION_0-2) or The
        // Netherlands Mutated (EROSION_3-4), which together already claim the same temp/humidity/
        // continentalness cell across the whole EROSION_0-4 range - and fits Moorlands' theme fine,
        // since EROSION_5-6 is vanilla's flat/rolling band anyway.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.NEAR_INLAND, ParameterUtils.Continentalness.MID_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_5, ParameterUtils.Erosion.EROSION_6))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.MOORLANDS));

        // Mystic Forest - bedrock temp=0.95, downfall=0.9
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.HUMID, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_4))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.MYSTIC_FOREST));

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

        // Shattered Tiaga Spikes - mutated variant of Tiaga Spikes. Low erosion,
        // positive-weirdness half only (see class javadoc). Capped to FAR_INLAND (rather than
        // reaching down to MID_INLAND) so it leaves room for Cold Mesa Bryce (MID_INLAND) - see
        // Cold Mesa Bryce comment above.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.FROZEN)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.FAR_INLAND)
                .erosion(lowErosion)
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(variantWeirdness)
                .build().forEach(point -> builder.add(point, ModBiomes.SHATTERED_TAIGA_SPIKES));

        // The Netherlands - bedrock temp=0.5, downfall=0.5 (Dutch tulip fields/windmills, overworld)
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.NEAR_INLAND, ParameterUtils.Continentalness.MID_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.THE_NETHERLANDS));

        // The Netherlands Mutated
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.NEUTRAL)
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.NEAR_INLAND, ParameterUtils.Continentalness.MID_INLAND))
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_3, ParameterUtils.Erosion.EROSION_4))
                .depth(ParameterUtils.Depth.FULL_RANGE)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, ModBiomes.THE_NETHERLANDS_MUTATED));

        // Tiaga Spikes - bedrock temp=0, downfall=1. Capped to COAST-NEAR_INLAND (rather than
        // reaching to MID_INLAND) so it leaves room for Cold Mesa Bryce (MID_INLAND) and Shattered
        // Taiga Spikes (FAR_INLAND) - see Cold Mesa Bryce comment above.
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

        // Add our points to the mapper
        builder.build().forEach(mapper::accept);
    }
}
