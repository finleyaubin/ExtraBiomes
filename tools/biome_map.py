#!/usr/bin/env python3
"""Seed preview map that knows about ExtraBiomes (a Chunkbase stand-in).

Chunkbase only reproduces vanilla worldgen, so it can't show TerraBlender biomes. This tool boots the
real mod through Fabric's game-test server (BiomeMapGameTests), samples the real TerraBlender-patched
overworld biome source for any seed, and renders the result as a PNG plus a self-contained HTML viewer
(hover for coordinates/biome, click a legend entry to highlight it, wheel to zoom, drag to pan).

  biome_map.py --seed 12345                          -> map of +-4000 blocks around 0,0
  biome_map.py --seed "my world" --radius 12000 --step 64 --center 2000,-3000
  biome_map.py --from-json tools/biome_maps/12345/map.json    (re-render without running the game)

Needs the same toolchain as the build (Java 25 on PATH / JAVA_HOME; run it from Windows if your WSL has
no JDK). Output goes to tools/biome_maps/<seed>/ (git-ignored). A text seed is hashed like Minecraft's
world-creation screen. Cost grows with (2*radius/step)^2 cells; 8000/32 = 250x250 takes about a minute.
"""
import argparse
import base64
import hashlib
import json
import os
import struct
import subprocess
import sys
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
JAVA_DIR = ROOT / "ExtraBiomes - Java"
MOD_NS = "extrabiomes"


def java_string_hash(s):
    h = 0
    for ch in s:
        h = (31 * h + ord(ch)) & 0xFFFFFFFF
    return h - (1 << 32) if h >= (1 << 31) else h


def parse_seed(text):
    try:
        return int(text)
    except ValueError:
        return java_string_hash(text)


def run_game(args, out_json):
    env = dict(os.environ)
    env.update({
        "EXTRABIOMES_MAP_OUT": str(out_json),
        "EXTRABIOMES_MAP_SEED": str(parse_seed(args.seed)),
        "EXTRABIOMES_MAP_CENTER_X": str(args.center[0]),
        "EXTRABIOMES_MAP_CENTER_Z": str(args.center[1]),
        "EXTRABIOMES_MAP_RADIUS": str(args.radius),
        "EXTRABIOMES_MAP_STEP": str(args.step),
        "EXTRABIOMES_MAP_SURFACE": "false" if args.fixed_y else "true",
    })
    gradle = "gradlew.bat" if os.name == "nt" else "./gradlew"
    cmd = [gradle, ":fabric:runGameTestServer"] + (["--offline"] if args.offline else [])
    print("running:", " ".join(cmd), "(env EXTRABIOMES_MAP_*)", flush=True)
    if out_json.exists():
        out_json.unlink()
    # The game-test run also executes the other game tests; a failure there shouldn't hide the map.
    code = subprocess.call(cmd, cwd=JAVA_DIR, env=env)
    if not out_json.exists():
        sys.exit(f"gradle exited {code} and wrote no map; check the log above")
    if code:
        print(f"note: gradle exited {code} (other game tests?), but the map was written", file=sys.stderr)


