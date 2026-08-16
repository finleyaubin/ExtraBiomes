const wallTag = 'extrabiomes:wall'

export class wall_Manager {
    static update_Wall_States(Wall) {
        let north = undefined;
        try {
            north = Wall.north(1);
        } catch { }
        let south = undefined;
        try {
            south = Wall.south(1);
        } catch { }
        let east = undefined;
        try {
            east = Wall.east(1);
        } catch { }
        let west = undefined;
        try {
            west = Wall.west(1);
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
                    Wall.setPermutation(Wall.permutation.withState("extrabiomes:" + blockData.side, true));
                } else {
                    Wall.setPermutation(Wall.permutation.withState("extrabiomes:" + blockData.side, false));
                }
            } else {
                Wall.setPermutation(Wall.permutation.withState("extrabiomes:" + blockData.side, false));
            }
        }
    }
    static updateWallsAround(Block) {
        let north = undefined;
        try {
            north = Block.north(1);
        } catch { }
        let south = undefined;
        try {
            south = Block.south(1);
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
                if (block.hasTag(wallTag)) this.update_Wall_States(block);
            }
        }
    }
}
