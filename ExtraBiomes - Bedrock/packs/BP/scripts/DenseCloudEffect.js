import { world, system, BlockVolume } from "@minecraft/server";

const CloudBlocks = ["extrabiomes:dense_cloud", "extrabiomes:dense_cloud_brick"];

system.runInterval(() => {
  const players = world.getAllPlayers();

  for (let player of players) {
    const velocity = player.getVelocity();  
    const fallSpeed = -velocity.y;           

    if (fallSpeed <= 0.5) continue; 


    // scale distance with fall speed
    const distance = Math.min(40, Math.max(4, Math.floor(fallSpeed * 3)));
    
    const { x, y, z } = player.location;
    if (y>256) continue;  
    const area = new BlockVolume(
      { x, y: y - distance, z },
      { x, y, z }
    );

    const blockArea = Array.from(area.getBlockLocationIterator());

    if (blockArea.some((loc) => CloudBlocks.includes(player.dimension.getBlock(loc)?.typeId))) {
      player.addEffect("slow_falling", 30, {
        amplifier: 1,
        showParticles: false,
      });
    }
  }
}, 1);