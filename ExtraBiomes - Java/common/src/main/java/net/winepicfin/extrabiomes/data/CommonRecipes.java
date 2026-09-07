package net.winepicfin.extrabiomes.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

import java.util.List;

/**
 * Shared, loader-agnostic recipe content for the Forge and Fabric {@code ModRecipeProvider}s.
 * <p>
 * This extends vanilla {@link RecipeProvider} (rather than being a plain standalone class) purely so
 * its methods can call the {@code protected} {@code has(...)}/{@code shaped(...)}/{@code shapeless(...)}
 * and {@code static getHasName(...)} helpers declared on {@link RecipeProvider}. Reusing those vanilla
 * helpers - instead of reimplementing them - guarantees the generated advancement criteria stay
 * byte-identical to the previous per-loader copies. {@code has()}/{@code shaped()}/{@code shapeless()}
 * became instance methods as of 1.21.2 (they resolve items through the provider's own item
 * {@code HolderGetter}), so unlike the old static-only design this now needs a real, if throwaway,
 * instance - {@link #build} constructs one as an anonymous subclass purely to run {@link #buildRecipes()}
 * once.
 *
 * <p>Lives in {@code ...extrabiomes.data} rather than the more obvious {@code ...extrabiomes.datagen}
 * because forge/ already owns that package name. Two modules exporting the same package is a JPMS
 * split package, which Forge's ModLauncher rejects at boot with
 * "Modules ... and extrabiomes export package net.winepicfin.extrabiomes.datagen" - it compiles
 * fine and only fails when a run task actually starts, so keep this package distinct from every
 * package under forge/ and fabric/.
 */
public abstract class CommonRecipes extends RecipeProvider {

    protected CommonRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static final List<ItemLike> DIAMOND_SMELTABLES = List.of(ModBlocks.NETHER_DIAMOND_ORE.get());
    public static final List<ItemLike> FROG_SMELTABLES = List.of(ModItems.FROGS_LEGS.get());
    public static final List<ItemLike> PIRANHA_SMELTABLES = List.of(ModItems.PIRANHA.get());

    public static void build(HolderLookup.Provider registries, RecipeOutput pWriter) {
        new CommonRecipes(registries, pWriter) {
            @Override
            protected void buildRecipes() {
                this.buildAll(pWriter);
            }
        }.buildRecipes();
    }

