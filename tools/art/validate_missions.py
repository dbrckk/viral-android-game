import json
from pathlib import Path

p=Path('assets/art/missions/missions.json')
data=json.loads(p.read_text())
assert data['schemaVersion']==1
assert set(data['states'])=={'locked','active','complete','claimed'}
items=data['missions']
assert len(items)==6
ids={x['id'] for x in items}
required={'first_25_levels','street_lv100','income_10k','hire_two','buy_four_upgrades','factory_lv500'}
assert ids==required
for x in items:
    assert x['icon'] and x['primary'].startswith('#') and x['secondary'].startswith('#')
    assert x['reward'] in {'cash','gems'}
print('missions art contract ok')
