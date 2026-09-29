"""Worldgen compatibility grid: boots real servers with published ExtraBiomes + one partner mod each.

  compat_grid.py plan                                   -> JSON list of {mc, loader} with a published ExtraBiomes
  compat_grid.py run --mc 1.20.1 --loader fabric --out results/1.20.1-fabric.json [--work DIR]
  compat_grid.py grid results/ --out grid/              -> grid/README.md + grid/grid.json

Dev mode tests what CI built from each branch instead of what is published:
  compat_grid.py plan-dev --repo OWNER/REPO [--branches 1.21.1,Java-Dev]  -> JSON list of {branch, mc, loader, run_id, label}
  compat_grid.py fetch-jar --repo OWNER/REPO --run-id N --loader fabric --out DIR -> prints the path of the CI-built jar
  compat_grid.py run ... --jar PATH --label BRANCH@SHA --branch BRANCH
  compat_grid.py grid results/ --out grid/ --dev
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
GRADLE_PROPERTIES = "ExtraBiomes - Java/gradle.properties"
BUILD_WORKFLOW = "gradle-build.yml"
DEV_BRANCH_PATTERN = re.compile(r"\d+\.\d+(\.\d+)?|Java-Dev")
PARTNERS = {
    "Terralith": "terralith",
    "Biomes O' Plenty": "biomes-o-plenty",
    "Oh The Biomes We've Gone": "oh-the-biomes-weve-gone",
    "Regions Unexplored": "regions-unexplored",
    "WWOO": "wwoo",
}
ALONE = "ExtraBiomes alone"
# TerraBlender's 26.3 build uses DataPackRegistryEvent$NewRegistry, which later 26.3 betas removed.
NEOFORGE_PINS = {"26.3": "26.3.0.19-beta"}
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
    if mc in NEOFORGE_PINS:
        return NEOFORGE_PINS[mc]
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


def resolve_dev_deps(mc, loader):
    """Dependencies for an ExtraBiomes build that may not be published: the newest published build's required dependencies, resolved for this version."""
    query = urllib.parse.urlencode({"loaders": json.dumps([loader])})
    newest = fetch_json(f"https://api.modrinth.com/v2/project/{EXTRABIOMES}/version?{query}")[0]
    resolved, missing = {}, []
    for dep in newest["dependencies"]:
        if dep["dependency_type"] != "required":
            continue
        versions, dep_missing = resolve_with_deps(dep["project_id"], mc, loader)
        resolved.update({v["project_id"]: v for v in versions})
        missing += dep_missing
    return list(resolved.values()), missing


def gh_api(path, raw=False):
    cmd = ["gh", "api", path] + (["-H", "Accept: application/vnd.github.raw"] if raw else [])
    out = subprocess.run(cmd, check=True, capture_output=True, text=True).stdout
    return out if raw else json.loads(out)


def parse_properties(text):
    return dict(line.split("=", 1) for line in text.splitlines() if "=" in line and not line.startswith("#"))


def newest_green(runs):
    # The API's own status filter and ordering have returned a stale run, so sort and filter here.
    green = [r for r in runs if r["conclusion"] == "success"]
    return max(green, key=lambda r: r["created_at"], default=None)


def dev_label(branch, built, tip):
    return f"{branch}@{built[:7]}" + ("" if built == tip else f" (tip {tip[:7]} not built)")


