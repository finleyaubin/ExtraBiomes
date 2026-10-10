# ExtraBiomes 3.1.0 Beta 5
Dense Cloud is now farmable on Bedrock, with the Java Sky City building that shows you how.

# Changes
## Structures
### Sky City
- New building: the Cloud Condenser. It has a magma-heated water tank with blue ice hung above it on a tall wooden frame, a puff of dense cloud already growing around the ice, and a lectern at the entrance with a book that walks through how the farm works. It generates along the Sky City paths like the other buildings.

## Blocks
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
- Worms breeding in a composter have a 5 minute cooldown before they can breed again.
- Worms show love hearts when they breed in a composter.
### Jellyfish
- Jellyfish on beaches now only spawn between Y 62 and 64, instead of anywhere on the sand.

## Fixes
- Distant terrain no longer has dark, anti-aliased looking outlines around blocks. The block texture atlas now uses the vanilla mipmap and padding settings, so neighbouring textures stop bleeding into each other at range.
- Snow no longer piles up a block above slabs, stairs and trapdoors, leaving floating snow layers over half-height blocks. The sky, gilded sky, mystic, palm and black sandstone variants now let snow pass through them the way the dense cloud blocks do.
- The Cloud Condenser's corner stairs now face the right way.
- Lava springs in the walls of the underground Netherlands now flow when you get near them, instead of sitting still until something disturbed them.
