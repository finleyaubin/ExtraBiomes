package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.winepicfin.extrabiomes.data.CommonRecipes;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// Fabric port of forge/datagen/ModRecipeProvider.java. All shared recipe content lives in
// net.winepicfin.extrabiomes.datagen.CommonRecipes (common module); this class only supplies the
// Fabric-specific base class (FabricRecipeProvider, constructor takes FabricDataOutput instead of
// Forge's plain PackOutput) and delegates to it.
//
// As of 1.21.2, RecipeProvider generation is factory-based: FabricRecipeProvider requires
// createRecipeProvider(HolderLookup.Provider, RecipeOutput) to return the RecipeProvider instance
// whose buildRecipes() actually runs, rather than a single void buildRecipes(RecipeOutput) override.
public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(@NotNull HolderLookup.Provider registries, @NotNull RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                CommonRecipes.build(registries, output);
            }
        };
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
