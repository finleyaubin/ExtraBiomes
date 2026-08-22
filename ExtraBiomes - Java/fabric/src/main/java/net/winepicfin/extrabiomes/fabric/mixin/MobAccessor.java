package net.winepicfin.extrabiomes.fabric.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Forge already widens Mob.targetSelector to public in its own access transformer; Fabric has no
// AT mechanism, so an accessor mixin is the direct replacement. Used by FabricServerEvents to add
// the phantom-targets-harpy goal ported in PhantomHarpyTargeting.
@Mixin(Mob.class)
public interface MobAccessor {
    @Accessor("targetSelector")
    GoalSelector extrabiomes$getTargetSelector();
}
