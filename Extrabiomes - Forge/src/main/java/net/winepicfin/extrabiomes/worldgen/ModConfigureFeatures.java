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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.worldgen.tree.custom.MysticTrunkPlacer;

import java.util.List;

public class ModConfigureFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_KEY = registerKey("mystic");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SKY_KEY = registerKey("sky");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALM_KEY = registerKey("palm");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHARRED_KEY = registerKey("charred");

    public static final ResourceKey<ConfiguredFeature<?, ?>> LUSH_GRASS_KEY = registerKey("lush_grass");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context){//todo untested, probably looks shit
        register(context, MYSTIC_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.MYSTIC_LOG.get()),
                new MysticTrunkPlacer(9, 3, 0, new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(1), 1).add(ConstantInt.of(2), 1).add(ConstantInt.of(3), 1).build()), UniformInt.of(2, 4), UniformInt.of(-4, -3), UniformInt.of(-1, 0)),
                BlockStateProvider.simple(ModBlocks.MYSTIC_LEAVES.get()),
                new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5), 0.25F, 0.5F, 0.16666667F, 0.33333334F),
                new TwoLayersFeatureSize(1,0,2)).decorators(ImmutableList.of(TrunkVineDecorator.INSTANCE, new LeaveVineDecorator(0.25F))).build()
        );
        register(context, SKY_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.SKY_LOG.get()),
                new StraightTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(ModBlocks.SKY_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2),ConstantInt.of(0),3),
                new TwoLayersFeatureSize(4, 10, 6)).build()
        );
        register(context, PALM_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.PALM_LOG.get()),
                new StraightTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(ModBlocks.PALM_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2),ConstantInt.of(0),3),
                new TwoLayersFeatureSize(4, 10, 6)).build()
        );
        register(context, CHARRED_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.BASALT),
                new FancyTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(Blocks.AIR),
                new BlobFoliagePlacer(ConstantInt.of(0),ConstantInt.of(0),0),
                new TwoLayersFeatureSize(0, 0, 0)).build()
        );
        RuleTest terracottaReplaceables = new TagMatchTest(BlockTags.TERRACOTTA);
        List<OreConfiguration.TargetBlockState> LushMesaGrassPatch = List.of(OreConfiguration.target(terracottaReplaceables, Blocks.GRASS_BLOCK.defaultBlockState()));
        register(context, LUSH_GRASS_KEY,Feature.ORE, new OreConfiguration(LushMesaGrassPatch, 32));
    }
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name){
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>>key,F feature, FC configuration){
        context.register(key,new ConfiguredFeature<>(feature,configuration));
    }
}
