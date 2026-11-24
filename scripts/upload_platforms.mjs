/**
 * Upload Christmas Ginger House jars from releases/ (or --jar-dir) to CurseForge. NightBeam publishes to CurseForge
 * only (24 Nov 2026); the existing Modrinth project stays up as it is but gets no new versions.
 *
 *   node scripts/upload_platforms.mjs --version 2.0.2 [--dry-run] [--changelog-file FILE]
 *                                     [--games 26.3,26.2] [--jar-dir DIR]
 *
 * Jars are named christmas_ginger_house-<loader>-<minecraft>-<version>.jar; each one is published for exactly
 * that loader and Minecraft version (see SUPPORTED below: the 8 NightBeam convention targets). Files already on
 * CurseForge (or recorded in .release-upload-state.json) are skipped, so a rerun is safe.
 * A dry run without tokens only prints what would be uploaded and makes no network call.
 *
 * Release notes: --changelog-file, else patchnotes/Christmas-Ginger-House-<version>-PatchNotes.md, else the
 * "## <version>" section of CHANGELOG.md.
 *
 * Environment: CURSEFORGE_TOKEN and CURSEFORGE_API_KEY.
 * Optional: CURSEFORGE_ID (the repo variable) to override the project id below.
 * Locally, secrets can also live in secrets/local.env (KEY=value lines, never committed).
 */
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.join(__dirname, "..");

const CURSEFORGE_ID = process.env.CURSEFORGE_ID || "1409805";
const MOD_TITLE = "Christmas Ginger House";
const JAR_PREFIX = "christmas_ginger_house-";
const CURSEFORGE_GAME_ID = 432;
const DEFAULT_JAR_DIR = path.join(ROOT, "releases");
const RELEASE_STATE_PATH = path.join(ROOT, ".release-upload-state.json");

const CURSEFORGE_LOADER_IDS = { fabric: 7499, forge: 7498, neoforge: 10150 };

/** Minecraft version of each version root and the loaders it builds (NightBeam convention: 8 jars). */
const SUPPORTED = {
  "26.3": ["fabric", "neoforge"],
  "26.2": ["fabric", "neoforge"],
  "1.21.1": ["fabric", "neoforge"],
  "1.20.1": ["fabric", "forge"],
};

