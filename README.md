# Empire Tycoon — Idle Conquest

Android idle/tycoon prototype written in Kotlin with a custom Canvas UI and a raster-first art pipeline.

## Current gameplay

- 4 businesses: Street Stand, Corner Shop, Workshop, Factory
- Buy modes: x1, x10, x25, MAX
- Milestones at levels 25 / 100 / 250 / 500 / 1000
- 4 managers with unlock levels and income multipliers
- 8 permanent upgrades
- 6 missions with cash/gem rewards
- Prestige/crown metaprogression
- Local persistence with primary + backup saves
- Offline income capped at 8 hours

## Project structure

```text
app/src/main/java/com/empiretycoon/idleconquest/
├── game/     # economy, missions, managers, prestige, persistence
├── art/      # raster/vector renderers and asset resolution
└── ui/       # main custom Canvas view

assets/art/   # source art copied into Android assets during preBuild
tools/art/    # art/runtime validators
.github/workflows/
├── android-build.yml
└── art-assets.yml
```

## Build requirements

- JDK 17
- Gradle 8.9
- Android SDK / compileSdk 35

## Local verification

```bash
gradle :app:testDebugUnitTest
gradle :app:lintDebug
gradle :app:assembleDebug
```

The debug APK is produced at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Art pipeline

`assets/art` is copied into the Android asset set by the `syncArtAssets` Gradle task before `preBuild`.

CI validates:

- art manifest/contracts
- business sprite groups
- vector sprites
- manager portraits
- permanent upgrades
- mission assets
- prestige assets
- runtime asset references
- Base64/WebP payload validity
- duplicate business raster atlases

## CI

`Android Build` runs, in order:

1. art/runtime validation
2. JVM unit tests
3. Android lint
4. debug APK assembly
5. APK artifact upload

`Art Assets Validation` runs independently for art-related changes.

## Persistence

`GameSaveStore` keeps a primary save and the previous valid payload as a backup. Restore falls back to the backup if the primary payload is invalid or unsupported. Offline earnings are capped at 8 hours.

## Current technical priorities

1. Reduce the continuous redraw rate in `BusinessShowcaseView` from display refresh rate to an idle-game-appropriate cadence.
2. Split `BusinessShowcaseView` into smaller rendering/input responsibilities.
3. Replace duplicated raster-atlas loading code with a shared atlas loader/renderer.
4. Migrate Base64 image assets to direct binary WebP where practical.
5. Add save-store tests around corruption, backup restore and migration.
6. Expand content and rebalance progression after the technical foundation is stable.

## Not yet production-ready

The project is an advanced prototype, not a Play Store release build. Missing product/release work includes, among other things:

- production signing / AAB release pipeline
- monetization (ads + lifetime remove-ads purchase)
- consent/privacy flow
- analytics/crash reporting
- store assets and policy documentation
- broader device/accessibility testing
- longer-term content and economy balancing

## Version

Current Android app version: `0.1.0`.
