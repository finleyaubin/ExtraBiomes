package net.winepicfin.extrabiomes.worldgen.features.structurescatter;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;

/**
 * Registers the {@link SingleStructureFeature} Feature TYPE (its {@code MapCodec}, in
 * Registries.FEATURE_TYPE) so it has a stable id ("extrabiomes:single_structure") and can be
 * referenced from any subsystem's Feature. This must be called once from the mod's main class - it
 * is NOT part of the per-world-generation Feature/PlacedFeature bootstrap (that happens in
 * Registries.FEATURE / Registries.PLACED_FEATURE via the datagen RegistrySetBuilder).
 */
public class ModStructureScatterFeatures {
    public static final DeferredRegister<MapCodec<? extends Feature>> FEATURES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FEATURE_TYPE);

    public static final RegistrySupplier<MapCodec<SingleStructureFeature>> SINGLE_STRUCTURE = FEATURES.register("single_structure", () -> SingleStructureFeature.CODEC);

    public static void register() {
        FEATURES.register();
    }
}
