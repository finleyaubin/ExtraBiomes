package net.winepicfin.extrabiomes.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;

public class ModConfigureFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_KEY = registerKey("mystic");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SKY_KEY = registerKey("sky");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALM_KEY = registerKey("palm");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context){//todo untested, probably looks shit
        register(context, MYSTIC_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.MYSTIC_LOG.get()),
                new FancyTrunkPlacer(9, 5,7),
                BlockStateProvider.simple(ModBlocks.MYSTIC_LEAVES.get()),
                new FancyFoliagePlacer(ConstantInt.of(7),ConstantInt.of(7),5),
                new TwoLayersFeatureSize(4,10,6)).build()
        );
        register(context, SKY_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.SKY_LOG.get()),
                new StraightTrunkPlacer(5, 5, 2),
                BlockStateProvider.simple(ModBlocks.SKY_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2),ConstantInt.of(0),3),
                new TwoLayersFeatureSize(4, 10, 6)).build()
        );
        register(context, PALM_KEY,Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.PALM_LOG.get()),
                new StraightTrunkPlacer(5, 5, 2),
                BlockStateProvider.simple(ModBlocks.PALM_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2),ConstantInt.of(0),3),
                new TwoLayersFeatureSize(4, 10, 6)).build()
        );
    }
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name){
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,new ResourceLocation(ExtraBiomes.MOD_ID, name));
    }
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>>key,F feature, FC configuration){
        context.register(key,new ConfiguredFeature<>(feature,configuration));
    }
}
