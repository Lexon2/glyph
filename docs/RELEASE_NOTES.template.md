# Glyph v{VERSION_NAME}

**Release date:** {YYYY-MM-DD}  
**Version code:** {VERSION_CODE}  
**Package:** `com.langoverlay.app`  
**Min Android:** 8.0 (API 26)  
**Target Android:** 15 (API 35)

## What's new

- {Describe user-visible changes}

## Bug fixes

- {Describe fixes, or "None."}

## Requirements

- Samsung tablet (or Android device) with an external Bluetooth keyboard
- Samsung Keyboard with multiple languages configured
- **Accessibility service** — required for layout shortcut detection and input-session tracking
- **Display over other apps** — required for the layout overlay
- **Notifications** (Android 13+) — optional; used for permission restore alerts

## Installation (sideload)

1. Download `glyph-v{VERSION_NAME}-release.apk`
2. Verify the SHA-256 checksum below matches your download
3. Install the APK (enable "Install unknown apps" for your browser/files app if prompted)
4. Open Glyph and complete onboarding
5. On Android 13+: App info → ⋮ → **Allow restricted settings**, then enable the accessibility service
6. Grant overlay permission when prompted

## Permissions used

| Permission | Purpose |
|------------|---------|
| Display over other apps | Show the layout indicator bubble |
| Accessibility service | Detect keyboard shortcuts and typing sessions |
| Receive boot completed | Optional health check after reboot (if enabled in settings) |
| Post notifications | Alert when permissions need to be restored |

**This app does not request Internet access and does not collect or transmit user data.**

## Known issues

- {List known issues, or "None."}

## Checksums

| File | SHA-256 |
|------|---------|
| `glyph-v{VERSION_NAME}-release.apk` | `{SHA256_HEX}` |

## Upgrading

Install over the previous version without uninstalling. Settings are preserved unless you clear app data.

## Support

{Link to issue tracker or contact method}