def cmd_plan_dev(args):
    """One entry per (branch, enabled loader), pointing at the newest green Build Mod run whose artifacts are still downloadable."""
    wanted = args.branches.split(",") if args.branches else None
    combos = []
    for entry in gh_api(f"repos/{args.repo}/branches?per_page=100"):
        branch, tip = entry["name"], entry["commit"]["sha"]
        if wanted is not None and branch not in wanted or wanted is None and not DEV_BRANCH_PATTERN.fullmatch(branch):
            continue
        props = parse_properties(gh_api(f"repos/{args.repo}/contents/{urllib.parse.quote(GRADLE_PROPERTIES)}?ref={urllib.parse.quote(branch)}", raw=True))
        run = newest_green(gh_api(f"repos/{args.repo}/actions/workflows/{BUILD_WORKFLOW}/runs?branch={urllib.parse.quote(branch)}&event=push&per_page=30")["workflow_runs"])
        if run is None:
            print(f"skipping {branch}: no green {BUILD_WORKFLOW} run", file=sys.stderr)
            continue
        if run["head_sha"] != tip:
            print(f"warning: {branch} tip {tip[:7]} has no green build; using {run['head_sha'][:7]}", file=sys.stderr)
        live = {a["name"] for a in gh_api(f"repos/{args.repo}/actions/runs/{run['id']}/artifacts?per_page=100")["artifacts"] if not a["expired"]}
        for loader in props["enabled_platforms"].split(","):
            if f"extrabiomes-{loader}-build" not in live:
                print(f"skipping {branch} {loader}: build artifact expired or missing", file=sys.stderr)
                continue
            combos.append({"branch": branch, "mc": props["minecraft_version"], "loader": loader, "run_id": run["id"], "label": dev_label(branch, run["head_sha"], tip)})
    print(json.dumps(combos))


def pick_jar(jars):
    """The remapped jar from a loader's build/libs: not the -raw, -sources or -dev intermediates."""
    candidates = [j for j in jars if not re.search(r"-(raw|sources|dev|dev-shadow)\.jar$", str(j))]
    if len(candidates) != 1:
        raise SystemExit(f"expected one release jar, found {[str(j) for j in candidates]} among {[str(j) for j in jars]}")
    return candidates[0]


def cmd_fetch_jar(args):
    out = Path(args.out)
    subprocess.run(["gh", "run", "download", str(args.run_id), "--repo", args.repo, "-n", f"extrabiomes-{args.loader}-build", "-D", str(out)], check=True, stdout=sys.stderr)
    print(pick_jar(sorted(out.glob("*.jar"))))


def cmd_plan(_):
    combos = {(gv, loader) for v in fetch_json(f"https://api.modrinth.com/v2/project/{EXTRABIOMES}/version") for gv in v["game_versions"] for loader in v["loaders"]}
    print(json.dumps([{"mc": mc, "loader": loader} for mc, loader in sorted(combos)]))


