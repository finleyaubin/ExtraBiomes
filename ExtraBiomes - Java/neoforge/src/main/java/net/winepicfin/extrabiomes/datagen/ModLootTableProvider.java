package net.winepicfin.extrabiomes.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.winepicfin.extrabiomes.datagen.loot.ModBlockLootTables;
import net.winepicfin.extrabiomes.datagen.loot.ModEntityLootTables;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider{
    public static LootTableProvider create(PackOutput out, CompletableFuture<HolderLookup.Provider> lookupProvider){
        return new LootTableProvider(out, Set.<ResourceKey<LootTable>>of(), List.of(
                new LootTableProvider.SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(ModEntityLootTables::new, LootContextParamSets.ENTITY)
        ), lookupProvider);
    }
}
