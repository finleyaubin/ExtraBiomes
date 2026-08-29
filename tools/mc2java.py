"""Convert Bedrock .mcstructure files to Java Edition 1.20.1 structure .nbt.

Usage:
    python tools/mc2java.py <input.mcstructure> <output.nbt>
    python tools/mc2java.py --batch <srcdir> <dstdir>

Batch mode walks <srcdir> recursively, converting every *.mcstructure to a
gzip-compressed Java structure .nbt under <dstdir>, preserving the subfolder
tree and LOWERCASING every output filename (Java resource locations are all
lowercase, so ``paths/T.mcstructure`` -> ``paths/t.nbt``).

Reads Bedrock (little-endian NBT) via the existing ``mcstructure`` module and
writes Java (big-endian, gzip) NBT with the small hand-rolled writer below.
"""
import os
import sys
import gzip
import struct
from collections import Counter, OrderedDict

from mcstructure import (
    load, to_py, Tag, T_byte, T_int, T_str, T_list, T_comp,
    TAG_END, TAG_BYTE, TAG_SHORT, TAG_INT, TAG_LONG, TAG_FLOAT, TAG_DOUBLE,
    TAG_BYTE_ARRAY, TAG_STRING, TAG_LIST, TAG_COMPOUND, TAG_INT_ARRAY, TAG_LONG_ARRAY,
)
import block_map


# ---------------------------------------------------------------------------
# big-endian NBT writer (Java uses big-endian; do NOT reuse the LE writer)
# ---------------------------------------------------------------------------

def _wstr(out, s):
    b = s.encode("utf-8")
    return out + struct.pack(">H", len(b)) + b


def _wpayload(out, tag):
    t = tag.type
    if t == TAG_BYTE:
        return out + struct.pack(">b", tag.value)
    if t == TAG_SHORT:
        return out + struct.pack(">h", tag.value)
    if t == TAG_INT:
        return out + struct.pack(">i", tag.value)
    if t == TAG_LONG:
        return out + struct.pack(">q", tag.value)
    if t == TAG_FLOAT:
        return out + struct.pack(">f", tag.value)
    if t == TAG_DOUBLE:
        return out + struct.pack(">d", tag.value)
    if t == TAG_STRING:
        return _wstr(out, tag.value)
    if t == TAG_LIST:
        item_type = tag.list_type if tag.list_type is not None else (
            tag.value[0].type if tag.value else TAG_END)
        out += struct.pack(">b", item_type) + struct.pack(">i", len(tag.value))
        for item in tag.value:
            out = _wpayload(out, item)
        return out
    if t == TAG_COMPOUND:
        for name, child in tag.value.items():
            out += struct.pack(">b", child.type)
            out = _wstr(out, name)
            out = _wpayload(out, child)
        return out + b"\x00"
    if t == TAG_INT_ARRAY:
        out += struct.pack(">i", len(tag.value))
        return out + struct.pack(">%di" % len(tag.value), *tag.value)
    if t == TAG_LONG_ARRAY:
        out += struct.pack(">i", len(tag.value))
        return out + struct.pack(">%dq" % len(tag.value), *tag.value)
    raise ValueError("unknown tag type %d" % t)


def write_java_nbt(path, root, root_name=""):
    """Write a gzip-compressed big-endian NBT file (Java structure format)."""
    out = struct.pack(">b", root.type)
    out = _wstr(out, root_name)
    out = _wpayload(out, root)
    with gzip.open(path, "wb") as f:
        f.write(out)


# ---------------------------------------------------------------------------
# conversion
# ---------------------------------------------------------------------------

DATA_VERSION = 3465  # Minecraft 1.20.1


def _props_comp(props):
    """dict[str,str] -> TAG_Compound of TAG_String, or None if empty."""
    if not props:
        return None
    return T_comp(OrderedDict((k, T_str(v)) for k, v in props.items()))


