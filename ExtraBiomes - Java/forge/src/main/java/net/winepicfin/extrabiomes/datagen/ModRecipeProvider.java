
package net.winepicfin.extrabiomes.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BAIT.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.WORM.get())
                .unlockedBy(getHasName(ModItems.WORM.get()), has(ModItems.WORM.get()))
                .save(pWriter);
        oreBlasting(pWriter, DIAMOND_SMELTABLES, RecipeCategory.MISC, Items.DIAMOND, 0.25f, 100, "diamond", Boolean.TRUE);
        foodCooking(pWriter, FROG_SMELTABLES, RecipeCategory.MISC, ModItems.COOKED_FROGS_LEGS.get(), 0.25f, 100, "cooked_frogs_legs", Boolean.TRUE);
        woodRecipes(pWriter, ModBlocks.MYSTIC_PLANKS.get(), ModBlocks.MYSTIC_LOG.get(), ModBlocks.MYSTIC_WOOD.get(), ModBlocks.STRIPED_MYSTIC_LOG.get(), ModBlocks.STRIPED_MYSTIC_WOOD.get(), ModBlocks.MYSTIC_STAIRS.get(), ModBlocks.MYSTIC_SLAB.get(), ModBlocks.MYSTIC_BUTTON.get(), ModBlocks.MYSTIC_PRESSURE_PLATE.get(), ModBlocks.MYSTIC_FENCE_GATE.get(), ModBlocks.MYSTIC_FENCE.get(), ModBlocks.MYSTIC_DOOR.get(), ModBlocks.MYSTIC_TRAPDOOR.get(),ModBlocks.MYSTIC_SIGN.get());
        woodRecipes(pWriter, ModBlocks.PALM_PLANKS.get(), ModBlocks.PALM_LOG.get(), ModBlocks.PALM_WOOD.get(), ModBlocks.STRIPED_PALM_LOG.get(), ModBlocks.STRIPED_PALM_WOOD.get(), ModBlocks.PALM_STAIRS.get(), ModBlocks.PALM_SLAB.get(), ModBlocks.PALM_BUTTON.get(), ModBlocks.PALM_PRESSURE_PLATE.get(), ModBlocks.PALM_FENCE_GATE.get(), ModBlocks.PALM_FENCE.get(), ModBlocks.PALM_DOOR.get(), ModBlocks.PALM_TRAPDOOR.get(), ModBlocks.PALM_SIGN.get());
        woodRecipes(pWriter, ModBlocks.SKY_PLANKS.get(), ModBlocks.SKY_LOG.get(), ModBlocks.SKY_WOOD.get(), ModBlocks.STRIPED_SKY_LOG.get(), ModBlocks.STRIPED_SKY_WOOD.get(), ModBlocks.SKY_STAIRS.get(), ModBlocks.SKY_SLAB.get(), ModBlocks.SKY_BUTTON.get(), ModBlocks.SKY_PRESSURE_PLATE.get(), ModBlocks.SKY_FENCE_GATE.get(), ModBlocks.SKY_FENCE.get(), ModBlocks.SKY_DOOR.get(), ModBlocks.SKY_TRAPDOOR.get(),ModBlocks.SKY_SIGN.get());
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

    public static final List<ItemLike> DIAMOND_SMELTABLES = List.of(ModBlocks.NETHER_DIAMOND_ORE.get());
    public static final List<ItemLike> FROG_SMELTABLES = List.of(ModItems.FROGS_LEGS.get());


    protected static void oreBlasting(Consumer<FinishedRecipe> recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, Boolean createSmelting) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, ingredients, category, result, experience, cookingTime, group, "_from_blasting");
        if (createSmelting) {
            oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, ingredients, category, result, experience, cookingTime * 2, group, "_from_smelting");
        }
    }

    protected static void foodCooking(Consumer<FinishedRecipe> recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, Boolean campfireAndSmoker) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, ingredients, category, result, experience, cookingTime, group, "_from_cooking");
        if (campfireAndSmoker) {
            campfireCooking(recipeOutput, "campfire_cooking", 600, ingredients, result, experience);
            smokingCooking(recipeOutput, "smoking", 100, ingredients, result, experience);
        }
    }

    protected static void oreCooking(Consumer<FinishedRecipe> recipeOutput, RecipeSerializer<? extends AbstractCookingRecipe> recipeSerializer, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String recipeSuffix) {
        for (ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, recipeSerializer).group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, ExtraBiomes.MOD_ID + ":" + getItemName(result) + recipeSuffix + "_" + getItemName(itemlike));
        }
    }

    private static void campfireCooking(Consumer<FinishedRecipe> recipeOutput, String cookingType, int cookingTime, List<ItemLike> ingredient, ItemLike output, float experience) {
        for (ItemLike itemlike : ingredient) {
            SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(itemlike), RecipeCategory.FOOD, output, experience, cookingTime).unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput, getItemName(output) + "_from_" + cookingType);
        }
    }

    private static void smokingCooking(Consumer<FinishedRecipe> recipeOutput, String cookingType, int cookingTime, List<ItemLike> ingredient, ItemLike output, float experience) {
        for (ItemLike itemlike : ingredient) {
            SimpleCookingRecipeBuilder.smoking(Ingredient.of(itemlike), RecipeCategory.FOOD, output, experience, cookingTime).unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput, getItemName(output) + "_from_" + cookingType);
        }
    }

    private static void gildRecipes(Consumer<FinishedRecipe> recipeOutput, List<Block> nonGilded, List<Block> gilded) {
        int recipeNum = 0;
        for (Block currentNonGilded : nonGilded) {
            gild(recipeOutput, currentNonGilded, gilded.get(recipeNum++));
        }
    }

    private static void woodRecipes(Consumer<FinishedRecipe> recipeOutput, Block plank, Block log, Block wood, Block strippedLog, Block stripedWood, Block stairs, Block slab, Block button, Block pressurePlate, Block fenceGate, Block fence, Block door, Block trapDoor, Block sign) {
        List<ItemLike> woods = List.of(log, strippedLog, wood, stripedWood);
        planks(recipeOutput, woods, plank);
        wood(recipeOutput, log, wood);
        wood(recipeOutput, strippedLog, stripedWood);
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

