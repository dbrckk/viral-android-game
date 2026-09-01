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
- Local persistence with primary + validated backup saves
- Offline income capped at 8 hours

## Project structure

```text
app/src/main/java/com/empiretycoon/idleconquest/
├── game/     # economy, missions, managers, prestige, persistence/offline progress
├── art/      # raster/vector renderers and asset resolution
└── ui/       # Canvas rendering plus pure layout/state/interaction helpers

assets/art/   # source art copied into Android assets during preBuild
tools/art/    # art/runtime validators
.github/workflows/
├── android-build.yml
└── art-assets.yml
```

The Canvas screen is progressively decomposed into testable helpers for layout, transient state, interaction results, formatting and game-loop timing instead of moving gameplay logic into Android drawing code.

## Build requirements

- JDK 17
- Gradle 8.9
- Android SDK / compileSdk 35
- minSdk 23

## Local verification

```bash
gradle :app:testDebugUnitTest
gradle :app:lintDebug
gradle :app:assembleDebug
gradle :app:assembleRelease
```

The debug APK is produced at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Art pipeline

`assets/art` is copied into the Android asset set by the `syncArtAssets` Gradle task before `preBuild`.

Raster decoding is centralized through `RasterAssetLoader`, with `RasterAtlas` providing shared horizontal and grid atlas drawing. CI rejects direct `Base64`/`BitmapFactory` raster decoding elsewhere in application code.

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
- centralized raster loader usage

## CI

`Android Build` runs, in order:

1. art/runtime validation
2. JVM unit tests
3. Android lint
4. debug APK assembly
5. minified release APK assembly through R8
6. debug APK artifact upload

`Art Assets Validation` runs independently for art-related changes.

## Runtime/performance

The main Canvas view redraws every 50 ms (about 20 FPS), which is appropriate for the current idle-game presentation and avoids continuously redrawing at display refresh rate. Asset resolution and raster decoding are cached. Frame timing, autosave cadence, background-session progress and transient UI state are isolated from Android rendering and covered by JVM tests.

## Persistence

`GameSaveStore` keeps a primary save and only promotes a restorable previous payload to backup. Restore falls back to that backup if the primary payload is invalid or unsupported. Snapshot restoration, catalog filtering, invalid numeric values and offline progression are covered by pure JVM tests. Offline earnings are capped at 8 hours and background resume progress is consumable only once.

## Economy safeguards

Economy tests cover milestone behavior, managers, permanent upgrades, prestige, invalid cash grants, MAX purchases and late-game integer saturation. MAX purchase calculation is checked against the actual geometric-series cost so floating-point boundary errors cannot buy an unaffordable extra level.

## Current technical priorities

1. Keep unit tests, Android lint, debug assembly and minified release assembly green on every application change.
2. Continue splitting `BusinessShowcaseView` into smaller rendering/input responsibilities where doing so materially improves testability.
3. Migrate Base64-wrapped raster assets to direct binary WebP where practical.
4. Expand device/accessibility coverage for the custom Canvas interface.
5. Increase gameplay/content depth and rebalance progression after the technical foundation remains stable.
6. Add production release infrastructure only when store/monetization work begins.

## Not yet production-ready

The project is an advanced prototype, not yet a Play Store release. Missing product/release work includes, among other things:

- production signing / AAB release pipeline
- monetization (ads + lifetime remove-ads purchase)
- consent/privacy flow
- analytics/crash reporting
- store assets and policy documentation
- broader device/accessibility testing
- longer-term content and economy balancing

## Version

Current Android app version: `0.1.0`.
