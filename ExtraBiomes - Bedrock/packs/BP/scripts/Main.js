//apparently not possible in current version :c
/* // Import world system and player component from "@minecraft/server"
import { world, system } from '@minecraft/server';

// Subscribe to an event that calls every Minecraft tick
system.runInterval(() => {
    // Get all online players
    const players = world.getPlayers();

    // Loop through players
    for (const player of players) {
		const equipment = player.getComponent("equipment_inventory");
        const head_equipment=equipment.getEquipmentSlot("head");
		world.sendMessage(head_equipment);//testing 
        // Check if the head equipment is a frog helmet
        if (head_equipment === 'extrabiomes:frog_helmet')()=> {
			// Apply the jump boost effect and water breathing effects
			(async () => {
				await world.getDimension("overworld").runCommandAsync(`effect ${player.getName()} jump_boost 15 1 true`);
				await world.getDimension("overworld").runCommandAsync(`effect ${player.getName()} water_breathing 15 1 true`);

			
				world.sendMessage("Ah Victory");
			})();
            
        }
    }
},300);//runs every 15 seconds */

