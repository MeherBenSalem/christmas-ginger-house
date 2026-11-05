"""Tiny pixel-art toolkit used to build every texture in the mod.

Sprites are authored as ASCII masks. Each letter is a *region* bound to a colour
ramp (darkest -> lightest). Regions are shaded automatically from a top-left
light source and the union of all regions gets a selective outline that takes
the darkest colour of the region it touches, which is how vanilla items read.
Single-colour accents use characters bound to a plain colour instead of a ramp.
"""
from __future__ import annotations

import json
import math
import os
import random
from typing import Dict, Iterable, List, Sequence, Tuple, Union

from PIL import Image

RGBA = Tuple[int, int, int, int]
Ramp = Sequence[str]


def hexc(value: str, alpha: int = 255) -> RGBA:
    value = value.lstrip('#')
    return (int(value[0:2], 16), int(value[2:4], 16), int(value[4:6], 16), alpha)


def mix(a: RGBA, b: RGBA, t: float) -> RGBA:
    return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(4))  # type: ignore


# --------------------------------------------------------------------------- ramps
R = {
    'ginger': ['#3d1d0b', '#6b3415', '#94501f', '#b5682c', '#cf8541', '#e6a861'],
    'dough': ['#5e3a1c', '#8f5c2e', '#b47d45', '#cf9a5d', '#e2b67c', '#f0d1a0'],
    'root': ['#4d3219', '#86602f', '#b58a50', '#d4ab6c', '#ead08f', '#f7e6b8'],
    'icing': ['#5d6f80', '#9fb2c3', '#c9d7e3', '#e4edf4', '#f7fbff', '#ffffff'],
    'red': ['#3f0710', '#73101f', '#a8172d', '#cf2337', '#ec4453', '#ff8a8a'],
    'white': ['#6d5f5a', '#b6aaa3', '#ddd3cb', '#efe8e2', '#fbf8f4', '#ffffff'],
    'green': ['#0b2a17', '#134524', '#1d6634', '#2a8a45', '#45ad5c', '#7fd486'],
    'spruce': ['#0e1f14', '#183424', '#22492f', '#2f613d', '#3f7a4b', '#5e9a62'],
    'gold': ['#4a2c05', '#87560d', '#c08518', '#e8b22a', '#ffd84a', '#fff4b0'],
    'silver': ['#2e3640', '#5d6876', '#8b97a5', '#b6c0cb', '#dde4ea', '#ffffff'],
    'blue': ['#0b1640', '#152a73', '#1f44a8', '#2e63cf', '#4c8af0', '#9cc6ff'],
    'brown': ['#24140a', '#3f2412', '#5e381c', '#7d4e28', '#9c6837', '#bd8a52'],
    'wood': ['#2a1a0e', '#4a2f19', '#6b4526', '#8a5c34', '#a87645', '#c99a62'],
    'cocoa': ['#1e0e06', '#3b1d0c', '#5a2e14', '#7a421f', '#98592c', '#b9773f'],
    'mug': ['#3b0a10', '#6e1420', '#a01f30', '#c83545', '#e65a62', '#ff9a9a'],
    'coal': ['#08080a', '#16161a', '#24242a', '#34343c', '#4a4a54', '#6a6a76'],
    'leather': ['#2b160b', '#4c2814', '#6f3d1f', '#8f532c', '#ad6d3c', '#c98b55'],
    'fur': ['#140c08', '#24160e', '#382416', '#4d3320', '#66452c', '#86603e'],
    'horn': ['#2a2116', '#4a3b28', '#6d5a3d', '#958058', '#bba77a', '#ddd0a4'],
    'skin': ['#3a1418', '#5e2228', '#86343a', '#a8484c', '#c86464', '#e48b86'],
    'pink': ['#4a1030', '#7d1c50', '#b52d72', '#df4b92', '#f57db3', '#ffb8d8'],
    'purple': ['#1f0b33', '#3a1661', '#57238f', '#7637b8', '#9a5ad8', '#c79af0'],
    'black': ['#050506', '#0d0d10', '#17171c', '#222228', '#2e2e36', '#3e3e48'],
    'snow': ['#7f93a8', '#b4c4d4', '#d7e2ec', '#ebf1f7', '#f8fbfd', '#ffffff'],
    'deer': ['#2a170c', '#46281a', '#664028', '#87593a', '#a7754f', '#c99a72'],
    'cream': ['#6f5a43', '#a68d6e', '#cdb594', '#e3cfb0', '#f2e3ca', '#fff6e6'],
}