    protected void buildAll(RecipeOutput pWriter) {
        shaped(RecipeCategory.MISC, ModItems.BAIT.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.WORM.get())
                .unlockedBy(getHasName(ModItems.WORM.get()), has(ModItems.WORM.get()))
                .save(pWriter);
        oreBlasting(pWriter, DIAMOND_SMELTABLES, RecipeCategory.MISC, Items.DIAMOND, 0.25f, 100, "diamond", Boolean.TRUE);
        foodCooking(pWriter, FROG_SMELTABLES, RecipeCategory.MISC, ModItems.COOKED_FROGS_LEGS.get(), 0.25f, 100, "cooked_frogs_legs", Boolean.TRUE);
        foodCooking(pWriter, PIRANHA_SMELTABLES, RecipeCategory.FOOD, ModItems.COOKED_PIRANHA.get(), 0.25f, 100, "cooked_piranha", Boolean.TRUE);
        pebbleRecipes(pWriter);
        razorFeatherRecipes(pWriter);
        stickPileRecipes(pWriter);
        woodRecipes(pWriter, ModBlocks.MYSTIC_PLANKS.get(), ModBlocks.MYSTIC_LOG.get(), ModBlocks.MYSTIC_WOOD.get(), ModBlocks.STRIPPED_MYSTIC_LOG.get(), ModBlocks.STRIPPED_MYSTIC_WOOD.get(), ModBlocks.MYSTIC_STAIRS.get(), ModBlocks.MYSTIC_SLAB.get(), ModBlocks.MYSTIC_BUTTON.get(), ModBlocks.MYSTIC_PRESSURE_PLATE.get(), ModBlocks.MYSTIC_FENCE_GATE.get(), ModBlocks.MYSTIC_FENCE.get(), ModBlocks.MYSTIC_DOOR.get(), ModBlocks.MYSTIC_TRAPDOOR.get(),ModBlocks.MYSTIC_SIGN.get());
        woodRecipes(pWriter, ModBlocks.PALM_PLANKS.get(), ModBlocks.PALM_LOG.get(), ModBlocks.PALM_WOOD.get(), ModBlocks.STRIPPED_PALM_LOG.get(), ModBlocks.STRIPPED_PALM_WOOD.get(), ModBlocks.PALM_STAIRS.get(), ModBlocks.PALM_SLAB.get(), ModBlocks.PALM_BUTTON.get(), ModBlocks.PALM_PRESSURE_PLATE.get(), ModBlocks.PALM_FENCE_GATE.get(), ModBlocks.PALM_FENCE.get(), ModBlocks.PALM_DOOR.get(), ModBlocks.PALM_TRAPDOOR.get(), ModBlocks.PALM_SIGN.get());
        woodRecipes(pWriter, ModBlocks.SKY_PLANKS.get(), ModBlocks.SKY_LOG.get(), ModBlocks.SKY_WOOD.get(), ModBlocks.STRIPPED_SKY_LOG.get(), ModBlocks.STRIPPED_SKY_WOOD.get(), ModBlocks.SKY_STAIRS.get(), ModBlocks.SKY_SLAB.get(), ModBlocks.SKY_BUTTON.get(), ModBlocks.SKY_PRESSURE_PLATE.get(), ModBlocks.SKY_FENCE_GATE.get(), ModBlocks.SKY_FENCE.get(), ModBlocks.SKY_DOOR.get(), ModBlocks.SKY_TRAPDOOR.get(),ModBlocks.SKY_SIGN.get());
        boatRecipes(pWriter, ModBlocks.MYSTIC_PLANKS.get(), ModItems.MYSTIC_BOAT.get(), ModItems.MYSTIC_CHEST_BOAT.get());
        boatRecipes(pWriter, ModBlocks.PALM_PLANKS.get(), ModItems.PALM_BOAT.get(), ModItems.PALM_CHEST_BOAT.get());
        boatRecipes(pWriter, ModBlocks.SKY_PLANKS.get(), ModItems.SKY_BOAT.get(), ModItems.SKY_CHEST_BOAT.get());
        boatRecipes(pWriter, ModBlocks.GILDED_SKY_PLANKS.get(), ModItems.GILDED_SKY_BOAT.get(), ModItems.GILDED_SKY_CHEST_BOAT.get());
        brick(pWriter, ModBlocks.DENSE_CLOUD.get(), ModBlocks.DENSE_CLOUD_BRICK.get());
        stair(pWriter, ModBlocks.DENSE_CLOUD_BRICK.get(), ModBlocks.DENSE_CLOUD_BRICK_STAIRS.get());
        slab(pWriter, ModBlocks.DENSE_CLOUD_BRICK.get(), ModBlocks.DENSE_CLOUD_BRICK_SLAB.get());
        blackSandRecipes(pWriter);
        gildRecipes(pWriter,
                List.of(
                        ModBlocks.SKY_PLANKS.get(),
                        ModBlocks.SKY_LOG.get(),
                        ModBlocks.SKY_WOOD.get(),
                        ModBlocks.SKY_STAIRS.get(),
                        ModBlocks.SKY_SLAB.get(),
                        ModBlocks.SKY_BUTTON.get(),
                        ModBlocks.SKY_PRESSURE_PLATE.get(),
                        ModBlocks.SKY_FENCE_GATE.get(),
                        ModBlocks.SKY_FENCE.get(),
                        ModBlocks.SKY_DOOR.get(),
                        ModBlocks.SKY_TRAPDOOR.get(),
                        ModBlocks.SKY_SIGN.get()
                ), List.of(
                        ModBlocks.GILDED_SKY_PLANKS.get(),
                        ModBlocks.GILDED_SKY_LOG.get(),
                        ModBlocks.GILDED_SKY_WOOD.get(),
                        ModBlocks.GILDED_SKY_STAIRS.get(),
                        ModBlocks.GILDED_SKY_SLAB.get(),
                        ModBlocks.GILDED_SKY_BUTTON.get(),
                        ModBlocks.GILDED_SKY_PRESSURE_PLATE.get(),
                        ModBlocks.GILDED_SKY_FENCE_GATE.get(),
                        ModBlocks.GILDED_SKY_FENCE.get(),
                        ModBlocks.GILDED_SKY_DOOR.get(),
                        ModBlocks.GILDED_SKY_TRAPDOOR.get(),
                        ModBlocks.GILDED_SKY_SIGN.get()
                ));
    }

