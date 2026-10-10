# ExtraBiomes 3.1.0 Beta 4
This beta brings recent Java gameplay changes back to Bedrock. the biggest changes being the floating jungle, glacier and underground netherlands revamp.
also the dense cloud farming mechanics, changes too the sky city's clouds, new spawn eggs and worm breeding.

# Changes
## Biomes
### Glacier
<img width="2560" height="1440" alt="image" src="https://github.com/user-attachments/assets/113f5a15-57cf-48d9-ab63-b409d2acc76a" />

Bedrock port of the Java Glacier overhaul.
- Geothermal caves: basalt-lined lava pools, lavafalls with a basalt cap and landing pad, magma veins, a blue ice depth layer, and snow and basalt pillars.
- Meltwater and frozen pools underground, plus ice-sealed vault chests with new loot (igloo loot and pottery sherds).
- Surface features: crevasses with snow bridges, meltwater streams ending in plunge pools (some with potent sulfur vents over magma), deep erosion shafts that end in chambers, glacial ponds, gravel moraines and andesite erratics.
- Water is now clear and pale blue with a clearer underwater fog.
- The climate is warmer, so the Glacier rains instead of snowing this allows for surface water that would freeze otherwise.
- Snow golems now spawn in the Glacier's caves.
- Added an ambient ice cracking sound.

### Snow Drifts
- Added the full set of Java's snow drifts: cornice ridges, sastrugi fields, crescent dunes, giant swirls, twisting spires, breaking waves, spiral cones and the 40 and 150 block tall spires.
- They generate in the Glacier, Cold Mesa and Taiga Spikes at Java's rarity.
- Added the snow spire summit loot table.

### Floating Jungle
<img width="100%" alt="image" src="https://github.com/user-attachments/assets/f1a83fdb-cc1d-4c30-b24c-b70c8ed52158" />

- Added sky islands floating above the biome, in several island and archipelago shapes.
- Added a floating island carrying a vanilla jungle temple, with its traps and loot chests.
- Added rare colossal jungle trees, around 40 blocks wide and 100 to 130 blocks tall, that grow from the island peaks.

### The Netherlands
<img width="100%" alt="the underground netherlands" src="https://github.com/user-attachments/assets/a4561ef9-855a-46a8-99a9-25e2cab0d5ba" />

- The netherrack layer now runs all the way down to bedrock instead of stopping about 10 blocks under the surface.
- the nether ore veins extend down to the bottom of the world.
- Added underground basalt and blackstone patches and hanging basalt pillars (may need some tweaking).
- Added lava springs in the netherrack walls that spill out into lavafalls. They start flowing when you get near them.

## Structures
### Sky City
<img width="2560" height="1440" alt="image" src="https://github.com/user-attachments/assets/072ebfc6-50c7-4963-94fc-a58e4589d389" />

- Rebuilt the cloud generation: clouds are now soft, lumpy and spread out, with rounded hanging undersides and satellite cloudlets, instead of noisy blocks and square domes.
- Added more cloud types: cumulus, streaks, mammatus and tower clouds, low cloud banks and rain-streak clouds.
- Added fuzzy cloud edges, hanging threads, and springs on the island flanks.
- Added more tree shapes in the sky, a gilded trunk variant and saplings at the foot of each tree. Trees now grow on more cloud surfaces, including the sides of satellite clouds.
- Sky tree leaves on satellite clouds now decay when the trunk is cut.
- New building: the Cloud Condenser. It has a magma-heated water tank with blue ice hung above it on a tall wooden frame, a puff of dense cloud already growing around the ice, and a lectern at the entrance with a book that walks through how the farm works. It generates along the Sky City paths like the other buildings.

## Blocks
- Added dense cloud slabs and stairs, used to round off the Sky City clouds. They give the slow-falling effect like the other dense cloud blocks.
- Dense cloud, dense cloud brick and all of their slabs and stairs are now slightly translucent, and match each other.
- Added crafting recipes for the dense cloud slab and stairs (6 slabs from 3 blocks, 4 stairs from 6) and for the dense cloud brick slab and stairs.
- Added a grass stone recipe (short grass over stone).
- Added heightmaps to the black sand blocks.

### Dense Cloud farming
<img width="100%" alt="Cloud Condenser structure" src="https://github.com/user-attachments/assets/7485706d-bb79-4213-b681-d817241bb0ff" />

- Dense cloud can now be grown instead of only being mined from Sky Cities. Build a column with magma at the bottom, water on top of it, and a blue ice block above the water. Dense cloud then buds outward from any dense cloud next to the ice, in a flattened cloud shape around it.
- The blue ice must be at Y 200 or higher, and at most 20 blocks above the water. The water has to sit directly on a magma block.
- Cloud grows out to 10 blocks sideways and 5 blocks up or down from the ice.
- Cloud only buds from existing dense cloud, so place a block of it next to the ice to start the farm. Only full dense cloud blocks bud, not the slabs, stairs or bricks.
- Cloud never grows into a space a player or mob is standing in.
- Steam rises off the water of any valid stack, whether or not any cloud has been seeded yet, up to the first block in the way.

## Mobs
### Worms
- Two worms inside a composter that has compost in it will breed. Each breeding uses one level of compost, and the new worm drops out of the composter.
- Worm items can be put straight into a composter by right-clicking its top.
- Worms breeding in a composter have a 5 minute cooldown before they can breed again, and show love particals above.
### Jellyfish
- Jellyfish on beaches now only spawn between Y 62 and 64, instead of anywhere on the sand to make sure they only wash up on the waters edge and not half way up a hill.

## Items
<img width="100%" alt="image" src="https://github.com/user-attachments/assets/7a501ee1-d3cb-469d-bb84-d0c3515022e6" />

- Replaced the spawn eggs for all mobs with custom icons to match the new default behavior.

## Fixes
- Fixed a bug that has been around for a while where distant blocks showed weirdly coloured outlines, I had assumed this was a quirk of my linux dev setup where I am emulating the android version of the game, but turns our I had got the 2 mipmap padding values in the resource pack the wrong way round lmao
- Snow no longer piles up a block above slabs, stairs and trapdoors, leaving floating snow layers over half-height blocks. The sky, gilded sky, mystic, palm and black sandstone variants now let snow pass through them the way the dense cloud blocks do.

