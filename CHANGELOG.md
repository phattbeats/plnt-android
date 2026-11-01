# Changelog

All notable changes to Relay for Teamspeak. Format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/); this project uses
semantic-ish versioning where `versionCode` is the bump that matters for
Android installs.

## [0.1.1-dev] - Test candidate

- Apply Relay for Teamspeak branding with the by PHATT TECH byline.
- Import an exported TeamSpeak identity file with a preview, bounded background reads, and visible errors.
- Keep identity replacement unavailable during a call or connection attempt.
- myTeamSpeak account sign-in and synced bookmark import are still under investigation.

- Package only ARM64 and x86_64, matching the native core.

## [0.1.0] - 2026-10-09

First public build, released as tag `v0.1-invite-links`.

### Added

- Server list with one-tap invite links — the reason for this release.
- Voice-only client against TeamSpeak 3.13.x (see `README.md` for scope).
- Reproducible build via `./build.sh`, wired to CI on Ubuntu 24.04.
  Toolchain pinned in `README.md` (Rust stable, NDK 27.0.12077973,
  android-34, build-tools 35.0.0, JDK 17).

### Known limitations

- Shipped artifact is a **debug-signed** `app-debug.apk`. It is fine for
  sideloading among friends; it is not Play-distributable.
- No chat, no file transfer, no streams, no TS6-only features. Voice only.

### Validated

- Physical device round 1 (PHA-3080): two phones plus Bluetooth earbuds
  against `teamspeak.phatt.vip`, reported passing by Brandon on 2026-10-18.
  That report is a human sign-off, not a CI run — no per-step transcript is
  recorded in-repo yet, so treat it as "round 1 did not turn up a defect"
  rather than as a full checklist attestation.

## [Unreleased]

Everything below is **on `main` or in the open account PR, but not yet in a
released APK**. The account test candidate is `versionCode` 3, `versionName`
`0.1.2-account-test`.

### Added

- myTeamSpeak account sign-in in Settings → myTeamSpeak (PHA-4850). It
  derives the login password locally, submits the recovered login RPC,
  handles verification-code (OTP) challenges including rejection of an invalid
  code, unwraps supported version 1/2 master keys, and requests the bookmark
  and identity classes with `requestServerItems`. It calls no sync-write,
  rekey, reset or account-change operation — the import is read-only.
- Synced servers and their identities are shown in an explicit selection
  preview before anything is saved locally. A server this phone already has is
  reported as already saved and is never added a second time.
- The synced folder a server came from is kept with the imported bookmark.
- Settings → myTeamSpeak → Sign out erases the imported identities and server
  passwords, removes the imported servers, and forgets this device's sign-in
  id. The myTeamSpeak session is *not* cancelled by this — the build cannot
  write to the account — so the owner revokes the device in the official
  client, and the UI says so.
- iOS client (`ios/`) — the same Rust core in a SwiftUI shell (PHA-4552).
  Sideloaded only, no licensing: CI produces an unsigned `.ipa` on a macOS
  runner, and AltStore/SideStore re-signs it with a free personal team.
  `AVAudioSession` `.playAndRecord` / `.voiceChat` is the iOS counterpart of
  Android's `VOICE_COMMUNICATION` source, so a Bluetooth headset's microphone
  is used and the platform echo canceller engages.
- Settings screen imports an identity from a `.ini` file, accepting either
  `id=` or `name=` for the identity label (PHA-4116).
- One live share at a time, with screen-share video settings (PHA-4176).

### Changed

- The app is named **Chlorophone** (PHA-4341): launcher icon, notification
  icon, and the About/Connected/Settings screens carry the mark 02 icon and
  the by PHATT TECH byline.
- Package only ARM64 and x86_64, matching the native core.
- Keep identity replacement unavailable during a call or connection attempt.