def ramp(name_or_list: Union[str, Ramp]) -> List[RGBA]:
    values = R[name_or_list] if isinstance(name_or_list, str) else name_or_list
    return [hexc(v) if isinstance(v, str) else v for v in values]


# --------------------------------------------------------------------------- sprites
def parse(rows: Sequence[str]) -> List[str]:
    rows = [r.rstrip('\n') for r in rows]
    width = max(len(r) for r in rows)
    return [r.ljust(width, '.') for r in rows]


def sprite(rows: Sequence[str], regions: Dict[str, Union[str, Ramp]], flats: Dict[str, str] | None = None,
           base: int = 3, outline: bool = True, outline_level: int = 0, shade: bool = True,
           soft: bool = False) -> Image.Image:
    """Render an ASCII sprite.

    regions: char -> ramp. Pixels are coloured ramp[base], raised by one where the
    pixel's up/left neighbour is outside the region and lowered where the
    down/right neighbour is outside. Upper-case chars of a region are forced one
    step lighter, so highlights can be painted by hand.
    flats: char -> exact colour (no shading).
    """
    rows = parse(rows)
    h, w = len(rows), len(rows[0])
    flats = flats or {}
    ramps = {k: ramp(v) for k, v in regions.items()}
    img = Image.new('RGBA', (w, h), (0, 0, 0, 0))
    px = img.load()

    def region_of(ch: str) -> str | None:
        if ch in ramps:
            return ch
        if ch.lower() in ramps and ch.isupper():
            return ch.lower()
        return None

    def at(x: int, y: int) -> str:
        if 0 <= x < w and 0 <= y < h:
            return rows[y][x]
        return '.'

    filled = [[rows[y][x] != '.' for x in range(w)] for y in range(h)]
    for y in range(h):
        for x in range(w):
            ch = rows[y][x]
            if ch == '.':
                continue
            if ch in flats:
                px[x, y] = hexc(flats[ch]) if isinstance(flats[ch], str) else flats[ch]
                continue
            reg = region_of(ch)
            if reg is None:
                raise ValueError(f'unknown sprite char {ch!r}')
            rp = ramps[reg]
            level = base
            if shade:
                same = lambda c: region_of(c) == reg
                up, left = at(x, y - 1), at(x - 1, y)
                down, right = at(x, y + 1), at(x + 1, y)
                if not same(up) or not same(left):
                    level += 1
                if not same(down) or not same(right):
                    level -= 1
                if soft and (not same(up) and not same(left)):
                    level += 1
            if ch.isupper() and ch.lower() in ramps:
                level += 1
            level = max(0, min(len(rp) - 1, level))
            px[x, y] = rp[level]
    if outline:
        add_outline(img, rows, ramps, flats, outline_level)
    return img


