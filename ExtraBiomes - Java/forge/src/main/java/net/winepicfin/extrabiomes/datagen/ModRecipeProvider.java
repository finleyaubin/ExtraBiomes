package net.winepicfin.extrabiomes.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.winepicfin.extrabiomes.data.CommonRecipes;
import org.jetbrains.annotations.NotNull;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput pWriter) {
        CommonRecipes.build(pWriter);
    }
}
