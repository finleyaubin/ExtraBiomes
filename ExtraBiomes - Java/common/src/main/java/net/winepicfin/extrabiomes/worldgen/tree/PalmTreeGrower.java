package net.winepicfin.extrabiomes.worldgen.tree;

import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.winepicfin.extrabiomes.worldgen.features.palm.PalmTreeFeatures;

// See MysticTreeGrower's comment - TreeGrower became final as of 1.20.4.
public class PalmTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower("palm", WeightedList.of(), WeightedList.of(PalmTreeFeatures.SELECT_PALM_KEY), WeightedList.of(), PalmTreeFeatures.SELECT_PALM_KEY);
}
