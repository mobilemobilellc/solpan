#!/usr/bin/env bash
# Prints Play "What's new" text for one version from CHANGELOG.md: only the Features, Bug Fixes and
# Performance entries, without release-please's links, cut to Play's 500-character limit.
set -euo pipefail

version="$1"
changelog="${2:-CHANGELOG.md}"
limit=500

notes=$(awk -v v="$version" '
  /^## / { in_version = index($0, "[" v "]") > 0; next }
  !in_version { next }
  /^### / { keep = ($0 ~ /^### (Features|Bug Fixes|Performance)$/); next }
  keep && /^\* / { print }
' "$changelog" |
  sed -E \
    -e 's/ \(\[[^]]*\]\([^)]*\)\)//g' \
    -e 's/^\* \*\*[^*]*:\*\* /* /' \
    -e 's/^\* (.)/• \u\1/' |
  awk '!seen[$0]++')

if [[ -z "$notes" ]]; then
  notes="Bug fixes and improvements."
fi

# Keep whole lines only, so a cut never lands mid-sentence.
out=""
while IFS= read -r line; do
  candidate="${out:+$out$'\n'}$line"
  (( ${#candidate} > limit )) && break
  out="$candidate"
done <<< "$notes"

printf '%s\n' "$out"
