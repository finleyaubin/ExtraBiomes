import { world } from "@minecraft/server";
import { wall_Manager } from "../wall_manager";
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const wall = {
    onPlace(event) {
        const block = event.block
        block.setPermutation(block.permutation.withState('extrabiomes:on_placed', true))
    },
};

world.afterEvents.playerBreakBlock.subscribe((data) => {
    wall_Manager.updateWallsAround(data.block)
})
world.afterEvents.playerPlaceBlock.subscribe((data) => {
    wall_Manager.updateWallsAround(data.block)
})
