# ExtraBiomes for Java Edition v3.10.0

**The first official release of ExtraBiomes for Java Edition.** The Java version of the mod has been in development on and off since 2023 so I am very happy to finally have the first non beta release :D
this release has been developed to maintain almost full parity with the bedrock version v3.1.0 (yes this is also supposed to be 3.10.0 but made a typo in the first beta so kept it lmao), yet there are some very minor differences between the 2 platforms.
this changelog is a brief overview of the whole java mod :) 

## Supported versions and Requirements
- Minecraft 1.20.1, 1.20.2, 1.20.4, 1.20.6, 1.21.1, 1.21.3, 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1.2, 26.2 or 26.3.
- Fabric (every version), NeoForge (1.20.2 and up) or Forge (1.20.1 and 1.20.2).
- Required mods: [TerraBlender](https://modrinth.com/mod/terrablender), [Architectury API](https://modrinth.com/mod/architectury-api) and [GeckoLib](https://modrinth.com/mod/geckolib), plus [Fabric API](https://modrinth.com/mod/fabric-api) on Fabric.
- Optional: [Create](https://modrinth.com/mod/create), which gives The Netherlands' windmill moving parts.

## Biomes
26 new overworld biomes.

### Highlights
<img width="100%" alt="Floating Jungle" src="https://github.com/user-attachments/assets/f7444b19-aa3d-4de9-b55b-48d787fe475e" />

- **Floating Jungle:** a jungle on the world's tallest peaks, with sky islands hanging 28 to 60 blocks above them. Islands have vines, roots and waterfalls trailing from their undersides, and some are joined by giant log canopy bridges. The rarest archipelago has an overgrown ruin around a chest of loot, and one rare island carries a vanilla jungle temple. Colossal jungle trees, 100 to 130 blocks tall, rise from the peaks.
- **Glacier:** an enormous block of ice stretching down to bedrock.
  - Underground, the caves are geothermal. They have basalt-lined lava pools, lavafalls, glowing magma veins, deep blue ice, snow and basalt pillars, meltwater pools and frozen lakes. Chests are sealed inside blocks of ice, and snow golems spawn deep down.
  - On the surface there are crevasses with snow bridges, and meltwater streams ending in plunge pools, some with geysers of potent sulfur (26.2 and up). Streams can sink into shafts that bore down through the ice into large chambers. The water is a clear pale blue.
  - It rains instead of snows, so surface water doesn't freeze.
- **The Netherlands:** tulip fields, wheat fields and windmills on the surface, with a mini Nether underneath. The netherrack runs down to bedrock, with basalt, blackstone and its own nether ores. **The Netherlands Mutated** swaps the tulips for wheat fields.
- **Sky City (structure):** a floating city above the mountains, see [Structures](#structures).

### Surface biomes
<img width="100%" alt="Jungal Pillars" src="https://github.com/user-attachments/assets/2cc2b2f7-82bb-46de-b193-0e9303086b26" />
- **Charred Forest** A burnt forest biome with petrified trees, some smoke and fire, plus a thick smokey fog in the air.
- **Cold Mesa:** a mesa in a cooler climate, with the underground mesa generating beneath it. **Cold Mesa Plateau** is its eroded mountain form, and **Cold Mesa Bryce** adds Bryce pillars.
- **Deep Dark Forest:** a forest corrupted by sculk.
- **Desert Bryce:** a desert with towering Bryce pillars.
- **Fungle Jungle:** a jungle that has met a mushroom island, with mycelium, new mushrooms and hoppleshrooms.
- **Future Desert:** a very rare desert of white concrete "sand" and little futuristic trees.
- **Grand Oasis:** a desert with streaks of red sand, lots of palm trees, hidden treasure and the bones of giants.
- **Jellyfish Fields:** a mossy sea floor with custom coral structures and sea pickles, home to jellyfish.
- **Jungle Marsh** A swampy jungle biome.
- **Jungle Pillars:** a jungle with huge stone pillars covered in vegetation and weathered with moss, vines and varied stone.
- **Lush Mesa:** a mesa humid enough for jungle to spread over its lower sections. **Lush Mesa Bryce** adds Bryce pillars.
- **Moorlands:** hilly lowlands of long grass, mud and podzol dotted with lakes, with plenty of wildlife.
- **Mystic Forest:** a fantasy swamp with purple-hued mystic trees, glowing leaves, purple fog and glowing goo on the floor.
- **Shattered Swamp:** a swamp with stone and tuff pillars, moss patches and bamboo.
- **Taiga Spikes:** ice spikes with everything a snowy taiga has. **Shattered Taiga Spikes** has extreme height differences.
- **Tropical Island:** sandy islands in warm oceans with palm trees, moss carpets, melons and coral.
- **Volcanic Moss Tundra:** an Iceland-inspired landscape of black sand, black sandstone and blackstone rocks, with lava rivers between basalt walls and moss that grows thicker with height.

### Underground
<img width="100%" alt="Glacial Caves at lava level" src="https://github.com/user-attachments/assets/1db2ae89-0cd4-4393-9578-3dab3a70295e" />

- **Underground glacier** this came before the vanilla ice caves were announced lol, I have since tried to revamp it to add some verity compared too the vanilla ice caves with some new fetures loot and sound effects :).
- **Deep Dark Green:** the deep dark competing with the underground jungle.
- Underground jungles, underground lush mesa, underground mesa and the fungal and fungle jungle.
- The overhauled vanilla Mushroom Island, with many new mushroom types.

### Biome placement
- Biomes are placed using TerraBlender. The config has biome weights for the primary, secondary, rare and frozen regions.
- Villages can generate in Moorlands, Grand Oasis, Deep Dark Forest and the Taiga Spikes biomes.

## Structures
### Sky City
<img width="100%" alt="Sky City" src="https://github.com/user-attachments/assets/a4748f97-ffcc-4d73-a02e-485e7ecce3f5" />

- A huge floating city above the mountains, built from a jigsaw structure with streets, roundabouts, fountains and buildings. The fountain's water pours through the island, so you can swim up into the city.
- The **Cloud Condenser** is a building with a working Dense Cloud farm and a lectern with a book that explains how it works.
- The island has a lumpy outline, with an underbelly of hanging clouds, cloud lobes under the streets, fuzzy edges, rain streaks and long low cloud banks.
- Sky trees come in three shapes, some with gilded trunks.
- Chests use tiered common, rare and epic loot tables, including the razor feather line, golden apples, enchanted books and totems.

### Others
<img width="100%" alt="Snow Drifts and spire" src="https://github.com/user-attachments/assets/2df5ecc2-0ae1-48c7-a9ae-49b9e28d9981" />

- **Windmills** in The Netherlands. They have moving parts when Create is installed.
- **Snow drifts** in Glacier, Cold Mesa and Taiga Spikes, there are a fair few different variations and rare 40-block and 150-block tall spiral snow and ioce spires. The tallest has a reward chest at its summit.
- Jellycoral structures, Bryce pillars, boulders, stick piles, huge mushrooms, palm groves and oasis fossils.

## Mobs
<img width="100%" alt="Saddled Puckoos" src="https://github.com/user-attachments/assets/10488ce4-9c84-419a-b18c-ef23c84210bd" />

- **Puckoo:** a chicken, koi and duck hybrid that likes eating the moss off pebbles. Tame it with a Mossy Pebble or by riding it. It can be saddled, and spawns on beaches.
- **Giant Tortoise** Inspired by the giant tortoises from terraria, a hostile mob that drops scutes.
- **Jellyfish:** found in Jellyfish Fields, and washes up on beaches and milked for jellyfish jam.
- **Piranha:** comes in small, normal and large sizes. they are lured away with bait, they spawn in jungle and swamp water, a single one does little damage, but they can spawn in large groups which is where they get scary and there is a config option to make them less aggressive.
- **Treefrog** spawns in swamps jungles and drops frogs legs which can bee food or turned into a frog helmet.
- **Hoppleshroom:** found in mushroom biomes.
- **Harpy** a flying mob also inspired by terraria, they throw razor feathers at players which they drop on death. 
- **Worm:** spawns more often in rain and always in thunderstorms. Place a worm with a right-click, pick it up with shift-right-click, and breed two in a composter with compost.

## Blocks and Items
<img alt="All of the blocks added by the mod" src="https://github.com/user-attachments/assets/b664a534-c310-4971-bcfc-e02434d9d464" />

- **Wood sets:** Mystic, Sky, Gilded Sky and Palm, each with a full set of blocks, stripped logs, doors, signs, hanging signs and boats.
- **Dense Cloud:** a translucent cloud block with brick, slab and stair variants. It gives a floaty slow fall, and can be farmed (see below).
- **Nether ores:** coal, copper, emerald, iron, lapis and redstone, found in The underground Netherlands.
- **Grass Stone:** forms the sea floor of Jellyfish Fields can be crafted with grass and stone.
- **Pebbles and Mossy Pebbles:** hold right-click and release to throw, like a bow, can be placed on the ground with a shift click and then stacked, and also mossy pebbles are used to breed puckoos.
- Stick Piles, Black Sand and Black Sandstone, huge colored mushrooms, Goo, and Bait for luring piranhas.
- **Gear and food:** Frog Helmet, Jellyfishing Net, Razor Feathers (diamond and netherite) and Cooked Piranha.

### Dense Cloud farming
<img width="100%" alt="Dense Cloud Farm" src="https://github.com/user-attachments/assets/e274c269-ffec-41e7-9d5f-d41a5e558e93" />

Build a column with magma at the bottom, water on top of it, and a blue ice block above the water. Dense Cloud then buds outward from any Dense Cloud next to the ice, in a flattened cloud shape.
- The blue ice must be at Y 200 or higher, and at most 20 blocks above the water.
- Cloud grows out to 10 blocks sideways and 5 blocks up or down from the ice.
- It only buds from full Dense Cloud blocks, so place one next to the ice to start the farm. It never grows into a space a player or mob is standing in.
- It breaks almost instantly and is easily blown up, so it can be harvested by hand or by machine.

## Recipes and Advancements
- Recipes for Pebbles, Stick Piles, Razor Feathers, Cooked Piranha, Black Sand, Grass Stone, the Frog Helmet, the Jellyfishing Net and the Dense Cloud variants.
- An advancement tree: taming a Puckoo, obtaining a Worm, collecting a Bucket of Goo, crafting Mystic Planks, obtaining a Frog Helmet, eating Cooked Piranha, luring a piranha with bait, visiting The Netherlands and mining its diamond ore, and a challenge for visiting every biome the mod adds.

## Visuals and sound
- LabPBR normal and specular maps are included for shader packs. Heightmaps and a few blocks still need tweaking.
- The Glacier has an ambient ice cracking and groaning sound (sources in credits.md).

## Compatibility
- **Create:** The Netherlands' windmill generates as a working Create variant.
- Tested against a grid of popular worldgen mods, and our features avoid vanilla's shared feature lists so they don't cause feature order cycle crashes. If you do find one, please open a GitHub issue with the mod and versions.
- Spawns are driven by biome tags, so third-party swamp, mushroom and similar biomes pick up our mobs automatically.

## Licence
ExtraBiomes is licensed under GPL-3.0.

Worlds made on any 3.10.0 beta are compatible with this release. There may still be some bugs so please report any on the issue tracker :)
