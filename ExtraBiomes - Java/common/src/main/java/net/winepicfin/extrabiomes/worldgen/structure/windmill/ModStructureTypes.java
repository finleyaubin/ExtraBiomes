package net.winepicfin.extrabiomes.worldgen.structure.windmill;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.winepicfin.extrabiomes.ExtraBiomes;

/**
 * Registers {@link WindmillStructure}'s {@link StructureType} itself (Registries.STRUCTURE_TYPE, a plain
 * synchronous registry, unlike Structure/StructureSet/StructureTemplatePool which are dynamic/datapack
 * registries populated via the datagen RegistrySetBuilder - see {@link WindmillStructures}) - same split
 * as {@link net.winepicfin.extrabiomes.worldgen.features.structurescatter.ModStructureScatterFeatures}
 * does for its own Feature type. Call {@link #register()} once from the mod's main class.
 */
public class ModStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.STRUCTURE_TYPE);

    public static final RegistrySupplier<StructureType<WindmillStructure>> WINDMILL = STRUCTURE_TYPES.register("windmill", () -> () -> WindmillStructure.CODEC);

    public static void register() {
        STRUCTURE_TYPES.register();
    }
}
