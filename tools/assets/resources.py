"""Generates every JSON resource for the 1.20.1 common module (and Forge biome modifiers)."""
import json
import os

import targets

MOD = 'christmas_ginger_house'


def ns(path):
    return f'{MOD}:{path}'


class Writer:
    def __init__(self, root, target='1.20.1', data=False):
        self.root = root
        self.count = 0
        self.target = target
        self.data = data

    def json(self, rel, data):
        if self.data:
            data = targets.translate(rel, data, self.target)
            rel = targets.translate_path(rel, self.target)
        path = os.path.join(self.root, rel)
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, 'w', encoding='utf-8') as fh:
            json.dump(data, fh, indent=2, ensure_ascii=False)
            fh.write('\n')
        self.count += 1


PRESENT_COLORS = ['red', 'green', 'blue', 'gold']
ORNAMENTS = ['red', 'gold', 'blue']

LANG = {
    'itemGroup.christmas_ginger_house.christmas': 'Christmas Ginger House',
    'modmenu.nameTranslation.christmas_ginger_house': 'Christmas Ginger House',
    'container.christmas_ginger_house.present': 'Present',
    # blocks
    'block.christmas_ginger_house.gingerbread_block': 'Gingerbread Block',
    'block.christmas_ginger_house.gingerbread_bricks': 'Gingerbread Bricks',
    'block.christmas_ginger_house.gingerbread_brick_stairs': 'Gingerbread Brick Stairs',
    'block.christmas_ginger_house.gingerbread_brick_slab': 'Gingerbread Brick Slab',
    'block.christmas_ginger_house.gingerbread_brick_wall': 'Gingerbread Brick Wall',
    'block.christmas_ginger_house.frosted_gingerbread_bricks': 'Frosted Gingerbread Bricks',
    'block.christmas_ginger_house.icing_block': 'Block of Icing',
    'block.christmas_ginger_house.gingerbread_door': 'Gingerbread Door',
    'block.christmas_ginger_house.gingerbread_trapdoor': 'Gingerbread Trapdoor',
    'block.christmas_ginger_house.candy_cane_block': 'Candy Cane Block',
    'block.christmas_ginger_house.peppermint_block': 'Peppermint Block',
    'block.christmas_ginger_house.christmas_lights': 'Christmas Lights',
    'block.christmas_ginger_house.wreath': 'Wreath',
    'block.christmas_ginger_house.stocking': 'Stocking',
    'block.christmas_ginger_house.star_topper': 'Star Topper',
    'block.christmas_ginger_house.red_ornament': 'Red Ornament',
    'block.christmas_ginger_house.gold_ornament': 'Gold Ornament',
    'block.christmas_ginger_house.blue_ornament': 'Blue Ornament',
    'block.christmas_ginger_house.advent_calendar': 'Advent Calendar',
    'block.christmas_ginger_house.red_present': 'Red Present',
    'block.christmas_ginger_house.green_present': 'Green Present',
    'block.christmas_ginger_house.blue_present': 'Blue Present',
    'block.christmas_ginger_house.gold_present': 'Gold Present',
    'block.christmas_ginger_house.ginger_crop': 'Ginger',
    'block.christmas_ginger_house.wild_ginger': 'Wild Ginger',
    'block.christmas_ginger_house.present.empty': 'Empty. Place it and use it to wrap up to 9 items.',
    'block.christmas_ginger_house.present.wrapped': 'Wrapped! Place it and use it to unwrap.',
    'block.christmas_ginger_house.present.tag': 'From: %s',
    'block.christmas_ginger_house.present.from': 'A present from %s!',
    'block.christmas_ginger_house.advent_calendar.opened': 'Door %s of 24 opened!',
    'block.christmas_ginger_house.advent_calendar.finale': 'Door %s: Merry Christmas!',
    'block.christmas_ginger_house.advent_calendar.wait': 'Door %s opens tomorrow.',
    # items
    'item.christmas_ginger_house.ginger_root': 'Ginger Root',
    'item.christmas_ginger_house.gingerbread_dough': 'Gingerbread Dough',
    'item.christmas_ginger_house.gingerbread': 'Gingerbread',
    'item.christmas_ginger_house.gingerbread_cookie': 'Gingerbread Cookie',
    'item.christmas_ginger_house.gingerbread_cookie.desc': 'Iced with care. Grants Absorption.',
    'item.christmas_ginger_house.icing': 'Icing',
    'item.christmas_ginger_house.candy_cane': 'Candy Cane',
    'item.christmas_ginger_house.candy_cane.desc': 'A quick sugar rush: Speed for 10 seconds.',
    'item.christmas_ginger_house.hot_cocoa': 'Hot Cocoa',
    'item.christmas_ginger_house.hot_cocoa.desc': 'Thaws you out and slowly heals.',
    'item.christmas_ginger_house.naughty_coal': 'Naughty Coal',
    'item.christmas_ginger_house.naughty_coal.desc': 'Someone was on the list. Burns for 16 items.',
    'item.christmas_ginger_house.candy_cane_sword': 'Candy Cane Sword',
    'item.christmas_ginger_house.candy_cane_sword.desc': 'Every hit gives you a sugar rush.',
    'item.christmas_ginger_house.sleigh_bells': 'Sleigh Bells',
    'item.christmas_ginger_house.sleigh_bells.desc': 'Ring to call nearby reindeer to your side.',
    'item.christmas_ginger_house.santa_hat': 'Santa Hat',
    'item.christmas_ginger_house.festive_sweater': 'Festive Sweater',
    'item.christmas_ginger_house.music_disc_jingle_bells': 'Music Disc',
    'item.christmas_ginger_house.music_disc_jingle_bells.desc': 'J. Pierpont - Jingle Bells (Music Box)',
    'item.christmas_ginger_house.gingerbread_man_spawn_egg': 'Gingerbread Man Spawn Egg',
    'item.christmas_ginger_house.reindeer_spawn_egg': 'Reindeer Spawn Egg',
    'item.christmas_ginger_house.krampus_spawn_egg': 'Krampus Spawn Egg',
    # entities
    'entity.christmas_ginger_house.gingerbread_man': 'Gingerbread Man',
    'entity.christmas_ginger_house.reindeer': 'Reindeer',
    'entity.christmas_ginger_house.krampus': 'Krampus',
    # structures
    'structure.christmas_ginger_house.christmas_ginger_house': 'Christmas Ginger House',
    'structure.christmas_ginger_house.christmas_tree': 'Christmas Tree',
    # subtitles
    'subtitles.christmas_ginger_house.item.sleigh_bells.ring': 'Sleigh bells jingle',
    'subtitles.christmas_ginger_house.block.present.unwrap': 'Present unwrapped',
    'subtitles.christmas_ginger_house.block.present.wrap': 'Paper rustles',
    'subtitles.christmas_ginger_house.block.advent_calendar.open': 'Advent door opens',
    'subtitles.christmas_ginger_house.entity.gingerbread_man.ambient': 'Gingerbread Man giggles',
    'subtitles.christmas_ginger_house.entity.gingerbread_man.hurt': 'Gingerbread Man crumbles',
    'subtitles.christmas_ginger_house.entity.gingerbread_man.death': 'Gingerbread Man crunches',
    'subtitles.christmas_ginger_house.entity.krampus.ambient': 'Krampus tolls',
    'subtitles.christmas_ginger_house.entity.krampus.hurt': 'Krampus hurts',
    'subtitles.christmas_ginger_house.entity.krampus.death': 'Krampus dies',
    'subtitles.christmas_ginger_house.entity.krampus.chains': 'Chains rattle',
    # advancements
    'advancements.christmas_ginger_house.root.title': 'Christmas Ginger House',
    'advancements.christmas_ginger_house.root.description': 'Find ginger root in a snowy forest, or a cookie in a gingerbread house',
    'advancements.christmas_ginger_house.home_sweet_home.title': 'Home Sweet Gingerbread Home',
    'advancements.christmas_ginger_house.home_sweet_home.description': 'Step inside a Christmas Ginger House',
    'advancements.christmas_ginger_house.unwrap_present.title': 'A Gift for You',
    'advancements.christmas_ginger_house.unwrap_present.description': 'Unwrap a present',
    'advancements.christmas_ginger_house.open_advent_door.title': 'The Countdown Begins',
    'advancements.christmas_ginger_house.open_advent_door.description': 'Open a door of an Advent Calendar',
    'advancements.christmas_ginger_house.christmas_eve.title': "'Twas the Night Before Christmas",
    'advancements.christmas_ginger_house.christmas_eve.description': 'Open all 24 doors of the Advent Calendar',
    'advancements.christmas_ginger_house.sweet_tooth.title': 'Sweet Tooth',
    'advancements.christmas_ginger_house.sweet_tooth.description': 'Eat a Candy Cane',
    'advancements.christmas_ginger_house.warm_and_cozy.title': 'Warm and Cozy',
    'advancements.christmas_ginger_house.warm_and_cozy.description': 'Drink a mug of Hot Cocoa',
    'advancements.christmas_ginger_house.deck_the_halls.title': 'Deck the Halls',
    'advancements.christmas_ginger_house.deck_the_halls.description': 'Put a Star Topper on something',
    'advancements.christmas_ginger_house.dress_the_part.title': 'Dress the Part',
    'advancements.christmas_ginger_house.dress_the_part.description': 'Have a Santa Hat and a Festive Sweater',
    'advancements.christmas_ginger_house.naughty_list.title': 'Naughty List',
    'advancements.christmas_ginger_house.naughty_list.description': 'Defeat Krampus',
    'advancements.christmas_ginger_house.reindeer_games.title': 'Reindeer Games',
    'advancements.christmas_ginger_house.reindeer_games.description': 'Breed two Reindeer',
}


