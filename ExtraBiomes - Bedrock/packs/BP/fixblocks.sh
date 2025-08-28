#!/usr/bin/env bash
# fix_blocks_all.sh
# Usage: ./fix_blocks_all.sh path/to/blocks

BLOCKS_DIR="$1"

if ! command -v jq >/dev/null 2>&1; then
    echo "Error: jq not installed. Run: sudo apt install jq"
    exit 1
fi

find "$BLOCKS_DIR" -type f -name "*.json" | while read -r file; do
    echo "Processing $file"

    tmp="$(mktemp)"
    jq '
      . as $in
      | ( $in["minecraft:block"].components["minecraft:creative_category"]? ) as $cat
      | ( $in["minecraft:block"].components["minecraft:destroy_time"]? ) as $dtime
      | ( $in["minecraft:block"].components["minecraft:explosion_resistance"]? ) as $eres
      | ( $in["minecraft:block"].components["minecraft:block_light_absorption"]? ) as $labs
      | ( $in["minecraft:block"].components["minecraft:block_light_emission"]? ) as $lemi
      | $in
      # migrate creative category -> menu_category
      | if $cat != null then
          .["minecraft:block"].description.menu_category.category = $cat.category
          | if $cat.group != null then
              .["minecraft:block"].description.menu_category.group = $cat.group
            else . end
          | del(.["minecraft:block"].components["minecraft:creative_category"])
        else . end
      # migrate destroy_time
      | if $dtime != null then
          .["minecraft:block"].components["minecraft:destructible_by_mining"] = {
            "seconds_to_destroy": $dtime
          }
          | del(.["minecraft:block"].components["minecraft:destroy_time"])
        else . end
      # migrate explosion_resistance
      | if $eres != null then
          .["minecraft:block"].components["minecraft:destructible_by_explosion"] = {
            "explosion_resistance": $eres
          }
          | del(.["minecraft:block"].components["minecraft:explosion_resistance"])
        else . end
      # migrate light absorption -> dampening
      | if $labs != null then
          .["minecraft:block"].components["minecraft:light_dampening"] = $labs
          | del(.["minecraft:block"].components["minecraft:block_light_absorption"])
        else . end
      # migrate light emission
      | if $lemi != null then
          .["minecraft:block"].components["minecraft:light_emission"] = $lemi
          | del(.["minecraft:block"].components["minecraft:block_light_emission"])
        else . end
      # remove unit_cube (no longer needed)
      | del(.["minecraft:block"].components["minecraft:unit_cube"])
    ' "$file" > "$tmp" && mv "$tmp" "$file"
done
