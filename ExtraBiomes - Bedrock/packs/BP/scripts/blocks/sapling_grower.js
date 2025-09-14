//if the player is holding bone meal, it has a chance to grow a large sapling structure of that type with the base replacing where the sapling was
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const SaplingGrowComponent = {
  onPlayerInteract(event) {
    const { block, player, dimension } = event;
    const item = player?.getComponent("minecraft:equippable")?.getEquipment("Mainhand");

    if (!item) return;

    function gettypeFromId(blockId) {
      const [, rest] = blockId.split(':');
      const [type] = rest.split('_');
      return type;
    }
    const type = gettypeFromId(block.type.id)

    // Check if the player is holding bone meal
    if (item.typeId === "minecraft:bone_meal") {


        dimension.spawnParticle("minecraft:crop_growth_emitter", block.center());
        dimension.playSound("item.bone_meal.use", block.center());
        item.amount--; // decrement stack
      // 75% chance to just consume bone meal with particles
      if (Math.random() < 0.75) {
        return;
      }

      // 25% chance: to also grow a tree
      switch (type) {
        case "palm":
          const weight=Math.random()
          if(weight<0.15){
            console.log("tree 1")
            dimension.runCommand(`structure load extrabiomes:palm_tree_1 ${block.location.x - 5} ${block.location.y} ${block.location.z - 2}`);
          }
          else if(weight>=0.15&& weight <0.35){
            console.log("tree 2")
            dimension.runCommand(`structure load extrabiomes:palm_tree_2 ${block.location.x - 1} ${block.location.y} ${block.location.z - 1}`);
          }
          else if(weight>=0.35&& weight <0.60){
            console.log("tree 3")
            dimension.runCommand(`structure load extrabiomes:palm_tree_3 ${block.location.x - 4} ${block.location.y} ${block.location.z - 5}`);
          }
          else{
            console.log("tree 4")
            dimension.runCommand(`structure load extrabiomes:palm_tree_4 ${block.location.x - 6} ${block.location.y} ${block.location.z - 4}`);
          }
          break;

        case "sky":
          dimension.runCommand(`structure load extrabiomes:sky_tree ${block.location.x - 2} ${block.location.y} ${block.location.z - 2}`);
          break;

        case "mystic":
          dimension.runCommand(`structure load extrabiomes:mystic_tree ${block.location.x - 4} ${block.location.y} ${block.location.z - 4}`);
          break;
      }
      item.amount--;
    }
  }
};