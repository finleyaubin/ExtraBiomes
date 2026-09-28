package net.winepicfin.extrabiomes.worldgen.biomes.surface;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
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
 * layered on top of (checked before, in the same MaterialRules.sequence) the base material rule
 * above, using {@link ModNoiseParameters}'s shared noises to control patch size - see that
 * class's javadoc for how Bedrock's per-adjustment noise_frequency_scale maps onto them.
 */
public class ModSurfaceRules {
    // Depth split for sand-topped biomes: top material for the shallow band, foundation for the deeper band below it, then normal stone resumes.
    private static final int TOP_DEPTH = 3;
    private static final int FOUNDATION_DEPTH = 10;

    private static final MaterialRule DIRT = makeStateRule(Blocks.DIRT);
    private static final MaterialRule GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final MaterialRule STONE = makeStateRule(Blocks.STONE);
    private static final MaterialRule SAND = makeStateRule(Blocks.SAND);
    private static final MaterialRule RED_SAND = makeStateRule(Blocks.RED_SAND);
    private static final MaterialRule SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
    private static final MaterialRule ICE = makeStateRule(Blocks.ICE);
    private static final MaterialRule WHITE_CONCRETE_POWDER = makeStateRule(Blocks.CONCRETE_POWDER.white());
    private static final MaterialRule WHITE_CONCRETE = makeStateRule(Blocks.CONCRETE.white());
    private static final MaterialRule NETHERRACK = makeStateRule(Blocks.NETHERRACK);
    private static final MaterialRule BEDROCK = makeStateRule(Blocks.BEDROCK);
    // moisture=7 (not defaultBlockState's 0) so the whole field starts fully hydrated rather than
    // waiting on random ticks to notice the buried water pockets one at a time.
    private static final MaterialRule FARMLAND = MaterialRules.state(Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 7));
    private static final MaterialRule MUD = makeStateRule(Blocks.MUD);
    private static final MaterialRule PACKED_MUD = makeStateRule(Blocks.PACKED_MUD);
    private static final MaterialRule MYCELIUM = makeStateRule(Blocks.MYCELIUM);
    private static final MaterialRule GRASS_STONE = makeStateRule(net.winepicfin.extrabiomes.block.ModBlocks.GRASS_STONE.get());
    private static final MaterialRule MOSS_BLOCK = makeStateRule(Blocks.MOSS_BLOCK);
    private static final MaterialRule SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    private static final MaterialRule BLACK_SAND = makeStateRule(net.winepicfin.extrabiomes.block.ModBlocks.BLACK_SAND.get());
    private static final MaterialRule BLACK_SANDSTONE = makeStateRule(net.winepicfin.extrabiomes.block.ModBlocks.BLACK_SANDSTONE.get());
    private static final MaterialRule WHITE_GLAZED_TERRACOTTA = makeStateRule(Blocks.GLAZED_TERRACOTTA.white());
    private static final MaterialRule ORANGE_GLAZED_TERRACOTTA = makeStateRule(Blocks.GLAZED_TERRACOTTA.orange());
    private static final MaterialRule RED_GLAZED_TERRACOTTA = makeStateRule(Blocks.GLAZED_TERRACOTTA.red());
    private static final MaterialRule BLACK_GLAZED_TERRACOTTA = makeStateRule(Blocks.GLAZED_TERRACOTTA.black());
    private static final MaterialRule MAGENTA_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.magenta());
    private static final MaterialRule LIGHT_BLUE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.lightBlue());
    private static final MaterialRule LIME_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.lime());
    private static final MaterialRule PINK_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.pink());
    private static final MaterialRule GRAY_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.gray());
    private static final MaterialRule PURPLE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.purple());
    private static final MaterialRule GREEN_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.green());

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
    private static MaterialCondition clearOfBedrock()
    {
        return MaterialRules.not(MaterialRules.verticalGradient(
                "extrabiomes_mesa_bedrock_margin",
                VerticalAnchor.absolute(-61), VerticalAnchor.absolute(-59)));
    }

    // Checked ahead of bandlands() in each sequence, so each single-block layer wins over the
    // usual banding at that exact Y only. Glazed: white at 2 of the 5 layers, orange/red/black once
    // each. Regular terracotta: colours bandlands() doesn't already use, more layers the deeper the
    // tier (1 shallow, 2 mid, 4 deep).
    private static MaterialRule depthBands()
    {
        MaterialRule glazedTerracottaBand = MaterialRules.sequence(
                singleYBand(GLAZED_LAYER_Y[0], WHITE_GLAZED_TERRACOTTA),
                singleYBand(GLAZED_LAYER_Y[1], ORANGE_GLAZED_TERRACOTTA),
                singleYBand(GLAZED_LAYER_Y[2], BLACK_GLAZED_TERRACOTTA),
                singleYBand(GLAZED_LAYER_Y[3], RED_GLAZED_TERRACOTTA),
                singleYBand(GLAZED_LAYER_Y[4], WHITE_GLAZED_TERRACOTTA));

        MaterialRule extraTerracottaBands = MaterialRules.sequence(
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[0], MAGENTA_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[1], LIGHT_BLUE_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[2], LIME_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[3], PINK_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[4], GRAY_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[5], PURPLE_TERRACOTTA),
                singleYBand(EXTRA_TERRACOTTA_LAYER_Y[6], GREEN_TERRACOTTA));

        return MaterialRules.sequence(extraTerracottaBands, glazedTerracottaBand);
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
    public static MaterialRule makeVanillaBadlandsAdditions(HolderGetter<Biome> biomes)
    {
        return MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                MaterialRules.ifTrue(clearOfBedrock(), MaterialRules.sequence(depthBands(), MaterialRules.bandlands())));
    }

    public static MaterialRule makeRules(HolderGetter<Biome> biomes)
    {
        MaterialCondition isAtOrBelowWaterLevel = MaterialRules.waterBlockCheck(-1, 0);
        // waterBlockCheck is true when dry (at/above the water table), so "submerged" needs the negation.
        MaterialCondition isSubmerged = MaterialRules.not(MaterialRules.waterBlockCheck(0, 0));

        // grass on exposed land, dirt underwater -> used by biomes with a dirt mid layer (default vanilla behaviour)
        MaterialRule grassOverDirt = MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                MaterialRules.sequence(MaterialRules.ifTrue(isAtOrBelowWaterLevel, GRASS_BLOCK), DIRT));
        // grass on exposed land, bare stone underneath -> used by biomes whose Bedrock mid_material is stone (no dirt layer)
        MaterialRule grassOverStone = MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                MaterialRules.sequence(MaterialRules.ifTrue(isAtOrBelowWaterLevel, GRASS_BLOCK), STONE));

        MaterialCondition clearOfBedrock = clearOfBedrock();
        MaterialRule depthBands = depthBands();
        // Our rules run before vanilla's bedrock rule, so it's repeated here (same gradient name = same bedrock pattern) or netherrack would replace it.
        MaterialRule netherrackDownToBedrock = MaterialRules.sequence(
                MaterialRules.ifTrue(MaterialRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK),
                NETHERRACK);

        return MaterialRules.sequence(
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.CHARRED_FOREST),
                        MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(), DIRT))),

                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.COLD_MESA),
                        MaterialRules.ifTrue(clearOfBedrock, MaterialRules.sequence(depthBands, MaterialRules.bandlands()))),
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.COLD_MESA_BRYCE),
                        MaterialRules.ifTrue(clearOfBedrock, MaterialRules.sequence(depthBands, MaterialRules.bandlands()))),
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.COLD_MESA_PLATEAU),
                        MaterialRules.ifTrue(clearOfBedrock, MaterialRules.sequence(depthBands, MaterialRules.bandlands()))),

                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.LUSH_MESA),
                        MaterialRules.ifTrue(clearOfBedrock, MaterialRules.sequence(
                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                        MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(), GRASS_BLOCK)),
                                depthBands,
                                MaterialRules.bandlands()))),
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.LUSH_MESA_BRYCE),
                        MaterialRules.ifTrue(clearOfBedrock, MaterialRules.sequence(
                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                        MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(), GRASS_BLOCK)),
                                depthBands,
                                MaterialRules.bandlands()))),

                // These sand rules previously had no depth guard (just isBiome(X) -> SAND), which replaced the entire column including load-bearing stone and collapsed the chunk on load.
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.DESERT_BRYCE), sandOverFoundation(SAND, SANDSTONE)),
                // red_sand is checked only at the very top block (Bedrock's top_material scope); sandstone matches anywhere in the band (mid_material scope).
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.GRAND_OASIS),
                        MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(), MaterialRules.sequence(
                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(TOP_DEPTH, false, CaveSurface.FLOOR),
                                        MaterialRules.sequence(
                                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                                        MaterialRules.ifTrue(MaterialRules.noiseCondition2d(ModNoiseParameters.MEDIUM_PATCH, 0.15, 0.3), RED_SAND)),
                                                MaterialRules.ifTrue(MaterialRules.noiseCondition2d(ModNoiseParameters.MEDIUM_PATCH, 0.45, 0.58), SANDSTONE),
                                                SAND)),
                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(FOUNDATION_DEPTH, false, CaveSurface.FLOOR), SANDSTONE)))),
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.TROPICAL_ISLAND), sandOverFoundation(SAND, SANDSTONE)),

                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.FUTURE_DESERT),
                        sandOverFoundation(WHITE_CONCRETE_POWDER, WHITE_CONCRETE)),

                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.GLACIER),
                        MaterialRules.sequence(
                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), SNOW_BLOCK),
                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(4, false, CaveSurface.FLOOR), ICE))),

                // moss_block patch is sequenced before the grass_stone sea floor so it takes priority when its noise band matches.
                // abovePreliminarySurface() keeps grass, grass stone and moss off cave floors, which ON_FLOOR alone also matches.
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.JELLYFISH_FIELDS),
                        MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(),
                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                        MaterialRules.sequence(
                                        MaterialRules.ifTrue(isSubmerged,
                                                MaterialRules.sequence(
                                                        MaterialRules.ifTrue(MaterialRules.noiseCondition2d(ModNoiseParameters.MEDIUM_PATCH, 0.1, 0.3), MOSS_BLOCK),
                                                        GRASS_STONE)),
                                grassOverStone)))),
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.JUNGLE_PILLARS), grassOverStone),
                // mud patch is sequenced before grassOverStone so it takes priority when its noise band matches.
                // abovePreliminarySurface() keeps the patch off cave floors, since stoneDepthCheck/ON_FLOOR alone also match those underground.
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.MOORLANDS),
                        MaterialRules.sequence(
                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                        MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(),
                                                MaterialRules.ifTrue(MaterialRules.noiseCondition2d(ModNoiseParameters.LARGE_PATCH, 0.50, 0.6), MUD))),
                                grassOverStone)),

                // No base rule needed (Bedrock top/mid already match vanilla default grass/dirt); only the mycelium noise patch is added.
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.FUNGLE_JUNGLE),
                        MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                MaterialRules.ifTrue(MaterialRules.noiseCondition2d(ModNoiseParameters.SMALL_PATCH, 0.2, 0.4), MYCELIUM))),

                // No base rule needed (top/mid already match vanilla default); two noise bands split nearly the whole range between packed_mud and mud.
                // abovePreliminarySurface() keeps these off cave floors, since ON_FLOOR alone also matches underground cave floors.
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.DEEP_DARK_FOREST),
                        MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(),
                                        MaterialRules.sequence(
                                                MaterialRules.ifTrue(MaterialRules.noiseCondition2d(ModNoiseParameters.REGIONAL_BAND, 0.212, 1.0), PACKED_MUD),
                                                MaterialRules.ifTrue(MaterialRules.noiseCondition2d(ModNoiseParameters.REGIONAL_BAND, -0.115, 0.212), MUD))))),

                // abovePreliminarySurface() keeps grass off cave floors, which ON_FLOOR alone also matches.
                // No dirt fallback underwater: Bedrock's sea_floor_depth is 0 here, so sea floors are bare netherrack.
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.THE_NETHERLANDS),
                        MaterialRules.sequence(
                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                        MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(),
                                                MaterialRules.ifTrue(isAtOrBelowWaterLevel, GRASS_BLOCK))),
                                netherrackDownToBedrock)),

                // Top layer is FARMLAND, not DIRT, so the whole floor is tillable ground and NetherlandsWheatFeatures'
                // crop scatter never has to convert terrain itself - it just needs a wheat block on top of every
                // column, so there are no untouched-dirt gaps between its (inherently probabilistic) patches.
                // Gated to dry columns only (isAtOrBelowWaterLevel) - without this, low points of this biome that
                // dip below sea level got farmland tilled straight onto the sea floor, since
                // ON_FLOOR/abovePreliminarySurface() alone don't distinguish dry land from underwater. Submerged
                // floors fall through to netherrack, matching Bedrock's sea_floor_depth of 0.
                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.THE_NETHERLANDS_MUTATED),
                        MaterialRules.sequence(
                                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                        MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(),
                                                MaterialRules.ifTrue(isAtOrBelowWaterLevel, FARMLAND))),
                                netherrackDownToBedrock)),

                MaterialRules.ifTrue(MaterialRules.isBiome(biomes, ModBiomes.VOLCANIC_MOSS_TUNDRA),
                        sandOverFoundation(BLACK_SAND, BLACK_SANDSTONE)),

                // Reference vanilla jungle rule kept from the original file.
                MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.JUNGLE),
                        MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR), grassOverDirt)))
        );
    }

    // yBlockCheck(anchor, 0) is true when blockY >= anchor, so this pair bounds an exact single-Y layer.
    private static MaterialRule singleYBand(int y, MaterialRule color)
    {
        return MaterialRules.ifTrue(MaterialRules.yBlockCheck(VerticalAnchor.absolute(y), 0),
                MaterialRules.ifTrue(MaterialRules.not(MaterialRules.yBlockCheck(VerticalAnchor.absolute(y + 1), 0)), color));
    }

    private static MaterialRule sandOverFoundation(MaterialRule top, MaterialRule foundation)
    {
        // stoneDepthCheck(..., CaveSurface.FLOOR) also matches cave floors underground, so abovePreliminarySurface() is needed to keep this off cave walls.
        return MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(), MaterialRules.sequence(
                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(TOP_DEPTH, false, CaveSurface.FLOOR), top),
                MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(FOUNDATION_DEPTH, false, CaveSurface.FLOOR), foundation)));
    }

    private static MaterialRule makeStateRule(Block block)
    {
        return MaterialRules.state(block.defaultBlockState());
    }
}
