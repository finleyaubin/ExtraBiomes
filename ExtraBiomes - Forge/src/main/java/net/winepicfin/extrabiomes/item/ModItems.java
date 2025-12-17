package net.winepicfin.extrabiomes.item;

import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.fluid.ModFluids;
import net.winepicfin.extrabiomes.item.custom.FrogHelmetItem;
import net.winepicfin.extrabiomes.item.custom.PebbleItem;
import net.winepicfin.extrabiomes.item.custom.RazorFeatherItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExtraBiomes.MOD_ID);
    public  static final RegistryObject<Item> PEBBLE = ITEMS.register("pebble",()-> new PebbleItem(new Item.Properties()));
    public  static final RegistryObject<Item> MOSSY_PEBBLE = ITEMS.register("mossy_pebble",()-> new PebbleItem(new Item.Properties()));
    public static final RegistryObject<Item> BUCKET_OF_GOO = ITEMS.register("bucket_of_goo",()-> new BucketItem(ModFluids.SOURCE_GOO, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> RAZOR_FEATHER = ITEMS.register("razor_feather",()-> new RazorFeatherItem(new Item.Properties()));
    public static final RegistryObject<Item> FROGS_LEGS = ITEMS.register("frogs_legs",()-> new Item(new Item.Properties().food(ModFoods.FROGS_LEGS)));
    public static final RegistryObject<Item> COOKED_FROGS_LEGS = ITEMS.register("cooked_frogs_legs",()-> new Item(new Item.Properties().food(ModFoods.COOKED_FROGS_LEGS)));
    public static final RegistryObject<Item> FROG_HELMET = ITEMS.register("frog_helmet",()-> new FrogHelmetItem(ModItemMaterials.FROG, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> MYSTIC_SIGN = ITEMS.register("mystic_sign",()-> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.MYSTIC_SIGN.get(),ModBlocks.MYSTIC_WALL_SIGN.get()));
    public static final RegistryObject<Item> MYSTIC_HANGING_SIGN = ITEMS.register("mystic_hanging_sign",()-> new HangingSignItem(ModBlocks.MYSTIC_HANGING_SIGN.get(),ModBlocks.MYSTIC_WALL_HANGING_SIGN.get(),new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> PALM_SIGN = ITEMS.register("palm_sign",()-> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.PALM_SIGN.get(),ModBlocks.PALM_WALL_SIGN.get()));
    public static final RegistryObject<Item> PALM_HANGING_SIGN = ITEMS.register("palm_hanging_sign",()-> new HangingSignItem(ModBlocks.PALM_HANGING_SIGN.get(),ModBlocks.PALM_WALL_HANGING_SIGN.get(),new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> SKY_SIGN = ITEMS.register("sky_sign",()-> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.SKY_SIGN.get(),ModBlocks.SKY_WALL_SIGN.get()));
    public static final RegistryObject<Item> SKY_HANGING_SIGN = ITEMS.register("sky_hanging_sign",()-> new HangingSignItem(ModBlocks.SKY_HANGING_SIGN.get(),ModBlocks.SKY_WALL_HANGING_SIGN.get(),new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> GILDED_SKY_SIGN = ITEMS.register("gilded_sky_sign",()-> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.GILDED_SKY_SIGN.get(),ModBlocks.GILDED_SKY_WALL_SIGN.get()));
    public static final RegistryObject<Item> GILDED_SKY_HANGING_SIGN = ITEMS.register("gilded_sky_hanging_sign",()-> new HangingSignItem(ModBlocks.GILDED_SKY_HANGING_SIGN.get(),ModBlocks.GILDED_SKY_WALL_HANGING_SIGN.get(),new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> PUCKOO_SPAWN_EGG= ITEMS.register("puckoo_spawn_egg",()->new ForgeSpawnEggItem(ModEntities.PUCKOO,0xffffff, 0xea7630,new Item.Properties()));


    public static void  register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
