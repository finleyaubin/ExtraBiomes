package net.winepicfin.extrabiomes.commondatagen.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;

import java.util.function.BiConsumer;

// Entity loot tables ported from Bedrock BP/loot_tables/entities/*.json; shared body of both loaders' generators, which adapt their differently-shaped callbacks to this EntityType-keyed shape so the table contents only need to be written once.
public class ModEntityLootTableEntries {
    public static void populate(HolderLookup.Provider registries, BiConsumer<EntityType<?>, LootTable.Builder> add) {
        HolderGetter<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);

        add.accept(ModEntities.GIANT_TORTOISE.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(exactlyInt(1))
                        .add(LootItem.lootTableItem(Items.TURTLE_SCUTE)
                                .apply(SetItemCountFunction.setCount(uniformInt(0, 1)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(enchantments, uniformFloat(0.0F, 1.0F))))));

        add.accept(ModEntities.TREEFROG.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(exactlyInt(1))
                        .add(LootItem.lootTableItem(ModItems.FROGS_LEGS.get())
                                .apply(SetItemCountFunction.setCount(uniformInt(0, 1)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(enchantments, uniformFloat(0.0F, 1.0F))))));

        add.accept(ModEntities.WORM.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(exactlyInt(1))
                        .add(LootItem.lootTableItem(ModItems.WORM.get())
                                .apply(SetItemCountFunction.setCount(exactlyInt(1))))));

        add.accept(ModEntities.PUCKOO.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(exactlyInt(1))
                        .add(LootItem.lootTableItem(Items.FEATHER)
                                .apply(SetItemCountFunction.setCount(uniformInt(0, 2)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(enchantments, uniformFloat(0.0F, 1.0F))))));

        add.accept(ModEntities.HARPY.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(exactlyInt(2))
                        .add(LootItem.lootTableItem(ModItems.RAZOR_FEATHER.get()).setWeight(2)
                                .apply(SetItemCountFunction.setCount(uniformInt(0, 1)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(enchantments, uniformFloat(0.0F, 3.0F))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(1)
                                .apply(SetItemCountFunction.setCount(uniformInt(0, 1)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(enchantments, uniformFloat(0.0F, 1.0F))))));

        add.accept(ModEntities.PIRANHA.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(exactlyInt(1))
                        .add(LootItem.lootTableItem(ModItems.PIRANHA.get())))
                .withPool(LootPool.lootPool().setRolls(exactlyInt(1))
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(enchantments, 0.25F, 0.01F))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(enchantments, uniformFloat(1.0F, 2.0F))))));
    }

    private static Holder<ContextIntProvider> exactlyInt(int value) {
        return Holder.direct(new net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue(value));
    }

    private static Holder<ContextIntProvider> uniformInt(int min, int max) {
        return Holder.direct(new net.minecraft.world.level.storage.loot.providers.number.ints.UniformGenerator(exactlyInt(min), exactlyInt(max)));
    }

    private static Holder<ContextFloatProvider> exactlyFloat(float value) {
        return Holder.direct(new net.minecraft.world.level.storage.loot.providers.number.floats.ConstantValue(value));
    }

    private static Holder<ContextFloatProvider> uniformFloat(float min, float max) {
        return Holder.direct(new net.minecraft.world.level.storage.loot.providers.number.floats.UniformGenerator(exactlyFloat(min), exactlyFloat(max)));
    }

    public static final java.util.List<EntityType<?>> KNOWN_ENTITY_TYPES = java.util.List.of(
            ModEntities.GIANT_TORTOISE.get(),
            ModEntities.TREEFROG.get(),
            ModEntities.WORM.get(),
            ModEntities.PUCKOO.get(),
            ModEntities.HARPY.get(),
            ModEntities.PIRANHA.get());
}
