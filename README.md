# Glyph

Android app that shows a tiny draggable overlay with your current keyboard layout while typing on a Samsung tablet with an external Bluetooth keyboard.

## Modules

| Module | Purpose |
|--------|---------|
| `:core` | Pure Kotlin models, `LanguageStateManager`, `CycleLogic` |
| `:detection` | `LangOverlayAccessibilityService`, overlay window, chord detection, input session |
| `:app` | Settings UI, DataStore, onboarding, boot health-check |
| `:prototype:key-spike` | Phase 0 hardware spike APK |

## Build

Open the project in **Android Studio** (recommended) or install JDK 17+ and generate the Gradle wrapper:

```bash
gradle wrapper --gradle-version 8.11.1
./gradlew :app:assembleDebug
./gradlew :prototype:key-spike:assembleDebug
./gradlew :core:test
```

## Setup (sideload)

1. Install APK
2. Open app → complete onboarding
3. Allow restricted settings (Android 13+, App info → ⋮)
4. Enable **Glyph** accessibility service
5. Grant **Display over other apps**
6. Disable battery optimization / remove from Samsung Sleeping apps
7. Configure languages in order matching Samsung Keyboard
8. Match shortcut in app settings with Samsung **Physical keyboard → Change language shortcut**

## Phase 0 spike

Install `prototype/key-spike` APK first on Samsung One UI 8.5 hardware to verify Alt+Shift and Ctrl+Space reach `AccessibilityService.onKeyEvent`. Record results in `docs/phase0-results.md`.

## Architecture

Single `LangOverlayAccessibilityService` hosts overlay + chord FSM + input session detector. No foreground service. Language state is app-owned; overlay shows during active text input (Auto mode) or always (Always show mode).

## Battery benchmarks

See `scripts/benchmark-battery.ps1` and `docs/benchmarks/` for repeatable soak-test methodology.

## DeX / multi-display

Default display only in v1.
