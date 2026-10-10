import { world, system, BlockVolume, MolangVariableMap } from "@minecraft/server";

// Dense cloud buds outward around blue ice that sits above water heated by magma, at sky city altitude or higher.
// Constants mirror the Java DenseCloudBudding so both editions grow the same cloud shape.
const CLOUD_HORIZONTAL_RADIUS = 10;
const CLOUD_VERTICAL_RADIUS = 5;
const MAX_ICE_HEIGHT_ABOVE_WATER = 20;
const MIN_ICE_Y = 200;
const SCAN_RADIUS = 16;
// The farm is found from its magma, which is rare near sky altitude, rather than from blue ice, which fills the Glacier.
// Java accepts a water column of any depth; here the magma must be within this many blocks below the water's top.
const MAX_HEATED_WATER_DEPTH = 16;
const SCAN_INTERVAL_TICKS = 40;
const MAGMA_CHECKS_PER_TICK = 128;
const GROWTH_INTERVAL_TICKS = 40;
const BUD_ATTEMPTS_PER_GROWTH = 6;
const STEAM_INTERVAL_TICKS = 2;
const STEAM_BILLOW_PUFFS = 3;
const STEAM_PARTICLE = "extrabiomes:steam";
const DENSE_CLOUD = "extrabiomes:dense_cloud";
const FARM_DIMENSIONS = new Set(["minecraft:overworld", "minecraft:the_end"]);
// Water sitting on magma becomes a bubble column in Bedrock, which Java still counts as water.
const WATER_BLOCKS = new Set(["minecraft:water", "minecraft:flowing_water", "minecraft:bubble_column"]);
const NEIGHBOUR_OFFSETS = [
  { x: 1, y: 0, z: 0 }, { x: -1, y: 0, z: 0 },
  { x: 0, y: 1, z: 0 }, { x: 0, y: -1, z: 0 },
  { x: 0, y: 0, z: 1 }, { x: 0, y: 0, z: -1 },
];

// Temporary diagnostics: set false once the farm is confirmed working.
const DEBUG = false;
const DEBUG_EVERY_NTH_RUN = 5;
const runCounts = {};

let plumes = [];

function debugRun(key) {
  runCounts[key] = (runCounts[key] ?? 0) + 1;
  return DEBUG && runCounts[key] % DEBUG_EVERY_NTH_RUN === 1;
}

function debug(message) {
  console.warn(`[DenseCloudFarm] ${message}`);
}

function at(pos) {
  return `${pos.x},${pos.y},${pos.z}`;
}

function inCloudShape(dx, dy, dz) {
  const h = CLOUD_HORIZONTAL_RADIUS * CLOUD_HORIZONTAL_RADIUS;
  const v = CLOUD_VERTICAL_RADIUS * CLOUD_VERTICAL_RADIUS;
  return dx * dx * v + dy * dy * h + dz * dz * v <= h * v;
}

function blocksOfType(dimension, from, to, typeId) {
  const volume = new BlockVolume(from, to);
  return [...dimension.getBlocks(volume, { includeTypes: [typeId] }, true).getBlockLocationIterator()];
}

// Walks up from the magma through its water to the first blue ice, which only air or dense cloud may separate from the water.
function findStackAboveMagma(dimension, magma) {
  const { x, z } = magma;
  let surfaceY = magma.y;
  const above = dimension.getBlock({ x, y: surfaceY + 1, z });
  if (!above) return { reason: "unloaded block above the magma" };
  if (!WATER_BLOCKS.has(above.typeId)) return { reason: `${above.typeId} above the magma is not water` };
  while (WATER_BLOCKS.has(dimension.getBlock({ x, y: surfaceY + 1, z })?.typeId)) surfaceY++;

  let capY;
  for (let height = 1; height <= MAX_ICE_HEIGHT_ABOVE_WATER; height++) {
    const pos = { x, y: surfaceY + height, z };
    const block = dimension.getBlock(pos);
    if (!block) return { reason: "unloaded block above the water" };
    if (block.typeId === "minecraft:blue_ice") {
      if (pos.y < MIN_ICE_Y) return { reason: `blue ice at y=${pos.y} is below y=${MIN_ICE_Y}` };
      return { plume: { dimension, ice: pos, surface: { x, y: surfaceY, z }, capY: capY ?? pos.y } };
    }
    if (block.isAir) continue;
    if (block.typeId !== DENSE_CLOUD) return { reason: `${block.typeId} blocks the way to the ice` };
    capY ??= pos.y;
  }
  return { reason: `no blue ice within ${MAX_ICE_HEIGHT_ABOVE_WATER} blocks above the water` };
}

function playersInScanRange() {
  return world.getAllPlayers()
    .map((player) => ({ name: player.name, dimension: player.dimension, location: player.location }))
    .filter(({ dimension, location }) => FARM_DIMENSIONS.has(dimension.id) && location.y + SCAN_RADIUS >= MIN_ICE_Y);
}

