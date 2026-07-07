# Release APK distribution

Glyph is distributed as a signed release APK for manual (sideload) installation. It is not published on Google Play.

## One-time setup

### 1. Create a release keystore

```powershell
.\scripts\generate-release-keystore.ps1
```

This creates `signing/release.keystore` (gitignored). **Back up the keystore and passwords securely.** You cannot publish updates with the same package name if the keystore is lost.

### 2. Configure signing

```powershell
Copy-Item keystore.properties.example keystore.properties
# Edit keystore.properties with your passwords
```

### 3. Bump version (each release)

Edit [`version.properties`](../version.properties):

```properties
VERSION_CODE=2
VERSION_NAME=1.1.0
```

`VERSION_CODE` must increase monotonically for every release.

## Build a release APK

```powershell
.\scripts\build-release.ps1
```

Output:

- `app/build/outputs/apk/release/app-release.apk` — Gradle default
- `dist/glyph-v{VERSION}-release.apk` — distribution copy with checksum

## Release checklist

- [ ] Version bumped in `version.properties`
- [ ] Release notes filled from `docs/RELEASE_NOTES.template.md`
- [ ] `.\scripts\build-release.ps1` succeeds
- [ ] Install release APK on a clean device (not debug)
- [ ] Onboarding, accessibility, overlay, and typing session verified
- [ ] SHA-256 checksum published with release notes
- [ ] Keystore backed up offline

## Release vs debug

| | Debug (`app-debug.apk`) | Release (`glyph-v*-release.apk`) |
|--|-------------------------|----------------------------------|
| Signing | Debug certificate | Your release keystore |
| `debuggable` | `true` | `false` |
| R8 minify | No | Yes |
| Resource shrink | No | Yes |
| Play Protect | Higher scrutiny | Lower scrutiny |
| For end users | **No** | **Yes** |

## Signing configuration

Signing is loaded from `keystore.properties` at the project root. The release build **fails** if this file or the keystore is missing.

## App icon

The launcher icon is an adaptive icon (API 26+, matches `minSdk`). Foreground: keyboard-style bars on blue background. Replace `app/src/main/res/drawable/ic_launcher_foreground.xml` and `values/ic_launcher_background.xml` before major public releases if branding is updated.

## ProGuard / R8

Release builds use R8 with `proguard-android-optimize.txt` and [`app/proguard-rules.pro`](../app/proguard-rules.pro). If release crashes on startup, check `app/build/outputs/mapping/release/mapping.txt` and add keep rules as needed.
