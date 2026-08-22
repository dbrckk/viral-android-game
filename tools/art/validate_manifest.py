#!/usr/bin/env python3
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
MANIFEST = ROOT / "assets" / "art" / "manifest.json"
DIRECTION = ROOT / "docs" / "art" / "DIRECTION_ART.md"

REQUIRED_TOP_LEVEL = {
    "schemaVersion",
    "project",
    "artDirection",
    "generatedReferences",
    "businessGroups",
    "layerContract",
    "requiredStates",
    "validation",
}

EXPECTED_LAYERS = [
    "base_structure",
    "details",
    "lights_emissive",
    "effects_particles",
]

EXPECTED_PROFILES = ["full", "reduced_motion", "power_save"]
EXPECTED_TIERS = ["base", "lv25", "lv100", "lv250", "lv500", "master"]


def fail(message: str) -> None:
    print(f"ART VALIDATION FAILED: {message}", file=sys.stderr)
    raise SystemExit(1)


def main() -> None:
    if not MANIFEST.is_file():
        fail(f"missing manifest: {MANIFEST.relative_to(ROOT)}")
    if not DIRECTION.is_file():
        fail(f"missing art direction: {DIRECTION.relative_to(ROOT)}")

    try:
        data = json.loads(MANIFEST.read_text(encoding="utf-8"))
    except json.JSONDecodeError as exc:
        fail(f"invalid JSON: {exc}")

    missing = REQUIRED_TOP_LEVEL - set(data)
    if missing:
        fail(f"missing top-level keys: {', '.join(sorted(missing))}")

    if data["artDirection"].get("perspective") != "isometric-3-4":
        fail("art perspective must remain isometric-3-4")

    if data["artDirection"].get("renderProfiles") != EXPECTED_PROFILES:
        fail(f"renderProfiles must be {EXPECTED_PROFILES}")

    if data.get("layerContract") != EXPECTED_LAYERS:
        fail(f"layerContract must be {EXPECTED_LAYERS}")

    refs = data.get("generatedReferences", [])
    if len(refs) < 5:
        fail("generated visual references are incomplete")

    groups = data.get("businessGroups", [])
    if not groups:
        fail("at least one business group is required")

    seen_ids = set()
    for group in groups:
        for asset in group.get("assets", []):
            asset_id = asset.get("id")
            if not asset_id:
                fail("business asset without id")
            if asset_id in seen_ids:
                fail(f"duplicate asset id: {asset_id}")
            seen_ids.add(asset_id)
            if asset.get("tiers") != EXPECTED_TIERS:
                fail(f"{asset_id}: invalid progression tiers")

    required_group_01 = {"street_stand", "corner_shop", "workshop", "factory"}
    if not required_group_01.issubset(seen_ids):
        fail("group_01 is incomplete")

    print("ART VALIDATION OK")
    print(f"references={len(refs)} assets={len(seen_ids)} profiles={len(EXPECTED_PROFILES)}")


if __name__ == "__main__":
    main()
