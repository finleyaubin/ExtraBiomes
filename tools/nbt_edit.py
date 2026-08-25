"""Minimal typed round-trip reader/writer for Java-edition structure .nbt files (gzip, big-endian).

Unlike verify_java_nbt.py (read-only, loses tag types), this keeps every tag's type alongside its
value so a tree can be read, edited, and written back byte-faithful (modulo tag ordering, which is
preserved since compounds are stored as ordered (name, Tag) lists).
"""
import gzip, struct

TAG_END, TAG_BYTE, TAG_SHORT, TAG_INT, TAG_LONG, TAG_FLOAT, TAG_DOUBLE = 0, 1, 2, 3, 4, 5, 6
TAG_BYTE_ARRAY, TAG_STRING, TAG_LIST, TAG_COMPOUND, TAG_INT_ARRAY, TAG_LONG_ARRAY = 7, 8, 9, 10, 11, 12


class Tag:
    __slots__ = ("type", "value")

    def __init__(self, type_, value):
        self.type = type_
        self.value = value

    def __repr__(self):
        return f"Tag({self.type}, {self.value!r})"


def _rstr(b, p):
    (n,) = struct.unpack_from(">H", b, p)
    p += 2
    return b[p:p + n].decode("utf-8"), p + n


def _wstr(s):
    enc = s.encode("utf-8")
    return struct.pack(">H", len(enc)) + enc


def read_tag(b, p, t):
    if t == TAG_BYTE:
        v = struct.unpack_from(">b", b, p)[0]; return Tag(t, v), p + 1
    if t == TAG_SHORT:
        v = struct.unpack_from(">h", b, p)[0]; return Tag(t, v), p + 2
    if t == TAG_INT:
        v = struct.unpack_from(">i", b, p)[0]; return Tag(t, v), p + 4
    if t == TAG_LONG:
        v = struct.unpack_from(">q", b, p)[0]; return Tag(t, v), p + 8
    if t == TAG_FLOAT:
        v = struct.unpack_from(">f", b, p)[0]; return Tag(t, v), p + 4
    if t == TAG_DOUBLE:
        v = struct.unpack_from(">d", b, p)[0]; return Tag(t, v), p + 8
    if t == TAG_STRING:
        v, p = _rstr(b, p); return Tag(t, v), p
    if t == TAG_BYTE_ARRAY:
        (n,) = struct.unpack_from(">i", b, p); p += 4
        v = list(struct.unpack_from(">%db" % n, b, p)); return Tag(t, v), p + n
    if t == TAG_INT_ARRAY:
        (n,) = struct.unpack_from(">i", b, p); p += 4
        v = list(struct.unpack_from(">%di" % n, b, p)); return Tag(t, v), p + 4 * n
    if t == TAG_LONG_ARRAY:
        (n,) = struct.unpack_from(">i", b, p); p += 4
        v = list(struct.unpack_from(">%dq" % n, b, p)); return Tag(t, v), p + 8 * n
    if t == TAG_LIST:
        elem_t = b[p]
        (n,) = struct.unpack_from(">i", b, p + 1)
        p += 5
        items = []
        for _ in range(n):
            it, p = read_tag(b, p, elem_t)
            items.append(it)
        return Tag(t, (elem_t, items)), p
    if t == TAG_COMPOUND:
        entries = []
        while True:
            ct = b[p]; p += 1
            if ct == TAG_END:
                break
            name, p = _rstr(b, p)
            val, p = read_tag(b, p, ct)
            entries.append((name, val))
        return Tag(t, entries), p
    raise ValueError("unhandled tag %d at %d" % (t, p))


def write_tag(tag):
    t, v = tag.type, tag.value
    if t == TAG_BYTE:
        return struct.pack(">b", v)
    if t == TAG_SHORT:
        return struct.pack(">h", v)
    if t == TAG_INT:
        return struct.pack(">i", v)
    if t == TAG_LONG:
        return struct.pack(">q", v)
    if t == TAG_FLOAT:
        return struct.pack(">f", v)
    if t == TAG_DOUBLE:
        return struct.pack(">d", v)
    if t == TAG_STRING:
        return _wstr(v)
    if t == TAG_BYTE_ARRAY:
        return struct.pack(">i", len(v)) + struct.pack(">%db" % len(v), *v)
    if t == TAG_INT_ARRAY:
        return struct.pack(">i", len(v)) + struct.pack(">%di" % len(v), *v)
    if t == TAG_LONG_ARRAY:
        return struct.pack(">i", len(v)) + struct.pack(">%dq" % len(v), *v)
    if t == TAG_LIST:
        elem_t, items = v
        out = bytes([elem_t]) + struct.pack(">i", len(items))
        for it in items:
            out += write_tag(it)
        return out
    if t == TAG_COMPOUND:
        out = b""
        for name, val in v:
            out += bytes([val.type]) + _wstr(name) + write_tag(val)
        out += bytes([TAG_END])
        return out
    raise ValueError("unhandled tag %d" % t)


def load(path):
    """Returns (root_name, root_Tag) where root_Tag.type == TAG_COMPOUND."""
    b = gzip.open(path, "rb").read()
    t = b[0]
    name, p = _rstr(b, 1)
    tag, p = read_tag(b, p, t)
    assert p == len(b), "trailing bytes %d/%d" % (p, len(b))
    return name, tag


def save(path, name, tag):
    body = bytes([tag.type]) + _wstr(name) + write_tag(tag)
    with open(path, "wb") as f:
        with gzip.GzipFile(filename="", mode="wb", fileobj=f, mtime=0) as gz:
            gz.write(body)


def compound_get(tag, key):
    for n, v in tag.value:
        if n == key:
            return v
    return None
