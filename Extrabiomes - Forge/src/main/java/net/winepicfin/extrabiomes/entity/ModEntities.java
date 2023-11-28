package net.winepicfin.extrabiomes.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.PuckooEntity;
import net.winepicfin.extrabiomes.entity.custom.projectile.PebbleProjectileEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExtraBiomes.MOD_ID);

    public static final RegistryObject<EntityType<PuckooEntity>> PUCKOO=ENTITIES.register("puckoo",()->EntityType.Builder.of(PuckooEntity::new, MobCategory.CREATURE)
            .sized(0.8f,1f).build("puckoo"));
    public static final RegistryObject<EntityType<PebbleProjectileEntity>> PEBBLE_PROJECTILE=ENTITIES.register("pebble_projectile",()->EntityType.Builder.<PebbleProjectileEntity>of(PebbleProjectileEntity::new, MobCategory.MISC)
            .sized(0.5f,0.5f).build("pebble_projectile"));


    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
