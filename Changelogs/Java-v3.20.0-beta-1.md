# ExtraBiomes for Java Edition v3.20.0 Beta 1
The first beta of the next big update! This one brings a brand new Larch Forest biome with its own wood set, shelves for all of extrabiome's wood types, a reworked Desert Bryce, Welsh translations 🏴󠁧󠁢󠁷󠁬󠁳󠁿, and some mob animation fixes to get closer to the Bedrock version :)
# Changes

## World Generation
### Larch Forest
<img width="100%" alt="Larch Forest" src="https://github.com/user-attachments/assets/4f81362d-56f7-42a0-a5ca-6d918fa20420" />

- New biome: the **Larch Forest**. It generates in the frozen TerraBlender region, on the same cold climate as vanilla Groves, but without being frozen.
- Larch trees have their own custom shape: a pointed spire on top, branches that widen as you go down, and drooping tips. There are tall and young variants too.
- Larch leaves change colour depending on where they are. The biome's temperature decides whether they're gold or green, and their position in the world picks the exact shade, so a forest blends from one colour into the other.
- The forest mixes larches with spruce and pine, opens up into meadow clearings, and is scattered with young conifers, dry grass, mossy rocks, snow patches and patches of podzol and coarse dirt.
- Features you'd find left behind by a glacier: big erratic boulders and meltwater erosion.

### Desert Bryce
<img width="100%" alt="Desert Bryce" src="https://github.com/user-attachments/assets/6e75e1ad-5828-4bab-a946-150c39155acd" />

- Desert Bryce has been reworked. The pillars are now smooth sandstone with occasional bands of sandstone, dripstone and white terracotta, a sand cap on top, and they're taller.
- The biome now has the vanilla desert mobs, desert music and a dusty fog.
- The ground has patches of packed mud and exposed sandstone, and pointed dripstone spikes grow out of the packed mud.

## Blocks
### Larch Wood
<img width="100%" alt="Larch wood set" src="https://github.com/user-attachments/assets/81343f3b-f3a7-4434-83ef-a3242b644f40" /> 

- A full new wood set for larch: logs, wood, stripped logs and wood, planks, stairs, slabs, fences, gates, doors, trapdoors, buttons, pressure plates, signs, hanging signs, boats, chest boats, a shelf, and larch leaves and saplings so you can grow your own.
#### Larch Leaves
<img width="50%" alt="Larch Leaves" src="https://github.com/user-attachments/assets/d0944548-0b72-4893-8307-1b169f816c38" />

- larch leaves will change colour based on their position in the world, allowing a range of colours in the larch forest.

### Shelves

<img width="100%" alt="Shelves" src="https://github.com/user-attachments/assets/492dcf03-bcd9-41f8-870a-639d1bdf770c" />

- Added wooden shelves for Mystic, Sky, Palm and Gilded Sky wood. They work just like the vanilla shelves.

### Textures
<img width="100%" alt="Updated sky textures" src="https://github.com/user-attachments/assets/91c04221-474a-4d05-bc35-f6a9d3931116" />

- Sky and Gilded Sky wood textures have more contrast so the grain stands out better.

## Mobs
### Treefrogs
- Treefrogs now swim properly with a breaststroke, surging forward each time they kick, instead of leaping off the surface of the water.
- Treefrogs can now hop out of water, rather than bobbing in place.
- The jump animation now plays at the same speed as on Bedrock.

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
- Added Welsh language support 🏴󠁧󠁢󠁷󠁬󠁳󠁿!
- Every missing translation has been filled in. 211 block, item, biome and advancement names only existed in English, and they're now translated into all 26 other languages.
- Fixed the British English Lang file's path, it was named ```en_uk``` rather than ```en_gb```  

## Fixes
- Fixed a bug in door generation that could make door textures appear inverted.
- On 26.3, Moorlands tall grass wasn't generating, and structures, larch erratics and meltwater erosion weren't treating grass, podzol and mud as soil. This was because Minecraft 26.3 changed what counts as dirt, and it's now fixed.
- Using a bottle on a jellyfish during its milking cooldown no longer fills the bottle with water.
- Fixed an issue with the moorelands generating grass underground.

## Regression Tests
- The treefrog swim gametest now checks that the frog paddles to the bank without leaping off the water.
- Fixed the glacier gametest so it still finds the glacier now that Larch Forests can push it onto a mountaintop.
