"""Block textures (16x16)."""
import math
from pix import new, hexc, mix, ramp, value_noise, rng, sprite, frames, R
from PIL import Image


def _put(img, x, y, c):
    if 0 <= x < img.size[0] and 0 <= y < img.size[1]:
        img.putpixel((x, y), c)


def _noise_fill(img, rp, seed, lo=1, hi=4, scale=4.0, region=None):
    n = value_noise(16, 16, seed, scale=scale, octaves=2)
    r = rng(seed)
    for y in range(16):
        for x in range(16):
            if region and not region(x, y):
                continue
            v = n[y][x] + (r.random() - 0.5) * 0.18
            idx = lo + int(round(v * (hi - lo)))
            img.putpixel((x, y), rp[max(lo, min(hi, idx))])


# ----------------------------------------------------------------- gingerbread
def gingerbread_block():
    g = ramp('ginger')
    img = new()
    _noise_fill(img, g, 'gb', lo=2, hi=4, scale=5)
    r = rng('gb-spice')
    # spice specks
    for _ in range(14):
        x, y = r.randrange(1, 15), r.randrange(1, 15)
        img.putpixel((x, y), g[1])
    # tiny sugar sparkles
    for _ in range(3):
        x, y = r.randrange(1, 15), r.randrange(1, 15)
        img.putpixel((x, y), mix(g[5], hexc('#fff3dc'), .3))
    # baked crust rim
    for i in range(16):
        for (x, y) in ((i, 0), (0, i)):
            img.putpixel((x, y), g[4] if (i % 5) else g[5])
        for (x, y) in ((i, 15), (15, i)):
            img.putpixel((x, y), g[2] if (i % 4) else g[1])
    return img


def _bricks(seed, mortar_ramp='icing', brick_ramp='ginger'):
    g, m = ramp(brick_ramp), ramp(mortar_ramp)
    img = new()
    n = value_noise(16, 16, seed, scale=4)
    r = rng(seed)
    # 4 rows of bricks, each 4px tall (3 brick + 1 mortar), half-offset per row
    for y in range(16):
        row, iy = y // 4, y % 4
        offset = 0 if row % 2 == 0 else 4
        for x in range(16):
            ix = (x + offset) % 8
            if iy == 3 or ix == 7:
                # mortar: icing, brighter on top edges
                img.putpixel((x, y), m[4] if iy == 3 else m[3])
                continue
            v = n[y][x] + (r.random() - .5) * .2
            level = 3 if v > .45 else 2
            if iy == 0 or ix == 0:
                level = 4
            if iy == 2 or ix == 6:
                level = 2 if level > 2 else 1
            img.putpixel((x, y), g[level])
    # spice specks
    for _ in range(9):
        x, y = r.randrange(16), r.randrange(16)
        if img.getpixel((x, y)) not in (m[4], m[3]):
            img.putpixel((x, y), g[1])
    # mortar shadow under each icing line for depth
    for y in range(16):
        if y % 4 == 0 and y > 0:
            for x in range(16):
                px = img.getpixel((x, y))
                if px not in (m[3], m[4]):
                    img.putpixel((x, y), mix(px, g[1], .35))
    return img


def gingerbread_bricks():
    return _bricks('gbb')


def icing_block():
    ic = ramp('icing')
    img = new()
    n = value_noise(16, 16, 'icing', scale=6, octaves=2)
    for y in range(16):
        for x in range(16):
            v = n[y][x]
            # soft swirl bands
            s = math.sin((x * 0.55 + y * 0.35) + v * 5.0)
            lvl = 5 if s > .6 else (4 if s > -.45 else 3)
            if v > .78:
                lvl = 5
            img.putpixel((x, y), ic[lvl])
    r = rng('icing-sp')
    for _ in range(6):
        img.putpixel((r.randrange(16), r.randrange(16)), ic[5])
    return img


def frosted_bricks_side():
    img = _bricks('gbf')
    ic = ramp('icing')
    # icing cap with drips
    drips = [5, 3, 4, 6, 3, 3, 4, 7, 5, 3, 4, 4, 6, 3, 3, 5]
    for x in range(16):
        d = drips[x]
        for y in range(d):
            lvl = 4 if y < d - 1 else 2
            if y == 0:
                lvl = 5
            img.putpixel((x, y), ic[lvl])
        # glossy highlight on long drips
        if d >= 6:
            img.putpixel((x, d - 3), ic[5])
    # drip shadow onto bricks
    for x in range(16):
        y = drips[x]
        if y < 16:
            img.putpixel((x, y), mix(img.getpixel((x, y)), hexc('#3d1d0b'), .35))
    return img


