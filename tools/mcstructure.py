"""Minimal little-endian NBT reader/writer for Bedrock .mcstructure files."""
import struct

TAG_END, TAG_BYTE, TAG_SHORT, TAG_INT, TAG_LONG, TAG_FLOAT, TAG_DOUBLE = 0, 1, 2, 3, 4, 5, 6
TAG_BYTE_ARRAY, TAG_STRING, TAG_LIST, TAG_COMPOUND, TAG_INT_ARRAY, TAG_LONG_ARRAY = 7, 8, 9, 10, 11, 12


class Tag:
    __slots__ = ("type", "value", "list_type")

    def __init__(self, tag_type, value, list_type=None):
        self.type = tag_type
        self.value = value
        self.list_type = list_type

    def __repr__(self):
        return f"Tag({self.type}, {self.value!r})"


def _read_string(buf, pos):
    (length,) = struct.unpack_from("<H", buf, pos)
    pos += 2
    s = buf[pos:pos + length].decode("utf-8")
    return s, pos + length


def _read_payload(buf, pos, tag_type):
    if tag_type == TAG_BYTE:
        return Tag(tag_type, struct.unpack_from("<b", buf, pos)[0]), pos + 1
    if tag_type == TAG_SHORT:
        return Tag(tag_type, struct.unpack_from("<h", buf, pos)[0]), pos + 2
    if tag_type == TAG_INT:
        return Tag(tag_type, struct.unpack_from("<i", buf, pos)[0]), pos + 4
    if tag_type == TAG_LONG:
        return Tag(tag_type, struct.unpack_from("<q", buf, pos)[0]), pos + 8
    if tag_type == TAG_FLOAT:
        return Tag(tag_type, struct.unpack_from("<f", buf, pos)[0]), pos + 4
    if tag_type == TAG_DOUBLE:
        return Tag(tag_type, struct.unpack_from("<d", buf, pos)[0]), pos + 8
    if tag_type == TAG_BYTE_ARRAY:
        (n,) = struct.unpack_from("<i", buf, pos)
        pos += 4
        return Tag(tag_type, list(struct.unpack_from(f"<{n}b", buf, pos))), pos + n
    if tag_type == TAG_STRING:
        s, pos = _read_string(buf, pos)
        return Tag(tag_type, s), pos
    if tag_type == TAG_LIST:
        item_type = buf[pos]
        (n,) = struct.unpack_from("<i", buf, pos + 1)
        pos += 5
        items = []
        for _ in range(n):
            item, pos = _read_payload(buf, pos, item_type)
            items.append(item)
        return Tag(tag_type, items, list_type=item_type), pos
    if tag_type == TAG_COMPOUND:
        d = {}
        while True:
            child_type = buf[pos]
            pos += 1
            if child_type == TAG_END:
                break
            name, pos = _read_string(buf, pos)
            child, pos = _read_payload(buf, pos, child_type)
            d[name] = child
        return Tag(tag_type, d), pos
    if tag_type == TAG_INT_ARRAY:
        (n,) = struct.unpack_from("<i", buf, pos)
        pos += 4
        return Tag(tag_type, list(struct.unpack_from(f"<{n}i", buf, pos))), pos + 4 * n
    if tag_type == TAG_LONG_ARRAY:
        (n,) = struct.unpack_from("<i", buf, pos)
        pos += 4
        return Tag(tag_type, list(struct.unpack_from(f"<{n}q", buf, pos))), pos + 8 * n
    raise ValueError(f"unknown tag type {tag_type} at {pos}")


def load(path):
    """Return (root_name, root_tag) from an uncompressed little-endian NBT file."""
    buf = open(path, "rb").read()
    tag_type = buf[0]
    name, pos = _read_string(buf, 1)
    root, pos = _read_payload(buf, pos, tag_type)
    if pos != len(buf):
        raise ValueError(f"trailing bytes: read {pos} of {len(buf)}")
    return name, root


def _write_string(out, s):
    b = s.encode("utf-8")
    out += struct.pack("<H", len(b)) + b
    return out


def _write_payload(out, tag):
    t = tag.type
    if t == TAG_BYTE:
        return out + struct.pack("<b", tag.value)
    if t == TAG_SHORT:
        return out + struct.pack("<h", tag.value)
    if t == TAG_INT:
        return out + struct.pack("<i", tag.value)
    if t == TAG_LONG:
        return out + struct.pack("<q", tag.value)
    if t == TAG_FLOAT:
        return out + struct.pack("<f", tag.value)
    if t == TAG_DOUBLE:
        return out + struct.pack("<d", tag.value)
    if t == TAG_BYTE_ARRAY:
        return out + struct.pack("<i", len(tag.value)) + struct.pack(f"<{len(tag.value)}b", *tag.value)
    if t == TAG_STRING:
        return _write_string(out, tag.value)
    if t == TAG_LIST:
        item_type = tag.list_type if tag.list_type is not None else (tag.value[0].type if tag.value else TAG_END)
        out += struct.pack("<bi", item_type, len(tag.value))
        for item in tag.value:
            out = _write_payload(out, item)
        return out
    if t == TAG_COMPOUND:
        for name, child in tag.value.items():
            out += struct.pack("<b", child.type)
            out = _write_string(out, name)
            out = _write_payload(out, child)
        return out + b"\x00"
    if t == TAG_INT_ARRAY:
        return out + struct.pack("<i", len(tag.value)) + struct.pack(f"<{len(tag.value)}i", *tag.value)
    if t == TAG_LONG_ARRAY:
        return out + struct.pack("<i", len(tag.value)) + struct.pack(f"<{len(tag.value)}q", *tag.value)
    raise ValueError(f"unknown tag type {t}")


def save(path, root, root_name=""):
    out = struct.pack("<b", root.type)
    out = _write_string(out, root_name)
    out = _write_payload(out, root)
    with open(path, "wb") as f:
        f.write(out)


# ---- convenience constructors ----
def T_byte(v):
    return Tag(TAG_BYTE, v)

def T_int(v):
    return Tag(TAG_INT, v)

def T_str(v):
    return Tag(TAG_STRING, v)

def T_list(items, item_type):
    return Tag(TAG_LIST, items, list_type=item_type)

def T_comp(d):
    return Tag(TAG_COMPOUND, d)


def to_py(tag):
    """Convert a Tag tree to plain python for printing."""
    if tag.type == TAG_COMPOUND:
        return {k: to_py(v) for k, v in tag.value.items()}
    if tag.type == TAG_LIST:
        return [to_py(v) for v in tag.value]
    return tag.value
