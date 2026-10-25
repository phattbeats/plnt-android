# Chlorophone for iOS

The iOS client for the TeamSpeak server. Same product as the Android app in
`../app` — same Rust core (`plnt-core`), same server, same identity format —
different shell.

**Sideloaded only, no licensing (for now).** Nothing in this directory needs
an Apple Developer Program account, an App Store record, or a provisioning
profile. CI produces an *unsigned* `.ipa`; you install it with AltStore /
SideStore (or sign it yourself with a free personal team on a Mac).

Parent issue: PHA-4552. The Android epic it belongs to is PHA-3072.

## What is shared with the Android app

Everything that talks to the server. `core/` is the Rust `plnt-core` crate
(tsclientlib + Opus, exposed through UniFFI), and it is **the same crate** the
Android app ships — the iOS build cross-compiles it for `aarch64-apple-ios`
and links the archive into the app binary.

| Piece | Where it lives | Android | iOS |
|---|---|---|---|
| Protocol, Opus, channel tree, identity | `../core/src/lib.rs` | ✅ | ✅ (same code) |
| Voice capture / playback | `Chlorophone/AudioEngine.swift` | `AudioEngine.kt` | ✅ ported |
| UI | `Chlorophone/*.swift` | Compose | SwiftUI |
| Chat (channel + private) | `Chlorophone/ChatView.swift` | ✅ | ✅ ported |
| Screen share (TS6) | — | ✅ | ❌ not in v1 |

The iOS port is voice-first: connect, channel tree, who is talking, join
channel, push-to-talk or open mic, mute/deafen, Bluetooth headset routing,
and reconnect when the network comes back.

## The audio mapping (this is the part that matters)

The Android app's whole reason for existing is that the official TeamSpeak
app cannot use a Bluetooth headset's microphone. iOS has the same trap, and
the same fix:

- `AVAudioSession` category `.playAndRecord`, mode `.voiceChat`. That mode
  engages the voice I/O unit — the platform echo canceller works, and a
  paired headset is routed over HFP/SCO so its *microphone* is used, not the
  phone's. This is the direct counterpart of Android's
  `MediaRecorder.AudioSource.VOICE_COMMUNICATION`.
- `.defaultToSpeaker` when no headset is attached (the Android app changed to
  loudspeaker-by-default in PHA-4176 — the earpiece made other people
  inaudible unless the phone was held to the ear).
- Push-to-talk gates *sending*, never capture. Stopping the capture stream
  drops the Bluetooth link and the next press audibly clips the first
  syllable.
- `UIBackgroundModes: audio` keeps the call alive with the screen locked —
  the iOS counterpart of the Android foreground service.

## Building

macOS with Xcode (the iOS SDK is the only place an iOS binary can be built).

```bash
./ios/build-ios.sh          # Rust core for iOS + UniFFI Swift bindings
brew install xcodegen       # once
cd ios && xcodegen generate
cd ios && xcodebuild \
  -project Chlorophone.xcodeproj \
  -scheme Chlorophone \
  -configuration Release \
  -sdk iphoneos \
  CODE_SIGNING_ALLOWED=NO \
  build
```

`build-ios.sh` writes:

```
ios/build/lib/iphoneos/libplnt_core.a          device slice (arm64)
ios/build/lib/iphonesimulator/libplnt_core.a    simulator slice
ios/PlntCoreFFI/plnt_core.swift                 UniFFI Swift bindings (committed)
ios/PlntCoreFFI/plnt_coreFFI.h                  the C header they import
ios/PlntCoreFFI/plnt_coreFFI.modulemap          makes `import plnt_coreFFI` work
```

Two things about that script are deliberate:

- **Libopus is built by the script, not by cargo.** `audiopus_sys` builds the
  vendored Opus with CMake, and the `cmake` crate has no way to pass a
  toolchain file — so it would compile a *host* (macOS) library and then try
  to link it into an iOS binary. The script builds Opus with an explicit iOS
  toolchain and points `audiopus_sys` at the result.
- **The bindings are committed.** A human can open the project without
  installing the Rust toolchain, and CI proves the committed copy still
  matches the Rust source (the "no-op diff" step).

## Sideloading the build

CI uploads `chlorophone-ios-unsigned-ipa`. To put it on a phone:

1. Install AltStore (or SideStore) on the iPhone.
2. Host the `.ipa` somewhere the phone can reach (Nextcloud works — the same
   place the Android APK lives).
3. In AltStore, add the `.ipa` URL as a source and install it. AltStore
   re-signs the app with your own Apple ID, which is the "no licensing" part:
   no developer account, no $99/year.

The free personal-team signature expires after seven days; AltStore refreshes
it automatically while it is running.

## Layout

```
ios/
├── build-ios.sh               Rust core for iOS + Swift bindings
├── project.yml                XcodeGen spec (the .xcodeproj is generated)
├── PlntCoreFFI/               committed UniFFI Swift bindings
├── build/                     build outputs (gitignored)
└── Chlorophone/
    ├── ChlorophoneApp.swift   app entry + root view
    ├── AppModel.swift         state, the core connection, reconnect
    ├── AudioEngine.swift      AVAudioSession + capture/playback
    ├── Stores.swift           Keychain-backed identity and servers
    ├── Models.swift           channel tree, roster, chat
    ├── Theme.swift            palette (ported from the Android app)
    ├── BookmarksView.swift    saved servers
    ├── ConnectedView.swift    channel tree + talk controls
    ├── ChatView.swift         channel and private chat
    └── SettingsView.swift     talk mode, identity, about
```

## Known gaps (v1)

- **No screen share.** The TS6 streaming verbs are in the core
  (`sendRawCommand`), but the WebRTC stack that consumes the offer is an
  Android-only dependency.
- **No myTeamSpeak bookmark import.** The Android app can sign in with
  myTeamSpeak to pull synced bookmarks; the iOS app takes servers manually.
- **Unsigned builds only.** CI cannot produce a device-installable `.ipa`
  without a signing identity, so the artifact is unsigned by design.
