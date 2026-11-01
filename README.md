# Chlorophone for TeamSpeak

**by PHATT TECH**

An independent mobile TeamSpeak client: native Android UI/audio, a shared Rust protocol/voice core, and an iOS companion implementation. The repository and Android application ID retain their historical PLNT names; the product is **Chlorophone**.

## Product scope and status — 2026-11-01

The original voice-only milestone is historical, not the current product limit. Approved scope includes **TeamSpeakID/myTeamSpeak account import, camera and screen sharing, files, chat/history, administration, a cleaner interface, and production-quality voice/security/release behavior**. See [ROADMAP.md](ROADMAP.md) and [PHA-4848](https://paperclip.phatt.vip/issues/PHA-4848), under the Android app epic [PHA-3072](https://paperclip.phatt.vip/issues/PHA-3072).

Implementation is not the same as delivery or device acceptance:

| Area | Current main source | Remaining acceptance / scope |
| --- | --- | --- |
| Voice/connections | Rust/tsclientlib + Opus, channel tree, PTT/open mic, mute/deafen, routing, foreground service, reconnect/session recovery | Hardware/network matrix, partial-playback-write fix, protected-channel passwords and invite targets; VAD, whisper, overlay PTT and call interruption |
| Chat | Android channel/private send and receive | Persistent history, unread/pokes and richer media workflows |
| Screen sharing | Android send screen + playback audio and watch TS6 WebRTC streams | TURN/NAT coverage, PiP/overlay, camera sharing and lifecycle/permission acceptance; see [STREAMING.md](STREAMING.md) |
| TeamSpeakID / myTeamSpeak | Separate account sign-in/import candidate in [PR #24](https://github.com/phattbeats/plnt-android/pull/24), not merged main | Test APK recorded in PHA-4344; owner real-account login/OTP, sync/import and imported-identity voice acceptance remain unverified. Reuse PHA-4109/4116/4350 recovery |
| Files/admin | No complete user-facing workflows in audited main | File browser/transfers/manage operations and permission-aware user/channel administration are approved |
| iOS | SwiftUI/shared Rust core, connection/channel/chat paths and unsigned build pipeline | Physical-device acceptance PHA-4553; no iOS streaming parity claim. See [ios/README.md](ios/README.md) |
| Releases/privacy | Debug APK workflow and historical prerelease | Signed production delivery, signing-key continuity, opt-in/redacted diagnostics, encrypted bookmark secrets and backup policy |

The source audit is pinned to main commit 3e49c75. It is not a claim that every feature passed on a real phone. PocketTS was reviewed as a comparison reference; its GPLv3 code is not copied into this project's license.

## Installing and testing

- Android main requires Android 8.0+ (API 26). Development builds are sideloaded APKs.
- Latest public GitHub prerelease at this update: v0.1-invite-links (October 9), a debug-signed historical build. Do not assume it includes current-main or account-sign-in candidate features.
- Account-import testing must use the exact build/instructions recorded in PHA-4344. Export existing identities first. Never put credentials, recovery keys or private identities in logs/bug reports.
- iOS requires iOS 16+ and a sideload/signing workflow; see its README.
- Release signing changes, production publishing, real-account tests and service mutations are separately gated; a successful build alone does not approve them.

## Architecture

- Kotlin/Jetpack Compose Android UI and service-owned audio/session lifecycle.
- Rust plnt-core wrapping pinned tsclientlib and audiopus, bridged with UniFFI.
- Android WebRTC streaming alongside the protocol core.
- SwiftUI iOS client reusing the Rust core; platform features tracked separately.

Retain the foundation and deliver incremental tested slices rather than a wholesale protocol rewrite. [AUDIO_ENGINE.md](AUDIO_ENGINE.md), [FOREGROUND_SERVICE.md](FOREGROUND_SERVICE.md) and [MANUAL_TEST_CHECKLIST.md](MANUAL_TEST_CHECKLIST.md) retain implementation/historical verification details; use this current status and the roadmap when early milestone notes describe narrower scope.

## Layout

```
plnt-android/
├── core/                             Rust workspace (the .so producing crate)
│   ├── Cargo.toml                    tsclientlib (git pinned) + audiopus + uniffi
│   ├── build.rs                      UniFFI scaffolding generator
│   ├── src/
│   │   ├── lib.rs                    core_version() smoke function
│   │   └── plnt_core.udl             UniFFI Definition Language
│   └── tests/smoke.rs                Host-side test of core_version()
├── tools/
│   └── plnt-uniffi-bindgen/         Workspace-local CLI shim — `cargo run --bin
│       │ plnt-uniffi-bindgen -- generate ...` because upstream
│       │ `uniffi_bindgen` is library-only (no [[bin]]).
├── app/                              Android Gradle project
│   ├── settings.gradle.kts
│   ├── build.gradle.kts              Top-level (Android-application plugin)
│   ├── gradle.properties
│   ├── gradle/libs.versions.toml     Pinned AGP 8.5.2 / Kotlin 2.0.21 / Compose BOM 2024.10.01
│   └── app/
│       ├── build.gradle.kts          Module-level
│       ├── proguard-rules.pro         Keep UniFFI bridge classes
│       └── src/main/
│           ├── AndroidManifest.xml   RECORD_AUDIO + BLUETOOTH_CONNECT +
│           │                          FOREGROUND_SERVICE_MICROPHONE per locked architecture
│           ├── kotlin/com/plnt/client/
│           │   ├── MainActivity.kt
│           │   └── uniffi/plnt_core/plnt_core.kt   Generated UniFFI binding
│           └── res/values/themes.xml
├── ios/                              iOS client (PHA-4552) — SwiftUI app, same Rust core
│   ├── README.md                     Build + sideload instructions
│   ├── build-ios.sh                  Rust core for aarch64-apple-ios + Swift bindings
│   ├── project.yml                   XcodeGen spec (the .xcodeproj is generated)
│   ├── PlntCoreFFI/                  Committed UniFFI Swift bindings
│   └── Chlorophone/                  The Swift app
├── .github/workflows/
│   ├── build.yml                     CI: ubuntu-24.04, builds via build.sh
│   └── build-ios.yml                 CI: macos-14, builds the unsigned .ipa
├── Makefile                          Quick local targets (verify, version, bindings, clean)
├── build.sh                          One-command build
├── Cargo.toml                         Workspace root (members: core, tools/*)
└── README.md                         (this file)
```

## Pinned toolchain versions

| Component | Version | Why |
|---|---|---|
| Android Gradle Plugin | 8.5.2 | Stable Aug 2024, Java 17 required |
| Kotlin | 2.0.21 | Compose Compiler plugin built into Kotlin since 2.0 |
| Gradle | 8.10.2 | Matches AGP 8.5.x requirements |
| Compose BOM | 2024.10.01 | Material3 + tooling |
| Android compileSdk | 35 | Android 15 (target SDK) |
| Android minSdk | 26 | Android 8.0 — matches the locked architecture spec |
| Android NDK | 27.0.12077973 | Required for `cargo-ndk 4.x` |
| Android Platform | android-34 | NDK 27 platform target |
| Rust targets | aarch64-linux-android, x86_64-linux-android | arm64-v8a devices + x86_64 emulator |
| iOS Rust targets | aarch64-apple-ios, aarch64-apple-ios-sim | iPhone + iOS Simulator (PHA-4552) |
| iOS deployment target | 16.0 | SwiftUI app minimum |
| cargo-ndk | 4.1.0 | Pinned for reproducibility |
| uniffi | 0.27 | UniFFI binding generator + scaffolding |
| tsclientlib | `ee3bc6f` (commit pin) | The version proven by the spike (#3073) |
| audiopus | 0.3.0-rc.0 | Matches tsclientlib's optional `audio` feature |

## Reproducible build on a clean machine

### Prerequisites (zero-state)

```bash
# Linux x86_64, Debian / Ubuntu recommended.
# Required: curl, git, tar, unzip, ca-certificates.
sudo apt-get update && sudo apt-get install -y curl git tar unzip ca-certificates
```

### Run

```bash
./build.sh
```

That single script:
1. Installs `rustup` + stable Rust if missing.
2. Adds the two Android Rust targets.
3. Installs `cargo-ndk 4.1.0` and `uniffi_bindgen-cli`.
4. Installs Android cmdline-tools, NDK 27.0.12077973, platform `android-34`, build-tools 35.0.0.
5. Bootstraps the Gradle wrapper (requires Java 17 on `PATH` first; install via `apt install openjdk-17-jdk`).
6. Generates the UniFFI Kotlin bindings from `core/src/plnt_core.udl`.
7. Cross-compiles `plnt-core` for both targets and drops the `.so` files in `app/app/src/main/jniLibs/<target>/`.
8. Runs `./gradlew assembleDebug`.
9. Verifies `app/app/build/outputs/apk/debug/app-debug.apk` exists.

### Manual environment overrides

```bash
# Already have NDK 26? Override:
NDK_VERSION=26.3.11579264 ./build.sh

# Already have Android SDK at a non-default path?
ANDROID_HOME=/my/sdk ./build.sh

# Only need one architecture?
RUST_TARGETS="aarch64-linux-android" ./build.sh
```

## Install + smoke (x86_64 emulator)

After `./build.sh` succeeds:

```bash
# x86_64 emulator from Android Studio's Device Manager, or:
emulator -avd Pixel_7_API_34 -no-snapshot-load &
adb wait-for-device
adb install -r app/app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.plnt.client/.MainActivity
adb logcat -d | grep -E 'plnt|plnt_core' | tail -20
```

You should see the "PLNT" bookmarks screen. Tap the gear icon → **Settings →
About**: the last line there is `core_version()`, rendered straight from the
Rust core, e.g. `plnt-core 0.1.0 (tsclientlib@ee3bc6f …)`. If the native
library failed to load it reads `unavailable: <exception>` instead.

## Historical spike scope

The September voice-only spike established the core architecture, not a permanent restriction. Its exclusions are superseded by ROADMAP.md. Android source includes chat and TS6 screen sharing; camera, files and admin are approved work, not shipped-feature claims.

## Identity policy

Every commit in this repo must satisfy the PHATT-TECH commit-identity
rule:

- Author and committer must be `phattbeats <21150921+phattbeats@users.noreply.github.com>`.
- Zero `Co-authored-by` trailers (no Paperclip-Paperclip contributor).
- Push via direct `git push` (not via the Paperclip GitHub App).
- Always override identity before committing:
  ```bash
  git config user.name  "phattbeats"
  git config user.email "21150921+phattbeats@users.noreply.github.com"
  ```

## Where this came from

- **Spike write-up** (#3073 plan + voice proof): <https://nextcloud.phatt.vip/s/MgjsSdgkA8M2q9S/download>
- **Two-client voice test harness** (`tsclientlib/examples/voicespike.rs`): <https://nextcloud.phatt.vip/s/2CstCCfpZgHyGws/download>
- **Spike build notes**: `/root/work/pha3073/BUILD_NOTES.md`
