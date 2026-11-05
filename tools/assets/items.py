"""Item sprites (16x16)."""
from pix import sprite, new, paste, hexc, mix, frames, outline_existing, R, ramp
from PIL import Image

W = '#ffffff'


def ginger_root():
    return sprite([
        '................',
        '................',
        '..........gG....',
        '.........gg.....',
        '....rr...rr.....',
        '...rRRr.rRrr....',
        '...rRrrkrRrrr...',
        '....rrrrrrrkrr..',
        '..rrkrrrrrrrrrp.',
        '.rRrrrrkrrrrrpp.',
        '.rrrrrrrrrrrr...',
        '..rrkrr.rrkrr...',
        '...rrr...rrr....',
        '....p.....pp....',
        '................',
        '................',
    ], {'r': 'root', 'g': 'green'}, {'k': '#8a6436', 'p': '#f4e39a'})


def gingerbread_dough():
    return sprite([
        '................',
        '................',
        '................',
        '.......dddd.....',
        '.....dDDDddd....',
        '....dDDddddddd..',
        '...dDdddddsddd..',
        '..dDddsddddddd..',
        '..ddddddddddsdd.',
        '..dddddsdddddd..',
        '..ddsddddddddd..',
        '...dddddddddd...',
        '....dddddddd....',
        '................',
        '................',
        '................',
    ], {'d': 'dough'}, {'s': '#8a5a2c'}, soft=True)


def gingerbread():
    return sprite([
        '................',
        '................',
        '................',
        '................',
        '....gggggggggg..',
        '...gGGGGGGGGgg..',
        '..gGgsgggsggggg.',
        '..gGggggggsgggg.',
        '..gGgggsgggggsg.',
        '..ggsgggggggggg.',
        '..gggggsgggsggg.',
        '..ggggggggggggg.',
        '...ccccccccccc..',
        '................',
        '................',
        '................',
    ], {'g': 'ginger', 'c': ['#3d1d0b', '#5a2a10', '#6b3415', '#7d3e19']}, {'s': '#7b3d16'})


def gingerbread_cookie():
    return sprite([
        '................',
        '......gggg......',
        '.....gGGggg.....',
        '.....gwggwg.....',
        '.....gggggg.....',
        '.....gwggwg.....',
        '......wwww......',
        '..gGgggggggggg..',
        '.wwGgggrrggggww.',
        '.wwggggggggggww.',
        '.....gggggg.....',
        '.....gggngg.....',
        '....gggggggg....',
        '...gggg..gggg...',
        '..wwggg..gggww..',
        '................',
    ], {'g': 'ginger'}, {'w': '#fbfdff', 'r': '#d8253b', 'n': '#2a9a4a'})


def icing():
    # A wooden bowl topped with a swirl of royal icing.
    return sprite([
        '................',
        '................',
        '.......ii.......',
        '......iIi.......',
        '.....iIiii......',
        '......iiii......',
        '....iIiiiiii....',
        '...iiiiiiiiiii..',
        '..wwwwwwwwwwww..',
        '..wWwwwwwwwwww..',
        '...wwwwwwwwww...',
        '....wwwwwwww....',
        '.....wwwwww.....',
        '................',
        '................',
        '................',
    ], {'i': 'icing', 'w': 'wood'})


def candy_cane():
    return sprite([
        '................',
        '.......wwrr.....',
        '......rrwwrr....',
        '.....wwr..wwr...',
        '.....rr....rw...',
        '.....ww....wr...',
        '...........rw...',
        '..........ww....',
        '..........rr....',
        '.........ww.....',
        '.........rr.....',
        '........ww......',
        '........rr......',
        '.......ww.......',
        '.......rr.......',
        '................',
    ], {'r': 'red', 'w': 'white'}, base=3)


def hot_cocoa():
    return sprite([
        '................',
        '.....s...s......',
        '......s...s.....',
        '.....s...s......',
        '................',
        '...mmmmmmmmm....',
        '...ccwccwcccwm..',
        '...mMMmmmmmmm.mm',
        '...mMmmmmmmmm..m',
        '...mMmmmwmmmm..m',
        '...mmmmwwwmmm.mm',
        '...mmmmmwmmmmm..',
        '...mmmmmmmmmm...',
        '....mmmmmmmm....',
        '................',
        '................',
    ], {'m': 'mug', 'c': 'cocoa'}, {'w': '#fff6ea', 's': '#e9eef3'}, base=3)


