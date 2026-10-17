# Changelog

All notable changes to plnt-android. Format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/); this project uses
semantic-ish versioning where `versionCode` is the bump that matters for
Android installs.

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

## [Unreleased]

### Added

- Settings screen imports an identity from a `.ini` file, accepting either
  `id=` or `name=` for the identity label (PHA-4116).
- One live share at a time, with screen-share video settings (PHA-4176).
