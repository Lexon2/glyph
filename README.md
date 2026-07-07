# Lang Overlay

Android app that shows a tiny draggable overlay (EN / RU / UA) for external Bluetooth keyboard layout on Samsung tablets.

## Modules

| Module | Purpose |
|--------|---------|
| `:core` | Pure Kotlin models, `LanguageStateManager`, `ToggleLogic` |
| `:detection` | `LangOverlayAccessibilityService`, overlay window, chord detection |
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
4. Enable **Lang Overlay** accessibility service
5. Grant **Display over other apps**
6. Disable battery optimization / remove from Samsung Sleeping apps
7. Match shortcut in app settings with Samsung **Physical keyboard → Change language shortcut**

## Phase 0 spike

Install `prototype/key-spike` APK first on Samsung One UI 8.5 hardware to verify Alt+Shift and Ctrl+Space reach `AccessibilityService.onKeyEvent`.

## Architecture

Single `LangOverlayAccessibilityService` hosts overlay + chord FSM. No foreground service. Language state is app-owned; IME sync deferred to v2.

## DeX / multi-display

v1 attaches overlay to the default display only.
