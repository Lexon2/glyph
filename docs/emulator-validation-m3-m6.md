# Emulator Validation — Milestones M3–M6

**Date:** 2026-07-07  
**Target:** emulator-5554 (API 35)

## M3 — Glyph Rebrand

| Check | Result |
|-------|--------|
| `application-label:'Glyph'` in APK | Pass |
| `Theme.Glyph` in resources | Pass |
| No FATAL/ANR on launch | Pass |

## M4 — Multi-Language

| Check | Result |
|-------|--------|
| `:core:test` (CycleLogic, LanguageListCodec, LanguageStateManager) | 10/10 pass |
| `LanguageDataStoreTest` instrumented | Pass |
| A11y enabled without crash | Pass |

## M5 — Input Session

| Check | Result |
|-------|--------|
| Auto mode: no overlay on home | Pass |
| Session active on Chrome text field (`GlyphSession` log) | Pass |
| `APPLICATION_OVERLAY` window during session | Pass (manual dumpsys) |
| Fix: `flagRetrieveInteractiveWindows` required | Applied |

Script: `scripts/validate-m5-session.ps1`

## M6 — Battery (emulator snapshot)

| Metric | 60s idle, a11y on, auto mode |
|--------|------------------------------|
| UID u0a212 CPU | u=1.7s s=4.4s (~6s total) |
| Fg Service time | 59.5s |
| Overlay hidden on home | Yes (auto mode) |

Script: `scripts/validate-m6-battery.ps1`  
Output: `docs/benchmarks/emulator-2026-07-07/`

Samsung soak targets (≥40% S3 vs S2) require hardware.

## M2 — Key Spike

| Check | Result |
|-------|--------|
| `:prototype:key-spike:assembleDebug` | Pass |
| Samsung chord detection | Deferred — `docs/phase0-results.md` |

## Regression

| Check | Result |
|-------|--------|
| `validate-emulator.ps1` | Pass (no ANR/FATAL) |
| Instrumented tests | 4/4 pass |
