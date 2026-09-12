"""Bake the Java PillarWeatheringProcessor pass into Bedrock stone-pillar structures.

Usage:
    python tools/weather_pillars.py            # rewrites the *_weathered_*.mcstructure files

Java weathers its pillars at placement time with a StructureProcessor; Bedrock's
`structure_template_feature` has no processor hook, so the same gradient + vegetation
pass runs here instead and is baked into a handful of pre-weathered variants that the
feature picks between. Sampling is structure-local rather than world-local, so each
variant is fixed - VARIANTS is the knob for how much repetition a player can spot.

Mirrors PillarWeatheringProcessor's variant thresholds and vegetation chances; the noise
is a plain seeded value-noise field rather than Minecraft's PerlinSimplexNoise, and its
XZ scale is retuned for structure-local sampling (see WEATHER_SCALE_XZ).
"""
import math
import os
import sys

from mcstructure import T_byte, T_comp, T_int, T_list, T_str, load, save, to_py, TAG_COMPOUND, TAG_INT

BLOCK_VERSION = 18168865
VARIANTS = 2

# Java samples this field in WORLD coordinates, so its 0.07 XZ scale varies across
# pillars rather than within one. Baked variants get no such spread, so the XZ term is
# tightened (and given equal weight to the vertical one) until a single pillar footprint
# spans enough of the field to show the whole dark-base -> light-crest gradient.
WEATHER_SCALE_XZ = 0.15
WEATHER_SCALE_Y = 0.10
WEATHER_XZ_WEIGHT = 0.5
ASSUMED_MAX_TEMPLATE_HEIGHT = 100.0
GRADIENT_WEIGHT = 0.5

VEGETATION_SCALE = 0.05
VINE_CHANCE_LOW = 0.30
VINE_CHANCE_HIGH = 0.12
TOP_GROWTH_CHANCE = 0.30

STONE_FAMILY = {"minecraft:stone", "minecraft:mossy_cobblestone", "minecraft:cobblestone",
                "minecraft:andesite", "minecraft:diorite", "minecraft:cracked_stone_bricks",
                "minecraft:mossy_stone_bricks", "minecraft:deepslate", "minecraft:tuff"}

# Bedrock vine_direction_bits: the face the vine is attached to.
VINE_BITS = {"south": 1, "west": 2, "north": 4, "east": 8}
HORIZONTAL = {"north": (0, 0, -1), "south": (0, 0, 1), "east": (1, 0, 0), "west": (-1, 0, 0)}


class Noise:
    """Seeded 2D value noise in [-1, 1], smoothstep-interpolated so values cluster."""

    def __init__(self, seed):
        self.seed = seed

    def _at(self, ix, iy):
        h = (ix * 0x9E3779B97F4A7C15 + iy * 0xBF58476D1CE4E5B9 + self.seed * 0x94D049BB133111EB) & 0xFFFFFFFFFFFFFFFF
        h = ((h ^ (h >> 30)) * 0xBF58476D1CE4E5B9) & 0xFFFFFFFFFFFFFFFF
        h = ((h ^ (h >> 27)) * 0x94D049BB133111EB) & 0xFFFFFFFFFFFFFFFF
        return ((h ^ (h >> 31)) & 0xFFFFFFFF) / 0x7FFFFFFF - 1.0

    def value(self, x, y):
        ix, iy = math.floor(x), math.floor(y)
        fx, fy = x - ix, y - iy
        sx = fx * fx * (3 - 2 * fx)
        sy = fy * fy * (3 - 2 * fy)
        top = self._at(ix, iy) * (1 - sx) + self._at(ix + 1, iy) * sx
        bot = self._at(ix, iy + 1) * (1 - sx) + self._at(ix + 1, iy + 1) * sx
        return top * (1 - sy) + bot * sy


class Rng:
    """Deterministic per-variant stand-in for RandomSource.nextFloat()."""

    def __init__(self, seed):
        self.state = seed & 0xFFFFFFFFFFFFFFFF

    def next_float(self):
        self.state = (self.state * 6364136223846793005 + 1442695040888963407) & 0xFFFFFFFFFFFFFFFF
        return ((self.state >> 33) & 0x7FFFFF) / float(0x800000)


