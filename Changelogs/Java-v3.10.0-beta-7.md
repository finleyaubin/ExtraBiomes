# ExtraBiomes for Java Edition v3.10.0 Beta 7
# Changes

## Structures
- Sky City: size matched to the Bedrock addon's actual scale (jigsaw depth 7 → 20, the vanilla max) - it's now noticeably larger and denser.
- Jungle Pillars: added a bDubs-inspired weathering pass - noise-clustered stone variants (deepslate/tuff at the base, shading up through mossy/cracked stone to clean andesite/diorite near the crest), plus vines and grass/moss patches (with the occasional sapling, bamboo, azalea, or fern) grown live on whichever faces of the placed structure actually turned out exposed. The raw pillar shapes themselves are untouched.

## Biomes
- Villages: vanilla village structure sets can now spawn in ExtraBiomes biomes with matching terrain - Moorlands (plains), Deep Dark Forest/Taiga Spikes/Shattered Taiga Spikes (taiga), Grand Oasis (savanna).
- Mushrooms: fixed several bugs preventing small mushrooms from spawning underground and on mycelium floors (a standalone underground scatter had no placement modifiers at all, and the floor-conversion feature couldn't act on grass or on already-mycelium ground). The mod's 9 existing colored small mushroom blocks are now actually used in world generation instead of always falling back to vanilla red/brown. Vanilla Mushroom Fields (and any other mushroom-tagged biome, modded included) now gets the same small mushroom variety as Fungle Jungle.

## Beta status
Still a beta release - the Java port remains newer and less battle-tested than the Bedrock addon, so please keep reporting anything that looks wrong on the GitHub issue tracker.
