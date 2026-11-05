// Renders the listing artwork in marketing/images/ from real in-game screenshots, the mod's own
// textures and the bundled OFL fonts.
// Usage (from the repository root): NODE_PATH="$(npm root -g)" node marketing/source/render.cjs [name...]
// Requires the `playwright` package with a Chromium build available.
const path = require('path');
const fs = require('fs');
const { chromium } = require('playwright');

// The release and the supported Minecraft versions. Adding a version is one line here
// (plus one row in the compatibility table of curseforge/description.md).
const RELEASE = { version: '2.0', name: 'The Christmas Update' };
const VERSIONS = [
  { minecraft: '1.20.1', loaders: ['Fabric', 'Forge'] },
  { minecraft: '1.21.1', loaders: ['Fabric', 'NeoForge'] },
  { minecraft: '26.1.2', loaders: ['Fabric', 'NeoForge'] },
  { minecraft: '26.2', loaders: ['Fabric', 'NeoForge'] },
];
const LOADERS = ['Fabric', 'Forge', 'NeoForge'].filter((loader) => VERSIONS.some((v) => v.loaders.includes(loader)));

const root = path.resolve(__dirname, '..', '..');
const out = path.join(root, 'marketing', 'images');
const shots = path.join(out, 'screenshots');
const fonts = path.join(__dirname, 'fonts');
const textures = path.join(root, '1.20.1', 'common', 'src', 'main', 'resources', 'assets', 'christmas_ginger_house', 'textures');
const url = (file) => 'file://' + file;
const shot = (name) => url(path.join(shots, name));
const source = (name) => url(path.join(__dirname, name));
const tex = (kind, name) => url(path.join(textures, kind, name + '.png'));

