#!/usr/bin/env python3
from __future__ import annotations

import base64
import hashlib
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / "assets"
KOTLIN = ROOT / "app" / "src" / "main" / "java"

errors: list[str] = []

literal_open = re.compile(r'context\.assets\.open\("([^"]+)"\)')
for kt in KOTLIN.rglob("*.kt"):
    text = kt.read_text(encoding="utf-8")
    for rel in literal_open.findall(text):
        target = ASSETS / rel
        if not target.is_file():
            errors.append(f"missing runtime asset referenced by {kt.relative_to(ROOT)}: {rel}")

for p in ASSETS.rglob("*.b64"):
    try:
        raw = base64.b64decode(p.read_text(encoding="utf-8").strip(), validate=True)
    except Exception as exc:
        errors.append(f"invalid base64: {p.relative_to(ROOT)} ({exc})")
        continue
    if len(raw) < 12:
        errors.append(f"decoded asset too small: {p.relative_to(ROOT)}")
        continue
    if not (raw.startswith(b"RIFF") and raw[8:12] == b"WEBP"):
        errors.append(f"decoded .b64 is not WebP: {p.relative_to(ROOT)}")

business_atlases = [
    ASSETS / "art/business/group_01/raster/street_stand_tiers.b64",
    ASSETS / "art/business/group_01/raster/corner_shop_tiers.b64",
    ASSETS / "art/business/group_01/raster/workshop_tiers.b64",
    ASSETS / "art/business/group_01/raster/factory_tiers.b64",
]
seen: dict[str, Path] = {}
for p in business_atlases:
    if not p.is_file():
        errors.append(f"missing business atlas: {p.relative_to(ROOT)}")
        continue
    digest = hashlib.sha256(p.read_bytes()).hexdigest()
    if digest in seen:
        errors.append(
            f"duplicate business atlas: {p.relative_to(ROOT)} == {seen[digest].relative_to(ROOT)}"
        )
    else:
        seen[digest] = p

required_ui = [
    "art/ui/raster/controls_atlas.b64",
    "art/ui/raster/business_card_states.b64",
    "art/ui/raster/hud_panels.b64",
    "art/ui/raster/screen_background.b64",
    "art/ui/raster/empire_tycoon_logo.b64",
]
for rel in required_ui:
    if not (ASSETS / rel).is_file():
        errors.append(f"missing required UI atlas: {rel}")

if errors:
    print("Runtime asset validation failed:")
    for err in errors:
        print(f" - {err}")
    sys.exit(1)

print("Runtime asset validation passed")
