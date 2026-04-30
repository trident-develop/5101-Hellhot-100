# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

This is a freshly-scaffolded Android Studio project. Both `MainActivity` and `LoadingActivity` have empty `setContent {}` blocks — no UI has been implemented yet. The bundled `res/raw/` audio files (`game_music.mp3`, `level_win.mp3`, `level_lose.mp3`, `slot_rounded.mp3`) and `res/drawable/bg_1.webp` suggest the planned app is a game (likely with a slot/casino mechanic), but no game logic exists in code yet. Treat most tasks as net-new feature work, not modifications to existing systems.

## Identifier mismatches (don't "fix" these without checking)

- Gradle project name: `Hellhot 100` (`settings.gradle.kts`)
- Android `namespace` and `applicationId`: `jp.co.mixi.monsterstr` (`app/build.gradle.kts`)
- Compose theme: `Hellhot100Theme` in `app/src/main/java/jp/co/mixi/monsterstr/ui/theme/Theme.kt`

The `jp.co.mixi.monsterstr` package looks like a placeholder copied from another project. Confirm with the user before renaming — the applicationId is the Play Store identity.

## Launcher entry point

`LoadingActivity` is the launcher (`MAIN`/`LAUNCHER` intent filter in `AndroidManifest.xml`), **not** `MainActivity`. `MainActivity` is `exported="false"` and currently unreachable — any new navigation must be wired from `LoadingActivity` (e.g. `startActivity(Intent(this, MainActivity::class.java))`).

## Build / run / test

Use the Gradle wrapper from the repo root:

```bash
./gradlew assembleDebug              # build debug APK
./gradlew installDebug               # install on a connected device/emulator
./gradlew lint                       # Android Lint
./gradlew test                       # JVM unit tests (app/src/test)
./gradlew connectedAndroidTest       # instrumented tests on a device (app/src/androidTest)
./gradlew clean
```

Run a single unit test class or method:

```bash
./gradlew :app:testDebugUnitTest --tests "jp.co.mixi.monsterstr.ExampleUnitTest"
./gradlew :app:testDebugUnitTest --tests "jp.co.mixi.monsterstr.ExampleUnitTest.addition_isCorrect"
```

Run a single instrumented test (requires a running emulator/device):

```bash
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=jp.co.mixi.monsterstr.ExampleInstrumentedTest
```

`local.properties` (gitignored) supplies `sdk.dir`. Java 11 toolchain is required (`compileOptions` in `app/build.gradle.kts`).

## SDK/toolchain versions

- AGP `9.1.1`, Kotlin `2.2.10`, Compose BOM `2026.02.01` (see `gradle/libs.versions.toml`)
- `compileSdk = 36` (with `minorApiLevel = 1`), `targetSdk = 36`, `minSdk = 24`
- Java/Kotlin source/target compatibility: 11

All dependency versions live in `gradle/libs.versions.toml` (version catalog); the top-level `build.gradle.kts` only declares plugin aliases. Add new libraries by editing the catalog rather than hard-coding coordinates in `app/build.gradle.kts`.

## UI stack

Single-module Compose app using Material3 from the Compose BOM. Activities extend `ComponentActivity` and call `enableEdgeToEdge()` before `setContent { ... }`. Wrap composables in `Hellhot100Theme { ... }` (defined in `ui/theme/Theme.kt`) so the app theme/typography/colors apply.

`buildTypes.release` currently has `isMinifyEnabled = false` and no `debug` block is declared — there is no shrinking/obfuscation in place. ProGuard rules live in `app/proguard-rules.pro` if/when minification is enabled.