def candy_cane_sword():
    return sprite([
        '................',
        '.............ww.',
        '............wrrw',
        '...........rrww.',
        '..........wwrr..',
        '.........rrww...',
        '........wwrr....',
        '.......rrww.....',
        '..k...wwrr......',
        '...k.rrww.......',
        '...kkwrr........',
        '....kgk.........',
        '...ggkkk........',
        '..gg...k........',
        '.oo.............',
        '.oo.............',
    ], {'r': 'red', 'w': 'white', 'k': 'gold', 'g': 'green'}, {'o': '#ffd84a'}, outline=False)


def sleigh_bells():
    return sprite([
        '................',
        '..ll............',
        '...ll...........',
        '....ll..........',
        '.....ll.........',
        '....bbll........',
        '...bBbbll.......',
        '...bbbb.ll......',
        '....kk..bbll....',
        '.......bBbbll...',
        '.......bbbb.ll..',
        '........kk..bbl.',
        '...........bBbb.',
        '...........bbbb.',
        '............kk..',
        '................',
    ], {'l': 'leather', 'b': 'gold'}, {'k': '#3a2408'})


def santa_hat():
    return sprite([
        '................',
        '................',
        '.........rr.....',
        '........rRrrr...',
        '.......rRrrrrr..',
        '......rRrrrr.pp.',
        '......rRrrr..pPp',
        '.....rRrrrr..ppp',
        '.....rRrrrrr....',
        '....rRrrrrrrr...',
        '....rrrrrrrrr...',
        '..ppppppppppppp.',
        '..pPPPPPPPPPPpp.',
        '..ppppppppppppp.',
        '................',
        '................',
    ], {'r': 'red', 'p': 'white'})


def festive_sweater():
    return sprite([
        '................',
        '................',
        '...gggg..gggg...',
        '..gGGggwwggggg..',
        '.gGgggggggggggg.',
        '.gGggggrgggggggg',
        '.ggrwrwrwrwrwrg.',
        '.ggggggtgggggggg',
        '.gg.ggtttggg.gg.',
        '.gg.gtttttgg.gg.',
        '.gg.ggtttggg.gg.',
        '.ww.ggg.gggg.ww.',
        '....wwwwwwww....',
        '................',
        '................',
        '................',
    ], {'g': 'green'}, {'w': '#f4f0ea', 'r': '#cf2337', 't': '#0f3d1f'})


def naughty_coal():
    return sprite([
        '................',
        '................',
        '................',
        '.....rr...rr....',
        '......rrrrr.....',
        '.......rr.......',
        '.....ccccc......',
        '....cCCcccc.....',
        '...cCcccccccc...',
        '...cCccsccccc...',
        '..ccccccccscc...',
        '..ccsccccccccc..',
        '...cccccccccc...',
        '....cccccccc....',
        '......cccc......',
        '................',
    ], {'c': 'coal', 'r': 'red'}, {'s': '#6c6c78'})


def music_disc():
    img = sprite([
        '................',
        '.....dddddd.....',
        '...ddDDddddd....',
        '..dDdrrrrrrddd..',
        '..dDrrrrrrrrdd..',
        '.dDrrgggggrrrdd.',
        '.ddrrgwwwggrrdd.',
        '.ddrrgw..wgrrdd.',
        '.ddrrgw..wgrrdd.',
        '.ddrrggwwggrrdd.',
        '.ddrrrggggrrrdd.',
        '..ddrrrrrrrrdd..',
        '..dddrrrrrrddd..',
        '...dddddddddd...',
        '.....dddddd.....',
        '................',
    ], {'d': 'black', 'r': 'red', 'g': 'green'}, {'w': '#ffffff'}, outline=False)
    return img


def wreath_item():
    return sprite([
        '................',
        '......ssss......',
        '....ssSSssss....',
        '...sSsbssbsss...',
        '..sSs......sss..',
        '..sSb......bss..',
        '.sSs........sss.',
        '.ssb........bss.',
        '.sss........sss.',
        '..sss......sss..',
        '..ssbs....sbss..',
        '...sssrrrrsss...',
        '....ssrRrrss....',
        '.....rr..rr.....',
        '....rr....rr....',
        '................',
    ], {'s': 'spruce', 'r': 'red'}, {'b': '#e0263c'})


