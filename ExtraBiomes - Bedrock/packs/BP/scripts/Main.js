//Imoport block components
import { system } from "@minecraft/server";
import { MushroomGrowComponent } from "./blocks/Components/mushroom_grower.js";
import { XpRewardComponent } from "./blocks/Components/xp_reward.js";
import { MushroomLootComponent } from "./blocks/Components/mushroom_loot.js";
import { PebbleUpdaterComponent } from "./blocks/Components/pebble_updater.js";
import { StripperComponent } from "./blocks/Components/stripper.js";
import { SlabberComponent } from "./blocks/Components/slabber.js";
import { LeafLootComponent } from "./blocks/Components/leaf_loot.js";
import { SaplingGrowComponent } from "./blocks/Components/sapling_grower.js";
import { OpenComponent } from "./blocks/Components/open.js";
import { fence } from "./blocks/Components/fence_place.js";
import { DoorCloseComponent, DoorOnPlace, DoorOpenComponent, ResetTop, ResetBottom } from "./blocks/Components/door.js";
import { SkyCityBlockUpdateComponent } from "./blocks/Components/sky_city_block_update.js";

//Import item components
import { JellyfishReleaseComponent } from "./items/jellyfish_release.js";
import { DrinkJellyfishJamComponent } from "./items/drink_jellyfish_jam.js";

//Runs Scripts
import "./blocks/dense_cloud_effect.js";
import "./blocks/stairs.js"
import { LeafDecay } from "./blocks/leaf_decay.js";

system.beforeEvents.startup.subscribe(({ blockComponentRegistry, itemComponentRegistry }) => {
  //Registers block components
  blockComponentRegistry.registerCustomComponent("extrabiomes:mushroom_grow",MushroomGrowComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:xp_reward_component",XpRewardComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:mushroom_loot",MushroomLootComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:pebbleUpdater",PebbleUpdaterComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:stripper_component",StripperComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:slabber_component",SlabberComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:leaf_loot",LeafLootComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:leaf_decay",LeafDecay);
  blockComponentRegistry.registerCustomComponent("extrabiomes:sapling_grower",SaplingGrowComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:open",OpenComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:fence",fence);
  blockComponentRegistry.registerCustomComponent("extrabiomes:door_place",DoorOnPlace);
  blockComponentRegistry.registerCustomComponent("extrabiomes:door_close",DoorCloseComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:door_open",DoorOpenComponent);
  blockComponentRegistry.registerCustomComponent("extrabiomes:reset_top",ResetTop);
  blockComponentRegistry.registerCustomComponent("extrabiomes:reset_bottom",ResetBottom);
  blockComponentRegistry.registerCustomComponent("extrabiomes:sky_city_block_update",SkyCityBlockUpdateComponent);

  //Registers item components
  itemComponentRegistry.registerCustomComponent("extrabiomes:jellyfish_release",JellyfishReleaseComponent);
  itemComponentRegistry.registerCustomComponent("extrabiomes:drink_jellyfish_jam",DrinkJellyfishJamComponent);
});