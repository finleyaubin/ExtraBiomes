import { world } from "@minecraft/server";
import { fence_Manager } from "./fence_manager";
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const fence = {
    onPlace(event) {
        const block = event.block
        block.setPermutation(block.permutation.withState('extrabiomes:on_placed', true))
    },
};

world.afterEvents.playerBreakBlock.subscribe((data) => {
    fence_Manager.updateFencesAround(data.block)
})
world.afterEvents.playerPlaceBlock.subscribe((data) => {
    fence_Manager.updateFencesAround(data.block)
})
