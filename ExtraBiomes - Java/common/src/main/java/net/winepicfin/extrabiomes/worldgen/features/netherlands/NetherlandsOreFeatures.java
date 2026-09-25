package net.winepicfin.extrabiomes.worldgen.features.netherlands;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
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

// Ports Bedrock's the_netherlands/*_ore_feature veins: Bedrock "count" is vein size, its feature_rules "iterations" is veins per chunk.
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

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // ModSurfaceRules paints this biome's underground as netherrack, not stone, so these veins must target netherrack to find anything to replace.
        BlockMatchTest replaceNetherrack = new BlockMatchTest(Blocks.NETHERRACK);
        context.register(COAL_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceNetherrack, ModBlocks.NETHER_COAL_ORE.get().defaultBlockState(), 17)));
        context.register(COPPER_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceNetherrack, ModBlocks.NETHER_COPPER_ORE.get().defaultBlockState(), 9)));
        context.register(DIAMOND_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceNetherrack, ModBlocks.NETHER_DIAMOND_ORE.get().defaultBlockState(), 8)));
        context.register(EMERALD_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceNetherrack, ModBlocks.NETHER_EMERALD_ORE.get().defaultBlockState(), 1)));
        context.register(GOLD_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceNetherrack, Blocks.NETHER_GOLD_ORE.defaultBlockState(), 9)));
        context.register(IRON_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceNetherrack, ModBlocks.NETHER_IRON_ORE.get().defaultBlockState(), 9)));
        context.register(LAPIS_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceNetherrack, ModBlocks.NETHER_LAPIS_ORE.get().defaultBlockState(), 3)));
        context.register(QUARTZ_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceNetherrack, Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), 9)));
        context.register(REDSTONE_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(replaceNetherrack, ModBlocks.NETHER_REDSTONE_ORE.get().defaultBlockState(), 8)));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        // Java's netherrack is only a 30-block band under the surface (see ModSurfaceRules), so Bedrock's y 0-16 veins are raised into it.
        register(context, configuredFeatures, COAL_ORE_KEY, COAL_ORE_PLACED_KEY, 20, 0, 128);
        register(context, configuredFeatures, COPPER_ORE_KEY, COPPER_ORE_PLACED_KEY, 20, 0, 64);
        register(context, configuredFeatures, DIAMOND_ORE_KEY, DIAMOND_ORE_PLACED_KEY, 1, 32, 48);
        register(context, configuredFeatures, EMERALD_ORE_KEY, EMERALD_ORE_PLACED_KEY, 20, 0, 64);
        register(context, configuredFeatures, GOLD_ORE_KEY, GOLD_ORE_PLACED_KEY, 20, 0, 64);
        register(context, configuredFeatures, IRON_ORE_KEY, IRON_ORE_PLACED_KEY, 20, 0, 64);
        register(context, configuredFeatures, LAPIS_ORE_KEY, LAPIS_ORE_PLACED_KEY, 1, 32, 48);
        register(context, configuredFeatures, QUARTZ_ORE_KEY, QUARTZ_ORE_PLACED_KEY, 20, 0, 64);
        register(context, configuredFeatures, REDSTONE_ORE_KEY, REDSTONE_ORE_PLACED_KEY, 8, 32, 48);
    }

    private static void register(BootstrapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
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
