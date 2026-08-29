package net.winepicfin.extrabiomes.fabric.event;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.fabric.mixin.SpawnPlacementsAccessor;
import net.winepicfin.extrabiomes.entity.custom.GiantTortoiseEntity;
import net.winepicfin.extrabiomes.entity.custom.HarpyEntity;
import net.winepicfin.extrabiomes.entity.custom.HoppleshroomEntity;
import net.winepicfin.extrabiomes.entity.custom.JellyfishEntity;
import net.winepicfin.extrabiomes.entity.custom.PiranhaEntity;
import net.winepicfin.extrabiomes.entity.custom.PuckooEntity;
import net.winepicfin.extrabiomes.entity.custom.TreefrogEntity;
import net.winepicfin.extrabiomes.entity.custom.WormEntity;

// Fabric equivalent of forge/.../event/ModEventBusEvents.java. Must run after ModEntities.register()
// - both attribute and spawn-placement registration need already-registered EntityTypes (spawn
// placement registration also throws if called twice for the same type, unlike Forge's
// Operation.REPLACE-aware event - fine here since this is the only registration site).
public class FabricModEvents {
    public static void register() {
        FabricDefaultAttributeRegistry.register(ModEntities.PUCKOO.get(), PuckooEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.WORM.get(), WormEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.TREEFROG.get(), TreefrogEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.HOPPLESHROOM.get(), HoppleshroomEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.GIANT_TORTOISE.get(), GiantTortoiseEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.JELLYFISH.get(), JellyfishEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.PIRANHA.get(), PiranhaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.HARPY.get(), HarpyEntity.createAttributes());

        SpawnPlacementsAccessor.invokeRegister(ModEntities.WORM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WormEntity::checkWormSpawnRules);
        SpawnPlacementsAccessor.invokeRegister(ModEntities.TREEFROG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules);
        SpawnPlacementsAccessor.invokeRegister(ModEntities.HOPPLESHROOM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules);
        SpawnPlacementsAccessor.invokeRegister(ModEntities.GIANT_TORTOISE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);
        SpawnPlacementsAccessor.invokeRegister(ModEntities.JELLYFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        SpawnPlacementsAccessor.invokeRegister(ModEntities.PIRANHA.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PiranhaEntity::checkPiranhaSpawnRules);
        SpawnPlacementsAccessor.invokeRegister(ModEntities.HARPY.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                HarpyEntity::checkHarpySpawnRules);
        SpawnPlacementsAccessor.invokeRegister(ModEntities.PUCKOO.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules);
    }
}
