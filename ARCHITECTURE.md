# ExtraBiomes Architecture

ExtraBiomes is a Minecraft: Java Edition mod (26 new overworld biomes, plus the mobs, blocks,
structures, and items that fill them) shipped as a multi-loader Architectury project, alongside
a separate, unrelated Bedrock Edition add-on that reimplements the same content for the Bedrock
platform. The two are independent codebases sharing only design intent and (some) art assets —
there is no shared runtime or build.

```
ExtraBiomes/
├── ExtraBiomes - Java/       Architectury multi-loader mod (Fabric + NeoForge, Forge on ice)
└── ExtraBiomes - Bedrock/    Bedrock add-on (behavior pack + resource pack), independent codebase
```

## 1. Java mod: Architectury multi-loader layout

Built with Gradle + the [Architectury](https://architectury.dev/) plugin/Loom toolchain, targeting
Minecraft 1.21.3. `settings.gradle` defines four subprojects, of which three are currently wired
into the build:

```
common/      loader-agnostic game logic (Minecraft + Architectury API only)
fabric/      Fabric Loader entry point, mixins, datagen, Fabric-only integrations
neoforge/    NeoForge entry point, event bus hooks, datagen, NeoForge-only integrations
forge/       kept in source, commented out of settings.gradle (`enabled_platforms=fabric,neoforge`)
```

`forge` is present on disk (and largely mirrors `neoforge`'s package layout) but intentionally
excluded from the current build — the comment in `settings.gradle` explains it's kept rather than
deleted so a future MC-version branch can re-enable it by uncommenting one line. See the
`bump-mc-version` skill for the version-bump workflow across these modules.

### 1.1 `common` — the shared core

Everything that only needs vanilla Minecraft + the Architectury API lives here; it compiles once
and is woven into both platform jars. Package breakdown (`common/src/main/java/net/winepicfin/extrabiomes/`):

| Package | Role |
|---|---|
| `worldgen/` (~95 files) | The bulk of the mod: `biomes/`, `features/` (one subpackage per biome's terrain features — boulder, glacier, moorland, mushroom, netherlands, palm, taiga spike, tropical, etc.), `structure/` (windmill structure + pools), `tree/`. |
| `entity/` (~62 files) | Custom mobs (Puckoo, Piranha, Hoppleshroom, etc.), `ai/`, `client/` renderers, `animations/`, `custom/` entity classes. |
| `block/` | `ModBlocks` registry + `custom/` block classes (fountain spout, supported leaves, sign variants, pebbles). |
| `item/` | `ModItems` registry, `ModFoods`, `ModItemMaterials`, `ModCreativeModeTabs`, `custom/` item classes (bait, razor feathers, jellyfishing net, spawn eggs). |
| `data/`, `commondatagen/` | `CommonRecipes` and loot-table-entry helpers shared by each platform's own datagen. |
| `advancements/` | `ModAdvancements`. |
| `platform/` | The Architectury `@ExpectPlatform` boundary (see 1.2). |
| `sound/`, `util/` | Small shared helpers. |

`ExtraBiomes` (`ExtraBiomes.java`) is the mod-identity anchor — just `MOD_ID` — referenced by
every registrar regardless of loader, and unsurprisingly the graph's most-connected node (93 edges).
Registries follow the standard `Mod*` pattern (`ModBlocks`, `ModItems`, `ModEntities`,
`ModBlockStateProvider`, ...) — these double as the graph's other "god nodes," i.e. they're the
hub every feature/mob/block class registers itself through.

### 1.2 The `@ExpectPlatform` boundary

`common`'s compile classpath is plain vanilla Minecraft, so any registration that needs a
loader-patched class (Forge/NeoForge access-transformed constructors, Fabric's builder APIs, a
Forge-only `LiquidBlock`/`BucketItem` constructor overload, mod-loaded checks for the Create
compat) can't be written directly in `common`. Architectury's `@ExpectPlatform` annotation is the
seam: `common/.../platform/ExtraBiomesExpectPlatform.java` declares stub methods
(`createLogBlock`, `registerWoodType`, `createBlockEntityType`, `createFrogHelmetItem`,
`isCreateLoaded`, `applyWindmillCreateCompat`, ...) that throw `AssertionError` and exist only to
be call-sites; the Architectury transformer rewrites each call to invoke a platform-specific
`ExtraBiomesExpectPlatformImpl` class at build time:

```
common/.../platform/ExtraBiomesExpectPlatform.java       declares the @ExpectPlatform stubs
fabric/.../platform/fabric/ExtraBiomesExpectPlatformImpl.java     Fabric implementation
neoforge/.../platform/neoforge/ExtraBiomesExpectPlatformImpl.java NeoForge implementation
forge/.../platform/forge/ExtraBiomesExpectPlatformImpl.java       Forge implementation (unbuilt)
```

This is the single, deliberately narrow bridge between the shared code and loader-specific
internals — everything else in `common` is loader-agnostic by construction.

### 1.3 `fabric` and `neoforge` — platform modules

Each platform module owns its own:
- **Entry point** (`ExtraBiomesFabric`/`ExtraBiomesFabricClient` vs `ExtraBiomesForge`) and
  loader-specific config (`FabricConfig` / `ForgeConfig`, despite the NeoForge module's class
  still being named `ForgeConfig` — a NeoForge-successor-of-Forge naming holdover).
- **Event wiring**: Fabric uses its callback API (`FabricModEvents`, `FabricServerEvents`);
  NeoForge uses its event bus (`ModEventBusEvents`, `ModEventBusClientEvents`, plus dedicated
  handlers like `HarpySpawnerHandler`, `PhantomHarpyTargetHandler`, `WolfFrogHatHandler`).
- **Datagen**: near-mirror `datagen/` packages (`ModBlockStateProvider`, `ModItemModelProvider`,
  `ModBlockTagGenerator`, `ModItemTagGenerator`, `ModBiomeTagProvider`, loot table providers,
  `ModRecipeProvider`, plus NeoForge's `ModWorldGenProvider`) — each loader generates its own
  assets/data off the same `common` registries.
- **Fabric-only mechanism**: `mixin/` (via `extrabiomes.mixins.json`) patches vanilla
  accessors (`MobAccessor`, `SpawnPlacementsAccessor`, `WoodTypeAccessor`,
  `TreeDecoratorTypeAccessor`, `TrunkPlacerTypeAccessor`, wolf renderer/model mixins) that
  Forge/NeoForge instead expose via access transformers (`accesstransformer.cfg`).
- **Third-party compat**: `compat/create/CreateWindmillCompat` (implemented per-loader, gated
  behind the `isCreateLoaded()` / `applyWindmillCreateCompat()` `@ExpectPlatform` calls above so
  Create's classes are never loaded unless Create is present).
- **GeckoLib jar-in-jar merge**: the root `build.gradle` contains a `mergeJarInJarDependency`
  helper (used by `forge`/`neoforge`'s `build.gradle`) that unpacks GeckoLib's bundled `mclib`
  and re-merges it into the mod jar at configure time — needed because Forge/NeoForge's per-mod
  module isolation won't let GeckoLib's classes see a separately-declared `mclib` dependency.
- **GameTests**: `gametest/` packages per platform (biome generation, biome-modifier
  application, spawn-egg items; NeoForge adds structure generation) — deterministic,
  seed-pinned tests exercised in CI (see the CI hyperedge below).

`fabric` and `neoforge` are structurally near-duplicates of each other by design: same package
shape, same provider classes, differing only in the loader API each must speak. `forge`'s
(unbuilt) source tree mirrors `neoforge`'s almost exactly, since NeoForge is Forge's fork.

### 1.4 Build system notes

- `settings.gradle` conditionally includes subprojects; `gradle.properties`'
  `enabled_platforms=fabric,neoforge` is the human-readable declaration of what's actually live.
- Version pins for MC (1.21.3), Fabric Loader/API, Architectury API/Loom/plugin, NeoForge,
  TerraBlender, and GeckoLib all live in `gradle.properties`.
- TerraBlender (biome-region blending) is a foreign-jar dependency per platform, fetched via
  Modrinth's Maven proxy for the Forge coordinate (JitPack no longer serves it).
- Datagen, once run per-platform, produces the actual JSON assets (models, loot tables, tags,
  recipes, biome tags) checked into each module's `src/main/resources`/`src/generated`.

## 2. Bedrock add-on

A completely separate, non-code content pack under `ExtraBiomes - Bedrock/packs/`, structured as
the standard Bedrock **BP** (Behavior Pack) + **RP** (Resource Pack) pair:

```
packs/BP/   biomes, blocks, entities, feature_rules, features, items, loot_tables, recipes,
            scripts (blocks/entities/items), spawn_rules, structures, trading, worldgen
            (processors, structure_sets, template_pools)
packs/RP/   animation_controllers, animations, attachables, biomes, entity, fogs, models,
            particles, render_controllers, sounds, textures (blocks/entity/items/models/ui), texts
```

It reimplements the same content (biomes, mobs like Puckoo, wood sets, structures) using Bedrock's
JSON-driven behavior/resource format rather than Java code — there's no shared toolchain with the
Java mod. The graphify report flags several `[INFERRED] semantically_similar_to` links between
Bedrock and Java texture files with matching names (e.g. `dense_cloud.png`,
`stick_pile.png`, mushroom block sets) — these are parallel, hand-maintained assets for the two
platforms, not a shared source of truth.

## 3. Cross-cutting notes

- **`Changelogs/`** at the repo root tracks per-version release notes for both platforms; a CI
  hyperedge (`java_release` workflow) reads a changelog file as the source of truth for release
  versioning.
- **CI** (`.github/workflows`) runs a seed-pinned GameTest pipeline with `runData` isolation and a
  poll-driven completion/timeout strategy — needed because Minecraft GameTests execute
  asynchronously inside a spun-up server, not as ordinary JUnit tests.
- **Searching this codebase**: project convention (see `CLAUDE.md`) is to use the `graphify`
  skill/knowledge graph rather than grep for anything beyond a narrow literal-string lookup.
