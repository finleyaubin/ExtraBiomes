package net.winepicfin.extrabiomes.datagen.loot;

import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.winepicfin.extrabiomes.commondatagen.loot.ModEntityLootTableEntries;

import java.util.stream.Stream;

public class ModEntityLootTables extends EntityLootSubProvider {
    public ModEntityLootTables() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {
        ModEntityLootTableEntries.populate(this::add);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntityLootTableEntries.KNOWN_ENTITY_TYPES.stream();
    }
}
