import { BlockPermutation } from "@minecraft/server";

/** @type {import("@minecraft/server").BlockCustomComponent} */
export const StripperComponent = {
    onPlayerInteract(event) {
        const { block, player } = event;
        const item = player?.getComponent("minecraft:equippable")?.getEquipment("Mainhand");

        if (!item||!item.hasTag("minecraft:is_axe")) return;
        

        function getTypeFromId(blockId) {
            const [, rest] = blockId.split(":");
            const [type] = rest.split("_");
            return type;
        }
        const type = getTypeFromId(block.typeId);

        const strippedId = `extrabiomes:stripped_${type}_log`;


        const oldPerm = block.permutation;
        const blockFace = oldPerm.getState("minecraft:block_face");

        const newPerm = BlockPermutation.resolve(strippedId, {
            "minecraft:block_face": blockFace,
        });

        block.setPermutation(newPerm);

        player.dimension.playSound("item.axe.strip", block.center());

    }
};