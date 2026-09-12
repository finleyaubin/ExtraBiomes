import { EquipmentSlot, GameMode } from "@minecraft/server";

/** @type {import("@minecraft/server").BlockCustomComponent} */
export const PebbleUpdaterComponent = {
  onPlayerInteract(event) {
    const { block, player } = event;

    const equippable = player?.getComponent("minecraft:equippable");
    if (!equippable) return;

    const mainhand = equippable.getEquipmentSlot(EquipmentSlot.Mainhand);
    if (!mainhand?.hasItem?.()) return;

    const held = mainhand.getItem ? mainhand.getItem() : mainhand;
    if (!held) return;

    let upgraded = false;

    if (held.typeId === "extrabiomes:pebble") {
      if (block.type.id === "extrabiomes:small_pebble") {
        block.setType("extrabiomes:medium_pebble");
        upgraded = true;
      }
      else if (block.type.id === "extrabiomes:medium_pebble") {
        block.setType("extrabiomes:large_pebble");
        upgraded = true;
      }
    }
    else if (held.typeId === "extrabiomes:mossy_pebble") {
      if (block.type.id === "extrabiomes:small_mossy_pebble") {
        block.setType("extrabiomes:medium_mossy_pebble");
        upgraded = true;
      }
      else if (block.type.id === "extrabiomes:medium_mossy_pebble") {
        block.setType("extrabiomes:large_mossy_pebble");
        upgraded = true;
      }
    }

    if (!upgraded) return;

    if (player.getGameMode?.() !== GameMode.Creative) {
      if (held.amount > 1) {
        held.amount--;
        mainhand.setItem?.(held);
      } else {
        mainhand.setItem?.(undefined);
      }
    }

    event.cancel = true;
  }
};