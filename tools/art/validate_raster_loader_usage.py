#!/usr/bin/env python3
from pathlib import Path
import sys

SOURCE_DIR = Path("app/src/main/java/com/empiretycoon/idleconquest")
ALLOWED_PATH = SOURCE_DIR / "art" / "RasterAssetLoader.kt"
FORBIDDEN = (
    "android.util.Base64",
    "android.graphics.BitmapFactory",
    "Base64.decode(",
    "BitmapFactory.decode",
)

violations = []
for path in sorted(SOURCE_DIR.rglob("*.kt")):
    if path == ALLOWED_PATH:
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
