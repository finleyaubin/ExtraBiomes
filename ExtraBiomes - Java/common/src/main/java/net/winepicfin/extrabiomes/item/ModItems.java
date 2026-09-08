package net.winepicfin.extrabiomes.item;

import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.entity.custom.projectile.BaitProjectileEntity;
import net.winepicfin.extrabiomes.item.custom.BaitItem;
import net.winepicfin.extrabiomes.item.custom.DiamondRazorFeatherItem;
import net.winepicfin.extrabiomes.item.custom.JellyfishJamBottleItem;
import net.winepicfin.extrabiomes.item.custom.JellyfishingNetItem;
import net.winepicfin.extrabiomes.item.custom.NetheriteRazorFeatherItem;
import net.winepicfin.extrabiomes.item.custom.PebbleItem;
import net.winepicfin.extrabiomes.item.custom.RazorFeatherItem;
import net.winepicfin.extrabiomes.item.custom.WormItem;
import net.winepicfin.extrabiomes.platform.ExtraBiomesExpectPlatform;

import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ExtraBiomes.MOD_ID, Registries.ITEM);
    public  static final RegistrySupplier<Item> PEBBLE = ITEMS.register("pebble",()-> new PebbleItem(new Item.Properties().setId(itemId("pebble"))));
    public  static final RegistrySupplier<Item> MOSSY_PEBBLE = ITEMS.register("mossy_pebble",()-> new PebbleItem(new Item.Properties().setId(itemId("mossy_pebble"))));
    public static final RegistrySupplier<Item> BUCKET_OF_GOO = ITEMS.register("bucket_of_goo",()-> ExtraBiomesExpectPlatform.createBucketOfGooItem(new Item.Properties().setId(itemId("bucket_of_goo")).craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistrySupplier<Item> RAZOR_FEATHER = ITEMS.register("razor_feather",()-> new RazorFeatherItem(new Item.Properties().setId(itemId("razor_feather"))));
    public static final RegistrySupplier<Item> DIAMOND_RAZOR_FEATHER = ITEMS.register("diamond_razor_feather",()-> new DiamondRazorFeatherItem(new Item.Properties().setId(itemId("diamond_razor_feather"))));
    public static final RegistrySupplier<Item> NETHERITE_RAZOR_FEATHER = ITEMS.register("netherite_razor_feather",()-> new NetheriteRazorFeatherItem(new Item.Properties().setId(itemId("netherite_razor_feather")).fireResistant()));
    // durability(), not stacksTo() - a picked-back-up bait needs to carry its remaining health as a
    // damage bar (see BaitProjectileEntity.interact()), and vanilla items can't be both damageable
    // and stackable, so this now stacks to 1 like any other durability item.
    public static final RegistrySupplier<Item> BAIT = ITEMS.register("bait",()-> new BaitItem(new Item.Properties().setId(itemId("bait")).durability(BaitProjectileEntity.MAX_HEALTH)));
    public static final RegistrySupplier<Item> FROGS_LEGS = ITEMS.register("frogs_legs",()-> new Item(new Item.Properties().setId(itemId("frogs_legs")).food(ModFoods.FROGS_LEGS, ModFoods.FROGS_LEGS_CONSUMABLE)));
    public static final RegistrySupplier<Item> COOKED_FROGS_LEGS = ITEMS.register("cooked_frogs_legs",()-> new Item(new Item.Properties().setId(itemId("cooked_frogs_legs")).food(ModFoods.COOKED_FROGS_LEGS)));
    public static final RegistrySupplier<Item> PIRANHA = ITEMS.register("piranha",()-> new Item(new Item.Properties().setId(itemId("piranha")).food(ModFoods.PIRANHA)));
    public static final RegistrySupplier<Item> COOKED_PIRANHA = ITEMS.register("cooked_piranha",()-> new Item(new Item.Properties().setId(itemId("cooked_piranha")).food(ModFoods.COOKED_PIRANHA)));
    public static final RegistrySupplier<Item> WORM = ITEMS.register("worm",()-> new WormItem(new Item.Properties().setId(itemId("worm"))));
    public static final RegistrySupplier<Item> JELLYFISH_JAM_BOTTLE = ITEMS.register("jellyfish_jam_bottle",()-> new JellyfishJamBottleItem(new Item.Properties().setId(itemId("jellyfish_jam_bottle")).food(ModFoods.JELLYFISH_JAM, ModFoods.JELLYFISH_JAM_CONSUMABLE).stacksTo(16).craftRemainder(Items.GLASS_BOTTLE)));
    public static final RegistrySupplier<Item> JELLYFISHING_NET_EMPTY = ITEMS.register("jellyfishing_net_empty",()-> new Item(new Item.Properties().setId(itemId("jellyfishing_net_empty")).stacksTo(1)));
    public static final RegistrySupplier<Item> JELLYFISHING_NET_FULL = ITEMS.register("jellyfishing_net_full",()-> new JellyfishingNetItem(new Item.Properties().setId(itemId("jellyfishing_net_full")).stacksTo(1)));
    public static final RegistrySupplier<Item> FROG_HELMET = ITEMS.register("frog_helmet",()-> ExtraBiomesExpectPlatform.createFrogHelmetItem(ModItemMaterials.FROG, ArmorType.HELMET, new Item.Properties().setId(itemId("frog_helmet")).durability(325)));
    public static final RegistrySupplier<Item> MYSTIC_SIGN = ITEMS.register("mystic_sign",()-> new SignItem(ModBlocks.MYSTIC_SIGN.get(),ModBlocks.MYSTIC_WALL_SIGN.get(), new Item.Properties().setId(itemId("mystic_sign")).stacksTo(16)));
    public static final RegistrySupplier<Item> MYSTIC_HANGING_SIGN = ITEMS.register("mystic_hanging_sign",()-> new HangingSignItem(ModBlocks.MYSTIC_HANGING_SIGN.get(),ModBlocks.MYSTIC_WALL_HANGING_SIGN.get(),new Item.Properties().setId(itemId("mystic_hanging_sign")).stacksTo(16)));
    public static final RegistrySupplier<Item> PALM_SIGN = ITEMS.register("palm_sign",()-> new SignItem(ModBlocks.PALM_SIGN.get(),ModBlocks.PALM_WALL_SIGN.get(), new Item.Properties().setId(itemId("palm_sign")).stacksTo(16)));
    public static final RegistrySupplier<Item> PALM_HANGING_SIGN = ITEMS.register("palm_hanging_sign",()-> new HangingSignItem(ModBlocks.PALM_HANGING_SIGN.get(),ModBlocks.PALM_WALL_HANGING_SIGN.get(),new Item.Properties().setId(itemId("palm_hanging_sign")).stacksTo(16)));
    public static final RegistrySupplier<Item> SKY_SIGN = ITEMS.register("sky_sign",()-> new SignItem(ModBlocks.SKY_SIGN.get(),ModBlocks.SKY_WALL_SIGN.get(), new Item.Properties().setId(itemId("sky_sign")).stacksTo(16)));
    public static final RegistrySupplier<Item> SKY_HANGING_SIGN = ITEMS.register("sky_hanging_sign",()-> new HangingSignItem(ModBlocks.SKY_HANGING_SIGN.get(),ModBlocks.SKY_WALL_HANGING_SIGN.get(),new Item.Properties().setId(itemId("sky_hanging_sign")).stacksTo(16)));
    public static final RegistrySupplier<Item> GILDED_SKY_SIGN = ITEMS.register("gilded_sky_sign",()-> new SignItem(ModBlocks.GILDED_SKY_SIGN.get(),ModBlocks.GILDED_SKY_WALL_SIGN.get(), new Item.Properties().setId(itemId("gilded_sky_sign")).stacksTo(16)));
    public static final RegistrySupplier<Item> GILDED_SKY_HANGING_SIGN = ITEMS.register("gilded_sky_hanging_sign",()-> new HangingSignItem(ModBlocks.GILDED_SKY_HANGING_SIGN.get(),ModBlocks.GILDED_SKY_WALL_HANGING_SIGN.get(),new Item.Properties().setId(itemId("gilded_sky_hanging_sign")).stacksTo(16)));

    public static final RegistrySupplier<Item> MYSTIC_BOAT = ITEMS.register("mystic_boat",()-> new BoatItem(ModEntities.MYSTIC_BOAT.get(), new Item.Properties().setId(itemId("mystic_boat")).stacksTo(1)));
    public static final RegistrySupplier<Item> MYSTIC_CHEST_BOAT = ITEMS.register("mystic_chest_boat",()-> new BoatItem(ModEntities.MYSTIC_CHEST_BOAT.get(), new Item.Properties().setId(itemId("mystic_chest_boat")).stacksTo(1)));
    public static final RegistrySupplier<Item> PALM_BOAT = ITEMS.register("palm_boat",()-> new BoatItem(ModEntities.PALM_BOAT.get(), new Item.Properties().setId(itemId("palm_boat")).stacksTo(1)));
    public static final RegistrySupplier<Item> PALM_CHEST_BOAT = ITEMS.register("palm_chest_boat",()-> new BoatItem(ModEntities.PALM_CHEST_BOAT.get(), new Item.Properties().setId(itemId("palm_chest_boat")).stacksTo(1)));
    public static final RegistrySupplier<Item> SKY_BOAT = ITEMS.register("sky_boat",()-> new BoatItem(ModEntities.SKY_BOAT.get(), new Item.Properties().setId(itemId("sky_boat")).stacksTo(1)));
    public static final RegistrySupplier<Item> SKY_CHEST_BOAT = ITEMS.register("sky_chest_boat",()-> new BoatItem(ModEntities.SKY_CHEST_BOAT.get(), new Item.Properties().setId(itemId("sky_chest_boat")).stacksTo(1)));
    public static final RegistrySupplier<Item> GILDED_SKY_BOAT = ITEMS.register("gilded_sky_boat",()-> new BoatItem(ModEntities.GILDED_SKY_BOAT.get(), new Item.Properties().setId(itemId("gilded_sky_boat")).stacksTo(1)));
    public static final RegistrySupplier<Item> GILDED_SKY_CHEST_BOAT = ITEMS.register("gilded_sky_chest_boat",()-> new BoatItem(ModEntities.GILDED_SKY_CHEST_BOAT.get(), new Item.Properties().setId(itemId("gilded_sky_chest_boat")).stacksTo(1)));

    // Shared across all 3 loaders' datagen (item tags, item models, creative tab) so the "which wood
    // types have boats, and which texture each uses" list only lives once. BoatModelEntry's texture is
    // the filename stem under textures/item/ - every boat and chest boat item now has its own editable
    // file (chest boat icons are placeholder copies of vanilla's oak_chest_boat.png - see
    // ModItemModelProvider's boatItem() javadoc-comment in each loader for the full texture story).
    public static final List<RegistrySupplier<Item>> BOAT_ITEMS = List.of(MYSTIC_BOAT, PALM_BOAT, SKY_BOAT, GILDED_SKY_BOAT);
    public static final List<RegistrySupplier<Item>> CHEST_BOAT_ITEMS = List.of(MYSTIC_CHEST_BOAT, PALM_CHEST_BOAT, SKY_CHEST_BOAT, GILDED_SKY_CHEST_BOAT);

    public record BoatModelEntry(RegistrySupplier<Item> item, String texture) {
    }

    public static final List<BoatModelEntry> BOAT_MODEL_ENTRIES = List.of(
            new BoatModelEntry(MYSTIC_BOAT, "boat_mystic"),
            new BoatModelEntry(MYSTIC_CHEST_BOAT, "boat_mystic_chest"),
            new BoatModelEntry(PALM_BOAT, "boat_palm"),
            new BoatModelEntry(PALM_CHEST_BOAT, "boat_palm_chest"),
            new BoatModelEntry(SKY_BOAT, "boat_sky"),
            new BoatModelEntry(SKY_CHEST_BOAT, "boat_sky_chest"),
            new BoatModelEntry(GILDED_SKY_BOAT, "boat_gilded_sky"),
            new BoatModelEntry(GILDED_SKY_CHEST_BOAT, "boat_gilded_sky_chest")
    );
    // Colors match the Bedrock addon's spawn_egg base_color/overlay_color exactly (see the entity
    // .entity.json files under ExtraBiomes - Bedrock/packs/RP/entity/) so the egg tint is consistent
    // across both editions.
    public static final RegistrySupplier<Item> PUCKOO_SPAWN_EGG= ITEMS.register("puckoo_spawn_egg",()->ExtraBiomesExpectPlatform.createSpawnEggItem(ModEntities.PUCKOO,0xffffff, 0xea7630,new Item.Properties().setId(itemId("puckoo_spawn_egg"))));
    public static final RegistrySupplier<Item> WORM_SPAWN_EGG = ITEMS.register("worm_spawn_egg",()->ExtraBiomesExpectPlatform.createSpawnEggItem(ModEntities.WORM,0xff81d9, 0xff4343,new Item.Properties().setId(itemId("worm_spawn_egg"))));
    public static final RegistrySupplier<Item> TREEFROG_SPAWN_EGG = ITEMS.register("treefrog_spawn_egg",()->ExtraBiomesExpectPlatform.createSpawnEggItem(ModEntities.TREEFROG,0x329b17, 0x034722,new Item.Properties().setId(itemId("treefrog_spawn_egg"))));
    public static final RegistrySupplier<Item> HOPPLESHROOM_SPAWN_EGG = ITEMS.register("hoppleshroom_spawn_egg",()->ExtraBiomesExpectPlatform.createSpawnEggItem(ModEntities.HOPPLESHROOM,0x9b1717, 0xfdd8d8,new Item.Properties().setId(itemId("hoppleshroom_spawn_egg"))));
    public static final RegistrySupplier<Item> GIANT_TORTOISE_SPAWN_EGG = ITEMS.register("giant_tortoise_spawn_egg",()->ExtraBiomesExpectPlatform.createSpawnEggItem(ModEntities.GIANT_TORTOISE,0x364710, 0xa66643,new Item.Properties().setId(itemId("giant_tortoise_spawn_egg"))));
    public static final RegistrySupplier<Item> JELLYFISH_SPAWN_EGG = ITEMS.register("jellyfish_spawn_egg",()->ExtraBiomesExpectPlatform.createSpawnEggItem(ModEntities.JELLYFISH,0x932a9e, 0xdc7ce6,new Item.Properties().setId(itemId("jellyfish_spawn_egg"))));
    public static final RegistrySupplier<Item> PIRANHA_SPAWN_EGG = ITEMS.register("piranha_spawn_egg",()->ExtraBiomesExpectPlatform.createSpawnEggItem(ModEntities.PIRANHA,0x444444, 0x251515,new Item.Properties().setId(itemId("piranha_spawn_egg"))));
    public static final RegistrySupplier<Item> HARPY_SPAWN_EGG = ITEMS.register("harpy_spawn_egg",()->ExtraBiomesExpectPlatform.createSpawnEggItem(ModEntities.HARPY,0x2319af, 0xe9c600,new Item.Properties().setId(itemId("harpy_spawn_egg"))));


    private static ResourceKey<Item> itemId(String name) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ExtraBiomes.MOD_ID, name));
    }

    public static void register() {
        ModItemMaterials.register();
        ITEMS.register();
    }
}
