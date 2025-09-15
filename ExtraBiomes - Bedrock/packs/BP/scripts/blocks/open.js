/** @type {import("@minecraft/server").BlockCustomComponent} */
export const OpenComponent = {
    onPlayerInteract(event) {
            const {block} = event
            if (block.hasTag('extrabiomes:trapdoor')) {
                if (block.permutation.getState('extrabiomes:is_open') == false) {
                    block.setPermutation(block.permutation.withState('extrabiomes:is_open', true))
                    block.dimension.playSound('open.wooden_trapdoor', block.center())
                }
                else if (block.permutation.getState('extrabiomes:is_open') == true) {
                    block.setPermutation(block.permutation.withState('extrabiomes:is_open', false))
                    block.dimension.playSound('close.wooden_trapdoor', block.center())
                }
            }
            if (block.hasTag('extrabiomes:fence_gate')) {
                if (block.permutation.getState('extrabiomes:is_open') == false) {
                    block.setPermutation(block.permutation.withState('extrabiomes:is_open', true))
                    block.dimension.playSound('open.fence_gate', block.center())
                }
                else if (block.permutation.getState('extrabiomes:is_open') == true) {
                    block.setPermutation(block.permutation.withState('extrabiomes:is_open', false))
                    block.dimension.playSound('close.fence_gate', block.center())
                }
            }
    }
};