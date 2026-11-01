# Chlorophone full-client roadmap

Approved 2026-11-01. Tracking: [PHA-4848](https://paperclip.phatt.vip/issues/PHA-4848), nested under [PHA-3072](https://paperclip.phatt.vip/issues/PHA-3072).

**This is approved implementation scope, not a shipped-feature list.** Android is the initial delivery target; shared-core changes must not regress iOS, and platform parity must be reported honestly.

## Brandon authorization — 2026-11-01 Signal
Create this issue under the TeamSpeak Android app nest (PHA-3072). Attack the entire PocketTS comparison: camera sharing, file sharing, administration, cleaner interface, voice/reliability/security/release hardening and TeamSpeakID/myTeamSpeak. This explicitly supersedes the original voice-only exclusions; do not narrow to an MVP or drop advanced features. Implement incrementally with acceptance gates, not a single unsafe rewrite.

## Existing work / don't duplicate
Repo phattbeats/plnt-android (Chlorophone); current audited main 3e49c75. PocketTS reference dd8803b, latest release1.5.0; some main features unreleased. Comparison initially missed unmerged myTeamSpeak work. Reconcile all open PRs/worktrees first, especially PR#24 (account sign-in/import), PHA-4109, PHA-4116, PHA-4344 (test APK delivered, real-account/OTP/import acceptance not verified), PHA-4350 (protocol recovery). Preserve six descriptors, key derivation known-answer tests, evidence and owner acceptance work. Don't mark ID shipped merely because research is done. Existing iOS PHA-4552/4553, screen-share PHA-3289, chat PHA-3281, routing PHA-3282, release/signing PR#31 and branding PHA-4399/4403/4406 must be reused.

## Workstreams — create/reconcile nested implementation children and encode dependencies
1. Connection correctness: channel passwords currently discarded by Rust join_channel; complete invited-channel ID/path/password, official tmspk.gg links, privilege keys, identity import/export/security-level raising, robust DNS/nickname/address handling.
2. TeamSpeakID/myTeamSpeak differentiation: finish/reconcile PR#24; account login, OTP, encrypted synced bookmarks/identities, safe read-only import, duplicate/folder handling, logout/revocation and error UX. No credentials/identities in logs; preserve encryption/2FA. Owner-authorized real-account acceptance and exact tested APK attribution required. No cloud-sync writeback without explicit additional approval.
3. Voice quality/background: correct AudioTrack partial-write handling; VAD with hangover, PTT/open mic, whisper targets, per-user volume, optional vetted noise suppression, floating PTT, call interruption/audio focus, Bluetooth routing, screen-off/OS-kill recovery/network handoff, simultaneous talkers/loss tests. Benchmark, don't assume Rust or Kotlin automatically wins.
4. Media: camera share (front/back/switch, orientation, permissions, mic), existing screen+playback-audio send/view, PiP/overlay, quality controls, TURN configuration and realistic NAT recovery, permission denial/revocation and viewer lifecycle. No default third-party relay spend.
5. Files and chat: browse/upload/download/progress/cancel/rename/delete/mkdir with server permissions, safe filenames/paths/content handling and destructive confirmations. Images via supported transport; durable local channel/private history, retention/delete/export, unread state/pokes. Don't promise proprietary myTeamSpeak picture-service interoperability without proof.
6. Administration: permission-aware move/kick/ban, channel create/edit/reorder/delete, channel/server info, useful denied-operation errors, confirmation and correct state refresh. Test allowed and denied roles.
7. Cleaner interface: coherent Chlorophone branding, simple connect/onboarding and TeamSpeakID entry, clear hierarchy, persistent accessible voice controls while chatting/viewing, consistent navigation for channels/chat/streams/files/admin, status/error/empty/loading states, large touch targets, TalkBack/font scaling/contrast, portrait/landscape/tablet and localization framework. Deliver before/after screenshots and real interactive flows, not just mockups.
8. Privacy/security: encrypt/migrate bookmark passwords and backup exclusions; production-safe diagnostics opt-in/redacted/user-controlled; remove surprise debug auto-connect with persisted open mic from normal distribution; review external components, identity/TLS/crypto and update trust. Do not copy PocketTS bootstrap-key fallback or plaintext-on-keystore-failure patterns. Potential upstream finding is unproven exploitability, not an excuse to probe third-party servers.
9. Delivery/test/docs: signed production release, signing continuity and existing key backups preserved, gate artifacts on tests, explicit integration skips vs passing, device matrix and install/update smoke. Android+iOS shared-core regression coverage; don't claim iOS media parity absent implementation/device proof. Update README/docs/changelog honestly and keep source vs branch/test APK vs stable delivery distinctions.

## Execution and acceptance
Vision Quest owns decomposition/implementation coordination; implementation owners chosen from live org, preserve existing owner where sensible. All nine workstreams stay in scope. Use maintained adequate components first and audit their licenses; PocketTS GPLv3 is reference, not permission to transplant code into PolyForm Noncommercial distribution. Required commit identity phattbeats <21150921+phattbeats@users.noreply.github.com>, no coauthor trailers (supersedes older issue-specific email notes).
Each child needs source/branch, dependencies, measurable acceptance, tests/screenshots, remaining limitations and report back here. Parent remains open until all approved scope is accounted for and verified; never close based solely on documentation/decomposition. Reconcile existing app parent blockers without deleting marketing/account review gates or restarting live services. No secret/signing rotation, production server mutation, paid service purchases or automatic publishing implied. Implementation and PRs authorized; present release/deployment actions for owner review.

## Initial verified code findings
main app/.../PlntViewModel.kt301–319 drops invite channel; core/src/lib.rs363–377 ignores password; Android AudioEngine.kt210–216 ignores nonblocking write result; PlntDataStore.kt66–98 serializes bookmark passwords; distributed debug paths enable diagnostic uploads/auto-connect. Revalidate against PR#24/current branches before fixing.

Ledger is updating stale README and adding a full-scope roadmap now; reconcile that docs PR before creating competing changes. Report back in this issue with hierarchy, first active implementation lane, PRs and verification, not a plan-only completion.


## Delivery evidence required

- Account flow: owner-authorized success, wrong password, OTP required/incorrect/cancel, sync/import duplicates/folders, logout and imported identity joining; no credential logging and no account-data mutation.
- Voice: two phones/ten-minute Bluetooth screen-off session, wired/speaker/BLE/SCO routes, incoming call, permission revoked, Wi-Fi/mobile handoff, OS-kill recovery, multiple talkers and network-loss/partial-write behavior. Record devices, OS, commit and APK digest.
- Media: camera front/back switch and mic, screen/playback audio, rotation, camera/projection permission denial/revocation, PTT while viewing, NAT/TURN, disconnect cleanup and resources released.
- Files/admin: permitted and denied roles; upload/download cancellation/progress, safe filenames/paths, destructive confirmation, channel/user state reconciliation.
- UI: before/after screenshots on phone portrait/landscape, font scaling/TalkBack/contrast and useful loading/offline/error states; voice controls stay reachable during chat/video.
- Delivery: tests finish before release artifact publication; installation/update retains identity/settings and signing continuity. Skipped tests are reported as skipped. Device-only gates are not assigned to agents as if they owned a physical phone.

PocketTS is a behavior/reference benchmark. Prefer maintained compatible components; do not transplant GPL implementation into this differently licensed project without a deliberate licensing decision. No latency, battery or reliability superiority claims without paired measurements.
