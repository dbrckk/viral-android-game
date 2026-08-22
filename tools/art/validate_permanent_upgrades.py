#!/usr/bin/env python3
import json
from pathlib import Path

ROOT=Path(__file__).resolve().parents[2]
P=ROOT/'assets/art/upgrades/permanent-upgrades.json'
D=json.loads(P.read_text(encoding='utf-8'))
expected_businesses={'street_stand','corner_shop','workshop','factory'}
expected_states=['locked','available','purchased']
assert D.get('schemaVersion')==1
assert D.get('states')==expected_states
items=D.get('upgrades',[])
assert len(items)==8
ids=[x['id'] for x in items]
assert len(ids)==len(set(ids))
for b in expected_businesses:
    group=[x for x in items if x.get('business')==b]
    assert len(group)==2, f'{b}: expected 2 permanent upgrade icons'
for x in items:
    for k in ('id','business','icon','primary','neon','glyph'):
        assert x.get(k), f"{x.get('id')}: missing {k}"
print('PERMANENT UPGRADE ART VALIDATION OK: 8 icons / 4 businesses / 3 states')
