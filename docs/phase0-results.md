# Phase 0 — Samsung Hardware Key Spike Results

**APK:** `prototype/key-spike/build/outputs/apk/debug/key-spike-debug.apk`  
**Target:** Samsung tablet, One UI 8.5+, Bluetooth keyboard, Samsung Keyboard as IME  
**Date:** _pending hardware test_  
**Tester:** _name_

## Decision Gate

| Outcome | Action |
|---------|--------|
| **PASS** — Alt+Shift and/or Ctrl+Space reach `onKeyEvent` | Proceed with chord FSM as primary language-cycle trigger in production |
| **FAIL** — No shortcut events observed | Rely on overlay tap; document Samsung limitation; defer chord detection |

**Result:** ☐ PASS  ☐ FAIL  ☐ PARTIAL (specify below)

---

## Test Environment

| Field | Value |
|-------|-------|
| Device model | |
| One UI version | |
| Android API level | |
| Keyboard model | |
| IME | Samsung Keyboard |
| Samsung shortcut setting | ☐ Alt+Shift  ☐ Ctrl+Space  ☐ Other: ___ |

---

## Test Matrix

Run each test with Chrome focused, a text field focused, and home screen.

| # | Shortcut | Chrome | Text field | Home screen | Logcat (`KeySpike`) | On-screen overlay |
|---|----------|--------|------------|-------------|---------------------|-------------------|
| 1 | Alt+Shift | ☐ | ☐ | ☐ | ☐ | ☐ |
| 2 | Ctrl+Space | ☐ | ☐ | ☐ | ☐ | ☐ |
| 3 | Shift+Space | ☐ | ☐ | ☐ | ☐ | ☐ |

---

## Setup Checklist

- [ ] Key Spike APK installed
- [ ] Overlay permission granted
- [ ] Key Spike accessibility service enabled
- [ ] Restricted settings allowed (API 33+ sideload)
- [ ] Bluetooth keyboard paired
- [ ] Samsung Keyboard set as default IME
- [ ] Physical keyboard shortcut matches test column

---

## Logcat Capture

```powershell
adb logcat -c
adb logcat -s KeySpike:V > spike-logcat.txt
```

Attach `spike-logcat.txt` or paste relevant excerpts below.

---

## Notes

_Document any consumed events, missing events, OEM-specific behavior, or conflicts with other accessibility services._

---

## Conclusion

_Summary and recommendation for production chord FSM wiring._
