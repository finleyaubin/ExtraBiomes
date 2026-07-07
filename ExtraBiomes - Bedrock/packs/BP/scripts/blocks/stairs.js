import {world} from "@minecraft/server";

world.afterEvents.playerPlaceBlock.subscribe((event) => {
    const block = event.block
    if (block.hasTag('extrabiomes:stairs')) {
        update_stairs_around(block)
    }

})

function update_stairs_around(block) {
    let north = undefined;
    try {
        north = block.north(1);
    } catch { }
    let south = undefined;
    try {
        south = block.south(1);
    } catch { }
    let east = undefined;
    try {
        east = block.east(1);
    } catch { }
    let west = undefined;
    try {
        west = block.west(1);
    } catch { }

    update_stair(block)
    if (north != undefined && north.hasTag("extrabiomes:stairs")) {
        update_stair(north)
    }
    if (south != undefined && south.hasTag("extrabiomes:stairs")) {
        update_stair(south)
    }
    if (east != undefined && east.hasTag("extrabiomes:stairs")) {
        update_stair(east)
    }
    if (west != undefined && west.hasTag("extrabiomes:stairs")) {
        update_stair(west)
    }
}

function update_stair(block) {
    let north = undefined;
    try {
        north = block.north(1);
    } catch { }
    let south = undefined;
    try {
        south = block.south(1);
    } catch { }
    let east = undefined;
    try {
        east = block.east(1);
    } catch { }
    let west = undefined;
    try {
        west = block.west(1);
    } catch { }

    //Inner Corners
    if (north != undefined && north.hasTag("extrabiomes:stairs_east") && !(north.hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_north")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 6))

        }
    }
    if (north != undefined && north.hasTag("extrabiomes:stairs_west") && !(north.hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_north")) {

            block.setPermutation(block.permutation.withState('extrabiomes:direction', 7))
        }
    }
    if (east != undefined && east.hasTag("extrabiomes:stairs_south") && !(east.hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_east")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 4))

        }
    }
    if (east != undefined && east.hasTag("extrabiomes:stairs_north") && !(east.hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_east")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 6))

        }
    }
    if (west != undefined && west.hasTag("extrabiomes:stairs_north") && !(west.hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_west")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 7))

        }
    }
    if (west != undefined && west.hasTag("extrabiomes:stairs_south") && !(west.hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_west")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 5))

        }
    }
    if (south != undefined && south.hasTag("extrabiomes:stairs_west") && !(south.hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_south")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 5))

        }
    }
    if (south != undefined && south.hasTag("extrabiomes:stairs_east") && !(south.hasTag("extrabiomes:stairs_inner"))) {
        if (block.hasTag("extrabiomes:stairs_south")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 4))

        }
    }
    //Inner Corners

    if (north != undefined && north.hasTag("extrabiomes:stairs_east")) {
        if (block.hasTag("extrabiomes:stairs_south")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 8))

        }
    }
    if (south != undefined && south.hasTag("extrabiomes:stairs_east")) {
        if (block.hasTag("extrabiomes:stairs_north")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 11))

        }
    }
    if (west != undefined && west.hasTag("extrabiomes:stairs_north")) {
        if (block.hasTag("extrabiomes:stairs_east")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 11))

        }
    }
    if (south != undefined && south.hasTag("extrabiomes:stairs_west")) {
        if (block.hasTag("extrabiomes:stairs_north")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 10))

        }
    }
    if (east != undefined && east.hasTag("extrabiomes:stairs_south")) {
        if (block.hasTag("extrabiomes:stairs_west")) {
            block.setPermutation(block.permutation.withState('extrabiomes:direction', 9))

        }
    }
}