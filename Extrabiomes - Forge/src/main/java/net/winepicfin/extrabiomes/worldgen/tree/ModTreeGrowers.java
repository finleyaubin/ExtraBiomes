package net.winepicfin.extrabiomes.worldgen.tree;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;

import java.util.Optional;

public class ModTreeGrowers  {
    public static final TreeGrower CHARRED = register("charred",0.1F,Optional.empty(),Optional.empty(), Optional.of(ModConfigureFeatures.CHARRED_KEY),Optional.empty(),Optional.empty(),Optional.empty());
    public static final TreeGrower MYSTIC = register("mystic",0.1F,Optional.empty(),Optional.empty(), Optional.of(ModConfigureFeatures.MYSTIC_KEY),Optional.empty(),Optional.empty(),Optional.empty());
    public static final TreeGrower SKY = register("palm",0.1F,Optional.empty(),Optional.empty(), Optional.of(ModConfigureFeatures.SKY_KEY),Optional.empty(),Optional.empty(),Optional.empty());
    public static final TreeGrower PALM = register("palm",0.1F,Optional.empty(),Optional.empty(), Optional.of(ModConfigureFeatures.PALM_KEY),Optional.empty(),Optional.empty(),Optional.empty());
    private static TreeGrower register(String name, float secondaryChance, Optional<ResourceKey<ConfiguredFeature<?, ?>>> megaTree, Optional<ResourceKey<ConfiguredFeature<?, ?>>> secondaryMegaTree, Optional<ResourceKey<ConfiguredFeature<?, ?>>> tree, Optional<ResourceKey<ConfiguredFeature<?, ?>>> secondaryTree, Optional<ResourceKey<ConfiguredFeature<?, ?>>> flowers, Optional<ResourceKey<ConfiguredFeature<?, ?>>> secondaryFlowers)
    {
        return new TreeGrower(name+":"+ ExtraBiomes.MOD_ID , secondaryChance, megaTree, secondaryMegaTree, tree, secondaryTree, flowers, secondaryFlowers);
    }
}