# ---- colours -------------------------------------------------------------------------------------
VANILLA = {  # familiar Chunkbase-ish colours for the biomes people look for; everything else is hashed
    "ocean": (0, 0, 112), "deep_ocean": (0, 0, 48), "warm_ocean": (0, 0, 172), "lukewarm_ocean": (0, 0, 144),
    "deep_lukewarm_ocean": (0, 0, 64), "cold_ocean": (32, 32, 112), "deep_cold_ocean": (32, 32, 56),
    "frozen_ocean": (112, 112, 214), "deep_frozen_ocean": (64, 64, 144), "river": (0, 0, 255),
    "frozen_river": (160, 160, 255), "beach": (250, 222, 85), "snowy_beach": (250, 240, 192),
    "stony_shore": (162, 162, 132), "plains": (141, 179, 96), "sunflower_plains": (181, 219, 136),
    "forest": (5, 102, 33), "flower_forest": (45, 142, 73), "birch_forest": (48, 116, 68),
    "old_growth_birch_forest": (88, 156, 108), "dark_forest": (64, 81, 26), "pale_garden": (160, 170, 160),
    "taiga": (11, 102, 89), "snowy_taiga": (49, 85, 74), "old_growth_pine_taiga": (89, 102, 81),
    "old_growth_spruce_taiga": (129, 142, 121), "snowy_plains": (255, 255, 255), "ice_spikes": (180, 220, 220),
    "desert": (250, 148, 24), "badlands": (217, 69, 21), "eroded_badlands": (255, 109, 61),
    "wooded_badlands": (176, 151, 101), "savanna": (189, 178, 95), "savanna_plateau": (167, 157, 100),
    "windswept_savanna": (231, 222, 134), "jungle": (83, 123, 9), "sparse_jungle": (98, 139, 23),
    "bamboo_jungle": (118, 142, 20), "swamp": (7, 249, 178), "mangrove_swamp": (103, 128, 76),
    "meadow": (96, 165, 77), "cherry_grove": (255, 183, 197), "grove": (150, 195, 190),
    "snowy_slopes": (210, 225, 230), "frozen_peaks": (160, 180, 200), "jagged_peaks": (190, 190, 190),
    "stony_peaks": (140, 140, 125), "windswept_hills": (96, 96, 96), "windswept_forest": (80, 112, 80),
    "windswept_gravelly_hills": (136, 136, 136), "mushroom_fields": (255, 0, 255),
    "dripstone_caves": (120, 90, 60), "lush_caves": (60, 160, 60), "deep_dark": (10, 40, 50),
}


def hsv(h, s, v):
    import colorsys
    r, g, b = colorsys.hsv_to_rgb(h, s, v)
    return int(r * 255), int(g * 255), int(b * 255)


def color_for(biome_id):
    ns, _, path = biome_id.partition(":")
    digest = hashlib.md5(biome_id.encode()).digest()
    hue = digest[0] / 255.0
    if ns == MOD_NS:  # saturated, so ExtraBiomes stands out from vanilla
        return hsv(hue, 0.85, 0.95)
    if ns == "minecraft" and path in VANILLA:
        return VANILLA[path]
    return hsv(hue, 0.35, 0.6)


def pretty(biome_id):
    return biome_id.partition(":")[2].replace("_", " ").title().replace("Tiaga", "Taiga")


# ---- output --------------------------------------------------------------------------------------
def png_bytes(width, height, rgb):
    def chunk(tag, payload):
        c = struct.pack(">I", len(payload)) + tag + payload
        return c + struct.pack(">I", zlib.crc32(tag + payload) & 0xFFFFFFFF)
    raw = b"".join(b"\x00" + rgb[y * width * 3:(y + 1) * width * 3] for y in range(height))
    return (b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0))
            + chunk(b"IDAT", zlib.compress(raw, 6)) + chunk(b"IEND", b""))


def load(path):
    m = json.loads(Path(path).read_text())
    raw = base64.b64decode(m["data"])
    cells = struct.unpack(f"<{len(raw) // 2}H", raw)
    return m, cells


def render(m, cells, out_dir, scale=4):
    out_dir.mkdir(parents=True, exist_ok=True)
    w, h, palette = m["width"], m["height"], m["palette"]
    colors = [color_for(b) for b in palette]
    counts = [0] * len(palette)
    for c in cells:
        counts[c] += 1
    total = len(cells)
    # PNG: each cell becomes a scale x scale block
    rows = []
    for y in range(h):
        row = b"".join(bytes(colors[c]) * scale for c in cells[y * w:(y + 1) * w])
        rows.extend([row] * scale)
    (out_dir / "map.png").write_bytes(png_bytes(w * scale, h * scale, b"".join(rows)))
    legend = sorted(
        ({"id": b, "name": pretty(b), "mod": b.startswith(MOD_NS + ":"), "color": "#%02x%02x%02x" % colors[i],
          "pct": counts[i] / total * 100, "idx": i} for i, b in enumerate(palette)),
        key=lambda e: (not e["mod"], -e["pct"]))
    (out_dir / "map.html").write_text(
        HTML.replace("__META__", json.dumps({k: m[k] for k in ("seed", "step", "minX", "minZ", "width", "height", "surface")}))
            .replace("__LEGEND__", json.dumps(legend))
            .replace("__DATA__", m["data"]), encoding="utf-8")
    mod = [e for e in legend if e["mod"]]
    print(f"seed {m['seed']}: {w}x{h} cells of {m['step']} blocks, {len(palette)} biomes "
          f"({len(mod)} ExtraBiomes, {sum(e['pct'] for e in mod):.2f}% of the area)")
    print("wrote", out_dir / "map.html", "and", out_dir / "map.png")


