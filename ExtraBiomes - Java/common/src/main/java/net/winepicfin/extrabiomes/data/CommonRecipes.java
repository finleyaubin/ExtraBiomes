package net.winepicfin.extrabiomes.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
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
import java.util.function.Consumer;

/**
 * Shared, loader-agnostic recipe content for the Forge and Fabric {@code ModRecipeProvider}s.
 * <p>
 * This extends vanilla {@link RecipeProvider} (rather than being a plain standalone class) purely so
 * its static methods can call the {@code protected static} {@code has(...)} and {@code getHasName(...)}
 * helpers declared on {@link RecipeProvider}. Reusing those vanilla helpers - instead of reimplementing
 * them - guarantees the generated advancement criteria stay byte-identical to the previous per-loader
 * copies. The class is never instantiated for real; its constructor exists only to satisfy
 * {@code RecipeProvider}'s constructor requirement.
 *
 * <p>Lives in {@code ...extrabiomes.data} rather than the more obvious {@code ...extrabiomes.datagen}
 * because forge/ already owns that package name. Two modules exporting the same package is a JPMS
 * split package, which Forge's ModLauncher rejects at boot with
 * "Modules ... and extrabiomes export package net.winepicfin.extrabiomes.datagen" - it compiles
 * fine and only fails when a run task actually starts, so keep this package distinct from every
 * package under forge/ and fabric/.
 */
public abstract class CommonRecipes extends RecipeProvider {

    private CommonRecipes(PackOutput packOutput) {
        super(packOutput);
    }

    public static final List<ItemLike> DIAMOND_SMELTABLES = List.of(ModBlocks.NETHER_DIAMOND_ORE.get());
    public static final List<ItemLike> FROG_SMELTABLES = List.of(ModItems.FROGS_LEGS.get());
    public static final List<ItemLike> PIRANHA_SMELTABLES = List.of(ModItems.PIRANHA.get());

    public static void build(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BAIT.get())
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

