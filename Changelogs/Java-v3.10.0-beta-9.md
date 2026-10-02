# ExtraBiomes for Java Edition v3.10.0 Beta 9
# Changes
## World Generation
- Moorlands now grow short and tall dry grass instead of dead bushes, with denser double tall grass on versions that support it.
- Deep Dark Forest and other biomes' fog distances now match Bedrock on the versions that support it, and the deepdark forest is now limited too the surface and ~30 blocks above to stop the caves below being full of sculk.
- Fixed a crash caused by feature ordering conflicts multiple other popular world gen mods across different versions as Identified by https://github.com/finleyaubin/ExtraBiomes/tree/compat-grid.
### Floating Jungle
The Floating Jungle Has had a massive overhaul!
<img width="100%" alt="Floating Jungle" src="https://github.com/user-attachments/assets/f7444b19-aa3d-4de9-b55b-48d787fe475e" />

- Sky islands now hang 28 to 60 blocks above the mountain peaks: grass and moss tops over stone cones, with jungle trees, vines and roots trailing from their undersides and flat-bottomed wisps of dense cloud drifting below. Some have a waterfall spilling off the rim.
- Archipelagos join a large island to smaller ones with giant log canopy bridges, and the rarest has an overgrown ruin around a chest of loot.
- A rare island carries a vanilla jungle temple.
- Colossal jungle trees rise from the peaks, about ten times the height of a normal jungle tree at 100 to 130 blocks. Each has a trunk around 20 blocks thick, buttress roots, sweeping branches, a layered crown, moss, azalea leaves and hanging vines. They are very rare, and do not generate where the peak leaves no room below the build limit.
### Glacier
Glacier gets a full overhaul, above and below the ice.
<img width="100%" alt="Glacial Caves at lava level" src="https://github.com/user-attachments/assets/1db2ae89-0cd4-4393-9578-3dab3a70295e" />

#### Underground
<img width="100%" alt="Meltwater Waterfall" src="https://github.com/user-attachments/assets/7f8355f3-beba-4f78-9150-6da530f54578" />

- Glacier caves are now geothermal. Every pool of lava is lined in basalt, and lava drips from cave ceilings in lavafalls wrapped in a wide shell of basalt that keeps nearby ice from melting into cobblestone.
- Magma veins glow through the ice, and the ice turns to deep blue ice further down.
- Chunky snow pillars rise from cave floors and basalt pillars hang from the ceilings.
- Small meltwater pools sit in the caves, and cave water freezes over into underground ice lakes.
- Chests are sealed inside blocks of regular ice. They use the igloo loot table plus one or two pottery sherds.
- Snow golems now spawn deep underground in the Glacier, at least 8 blocks below the surface, with no more than three near any one player.
- Ice cracks and groans in the Glacier as an ambient sound (sources in credits.md).

#### Surface
<img width="100%" alt="Glacial Surface with geyser in the corner" src="https://github.com/user-attachments/assets/1986539a-8b58-496c-b756-6c219d265fae" />

- Crevasses cut into the ice, with snow bridges across some of them.
- Meltwater streams run downhill across the glacier. Most end in a plunge pool, and around half of those have a geysers using the new potent sulfur (only on 1.26.2 and up).
- Some streams sink into a wide shaft that bores down through the ice. Where it meets hard rock it turns and follows the rock sideways, dropping again where it can, and it ends in a large open chamber. Shafts and tunnels are 5 to 11 blocks wide and chambers up to about 19.
- Glacial ponds, gravel moraine and andesite boulders dot the surface.
- Glacier water is now a pristine, clearer pale blue. The biome is slightly warmer, so surface water no longer freezes. I side effect of this is that It rains instead of snows, ice and snow blocks do not melt at this temperature.



### Sky City
The clouds under the Sky City are redone so they look a bit more natural instead of grid like The city on top is unchanged. 
<img  width="100%" alt="Sky city from below" src="https://github.com/user-attachments/assets/2e125206-e1d3-4f3c-93d6-d423428bd9dd" />

- The island the city sits on has a lumpy, irregular outline and an underbelly of hanging cloud pouches instead of a smooth stepped cone.
- Clouds under each street and building now hang as rounded lobes, and the gaps between paths are filled with organic oval slabs in several sizes instead of identical squares.
- Larger clouds sit off to one side of the city instead of stacking up directly underneath it, with smaller cloudlets trailing off below and beside them so the clouds thin out gradually.
- Sky trees now grow on some of the flat cloud in the gaps between paths and on the smaller flat-topped cloudlets.
- The new Dense Cloud slabs and stairs round off the curves of the clouds, so their undersides and edges are smoother.
- Cloud edges are fuzzier and some clouds hang thin threads of cloud from their undersides. A few clouds trail rain streaks, and long low cloud banks stretch out far from the city, some with trees of their own.
- Sky trees come in three shapes now (the original, a tall spruce-like one and a broad round one), some with a gilded trunk, with saplings at their feet.
- The fountain's water still pours through the island and falls to the ground, so you can swim up into the city.

## Blocks
<img width="100%" alt="New translucent dense clouds" src="https://github.com/user-attachments/assets/07565cd1-c8c7-4d0c-b680-15883b413329" />

- Dense Cloud and Dense Cloud Brick (and the brick slab and stairs) are now slightly translucent.
- Added Dense Cloud Slab and Dense Cloud Stairs, crafted from Dense Cloud like the brick versions. They break as easily as Dense Cloud and give the same floaty slow fall effect when you drop onto them.
  


## Mobs
- Puckoos can now have saddles equipped again on newer versions.
  <img  width="100%" alt="Saddled Puckoos" src="https://github.com/user-attachments/assets/10488ce4-9c84-419a-b18c-ef23c84210bd" />


## Misc
- The licence has been updated from MIT to GPL-3.0.
### Minecraft versions
- Added support for Minecraft 1.21.8, 1.21.10, 1.21.11, 26.1.2, 26.2 and 26.3 meaning that the mod now supports all versions from 1.20.1 (The latest version when I started development) all the way up too the current game release :D
## Beta status
Still a beta release - the Java port remains newer and less battle-tested than the Bedrock addon, so please keep reporting anything that looks wrong on the GitHub issue tracker, however I think that extrabiomes is nearing the first full Java release.