def cmd_run(args):
    work = Path(args.work).resolve()
    cache, server_dir, logs = work / "cache", work / f"server-{args.mc}-{args.loader}", Path(args.out).resolve().parent / "logs"
    for d in (cache, logs):
        d.mkdir(parents=True, exist_ok=True)
    result = {"mc": args.mc, "loader": args.loader, "results": {}}
    if args.jar:
        result["extrabiomes"] = args.label or f"local {Path(args.jar).name}"
        base_versions, base_missing = resolve_dev_deps(args.mc, args.loader)
    else:
        result["extrabiomes"] = modrinth_version(EXTRABIOMES, args.mc, args.loader)["version_number"]
        base_versions, base_missing = resolve_with_deps(EXTRABIOMES, args.mc, args.loader)
    if args.branch:
        result["branch"] = args.branch
    launch, result["loader_version"] = install_server(args.mc, args.loader, server_dir)

    partners = {n: p for n, p in PARTNERS.items() if not args.only or n in args.only.split(",")}
    for name, project in {ALONE: None, **partners}.items():
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
        jar = [Path(args.jar).resolve()] if args.jar else []
        entry["status"], entry["reason"] = boot(launch, server_dir, jar + files, logs / f"{args.mc}-{args.loader}-{slug}.log")
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
    rows = sorted((json.loads(p.read_text()) for p in Path(args.results).glob("*.json")), key=lambda r: (version_key(r["mc"]), r.get("branch", ""), r["loader"]))
    columns = [ALONE, *PARTNERS]
    today = datetime.date.today().isoformat()
    subject = ("the jar CI built from the tip of each branch (unreleased code)", "Compat Grid (dev) workflow") if args.dev else ("the published ExtraBiomes build", "Compat Grid workflow")
    lines = [
        "# ExtraBiomes worldgen compatibility" + (" (dev branches)" if args.dev else ""),
        "",
        f"Generated {today} by the {subject[1]}. Each cell boots a real server with {subject[0]} and one other mod, generates a world, and stops it.",
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
        lines += [f"- **{r['mc']} {r['loader']}{' (' + r['branch'] + ')' if 'branch' in r else ''} + {c}** ({e['status']}): `{e['reason']}`" for r, c, e in failures]
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
    assert parse_properties("# c\nminecraft_version=26.3\nenabled_platforms=fabric,neoforge\n") == {"minecraft_version": "26.3", "enabled_platforms": "fabric,neoforge"}
    assert [bool(DEV_BRANCH_PATTERN.fullmatch(b)) for b in ("1.20.1", "26.2", "1.21.11", "Java-Dev", "main", "Bedrock-Dev", "license-gpl3/1.20.1", "26.3-fix")] == [True, True, True, True, False, False, False, False]
    assert pick_jar([Path("a/ExtraBiomes-3.10-raw.jar"), Path("a/ExtraBiomes-3.10-sources.jar"), Path("a/ExtraBiomes-fabric-v3.10-26.3.jar")]).name == "ExtraBiomes-fabric-v3.10-26.3.jar"
    runs = [{"id": 1, "conclusion": "success", "created_at": "2026-09-10T09:51:18Z"}, {"id": 3, "conclusion": "failure", "created_at": "2026-09-29T06:40:33Z"}, {"id": 2, "conclusion": "success", "created_at": "2026-09-27T15:07:16Z"}]
    assert newest_green(runs)["id"] == 2 and newest_green([runs[1]]) is None
    assert dev_label("1.21.1", "04a6d61" + "0" * 33, "04a6d61" + "0" * 33) == "1.21.1@04a6d61"
    assert dev_label("1.21.1", "04a6d61" + "0" * 33, "9999999" + "0" * 33) == "1.21.1@04a6d61 (tip 9999999 not built)"
    print("selftest ok")


def main():
    parser = argparse.ArgumentParser()
    sub = parser.add_subparsers(dest="cmd", required=True)
    sub.add_parser("plan")
    plan_dev = sub.add_parser("plan-dev")
    plan_dev.add_argument("--repo", required=True)
    plan_dev.add_argument("--branches", help="comma-separated branches; default is every Minecraft-version branch plus Java-Dev")
    fetch_jar = sub.add_parser("fetch-jar")
    fetch_jar.add_argument("--repo", required=True)
    fetch_jar.add_argument("--run-id", required=True)
    fetch_jar.add_argument("--loader", required=True)
    fetch_jar.add_argument("--out", required=True)
    sub.add_parser("selftest")
    run = sub.add_parser("run")
    run.add_argument("--mc", required=True)
    run.add_argument("--loader", required=True)
    run.add_argument("--out", required=True)
    run.add_argument("--work", default="compat-work")
    run.add_argument("--jar", help="test this local ExtraBiomes jar instead of the published one")
    run.add_argument("--label", help="what to show in the ExtraBiomes column for --jar, e.g. Java-Dev@01261ab")
    run.add_argument("--branch", help="branch the --jar was built from, recorded in the result and shown next to failures")
    run.add_argument("--only", help="comma-separated partner names to test, e.g. Terralith")
    grid = sub.add_parser("grid")
    grid.add_argument("results")
    grid.add_argument("--out", required=True)
    grid.add_argument("--dev", action="store_true", help="describe the results as unreleased branch builds")
    args = parser.parse_args()
    {"plan": cmd_plan, "plan-dev": cmd_plan_dev, "fetch-jar": cmd_fetch_jar, "run": cmd_run, "grid": cmd_grid, "selftest": cmd_selftest}[args.cmd](args)


if __name__ == "__main__":
    sys.exit(main())
