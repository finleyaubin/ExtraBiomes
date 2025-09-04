import { ItemStack } from "@minecraft/server";
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const LeafLootComponent = {
  onPlayerBreak(event) {
    const { block, player, dimension, brokenBlockPermutation } = event;
    const item = player?.getComponent("minecraft:equippable")?.getEquipment("Mainhand");

    if (!item) return;


    //gets the leaf of the leaf block so that this can be used for all leaves
    function getLeafFromId(blockId) {
      const [, rest] = blockId.split(':');
      const [leaf] = rest.split('_');
      return leaf;
    }
    const leaf = getLeafFromId(brokenBlockPermutation.type.id)
    var extraLoot="none";
    switch (leaf){
      case"palm":
        extraLoot="cocoa_beans"
        break;
      case "mystic":
         extraLoot="glow_berries"
         break;
    }
    

    const weight = Math.random();
    if (item.typeId === "minecraft:shears") {
      dimension.spawnItem(new ItemStack(`extrabiomes:${leaf}_leaves`, 1), block.center());
    }
    else {
      
      if (weight <= 0.10) {
        dimension.spawnItem(new ItemStack(`extrabiomes:${leaf}_sapling`, 1), block.center());
      }
      else if (weight > 0.10 && weight <= 0.20) {
        dimension.spawnItem(new ItemStack(`minecraft:stick`, 1), block.center());
      }
      else if (weight <= 0.30 && extraLoot && extraLoot !== "none") {
        dimension.spawnItem(new ItemStack(extraLoot, 1), block.center());
      }
    }
  }
};