const css = `
@font-face { font-family: XSerif; src: url('${url(path.join(fonts, 'eb-garamond.ttf'))}'); }
@font-face { font-family: XDisplay; src: url('${url(path.join(fonts, 'cinzel.ttf'))}'); }
:root { --text:#e4e9ef; --dim:#a3adba; --header:#f6f1e4; --gold:#e8c46a; --gold-hi:#ffe39a; --red:#e2414f; --green:#4fbf6b;
        --bg:#070d17; --line:rgba(232,196,106,.4); }
* { box-sizing: border-box; }
html, body { margin: 0; background: var(--bg); color: var(--text); font-family: XSerif, serif; }
.stage { position: relative; overflow: hidden; background: radial-gradient(ellipse at 30% 35%, #13233a 0%, #070d17 72%); }
.display { font-family: XDisplay, serif; letter-spacing: .04em; }
.gold { background: linear-gradient(180deg, #fff6d6 0%, #ffe39a 35%, #e8c46a 70%, #9a7a3a 100%);
        -webkit-background-clip: text; background-clip: text; color: transparent; }
.glow { filter: drop-shadow(0 0 18px rgba(232,196,106,.35)); }
.kicker { font-family: XDisplay, serif; color: var(--gold); letter-spacing: .22em; font-size: 22px; }
.kicker .flake { color: #dfeaf6; margin-right: 12px; }
.rule { height: 2px; background: repeating-linear-gradient(90deg, #e2414f 0 14px, #f6f1e4 14px 28px);
        mask: linear-gradient(90deg, transparent, #000 15%, #000 75%, transparent); -webkit-mask: linear-gradient(90deg, transparent, #000 15%, #000 75%, transparent); }
.frame { position: relative; border: 3px solid #f6f1e4; outline: 3px solid #b8252f; outline-offset: 3px;
         box-shadow: 0 30px 80px rgba(0,0,0,.7), 0 0 60px rgba(232,196,106,.18); background: #000; }
.frame > img { display: block; width: 100%; }
canvas.snow { position: absolute; left: 0; top: 0; width: 100%; height: 100%; pointer-events: none; }
.vignette { position: absolute; inset: 0; background: radial-gradient(ellipse at center, rgba(0,0,0,0) 45%, rgba(0,0,0,.75) 100%); }
.backdrop { position: absolute; inset: -40px; background-size: cover; background-position: center; filter: blur(14px) brightness(.38) saturate(1.15); }
.pill { display: inline-block; border: 1px solid rgba(246,241,228,.55); padding: 6px 14px 4px; margin-right: 10px;
        font-family: XDisplay, serif; font-size: 18px; letter-spacing: .12em; color: var(--header); background: rgba(0,0,0,.35); }
.label { position: absolute; font-family: XDisplay, serif; font-size: 20px; letter-spacing: .12em; color: var(--header);
         background: rgba(7,13,23,.88); border: 1px solid var(--gold); padding: 7px 14px 5px; white-space: nowrap;
         box-shadow: 0 8px 26px rgba(0,0,0,.6); z-index: 2; }
.label small { display: block; font-family: XSerif, serif; letter-spacing: 0; font-size: 18px; color: var(--dim); margin-top: 1px; }
ul.points { list-style: none; padding: 0; margin: 0; }
ul.points li { position: relative; padding-left: 30px; margin: 0 0 14px; font-size: 27px; line-height: 1.32; color: var(--text); }
ul.points li::before { content: '\\2744'; position: absolute; left: 0; top: 1px; font-size: 20px; color: #cfe3f7; }
.stats { display: flex; margin-top: 22px; border: 1px solid rgba(246,241,228,.35); background: rgba(7,13,23,.84); box-shadow: 0 18px 50px rgba(0,0,0,.55); }
.stats > div { flex: 1; padding: 14px 22px 12px; border-left: 1px solid rgba(246,241,228,.2); }
.stats > div:first-child { border-left: 0; }
.stats .t { font-family: XDisplay, serif; font-size: 15px; letter-spacing: .18em; color: var(--dim); }
.stats .v { font-family: XDisplay, serif; font-size: 28px; color: var(--header); margin-top: 4px; white-space: nowrap; }
b.hi { color: var(--header); font-weight: normal; }
img.px { image-rendering: pixelated; }
.tile { background: rgba(7,13,23,.84); border: 1px solid rgba(246,241,228,.3); display: flex; flex-direction: column; align-items: center;
        padding: 16px 8px 12px; box-shadow: 0 12px 30px rgba(0,0,0,.5); }
.tile img { width: 96px; height: 96px; image-rendering: pixelated; filter: drop-shadow(0 6px 10px rgba(0,0,0,.6)); }
.tile span { font-family: XDisplay, serif; font-size: 17px; letter-spacing: .08em; color: var(--header); margin-top: 10px; text-align: center; }
.tile small { font-size: 18px; color: var(--dim); text-align: center; margin-top: 4px; line-height: 1.2; }
.step { display: inline-flex; width: 40px; height: 40px; border-radius: 50%; background: #b8252f; border: 2px solid #f6f1e4; color: #fff;
        font-family: XDisplay, serif; font-size: 22px; align-items: center; justify-content: center; margin-right: 10px; vertical-align: middle; }
`;

// Gentle snowfall. Deterministic so renders are reproducible.
const snow = `<canvas class="snow"></canvas><script>
(() => { const c = document.querySelector('canvas.snow'); const r = c.getBoundingClientRect();
  c.width = r.width; c.height = r.height; const g = c.getContext('2d'); let s = 25;
  const rnd = () => (s = (s * 16807) % 2147483647) / 2147483647;
  for (let i = 0; i < 140; i++) { const x = rnd() * c.width, y = rnd() * c.height, a = .25 + rnd() * .6, z = .8 + rnd() * 2.4;
    const h = g.createRadialGradient(x, y, 0, x, y, z * 3); h.addColorStop(0, 'rgba(255,255,255,' + a + ')'); h.addColorStop(1, 'rgba(255,255,255,0)');
    g.fillStyle = h; g.beginPath(); g.arc(x, y, z * 3, 0, Math.PI * 2); g.fill(); } })();
</script>`;

