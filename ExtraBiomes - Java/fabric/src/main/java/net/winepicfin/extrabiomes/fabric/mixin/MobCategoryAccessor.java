package net.winepicfin.extrabiomes.fabric.mixin;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

// Forge widens MobCategory.max in its own access transformer; Fabric has no AT mechanism, so an
// accessor mixin is the direct replacement (same pattern as WolfModelAccessor/MobAccessor).
// @Mutable is what strips final, matching the AT's "public-f". Used by FabricSpawnCaps to raise
// the WATER_AMBIENT natural-spawn cap - see common/.../worldgen/MobSpawnCapTuning.
@Mixin(MobCategory.class)
public interface MobCategoryAccessor {
    @Mutable
    @Accessor("max")
    void extrabiomes$setMaxInstancesPerChunk(int max);
}
