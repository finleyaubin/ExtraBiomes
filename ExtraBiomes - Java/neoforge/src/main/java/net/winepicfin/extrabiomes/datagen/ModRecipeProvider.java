package net.winepicfin.extrabiomes.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.winepicfin.extrabiomes.advancements.ModAdvancements;
import net.winepicfin.extrabiomes.data.CommonRecipes;

import java.util.Set;

// CommonRecipes.build writes recipe-unlock criteria straight into the same BootstrapContext<Advancement>
// ModAdvancements uses, so both must run against one shared context inside a single MultiRegistryBootstrap
// requesting RECIPE + ADVANCEMENT - two separate RegistrySetBuilder.add() calls targeting ADVANCEMENT would
// throw "Multiple entries with same key" (see ModWorldGenProvider's identical note for FEATURE/PLACED_FEATURE).
public class ModRecipeProvider implements MultiRegistryBootstrap {
    public static final ModRecipeProvider BOOTSTRAP = new ModRecipeProvider();

    @Override
    public Set<ResourceKey<? extends Registry<?>>> requestedRegistries() {
        return Set.of(Registries.RECIPE, Registries.ADVANCEMENT);
    }

    @Override
    public void run(BootstrapGetter getter) {
        BootstrapContext<Advancement> advancements = getter.get(Registries.ADVANCEMENT);
        CommonRecipes.build(getter.get(Registries.RECIPE), advancements);
        new ModAdvancements(advancements).generate();
    }
}