# ----------------------------------------------------------------- candy
def candy_cane_side():
    red, wh = ramp('red'), ramp('white')
    img = new()
    for y in range(16):
        for x in range(16):
            band = ((x + y) // 4) % 2
            k = (x + y) % 4
            if band == 0:
                c = red[4] if k == 0 else (red[2] if k == 3 else red[3])
            else:
                c = wh[5] if k == 0 else (wh[2] if k == 3 else wh[4])
            img.putpixel((x, y), c)
    # vertical gloss + shade (it is a cylinder-ish candy)
    for y in range(16):
        img.putpixel((3, y), mix(img.getpixel((3, y)), hexc('#ffffff'), .35))
        img.putpixel((4, y), mix(img.getpixel((4, y)), hexc('#ffffff'), .2))
        img.putpixel((14, y), mix(img.getpixel((14, y)), hexc('#000000'), .18))
        img.putpixel((15, y), mix(img.getpixel((15, y)), hexc('#000000'), .28))
    return img


def _pinwheel(rim=True, wedges=8, twist=0.9):
    red, wh = ramp('red'), ramp('white')
    img = new()
    for y in range(16):
        for x in range(16):
            dx, dy = x - 7.5, y - 7.5
            d = math.hypot(dx, dy)
            a = math.atan2(dy, dx) + d * twist / 7.5
            seg = int(((a + math.pi) / (2 * math.pi)) * wedges) % wedges
            c = red[3] if seg % 2 == 0 else wh[4]
            if d > 6.2 and rim:
                c = wh[3] if d < 7.4 else wh[2]
            if d < 1.2:
                c = wh[5]
            # dome lighting
            if dx + dy < -6 and d < 6.2:
                c = mix(c, hexc('#ffffff'), .25)
            if dx + dy > 7 and d < 6.2:
                c = mix(c, hexc('#000000'), .18)
            img.putpixel((x, y), c)
    return img


def candy_cane_top():
    return _pinwheel(rim=True, wedges=6, twist=1.6)


def peppermint_block():
    return _pinwheel(rim=True, wedges=8, twist=0.0)


# ----------------------------------------------------------------- presents
PRESENTS = {
    'red': ('red', 'gold'),
    'green': ('green', 'red'),
    'blue': ('blue', 'silver'),
    'gold': ('gold', 'red'),
}


def _paper(color, seed):
    p = ramp(color)
    img = new()
    r = rng(seed)
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), p[3])
    # pattern: tiny 5-point snowflakes/stars in a diagonal grid
    for cy in range(2, 16, 6):
        for cx in range((cy // 6) % 2 * 3 + 1, 16, 6):
            for dx, dy in ((0, 0), (1, 0), (-1, 0), (0, 1), (0, -1)):
                c = p[5] if (dx, dy) == (0, 0) else p[4]
                _put(img, cx + dx, cy + dy, c)
    return img


def present_side(color):
    paper, rib = PRESENTS[color]
    img = _paper(paper, 'p' + color)
    rb = ramp(rib)
    for y in range(16):
        img.putpixel((7, y), rb[4])
        img.putpixel((8, y), rb[3])
        img.putpixel((6, y), rb[2])
        img.putpixel((9, y), rb[2])
    # bottom shade and top lip
    p = ramp(paper)
    for x in range(16):
        img.putpixel((x, 15), mix(img.getpixel((x, 15)), p[1], .6))
        img.putpixel((x, 0), mix(img.getpixel((x, 0)), p[5], .4))
    return img


def present_top(color):
    paper, rib = PRESENTS[color]
    img = _paper(paper, 'pt' + color)
    rb = ramp(rib)
    for i in range(16):
        for (x, y) in ((7, i), (i, 7)):
            img.putpixel((x, y), rb[4])
        for (x, y) in ((8, i), (i, 8)):
            img.putpixel((x, y), rb[3])
        for (x, y) in ((6, i), (i, 6), (9, i), (i, 9)):
            img.putpixel((x, y), rb[2])
    return img


def present_bow(color):
    _, rib = PRESENTS[color]
    return sprite([
        '................',
        '.bbb........bbb.',
        'bBBbb......bbBBb',
        'bBbbbb....bbbbBb',
        'bbbbbbbkkbbbbbbb',
        '.bbbbbbkkbbbbbb.',
        '..bbbb.kk.bbbb..',
        '.......kk.......',
        '......b..b......',
        '.....bb..bb.....',
        '.....b....b.....',
        '................',
        '................',
        '................',
        '................',
        '................',
    ], {'b': rib, 'k': rib}, outline=False)


# ----------------------------------------------------------------- lights
BULBS = ['red', 'gold', 'green', 'blue', 'pink']


def christmas_lights():
    """Animated: 4 frames, the five bulbs twinkle in a slow chase."""
    wire, wire_hi, socket = hexc('#173322'), hexc('#2f5a3c'), hexc('#3a463d')
    path = [(x, 3 + int(round(3.0 * math.sin(math.pi * (x + 0.5) / 16.0)))) for x in range(16)]
    bulbs = [(1, 'red'), (4, 'gold'), (7, 'green'), (10, 'blue'), (13, 'pink')]
    out = []
    for f in range(4):
        img = new()
        for x, y in path:
            img.putpixel((x, y), wire)
            if x % 4 == 1:
                _put(img, x, y - 1, wire_hi)
        for i, (bx, name) in enumerate(bulbs):
            col = ramp(name)
            lit = (i + f) % 4 != 0
            by = max(path[bx][1], path[bx + 1][1]) + 1
            _put(img, bx, by, socket); _put(img, bx + 1, by, socket)
            body = [(0, 1), (1, 1), (0, 2), (1, 2), (0, 3), (1, 3), (0, 4)] if i % 2 == 0 else \
                   [(0, 1), (1, 1), (0, 2), (1, 2), (0, 3), (1, 3), (1, 4)]
            for dx, dy in body:
                c = col[4] if lit else col[2]
                if (dx, dy) == (0, 1):
                    c = col[5] if lit else col[3]
                if dy == 4 or (dx == 1 and dy == 3):
                    c = col[3] if lit else col[1]
                _put(img, bx + dx, by + dy, c)
            if lit:
                for hx, hy in ((bx - 1, by + 2), (bx + 2, by + 2), (bx - 1, by + 3), (bx + 2, by + 3), (bx, by + 5), (bx + 1, by + 5)):
                    if 0 <= hx < 16 and 0 <= hy < 16 and img.getpixel((hx, hy))[3] == 0:
                        _put(img, hx, hy, (*col[4][:3], 70))
        out.append(img)
    return out


# ----------------------------------------------------------------- ornaments (block model faces)
def ornament_face(color):
    c = ramp(color)
    img = new()
    for y in range(16):
        for x in range(16):
            dx, dy = x - 7.5, y - 7.5
            d = math.hypot(dx + 2, dy + 2)
            lvl = 4 if d < 4 else (3 if d < 9 else 2)
            if x + y > 24:
                lvl = 1
            img.putpixel((x, y), c[lvl])
    for (x, y) in ((4, 4), (5, 4), (4, 5)):
        img.putpixel((x, y), hexc('#ffffff'))
    # glitter band
    for x in range(16):
        img.putpixel((x, 9), mix(c[5], hexc('#ffffff'), .3) if x % 2 == 0 else c[4])
    return img


def ornament_cap():
    g = ramp('gold')
    img = new()
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), g[4] if (x + y) % 5 else g[5])
    for i in range(16):
        img.putpixel((i, 15), g[2])
        img.putpixel((15, i), g[2])
    return img


