# ExtraBiomes for Java Edition v3.10.0 Beta 10
A small one this time just addressing a few bug fixes and oversites
# Changes

## Mobs
### Worms
- Two worms placed inside a composter that has compost in it will breed. Each breeding uses one level of compost, and the new worm drops out of the bottom of the composter.
- Worm items can be placed straight into a composter by right-clicking its top.
- Added a new Easter egg.
An example worm farm using this setup can be shown here:
<img width="100%" alt="Worm farm" src="https://github.com/user-attachments/assets/85f679bc-4291-4e40-ae0d-84da57de3a6a" />


## World Generation
### Moorlands
<img width="100%" alt="moorlands" src="https://github.com/user-attachments/assets/8acad46a-8ef7-4df2-abeb-6f52024a6d20" />

- Moorlands no longer grow dead bushes, and almost every block of its floor is now tall grass.
- Moorlands mud patches are now about 20 blocks across like Bedrock, instead of small speckles, and podzol now forms big smooth blobs like Bedrock, wider in some regions and smaller in others, instead of scattered single blocks.
### The Netherlands
<img width="100%" alt="the netherlands" src="https://github.com/user-attachments/assets/af2d570b-8fc2-4f17-ad34-2fd0a1dc5e7e" />

- The Netherlands tulips are planted in neat colour stripes like Bedrock, instead of being scattered at random.
## Items
### Pebbles
- Throwing a pebble now has a 0.8 second charge-up: hold right-click and release, like a bow. Releasing early does nothing and keeps the pebble. This replaces the old half-second throw cooldown.
- Sneak-clicking the air no longer uses up a pebble without throwing it, and you can now throw pebbles while aiming at a block.

## Fixes
<img width="359" height="201" alt="20261004_013142" src="https://github.com/user-attachments/assets/d3edba15-43e2-42c7-beb4-995df3dbd6da" />
<img width="366" height="202" alt="20261004_013125" src="https://github.com/user-attachments/assets/ea6c21ca-36ee-4a53-85d1-6907151d3e59" />

- The Frog Helmet and the Jellyfishing Net can now be crafted, using the same recipes as Bedrock.
- Palm trees, and the log-supported leaves of the Floating Jungle giant trees, now decay when their logs are removed. Their leaves were being generated as permanent.
- Huge mushroom blocks now render like vanilla ones: faces that used to touch another mushroom block show the pore texture once the neighbour is gone, instead of the cap texture.
- Jellyfish now actually spawn in Jellyfish Fields. They shared a spawn cap of 5 with squid and dolphins; they now use the same, much larger, cap as piranhas.
- The advancement tab background is palm wood instead of stone.
- The mod menu entry has its icon and links to the project page and issue tracker.
- Fixed a rare world generation crash ("Requested chunk unavailable during world generation") caused by Glacier water pools and ponds placed in the last columns of a chunk.
- Removed the "missing BiomeFilter" errors from the log for the Moorlands, Netherlands, Glacier and Volcanic Moss Tundra features.
