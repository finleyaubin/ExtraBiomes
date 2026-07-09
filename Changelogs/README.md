Each release's notes live here as `<VERSION>.md`, where `<VERSION>` matches the version string the CI/CD pipeline extracts from the git tag (the tag with any `Bedrock-`/`v`/`V` prefix stripped).

Examples:
- Tag `Bedrock-V3.0.0` → `3.0.0.md`
- Tag `Bedrock-V3.0.0-beta6` → `3.0.0-beta6.md`

When cutting a release, add the matching `<VERSION>.md` file **before** pushing the tag. The workflow (`.github/workflows/manual.yml`) reads this file and uses it as the body of the GitHub Release and the changelog on CurseForge. If no matching file exists, it falls back to a placeholder message.
