package net.winepicfin.extrabiomes.worldgen.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import net.winepicfin.extrabiomes.worldgen.ModConfigureFeatures;

import java.util.Optional;

// See MysticTreeGrower's comment - TreeGrower became final as of 1.20.4.
public class CharredTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower("charred", Optional.empty(), Optional.of(ModConfigureFeatures.CHARRED_KEY), Optional.empty());
}
