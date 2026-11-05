"""Structure templates.

* christmas_tree / christmas_tree_gold: a decorated spruce built here from scratch, with a
  spiral of lights, hanging ornaments, a star on top and sealed presents underneath.
* house_ginger: the original gingerbread house, re-skinned with the mod's own blocks and
  given loot-table barrels and presents. The source layout is kept in tools/assets/source.
"""
import math
import os

import gzip
import io

import nbtlib
from nbtlib import Compound, Int, List, String

MOD = 'christmas_ginger_house'
DATA_VERSION = 3465  # 1.20.1


def save_nbt(root, path):
    """Gzipped NBT with a zero timestamp, so regenerating gives byte-identical files."""
    buf = io.BytesIO()
    nbtlib.File(root).write(buf)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'wb') as fh:
        fh.write(gzip.compress(buf.getvalue(), mtime=0))


class Template:
    def __init__(self, size):
        self.size = size
        self.blocks = {}

    def set(self, pos, name, props=None, nbt=None):
        self.blocks[tuple(pos)] = (name, tuple(sorted((props or {}).items())), nbt)

    def get(self, pos):
        b = self.blocks.get(tuple(pos))
        return b[0] if b else None

    def save(self, path):
        palette, index = [], {}
        blocks = []
        for pos, (name, props, nbt) in sorted(self.blocks.items(), key=lambda kv: (kv[0][1], kv[0][2], kv[0][0])):
            key = (name, props)
            if key not in index:
                index[key] = len(palette)
                entry = Compound({'Name': String(name)})
                if props:
                    entry['Properties'] = Compound({k: String(v) for k, v in props})
                palette.append(entry)
            b = Compound({'pos': List[Int]([Int(c) for c in pos]), 'state': Int(index[key])})
            if nbt is not None:
                b['nbt'] = nbt
            blocks.append(b)
        root = Compound({
            'size': List[Int]([Int(c) for c in self.size]),
            'entities': List[Compound]([]),
            'blocks': List[Compound](blocks),
            'palette': List[Compound](palette),
            'DataVersion': Int(DATA_VERSION),
        })
        save_nbt(root, path)


def present_nbt(table):
    return Compound({'id': String(f'{MOD}:present'), 'LootTable': String(f'{MOD}:{table}')})


FACING = {(1, 0): 'east', (-1, 0): 'west', (0, 1): 'south', (0, -1): 'north'}


