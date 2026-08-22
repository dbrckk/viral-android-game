#!/usr/bin/env python3
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
GROUP = ROOT / "assets" / "art" / "business" / "group_01" / "group_manifest.json"
RUNTIME = ROOT / "assets" / "art" / "business" / "group_01" / "runtime-index.json"
DOC = ROOT / "docs" / "art" / "business-group-01.md"

EXPECTED_BUSINESSES = ["street_stand", "corner_shop", "workshop", "factory"]
EXPECTED_TIERS = ["base", "lv25", "lv100", "lv250", "lv500", "master"]
EXPECTED_STATES = ["default", "unlock", "milestone"]
EXPECTED_LAYERS = ["base_structure", "details", "lights_emissive", "effects_particles"]
EXPECTED_PROFILES = ["full", "reduced_motion", "power_save"]


def fail(msg: str) -> None:
    print(f"GROUP01 VALIDATION FAILED: {msg}", file=sys.stderr)
    raise SystemExit(1)


def load(path: Path):
    if not path.is_file():
        fail(f"missing {path.relative_to(ROOT)}")
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except json.JSONDecodeError as exc:
        fail(f"invalid JSON in {path.relative_to(ROOT)}: {exc}")


def main() -> None:
    if not DOC.is_file():
        fail("missing docs/art/business-group-01.md")

    group = load(GROUP)
    runtime = load(RUNTIME)

    if group.get("groupId") != "group_01" or runtime.get("groupId") != "group_01":
        fail("group id mismatch")

    delivery = group.get("delivery", {})
    if delivery.get("perspective") != "isometric-3-4":
        fail("perspective drift")
    if delivery.get("layers") != EXPECTED_LAYERS:
        fail("layer contract drift")
    if delivery.get("profiles") != EXPECTED_PROFILES:
        fail("render profile drift")

    businesses = group.get("businesses", [])
    ids = [item.get("id") for item in businesses]
    if ids != EXPECTED_BUSINESSES:
        fail(f"business order must be {EXPECTED_BUSINESSES}")

    for item in businesses:
        if item.get("tiers") != EXPECTED_TIERS:
            fail(f"{item.get('id')}: tier contract drift")
        if item.get("states") != EXPECTED_STATES:
            fail(f"{item.get('id')}: state contract drift")

    if runtime.get("layers") != EXPECTED_LAYERS:
        fail("runtime layers differ from production layers")
    if runtime.get("states") != EXPECTED_STATES:
        fail("runtime states differ from production states")
    if runtime.get("profiles") != EXPECTED_PROFILES:
        fail("runtime profiles differ from production profiles")

    runtime_ids = list(runtime.get("businesses", {}).keys())
    if runtime_ids != EXPECTED_BUSINESSES:
        fail("runtime business index drift")

    print("GROUP01 VALIDATION OK")
    print("businesses=4 tiers=6 states=3 layers=4 profiles=3")


if __name__ == "__main__":
    main()
