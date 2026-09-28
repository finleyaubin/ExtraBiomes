"""Worldgen compatibility grid: boots real servers with published ExtraBiomes + one partner mod each.

  compat_grid.py plan                                   -> JSON list of {mc, loader} with a published ExtraBiomes
  compat_grid.py run --mc 1.20.1 --loader fabric --out results/1.20.1-fabric.json [--work DIR]
  compat_grid.py grid results/ --out grid/              -> grid/README.md + grid/grid.json
"""
import argparse
import datetime
import json
import os
import re
import shutil
import subprocess
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

EXTRABIOMES = "extrabiome"
PARTNERS = {
    "Terralith": "terralith",
    "Biomes O' Plenty": "biomes-o-plenty",
    "Oh The Biomes We've Gone": "oh-the-biomes-weve-gone",
    "Regions Unexplored": "regions-unexplored",
    "WWOO": "wwoo",
}
ALONE = "ExtraBiomes alone"
BOOT_TIMEOUT = 900
HEADERS = {"User-Agent": "finleyaubin/ExtraBiomes compat-grid"}


def fetch(url):
    # Modrinth asks for a descriptive User-Agent; the Forge/NeoForge Mavens reject anything but a curl-like one.
    headers = HEADERS if urllib.parse.urlsplit(url).hostname == "api.modrinth.com" else {"User-Agent": "curl/8.5.0"}
    # The Mavens intermittently 404 when many matrix jobs hit them at once.
    for attempt in range(4):
        try:
            with urllib.request.urlopen(urllib.request.Request(url, headers=headers), timeout=120) as r:
                return r.read()
        except (urllib.error.URLError, TimeoutError):
            if attempt == 3:
                raise
            time.sleep(15 * (attempt + 1))


def fetch_json(url):
    return json.loads(fetch(url))


def download(url, dest):
    dest = Path(dest)
    if not dest.exists():
        dest.write_bytes(fetch(url))
    return dest


def modrinth_version(project, mc, loader):
    query = urllib.parse.urlencode({"loaders": json.dumps([loader]), "game_versions": json.dumps([mc])})
    versions = fetch_json(f"https://api.modrinth.com/v2/project/{project}/version?{query}")
    releases = [v for v in versions if v["version_type"] == "release"]
    return (releases or versions or [None])[0]


def resolve_with_deps(project, mc, loader):
    """Returns (versions to install, missing dependency project ids)."""
    resolved, missing, queue = {}, [], [(project, None)]
    while queue:
        project_id, version_id = queue.pop()
        version = fetch_json(f"https://api.modrinth.com/v2/version/{version_id}") if version_id else modrinth_version(project_id, mc, loader)
        if version is None:
            missing.append(project_id)
            continue
        if version["project_id"] in resolved:
            continue
        resolved[version["project_id"]] = version
        for dep in version["dependencies"]:
            if dep["dependency_type"] == "required" and dep["project_id"] not in resolved:
                queue.append((dep["project_id"], None))
    return list(resolved.values()), missing


def java_major(mc):
    if not mc.startswith("1."):
        return 25
    minor, patch = (int(p) for p in (mc.split(".")[1:] + ["0"])[:2])
    return 17 if (minor, patch) <= (20, 4) else 21


def java_for(mc):
    major = java_major(mc)
    home = os.environ.get(f"JAVA_HOME_{major}_X64")
    return os.environ.get(f"JAVA{major}") or (home and f"{home}/bin/java") or "java"


def neoforge_prefix(mc):
    parts = mc.split(".")
    if mc.startswith("1."):
        return f"{parts[1]}.{parts[2] if len(parts) > 2 else 0}."
    return ".".join((parts + ["0"])[:3]) + "."


def sorted_versions(versions, prefix):
    return sorted((v for v in versions if v.startswith(prefix)), key=lambda v: [int(n) for n in re.findall(r"\d+", v)])


def maven_versions(artifact_url, prefix):
    return sorted_versions(re.findall(r"<version>([^<]+)</version>", fetch(f"{artifact_url}/maven-metadata.xml").decode()), prefix)


def neoforge_version(mc):
    # NeoForge's maven-metadata.xml can briefly list only the newest build after a publish; the directory listing can't.
    listing = fetch_json("https://maven.neoforged.net/api/maven/details/releases/net/neoforged/neoforge")
    matching = sorted_versions([f["name"] for f in listing["files"] if f.get("type") == "DIRECTORY"], neoforge_prefix(mc))
    stable = [v for v in matching if "beta" not in v]
    return (stable or matching)[-1]


