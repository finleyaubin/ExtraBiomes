package net.winepicfin.extrabiomes.commondatagen.loot;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;

import java.util.function.BiConsumer;

// Entity loot tables ported from the Bedrock BP/loot_tables/entities/*.json files - shared body of
// both loaders' entity loot table generators (forge/.../datagen/loot/ModEntityLootTables and
// fabric/.../datagen/loot/ModEntityLootTables). Forge's EntityLootSubProvider.add(EntityType, Builder)
// and Fabric's raw SimpleFabricLootTableProvider BiConsumer<ResourceLocation, Builder> callback have
// different shapes (Fabric has no entity-specific scoped provider like FabricBlockLootTableProvider,
// so it sidesteps EntityLootSubProvider's registry-wide completeness check entirely by extending the
// generic SimpleFabricLootTableProvider instead) - each loader adapts its own callback to this
// shared EntityType-keyed shape (Fabric via entityType.getDefaultLootTable()), so the actual table
// contents only need to be written once.
public class ModEntityLootTableEntries {
    public static void populate(BiConsumer<EntityType<?>, LootTable.Builder> add) {
        // giant_tortoise: scute 0-1 (+looting)
        add.accept(ModEntities.GIANT_TORTOISE.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.SCUTE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

        // treefrog: frogs_legs 0-1 (+looting)
        add.accept(ModEntities.TREEFROG.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.FROGS_LEGS.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

        // worm: 1 worm item
        add.accept(ModEntities.WORM.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.WORM.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))));

        // puckoo: feather 0-2 (+looting)
        add.accept(ModEntities.PUCKOO.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.FEATHER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

        // harpy: 2 rolls of (razor_feather w2 0-1 +looting3) or (gold_ingot w1 0-1 +looting1)
        add.accept(ModEntities.HARPY.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(2))
                        .add(LootItem.lootTableItem(ModItems.RAZOR_FEATHER.get()).setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 3.0F))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(1)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

        // piranha: raw piranha item; + 25% (looting-boosted) chance of bone 1-2
        add.accept(ModEntities.PIRANHA.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.PIRANHA.get())))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.25F, 0.01F))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 2.0F))))));
    }

    public static final java.util.List<EntityType<?>> KNOWN_ENTITY_TYPES = java.util.List.of(
            ModEntities.GIANT_TORTOISE.get(),
            ModEntities.TREEFROG.get(),
            ModEntities.WORM.get(),
            ModEntities.PUCKOO.get(),
            ModEntities.HARPY.get(),
            ModEntities.PIRANHA.get());
}
