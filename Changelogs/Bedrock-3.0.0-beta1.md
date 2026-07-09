# ExtraBiomes 3.0.0 Beta 1
3.0.0 Is the first beta release for the next version of Extrabiomes for Bedrock.
This is the first beta on Bedrock with custom biome support, following the addition of caves and cliffs, as custom biomes were previously disabled between these updates.

## The purpose of this beta
Now that I have a working prototype for modern Minecraft Bedrock, I would like some feedback, **mainly on how biome size, placement feel, and freqency** as the way custom biomes generate has been changed to a system where I have to manually override areas in vanilla biomes as opposed to having them generate alongside vanilla biomes, which has some undesired effects like weird blending between biomes, there is nothing I can do about this.

## Changes
- All Biomes now generate again! :D
- All Block Events have been updated to use the JavaScript API as opposed to the JSON-based event system
- Dense Clouds and their variants now break a player's fall.
- Removed all river biomes as river transformations no longer work :(
- removed variants of biomes that were just height changes, and either consolidated them into single biomes, had them replace things like mountains, or removed them.
- Redesigned the Shattered Swamp:<img width="2560" height="1400" alt="Screenshot_20251019_103906" src="https://github.com/user-attachments/assets/b78f75cd-1b9a-4cab-8316-c5a6e800ba11" />
- added dry grass and mud too the moorlands: <img width="2560" height="1400" alt="Screenshot_20251019_105954" src="https://github.com/user-attachments/assets/fd7e470a-454c-415c-8537-9b8cd6f10689" />
-  Shattered Taiga Spikes now use structures for the ice spikes, as vanilla Ice spikes no longer seem to generate in custom biomes. Also added a super tall variant <img width="2560" height="1400" alt="Screenshot_20251019_132748" src="https://github.com/user-attachments/assets/e3557f54-7f9b-439a-ace3-ef5dea2d46e3" />
- The Floating jungle will now generate on warm mountain peaks around the world, as opposed to before, where it was a very tall flat biome that would create a cliff face in jungles.
- Unfortunately, due to the new world gen changes, the Netherlands can no longer be completely flat, and hills will generate, and the netherack generation below only goes down a few blocks now  :( <img width="2560" height="1400" alt="Screenshot_20251019_133802" src="https://github.com/user-attachments/assets/eb7311e8-4054-40d9-8e5d-79f64b40afc0" />
- Simmarly the mutated variant of the Netherlands also is not completely flat, which introduces blocky generation hills which may need addressing <img width="2560" height="1400" alt="Screenshot_20251019_134336" src="https://github.com/user-attachments/assets/01d9f80f-eecc-40aa-963e-b37b0424f6bb" />
- added A new biome, the Grand Oasis  <img width="2560" height="1400" alt="Screenshot_20251019_134912" src="https://github.com/user-attachments/assets/efb0a0b4-82e4-43e1-a73c-52fd8d378771" />
- Added a new palm tree shape, and made a new palm leaves texture <img width="2560" height="1400" alt="Screenshot_20251019_135144" src="https://github.com/user-attachments/assets/96bb0223-acbf-44ae-9649-f0fa1a2f70ea" />
- Renamed Hopping Spore to Hoppleshroom

## Known Issues
- Client Side biome features need updating to the new format (grass colour, fog water colour, etc.)
- Vanilla Animal variant tags need adding to custom biomes.
- I have not addressed Items or entities yet. And so Lots of item interactions are broken, mainly shooting, and due to overrides made on a previous version, vanilla wolves will not have their variants or be able to wear wolf armour currently.
- Sky islands currently generate, but  Sky Cities do not

**Full Changelog**: https://github.com/finleyaubin/ExtraBiomes/compare/Bedrock-V2.0.2...Bedrock-V3.0.0-beta1
