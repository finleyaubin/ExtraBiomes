package net.winepicfin.extrabiomes.fluid;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.winepicfin.extrabiomes.ExtraBiomes;
import org.joml.Vector3f;

// FluidType/ForgeRegistries.Keys.FLUID_TYPES is a Forge/NeoForge-only concept with no vanilla or
// Fabric equivalent registry key, so this one file keeps a direct Forge import for the key itself.
public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "misc/goo_still");
    public static final ResourceLocation WATER_FLOWING_RL = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "misc/goo_flow");
    public static final ResourceLocation GOO_OVERLAY_RL = ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "misc/goo");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ExtraBiomes.MOD_ID, ForgeRegistries.Keys.FLUID_TYPES);

    public static final RegistrySupplier<FluidType> GOO_FLUID_TYPE = register("goo_fluid",
            FluidType.Properties.create().viscosity(40).canDrown(true).canConvertToSource(false).canPushEntity(true).canSwim(true).canExtinguish(false).density(10));


    private static RegistrySupplier<FluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(WATER_STILL_RL, WATER_FLOWING_RL, GOO_OVERLAY_RL,  0xFFFFFFFF, new Vector3f(224f / 255f, 56f / 255f, 208f / 255f), properties));
    }

    public static void register() {
        FLUID_TYPES.register();
    }

}
