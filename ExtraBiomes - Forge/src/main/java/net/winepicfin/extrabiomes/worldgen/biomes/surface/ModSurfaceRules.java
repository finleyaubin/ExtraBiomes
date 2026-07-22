package net.winepicfin.extrabiomes.worldgen.biomes.surface;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
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

                // --- Sandy biomes: top=sand, mid=sand ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.DESERT_BRYCE), SAND),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GRAND_OASIS), SAND),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.TROPICAL_ISLAND), SAND),

                // --- Future Desert: top=white concretepowder ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.FUTURE_DESERT), WHITE_CONCRETE_POWDER),

                // --- Glacier: top=snow, mid=ice ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.GLACIER),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SNOW_BLOCK),
                                ICE)),

                // --- Biomes whose Bedrock mid_material is bare stone instead of dirt:
                //     grass caps directly on stone, no dirt layer ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.JELLYFISH_FIELDS), grassOverStone),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.JUNGLE_PILLARS), grassOverStone),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.MOORLANDS), grassOverStone),

                // --- The Netherlands (mutated): top=dirt, no grass cap. The base (non-mutated)
                //     variant already matches the vanilla default (grass_block/dirt), so it has
                //     no explicit rule here. Both variants have a netherrack foundation instead
                //     of stone at depth; TODO: port with a stoneDepthCheck-based rule once the
                //     windmill/wheat/tulip features are ported (see TheNetherlands.java). ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.THE_NETHERLANDS_MUTATED),
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), DIRT))),

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