def christmas_tree(gold=False):
    t = Template((13, 17, 13))
    c = 6
    tiers = {2: 5.2, 3: 4.3, 4: 3.4, 5: 4.4, 6: 3.5, 7: 2.6, 8: 3.5, 9: 2.6, 10: 1.8, 11: 2.4, 12: 1.6, 13: 1.0, 14: 0.0}
    leaves = {'distance': '1', 'persistent': 'true', 'waterlogged': 'false'}
    for y in range(0, 14):
        t.set((c, y, c), 'minecraft:spruce_log', {'axis': 'y'})
    for y, r in tiers.items():
        for x in range(13):
            for z in range(13):
                dx, dz = x - c, z - c
                if (dx, dz) == (0, 0) and y < 14:
                    continue
                if dx * dx + dz * dz <= r * r + 0.3:
                    t.set((x, y, z), 'minecraft:spruce_leaves', leaves)
    t.set((c, 15, c), f'{MOD}:star_topper', {'facing': 'south'})

    # garland of lights spiralling up the outside of the tree
    for y in range(2, 14):
        for x in range(13):
            for z in range(13):
                if t.get((x, y, z)) is not None:
                    continue
                for (dx, dz), facing in FACING.items():
                    if t.get((x - dx, y, z - dz)) == 'minecraft:spruce_leaves':
                        ang = (math.degrees(math.atan2(z - c, x - c)) + 360) % 360
                        if int((ang + y * 55) // 40) % 3 == 0:
                            t.set((x, y, z), f'{MOD}:christmas_lights', {'facing': facing})
                        break

    # baubles hanging under the overhang of each wider tier
    colours = ['red', 'gold', 'blue']
    n = 0
    for y in range(1, 13):
        for x in range(13):
            for z in range(13):
                if t.get((x, y, z)) is None and t.get((x, y + 1, z)) == 'minecraft:spruce_leaves' and (x + z + y) % 3 == 0:
                    dist = math.hypot(x - c, z - c)
                    if dist >= 1.5:
                        t.set((x, y, z), f'{MOD}:{colours[n % 3]}_ornament', {'hanging': 'true'})
                        n += 1

    # snow underfoot and presents round the trunk
    for x in range(13):
        for z in range(13):
            d = math.hypot(x - c, z - c)
            if d <= 5.6 and t.get((x, 0, z)) is None:
                t.set((x, 0, z), 'minecraft:snow', {'layers': '1'})
    gifts = [((c - 2, c + 1), 'red', 'south'), ((c + 1, c + 2), 'green', 'east'), ((c + 2, c - 1), 'blue', 'west'),
             ((c - 1, c - 2), 'gold' if gold else 'red', 'north')]
    for (x, z), colour, facing in gifts:
        table = 'gifts/rare' if colour == 'gold' else 'gifts/common'
        t.set((x, 0, z), f'{MOD}:{colour}_present', {'facing': facing}, present_nbt(table))
    return t


# --------------------------------------------------------------------------- the house
HOUSE_REMAP = {
    'minecraft:spruce_planks': (f'{MOD}:gingerbread_bricks', {}),
    'minecraft:oak_planks': (f'{MOD}:gingerbread_block', {}),
    'minecraft:smooth_quartz': (f'{MOD}:icing_block', {}),
    'minecraft:red_concrete': (f'{MOD}:candy_cane_block', {'axis': 'y'}),
    'minecraft:white_concrete': (f'{MOD}:peppermint_block', {}),
    'minecraft:bricks': (f'{MOD}:frosted_gingerbread_bricks', {}),
    'minecraft:oak_trapdoor': (f'{MOD}:gingerbread_trapdoor', None),
    'minecraft:spruce_trapdoor': (f'{MOD}:gingerbread_trapdoor', None),
    'minecraft:oak_door': (f'{MOD}:gingerbread_door', None),
    'minecraft:oak_stairs': (f'{MOD}:gingerbread_brick_stairs', None),
    'minecraft:spruce_stairs': (f'{MOD}:gingerbread_brick_stairs', None),
    'minecraft:brick_stairs': (f'{MOD}:gingerbread_brick_stairs', None),
    'minecraft:oak_slab': (f'{MOD}:gingerbread_brick_slab', None),
}


def remap_house(src, dst):
    f = nbtlib.load(src)
    root = f
    palette = root['palette']
    for entry in palette:
        name = str(entry['Name'])
        if name in HOUSE_REMAP:
            new_name, props = HOUSE_REMAP[name]
            entry['Name'] = String(new_name)
            if props is None:      # same property set as the vanilla block: keep them
                continue
            if props:
                entry['Properties'] = Compound({k: String(v) for k, v in props.items()})
            elif 'Properties' in entry:
                del entry['Properties']
    names = [str(p['Name']) for p in palette]
    for b in root['blocks']:
        if names[int(b['state'])] == 'minecraft:barrel' and 'nbt' in b:
            nbt = b['nbt']
            if 'Items' in nbt:
                del nbt['Items']
            nbt['LootTable'] = String(f'{MOD}:chests/gingerbread_house')
    decorate_house(root)
    save_nbt(root, dst)


def decorate_house(root):
    """Adds a wreath on the front door, lights on the porch posts and presents in the yard."""
    palette = root['palette']
    names = [str(p['Name']) for p in palette]
    grid = {tuple(int(c) for c in b['pos']): names[int(b['state'])] for b in root['blocks']}
    solid = lambda n: n not in (None, 'minecraft:air', 'minecraft:snow', 'minecraft:cave_air')

    def state_index(name, props):
        for i, p in enumerate(palette):
            if str(p['Name']) == name and {k: str(v) for k, v in p.get('Properties', {}).items()} == props:
                return i
        entry = Compound({'Name': String(name)})
        if props:
            entry['Properties'] = Compound({k: String(v) for k, v in props.items()})
        palette.append(entry)
        return len(palette) - 1

    def put(pos, name, props, nbt=None):
        blocks = root['blocks']
        for b in list(blocks):
            if tuple(int(c) for c in b['pos']) == pos:
                blocks.remove(b)
        b = Compound({'pos': List[Int]([Int(c) for c in pos]), 'state': Int(state_index(name, props))})
        if nbt is not None:
            b['nbt'] = nbt
        blocks.append(b)
        grid[pos] = name

    air = lambda pos: grid.get(pos) in (None, 'minecraft:air')
    # wreath above the front door (door at x=25, z=19, front faces +x)
    if air((26, 3, 19)) and solid(grid.get((25, 3, 19))):
        put((26, 3, 19), f'{MOD}:wreath', {'facing': 'east'})
    # lights wrapped round the porch posts
    for z in (18, 20):
        for y in (1, 2):
            if solid(grid.get((26, y, z))) and air((27, y, z)):
                put((27, y, z), f'{MOD}:christmas_lights', {'facing': 'east'})
    # stockings either side of the door
    for z in (17, 21):
        if air((26, 2, z)) and solid(grid.get((25, 2, z))):
            put((26, 2, z), f'{MOD}:stocking', {'facing': 'east'})
    # presents in the front yard
    placed = 0
    for (x, z, colour) in ((28, 16, 'red'), (28, 22, 'green'), (29, 17, 'blue'), (29, 21, 'gold')):
        if air((x, 1, z)) and solid(grid.get((x, 0, z))):
            table = 'gifts/rare' if colour == 'gold' else 'gifts/common'
            put((x, 1, z), f'{MOD}:{colour}_present', {'facing': 'west'}, present_nbt(table))
            placed += 1
    print('house decorations: presents', placed)


def generate(data_dir, source_house, folder='structures'):
    out = os.path.join(data_dir, folder)
    christmas_tree(False).save(os.path.join(out, 'christmas_tree.nbt'))
    christmas_tree(True).save(os.path.join(out, 'christmas_tree_gold.nbt'))
    remap_house(source_house, os.path.join(out, 'house_ginger.nbt'))
