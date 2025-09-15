import {world} from "@minecraft/server";

world.afterEvents.playerPlaceBlock.subscribe((event) => {
    const block = event.block
    if (block.hasTag('extrabiomes:stairs')) {
        update_stairs_around(block)
    }

})

function update_stairs_around(block) {

    update_stair(block)
    if (block.north(1).hasTag("extrabiomes:stairs")) {
        update_stair(block.north(1))
    }
    if (block.south(1).hasTag("extrabiomes:stairs")) {
        update_stair(block.south(1))
    }
    if (block.east(1).hasTag("extrabiomes:stairs")) {
        update_stair(block.east(1))
    }
    if (block.west(1).hasTag("extrabiomes:stairs")) {
        update_stair(block.west(1))
    }
}

function update_stair(block) {
    //Inner Corners
    if (block.north(1).hasTag("extrabiomes:stairs_east") && !(block.north(1).hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_north")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 6))

        }
    }
    if (block.north(1).hasTag("extrabiomes:stairs_west") && !(block.north(1).hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_north")) {

            block.setPermutation(block.permutation.withState('extrabiomes:direction', 7))
        }
    }
    if (block.east(1).hasTag("extrabiomes:stairs_south") && !(block.north(1).hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_east")) {

            block.setPermutation(block.permutation.withState('extrabiomes:direction', 4))
        }
    }

    if (block.east(1).hasTag("extrabiomes:stairs_south") && !(block.east(1).hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_east")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 4))

        }
    }
    if (block.east(1).hasTag("extrabiomes:stairs_north") && !(block.east(1).hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_east")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 6))

        }
    }
    if (block.west(1).hasTag("extrabiomes:stairs_north") && !(block.west(1).hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_west")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 7))

        }
    }
    if (block.west(1).hasTag("extrabiomes:stairs_south") && !(block.west(1).hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_west")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 5))

        }
    }
    if (block.south(1).hasTag("extrabiomes:stairs_west") && !(block.south(1).hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_south")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 5))

        }
    }
    if (block.south(1).hasTag("extrabiomes:stairs_east") && !(block.south(1).hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_south")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 4))

        }
    }
    //Extra
    if (block.south(1).hasTag("extrabiomes:stairs_east") && !(block.south(1).hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_south")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 4))

        }
    }
    //Inner Corners

    if (block.north(1).hasTag("extrabiomes:stairs_east")) {
        if (block.hasTag("extrabiomes:stairs_south")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 8))

        }
    }
    if (block.south(1).hasTag("extrabiomes:stairs_east")) {
        if (block.hasTag("extrabiomes:stairs_north")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 11))

        }
    }
    if (block.west(1).hasTag("extrabiomes:stairs_north")) {
        if (block.hasTag("extrabiomes:stairs_east")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 11))

        }
    }
    if (block.south(1).hasTag("extrabiomes:stairs_west")) {
        if (block.hasTag("extrabiomes:stairs_north")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 10))

        }
    }
    if (block.east(1).hasTag("extrabiomes:stairs_south")) {
        if (block.hasTag("extrabiomes:stairs_west")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 9))

        }
    }
}