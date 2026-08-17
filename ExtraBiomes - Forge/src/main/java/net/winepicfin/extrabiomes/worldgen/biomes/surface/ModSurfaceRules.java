package net.winepicfin.extrabiomes.worldgen.biomes.surface;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
    // Shared depth split for sand-topped biomes: top material for the shallow band, that biome's
    // "sandstone" foundation for the deeper band below it, then normal stone resumes.
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
    private static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource PACKED_MUD = makeStateRule(Blocks.PACKED_MUD);
    private static final SurfaceRules.RuleSource MYCELIUM = makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.RuleSource MOSS_BLOCK = makeStateRule(Blocks.MOSS_BLOCK);
    private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.RuleSource BLACK_SAND = makeStateRule(net.winepicfin.extrabiomes.block.ModBlocks.BLACK_SAND.get());
    private static final SurfaceRules.RuleSource BLACK_SANDSTONE = makeStateRule(net.winepicfin.extrabiomes.block.ModBlocks.BLACK_SANDSTONE.get());

    public static SurfaceRules.RuleSource makeRules()
    {
        SurfaceRules.ConditionSource isAtOrBelowWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);
        // waterBlockCheck is true when the position is AT/ABOVE the water table (dry), false when
        // genuinely submerged - so "submerged" needs the negation, not the check itself.
        SurfaceRules.ConditionSource isSubmerged = SurfaceRules.not(SurfaceRules.waterBlockCheck(0, 0));

        // grass on exposed land, dirt underwater -> used by biomes with a dirt mid layer (default vanilla behaviour)
        SurfaceRules.RuleSource grassOverDirt = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrBelowWaterLevel, GRASS_BLOCK), DIRT));
        // grass on exposed land, bare stone underneath -> used by biomes whose Bedrock mid_material is stone (no dirt layer)
        SurfaceRules.RuleSource grassOverStone = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrBelowWaterLevel, GRASS_BLOCK), STONE));

        // SurfaceRules.bandlands() (vanilla's own badlands terracotta banding) has no built-in
        // depth guard - like the earlier unguarded ICE/grassOverStone bugs, it can match at every
        // Y the surface pass scans in a mesa biome, including right down into the world's bottom,
        // randomized 1-5-block bedrock layer (starts at y=-64). verticalGradient gives a smooth
        // noise-blended transition rather than a razor-flat cutoff (matching how vanilla itself
        // blends its own stone/deepslate transition), with the blend band placed just above the
        // guaranteed-safe threshold so no part of it dips into where bedrock actually generates.
        SurfaceRules.ConditionSource clearOfBedrock = SurfaceRules.not(SurfaceRules.verticalGradient(
                "extrabiomes_mesa_bedrock_margin",
                VerticalAnchor.absolute(-61), VerticalAnchor.absolute(-59)));

        return SurfaceRules.sequence(
                // --- Charred Forest: top=dirt, mid=dirt (burnt, grassless ground) ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.CHARRED_FOREST),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), DIRT))),

                // --- Mesa / Badlands family: top=red_sand, mid=hardened_clay -> vanilla banded terracotta ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.COLD_MESA),
                        SurfaceRules.ifTrue(clearOfBedrock, SurfaceRules.bandlands())),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.COLD_MESA_BRYCE),
                        SurfaceRules.ifTrue(clearOfBedrock, SurfaceRules.bandlands())),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.COLD_MESA_PLATEAU),
                        SurfaceRules.ifTrue(clearOfBedrock, SurfaceRules.bandlands())),

                // --- Lush Mesa family: top=grass over hardened_clay/stained_hardened_clay ->
                //     grass cap over the vanilla badlands terracotta banding ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.LUSH_MESA),
                        SurfaceRules.ifTrue(clearOfBedrock, SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), GRASS_BLOCK)),
                                SurfaceRules.bandlands()))),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.LUSH_MESA_BRYCE),
                        SurfaceRules.ifTrue(clearOfBedrock, SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), GRASS_BLOCK)),
                                SurfaceRules.bandlands()))),

                // --- Sandy biomes: top material for a shallow band, then that biome's own
                //     "sandstone" foundation for a deeper band below it, then fall through to the
                //     normal stone base - top=3 blocks deep, foundation=the next 7 (so normal
                //     terrain resumes at depth 10). NOTE: these rules previously had no depth guard
                //     at all (just isBiome(X) -> SAND), which matches at every Y-level the surface
                //     pass visits - not just near the surface - so it replaced the ENTIRE column,
                //     stone and all, with sand. With nothing solid left to rest on, the whole
                //     column collapsed as soon as the chunk loaded. ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.DESERT_BRYCE), sandOverFoundation(SAND, SANDSTONE)),
                // Grand Oasis also has two surface_material_adjustments patches layered inside the
                // shallow top band: top_material -> red_sand in one noise band (checked only at
                // the very top block, matching Bedrock's top_material scope), and
                // mid_material -> sandstone in another (applies anywhere in the band, matching
                // Bedrock's mid_material filling everything below/around the top) - the deeper
                // foundation band below reuses the same sandstone rule.
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

                // --- Future Desert: top=white concretepowder, foundation=white concrete (its
                //     solidified "sandstone" equivalent) ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.FUTURE_DESERT),
                        sandOverFoundation(WHITE_CONCRETE_POWDER, WHITE_CONCRETE)),

                // --- Glacier: top=snow, mid=ice ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GLACIER),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SNOW_BLOCK),
                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, CaveSurface.FLOOR), ICE))),

                // --- Biomes whose Bedrock mid_material is bare stone instead of dirt:
                //     grass caps directly on stone, no dirt layer ---
                // Jellyfish Fields also has a "minecraft:surface_material_adjustments" patch:
                // sea_floor_material -> moss_block in a noise band, layered as a higher-priority
                // override on top of the submerged-floor case that would otherwise apply here.
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.JELLYFISH_FIELDS),
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(isSubmerged,
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.MEDIUM_PATCH, 0.1, 0.3), MOSS_BLOCK)),
                                grassOverStone))),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.JUNGLE_PILLARS), grassOverStone),
                // Moorlands also has a surface_material_adjustments patch: top_material -> mud in a
                // noise band, overriding the grass cap wherever the noise matches.
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.MOORLANDS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.LARGE_PATCH, 0.50, 0.6), MUD)),
                                grassOverStone)),

                // --- Fungle Jungle: no base entry needed (Bedrock top/mid already match vanilla's
                //     default grass/dirt), but it has a surface_material_adjustments patch:
                //     top_material -> mycelium in a noise band. ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.FUNGLE_JUNGLE),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.SMALL_PATCH, 0.2, 0.4), MYCELIUM))),

                // --- Deep Dark Forest: no base entry needed (top/mid already match vanilla's
                //     default grass/dirt), but it has two surface_material_adjustments patches
                //     covering nearly the whole noise range between them: top_material -> packed_mud
                //     for the majority band, top_material -> mud for the rest. ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.DEEP_DARK_FOREST),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.REGIONAL_BAND, 0.212, 1.0), PACKED_MUD),
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.REGIONAL_BAND, -0.115, 0.212), MUD)))),

                // --- The Netherlands: top=grass/dirt (base) or dirt with no grass cap (mutated),
                //     but both variants have a netherrack foundation instead of stone at depth
                //     ("nethrack" pun) - stoneDepthCheck bounds the top layer to a shallow band,
                //     same pattern as the sandy biomes above, then netherrack fills the rest. ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_NETHERLANDS),
                        SurfaceRules.sequence(
                                grassOverDirt,
                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, CaveSurface.FLOOR), NETHERRACK))),

                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_NETHERLANDS_MUTATED),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), DIRT)),
                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, CaveSurface.FLOOR), NETHERRACK))),

                // --- Volcanic Moss Tundra: top=mid=black_sand, foundation=black_sandstone instead
                //     of stone - same shallow-top/deeper-foundation pattern as the sandy biomes
                //     above (black_sand for 3 blocks, black_sandstone for the next 7, then normal
                //     stone resumes). ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.VOLCANIC_MOSS_TUNDRA),
                        sandOverFoundation(BLACK_SAND, BLACK_SANDSTONE)),

                // --- Reference vanilla jungle rule kept from the original file ---
                SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JUNGLE),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, grassOverDirt)))
        );
    }

    private static SurfaceRules.RuleSource sandOverFoundation(SurfaceRules.RuleSource top, SurfaceRules.RuleSource foundation)
    {
        // stoneDepthCheck(..., CaveSurface.FLOOR) matches ANY solid-to-air floor transition,
        // including cave floors/ceilings underground - not just the real world surface. Without
        // gating on abovePreliminarySurface(), this band would also paint sand/sandstone onto
        // cave walls far below the surface.
        return SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(TOP_DEPTH, false, CaveSurface.FLOOR), top),
                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(FOUNDATION_DEPTH, false, CaveSurface.FLOOR), foundation)));
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block)
    {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
