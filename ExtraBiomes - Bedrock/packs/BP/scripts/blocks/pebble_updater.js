/** @type {import("@minecraft/server").BlockCustomComponent} */
export const PebbleUpdaterComponent = {
  onPlayerInteract(event) {
    const { block, player } = event;
    const item = player?.getComponent("minecraft:equippable")?.getEquipment("Mainhand");

    if (!item) return;


    if (item.typeId === "extrabiomes:pebble") {
      if (block.type.id === "extrabiomes:small_pebble")
        block.setType("extrabiomes:medium_pebble");
      else if (block.type.id === "extrabiomes:medium_pebble")
        block.setType("extrabiomes:large_pebble");
    }
    else if (item.typeId === "extrabiomes:mossy_pebble") {
      if (block.type.id === "extrabiomes:small_mossy_pebble")
        block.setType("extrabiomes:medium_mossy_pebble");
      else if (block.type.id === "extrabiomes:medium_mossy_pebble")
        block.setType("extrabiomes:large_mossy_pebble");
    }
  }
};