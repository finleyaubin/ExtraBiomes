package net.winepicfin.extrabiomes.worldgen.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;

import java.util.Optional;

// TreeGrower became a final, non-extendable class as of 1.20.4 (was AbstractTreeGrower, an
// abstract class overriding getConfiguredFeature) - single-feature growers like this one just
// build a TreeGrower instance directly, matching vanilla's own AZALEA/BIRCH growers' shape.
public class MysticTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower("mystic", Optional.empty(), Optional.of(ModConfigureFeatures.MYSTIC_SELECT_KEY), Optional.empty());
}
