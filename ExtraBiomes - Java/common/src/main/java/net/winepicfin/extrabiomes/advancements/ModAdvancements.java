package net.winepicfin.extrabiomes.advancements;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.block.ModBlocks;
import net.winepicfin.extrabiomes.entity.ModEntities;
import net.winepicfin.extrabiomes.item.ModItems;
import net.winepicfin.extrabiomes.worldgen.biomes.ModBiomes;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Playtest-requested achievement tree ("add some relevant achievements to the mod") - not ported
 * from Bedrock (Bedrock addons have no advancement/achievement equivalent), so this is an original
 * set covering the mod's headline content: taming, the new worm/goo items, mystic wood, frog gear,
 * The Netherlands biome + its ore, and (mirroring vanilla's own "Adventuring Time") visiting every
 * biome the mod adds.
 * <p>
 * In a package of its own (not {@code net.winepicfin.extrabiomes.datagen}, which forge/'s own
 * datagen package already occupies) because Forge's modlauncher runs under the Java module system,
 * which forbids two modules exporting the exact same package name (a "split package") - putting
 * this in common under that name failed at runtime with a ResolutionException, not a compile error.
 * <p>
 * Shared between Forge and Fabric ({@code net.minecraft.data.advancements.AdvancementProvider} is a
 * concrete vanilla class taking a {@code List<AdvancementSubProvider>} directly - no per-loader
 * subclass needed, unlike the other datagen providers in this project that depend on
 * Forge/Fabric-specific wrapper types.
 */
public class ModAdvancements implements AdvancementSubProvider {
    // Kept as an explicit list (rather than reflecting over ModBiomes's fields) so a new biome only shows up in the "visit them all" advancement once someone deliberately adds it here.
    private static final List<ResourceKey<Biome>> ALL_BIOMES = List.of(
            ModBiomes.CHARRED_FOREST, ModBiomes.COLD_MESA, ModBiomes.COLD_MESA_BRYCE, ModBiomes.COLD_MESA_PLATEAU,
            ModBiomes.DEEP_DARK_FOREST, ModBiomes.DEEP_DARK_GREEN, ModBiomes.DESERT_BRYCE, ModBiomes.FLOATING_JUNGLE,
            ModBiomes.FUNGLE_JUNGLE, ModBiomes.FUTURE_DESERT, ModBiomes.GLACIER, ModBiomes.GRAND_OASIS,
            ModBiomes.JELLYFISH_FIELDS, ModBiomes.JUNGLE_MARSH, ModBiomes.JUNGLE_PILLARS, ModBiomes.LUSH_MESA,
            ModBiomes.LUSH_MESA_BRYCE, ModBiomes.MOORLANDS, ModBiomes.MYSTIC_FOREST, ModBiomes.SHATTERED_SWAMP,
            ModBiomes.SHATTERED_TAIGA_SPIKES, ModBiomes.THE_NETHERLANDS, ModBiomes.THE_NETHERLANDS_MUTATED,
            ModBiomes.TAIGA_SPIKES, ModBiomes.TROPICAL_ISLAND, ModBiomes.VOLCANIC_MOSS_TUNDRA);

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver) {
        HolderGetter<Biome> biomes = registries.lookupOrThrow(Registries.BIOME);
        ResourceLocation rootId = new ResourceLocation(ExtraBiomes.MOD_ID, "root");
        Advancement.Builder rootBuilder = Advancement.Builder.advancement()
                .display(ModItems.WORM.get(),
                        Component.translatable("advancements.extrabiomes.root.title"),
                        Component.translatable("advancements.extrabiomes.root.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        AdvancementType.TASK, false, false, false)
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick());
        AdvancementHolder root = rootBuilder.build(rootId);
        saver.accept(root);

        ResourceLocation tamePuckooId = new ResourceLocation(ExtraBiomes.MOD_ID, "tame_puckoo");
        saver.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.PUCKOO_SPAWN_EGG.get(),
                        Component.translatable("advancements.extrabiomes.tame_puckoo.title"),
                        Component.translatable("advancements.extrabiomes.tame_puckoo.description"),
                        null, AdvancementType.TASK, true, true, false)
                .addCriterion("tamed_puckoo", TameAnimalTrigger.TriggerInstance.tamedAnimal(
                        EntityPredicate.Builder.entity().of(ModEntities.PUCKOO.get())))
                .build(tamePuckooId));

        ResourceLocation catchWormId = new ResourceLocation(ExtraBiomes.MOD_ID, "catch_worm");
        saver.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.WORM.get(),
                        Component.translatable("advancements.extrabiomes.catch_worm.title"),
                        Component.translatable("advancements.extrabiomes.catch_worm.description"),
                        null, AdvancementType.TASK, true, true, false)
                .addCriterion("has_worm", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.WORM.get()))
                .build(catchWormId));

        ResourceLocation gooCollectorId = new ResourceLocation(ExtraBiomes.MOD_ID, "goo_collector");
        saver.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.BUCKET_OF_GOO.get(),
                        Component.translatable("advancements.extrabiomes.goo_collector.title"),
                        Component.translatable("advancements.extrabiomes.goo_collector.description"),
                        null, AdvancementType.TASK, true, true, false)
                .addCriterion("has_goo", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BUCKET_OF_GOO.get()))
                .build(gooCollectorId));

        ResourceLocation mysticWoodworkerId = new ResourceLocation(ExtraBiomes.MOD_ID, "mystic_woodworker");
        saver.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(ModBlocks.MYSTIC_PLANKS.get(),
                        Component.translatable("advancements.extrabiomes.mystic_woodworker.title"),
                        Component.translatable("advancements.extrabiomes.mystic_woodworker.description"),
                        null, AdvancementType.TASK, true, true, false)
                .addCriterion("has_mystic_planks", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.MYSTIC_PLANKS.get()))
                .build(mysticWoodworkerId));

        ResourceLocation amphibiousArmorId = new ResourceLocation(ExtraBiomes.MOD_ID, "amphibious_armor");
        saver.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.FROG_HELMET.get(),
                        Component.translatable("advancements.extrabiomes.amphibious_armor.title"),
                        Component.translatable("advancements.extrabiomes.amphibious_armor.description"),
                        null, AdvancementType.TASK, true, true, false)
                .addCriterion("has_frog_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.FROG_HELMET.get()))
                .build(amphibiousArmorId));

        ResourceLocation piranhaDinnerId = new ResourceLocation(ExtraBiomes.MOD_ID, "piranha_dinner");
        saver.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.COOKED_PIRANHA.get(),
                        Component.translatable("advancements.extrabiomes.piranha_dinner.title"),
                        Component.translatable("advancements.extrabiomes.piranha_dinner.description"),
                        null, AdvancementType.TASK, true, true, false)
                .addCriterion("ate_cooked_piranha", ConsumeItemTrigger.TriggerInstance.usedItem(ModItems.COOKED_PIRANHA.get()))
                .build(piranhaDinnerId));

        ResourceLocation visitNetherlandsId = new ResourceLocation(ExtraBiomes.MOD_ID, "visit_netherlands");
        AdvancementHolder visitNetherlands = Advancement.Builder.advancement()
                .parent(root)
                .display(Items.WHEAT,
                        Component.translatable("advancements.extrabiomes.visit_netherlands.title"),
                        Component.translatable("advancements.extrabiomes.visit_netherlands.description"),
                        null, AdvancementType.GOAL, true, true, false)
                .addCriterion("in_netherlands", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.Builder.inBiome(biomes.getOrThrow(ModBiomes.THE_NETHERLANDS))))
                .addCriterion("in_netherlands_mutated", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.Builder.inBiome(biomes.getOrThrow(ModBiomes.THE_NETHERLANDS_MUTATED))))
                .build(visitNetherlandsId);
        saver.accept(visitNetherlands);

        ResourceLocation baitAndSwitchId = new ResourceLocation(ExtraBiomes.MOD_ID, "bait_and_switch");
        saver.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.BAIT.get(),
                        Component.translatable("advancements.extrabiomes.bait_and_switch.title"),
                        Component.translatable("advancements.extrabiomes.bait_and_switch.description"),
                        null, AdvancementType.TASK, true, true, false)
                .addCriterion("lured_piranha_with_bait", ModCriteriaTriggers.LURED_PIRANHA_WITH_BAIT.get().createCriterion(BaitLureTrigger.TriggerInstance.luredPiranhaWithBait()))
                .build(baitAndSwitchId));

        ResourceLocation dutchTreasureId = new ResourceLocation(ExtraBiomes.MOD_ID, "dutch_treasure");
        saver.accept(Advancement.Builder.advancement()
                .parent(visitNetherlands)
                .display(ModBlocks.NETHER_DIAMOND_ORE.get(),
                        Component.translatable("advancements.extrabiomes.dutch_treasure.title"),
                        Component.translatable("advancements.extrabiomes.dutch_treasure.description"),
                        null, AdvancementType.CHALLENGE, true, true, false)
                .addCriterion("has_nether_diamond_ore", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.NETHER_DIAMOND_ORE.get()))
                .build(dutchTreasureId));

        ResourceLocation biomeExplorerId = new ResourceLocation(ExtraBiomes.MOD_ID, "biome_explorer");
        Advancement.Builder biomeExplorer = Advancement.Builder.advancement()
                .parent(root)
                .display(Items.FILLED_MAP,
                        Component.translatable("advancements.extrabiomes.biome_explorer.title"),
                        Component.translatable("advancements.extrabiomes.biome_explorer.description"),
                        null, AdvancementType.CHALLENGE, true, true, false);
        for (ResourceKey<Biome> biome : ALL_BIOMES) {
            biomeExplorer.addCriterion(biome.location().getPath(), PlayerTrigger.TriggerInstance.located(
                    LocationPredicate.Builder.inBiome(biomes.getOrThrow(biome))));
        }
        saver.accept(biomeExplorer.build(biomeExplorerId));
    }

    private static String advancementId(String name) {
        return new ResourceLocation(ExtraBiomes.MOD_ID, name).toString();
    }
}
