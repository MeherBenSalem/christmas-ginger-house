"""Regenerates every texture, model, data file, sound and structure for the 1.20.1 root.

    python3 tools/assets/build_all.py            # everything
    python3 tools/assets/build_all.py --no-sound # skip the (slower) audio synthesis
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
from pix import frames, hexc, mix, new, save  # noqa: E402

ROOT = os.path.abspath(os.path.join(HERE, '..', '..'))
COMMON = os.path.join(ROOT, '1.20.1', 'common', 'src', 'main', 'resources')
FORGE = os.path.join(ROOT, '1.20.1', 'forge', 'src', 'main', 'resources')
ASSETS = os.path.join(COMMON, 'assets', 'christmas_ginger_house')
DATA = os.path.join(COMMON, 'data', 'christmas_ginger_house')
TEX = os.path.join(ASSETS, 'textures')


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


def main():
    with_sound = '--no-sound' not in sys.argv
    n = 0
    for name, fn in items.ITEMS.items():
        save(fn(), os.path.join(TEX, 'item', f'{name}.png'))
        n += 1
    for name, fn in blocks.BLOCKS.items():
        save(fn(), os.path.join(TEX, 'block', f'{name}.png'))
        n += 1
    save(frames(blocks.christmas_lights()), os.path.join(TEX, 'block', 'christmas_lights.png'),
         {'animation': {'frametime': 12, 'interpolate': False}})
    save(items.wreath_item(), os.path.join(TEX, 'block', 'wreath.png'))
    save(items.stocking_item(), os.path.join(TEX, 'block', 'stocking.png'))
    save(star_frames(items.star_item()), os.path.join(TEX, 'block', 'star_topper.png'),
         {'animation': {'frametime': 3, 'frames': [0, 1, 2, 3, 4, 5, {'index': 0, 'time': 40}]}})
    for name, fn in entities.ENTITIES.items():
        save(fn(), os.path.join(TEX, f'{name}.png'))
        n += 4
    save(icon(), os.path.join(ASSETS, 'icon.png'))
    save(icon(), os.path.join(COMMON, 'icon.png'))
    print('textures:', n)
    print('json files:', resources.generate(COMMON, FORGE))
    structures.generate(DATA, os.path.join(HERE, 'source', 'house_ginger_original.nbt'))
    print('structures: 3')
    if with_sound:
        count, seconds = sounds.generate(os.path.join(ASSETS, 'sounds'))
        print(f'sounds: {count} (record {seconds:.1f}s)')


if __name__ == '__main__':
    main()
