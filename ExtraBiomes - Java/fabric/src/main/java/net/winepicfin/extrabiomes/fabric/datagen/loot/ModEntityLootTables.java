package net.winepicfin.extrabiomes.fabric.datagen.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;

import java.util.function.BiConsumer;

// Fabric port of forge/datagen/loot/ModEntityLootTables.java. Unlike Forge, plain vanilla's
// EntityLootSubProvider.generate(BiConsumer) hardcodes a completeness check against the ENTIRE
// BuiltInRegistries.ENTITY_TYPE registry (throws "Missing loottable 'minecraft:entities/allay' for
// 'minecraft:allay'" - and every other vanilla/other-mod entity - since nothing here generates
// tables for entities that aren't ours), the same problem ModBlockLootTables hit. Fabric API has no
// entity-specific scoped provider (unlike FabricBlockLootTableProvider for blocks), so this instead
// extends the generic SimpleFabricLootTableProvider and implements the raw BiConsumer callback
// directly, sidestepping EntityLootSubProvider (and its completeness check) entirely.
public class ModEntityLootTables extends SimpleFabricLootTableProvider {
    public ModEntityLootTables(FabricDataOutput output) {
        super(output, LootContextParamSets.ENTITY);
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        // giant_tortoise: scute 0-1 (+looting)
        consumer.accept(ModEntities.GIANT_TORTOISE.get().getDefaultLootTable(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.SCUTE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

        // treefrog: frogs_legs 0-1 (+looting)
        consumer.accept(ModEntities.TREEFROG.get().getDefaultLootTable(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.FROGS_LEGS.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

        // worm: 1 worm item
        consumer.accept(ModEntities.WORM.get().getDefaultLootTable(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.WORM.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))));

        // puckoo: feather 0-2 (+looting)
        consumer.accept(ModEntities.PUCKOO.get().getDefaultLootTable(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.FEATHER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

        // harpy: 2 rolls of (razor_feather w2 0-1 +looting3) or (gold_ingot w1 0-1 +looting1)
        consumer.accept(ModEntities.HARPY.get().getDefaultLootTable(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(2))
                        .add(LootItem.lootTableItem(ModItems.RAZOR_FEATHER.get()).setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 3.0F))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(1)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));

        // piranha: raw piranha item; + 25% (looting-boosted) chance of bone 1-2
        consumer.accept(ModEntities.PIRANHA.get().getDefaultLootTable(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.PIRANHA.get())))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.25F, 0.01F))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 2.0F))))));
    }
}