# ----------------------------------------------------------------- advent calendar
def advent_front():
    wd, gd = ramp('wood'), ramp('gold')
    img = new()
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), wd[3] if (x + y * 3) % 7 else wd[2])
    for i in range(16):
        img.putpixel((i, 0), wd[4]); img.putpixel((0, i), wd[4])
        img.putpixel((i, 15), wd[1]); img.putpixel((15, i), wd[1])
    # header plaque with a star
    for x in range(3, 13):
        for y in range(1, 4):
            img.putpixel((x, y), ramp('red')[3] if y != 1 else ramp('red')[4])
    for (x, y) in ((7, 1), (8, 1), (6, 2), (7, 2), (8, 2), (9, 2), (7, 3), (8, 3)):
        img.putpixel((x, y), gd[4] if (x, y) not in ((7, 1), (8, 1)) else gd[5])
    # 24 doors: 6 columns x 4 rows of 2x2 doors with 0px gap, frame lines between
    door_cols = ['red', 'green', 'gold', 'cream', 'green', 'red']
    for row in range(4):
        for col in range(6):
            x0, y0 = 2 + col * 2, 5 + row * 2 + (row // 2)
            cr = ramp(door_cols[(col + row) % 6])
            img.putpixel((x0, y0), cr[4])
            img.putpixel((x0 + 1, y0), cr[3])
            img.putpixel((x0, y0 + 1), cr[3])
            img.putpixel((x0 + 1, y0 + 1), cr[2])
    # brass knob row marker
    for y in (14,):
        for x in range(2, 14):
            img.putpixel((x, y), wd[2])
    img.putpixel((7, 14), gd[4]); img.putpixel((8, 14), gd[3])
    return img


def advent_side():
    wd = ramp('wood')
    img = new()
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), wd[3] if (y + x // 5) % 4 else wd[2])
    for i in range(16):
        img.putpixel((i, 0), wd[4]); img.putpixel((0, i), wd[4])
        img.putpixel((i, 15), wd[1]); img.putpixel((15, i), wd[1])
    # holly sprig
    hol = ramp('green')
    for (x, y) in ((5, 6), (6, 5), (6, 6), (7, 6), (9, 6), (9, 5), (10, 6), (8, 7)):
        img.putpixel((x, y), hol[3])
    for (x, y) in ((7, 8), (8, 8), (8, 9)):
        img.putpixel((x, y), ramp('red')[4])
    return img


def advent_top():
    wd = ramp('wood')
    img = new()
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), wd[3] if (x + y // 4) % 5 else wd[2])
    for i in range(16):
        img.putpixel((i, 0), wd[4]); img.putpixel((0, i), wd[4])
        img.putpixel((i, 15), wd[1]); img.putpixel((15, i), wd[1])
    return img


# ----------------------------------------------------------------- plants
def ginger_stage(stage):
    leaf = {'l': 'green', 'r': 'root'}
    rows = {
        0: [
            '................', '................', '................', '................',
            '................', '................', '................', '................',
            '................', '................', '.......l........', '......ll.l......',
            '.......Ll.......', '.....l.l.l......', '......lll.......', '.......l........'],
        1: [
            '................', '................', '................', '................',
            '................', '................', '........l.......', '...l....l...l...',
            '....l..lL..l....', '....ll.l..ll....', '.....lLl.lL.....', '..l...lllL...l..',
            '...ll..ll..ll...', '....lLl.l.Ll....', '......lll.......', '.......l........'],
        2: [
            '................', '................', '.......l........', '..l....l....l...',
            '...l..lL...l....', '...ll.l...ll....', '....lLl..lL.....', '.l...lll.l...l..',
            '..l...lL.l..l...', '..ll..ll.lLll...', '...lLl.l.l.l....', '....llll.ll.....',
            '..l...lllL...l..', '...ll..ll..ll...', '....lLlll.Ll....', '......llll......'],
        3: [
            '.......l........', '..l....l....l...', '...l..lL...l....', '...ll.l...ll....',
            '....lLl..lL.....', '.l...lll.l...l..', '..l...lL.l..l...', '..ll..ll.lLll...',
            '...lLl.l.l.l....', '....llll.ll.....', '..l...lllL...l..', '...ll..ll..ll...',
            '....lLlll.Ll....', '...rrRlllrr.....', '..rRrrrrrrrr....', '...rr.rrr.rr....'],
    }[stage]
    return sprite(rows, leaf, outline=False)


def wild_ginger():
    return sprite([
        '................',
        '.......pp.......',
        '......pPpp......',
        '......pppp......',
        '.......pp.......',
        '..l....l....l...',
        '...l..lL...l....',
        '...ll.l...ll....',
        '....lLl..lL..pp.',
        '.l...lll.l..pPp.',
        '..l...lL.l...pp.',
        '..ll..ll.lLll.l.',
        '...lLl.l.l.l.l..',
        '....llll.ll.l...',
        '..l...lllL......',
        '...ll..lll......',
    ], {'l': 'green', 'p': 'red'}, outline=False)


BLOCKS = {
    'gingerbread_block': gingerbread_block,
    'gingerbread_bricks': gingerbread_bricks,
    'frosted_gingerbread_bricks': frosted_bricks_side,
    'icing_block': icing_block,
    'candy_cane_block': candy_cane_side,
    'candy_cane_block_top': candy_cane_top,
    'peppermint_block': peppermint_block,
    'ornament_cap': ornament_cap,
    'advent_calendar_front': advent_front,
    'advent_calendar_side': advent_side,
    'advent_calendar_top': advent_top,
    'wild_ginger': wild_ginger,
}
for _c in PRESENTS:
    BLOCKS[f'{_c}_present_side'] = (lambda c=_c: present_side(c))
    BLOCKS[f'{_c}_present_top'] = (lambda c=_c: present_top(c))
    BLOCKS[f'{_c}_present_bow'] = (lambda c=_c: present_bow(c))
for _o in ('red', 'gold', 'blue'):
    BLOCKS[f'{_o}_ornament'] = (lambda c=_o: ornament_face(c))
for _s in range(4):
    BLOCKS[f'ginger_crop_stage{_s}'] = (lambda s=_s: ginger_stage(s))


# ----------------------------------------------------------------- door & trapdoor
def _panel(seed):
    img = new()
    g = ramp('ginger')
    n = value_noise(16, 16, seed, scale=5)
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), g[3] if n[y][x] > .45 else g[2])
    return img


