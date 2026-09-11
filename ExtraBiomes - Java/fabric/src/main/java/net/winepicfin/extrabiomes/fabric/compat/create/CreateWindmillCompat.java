package net.winepicfin.extrabiomes.fabric.compat.create;

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The real Create-touching half of windmill Create compat (see
 * {@link net.winepicfin.extrabiomes.platform.fabric.ExtraBiomesExpectPlatformImpl#applyWindmillCreateCompat}
 * for the ModList-gated call site that's the only thing allowed to reference this class, so it's never
 * classloaded on a build without Create). windmill_create.nbt (see WindmillStructures) is a hand-built
 * Create contraption - a Windmill Bearing driving a shaft/cogwheel line down to a Mechanical Bearing, belts
 * and a crushing wheel pair - captured with a structure block, so every block (including the bearings
 * themselves) is already baked into the template; nothing needs to be placed procedurally here.
 * <p>
 * Two things are left to do, both because the capture froze every KineticBlockEntity's runtime state
 * (network/source/speed) as of whatever moment the structure block copied it, and that stale state doesn't
 * self-heal just from being re-placed somewhere else:
 * <ul>
 *   <li>Queue the Windmill Bearing to assemble on its own next real tick, via its protected
 *       assembleNextTick field (the exact mechanism Create's own right-click handler uses) rather than
 *       calling assemble() here directly - assemble() spawns a moving contraption entity and scans
 *       neighbouring blocks, neither of which is safe to do mid-worldgen (chunks around the structure may
 *       not exist yet).</li>
 *   <li>Reset every other KineticBlockEntity in the structure (shafts, cogwheels, the driven Mechanical
 *       Bearing, and the belts) via clearKineticInformation(). Without this the belts sat visually static
 *       forever: a KineticBlockEntity only re-attaches to a network on its own when it has no source, and
 *       the captured NBT still has whatever (now stale, since this copy isn't at the original capture
 *       location) source/network it had at capture time, so its own tick() logic never realizes it needs
 *       to reattach. Clearing that state makes each one look freshly-placed, so it self-attaches into the
 *       real network - fed by the windmill bearing above - the moment the structure starts ticking for
 *       real, exactly like a player-built contraption would.</li>
 * </ul>
 */
public final class CreateWindmillCompat {
    private static Field assembleNextTickField;
    private static Method clearKineticInformationMethod;

    private CreateWindmillCompat() {
    }

    public static void apply(WorldGenLevel level, BoundingBox box) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minY(); y <= box.maxY(); y++) {
                for (int z = box.minZ(); z <= box.maxZ(); z++) {
                    BlockEntity blockEntity = level.getBlockEntity(pos.set(x, y, z));
                    if (blockEntity instanceof WindmillBearingBlockEntity bearing) {
                        queueAssembly(bearing);
                    } else if (blockEntity instanceof KineticBlockEntity kinetic) {
                        clearKineticInformation(kinetic);
                    }
                }
            }
        }
    }

    private static void queueAssembly(MechanicalBearingBlockEntity bearing) {
        try {
            if (assembleNextTickField == null) {
                Field field = MechanicalBearingBlockEntity.class.getDeclaredField("assembleNextTick");
                field.setAccessible(true);
                assembleNextTickField = field;
            }
            assembleNextTickField.setBoolean(bearing, true);
        } catch (ReflectiveOperationException e) {
            // Create changed MechanicalBearingBlockEntity's internals - fail quiet, the bearing just sits
            // idle until right-clicked instead of crashing worldgen over a cosmetic compat feature.
        }
    }

    private static void clearKineticInformation(KineticBlockEntity kinetic) {
        try {
            if (clearKineticInformationMethod == null) {
                Method method = KineticBlockEntity.class.getDeclaredMethod("clearKineticInformation");
                method.setAccessible(true);
                clearKineticInformationMethod = method;
            }
            clearKineticInformationMethod.invoke(kinetic);
        } catch (ReflectiveOperationException e) {
            // Create changed KineticBlockEntity's internals - fail quiet, the belt/shaft/cogwheel just sits
            // idle until a neighbouring block update jars it into reattaching, instead of crashing worldgen
            // over a cosmetic compat feature.
        }
    }
}
