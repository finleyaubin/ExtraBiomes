package net.winepicfin.extrabiomes.fabric.worldgen;

import net.minecraft.world.entity.MobCategory;
import net.winepicfin.extrabiomes.fabric.mixin.MobCategoryAccessor;
import net.winepicfin.extrabiomes.worldgen.MobSpawnCapTuning;

// Fabric equivalent of forge/.../worldgen/ModSpawnCaps.java - same write, reached through the
// MobCategoryAccessor mixin instead of an access transformer. See MobSpawnCapTuning for why the
// WATER_AMBIENT cap is raised at all and why there's no API for it in 1.20.1.
public final class FabricSpawnCaps {
    public static void register() {
        int raised = MobSpawnCapTuning.raisedWaterAmbientCap(MobCategory.WATER_AMBIENT.getMaxInstancesPerChunk());
        ((MobCategoryAccessor) (Object) MobCategory.WATER_AMBIENT).extrabiomes$setMaxInstancesPerChunk(raised);
    }

    private FabricSpawnCaps() {
    }
}