def _build_jigsaw_be(be, warnings):
    """Build the Java jigsaw block-entity compound from Bedrock JigsawBlock data."""
    d = OrderedDict()
    d["id"] = T_str("minecraft:jigsaw")
    d["name"] = T_str(be.get("name", "minecraft:empty"))
    d["target"] = T_str(be.get("target", "minecraft:empty"))
    # target_pool (Bedrock) -> pool (Java), verbatim.
    d["pool"] = T_str(be.get("target_pool", "minecraft:empty"))
    d["final_state"] = T_str(block_map.map_final_state(be.get("final_state", "air")))
    d["joint"] = T_str(be.get("joint", "rollable"))
    d["selection_priority"] = T_int(int(be.get("selection_priority", 0)))
    d["placement_priority"] = T_int(int(be.get("placement_priority", 0)))
    return T_comp(d)


def _build_sign_be(be):
    """Carry sign text into a Java 1.20 sign block entity, or None if empty."""
    front = be.get("FrontText", {}) or {}
    back = be.get("BackText", {}) or {}
    ftext = front.get("Text", "") or ""
    btext = back.get("Text", "") or ""
    if not ftext and not btext:
        return None

    def side(text):
        # One JSON string message per line; pad to the 4 lines Java expects.
        lines = text.split("\n")[:4]
        while len(lines) < 4:
            lines.append("")
        msgs = T_list([T_str('"%s"' % ln.replace('"', '\\"')) for ln in lines], TAG_STRING)
        return T_comp(OrderedDict([
            ("has_glowing_text", T_byte(0)),
            ("color", T_str("black")),
            ("messages", msgs),
        ]))

    d = OrderedDict()
    d["id"] = T_str("minecraft:sign")
    d["is_waxed"] = T_byte(1 if be.get("IsWaxed") in (1, True) else 0)
    d["front_text"] = side(ftext)
    d["back_text"] = side(btext)
    return T_comp(d)


# Bedrock loot table paths (as referenced by container block_entity_data's
# "LootTable" field, relative to a behavior pack's loot_tables/ folder) that
# have a known Java-side equivalent resource location. Add an entry here
# before a structure containing a Barrel/Chest with that loot table can carry
# it through conversion - anything not listed is left empty (with a warning)
# rather than guessing.
LOOT_TABLE_MAP = {
    "loot_tables/chests/shipwrecktreasure.json": "minecraft:chests/shipwreck_treasure",
    # Custom loot tables (no vanilla equivalent) hand-ported to
    # data/extrabiomes/loot_tables/chests/*.json - see those files for the
    # item-id fixups applied versus the Bedrock source (legacy ids like
    # "appleEnchanted"/"horsearmorgold"/"record_otherside"/"web" -> their
    # modern Java equivalents).
    "loot_tables/chests/windmill.json": "extrabiomes:chests/windmill",
    "loot_tables/chests/common_skycity.json": "extrabiomes:chests/common_skycity",
    "loot_tables/chests/rare_skycity.json": "extrabiomes:chests/rare_skycity",
    "loot_tables/chests/epic_skycity.json": "extrabiomes:chests/epic_skycity",
}


def _build_container_be(java_id, be, warnings):
    """Carry a Bedrock Barrel/Chest's LootTable reference into a Java container
    block entity, so the container auto-fills from that loot table on first
    open (Java honors a "LootTable" tag with no "Items" the same way)."""
    loot = be.get("LootTable")
    if not loot:
        return None
    java_loot = LOOT_TABLE_MAP.get(loot)
    if java_loot is None:
        warnings.append("unmapped loot table: %s" % loot)
        return None
    d = OrderedDict()
    d["id"] = T_str(java_id)
    d["LootTable"] = T_str(java_loot)
    return T_comp(d)


def _build_spawner_be(be):
    """Best-effort carry of the Bedrock spawner entity into a Java spawner."""
    ent_id = be.get("EntityIdentifier")
    if not ent_id:
        return None
    spawn_data = T_comp(OrderedDict([
        ("entity", T_comp(OrderedDict([("id", T_str(ent_id))]))),
    ]))
    d = OrderedDict()
    d["id"] = T_str("minecraft:mob_spawner")
    d["SpawnData"] = spawn_data
    return T_comp(d)


def _is_pane(java_name):
    return java_name == "minecraft:glass_pane" or java_name.endswith("_glass_pane")


