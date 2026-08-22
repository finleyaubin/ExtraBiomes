#!/usr/bin/env python3
"""Convert a Bedrock Blockbench .geo.json entity model into a Java (Forge / Mojang
mappings) HierarchicalModel class, replicating Blockbench's "Java Edition" export math.

Coordinate transform (verified against puckoo.geo.json -> PuckooModel.java):
  root  bone PartPose : ( px , 24 - py , pz )
  child bone PartPose : ( px - parentPx , parentPy - py , pz - parentPz )
  cube addBox (relative to the pivot it is attached to):
        ax = ox - pivotX
        ay = pivotY - oy - sizeY
        az = oz - pivotZ
  A cube with a non-zero rotation is emitted as its own `<bone>_rN` sub-part whose
  PartPose offset is (cubePivot - bonePivot, y-flipped) and whose rotation is the
  direct degrees->radians of the cube rotation on each axis. `inflate` -> CubeDeformation.

Usage:
  python tools/geo2java.py <in.geo.json> <ClassName> [package] > OutModel.java
  python tools/geo2java.py <in.geo.json> --batch <out_dir> [package]
"""
import json, math, sys, os, re

DEFAULT_PKG = "net.winepicfin.extrabiomes.entity.client"


def fnum(x):
    x = round(float(x), 4) + 0.0
    if x == int(x):
        return f"{int(x)}.0F"
    return f"{x}F"


def frad(deg):
    return fnum(math.radians(float(deg)))


def sanitize(name, used):
    v = re.sub(r'[^0-9A-Za-z_]', '_', name)
    if not v or v[0].isdigit():
        v = "b_" + v
    base = v
    i = 2
    while v in used:
        v = f"{base}{i}"
        i += 1
    used.add(v)
    return v


def cube_is_rotated(cube):
    r = cube.get("rotation")
    return bool(r) and any(abs(float(v)) > 1e-9 for v in r)


def box_code(cube, pivot):
    ox, oy, oz = cube["origin"]
    sx, sy, sz = cube["size"]
    ax = ox - pivot[0]
    ay = pivot[1] - oy - sy
    az = oz - pivot[2]
    uv = cube.get("uv", [0, 0])
    if isinstance(uv, dict):
        # per-face UV not supported by the simple CubeListBuilder; fall back to 0,0
        sys.stderr.write("WARN: per-face uv encountered, using texOffs(0,0)\n")
        u, v = 0, 0
    else:
        u, v = uv
    infl = cube.get("inflate", 0.0)
    mirror = cube.get("mirror", False)
    parts = []
    if mirror:
        parts.append(".mirror()")
    parts.append(f".texOffs({int(u)}, {int(v)})")
    parts.append(
        f".addBox({fnum(ax)}, {fnum(ay)}, {fnum(az)}, {fnum(sx)}, {fnum(sy)}, {fnum(sz)}"
        f", new CubeDeformation({fnum(infl)}))"
    )
    if mirror:
        parts.append(".mirror(false)")
    return "".join(parts)


