import { ItemStack } from "@minecraft/server";
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const MushroomLootComponent = {
  onPlayerBreak(event) {
    const { block, player, dimension, brokenBlockPermutation } = event;
    const item = player?.getComponent("minecraft:equippable")?.getEquipment("Mainhand");

    if (!item) return;


    //gets the colour of the mushroom block so that this can be used for all mushrooms
    function getColourFromId(blockId) {
        const [, rest] = blockId.split(':'); 
        const [colour] = rest.split('_');    
        return colour;                           
    }
    const colour=getColourFromId(brokenBlockPermutation.type.id)

    if (item.typeId === "minecraft:shears") {
      dimension.spawnItem(new ItemStack(`extrabiomes:${colour}_mushroom_block`, 1), block.center());
    }
    else{
      if(Math.random()<0.15){
        dimension.spawnItem(new ItemStack(`extrabiomes:${colour}_mushroom`, 1), block.center());
      }
    }
  }
};