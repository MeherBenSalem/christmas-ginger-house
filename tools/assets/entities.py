"""Entity and armor textures, painted face by face onto the model UV layouts.

The box() helper mirrors Minecraft's cube unwrapping: for a cube at texOffs(u, v) with
size (w, h, d) the faces are laid out as

    top    [u+d, v]        w x d   (high v edge touches the front)
    bottom [u+d+w, v]      w x d
    right  [u, v+d]        d x h   (high u edge touches the front)
    front  [u+d, v+d]      w x h   (low u = the entity's right side)
    left   [u+d+w, v+d]    d x h   (low u edge touches the front)
    back   [u+2d+w, v+d]   w x h
"""
from pix import new, hexc, mix, ramp, rng, value_noise

FACES = ('top', 'bottom', 'right', 'front', 'left', 'back')


def face_rects(u, v, w, h, d):
    return {
        'top': (u + d, v, w, d),
        'bottom': (u + d + w, v, w, d),
        'right': (u, v + d, d, h),
        'front': (u + d, v + d, w, h),
        'left': (u + d + w, v + d, d, h),
        'back': (u + 2 * d + w, v + d, w, h),
    }


def box(img, u, v, w, h, d, paint):
    """paint(face, x, y, fw, fh) -> RGBA or None, with x/y local to the face."""
    for face, (fx, fy, fw, fh) in face_rects(u, v, w, h, d).items():
        for y in range(fh):
            for x in range(fw):
                c = paint(face, x, y, fw, fh)
                if c is not None:
                    img.putpixel((fx + x, fy + y), c)


def material(name, seed, lo=2, hi=4, scale=3.0):
    """Returns f(x, y, salt) giving a noisy ramp colour; deterministic per salt."""
    rp = ramp(name)
    noise = value_noise(64, 64, seed, scale=scale, octaves=2)
    r = rng(seed)
    jitter = [[r.random() for _ in range(64)] for _ in range(64)]

    def f(x, y, salt=0, bias=0.0, lo_=lo, hi_=hi):
        xx, yy = (x + salt * 7) % 64, (y + salt * 13) % 64
        val = noise[yy][xx] * 0.75 + jitter[yy][xx] * 0.25 + bias
        idx = lo_ + int(round(val * (hi_ - lo_)))
        return rp[max(0, min(len(rp) - 1, idx))]
    return f


# =========================================================================== gingerbread man
def gingerbread_man():
    img = new(32, 32)
    dough = material('ginger', 'gbm', 2, 4, scale=3)
    g = ramp('ginger')
    icing, red, green = hexc('#fbfdff'), hexc('#d8253b'), hexc('#2a9a4a')

    def base(salt):
        def p(face, x, y, fw, fh):
            if face in ('right', 'left', 'top', 'bottom'):
                return g[2] if (x + y) % 3 else g[1]   # baked crust edge
            c = dough(x, y, salt)
            if x == 0 or y == 0:
                c = mix(c, g[5], .35)
            if x == fw - 1 or y == fh - 1:
                c = mix(c, g[1], .35)
            return c
        return p

    box(img, 0, 0, 6, 6, 2, base(1))      # head
    box(img, 0, 8, 6, 5, 2, base(2))      # body
    box(img, 16, 0, 4, 3, 2, base(3))     # right arm
    box(img, 16, 5, 4, 3, 2, base(4))     # left arm
    box(img, 0, 15, 3, 5, 2, base(5))     # right leg
    box(img, 10, 15, 3, 5, 2, base(6))    # left leg

    put = img.putpixel
    # face: icing eyes, rosy cheeks and a piped smile (head front at 2..8, 2..8)
    for (x, y) in ((1, 2), (4, 2)):
        put((2 + x, 2 + y), icing)
    for (x, y) in ((0, 3), (5, 3)):
        put((2 + x, 2 + y), mix(hexc('#e8737a'), g[4], .4))
    for (x, y) in ((1, 4), (2, 5), (3, 5), (4, 4)):
        put((2 + x, 2 + y), icing)
    # back of head: a squiggle of icing hair
    for (x, y) in ((1, 0), (2, 1), (3, 0), (4, 1)):
        put((10 + x, 2 + y), icing)
    # body front (2..8, 10..15): gumdrop buttons
    for (x, y, c) in ((2, 0, red), (3, 0, red), (2, 2, green), (3, 2, green), (2, 4, red), (3, 4, red)):
        put((2 + x, 10 + y), c)
    # neck ruffle: icing zigzag on the body's top row (front and back)
    for x in range(6):
        if x % 2 == 0:
            put((2 + x, 10), icing) if x not in (2,) else None
    # arms: zig-zag icing cuffs at the hands. right arm front 18..22,2..5 (hand = low u)
    for (x, y) in ((1, 0), (0, 1), (1, 2)):
        put((18 + x, 2 + y), icing)
    # right arm back 24..28 (hand = high u)
    for (x, y) in ((2, 0), (3, 1), (2, 2)):
        put((24 + x, 2 + y), icing)
    # left arm front 18..22,7..10 (hand = high u)
    for (x, y) in ((2, 0), (3, 1), (2, 2)):
        put((18 + x, 7 + y), icing)
    for (x, y) in ((1, 0), (0, 1), (1, 2)):
        put((24 + x, 7 + y), icing)
    # legs: icing zig-zag above the feet. right leg front 2..5,17..22 ; left leg front 12..15,17..22
    for lx in (2, 12, 7, 17):  # front and back faces
        for x in range(3):
            put((lx + x, 17 + 3 + (x % 2)), icing)
    return img


