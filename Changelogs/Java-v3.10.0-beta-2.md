# ExtraBiomes for Java Edition 3.1.0 Beta 2
# Changes

## Biomes
### The Netherlands
- Fixed the netherrack layer not generating any ore - the ore veins were checking for stone instead of the netherrack the biome actually generates.
- The netherrack layer no longer replaces the entire underground column down to bedrock; it's now capped to a shallow band near the surface, with normal stone (and normal caves) resuming below it.
- Unwatered farmland was slowly reverting to dirt and killing the crops on it, which is what made the wheat fields look patchy over time. Added hidden water sources buried under the farmland to keep it hydrated without any visible ponds.
### Underground Mesa
- Regular vanilla ore (coal, iron, gold, etc.) can now generate alongside the large colored-terracotta veins underground - previously the terracotta banding left ore veins nothing to replace, so mesa/badlands biomes never got any regular ore.
### General
- Every ExtraBiomes biome now has a proper display name instead of a raw translation key.
- Boulders and stick piles now generate using the same tag-based targeting Underground Jungle vegetation already used (plains/forest/jungle biome tags) instead of being hardcoded onto a fixed list of this mod's own biomes - so vanilla Plains/Forest/Jungle, and any other mod's biome carrying those tags, get them too.

## Mobs
- Puckoos are now tameable - getting bucked off while riding one builds temper toward taming, the same as a horse.

## Items & Blocks
- Fixed the worm item's texture (it had a stray extra line baked into the bottom) and gave it a small flipbook animation.
- Worm items can now be right-clicked against a block to place a worm entity, the same way a spawn egg works.
- Fixed the small glow mushroom's item icon, which was missing/broken.
- Pebble and Mossy Pebble blocks now pick-block to their placeable item form instead of the plain block form, and show a proper name instead of a raw key.
- Fixed Stick Pile rotating onto the wrong axis when placed.
- The 9 huge mushroom blocks (blue, purple, yellow, green, cyan, white, black, orange, glow) now only drop themselves with Silk Touch; otherwise they drop a small chance of the matching small mushroom, same as vanilla's mushroom blocks.

## Recipes
- Pebbles and Mossy Pebbles are now craftable from Cobblestone and Mossy Cobblestone (and back).
- Stick Piles are now craftable from 9 sticks (and back).
- Diamond and Netherite Razor Feathers now have crafting recipes.
- Cooked Piranha is now craftable via furnace, smoker, and campfire.
- Black Sand can now be crafted by dyeing a batch of sand black.

## Achievements
- Added a full advancement tree: taming a Puckoo, obtaining a Worm, collecting a Bucket of Goo, crafting Mystic Planks, obtaining a Frog Helmet, eating Cooked Piranha, visiting The Netherlands and mining its diamond ore there, and a challenge advancement for visiting every biome this mod adds.

## Fixed
- Declared Fabric API and Architectury API as required runtime dependencies, so launchers correctly prompt for them instead of players hitting an unexplained crash.

## Beta status
Still a beta release - the Java port remains newer and less battle-tested than the Bedrock addon, so please keep reporting anything that looks wrong on the GitHub issue tracker.