const page = (width, height, body, transparent = false) => `<!doctype html><html><head><meta charset="utf-8"><style>${css}
body { width: ${width}px; height: ${height}px; } .stage { width: ${width}px; height: ${height}px; }
${transparent ? 'html, body, .stage { background: transparent; }' : ''}</style></head>
<body><div class="stage">${body}</div></body></html>`;

const header = (title) => page(1400, 120, `
  <div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;gap:26px;background:linear-gradient(90deg,rgba(0,0,0,0),rgba(7,13,23,.92) 28%,rgba(7,13,23,.92) 72%,rgba(0,0,0,0))">
    <div class="rule" style="width:280px;transform:scaleX(-1)"></div>
    <img class="px" src="${tex('item', 'star_topper')}" style="width:40px;height:40px">
    <div class="display gold glow" style="font-size:44px;white-space:nowrap">${title}</div>
    <img class="px" src="${tex('item', 'star_topper')}" style="width:40px;height:40px">
    <div class="rule" style="width:280px"></div>
  </div>`, true);

// A feature card: copy on the left, artwork on the right.
const card = ({ kicker, title, points, art, extra = '', backdrop = 'backdrop.jpg', width = 1600, height = 900, artWidth = 900 }) => page(width, height, `
  <div class="backdrop" style="background-image:url('${source(backdrop)}');opacity:.6"></div>
  <div class="vignette"></div>${snow}
  <div style="position:absolute;left:80px;top:0;bottom:0;width:${width - artWidth - 160}px;display:flex;flex-direction:column;justify-content:center">
    <div class="kicker" style="font-size:20px"><span class="flake">&#10052;</span>${kicker}</div>
    <div class="display gold glow" style="font-size:56px;line-height:1.08;margin:18px 0 22px">${title}</div>
    <div class="rule" style="width:80%;margin-bottom:30px"></div>
    <ul class="points">${points.map((point) => `<li>${point}</li>`).join('')}</ul>${extra}
  </div>
  <div style="position:absolute;right:64px;top:50%;transform:translateY(-50%);width:${artWidth}px">${art}</div>`);

const frame = (name, style = '') => `<div class="frame" style="${style}"><img src="${shot(name)}"></div>`;
const tiles = (items, cols = 4) => `<div style="display:grid;grid-template-columns:repeat(${cols},1fr);gap:18px">${items.map(([kind, file, name, note]) =>
  `<div class="tile"><img src="${tex(kind, file)}"><span>${name}</span>${note ? `<small>${note}</small>` : ''}</div>`).join('')}</div>`;
const stats = (cells) => `<div class="stats">${cells.map(([t, v]) => `<div><div class="t">${t}</div><div class="v">${v}</div></div>`).join('')}</div>`;

