import { system } from "@minecraft/server";
import { MushroomGrowComponent } from "./mushroom_grower.js";
import { XpRewardComponent } from "./xp_reward.js";
import { MushroomLootComponent } from "./mushroom_loot.js";
import { PebbleUpdaterComponent } from "./pebble_updater.js";
import "./DenseCloudEffect.js";
import { StripperComponent } from "./stripper.js";
import { SlabberComponent } from "./slabber.js";
import { LeafLootComponent } from "./leaf_loot.js";


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
});