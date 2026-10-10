# ExtraBiomes 3.1.0 Beta 4
This beta brings the Glacier overhaul, Floating Jungle sky islands, snow drifts and a much more natural Sky City to Bedrock, along with a deeper Netherlands and farmable Dense Cloud.

**World generation changes only appear in newly generated chunks, so create a new world (or explore fresh terrain) to see them.**

# Changes
## Biomes
### Glacier
Bedrock port of the Java Glacier overhaul.
- Geothermal caves: basalt-lined lava pools, lavafalls with a basalt cap and landing pad, magma veins, a blue ice depth layer, and snow and basalt pillars.
- Meltwater and frozen pools underground, plus ice-sealed vault chests with new loot (igloo loot and pottery sherds).
- Surface features: crevasses with snow bridges, meltwater streams ending in plunge pools (some with potent sulfur vents over magma), deep erosion shafts that end in chambers, glacial ponds, gravel moraines and andesite erratics.
- Water is now clear and pale blue with a clearer underwater fog.
- The climate is warmer, so the Glacier rains instead of snowing.
- Snow golems now spawn in the Glacier's caves.
- Added an ambient ice cracking sound.

### Snow Drifts
- Added the full set of Java's snow drifts: cornice ridges, sastrugi fields, crescent dunes, giant swirls, twisting spires, breaking waves, spiral cones and the 40 and 150 block tall spires.
- They generate in the Glacier, Cold Mesa and Taiga Spikes at Java's rarity.
- Added the snow spire summit loot table.

### Floating Jungle
- Added sky islands floating above the biome, in several island and archipelago shapes.
- Added a floating island carrying a vanilla jungle temple, with its traps and loot chests.
- Added rare colossal jungle trees, around 40 blocks wide and 100 to 130 blocks tall, that grow from the island peaks.

### The Netherlands
- The netherrack layer now runs all the way down to bedrock instead of stopping about 10 blocks under the surface.
- Ore veins extend down to the bottom of the world.
- Added underground basalt and blackstone patches and hanging basalt pillars.
- Added lava springs in the netherrack walls that spill out into lavafalls. They start flowing when you get near them.

## Structures
### Sky City
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
- Worms breeding in a composter have a 5 minute cooldown before they can breed again, and show love hearts.
### Jellyfish
- Jellyfish on beaches now only spawn between Y 62 and 64, instead of anywhere on the sand.

## Items
- Replaced the tinted default spawn eggs for the giant tortoise, harpy, hoppleshroom, jellyfish, piranha, puckoo, treefrog and worm with the custom icons from Java.

## Fixes
- Distant terrain no longer has dark, anti-aliased looking outlines around blocks. The block texture atlas now uses the vanilla mipmap and padding settings, so neighbouring textures stop bleeding into each other at range.
- Snow no longer piles up a block above slabs, stairs and trapdoors, leaving floating snow layers over half-height blocks. The sky, gilded sky, mystic, palm and black sandstone variants now let snow pass through them the way the dense cloud blocks do.

## Known issues
- Lava springs and the deep netherrack layer in The Netherlands are new and still being tuned, so their density may change in a later beta.
