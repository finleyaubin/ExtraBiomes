package net.winepicfin.extrabiomes.neoforge.fluid;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.winepicfin.extrabiomes.ExtraBiomes;
import org.joml.Vector3f;

// FluidType/NeoForgeRegistries.Keys.FLUID_TYPES is a Forge/NeoForge-only concept with no vanilla or
// Fabric equivalent registry key, so this one file keeps a direct Forge import for the key itself.
//
// Uses Forge's own DeferredRegister (not architectury's) on purpose: architectury's version
// resolves the registrar eagerly at .register() call time, but forge:fluid_type is a custom
// registry Forge creates via NewRegistryEvent during the LOAD mod-loading phase - strictly
// after CONSTRUCT, where this mod's own registration call runs. Forge's DeferredRegister
// instead subscribes a RegisterEvent listener on the given event bus and genuinely defers
// until that event fires, which is what a registry created this late actually requires.
public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation(ExtraBiomes.MOD_ID, "misc/goo_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation(ExtraBiomes.MOD_ID, "misc/goo_flow");
    public static final ResourceLocation GOO_OVERLAY_RL = new ResourceLocation(ExtraBiomes.MOD_ID, "misc/goo");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ExtraBiomes.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> GOO_FLUID_TYPE = register("goo_fluid",
            FluidType.Properties.create().viscosity(40).canDrown(true).canConvertToSource(false).canPushEntity(true).canSwim(true).canExtinguish(false).density(10));


    private static DeferredHolder<FluidType, FluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(WATER_STILL_RL, WATER_FLOWING_RL, GOO_OVERLAY_RL,  0xFFFFFFFF, new Vector3f(224f / 255f, 56f / 255f, 208f / 255f), properties));
    }

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }

}
