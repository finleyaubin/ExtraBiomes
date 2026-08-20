package net.winepicfin.extrabiomes.fabric.fluid;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FLUID);

    public static final RegistrySupplier<Fluid> SOURCE_GOO = FLUIDS.register("source_goo", GooFluid.Source::new);
    public static final RegistrySupplier<Fluid> FLOWING_GOO = FLUIDS.register("flowing_goo", GooFluid.Flowing::new);

    public static void register() {
        FLUIDS.register();
    }
}
