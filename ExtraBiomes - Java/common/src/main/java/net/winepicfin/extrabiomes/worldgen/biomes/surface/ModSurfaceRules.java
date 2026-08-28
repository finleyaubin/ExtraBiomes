package net.winepicfin.extrabiomes.worldgen.biomes.surface;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

/**
 * Surface rules derived from each biome's "minecraft:surface_builder" component in the
 * Bedrock BP (ExtraBiomes - Bedrock/packs/BP/biomes/*.biome.json).
 *
 * Bedrock's surface_builder gives us three materials per biome: top_material, mid_material
 * and foundation_material. Java's default deep-terrain material is already stone, so a rule
 * is only added below when a biome's top/mid materials differ from vanilla's default
 * grass_block-over-dirt. Biomes whose Bedrock top/mid already match that default (dirt
 * forests, jungles, plains-like biomes, etc.) intentionally have no entry here.
 * <p>
 * Some biomes additionally have a "minecraft:surface_material_adjustments" component: one or
 * more patchy top/mid/sea_floor material overrides gated by a Perlin noise band. Those are
 * layered on top of (checked before, in the same SurfaceRules.sequence) the base material rule
 * above, using {@link ModNoiseParameters}'s shared noises to control patch size - see that
 * class's javadoc for how Bedrock's per-adjustment noise_frequency_scale maps onto them.
 */
