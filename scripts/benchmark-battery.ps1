param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("S0", "S1", "S2", "S3", "S4", "collect")]
    [string]$Scenario,

    [string]$Package = "com.langoverlay.app",
    [string]$OutputDir = "docs/benchmarks",
    [int]$DurationMinutes = 30
)

$ErrorActionPreference = "Stop"
$timestamp = Get-Date -Format "yyyy-MM-dd_HHmm"
$runDir = Join-Path $OutputDir $timestamp
New-Item -ItemType Directory -Force -Path $runDir | Out-Null

function Reset-BatteryStats {
    adb shell dumpsys batterystats --reset | Out-Null
    Write-Host "Battery stats reset."
}

function Collect-Metrics {
    param([string]$Label)
    $prefix = Join-Path $runDir "${Label}"
    adb shell dumpsys batterystats $Package | Out-File "${prefix}_batterystats.txt" -Encoding utf8
    adb shell dumpsys meminfo $Package | Out-File "${prefix}_meminfo.txt" -Encoding utf8
    adb shell dumpsys window windows | Select-String -Pattern "glyph|overlay|$Package" |
        Out-File "${prefix}_windows.txt" -Encoding utf8
    adb shell dumpsys activity services $Package | Out-File "${prefix}_services.txt" -Encoding utf8
    Write-Host "Collected metrics -> $runDir ($Label)"
}

switch ($Scenario) {
    "S0" {
        Write-Host @"
Scenario S0 — Idle baseline ($DurationMinutes min)
1. Force-stop Glyph; disable accessibility service
2. Reset battery stats
3. Wait $DurationMinutes minutes (screen on)
4. Re-run: .\scripts\benchmark-battery.ps1 -Scenario collect
"@
        Reset-BatteryStats
    }
    "S1" {
        Write-Host @"
Scenario S1 — A11y on, Auto mode, overlay hidden ($DurationMinutes min)
1. Enable Glyph accessibility + overlay permission
2. Set overlay visibility to Auto in app settings
3. Stay on home screen / no text fields
4. Reset battery stats, wait $DurationMinutes min
5. Re-run: .\scripts\benchmark-battery.ps1 -Scenario collect
"@
        Reset-BatteryStats
    }
    "S2" {
        Write-Host @"
Scenario S2 — Always show overlay ($DurationMinutes min)
1. Set overlay visibility to Always show
2. Reset battery stats, wait $DurationMinutes min on home screen
3. Re-run: .\scripts\benchmark-battery.ps1 -Scenario collect
"@
        Reset-BatteryStats
    }
    "S3" {
        Write-Host @"
Scenario S3 — Auto mode idle ($DurationMinutes min)
Same as S1; compare CPU against S2 after collection.
"@
        Reset-BatteryStats
    }
    "S4" {
        Write-Host @"
Scenario S4 — Active typing ($DurationMinutes min)
1. Auto mode; open Notes or similar
2. Focus text field; type continuously (~30 WPM)
3. Reset battery stats at session start, wait $DurationMinutes min
4. Re-run: .\scripts\benchmark-battery.ps1 -Scenario collect
"@
        Reset-BatteryStats
    }
    "collect" {
        Collect-Metrics -Label $Scenario
        $summary = @"
# Benchmark run $timestamp

Package: $Package
Scenario: manual (see TEST_CHECKLIST.md)

## Files
- ${Scenario}_batterystats.txt
- ${Scenario}_meminfo.txt
- ${Scenario}_windows.txt
- ${Scenario}_services.txt

## Notes
Fill in device model, One UI version, and observed CPU/power deltas after Samsung soak test.
"@
        $summary | Out-File (Join-Path $runDir "README.md") -Encoding utf8
    }
}
