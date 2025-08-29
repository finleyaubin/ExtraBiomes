import { system, ItemTypes, world } from "@minecraft/server";
import { MushroomGrowComponent } from "./mushroomgrower.js";
import { XpRewardComponent } from "./xpReward.js";


system.beforeEvents.startup.subscribe(({blockComponentRegistry})=>{
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:mushroom_grow",
    MushroomGrowComponent
  );
  blockComponentRegistry.registerCustomComponent(
    "extrabiomes:xp_reward_component",
    XpRewardComponent
  );
});