package net.winepicfin.extrabiomes.advancements;

import net.minecraft.advancements.CriteriaTriggers;

// CriteriaTriggers.register() is a plain static call, not a registry - no event bus/deferred
// register involved, unlike the Mod*.register() classes elsewhere in the mod.
public class ModCriteriaTriggers {
    public static final BaitLureTrigger LURED_PIRANHA_WITH_BAIT = CriteriaTriggers.register("extrabiomes:lured_piranha_with_bait", new BaitLureTrigger());

    // No-op: referencing this class is enough to run the field initializer above, but every
    // other loader entry point calls a register() for its Mod* classes, so this keeps that pattern.
    public static void register() {
    }
}
