# Emulator validation script for Glyph Milestone 1
$ErrorActionPreference = "Stop"
$adb = "C:\Users\Lexon2\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$pkg = "com.langoverlay.app"
$a11yService = "$pkg/com.langoverlay.detection.accessibility.LangOverlayAccessibilityService"

function Log($msg) {
    $ts = Get-Date -Format "HH:mm:ss"
    Write-Output "[$ts] $msg"
}

& $adb logcat -c
& $adb shell pm clear $pkg | Out-Null
Start-Sleep -Seconds 1

Log "=== 1. Grant overlay permission before launch ==="
& $adb shell appops set $pkg SYSTEM_ALERT_WINDOW allow

Log "=== 2. Launch app (creates DataStore) ==="
& $adb shell am start -n "$pkg/.MainActivity"
Start-Sleep -Seconds 6

Log "=== 3. Enable accessibility service ==="
& $adb shell settings put secure enabled_accessibility_services $a11yService
& $adb shell settings put secure accessibility_enabled 1
Start-Sleep -Seconds 4

Log "=== 4. Restart a11y to pick up overlay permission ==="
& $adb shell settings put secure accessibility_enabled 0
Start-Sleep -Seconds 1
& $adb shell settings put secure enabled_accessibility_services $a11yService
& $adb shell settings put secure accessibility_enabled 1
Start-Sleep -Seconds 4

Log "=== 5. Rotation test ==="
& $adb shell settings put system accelerometer_rotation 0
& $adb shell settings put system user_rotation 1
Start-Sleep -Seconds 2
& $adb shell settings put system user_rotation 0
Start-Sleep -Seconds 1

Log "=== 6. Background / foreground ==="
& $adb shell input keyevent KEYCODE_HOME
Start-Sleep -Seconds 2
& $adb shell am start -n "$pkg/.MainActivity"
Start-Sleep -Seconds 2

Log "=== 7. Process recreation ==="
& $adb shell am force-stop $pkg
Start-Sleep -Seconds 2
& $adb shell am start -n "$pkg/.MainActivity"
Start-Sleep -Seconds 3

Log "=== 8. Boot health check (internal alarm only; external broadcast removed for security) ==="
Log "Skipping external HEALTH_CHECK broadcast — receiver is non-exported"

Log "=== 9. Checks ==="
$appPid = (& $adb shell pidof $pkg).Trim()
Log "PID: $appPid"
$prevEap = $ErrorActionPreference
$ErrorActionPreference = "Continue"
$ds = (& $adb shell run-as $pkg ls files/datastore/ 2>&1 | Out-String).Trim()
$ErrorActionPreference = $prevEap
if ($ds -match "preferences_pb") {
    Log "DataStore: OK ($ds)"
} else {
    Log "DataStore: not yet created (launch app once) - $ds"
}
Log "A11y: $( & $adb shell settings get secure enabled_accessibility_services )"
$windows = & $adb shell dumpsys window windows | Select-String "langoverlay"
Log "Windows: $($windows.Count) matches"
$anr = & $adb logcat -d | Select-String -Pattern "ANR in com.langoverlay.app|FATAL EXCEPTION.*langoverlay"
if ($anr) { $anr | ForEach-Object { Log "FAIL: $_" } } else { Log "No ANR/FATAL for app" }
