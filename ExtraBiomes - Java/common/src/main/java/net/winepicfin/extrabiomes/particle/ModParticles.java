package net.winepicfin.extrabiomes.particle;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.winepicfin.extrabiomes.ExtraBiomes;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> STEAM =
            PARTICLE_TYPES.register("steam", () -> new SteamParticleType());

    public static void register() {
        PARTICLE_TYPES.register();
    }
}
