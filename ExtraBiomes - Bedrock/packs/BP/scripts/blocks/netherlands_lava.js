import { world, system, BlockVolume } from "@minecraft/server";

// Lava placed by world generation is never scheduled to flow, so springs in the Netherlands walls sit still.
// Re-placing each exposed source near a player starts the flow.
const NETHERLANDS_BIOMES = new Set(["extrabiomes:the_netherlands", "extrabiomes:the_netherlands_mutated"]);
const LAVA_BLOCKS = ["minecraft:lava", "minecraft:flowing_lava"];
const SCAN_RADIUS = 16;
const SCAN_INTERVAL_TICKS = 40;
const LAVA_CHECKS_PER_TICK = 64;
const MAX_REMEMBERED_SOURCES = 100000;
const NEIGHBOUR_OFFSETS = [
  { x: 1, y: 0, z: 0 }, { x: -1, y: 0, z: 0 },
  { x: 0, y: 1, z: 0 }, { x: 0, y: -1, z: 0 },
  { x: 0, y: 0, z: 1 }, { x: 0, y: 0, z: -1 },
];

// Temporary diagnostics: set false once the springs are confirmed flowing.
const DEBUG = false;

const startedSources = new Set();
let scanRunning = false;

function hasAirNeighbour(dimension, pos) {
  return NEIGHBOUR_OFFSETS.some((offset) =>
    dimension.getBlock({ x: pos.x + offset.x, y: pos.y + offset.y, z: pos.z + offset.z })?.isAir);
}

function startFlow(dimension, pos) {
  dimension.setBlockType(pos, "minecraft:air");
  dimension.setBlockType(pos, "minecraft:flowing_lava");
}

function* scanJob() {
  let started = 0;
  let checked = 0;
  for (const player of world.getAllPlayers()) {
    const dimension = player.dimension;
    const { x, y, z } = player.location;
    const from = { x: Math.floor(x) - SCAN_RADIUS, y: Math.max(dimension.heightRange.min, Math.floor(y) - SCAN_RADIUS), z: Math.floor(z) - SCAN_RADIUS };
    const to = { x: Math.floor(x) + SCAN_RADIUS, y: Math.min(dimension.heightRange.max - 1, Math.floor(y) + SCAN_RADIUS), z: Math.floor(z) + SCAN_RADIUS };
    const lava = [...dimension.getBlocks(new BlockVolume(from, to), { includeTypes: LAVA_BLOCKS }, true).getBlockLocationIterator()];
    for (const pos of lava) {
      const key = `${dimension.id}:${pos.x},${pos.y},${pos.z}`;
      if (!startedSources.has(key)) {
        const block = dimension.getBlock(pos);
        const isSource = block?.permutation.getState("liquid_depth") === 0;
        if (isSource && hasAirNeighbour(dimension, pos) && NETHERLANDS_BIOMES.has(dimension.getBiome(pos).id)) {
          startFlow(dimension, pos);
          startedSources.add(key);
          started++;
        }
      }
      if (++checked % LAVA_CHECKS_PER_TICK === 0) yield;
    }
  }
  if (startedSources.size > MAX_REMEMBERED_SOURCES) startedSources.clear();
  if (DEBUG && started > 0) console.warn(`[NetherlandsLava] started flow on ${started} lava source(s), ${checked} lava block(s) checked`);
}

system.runInterval(() => {
  if (scanRunning) return;
  scanRunning = true;
  system.runJob((function* () {
    try {
      yield* scanJob();
    } finally {
      scanRunning = false;
    }
  })());
}, SCAN_INTERVAL_TICKS);