# =========================================================================== reindeer
def reindeer():
    img = new(64, 64)
    fur = material('deer', 'deer', 2, 4, scale=3)
    cream = material('cream', 'deer-c', 2, 4, scale=3)
    dk = ramp('deer')
    cr = ramp('cream')

    def body(face, x, y, fw, fh):
        if face == 'bottom':
            return cream(x, y, 1)
        if face == 'front':  # shaggy white chest mane
            c = cream(x, y, 2, bias=.1)
            return mix(c, dk[3], .3) if (x + y * 2) % 7 == 0 else c
        if face == 'back':   # rump patch and tail area
            if 1 <= x <= fw - 2 and y >= 1:
                return cream(x, y, 3, bias=.15)
            return fur(x, y, 3)
        if face == 'top':
            c = fur(x, y, 4, bias=-.1)
            if x in (3, 4):
                c = mix(c, dk[1], .45)  # dark dorsal stripe
            if y >= fh - 4:              # mane spills over the shoulders
                c = mix(c, cr[3], .45 * (y - (fh - 5)) / 4)
            return c
        # sides: right face front is high u, left face front is low u
        front_dist = (fw - 1 - x) if face == 'right' else x
        c = fur(x, y, 5 if face == 'right' else 6)
        if y >= fh - 2:
            c = mix(c, cr[2], .65)                    # pale belly line
        if y == 0:
            c = mix(c, dk[1], .35)
        if front_dist < 4:
            c = mix(c, cr[3], .55 - front_dist * .12)  # neck mane
        if front_dist > fw - 3 and y > 1:
            c = mix(c, cr[3], .35)                    # pale rump edge
        return c

    box(img, 0, 0, 8, 8, 16, body)

    def neck(face, x, y, fw, fh):
        if face == 'front':
            c = cream(x, y, 7, bias=.15)
            return mix(c, cr[1], .3) if (x + y) % 4 == 0 else c
        if face in ('right', 'left'):
            fd = (fw - 1 - x) if face == 'right' else x
            c = fur(x, y, 8)
            return mix(c, cr[3], .6) if fd == 0 else (mix(c, cr[3], .3) if fd == 1 else c)
        if face == 'back':
            return mix(fur(x, y, 9), dk[1], .25)
        return fur(x, y, 10)

    box(img, 48, 0, 4, 8, 4, neck)

    def head(face, x, y, fw, fh):
        c = fur(x, y, 11)
        if face == 'top':
            c = mix(c, dk[1], .25)
        if face == 'bottom':
            c = cream(x, y, 12)
        if face in ('right', 'left'):
            fd = (fw - 1 - x) if face == 'right' else x
            if y == fh - 1:
                c = mix(c, cr[2], .5)   # pale jaw line
            if fd == 1 and y == 1:
                return hexc('#120a06')   # eye
            if fd == 1 and y == 0:
                return dk[1]             # brow
            if fd == 2 and y == 1:
                return hexc('#2a1a10')
        return c

    box(img, 0, 24, 5, 5, 6, head)
    # eye glints
    img.putpixel((4, 31), hexc('#f2e8d8'))
    img.putpixel((12, 31), hexc('#f2e8d8'))

    def snout(face, x, y, fw, fh):
        base = mix(fur(x, y, 13), cr[2], .35)
        if face == 'bottom':
            return cream(x, y, 14)
        if face == 'front':
            if y == fh - 1:
                return hexc('#3a2a22')  # mouth
            return mix(base, hexc('#5a4a40'), .3)
        return base

    box(img, 22, 24, 3, 3, 3, snout)

    def nose(face, x, y, fw, fh):
        n = ramp('black')
        if face == 'top' or (face == 'front' and y == 0 and x == 0):
            return n[4]
        return n[2]

    box(img, 34, 24, 2, 2, 1, nose)

    def ear(face, x, y, fw, fh):
        if face == 'front':
            return hexc('#e6cbb0')
        return fur(x, y, 15)

    box(img, 40, 24, 3, 1, 2, ear)
    box(img, 40, 27, 3, 1, 2, ear)

    horn = ramp('horn')
    for (x0, y0, x1, y1) in ((50, 24, 62, 40),):
        r = rng('antler')
        for y in range(y0, y1):
            for x in range(x0, x1):
                lvl = 2 + (1 if r.random() > .5 else 0) + (1 if (x + y) % 5 == 0 else 0)
                img.putpixel((x, y), horn[min(5, lvl)])
    # pale tips on the tip cubes
    for (tx, ty) in ((58, 24), (58, 32)):
        for y in range(ty, ty + 1):
            for x in range(tx, tx + 4):
                img.putpixel((x, y), horn[5])

    def leg(salt):
        def p(face, x, y, fw, fh):
            hoof = ramp('black')
            if face == 'bottom':
                return hoof[3]
            if face == 'top':
                return fur(x, y, salt)
            if y >= fh - 2:
                return hoof[3] if y == fh - 2 else hoof[2]
            c = fur(x, y, salt, bias=-.15 * (y / fh))
            if y == fh - 3:
                c = mix(c, cr[4], .7)   # white fetlock
            return c
        return p

    for i, ux in enumerate((0, 12, 24, 36)):
        box(img, ux, 36, 3, 9, 3, leg(20 + i))

    def tail(face, x, y, fw, fh):
        if face in ('back', 'bottom'):
            return cr[5]
        return cream(x, y, 30, bias=.2)

    box(img, 48, 40, 3, 3, 2, tail)
    return img


