//registers the events from the scripts so that they can be used by the blocks.json's
import { system } from "@minecraft/server";
import { MushroomGrowComponent } from "./blocks/mushroom_grower.js";
import { XpRewardComponent } from "./blocks/xp_reward.js";
import { MushroomLootComponent } from "./blocks/mushroom_loot.js";
import { PebbleUpdaterComponent } from "./blocks/pebble_updater.js";
import "./blocks/DenseCloudEffect.js";
import { StripperComponent } from "./blocks/stripper.js";
import { SlabberComponent } from "./blocks/slabber.js";
import { LeafLootComponent } from "./blocks/leaf_loot.js";
import { LeafDecayComponent } from "./blocks/leaf_decay.js";
import { SaplingGrowComponent } from "./blocks/sapling_grower.js";


system.beforeEvents.startup.subscribe(({ blockComponentRegistry }) => {
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:mushroom_grow",
    MushroomGrowComponent
  );
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:xp_reward_component",
    XpRewardComponent
  );
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:mushroom_loot",
    MushroomLootComponent
  );
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:pebbleUpdater",
    PebbleUpdaterComponent
  );
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:stripper_component",
    StripperComponent
  );
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:slabber_component",
    SlabberComponent
  );
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:leaf_loot",
    LeafLootComponent
  );
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:leaf_decay",
    LeafDecayComponent
  );
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:sapling_grower",
    SaplingGrowComponent
  );
});