function* scanJob() {
  const log = debugRun("scan");
  const found = new Map();
  const rejections = {};
  let magmaCount = 0;
  let checked = 0;
  for (const { name, dimension, location } of playersInScanRange()) {
    const px = Math.floor(location.x);
    const py = Math.floor(location.y);
    const pz = Math.floor(location.z);
    const lowestIce = Math.max(MIN_ICE_Y, py - SCAN_RADIUS);
    const from = {
      x: px - SCAN_RADIUS,
      y: Math.max(dimension.heightRange.min, lowestIce - MAX_ICE_HEIGHT_ABOVE_WATER - MAX_HEATED_WATER_DEPTH),
      z: pz - SCAN_RADIUS,
    };
    const to = { x: px + SCAN_RADIUS, y: Math.min(dimension.heightRange.max - 1, py + SCAN_RADIUS), z: pz + SCAN_RADIUS };
    const magmas = blocksOfType(dimension, from, to, "minecraft:magma");
    magmaCount += magmas.length;
    if (log) debug(`${name} at ${at({ x: px, y: py, z: pz })}: ${magmas.length} magma in y ${from.y}..${to.y}`);
    for (const magma of magmas) {
      const { plume, reason } = findStackAboveMagma(dimension, magma);
      if (plume) {
        found.set(`${dimension.id}:${at(plume.ice)}`, plume);
        if (log) debug(`valid stack: magma ${at(magma)}, water surface y=${plume.surface.y}, ice ${at(plume.ice)}`);
      } else {
        rejections[reason] = (rejections[reason] ?? 0) + 1;
      }
      if (++checked % MAGMA_CHECKS_PER_TICK === 0) yield;
    }
  }
  plumes = [...found.values()];
  if (log) debug(`scan: ${magmaCount} magma, ${plumes.length} active plume(s), rejections ${JSON.stringify(rejections)}`);
}

let scanRunning = false;
function startScan() {
  if (scanRunning) return;
  scanRunning = true;
  system.runJob((function* () {
    try {
      yield* scanJob();
    } finally {
      scanRunning = false;
    }
  })());
}

function isOccupiedByCreature(dimension, target) {
  const center = { x: target.x + 0.5, y: target.y + 0.5, z: target.z + 0.5 };
  return dimension.getEntities({ location: center, maxDistance: 1 })
    .some((entity) => entity.typeId === "minecraft:player" || entity.getComponent("minecraft:health"));
}

// Cloud only buds from existing cloud, into air inside this ice's cloud shape.
function growCloud({ dimension, ice }) {
  const log = debugRun("grow");
  const from = { x: ice.x - CLOUD_HORIZONTAL_RADIUS, y: ice.y - CLOUD_VERTICAL_RADIUS, z: ice.z - CLOUD_HORIZONTAL_RADIUS };
  const to = { x: ice.x + CLOUD_HORIZONTAL_RADIUS, y: ice.y + CLOUD_VERTICAL_RADIUS, z: ice.z + CLOUD_HORIZONTAL_RADIUS };
  const clouds = blocksOfType(dimension, from, to, DENSE_CLOUD);
  if (clouds.length === 0) {
    if (log) debug(`ice ${at(ice)}: no dense cloud within reach to bud from, place one next to the ice`);
    return;
  }

  const tally = { placed: 0, outsideShape: 0, notAir: 0, occupied: 0 };
  for (let attempt = 0; attempt < BUD_ATTEMPTS_PER_GROWTH; attempt++) {
    const source = clouds[Math.floor(Math.random() * clouds.length)];
    const offset = NEIGHBOUR_OFFSETS[Math.floor(Math.random() * NEIGHBOUR_OFFSETS.length)];
    const target = { x: source.x + offset.x, y: source.y + offset.y, z: source.z + offset.z };
    if (!inCloudShape(target.x - ice.x, target.y - ice.y, target.z - ice.z)) { tally.outsideShape++; continue; }
    if (!dimension.getBlock(target)?.isAir) { tally.notAir++; continue; }
    if (isOccupiedByCreature(dimension, target)) { tally.occupied++; continue; }
    dimension.setBlockType(target, DENSE_CLOUD);
    tally.placed++;
  }
  if (log) debug(`ice ${at(ice)}: ${clouds.length} cloud block(s), bud attempts ${JSON.stringify(tally)}`);
}

// One particle call fills the whole column (the particle sizes it from variable.height), then a few puffs billow out under the first block in the way.
function emitSteam({ dimension, surface, capY }) {
  if (debugRun("steam")) debug(`steam from water ${at(surface)} up to y=${capY}`);
  const bottom = surface.y + 1;
  const column = new MolangVariableMap();
  column.setFloat("variable.height", Math.max(capY - bottom, 0));
  dimension.spawnParticle(STEAM_PARTICLE, { x: surface.x + 0.5, y: bottom, z: surface.z + 0.5 }, column);

  for (let i = 0; i < STEAM_BILLOW_PUFFS; i++) {
    const angle = Math.random() * Math.PI * 2;
    const push = 0.4 + Math.random() * 0.8;
    const billow = new MolangVariableMap();
    billow.setFloat("variable.ax", Math.cos(angle) * push);
    billow.setFloat("variable.az", Math.sin(angle) * push);
    dimension.spawnParticle(STEAM_PARTICLE, {
      x: surface.x + 0.5 + (Math.random() - 0.5) * 0.3, y: capY - 0.5, z: surface.z + 0.5 + (Math.random() - 0.5) * 0.3,
    }, billow);
  }
}

system.runInterval(startScan, SCAN_INTERVAL_TICKS);
system.runInterval(() => plumes.forEach(growCloud), GROWTH_INTERVAL_TICKS);
system.runInterval(() => plumes.forEach(emitSteam), STEAM_INTERVAL_TICKS);

if (DEBUG) debug(`script loaded, scanning every ${SCAN_INTERVAL_TICKS} ticks for magma-heated blue ice stacks at y>=${MIN_ICE_Y}`);
