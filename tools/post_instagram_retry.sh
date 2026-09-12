#!/usr/bin/env bash
# One-off local retry of the Instagram story post for a release, without going through
# GitHub Actions. Renders slides from a changelog and posts them via the Graph API.
#
# Usage:
#   IG_TOKEN=... IG_USER_ID=... ./tools/post_instagram_retry.sh 3.1.0-beta-3
#
# The slides need a stable public URL for Meta's fetcher to pull from - this pushes them
# to the media-slides branch itself (same as the release workflow) before posting.
set -euo pipefail

VERSION="${1:?usage: post_instagram_retry.sh <version, e.g. 3.1.0-beta-3>}"
: "${IG_TOKEN:?set IG_TOKEN}"
: "${IG_USER_ID:?set IG_USER_ID}"

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$REPO_ROOT"

CHANGELOG="Changelogs/Bedrock-${VERSION}.md"
[ -f "$CHANGELOG" ] || { echo "No changelog at $CHANGELOG" >&2; exit 1; }

WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT
cp "$CHANGELOG" "$WORK/changelog.md"

pip install --quiet pillow
python3 tools/render_slides.py "$WORK/changelog.md" \
  --version "$VERSION" --edition Bedrock --out "$WORK/slides"

DIR="media-slides/bedrock/${VERSION}"
WT=$(mktemp -d)
git fetch origin media-slides 2>/dev/null || true
if git rev-parse --verify origin/media-slides >/dev/null 2>&1; then
  git worktree add -q -B media-slides "$WT" origin/media-slides
else
  git worktree add -q --detach "$WT" HEAD
  (cd "$WT" && git checkout --orphan media-slides && git reset --hard)
fi
cd "$WT"
mkdir -p "$DIR"
cp "$WORK"/slides/*.jpg "$DIR/"
git add "$DIR"
git commit -qm "Story slides for Bedrock-V${VERSION} (local retry)" || echo "no slide changes"
git push -q origin media-slides
SHA=$(git rev-parse HEAD)
cd "$REPO_ROOT"
git worktree remove -f "$WT"

RAW="https://raw.githubusercontent.com/finleyaubin/ExtraBiomes/${SHA}/${DIR}"
API="https://graph.instagram.com/v25.0"

call() {
  RESPONSE=$(curl -sS -w '\n%{http_code}' "$@")
  CODE=$(printf '%s' "$RESPONSE" | tail -n1)
  RESPONSE=$(printf '%s' "$RESPONSE" | sed '$d')
  if ! printf '%s' "$RESPONSE" | jq -e . >/dev/null 2>&1; then
    echo "HTTP $CODE, non-JSON body: $RESPONSE" >&2
    return 1
  fi
  if printf '%s' "$RESPONSE" | jq -e '.error' >/dev/null 2>&1; then
    echo "HTTP $CODE, API error: $RESPONSE" >&2
    return 1
  fi
  printf '%s' "$RESPONSE"
}

FIRST=1
for slide in $(ls "$WORK"/slides/*.jpg | sort -V); do
  NAME=$(basename "$slide")
  [ "$FIRST" = 1 ] || sleep 10
  FIRST=0
  IMAGE="$RAW/$NAME"
  echo "--- $NAME -> $IMAGE"
  CONTAINER=""
  for attempt in 1 2 3; do
    BODY=$(call -X POST "$API/$IG_USER_ID/media" \
      -d "media_type=STORIES" -d "image_url=$IMAGE" -d "access_token=$IG_TOKEN") || BODY=""
    CONTAINER=$(printf '%s' "$BODY" | jq -r '.id // empty' 2>/dev/null)
    [ -n "$CONTAINER" ] && break
    sleep $((attempt * 10))
  done
  [ -n "$CONTAINER" ] || { echo "ERROR: $NAME container creation failed" >&2; exit 1; }
  STATUS=""
  for _ in 1 2 3 4 5 6 7 8 9 10; do
    BODY=$(call "$API/$CONTAINER?fields=status_code&access_token=$IG_TOKEN") \
      || { echo "ERROR: $NAME status poll failed" >&2; exit 1; }
    STATUS=$(printf '%s' "$BODY" | jq -r '.status_code // empty')
    [ "$STATUS" = "FINISHED" ] && break
    case "$STATUS" in ERROR|EXPIRED) echo "ERROR: $NAME container $STATUS: $BODY" >&2; exit 1;; esac
    sleep 6
  done
  [ "$STATUS" = "FINISHED" ] || { echo "ERROR: $NAME never reached FINISHED (last: $STATUS)" >&2; exit 1; }
  call -X POST "$API/$IG_USER_ID/media_publish" \
    -d "creation_id=$CONTAINER" -d "access_token=$IG_TOKEN" >/dev/null \
    || { echo "ERROR: $NAME publish failed" >&2; exit 1; }
  echo "published $NAME"
done
