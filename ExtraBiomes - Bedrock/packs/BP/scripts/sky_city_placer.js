import { world, system } from "@minecraft/server";

const CLOUD_BLOCK = "extrabiomes:dense_cloud";
const CLOUD_Y_MIN = 213;
const CLOUD_Y_MAX = 228;
const CITY_Y = 223;

// Sky city attempts are deduplicated on a CITY_GRID x CITY_GRID block grid.
// One island can span multiple chunks, so this ensures at most one city per island cluster.
const CITY_GRID = 256;

// Fraction of island grid cells that actually get a city placed.
const PLACE_CHANCE = 0.4;

const JIGSAW_POOL  = "extrabiomes:sky_city_connection";
const JIGSAW_TARGET = "extrabiomes:sky_city";
const JIGSAW_DEPTH  = 20;

function cellOrigin(x, z) {
    return {
        x: Math.floor(x / CITY_GRID) * CITY_GRID,
        z: Math.floor(z / CITY_GRID) * CITY_GRID,
    };
}

function cellKey(x, z) {
    const o = cellOrigin(x, z);
    return `eb_sky_city_${o.x}_${o.z}`;
}

// Sample several points near the cell centre to find cloud blocks.
function hasCloudNear(dim, cx, cz) {
    const offsets = [[0,0],[32,0],[-32,0],[0,32],[0,-32],[24,24],[-24,-24]];
    for (const [dx, dz] of offsets) {
        for (let y = CLOUD_Y_MIN; y <= CLOUD_Y_MAX; y++) {
            try {
                const block = dim.getBlock({ x: cx + dx, y, z: cz + dz });
                if (block?.typeId === CLOUD_BLOCK) return true;
            } catch { /* chunk not yet loaded */ }
        }
    }
    return false;
}

// In-memory cache avoids redundant dynamic-property lookups within a session.
const processed = new Set();

system.runInterval(() => {
    for (const player of world.getAllPlayers()) {
        if (player.dimension.id !== "minecraft:overworld") continue;

        const { x, z } = player.location;
        const key = cellKey(x, z);

        if (processed.has(key)) continue;

        // Already handled in a previous session.
        if (world.getDynamicProperty(key) !== undefined) {
            processed.add(key);
            continue;
        }

        // Mark processed immediately so concurrent interval ticks don't duplicate.
        processed.add(key);

        const origin = cellOrigin(x, z);
        const cx = origin.x + CITY_GRID / 2;
        const cz = origin.z + CITY_GRID / 2;

        if (!hasCloudNear(player.dimension, cx, cz)) continue;

        // Persist result so we never attempt this cell again.
        world.setDynamicProperty(key, true);

        if (Math.random() < PLACE_CHANCE) {
            player.dimension
                .runCommandAsync(
                    `place jigsaw ${JIGSAW_POOL} ${JIGSAW_TARGET} ${JIGSAW_DEPTH} ${cx} ${CITY_Y} ${cz}`
                )
                .catch(e => console.warn("[ExtraBiomes] Sky city placement failed:", e));
        }
    }
}, 200); // runs every 200 ticks (~10 s)
