# ExtraBiomes for Java Edition v3.10.0 Beta 4
# Changes

## Biomes
<img width="3200" height="1800" alt="2026-08-28_13 21 16" src="https://github.com/user-attachments/assets/19b20f5b-b189-424e-9dc2-2a771a34220c" />

- Reworked Bryce Pillars generation for sturdier, better-shaped spires.
- Jungle Pillars' stone spires are now sunk into the ground instead of sitting on top of it.
- The Netherlands' wheat fields and hydration are now painted deterministically across the whole floor of each chunk column instead of scattering probabilistically, fixing wheat and buried ponds that were sparse or often failed to place at all; vanilla springs and default grass/flowers no longer pollute the farmland biomes' floor.
- Dropped the Netherlands canal feature.
- Enlarged Sky City to match its Bedrock counterpart's size (its jigsaw recursion depth was capped well below Bedrock's).
- Fixed Sky City's fountains never actually holding water - their water column relied on a Bedrock-only mechanic that does nothing on Java, so the water just sat inert.
- Rebalanced Sky City's structure piece pool to favor multi-exit pieces (cross, roundabout, fountain) over corridors/dead-ends, so each generated instance branches wider and has more total footprint.
- Fixed Charred Forest using a partial depth range instead of the full range every other rare-overworld-region biome uses.
- Jellycoral structures now check that enough of their placement footprint is actually underwater before generating, since their templates no longer bundle their own water fill.

## Mobs
<img width="3200" height="1800" alt="2026-08-28_12 42 55" src="https://github.com/user-attachments/assets/9560c1e3-7e48-4ee3-b944-8c414ae2198f" />
- Piranhas no longer attack players' tamed animals.
- Piranha bait can now be picked up and re-thrown again after landing, and their remaining health carries over as durability instead of resetting.
- Added a config option to make piranhas more tame (less aggressive).
- Added an advancement for luring a piranha away with bait.
- Piranha spawns now have their own local density cap, independent of the shared water-ambient category cap.
- Harpies now use a fixed minimum spawn height instead of an offset from the world's top.
- Worms now spawn based on current weather (rare in clear weather, common in rain, guaranteed in thunder) instead of at a flat rate year-round.
- Puckoos can now spawn naturally on beach biomes, instead of only being obtainable via eggs or breeding.
- Piranha, Treefrog, Hoppleshroom, and Jellyfish spawn biomes are now driven by biome tags instead of hardcoded lists, so third-party swamp/mushroom biomes tagged into either mod-loader's convention tags pick up these spawns automatically - and this mod's own biomes now contribute back into those same tags.
- Frog Helmet now has proper durability instead of being unbreakable.

## Compatibility
<img width="3200" height="1800" alt="2026-08-28_13 19 38" src="https://github.com/user-attachments/assets/20f1a5fb-2511-4305-b7dc-e4613c04aab5" />

- Added a Create mod compatibility windmill: when Create is installed, The Netherlands' windmill generates as a functional Create-powered variant instead of the plain decorative structure.

## Items & Blocks
- Improved Mystic tree generation.

## Beta status
Still a beta release - the Java port remains newer and less battle-tested than the Bedrock addon, so please keep reporting anything that looks wrong on the GitHub issue tracker.
