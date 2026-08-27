package net.winepicfin.extrabiomes.forge.compat.create;

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.lang.reflect.Field;

/**
 * The real Create-touching half of windmill Create compat (see
 * {@link net.winepicfin.extrabiomes.platform.forge.ExtraBiomesExpectPlatformImpl#applyWindmillCreateCompat}
 * for the ModList-gated call site that's the only thing allowed to reference this class, so it's never
 * classloaded on a build without Create). windmill_create.nbt (see WindmillStructures) is a hand-built
 * Create contraption - a Windmill Bearing driving a shaft/cogwheel line down to a Mechanical Bearing and a
 * crushing wheel pair - captured with a structure block, so every block (including the bearings themselves)
 * is already baked into the template; nothing needs to be placed procedurally here.
 * <p>
 * The only thing left to do is queue the Windmill Bearing to assemble on its own next real tick, via its
 * protected assembleNextTick field (the exact mechanism Create's own right-click handler uses) rather than
 * calling assemble() here directly - assemble() spawns a moving contraption entity and scans neighbouring
 * blocks, neither of which is safe to do mid-worldgen (chunks around the structure may not exist yet).
 * Nothing else in the build needs its own explicit trigger: once the windmill bearing is actually turning,
 * the shafts/cogwheels/mechanical bearing/crushing wheels all receive rotation through Create's kinetic
 * network the same way they would from a player starting it by hand.
 */
public final class CreateWindmillCompat {
    private static Field assembleNextTickField;

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
}
