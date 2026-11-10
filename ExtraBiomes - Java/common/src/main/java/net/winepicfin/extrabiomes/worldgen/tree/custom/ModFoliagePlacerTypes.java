package net.winepicfin.extrabiomes.worldgen.tree.custom;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.platform.ExtraBiomesExpectPlatform;

public class ModFoliagePlacerTypes {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACER = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FOLIAGE_PLACER_TYPE);
    public static final RegistrySupplier<FoliagePlacerType<LarchFoliagePlacer>> LARCH_FOLIAGE_PLACER = FOLIAGE_PLACER.register("larch_foliage_placer", () -> ExtraBiomesExpectPlatform.createFoliagePlacerType(LarchFoliagePlacer.CODEC));

    public static void register() {
        FOLIAGE_PLACER.register();
    }
}
