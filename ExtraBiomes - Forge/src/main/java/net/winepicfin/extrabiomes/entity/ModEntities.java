package net.winepicfin.extrabiomes.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.entity.custom.GiantTortoiseEntity;
import net.winepicfin.extrabiomes.entity.custom.HarpyEntity;
import net.winepicfin.extrabiomes.entity.custom.HoppleshroomEntity;
import net.winepicfin.extrabiomes.entity.custom.JellyfishEntity;
import net.winepicfin.extrabiomes.entity.custom.PiranhaEntity;
import net.winepicfin.extrabiomes.entity.custom.PuckooEntity;
import net.winepicfin.extrabiomes.entity.custom.TreefrogEntity;
import net.winepicfin.extrabiomes.entity.custom.WormEntity;
import net.winepicfin.extrabiomes.entity.custom.projectile.BaitProjectileEntity;
import net.winepicfin.extrabiomes.entity.custom.projectile.DiamondRazorFeatherProjectileEntity;
import net.winepicfin.extrabiomes.entity.custom.projectile.MossyPebbleProjectileEntity;
import net.winepicfin.extrabiomes.entity.custom.projectile.NetheriteRazorFeatherProjectileEntity;
import net.winepicfin.extrabiomes.entity.custom.projectile.PebbleProjectileEntity;
import net.winepicfin.extrabiomes.entity.custom.projectile.RazorFeatherProjectileEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExtraBiomes.MOD_ID);

    // ~~~~~~~~~~~~~ Mobs ~~~~~~~~~~~~~ \\
    public static final RegistryObject<EntityType<PuckooEntity>> PUCKOO = ENTITIES.register("puckoo",
            () -> EntityType.Builder.of(PuckooEntity::new, MobCategory.CREATURE).sized(0.8f, 1f).build("puckoo"));
    public static final RegistryObject<EntityType<WormEntity>> WORM = ENTITIES.register("worm",
            () -> EntityType.Builder.of(WormEntity::new, MobCategory.CREATURE).sized(0.3f, 0.2f).build("worm"));
    public static final RegistryObject<EntityType<TreefrogEntity>> TREEFROG = ENTITIES.register("treefrog",
            () -> EntityType.Builder.of(TreefrogEntity::new, MobCategory.CREATURE).sized(0.5f, 0.5f).build("treefrog"));
    public static final RegistryObject<EntityType<HoppleshroomEntity>> HOPPLESHROOM = ENTITIES.register("hoppleshroom",
            () -> EntityType.Builder.of(HoppleshroomEntity::new, MobCategory.CREATURE).sized(0.6f, 0.8f).build("hoppleshroom"));
    public static final RegistryObject<EntityType<GiantTortoiseEntity>> GIANT_TORTOISE = ENTITIES.register("giant_tortoise",
            () -> EntityType.Builder.of(GiantTortoiseEntity::new, MobCategory.CREATURE).sized(2.0f, 1.0f).build("giant_tortoise"));
    public static final RegistryObject<EntityType<JellyfishEntity>> JELLYFISH = ENTITIES.register("jellyfish",
            () -> EntityType.Builder.of(JellyfishEntity::new, MobCategory.WATER_CREATURE).sized(0.8f, 1.0f).build("jellyfish"));
    public static final RegistryObject<EntityType<PiranhaEntity>> PIRANHA = ENTITIES.register("piranha",
            () -> EntityType.Builder.of(PiranhaEntity::new, MobCategory.WATER_CREATURE).sized(0.6f, 0.3f).build("piranha"));
    public static final RegistryObject<EntityType<HarpyEntity>> HARPY = ENTITIES.register("harpy",
            () -> EntityType.Builder.of(HarpyEntity::new, MobCategory.MONSTER).sized(2.0f, 2.0f).build("harpy"));

    // ~~~~~~~~~~~~~ Projectiles ~~~~~~~~~~~~~ \\
    public static final RegistryObject<EntityType<PebbleProjectileEntity>> PEBBLE_PROJECTILE = ENTITIES.register("pebble_projectile",
            () -> EntityType.Builder.<PebbleProjectileEntity>of(PebbleProjectileEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).build("pebble_projectile"));
    public static final RegistryObject<EntityType<MossyPebbleProjectileEntity>> MOSSY_PEBBLE_PROJECTILE = ENTITIES.register("mossy_pebble_projectile",
            () -> EntityType.Builder.<MossyPebbleProjectileEntity>of(MossyPebbleProjectileEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(10).build("mossy_pebble_projectile"));
    public static final RegistryObject<EntityType<RazorFeatherProjectileEntity>> RAZOR_FEATHER = ENTITIES.register("razor_feather",
            () -> EntityType.Builder.<RazorFeatherProjectileEntity>of(RazorFeatherProjectileEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("razor_feather"));
    public static final RegistryObject<EntityType<DiamondRazorFeatherProjectileEntity>> DIAMOND_RAZOR_FEATHER = ENTITIES.register("diamond_razor_feather",
            () -> EntityType.Builder.<DiamondRazorFeatherProjectileEntity>of(DiamondRazorFeatherProjectileEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("diamond_razor_feather"));
    public static final RegistryObject<EntityType<NetheriteRazorFeatherProjectileEntity>> NETHERITE_RAZOR_FEATHER = ENTITIES.register("netherite_razor_feather",
            () -> EntityType.Builder.<NetheriteRazorFeatherProjectileEntity>of(NetheriteRazorFeatherProjectileEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("netherite_razor_feather"));
    public static final RegistryObject<EntityType<BaitProjectileEntity>> BAIT_PROJECTILE = ENTITIES.register("bait_projectile",
            () -> EntityType.Builder.<BaitProjectileEntity>of(BaitProjectileEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("bait_projectile"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