def pick_variant(n):
    """Darkest (deepslate, base) -> lightest (diorite, crest); the dead zone leaves bare stone."""
    if n < -0.85: return "minecraft:deepslate"
    if n < -0.65: return "minecraft:tuff"
    if n < -0.45: return "minecraft:mossy_stone_bricks"
    if n < -0.25: return "minecraft:cracked_stone_bricks"
    if n < -0.05: return "minecraft:mossy_cobblestone"
    if n < 0.05: return None
    if n < 0.25: return "minecraft:cobblestone"
    if n < 0.45: return "minecraft:andesite"
    if n < 0.65: return "minecraft:diorite"
    return None


class Structure:
    def __init__(self, path):
        _, root = load(path)
        d = to_py(root)
        self.sx, self.sy, self.sz = d["size"]
        self.origin = d["structure_world_origin"]
        pal = d["structure"]["palette"]["default"]["block_palette"]
        self.names = [e["name"] for e in pal]
        self.indices = list(d["structure"]["block_indices"][0])
        self.cells = {}
        for flat, pidx in enumerate(self.indices):
            if pidx >= 0:
                self.cells[self.coord(flat)] = (self.names[pidx], {})

    def coord(self, flat):
        return (flat // (self.sy * self.sz), (flat // self.sz) % self.sy, flat % self.sz)

    def flat(self, pos):
        x, y, z = pos
        return (x * self.sy + y) * self.sz + z

    def inside(self, pos):
        x, y, z = pos
        return 0 <= x < self.sx and 0 <= y < self.sy and 0 <= z < self.sz

    def name_at(self, pos):
        entry = self.cells.get(pos)
        return entry[0] if entry else None

    def is_open(self, pos):
        """Void or air, or off the template entirely - i.e. nothing solid stands here."""
        name = self.name_at(pos)
        return name is None or name == "minecraft:air"

    def save(self, path):
        palette, index_of, indices = [], {}, [-1] * (self.sx * self.sy * self.sz)
        for pos, (name, states) in self.cells.items():
            key = (name, tuple(sorted((k, t.value) for k, t in states.items())))
            if key not in index_of:
                index_of[key] = len(palette)
                palette.append(T_comp({"name": T_str(name), "states": T_comp(states),
                                       "version": T_int(BLOCK_VERSION)}))
            indices[self.flat(pos)] = index_of[key]
        root = T_comp({
            "format_version": T_int(1),
            "size": T_list([T_int(self.sx), T_int(self.sy), T_int(self.sz)], TAG_INT),
            "structure": T_comp({
                "block_indices": T_list([
                    T_list([T_int(i) for i in indices], TAG_INT),
                    T_list([T_int(-1)] * len(indices), TAG_INT),
                ], 9),
                "entities": T_list([], TAG_COMPOUND),
                "palette": T_comp({"default": T_comp({
                    "block_palette": T_list(palette, TAG_COMPOUND),
                    "block_position_data": T_comp({}),
                })}),
            }),
            "structure_world_origin": T_list([T_int(v) for v in self.origin], TAG_INT),
        })
        save(path, root)


def reskin(struct, weather):
    for pos, (name, _) in list(struct.cells.items()):
        if name != "minecraft:stone":
            continue
        x, y, z = pos
        xz = weather.value(x * WEATHER_SCALE_XZ, z * WEATHER_SCALE_XZ)
        vertical = weather.value(y * WEATHER_SCALE_Y, (x - z) * WEATHER_SCALE_Y * 0.5)
        n = xz * WEATHER_XZ_WEIGHT + vertical * (1 - WEATHER_XZ_WEIGHT)
        height_fraction = min(1.0, max(0.0, y / ASSUMED_MAX_TEMPLATE_HEIGHT))
        variant = pick_variant(n + (height_fraction - 0.5) * 2.0 * GRADIENT_WEIGHT)
        if variant:
            struct.cells[pos] = (variant, {})


def is_exterior_face(struct, n, approach):
    """Reject a cell boxed in on every side but the one it was reached from - a notch, not a wall."""
    dx, dy, dz = HORIZONTAL[approach]
    for direction, (ox, oy, oz) in HORIZONTAL.items():
        if (ox, oy, oz) in ((dx, dy, dz), (-dx, -dy, -dz)):
            continue
        if struct.is_open((n[0] + ox, n[1] + oy, n[2] + oz)):
            return True
    return False


def is_wide_ledge(struct, stone_positions, pos):
    for ox, oy, oz in HORIZONTAL.values():
        n = (pos[0] + ox, pos[1] + oy, pos[2] + oz)
        if n not in stone_positions or not struct.is_open((n[0], n[1] + 1, n[2])):
            return False
    return True


def grow_vegetation(struct, vegetation, soil, rng):
    stone_positions = {p for p, (name, _) in struct.cells.items() if name in STONE_FAMILY}

    def density(pos):
        return max(0.0, (vegetation.value(pos[0] * VEGETATION_SCALE, pos[2] * VEGETATION_SCALE) + 1.0) / 2.0)

    for pos in sorted(stone_positions):
        x, y, z = pos
        for direction, (ox, oy, oz) in HORIZONTAL.items():
            n = (x + ox, y + oy, z + oz)
            beyond = (n[0] + ox, n[1] + oy, n[2] + oz)
            if not struct.is_open(n) or not struct.is_open(beyond) or not is_exterior_face(struct, n, direction):
                continue
            chance = (VINE_CHANCE_LOW if y < struct.sy * 0.45 else VINE_CHANCE_HIGH) * density(n)
            if rng.next_float() >= chance or not struct.inside(n):
                continue
            opposite = {"north": "south", "south": "north", "east": "west", "west": "east"}[direction]
            bits = VINE_BITS[opposite]
            existing = struct.cells.get(n)
            if existing and existing[0] == "minecraft:vine":
                bits |= existing[1]["vine_direction_bits"].value
            struct.cells[n] = ("minecraft:vine", {"vine_direction_bits": T_int(bits)})

        above = (x, y + 1, z)
        if not struct.is_open(above):
            continue
        if rng.next_float() >= TOP_GROWTH_CHANCE * density(above):
            continue

        mossy = soil.value(x * VEGETATION_SCALE, z * VEGETATION_SCALE) < 0.0
        struct.cells[pos] = ("minecraft:moss_block" if mossy else "minecraft:grass_block", {})

        if not struct.inside(above):
            continue
        roll = rng.next_float()
        wide = is_wide_ledge(struct, stone_positions, pos)
        if wide and roll < 0.12:
            struct.cells[above] = ("minecraft:jungle_sapling", {"age_bit": T_byte(0)})
        elif wide and roll < 0.22:
            struct.cells[above] = ("minecraft:bamboo", {"age_bit": T_byte(0),
                                                        "bamboo_leaf_size": T_str("large_leaves"),
                                                        "bamboo_stalk_thickness": T_str("thin")})
        elif roll < 0.34:
            struct.cells[above] = ("minecraft:flowering_azalea" if mossy else "minecraft:azalea", {})
        elif roll < 0.55:
            struct.cells[above] = ("minecraft:fern" if mossy else "minecraft:short_grass", {})


def main(structures_dir):
    for shape in (1, 2, 3):
        src = os.path.join(structures_dir, "stone_pillar_%d.mcstructure" % shape)
        for variant in range(VARIANTS):
            seed = shape * 1000 + variant
            struct = Structure(src)
            reskin(struct, Noise(5551 + seed))
            grow_vegetation(struct, Noise(9113 + seed), Noise(7331 + seed), Rng(seed))
            dst = os.path.join(structures_dir, "stone_pillar_%d_weathered_%d.mcstructure" % (shape, variant + 1))
            struct.save(dst)
            print("wrote", os.path.basename(dst))


if __name__ == "__main__":
    main(sys.argv[1] if len(sys.argv) > 1 else
         "ExtraBiomes - Bedrock/packs/BP/structures/extrabiomes")
