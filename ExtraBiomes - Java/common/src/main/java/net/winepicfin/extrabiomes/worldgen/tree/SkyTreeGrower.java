package net.winepicfin.extrabiomes.worldgen.tree;

import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;

// See MysticTreeGrower's comment - TreeGrower became final as of 1.20.4.
public class SkyTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower("sky", WeightedList.of(), WeightedList.of(ModConfigureFeatures.SKY_KEY), WeightedList.of(), ModConfigureFeatures.SKY_KEY);
}
