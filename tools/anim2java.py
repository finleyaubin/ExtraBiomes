#!/usr/bin/env python3
"""Accurately port a Bedrock entity .animation.json (molang-driven, procedural) into a Java
HierarchicalModel.setupAnim() body, by transpiling the molang expressions to Java.

These ExtraBiomes animations are NOT keyframe animations - every channel is a molang expression
(math.sin/cos/abs/clamp over query.* / variable.*), so the vanilla keyframe AnimationDefinition
system does not fit. Instead we translate each expression to a Java float expression and apply it
directly to the model's ModelPart fields (which tools/geo2java.py emits, one per bone).

Conventions (verified against the geo2java geometry transform + vanilla KeyframeAnimations):
  * rotation is in DEGREES, applied directly (no axis flip): part.<axis>Rot += expr * DEG2RAD
  * position: Y is negated (Bedrock Y-up -> Java model Y-down): x += ex, y += -ey, z += ez
  * scale: applied directly (base pose scale is 1): part.<axis>Scale = expr
  * Bedrock math.sin/cos take DEGREES -> Mth.sin/cos(expr * DEG2RAD)
  * `this` (as in `-this`, `expr - this`) = the bone's current value; for a single animation on the
    rest pose that is 0, so `this` -> 0f. (`-this` therefore leaves an axis at rest.)

query/variable -> Java driver mapping:
  query.modified_distance_moved -> limbSwing         query.modified_move_speed -> limbSwingAmount
  query.life_time               -> ageInTicks/20     query.vertical_speed      -> vspeed (deltaMovement.y)
  query.anim_time               -> limbSwing if the animation's anim_time_update is
                                    query.modified_distance_moved, else ageInTicks/20
  query.has_target              -> hasTarget (1f/0f)
  variable.animationamountblend -> ageInTicks   variable.gliding_speed_value -> 1f   others -> 0f

Usage:
  python tools/anim2java.py <animation.json> <geo.json> [anim1 anim2 ...]
    (anim names without the "animation.<entity>." prefix; omit to include every looping animation)
"""
import json, re, sys, os

DEG2RAD = "0.017453292f"


class Tok:
    def __init__(self, kind, val):
        self.kind = kind
        self.val = val


def tokenize(s):
    toks = []
    i = 0
    while i < len(s):
        c = s[i]
        if c.isspace():
            i += 1
            continue
        if c in "()+-*/,":
            toks.append(Tok(c, c)); i += 1; continue
        if c == '?' or c == ':':
            toks.append(Tok(c, c)); i += 1; continue
        if c in "<>=!":
            if i + 1 < len(s) and s[i + 1] == '=':
                toks.append(Tok('cmp', c + '=')); i += 2; continue
            toks.append(Tok('cmp', c)); i += 1; continue
        m = re.match(r'\d*\.\d+|\d+\.?', s[i:])
        if m and (c.isdigit() or c == '.'):
            toks.append(Tok('num', m.group())); i += len(m.group()); continue
        m = re.match(r'[A-Za-z_][A-Za-z0-9_.]*', s[i:])
        if m:
            toks.append(Tok('id', m.group())); i += len(m.group()); continue
        raise ValueError(f"unexpected char {c!r} in {s!r}")
    toks.append(Tok('end', None))
    return toks


