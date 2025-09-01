import { system, ItemTypes, world, ItemStack } from "@minecraft/server";
import { MushroomGrowComponent } from "./mushroom_grower.js";
import { XpRewardComponent } from "./xp_reward.js";
import { MushroomLootComponent } from "./mushroom_loot.js";
import { PebbleUpdaterComponent } from "./pebble_updater.js";
import { DenseCloudEffect } from "./DenseCloudEffect.js"


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
    "extrabiomes:denseCloudEffect",
    DenseCloudEffect
  );
});