const assets = {
  banner: () => page(1800, 600, `
    <div class="backdrop" style="background-image:url('${source('backdrop.jpg')}')"></div>
    <div style="position:absolute;inset:0;background:linear-gradient(90deg,rgba(7,13,23,.95) 0%,rgba(7,13,23,.8) 42%,rgba(7,13,23,.1) 100%)"></div>
    <div class="vignette"></div>${snow}
    <div style="position:absolute;right:-50px;top:64px;width:960px;transform:perspective(1600px) rotateY(-15deg) rotateX(3deg);transform-origin:left center">
      ${frame('christmas-tree-night.jpg')}</div>
    <img class="px" src="${tex('item', 'gingerbread_cookie')}" style="position:absolute;left:66px;top:60px;width:150px;height:150px;filter:drop-shadow(0 10px 20px rgba(0,0,0,.7))">
    <div style="position:absolute;left:240px;top:70px;width:900px">
      <div class="kicker"><span class="flake">&#10052;</span>${RELEASE.version} &middot; ${RELEASE.name}</div>
      <div class="display gold glow" style="font-size:72px;line-height:1.04;margin:16px 0 0;white-space:nowrap">Christmas<br>Ginger House</div>
    </div>
    <div style="position:absolute;left:90px;top:330px;width:900px">
      <div class="rule" style="width:640px;margin-bottom:24px"></div>
      <div style="font-size:33px;color:var(--header);line-height:1.3">The all-in-one Christmas mod. Gingerbread houses,<br>presents for friends, reindeer, and Krampus.</div>
      <div style="margin-top:32px">${LOADERS.map((l) => `<span class="pill">${l}</span>`).join('')}
        <span style="font-size:24px;color:var(--dim);margin-left:8px">${VERSIONS.map((v) => v.minecraft).join(' · ')}</span></div>
    </div>`),

  icon: () => page(512, 512, `
    <div style="position:absolute;inset:0;background:radial-gradient(circle at 50% 40%,#1d3a5c 0%,#0c1a2c 58%,#050a12 100%)"></div>${snow}
    <div style="position:absolute;left:0;right:0;bottom:0;height:120px;background:linear-gradient(180deg,#e9f1f7,#c9d8e6)"></div>
    <div style="position:absolute;left:40px;top:40px;width:432px;height:432px;border-radius:50%;border:10px solid transparent;
      background:repeating-conic-gradient(#d8253b 0 15deg,#f6f1e4 15deg 30deg) border-box;
      -webkit-mask:linear-gradient(#000 0 0) padding-box exclude,linear-gradient(#000 0 0);mask:linear-gradient(#000 0 0) padding-box exclude,linear-gradient(#000 0 0)"></div>
    <img class="px" src="${tex('item', 'gingerbread_cookie')}" style="position:absolute;left:96px;top:86px;width:320px;height:320px;filter:drop-shadow(0 14px 24px rgba(0,0,0,.6))">
    <img class="px" src="${tex('item', 'star_topper')}" style="position:absolute;left:216px;top:14px;width:80px;height:80px;filter:drop-shadow(0 0 16px rgba(255,216,74,.8))">`),

  'card-house': () => card({
    kicker: 'Worldgen', title: 'A house made<br>of gingerbread',
    points: ['Generates in snowy plains, snowy taiga, groves, snowy slopes and plains',
      'Gingerbread walls, an iced roof, lattice shutters and a candy-cane chimney',
      'A wreath on the door, stockings, lights on the porch and <b class="hi">wrapped presents</b> in the yard',
      'Barrels stocked with baking supplies and treats'],
    art: `<div style="position:relative">${frame('gingerbread-house.jpg')}
      <div class="frame" style="position:absolute;right:-30px;bottom:-70px;width:44%">${'<img src="' + shot('gingerbread-house-worldgen.jpg') + '">'}</div>
      <div class="label" style="right:-10px;bottom:-114px">Found in a snowy taiga<small>regular worldgen, no commands</small></div></div>` }),

  'card-tree': () => card({
    kicker: 'Worldgen', title: 'Decorated trees<br>in the snow',
    points: ['Christmas trees generate across snowy biomes and taiga',
      'A spiral of twinkling lights, hanging ornaments and a star on top',
      'Sealed presents underneath. One tree in four hides a <b class="hi">gold present</b>',
      'Every decoration can be crafted, so you can build your own'],
    backdrop: 'backdrop.jpg',
    art: `<div style="position:relative">${frame('christmas-tree-night.jpg')}
      <div class="frame" style="position:absolute;right:-30px;bottom:-70px;width:42%"><img src="${shot('christmas-tree-taiga.jpg')}"></div></div>` }),

  'card-presents': () => card({
    kicker: 'Presents', title: 'Wrap a gift<br>for a friend',
    points: ['<span class="step">1</span>Place a present and use it to open the 3&times;3 wrapping grid',
      '<span class="step">2</span>Fill it and close it. Your name goes on the tag',
      '<span class="step">3</span>Break it. The gift travels inside the item',
      '<span class="step">4</span>Your friend places it and uses it: confetti, a jingle, and the gift pops out'],
    art: `<div style="position:relative">${frame('present-wrapping.jpg')}
      <div class="frame" style="position:absolute;right:-30px;bottom:-80px;width:46%"><img src="${url(path.join(out, 'gifs', 'present-unwrap.gif'))}"></div></div>` }),

  'card-advent': () => card({
    kicker: 'Advent Calendar', title: 'Twenty-four<br>little doors',
    points: ['Open one door per Minecraft day, from any calendar', 'Each door holds a treat: candy, cocoa, emeralds, or a sealed present',
      'Door 24 opens a <b class="hi">gold present</b> and a festive extra', 'Progress is saved per player, per world'],
    extra: stats([['Doors', '24'], ['Per day', '1'], ['Finale', 'Gold']]),
    art: `<div style="position:relative">${frame('advent-calendar.jpg')}
      <img class="px" src="${tex('block', 'advent_calendar_front')}" style="position:absolute;right:-40px;bottom:-60px;width:200px;height:220px;border:3px solid #f6f1e4;box-shadow:0 20px 50px rgba(0,0,0,.7)"></div>` }),

  'card-decorations': () => card({
    kicker: 'Deck the halls', title: 'Lights, wreaths<br>and stockings',
    points: ['Christmas lights twinkle and glow at light level 11, on walls and leaves alike',
      'Wreaths, stockings and a shining star topper', 'Red, gold and blue ornaments hang from branches or stand on tables',
      'Candy-cane pillars, peppermint, icing and gingerbread to build with'],
    art: `<div style="position:relative">${frame('decorations-night.jpg')}
      <div class="frame" style="position:absolute;right:-30px;bottom:-70px;width:40%"><img src="${shot('decorations.jpg')}"></div></div>` }),

  'card-creatures': () => card({
    kicker: 'Creatures', title: 'Gingerbread men<br>and reindeer',
    points: ['<b class="hi">Gingerbread Men</b> wander snowy biomes, follow icing and sugar, and bolt from foxes and wolves',
      '<b class="hi">Reindeer</b> live in herds. Breed them with carrots, apples or candy canes',
      'One reindeer in fifty has a glowing red nose, and red-nosed parents pass it on',
      'Ring <b class="hi">Sleigh Bells</b> and every reindeer nearby comes running'],
    art: `<div style="position:relative;height:640px">
      <div class="frame" style="position:absolute;left:0;top:0;width:76%"><img src="${shot('gingerbread-men.jpg')}"></div>
      <div class="frame" style="position:absolute;right:0;bottom:0;width:62%"><img src="${shot('red-nosed-reindeer.jpg')}"></div></div>` }),

  'card-krampus': () => card({
    kicker: 'The other side of the season', title: 'Beware<br>of Krampus',
    points: ['A rare night stalker in snowy and frozen biomes', 'Rattles his chains when he has your scent',
      'His blows slow you down: Slowness II, longer on Hard', 'Drops Naughty Coal, chains, and sometimes a stolen gold present'],
    extra: stats([['Health', '60'], ['Damage', '7'], ['Armor', '4']]),
    art: `<div class="frame" style="height:560px;overflow:hidden"><img src="${shot('krampus.jpg')}" style="width:175%;margin-left:-38%;margin-top:-32%"></div>` }),

  'card-treats': () => card({
    kicker: 'Festive food', title: 'Bake, ice<br>and enjoy',
    points: ['Grow ginger, mix gingerbread dough and bake it in a furnace, smoker or campfire',
      'Ice it into cookies that grant Absorption', 'Candy canes give a quick sugar rush',
      'Hot cocoa thaws you out and slowly heals'],
    art: tiles([['item', 'ginger_root', 'Ginger Root', 'plant it'], ['item', 'gingerbread_dough', 'Dough', 'bake it'],
      ['item', 'gingerbread', 'Gingerbread', '5 hunger'], ['item', 'icing', 'Icing', 'sugar + egg'],
      ['item', 'gingerbread_cookie', 'Cookie', 'Absorption'], ['item', 'candy_cane', 'Candy Cane', 'Speed'],
      ['item', 'hot_cocoa', 'Hot Cocoa', 'Regeneration'], ['item', 'naughty_coal', 'Naughty Coal', 'burns 16 items']]) }),

  'card-gear': () => card({
    kicker: 'Gear', title: 'Dress the part',
    points: ['A <b class="hi">Santa Hat</b> with its own 3D model, pompom and all', 'A knitted <b class="hi">Festive Sweater</b>',
      'Either one keeps you from freezing in powder snow', 'The <b class="hi">Candy Cane Sword</b>: iron-tier, with a sugar rush on every hit',
      'A <b class="hi">Jingle Bells</b> music-box record for your jukebox'],
    art: `<div style="position:relative">${frame('santa-hat-sweater.jpg')}
      <div style="position:absolute;right:-20px;bottom:-70px;display:flex;gap:14px">${['santa_hat', 'festive_sweater', 'candy_cane_sword', 'sleigh_bells', 'music_disc_jingle_bells']
        .map((n) => `<div class="tile" style="padding:12px"><img src="${tex('item', n)}" style="width:80px;height:80px"></div>`).join('')}</div></div>` }),

  'card-farm': () => card({
    kicker: 'Farming', title: 'Grow your<br>own ginger',
    points: ['Wild ginger with its red flowers grows in taiga and snowy forests', 'Break it for ginger root, or shear it to keep the plant',
      'Plant ginger root on farmland. Four growth stages', 'Bees speed it up and Fortune applies'],
    art: frame('ginger-farm.jpg') }),

  'card-everything': () => card({
    kicker: 'All in one tab', title: 'Forty festive<br>items',
    points: ['Gingerbread blocks, bricks, stairs, slabs, walls, a door and a trapdoor', 'Icing, candy-cane and peppermint blocks',
      'Four colours of present, three ornaments, lights, wreath, stocking and star', 'Recipes for everything, all in one creative tab'],
    art: frame('item-gallery.jpg') }),
};