function loadLocalEnv() {
  const home = process.env.HOME || process.env.USERPROFILE || "";
  const candidates = [
    path.join(ROOT, "secrets", "local.env"),
    path.join(home, "NightBeam-Knowledge-Base", "secrets", "local.env"),
  ];
  for (const envPath of candidates) {
    if (!fs.existsSync(envPath)) continue;
    for (const line of fs.readFileSync(envPath, "utf8").split(/\r?\n/)) {
      const m = line.match(/^([^#=]+)=(.*)$/);
      if (m && !process.env[m[1].trim()]) process.env[m[1].trim()] = m[2].trim();
    }
    console.log("Loaded secrets from", envPath);
    return;
  }
}

function parseArgs(argv) {
  const out = { dryRun: false, jarDir: DEFAULT_JAR_DIR };
  for (let i = 2; i < argv.length; i++) {
    const a = argv[i];
    if (a === "--version") out.version = argv[++i];
    else if (a === "--changelog-file") out.changelogFile = argv[++i];
    else if (a === "--dry-run") out.dryRun = true;
    else if (a === "--jar-dir") out.jarDir = path.resolve(argv[++i] || "");
    else if (a === "--games") out.games = (argv[++i] || "").split(/[\s,]+/).filter(Boolean);
    else throw new Error(`Unknown arg ${a}`);
  }
  if (!out.version) throw new Error("--version required");
  out.version = out.version.replace(/^v/, "");
  if (out.games && !out.games.length) throw new Error("--games needs at least one Minecraft version");
  for (const game of out.games || []) {
    if (!SUPPORTED[game]) throw new Error(`--games: ${game} is not a version root of this repository`);
  }
  return out;
}

function parseJar(jar) {
  const name = path.basename(jar);
  const m = name.match(/^christmas_ginger_house-(fabric|forge|neoforge)-(.+)-(\d+\.\d+\.\d+)\.jar$/i);
  if (!m) throw new Error(`Cannot parse jar name: ${name}`);
  const loader = m[1].toLowerCase();
  const game = m[2];
  if (!SUPPORTED[game]?.includes(loader)) {
    throw new Error(`${name}: ${loader} on Minecraft ${game} is not a version root of this repository`);
  }
  return { jar, name, loader, game, version: m[3] };
}

function collectJars(version, jarDir) {
  if (!fs.existsSync(jarDir)) throw new Error(`Missing ${jarDir}; build first (./gradlew buildAll)`);
  return fs
    .readdirSync(jarDir)
    .filter((n) => n.startsWith(JAR_PREFIX) && n.endsWith(`-${version}.jar`))
    .map((n) => path.join(jarDir, n))
    .sort();
}

/** This version's patch notes, else its "## x.y.z" section of CHANGELOG.md. */
function readChangelog(version, changelogFile) {
  if (changelogFile) return fs.readFileSync(changelogFile, "utf8");
  const notes = path.join(ROOT, "patchnotes", `Christmas-Ginger-House-${version}-PatchNotes.md`);
  if (fs.existsSync(notes)) {
    console.log("Changelog:", path.relative(ROOT, notes));
    return fs.readFileSync(notes, "utf8");
  }
  const file = path.join(ROOT, "CHANGELOG.md");
  if (fs.existsSync(file)) {
    const lines = fs.readFileSync(file, "utf8").replace(/^﻿/, "").split(/\r?\n/);
    const heading = new RegExp(`^##\\s+v?${version.replace(/\./g, "\\.")}(\\s|$)`);
    const start = lines.findIndex((l) => heading.test(l));
    if (start >= 0) {
      let end = lines.findIndex((l, i) => i > start && /^##?\s/.test(l));
      if (end < 0) end = lines.length;
      const section = lines.slice(start + 1, end).join("\n").trim();
      if (section) {
        console.log("Changelog: CHANGELOG.md section", lines[start].trim());
        return section;
      }
    }
  }
  return `## ${MOD_TITLE} ${version}`;
}

function loadReleaseState() {
  try {
    const parsed = JSON.parse(fs.readFileSync(RELEASE_STATE_PATH, "utf8"));
    if (Array.isArray(parsed.uploads)) return parsed;
  } catch {}
  return { uploads: [] };
}

function hasRecordedUpload(state, platform, version, jar) {
  return state.uploads.some((e) => e.platform === platform && e.version === version && e.fileName === jar.name);
}

function recordUpload(state, platform, version, jar, remoteId) {
  if (hasRecordedUpload(state, platform, version, jar)) return;
  state.uploads.push({ platform, version, loader: jar.loader, game: jar.game, fileName: jar.name, remoteId, recordedAt: new Date().toISOString() });
  fs.writeFileSync(RELEASE_STATE_PATH, `${JSON.stringify(state, null, 2)}\n`, "utf8");
}

const versionLabel = (version, jar) => `${version} · ${jar.loader} · ${jar.game}`;

async function fetchJson(url, options = {}, allow404 = false) {
  const res = await fetch(url, options);
  if (allow404 && res.status === 404) return null;
  const text = await res.text();
  if (!res.ok) throw new Error(`${url} -> ${res.status} ${text.slice(0, 500)}`);
  return text ? JSON.parse(text) : null;
}

// ------------------------------------------------------------------ CurseForge

async function resolveCurseForgeCreds(token, apiKey) {
  // NightBeam local.env sometimes swaps CURSEFORGE_TOKEN / CURSEFORGE_API_KEY names.
  // Core API wants x-api-key; the legacy upload API wants X-Api-Token.
  for (const [coreKey, uploadToken] of [
    [apiKey, token],
    [token, apiKey],
  ]) {
    const res = await fetch(`https://api.curseforge.com/v1/mods/${CURSEFORGE_ID}`, {
      headers: { "x-api-key": coreKey, Accept: "application/json" },
    });
    if (res.ok) return { coreKey, uploadToken };
  }
  throw new Error("Neither CURSEFORGE_TOKEN nor CURSEFORGE_API_KEY works as CurseForge Core API key");
}

async function fetchExistingCurseForge(apiKey) {
  const payload = await fetchJson(`https://api.curseforge.com/v1/mods/${CURSEFORGE_ID}/files?pageSize=200&index=0`, {
    headers: { "x-api-key": apiKey, Accept: "application/json" },
  });
  const fileNames = new Set();
  const displayNames = new Set();
  for (const file of payload?.data || []) {
    if (file.fileName) fileNames.add(file.fileName);
    if (file.displayName) displayNames.add(file.displayName);
  }
  return { fileNames, displayNames };
}

async function fetchCurseForgeVersionContext(token, apiKey) {
  const legacyPayload = await fetchJson("https://minecraft.curseforge.com/api/game/versions", {
    headers: { "X-Api-Token": token },
  });
  const legacyFlat = Array.isArray(legacyPayload) ? legacyPayload : legacyPayload?.data || [];
  const findLegacy = (want) => {
    const hit = legacyFlat.find((v) => v.name === want);
    if (!hit) throw new Error(`No CurseForge legacy version for ${want}`);
    return hit.id;
  };
  const versionTypesPayload = await fetchJson(`https://api.curseforge.com/v1/games/${CURSEFORGE_GAME_ID}/version-types`, {
    headers: { "x-api-key": apiKey, Accept: "application/json" },
  });
  const minecraftTypeIds = [];
  for (const type of versionTypesPayload?.data || []) {
    if (/minecraft/i.test(type.name) && !/bukkit|spigot|paper|plugin/i.test(type.name)) minecraftTypeIds.push(type.id);
  }
  return {
    legacyFlat,
    clientId: findLegacy("Client"),
    serverId: findLegacy("Server"),
    minecraftTypeIds: [...minecraftTypeIds, 77784, 1],
  };
}

async function resolveCurseForgeGameId(game, apiKey, legacyFlat, minecraftTypeIds) {
  const direct = await fetchJson(
    `https://api.curseforge.com/v1/minecraft/version/${encodeURIComponent(game)}`,
    { headers: { "x-api-key": apiKey, Accept: "application/json" } },
    true,
  );
  const directId = direct?.data?.gameVersionId;
  if (directId) return directId;
  const slug = game.replace(/\./g, "-");
  const candidates = legacyFlat.filter((v) => v.name === game || v.slug === slug);
  for (const typeId of minecraftTypeIds) {
    const hit = candidates.find((v) => v.gameVersionTypeID === typeId);
    if (hit) return hit.id;
  }
  throw new Error(`No CurseForge game version id for ${game}`);
}

function createCurseForgeDescriptor(jar, version, changelog, clientId, serverId, loaderId, gameId) {
  return {
    changelog,
    changelogType: "markdown",
    displayName: versionLabel(version, jar),
    gameVersions: [clientId, serverId, loaderId, gameId],
    releaseType: "release",
    ...(jar.loader === "fabric" ? { relations: { projects: [{ slug: "fabric-api", type: "requiredDependency" }] } } : {}),
  };
}

async function uploadToCurseForge(jars, version, changelog, token, apiKey, dryRun, state) {
  if (dryRun && (!token || !apiKey)) {
    for (const jar of jars) console.log("[dry-run] CurseForge", versionLabel(version, jar), jar.name);
    return;
  }
  const { coreKey, uploadToken } = await resolveCurseForgeCreds(token, apiKey);
  const existing = await fetchExistingCurseForge(coreKey);
  const ctx = await fetchCurseForgeVersionContext(uploadToken, coreKey);
  let uploaded = 0;
  let skipped = 0;
  for (const jar of jars) {
    const loaderId = CURSEFORGE_LOADER_IDS[jar.loader];
    if (!loaderId) throw new Error(`Unknown loader for ${jar.name}`);
    const gameId = await resolveCurseForgeGameId(jar.game, coreKey, ctx.legacyFlat, ctx.minecraftTypeIds);
    const meta = createCurseForgeDescriptor(jar, version, changelog, ctx.clientId, ctx.serverId, loaderId, gameId);
    if (hasRecordedUpload(state, "curseforge", version, jar) || existing.fileNames.has(jar.name) || existing.displayNames.has(meta.displayName)) {
      console.log("CurseForge skip (already published)", meta.displayName, jar.name);
      skipped += 1;
      continue;
    }
    if (dryRun) {
      console.log("[dry-run] CurseForge", meta.displayName, jar.name);
      uploaded += 1;
      continue;
    }
    for (let attempt = 1; ; attempt++) {
      const form = new FormData();
      form.append("metadata", JSON.stringify(meta));
      form.append("file", new Blob([fs.readFileSync(jar.jar)]), jar.name);
      const res = await fetch(`https://minecraft.curseforge.com/api/projects/${CURSEFORGE_ID}/upload-file`, {
        method: "POST",
        headers: { "X-Api-Token": uploadToken },
        body: form,
      });
      const text = await res.text();
      if (res.ok) {
        let remoteId = null;
        try {
          remoteId = JSON.parse(text).id ?? null;
        } catch {}
        recordUpload(state, "curseforge", version, jar, remoteId);
        console.log("CurseForge OK", meta.displayName, jar.name);
        uploaded += 1;
        break;
      }
      if ((res.status === 429 || res.status >= 500) && attempt < 4) {
        console.warn(`CurseForge ${res.status} for ${jar.name}, retrying in ${15 * attempt}s`);
        await new Promise((resolve) => setTimeout(resolve, 15000 * attempt));
        continue;
      }
      throw new Error(`CurseForge ${res.status} ${jar.name} ${text.slice(0, 500)}`);
    }
  }
  console.log("CurseForge result:", uploaded, "upload(s),", skipped, "skip(s)");
}

// ------------------------------------------------------------------ main

async function main() {
  loadLocalEnv();
  const args = parseArgs(process.argv);
  // --games publishes only those Minecraft versions, e.g. roots added to an already released version.
  const jars = collectJars(args.version, args.jarDir).map(parseJar).filter((j) => !args.games || args.games.includes(j.game));
  if (!jars.length) throw new Error(`No ${JAR_PREFIX}*-${args.version}.jar in ${args.jarDir}`);
  for (const game of args.games || []) {
    const missing = SUPPORTED[game].filter((loader) => !jars.some((j) => j.game === game && j.loader === loader));
    if (missing.length) throw new Error(`--games: no ${missing.join(", ")} jar for Minecraft ${game}`);
  }
  const changelog = readChangelog(args.version, args.changelogFile);
  const state = loadReleaseState();
  const { CURSEFORGE_TOKEN, CURSEFORGE_API_KEY } = process.env;
  if (!args.dryRun && (!CURSEFORGE_TOKEN || !CURSEFORGE_API_KEY)) throw new Error("CURSEFORGE_TOKEN and CURSEFORGE_API_KEY must be set");

  console.log(`Publishing ${MOD_TITLE} ${args.version}${args.dryRun ? " (dry run)" : ""} (CurseForge ${CURSEFORGE_ID})`);
  for (const j of jars) console.log(`  ${j.name} -> ${j.loader} ${j.game}`);

  await uploadToCurseForge(jars, args.version, changelog, CURSEFORGE_TOKEN, CURSEFORGE_API_KEY, args.dryRun, state);
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});
