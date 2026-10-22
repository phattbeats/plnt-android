# myTeamSpeak account test candidate

The account flow is in Settings → myTeamSpeak → Sign in with myTeamSpeak.
It derives the login password locally, submits the recovered login RPC, handles
verification-code challenges, unwraps supported version 1/2 master keys, and
requests bookmark/identity classes using `requestServerItems`. It verifies each
item digest before interpreting protobuf and validates identity strings through
the existing native identity parser. It exposes an explicit server-selection
preview before any local import. It never calls `synchronizeItems` or account
reset/rekey/change endpoints.

Account passwords, OTPs, sessions, and master keys are not persisted. The sheet
is protected from screenshots/recents and does not save credentials across
recreation. Imported private identities and server passwords use the same
Android Keystore-backed, backup-excluded encrypted preferences as the existing
identity store. Bookmarks only contain public metadata and an account-scoped
hashed item identifier; their passwords are resolved from encrypted storage at
connect time. A missing key on foreground or headless reconnect fails closed.
The existing device identity is preserved. Reimporting a selected server updates
the same local ID, and different accounts do not share IDs.

## Validation boundary

Host tests use independently generated synthetic fixtures plus native KATs from
[the protocol research](MYTEAMSPEAK-PROTOCOL.md). They exercise login derivation,
both master-key versions, unusual CTR carry behavior, digest/tag rejection,
malformed protobuf, OTP/error states, read-only RPC selection, verified preview,
missing/invalid identity rejection, and secret-bearing exception redaction.
Regenerate the synthetic Android fixtures with Python and PyCryptodome using
`python tools/account-crypto/generate-android-fixtures.py` from any directory.

No successful live-account exchange, Android device install, real sync identity
encoding, OTP retry, or imported voice connection has been verified yet. The
request's initial version/detail semantics are based on recovered schemas and
need account-owner testing. Responses containing only item versions, unsupported
key versions or identity encodings, non-ASCII email addresses, and legacy backup
key recovery fail explicitly. This candidate does not restore default channels,
channel passwords, autoconnect, phonetic names or other official-client options.
Locally clearing a stored imported server password requires reimport; a blank
password in the existing bookmark editor continues to use the encrypted imported
password. There is no background account session or continuous sync.

## Owner test

1. Install the shared-signed CI candidate on Android 8+ ARM64/x86_64. Preserve
   existing identities via export first. Open Settings while disconnected.
2. Sign in locally on the phone; never send credentials in issue comments.
   Verify a wrong password gives an error and any required OTP challenge is
   honored, including rejection of an invalid code.
3. Inspect returned server names, addresses and nicknames. Cancel once and
   verify no bookmarks were added. Sign in again, select servers and import.
4. Connect from Servers and verify the expected identity, server permissions,
   server password and voice. Disconnect, reopen and reconnect. Repeat import
   and confirm the same server is updated without duplicates.
5. Report the visible stage and sanitized error text if it fails. Do not share
   account passwords, sessions, identity keys or response captures. A real
   account/device pass remains required before claiming the objective complete.
