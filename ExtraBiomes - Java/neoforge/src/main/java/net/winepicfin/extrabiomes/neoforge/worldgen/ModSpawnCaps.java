package net.winepicfin.extrabiomes.neoforge.worldgen;

import net.minecraft.world.entity.MobCategory;
import net.winepicfin.extrabiomes.worldgen.MobSpawnCapTuning;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

// Forge half of the WATER_AMBIENT spawn-cap raise described in MobSpawnCapTuning. Forge has no
// event or config for per-category caps in 1.20.1, so MobCategory.max is widened in
// META-INF/accesstransformer.cfg and written directly here; Fabric does the same write through
// the MobCategoryAccessor mixin.
//
// On the neoforge module the AT entry doesn't take effect on the Architectury Loom NeoForge
// merged jar (see ModVanillaCompat's identical situation for FireBlock#setFlammable) - and
// MobCategory#max is additionally `final`, which plain Field#setAccessible(true) cannot bypass
// for writes on modern JDKs. sun.misc.Unsafe writes the field directly, skipping the final check
// entirely, as a pragmatic fallback for this one-time mod-setup write.
public final class ModSpawnCaps {
    private static final Unsafe UNSAFE;
    private static final long MAX_OFFSET;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
            MAX_OFFSET = UNSAFE.objectFieldOffset(MobCategory.class.getDeclaredField("max"));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void register() {
        int raised = MobSpawnCapTuning.raisedWaterAmbientCap(MobCategory.WATER_AMBIENT.getMaxInstancesPerChunk());
        UNSAFE.putInt(MobCategory.WATER_AMBIENT, MAX_OFFSET, raised);
    }

    private ModSpawnCaps() {
    }
}
