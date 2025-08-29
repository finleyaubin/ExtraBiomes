/** @type {import("@minecraft/server").BlockCustomComponent} */
export const MushroomGrowComponent = {
  onPlayerInteract(event) {
    const { block, player, dimension } = event;
    const item = player?.getComponent("minecraft:equippable")?.getEquipment("Mainhand");

    if (!item) return;

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
      dimension.runCommand(
        `structure load extrabiomes:huge_black_mushroom ${block.location.x - 6} ${block.location.y} ${block.location.z - 6}`
      );
      item.amount--; 
    }
  }
};