def stocking_item():
    return sprite([
        '................',
        '......l.........',
        '.....l.l........',
        '....ccccccc.....',
        '....cCCCCcc.....',
        '....ccccccc.....',
        '.....rrrrr......',
        '.....rRrrr......',
        '.....rRwrr......',
        '.....rrrrr......',
        '.....rrrrrr.....',
        '....grrrrrrrr...',
        '...ggrrrrrrrrr..',
        '...gggrrrrrggg..',
        '....ggg....ggg..',
        '................',
    ], {'c': 'white', 'r': 'red', 'g': 'green'}, {'l': '#c08518', 'w': '#ffd84a'})


def star_item():
    return sprite([
        '................',
        '.......ss.......',
        '.......ss.......',
        '......sSss......',
        '......sSss......',
        '.sssssSSsssssss.',
        '..sSSSSsssssss..',
        '...ssssssssss...',
        '....ssssssss....',
        '....ssssssss....',
        '...ssssssssss...',
        '...ssss..ssss...',
        '..sss......sss..',
        '..s..........s..',
        '................',
        '................',
    ], {'s': 'gold'})


def ornament_item(color):
    return sprite([
        '................',
        '.......kk.......',
        '......k..k......',
        '.......kk.......',
        '......gggg......',
        '......gGgg......',
        '.....oooooo.....',
        '....oWWooooo....',
        '...oWWooooooo...',
        '..oWoooooooooo..',
        '..ooopppppppoo..',
        '..oooooooooooo..',
        '...oooooooooo...',
        '....oooooooo....',
        '......oooo......',
        '................',
    ], {'o': color, 'g': 'gold'}, {'k': '#8a6436', 'W': '#ffffff', 'p': mix(hexc(R[color][4]), hexc('#ffffff'), .35)})


def lights_item():
    return sprite([
        '................',
        '................',
        '................',
        'kk..........kk..',
        '..kk......kk..kk',
        '...k.kkkkk..k...',
        '...h...h..h.h...',
        '..rRr.gGg.bBb.y.',
        '..rrr.ggg.bbb.yY',
        '..rrr.ggg.bbb.yy',
        '...r...g...b..y.',
        '................',
        '................',
        '................',
        '................',
        '................',
    ], {'r': 'red', 'g': 'green', 'b': 'blue', 'y': 'gold'}, {'k': '#1d3a22', 'h': '#3d4a3f'}, outline=False)


def wild_ginger_item():
    # same as the block texture, reused for the item
    return None


ITEMS = {
    'ginger_root': ginger_root,
    'gingerbread_dough': gingerbread_dough,
    'gingerbread': gingerbread,
    'gingerbread_cookie': gingerbread_cookie,
    'icing': icing,
    'candy_cane': candy_cane,
    'hot_cocoa': hot_cocoa,
    'candy_cane_sword': candy_cane_sword,
    'sleigh_bells': sleigh_bells,
    'santa_hat': santa_hat,
    'festive_sweater': festive_sweater,
    'naughty_coal': naughty_coal,
    'music_disc_jingle_bells': music_disc,
    'wreath': wreath_item,
    'stocking': stocking_item,
    'star_topper': star_item,
    'red_ornament': lambda: ornament_item('red'),
    'gold_ornament': lambda: ornament_item('gold'),
    'blue_ornament': lambda: ornament_item('blue'),
    'christmas_lights': lights_item,
}


def gingerbread_door_item():
    return sprite([
        '................',
        '....gggggggg....',
        '....giiiiiig....',
        '....gibbbbig....',
        '....gibiibig....',
        '....gibbbbig....',
        '....giiiiiig....',
        '....gggggggg....',
        '....gggggggg....',
        '....ggiggigg....',
        '....gggiigrg....',
        '....ggggiggg....',
        '....gggggggg....',
        '....giiiiiig....',
        '....gggggggg....',
        '................',
    ], {'g': 'ginger'}, {'i': '#f7fbff', 'b': '#a9d3f5', 'r': '#d8253b'}, outline=True)


ITEMS['gingerbread_door'] = gingerbread_door_item
