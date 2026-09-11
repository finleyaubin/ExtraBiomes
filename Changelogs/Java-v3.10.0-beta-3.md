# ExtraBiomes for Java Edition 3.1.0 Beta 3
# Changes

## Biomes
- Fixed Floating Jungle spawning over flat terrain instead of mountain peaks - its erosion band never actually matched vanilla's real Peaks/Slopes range.
- Fixed a game-crashing "Feature order cycle" that could occur between vanilla Dark Forest and Jungle Marsh once both shared stick pile generation; Dark Forest no longer generates stick piles (every other forest biome still does).
- Rebalanced huge mushroom colors against vanilla's red/brown so modded colors actually show up in Mushroom Fields/Dark Forest instead of reading as barely-there.
- Split the primary TerraBlender region in two and rebalanced region weights, spreading this mod's ~17 biomes across two regions instead of one to fix biome-adjacency generation quirks and rare-biome weighting.
- Fixed the mesa "regular vanilla ore" fix from the last release never actually taking effect - it was written to the wrong (1.21+-style) tag path for this version of Minecraft.
- Made Dense Cloud (and its brick/slab/stairs variants) properly translucent to light instead of fully opaque.
- Stick piles no longer intersect trees/terrain they land inside of, or float over ledges and gaps - placement now checks the whole footprint's floor, not just one sampled point.
- Raised stick pile placement by one block so it no longer sits sunk into the ground.
- Fixed the stick pile item icon rendering with no inventory/GUI framing, and made it mineable with an axe.
- Grand Oasis's palm groves and Oasis puddle placement no longer float above or bury under uneven sand.
- Oasis surface fossils are now more common (1-in-10 instead of 1-in-48).
- The windmill structure no longer floats over uneven terrain.
- Jungle Pillars now caps its stone spires with grass so jungle trees and vegetation can actually grow on top of them, not just on the ground between them.
- Floating Jungle now generates jungle trees, which were missing entirely.

## Mobs
- Fixed piranhas not actually swimming to chase bait - their move-toward call was never picked up by their smooth-swimming AI, so they'd just stare at it instead of closing the distance.
- Piranhas now vary in size (small/normal/large, weighted like Bedrock's spawn table), scaling health, attack damage, and their model/hitbox together instead of every piranha being identical.
- Puckoos can now actually be tamed by feeding them a Mossy Pebble (their real Bedrock taming method) alongside the existing ride-and-buck path.
- Puckoos now have proper ambient, hurt, and death sounds instead of borrowing a chicken's.
- Added a saddle render layer so ridden Puckoos actually show their saddle.
- Fixed several other small AI/model issues found during play testing (tortoise charge, hoppleshroom/treefrog hopping, tortoise shell roll pivot).

## Items & Blocks
- Fixed several wood blocks across the Mystic, Sky, and Palm sets being misnamed "striped" instead of "stripped".
- Gilded Sky wood was missing its Stripped Log and Stripped Wood blocks entirely - added, including their crafting recipes and creative tab entries.
- Fixed block sounds to match the Bedrock reference: leaves no longer use a crystal-chime sound, huge mushroom blocks have a proper sound instead of falling back to wood, and Goo now has a slime squish sound instead of none at all.
- Pebble and Mossy Pebble now have a short throw cooldown so they can't be spammed.
- Bait now has proper gravity when thrown instead of barely falling, and floats up to rest at the water's surface instead of sinking to the bottom - keeping it reachable by piranhas.
- Bait's model no longer renders upside down or offset from its hitbox, its worm animation is slowed down and no longer has all 9 worms wiggling in perfect sync, and its hitbox is now sized to match the model instead of a tiny placeholder box.
- Landed bait is now a proper attackable target with much more health, so it survives as a decoy instead of being an inert prop only piranhas could interact with.
- Declared Fabric API and Architectury API as required runtime dependencies (carried over from Beta 2 for anyone who missed it).

## Fixed
- Several stray/stale code comments cleaned up and dead legacy mesa feature code removed (no gameplay effect).

## Beta status
Still a beta release - the Java port remains newer and less battle-tested than the Bedrock addon, so please keep reporting anything that looks wrong on the GitHub issue tracker.