def generate(common_res, loader_res, target='1.20.1'):
    """loader_res: resources of the Forge (1.20.1) or NeoForge (1.21+) module."""
    a = Writer(os.path.join(common_res, 'assets', MOD))
    d = Writer(os.path.join(common_res, 'data', MOD), target, True)
    mc = Writer(os.path.join(common_res, 'data', 'minecraft'), target, True)
    fg = Writer(os.path.join(loader_res, 'data', MOD), target, True)
    modern = targets.TARGETS[target]['components']

    # ------------------------------------------------------------------ simple cubes
    def cube_all(name, tex=None):
        a.json(f'models/block/{name}.json', {'parent': 'minecraft:block/cube_all', 'textures': {'all': ns(f'block/{tex or name}')}})
        a.json(f'blockstates/{name}.json', {'variants': {'': {'model': ns(f'block/{name}')}}})
        a.json(f'models/item/{name}.json', {'parent': ns(f'block/{name}')})

    for n in ('gingerbread_block', 'gingerbread_bricks', 'icing_block', 'peppermint_block'):
        cube_all(n)

    a.json('models/block/frosted_gingerbread_bricks.json', {'parent': 'minecraft:block/cube_bottom_top', 'textures': {
        'side': ns('block/frosted_gingerbread_bricks'), 'top': ns('block/icing_block'), 'bottom': ns('block/gingerbread_bricks')}})
    a.json('blockstates/frosted_gingerbread_bricks.json', {'variants': {'': {'model': ns('block/frosted_gingerbread_bricks')}}})
    a.json('models/item/frosted_gingerbread_bricks.json', {'parent': ns('block/frosted_gingerbread_bricks')})

    # candy cane pillar
    tex = {'end': ns('block/candy_cane_block_top'), 'side': ns('block/candy_cane_block')}
    a.json('models/block/candy_cane_block.json', {'parent': 'minecraft:block/cube_column', 'textures': tex})
    a.json('models/block/candy_cane_block_horizontal.json', {'parent': 'minecraft:block/cube_column_horizontal', 'textures': tex})
    a.json('blockstates/candy_cane_block.json', {'variants': {
        'axis=y': {'model': ns('block/candy_cane_block')},
        'axis=z': {'model': ns('block/candy_cane_block_horizontal'), 'x': 90},
        'axis=x': {'model': ns('block/candy_cane_block_horizontal'), 'x': 90, 'y': 90}}})
    a.json('models/item/candy_cane_block.json', {'parent': ns('block/candy_cane_block')})

    # stairs / slab / wall
    bt = ns('block/gingerbread_bricks')
    for m, parent in (('', 'stairs'), ('_inner', 'inner_stairs'), ('_outer', 'outer_stairs')):
        a.json(f'models/block/gingerbread_brick_stairs{m}.json', {'parent': f'minecraft:block/{parent}',
                                                                  'textures': {'bottom': bt, 'top': bt, 'side': bt}})
    variants = {}
    for facing, y0 in (('east', 0), ('south', 90), ('west', 180), ('north', 270)):
        for half in ('bottom', 'top'):
            for shape in ('straight', 'inner_left', 'inner_right', 'outer_left', 'outer_right'):
                model = 'gingerbread_brick_stairs' + ('_inner' if 'inner' in shape else '_outer' if 'outer' in shape else '')
                y = y0
                if half == 'bottom' and shape.endswith('_left'):
                    y = (y0 - 90) % 360
                if half == 'top' and shape.endswith('_right'):
                    y = (y0 + 90) % 360
                v = {'model': ns(f'block/{model}')}
                if half == 'top':
                    v['x'] = 180
                if y:
                    v['y'] = y
                if y or half == 'top':
                    v['uvlock'] = True
                variants[f'facing={facing},half={half},shape={shape}'] = v
    a.json('blockstates/gingerbread_brick_stairs.json', {'variants': variants})
    a.json('models/item/gingerbread_brick_stairs.json', {'parent': ns('block/gingerbread_brick_stairs')})

    a.json('models/block/gingerbread_brick_slab.json', {'parent': 'minecraft:block/slab', 'textures': {'bottom': bt, 'top': bt, 'side': bt}})
    a.json('models/block/gingerbread_brick_slab_top.json', {'parent': 'minecraft:block/slab_top', 'textures': {'bottom': bt, 'top': bt, 'side': bt}})
    a.json('blockstates/gingerbread_brick_slab.json', {'variants': {
        'type=bottom': {'model': ns('block/gingerbread_brick_slab')},
        'type=top': {'model': ns('block/gingerbread_brick_slab_top')},
        'type=double': {'model': ns('block/gingerbread_bricks')}}})
    a.json('models/item/gingerbread_brick_slab.json', {'parent': ns('block/gingerbread_brick_slab')})

    for m, parent in (('_post', 'template_wall_post'), ('_side', 'template_wall_side'), ('_side_tall', 'template_wall_side_tall'), ('_inventory', 'wall_inventory')):
        a.json(f'models/block/gingerbread_brick_wall{m}.json', {'parent': f'minecraft:block/{parent}', 'textures': {'wall': bt}})
    mp = [{'when': {'up': 'true'}, 'apply': {'model': ns('block/gingerbread_brick_wall_post')}}]
    for side, y in (('north', 0), ('east', 90), ('south', 180), ('west', 270)):
        for h, m in (('low', '_side'), ('tall', '_side_tall')):
            apply = {'model': ns(f'block/gingerbread_brick_wall{m}'), 'uvlock': True}
            if y:
                apply['y'] = y
            mp.append({'when': {side: h}, 'apply': apply})
    a.json('blockstates/gingerbread_brick_wall.json', {'multipart': mp})
    a.json('models/item/gingerbread_brick_wall.json', {'parent': ns('block/gingerbread_brick_wall_inventory')})

    # ------------------------------------------------------------------ door & trapdoor (vanilla templates)
    src = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'source')
    with open(os.path.join(src, 'vanilla_oak_door.blockstate.json')) as fh:
        door_state = json.load(fh)
    with open(os.path.join(src, 'vanilla_oak_trapdoor.blockstate.json')) as fh:
        trap_state = json.load(fh)
    rename = lambda state, old, new: json.loads(json.dumps(state).replace(f'minecraft:block/{old}', ns(f'block/{new}')))
    a.json('blockstates/gingerbread_door.json', rename(door_state, 'oak_door', 'gingerbread_door'))
    a.json('blockstates/gingerbread_trapdoor.json', rename(trap_state, 'oak_trapdoor', 'gingerbread_trapdoor'))
    dt = {'top': ns('block/gingerbread_door_top'), 'bottom': ns('block/gingerbread_door_bottom')}
    for half in ('bottom', 'top'):
        for hinge in ('left', 'right'):
            for opened in ('', '_open'):
                m = f'door_{half}_{hinge}{opened}'
                a.json(f'models/block/gingerbread_{m}.json', {'parent': f'minecraft:block/{m}', 'render_type': 'minecraft:cutout', 'textures': dt})
    for part in ('bottom', 'top', 'open'):
        a.json(f'models/block/gingerbread_trapdoor_{part}.json', {'parent': f'minecraft:block/template_orientable_trapdoor_{part}',
                                                                  'render_type': 'minecraft:cutout',
                                                                  'textures': {'texture': ns('block/gingerbread_trapdoor')}})
    a.json('models/item/gingerbread_door.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': ns('item/gingerbread_door')}})
    a.json('models/item/gingerbread_trapdoor.json', {'parent': ns('block/gingerbread_trapdoor_bottom')})

    # ------------------------------------------------------------------ wall decorations (flat planes against the wall)
    def wall_plane(name, z=15.5, thick=0.0, light=False):
        el = {'from': [0, 0, z - thick], 'to': [16, 16, z], 'shade': not light,
              'faces': {'north': {'uv': [0, 0, 16, 16], 'texture': '#tex'},
                        'south': {'uv': [16, 0, 0, 16], 'texture': '#tex'}}}
        model = {'parent': 'minecraft:block/block', 'ambientocclusion': False, 'render_type': 'minecraft:cutout',
                 'textures': {'tex': ns(f'block/{name}'), 'particle': ns(f'block/{name}')}, 'elements': [el]}
        a.json(f'models/block/{name}.json', model)
        a.json(f'blockstates/{name}.json', {'variants': {
            f'facing={f}': ({'model': ns(f'block/{name}')} | ({'y': y} if y else {}))
            for f, y in (('north', 0), ('east', 90), ('south', 180), ('west', 270))}})
        a.json(f'models/item/{name}.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': ns(f'item/{name}')}})

    wall_plane('christmas_lights', 15.4, light=True)
    wall_plane('wreath', 15.0, thick=0.8)
    wall_plane('stocking', 15.2, thick=0.4)

    # star topper: crossed planes
    a.json('models/block/star_topper.json', {'parent': 'minecraft:block/cross', 'render_type': 'minecraft:cutout',
                                            'textures': {'cross': ns('block/star_topper')}})
    a.json('blockstates/star_topper.json', {'variants': {
        f'facing={f}': ({'model': ns('block/star_topper')} | ({'y': y} if y else {}))
        for f, y in (('north', 0), ('east', 90), ('south', 180), ('west', 270))}})
    a.json('models/item/star_topper.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': ns('item/star_topper')}})

    # ornaments: rounded bauble built from overlapping cubes, a gold cap and a hanging string
    def bauble(dy):
        def cube(f, t, tex='#ball'):
            return {'from': [f[0], f[1] + dy, f[2]], 'to': [t[0], t[1] + dy, t[2]],
                    'faces': {k: {'texture': tex} for k in ('north', 'south', 'east', 'west', 'up', 'down')}}
        els = [cube([5.5, 1.5, 5.5], [10.5, 6.5, 10.5]), cube([6.5, 1, 6.5], [9.5, 7, 9.5]),
               cube([5, 2.5, 6.5], [11, 5.5, 9.5]), cube([6.5, 2.5, 5], [9.5, 5.5, 11]),
               cube([7, 6.5, 7], [9, 8, 9], '#cap')]
        return els

    for c in ORNAMENTS:
        tex = {'ball': ns(f'block/{c}_ornament'), 'cap': ns('block/ornament_cap'), 'particle': ns(f'block/{c}_ornament'),
               'string': 'minecraft:block/chain'}
        hang = bauble(6)
        hang.append({'from': [7.5, 14, 8], 'to': [8.5, 16, 8], 'shade': False,
                     'faces': {'north': {'uv': [6, 0, 7, 2], 'texture': '#string'}, 'south': {'uv': [6, 0, 7, 2], 'texture': '#string'}}})
        hang.append({'from': [8, 14, 7.5], 'to': [8, 16, 8.5], 'shade': False,
                     'faces': {'east': {'uv': [6, 0, 7, 2], 'texture': '#string'}, 'west': {'uv': [6, 0, 7, 2], 'texture': '#string'}}})
        a.json(f'models/block/{c}_ornament_hanging.json', {'parent': 'minecraft:block/block', 'render_type': 'minecraft:cutout',
                                                           'textures': tex, 'elements': hang})
        a.json(f'models/block/{c}_ornament.json', {'parent': 'minecraft:block/block', 'render_type': 'minecraft:cutout',
                                                   'textures': tex, 'elements': bauble(-1.0)})
        a.json(f'blockstates/{c}_ornament.json', {'variants': {
            'hanging=true': {'model': ns(f'block/{c}_ornament_hanging')},
            'hanging=false': {'model': ns(f'block/{c}_ornament')}}})
        a.json(f'models/item/{c}_ornament.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': ns(f'item/{c}_ornament')}})

    # advent calendar
    a.json('models/block/advent_calendar.json', {'parent': 'minecraft:block/orientable', 'textures': {
        'front': ns('block/advent_calendar_front'), 'side': ns('block/advent_calendar_side'), 'top': ns('block/advent_calendar_top')}})
    a.json('blockstates/advent_calendar.json', {'variants': {
        f'facing={f}': ({'model': ns('block/advent_calendar')} | ({'y': y} if y else {}))
        for f, y in (('north', 0), ('east', 90), ('south', 180), ('west', 270))}})
    a.json('models/item/advent_calendar.json', {'parent': ns('block/advent_calendar')})

    # presents: a wrapped box with a bow made of two crossed planes
    for c in PRESENT_COLORS:
        box_faces = {f: {'uv': [1, 4, 15, 16], 'texture': '#side'} for f in ('north', 'south', 'east', 'west')}
        box_faces['up'] = {'uv': [1, 1, 15, 15], 'texture': '#top'}
        box_faces['down'] = {'uv': [1, 1, 15, 15], 'texture': '#top', 'cullface': 'down'}
        els = [{'from': [1, 0, 1], 'to': [15, 12, 15], 'faces': box_faces},
               {'from': [2, 12, 8], 'to': [14, 19.5, 8], 'shade': False,
                'faces': {'north': {'uv': [0, 0, 16, 10], 'texture': '#bow'}, 'south': {'uv': [0, 0, 16, 10], 'texture': '#bow'}}},
               {'from': [8, 12, 2], 'to': [8, 19.5, 14], 'shade': False,
                'faces': {'east': {'uv': [0, 0, 16, 10], 'texture': '#bow'}, 'west': {'uv': [0, 0, 16, 10], 'texture': '#bow'}}}]
        tex = {'side': ns(f'block/{c}_present_side'), 'top': ns(f'block/{c}_present_top'),
               'bow': ns(f'block/{c}_present_bow'), 'particle': ns(f'block/{c}_present_side')}
        a.json(f'models/block/{c}_present.json', {'parent': 'minecraft:block/block', 'render_type': 'minecraft:cutout',
                                                  'textures': tex, 'elements': els})
        a.json(f'blockstates/{c}_present.json', {'variants': {
            f'facing={f}': ({'model': ns(f'block/{c}_present')} | ({'y': y} if y else {}))
            for f, y in (('north', 0), ('east', 90), ('south', 180), ('west', 270))}})
        a.json(f'models/item/{c}_present.json', {'parent': ns(f'block/{c}_present')})

    # plants
    for s in range(4):
        a.json(f'models/block/ginger_crop_stage{s}.json', {'parent': 'minecraft:block/crop', 'render_type': 'minecraft:cutout',
                                                           'textures': {'crop': ns(f'block/ginger_crop_stage{s}')}})
    a.json('blockstates/ginger_crop.json', {'variants': {f'age={s}': {'model': ns(f'block/ginger_crop_stage{s}')} for s in range(4)}})
    a.json('models/block/wild_ginger.json', {'parent': 'minecraft:block/cross', 'render_type': 'minecraft:cutout',
                                            'textures': {'cross': ns('block/wild_ginger')}})
    a.json('blockstates/wild_ginger.json', {'variants': {'': {'model': ns('block/wild_ginger')}}})
    a.json('models/item/wild_ginger.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': ns('block/wild_ginger')}})

    # ------------------------------------------------------------------ item models
    for n in ('ginger_root', 'gingerbread_dough', 'gingerbread', 'gingerbread_cookie', 'icing', 'candy_cane', 'hot_cocoa',
              'naughty_coal', 'sleigh_bells', 'santa_hat', 'festive_sweater', 'music_disc_jingle_bells'):
        a.json(f'models/item/{n}.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': ns(f'item/{n}')}})
    a.json('models/item/candy_cane_sword.json', {'parent': 'minecraft:item/handheld', 'textures': {'layer0': ns('item/candy_cane_sword')}})
    for e in ('gingerbread_man', 'reindeer', 'krampus'):
        a.json(f'models/item/{e}_spawn_egg.json', {'parent': 'minecraft:item/template_spawn_egg'})

    # ------------------------------------------------------------------ lang + sounds
    a.json('lang/en_us.json', dict(sorted(LANG.items())))

    def snd(names, subtitle=None, stream=False, **kw):
        out = {'sounds': [({'name': ns(n), 'stream': True} if stream else ns(n)) for n in names]}
        if subtitle:
            out['subtitle'] = subtitle
        return out
    a.json('sounds.json', {
        'item.sleigh_bells.ring': snd(['sleigh_bells1', 'sleigh_bells2'], 'subtitles.christmas_ginger_house.item.sleigh_bells.ring'),
        'block.present.unwrap': snd(['present_unwrap1', 'present_unwrap2'], 'subtitles.christmas_ginger_house.block.present.unwrap'),
        'block.present.wrap': snd(['present_wrap'], 'subtitles.christmas_ginger_house.block.present.wrap'),
        'block.advent_calendar.open': snd(['advent_open'], 'subtitles.christmas_ginger_house.block.advent_calendar.open'),
        'entity.gingerbread_man.ambient': snd(['gingerbread_giggle1', 'gingerbread_giggle2'], 'subtitles.christmas_ginger_house.entity.gingerbread_man.ambient'),
        'entity.gingerbread_man.hurt': snd(['gingerbread_crunch1', 'gingerbread_crunch2'], 'subtitles.christmas_ginger_house.entity.gingerbread_man.hurt'),
        'entity.gingerbread_man.death': snd(['gingerbread_crumble'], 'subtitles.christmas_ginger_house.entity.gingerbread_man.death'),
        'entity.krampus.ambient': snd(['krampus_toll1', 'krampus_toll2'], 'subtitles.christmas_ginger_house.entity.krampus.ambient'),
        'entity.krampus.hurt': snd(['krampus_hurt1', 'krampus_hurt2'], 'subtitles.christmas_ginger_house.entity.krampus.hurt'),
        'entity.krampus.death': snd(['krampus_death'], 'subtitles.christmas_ginger_house.entity.krampus.death'),
        'entity.krampus.chains': snd(['krampus_chains1', 'krampus_chains2'], 'subtitles.christmas_ginger_house.entity.krampus.chains'),
        'music_disc.jingle_bells': snd(['records/jingle_bells'], stream=True),
    })

    # ------------------------------------------------------------------ loot tables
    def self_drop(name):
        d.json(f'loot_tables/blocks/{name}.json', {'type': 'minecraft:block', 'random_sequence': ns(f'blocks/{name}'), 'pools': [{
            'rolls': 1, 'bonus_rolls': 0, 'entries': [{'type': 'minecraft:item', 'name': ns(name)}],
            'conditions': [{'condition': 'minecraft:survives_explosion'}]}]})

    for n in ('gingerbread_block', 'gingerbread_bricks', 'gingerbread_brick_stairs', 'gingerbread_brick_wall',
              'frosted_gingerbread_bricks', 'gingerbread_trapdoor', 'icing_block', 'candy_cane_block', 'peppermint_block', 'christmas_lights',
              'wreath', 'stocking', 'star_topper', 'advent_calendar') + tuple(f'{c}_ornament' for c in ORNAMENTS):
        self_drop(n)
    d.json('loot_tables/blocks/gingerbread_door.json', {'type': 'minecraft:block', 'random_sequence': ns('blocks/gingerbread_door'), 'pools': [{
        'rolls': 1, 'bonus_rolls': 0, 'entries': [{'type': 'minecraft:item', 'name': ns('gingerbread_door'), 'conditions': [
            {'condition': 'minecraft:block_state_property', 'block': ns('gingerbread_door'), 'properties': {'half': 'lower'}}]}],
        'conditions': [{'condition': 'minecraft:survives_explosion'}]}]})
    d.json('loot_tables/blocks/gingerbread_brick_slab.json', {'type': 'minecraft:block', 'random_sequence': ns('blocks/gingerbread_brick_slab'), 'pools': [{
        'rolls': 1, 'bonus_rolls': 0, 'entries': [{'type': 'minecraft:item', 'name': ns('gingerbread_brick_slab'), 'functions': [
            {'function': 'minecraft:set_count', 'count': 2, 'add': False,
             'conditions': [{'condition': 'minecraft:block_state_property', 'block': ns('gingerbread_brick_slab'), 'properties': {'type': 'double'}}]},
            {'function': 'minecraft:explosion_decay'}]}]}]})
    for c in PRESENT_COLORS:
        d.json(f'loot_tables/blocks/{c}_present.json', {'type': 'minecraft:block', 'random_sequence': ns(f'blocks/{c}_present'), 'pools': [{
            'rolls': 1, 'bonus_rolls': 0, 'entries': [{'type': 'minecraft:item', 'name': ns(f'{c}_present'), 'functions': [
                {'function': 'minecraft:copy_name', 'source': 'block_entity'},
                {'function': 'minecraft:copy_nbt', 'source': 'block_entity', 'ops': [
                    {'source': 'Items', 'target': 'BlockEntityTag.Items', 'op': 'replace'},
                    {'source': 'LootTable', 'target': 'BlockEntityTag.LootTable', 'op': 'replace'},
                    {'source': 'LootTableSeed', 'target': 'BlockEntityTag.LootTableSeed', 'op': 'replace'},
                    {'source': 'Sender', 'target': 'BlockEntityTag.Sender', 'op': 'replace'}]}]}]}]})
    ripe = {'condition': 'minecraft:block_state_property', 'block': ns('ginger_crop'), 'properties': {'age': '3'}}
    d.json('loot_tables/blocks/ginger_crop.json', {'type': 'minecraft:block', 'random_sequence': ns('blocks/ginger_crop'), 'pools': [
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [{'type': 'minecraft:item', 'name': ns('ginger_root')}]},
        {'rolls': 1, 'bonus_rolls': 0, 'conditions': [ripe], 'entries': [{'type': 'minecraft:item', 'name': ns('ginger_root'), 'functions': [
            {'function': 'minecraft:apply_bonus', 'enchantment': 'minecraft:fortune', 'formula': 'minecraft:binomial_with_bonus_count',
             'parameters': {'extra': 3, 'probability': 0.5714286}}]}]}],
        'functions': [{'function': 'minecraft:explosion_decay'}]})
    d.json('loot_tables/blocks/wild_ginger.json', {'type': 'minecraft:block', 'random_sequence': ns('blocks/wild_ginger'), 'pools': [{
        'rolls': 1, 'bonus_rolls': 0, 'entries': [{'type': 'minecraft:alternatives', 'children': [
            {'type': 'minecraft:item', 'name': ns('wild_ginger'), 'conditions': [{'condition': 'minecraft:match_tool', 'predicate': {'items': ['minecraft:shears']}}]},
            {'type': 'minecraft:item', 'name': ns('ginger_root'), 'functions': [
                {'function': 'minecraft:set_count', 'count': {'type': 'minecraft:uniform', 'min': 1, 'max': 2}},
                {'function': 'minecraft:apply_bonus', 'enchantment': 'minecraft:fortune', 'formula': 'minecraft:uniform_bonus_count', 'parameters': {'bonusMultiplier': 1}},
                {'function': 'minecraft:explosion_decay'}]}]}]}]})

    def item(name, weight=1, lo=1, hi=1, extra=None):
        e = {'type': 'minecraft:item', 'name': name if ':' in name else ns(name), 'weight': weight}
        fns = []
        if hi > 1 or lo != 1:
            fns.append({'function': 'minecraft:set_count', 'count': {'type': 'minecraft:uniform', 'min': lo, 'max': hi}})
        if extra:
            fns.extend(extra)
        if fns:
            e['functions'] = fns
        return e

    def sealed(color, table, weight=1):
        return item(f'{color}_present', weight, extra=[{'function': 'minecraft:set_nbt',
                                                       'tag': '{BlockEntityTag:{LootTable:"%s"}}' % ns(table)}])

    looting = lambda lo, hi: [{'function': 'minecraft:looting_enchant', 'count': {'type': 'minecraft:uniform', 'min': lo, 'max': hi}}]

    d.json('loot_tables/entities/gingerbread_man.json', {'type': 'minecraft:entity', 'random_sequence': ns('entities/gingerbread_man'), 'pools': [
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [item('gingerbread', 1, 1, 2, looting(0, 1))]},
        {'rolls': 1, 'bonus_rolls': 0, 'conditions': [{'condition': 'minecraft:random_chance_with_looting', 'chance': 0.35, 'looting_multiplier': 0.1}],
         'entries': [item('icing')]}]})
    d.json('loot_tables/entities/reindeer.json', {'type': 'minecraft:entity', 'random_sequence': ns('entities/reindeer'), 'pools': [
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [item('minecraft:leather', 1, 0, 2, looting(0, 1))]}]})
    d.json('loot_tables/entities/krampus.json', {'type': 'minecraft:entity', 'random_sequence': ns('entities/krampus'), 'pools': [
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [item('naughty_coal', 1, 2, 4, looting(0, 2))]},
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [item('minecraft:chain', 1, 1, 3)]},
        {'rolls': 1, 'bonus_rolls': 0, 'conditions': [{'condition': 'minecraft:killed_by_player'},
                                                       {'condition': 'minecraft:random_chance_with_looting', 'chance': 0.3, 'looting_multiplier': 0.1}],
         'entries': [sealed('gold', 'gifts/stolen')]}]})

    treats = [item('candy_cane', 6, 1, 3), item('gingerbread_cookie', 6, 1, 3), item('hot_cocoa', 3), item('gingerbread', 4, 2, 4),
              item('minecraft:cookie', 3, 2, 6), item('minecraft:pumpkin_pie', 1)]
    d.json('loot_tables/gifts/common.json', {'type': 'minecraft:chest', 'random_sequence': ns('gifts/common'), 'pools': [
        {'rolls': {'type': 'minecraft:uniform', 'min': 2, 'max': 3}, 'bonus_rolls': 0, 'entries': treats},
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [
            item('minecraft:emerald', 8, 2, 5), item('minecraft:gold_ingot', 6, 1, 3), item('minecraft:iron_ingot', 8, 2, 5),
            item('minecraft:diamond', 2), item('minecraft:firework_rocket', 6, 3, 6, [{'function': 'minecraft:set_nbt', 'tag': '{Fireworks:{Flight:2b,Explosions:[{Type:1b,Colors:[I;14427686,3838501,16766280],FadeColors:[I;16777215]}]}}'}]),
            item('sleigh_bells', 3), item('santa_hat', 3), item('festive_sweater', 3), item('music_disc_jingle_bells', 2),
            item('minecraft:white_wool', 4, 2, 6), item('minecraft:red_wool', 4, 2, 6), item('minecraft:snowball', 4, 8, 16)]}]})
    d.json('loot_tables/gifts/rare.json', {'type': 'minecraft:chest', 'random_sequence': ns('gifts/rare'), 'pools': [
        {'rolls': 2, 'bonus_rolls': 0, 'entries': treats},
        {'rolls': {'type': 'minecraft:uniform', 'min': 2, 'max': 3}, 'bonus_rolls': 0, 'entries': [
            item('minecraft:diamond', 6, 1, 3), item('minecraft:emerald', 8, 4, 9), item('minecraft:golden_apple', 5, 1, 2),
            item('minecraft:enchanted_golden_apple', 1), item('candy_cane_sword', 5), item('santa_hat', 4), item('festive_sweater', 4),
            item('music_disc_jingle_bells', 4), item('sleigh_bells', 4),
            item('minecraft:book', 6, extra=[{'function': 'minecraft:enchant_randomly'}]),
            item('minecraft:experience_bottle', 5, 3, 8)]}]})
    d.json('loot_tables/gifts/stolen.json', {'type': 'minecraft:chest', 'random_sequence': ns('gifts/stolen'), 'pools': [
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [{'type': 'minecraft:loot_table', 'name': ns('gifts/rare')}]},
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [item('naughty_coal', 1, 1, 3)]}]})
    d.json('loot_tables/chests/gingerbread_house.json', {'type': 'minecraft:chest', 'random_sequence': ns('chests/gingerbread_house'), 'pools': [
        {'rolls': {'type': 'minecraft:uniform', 'min': 3, 'max': 6}, 'bonus_rolls': 0, 'entries': [
            item('gingerbread', 8, 2, 5), item('gingerbread_dough', 6, 2, 4), item('gingerbread_cookie', 6, 1, 4), item('icing', 6, 2, 6),
            item('ginger_root', 8, 2, 5), item('candy_cane', 6, 2, 5), item('hot_cocoa', 3), item('minecraft:sugar', 6, 3, 8),
            item('minecraft:cocoa_beans', 4, 2, 5), item('minecraft:wheat', 5, 3, 7), item('minecraft:egg', 4, 1, 4),
            item('minecraft:milk_bucket', 2), item('minecraft:cake', 1)]},
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [
            {'type': 'minecraft:empty', 'weight': 6}, sealed('red', 'gifts/common', 2), sealed('green', 'gifts/common', 2),
            item('sleigh_bells', 1), item('santa_hat', 1), item('advent_calendar', 1)]}]})
    d.json('loot_tables/gameplay/advent_calendar/door.json', {'type': 'minecraft:gift', 'random_sequence': ns('gameplay/advent_calendar/door'), 'pools': [
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [
            item('candy_cane', 10, 2, 4), item('gingerbread_cookie', 10, 2, 3), item('hot_cocoa', 6), item('minecraft:emerald', 6, 1, 3),
            item('minecraft:gold_nugget', 6, 4, 9), item('minecraft:cookie', 8, 3, 6), item('icing', 5, 2, 4),
            item('minecraft:firework_rocket', 5, 3, 5), item('minecraft:iron_ingot', 4, 2, 4),
            sealed('red', 'gifts/common', 3), sealed('green', 'gifts/common', 3), sealed('blue', 'gifts/common', 3)]}]})
    d.json('loot_tables/gameplay/advent_calendar/christmas_eve.json', {'type': 'minecraft:gift', 'random_sequence': ns('gameplay/advent_calendar/christmas_eve'), 'pools': [
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [sealed('gold', 'gifts/rare')]},
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [item('candy_cane', 1, 3, 5)]},
        {'rolls': 1, 'bonus_rolls': 0, 'entries': [item('music_disc_jingle_bells', 1), item('santa_hat', 1), item('festive_sweater', 1)]}]})

    # ------------------------------------------------------------------ recipes
    def shaped(name, pattern, key, result, count=1, group=None):
        r = {'type': 'minecraft:crafting_shaped', 'category': 'misc', 'pattern': pattern,
             'key': {k: ({'item': v} if not v.startswith('#') else {'tag': v[1:]}) for k, v in key.items()},
             'result': {'item': result if ':' in result else ns(result), 'count': count}}
        if group:
            r['group'] = group
        d.json(f'recipes/{name}.json', r)

    def shapeless(name, ings, result, count=1, category='misc'):
        d.json(f'recipes/{name}.json', {'type': 'minecraft:crafting_shapeless', 'category': category,
                                        'ingredients': [({'item': i} if not i.startswith('#') else {'tag': i[1:]}) for i in ings],
                                        'result': {'item': result if ':' in result else ns(result), 'count': count}})

    def cook(name, ing, result, xp=0.35):
        for kind, time in (('smelting', 200), ('smoking', 100), ('campfire_cooking', 600)):
            d.json(f'recipes/{name}_from_{kind}.json', {'type': f'minecraft:{kind}', 'category': 'food', 'ingredient': {'item': ing},
                                                        'result': ns(result), 'experience': xp, 'cookingtime': time})

    def cut(name, ing, result, count=1):
        d.json(f'recipes/{name}_stonecutting.json', {'type': 'minecraft:stonecutting', 'ingredient': {'item': ns(ing)},
                                                     'result': ns(result), 'count': count})

    G = lambda n: ns(n)
    shapeless('gingerbread_dough', ['minecraft:wheat', 'minecraft:wheat', G('ginger_root'), 'minecraft:sugar'], 'gingerbread_dough', 2)
    cook('gingerbread', G('gingerbread_dough'), 'gingerbread')
    shapeless('icing', ['minecraft:sugar', 'minecraft:sugar', 'minecraft:egg'], 'icing', 3)
    shapeless('gingerbread_cookie', [G('gingerbread'), G('icing')], 'gingerbread_cookie', 2)
    shapeless('candy_cane', ['minecraft:sugar', 'minecraft:sugar', 'minecraft:red_dye'], 'candy_cane', 2)
    shapeless('hot_cocoa', ['minecraft:cocoa_beans', 'minecraft:milk_bucket', 'minecraft:sugar', 'minecraft:bowl'], 'hot_cocoa', 1)
    shaped('gingerbread_block', ['GG', 'GG'], {'G': G('gingerbread')}, 'gingerbread_block')
    shaped('gingerbread_bricks', ['GG', 'GG'], {'G': G('gingerbread_block')}, 'gingerbread_bricks', 4)
    shaped('gingerbread_brick_stairs', ['B  ', 'BB ', 'BBB'], {'B': G('gingerbread_bricks')}, 'gingerbread_brick_stairs', 4)
    shaped('gingerbread_brick_slab', ['BBB'], {'B': G('gingerbread_bricks')}, 'gingerbread_brick_slab', 6)
    shaped('gingerbread_brick_wall', ['BBB', 'BBB'], {'B': G('gingerbread_bricks')}, 'gingerbread_brick_wall', 6)
    shaped('frosted_gingerbread_bricks', ['I', 'B'], {'I': G('icing'), 'B': G('gingerbread_bricks')}, 'frosted_gingerbread_bricks')
    shaped('gingerbread_door', ['GG', 'GG', 'GG'], {'G': G('gingerbread_block')}, 'gingerbread_door', 3)
    shaped('gingerbread_trapdoor', ['GGG', 'GGG'], {'G': G('gingerbread')}, 'gingerbread_trapdoor', 2)
    shaped('icing_block', ['II', 'II'], {'I': G('icing')}, 'icing_block')
    shapeless('icing_from_icing_block', [G('icing_block')], 'icing', 4)
    shaped('candy_cane_block', ['CC', 'CC'], {'C': G('candy_cane')}, 'candy_cane_block')
    shaped('peppermint_block', ['SRS', 'RSR', 'SRS'], {'S': 'minecraft:sugar', 'R': 'minecraft:red_dye'}, 'peppermint_block', 4)
    for n in ('gingerbread_bricks', 'gingerbread_brick_stairs', 'gingerbread_brick_wall'):
        cut(n, 'gingerbread_block', n)
    cut('gingerbread_brick_slab', 'gingerbread_block', 'gingerbread_brick_slab', 2)
    cut('gingerbread_brick_stairs_from_bricks', 'gingerbread_bricks', 'gingerbread_brick_stairs')
    cut('gingerbread_brick_slab_from_bricks', 'gingerbread_bricks', 'gingerbread_brick_slab', 2)
    cut('gingerbread_brick_wall_from_bricks', 'gingerbread_bricks', 'gingerbread_brick_wall')
    shaped('christmas_lights', ['SSS', 'RYB'], {'S': 'minecraft:string', 'R': 'minecraft:red_dye', 'Y': 'minecraft:glowstone_dust',
                                                'B': 'minecraft:blue_dye'}, 'christmas_lights', 6)
    shaped('wreath', ['LLL', 'L L', 'LRL'], {'L': 'minecraft:spruce_leaves', 'R': 'minecraft:red_dye'}, 'wreath')
    shaped('stocking', ['WW ', 'RR ', 'RRR'], {'W': 'minecraft:white_wool', 'R': 'minecraft:red_wool'}, 'stocking')
    shaped('star_topper', [' N ', 'NGN', 'N N'], {'N': 'minecraft:gold_nugget', 'G': 'minecraft:glowstone_dust'}, 'star_topper')
    for c, dye in (('red', 'minecraft:red_dye'), ('gold', 'minecraft:yellow_dye'), ('blue', 'minecraft:blue_dye')):
        shaped(f'{c}_ornament', [' N ', 'GDG', ' G '], {'N': 'minecraft:gold_nugget', 'G': 'minecraft:glass_pane', 'D': dye}, f'{c}_ornament', 4)
    for c, dye in (('red', 'minecraft:red_dye'), ('green', 'minecraft:green_dye'), ('blue', 'minecraft:blue_dye'), ('gold', 'minecraft:gold_nugget')):
        shaped(f'{c}_present', [' S ', 'PDP', 'PPP'], {'S': 'minecraft:string', 'P': 'minecraft:paper', 'D': dye}, f'{c}_present', 1, 'presents')
    shaped('advent_calendar', ['SSS', 'SCS', 'SSS'], {'S': 'minecraft:spruce_planks', 'C': G('gingerbread_cookie')}, 'advent_calendar')
    shaped('candy_cane_sword', ['C', 'C', 'S'], {'C': G('candy_cane_block'), 'S': 'minecraft:stick'}, 'candy_cane_sword')
    shaped('sleigh_bells', [' S ', 'GLG', ' G '], {'S': 'minecraft:string', 'G': 'minecraft:gold_nugget', 'L': 'minecraft:leather'}, 'sleigh_bells')
    shaped('santa_hat', [' RW', 'RRR', 'W W'], {'R': 'minecraft:red_wool', 'W': 'minecraft:white_wool'}, 'santa_hat')
    shaped('festive_sweater', ['R R', 'GWG', 'RRR'], {'R': 'minecraft:red_wool', 'G': 'minecraft:green_wool', 'W': 'minecraft:white_wool'}, 'festive_sweater')
    shapeless('ginger_root_from_wild_ginger', [G('wild_ginger')], 'ginger_root', 2)

    # ------------------------------------------------------------------ tags
    def tag(w, path, values):
        w.json(f'tags/{path}.json', {'replace': False, 'values': [v if ':' in v or v.startswith('#') else ns(v) for v in values]})

    bricks = ['gingerbread_block', 'gingerbread_bricks', 'gingerbread_brick_stairs', 'gingerbread_brick_slab', 'gingerbread_brick_wall',
              'frosted_gingerbread_bricks', 'gingerbread_door', 'gingerbread_trapdoor']
    tag(mc, 'blocks/mineable/axe', bricks + ['advent_calendar'] + [f'{c}_present' for c in PRESENT_COLORS])
    tag(mc, 'blocks/mineable/pickaxe', ['candy_cane_block', 'peppermint_block', 'star_topper'])
    tag(mc, 'blocks/mineable/shovel', ['icing_block'])
    tag(mc, 'blocks/mineable/hoe', ['wreath'])
    tag(mc, 'blocks/stairs', ['gingerbread_brick_stairs'])
    tag(mc, 'blocks/slabs', ['gingerbread_brick_slab'])
    tag(mc, 'blocks/walls', ['gingerbread_brick_wall'])
    tag(mc, 'blocks/wooden_doors', ['gingerbread_door'])
    tag(mc, 'blocks/wooden_trapdoors', ['gingerbread_trapdoor'])
    tag(mc, 'items/wooden_doors', ['gingerbread_door'])
    tag(mc, 'items/wooden_trapdoors', ['gingerbread_trapdoor'])
    tag(mc, 'blocks/crops', ['ginger_crop'])
    tag(mc, 'blocks/bee_growables', ['ginger_crop'])
    tag(mc, 'blocks/maintains_farmland', ['ginger_crop'])
    tag(mc, 'blocks/enderman_holdable', ['wild_ginger'])
    tag(mc, 'blocks/replaceable_by_trees', ['wild_ginger'])
    tag(mc, 'items/stairs', ['gingerbread_brick_stairs'])
    tag(mc, 'items/slabs', ['gingerbread_brick_slab'])
    tag(mc, 'items/walls', ['gingerbread_brick_wall'])
    tag(mc, 'items/freeze_immune_wearables', ['santa_hat', 'festive_sweater'])
    tag(mc, 'items/music_discs', ['music_disc_jingle_bells'])
    tag(mc, 'items/creeper_drop_music_discs', ['music_disc_jingle_bells'])
    tag(d, 'blocks/festive_mobs_spawnable_on', ['minecraft:grass_block', 'minecraft:snow_block', 'minecraft:snow', 'minecraft:podzol',
                                                'minecraft:dirt', 'minecraft:coarse_dirt', 'minecraft:moss_block', 'minecraft:powder_snow'])
    tag(d, 'items/presents', [f'{c}_present' for c in PRESENT_COLORS])
    tag(d, 'blocks/presents', [f'{c}_present' for c in PRESENT_COLORS])
    snowy = ['minecraft:snowy_plains', 'minecraft:snowy_taiga', 'minecraft:grove']
    tag(d, 'worldgen/biome/spawns_gingerbread_men', snowy + ['minecraft:snowy_slopes'])
    tag(d, 'worldgen/biome/spawns_reindeer', snowy + ['minecraft:taiga', 'minecraft:old_growth_spruce_taiga'])
    tag(d, 'worldgen/biome/spawns_krampus', snowy + ['minecraft:snowy_slopes', 'minecraft:ice_spikes', 'minecraft:frozen_peaks', 'minecraft:jagged_peaks'])
    tag(d, 'worldgen/biome/has_wild_ginger', ['minecraft:taiga', 'minecraft:snowy_taiga', 'minecraft:old_growth_spruce_taiga',
                                              'minecraft:old_growth_pine_taiga', 'minecraft:grove'])
    tag(d, 'worldgen/biome/has_structure/christmas_ginger_house', snowy + ['minecraft:snowy_slopes', 'minecraft:plains'])
    tag(d, 'worldgen/biome/has_structure/christmas_tree', snowy + ['minecraft:taiga', 'minecraft:snowy_slopes'])

    # ------------------------------------------------------------------ worldgen
    d.json('worldgen/configured_feature/patch_wild_ginger.json', {'type': 'minecraft:random_patch', 'config': {
        'tries': 24, 'xz_spread': 6, 'y_spread': 2, 'feature': {
            'feature': {'type': 'minecraft:simple_block', 'config': {'to_place': {'type': 'minecraft:simple_state_provider',
                                                                                  'state': {'Name': ns('wild_ginger')}}}},
            'placement': [{'type': 'minecraft:block_predicate_filter', 'predicate': {'type': 'minecraft:all_of', 'predicates': [
                {'type': 'minecraft:matching_blocks', 'blocks': 'minecraft:air'},
                {'type': 'minecraft:would_survive', 'state': {'Name': ns('wild_ginger')}}]}}]}}})
    d.json('worldgen/placed_feature/patch_wild_ginger.json', {'feature': ns('patch_wild_ginger'), 'placement': [
        {'type': 'minecraft:rarity_filter', 'chance': 5}, {'type': 'minecraft:in_square'},
        {'type': 'minecraft:heightmap', 'heightmap': 'MOTION_BLOCKING'}, {'type': 'minecraft:biome'}]})

    def structure(name, pool, size_y_proj='WORLD_SURFACE_WG', adapt='beard_thin', spacing=40, sep=16, salt=0):
        d.json(f'worldgen/structure/{name}.json', {
            'type': 'minecraft:jigsaw', 'start_pool': ns(name), 'size': 1, 'max_distance_from_center': 80,
            'biomes': f'#{ns("has_structure/" + name)}', 'step': 'surface_structures', 'spawn_overrides': {},
            'terrain_adaptation': adapt, 'start_height': {'absolute': 0}, 'project_start_to_heightmap': size_y_proj,
            'use_expansion_hack': False})
        d.json(f'worldgen/template_pool/{name}.json', {'name': ns(name), 'fallback': 'minecraft:empty', 'elements': [
            {'weight': weight, 'element': {'element_type': 'minecraft:single_pool_element', 'location': ns(location),
                                           'projection': 'rigid', 'processors': {'processors': [{'processor_type': 'minecraft:block_ignore',
                                                                                                  'blocks': [{'Name': 'minecraft:structure_block'}]}]}}}
            for location, weight in pool]})
        d.json(f'worldgen/structure_set/{name}.json', {'structures': [{'structure': ns(name), 'weight': 1}],
                                                       'placement': {'type': 'minecraft:random_spread', 'spacing': spacing,
                                                                     'separation': sep, 'salt': salt}})

    structure('christmas_ginger_house', [('house_ginger', 1)], adapt='beard_thin', spacing=56, sep=24, salt=49742572)
    structure('christmas_tree', [('christmas_tree', 3), ('christmas_tree_gold', 1)], adapt='beard_thin', spacing=28, sep=10, salt=1225202512)

    # Forge biome modifiers (Fabric does the same in code)
    fg.json('forge/biome_modifier/add_gingerbread_men.json', {'type': 'forge:add_spawns', 'biomes': f'#{ns("spawns_gingerbread_men")}',
                                                              'spawners': {'type': ns('gingerbread_man'), 'weight': 6, 'minCount': 2, 'maxCount': 3}})
    fg.json('forge/biome_modifier/add_reindeer.json', {'type': 'forge:add_spawns', 'biomes': f'#{ns("spawns_reindeer")}',
                                                       'spawners': {'type': ns('reindeer'), 'weight': 8, 'minCount': 2, 'maxCount': 4}})
    fg.json('forge/biome_modifier/add_krampus.json', {'type': 'forge:add_spawns', 'biomes': f'#{ns("spawns_krampus")}',
                                                      'spawners': {'type': ns('krampus'), 'weight': 4, 'minCount': 1, 'maxCount': 1}})
    fg.json('forge/biome_modifier/add_wild_ginger.json', {'type': 'forge:add_features', 'biomes': f'#{ns("has_wild_ginger")}',
                                                          'features': ns('patch_wild_ginger'), 'step': 'vegetal_decoration'})

    # ------------------------------------------------------------------ advancements
    def adv(name, parent, icon, criteria, frame='task', hidden=False, rewards=None, show=True):
        data = {'display': {'icon': {'item': icon if ':' in icon else ns(icon)},
                            'title': {'translate': f'advancements.{MOD}.{name}.title'},
                            'description': {'translate': f'advancements.{MOD}.{name}.description'},
                            'frame': frame, 'show_toast': show, 'announce_to_chat': show, 'hidden': hidden},
                'criteria': criteria, 'requirements': [[k] for k in criteria] if name != 'root' else [list(criteria)]}
        if parent:
            data['parent'] = ns(parent)
        else:
            data['display']['background'] = 'minecraft:textures/block/snow.png'
        if rewards:
            data['rewards'] = rewards
        d.json(f'advancements/{name}.json', data)

    inv = lambda *items: {'trigger': 'minecraft:inventory_changed', 'conditions': {'items': [{'items': [ns(i) for i in items]}]}}
    impossible = lambda n: {n: {'trigger': 'minecraft:impossible'}}
    adv('root', None, 'gingerbread_cookie', {'ginger_root': inv('ginger_root'), 'gingerbread': inv('gingerbread'),
                                             'cookie': inv('gingerbread_cookie'), 'candy_cane': inv('candy_cane')}, show=False)
    adv('home_sweet_home', 'root', 'frosted_gingerbread_bricks', {'entered': {'trigger': 'minecraft:location', 'conditions': {
        'player': [{'condition': 'minecraft:entity_properties', 'entity': 'this', 'predicate': {'location': {'structure': ns('christmas_ginger_house')}}}]}}})
    adv('sweet_tooth', 'root', 'candy_cane', {'ate': {'trigger': 'minecraft:consume_item', 'conditions': {'item': {'items': [ns('candy_cane')]}}}})
    adv('warm_and_cozy', 'sweet_tooth', 'hot_cocoa', {'drank': {'trigger': 'minecraft:consume_item', 'conditions': {'item': {'items': [ns('hot_cocoa')]}}}})
    adv('unwrap_present', 'root', 'red_present', impossible('unwrap_present'))
    adv('open_advent_door', 'unwrap_present', 'advent_calendar', impossible('open_advent_door'))
    adv('christmas_eve', 'open_advent_door', 'star_topper', impossible('christmas_eve'), frame='challenge',
        rewards={'experience': 250})
    adv('deck_the_halls', 'root', 'star_topper', {'placed': {'trigger': 'minecraft:placed_block', 'conditions': {
        'location': [{'condition': 'minecraft:match_tool', 'predicate': {'items': [ns('star_topper')]}}]}}})
    adv('dress_the_part', 'deck_the_halls', 'santa_hat', {'outfit': {'trigger': 'minecraft:inventory_changed', 'conditions': {
        'items': [{'items': [ns('santa_hat')]}, {'items': [ns('festive_sweater')]}]}}})
    adv('reindeer_games', 'root', 'sleigh_bells', {'bred': {'trigger': 'minecraft:bred_animals', 'conditions': {
        'child': [{'condition': 'minecraft:entity_properties', 'entity': 'this', 'predicate': {'type': ns('reindeer')}}]}}})
    adv('naughty_list', 'root', 'naughty_coal', {'killed': {'trigger': 'minecraft:player_killed_entity', 'conditions': {
        'entity': [{'condition': 'minecraft:entity_properties', 'entity': 'this', 'predicate': {'type': ns('krampus')}}]}}}, frame='goal')

    if modern:
        # 1.21 jukebox songs are data, and NeoForge reads fuel and compost values from data maps.
        d.json('jukebox_song/jingle_bells.json', {'sound_event': ns('music_disc.jingle_bells'),
                                                  'description': {'translate': f'jukebox_song.{MOD}.jingle_bells'},
                                                  'length_in_seconds': 64.0, 'comparator_output': 12})
        nf = Writer(os.path.join(loader_res, 'data', 'neoforge'))
        nf.json('data_maps/item/furnace_fuels.json', {'values': {ns('naughty_coal'): {'burn_time': 3200}}})
        nf.json('data_maps/item/compostables.json', {'values': {
            ns('ginger_root'): {'chance': 0.3}, ns('gingerbread_dough'): {'chance': 0.5}, ns('gingerbread'): {'chance': 0.65},
            ns('gingerbread_cookie'): {'chance': 0.85}, ns('wild_ginger'): {'chance': 0.65}}})
        lang = dict(LANG)
        lang[f'jukebox_song.{MOD}.jingle_bells'] = 'J. Pierpont - Jingle Bells (Music Box)'
        a.json('lang/en_us.json', dict(sorted(lang.items())))
    return a.count + d.count + mc.count + fg.count
