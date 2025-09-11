//special thanks to ZoMb1eRaBb1tT for the palm used for this leaf decay. 

import { world, BlockPermutation, ItemStack} from '@minecraft/server';

// Allowed and leaf blocks combined into sets for quick lookups
const allowedBlocksSet = new Set([
  'extrabiomes:palm_log', 'extrabiomes:palm_log_stripped'
]);

const leafBlocksSet = new Set([
  'extrabiomes:palm_leaves'
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
  const newStates = { ...currentStates, 'extrabiomes:persist': persistent };
  const newPermutation = BlockPermutation.resolve(block.typeId, newStates);
  block.setPermutation(newPermutation);

  if (!persistent && !block.permutation.getState('extrabiomes:placed')) {
    const { x, y, z } = block.location;
    try {
      drop_leaf_loot(block);
      block.dimension.runCommand(`/setblock ${x} ${y} ${z} air destroy`);
    } catch (error) {
      console.error("Failed to set block to air:", error);
    }
  }
}

function drop_leaf_loot(block) {
      function getLeafFromId(blockId) {
      const [, rest] = blockId.split(':');
      const [leaf] = rest.split('_');
      return leaf;
    }
    const leaf = getLeafFromId(block.type.id)
    var extraLoot="none";
    switch (leaf){
      case"palm":
        extraLoot="cocoa_beans"
        break;
      case "mystic":
         extraLoot="glow_berries"
         break;
    }
      const weight = Math.random();
      if (weight <= 0.10) {
        block.dimension.spawnItem(new ItemStack(`extrabiomes:${leaf}_sapling`, 1), block.center());
      }
      else if (weight > 0.10 && weight <= 0.20) {
        block.dimension.spawnItem(new ItemStack(`minecraft:stick`, 1), block.center());
      }
      else if (weight <= 0.30 && extraLoot && extraLoot !== "none") {
        block.dimension.spawnItem(new ItemStack(extraLoot, 1), block.center());
      }
}


// Register custom component for leaf decay
export const LeafDecayComponent = {
  // Handle player breaking blocks
  onPlayerBreak(eventData) {
    const { block } = eventData;
    if (allowedBlocksSet.has(block.typeId) || leafBlocksSet.has(block.typeId)) {
      recalculatePersistence(block);
    }
  },

  // Handle player placing blocks
  onPlayerPlace(eventData) {
    const { block } = eventData;
    if (leafBlocksSet.has(block.typeId) && !block.permutation.getState('extrabiomes:placed')) {
      const currentStates = block.permutation.getAllStates();
      const newStates = { ...currentStates, 'extrabiomes:placed': true };
      const newPermutation = BlockPermutation.resolve(block.typeId, newStates);
      block.setPermutation(newPermutation);
    }
  },
    onRandomTick: (e) => {
    const { block } = e;
    if (leafBlocksSet.has(block.typeId)) recalculatePersistence(block);
  },
};
