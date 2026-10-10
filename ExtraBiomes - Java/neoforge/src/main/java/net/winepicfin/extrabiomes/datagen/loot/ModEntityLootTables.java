package net.winepicfin.extrabiomes.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.winepicfin.extrabiomes.commondatagen.loot.ModEntityLootTableEntries;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ModEntityLootTables extends EntityLootSubProvider {
    // EntityLootSubProvider only exposes a LootTableSubProvider.Context now (per-registry
    // HolderGetters), not a full HolderLookup.Provider - ModEntityLootTableEntries.populate needs
    // the latter (registries.lookupOrThrow(Registries.ENCHANTMENT)), so it's threaded in separately
    // from the same CompletableFuture<HolderLookup.Provider> DataGenerators already has, mirroring
    // fabric/'s ModEntityLootTables, which does the same registriesFuture.join().
    private final CompletableFuture<HolderLookup.Provider> registriesFuture;

    public ModEntityLootTables(LootTableSubProvider.Context context, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(FeatureFlags.REGISTRY.allFlags(), context);
        this.registriesFuture = registriesFuture;
    }

    @Override
    public void generate() {
        ModEntityLootTableEntries.populate(registriesFuture.join(), this::add);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntityLootTableEntries.KNOWN_ENTITY_TYPES.stream();
    }
}
