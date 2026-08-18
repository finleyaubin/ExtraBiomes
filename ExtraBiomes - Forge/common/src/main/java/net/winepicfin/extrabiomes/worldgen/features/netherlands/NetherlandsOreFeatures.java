package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.worldgen.features.ore.ModOrePlacement;

/**
 * The nine ore veins from Bedrock's "extrabiomes:the_netherlands/*_ore_feature" ({@code minecraft:ore_feature})
 * plus their "extrabiomes:netherlands_underground_*_ore_feature" feature_rules (placement_pass "underground_pass").
 * <p>
 * Bedrock ore_feature's "count" is the vein SIZE (blocks per vein) -> {@link OreConfiguration}'s size parameter.
 * The matching feature_rules' "iterations" is the number of vein ATTEMPTS per chunk -> {@link ModOrePlacement#commonOrePlacement}'s
 * count parameter (via {@code CountPlacement}). The feature_rules' y "extent" is the absolute height range each vein can roll within.
 * <p>
 * SIMPLIFICATION: every one of these Bedrock ore_feature JSONs has {@code replace_rules.may_replace = minecraft:netherrack},
 * which cannot function as-is since this biome (despite its "the_netherlands/nether..." naming, inherited from an
 * apparent Bedrock nether-reskin template) generates in the OVERWORLD, not on netherrack. This is ported as
 * "may replace {@code minecraft:stone}" instead (matching where {@code BiomeDefaultFeatures.addDefaultOres} already
 * places vanilla ore for this biome in TheNetherlands.java), which is the closest faithful equivalent.
 * <p>
 * SIMPLIFICATION 2: Bedrock's {@code places_block} for coal/copper/iron/lapis/redstone are custom addon blocks
 * ("extrabiomes:nether_coal_ore" etc.) that were never ported to the Java block registry (only
 * {@link ModBlocks#NETHER_DIAMOND_ORE} exists in ModBlocks - checked, no others). Per the task's own guidance to
 * check for "vanilla ores re-skinned" as an acceptable target, these use their plain vanilla ore-block equivalents
 * instead (Blocks.COAL_ORE, COPPER_ORE, IRON_ORE, LAPIS_ORE, REDSTONE_ORE, EMERALD_ORE). The diamond vein reuses the
 * existing {@link ModBlocks#NETHER_DIAMOND_ORE} custom reskin exactly as Bedrock specified. Gold and quartz already
 * used vanilla block ids in Bedrock ("minecraft:nether_gold_ore" / "minecraft:quartz_ore", the Bedrock id for
 * Java's {@code minecraft:nether_quartz_ore}) so those are honored literally as Blocks.NETHER_GOLD_ORE / Blocks.NETHER_QUARTZ_ORE.
 */
public class NetherlandsOreFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> COAL_ORE_KEY = key("netherlands_coal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COPPER_ORE_KEY = key("netherlands_copper_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DIAMOND_ORE_KEY = key("netherlands_diamond_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> EMERALD_ORE_KEY = key("netherlands_emerald_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GOLD_ORE_KEY = key("netherlands_gold_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> IRON_ORE_KEY = key("netherlands_iron_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LAPIS_ORE_KEY = key("netherlands_lapis_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> QUARTZ_ORE_KEY = key("netherlands_quartz_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> REDSTONE_ORE_KEY = key("netherlands_redstone_ore");

    public static final ResourceKey<PlacedFeature> COAL_ORE_PLACED_KEY = placedKey("netherlands_coal_ore");
    public static final ResourceKey<PlacedFeature> COPPER_ORE_PLACED_KEY = placedKey("netherlands_copper_ore");
    public static final ResourceKey<PlacedFeature> DIAMOND_ORE_PLACED_KEY = placedKey("netherlands_diamond_ore");
    public static final ResourceKey<PlacedFeature> EMERALD_ORE_PLACED_KEY = placedKey("netherlands_emerald_ore");
    public static final ResourceKey<PlacedFeature> GOLD_ORE_PLACED_KEY = placedKey("netherlands_gold_ore");
    public static final ResourceKey<PlacedFeature> IRON_ORE_PLACED_KEY = placedKey("netherlands_iron_ore");
    public static final ResourceKey<PlacedFeature> LAPIS_ORE_PLACED_KEY = placedKey("netherlands_lapis_ore");
    public static final ResourceKey<PlacedFeature> QUARTZ_ORE_PLACED_KEY = placedKey("netherlands_quartz_ore");
    public static final ResourceKey<PlacedFeature> REDSTONE_ORE_PLACED_KEY = placedKey("netherlands_redstone_ore");

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        BlockMatchTest replaceStone = new BlockMatchTest(Blocks.STONE);
        // count (vein size) values read from features/the_netherlands/*_ore_feature.json
        context.register(COAL_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceStone, Blocks.COAL_ORE.defaultBlockState(), 17)));
        context.register(COPPER_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceStone, Blocks.COPPER_ORE.defaultBlockState(), 9)));
        context.register(DIAMOND_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceStone, ModBlocks.NETHER_DIAMOND_ORE.get().defaultBlockState(), 8)));
        context.register(EMERALD_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceStone, Blocks.EMERALD_ORE.defaultBlockState(), 1)));
        context.register(GOLD_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceStone, Blocks.NETHER_GOLD_ORE.defaultBlockState(), 9)));
        context.register(IRON_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceStone, Blocks.IRON_ORE.defaultBlockState(), 9)));
        context.register(LAPIS_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceStone, Blocks.LAPIS_ORE.defaultBlockState(), 3)));
        context.register(QUARTZ_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceStone, Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), 9)));
        context.register(REDSTONE_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceStone, Blocks.REDSTONE_ORE.defaultBlockState(), 8)));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        // iterations (veins/chunk) and y extent read from feature_rules/the_netherlands/netherlands_underground_*_ore_feature.json
        register(context, configuredFeatures, COAL_ORE_KEY, COAL_ORE_PLACED_KEY, 20, 0, 128);
        register(context, configuredFeatures, COPPER_ORE_KEY, COPPER_ORE_PLACED_KEY, 20, 0, 64);
        register(context, configuredFeatures, DIAMOND_ORE_KEY, DIAMOND_ORE_PLACED_KEY, 1, 0, 16);
        register(context, configuredFeatures, EMERALD_ORE_KEY, EMERALD_ORE_PLACED_KEY, 20, 0, 64);
        register(context, configuredFeatures, GOLD_ORE_KEY, GOLD_ORE_PLACED_KEY, 20, 0, 64);
        register(context, configuredFeatures, IRON_ORE_KEY, IRON_ORE_PLACED_KEY, 20, 0, 64);
        register(context, configuredFeatures, LAPIS_ORE_KEY, LAPIS_ORE_PLACED_KEY, 1, 0, 16);
        register(context, configuredFeatures, QUARTZ_ORE_KEY, QUARTZ_ORE_PLACED_KEY, 20, 0, 64);
        register(context, configuredFeatures, REDSTONE_ORE_KEY, REDSTONE_ORE_PLACED_KEY, 8, 0, 16);
    }

    private static void register(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
                                  ResourceKey<ConfiguredFeature<?, ?>> configuredKey, ResourceKey<PlacedFeature> placedKey,
                                  int veinsPerChunk, int yMin, int yMax) {
        context.register(placedKey, new PlacedFeature(
                configuredFeatures.getOrThrow(configuredKey),
                ModOrePlacement.commonOrePlacement(veinsPerChunk, HeightRangePlacement.uniform(VerticalAnchor.absolute(yMin), VerticalAnchor.absolute(yMax)))
        ));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
}
