// Dense cloud buds outward around blue ice that sits above water heated by magma, at sky city altitude or higher.
// Constants mirror the Java DenseCloudBudding so both editions grow the same cloud shape.
const CLOUD_HORIZONTAL_RADIUS = 10;
const CLOUD_VERTICAL_RADIUS = 5;
const MAX_ICE_HEIGHT_ABOVE_WATER = 20;
const MIN_ICE_Y = 200;
const BUD_ONE_IN = 4;
const WATER_BLOCKS = new Set(["minecraft:water", "minecraft:flowing_water"]);
const NEIGHBOUR_OFFSETS = [
  { x: 1, y: 0, z: 0 }, { x: -1, y: 0, z: 0 },
  { x: 0, y: 1, z: 0 }, { x: 0, y: -1, z: 0 },
  { x: 0, y: 0, z: 1 }, { x: 0, y: 0, z: -1 },
];

function inCloudShape(dx, dy, dz) {
  const h = CLOUD_HORIZONTAL_RADIUS * CLOUD_HORIZONTAL_RADIUS;
  const v = CLOUD_VERTICAL_RADIUS * CLOUD_VERTICAL_RADIUS;
  return dx * dx * v + dy * dy * h + dz * dz * v <= h * v;
}

function waterEndsOnMagma(dimension, waterPos) {
  let y = waterPos.y;
  while (WATER_BLOCKS.has(dimension.getBlock({ x: waterPos.x, y, z: waterPos.z })?.typeId)) y--;
  return dimension.getBlock({ x: waterPos.x, y, z: waterPos.z })?.typeId === "minecraft:magma";
}

function sitsOverHeatedWater(dimension, icePos) {
  for (let i = 1; i <= MAX_ICE_HEIGHT_ABOVE_WATER; i++) {
    const pos = { x: icePos.x, y: icePos.y - i, z: icePos.z };
    const block = dimension.getBlock(pos);
    if (!block) return false;
    if (WATER_BLOCKS.has(block.typeId)) return waterEndsOnMagma(dimension, pos);
    if (!block.isAir && block.typeId !== "extrabiomes:dense_cloud") return false;
  }
  return false;
}

function canBudAt(dimension, target) {
  if (target.y + CLOUD_VERTICAL_RADIUS < MIN_ICE_Y) return false;
  for (let dx = -CLOUD_HORIZONTAL_RADIUS; dx <= CLOUD_HORIZONTAL_RADIUS; dx++) {
    for (let dz = -CLOUD_HORIZONTAL_RADIUS; dz <= CLOUD_HORIZONTAL_RADIUS; dz++) {
      for (let dy = -CLOUD_VERTICAL_RADIUS; dy <= CLOUD_VERTICAL_RADIUS; dy++) {
        if (!inCloudShape(dx, dy, dz)) continue;
        const icePos = { x: target.x + dx, y: target.y + dy, z: target.z + dz };
        if (icePos.y < MIN_ICE_Y) continue;
        if (dimension.getBlock(icePos)?.typeId === "minecraft:blue_ice" && sitsOverHeatedWater(dimension, icePos)) return true;
      }
    }
  }
  return false;
}

function isOccupiedByCreature(dimension, target) {
  const center = { x: target.x + 0.5, y: target.y + 0.5, z: target.z + 0.5 };
  return dimension.getEntities({ location: center, maxDistance: 1 })
    .some((entity) => entity.typeId === "minecraft:player" || entity.getComponent("minecraft:health"));
}

/** @type {import("@minecraft/server").BlockCustomComponent} */
export const DenseCloudBuddingComponent = {
  onRandomTick({ block, dimension }) {
    if (Math.floor(Math.random() * BUD_ONE_IN) !== 0) return;
    const offset = NEIGHBOUR_OFFSETS[Math.floor(Math.random() * NEIGHBOUR_OFFSETS.length)];
    const target = block.offset(offset);
    if (!target?.isAir || isOccupiedByCreature(dimension, target.location)) return;
    if (canBudAt(dimension, target.location)) target.setPermutation(block.permutation);
  },
};
