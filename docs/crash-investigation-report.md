# Launch Crash — Root Cause Investigation Report

**Date:** 2026-07-07  
**Device:** emulator-5554 (API 35)  
**Logcat:** `docs/crash-investigation-logcat.txt`

## Root cause

`IllegalStateException` on the main thread during Compose layout:

> Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like **LazyColumn** and **Column(Modifier.verticalScroll())**.

**Location:** `LanguageManagerSection` renders a `LazyColumn` (drag-and-drop language list) inside `SettingsScreen`, which wraps all content in `Column(Modifier.verticalScroll())`.

The crash occurs when the **Settings** route is composed (i.e. `onboardingCompleted == true`). It does not occur on the onboarding route.

## Files changed

| File | Change |
|------|--------|
| [`app/src/main/kotlin/com/langoverlay/app/ui/settings/LanguageManagerSection.kt`](app/src/main/kotlin/com/langoverlay/app/ui/settings/LanguageManagerSection.kt) | Added `Modifier.heightIn(max = (languages.size * 72).dp)` on `LazyColumn` to bound height inside the parent scroll container |

## Why the fix works

`LazyColumn` must not receive unbounded max height. Constraining it with `heightIn(max = …)` gives the list a finite measurement height while the outer `Column.verticalScroll()` continues to scroll the full settings page. Drag-and-drop reordering is unchanged.

## Why previous validation did not detect it

| Validation | Why it missed the crash |
|------------|-------------------------|
| `MainActivityTest.mainActivityLaunchesWithoutCrashing` | Only asserts the activity class; does not wait for Settings composition |
| `pm clear` + launch scripts | Reset `onboardingCompleted` → app opens **Onboarding**, not Settings |
| `validate-emulator.ps1` | Same: fresh install lands on onboarding; Settings never composed |
| M3 branding check | Used `aapt` / cold start; did not require completed onboarding |
| Build success | Compile-time only; layout constraint is a runtime Compose error |

The crash was reproducible only when DataStore had `onboardingCompleted = true` (returning user or emulator state from prior sessions).

## Regression risks

| Risk | Mitigation |
|------|------------|
| Very long language lists (20 items) make the bounded list tall | Acceptable; outer scroll still works; max height scales with count |
| `heightIn` too small clips rows on large font scales | Uses 72dp/row; monitor on Samsung with large display size |
| Other nested scrollables added later | Avoid `LazyColumn` inside `verticalScroll` parents |

## Additional tests performed (post-fix)

| Test | Result |
|------|--------|
| Cold launch (onboarding-complete state) | **PASS** — no `FATAL EXCEPTION` |
| Background / foreground | **PASS** |
| Screen rotation | **PASS** |
| Chrome text field + a11y enabled | **PASS** — no crash |
| `:core:test` | **PASS** (10/10) |
| Instrumented tests | **PASS** (4/4) |

## First fatal stack trace (excerpt)

```
E AndroidRuntime: FATAL EXCEPTION: main
E AndroidRuntime: Process: com.langoverlay.app, PID: 18093
E AndroidRuntime: java.lang.IllegalStateException: Vertically scrollable component was measured
    with an infinity maximum height constraints...
    at androidx.compose.foundation.lazy.LazyListKt$rememberLazyListMeasurePolicy$1$1.invoke
```
