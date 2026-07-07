import { BlockPermutation, ItemStack } from "@minecraft/server";

const doorWoodTypes = ['gilded_sky', 'mystic', 'palm', 'sky'];

function getWoodType(id) {
    const [, rest] = id.split(':');
    return doorWoodTypes.find((type) => rest.startsWith(type + '_'));
}
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const DoorOpenComponent = {
    onPlayerInteract(event) {
        const { block } = event
        if (block.hasTag('extrabiomes:door')) {
            if (block.permutation.getState('extrabiomes:is_open') == false) {
                door_open_close(block, true)
                if (!(block.hasTag("extrabiomes:door_top"))) {
                    door_open_close(block.above(1), true)
                }
                if (block.hasTag("extrabiomes:door_top")) {
                    door_open_close(block.below(1), true)
                }
            }
        }

    }
};

export const DoorCloseComponent = {
    onPlayerInteract(event) {
        const { block } = event
        if (block.hasTag('extrabiomes:door')) {
            if (block.permutation.getState('extrabiomes:is_open') == true) {
                door_open_close(block, false)
                if (!(block.hasTag("extrabiomes:door_top"))) {
                    door_open_close(block.above(1), false)
                }
                if (block.hasTag("extrabiomes:door_top")) {
                    door_open_close(block.below(1), false)
                }
            }
        }

    }
};

export const DoorOnPlace = {
    onPlace(event) {
        const { block } = event
        const type = getWoodType(block.typeId);
        switch (block.typeId) {
            case `extrabiomes:${type}_door_bottom`:
                block.above(1).setPermutation(BlockPermutation.resolve(`extrabiomes:${type}_door_top`))
                block.above(1).setPermutation(block.above(1).permutation.withState('minecraft:cardinal_direction', block.permutation.getState('minecraft:cardinal_direction')))
                break;

            case `extrabiomes:${type}_door_bottom_flipped`:

                block.above(1).setPermutation(BlockPermutation.resolve(`extrabiomes:${type}_door_top_flipped`))
                block.above(1).setPermutation(block.above(1).permutation.withState('minecraft:cardinal_direction', block.permutation.getState('minecraft:cardinal_direction')))
                break;
        }
    }
}

function door_open_close(block, istrue) {
    block.setPermutation(block.permutation.withState('extrabiomes:is_open', istrue))
    if (!(block.hasTag('extrabiomes:door_top')) && istrue == true) {
        block.dimension.playSound('open.wooden_door', block.center())
    }
    if (!(block.hasTag('extrabiomes:door_top')) && istrue == false) {
        block.dimension.playSound('close.wooden_door', block.center())
    }

}

export const ResetTop = {
    onPlayerBreak(event) {
        const { block, brokenBlockPermutation} = event
        block.above(1).setPermutation(BlockPermutation.resolve('minecraft:air'))
        const type = getTypeFromId(brokenBlockPermutation.type.id)
        block.dimension.spawnItem(new ItemStack(`extrabiomes:${type}_door`, 1), block.center());
    }
}


export const ResetBottom = {
    onPlayerBreak(event) {
        const { block, brokenBlockPermutation} = event
        block.below(1).setPermutation(BlockPermutation.resolve('minecraft:air'))
        const type = getTypeFromId(brokenBlockPermutation.type.id)
        block.dimension.spawnItem(new ItemStack(`extrabiomes:${type}_door`, 1), block.center());
    }
};


function getTypeFromId(blockId) {
    return getWoodType(blockId);
}