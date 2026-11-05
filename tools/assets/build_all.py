"""Regenerates every texture, model, data file, sound and structure for each ported root.

    python3 tools/assets/build_all.py                    # every ported root
    python3 tools/assets/build_all.py --target 1.21.1    # one root
    python3 tools/assets/build_all.py --no-sound         # skip the (slower) audio synthesis
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)

from PIL import Image  # noqa: E402

import blocks  # noqa: E402
import entities  # noqa: E402
import items  # noqa: E402
import resources  # noqa: E402
import sounds  # noqa: E402
import structures  # noqa: E402
import targets  # noqa: E402
from pix import frames, hexc, mix, new, save  # noqa: E402

ROOT = os.path.abspath(os.path.join(HERE, '..', '..'))
PORTED = [t for t in targets.TARGETS if os.path.isdir(os.path.join(ROOT, t))]


def star_frames(star):
    """Star topper shimmer: a glint sweeps across the star."""
    out = []
    w, h = star.size
    for f in range(6):
        im = star.copy()
        px = im.load()
        for y in range(h):
            for x in range(w):
                if px[x, y][3] and (x + y) in (f * 4 + 2, f * 4 + 3):
                    px[x, y] = mix(px[x, y], hexc('#ffffff'), .55)
        out.append(im)
    return frames(out)


def icon(size=128):
    """Square mod icon: the iced cookie on a snowy night."""
    cookie = items.gingerbread_cookie()
    bg = new(32, 32)
    p = bg.load()
    for y in range(32):
        for x in range(32):
            t = y / 31
            p[x, y] = mix(hexc('#0f2238'), hexc('#2a4d74'), t)
    for (x, y) in ((3, 4), (27, 3), (22, 9), (6, 14), (29, 18), (12, 2), (17, 6), (2, 24), (26, 26)):
        p[x, y] = hexc('#ffffff')
    for x in range(32):
        for y in range(27, 32):
            p[x, y] = hexc('#e9f1f7') if y > 27 else hexc('#ffffff')
    big = cookie.resize((26, 26), Image.NEAREST)
    bg.alpha_composite(big, (3, 3))
    return bg.resize((size, size), Image.NEAREST)


def build(target, with_sound):
    common = os.path.join(ROOT, target, 'common', 'src', 'main', 'resources')
    loader = os.path.join(ROOT, target, targets.TARGETS[target]['loader_data'], 'src', 'main', 'resources')
    assets = os.path.join(common, 'assets', 'christmas_ginger_house')
    data = os.path.join(common, 'data', 'christmas_ginger_house')
    tex = os.path.join(assets, 'textures')
    modern = targets.TARGETS[target]['components']
    n = 0
    for name, fn in items.ITEMS.items():
        save(fn(), os.path.join(tex, 'item', f'{name}.png'))
        n += 1
    for name, fn in blocks.BLOCKS.items():
        save(fn(), os.path.join(tex, 'block', f'{name}.png'))
        n += 1
    save(frames(blocks.christmas_lights()), os.path.join(tex, 'block', 'christmas_lights.png'),
         {'animation': {'frametime': 12, 'interpolate': False}})
    save(items.wreath_item(), os.path.join(tex, 'block', 'wreath.png'))
    save(items.stocking_item(), os.path.join(tex, 'block', 'stocking.png'))
    save(star_frames(items.star_item()), os.path.join(tex, 'block', 'star_topper.png'),
         {'animation': {'frametime': 3, 'frames': [0, 1, 2, 3, 4, 5, {'index': 0, 'time': 40}]}})
    v26 = targets.v26(target)
    for name, fn in entities.ENTITIES.items():
        img = fn()
        if v26 and name.startswith('models/armor/'):
            # 26.x equipment assets read textures/entity/equipment/<layer type>/<texture>.png
            name = {'models/armor/santa_hat': 'entity/equipment/humanoid/santa_hat',
                    'models/armor/festive_layer_1': 'entity/equipment/humanoid/festive'}[name]
        elif modern and name == 'models/armor/santa_hat':
            name = 'models/armor/santa_hat_layer_1'   # 1.21 armor materials name their layer textures
        save(img, os.path.join(tex, f'{name}.png'))
        n += 1
    if v26:
        # 26.x spawn eggs are plain items: a grey shell and spots, tinted per mob by the item definition.
        save(items.spawn_egg(), os.path.join(tex, 'item', 'spawn_egg.png'))
        save(items.spawn_egg_overlay(), os.path.join(tex, 'item', 'spawn_egg_overlay.png'))
    save(icon(), os.path.join(assets, 'icon.png'))
    save(icon(), os.path.join(common, 'icon.png'))
    print(f'[{target}] textures:', n)
    print(f'[{target}] json files:', resources.generate(common, loader, target))
    structures.generate(data, os.path.join(HERE, 'source', 'house_ginger_original.nbt'),
                        'structure' if targets.TARGETS[target]['singular'] else 'structures')
    if with_sound:
        count, seconds = sounds.generate(os.path.join(assets, 'sounds'))
        print(f'[{target}] sounds: {count} (record {seconds:.1f}s)')


def main():
    with_sound = '--no-sound' not in sys.argv
    wanted = PORTED
    if '--target' in sys.argv:
        wanted = [sys.argv[sys.argv.index('--target') + 1]]
    for target in wanted:
        build(target, with_sound)


if __name__ == '__main__':
    main()