    protected void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, Boolean createSmelting) {
        modOreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, ingredients, category, result, experience, cookingTime, group, "_from_blasting");
        if (createSmelting) {
            modOreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, ingredients, category, result, experience, cookingTime * 2, group, "_from_smelting");
        }
    }

    protected void foodCooking(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, Boolean campfireAndSmoker) {
        modOreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, ingredients, category, result, experience, cookingTime, group, "_from_cooking");
        if (campfireAndSmoker) {
            campfireCooking(recipeOutput, "campfire_cooking", 600, ingredients, result, experience);
            smokingCooking(recipeOutput, "smoking", 100, ingredients, result, experience);
        }
    }

    // Named modOreCooking (not oreCooking) because vanilla RecipeProvider already declares an instance oreCooking with a similar signature - redeclaring it would be a "cannot hide"/override clash.
    //
    // SimpleCookingRecipeBuilder.generic() gained a trailing AbstractCookingRecipe.Factory<T> arg as of
    // 1.20.4 (needed to actually construct the T for the codec-based recipe system) - rather than thread
    // a Factory through every call site, dispatch to the two loader-agnostic convenience methods
    // (.blasting()/.smelting()) that already bind the right serializer+factory pair, matching this
    // method's only two real callers below.
    protected void modOreCooking(RecipeOutput recipeOutput, RecipeSerializer<? extends AbstractCookingRecipe> recipeSerializer, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String recipeSuffix) {
        for (ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder builder = recipeSerializer == RecipeSerializer.BLASTING_RECIPE
                    ? SimpleCookingRecipeBuilder.blasting(Ingredient.of(itemlike), category, result, experience, cookingTime)
                    : SimpleCookingRecipeBuilder.smelting(Ingredient.of(itemlike), category, result, experience, cookingTime);
            builder.group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(result) + recipeSuffix + "_" + getItemName(itemlike));
        }
    }

    // The save id must carry the "extrabiomes:" namespace explicitly - an unqualified id is parsed as "minecraft:<id>" by RecipeBuilder.save(Consumer, String), silently writing recipes under data/minecraft/recipes/ instead.
    private void campfireCooking(RecipeOutput recipeOutput, String cookingType, int cookingTime, List<ItemLike> ingredient, ItemLike output, float experience) {
        for (ItemLike itemlike : ingredient) {
            SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(itemlike), RecipeCategory.FOOD, output, experience, cookingTime).unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + cookingType);
        }
    }

    private void smokingCooking(RecipeOutput recipeOutput, String cookingType, int cookingTime, List<ItemLike> ingredient, ItemLike output, float experience) {
        for (ItemLike itemlike : ingredient) {
            SimpleCookingRecipeBuilder.smoking(Ingredient.of(itemlike), RecipeCategory.FOOD, output, experience, cookingTime).unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + cookingType);
        }
    }

    private void gildRecipes(RecipeOutput recipeOutput, List<Block> nonGilded, List<Block> gilded) {
        int recipeNum = 0;
        for (Block currentNonGilded : nonGilded) {
            gild(recipeOutput, currentNonGilded, gilded.get(recipeNum++));
        }
    }

    private void woodRecipes(RecipeOutput recipeOutput, Block plank, Block log, Block wood, Block strippedLog, Block strippedWood, Block stairs, Block slab, Block button, Block pressurePlate, Block fenceGate, Block fence, Block door, Block trapDoor, Block sign) {
        List<ItemLike> woods = List.of(log, strippedLog, wood, strippedWood);
        planks(recipeOutput, woods, plank);
        wood(recipeOutput, log, wood);
        wood(recipeOutput, strippedLog, strippedWood);
        stair(recipeOutput, plank, stairs);
        slab(recipeOutput, plank, slab);
        fence(recipeOutput, plank, fence);
        fenceGate(recipeOutput, plank, fenceGate);
        door(recipeOutput, plank, door);
        trapDoor(recipeOutput, plank, trapDoor);
        pressurePlate(recipeOutput, plank, pressurePlate);
        sign(recipeOutput,plank, sign);
        oneToOne(recipeOutput, plank, button);

    }

    private void planks(RecipeOutput recipeOutput, List<ItemLike> ingredients, Block output) {
        for (ItemLike itemlike : ingredients) {
            shapeless(RecipeCategory.MISC, output, 4)
                    .requires(itemlike)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(itemlike));
        }
    }

    private void wood(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output, 3)
                .pattern("&&")
                .pattern("&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private void stair(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output, 4)
                .pattern("&  ")
                .pattern("&& ")
                .pattern("&&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private void slab(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output, 6)
                .pattern("&&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private void fence(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output, 3)
                .pattern("&$&")
                .pattern("&$&")
                .define('&', ingredient)
                .define('$', Items.STICK)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private void fenceGate(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output, 3)
                .pattern("$&$")
                .pattern("$&$")
                .define('&', ingredient)
                .define('$', Items.STICK)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private void door(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output, 3)
                .pattern("&&")
                .pattern("&&")
                .pattern("&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private void trapDoor(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output, 2)
                .pattern("&&&")
                .pattern("&&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private void pressurePlate(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output, 2)
                .pattern("&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }
    private void sign(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output, 3)
                .pattern("&&&")
                .pattern("&&&")
                .pattern(" $ ")
                .define('&', ingredient)
                .define('$', Items.STICK)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    // Matches vanilla's own oak_boat/oak_chest_boat recipes: 5 planks shaped into a boat, then a chest onto a boat shapeless.
    private void boatRecipes(RecipeOutput recipeOutput, Block plank, Item boat, Item chestBoat) {
        shaped(RecipeCategory.MISC, boat)
                .pattern("& &")
                .pattern("&&&")
                .define('&', plank)
                .group("boat")
                .unlockedBy(getHasName(plank), has(plank))
                .save(recipeOutput);
        shapeless(RecipeCategory.MISC, chestBoat)
                .requires(Items.CHEST)
                .requires(boat)
                .group("chest_boat")
                .unlockedBy(getHasName(boat), has(boat))
                .save(recipeOutput);
    }

    private void oneToOne(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shapeless(RecipeCategory.MISC, output, 1)
                .requires(ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private void brick(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output)
                .pattern("&&")
                .pattern("&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput);
    }

    private void blackSandRecipes(RecipeOutput recipeOutput) {
        // Not from Bedrock (no equivalent recipe there) - added per playtest request, using vanilla's own "8 around a dye" bulk-dyeing shape.
        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLACK_SAND.get(), 8)
                .pattern("###")
                .pattern("#$#")
                .pattern("###")
                .define('#', Items.SAND)
                .define('$', Items.BLACK_DYE)
                .unlockedBy(getHasName(Items.SAND), has(Items.SAND))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":black_sand_from_dye");

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.BLACK_SAND.get()), RecipeCategory.MISC, Items.GLASS, 0.1F, 200)
                .unlockedBy(getHasName(ModBlocks.BLACK_SAND.get()), has(ModBlocks.BLACK_SAND.get()))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":glass_from_black_sand");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.BLACK_SANDSTONE.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.SMOOTH_BLACK_SANDSTONE.get(), 0.1F, 200)
                .unlockedBy(getHasName(ModBlocks.BLACK_SANDSTONE.get()), has(ModBlocks.BLACK_SANDSTONE.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLACK_SANDSTONE.get())
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.BLACK_SAND.get())
                .unlockedBy(getHasName(ModBlocks.BLACK_SAND.get()), has(ModBlocks.BLACK_SAND.get()))
                .save(recipeOutput);
        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_BLACK_SANDSTONE.get(), 4)
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.BLACK_SANDSTONE.get())
                .unlockedBy(getHasName(ModBlocks.BLACK_SANDSTONE.get()), has(ModBlocks.BLACK_SANDSTONE.get()))
                .save(recipeOutput);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.CHISELED_BLACK_SANDSTONE.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.BLACK_SANDSTONE_SLAB.get())
                .unlockedBy(getHasName(ModBlocks.BLACK_SANDSTONE_SLAB.get()), has(ModBlocks.BLACK_SANDSTONE_SLAB.get()))
                .save(recipeOutput);
        shaped(RecipeCategory.COMBAT, Items.TNT)
                .pattern("#~#")
                .pattern("~#~")
                .pattern("#~#")
                .define('#', ModBlocks.BLACK_SAND.get())
                .define('~', Items.GUNPOWDER)
                .unlockedBy(getHasName(ModBlocks.BLACK_SAND.get()), has(ModBlocks.BLACK_SAND.get()))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":tnt_from_black_sand");

        slab(recipeOutput, ModBlocks.BLACK_SANDSTONE.get(), ModBlocks.BLACK_SANDSTONE_SLAB.get());
        slab(recipeOutput, ModBlocks.CUT_BLACK_SANDSTONE.get(), ModBlocks.CUT_BLACK_SANDSTONE_SLAB.get());
        slab(recipeOutput, ModBlocks.SMOOTH_BLACK_SANDSTONE.get(), ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB.get());
        stair(recipeOutput, ModBlocks.BLACK_SANDSTONE.get(), ModBlocks.BLACK_SANDSTONE_STAIRS.get());
        stair(recipeOutput, ModBlocks.SMOOTH_BLACK_SANDSTONE.get(), ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS.get());
        wall(recipeOutput, ModBlocks.BLACK_SANDSTONE.get(), ModBlocks.BLACK_SANDSTONE_WALL.get());

        stonecutting(recipeOutput, ModBlocks.BLACK_SANDSTONE.get(), ModBlocks.CHISELED_BLACK_SANDSTONE.get(), 1);
        stonecutting(recipeOutput, ModBlocks.BLACK_SANDSTONE.get(), ModBlocks.CUT_BLACK_SANDSTONE.get(), 1);
        stonecutting(recipeOutput, ModBlocks.BLACK_SANDSTONE.get(), ModBlocks.BLACK_SANDSTONE_SLAB.get(), 2);
        stonecutting(recipeOutput, ModBlocks.BLACK_SANDSTONE.get(), ModBlocks.BLACK_SANDSTONE_STAIRS.get(), 1);
        stonecutting(recipeOutput, ModBlocks.BLACK_SANDSTONE.get(), ModBlocks.BLACK_SANDSTONE_WALL.get(), 1);
        stonecutting(recipeOutput, ModBlocks.CUT_BLACK_SANDSTONE.get(), ModBlocks.CUT_BLACK_SANDSTONE_SLAB.get(), 2);
        stonecutting(recipeOutput, ModBlocks.SMOOTH_BLACK_SANDSTONE.get(), ModBlocks.SMOOTH_BLACK_SANDSTONE_SLAB.get(), 2);
        stonecutting(recipeOutput, ModBlocks.SMOOTH_BLACK_SANDSTONE.get(), ModBlocks.SMOOTH_BLACK_SANDSTONE_STAIRS.get(), 1);
    }

    private void wall(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.DECORATIONS, output, 6)
                .pattern("&&&")
                .pattern("&&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private void stonecutting(RecipeOutput recipeOutput, Block ingredient, Block output, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, output, count)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient) + "_stonecutting");
    }

    // Ported from ExtraBiomes - Bedrock/packs/BP/recipes/pebbles/*.json.
    private void pebbleRecipes(RecipeOutput recipeOutput) {
        shapeless(RecipeCategory.MISC, ModItems.PEBBLE.get(), 4)
                .requires(Items.COBBLESTONE)
                .unlockedBy(getHasName(Items.COBBLESTONE), has(Items.COBBLESTONE))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":pebble_from_cobble");
        shaped(RecipeCategory.BUILDING_BLOCKS, Items.COBBLESTONE)
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.PEBBLE.get())
                .unlockedBy(getHasName(ModItems.PEBBLE.get()), has(ModItems.PEBBLE.get()))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":cobble_from_pebble");
        shapeless(RecipeCategory.MISC, ModItems.MOSSY_PEBBLE.get(), 4)
                .requires(Items.MOSSY_COBBLESTONE)
                .unlockedBy(getHasName(Items.MOSSY_COBBLESTONE), has(Items.MOSSY_COBBLESTONE))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":mossy_pebble_from_cobble");
        shaped(RecipeCategory.BUILDING_BLOCKS, Items.MOSSY_COBBLESTONE)
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.MOSSY_PEBBLE.get())
                .unlockedBy(getHasName(ModItems.MOSSY_PEBBLE.get()), has(ModItems.MOSSY_PEBBLE.get()))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":mossy_cobble_from_pebble");
    }

    // Ported from ExtraBiomes - Bedrock/packs/BP/recipes/diamond_razor_feather.json and diamond_razor_feather_to_netherite.json.
    private void razorFeatherRecipes(RecipeOutput recipeOutput) {
        shaped(RecipeCategory.COMBAT, ModItems.DIAMOND_RAZOR_FEATHER.get(), 3)
                .pattern("#+")
                .pattern("##")
                .define('#', ModItems.RAZOR_FEATHER.get())
                .define('+', Items.DIAMOND)
                .unlockedBy(getHasName(ModItems.RAZOR_FEATHER.get()), has(ModItems.RAZOR_FEATHER.get()))
                .save(recipeOutput);
        shaped(RecipeCategory.COMBAT, ModItems.NETHERITE_RAZOR_FEATHER.get(), 8)
                .pattern("##+")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.DIAMOND_RAZOR_FEATHER.get())
                .define('+', Items.NETHERITE_INGOT)
                .unlockedBy(getHasName(ModItems.DIAMOND_RAZOR_FEATHER.get()), has(ModItems.DIAMOND_RAZOR_FEATHER.get()))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":diamond_razor_feather_to_netherite");
    }

    // Ported from ExtraBiomes - Bedrock/packs/BP/recipes/stick_pile_from_stick.json and stick_from_stick_pile.json.
    private void stickPileRecipes(RecipeOutput recipeOutput) {
        shaped(RecipeCategory.DECORATIONS, ModBlocks.STICK_PILE.get())
                .pattern("~~~")
                .pattern("~~~")
                .pattern("~~~")
                .define('~', Items.STICK)
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":stick_pile_from_stick");
        shapeless(RecipeCategory.MISC, Items.STICK, 9)
                .requires(ModBlocks.STICK_PILE.get())
                .unlockedBy(getHasName(ModBlocks.STICK_PILE.get()), has(ModBlocks.STICK_PILE.get()))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":stick_from_stick_pile");
    }

    private void gild(RecipeOutput recipeOutput, Block ingredient, Block output) {
        shaped(RecipeCategory.MISC, output)
                .pattern(" * ")
                .pattern("*^*")
                .pattern(" * ")
                .define('^', ingredient)
                .define('*', Items.GOLD_NUGGET)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .unlockedBy(getHasName(Items.GOLD_NUGGET), has(Items.GOLD_NUGGET))
                .save(recipeOutput);
    }

}
