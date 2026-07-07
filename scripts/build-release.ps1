# Build a signed release APK and copy it to dist/ with a SHA-256 checksum.
$ErrorActionPreference = "Stop"
$root = Split-Path $PSScriptRoot -Parent
Set-Location $root

$keystoreProps = Join-Path $root "keystore.properties"
if (-not (Test-Path $keystoreProps)) {
    Write-Error "keystore.properties not found. Copy keystore.properties.example and configure signing. See docs/RELEASE_DISTRIBUTION.md"
}

$versionProps = Join-Path $root "version.properties"
$versionName = "unknown"
if (Test-Path $versionProps) {
    Get-Content $versionProps | ForEach-Object {
        if ($_ -match '^\s*VERSION_NAME=(.+)$') { $versionName = $matches[1].Trim() }
    }
}

Write-Host "Building release APK (v$versionName)..."
& (Join-Path $root "gradlew.ps1") :app:assembleRelease
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$apkSource = Join-Path $root "app\build\outputs\apk\release\app-release.apk"
if (-not (Test-Path $apkSource)) {
    Write-Error "Release APK not found at $apkSource"
}

$distDir = Join-Path $root "dist"
New-Item -ItemType Directory -Force -Path $distDir | Out-Null
$apkName = "glyph-v$versionName-release.apk"
$apkDest = Join-Path $distDir $apkName
Copy-Item -Force $apkSource $apkDest

$hash = (Get-FileHash -Path $apkDest -Algorithm SHA256).Hash.ToLower()
$checksumFile = Join-Path $distDir "$apkName.sha256"
Set-Content -Path $checksumFile -Value "$hash  $apkName" -NoNewline

Write-Host ""
Write-Host "Release APK: $apkDest"
Write-Host "SHA-256:     $hash"
Write-Host "Checksum:    $checksumFile"
Write-Host ""
Write-Host "Fill docs/RELEASE_NOTES.template.md and publish the APK with the checksum."
