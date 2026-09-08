package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.winepicfin.extrabiomes.data.CommonRecipes;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// RecipeProvider itself is no longer a DataProvider as of 1.21.3 (its nested Runner is) - the
// Runner supplies buildRecipes() with the registries/output pair CommonRecipes.build now needs.
public class ModRecipeProvider extends RecipeProvider.Runner {
    public ModRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider registries, @NotNull RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            protected void buildRecipes() {
                CommonRecipes.build(this.registries, this.output);
            }
        };
    }

    @Override
    public @NotNull String getName() {
        return "Recipes";
    }
}
