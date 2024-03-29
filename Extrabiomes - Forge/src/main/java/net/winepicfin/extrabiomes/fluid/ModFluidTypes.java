package net.winepicfin.extrabiomes.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import org.joml.Vector3f;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation(ExtraBiomes.MOD_ID, "misc/goo_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation(ExtraBiomes.MOD_ID, "misc/goo_flow");
    public static final ResourceLocation GOO_OVERLAY_RL = new ResourceLocation(ExtraBiomes.MOD_ID, "misc/goo");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ExtraBiomes.MOD_ID);

    public static final RegistryObject<FluidType> GOO_FLUID_TYPE = register("goo_fluid",
            FluidType.Properties.create().viscosity(40).canDrown(true).canConvertToSource(false).canPushEntity(true).canSwim(true).canExtinguish(false).density(10));


    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(WATER_STILL_RL, WATER_FLOWING_RL, GOO_OVERLAY_RL,  0xFFFFFFFF, new Vector3f(224f / 255f, 56f / 255f, 208f / 255f), properties));
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }

}
