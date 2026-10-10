package net.winepicfin.extrabiomes.worldgen.tree;

import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;

// TreeGrower became a final, non-extendable class as of 1.20.4 (was AbstractTreeGrower, an
// abstract class overriding getConfiguredFeature) - single-feature growers like this one just
// build a TreeGrower instance directly, matching vanilla's own AZALEA/BIRCH growers' shape.
public class MysticTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower("mystic", WeightedList.of(), WeightedList.of(ModConfigureFeatures.MYSTIC_SELECT_KEY), WeightedList.of(), ModConfigureFeatures.MYSTIC_SELECT_KEY);
}
