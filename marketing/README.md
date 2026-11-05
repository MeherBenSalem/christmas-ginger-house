# Marketing assets: 2.0 "The Christmas Update"

Listing copy and artwork for CurseForge and Modrinth. Nothing here ships in the mod JARs.

## Listing

| File | Use |
| --- | --- |
| [`curseforge/description.md`](curseforge/description.md) | Full project description (Markdown; also works on Modrinth) |
| [`curseforge/summary.txt`](curseforge/summary.txt) | Short project summary (237 characters) |
| [`../Christmas-Ginger-House-2.0.0-PatchNotes.md`](../Christmas-Ginger-House-2.0.0-PatchNotes.md) | File changelog for 2.0.0 |

The description leads with the gingerbread house, then Christmas trees, presents, the advent calendar, decorations, creatures, Krampus, food and farming, gear, everything else, downloads and compatibility, the FAQ and a gallery. It ends with the **Follow NightBeam Studio** badge block (YouTube, Instagram, website, X, Facebook, Discord) and a source badge. Keep both when rewriting the copy.

Image links point at `raw.githubusercontent.com/MeherBenSalem/christmas-ginger-house/main/marketing/images/...`, so they work once this folder is on `main`. To host on CurseForge instead, upload the images to the project gallery and replace each URL.

## Adding a Minecraft version

The supported versions are listed in exactly two places:

1. `VERSIONS` at the top of [`source/render.cjs`](source/render.cjs). Add one line, for example `{ minecraft: '26.3', loaders: ['Fabric', 'NeoForge'] },`, then rerun the script for `banner`.
2. The table under **Downloads & Compatibility** in `curseforge/description.md`.

## Images

| File | Size | Use |
| --- | --- | --- |
| `images/banner.png` | 1800×600 | Description header: the decorated tree at night, "2.0 · The Christmas Update", loader pills and versions |
| `images/icon.png` | 512×512 | Project avatar: the iced gingerbread cookie in a candy-cane ring under a star |
| `images/card-house.png` | 1600×900 | The gingerbread house, with a world-generated one as an inset |
| `images/card-tree.png` | 1600×900 | A decorated tree at night, plus one generated in a taiga |
| `images/card-presents.png` | 1600×900 | The four steps of gifting: wrapping grid and the unwrap animation |
| `images/card-advent.png` | 1600×900 | Opening an advent door; 24 doors, 1 per day, gold finale |
| `images/card-decorations.png` | 1600×900 | The decorated facade at night and by day |
| `images/card-creatures.png` | 1600×900 | Gingerbread men and a red-nosed reindeer at dusk |
| `images/card-krampus.png` | 1600×900 | Krampus by the tree at night; 60 / 7 / 4 |
| `images/card-treats.png` | 1600×900 | Food items drawn from the mod's own textures |
| `images/card-gear.png` | 1600×900 | Santa hat and sweater on an armor stand, with the gear sprites |
| `images/card-farm.png` | 1600×900 | A ginger farm with wild ginger beside it |
| `images/card-everything.png` | 1600×900 | Every item in item frames |
| `images/header-*.png` | 1400×120, transparent | 13 section headings |
| `images/divider.png` | 1400×40, transparent | Section divider |
| `images/gifs/present-unwrap.gif` | 640×360, 4.5 s | A gold present being unwrapped |
| `images/gifs/lights-twinkle.gif` | 640×360, 4 s | The decorated facade's lights at night |
| `images/screenshots/*.jpg` | 1920×1080 | 16 captures: gallery uploads and card art |

### How the screenshots were captured

Every screenshot and both GIFs are unedited captures of the **1.20.1 Fabric development client** (the same scenes were checked on every other version and loader) (`./gradlew :fabric:runClient`, Christmas Ginger House 2.0.0, default configuration) with software rendering (Mesa llvmpipe on Xvfb) and GUI scale 3, HUD hidden unless a menu is shown.

- **World:** a regular world. `christmas-tree-taiga.jpg` and `gingerbread-house-worldgen.jpg` are the structures exactly as they generated. The other scenes were set up on a flattened patch of snowy taiga with `/place template` (the same templates worldgen uses) and `/setblock` for the decorated facade, farm and block sampler.
- **Creatures** were summoned with `NoAI` so they hold their pose. The red-nosed reindeer has `RedNose:1b`.
- **Lighting:** day shots at time 6000, dusk at 12700–13000, night at 14200–15000. Nothing was edited afterwards.
- **GIFs** were recorded from the X display with ffmpeg and converted with a 128-colour palette.

## Regenerating the artwork

The cards, banner, icon and headers are HTML rendered with Playwright from the screenshots above, the mod's textures and the bundled fonts:

```bash
NODE_PATH="$(npm root -g)" node marketing/source/render.cjs            # everything
NODE_PATH="$(npm root -g)" node marketing/source/render.cjs banner icon  # selected assets
```

- `source/backdrop.jpg` is the blurred background, a 1280×720 copy of `screenshots/christmas-tree-night.jpg`.
- `source/logo.png` is the mod icon shipped in the JAR.
- `source/fonts/` holds Cinzel and EB Garamond with their SIL Open Font License 1.1 texts, the same pair the other NightBeam listings use.
- Item sprites on the cards are read straight from `1.20.1/common/src/main/resources/assets/christmas_ginger_house/textures`.

## Wording

Say "Christmas" and "festive". Do not use other games', films' or brands' names, logos or art in listing images or titles. The red-nosed reindeer is this mod's own design: call it "a red-nosed reindeer" in artwork and titles.
