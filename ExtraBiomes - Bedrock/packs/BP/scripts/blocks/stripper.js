import { BlockPermutation } from "@minecraft/server";
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const StripperComponent = {
    onPlayerInteract(event) {
        const { block, player } = event;
        const item = player?.getComponent("minecraft:equippable")?.getEquipment("Mainhand");
        if (!item || !item.hasTag("minecraft:is_axe")) return;
        
        const originalTypeId = block.typeId;
        const [, rest] = originalTypeId.split(":");
        const parts = rest.split("_");
        const type = parts[0]; // Get the wood type
        
        let strippedId = "";
        if (rest.includes("wood")) {
            // Handle wood blocks
            strippedId = `extrabiomes:stripped_${type}_wood`;
        } else if (rest.includes("log")) {
            // Handle log blocks
            strippedId = `extrabiomes:stripped_${type}_log`;
        }
        
        if (!strippedId) return; // If we couldn't determine the stripped type
        
        const oldPerm = block.permutation;
        const blockFace = oldPerm.getState("minecraft:block_face");
        const newPerm = BlockPermutation.resolve(strippedId, {
            "minecraft:block_face": blockFace,
        });
        block.setPermutation(newPerm);
        player.dimension.playSound("item.axe.strip", block.center());
    }
};