# Generate a release signing keystore for Glyph APK distribution.
$ErrorActionPreference = "Stop"
$root = Split-Path $PSScriptRoot -Parent
$signingDir = Join-Path $root "signing"
$keystore = Join-Path $signingDir "release.keystore"
$alias = "glyph"

if (Test-Path $keystore) {
    Write-Error "Keystore already exists: $keystore. Delete it first if you intend to regenerate."
}

$javaHome = $env:JAVA_HOME
$localProps = Join-Path $root "local.properties"
if (Test-Path $localProps) {
    Get-Content $localProps | ForEach-Object {
        if ($_ -match '^\s*java\.home=(.+)$') {
            $javaHome = $matches[1].Trim() -replace '\\:', ':' -replace '\\\\', '\'
            $env:JAVA_HOME = $javaHome
        }
    }
}

$keytool = if ($javaHome) { Join-Path $javaHome "bin\keytool.exe" } else { "keytool" }
if (-not (Get-Command $keytool -ErrorAction SilentlyContinue)) {
    Write-Error "keytool not found. Set java.home in local.properties or JAVA_HOME."
}

New-Item -ItemType Directory -Force -Path $signingDir | Out-Null

Write-Host "Creating release keystore at: $keystore"
Write-Host "You will be prompted for keystore and key passwords. Remember them for keystore.properties."
Write-Host ""

& $keytool -genkeypair -v `
    -keystore $keystore `
    -alias $alias `
    -keyalg RSA `
    -keysize 2048 `
    -validity 10000 `
    -dname "CN=Glyph, OU=Mobile, O=LangOverlay, L=Unknown, ST=Unknown, C=US"

Write-Host ""
Write-Host "Next steps:"
Write-Host "  1. Copy keystore.properties.example to keystore.properties"
Write-Host "  2. Set storePassword and keyPassword in keystore.properties"
Write-Host "  3. Run: .\scripts\build-release.ps1"
Write-Host ""
Write-Host "Back up signing/release.keystore and passwords in a secure location."
