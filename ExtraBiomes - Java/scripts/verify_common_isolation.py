#!/usr/bin/env python3
"""Fail loudly if common/ imports anything Forge-only beyond the documented exceptions.

Run this after moving more code into common/ during the Fabric/NeoForge follow-up passes -
every new net.minecraftforge import found here needs either removing, or adding to
ACCEPTED_EXCEPTIONS with a comment explaining why it's still an accepted Forge-shaped value.
"""
import re
import sys
from pathlib import Path

COMMON_JAVA = Path(__file__).resolve().parent.parent / "common/src/main/java"

# No file in common/ should import net.minecraftforge.* anymore - Config, ModFluids/
# ModFluidTypes/BaseFluidType, ModItems (spawn eggs), FrogHelmetItem, ModLogs, and
# ModLeavesWithSupport (IForgeShearable) have all either moved out to forge/'s own packages or
# been rewritten loader-agnostically now that the fabric module needs real implementations too.
ACCEPTED_EXCEPTIONS = set()


def main():
    violations = []
    for java_file in sorted(COMMON_JAVA.rglob("*.java")):
        rel = java_file.relative_to(COMMON_JAVA).as_posix()
        text = java_file.read_text(encoding="utf-8")
        code_lines = [l for l in text.splitlines() if not re.match(r"^\s*(//|\*|/\*)", l)]
        if any("net.minecraftforge" in l for l in code_lines):
            if rel not in ACCEPTED_EXCEPTIONS:
                violations.append(rel)

    missing_exceptions = [
        rel for rel in ACCEPTED_EXCEPTIONS if not (COMMON_JAVA / rel).exists()
    ]

    if violations:
        print("FAIL: unexpected net.minecraftforge imports in common/ (not in ACCEPTED_EXCEPTIONS):")
        for v in violations:
            print(f"  {v}")
    if missing_exceptions:
        print("WARN: ACCEPTED_EXCEPTIONS entries no longer exist (stale list entries):")
        for v in missing_exceptions:
            print(f"  {v}")

    if violations:
        sys.exit(1)
    print(f"OK: common/ only imports net.minecraftforge in the {len(ACCEPTED_EXCEPTIONS)} accepted exception files.")


if __name__ == "__main__":
    main()
