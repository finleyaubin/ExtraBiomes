package net.winepicfin.extrabiomes.particle;

import net.minecraft.core.particles.SimpleParticleType;

// SimpleParticleType's constructor is protected, so common code needs a subclass to create one.
public class SteamParticleType extends SimpleParticleType {
    public SteamParticleType() {
        super(false);
    }
}
