# ExtraBiomes for Java Edition v3.10.0 Beta 11
Dense Cloud is now farmable, and the Sky City has a new building that shows you how, alongside a round of fixes from playtesting beta 10.
# Changes

## Blocks
### Dense Cloud farming
<!-- screenshot: grown cloud around the blue ice -->

Dense Cloud can now be grown instead of only being mined from Sky Cities. Build a column with magma at the bottom, water on top of it, and a blue ice block above the water. Dense Cloud then buds outward from any Dense Cloud next to the ice, in a flattened cloud shape around it.

- The blue ice must be at Y 200 or higher, around where Sky Cities generate, and at most 20 blocks above the water. The water has to sit directly on a magma block.
- Cloud grows out to 10 blocks sideways and 5 blocks up or down from the ice.
- Cloud only buds from existing Dense Cloud, so place a block of it next to the ice to start the farm. Only full Dense Cloud blocks bud, not the slabs, stairs or bricks.
- Cloud never grows into a space a player or mob is standing in.
- It breaks almost instantly and can be pushed with pistons, so it can be harvested by hand or with a machine.

## World Generation
### Sky City
<!-- screenshot: the Cloud Condenser -->

- New building: the Cloud Condenser. It has a magma-heated water tank with blue ice hung above it on a tall wooden frame, a puff of Dense Cloud already growing around the ice, and a lectern at the entrance with a book that walks through how the farm works. It sits on a trimmed brick plaza with benches, flowers and lantern posts. It generates along the Sky City paths like the other buildings.

## Fixes
- Palm leaves, along with the Mystic and Sky leaves, are now mined quickly with a hoe and with shears. They were missing from the leaves tag, so no tool was effective on them.
- Palm logs and palm wood can now be stripped with an axe.
- Fixed axes not stripping any of the Mystic, Sky, Gilded Sky or Palm logs and wood on the newest NeoForge versions.
- Stripped Palm Wood is now counted as a log, and the palm and gilded sky logs now appear in the log item tags, so they work with recipes and tools that look for logs.
- Pebbles no longer destroy other blocks. Placing one used to wipe out redstone, levers and other small blocks on the ground, and pebbles generating in the world could replace tree trunks. They now only take the place of air, or of lava and water, and a pebble placed in water is waterlogged.
- Jellyfish on beaches now only spawn between Y 62 and 64 and within 5 blocks of water, instead of anywhere on the sand.
- Fixed signs and hanging signs from every wood type rendering with no board on the newest versions, leaving just floating text. They now show the proper sign, with matching textures for each wood.
