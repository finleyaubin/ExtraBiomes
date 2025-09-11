import { ItemStack } from "@minecraft/server";
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const LeafLootComponent = {
  onPlayerBreak(event) {
    const { block, player, brokenBlockPermutation } = event;
    const item = player?.getComponent("minecraft:equippable")?.getEquipment("Mainhand");

    if (!item) return;

    drop_leaf_loot(brokenBlockPermutation, item.typeId === "minecraft:shears", block)

  }
};

export function drop_leaf_loot(block, is_sheared, newblock) {
  function getLeafFromId(blockId) {
    const [, rest] = blockId.split(':');
    const [leaf] = rest.split('_');
    return leaf;
  }

  const leaf = getLeafFromId(block.type.id)
  var extraLoot = "none";
  switch (leaf) {
    case "palm":
      extraLoot = "cocoa_beans"
      break;
    case "mystic":
      extraLoot = "glow_berries"
      break;
  }
  const weight = Math.random();
  if (is_sheared) {
    newblock.dimension.spawnItem(new ItemStack(`extrabiomes:${leaf}_leaves`, 1), newblock.center());
  }
  else {
    if (weight <= 0.10) {
      newblock.dimension.spawnItem(new ItemStack(`extrabiomes:${leaf}_sapling`, 1), newblock.center());
    }
    else if (weight > 0.10 && weight <= 0.20) {
      newblock.dimension.spawnItem(new ItemStack(`minecraft:stick`, 1), newblock.center());
    }
    else if (weight <= 0.30 && extraLoot && extraLoot !== "none") {
      newblock.dimension.spawnItem(new ItemStack(extraLoot, 1), newblock.center());
    }
  }
}