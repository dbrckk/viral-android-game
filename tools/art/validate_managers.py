import json
from pathlib import Path

art=json.loads(Path('assets/art/managers/manager-sprites.json').read_text())
expected={'mia_flux':('street_stand',15,1.5),'noah_vector':('corner_shop',40,1.75),'aya_forge':('workshop',100,2.0),'rex_nova':('factory',250,2.5)}
portraits={p['id']:p for p in art['portraits']}
assert set(portraits)==set(expected), 'manager portrait ids differ from runtime catalog'
assert art['states']==['locked','available','hired','boosted']
for mid,(business,level,bonus) in expected.items():
 p=portraits[mid];assert p['business']==business and p['unlockLevel']==level and p['bonus']==bonus
 for key in ('primary','neon','skin','hair','outfit','accessory','name','role'): assert p.get(key), f'{mid}: missing {key}'
print('Manager art contract OK: 4 portraits x 4 states')
