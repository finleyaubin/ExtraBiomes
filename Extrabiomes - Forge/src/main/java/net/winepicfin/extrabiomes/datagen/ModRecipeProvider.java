
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        oreBlasting(pWriter, DIAMOND_SMELTABLES, RecipeCategory.MISC, Items.DIAMOND, 0.25f, 100, "diamond", Boolean.TRUE);
        foodCooking(pWriter, FROG_SMELTABLES, RecipeCategory.MISC, ModItems.COOKED_FROGS_LEGS.get(), 0.25f, 100, "cooked_frogs_legs", Boolean.TRUE);
        woodRecipes(pWriter, ModBlocks.MYSTIC_PLANKS.get(), ModBlocks.MYSTIC_LOG.get(), ModBlocks.MYSTIC_WOOD.get(), ModBlocks.STRIPED_MYSTIC_LOG.get(), ModBlocks.STRIPED_MYSTIC_WOOD.get(), ModBlocks.MYSTIC_STAIRS.get(), ModBlocks.MYSTIC_SLAB.get(), ModBlocks.MYSTIC_BUTTON.get(), ModBlocks.MYSTIC_PRESSURE_PLATE.get(), ModBlocks.MYSTIC_FENCE_GATE.get(), ModBlocks.MYSTIC_FENCE.get(), ModBlocks.MYSTIC_DOOR.get(), ModBlocks.MYSTIC_TRAPDOOR.get());
        woodRecipes(pWriter, ModBlocks.PALM_PLANKS.get(), ModBlocks.PALM_LOG.get(), ModBlocks.PALM_WOOD.get(), ModBlocks.STRIPED_PALM_LOG.get(), ModBlocks.STRIPED_PALM_WOOD.get(), ModBlocks.PALM_STAIRS.get(), ModBlocks.PALM_SLAB.get(), ModBlocks.PALM_BUTTON.get(), ModBlocks.PALM_PRESSURE_PLATE.get(), ModBlocks.PALM_FENCE_GATE.get(), ModBlocks.PALM_FENCE.get(), ModBlocks.PALM_DOOR.get(), ModBlocks.PALM_TRAPDOOR.get());
        woodRecipes(pWriter, ModBlocks.SKY_PLANKS.get(), ModBlocks.SKY_LOG.get(), ModBlocks.SKY_WOOD.get(), ModBlocks.STRIPED_SKY_LOG.get(), ModBlocks.STRIPED_SKY_WOOD.get(), ModBlocks.SKY_STAIRS.get(), ModBlocks.SKY_SLAB.get(), ModBlocks.SKY_BUTTON.get(), ModBlocks.SKY_PRESSURE_PLATE.get(), ModBlocks.SKY_FENCE_GATE.get(), ModBlocks.SKY_FENCE.get(), ModBlocks.SKY_DOOR.get(), ModBlocks.SKY_TRAPDOOR.get());
        brick(pWriter, ModBlocks.DENSE_CLOUD.get(), ModBlocks.DENSE_CLOUD_BRICK.get());
        gildRecipes(pWriter,
                new ArrayList<>() {{
                        add(ModBlocks.SKY_PLANKS.get());
                        add(ModBlocks.SKY_LOG.get());
                        add(ModBlocks.SKY_WOOD.get());
                        add(ModBlocks.SKY_STAIRS.get());
                        add(ModBlocks.SKY_SLAB.get());
                        add(ModBlocks.SKY_BUTTON.get());
                        add(ModBlocks.SKY_PRESSURE_PLATE.get());
                        add(ModBlocks.SKY_FENCE_GATE.get());
                        add(ModBlocks.SKY_FENCE.get());
                        add(ModBlocks.SKY_DOOR.get());
                        add(ModBlocks.SKY_TRAPDOOR.get());
                    }
                }, new ArrayList<>() {{
                    add(ModBlocks.GILDED_SKY_PLANKS.get());
                    add(ModBlocks.GILDED_SKY_LOG.get());
                    add(ModBlocks.GILDED_SKY_WOOD.get());
                    add(ModBlocks.GILDED_SKY_STAIRS.get());
                    add(ModBlocks.GILDED_SKY_SLAB.get());
                    add(ModBlocks.GILDED_SKY_BUTTON.get());
                    add(ModBlocks.GILDED_SKY_PRESSURE_PLATE.get());
                    add(ModBlocks.GILDED_SKY_FENCE_GATE.get());
                    add(ModBlocks.GILDED_SKY_FENCE.get());
                    add(ModBlocks.GILDED_SKY_DOOR.get());
                    add(ModBlocks.GILDED_SKY_TRAPDOOR.get());
                }});
    }

    public static final List<ItemLike> DIAMOND_SMELTABLES = List.of(ModBlocks.NETHER_DIAMOND_ORE.get());
    public static final List<ItemLike> FROG_SMELTABLES = List.of(ModItems.FROGS_LEGS.get());


    protected static void oreSmelting(Consumer<FinishedRecipe> p_250654_, List<ItemLike> p_250172_, RecipeCategory p_250588_, ItemLike p_251868_, float p_250789_, int p_252144_, String p_251687_) {
        oreCooking(p_250654_, RecipeSerializer.SMELTING_RECIPE, p_250172_, p_250588_, p_251868_, p_250789_, p_252144_, p_251687_, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> p_248775_, List<ItemLike> p_251504_, RecipeCategory p_248846_, ItemLike p_249735_, float p_248783_, int p_250303_, String p_251984_, Boolean createSmelting) {
        oreCooking(p_248775_, RecipeSerializer.BLASTING_RECIPE, p_251504_, p_248846_, p_249735_, p_248783_, p_250303_, p_251984_, "_from_blasting");
        if (createSmelting) {
            oreSmelting(p_248775_, p_251504_, p_248846_, p_249735_, p_248783_, p_250303_ * 2, p_251984_);
        }
    }

    protected static void foodCooking(Consumer<FinishedRecipe> p_250654_, List<ItemLike> p_250172_, RecipeCategory p_250588_, ItemLike p_251868_, float p_250789_, int p_252144_, String p_251687_, Boolean campfireAndSmoker) {
        oreCooking(p_250654_, RecipeSerializer.SMELTING_RECIPE, p_250172_, p_250588_, p_251868_, p_250789_, p_252144_, p_251687_, "_from_cooking");
        if (campfireAndSmoker) {
            campfireCooking(p_250654_, "campfire_cooking", 600, p_250172_, p_251868_, p_250789_);
            smokingCooking(p_250654_, "smoking", 100, p_250172_, p_251868_, p_250789_);
        }
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pfinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> recipeSerializer, List<ItemLike> p_249619_, RecipeCategory p_251154_, ItemLike p_250066_, float p_251871_, int p_251316_, String p_251450_, String pRecipieName) {
        for (ItemLike itemlike : p_249619_) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), p_251154_, p_250066_, p_251871_, p_251316_, recipeSerializer).group(p_251450_).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pfinishedRecipeConsumer, ExtraBiomes.MOD_ID + ":" + getItemName(p_250066_) + pRecipieName + "_" + getItemName(itemlike));
        }
    }

    private static void campfireCooking(Consumer<FinishedRecipe> recipeOutput, String cookingType, int cookingTime, List<ItemLike> ingredient, ItemLike output, float p_252138_) {
        for (ItemLike itemlike : ingredient) {
            SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(itemlike), RecipeCategory.FOOD, output, p_252138_, cookingTime).unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput, getItemName(output) + "_from_" + cookingType);
        }
    }

    private static void smokingCooking(Consumer<FinishedRecipe> recipeOutput, String cookingType, int cookingTime, List<ItemLike> ingredient, ItemLike output, float p_252138_) {
        for (ItemLike itemlike : ingredient) {
            SimpleCookingRecipeBuilder.smoking(Ingredient.of(itemlike), RecipeCategory.FOOD, output, p_252138_, cookingTime).unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput, getItemName(output) + "_from_" + cookingType);
        }
    }

    private static void gildRecipes(Consumer<FinishedRecipe> recipeOutput, ArrayList<Block> nonGilded, ArrayList<Block> gilded) {
        int recipeNum = 0;
        for (Block currentNonGilded : nonGilded) {
            gild(recipeOutput, currentNonGilded, gilded.get(recipeNum++));
        }
    }

    private static void woodRecipes(Consumer<FinishedRecipe> recipeOutput, Block plank, Block log, Block wood, Block strippedLog, Block stripedWood, Block stairs, Block slab, Block button, Block pressurePlate, Block fenceGate, Block fence, Block door, Block trapDoor) {
        List<ItemLike> woods = new ArrayList<>() {{
            add(log);
            add(strippedLog);
            add(wood);
            add(stripedWood);
        }};
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

