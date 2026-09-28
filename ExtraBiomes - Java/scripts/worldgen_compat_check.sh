#!/usr/bin/env bash
# Usage: scripts/worldgen_compat_check.sh "terralith:WeYhEb5d[,slug:modrinthVersionId...]"
set -uo pipefail
cd "$(dirname "$0")/.."

mods="$1"
log="worldgen-compat.log"
mkdir -p fabric/run
echo "eula=true" > fabric/run/eula.txt
rm -rf fabric/run/world

# "stop" is only handled once the world (and its spawn chunks) has finished generating.
echo stop | ./gradlew :fabric:runServer -PcompatMods="$mods" --args="nogui --port 0" > "$log" 2>&1 &
gradle_pid=$!

# Loom's dev launch never exits after the server stops, so wait for a final state in the log instead.
until grep -qE "All dimensions are saved|Feature order cycle found|crash report|BUILD FAILED" "$log" || ! kill -0 "$gradle_pid" 2>/dev/null; do
    sleep 2
done
pkill -f "architectury.main.class=$PWD/fabric/" || true
kill "$gradle_pid" 2>/dev/null || true
cat "$log"

if grep -q "Feature order cycle found" "$log"; then
    echo "::error::Feature order cycle with $mods"
    exit 1
fi
if ! grep -q "All dimensions are saved" "$log"; then
    echo "::error::Server did not start and stop cleanly with $mods"
    exit 1
fi
echo "No feature order cycle with $mods"
