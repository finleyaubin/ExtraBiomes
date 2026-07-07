import { BlockPermutation } from "@minecraft/server";

// Known wood-type prefixes, longest-first so multi-word types (e.g. "gilded_sky")
// are matched before a shorter substring type (e.g. "sky") could match instead.
const woodTypes = ['gilded_sky', 'mystic', 'palm', 'sky'].sort((a, b) => b.length - a.length);

function getWoodType(id) {
    const [, rest] = id.split(':');
    return woodTypes.find((type) => rest.startsWith(type + '_'));
}

/** @type {import("@minecraft/server").BlockCustomComponent} */
export const StripperComponent = {
    onPlayerInteract(event) {
        const { block, player } = event;
        const item = player?.getComponent("minecraft:equippable")?.getEquipment("Mainhand");
        if (!item || !item.hasTag("minecraft:is_axe")) return;

        const originalTypeId = block.typeId;
        const [, rest] = originalTypeId.split(":");
        const type = getWoodType(originalTypeId); // Get the wood type
        if (!type) return;

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