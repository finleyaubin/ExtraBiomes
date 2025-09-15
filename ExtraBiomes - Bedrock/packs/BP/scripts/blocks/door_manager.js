import { world} from "@minecraft/server";


world.afterEvents.playerPlaceBlock.subscribe((eventData) => {
    const block = eventData.block

    const [, rest] = block.type.id.split(':');
    const [type] = rest.split('_');

    //Door flips for types of block
    door_flip(block, type)
})

function door_flip(block, type) {
    let door = "extrabiomes:" + type + "_door_bottom"
    if (block.typeId == door && ((block.permutation.getState('minecraft:cardinal_direction') == 'south' && block.east(1).hasTag('extrabiomes:door')) || (block.permutation.getState('minecraft:cardinal_direction') == 'east' && block.north(1).hasTag('extrabiomes:door')) || (block.permutation.getState('minecraft:cardinal_direction') == 'north' && block.west(1).hasTag('extrabiomes:door')) || (block.permutation.getState('minecraft:cardinal_direction') == 'west' && block.south(1).hasTag('extrabiomes:door')))) {
        let direction = block.permutation.getState('minecraft:cardinal_direction')
        block.setPermutation(BlockPermutation.resolve("extrabiomes:" + type + "_door_bottom_flipped"))
        block.setPermutation(block.permutation.withState('minecraft:cardinal_direction', direction))
    }
}