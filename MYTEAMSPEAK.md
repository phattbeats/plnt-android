# myTeamSpeak account import investigation

Relay for Teamspeak currently imports an exported identity file. Account sign-in
and synced bookmark import are not implemented. An offline identity file does
not replace the account flow for a user who has only a phone.

## Artifact and method

Examined the public Linux TS6 6.0.0-beta5 package without launching it or using
an account:

- [Official package](https://files.teamspeak-services.com/pre_releases/client/6.0.0-beta5/teamspeak-client.tar.xz)
- Archive SHA-256: `394472a25ee9c2419c44a5e71a401c21bffecf2fdcb27fbe2173d0fffd1875a4`
- `TeamSpeak` executable SHA-256: `1189672c2c290997a419100b0f29f4da95d0df9d1e744b07a764eba1e8a0944f`

The executable contains serialized protobuf FileDescriptorProto records. Offline
wire parsing followed by the standard protobuf decoder recovered the records
below. File offsets refer to the executable bytes, not a disassembler address.
These are static schema observations, not observed requests or proof that the
service currently accepts a third-party client.

| Descriptor | File offset | Bytes |
|---|---:|---:|
| myteamspeak_common.proto | 0x11423f0 | 3645 |
| myteamspeak_login.proto | 0x1143800 | 2421 |
| synchronization_common.proto | 0x114e190 | 1329 |
| myteamspeak_synchronization.proto | 0x1144490 | 499 |
| Sync_Serializing.proto | 0x1150e30 | 4551 |
| Account_Serializing.proto | 0x1152270 | 2150 |

## Reproduce the schema recovery

Create an isolated Python environment with the `protobuf` package, unpack the
public archive above, then run the repository's offline extractor:

```sh
python tools/extract-account-schema.py path/to/TeamSpeak out/account-schema
```

The output directory must be new. The tool writes six selected descriptors and
a manifest with source SHA-256, file offsets, byte lengths, and descriptor
hashes. The six outputs were regenerated and compared byte-for-byte by hash
with the initial extraction. This validates schema recovery from that artifact,
not a successful account exchange or decryption.

## Login schema

`LoginService.login` takes `LoginData` and returns `LoginSession`. The binary
also contains the literal endpoint
`https://clientapi.myteamspeak.com/authentication`; its association with a
particular request framing still needs a native call trace.

`LoginData` fields: email (1, string), password (2, string), skipSession
(3, bool), origin (4, enum), sync_version (5, enum), otp (6, string), device_id
(7, string), otp_renewal_token (8, string), device_name (9, string), recovery_key
(10, string). Origin values are CLIENT=0 and WEBSITE=1. A field called
`password` does not establish whether the input is plaintext, hashed, or derived.

`LoginSession` contains key (1, bytes), session (2, string), limits (3,
repeated), uuid (4, string), error (5, enum), username (8, string), and
myts_id_data (9, message), among other fields. Alternative login information
contains renewal_token, otp_renewal_token, and device_id. No live session was
obtained or tested.

The error enum distinguishes LOGIN_OK=200, LOGIN_FAILED=202,
AUTH_REQUIRES_OTP=208, OTP_INVALID=209, DEVICE_ID_MISSING=211,
RECOVERY_KEY_INVALID=212, and NO_RECOVERY_KEYS=213. A real implementation must
honor 2FA rather than treating these as generic failures or bypassing them.

## Sync schema

`SynchronizationService` exposes `requestServerItems` and `synchronizeItems`.
Both accept `Sync_Request_ItemClasses` and return `Sync_Reply_ItemClasses`.
The binary contains `https://clientapi.myteamspeak.com/synchronization`.
Only the read operation is a candidate for the initial importer; no sync,
account reset, key replacement, or deletion operation has been invoked.

The request has session (1, string), classes (2, repeated message),
globalversion (3, string), and sync_version (4, enum). Each class has itemclass
(1, enum), version (2, string), detail (3, repeated message), and not_in_db
(4, bool). BOOKMARK=0 and IDENTITY=1. Each detail has item_uuid (1, string),
item_version (2, string), item_blob (3, bytes), and the descriptor's misspelled
`item_delte_on_server` (4, bool). The reply carries status, classes, and
globalversion. Sync version values are DEFAULT=0 and V1_1=1.

## Internal bookmark and identity serialization

`Sync_Serializing.proto`, package `com.teamspeak.sync.proto`, defines these
records. They are candidates for the decoded item format; tracing the actual
blob decryption/parse path is still required to confirm that mapping.

`Item_Data` includes item_uuid (2), sync_version_uuid (3), manipulated (4),
last_known_version (5), item_type (6), parent (7), sort_order (8), timestamp
(9), bookmark (16), and identity (17).

`Bookmark_Data` includes name (1, string), address (2, string), port (3,
uint32), nickname (4, string), phonetic_nickname (5, string), identity
(6, string), server_password (7, string), default_channel (8, string),
default_channel_password (9, string), default_channel_id (10, uint64),
autoconnect (14, bool), server_uid (15, string), and other playback,
subscription, and presence settings. The identity reference must be resolved;
it must not silently become a fresh key when a matching identity is unavailable.

`Identity_Data` contains unique_identity (1, string), id_name (2, string),
nickname (3, string), phonetic_nickname (4, string), and is_default (5, bool).
The encoding of unique_identity needs validation before using the file-import
parser on it.

`Account_Serializing.proto` additionally has encryption_password (5, bytes),
key (52, bytes), and backup_ed_key (53, bytes). Its `Auth_Token_Package`
contains version, auth_token, encrypted_encryption_password, encryption_tag,
and iv. Names alone do not establish a cipher, KDF, salt, iteration count,
key relationship, or whether every new device needs a backup key.

## Remaining implementation requirements

1. Trace native login preprocessing and protobuf RPC HTTP framing, including any
   required client authentication. Do not replay an official-client secret.
2. Trace password/backup-key derivation and key unwrapping, including invalid-key
   and 2FA paths. Do not infer the algorithm from bundled crypto-library strings.
3. Confirm the encrypted item envelope and decrypted serialization with synthetic
   known-answer fixtures before adding network/account UI.
4. Implement read-only retrieval and explicit preview/selection. Keep tokens,
   passwords, private identities, and server passwords out of diagnostics.
5. Build an APK and test the supported account flow with the account owner. No
   account sign-in, device round-trip, or synced import has been demonstrated yet.
