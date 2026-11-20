package net.winepicfin.extrabiomes.worldgen.tree;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;
import org.jetbrains.annotations.Nullable;

public class LarchTreeGrower extends AbstractTreeGrower {
    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        return ModConfigureFeatures.LARCH_KEY;
    }
}
