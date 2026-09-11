package net.winepicfin.extrabiomes.worldgen.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import net.winepicfin.extrabiomes.worldgen.features.palm.PalmTreeFeatures;

import java.util.Optional;

// See MysticTreeGrower's comment - TreeGrower became final as of 1.20.4.
public class PalmTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower("palm", Optional.empty(), Optional.of(PalmTreeFeatures.SELECT_PALM_KEY), Optional.empty());
}