def install_server(mc, loader, server_dir):
    """Installs the loader's server once and returns the java arguments that launch it."""
    java = java_for(mc)
    server_dir.mkdir(parents=True, exist_ok=True)
    if loader == "fabric":
        loader_version = next(v["loader"]["version"] for v in fetch_json(f"https://meta.fabricmc.net/v2/versions/loader/{mc}") if v["loader"]["stable"])
        installer = next(v["version"] for v in fetch_json("https://meta.fabricmc.net/v2/versions/installer") if v["stable"])
        download(f"https://meta.fabricmc.net/v2/versions/loader/{mc}/{loader_version}/{installer}/server/jar", server_dir / "fabric-server.jar")
        return [java, "-jar", "fabric-server.jar"], loader_version
    if loader == "forge":
        full = maven_versions("https://maven.minecraftforge.net/net/minecraftforge/forge", f"{mc}-")[-1]
        version = full.removeprefix(f"{mc}-")
        installer_url = f"https://maven.minecraftforge.net/net/minecraftforge/forge/{full}/forge-{full}-installer.jar"
        args_file = f"libraries/net/minecraftforge/forge/{full}/unix_args.txt"
    else:
        version = neoforge_version(mc)
        installer_url = f"https://maven.neoforged.net/releases/net/neoforged/neoforge/{version}/neoforge-{version}-installer.jar"
        args_file = f"libraries/net/neoforged/neoforge/{version}/unix_args.txt"
    if not (server_dir / args_file).exists():
        download(installer_url, server_dir / "installer.jar")
        subprocess.run([java, "-jar", "installer.jar", "--installServer"], cwd=server_dir, check=True, capture_output=True)
    return [java, f"@{args_file}"], version


def boot(launch, server_dir, mod_files, log_path):
    mods = server_dir / "mods"
    shutil.rmtree(mods, ignore_errors=True)
    shutil.rmtree(server_dir / "world", ignore_errors=True)
    mods.mkdir()
    for f in mod_files:
        shutil.copy(f, mods)
    (server_dir / "eula.txt").write_text("eula=true\n")
    (server_dir / "server.properties").write_text("level-seed=extrabiomes\nserver-port=0\nonline-mode=false\n")
    with open(log_path, "w") as log:
        server = subprocess.Popen([*launch, "nogui"], cwd=server_dir, stdin=subprocess.PIPE, stdout=log, stderr=subprocess.STDOUT)
        code = wait_for_boot_then_stop(server, Path(log_path))
    text = Path(log_path).read_text(errors="replace")
    if "Feature order cycle found" in text:
        return "cycle", "biomes: " + ", ".join(cycle_biomes(text))
    if code is None:
        return "timeout", f"no clean shutdown within {BOOT_TIMEOUT}s"
    if code == 0 and "Done (" in text:
        return "pass", ""
    return "crash", first_line(text, r"(Caused by: .*|.*Incompatible mods? found.*|.*requires .*|.*Exception: .*)")


def wait_for_boot_then_stop(server, log_path):
    """Returns the exit code, or None if the server had to be killed."""
    deadline = time.monotonic() + BOOT_TIMEOUT
    stopping = False
    while time.monotonic() < deadline and server.poll() is None:
        text = log_path.read_text(errors="replace")
        if "Feature order cycle found" in text:
            break
        # Commands queued before "Done" can fail on newer versions, leaving the server idling forever.
        if "Done (" in text and not stopping:
            server.stdin.write(b"stop\n")
            server.stdin.flush()
            stopping = True
        time.sleep(2)
    if server.poll() is None:
        server.kill()
        server.wait()
        return None
    return server.returncode


def cycle_biomes(text):
    line = re.search(r"Feature order cycle found.*", text).group(0)
    return sorted(set(re.findall(r"worldgen/biome / ([\w.-]+:[\w/.-]+)", line)))


def first_line(text, pattern):
    match = re.search(pattern, text)
    return match.group(0).strip()[:300] if match else "see log"


def cmd_plan(_):
    combos = {(gv, loader) for v in fetch_json(f"https://api.modrinth.com/v2/project/{EXTRABIOMES}/version") for gv in v["game_versions"] for loader in v["loaders"]}
    print(json.dumps([{"mc": mc, "loader": loader} for mc, loader in sorted(combos)]))


def cmd_run(args):
    work = Path(args.work).resolve()
    cache, server_dir, logs = work / "cache", work / f"server-{args.mc}-{args.loader}", Path(args.out).resolve().parent / "logs"
    for d in (cache, logs):
        d.mkdir(parents=True, exist_ok=True)
    result = {"mc": args.mc, "loader": args.loader, "results": {}}
    extrabiomes = modrinth_version(EXTRABIOMES, args.mc, args.loader)
    result["extrabiomes"] = extrabiomes["version_number"]
    launch, result["loader_version"] = install_server(args.mc, args.loader, server_dir)
    base_versions, base_missing = resolve_with_deps(EXTRABIOMES, args.mc, args.loader)

    for name, project in {ALONE: None, **PARTNERS}.items():
        entry = {"status": "n/a", "version": "", "reason": ""}
        result["results"][name] = entry
        versions, missing = list(base_versions), list(base_missing)
        if project:
            partner_versions, partner_missing = resolve_with_deps(project, args.mc, args.loader)
            if project in partner_missing:
                entry["reason"] = f"no {args.loader} {args.mc} build"
                continue
            installed = {v["project_id"] for v in versions}
            versions += [v for v in partner_versions if v["project_id"] not in installed]
            missing += partner_missing
            entry["version"] = partner_versions[0]["version_number"]
        if missing:
            entry.update(status="missing-deps", reason="no build for dependencies " + ", ".join(missing))
            continue
        files = []
        for v in versions:
            primary = next((f for f in v["files"] if f["primary"]), v["files"][0])
            files.append(download(primary["url"], cache / primary["filename"]))
        slug = project or "alone"
        entry["status"], entry["reason"] = boot(launch, server_dir, files, logs / f"{args.mc}-{args.loader}-{slug}.log")
        if project and entry["status"] != "pass":
            partner_files = [f for v, f in zip(versions, files) if v["project_id"] in {p["project_id"] for p in partner_versions}]
            alone_status, _ = boot(launch, server_dir, partner_files, logs / f"{args.mc}-{args.loader}-{slug}-without-extrabiomes.log")
            if alone_status == entry["status"]:
                entry["status"] = "partner-broken"
        print(f"{args.mc} {args.loader} {name}: {entry['status']} {entry['reason']}", flush=True)

    Path(args.out).write_text(json.dumps(result, indent=2))


