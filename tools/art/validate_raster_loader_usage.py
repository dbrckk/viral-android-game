#!/usr/bin/env python3
from pathlib import Path
import sys

ART_DIR = Path("app/src/main/java/com/empiretycoon/idleconquest/art")
ALLOWED_FILE = "RasterAssetLoader.kt"
FORBIDDEN = (
    "android.util.Base64",
    "android.graphics.BitmapFactory",
    "Base64.decode(",
    "BitmapFactory.decode",
)

violations = []
for path in sorted(ART_DIR.glob("*.kt")):
    if path.name == ALLOWED_FILE:
        continue
    text = path.read_text(encoding="utf-8")
    for token in FORBIDDEN:
        if token in text:
            violations.append(f"{path}: forbidden raster decoding token: {token}")

if violations:
    print("RASTER LOADER USAGE VALIDATION FAILED")
    for violation in violations:
        print(f"- {violation}")
    sys.exit(1)

print("Raster loader usage validation passed")
