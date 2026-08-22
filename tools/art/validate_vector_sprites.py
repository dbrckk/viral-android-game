#!/usr/bin/env python3
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
PATH = ROOT / "assets/art/business/group_01/vector-sprites.json"
BUSINESSES = ["street_stand", "corner_shop", "workshop", "factory"]
TIERS = ["base", "lv25", "lv100", "lv250", "lv500", "master"]
STATES = ["default", "unlock", "milestone"]
PALETTE = ["primary", "secondary", "neon", "roof"]
SPEC = ["floors", "width", "height", "roof", "sign", "props", "lights", "tower"]

def fail(msg):
    print(f"VECTOR SPRITE VALIDATION FAILED: {msg}", file=sys.stderr)
    raise SystemExit(1)

def main():
    if not PATH.is_file(): fail("vector-sprites.json missing")
    data = json.loads(PATH.read_text(encoding="utf-8"))
    if data.get("perspective") != "isometric-3-4": fail("perspective drift")
    businesses = data.get("businesses", {})
    for bid in BUSINESSES:
        if bid not in businesses: fail(f"missing business {bid}")
        item = businesses[bid]
        for key in PALETTE:
            if key not in item.get("palette", {}): fail(f"{bid}: missing palette {key}")
        tiers = item.get("tiers", {})
        for tier in TIERS:
            if tier not in tiers: fail(f"{bid}: missing tier {tier}")
            for key in SPEC:
                if key not in tiers[tier]: fail(f"{bid}/{tier}: missing {key}")
    states = data.get("states", {})
    for state in STATES:
        if state not in states: fail(f"missing state {state}")
    print(f"VECTOR SPRITE VALIDATION OK: {len(BUSINESSES)*len(TIERS)} sprites, {len(STATES)} states")

if __name__ == "__main__": main()
