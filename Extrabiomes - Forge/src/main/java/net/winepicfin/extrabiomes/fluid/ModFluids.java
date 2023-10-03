package net.winepicfin.extrabiomes.fluid;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ExtraBiomes.MOD_ID);
    public static final RegistryObject<FlowingFluid>SOURCE_GOO = FLUIDS.register("source_goo",()->new ForgeFlowingFluid.Source(ModFluids.GOO_PROPERTIES));
    public static final RegistryObject<FlowingFluid>FLOWING_GOO = FLUIDS.register("flowing_goo",()->new ForgeFlowingFluid.Flowing(ModFluids.GOO_PROPERTIES));
public static final ForgeFlowingFluid.Properties GOO_PROPERTIES = new ForgeFlowingFluid.Properties(
        ModFluidTypes.GOO_FLUID_TYPE, SOURCE_GOO,FLOWING_GOO).slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.GOO).bucket(ModItems.BUCKET_OF_GOO);

    public static void register(IEventBus eventBus){
        FLUIDS.register(eventBus);
    }
}
