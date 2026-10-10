package net.winepicfin.extrabiomes.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.winepicfin.extrabiomes.datagen.loot.ModBlockLootTables;
import net.winepicfin.extrabiomes.datagen.loot.ModEntityLootTables;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider {
    public static LootTableProvider create(CompletableFuture<HolderLookup.Provider> lookupProvider) {
        return new LootTableProvider(Set.<ResourceKey<LootTable>>of(), List.of(
                new LootTableProvider.SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(context -> new ModEntityLootTables(context, lookupProvider), LootContextParamSets.ENTITY)
        ));
    }
}
