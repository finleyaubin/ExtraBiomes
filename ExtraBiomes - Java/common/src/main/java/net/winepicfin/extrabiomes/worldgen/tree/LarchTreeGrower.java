package net.winepicfin.extrabiomes.worldgen.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;

import java.util.Optional;

public class LarchTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower("larch", Optional.empty(), Optional.of(ModConfigureFeatures.LARCH_KEY), Optional.empty());
}
