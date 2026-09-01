package net.winepicfin.extrabiomes.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.worldgen.tree.custom.CaveVineTreeDecorator;
import net.winepicfin.extrabiomes.worldgen.tree.custom.MysticTrunkPlacer;

import java.util.List;

public class ModConfigureFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_KEY = registerKey("mystic");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_LARGE_KEY = registerKey("mystic_large");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_SELECT_KEY = registerKey("mystic_select");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SKY_KEY = registerKey("sky");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHARRED_KEY = registerKey("charred");

    public static final ResourceKey<ConfiguredFeature<?, ?>> LUSH_GRASS_KEY = registerKey("lush_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRAND_OASIS_DEAD_BUSH_KEY = registerKey("grand_oasis_dead_bush");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context){
        // In-game render showed radius 5 giving every attachment (main top + both branch tips) a
        // full-size canopy blob that overlapped its neighbours, fusing into one flat continuous mass
        // instead of a domed crown with separate branch tufts like the Bedrock structure. Shrinking
        // the per-attachment radius and pushing branch_horizontal_length out past that radius keeps
        // the branch tufts visually distinct from the main crown, matching the reference screenshot.
        register(context, MYSTIC_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.MYSTIC_LOG.get()),
                new MysticTrunkPlacer(9, 5, 0, new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(1), 1).add(ConstantInt.of(2), 1).add(ConstantInt.of(3), 1).build()), UniformInt.of(4, 8), UniformInt.of(-5, -3), UniformInt.of(-1, 1)),
                BlockStateProvider.simple(ModBlocks.MYSTIC_LEAVES.get()),
                new CherryFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(5), 0.25F, 0.5F, 0.16666667F, 0.33333334F),
                new TwoLayersFeatureSize(1,0,2)).decorators(ImmutableList.of(new CaveVineTreeDecorator(0.25F, 5))).build()
        );
        // Rare "elder" variant reviving the scale of Bedrock's unused Large_mystic_tree.mcstructure
        // (never referenced by any Bedrock feature, so not something to shape-match exactly - just a
        // nod to its existence as an occasional taller/wider tree among the normal MYSTIC_KEY ones).
        register(context, MYSTIC_LARGE_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.MYSTIC_LOG.get()),
                new MysticTrunkPlacer(15, 6, 0, new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(1), 1).add(ConstantInt.of(2), 1).add(ConstantInt.of(3), 1).build()), UniformInt.of(6, 10), UniformInt.of(-6, -4), UniformInt.of(-1, 1)),
                BlockStateProvider.simple(ModBlocks.MYSTIC_LEAVES.get()),
                new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(6), 0.25F, 0.5F, 0.16666667F, 0.33333334F),
                new TwoLayersFeatureSize(1,0,2)).decorators(ImmutableList.of(new CaveVineTreeDecorator(0.25F, 5))).build()
        );
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatureLookup = context.lookup(Registries.CONFIGURED_FEATURE);
        register(context, MYSTIC_SELECT_KEY, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configuredFeatureLookup.getOrThrow(MYSTIC_LARGE_KEY)), 0.08F)),
                PlacementUtils.inlinePlaced(configuredFeatureLookup.getOrThrow(MYSTIC_KEY))
        ));
        // sky_tree.mcstructure has a conical/tapered silhouette that SpruceFoliagePlacer matches far better than the round BlobFoliagePlacer this used before.
        register(context, SKY_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.SKY_LOG.get()),
                new StraightTrunkPlacer(6, 3, 0),
                BlockStateProvider.simple(ModBlocks.SKY_LEAVES.get()),
                new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), UniformInt.of(3, 5)),
                new TwoLayersFeatureSize(4, 10, 6)).build()
        );
        // Palm trees are no longer a procedural Feature.TREE - see PalmTreeFeatures.SELECT_PALM_KEY, which PalmTreeGrower/ModPlacedFeatures reference instead, since real palm trunks lean/kink and can't be reproduced by a TrunkPlacer/FoliagePlacer pair.
        register(context, CHARRED_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.BASALT),
                new FancyTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(Blocks.AIR),
                new BlobFoliagePlacer(ConstantInt.of(0),ConstantInt.of(0),0),
                new TwoLayersFeatureSize(0, 0, 0)).build()
        );
        List<OreConfiguration.TargetBlockState> grassBlob = List.of(OreConfiguration.target(new TagMatchTest(BlockTags.TERRACOTTA), Blocks.GRASS_BLOCK.defaultBlockState()));
        register(context, LUSH_GRASS_KEY, Feature.ORE, new OreConfiguration(grassBlob, 30, 0));
        // Requested addition: a small scattering of dead bush on Grand Oasis's sand - deliberately its own
        // low-count PlacedFeature (see ModPlacedFeatures.GRAND_OASIS_DEAD_BUSH_PLACED_KEY) rather than
        // relying on BiomeDefaultFeatures.addDesertVegetation's own dead bush patch, so this biome's count
        // can be tuned independently of vanilla desert's.
        register(context, GRAND_OASIS_DEAD_BUSH_KEY, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.DEAD_BUSH)));
    }
    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name){
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>>key,F feature, FC configuration){
        context.register(key,new ConfiguredFeature<>(feature,configuration));
    }
}
