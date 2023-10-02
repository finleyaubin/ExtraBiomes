package net.winepicfin.extrabiomes.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.item.ModItems;

import java.util.List;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput pWriter) {
        oreBlasting(pWriter, DIAMOND_SMELTABLES, RecipeCategory.MISC, Items.DIAMOND, 0.25f, 100, "diamond", Boolean.TRUE);
        foodCooking(pWriter, FROG_SMELTABLES, RecipeCategory.MISC, ModItems.COOKED_FROGS_LEGS.get(), 0.25f, 100, "cooked_frogs_legs", Boolean.TRUE);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModBlocks.DENSE_CLOUD_BRICK.get())
                .pattern("&&")
                .pattern("&&")
                .define('&', ModBlocks.DENSE_CLOUD.get())
                .unlockedBy(getHasName(ModBlocks.DENSE_CLOUD.get()), has(ModBlocks.DENSE_CLOUD.get()))
                .save(pWriter);
    }

    public static final List<ItemLike> DIAMOND_SMELTABLES = List.of(ModBlocks.NETHER_DIAMOND_ORE.get());
    public static final List<ItemLike> FROG_SMELTABLES = List.of(ModItems.FROGS_LEGS.get());


    protected static void oreSmelting(RecipeOutput p_250654_, List<ItemLike> p_250172_, RecipeCategory p_250588_, ItemLike p_251868_, float p_250789_, int p_252144_, String p_251687_) {
        oreCooking(p_250654_, RecipeSerializer.SMELTING_RECIPE, p_250172_, p_250588_, p_251868_, p_250789_, p_252144_, p_251687_, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput p_248775_, List<ItemLike> p_251504_, RecipeCategory p_248846_, ItemLike p_249735_, float p_248783_, int p_250303_, String p_251984_, Boolean createSmelting) {
        oreCooking(p_248775_, RecipeSerializer.BLASTING_RECIPE, p_251504_, p_248846_, p_249735_, p_248783_, p_250303_, p_251984_, "_from_blasting");
        if (createSmelting){
            oreSmelting(p_248775_, p_251504_, p_248846_, p_249735_, p_248783_, p_250303_*2, p_251984_);
        }
    }

    protected static void foodCooking(RecipeOutput p_250654_, List<ItemLike> p_250172_, RecipeCategory p_250588_, ItemLike p_251868_, float p_250789_, int p_252144_, String p_251687_, Boolean campfireAndSmoker) {
        oreCooking(p_250654_, RecipeSerializer.SMELTING_RECIPE, p_250172_, p_250588_, p_251868_, p_250789_, p_252144_, p_251687_, "_from_cooking");
        if (campfireAndSmoker){
            campfireCooking(p_250654_,"campfire_cooking", 600, p_250172_, p_251868_,p_250789_);
            SmokingCooking(p_250654_,"smoking", 100, p_250172_, p_251868_,p_250789_);
        }
    }

    protected static void oreCooking(RecipeOutput pfinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> recipeSerializer, List<ItemLike> p_249619_, RecipeCategory p_251154_, ItemLike p_250066_, float p_251871_, int p_251316_, String p_251450_, String pRecipieName) {
        for(ItemLike itemlike : p_249619_) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), p_251154_, p_250066_, p_251871_, p_251316_, recipeSerializer).group(p_251450_).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pfinishedRecipeConsumer, ExtraBiomes.MOD_ID + ":" + getItemName(p_250066_) + pRecipieName + "_" + getItemName(itemlike));
        }
    }

    private static void campfireCooking(RecipeOutput recipeOutput, String cookingType, int cookingTime, List<ItemLike> ingredient, ItemLike output, float p_252138_) {
        for(ItemLike itemlike : ingredient) {
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(itemlike), RecipeCategory.FOOD, output, p_252138_, cookingTime).unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput, getItemName(output) + "_from_" + cookingType);
        }
    }

    private static void SmokingCooking(RecipeOutput recipeOutput, String cookingType, int cookingTime, List<ItemLike> ingredient, ItemLike output, float p_252138_) {
        for(ItemLike itemlike : ingredient) {
            SimpleCookingRecipeBuilder.smoking(Ingredient.of(itemlike), RecipeCategory.FOOD, output, p_252138_, cookingTime).unlockedBy(getHasName(itemlike), has(itemlike)).save(recipeOutput, getItemName(output) + "_from_" + cookingType);
        }
    }

}
