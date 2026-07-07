# Battery Benchmark — Glyph

Repeatable soak-test methodology for comparing overlay visibility modes and idle battery draw.

## Prerequisites

- Device connected via USB debugging
- Glyph installed (`com.langoverlay.app`)
- Accessibility service and overlay permission granted
- Screen on, fixed brightness (e.g. 50%), Wi‑Fi off optional for consistency

## Scenarios

| ID | Duration | Setup |
|----|----------|-------|
| S0 | 30 min | App installed, a11y **off** — baseline |
| S1 | 30 min | A11y on, overlay visibility **Auto**, no text field focused |
| S2 | 30 min | Overlay visibility **Always show** |
| S3 | 30 min | Overlay visibility **Auto**, no interaction |
| S4 | 30 min | **Auto**, focus Notes/text field, type ~30 WPM (scripted or manual) |
| S5 | once | Reboot; measure single boot health AlarmManager wakeup |

## Metrics

| Metric | Source |
|--------|--------|
| UID CPU time | `dumpsys batterystats com.langoverlay.app` |
| Estimated power | batterystats summary |
| Wakeups / alarms | batterystats detail |
| Overlay window present | `dumpsys window windows` |
| Memory | `dumpsys meminfo com.langoverlay.app` |
| A11y event rate (debug) | logcat tag `GlyphSession` |

## Success criteria (tune on Samsung hardware)

- S3 vs S2: ≥40% reduction in app-attributed CPU ms per 30 min
- S1 idle: no recurring wakeups beyond boot health
- S4: no memory growth over 30 min typing

## Collection

Use `scripts/benchmark-battery.ps1` or run manually:

```powershell
adb shell dumpsys batterystats --reset
# ... run scenario ...
adb shell dumpsys batterystats com.langoverlay.app > batterystats_glyph_S1.txt
adb shell dumpsys meminfo com.langoverlay.app > meminfo_glyph_S1.txt
```

Store outputs under `docs/benchmarks/YYYY-MM-DD/`.
