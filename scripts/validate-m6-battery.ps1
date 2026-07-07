# M6 — Battery impact snapshot (emulator)
$ErrorActionPreference = "Stop"
$adb = "C:\Users\Lexon2\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$pkg = "com.langoverlay.app"
$a11y = "$pkg/com.langoverlay.detection.accessibility.LangOverlayAccessibilityService"
$outDir = "docs/benchmarks/emulator-$(Get-Date -Format 'yyyy-MM-dd')"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

function Log($msg) { Write-Output "[$(Get-Date -Format 'HH:mm:ss')] $msg" }

& $adb shell dumpsys batterystats --reset | Out-Null
Log "Battery stats reset"

& $adb shell appops set $pkg SYSTEM_ALERT_WINDOW allow
& $adb shell am start -n "$pkg/.MainActivity" | Out-Null
Start-Sleep 2
& $adb shell settings put secure enabled_accessibility_services $a11y
& $adb shell settings put secure accessibility_enabled 1
Start-Sleep 3

Log "S1: a11y on, auto mode, idle 60s"
& $adb shell input keyevent KEYCODE_HOME
Start-Sleep 60

$s1 = Join-Path $outDir "S1_idle_auto_batterystats.txt"
& $adb shell dumpsys batterystats $pkg | Out-File $s1 -Encoding utf8
$cpuLine = Get-Content $s1 | Select-String "cpu_time|Computed drain|UID u0a" | Select-Object -First 5
Log "S1 snapshot: $outDir"
$cpuLine | ForEach-Object { Log "  $_" }

Log "M6 emulator battery snapshot complete (see $outDir)"
