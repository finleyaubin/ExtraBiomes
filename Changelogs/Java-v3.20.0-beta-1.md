# ExtraBiomes for Java Edition v3.20.0 Beta 1
The first beta of the next big update! This one brings a brand new Larch Forest biome with its own wood set, a reworked Desert Bryce, Welsh translations, and a round of mob fixes to get closer to the Bedrock version :)
# Changes

## World Generation
### Larch Forest
- New biome: the **Larch Forest**. It generates in the frozen TerraBlender region, on the same cold climate as vanilla Groves, but without being frozen.
- Larch trees have their own custom shape: a pointed spire on top, branches that widen as you go down, and drooping tips. There are tall and young variants too.
- Larch leaves change colour depending on where they are, from autumn gold to summer green, so a forest blends from one colour into the other.
- The forest mixes larches with spruce and pine, opens up into meadow clearings, and is scattered with young conifers, dry grass, mossy rocks, snow patches and patches of podzol and coarse dirt.
- Features you'd find left behind by a glacier: big erratic boulders and meltwater erosion.

### Desert Bryce
- Desert Bryce has been reworked. The pillars are now smooth sandstone with occasional bands of sandstone, dripstone and white terracotta, a sand cap on top, and they're taller.
- The biome now has the vanilla desert mobs, desert music and a dusty fog.
- The ground has patches of packed mud and exposed sandstone, and pointed dripstone spikes grow out of the packed mud.

## Blocks
### Larch Wood
- A full new wood set for larch: logs, wood, stripped logs and wood, planks, stairs, slabs, fences, gates, doors, trapdoors, buttons, pressure plates, signs, hanging signs, boats, chest boats, and larch leaves and saplings so you can grow your own.

### Textures
- Sky and Gilded Sky wood textures have more contrast so the grain stands out better.

## Mobs
### Treefrogs
- Treefrogs now swim properly with a breaststroke, surging forward each time they kick, instead of leaping off the surface of the water.
- Treefrogs can now hop out of water, rather than bobbing in place.
- The jump animation now plays at the same speed as on Bedrock. Before, the legs barely moved lol

### Jellyfish
- Jellyfish now have a 30 second cooldown after being milked, matching Bedrock.

### Worms
- Updated the worm breeding easter egg... I'll let you find out what it does ;)

## Food
- Food stats now match the Bedrock version:
  - Frog's Legs and Cooked Frog's Legs are a bit more filling.
  - Piranha and Cooked Piranha restore more hunger.
  - Jellyfish Jam is now a filling drink, and it **cures** poison instead of giving it to you.

## Languages
- Added Welsh (Cymraeg)!
- Missing translations have been filled in for every language.
- British English now uses British spellings, and the unused `en_uk` file has been removed (Minecraft doesn't have that language).

## Fixes
- Fixed a bug in door generation that could make door textures appear inverted.
- Using a bottle on a jellyfish during its milking cooldown no longer fills the bottle with water.

## Regression Tests
- The treefrog swim gametest now checks that the frog paddles to the bank without leaping off the water.
- Fixed the glacier gametest so it still finds the glacier now that Larch Forests can push it onto a mountaintop.