def reindeer_red_nose():
    img = new(64, 64)
    red, hi = hexc('#ff2a2a'), hexc('#ffb0a0')

    def nose(face, x, y, fw, fh):
        if face == 'front' and x == 0 and y == 0:
            return hi
        return red

    box(img, 34, 24, 2, 2, 1, nose)
    return img


# =========================================================================== krampus
def krampus():
    img = new(64, 64)
    fur = material('fur', 'kr', 1, 4, scale=2.5)
    fr = ramp('fur')
    skin = ramp('skin')
    metal = ramp('silver')

    def shaggy(salt, darker=0.0):
        def p(face, x, y, fw, fh):
            c = fur(x, y, salt, bias=-darker)
            # long hair: vertical streaks
            if (x * 3 + salt) % 4 == 0:
                c = mix(c, fr[0], .35)
            elif (x * 5 + y // 3 + salt) % 7 == 0:
                c = mix(c, fr[5], .3)
            return c
        return p

    box(img, 0, 0, 10, 13, 6, shaggy(1))
    # chest: a crossing chain with a brass bell (front 6..16, 6..19)
    for i in range(10):
        x, y = 6 + i, 6 + 2 + int(i * 0.9)
        img.putpixel((x, y), metal[4] if i % 2 == 0 else metal[2])
        img.putpixel((x, y + 1), metal[1])
    for (x, y) in ((11, 12), (12, 12), (11, 13), (12, 13)):
        img.putpixel((x, y), ramp('gold')[4] if y == 12 else ramp('gold')[2])
    # back: chain continues (22..32, 6..19)
    for i in range(10):
        x, y = 22 + i, 6 + 10 - int(i * 0.9)
        img.putpixel((x, y), metal[4] if i % 2 == 0 else metal[2])

    def head(face, x, y, fw, fh):
        c = shaggy(2)(face, x, y, fw, fh)
        if face == 'front':
            # dark skinned face framed by fur
            if 1 <= x <= 6 and 2 <= y <= 7:
                c = skin[1] if (x + y) % 3 else skin[2]
            if y == 2 and 1 <= x <= 6:
                c = skin[0]          # heavy brow
            if y == 3 and x in (1, 2, 5, 6):
                c = hexc('#1a0604')  # eye sockets (glow layer adds the eyes)
            if y == 5 and x in (3, 4):
                c = skin[0]          # nostrils
        return c

    box(img, 32, 0, 8, 8, 8, head)

    def jaw(face, x, y, fw, fh):
        if face == 'front':
            if y == 0 and x in (0, 3):
                return hexc('#f3ead6')  # fangs
            if y == 2:
                return skin[1]
            return hexc('#3a0a0c') if y == 1 else skin[2]
        if face == 'bottom':
            return fr[1]
        return skin[1]

    box(img, 32, 16, 4, 3, 2, jaw)

    def tongue(face, x, y, fw, fh):
        t = ramp('pink')
        return t[3] if y < fh - 1 else t[2]

    box(img, 44, 16, 2, 5, 1, tongue)

    def ear(face, x, y, fw, fh):
        if face == 'front':
            return skin[3]
        return fur(x, y, 5)

    box(img, 50, 16, 3, 2, 1, ear)
    box(img, 50, 19, 3, 2, 1, ear)

    def arm(salt):
        def p(face, x, y, fw, fh):
            if face == 'bottom' or y >= fh - 3:
                c = skin[0] if (x + y) % 2 else hexc('#160709')
                if y == fh - 1 and face == 'front':
                    return hexc('#d9cbb0')   # claws
                return c
            c = shaggy(salt)(face, x, y, fw, fh)
            if y >= fh - 5:
                c = mix(c, fr[0], .3)        # shaggy cuff
            return c
        return p

    box(img, 32, 32, 3, 16, 4, arm(6))
    box(img, 46, 32, 3, 16, 4, arm(7))

    def leg(salt):
        def p(face, x, y, fw, fh):
            if face == 'bottom' or y >= fh - 2:
                return hexc('#0c0a0a') if y != fh - 2 else hexc('#2a2622')   # cloven hooves
            if face == 'front' and x == fw // 2 and y >= fh - 3:
                return hexc('#0c0a0a')
            c = shaggy(salt)(face, x, y, fw, fh)
            if y > 8:
                c = mix(c, fr[0], .35)       # dark goat shins
            return c
        return p

    box(img, 0, 32, 4, 15, 4, leg(8))
    box(img, 16, 32, 4, 15, 4, leg(9))

    hn = ramp('horn')

    def horn_paint(face, x, y, fw, fh):
        lvl = 3 if (y % 2 == 0) else 2   # ridged rings
        if face in ('right', 'back'):
            lvl -= 1
        return hn[max(0, lvl)]

    def horn_tip(face, x, y, fw, fh):
        return hn[4] if y < 2 else hn[3]

    box(img, 0, 52, 2, 4, 2, horn_paint)
    box(img, 8, 52, 1, 4, 1, horn_tip)
    box(img, 12, 52, 2, 4, 2, horn_paint)
    box(img, 20, 52, 1, 4, 1, horn_tip)

    def switches(face, x, y, fw, fh):
        bark = [hexc('#efe9dd'), hexc('#d9d1c2'), hexc('#2a2420')]
        if y in (2, 6):
            return hexc('#7a2a1a')   # red cord ties
        if face == 'bottom':
            return hexc('#8a6a4a')
        c = bark[0] if (x + y) % 3 else bark[1]
        if (x * 7 + y * 3) % 9 == 0:
            c = bark[2]                # birch marks
        return c

    box(img, 30, 52, 2, 9, 2, switches)

    def chain(face, x, y, fw, fh):
        if face in ('right', 'left'):
            return metal[3] if y % 3 != 2 else metal[1]
        return metal[2] if y % 3 == 1 else (metal[4] if y % 3 == 0 else None)

    box(img, 38, 52, 1, 9, 3, chain)

    def tail(face, x, y, fw, fh):
        if y >= fh - 3:
            return fr[0]
        return fr[2]

    box(img, 46, 52, 1, 8, 1, tail)
    return img


def krampus_eyes():
    img = new(64, 64)
    amber, core = hexc('#ffb02e'), hexc('#fff3a0')
    # head front at (40..48, 8..16): eyes on row 3, x 1-2 and 5-6
    for (x, c) in ((1, amber), (2, core), (5, core), (6, amber)):
        img.putpixel((40 + x, 8 + 3), c)
    return img


# =========================================================================== armor
def santa_hat_armor():
    img = new(64, 32)
    red = material('red', 'hat', 2, 4, scale=2)
    white = material('snow', 'hat-w', 3, 5, scale=2)

    def trim(face, x, y, fw, fh):
        c = white(x, y, 1)
        if face == 'bottom':
            return None
        return c

    def velvet(salt):
        def p(face, x, y, fw, fh):
            c = red(x, y, salt)
            if face in ('right', 'back'):
                c = mix(c, hexc('#3f0710'), .25)
            if face == 'top':
                c = mix(c, hexc('#ff8a8a'), .12)
            return c
        return p

    box(img, 0, 0, 9, 3, 9, trim)
    box(img, 0, 12, 7, 3, 7, velvet(2))
    box(img, 28, 12, 5, 3, 5, velvet(3))
    box(img, 48, 12, 3, 3, 3, velvet(4))
    box(img, 0, 22, 3, 3, 3, lambda f, x, y, fw, fh: white(x, y, 5, bias=.15))
    return img


def sweater_armor():
    img = new(64, 32)
    green = ramp('green')
    red = ramp('red')
    white = hexc('#f4f0ea')

    def knit(x, y):
        # V-stitch knit: alternate shades in a chevron
        return green[3] if ((x + (y % 2)) % 2 == 0) else green[2]

    def body(face, x, y, fw, fh):
        if face in ('top', 'bottom'):
            return red[2] if face == 'top' else green[1]
        c = knit(x, y)
        if y == 0:
            return red[3] if x % 2 else red[2]              # ribbed collar
        if y == fh - 1:
            return red[3] if x % 2 else red[2]              # ribbed hem
        if y in (3, 5):
            return red[3]
        if y == 4:
            return white if (x % 4) in (0, 2) else red[3]   # snowflake band
        if face in ('front', 'back') and 6 <= y <= 9:
            # a little pine tree on the front and back
            cx = fw // 2
            rows = {6: (cx - 1, cx), 7: (cx - 2, cx + 1), 8: (cx - 3, cx + 2), 9: (cx - 1, cx)}
            lo, hi = rows[y]
            if lo <= x <= hi:
                if y == 9:
                    return hexc('#6b4526')
                return hexc('#0f3d1f') if (x + y) % 3 else hexc('#ffd84a')
        return c

    def arm(face, x, y, fw, fh):
        if face in ('top', 'bottom'):
            return green[2]
        if y >= fh - 2:
            return red[3] if x % 2 else red[2]              # ribbed cuffs
        if y in (3, 5):
            return red[3]
        if y == 4:
            return white if x % 2 == 0 else red[3]
        return knit(x, y)

    box(img, 16, 16, 8, 12, 4, body)
    box(img, 40, 16, 4, 12, 4, arm)
    return img


ENTITIES = {
    'entity/gingerbread_man': gingerbread_man,
    'entity/reindeer/reindeer': reindeer,
    'entity/reindeer/red_nose': reindeer_red_nose,
    'entity/krampus/krampus': krampus,
    'entity/krampus/krampus_eyes': krampus_eyes,
    'models/armor/santa_hat': santa_hat_armor,
    'models/armor/festive_layer_1': sweater_armor,
}
