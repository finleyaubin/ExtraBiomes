package net.winepicfin.extrabiomes.fabric.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// SpawnPlacements.register(...) is private in vanilla 1.20.6 - Forge's access transformer widens
// it, this static invoker mixin is Fabric's equivalent, used by FabricModEvents to register spawn
// placements for our custom entities.
@Mixin(SpawnPlacements.class)
public interface SpawnPlacementsAccessor {
    @Invoker("register")
    static <T extends Mob> void invokeRegister(EntityType<T> entityType, SpawnPlacementType placementType, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate) {
        throw new AssertionError();
    }
}
