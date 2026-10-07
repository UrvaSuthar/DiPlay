# Handoff — DiPlay Android 8.1 port (2026-10-08)

## Goal
Run DiPlay (CarPlay receiver) on the MAXLINK GD230 head unit: Allwinner T3L, armeabi-v7a,
real Android 8.1 / API 27 (Settings shows "Android 11" — cosmetic; build is `t3_p1-eng 8.1.0 OPM1.171019.026`).

## State: implementation done, hardware test pending
- Branch `android-8.1` on UrvaSuthar/DiPlay, base upstream 0.2.13 (`4bccb39`), head `67771f6`.
- CI `Android 8.1 port` green: unit tests, `:mobile` lint, library API 27/28 lint gate, armeabi-v7a check,
  API 27 emulator install + launch smoke test. Upstream `Android checks` also green on the branch.
- Spec: `docs/superpowers/specs/2026-10-07-android-8.1-port-design.md`
- Plan: `docs/superpowers/plans/2026-10-07-android-8.1-port.md`
- Status + GD230 checklist: `docs/ANDROID81.md`
- Test APK: artifact `DiPlay-android81-debug` from the latest `Android 8.1 port` run
  (debug package `com.shihab.diplay.hudtest`).

## What changed (all marked `// android-8.1:`)
- minSdk 28 → 26; `APP_PLATFORM=android-26`.
- API 27/28 calls guarded: P2P `Channel.close`, `isLocationEnabled`, LocalOnlyHotspot BSSID (`legacyBssid`),
  BYD HUD diagnostics, debug HUD demo.
- `WildcardBind`: `::` socket binds fall back to IPv4 (AirPlay screen/event/keepalive/audio/NTP + microphone);
  `WildcardBindCoverageTest` fails on any new direct `::` bind.
- Wi-Fi Direct hidden below API 28; saved WIFI_P2P loads as MANUAL.

## Manually verified on a local API 27 arm64 emulator
App launches, no crashes; Connection setup shows only Built-in car hotspot and Existing Wi-Fi / Same LAN;
mode persists across restart. Connect is blocked by missing accessory identity (`setupError`), and the
emulator has no USB passthrough or Bluetooth, so no iPhone session is possible there.

## Blockers / open decisions
- **Accessory identity**: CI/source builds omit it, so no CarPlay handshake. Upstream's release APK bundles
  an identity recovered from Carlinkit firmware; adding it is the owner's legal call. Not done.
- **Hardware unknowns**: USB CarPlay, T3L decoder, 5 GHz Wi-Fi, Bluetooth, 32-bit ARM at runtime.

## Next steps
1. Install the CI APK on the GD230; run the `docs/ANDROID81.md` checklist; save a diagnostic report.
2. Optional: test wireless CarPlay with official DiPlay 0.2.13 on any Android 9+ device (Same LAN mode).
3. Decide: PR `android-8.1` → fork `main`; post on upstream issue #136 (public — needs explicit OK).

## Deferred minor review findings
- Library lint gate passes if a lint XML report is missing.
- InlinedApi API 27/28 constants not reported.
- "Reset CarPlay Wi-Fi" still shown on 8.x; leaks a P2P channel per tap on API 26 only.
- `WildcardBind.create()` outside try — add a comment explaining why.
- `legacyBssid` stricter than `MacAddress` (2 hex digits per octet).
- Wi-Fi Direct cutoff P (home screen) vs Q (in-session pickers) — pre-existing.

## Local machine state
- AVD `diplay81` (~/.android/avd) + image ~/Library/Android/sdk/system-images/android-27 (~3.8 GB total);
  start: `~/Library/Android/sdk/emulator/emulator -avd diplay81 -no-snapshot`. Delete both to reclaim space.
- Disk was near full; caches cleared 2026-10-08 (~4 GB). No local clone kept (work lives on the fork).
