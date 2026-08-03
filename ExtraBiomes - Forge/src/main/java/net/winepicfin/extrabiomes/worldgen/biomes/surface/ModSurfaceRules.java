package net.winepicfin.extrabiomes.worldgen.biomes.surface;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
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
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource STONE = makeStateRule(Blocks.STONE);
    private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.RuleSource SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.RuleSource ICE = makeStateRule(Blocks.ICE);
    private static final SurfaceRules.RuleSource WHITE_CONCRETE_POWDER = makeStateRule(Blocks.WHITE_CONCRETE_POWDER);
    private static final SurfaceRules.RuleSource NETHERRACK = makeStateRule(Blocks.NETHERRACK);
    private static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource PACKED_MUD = makeStateRule(Blocks.PACKED_MUD);
    private static final SurfaceRules.RuleSource MYCELIUM = makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.RuleSource MOSS_BLOCK = makeStateRule(Blocks.MOSS_BLOCK);
    private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);

    public static SurfaceRules.RuleSource makeRules()
    {
        SurfaceRules.ConditionSource isAtOrBelowWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);

        // grass on exposed land, dirt underwater -> used by biomes with a dirt mid layer (default vanilla behaviour)
        SurfaceRules.RuleSource grassOverDirt = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrBelowWaterLevel, GRASS_BLOCK), DIRT);
        // grass on exposed land, bare stone underneath -> used by biomes whose Bedrock mid_material is stone (no dirt layer)
        SurfaceRules.RuleSource grassOverStone = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrBelowWaterLevel, GRASS_BLOCK), STONE);

        return SurfaceRules.sequence(
                // --- Charred Forest: top=dirt, mid=dirt (burnt, grassless ground) ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.CHARRED_FOREST),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), DIRT))),

                // --- Mesa / Badlands family: top=red_sand, mid=hardened_clay -> vanilla banded terracotta ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.COLD_MESA), SurfaceRules.bandlands()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.COLD_MESA_BRYCE), SurfaceRules.bandlands()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.COLD_MESA_PLATEAU), SurfaceRules.bandlands()),

                // --- Lush Mesa family: top=grass over hardened_clay/stained_hardened_clay ->
                //     grass cap over the vanilla badlands terracotta banding ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.LUSH_MESA),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), GRASS_BLOCK)),
                                SurfaceRules.bandlands())),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.LUSH_MESA_BRYCE),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), GRASS_BLOCK)),
                                SurfaceRules.bandlands())),

                // --- Sandy biomes: top=sand, mid=sand for a few blocks, then fall through to the
                //     normal stone base. NOTE: these rules previously had no depth guard at all
                //     (just isBiome(X) -> SAND), which matches at every Y-level the surface pass
                //     visits - not just near the surface - so it replaced the ENTIRE column,
                //     stone and all, with sand. With nothing solid left to rest on, the whole
                //     column collapsed as soon as the chunk loaded. stoneDepthCheck bounds the
                //     sand to a shallow band like vanilla's own desert rule. ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.DESERT_BRYCE),
                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, CaveSurface.FLOOR), SAND)),
                // Grand Oasis also has two surface_material_adjustments patches layered inside that
                // same shallow band: top_material -> red_sand in one noise band (checked only at
                // the very top block, matching Bedrock's top_material scope), and
                // mid_material -> sandstone in another (applies anywhere in the band, matching
                // Bedrock's mid_material filling everything below/around the top).
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GRAND_OASIS),
                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, CaveSurface.FLOOR),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.MEDIUM_PATCH, 0.15, 0.3), RED_SAND)),
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.MEDIUM_PATCH, 0.45, 0.58), SANDSTONE),
                                        SAND))),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.TROPICAL_ISLAND),
                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, CaveSurface.FLOOR), SAND)),

                // --- Future Desert: top=white concretepowder ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.FUTURE_DESERT),
                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, false, CaveSurface.FLOOR), WHITE_CONCRETE_POWDER)),

                // --- Glacier: top=snow, mid=ice ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GLACIER),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SNOW_BLOCK),
                                ICE)),

                // --- Biomes whose Bedrock mid_material is bare stone instead of dirt:
                //     grass caps directly on stone, no dirt layer ---
                // Jellyfish Fields also has a "minecraft:surface_material_adjustments" patch:
                // sea_floor_material -> moss_block in a noise band, layered as a higher-priority
                // override on top of the submerged-floor case that would otherwise apply here.
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.JELLYFISH_FIELDS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.ifTrue(isAtOrBelowWaterLevel,
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoiseParameters.MEDIUM_PATCH, 0.1, 0.3), MOSS_BLOCK))),
                                grassOverStone)),
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

                // --- Reference vanilla jungle rule kept from the original file ---
                SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JUNGLE),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, grassOverDirt)))
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block)
    {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