class Parser:
    def __init__(self, toks, anim_time_repl, warn):
        self.t = toks
        self.i = 0
        self.anim_time = anim_time_repl
        self.warn = warn

    def peek(self):
        return self.t[self.i]

    def eat(self, kind=None):
        tok = self.t[self.i]
        if kind and tok.kind != kind:
            raise ValueError(f"expected {kind}, got {tok.kind}:{tok.val}")
        self.i += 1
        return tok

    def parse(self):
        e = self.ternary()
        return e

    def ternary(self):
        cond = self.comparison()
        if self.peek().kind == '?':
            self.eat('?')
            a = self.ternary()
            self.eat(':')
            b = self.ternary()
            return f"({cond} ? {a} : {b})"
        return cond

    def comparison(self):
        left = self.add()
        if self.peek().kind == 'cmp':
            op = self.eat('cmp').val
            right = self.add()
            jop = {'==': '==', '!=': '!=', '<': '<', '>': '>', '<=': '<=', '>=': '>='}[op]
            return f"({left} {jop} {right})"
        return left

    def add(self):
        left = self.mul()
        while self.peek().kind in ('+', '-'):
            op = self.eat().val
            right = self.mul()
            left = f"({left} {op} {right})"
        return left

    def mul(self):
        left = self.unary()
        while self.peek().kind in ('*', '/'):
            op = self.eat().val
            right = self.unary()
            left = f"({left} {op} {right})"
        return left

    def unary(self):
        if self.peek().kind == '-':
            self.eat('-')
            return f"(-{self.unary()})"
        if self.peek().kind == '+':
            self.eat('+')
            return self.unary()
        return self.primary()

    def primary(self):
        tok = self.peek()
        if tok.kind == '(':
            self.eat('(')
            e = self.ternary()
            self.eat(')')
            return f"({e})"
        if tok.kind == 'num':
            self.eat('num')
            v = tok.val.rstrip('.')
            return f"{v}f"
        if tok.kind == 'id':
            return self.ident()
        raise ValueError(f"unexpected token {tok.kind}:{tok.val}")

    def ident(self):
        name = self.eat('id').val
        low = name.lower()
        # function call?
        if self.peek().kind == '(':
            self.eat('(')
            args = []
            if self.peek().kind != ')':
                args.append(self.ternary())
                while self.peek().kind == ',':
                    self.eat(',')
                    args.append(self.ternary())
            self.eat(')')
            return self.func(low, args)
        return self.var(low, name)

    def func(self, name, args):
        n = name.split('.')[-1]
        if n == 'sin':
            return f"Mth.sin(({args[0]}) * {DEG2RAD})"
        if n == 'cos':
            return f"Mth.cos(({args[0]}) * {DEG2RAD})"
        if n == 'abs':
            return f"Math.abs({args[0]})"
        if n == 'clamp':
            return f"Mth.clamp((float)({args[0]}), (float)({args[1]}), (float)({args[2]}))"
        if n == 'max':
            return f"Math.max({args[0]}, {args[1]})"
        if n == 'min':
            return f"Math.min({args[0]}, {args[1]})"
        if n == 'mod':
            return f"(({args[0]}) % ({args[1]}))"
        if n in ('floor',):
            return f"Mth.floor({args[0]})"
        self.warn.add(f"unhandled function math.{n} -> 0f")
        return "0f"

    def var(self, low, orig):
        if low == 'this':
            return "0f"
        if low.startswith('query.') or low.startswith('q.'):
            q = low.split('.', 1)[1]
            qmap = {
                'modified_distance_moved': 'limbSwing',
                'modified_move_speed': 'limbSwingAmount',
                'life_time': '(ageInTicks / 20.0f)',
                'vertical_speed': 'vspeed',
                'anim_time': self.anim_time,
                'has_target': 'hasTarget',
            }
            if q in qmap:
                return qmap[q]
            self.warn.add(f"unmapped query.{q} -> 0f")
            return "0f"
        if low.startswith('variable.') or low.startswith('v.'):
            v = low.split('.', 1)[1]
            known = {
                'animationamountblend': 'ageInTicks',
                'gliding_speed_value': '1f',
            }
            if v in known:
                return known[v]
            self.warn.add(f"unmapped variable.{v} -> 0f")
            return "0f"
        self.warn.add(f"unknown identifier {orig} -> 0f")
        return "0f"