def convert(geo_path, class_name, package):
    with open(geo_path, "r", encoding="utf-8") as fh:
        data = json.load(fh)
    if "minecraft:geometry" in data:  # format 1.12+
        geo = data["minecraft:geometry"][0]
        desc = geo["description"]
        tw = int(desc.get("texture_width", 64))
        th = int(desc.get("texture_height", 64))
    else:  # legacy format 1.8.0: top-level "geometry.<name>" object
        key = next(k for k in data if k.startswith("geometry."))
        geo = data[key]
        tw = int(geo.get("texturewidth", geo.get("texture_width", 64)))
        th = int(geo.get("textureheight", geo.get("texture_height", 64)))
    bones = geo.get("bones", [])

    by_name = {b["name"]: b for b in bones}

    # topological order: parents before children
    ordered = []
    seen = set()

    def visit(b):
        if b["name"] in seen:
            return
        p = b.get("parent")
        if p and p in by_name:
            visit(by_name[p])
        seen.add(b["name"])
        ordered.append(b)

    for b in bones:
        visit(b)

    used_vars = {"modelRoot"}
    var_of = {}
    lines = []
    field_decls = []
    field_inits = []
    rcounter = [0]

    for b in ordered:
        name = b["name"]
        pivot = b.get("pivot", [0, 0, 0])
        parent = b.get("parent")
        var = sanitize(name, used_vars)
        var_of[name] = var
        parent_var = var_of.get(parent, "partdefinition")

        field_decls.append(f"\tprivate final ModelPart {var};\n")
        if parent and parent in by_name:
            field_inits.append(f"\t\tthis.{var} = this.{var_of[parent]}.getChild(\"{name}\");\n")
        else:
            field_inits.append(f"\t\tthis.{var} = root.getChild(\"{name}\");\n")

        if parent and parent in by_name:
            pp = by_name[parent].get("pivot", [0, 0, 0])
            off = (pivot[0] - pp[0], pp[1] - pivot[1], pivot[2] - pp[2])
        else:
            off = (pivot[0], 24 - pivot[1], pivot[2])

        brot = b.get("rotation")
        if brot and any(abs(float(v)) > 1e-9 for v in brot):
            pose = (f"PartPose.offsetAndRotation({fnum(off[0])}, {fnum(off[1])}, {fnum(off[2])}, "
                    f"{frad(brot[0])}, {frad(brot[1])}, {frad(brot[2])})")
        else:
            pose = f"PartPose.offset({fnum(off[0])}, {fnum(off[1])}, {fnum(off[2])})"

        cubes = b.get("cubes", [])
        flat = [c for c in cubes if not cube_is_rotated(c)]
        rotated = [c for c in cubes if cube_is_rotated(c)]

        clb = "CubeListBuilder.create()"
        for c in flat:
            clb += box_code(c, pivot)

        lines.append(f"\t\tPartDefinition {var} = {parent_var}.addOrReplaceChild(\"{name}\", {clb}, {pose});\n")

        for c in rotated:
            rcounter[0] += 1
            rn = f"{var}_r{rcounter[0]}"
            cpiv = c.get("pivot", pivot)
            coff = (cpiv[0] - pivot[0], pivot[1] - cpiv[1], cpiv[2] - pivot[2])
            rot = c["rotation"]
            cpose = (f"PartPose.offsetAndRotation({fnum(coff[0])}, {fnum(coff[1])}, {fnum(coff[2])}, "
                     f"{frad(rot[0])}, {frad(rot[1])}, {frad(rot[2])})")
            clb2 = "CubeListBuilder.create()" + box_code(c, cpiv)
            lines.append(f"\t\tPartDefinition {rn} = {var}.addOrReplaceChild(\"{rn}\", {clb2}, {cpose});\n")

    body = "".join(lines)
    decls = "".join(field_decls)
    inits = "".join(field_inits)
    return f"""package {package};
// Generated from {os.path.basename(geo_path)} by tools/geo2java.py

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class {class_name}<T extends Entity> extends HierarchicalModel<T> {{
\tprivate final ModelPart modelRoot;
{decls}
\tpublic {class_name}(ModelPart root) {{
\t\tthis.modelRoot = root;
{inits}\t}}

\tpublic static LayerDefinition createBodyLayer() {{
\t\tMeshDefinition meshdefinition = new MeshDefinition();
\t\tPartDefinition partdefinition = meshdefinition.getRoot();

{body}
\t\treturn LayerDefinition.create(meshdefinition, {tw}, {th});
\t}}

\t@Override
\tpublic void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {{
\t\tthis.root().getAllParts().forEach(ModelPart::resetPose);
\t}}

\t@Override
\tpublic void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {{
\t\tmodelRoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
\t}}

\t@Override
\tpublic ModelPart root() {{
\t\treturn this.modelRoot;
\t}}
}}
"""


def pascal(stem):
    return "".join(p.capitalize() for p in re.split(r'[^0-9A-Za-z]+', stem) if p)


def main():
    args = sys.argv[1:]
    if len(args) < 2:
        sys.exit(__doc__)
    geo_path = args[0]
    if args[1] == "--batch":
        out_dir = args[2]
        package = args[3] if len(args) > 3 else DEFAULT_PKG
        stem = os.path.splitext(os.path.splitext(os.path.basename(geo_path))[0])[0]
        cls = pascal(stem) + "Model"
        code = convert(geo_path, cls, package)
        os.makedirs(out_dir, exist_ok=True)
        out = os.path.join(out_dir, cls + ".java")
        with open(out, "w", encoding="utf-8") as fh:
            fh.write(code)
        print(f"wrote {out}")
    else:
        class_name = args[1]
        package = args[2] if len(args) > 2 else DEFAULT_PKG
        sys.stdout.write(convert(geo_path, class_name, package))


if __name__ == "__main__":
    main()