ICONS = {"pass": "✅", "cycle": "❌ feature cycle", "crash": "💥 crash", "timeout": "⏱ timeout", "partner-broken": "⚠️ fails without ExtraBiomes too", "missing-deps": "➖ deps missing", "n/a": "➖"}


def version_key(mc):
    return [int(p) for p in mc.split(".")]


def cmd_grid(args):
    rows = sorted((json.loads(p.read_text()) for p in Path(args.results).glob("*.json")), key=lambda r: (version_key(r["mc"]), r["loader"]))
    columns = [ALONE, *PARTNERS]
    today = datetime.date.today().isoformat()
    lines = [
        "# ExtraBiomes worldgen compatibility",
        "",
        f"Generated {today} by the Compat Grid workflow. Each cell boots a real server with the published ExtraBiomes build and one other mod, generates a world, and stops it.",
        "",
        "✅ works · ❌ feature order cycle · 💥 crash on startup · ⏱ didn't finish · ⚠️ the other mod fails even without ExtraBiomes · ➖ that mod (or a dependency) has no build for this version",
        "",
        "| Minecraft | Loader | ExtraBiomes | " + " | ".join(columns) + " |",
        "|" + "---|" * (len(columns) + 3),
    ]
    for r in rows:
        cells = [ICONS[r["results"][c]["status"]] if c in r["results"] else "?" for c in columns]
        lines.append(f"| {r['mc']} | {r['loader']} | {r['extrabiomes']} | " + " | ".join(cells) + " |")
    failures = [(r, c, e) for r in rows for c, e in r["results"].items() if e["status"] in ("cycle", "crash", "timeout")]
    if failures:
        lines += ["", "## Failures", ""]
        lines += [f"- **{r['mc']} {r['loader']} + {c}** ({e['status']}): `{e['reason']}`" for r, c, e in failures]
    out = Path(args.out)
    out.mkdir(parents=True, exist_ok=True)
    (out / "README.md").write_text("\n".join(lines) + "\n")
    (out / "grid.json").write_text(json.dumps({"generated": today, "rows": rows}, indent=2))
    print("\n".join(lines))


def cmd_selftest(_):
    assert [java_major(v) for v in ("1.20.1", "1.20.4", "1.20.6", "1.21", "1.21.11", "26.1.2")] == [17, 17, 21, 21, 21, 25]
    assert [neoforge_prefix(v) for v in ("1.20.2", "1.21", "1.21.10", "26.1.2", "26.2")] == ["20.2.", "21.0.", "21.10.", "26.1.2.", "26.2.0."]
    assert sorted(["1.21.10", "1.20.1", "26.2", "1.21.4"], key=version_key) == ["1.20.1", "1.21.4", "1.21.10", "26.2"]
    cycle = "Caused by: java.lang.IllegalStateException: Feature order cycle found, involved sources: [Reference{ResourceKey[minecraft:worldgen/biome / terralith:warm_river]=B@1}, Reference{ResourceKey[minecraft:worldgen/biome / extrabiomes:lush_mesa_bryce]=B@2}]"
    assert cycle_biomes(cycle) == ["extrabiomes:lush_mesa_bryce", "terralith:warm_river"]
    print("selftest ok")


def main():
    parser = argparse.ArgumentParser()
    sub = parser.add_subparsers(dest="cmd", required=True)
    sub.add_parser("plan")
    sub.add_parser("selftest")
    run = sub.add_parser("run")
    run.add_argument("--mc", required=True)
    run.add_argument("--loader", required=True)
    run.add_argument("--out", required=True)
    run.add_argument("--work", default="compat-work")
    grid = sub.add_parser("grid")
    grid.add_argument("results")
    grid.add_argument("--out", required=True)
    args = parser.parse_args()
    {"plan": cmd_plan, "run": cmd_run, "grid": cmd_grid, "selftest": cmd_selftest}[args.cmd](args)


if __name__ == "__main__":
    sys.exit(main())
