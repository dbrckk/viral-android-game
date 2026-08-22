import json
from pathlib import Path

p=Path('assets/art/prestige/prestige.json')
assert p.exists(), 'prestige.json missing'
data=json.loads(p.read_text())
assert data['schemaVersion']==1
assert data['currency']['id']=='prestige_crown'
assert data['currency']['glyph']=='crown_core'
for key in ('primary','secondary','energy'):
    value=data['currency'][key]
    assert isinstance(value,str) and value.startswith('#') and len(value)==7
for state in ('locked','ready','prestiging','owned'):
    assert state in data['states'], f'missing prestige state {state}'
for effect in ('crown_burst','energy_ring','ascending_particles','empire_reset_flash'):
    assert effect in data['effects'], f'missing effect {effect}'
print('prestige art contract: OK')
