# Contributing to ExtraBiomes

Thanks for your interest in contributing to ExtraBiomes! This mod ships in two forms — a **Bedrock addon** and a **Java (Forge) port** — and the repo is structured around that split. Please read this doc before opening a PR so your changes land in the right place and get reviewed quickly.

## Branch structure

| Branch | Purpose |
|---|---|
| `main` | Stable, released code. Reflects what's live on CurseForge/MCPEDL and the latest Java build. **Do not target this branch directly.** |
| `Bedrock-Dev` | Active development for the Bedrock addon (`ExtraBiomes - Bedrock`). |
| `Java-Dev` | Active development for the Java/Forge port (`ExtraBiomes - Forge`). |

Open your pull request against `Bedrock-Dev` or `Java-Dev`, whichever matches the part of the mod you're working on. Changes are merged into `main` as part of a release, not per-PR.

## Getting started

1. Fork the repo.
2. Clone your fork and check out the correct dev branch:
   ```bash
   git clone https://github.com/<your-username>/ExtraBiomes.git
   cd ExtraBiomes
   git checkout Bedrock-Dev   # or Java-Dev
   ```
3. Create a feature branch off of that dev branch:
   ```bash
   git checkout -b feature/short-description
   ```
4. Make your changes, test them in-game, and commit.
5. Push to your fork and open a PR **against `Bedrock-Dev` or `Java-Dev`** (matching where you branched from).

## Working on the Bedrock addon

- Source lives in `ExtraBiomes - Bedrock/`.
- Test changes by loading the behavior/resource packs into a local world (or symlinking the pack folders into your `com.mojang` development pack directories) rather than a distributed `.mcaddon`, so you can iterate quickly.
- Bump the `version` fields in `manifest.json` (behavior and resource pack) if your change is meant to ship as part of the next release — but check with a maintainer first, since version bumps are usually coordinated at release time.
- Keep biome, entity, and block identifiers namespaced consistently with the existing content (e.g. `extrabiomes:` prefix) to avoid collisions.
- If you're adding new entities/mobs, please include the associated resource pack assets (models, textures, animations) in the same PR — partial content (behavior only, or resource only) is hard to review and test.

## Working on the Java (Forge) port

- Source lives in `ExtraBiomes - Forge/`.
- Build with Gradle:
  ```bash
  ./gradlew build
  ```
- Run a local test client with:
  ```bash
  ./gradlew runClient
  ```
- Match the existing package structure and naming conventions when adding new blocks, items, biomes, or entities.
- The Java port is still catching up to Bedrock feature parity — if you're porting a Bedrock feature over, it's worth mentioning that in your PR description so it's clear what it's based on.

## Pull request guidelines

- Keep PRs focused — one feature, biome, mob, or fix per PR where reasonably possible. Large multi-feature PRs are harder to review and more likely to get stuck.
- Write a clear PR description: what changed, why, and how you tested it (e.g. "spawned in creative, confirmed biome generates in a fresh seed").
- Include screenshots or a short clip for anything visual (new blocks, mobs, biomes, structures) — it makes review much faster.
- Reference any related issue with `Closes #123` if applicable.
- Be responsive to review feedback; PRs that go stale without updates may be closed and can always be reopened later.

## Reporting bugs / suggesting features

- Use [GitHub Issues](https://github.com/finleyaubin/ExtraBiomes/issues) for bug reports and feature suggestions.
- For bugs, please include: your platform (Bedrock/Java), version, Minecraft version, steps to reproduce, and — if possible — a screenshot or crash log.
- For feature/content suggestions (new biomes, mobs, blocks), a short description and any reference art or examples are appreciated, but not required.

## Code style

- Match the formatting and naming conventions already used in the file you're editing rather than introducing a new style.
- Comment non-obvious logic (spawn conditions, custom Mob behaviors, world-gen rules) — these are the areas most likely to need future maintenance.

## License

By contributing, you agree that your contributions will be licensed under the same [MIT License](LICENSE) that covers the rest of the project.

## Questions

If anything here is unclear, or you're not sure whether a change belongs on `Bedrock-Dev` or `Java-Dev`, feel free to open a draft PR or an issue and ask before doing a lot of work.
