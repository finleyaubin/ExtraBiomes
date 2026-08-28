package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.winepicfin.extrabiomes.data.CommonRecipes;
import org.jetbrains.annotations.NotNull;

// Fabric port of forge/datagen/ModRecipeProvider.java. All shared recipe content lives in
// net.winepicfin.extrabiomes.datagen.CommonRecipes (common module); this class only supplies the
// Fabric-specific base class (FabricRecipeProvider, constructor takes FabricDataOutput instead of
// Forge's plain PackOutput) and delegates buildRecipes to it.
public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(@NotNull RecipeOutput pWriter) {
        CommonRecipes.build(pWriter);
    }
}
