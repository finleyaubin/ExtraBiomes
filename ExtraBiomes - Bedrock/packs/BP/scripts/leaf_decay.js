//special thanks to ZoMb1eRaBb1tT for the palm used for this leaf decay. 
import { BlockPermutation, world } from "@minecraft/server";

const MAX_DISTANCE = 6;
const LOG_IDS = new Set([
  "extrabiomes:palm_log",
  "extrabiomes:palm_log_stripped"
]);

// --- Utility: check if any logs are nearby ---
function hasNearbyLog(block) {
  const { x, y, z } = block.location;
  const dim = block.dimension;

  // Cheaper check: scan cube, but break ASAP when log found
  for (let dx = -MAX_DISTANCE; dx <= MAX_DISTANCE; dx++) {
    for (let dy = -MAX_DISTANCE; dy <= MAX_DISTANCE; dy++) {
      for (let dz = -MAX_DISTANCE; dz <= MAX_DISTANCE; dz++) {
        const b = dim.getBlock({ x: x + dx, y: y + dy, z: z + dz });
        if (b && LOG_IDS.has(b.typeId)) {
          return true;
        }
      }
    }
  }
  return false;
}

// --- Update a leaf's decay state ---
function updateLeafDecay(block) {
  const hasLog = hasNearbyLog(block);
  const states = block.permutation.getAllStates();
  const newStates = { ...states, "extrabiomes:can_decay": !hasLog };
  block.setPermutation(BlockPermutation.resolve(block.typeId, newStates));
}

// --- Custom component: only destroy decaying leaves ---
export const LeafDecayComponent = {
  onRandomTick(event) {
    const { block } = event;
    if (!block) return;

    const perm = block.permutation;
    if (perm.getState("extrabiomes:can_decay")) {
      // 20% chance to decay on random tick
      if (Math.random() < 0.2) {
        block.setPermutation(BlockPermutation.resolve("minecraft:air"));
      }
    }
  }
};

// --- Global event hooks to update state only when needed ---
world.afterEvents.playerBreakBlock.subscribe(({ block }) => {
  if (LOG_IDS.has(block.typeId)) {
    // Update nearby leaves after log broken
    const { x, y, z } = block.location;
    const dim = block.dimension;
    for (let dx = -MAX_DISTANCE; dx <= MAX_DISTANCE; dx++) {
      for (let dy = -MAX_DISTANCE; dy <= MAX_DISTANCE; dy++) {
        for (let dz = -MAX_DISTANCE; dz <= MAX_DISTANCE; dz++) {
          const b = dim.getBlock({ x: x + dx, y: y + dy, z: z + dz });
          if (b?.typeId === "extrabiomes:palm_leaves") {
            updateLeafDecay(b);
          }
        }
      }
    }
  }
});