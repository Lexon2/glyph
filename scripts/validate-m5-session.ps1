# M5 — Input Session Emulator Validation
$ErrorActionPreference = "Stop"
$adb = "C:\Users\Lexon2\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$pkg = "com.langoverlay.app"
$a11y = "$pkg/com.langoverlay.detection.accessibility.LangOverlayAccessibilityService"

function Log($msg) {
    Write-Output "[$(Get-Date -Format 'HH:mm:ss')] $msg"
}

function Count-OverlayWindows {
    $matches = & $adb shell dumpsys window windows 2>&1 |
        Select-String "Window\{.*com\.langoverlay\.app\}" |
        Where-Object { $_ -notmatch "MainActivity" }
    return @($matches).Count
}

& $adb logcat -c
& $adb shell pm clear $pkg | Out-Null
Start-Sleep 1
& $adb shell appops set $pkg SYSTEM_ALERT_WINDOW allow

Log "Launch app (installs locator)"
& $adb shell am start -n "$pkg/.MainActivity"
Start-Sleep 3

Log "Enable accessibility"
& $adb shell settings put secure enabled_accessibility_services $a11y
& $adb shell settings put secure accessibility_enabled 1
Start-Sleep 5

Log "=== AUTO mode: home screen (expect 0 overlay windows) ==="
& $adb shell input keyevent KEYCODE_HOME
Start-Sleep 2
$homeOverlays = Count-OverlayWindows
Log "Overlay windows on home: $homeOverlays"

Log "=== Chrome URL bar (text field) ==="
& $adb shell am start -n com.android.chrome/com.google.android.apps.chrome.Main
Start-Sleep 3
& $adb shell input tap 400 150
Start-Sleep 2
& $adb shell input text "glyph"
Start-Sleep 3
$chromeOverlays = Count-OverlayWindows
Log "Overlay windows with chrome text field: $chromeOverlays"

$sessionLogs = & $adb logcat -d -s GlyphSession:D
Log "=== GlyphSession logcat ==="
if ($sessionLogs) { $sessionLogs | ForEach-Object { Log $_ } } else { Log "(no GlyphSession entries)" }

$fatal = & $adb logcat -d | Select-String -Pattern "FATAL EXCEPTION|ANR in $pkg"
if ($fatal) {
    Log "FAIL: crash detected"
    $fatal | ForEach-Object { Log $_ }
    exit 1
}

$pass = $true
if ($homeOverlays -ne 0) {
    Log "FAIL: overlay visible on home in auto mode"
    $pass = $false
} else {
    Log "PASS: no overlay on home (auto mode)"
}

if ($chromeOverlays -eq 0 -and -not $sessionLogs) {
    Log "FAIL: overlay did not appear for text field and no session log"
    $pass = $false
} else {
    Log "PASS: session detected (overlay=$chromeOverlays, logs=$($sessionLogs.Count))"
}

if (-not $pass) { exit 1 }
Log "M5 validation complete"
