package net.winepicfin.extrabiomes.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

// A soft, translucent, fullbright puff that fades in, swells as it rises and fades out.
public class SteamParticle extends TextureSheetParticle {
    private static final float START_ALPHA = 0.32f;
    private static final float GROWTH = 0.8f;
    private static final int FULL_BRIGHT = 0xF000F0;

    private final float baseSize;
    private final double swayPhase;

    protected SteamParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z);
        pickSprite(sprites);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.baseSize = 0.28f + random.nextFloat() * 0.2f;
        this.quadSize = baseSize;
        this.lifetime = 24 + random.nextInt(18);
        this.swayPhase = random.nextDouble() * Math.PI * 2.0;
        this.gravity = 0.0f;
        this.friction = 0.97f;
        this.hasPhysics = false;
        setColor(1.0f, 1.0f, 1.0f);
        setAlpha(0.0f);
    }

    @Override
    public void tick() {
        super.tick();
        float life = Math.min(age / (float) lifetime, 1.0f);
        quadSize = baseSize * (1.0f + life * GROWTH);
        setAlpha(START_ALPHA * Math.min(1.0f, life * 5.0f) * (1.0f - life));
        xd += Math.sin(age * 0.25 + swayPhase) * 0.0015;
        zd += Math.cos(age * 0.21 + swayPhase) * 0.0015;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new SteamParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
