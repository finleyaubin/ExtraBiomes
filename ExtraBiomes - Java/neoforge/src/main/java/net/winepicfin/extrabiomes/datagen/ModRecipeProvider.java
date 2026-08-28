package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.winepicfin.extrabiomes.data.CommonRecipes;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// NeoForge's vanilla RecipeProvider constructor takes the registry lookup future directly
// (unlike Forge's patched single-PackOutput-arg convenience constructor forge/'s copy of this
// class relies on) - this mod doesn't need the lookup for anything, so it's accepted and ignored.
public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput pWriter) {
        CommonRecipes.build(pWriter);
    }
}