public class ModSurfaceRules {
    // Depth split for sand-topped biomes: top material for the shallow band, foundation for the deeper band below it, then normal stone resumes.
    private static final int TOP_DEPTH = 3;
    private static final int FOUNDATION_DEPTH = 10;

    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource STONE = makeStateRule(Blocks.STONE);
    private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.RuleSource SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.RuleSource ICE = makeStateRule(Blocks.ICE);
    private static final SurfaceRules.RuleSource WHITE_CONCRETE_POWDER = makeStateRule(Blocks.WHITE_CONCRETE_POWDER);
    private static final SurfaceRules.RuleSource WHITE_CONCRETE = makeStateRule(Blocks.WHITE_CONCRETE);
    private static final SurfaceRules.RuleSource NETHERRACK = makeStateRule(Blocks.NETHERRACK);
    // moisture=7 (not defaultBlockState's 0) so the whole field starts fully hydrated rather than
    // waiting on random ticks to notice the buried water pockets one at a time.
    private static final SurfaceRules.RuleSource FARMLAND = SurfaceRules.state(Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7));
    private static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource PACKED_MUD = makeStateRule(Blocks.PACKED_MUD);
    private static final SurfaceRules.RuleSource MYCELIUM = makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.RuleSource MOSS_BLOCK = makeStateRule(Blocks.MOSS_BLOCK);
    private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.RuleSource BLACK_SAND = makeStateRule(net.winepicfin.extrabiomes.block.ModBlocks.BLACK_SAND.get());
    private static final SurfaceRules.RuleSource BLACK_SANDSTONE = makeStateRule(net.winepicfin.extrabiomes.block.ModBlocks.BLACK_SANDSTONE.get());
    private static final SurfaceRules.RuleSource WHITE_GLAZED_TERRACOTTA = makeStateRule(Blocks.WHITE_GLAZED_TERRACOTTA);
    private static final SurfaceRules.RuleSource ORANGE_GLAZED_TERRACOTTA = makeStateRule(Blocks.ORANGE_GLAZED_TERRACOTTA);
    private static final SurfaceRules.RuleSource RED_GLAZED_TERRACOTTA = makeStateRule(Blocks.RED_GLAZED_TERRACOTTA);
    private static final SurfaceRules.RuleSource BLACK_GLAZED_TERRACOTTA = makeStateRule(Blocks.BLACK_GLAZED_TERRACOTTA);
    private static final SurfaceRules.RuleSource MAGENTA_TERRACOTTA = makeStateRule(Blocks.MAGENTA_TERRACOTTA);
    private static final SurfaceRules.RuleSource LIGHT_BLUE_TERRACOTTA = makeStateRule(Blocks.LIGHT_BLUE_TERRACOTTA);
    private static final SurfaceRules.RuleSource LIME_TERRACOTTA = makeStateRule(Blocks.LIME_TERRACOTTA);
    private static final SurfaceRules.RuleSource PINK_TERRACOTTA = makeStateRule(Blocks.PINK_TERRACOTTA);
    private static final SurfaceRules.RuleSource GRAY_TERRACOTTA = makeStateRule(Blocks.GRAY_TERRACOTTA);
    private static final SurfaceRules.RuleSource PURPLE_TERRACOTTA = makeStateRule(Blocks.PURPLE_TERRACOTTA);
    private static final SurfaceRules.RuleSource GREEN_TERRACOTTA = makeStateRule(Blocks.GREEN_TERRACOTTA);

    // Single-block-thick glazed terracotta layers spread through the deepslate range (rather than
    // one contiguous band at the bottom), each an entire uniform colour - white shows up most,
    // orange/red/black once each. Kept clear of the -59..-61 bedrock margin (see clearOfBedrock).
    private static final int[] GLAZED_LAYER_Y = {-8, -20, -32, -44, -56};

    // Extra single-block regular-terracotta layers, in colours bandlands()/the glazed layers above
    // don't already use (magenta/light_blue/lime/pink/gray/purple/green - white/orange/yellow/
    // light_gray/cyan/blue/brown/red/black/plain terracotta are all already in play elsewhere).
    // Split into three depth tiers with more layers the deeper the tier (1, then 2, then 4), same
    // "clear of bedrock" limit as the rest, and offset from GLAZED_LAYER_Y so nothing overlaps.
    private static final int[] EXTRA_TERRACOTTA_LAYER_Y = {-5, -24, -30, -40, -46, -49, -52};

    // bandlands() has no built-in depth guard and can match down into the randomized bedrock layer (y=-64 to -59), so this gate keeps it clear.
    private static SurfaceRules.ConditionSource clearOfBedrock()
    {
        return SurfaceRules.not(SurfaceRules.verticalGradient(
                "extrabiomes_mesa_bedrock_margin",
                VerticalAnchor.absolute(-61), VerticalAnchor.absolute(-59)));
    }

    // Checked ahead of bandlands() in each sequence, so each single-block layer wins over the
    // usual banding at that exact Y only. Glazed: white at 2 of the 5 layers, orange/red/black once
    // each. Regular terracotta: colours bandlands() doesn't already use, more layers the deeper the
    // tier (1 shallow, 2 mid, 4 deep).
    private static SurfaceRules.RuleSource depthBands()
    {
        SurfaceRules.RuleSource glazedTerracottaBand = SurfaceRules.sequence(
                singleYBand(GLAZED_LAYER_Y[0], WHITE_GLAZED_TERRACOTTA),
                singleYBand(GLAZED_LAYER_Y[1], ORANGE_GLAZED_TERRACOTTA),
                singleYBand(GLAZED_LAYER_Y[2], BLACK_GLAZED_TERRACOTTA),
                singleYBand(GLAZED_LAYER_Y[3], RED_GLAZED_TERRACOTTA),
                singleYBand(GLAZED_LAYER_Y[4], WHITE_GLAZED_TERRACOTTA));

        SurfaceRules.RuleSource extraTerracottaBands = SurfaceRules.sequence(
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[0], MAGENTA_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[1], LIGHT_BLUE_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[2], LIME_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[3], PINK_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[4], GRAY_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[5], PURPLE_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[6], GREEN_TERRACOTTA));

        return SurfaceRules.sequence(extraTerracottaBands, glazedTerracottaBand);
    }

    /**
     * TerraBlender's {@code addSurfaceRules} only ever applies {@link #makeRules()} to biomes whose
     * registry key namespace is this mod's own ("extrabiomes") - vanilla's badlands/eroded_badlands/
     * wooded_badlands (namespace "minecraft") always use vanilla's own default ruleset instead,
     * tags or not. To reach them too, this is registered separately via
     * {@code SurfaceRuleManager.addToDefaultSurfaceRulesAtStage}, which injects into that shared
     * default ruleset used by every biome that doesn't have its own namespaced entry - so it must
     * scope itself to the three vanilla badlands biomes explicitly.
     * <p>
     * TerraBlender's own copy of vanilla's badlands rules only bands terracotta near the local
     * generated surface (it's nested inside an {@code abovePreliminarySurface()} check deep in that
     * default ruleset) - deep caves stay plain stone/deepslate there. This runs earlier in the
     * overall sequence, unwrapped, exactly like {@link #makeRules()} already does for this mod's own
     * badlands biomes, so {@code bandlands()} bands the full depth here too; the later, surface-only
     * vanilla copy just never gets reached for these three biomes once this matches.
     */
    public static SurfaceRules.RuleSource makeVanillaBadlandsAdditions()
    {
        return SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                SurfaceRules.ifTrue(clearOfBedrock(), SurfaceRules.sequence(depthBands(), SurfaceRules.bandlands())));
    }

    public static SurfaceRules.RuleSource makeRules()
    {
        SurfaceRules.ConditionSource isAtOrBelowWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);
        // waterBlockCheck is true when dry (at/above the water table), so "submerged" needs the negation.
        SurfaceRules.ConditionSource isSubmerged = SurfaceRules.not(SurfaceRules.waterBlockCheck(0, 0));

        // grass on exposed land, dirt underwater -> used by biomes with a dirt mid layer (default vanilla behaviour)
        SurfaceRules.RuleSource grassOverDirt = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrBelowWaterLevel, GRASS_BLOCK), DIRT));
        // grass on exposed land, bare stone underneath -> used by biomes whose Bedrock mid_material is stone (no dirt layer)
        SurfaceRules.RuleSource grassOverStone = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrBelowWaterLevel, GRASS_BLOCK), STONE));

        SurfaceRules.ConditionSource clearOfBedrock = clearOfBedrock();
        SurfaceRules.RuleSource depthBands = depthBands();

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.CHARRED_FOREST),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), DIRT))),

                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.COLD_MESA),
                        SurfaceRules.ifTrue(clearOfBedrock, SurfaceRules.sequence(depthBands, SurfaceRules.bandlands()))),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.COLD_MESA_BRYCE),
                        SurfaceRules.ifTrue(clearOfBedrock, SurfaceRules.sequence(depthBands, SurfaceRules.bandlands()))),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.COLD_MESA_PLATEAU),
                        SurfaceRules.ifTrue(clearOfBedrock, SurfaceRules.sequence(depthBands, SurfaceRules.bandlands()))),

                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.LUSH_MESA),
                        SurfaceRules.ifTrue(clearOfBedrock, SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), GRASS_BLOCK)),
                                depthBands,
                                SurfaceRules.bandlands()))),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.LUSH_MESA_BRYCE),
                        SurfaceRules.ifTrue(clearOfBedrock, SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), GRASS_BLOCK)),
                                depthBands,
                                SurfaceRules.bandlands()))),

                // These sand rules previously had no depth guard (just isBiome(X) -> SAND), which replaced the entire column including load-bearing stone and collapsed the chunk on load.
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.DESERT_BRYCE), sandOverFoundation(SAND, SANDSTONE)),
                // red_sand is checked only at the very top block (Bedrock's top_material scope); sandstone matches anywhere in the band (mid_material scope).
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GRAND_OASIS),
                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(TOP_DEPTH, false, CaveSurface.FLOOR),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.MEDIUM_PATCH, 0.15, 0.3), RED_SAND)),
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.MEDIUM_PATCH, 0.45, 0.58), SANDSTONE),
                                                SAND)),
                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(FOUNDATION_DEPTH, false, CaveSurface.FLOOR), SANDSTONE)))),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.TROPICAL_ISLAND), sandOverFoundation(SAND, SANDSTONE)),

                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.FUTURE_DESERT),
                        sandOverFoundation(WHITE_CONCRETE_POWDER, WHITE_CONCRETE)),

                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GLACIER),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SNOW_BLOCK),
                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, CaveSurface.FLOOR), ICE))),

                // moss_block patch is sequenced before the submerged fallback so it takes priority when its noise band matches.
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.JELLYFISH_FIELDS),
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(isSubmerged,
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.MEDIUM_PATCH, 0.1, 0.3), MOSS_BLOCK)),
                                grassOverStone))),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.JUNGLE_PILLARS), grassOverStone),
                // mud patch is sequenced before grassOverStone so it takes priority when its noise band matches.
                // abovePreliminarySurface() keeps the patch off cave floors, since stoneDepthCheck/ON_FLOOR alone also match those underground.
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.MOORLANDS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(),
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.LARGE_PATCH, 0.50, 0.6), MUD))),
                                grassOverStone)),

                // No base rule needed (Bedrock top/mid already match vanilla default grass/dirt); only the mycelium noise patch is added.
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.FUNGLE_JUNGLE),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.SMALL_PATCH, 0.2, 0.4), MYCELIUM))),

                // No base rule needed (top/mid already match vanilla default); two noise bands split nearly the whole range between packed_mud and mud.
                // abovePreliminarySurface() keeps these off cave floors, since ON_FLOOR alone also matches underground cave floors.
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.DEEP_DARK_FOREST),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.REGIONAL_BAND, 0.212, 1.0), PACKED_MUD),
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.REGIONAL_BAND, -0.115, 0.212), MUD))))),

                // Netherrack band capped to 30 blocks (unlike Bedrock's full-column replace), so vanilla cave carving resumes above bedrock and no custom carver is needed on Java.
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_NETHERLANDS),
                        SurfaceRules.sequence(
                                grassOverDirt,
                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(30, false, CaveSurface.FLOOR), NETHERRACK))),

                // Top layer is FARMLAND, not DIRT, so the whole floor is tillable ground and NetherlandsWheatFeatures'
                // crop scatter never has to convert terrain itself - it just needs a wheat block on top of every
                // column, so there are no untouched-dirt gaps between its (inherently probabilistic) patches.
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_NETHERLANDS_MUTATED),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), FARMLAND)),
                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(30, false, CaveSurface.FLOOR), NETHERRACK))),

                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.VOLCANIC_MOSS_TUNDRA),
                        sandOverFoundation(BLACK_SAND, BLACK_SANDSTONE)),

                // Reference vanilla jungle rule kept from the original file.
                SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JUNGLE),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, grassOverDirt)))
        );
    }

    // yBlockCheck(anchor, 0) is true when blockY >= anchor, so this pair bounds an exact single-Y layer.
    private static SurfaceRules.RuleSource singleYBand(int y, SurfaceRules.RuleSource color)
    {
        return SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(y), 0),
                SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(y + 1), 0)), color));
    }

    private static SurfaceRules.RuleSource sandOverFoundation(SurfaceRules.RuleSource top, SurfaceRules.RuleSource foundation)
    {
        // stoneDepthCheck(..., CaveSurface.FLOOR) also matches cave floors underground, so abovePreliminarySurface() is needed to keep this off cave walls.
        return SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(TOP_DEPTH, false, CaveSurface.FLOOR), top),
                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(FOUNDATION_DEPTH, false, CaveSurface.FLOOR), foundation)));
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block)
    {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