    protected static void oreBlasting(Consumer<FinishedRecipe> recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, Boolean createSmelting) {
        modOreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, ingredients, category, result, experience, cookingTime, group, "_from_blasting");
        if (createSmelting) {
            modOreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, ingredients, category, result, experience, cookingTime * 2, group, "_from_smelting");
        }
    }

    protected static void foodCooking(Consumer<FinishedRecipe> recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, Boolean campfireAndSmoker) {
        modOreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, ingredients, category, result, experience, cookingTime, group, "_from_cooking");
        if (campfireAndSmoker) {
            campfireCooking(recipeOutput, "campfire_cooking", 600, ingredients, result, experience);
            smokingCooking(recipeOutput, "smoking", 100, ingredients, result, experience);
        }
    }

    // Named modOreCooking (not oreCooking) because vanilla RecipeProvider already declares a static
    // oreCooking with this exact signature - redeclaring it here would be a "cannot hide" compile error.
    protected static void modOreCooking(Consumer<FinishedRecipe> recipeOutput, RecipeSerializer<? extends AbstractCookingRecipe> recipeSerializer, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String recipeSuffix) {
        for (ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, recipeSerializer).group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(result) + recipeSuffix + "_" + getItemName(itemlike));
        }
    }

    // NOTE: the save id below must carry the "extrabiomes:" namespace explicitly - an unqualified
    // id string is parsed as "minecraft:<id>" by RecipeBuilder.save(Consumer, String), which
    // silently wrote every campfire/smoking recipe (frogs legs included, not just the new piranha
    // ones) under data/minecraft/recipes/ instead of data/extrabiomes/recipes/. Still functionally
    // loaded either way (recipe ids don't have to match their content's namespace), but wrong.
    private static void campfireCooking(Consumer<FinishedRecipe> recipeOutput, String cookingType, int cookingTime, List<ItemLike> ingredient, ItemLike output, float experience) {
        for (ItemLike itemlike : ingredient) {
            SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(itemlike), RecipeCategory.FOOD, output, experience, cookingTime).unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + cookingType);
        }
    }

    private static void smokingCooking(Consumer<FinishedRecipe> recipeOutput, String cookingType, int cookingTime, List<ItemLike> ingredient, ItemLike output, float experience) {
        for (ItemLike itemlike : ingredient) {
            SimpleCookingRecipeBuilder.smoking(Ingredient.of(itemlike), RecipeCategory.FOOD, output, experience, cookingTime).unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + cookingType);
        }
    }

    private static void gildRecipes(Consumer<FinishedRecipe> recipeOutput, List<Block> nonGilded, List<Block> gilded) {
        int recipeNum = 0;
        for (Block currentNonGilded : nonGilded) {
            gild(recipeOutput, currentNonGilded, gilded.get(recipeNum++));
        }
    }

    private static void woodRecipes(Consumer<FinishedRecipe> recipeOutput, Block plank, Block log, Block wood, Block strippedLog, Block strippedWood, Block stairs, Block slab, Block button, Block pressurePlate, Block fenceGate, Block fence, Block door, Block trapDoor, Block sign) {
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

    private static void planks(Consumer<FinishedRecipe> recipeOutput, List<ItemLike> ingredients, Block output) {
        for (ItemLike itemlike : ingredients) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, 4)
                    .requires(itemlike)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(itemlike));
        }
    }

    private static void wood(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 3)
                .pattern("&&")
                .pattern("&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private static void stair(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 4)
                .pattern("&  ")
                .pattern("&& ")
                .pattern("&&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private static void slab(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 6)
                .pattern("&&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private static void fence(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 3)
                .pattern("&$&")
                .pattern("&$&")
                .define('&', ingredient)
                .define('$', Items.STICK)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private static void fenceGate(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 3)
                .pattern("$&$")
                .pattern("$&$")
                .define('&', ingredient)
                .define('$', Items.STICK)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private static void door(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 3)
                .pattern("&&")
                .pattern("&&")
                .pattern("&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private static void trapDoor(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 2)
                .pattern("&&&")
                .pattern("&&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private static void pressurePlate(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 2)
                .pattern("&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }
    private static void sign(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 3)
                .pattern("&&&")
                .pattern("&&&")
                .pattern(" $ ")
                .define('&', ingredient)
                .define('$', Items.STICK)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private static void oneToOne(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, 1)
                .requires(ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private static void brick(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output)
                .pattern("&&")
                .pattern("&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput);
    }

    private static void blackSandRecipes(Consumer<FinishedRecipe> recipeOutput) {
        // Not from Bedrock (no equivalent recipe there) - added per playtest request: dye a batch of
        // sand black, same "8 around a dye" bulk-dyeing shape vanilla itself uses (e.g. wool/concrete
        // powder), 8 sand in -> 8 black sand out.
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLACK_SAND.get(), 8)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLACK_SANDSTONE.get())
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.BLACK_SAND.get())
                .unlockedBy(getHasName(ModBlocks.BLACK_SAND.get()), has(ModBlocks.BLACK_SAND.get()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_BLACK_SANDSTONE.get(), 4)
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.BLACK_SANDSTONE.get())
                .unlockedBy(getHasName(ModBlocks.BLACK_SANDSTONE.get()), has(ModBlocks.BLACK_SANDSTONE.get()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CHISELED_BLACK_SANDSTONE.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.BLACK_SANDSTONE_SLAB.get())
                .unlockedBy(getHasName(ModBlocks.BLACK_SANDSTONE_SLAB.get()), has(ModBlocks.BLACK_SANDSTONE_SLAB.get()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.TNT)
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

    private static void wall(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, output, 6)
                .pattern("&&&")
                .pattern("&&&")
                .define('&', ingredient)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient));
    }

    private static void stonecutting(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, output, count)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(output) + "_from_" + getItemName(ingredient) + "_stonecutting");
    }

    // Ported from ExtraBiomes - Bedrock/packs/BP/recipes/pebbles/*.json.
    private static void pebbleRecipes(Consumer<FinishedRecipe> recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PEBBLE.get(), 4)
                .requires(Items.COBBLESTONE)
                .unlockedBy(getHasName(Items.COBBLESTONE), has(Items.COBBLESTONE))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":pebble_from_cobble");
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.COBBLESTONE)
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.PEBBLE.get())
                .unlockedBy(getHasName(ModItems.PEBBLE.get()), has(ModItems.PEBBLE.get()))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":cobble_from_pebble");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MOSSY_PEBBLE.get(), 4)
                .requires(Items.MOSSY_COBBLESTONE)
                .unlockedBy(getHasName(Items.MOSSY_COBBLESTONE), has(Items.MOSSY_COBBLESTONE))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":mossy_pebble_from_cobble");
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.MOSSY_COBBLESTONE)
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.MOSSY_PEBBLE.get())
                .unlockedBy(getHasName(ModItems.MOSSY_PEBBLE.get()), has(ModItems.MOSSY_PEBBLE.get()))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":mossy_cobble_from_pebble");
    }

    // Ported from ExtraBiomes - Bedrock/packs/BP/recipes/diamond_razor_feather.json and
    // diamond_razor_feather_to_netherite.json.
    private static void razorFeatherRecipes(Consumer<FinishedRecipe> recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.DIAMOND_RAZOR_FEATHER.get(), 3)
                .pattern("#+")
                .pattern("##")
                .define('#', ModItems.RAZOR_FEATHER.get())
                .define('+', Items.DIAMOND)
                .unlockedBy(getHasName(ModItems.RAZOR_FEATHER.get()), has(ModItems.RAZOR_FEATHER.get()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.NETHERITE_RAZOR_FEATHER.get(), 8)
                .pattern("##+")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.DIAMOND_RAZOR_FEATHER.get())
                .define('+', Items.NETHERITE_INGOT)
                .unlockedBy(getHasName(ModItems.DIAMOND_RAZOR_FEATHER.get()), has(ModItems.DIAMOND_RAZOR_FEATHER.get()))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":diamond_razor_feather_to_netherite");
    }

    // Ported from ExtraBiomes - Bedrock/packs/BP/recipes/stick_pile_from_stick.json and
    // stick_from_stick_pile.json.
    private static void stickPileRecipes(Consumer<FinishedRecipe> recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.STICK_PILE.get())
                .pattern("~~~")
                .pattern("~~~")
                .pattern("~~~")
                .define('~', Items.STICK)
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":stick_pile_from_stick");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.STICK, 9)
                .requires(ModBlocks.STICK_PILE.get())
                .unlockedBy(getHasName(ModBlocks.STICK_PILE.get()), has(ModBlocks.STICK_PILE.get()))
                .save(recipeOutput, ExtraBiomes.MOD_ID + ":stick_from_stick_pile");
    }

    private static void gild(Consumer<FinishedRecipe> recipeOutput, Block ingredient, Block output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output)
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
