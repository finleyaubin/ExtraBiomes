package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;
import net.winepicfin.extrabiomes.data.CommonRecipes;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// Fabric port of forge/datagen/ModRecipeProvider.java. All shared recipe content lives in
// net.winepicfin.extrabiomes.datagen.CommonRecipes (common module); this class only supplies the
// Fabric-specific base class (FabricRecipeProvider, constructor takes FabricDataOutput instead of
// Forge's plain PackOutput) and delegates to it.
//
// As of 26.3, RecipeProvider generation is factory-based: FabricRecipeProvider requires
// createRecipeProvider(HolderLookup.Provider, BootstrapContext<Recipe<?>>, BootstrapContext<Advancement>)
// to return the RecipeProvider instance whose buildRecipes() actually runs, rather than a single void
// buildRecipes(RecipeOutput) override.
public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(@NotNull HolderLookup.Provider registries, @NotNull BootstrapContext<Recipe<?>> recipes, @NotNull BootstrapContext<Advancement> advancements) {
        return new RecipeProvider(recipes, advancements) {
            @Override
            public void buildRecipes() {
                CommonRecipes.build(recipes, advancements);
            }
        };
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
