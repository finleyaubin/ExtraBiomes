package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;

/**
 * Registers the {@link SingleStructureFeature} Feature type itself (Registries.FEATURE) so it has
 * a stable id ("extrabiomes:single_structure") and can be referenced from any subsystem's
 * ConfiguredFeature. This must be called once from the mod's main class - it is NOT part of the
 * per-world-generation ConfiguredFeature/PlacedFeature bootstrap (that happens in
 * Registries.CONFIGURED_FEATURE / Registries.PLACED_FEATURE via the datagen RegistrySetBuilder).
 */
public class ModStructureScatterFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ExtraBiomes.MOD_ID);

    public static final RegistryObject<SingleStructureFeature> SINGLE_STRUCTURE = FEATURES.register("single_structure", () -> new SingleStructureFeature(SingleStructureConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
