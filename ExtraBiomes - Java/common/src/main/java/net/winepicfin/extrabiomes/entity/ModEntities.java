package net.winepicfin.extrabiomes.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import dev.architectury.registry.registries.RegistrySupplier;
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
import net.winepicfin.extrabiomes.item.ModItems;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<PuckooEntity>> PUCKOO = ENTITIES.register("puckoo",
            () -> EntityType.Builder.of(PuckooEntity::new, MobCategory.CREATURE).sized(0.8f, 1f).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "puckoo"))));
    public static final RegistrySupplier<EntityType<WormEntity>> WORM = ENTITIES.register("worm",
            () -> EntityType.Builder.of(WormEntity::new, MobCategory.CREATURE).sized(0.3f, 0.2f).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "worm"))));
    public static final RegistrySupplier<EntityType<TreefrogEntity>> TREEFROG = ENTITIES.register("treefrog",
            () -> EntityType.Builder.of(TreefrogEntity::new, MobCategory.CREATURE).sized(0.5f, 0.5f).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "treefrog"))));
    public static final RegistrySupplier<EntityType<HoppleshroomEntity>> HOPPLESHROOM = ENTITIES.register("hoppleshroom",
            () -> EntityType.Builder.of(HoppleshroomEntity::new, MobCategory.CREATURE).sized(0.6f, 0.8f).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "hoppleshroom"))));
    public static final RegistrySupplier<EntityType<GiantTortoiseEntity>> GIANT_TORTOISE = ENTITIES.register("giant_tortoise",
            () -> EntityType.Builder.of(GiantTortoiseEntity::new, MobCategory.MONSTER).sized(1.6f, 1.0f).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "giant_tortoise"))));
    public static final RegistrySupplier<EntityType<JellyfishEntity>> JELLYFISH = ENTITIES.register("jellyfish",
            () -> EntityType.Builder.of(JellyfishEntity::new, MobCategory.WATER_CREATURE).sized(0.8f, 1.0f).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "jellyfish"))));
    // WATER_AMBIENT, not WATER_CREATURE: its higher per-area spawn cap (20 vs 5) was what kept jungle water from teeming, and its despawn distance is closer to Bedrock's.
    public static final RegistrySupplier<EntityType<PiranhaEntity>> PIRANHA = ENTITIES.register("piranha",
            () -> EntityType.Builder.of(PiranhaEntity::new, MobCategory.WATER_AMBIENT).sized(0.6f, 0.3f).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "piranha"))));
    public static final RegistrySupplier<EntityType<HarpyEntity>> HARPY = ENTITIES.register("harpy",
            () -> EntityType.Builder.of(HarpyEntity::new, MobCategory.MONSTER).sized(2.0f, 2.0f).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "harpy"))));

    public static final RegistrySupplier<EntityType<PebbleProjectileEntity>> PEBBLE_PROJECTILE = ENTITIES.register("pebble_projectile",
            () -> EntityType.Builder.<PebbleProjectileEntity>of(PebbleProjectileEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "pebble_projectile"))));
    public static final RegistrySupplier<EntityType<MossyPebbleProjectileEntity>> MOSSY_PEBBLE_PROJECTILE = ENTITIES.register("mossy_pebble_projectile",
            () -> EntityType.Builder.<MossyPebbleProjectileEntity>of(MossyPebbleProjectileEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(10).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "mossy_pebble_projectile"))));
    public static final RegistrySupplier<EntityType<RazorFeatherProjectileEntity>> RAZOR_FEATHER = ENTITIES.register("razor_feather",
            () -> EntityType.Builder.<RazorFeatherProjectileEntity>of(RazorFeatherProjectileEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "razor_feather"))));
    public static final RegistrySupplier<EntityType<DiamondRazorFeatherProjectileEntity>> DIAMOND_RAZOR_FEATHER = ENTITIES.register("diamond_razor_feather",
            () -> EntityType.Builder.<DiamondRazorFeatherProjectileEntity>of(DiamondRazorFeatherProjectileEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "diamond_razor_feather"))));
    public static final RegistrySupplier<EntityType<NetheriteRazorFeatherProjectileEntity>> NETHERITE_RAZOR_FEATHER = ENTITIES.register("netherite_razor_feather",
            () -> EntityType.Builder.<NetheriteRazorFeatherProjectileEntity>of(NetheriteRazorFeatherProjectileEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "netherite_razor_feather"))));
    // Sized to match Bedrock's minecraft:collision_box (0.4 wide, 0.5 tall) rather than the 0.25 default other thrown projectiles use, since the worm model spreads well beyond a snowball-sized box.
    public static final RegistrySupplier<EntityType<BaitProjectileEntity>> BAIT_PROJECTILE = ENTITIES.register("bait_projectile",
            () -> EntityType.Builder.<BaitProjectileEntity>of(BaitProjectileEntity::new, MobCategory.MISC).sized(0.4f, 0.5f).clientTrackingRange(4).updateInterval(10).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, "bait_projectile"))));

    // Vanilla (1.21.2+) keys boats to a dedicated EntityType per wood rather than a shared Boat.Type
    // enum - Boat/ChestBoat themselves are plain vanilla classes, no subclass needed. sized/eyeHeight/
    // clientTrackingRange/noLootTable mirror vanilla's own oak_boat/oak_chest_boat registration exactly
    // (boats drop themselves via getDropItem(), never a loot table).
    public static final RegistrySupplier<EntityType<Boat>> MYSTIC_BOAT = registerBoat("mystic_boat", () -> ModItems.MYSTIC_BOAT.get());
    public static final RegistrySupplier<EntityType<ChestBoat>> MYSTIC_CHEST_BOAT = registerChestBoat("mystic_chest_boat", () -> ModItems.MYSTIC_CHEST_BOAT.get());
    public static final RegistrySupplier<EntityType<Boat>> PALM_BOAT = registerBoat("palm_boat", () -> ModItems.PALM_BOAT.get());
    public static final RegistrySupplier<EntityType<ChestBoat>> PALM_CHEST_BOAT = registerChestBoat("palm_chest_boat", () -> ModItems.PALM_CHEST_BOAT.get());
    public static final RegistrySupplier<EntityType<Boat>> SKY_BOAT = registerBoat("sky_boat", () -> ModItems.SKY_BOAT.get());
    public static final RegistrySupplier<EntityType<ChestBoat>> SKY_CHEST_BOAT = registerChestBoat("sky_chest_boat", () -> ModItems.SKY_CHEST_BOAT.get());
    public static final RegistrySupplier<EntityType<Boat>> GILDED_SKY_BOAT = registerBoat("gilded_sky_boat", () -> ModItems.GILDED_SKY_BOAT.get());
    public static final RegistrySupplier<EntityType<ChestBoat>> GILDED_SKY_CHEST_BOAT = registerChestBoat("gilded_sky_chest_boat", () -> ModItems.GILDED_SKY_CHEST_BOAT.get());

    private static RegistrySupplier<EntityType<Boat>> registerBoat(String name, Supplier<Item> dropItem) {
        return ENTITIES.register(name, () -> EntityType.Builder.<Boat>of(
                        (type, level) -> new Boat(type, level, dropItem), MobCategory.MISC)
                .noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
                .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name))));
    }

    private static RegistrySupplier<EntityType<ChestBoat>> registerChestBoat(String name, Supplier<Item> dropItem) {
        return ENTITIES.register(name, () -> EntityType.Builder.<ChestBoat>of(
                        (type, level) -> new ChestBoat(type, level, dropItem), MobCategory.MISC)
                .noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
                .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name))));
    }

    public static void register() {
        ENTITIES.register();
    }
}
