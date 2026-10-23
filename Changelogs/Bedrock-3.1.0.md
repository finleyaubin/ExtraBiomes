# ExtraBiomes 3.1.0
This release brings the Bedrock edition much closer to Java: three new biomes (Deep Dark Forest, Volcanic Moss Tundra and the Glacier), a floating Jungle, a revamped underground Netherlands, snow drifts, a rebuilt Sky City with farmable dense cloud, chest boats, new villages, worm breeding and a lot of fixes.

# Changes
## Biomes
### Deep Dark Forest
<img width="2560" height="1440" alt="image" src="https://github.com/user-attachments/assets/44aa6dba-88e7-4f7a-99b6-d9af62294173" />

- New very rare Overworld surface biome. Once a lovely forest with towering spruce trees and a dark oak canopy, now infested with sculk and a thick black fog.

### Volcanic Moss Tundra
<img width="2563" height="1446" alt="Screenshot 2026-08-15 172925" src="https://github.com/user-attachments/assets/e7717fbe-9306-4d06-af89-e479fb65a9b1" />

A cold, volcanic take on the tundra, inspired by parts of Iceland. Dark volcanic terrain (black sand and black sandstone) rather than snow, with patches of moss and moss carpet blanketing the higher ground. Despite the volcanic theme it keeps tundra-like cold-biome behavior (cold-variant farm animals and frogs spawn here), though snowfall itself is disabled.

The land is shaped by volcanic terrain features (boulders, rock formations, basalt pillars and volcanoes) and cut through by a winding river of lava, bordered by magma banks with clusters of basalt pillars jutting out along its edges.

Moss grows on the higher parts of the biome but not lower down, where it is almost desert-like.

It generates as a rare replacement for ice plains in the Overworld.

### Glacier
<img width="100%" alt="Underground glacier" src="https://github.com/user-attachments/assets/113f5a15-57cf-48d9-ab63-b409d2acc76a" />

Bedrock port of the Java Glacier overhaul.
- Geothermal caves: basalt-lined lava pools, lavafalls with a basalt cap and landing pad, magma veins, a blue ice depth layer, and snow and basalt pillars.
- Meltwater and frozen pools underground, plus ice-sealed vault chests with new loot (igloo loot and pottery sherds).
- Surface features: crevasses with snow bridges, meltwater streams ending in plunge pools (some with potent sulfur vents over magma), deep erosion shafts that end in chambers, glacial ponds, gravel moraines and andesite erratics.
- Water is now clear and pale blue with a clearer underwater fog.
- The climate is warmer, so the Glacier rains instead of snowing. This allows for surface water that would otherwise freeze.
- Snow golems now spawn in the Glacier's caves.
- Added an ambient ice cracking sound.

### Snow Drifts
- Added the full set of Java's snow drifts: cornice ridges, sastrugi fields, crescent dunes, giant swirls, twisting spires, breaking waves, spiral cones and the 40 and 150 block tall spires.
- They generate in the Glacier, Cold Mesa and Taiga Spikes at Java's rarity.
- Added the snow spire summit loot table.

### Floating Jungle
<img width="100%" alt="Floating jungle" src="https://github.com/user-attachments/assets/f1a83fdb-cc1d-4c30-b24c-b70c8ed52158" />

- Added sky islands floating above the biome, in several island and archipelago shapes.
- Added a floating island carrying a vanilla jungle temple, with its traps and loot chests.
- Added rare colossal jungle trees, around 40 blocks wide and 100 to 130 blocks tall, that grow from the island peaks.

### The Netherlands
<img width="100%" alt="the underground netherlands" src="https://github.com/user-attachments/assets/a4561ef9-855a-46a8-99a9-25e2cab0d5ba" />

- The netherrack layer now runs all the way down to bedrock instead of stopping about 10 blocks under the surface.
- The nether ore veins extend down to the bottom of the world.
- Added underground basalt and blackstone patches and hanging basalt pillars (may need some tweaking).
- Added lava springs in the netherrack walls that spill out into lavafalls. They start flowing when you get near them.

## Structures
### Sky City
<img width="100%" alt="Sky City from below" src="https://github.com/user-attachments/assets/072ebfc6-50c7-4963-94fc-a58e4589d389" />

- Rebuilt the cloud generation: clouds are now soft, lumpy and spread out, with rounded hanging undersides and satellite cloudlets, instead of noisy blocks and square domes.
- Added more cloud types: cumulus, streaks, mammatus and tower clouds, low cloud banks and rain-streak clouds.
- Added fuzzy cloud edges, hanging threads, and springs on the island flanks.
- Added more tree shapes in the sky, a gilded trunk variant and saplings at the foot of each tree. Trees now grow on more cloud surfaces, including the sides of satellite clouds.
- Sky tree leaves on satellite clouds now decay when the trunk is cut.
- New building: the Cloud Condenser. It has a magma-heated water tank with blue ice hung above it on a tall wooden frame, a puff of dense cloud already growing around the ice, and a lectern at the entrance with a book that walks through how the farm works. It generates along the Sky City paths like the other buildings.
- New statue piece, matching Java's rarity.

<img width="2560" height="1440" alt="statue" src="https://github.com/user-attachments/assets/d17bf20c-ea41-4df9-a662-bfd26d1f0810" />

### Villages
<img width="2560" height="1440" alt="villages" src="https://github.com/user-attachments/assets/b3924b81-a619-4e55-95bc-052b5c0cc468" />

