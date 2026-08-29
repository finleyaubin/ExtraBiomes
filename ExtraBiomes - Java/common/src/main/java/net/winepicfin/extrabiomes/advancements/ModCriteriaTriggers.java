package net.winepicfin.extrabiomes.advancements;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.winepicfin.extrabiomes.ExtraBiomes;

// CriteriaTriggers.register() (a plain static call, not going through a registry event) started
// throwing "Registry is already frozen" as of 1.20.4 - Registries.TRIGGER_TYPE became a real
// BuiltInRegistries-backed registry that freezes before FML constructs mods, so custom triggers
// now have to go through the normal DeferredRegister/RegisterEvent lifecycle like every other
// registry in this mod, not an eager static-init call in the mod's constructor.
public class ModCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.TRIGGER_TYPE);

    public static final RegistrySupplier<BaitLureTrigger> LURED_PIRANHA_WITH_BAIT = TRIGGER_TYPES.register("lured_piranha_with_bait", BaitLureTrigger::new);

    public static void register() {
        TRIGGER_TYPES.register();
    }
}
