import json
from pathlib import Path
p=Path('assets/art/managers/managers.json')
d=json.loads(p.read_text())
expected={'mia_flux':'street_stand','noah_vector':'corner_shop','aya_forge':'workshop','rex_nova':'factory'}
items=d.get('managers',[])
assert len(items)==4, 'Expected 4 managers'
assert {x['id']:x['business'] for x in items}==expected
for x in items:
    assert x['unlockLevel']>0 and x['cost']>0 and x['incomeMultiplier']>1
    assert {'skin','hair','primary','neon'} <= set(x['palette'])
    assert {'hair','outfit','prop','badge'} <= set(x['portrait'])
assert set(d['states'])=={'locked','available','hired'}
print('Manager art/gameplay contract OK')
