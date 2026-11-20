package net.winepicfin.extrabiomes.entity.custom;

// One breaststroke cycle: the front legs sweep out, pull in and push forward, then the hind legs tuck and kick, then a glide.
// Shared by the server's swim thrust and the client's leg poses so the frog surges when it kicks.
// No Minecraft imports, deliberately: lets tests exercise the stroke without a game bootstrap.
public final class TreefrogStroke {
    public static final float CYCLE_TICKS = 40f;

    private static final float SWEEP_END = 0.08f;
    private static final float PULL_END = 0.18f;
    private static final float RECOVERY_END = 0.32f;
    private static final float TUCK_END = 0.5f;
    private static final float KICK_END = 0.62f;

    private static final float PULL_THRUST = 0.6f;
    private static final float RECOVERY_THRUST = 0.2f;
    private static final float KICK_THRUST = 1.8f;
    private static final float GLIDE_END_THRUST = 0.3f;

    private TreefrogStroke() {
    }

    public static float phaseAt(float ticks) {
        float cycles = ticks / CYCLE_TICKS;
        return cycles - (float) Math.floor(cycles);
    }

    // How far the straight front legs are swung out to the sides, 0 (pointing ahead) to 1 (a wide Y).
    public static float sweepAt(float phase) {
        if (phase < SWEEP_END) {
            return easeInOut(phase / SWEEP_END);
        }
        if (phase < PULL_END) {
            return 1f - easeInOut((phase - SWEEP_END) / (PULL_END - SWEEP_END));
        }
        return 0f;
    }

    // How far the front legs reach forward, 0 (pulled in against the chest) to 1 (stretched ahead).
    public static float reachAt(float phase) {
        if (phase < SWEEP_END) {
            return 1f;
        }
        if (phase < PULL_END) {
            return 1f - easeInOut((phase - SWEEP_END) / (PULL_END - SWEEP_END));
        }
        if (phase < RECOVERY_END) {
            return easeInOut((phase - PULL_END) / (RECOVERY_END - PULL_END));
        }
        return 1f;
    }

    // How far the hind legs are kicked out, 0 (heels tucked up) to 1 (straight back and together).
    public static float kickAt(float phase) {
        if (phase < RECOVERY_END) {
            return 1f;
        }
        if (phase < TUCK_END) {
            return 1f - easeInOut((phase - RECOVERY_END) / (TUCK_END - RECOVERY_END));
        }
        if (phase < KICK_END) {
            return easeInOut((phase - TUCK_END) / (KICK_END - TUCK_END));
        }
        return 1f;
    }

    // Share of the frog's swim speed it is pushing with: most on the kick, fading through the glide.
    public static float thrustAt(float phase) {
        if (phase < PULL_END) {
            return PULL_THRUST;
        }
        if (phase < TUCK_END) {
            return RECOVERY_THRUST;
        }
        if (phase < KICK_END) {
            return KICK_THRUST;
        }
        float glide = (phase - KICK_END) / (1f - KICK_END);
        return KICK_THRUST + (GLIDE_END_THRUST - KICK_THRUST) * glide;
    }

    private static float easeInOut(float progress) {
        return progress * progress * (3f - 2f * progress);
    }
}
