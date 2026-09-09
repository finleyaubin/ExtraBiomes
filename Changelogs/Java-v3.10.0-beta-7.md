# ExtraBiomes for Java Edition v3.10.0 Beta 7
# Changes

## Structures
### Sky City
<img width="100%" alt="Sky City" src="https://github.com/user-attachments/assets/a4748f97-ffcc-4d73-a02e-485e7ecce3f5" />

- size matched to the Bedrock addon's actual scale (jigsaw depth 7 → 20, the vanilla max) - it's now noticeably larger and denser.
- fixed several trapdoors and stairs left in the wrong spot by the Bedrock port, across all four buildings and most of the path pieces.
- added a new, much rarer path piece - a roundabout with a statue centerpiece.
### Jungle Pillars 
<img width="100%" alt="Jungal Pillars" src="https://github.com/user-attachments/assets/2cc2b2f7-82bb-46de-b193-0e9303086b26" />

- Jungle Pillars: added a bDubs-inspired weathering pass - noise-clustered stone variants (deepslate/tuff at the base, shading up through mossy/cracked stone to clean andesite/diorite near the crest), plus vines and grass/moss patches (with the occasional sapling, bamboo, azalea, or fern) grown live on whichever faces of the placed structure actually turned out exposed. The raw pillar shapes themselves are untouched.

## Loot
- Sky City chests: reworked the common/rare/epic loot tables so a full chest can't roll all nuggets - junk and gear now come from separate pools, and rare/epic chests can pull the razor feather line's diamond/netherite tiers, golden apples, enchanted books, and totems instead of just golden armor.

## Biomes
<img width="100%" alt="Grand Oasis Vilage" src="https://github.com/user-attachments/assets/9bf2bb59-7621-40c6-a0dc-29029bf7e4fa" />


- Villages: vanilla village structure sets can now spawn in ExtraBiomes biomes with matching terrain - Moorlands (plains), Deep Dark Forest/Taiga Spikes/Shattered Taiga Spikes (taiga), Grand Oasis (savanna).
- Mushrooms: fixed several bugs preventing small mushrooms from spawning underground and on mycelium floors (a standalone underground scatter had no placement modifiers at all, and the floor-conversion feature couldn't act on grass or on already-mycelium ground). The mod's 9 existing colored small mushroom blocks are now actually used in world generation instead of always falling back to vanilla red/brown. Vanilla Mushroom Fields (and any other mushroom-tagged biome, modded included) now gets the same small mushroom variety as Fungle Jungle.

## Mobs
- Puckoo: doubled beach spawn weight - noticeably more common along the shore.
- Jellyfish: beach spawns now wash up on the sand instead of appearing in the water, and no longer take drowning damage while stranded there.
- Worm: shift-right-clicking a worm now picks it up as an item, matching the Bedrock addon. A renamed worm keeps its name across the item/entity round-trip.

## Compatibility
- Boulders/stick piles: no longer added to every third-party mod's forest/jungle/plains biome via the broad vanilla tags - a large enough modpack could hit a "Feature order cycle found" world-load crash once another mod's biome happened to share one of these features (seen with Ars Elemental and Biomes We've Gone). Now added only to vanilla's and this mod's own biomes via a curated list.
- Mystic Forest: no longer shares its flowers, grass, or mushroom patches with vanilla's versions of those features, for the same cross-mod crash reason above.
- Fixed a crash placing structures (huge mushrooms, Jungle Pillars, glacier snow drifts, etc.) whenever Ars Nouveau was installed alongside this mod.

## Beta status
Still a beta release - the Java port remains newer and less battle-tested than the Bedrock addon, so please keep reporting anything that looks wrong on the GitHub issue tracker.