HTML = r"""<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>ExtraBiomes Seed Map</title><style>
:root{--bg:#fbfaf7;--fg:#1d2420;--mut:#5d675f;--card:#fff;--line:#e2e0d8}
@media(prefers-color-scheme:dark){:root{--bg:#14181a;--fg:#e8ece9;--mut:#9aa5a0;--card:#1b2023;--line:#2c3336}}
*{box-sizing:border-box}body{margin:0;background:var(--bg);color:var(--fg);font:14px/1.45 system-ui,sans-serif;display:flex;height:100vh}
#side{width:300px;flex:none;overflow:auto;border-right:1px solid var(--line);background:var(--card);padding:12px}
#view{flex:1;position:relative;overflow:hidden;cursor:grab;background:#000}#view.drag{cursor:grabbing}
canvas{position:absolute;image-rendering:pixelated;transform-origin:0 0}
h1{font-size:16px;margin:0 0 2px}.sub{color:var(--mut);font-size:12px;margin-bottom:10px}
.row{display:flex;align-items:center;gap:6px;padding:2px 4px;border-radius:5px;cursor:pointer;font-size:13px}.row:hover,.row.on{background:var(--bg)}
.sw{width:12px;height:12px;border-radius:3px;flex:none;border:1px solid rgba(0,0,0,.25)}.pct{margin-left:auto;color:var(--mut);font-variant-numeric:tabular-nums}
.sec{font-size:11px;text-transform:uppercase;letter-spacing:.05em;color:var(--mut);margin:12px 0 4px}
#tip{position:absolute;left:10px;bottom:10px;background:rgba(0,0,0,.75);color:#fff;padding:5px 9px;border-radius:6px;font-size:12px;pointer-events:none}
label{font-size:12px;color:var(--mut);display:block;margin:4px 0}
@media(max-width:700px){body{flex-direction:column}#side{width:100%;height:38vh;border-right:0;border-bottom:1px solid var(--line)}}
</style></head><body><div id="side"><h1>ExtraBiomes seed map</h1><div class="sub" id="sub"></div>
<label><input type="checkbox" id="dim" checked> Dim vanilla biomes</label>
<div class="sec">ExtraBiomes</div><div id="mod"></div><div class="sec">Vanilla</div><div id="van"></div></div>
<div id="view"><canvas id="c"></canvas><div id="tip">hover the map</div></div>
<script>
const META=__META__,LEG=__LEGEND__,B64="__DATA__";
const W=META.width,H=META.height,raw=Uint8Array.from(atob(B64),c=>c.charCodeAt(0)),cells=new Uint16Array(raw.buffer);
const view=document.getElementById('view'),cv=document.getElementById('c'),ctx=cv.getContext('2d'),tip=document.getElementById('tip');
cv.width=W;cv.height=H;let sel=null,dim=true;
const rgb=h=>[1,3,5].map(i=>parseInt(h.slice(i,i+2),16));const byIdx=[];LEG.forEach(e=>byIdx[e.idx]=e);
document.getElementById('sub').textContent='seed '+META.seed+' · '+W+'×'+H+' cells of '+META.step+' blocks · '+(META.surface?'terrain surface':'Y=64');
function draw(){const img=ctx.createImageData(W,H);for(let i=0;i<cells.length;i++){const e=byIdx[cells[i]];let c=rgb(e.color);
 let f=1;if(sel!==null)f=(e.idx===sel)?1:.18;else if(dim&&!e.mod)f=.55;
 const g=c[0]*.3+c[1]*.59+c[2]*.11,o=i*4;const mix=(sel!==null&&e.idx!==sel)||(sel===null&&dim&&!e.mod)?.0:1;
 img.data[o]=c[0]*f;img.data[o+1]=c[1]*f;img.data[o+2]=c[2]*f;img.data[o+3]=255}ctx.putImageData(img,0,0);
 const sx=(0-META.minX)/META.step,sz=(0-META.minZ)/META.step;if(sx>=0&&sx<W&&sz>=0&&sz<H){ctx.strokeStyle='#fff';ctx.lineWidth=Math.max(1,W/300);ctx.beginPath();ctx.arc(sx,sz,Math.max(2,W/120),0,7);ctx.stroke()}}
function list(el,mod){el.innerHTML='';LEG.filter(e=>e.mod===mod).forEach(e=>{const d=document.createElement('div');d.className='row'+(sel===e.idx?' on':'');
 d.innerHTML='<span class="sw" style="background:'+e.color+'"></span><span>'+e.name+'</span><span class="pct">'+(e.pct<0.01?'<0.01':e.pct.toFixed(2))+'%</span>';
 d.onclick=()=>{sel=sel===e.idx?null:e.idx;refresh()};el.appendChild(d)})}
function refresh(){list(document.getElementById('mod'),true);list(document.getElementById('van'),false);draw()}
document.getElementById('dim').onchange=e=>{dim=e.target.checked;draw()};
let k=1,ox=0,oy=0;function place(){cv.style.width=W*k+'px';cv.style.height=H*k+'px';cv.style.left=ox+'px';cv.style.top=oy+'px'}
function fit(){k=Math.min(view.clientWidth/W,view.clientHeight/H);ox=(view.clientWidth-W*k)/2;oy=(view.clientHeight-H*k)/2;place()}
view.addEventListener('wheel',e=>{e.preventDefault();const r=view.getBoundingClientRect(),mx=e.clientX-r.left,my=e.clientY-r.top,n=Math.max(.2,Math.min(80,k*(e.deltaY<0?1.25:.8)));
 ox=mx-(mx-ox)*n/k;oy=my-(my-oy)*n/k;k=n;place()},{passive:false});
let drag=null;view.onmousedown=e=>{drag=[e.clientX-ox,e.clientY-oy];view.classList.add('drag')};window.onmouseup=()=>{drag=null;view.classList.remove('drag')};
view.onmousemove=e=>{if(drag){ox=e.clientX-drag[0];oy=e.clientY-drag[1];place()}const r=cv.getBoundingClientRect(),cx=Math.floor((e.clientX-r.left)/k),cy=Math.floor((e.clientY-r.top)/k);
 if(cx>=0&&cy>=0&&cx<W&&cy<H){const en=byIdx[cells[cy*W+cx]];tip.textContent='x '+(META.minX+cx*META.step+META.step/2)+'  z '+(META.minZ+cy*META.step+META.step/2)+'  ·  '+en.name+(en.mod?'  (ExtraBiomes)':'')}};
window.onresize=fit;fit();refresh();
</script></body></html>"""


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--seed", default="0", help="numeric seed, or any text (hashed like Minecraft does)")
    ap.add_argument("--radius", type=int, default=4000, help="half-width of the map in blocks")
    ap.add_argument("--step", type=int, default=32, help="blocks per map cell (multiple of 4; larger = faster/coarser)")
    ap.add_argument("--center", default="0,0", help="map centre as X,Z in blocks")
    ap.add_argument("--fixed-y", action="store_true", help="sample at Y=64 instead of the terrain surface")
    ap.add_argument("--out", help="output directory (default tools/biome_maps/<seed>)")
    ap.add_argument("--offline", action="store_true", help="pass --offline to gradle")
    ap.add_argument("--from-json", help="skip the game run and render an existing map.json")
    args = ap.parse_args()
    args.center = tuple(int(v) for v in args.center.split(","))
    if args.from_json:
        src = Path(args.from_json)
        render(*load(src), src.parent)
        return
    out_dir = Path(args.out) if args.out else ROOT / "tools" / "biome_maps" / str(parse_seed(args.seed))
    out_dir.mkdir(parents=True, exist_ok=True)
    out_json = (out_dir / "map.json").resolve()
    run_game(args, out_json)
    render(*load(out_json), out_dir)


if __name__ == "__main__":
    main()