- Villages can now spawn in Moorlands, Grand Oasis, Deep Dark Forest, Taiga Spikes and Shattered Taiga Spikes.

### Jungle Pillars
- Jungle pillars get weathered variants (baked offline, since Bedrock has no processor hook for this), matching Java's look.

## Blocks
- Added black sand and a full black sandstone block set, including chiseled, cut and smooth variants, along with slabs, stairs and walls, and the recipes/stonecutter recipes to craft them.
- Added a recipe to turn black sand into glass, and to craft TNT from black sand.
- Added heightmaps to the black sand blocks.
- Added dense cloud slabs and stairs, used to round off the Sky City clouds. They give the slow-falling effect like the other dense cloud blocks.
- Dense cloud, dense cloud brick and all of their slabs and stairs are now slightly translucent, and match each other.
- Added crafting recipes for the dense cloud slab and stairs (6 slabs from 3 blocks, 4 stairs from 6) and for the dense cloud brick slab and stairs.
- Added a grass stone recipe (short grass over stone).
- Sized pebble selection/collision boxes for small, medium and large pebbles instead of one size for all.
- Adjusted the nine small mushrooms' selection boxes to match their model instead of a full block footprint.
- Added snow logging for some blocks and the ability to ignore weather for others.
- Fixed the collision boxes and texture UVs on stairs across all types.

### Dense Cloud farming
<img width="100%" alt="Cloud Condenser structure" src="https://github.com/user-attachments/assets/7485706d-bb79-4213-b681-d817241bb0ff" />

- Dense cloud can now be grown instead of only being mined from Sky Cities. Build a column with magma at the bottom, water on top of it, and a blue ice block above the water. Dense cloud then buds outward from any dense cloud next to the ice, in a flattened cloud shape around it.
- The blue ice must be at Y 200 or higher, and at most 20 blocks above the water. The water has to sit directly on a magma block.
- Cloud grows out to 10 blocks sideways and 5 blocks up or down from the ice.
- Cloud only buds from existing dense cloud, so place a block of it next to the ice to start the farm. Only full dense cloud blocks bud, not the slabs, stairs or bricks.
- Cloud never grows into a space a player or mob is standing in.
- Steam rises off the water of any valid stack, whether or not any cloud has been seeded yet, up to the first block in the way.

## Boats
<img width="100%" alt="boats" src="https://github.com/user-attachments/assets/7ed02690-cf66-4fe3-b4b7-28a9a4fab3d4" />

- Added chest boats for all mod wood types, and gilded boats and chest boats too.

## Mobs
### Jellyfish
<img width="2560" height="1440" alt="image" src="https://github.com/user-attachments/assets/3fd8aebb-e097-4ef8-8161-9a49c642172c" />

- Jellyfish now dry up outside of water, and wash up on beach shorelines.
- Jellyfish on beaches only spawn between Y 62 and 64, so they wash up on the water's edge and not halfway up a hill.
- Jellyfish no longer spawn on stony shores.

### Puckoos
- Puckoos now have a full set of ambient and cry sounds, made from distorted peacock recordings.
- Puckoos now actively seek out and path to nearby mossy pebbles before eating them, and eat them reliably once they arrive.

### Worms
- Two worms inside a composter that has compost in it will breed. Each breeding uses one level of compost, and the new worm drops out of the composter.
- Worm items can be put straight into a composter by right-clicking its top.
- Worms breeding in a composter have a 5 minute cooldown before they can breed again, and show love particles above.
- Worms can now be shift-clicked to pick back up as an item, keeping their name, and are placeable from the item to spawn a worm entity.

## Items
<img width="100%" alt="archie worm bait" src="https://github.com/user-attachments/assets/43e2cb2f-b609-4883-b1c2-cf70c259ace7" />

- Bait: health raised to 300 (up from 90), matching Java's durability-300 stacking behavior; fixed a health/damage-bar sync bug where a re-thrown bait's health never matched its remaining durability.
- Fixed pebble placement not properly decrementing the item stack.

<img width="100%" alt="Spawn Eggs" src="https://github.com/user-attachments/assets/7a501ee1-d3cb-469d-bb84-d0c3515022e6" />

- Replaced the spawn eggs for all mobs with custom icons to match the new default behavior.

## Language Support
- Used an LLM to attempt to add translations for a lot of different languages. This may have some issues, so if you find a bug, please report it :)

## Fixes
<img width="100%" alt="Mipmapping bug" src="https://github.com/user-attachments/assets/0112d64c-383e-4508-a144-785e62022018" />

- Fixed a bug that has been around for a while where distant blocks showed weirdly colored outlines. I had assumed this was a quirk of my Linux (I use arch btw) dev setup where I am emulating the Android version of the game, but it turns out I had got the 2 mipmap padding values in the resource pack the wrong way round and it was causing colour bleed lmao.
- Snow no longer piles up a block above slabs, stairs and trapdoors, leaving floating snow layers over half-height blocks. The sky, gilded sky, mystic, palm and black sandstone variants now let snow pass through them the way the dense cloud blocks do.
- Fixed goo's flipbook texture pointing at a stale atlas tile after a texture key rename.
- Normalized every JSON file in the pack to consistent 4-space formatting (no functional changes).

## Known issues
- Vines or sculk can still grow on the sides of pebbles or mushrooms. I tried to fix this but cannot currently find a way to do so.
