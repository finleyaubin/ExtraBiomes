package net.winepicfin.extrabiomes.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.core.HolderLookup;
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
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver) {
        Advancement root = Advancement.Builder.advancement()
                .display(ModItems.WORM.get(),
                        Component.translatable("advancements.extrabiomes.root.title"),
                        Component.translatable("advancements.extrabiomes.root.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK, false, false, false)
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                .save(saver, advancementId("root"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.PUCKOO_SPAWN_EGG.get(),
                        Component.translatable("advancements.extrabiomes.tame_puckoo.title"),
                        Component.translatable("advancements.extrabiomes.tame_puckoo.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("tamed_puckoo", TameAnimalTrigger.TriggerInstance.tamedAnimal(
                        EntityPredicate.Builder.entity().of(ModEntities.PUCKOO.get()).build()))
                .save(saver, advancementId("tame_puckoo"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.WORM.get(),
                        Component.translatable("advancements.extrabiomes.catch_worm.title"),
                        Component.translatable("advancements.extrabiomes.catch_worm.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("has_worm", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.WORM.get()))
                .save(saver, advancementId("catch_worm"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.BUCKET_OF_GOO.get(),
                        Component.translatable("advancements.extrabiomes.goo_collector.title"),
                        Component.translatable("advancements.extrabiomes.goo_collector.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("has_goo", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BUCKET_OF_GOO.get()))
                .save(saver, advancementId("goo_collector"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(ModBlocks.MYSTIC_PLANKS.get(),
                        Component.translatable("advancements.extrabiomes.mystic_woodworker.title"),
                        Component.translatable("advancements.extrabiomes.mystic_woodworker.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("has_mystic_planks", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.MYSTIC_PLANKS.get()))
                .save(saver, advancementId("mystic_woodworker"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.FROG_HELMET.get(),
                        Component.translatable("advancements.extrabiomes.amphibious_armor.title"),
                        Component.translatable("advancements.extrabiomes.amphibious_armor.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("has_frog_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.FROG_HELMET.get()))
                .save(saver, advancementId("amphibious_armor"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.COOKED_PIRANHA.get(),
                        Component.translatable("advancements.extrabiomes.piranha_dinner.title"),
                        Component.translatable("advancements.extrabiomes.piranha_dinner.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("ate_cooked_piranha", ConsumeItemTrigger.TriggerInstance.usedItem(ModItems.COOKED_PIRANHA.get()))
                .save(saver, advancementId("piranha_dinner"));

        // Either biome counts - requirements(OR) so the mutated variant doesn't need its own separate advancement.
        Advancement visitNetherlands = Advancement.Builder.advancement()
                .parent(root)
                .display(Items.WHEAT,
                        Component.translatable("advancements.extrabiomes.visit_netherlands.title"),
                        Component.translatable("advancements.extrabiomes.visit_netherlands.description"),
                        null, FrameType.GOAL, true, true, false)
                .addCriterion("in_netherlands", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.Builder.location().setBiome(ModBiomes.THE_NETHERLANDS).build()))
                .addCriterion("in_netherlands_mutated", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.Builder.location().setBiome(ModBiomes.THE_NETHERLANDS_MUTATED).build()))
                .requirements(RequirementsStrategy.OR)
                .save(saver, advancementId("visit_netherlands"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(ModItems.BAIT.get(),
                        Component.translatable("advancements.extrabiomes.bait_and_switch.title"),
                        Component.translatable("advancements.extrabiomes.bait_and_switch.description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("lured_piranha_with_bait", BaitLureTrigger.TriggerInstance.luredPiranhaWithBait())
                .save(saver, advancementId("bait_and_switch"));

        Advancement.Builder.advancement()
                .parent(visitNetherlands)
                .display(ModBlocks.NETHER_DIAMOND_ORE.get(),
                        Component.translatable("advancements.extrabiomes.dutch_treasure.title"),
                        Component.translatable("advancements.extrabiomes.dutch_treasure.description"),
                        null, FrameType.CHALLENGE, true, true, false)
                .addCriterion("has_nether_diamond_ore", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.NETHER_DIAMOND_ORE.get()))
                .save(saver, advancementId("dutch_treasure"));

        // Mirrors vanilla's own "Adventuring Time": one location criterion per ModBiomes entry, all required (no requirements() override -> defaults to AND-all).
        Advancement.Builder biomeExplorer = Advancement.Builder.advancement()
                .parent(root)
                .display(Items.FILLED_MAP,
                        Component.translatable("advancements.extrabiomes.biome_explorer.title"),
                        Component.translatable("advancements.extrabiomes.biome_explorer.description"),
                        null, FrameType.CHALLENGE, true, true, false);
        for (ResourceKey<Biome> biome : ALL_BIOMES) {
            biomeExplorer.addCriterion(biome.location().getPath(), PlayerTrigger.TriggerInstance.located(
                    LocationPredicate.Builder.location().setBiome(biome).build()));
        }
        biomeExplorer.save(saver, advancementId("biome_explorer"));
    }

    private static String advancementId(String name) {
        return new ResourceLocation(ExtraBiomes.MOD_ID, name).toString();
    }
}
