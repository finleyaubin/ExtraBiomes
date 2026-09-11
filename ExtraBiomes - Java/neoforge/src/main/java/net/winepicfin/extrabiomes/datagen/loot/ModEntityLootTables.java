package net.winepicfin.extrabiomes.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.winepicfin.extrabiomes.commondatagen.loot.ModEntityLootTableEntries;

import java.util.stream.Stream;

public class ModEntityLootTables extends EntityLootSubProvider {
    public ModEntityLootTables(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        ModEntityLootTableEntries.populate(this.registries, this::add);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntityLootTableEntries.KNOWN_ENTITY_TYPES.stream();
    }
}