def _is_fence(java_name):
    return java_name.endswith("_fence") and not java_name.endswith("_fence_gate")


# Structures are exported piece-by-piece, so a pane/fence at a piece's edge
# can't see the neighbor that will sit there once jigsaw assembly places the
# adjacent piece - and relying on Java's own post-placement shape recompute
# for this turned out to *not* reliably fire for structure-placed blocks
# (confirmed empirically: panes/fences shipped with recompute-only state were
# rendering disconnected in-game). So bake real connection states here from
# each structure's own neighbor layout - anything solid (non-air) in a
# cardinal direction counts as connected, matching vanilla's own connect-to-
# solid-face behavior closely enough for hand-built decorative structures.
_CONNECT_DELTAS = {"north": (0, 0, -1), "south": (0, 0, 1), "east": (1, 0, 0), "west": (-1, 0, 0)}


def convert(path, warnings, id_counter):
    """Read a .mcstructure and return the Java structure root Tag."""
    _, root = load(path)
    r = to_py(root)
    sx, sy, sz = r["size"]
    struct_d = r["structure"]
    layers = struct_d["block_indices"]
    layer0 = layers[0]
    layer1 = layers[1] if len(layers) > 1 else None
    palette = struct_d["palette"]["default"]["block_palette"]
    bpd = struct_d["palette"]["default"].get("block_position_data", {}) or {}

    # Java palette de-dup: (Name, tuple(sorted props)) -> index.
    pal_index = OrderedDict()
    pal_entries = []  # list of TAG_Compound

    def palette_id(java_name, props):
        key = (java_name, tuple(sorted(props.items())))
        if key not in pal_index:
            pal_index[key] = len(pal_entries)
            comp = OrderedDict([("Name", T_str(java_name))])
            pc = _props_comp(props)
            if pc is not None:
                comp["Properties"] = pc
            pal_entries.append(T_comp(comp))
        return pal_index[key]

    def coord(flat):
        x = flat // (sy * sz)
        y = (flat // sz) % sy
        z = flat % sz
        return x, y, z

    def is_water_source(pidx):
        if pidx is None or pidx < 0:
            return False
        e = palette[pidx]
        return e["name"] == "minecraft:water" and int(e.get("states", {}).get("liquid_depth", 0)) == 0

    def has_solid_neighbor(flat, direction):
        x, y, z = coord(flat)
        dx, dy, dz = _CONNECT_DELTAS[direction]
        nx, ny, nz = x + dx, y + dy, z + dz
        if not (0 <= nx < sx and 0 <= ny < sy and 0 <= nz < sz):
            return False
        npidx = layer0[(nx * sy + ny) * sz + nz]
        return npidx >= 0 and palette[npidx]["name"] != "minecraft:air"

    blocks = []
    for flat, pidx in enumerate(layer0):
        if pidx < 0:  # void -> emit nothing
            continue
        entry = palette[pidx]
        name = entry["name"]
        states = entry.get("states", {})
        id_counter[name] += 1

        be = None
        be_wrap = bpd.get(str(flat))
        if be_wrap:
            be = be_wrap.get("block_entity_data")

        # --- resolve Java block + props ---
        if name == "minecraft:jigsaw":
            java_name = "minecraft:jigsaw"
            fd = states.get("facing_direction", 2)
            rot = states.get("rotation", 0)
            orientation, warn = block_map.jigsaw_orientation(fd, rot)
            if warn:
                warnings.append("%s: %s" % (os.path.basename(path), warn))
            props = {"orientation": orientation}
        else:
            java_name, props = block_map.map_block(name, states, be)
            if java_name is None:
                warnings.append("unmapped block id: %s states=%s" % (name, states))
                java_name, props = "minecraft:air", {}

            if _is_pane(java_name) or _is_fence(java_name):
                for direction in _CONNECT_DELTAS:
                    props[direction] = "true" if has_solid_neighbor(flat, direction) else "false"

            # Bedrock only actually uses the LOWER door half's direction/hinge for
            # rendering in-game - the upper half's own direction/door_hinge_bit is
            # vestigial and frequently stale/inconsistent with the half below it
            # (confirmed against windmill.mcstructure: its door's lower half has
            # direction=1/west, upper has direction=0/south). Java's DoorBlock, unlike
            # Bedrock, renders each half from its OWN stored facing, so copying the
            # upper half's raw state literally produces a door whose two halves face
            # different directions (looks visibly broken/split in-world). Overwrite
            # with the lower half's real facing/hinge instead of trusting the upper's.
            if name == "minecraft:spruce_door" and states.get("upper_block_bit") in (1, True):
                below_pidx = layer0[flat - sz] if flat - sz >= 0 else -1
                if below_pidx is not None and below_pidx >= 0:
                    below_entry = palette[below_pidx]
                    if below_entry["name"] == "minecraft:spruce_door":
                        below_states = below_entry.get("states", {})
                        props["facing"] = block_map.BED_DIRECTION.get(int(below_states.get("direction", 0)), "south")
                        props["hinge"] = "right" if below_states.get("door_hinge_bit") in (1, True) else "left"

        # --- waterlogging from secondary layer ---
        if "waterlogged" in props and layer1 is not None:
            if is_water_source(layer1[flat]):
                props["waterlogged"] = "true"

        state_idx = palette_id(java_name, props)

        blk = OrderedDict([
            ("state", T_int(state_idx)),
            ("pos", T_list([T_int(c) for c in coord(flat)], TAG_INT)),
        ])

        # --- block entity ---
        nbt = None
        if be:
            beid = be.get("id")
            if name == "minecraft:jigsaw" or beid == "JigsawBlock":
                nbt = _build_jigsaw_be(be, warnings)
            elif beid == "Sign":
                nbt = _build_sign_be(be)
            elif beid == "MobSpawner":
                nbt = _build_spawner_be(be)
            elif beid == "Barrel":
                nbt = _build_container_be("minecraft:barrel", be, warnings)
            elif beid in ("Chest", "ChestBlock"):
                nbt = _build_container_be("minecraft:chest", be, warnings)
        if nbt is not None:
            blk["nbt"] = nbt

        blocks.append(T_comp(blk))

    root_d = OrderedDict()
    root_d["size"] = T_list([T_int(sx), T_int(sy), T_int(sz)], TAG_INT)
    root_d["entities"] = T_list([], TAG_COMPOUND)
    root_d["blocks"] = T_list(blocks, TAG_COMPOUND)
    root_d["palette"] = T_list(pal_entries, TAG_COMPOUND)
    root_d["DataVersion"] = T_int(DATA_VERSION)
    return T_comp(root_d)


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def convert_one(src, dst, warnings, id_counter):
    os.makedirs(os.path.dirname(dst) or ".", exist_ok=True)
    root = convert(src, warnings, id_counter)
    write_java_nbt(dst, root)


def main(argv):
    warnings = []
    id_counter = Counter()

    if len(argv) >= 2 and argv[0] == "--batch":
        srcdir, dstdir = argv[1], argv[2]
        count = 0
        for dirpath, _dirs, files in os.walk(srcdir):
            for fn in files:
                if not fn.endswith(".mcstructure"):
                    continue
                src = os.path.join(dirpath, fn)
                rel = os.path.relpath(src, srcdir)
                # lowercase every path component + swap extension
                rel_lower = rel.replace("\\", "/").lower()
                rel_out = rel_lower[: -len(".mcstructure")] + ".nbt"
                dst = os.path.join(dstdir, rel_out)
                convert_one(src, dst, warnings, id_counter)
                count += 1
        print("Converted %d files -> %s" % (count, dstdir))
    elif len(argv) == 2:
        convert_one(argv[0], argv[1], warnings, id_counter)
        print("Converted %s -> %s" % (argv[0], argv[1]))
    else:
        print(__doc__)
        return 1

    print("\nUnique Bedrock block ids seen: %d" % len(id_counter))
    for name in sorted(id_counter):
        print("  %6d  %s" % (id_counter[name], name))

    print("\nWARNINGS: %d" % len(warnings))
    for w in sorted(set(warnings)):
        n = warnings.count(w)
        print("  [x%d] %s" % (n, w))
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
