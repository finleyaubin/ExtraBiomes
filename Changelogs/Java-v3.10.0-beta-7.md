# ExtraBiomes for Java Edition v3.10.0 Beta 7
# Changes

## Structures
- Sky City: size matched to the Bedrock addon's actual scale (jigsaw depth 7 → 20, the vanilla max) - it's now noticeably larger and denser.
- Sky City: fixed several trapdoors and stairs left in the wrong spot by the Bedrock port, across all four buildings and most of the path pieces.
- Sky City: added a new, much rarer path piece - a roundabout with a statue centerpiece.
- Jungle Pillars: added a bDubs-inspired weathering pass - noise-clustered stone variants (deepslate/tuff at the base, shading up through mossy/cracked stone to clean andesite/diorite near the crest), plus vines and grass/moss patches (with the occasional sapling, bamboo, azalea, or fern) grown live on whichever faces of the placed structure actually turned out exposed. The raw pillar shapes themselves are untouched.

## Loot
- Sky City chests: reworked the common/rare/epic loot tables so a full chest can't roll all nuggets - junk and gear now come from separate pools, and rare/epic chests can pull the razor feather line's diamond/netherite tiers, golden apples, enchanted books, and totems instead of just golden armor.

## Biomes
- Villages: vanilla village structure sets can now spawn in ExtraBiomes biomes with matching terrain - Moorlands (plains), Deep Dark Forest/Taiga Spikes/Shattered Taiga Spikes (taiga), Grand Oasis (savanna).
- Mushrooms: fixed several bugs preventing small mushrooms from spawning underground and on mycelium floors (a standalone underground scatter had no placement modifiers at all, and the floor-conversion feature couldn't act on grass or on already-mycelium ground). The mod's 9 existing colored small mushroom blocks are now actually used in world generation instead of always falling back to vanilla red/brown. Vanilla Mushroom Fields (and any other mushroom-tagged biome, modded included) now gets the same small mushroom variety as Fungle Jungle.

## Mobs
- Puckoo: doubled beach spawn weight - noticeably more common along the shore.
- Jellyfish: beach spawns now wash up on the sand instead of appearing in the water, and no longer take drowning damage while stranded there.
- Worm: shift-right-clicking a worm now picks it up as an item, matching the Bedrock addon. A renamed worm keeps its name across the item/entity round-trip.

## Beta status
Still a beta release - the Java port remains newer and less battle-tested than the Bedrock addon, so please keep reporting anything that looks wrong on the GitHub issue tracker.
