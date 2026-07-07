# Milestone 1 — Emulator Validation Report

**Target:** Pixel Tablet AVD, API 35 (Android 15)  
**Date:** 2026-07-07  
**APK:** `app/build/outputs/apk/debug/app-debug.apk`

## Bugs Found & Fixed During Validation

| Issue | Severity | Fix |
|-------|----------|-----|
| Hilt dependency cycle in `AppModule.provideAccessibilityServiceLocator` | Build blocker | Removed redundant `@Provides` method |
| `LanguageStateManagerTest` debounce test flaky | Unit test | Added `advanceUntilIdle()` |
| `BootHealthReceiver` ANR on `BOOT_COMPLETED` | Critical | Removed 60s `goAsync()` hold; schedule via `AlarmManager` |
| Overlay not shown when permission granted after service start | High | `ensureOverlayVisible()` + retry on settings flow |
| Overlay lost on screen rotation | High | Re-show overlay in `onConfigurationChanged` |
| Missing `testInstrumentationRunner` | Test infra | Added `AndroidJUnitRunner` to `app/build.gradle.kts` |
| JDK 17 toolchain unavailable | Build | `:core` uses JVM toolchain 21 |

## What Was Tested

### Build & unit tests
- `./gradlew :core:test` — **7/7 passed**
- `./gradlew :app:assembleDebug` — **success**
- `./gradlew :prototype:key-spike:assembleDebug` — **success** (after `SpikeActivity` fix)

### Instrumented tests (emulator)
- `MainActivityTest.appContextHasCorrectPackage` — **passed**
- `MainActivityTest.mainActivityLaunchesWithoutCrashing` — **passed**

### Manual / adb automation
- APK install on `emulator-5554`
- Cold start `MainActivity`
- Overlay permission via `appops` (`SYSTEM_ALERT_WINDOW: allow`)
- Accessibility service enable via `settings put secure enabled_accessibility_services`
- Overlay window presence (`dumpsys window windows`)
- Screen rotation (`user_rotation` 0 → 1 → 0)
- Foreground / background (`KEYCODE_HOME` + relaunch)
- Process recreation (`am force-stop` + relaunch)
- `ACTION_HEALTH_CHECK` broadcast (no ANR)
- DataStore file creation (`files/datastore/lang_overlay_settings.preferences_pb`)
- Logcat scan for `FATAL EXCEPTION` / `ANR in com.langoverlay.app` (post-fix)

## What Passed

| Area | Result |
|------|--------|
| Debug build | Pass |
| Core unit tests (ToggleLogic, LanguageStateManager) | Pass |
| Instrumented activity launch | Pass |
| App startup without crash | Pass |
| Overlay permission handling (appops) | Pass |
| Accessibility service registration in manifest | Pass |
| Accessibility service binds when app process active | Pass |
| Overlay `TYPE_APPLICATION_OVERLAY` window created | Pass |
| Overlay survives rotation (after fix) | Pass |
| DataStore initialization (after Application warm-up) | Pass |
| Boot health check broadcast (no ANR after fix) | Pass |
| No post-fix FATAL/ANR in logcat | Pass |

## What Failed (before fixes; now resolved)

| Area | Original failure | Status |
|------|------------------|--------|
| `BootHealthReceiver` | ANR — held `goAsync()` for 60s | **Fixed** |
| Overlay on rotation | Window removed after rotation | **Fixed** |
| Overlay when a11y enabled before app | Service DEAD, no overlay | **Mitigated** — launch app before/with a11y enable |
| Hilt graph | Dependency cycle | **Fixed** |

## What Could Not Be Tested on Emulator

| Item | Reason |
|------|--------|
| Alt+Shift / Ctrl+Space chord detection | Requires physical keyboard; emulator soft keyboard doesn't exercise chord FSM meaningfully |
| Samsung One UI restricted settings flow | Emulator runs stock Android 15, not One UI |
| Samsung battery / Sleeping apps | OEM-specific |
| Bluetooth keyboard attach/detach | No BT keyboard paired to AVD |
| IME subtype correlation | Not wired in v1; needs Samsung + hardware keyboard |
| Overlay drag/tap toggle | UI automator could not access overlay window (`null root node`); overlay is separate window layer |
| Onboarding wizard end-to-end taps | Activity often behind launcher in automated dumps; manual UI path not reliably automated |
| `BOOT_COMPLETED` real delivery | `adb broadcast BOOT_COMPLETED` blocked by system (`SecurityException`) — used `ACTION_HEALTH_CHECK` instead |
| Long-run battery / memory leak | Requires extended soak test beyond this session |

## What Requires a Samsung Device

1. **Phase 0 key-spike:** Do Alt+Shift / Ctrl+Space reach `AccessibilityService.onKeyEvent`?
2. **Overlay stability** on One UI 8.5 under real usage
3. **Restricted settings** onboarding (API 33+ sideload)
4. **Samsung Keyboard** as IME with physical keyboard shortcuts
5. **Battery optimization / Sleeping apps** survival
6. **DeX / multi-display** (documented as default-display-only in v1)

## Remaining Technical Risks

| Risk | Severity | Notes |
|------|----------|-------|
| System consumes layout-switch shortcuts before a11y | **Critical** | Unverified until Samsung hardware test |
| A11y service starts before `Application` installs locator | Medium | Rare race; consider `EntryPoint` in service with retry |
| Overlay permission after a11y already connected | Medium | Partially mitigated by `ensureOverlayVisible` |
| Only one key-filter accessibility service | Medium | TalkBack conflicts |
| DataStore read-only until first write on very fast path | Low | Application now warms settings on start |
| Compose lock verification warnings on emulator x86 | Low | Debug/emulator artifact |
| `org.gradle.java.home` not in repo | Low | Set `JAVA_HOME` to Android Studio JBR for CLI builds |

## Automated Test Commands

```powershell
$env:JAVA_HOME = "E:\techs\android-studio\jbr"
.\gradlew.bat :core:test :app:assembleDebug :app:assembleDebugAndroidTest
.\gradlew.bat :app:installDebug :app:installDebugAndroidTest
adb shell am instrument -w com.langoverlay.app.test/androidx.test.runner.AndroidJUnitRunner
powershell -ExecutionPolicy Bypass -File scripts/validate-emulator.ps1
```

---

## Proposed Milestone 2 (smallest next step)

**Goal:** Validate the highest-risk assumption — **whether layout-switch shortcuts are observable at all**.

**Scope only:**
1. Install `:prototype:key-spike` on **Samsung tablet** (not emulator)
2. Pair Bluetooth keyboard, set Samsung Keyboard as IME
3. Run scripted test matrix (Alt+Shift, Ctrl+Space, Shift+Space) per `TEST_CHECKLIST.md`
4. Record logcat + on-screen spike overlay output
5. **Decision gate:** proceed with chord FSM as primary detector **or** pivot to fallback (tap-only + optional IME observer)

**No new product features.** Only hardware evidence collection and a one-page results doc (`docs/phase0-results.md`).

If spike passes on Samsung → Milestone 3 wires chord detection to production overlay with confidence.  
If spike fails → Milestone 3 becomes fallback architecture discussion before further coding.
