//if the player is holding bone meal, it has a chance to grow a large mushroom structure of that type with the base replacing where the mushroom was
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const MushroomGrowComponent = {
  onPlayerInteract(event) {
    const { block, player, dimension } = event;
    const item = player?.getComponent("minecraft:equippable")?.getEquipment("Mainhand");

    if (!item) return;

    function getColourFromId(blockId) {
      const [, rest] = blockId.split(':');
      const [colour] = rest.split('_');
      return colour;
    }
    const colour = getColourFromId(block.type.id)

    // Check if the player is holding bone meal
    if (item.typeId === "minecraft:bone_meal") {
      // 75% chance to just consume bone meal with particles
      if (Math.random() < 0.75) {
        dimension.spawnParticle("minecraft:crop_growth_emitter", block.center());
        dimension.playSound("item.bone_meal.use", block.center());
        item.amount--; // decrement stack
        return;
      }

      // 25% chance: grow huge mushroom
      switch (colour) {
        case "black":
          dimension.runCommand(`structure load extrabiomes:huge_black_mushroom ${block.location.x - 6} ${block.location.y} ${block.location.z - 6}`);
          break;

        case "blue":
          dimension.runCommand(`structure load extrabiomes:huge_blue_mushroom ${block.location.x - 4} ${block.location.y} ${block.location.z - 4}`);
          break;

        case "cyan":
          dimension.runCommand(`structure load extrabiomes:huge_cyan_mushroom ${block.location.x - 4} ${block.location.y} ${block.location.z - 4}`);
          break;

        case "glow":
          dimension.runCommand(`structure load extrabiomes:huge_glow_mushroom ${block.location.x - 1} ${block.location.y} ${block.location.z - 1}`);
          break;

        case "green":
          dimension.runCommand(`structure load extrabiomes:huge_green_mushroom ${block.location.x - 2} ${block.location.y} ${block.location.z - 2}`);
          break;

        case "orange":
          dimension.runCommand(`structure load extrabiomes:huge_orange_mushroom ${block.location.x - 5} ${block.location.y} ${block.location.z - 5}`);
          break;

        case "purple":
          dimension.runCommand(`structure load extrabiomes:huge_purple_mushroom ${block.location.x - 3} ${block.location.y} ${block.location.z - 4}`);
          break;

        case "white":
          dimension.runCommand(`structure load extrabiomes:huge_white_mushroom ${block.location.x - 3} ${block.location.y} ${block.location.z - 3}`);
          break;

        case "yellow":
          dimension.runCommand(`structure load extrabiomes:huge_yellow_mushroom ${block.location.x - 4} ${block.location.y} ${block.location.z - 4}`);
          break;
      }
      item.amount--;
    }
  }
};