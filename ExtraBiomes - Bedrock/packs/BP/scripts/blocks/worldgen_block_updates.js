import { world, system, BlockVolume } from "@minecraft/server";

// Blocks placed by world generation are never scheduled for their first update, so Netherlands springs and
// Glacier lavafalls sit still and Glacier sulfur vents never erupt. Re-placing them near a player starts them.
const LAVA_BIOMES = new Set(["extrabiomes:the_netherlands", "extrabiomes:the_netherlands_mutated", "extrabiomes:glacier"]);
const VENT_BIOMES = new Set(["extrabiomes:glacier"]);
const LAVA_BLOCKS = ["minecraft:lava", "minecraft:flowing_lava"];
const POTENT_SULFUR = "minecraft:potent_sulfur";
const MAGMA = "minecraft:magma";
const SCAN_RADIUS = 16;
const SCAN_INTERVAL_TICKS = 40;
const BLOCKS_PER_TICK = 64;
const MAX_REMEMBERED_BLOCKS = 100000;
const NEIGHBOUR_OFFSETS = [
  { x: 1, y: 0, z: 0 }, { x: -1, y: 0, z: 0 },
  { x: 0, y: 1, z: 0 }, { x: 0, y: -1, z: 0 },
  { x: 0, y: 0, z: 1 }, { x: 0, y: 0, z: -1 },
];

// Temporary diagnostics: set true to log restarted lava sources and sulfur vents.
const DEBUG = false;

const startedBlocks = new Set();
let scanRunning = false;

function hasAirNeighbour(dimension, pos) {
  return NEIGHBOUR_OFFSETS.some((offset) =>
    dimension.getBlock({ x: pos.x + offset.x, y: pos.y + offset.y, z: pos.z + offset.z })?.isAir);
}

function startLavaFlow(dimension, pos) {
  dimension.setBlockType(pos, "minecraft:air");
  dimension.setBlockType(pos, "minecraft:flowing_lava");
}

function restartSulfurVent(dimension, block) {
  const permutation = block.permutation;
  dimension.setBlockType(block.location, "minecraft:air");
  dimension.setBlockPermutation(block.location, permutation);
}

function isExposedLavaSource(dimension, block) {
  return block.permutation.getState("liquid_depth") === 0
    && hasAirNeighbour(dimension, block.location)
    && LAVA_BIOMES.has(dimension.getBiome(block.location).id);
}

function isVentOverMagma(dimension, block) {
  const below = dimension.getBlock({ x: block.location.x, y: block.location.y - 1, z: block.location.z });
  return below?.typeId === MAGMA && VENT_BIOMES.has(dimension.getBiome(block.location).id);
}

function* scanJob() {
  const started = { lava: 0, vents: 0 };
  let checked = 0;
  for (const player of world.getAllPlayers()) {
    const dimension = player.dimension;
    const { x, y, z } = player.location;
    const from = { x: Math.floor(x) - SCAN_RADIUS, y: Math.max(dimension.heightRange.min, Math.floor(y) - SCAN_RADIUS), z: Math.floor(z) - SCAN_RADIUS };
    const to = { x: Math.floor(x) + SCAN_RADIUS, y: Math.min(dimension.heightRange.max - 1, Math.floor(y) + SCAN_RADIUS), z: Math.floor(z) + SCAN_RADIUS };
    const filter = { includeTypes: [...LAVA_BLOCKS, POTENT_SULFUR] };
    const candidates = [...dimension.getBlocks(new BlockVolume(from, to), filter, true).getBlockLocationIterator()];
    for (const pos of candidates) {
      const key = `${dimension.id}:${pos.x},${pos.y},${pos.z}`;
      if (!startedBlocks.has(key)) {
        const block = dimension.getBlock(pos);
        if (block?.typeId === POTENT_SULFUR && isVentOverMagma(dimension, block)) {
          restartSulfurVent(dimension, block);
          startedBlocks.add(key);
          started.vents++;
        } else if (block && LAVA_BLOCKS.includes(block.typeId) && isExposedLavaSource(dimension, block)) {
          startLavaFlow(dimension, pos);
          startedBlocks.add(key);
          started.lava++;
        }
      }
      if (++checked % BLOCKS_PER_TICK === 0) yield;
    }
  }
  if (startedBlocks.size > MAX_REMEMBERED_BLOCKS) startedBlocks.clear();
  if (DEBUG && (started.lava > 0 || started.vents > 0)) {
    console.warn(`[WorldgenBlockUpdates] restarted ${started.lava} lava source(s) and ${started.vents} sulfur vent(s), ${checked} block(s) checked`);
  }
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
