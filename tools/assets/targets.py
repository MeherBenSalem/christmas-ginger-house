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
    # 26.x: string ingredients, item model definitions, equipment assets, no random_patch.
    # 26.2 also renamed the entity predicate's "type" field to "minecraft:entity_type".
    '26.2': {'pack_format': None, 'data_version': 4903, 'singular': True, 'components': True,
             'loader_data': 'neoforge', 'v26': True, 'entity_type_key': True},
    # 26.3: single "condition"/"modifier" objects, block states as {id, properties}, flat feature
    # configs in worldgen/feature, spawner counts as int providers (see upgrade_263). Structures keep
    # the 26.2 DataVersion; the game's data fixers read it.
    '26.3': {'pack_format': None, 'data_version': 4903, 'singular': True, 'components': True,
             'loader_data': 'neoforge', 'v26': True, 'entity_type_key': True, 'v263': True},
}


def v26(target):
    return TARGETS[target].get('v26', False)


def _ingredient(value):
    if isinstance(value, dict):
        if 'item' in value:
            return value['item']
        if 'tag' in value:
            return '#' + value['tag']
    return value

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
    if t.get('v263'):
        rel = rel.replace('worldgen/configured_feature/', 'worldgen/feature/')
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


RENAMED_26 = {'minecraft:chain': 'minecraft:iron_chain'}


def _rename_26(node):
    if isinstance(node, dict):
        return {k: _rename_26(v) for k, v in node.items()}
    if isinstance(node, list):
        return [_rename_26(v) for v in node]
    if isinstance(node, str):
        return RENAMED_26.get(node, node)
    return node


def translate(rel, data, target):
    t = TARGETS[target]
    if t.get('v26'):
        data = _rename_26(data)
    if not t['components']:
        return data
    if rel.startswith('recipes/') and t.get('v26'):
        if 'key' in data:
            data['key'] = {k: _ingredient(v) for k, v in data['key'].items()}
        if 'ingredients' in data:
            data['ingredients'] = [_ingredient(v) for v in data['ingredients']]
        if 'ingredient' in data:
            data['ingredient'] = _ingredient(data['ingredient'])
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
        if t.get('entity_type_key'):
            def entity_type(node):
                if node.get('condition') == 'minecraft:entity_properties' and 'type' in node.get('predicate', {}):
                    node = dict(node)
                    pred = dict(node['predicate'])
                    pred['minecraft:entity_type'] = pred.pop('type')
                    node['predicate'] = pred
                return node
            data = _walk(data, entity_type)
    if rel.startswith('forge/biome_modifier/') and t['loader_data'] == 'neoforge':
        data['type'] = data['type'].replace('forge:', 'neoforge:')
    if t.get('v263'):
        data = upgrade_263(rel, data)
    return data


# ---- 26.3 ------------------------------------------------------------------------------------
# The rules Mojang applied to the vanilla data pack between 26.2 and 26.3, as other NightBeam
# mods apply them (The Birth Of Steve's Json263, Bosses' converted data). 26.3 silently ignores
# the old keys, so a clean log does not prove a file was converted.

def _is_condition_list(value):
    return isinstance(value, list) and value and all(isinstance(v, dict) and isinstance(v.get('condition'), str) for v in value)


def _is_function_list(value):
    return isinstance(value, list) and value and all(isinstance(v, dict) and isinstance(v.get('function'), str) for v in value)


def _whole(value):
    if isinstance(value, float) and value.is_integer():
        return int(value)
    if isinstance(value, dict):
        return {k: (_whole(v) if k in ('min', 'max', 'value') else _data_263(v)) for k, v in value.items()}
    return _data_263(value)


def _condition_263(cond):
    cond = dict(cond)
    if cond['condition'] == 'minecraft:block_state_property':
        # match_block: "blocks" (a block, list or tag) and "state" (was "properties").
        cond = {'condition': 'minecraft:match_block', 'blocks': cond['block'],
                **({'state': cond['properties']} if 'properties' in cond else {})}
    if cond['condition'] == 'minecraft:entity_properties' and isinstance(cond.get('predicate'), dict):
        pred = dict(cond['predicate'])
        if 'location' in pred:
            pred['minecraft:location'] = pred.pop('location')
        cond['predicate'] = pred
    out = {'type': cond.pop('condition')}
    for k, v in cond.items():
        if k == 'terms' and isinstance(v, list):
            out[k] = [_condition_263(x) if isinstance(x, dict) and isinstance(x.get('condition'), str) else _data_263(x) for x in v]
        elif k == 'term' and isinstance(v, dict) and isinstance(v.get('condition'), str):
            out[k] = _condition_263(v)
        else:
            out[k] = _data_263(v)
    return out


