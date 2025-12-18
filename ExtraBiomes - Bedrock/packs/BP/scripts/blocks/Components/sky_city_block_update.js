import { BlockVolume, BlockPermutation} from "@minecraft/server";
/** @type {import("@minecraft/server").BlockCustomComponent} */
export const SkyCityBlockUpdateComponent = {
  onRandomTick(event) {
    const { dimension, block } = event;
    dimension.runCommand(`structure load "extrabiomes:sky_city/sky_start" ${block.location.x - 5} ${ block.location.y- 1} ${block.location.z - 5}`)
    dimension.runCommand(`tickingarea add circle ${block.location.x} 222 ${block.location.z} 4 sky_city`)


    dimension.fillBlocks(new BlockVolume({ x: block.location.x - 55, y: block.location.y, z: block.location.z - 55 }, { x: block.location.x + 55, y: block.location.y, z: block.location.z + 55 }), 'minecraft:air',{ BlockFilter:{ includeTypes:['extrabiomes:sky_city_block']}})

    //as multiple commands as too not exceed block fill limit
    dimension.fillBlocks(new BlockVolume({ x: block.location.x - 55, y: block.location.y, z: block.location.z - 55 }, { x: block.location.x + 55, y: block.location.y, z: block.location.z + 55 }), 'minecraft:air', { BlockFilter:{ includeTypes:['extrabiomes:dense_cloud']}})
    dimension.fillBlocks(new BlockVolume({ x: block.location.x - 55, y: block.location.y + 1, z: block.location.z - 55 }, { x: block.location.x + 55, y: block.location.y + 1, z: block.location.z + 55 }), 'minecraft:air', { BlockFilter:{ includeTypes:['extrabiomes:dense_cloud']}})
    dimension.fillBlocks(new BlockVolume({ x: block.location.x - 55, y: block.location.y + 2, z: block.location.z - 55 }, { x: block.location.x + 55, y: block.location.y + 2, z: block.location.z + 55 }), 'minecraft:air', { BlockFilter:{ includeTypes:['extrabiomes:dense_cloud']}})
    dimension.fillBlocks(new BlockVolume({ x: block.location.x - 55, y: block.location.y + 3, z: block.location.z - 55 }, { x: block.location.x + 55, y: block.location.y + 3, z: block.location.z + 55 }), 'minecraft:air', { BlockFilter:{ includeTypes:['extrabiomes:dense_cloud']}})
    dimension.fillBlocks(new BlockVolume({ x: block.location.x - 55, y: block.location.y + 4, z: block.location.z - 55 }, { x: block.location.x + 55, y: block.location.y + 4, z: block.location.z + 55 }), 'minecraft:air', { BlockFilter:{ includeTypes:['extrabiomes:dense_cloud']}})




    dimension.runCommand(`tickingarea remove sky_city`)
  }
};