def gingerbread_door_top():
    img = _panel('door-top')
    ic, g = ramp('icing'), ramp('ginger')
    glass = [hexc('#7fb6e6'), hexc('#a9d3f5'), hexc('#d6ecff')]
    for i in range(16):
        img.putpixel((1, i), ic[4]); img.putpixel((14, i), ic[3])
    for x in range(1, 15):
        img.putpixel((x, 1), ic[5])
    for x in range(2, 14, 2):           # scalloped top edge
        img.putpixel((x, 2), ic[4])
    for y in range(4, 13):               # round candy-glass window with an icing cross
        for x in range(3, 13):
            d = math.hypot(x - 7.5, y - 8.0)
            if d < 4.2:
                c = glass[1]
                if x - 7.5 + y - 8 < -3:
                    c = glass[2]
                if x - 7.5 + y - 8 > 3:
                    c = glass[0]
                if x in (7, 8) or y == 8:
                    c = ic[4]
                img.putpixel((x, y), c)
            elif d < 5.1:
                img.putpixel((x, y), ic[3])
    for x in range(2, 14):
        img.putpixel((x, 15), g[1])
    return img


def gingerbread_door_bottom():
    img = _panel('door-bottom')
    ic, g, rd = ramp('icing'), ramp('ginger'), ramp('red')
    for i in range(16):
        img.putpixel((1, i), ic[4]); img.putpixel((14, i), ic[3])
    for x in range(1, 15):
        img.putpixel((x, 14), ic[4])
    for x in range(2, 14):
        img.putpixel((x, 0), g[1])
    # icing heart
    heart = ['.##.##.', '#######', '#######', '.#####.', '..###..', '...#...']
    for yy, row in enumerate(heart):
        for xx, ch in enumerate(row):
            if ch == '#':
                img.putpixel((4 + xx, 5 + yy), ic[5] if (xx + yy) % 3 else ic[3])
    # gumdrop knob
    for (x, y, c) in ((11, 1, rd[4]), (12, 1, rd[3]), (11, 2, rd[3]), (12, 2, rd[2])):
        img.putpixel((x, y), c)
    return img


def gingerbread_trapdoor():
    img = _panel('trapdoor')
    ic = ramp('icing')
    for i in range(16):
        for (x, y) in ((i, 0), (0, i)):
            img.putpixel((x, y), ic[5])
        for (x, y) in ((i, 15), (15, i)):
            img.putpixel((x, y), ic[3])
    for i in range(2, 14):
        img.putpixel((i, i), ic[4])
        img.putpixel((15 - i, i), ic[4])
    for (x, y) in ((7, 3), (8, 3), (3, 7), (3, 8), (12, 7), (12, 8), (7, 12), (8, 12)):
        img.putpixel((x, y), ramp('red')[4])   # gumdrops between the lattice
    return img


BLOCKS['gingerbread_door_top'] = gingerbread_door_top
BLOCKS['gingerbread_door_bottom'] = gingerbread_door_bottom
BLOCKS['gingerbread_trapdoor'] = gingerbread_trapdoor