const headers = {
  'header-house': 'The Gingerbread House', 'header-trees': 'Christmas Trees', 'header-presents': 'Presents',
  'header-advent': 'The Advent Calendar', 'header-decorations': 'Decorations', 'header-creatures': 'Creatures',
  'header-krampus': 'Krampus', 'header-food': 'Festive Food & Farming', 'header-gear': 'Gear', 'header-everything': 'Everything Else',
  'header-compatibility': 'Downloads & Compatibility', 'header-faq': 'Questions', 'header-gallery': 'Gallery',
};
for (const [name, title] of Object.entries(headers)) assets[name] = () => header(title);
assets.divider = () => page(1400, 40, `<div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;gap:18px">
  <div class="rule" style="width:520px;transform:scaleX(-1)"></div><img class="px" src="${tex('item', 'red_ornament')}" style="width:32px;height:32px"><div class="rule" style="width:520px"></div></div>`, true);

(async () => {
  const wanted = process.argv.slice(2);
  const browser = await chromium.launch();
  fs.mkdirSync(out, { recursive: true });
  for (const [name, build] of Object.entries(assets)) {
    if (wanted.length && !wanted.includes(name)) continue;
    const html = build();
    const size = html.match(/body \{ width: (\d+)px; height: (\d+)px; \}/);
    const tab = await browser.newPage({ viewport: { width: +size[1], height: +size[2] }, deviceScaleFactor: 1 });
    const file = path.join(__dirname, `.render-${name}.html`);
    fs.writeFileSync(file, html);
    await tab.goto(url(file));
    await tab.evaluate(() => document.fonts.ready);
    await tab.waitForTimeout(300);
    const transparent = /^header-|^divider/.test(name);
    await tab.screenshot({ path: path.join(out, `${name}.png`), omitBackground: transparent });
    fs.unlinkSync(file);
    await tab.close();
    console.log('rendered', name);
  }
  await browser.close();
})();
