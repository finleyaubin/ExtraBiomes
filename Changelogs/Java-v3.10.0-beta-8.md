# ExtraBiomes for Java Edition v3.10.0 Beta 8
# Changes

## World Generation
<img width="100%" alt="Snow Drifts and spire" src="https://github.com/user-attachments/assets/2df5ecc2-0ae1-48c7-a9ae-49b9e28d9981" />

- Added 10 new snow drifts to Glacier, Cold Mesa and Taiga Spikes biomes: a cornice ridge, a sastrugi field, a crescent dune, a moraine drift with boulders, a giant swirl, a twisting spire, a breaking wave, a spiral cone, and rare 40-block and 150-block tall spires.
- Snow drifts now include powder snow, blue ice, and glacial gravel, andesite and calcite deposits.
- The 150-block spire has a reward chest at its summit.
- The Netherlands and its wheat-field variant are now netherrack all the way down to bedrock, with caves and ravines carving through it.
- The Netherlands underground now has patches of basalt and blackstone, plus basalt pillars hanging from cave ceilings.
- Fixed grass generating on cave floors in The Netherlands, and dirt on its ocean floors (now netherrack, matching Bedrock).
- Netherlands ores now generate all the way down to bedrock instead of stopping around y=0.
- Fixed grass generating on cave floors under Jellyfish Fields.

## Blocks
<img width="100%" alt="2026-09-25_23 18 23" src="https://github.com/user-attachments/assets/06187c41-fe6f-445b-9d72-ba881425b33a" />

- Added Nether Coal, Copper, Emerald, Iron, Lapis and Redstone Ores (ported from Bedrock), which replace the vanilla stone ores in The Netherlands.
- Added Grass Stone (ported from Bedrock, including its rare Easter egg top texture). It now forms the sea floor of Jellyfish Fields wherever there is no moss. Craftable with short grass over stone.
- Custom wood signs (Mystic, Sky, Palm, Gilded Sky): fixed break particles showing the missing-texture sprite instead of the sign's plank texture.

## Visuals
<img width="100%" alt="2026-09-25_22 46 09" src="https://github.com/user-attachments/assets/99719abc-5745-4c92-8e64-c1f9b3ed1af9" />

- Generated LabPBR normal/specular maps from ported from the Bedrock addon's PBR set, however heightmaps and a few blocks need some tweaking still.
- Updated the palm sapling model (again and again still having issues with it lmaooo).



## Misc
- Fixed NeoForge jars shipping without their `data/neoforge/**` generated tags and their biome modifiers (the mob spawns and extra features ExtraBiomes adds to vanilla biomes).
- Reorganized the block and item textures into sub folders (mushrooms, black sand, clouds, nether ores, grass stone, one folder per wood set, boats, food, tools, doors and signs).

## Beta status
Still a beta release - the Java port remains newer and less battle-tested than the Bedrock addon, so please keep reporting anything that looks wrong on the GitHub issue tracker.
