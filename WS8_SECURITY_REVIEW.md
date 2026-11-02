# WS8 — privacy and security hardening (PHA-4856)

Scope: what Chlorophone keeps on the phone, what leaves it, and what it
trusts. This file covers what changed in WS8 and what was reviewed, including
known gaps that are still open. Nothing here was tested against any
third-party server. Any upstream issue named below is unproven.

## What changed

| Area | Before | After |
|---|---|---|
| Bookmark secrets (server password, channel password, privilege key) | Plaintext JSON in DataStore (`PlntDataStore`), included in cloud backup and device transfer | Encrypted vault `plnt_bookmark_secrets` (EncryptedSharedPreferences, Android Keystore master key), excluded from both. The DataStore blob keeps only public fields |
| Migration | — | On first load, legacy secrets move into the vault. The plaintext copy is removed **only after** the vault write commits. If the vault can't be read or written, nothing is removed and the move is retried on the next load |
| Keystore unavailable | n/a | Fails closed: the bookmark is saved without its secrets, the user is told (`SECRETS_NOT_STORED`), and the secrets stay in memory for that run only. There is no plaintext fallback and no bootstrap key |
| Diagnostics (`Diag`) | On in every debug build, which is the APK CI hands to testers: an automatic trace upload to a Nextcloud drop, a stall watchdog, and logcat lines | Off by default in every build. The user turns it on in Settings > Diagnostics. Every line goes through `DiagRedactor` before it is written. Turning it off stops uploads and deletes the local trace. The opt-in is per-phone and excluded from backup |
| Debug auto-join on open | Debug builds connected to the last server as soon as the app opened. With a persisted open-mic setting, that meant transmitting with no tap | Removed. Connecting always takes a tap. The restart-reconnect of a session the user started (#3290) is unchanged |
| Release build | Unsigned, never built in CI | Signed with the existing shared key when the CI secret is present, built and audited on every run, and kept as an artifact |
| Audit | — | `tools/audit_apk.py` runs in CI on the debug and release APKs (merged manifest, plus BuildConfig for release). `BackupRulesTest` and `ManifestAuditTest` run on the host |

## Tests

* `data/BookmarkSecretsMigrationTest`: legacy migration, idempotence, a vault-write failure that loses nothing, a vault-read failure that wipes nothing, save never writing plaintext, Keystore failure failing closed, and delete dropping secrets.
* `security/BackupRulesTest`: scans the source for every `openEncryptedPrefs` store and checks that each one, plus the diagnostics files, is excluded in cloud-backup, device-transfer and `backup_rules.xml`.
* `security/ManifestAuditTest`: checks the source manifest: not debuggable, no cleartext, only `MainActivity` exported, every component declares `exported`, no `autoVerify` on hosts we don't own.
* `service/DiagRedactorTest`: secrets, nicknames, IPs, hostnames, URLs, e-mails and key blobs are removed. Stack traces, timings and states are kept.

## Reviewed: external components

| Component | Version / pin | Notes |
|---|---|---|
| tsclientlib / tsproto (ReSpeak) | git rev `ee3bc6f` | Pinned by commit. Provides TeamSpeak transport crypto (ECDH + EAX). `default-tls` (reqwest/rustls) is used only for a nickname lookup the app never calls |
| audiopus / libopus | 0.3.0-rc.0 | Decodes untrusted network audio. Parser bugs here would be remote input. Keep it current |
| io.github.webrtc-sdk:android | 125.6422.07 | Community Maven build of upstream M125, not Google-published. Handles untrusted video and DTLS from peers. M125 is behind upstream security fixes, so tracking a newer build is a follow-up |
| androidx.security:security-crypto | 1.1.0-alpha06 | Alpha. Google has deprecated the library. It works and is Keystore-backed, but plan a move to Tink/Keystore directly. Do not copy PocketTS's key handling |
| JNA | 5.14.0 | Loader for the UniFFI bindings |
| zxing-core, org.json | 3.5.3, Android built-in | Low risk |

PocketTS (GPLv3) was used as reference only. No code was taken from it.

## Reviewed: identity, TLS, crypto

* **Identity PEM**: encrypted (`plnt_identity`), backup-excluded, with one canonical copy. If the store is unreadable it is reset, and a new identity is minted on the next interactive use (a headless restart never mints one).
* **myTeamSpeak**: `AccountClient` uses `HttpsURLConnection` with the platform trust store and no redirects. `AccountCrypto` uses AES-GCM for key unwrap, and AES-ECB only as the block primitive of TeamSpeak's own CTR variant (decrypt-only interop). Imported identities sit in the encrypted identity store and fail closed when missing.
* **Invite lookup**: HTTPS to `invites.teamspeak.com`, no redirects.
* **Cleartext**: no `usesCleartextTraffic` and no network security config, so the platform default applies (off, targetSdk 35).
* **TeamSpeak server authenticity**: the protocol has no CA, and tsclientlib does not pin a server's key across connects (no TOFU). An on-path attacker on the *first* connect is not detectable. This is open, and it is a protocol and core-library limitation, not something WS8 changed.
* **Deep links**: `ts3server`/`tsserver`/`teamspeak` and `tmspk.gg` filters have no `autoVerify`. Note that the `tmspk.gg` filter uses `android.intent.DEFAULT`/`android.intent.BROWSABLE` as category names instead of `android.intent.category.*`, so it likely never matches a browser VIEW intent. That is a functional bug from PHA-4849, outside WS8. Pasting a link still works.

## Update trust

There is no in-app updater. APKs reach testers as GitHub Actions artifacts signed with one shared key held in repo secrets (`PLNT_KEYSTORE_B64`). Trust therefore rests on the `phattbeats` GitHub account and that secret. No key was rotated in WS8.

## Open limitations

1. **Testers still receive the debuggable build** unless someone switches them to the `plnt-app-release-apk` artifact. A debuggable app lets anyone with ADB on an unlocked phone attach and use its Keystore keys, which defeats the encryption above for that attacker. Switching is an owner decision.
2. **The diagnostics drop token is in the APK.** The token is upload-only (it can't list or read). Rotating it or moving it behind a relay is a secret-rotation task, so it was not done here.
3. **Redaction is pattern-based**: best-effort, tested on the shapes the trace actually produces. An unusual hostname TLD or a secret under an unrecognised key name could pass. It only applies after the user opts in.
4. **Old plaintext bytes**: DataStore rewrites its file atomically, so the plaintext blob is replaced, not overwritten in place. Freed flash blocks are not scrubbed.
5. A cloud backup taken **before** this update may still contain plaintext bookmark secrets. Only new backups are clean.
6. Device and real-account acceptance is still the open gate set by the owner on PHA-4850.
