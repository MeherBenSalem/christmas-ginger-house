"""Per-Minecraft-version translation of generated resources.

resources.py authors everything in the 1.20.1 format. For newer targets, Writer passes each
file through translate(), which renames data folders and rewrites the JSON shapes that
Mojang changed (1.20.5 item components, 1.21 singular folders and enchantment-driven loot).
"""

MOD = 'christmas_ginger_house'

TARGETS = {
    '1.20.1': {'pack_format': 15, 'data_version': 3465, 'singular': False, 'components': False,
               'loader_data': 'forge'},
    '1.21.1': {'pack_format': 48, 'data_version': 3955, 'singular': True, 'components': True,
               'loader_data': 'neoforge'},
}

FOLDERS = [('loot_tables/', 'loot_table/'), ('recipes/', 'recipe/'), ('advancements/', 'advancement/'),
           ('tags/blocks/', 'tags/block/'), ('tags/items/', 'tags/item/'), ('tags/entity_types/', 'tags/entity_type/'),
           ('structures/', 'structure/')]


def translate_path(rel, target):
    t = TARGETS[target]
    if t['singular']:
        for old, new in FOLDERS:
            rel = rel.replace(old, new)
    if t['loader_data'] == 'neoforge':
        rel = rel.replace('forge/biome_modifier/', 'neoforge/biome_modifier/')
    return rel


def _walk(node, fn):
    if isinstance(node, dict):
        node = fn(node)
        return {k: _walk(v, fn) for k, v in node.items()}
    if isinstance(node, list):
        return [_walk(v, fn) for v in node]
    return node


FIREWORK = {'minecraft:fireworks': {'flight_duration': 2, 'explosions': [
    {'shape': 'large_ball', 'colors': [14427686, 3838501, 16766280], 'fade_colors': [16777215]}]}}


def _components(node):
    fn = node.get('function')
    if fn == 'minecraft:copy_nbt':
        return {'function': 'minecraft:copy_components', 'source': 'block_entity',
                'include': ['minecraft:container', 'minecraft:container_loot', f'{MOD}:sender']}
    if fn == 'minecraft:set_nbt':
        tag = node['tag']
        if 'LootTable' in tag:
            table = tag.split('LootTable:"')[1].split('"')[0]
            return {'function': 'minecraft:set_components', 'components': {'minecraft:container_loot': {'loot_table': table}}}
        if 'Fireworks' in tag:
            return {'function': 'minecraft:set_components', 'components': FIREWORK}
        raise ValueError('untranslated set_nbt ' + tag)
    if node.get('type') == 'minecraft:loot_table' and 'name' in node:
        node = dict(node)
        node['value'] = node.pop('name')
        return node
    if fn == 'minecraft:looting_enchant':
        return {'function': 'minecraft:enchanted_count_increase', 'enchantment': 'minecraft:looting', 'count': node['count']}
    if node.get('condition') == 'minecraft:random_chance_with_looting':
        chance, per = node['chance'], node['looting_multiplier']
        return {'condition': 'minecraft:random_chance_with_enchanted_bonus', 'enchantment': 'minecraft:looting',
                'unenchanted_chance': chance,
                'enchanted_chance': {'type': 'minecraft:linear', 'base': chance + per, 'per_level_above_first': per}}
    return node


def translate(rel, data, target):
    t = TARGETS[target]
    if not t['components']:
        return data
    if '/recipes/' in '/' + rel or rel.startswith('recipes/'):
        res = data.get('result')
        if isinstance(res, dict) and 'item' in res:
            data['result'] = {'id': res['item'], 'count': res.get('count', 1)}
        elif isinstance(res, str):
            data['result'] = {'id': res, 'count': data.pop('count', 1)}
    if rel.startswith('loot_tables/'):
        data = _walk(data, _components)
    if rel.startswith('advancements/'):
        icon = data.get('display', {}).get('icon')
        if icon and 'item' in icon:
            data['display']['icon'] = {'id': icon['item']}

        def loc(node):
            if 'location' in node and isinstance(node['location'], dict) and 'structure' in node['location']:
                node = dict(node)
                node['location'] = {'structures': node['location']['structure']}
            return node
        data = _walk(data, loc)
    if rel.startswith('forge/biome_modifier/') and t['loader_data'] == 'neoforge':
        data['type'] = data['type'].replace('forge:', 'neoforge:')
    return data
