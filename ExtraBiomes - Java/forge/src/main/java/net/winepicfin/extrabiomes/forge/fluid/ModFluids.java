package net.winepicfin.extrabiomes.forge.fluid;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

// ForgeFlowingFluid has no Fabric/NeoForge equivalent; a real fluid abstraction is needed
// before a second loader is added. The registry *mechanism* below is already loader-agnostic.
public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.FLUID);
    public static final RegistrySupplier<FlowingFluid>SOURCE_GOO = FLUIDS.register("source_goo",()->new ForgeFlowingFluid.Source(ModFluids.GOO_PROPERTIES));
    public static final RegistrySupplier<FlowingFluid>FLOWING_GOO = FLUIDS.register("flowing_goo",()->new ForgeFlowingFluid.Flowing(ModFluids.GOO_PROPERTIES));
public static final ForgeFlowingFluid.Properties GOO_PROPERTIES = new ForgeFlowingFluid.Properties(
        ModFluidTypes.GOO_FLUID_TYPE, SOURCE_GOO,FLOWING_GOO).slopeFindDistance(1).levelDecreasePerBlock(5).block(ModBlocks.GOO).bucket(ModItems.BUCKET_OF_GOO);

    public static void register(){
        FLUIDS.register();
    }
}