def add_outline(img: Image.Image, rows: List[str], ramps, flats, level: int = 0) -> None:
    """Draw a 1px outline on transparent pixels adjacent (4-neighbour) to the sprite.

    Only used when the sprite's ASCII leaves a margin; colour is the darkest step of
    the touching region so the outline reads as part of the material."""
    w, h = img.size
    px = img.load()
    src = img.copy().load()
    for y in range(h):
        for x in range(w):
            if src[x, y][3] != 0:
                continue
            touching = []
            for dx, dy in ((0, -1), (-1, 0), (1, 0), (0, 1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < w and 0 <= ny < h and src[nx, ny][3] != 0:
                    ch = rows[ny][nx]
                    key = ch.lower() if ch.lower() in ramps else ch
                    if key in ramps:
                        touching.append(ramps[key][level])
                    elif key in flats:
                        c = hexc(flats[key]) if isinstance(flats[key], str) else flats[key]
                        touching.append(mix(c, (0, 0, 0, 255), 0.55))
            if touching:
                px[x, y] = touching[0]


def outline_existing(img: Image.Image, color: RGBA | None = None, factor: float = 0.45) -> Image.Image:
    """Outline an already rendered sprite (4-neighbour) using a darkened copy of the neighbour."""
    w, h = img.size
    out = img.copy()
    src = img.load()
    px = out.load()
    for y in range(h):
        for x in range(w):
            if src[x, y][3] != 0:
                continue
            for dx, dy in ((0, -1), (-1, 0), (1, 0), (0, 1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < w and 0 <= ny < h and src[nx, ny][3] != 0:
                    px[x, y] = color or mix(src[nx, ny], (8, 4, 2, 255), 1 - factor)
                    break
    return out


# --------------------------------------------------------------------------- noise helpers
def rng(seed: Union[int, str]) -> random.Random:
    return random.Random(str(seed))


def value_noise(w: int, h: int, seed, scale: float = 4.0, octaves: int = 2) -> List[List[float]]:
    """Tileable value noise in [0,1]."""
    r = rng(seed)
    total = [[0.0] * w for _ in range(h)]
    amp, norm = 1.0, 0.0
    for o in range(octaves):
        cells = max(1, int(round(w / scale)) * (2 ** o))
        grid = [[r.random() for _ in range(cells)] for _ in range(cells)]
        for y in range(h):
            for x in range(w):
                gx, gy = x / w * cells, y / h * cells
                x0, y0 = int(gx) % cells, int(gy) % cells
                x1, y1 = (x0 + 1) % cells, (y0 + 1) % cells
                fx, fy = gx - int(gx), gy - int(gy)
                fx, fy = fx * fx * (3 - 2 * fx), fy * fy * (3 - 2 * fy)
                a = grid[y0][x0] + (grid[y0][x1] - grid[y0][x0]) * fx
                b = grid[y1][x0] + (grid[y1][x1] - grid[y1][x0]) * fx
                total[y][x] += (a + (b - a) * fy) * amp
        norm += amp
        amp *= 0.5
    return [[v / norm for v in row] for row in total]


def new(w: int = 16, h: int = 16) -> Image.Image:
    return Image.new('RGBA', (w, h), (0, 0, 0, 0))


def paste(dst: Image.Image, src: Image.Image, x: int = 0, y: int = 0) -> Image.Image:
    dst.alpha_composite(src, (x, y))
    return dst


def save(img: Image.Image, path: str, mcmeta: dict | None = None) -> None:
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path, optimize=True)
    if mcmeta is not None:
        with open(path + '.mcmeta', 'w', encoding='utf-8') as fh:
            json.dump(mcmeta, fh, indent=2)
            fh.write('\n')


def frames(stack: Iterable[Image.Image]) -> Image.Image:
    stack = list(stack)
    w, h = stack[0].size
    out = new(w, h * len(stack))
    for i, f in enumerate(stack):
        out.alpha_composite(f, (0, i * h))
    return out


def preview(images: Dict[str, Image.Image], path: str, scale: int = 8, cols: int = 8, bg=(54, 57, 63, 255)) -> None:
    """Contact sheet for eyeballing the art."""
    from PIL import ImageDraw
    items = list(images.items())
    cell = 16 * scale + 20
    rows_n = math.ceil(len(items) / cols)
    sheet = Image.new('RGBA', (cols * cell, rows_n * (cell + 14)), bg)
    d = ImageDraw.Draw(sheet)
    for i, (name, im) in enumerate(items):
        cx, cy = (i % cols) * cell, (i // cols) * (cell + 14)
        im2 = im
        if im.size[1] > im.size[0] and im.size[1] % im.size[0] == 0:
            im2 = im.crop((0, 0, im.size[0], im.size[0]))
        s = scale * 16 // max(im2.size)
        big = im2.resize((im2.size[0] * s, im2.size[1] * s), Image.NEAREST)
        # checker
        for yy in range(0, big.size[1], 8):
            for xx in range(0, big.size[0], 8):
                if (xx // 8 + yy // 8) % 2 == 0:
                    d.rectangle([cx + 10 + xx, cy + 10 + yy, cx + 10 + xx + 7, cy + 10 + yy + 7], fill=(64, 68, 75, 255))
        sheet.alpha_composite(big, (cx + 10, cy + 10))
        d.text((cx + 10, cy + cell - 6), name[:22], fill=(230, 230, 230, 255))
    sheet.save(path)
