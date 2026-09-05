# plnt-android

PLNT Android client: a voice-only TeamSpeak 3/6 client built on
[`ReSpeak/tsclientlib`](https://github.com/ReSpeak/tsclientlib) (pinned to
commit `ee3bc6f`) and a Kotlin/Compose UI. This repo packages the Rust
core as Android loadable libraries (`.so` files) via
[`cargo-ndk`](https://github.com/bbqsrc/cargo-ndk) and exposes it to
Kotlin through [UniFFI](https://github.com/mozilla/uniffi-rs).

Goal of PHA-3074: `./build.sh` produces an installable `app-debug.apk`
on a clean machine. Voice scope only — connect, channel tree,
who-is-talking, join channel, push-to-talk / open mic, mute/deafen,
Bluetooth headsets. No chat, no streams, no file transfer, no
TS6-only features.

## Status

| Phase | Status |
|---|---|
| PHA-3073 voice spike (Rust core → TS6 server, 4 s of 440 Hz) | **PASS** — spike write-up + voicespike harness: <https://nextcloud.phatt.vip/s/MgjsSdgkA8M2q9S/download> |
| PHA-3074 toolchain (this repo) | **SCAFFOLD COMPLETE + CI WIRED** — Cargo workspace + `plnt-core` (verified `cargo build`/`cargo test` clean host-side) + UniFFI bindings (generated, committed at `app/app/src/main/kotlin/com/plnt/client/uniffi/plnt_core/plnt_core.kt`) + Gradle/Compose app + `build.sh` + `.github/workflows/build.yml` (CI runs on Ubuntu 24.04). Live APK build needs a CI runner with Rust + JDK 17 + Android SDK 34 + NDK 27 — wired in the workflow. |

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
├── .github/workflows/
│   └── build.yml                     CI: ubuntu-24.04, builds via build.sh
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
| cargo-ndk | 4.1.0 | Pinned for reproducibility |
| uniffi | 0.27 | UniFFI binding generator + scaffolding |
| tsclientlib | `ee3bc6f` (commit pin) | The version proven by the spike (PHA-3073) |
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

You should see "PLNT" and `core_version()` rendered on the device
screen, plus a logcat line containing `plnt-core 0.1.0 (tsclientlib@ee3bc6f …)`.

## Architecture decisions (locked from the spike)

- **Voice only.** Connect, channel tree, who-is-talking, join channel,
  push-to-talk / open mic, mute/deafen, Bluetooth headsets that
  actually work. No chat, no streams, no file transfer, no TS6-only
  features.
- **Rust core** wraps `tsclientlib` + `audiopus` (Opus encoder/decoder).
- **Kotlin audio** uses `VOICE_COMMUNICATION` stream type.
- **Foreground service** with `FOREGROUND_SERVICE_MICROPHONE` for
  background voice.
- **Compose UI** with the PLNT palette (colors locked from the spike).

The build pipeline delivers the bridge for those features. Voice-session
APIs (connect, channel tree, PTT) are scheduled for follow-up tickets —
they need a real-device test plan and Bluetooth headset validation
beyond what `build.sh` produces.

## Identity policy

Every commit in this repo must satisfy the PHATT-TECH commit-identity
rule:

- Author and committer must be `phattbeats <obiwouldjablowme@protonmail.com>`.
- Zero `Co-authored-by` trailers (no Paperclip-Paperclip contributor).
- Push via direct `git push` (not via the Paperclip GitHub App).
- Always override identity before committing:
  ```bash
  git config user.name  "phattbeats"
  git config user.email "obiwouldjablowme@protonmail.com"
  ```

## Where this came from

- **Spike write-up** (PHA-3073 plan + voice proof): <https://nextcloud.phatt.vip/s/MgjsSdgkA8M2q9S/download>
- **Two-client voice test harness** (`tsclientlib/examples/voicespike.rs`): <https://nextcloud.phatt.vip/s/2CstCCfpZgHyGws/download>
- **Spike build notes**: `/root/work/pha3073/BUILD_NOTES.md`
