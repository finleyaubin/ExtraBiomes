package net.winepicfin.extrabiomes.item;

import net.minecraft.world.item.*;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.custom.projectile.BaitProjectileEntity;
import net.winepicfin.extrabiomes.item.custom.BaitItem;
import net.winepicfin.extrabiomes.item.custom.ExtraBiomesSpawnEggItem;
import net.winepicfin.extrabiomes.item.custom.DiamondRazorFeatherItem;
import net.winepicfin.extrabiomes.item.custom.JellyfishJamBottleItem;
import net.winepicfin.extrabiomes.item.custom.JellyfishingNetItem;
import net.winepicfin.extrabiomes.item.custom.NetheriteRazorFeatherItem;
import net.winepicfin.extrabiomes.item.custom.PebbleItem;
import net.winepicfin.extrabiomes.item.custom.RazorFeatherItem;
import net.winepicfin.extrabiomes.item.custom.WormItem;
import net.winepicfin.extrabiomes.platform.ExtraBiomesExpectPlatform;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.ITEM);
    public  static final RegistrySupplier<Item> PEBBLE = ITEMS.register("pebble",()-> new PebbleItem(new Item.Properties()));
    public  static final RegistrySupplier<Item> MOSSY_PEBBLE = ITEMS.register("mossy_pebble",()-> new PebbleItem(new Item.Properties()));
    public static final RegistrySupplier<Item> BUCKET_OF_GOO = ITEMS.register("bucket_of_goo",()-> ExtraBiomesExpectPlatform.createBucketOfGooItem(new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistrySupplier<Item> RAZOR_FEATHER = ITEMS.register("razor_feather",()-> new RazorFeatherItem(new Item.Properties()));
    public static final RegistrySupplier<Item> DIAMOND_RAZOR_FEATHER = ITEMS.register("diamond_razor_feather",()-> new DiamondRazorFeatherItem(new Item.Properties()));
    public static final RegistrySupplier<Item> NETHERITE_RAZOR_FEATHER = ITEMS.register("netherite_razor_feather",()-> new NetheriteRazorFeatherItem(new Item.Properties().fireResistant()));
    // durability(), not stacksTo() - a picked-back-up bait needs to carry its remaining health as a
    // damage bar (see BaitProjectileEntity.interact()), and vanilla items can't be both damageable
    // and stackable, so this now stacks to 1 like any other durability item.
    public static final RegistrySupplier<Item> BAIT = ITEMS.register("bait",()-> new BaitItem(new Item.Properties().durability(BaitProjectileEntity.MAX_HEALTH)));
    public static final RegistrySupplier<Item> FROGS_LEGS = ITEMS.register("frogs_legs",()-> new Item(new Item.Properties().food(ModFoods.FROGS_LEGS)));
    public static final RegistrySupplier<Item> COOKED_FROGS_LEGS = ITEMS.register("cooked_frogs_legs",()-> new Item(new Item.Properties().food(ModFoods.COOKED_FROGS_LEGS)));
    public static final RegistrySupplier<Item> PIRANHA = ITEMS.register("piranha",()-> new Item(new Item.Properties().food(ModFoods.PIRANHA)));
    public static final RegistrySupplier<Item> COOKED_PIRANHA = ITEMS.register("cooked_piranha",()-> new Item(new Item.Properties().food(ModFoods.COOKED_PIRANHA)));
    public static final RegistrySupplier<Item> WORM = ITEMS.register("worm",()-> new WormItem(new Item.Properties()));
    public static final RegistrySupplier<Item> JELLYFISH_JAM_BOTTLE = ITEMS.register("jellyfish_jam_bottle",()-> new JellyfishJamBottleItem(new Item.Properties().food(ModFoods.JELLYFISH_JAM).stacksTo(16).craftRemainder(Items.GLASS_BOTTLE)));
    public static final RegistrySupplier<Item> JELLYFISHING_NET_EMPTY = ITEMS.register("jellyfishing_net_empty",()-> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistrySupplier<Item> JELLYFISHING_NET_FULL = ITEMS.register("jellyfishing_net_full",()-> new JellyfishingNetItem(new Item.Properties().stacksTo(1)));
    public static final RegistrySupplier<Item> FROG_HELMET = ITEMS.register("frog_helmet",()-> ExtraBiomesExpectPlatform.createFrogHelmetItem(ModItemMaterials.FROG, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistrySupplier<Item> MYSTIC_SIGN = ITEMS.register("mystic_sign",()-> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.MYSTIC_SIGN.get(),ModBlocks.MYSTIC_WALL_SIGN.get()));
    public static final RegistrySupplier<Item> MYSTIC_HANGING_SIGN = ITEMS.register("mystic_hanging_sign",()-> new HangingSignItem(ModBlocks.MYSTIC_HANGING_SIGN.get(),ModBlocks.MYSTIC_WALL_HANGING_SIGN.get(),new Item.Properties().stacksTo(16)));
    public static final RegistrySupplier<Item> PALM_SIGN = ITEMS.register("palm_sign",()-> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.PALM_SIGN.get(),ModBlocks.PALM_WALL_SIGN.get()));
    public static final RegistrySupplier<Item> PALM_HANGING_SIGN = ITEMS.register("palm_hanging_sign",()-> new HangingSignItem(ModBlocks.PALM_HANGING_SIGN.get(),ModBlocks.PALM_WALL_HANGING_SIGN.get(),new Item.Properties().stacksTo(16)));
    public static final RegistrySupplier<Item> SKY_SIGN = ITEMS.register("sky_sign",()-> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.SKY_SIGN.get(),ModBlocks.SKY_WALL_SIGN.get()));
    public static final RegistrySupplier<Item> SKY_HANGING_SIGN = ITEMS.register("sky_hanging_sign",()-> new HangingSignItem(ModBlocks.SKY_HANGING_SIGN.get(),ModBlocks.SKY_WALL_HANGING_SIGN.get(),new Item.Properties().stacksTo(16)));
    public static final RegistrySupplier<Item> GILDED_SKY_SIGN = ITEMS.register("gilded_sky_sign",()-> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.GILDED_SKY_SIGN.get(),ModBlocks.GILDED_SKY_WALL_SIGN.get()));
    public static final RegistrySupplier<Item> GILDED_SKY_HANGING_SIGN = ITEMS.register("gilded_sky_hanging_sign",()-> new HangingSignItem(ModBlocks.GILDED_SKY_HANGING_SIGN.get(),ModBlocks.GILDED_SKY_WALL_HANGING_SIGN.get(),new Item.Properties().stacksTo(16)));
    public static final RegistrySupplier<Item> PUCKOO_SPAWN_EGG= ITEMS.register("puckoo_spawn_egg",()->new ExtraBiomesSpawnEggItem(ModEntities.PUCKOO,0xffffff, 0xea7630,new Item.Properties()));
    public static final RegistrySupplier<Item> WORM_SPAWN_EGG = ITEMS.register("worm_spawn_egg",()->new ExtraBiomesSpawnEggItem(ModEntities.WORM,0xd98b7b, 0xb35a4a,new Item.Properties()));
    public static final RegistrySupplier<Item> TREEFROG_SPAWN_EGG = ITEMS.register("treefrog_spawn_egg",()->new ExtraBiomesSpawnEggItem(ModEntities.TREEFROG,0x5aa63c, 0xe0d84a,new Item.Properties()));
    public static final RegistrySupplier<Item> HOPPLESHROOM_SPAWN_EGG = ITEMS.register("hoppleshroom_spawn_egg",()->new ExtraBiomesSpawnEggItem(ModEntities.HOPPLESHROOM,0xc94b4b, 0xe6d2b5,new Item.Properties()));
    public static final RegistrySupplier<Item> GIANT_TORTOISE_SPAWN_EGG = ITEMS.register("giant_tortoise_spawn_egg",()->new ExtraBiomesSpawnEggItem(ModEntities.GIANT_TORTOISE,0x3e6b3a, 0x8a6b3f,new Item.Properties()));
    public static final RegistrySupplier<Item> JELLYFISH_SPAWN_EGG = ITEMS.register("jellyfish_spawn_egg",()->new ExtraBiomesSpawnEggItem(ModEntities.JELLYFISH,0xe57fb0, 0x9b5ac2,new Item.Properties()));
    public static final RegistrySupplier<Item> PIRANHA_SPAWN_EGG = ITEMS.register("piranha_spawn_egg",()->new ExtraBiomesSpawnEggItem(ModEntities.PIRANHA,0x556b2f, 0xc23b2b,new Item.Properties()));
    public static final RegistrySupplier<Item> HARPY_SPAWN_EGG = ITEMS.register("harpy_spawn_egg",()->new ExtraBiomesSpawnEggItem(ModEntities.HARPY,0x8a7b6b, 0x4a3f34,new Item.Properties()));


    public static void register() {
        ITEMS.register();
    }
}