def _merge_conditions(conds):
    terms = [_condition_263(c) for c in conds]
    return terms[0] if len(terms) == 1 else {'type': 'minecraft:all_of', 'terms': terms}


def _copy_name_to_components(functions):
    """copy_name has no 26.3 user to vouch for it; copy the custom_name component instead."""
    names = [f for f in functions if f.get('function') == 'minecraft:copy_name' and f.get('source') == 'block_entity']
    if not names:
        return functions
    out = [f for f in functions if f not in names]
    for f in out:
        if f.get('function') == 'minecraft:copy_components' and f.get('source') == 'block_entity':
            f['include'] = ['minecraft:custom_name'] + [c for c in f.get('include', []) if c != 'minecraft:custom_name']
            return out
    return [{'function': 'minecraft:copy_components', 'source': 'block_entity', 'include': ['minecraft:custom_name']}] + out


def _function_263(fn):
    out = {'type': fn['function']}
    for k, v in fn.items():
        if k == 'function' or (k == 'conditions' and v == []):
            continue
        if k == 'functions' and fn['function'] == 'minecraft:sequence' and _is_function_list(v):
            out[k] = [_function_263(f) for f in _copy_name_to_components(v)]
        elif _is_condition_list(v):
            out['condition' if k == 'conditions' else k] = _merge_conditions(v)
        else:
            out[k] = _data_263(v)
    return out


def _data_263(node):
    if isinstance(node, list):
        return [_data_263(v) for v in node]
    if not isinstance(node, dict):
        return node
    out = {}
    for k, v in node.items():
        if k == 'conditions' and v == []:
            continue
        if _is_condition_list(v):
            out['condition' if k == 'conditions' else k] = _merge_conditions(v)
        elif k == 'functions' and _is_function_list(v):
            fns = [_function_263(f) for f in _copy_name_to_components(v)]
            out['modifier'] = fns[0] if len(fns) == 1 else fns
        elif k == 'rolls':
            out[k] = _whole(v)
        else:
            out[k] = _data_263(v)
    return out


def _block_states_263(node):
    """{"Name", "Properties"} block states become {"id", "properties"}; simple providers become the bare state."""
    if isinstance(node, list):
        return [_block_states_263(v) for v in node]
    if not isinstance(node, dict):
        return node
    if isinstance(node.get('Name'), str) and set(node) <= {'Name', 'Properties'}:
        state = {'id': node['Name']}
        if 'Properties' in node:
            state['properties'] = node['Properties']
        return state
    if node.get('type') == 'minecraft:simple_state_provider' and 'state' in node:
        return _block_states_263(node['state'])
    if node.get('type') == 'minecraft:random_offset':
        xz, y = node.get('xz_spread', 0), node.get('y_spread', 0)
        return {'type': 'minecraft:offset', 'x': xz, 'y': y, 'z': xz}
    return {k: _block_states_263(v) for k, v in node.items()}


def _count_263(lo, hi):
    return lo if lo == hi else {'type': 'minecraft:uniform', 'min_inclusive': lo, 'max_inclusive': hi}


def upgrade_263(rel, data):
    """Rewrites one 26.2-shaped data file (path relative to data/<namespace>/) into the 26.3 shape."""
    if rel.startswith(('loot_tables/', 'loot_table/', 'advancements/', 'advancement/')):
        return _data_263(data)
    if rel.startswith('worldgen/'):
        if rel.startswith('worldgen/configured_feature/') and isinstance(data.get('config'), dict):
            data = {'type': data['type'], **data['config']}
        return _block_states_263(data)
    if 'biome_modifier/' in rel and data.get('type') == 'neoforge:add_spawns':
        spawners = data['spawners'] if isinstance(data['spawners'], list) else [data['spawners']]
        data = dict(data)
        data['spawners'] = [{'type': s['type'], 'count': _count_263(s['minCount'], s['maxCount']), 'weight': s['weight']}
                            if 'minCount' in s else s for s in spawners]
        return data
    return data


def upgrade_model_263(model):
    """Block-model elements: "shade": false becomes "shade_direction_override": "up"; "shade": true is the default."""
    if not isinstance(model.get('elements'), list):
        return model
    model = dict(model)
    elements = []
    for el in model['elements']:
        if isinstance(el, dict) and isinstance(el.get('shade'), bool):
            el = dict(el)
            if not el.pop('shade') and 'shade_direction_override' not in el:
                el['shade_direction_override'] = 'up'
        elements.append(el)
    model['elements'] = elements
    return model
