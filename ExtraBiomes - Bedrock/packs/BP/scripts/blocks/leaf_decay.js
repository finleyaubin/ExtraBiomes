//special thanks to ZoMb1eRaBb1tT for the palm used for this leaf decay. 

import { BlockPermutation} from '@minecraft/server';
import { drop_leaf_loot } from './Components/leaf_loot';

// Allowed and leaf blocks combined into sets for quick lookups
const allowedBlocksSet = new Set([
  'extrabiomes:palm_log', 'extrabiomes:palm_log_stripped', 'extrabiomes:mystic_log', 'extrabiomes:mystic_log_stripped', 'extrabiomes:sky_log', 'extrabiomes:sky_log_stripped'
]);

const leafBlocksSet = new Set([
  'extrabiomes:palm_leaves', 'extrabiomes:mystic_leaves', 'extrabiomes:sky_leaves'
]);

// Check if an allowed block is nearby within a 6-block radius
function isWithinRadiusOfAllowedBlock(block, maxDistance) {
  const { x: startX, y: startY, z: startZ } = block.location;
  for (let x = startX - maxDistance; x <= startX + maxDistance; x++) {
    for (let y = startY - maxDistance; y <= startY + maxDistance; y++) {
      for (let z = startZ - maxDistance; z <= startZ + maxDistance; z++) {
        const currentBlock = block.dimension.getBlock({ x, y, z });
        if (currentBlock && allowedBlocksSet.has(currentBlock.typeId)) {
          const distance = Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2) + Math.pow(z - startZ, 2));
          if (distance <= maxDistance) return true;
        }
      }
    }
  }
  return false;
}

// Recalculate persistence for a block based on nearby allowed blocks
function recalculatePersistence(block) {
  const persistent = isWithinRadiusOfAllowedBlock(block, 6);
  const currentStates = block.permutation.getAllStates();
  const newStates = { ...currentStates, 'extrabiomes:persist': persistent ? 1 : 0 };
  try {
    const newPermutation = BlockPermutation.resolve(block.typeId, newStates);
    block.setPermutation(newPermutation);
  } catch (error) {
    console.error("Failed to resolve/set persistence permutation:", error);
  }

  if (!persistent && !block.permutation.getState('extrabiomes:placed')) {
    try {
      drop_leaf_loot(block, false, block);
      block.setType("air");
    } catch (error) {
      console.error("Failed to set block to air:", error);
    }
  }
}




// Register custom component for leaf decay
export const LeafDecay = {
  // Handle player breaking blocks
  onPlayerBreak(event) {
    const { block } = event;
    if (allowedBlocksSet.has(block.typeId) || leafBlocksSet.has(block.typeId)) {
      recalculatePersistence(block);
    }
  },

  // Handle player placing blocks
onPlace(event) {
  const { block } = event;
  if (leafBlocksSet.has(block.typeId)) {
    // Check if the block has the decay state and it's not 1
    const decayState = block.permutation.getState('extrabiomes:decay');
    
    if (decayState !== 1) {
      const newPermutation = block.permutation.withState('extrabiomes:placed', true);
      block.setPermutation(newPermutation);
    }
  }
},

    onRandomTick: (e) => {
    const { block } = e;
    if (leafBlocksSet.has(block.typeId)) recalculatePersistence(block);
  },
};