def transpile(expr, anim_time_repl, warn):
    if isinstance(expr, (int, float)):
        return None if float(expr) == 0.0 else f"{float(expr)}f"
    expr = str(expr).strip().rstrip(';').strip()
    if expr in ("", "0", "0.0", "0.0f"):
        return None
    toks = tokenize(expr)
    return Parser(toks, anim_time_repl, warn).parse()


def sanitize(name, used):
    v = re.sub(r'[^0-9A-Za-z_]', '_', name)
    if not v or v[0].isdigit():
        v = "b_" + v
    return v


def geo_bone_vars(geo_path):
    with open(geo_path, encoding='utf-8') as fh:
        data = json.load(fh)
    if "minecraft:geometry" in data:
        geo = data["minecraft:geometry"][0]
    else:
        geo = data[next(k for k in data if k.startswith("geometry."))]
    used = set()
    result = {}
    for b in geo.get("bones", []):
        result[b["name"]] = sanitize(b["name"], used)
    return result


def main():
    if len(sys.argv) < 3:
        sys.exit(__doc__)
    anim_path, geo_path = sys.argv[1], sys.argv[2]
    wanted = set(sys.argv[3:])
    bone_vars = geo_bone_vars(geo_path)
    with open(anim_path, encoding='utf-8') as fh:
        anims = json.load(fh)["animations"]

    warn = set()
    lines = []
    needs = {'vspeed': False, 'hasTarget': False}

    for anim_name, anim in anims.items():
        short = anim_name.split('.')[-1]
        if wanted and short not in wanted:
            continue
        if not anim.get("loop"):
            continue
        bones = anim.get("bones", {})
        if not bones:
            continue
        atu = str(anim.get("anim_time_update", "")).lower()
        anim_time_repl = "limbSwing" if "modified_distance_moved" in atu else "(ageInTicks / 20.0f)"
        header = f"\t\t// animation.{'.'.join(anim_name.split('.')[1:-1] + [short])}"
        block = []
        for bone, chans in bones.items():
            if bone not in bone_vars:
                warn.add(f"bone '{bone}' not in model geometry - skipped")
                continue
            var = bone_vars[bone]
            for chan in ("rotation", "position", "scale"):
                if chan not in chans:
                    continue
                val = chans[chan]
                if isinstance(val, list):
                    axes = val
                else:
                    axes = [val, val, val]  # single value -> all three (scale)
                for idx, axis in enumerate(("x", "y", "z")):
                    j = transpile(axes[idx], anim_time_repl, warn)
                    if j is None:
                        continue
                    # drop channels that collapse to a static zero (e.g. `-this`, unmapped var -> 0f)
                    if re.sub(r'[()\-\s]', '', j) == '0f':
                        continue
                    if "vspeed" in j:
                        needs['vspeed'] = True
                    if "hasTarget" in j:
                        needs['hasTarget'] = True
                    if chan == "rotation":
                        block.append(f"\t\tthis.{var}.{axis}Rot += ({j}) * {DEG2RAD};")
                    elif chan == "position":
                        sign = "-" if axis == "y" else ""
                        block.append(f"\t\tthis.{var}.{axis} += {sign}({j});")
                    else:  # scale
                        sax = {"x": "xScale", "y": "yScale", "z": "zScale"}[axis]
                        block.append(f"\t\tthis.{var}.{sax} = {j};")
        if block:
            lines.append(header)
            lines.extend(block)
            lines.append("")

    preamble = []
    if needs['vspeed']:
        preamble.append("\t\tfloat vspeed = (float) entity.getDeltaMovement().y;")
    if needs['hasTarget']:
        preamble.append("\t\tfloat hasTarget = (entity instanceof net.minecraft.world.entity.Mob __m && __m.getTarget() != null) ? 1.0f : 0.0f;")

    out = []
    out.extend(preamble)
    if preamble:
        out.append("")
    out.extend(lines)
    sys.stdout.write("\n".join(out).rstrip() + "\n")
    for w in sorted(warn):
        sys.stderr.write("WARN: " + w + "\n")


if __name__ == "__main__":
    main()
