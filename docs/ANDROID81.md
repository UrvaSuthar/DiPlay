# Android 8.1 port (branch `android-8.1`)

Unofficial DiPlay port for Android 8.0/8.1 (API 26/27) head units, first target MAXLINK GD230
(Allwinner T3L, armeabi-v7a). Based on upstream 0.2.13. Not Apple-certified.

## What changed
- `minSdk` 26 (was 28); native `APP_PLATFORM` android-26.
- API 27/28 calls guarded (`// android-8.1:` comments): Wi-Fi P2P channel close, location check,
  LocalOnlyHotspot BSSID parsing, BYD HUD diagnostics.
- AirPlay sockets fall back to IPv4 when the kernel has no IPv6 (`WildcardBind`).
- Wi-Fi Direct hidden below Android 9; a saved Wi-Fi Direct choice becomes Built-in car hotspot.
  Wireless on 8.x: Built-in car hotspot or Existing Wi-Fi / Same LAN.

## Verified in CI (`Android 8.1 port` workflow)
- Builds with minSdk 26; upstream unit tests and `:mobile` lint pass; no API 27/28 lint
  findings in `:shared`/`:common`.
- APK contains armeabi-v7a libraries.
- Installs and launches on an API 27 x86_64 emulator without crashing.

## Not verified (needs the real head unit)
- Wired USB CarPlay (USB host role, data port, local VPN permission).
- Allwinner T3L H.264 decoding at 30 fps.
- Whether the unit has 5 GHz Wi-Fi; built-in car hotspot and Existing Wi-Fi / Same LAN wireless.
- Bluetooth handshake with the iPhone.
- 32-bit ARM native libraries at runtime.
- Upstream API 29+ calls that lint flags in the libraries pre-date this port; any unguarded one
  would also affect Android 9 upstream.

A CI APK has no accessory identity, so the iPhone handshake cannot complete with it.

## First GD230 test
1. Confirm API level: Settings → About is cosmetic; use a device-info app (expect API 27, armeabi-v7a).
2. Install the `DiPlay-android81-debug` APK from the latest `Android 8.1 port` run (debug package
   `com.shihab.diplay.hudtest`, so it installs beside any official DiPlay).
3. Open DiPlay, browse Settings and Connection setup; it must not crash.
4. Check Wi-Fi: can the unit see or create a 5 GHz network?
5. Save Settings → Diagnostics → Save diagnostic report and attach it to an issue on this fork.
