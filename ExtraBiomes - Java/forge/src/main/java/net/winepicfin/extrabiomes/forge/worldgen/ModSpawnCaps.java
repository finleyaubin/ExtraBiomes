package net.winepicfin.extrabiomes.forge.worldgen;

import net.minecraft.world.entity.MobCategory;
import net.winepicfin.extrabiomes.worldgen.MobSpawnCapTuning;

// Forge half of the WATER_AMBIENT spawn-cap raise described in MobSpawnCapTuning. Forge has no
// event or config for per-category caps in 1.20.1, so MobCategory.max is widened in
// META-INF/accesstransformer.cfg and written directly here; Fabric does the same write through
// the MobCategoryAccessor mixin.
public final class ModSpawnCaps {
    public static void register() {
        MobCategory.WATER_AMBIENT.max =
                MobSpawnCapTuning.raisedWaterAmbientCap(MobCategory.WATER_AMBIENT.getMaxInstancesPerChunk());
    }

    private ModSpawnCaps() {
    }
}
