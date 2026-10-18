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

Everything below is **merged to `main` but not yet in an APK**. `versionCode`
is still 1 and `versionName` still `0.1.0`, because no build has been cut
since `v0.1-invite-links` — these entries ship with the next one.

### Added

- iOS client (`ios/`) — the same Rust core in a SwiftUI shell (PHA-4552).
  Sideloaded only, no licensing: CI produces an unsigned `.ipa` on a macOS
  runner, and AltStore/SideStore re-signs it with a free personal team.
  `AVAudioSession` `.playAndRecord` / `.voiceChat` is the iOS counterpart of
  Android's `VOICE_COMMUNICATION` source, so a Bluetooth headset's microphone
  is used and the platform echo canceller engages.
- Settings screen imports an identity from a `.ini` file, accepting either
  `id=` or `name=` for the identity label (PHA-4116).
- One live share at a time, with screen-share video settings (PHA-4176).
- Channel passwords work, end to end (PHA-4849). `join_channel` used to
  accept a password and drop it on the floor, and the command that did go
  out carried the password in the clear instead of TeamSpeak's
  `base64(sha1(password))` form — so a password-protected channel could not
  be entered no matter what was typed. Tapping a protected channel now asks
  for the password, and the password reaches the server correctly.
- TeamSpeak's own link host, `tmspk.gg` (PHA-4849): `tmspk.gg/s=host?…` is
  read as a server link, and `tmspk.gg/<code>` is an invite, resolved
  against TeamSpeak's invite service. An `https://tmspk.gg/…` link opens
  Chlorophone (Android offers it in the "Open with" chooser — the domain is
  not ours, so there is nothing to verify against).
- Privilege keys (PHA-4849): a `token=` in a link is spent once, on the
  connection it comes with, through TeamSpeak's `privilegekeyuse`. The
  server's answer reaches the app, so a refused key is visible.
- An invited channel is joined (PHA-4849). The channel an invite names — by
  id or by a path of names — is joined on arrival, with its password when
  one came with it.
- The identity's security level can be raised (PHA-4849). TeamSpeak's level
  is a hash-cash property of the identity, so raising it is CPU work:
  Settings → Identity → Security level. Level 12 is instant, 16 takes a
  moment, 18 and 20 take minutes.
- An empty port means "let the server's own DNS entry decide" (PHA-4849) —
  the `_ts3._udp.` SRV record, or TSDNS. A TeamSpeak *nickname* needs it:
  the nickname lookup is what knows the server's port.
