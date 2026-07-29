package net.winepicfin.extrabiomes.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.worldgen.tree.custom.CaveVineTreeDecorator;
import net.winepicfin.extrabiomes.worldgen.tree.custom.MysticTrunkPlacer;

import java.util.List;

public class ModConfigureFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_KEY = registerKey("mystic");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SKY_KEY = registerKey("sky");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALM_KEY = registerKey("palm");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHARRED_KEY = registerKey("charred");

    public static final ResourceKey<ConfiguredFeature<?, ?>> LUSH_GRASS_KEY = registerKey("lush_grass");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context){
        // Tuned against the actual Bedrock structures (packs/BP/structures/extrabiomes/mystic_tree
        // + Large_mystic_tree.mcstructure, dumped with tools/viz_tree.py): both show a sprawling
        // 2-branch canopy reaching a leaf radius of ~6-9 blocks from the trunk, well beyond a plain
        // vanilla Cherry tree's ~4 block spread. branch_horizontal_length and the foliage radius are
        // widened to reach that spread, and the trunk height range is widened so tall rolls
        // occasionally approach the scale of the rare Large_mystic_tree variant, while still using
        // IntProviders throughout so every tree keeps procedural (non-static) variation.
        register(context, MYSTIC_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.MYSTIC_LOG.get()),
                new MysticTrunkPlacer(9, 5, 0, new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(1), 1).add(ConstantInt.of(2), 1).add(ConstantInt.of(3), 1).build()), UniformInt.of(3, 7), UniformInt.of(-5, -3), UniformInt.of(-1, 1)),
                BlockStateProvider.simple(ModBlocks.MYSTIC_LEAVES.get()),
                new CherryFoliagePlacer(ConstantInt.of(5), ConstantInt.of(0), ConstantInt.of(6), 0.25F, 0.5F, 0.16666667F, 0.33333334F),
                new TwoLayersFeatureSize(1,0,2)).decorators(ImmutableList.of(new CaveVineTreeDecorator(0.25F, 5))).build()
        );
        // sky_tree.mcstructure is a single 1-wide trunk (~9-10 tall) with leaves tapering from a
        // radius-0 point at the very top down to a ~2 block radius band and back to bare trunk near
        // the bottom - a conical/tapered silhouette that SpruceFoliagePlacer matches far better than
        // the round BlobFoliagePlacer this used before.
        register(context, SKY_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.SKY_LOG.get()),
                new StraightTrunkPlacer(6, 3, 0),
                BlockStateProvider.simple(ModBlocks.SKY_LEAVES.get()),
                new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), UniformInt.of(3, 5)),
                new TwoLayersFeatureSize(4, 10, 6)).build()
        );
        // The four palm_tree_*.mcstructure variants are a mostly-bare trunk (height varies 4-15
        // across variants) topped with a compact frond crown concentrated in the top 2 layers rather
        // than a canopy wrapped down the trunk - hence a low-height BlobFoliagePlacer (height 2,
        // instead of the previous 3-layer blob copied from the sky tree) so the crown stays
        // concentrated near the trunk top. offset must stay >= 0: FoliagePlacer's codec
        // (IntProvider.codec(0, 16)) rejects negative offsets, which silently dropped this
        // configured feature's generated JSON (palm.json) the first time this used offset -1.
        register(context, PALM_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.PALM_LOG.get()),
                new StraightTrunkPlacer(6, 4, 0),
                BlockStateProvider.simple(ModBlocks.PALM_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2),
                new TwoLayersFeatureSize(4, 10, 6)).build()
        );
        register(context, CHARRED_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.BASALT),
                new FancyTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(Blocks.AIR),
                new BlobFoliagePlacer(ConstantInt.of(0),ConstantInt.of(0),0),
                new TwoLayersFeatureSize(0, 0, 0)).build()
        );
        List<OreConfiguration.TargetBlockState> grassBlob = List.of(OreConfiguration.target(new TagMatchTest(BlockTags.TERRACOTTA), Blocks.GRASS_BLOCK.defaultBlockState()));
        register(context, LUSH_GRASS_KEY, Feature.ORE, new OreConfiguration(grassBlob, 30, 0));
    }
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name){
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>>key,F feature, FC configuration){
        context.register(key,new ConfiguredFeature<>(feature,configuration));
    }
}
