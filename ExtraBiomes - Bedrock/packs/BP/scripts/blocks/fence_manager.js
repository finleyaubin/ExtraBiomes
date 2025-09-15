const fenceTag = 'extrabiomes:fence'

export class fence_Manager {
    static update_Fence_States(Fence) {
        let north = undefined;
        try {
            north = Fence.north(1);
        } catch { }
        let south = undefined;
        try {
            south = Fence.south(1);
        } catch { }
        let east = undefined;
        try {
            east = Fence.east(1);
        } catch { }
        let west = undefined;
        try {
            west = Fence.west(1);
        } catch { }
        const blocks = [
            { block: north, side: "north" },
            { block: south, side: "south" },
            { block: east, side: "east" },
            { block: west, side: "west" },
        ];
        for (const blockData of blocks) {
            if (blockData.block != undefined) {
                if (!(blockData.block.isLiquid || blockData.block.isAir)) {
                    Fence.setPermutation(Fence.permutation.withState("extrabiomes:" + blockData.side, true));
                } else {
                    Fence.setPermutation(Fence.permutation.withState("extrabiomes:" + blockData.side, false));
                }
            } else {
                Fence.setPermutation(Fence.permutation.withState("extrabiomes:" + blockData.side, false));
            }
        }
    }
    static updateFencesAround(Block) {
        let north = undefined;
        try {
            north = Block.north(1);
        } catch { }
        let south = undefined;
        try {
            south = Block.south(1);2
        } catch { }
        let east = undefined;
        try {
            east = Block.east(1);
        } catch { }
        let west = undefined;
        try {
            west = Block.west(1);
        } catch { }
        const blocks = [Block, north, south, east, west];
        for (const block of blocks) {
            if (block != undefined) {
                if (block.hasTag(fenceTag)) this.update_Fence_States(block);
            }
        }
    }
}