# myTeamSpeak native protocol recovery (PHA-4350)

This completes the bounded protocol-recovery stage. Relay still has no account
sign-in UI or demonstrated account import. The next stage is implementation and
owner-authorized testing, including 2FA and real synced data.

## Evidence boundary

The target is the public Linux TS6 6.0.0-beta5 executable, SHA-256
`1189672c2c290997a419100b0f29f4da95d0df9d1e744b07a764eba1e8a0944f`.
[MYTEAMSPEAK.md](../MYTEAMSPEAK.md) records the archive and schema offsets.
The binary was not launched. Selected functions ran under Unicorn with synthetic
inputs; allocator/memory imports were substituted, unsupported imports failed
closed, and no process initialization, RNG constructor, account, or network was
available to those checks. Ghidra analysis reused the existing project and
explicit function bounds with whole-program analysis disabled.

Evidence is retained in [tools/account-crypto](../tools/account-crypto/README.md).
Addresses below are original ELF virtual addresses. The saved Ghidra C/assembly
files use an image base **0x100000 higher**; subtract that before comparing them
with Capstone `.asm` files or harness calls. Decompiled signatures, especially
hidden return arguments and exception-tail code, are approximations. Do not
interpret an erroneous decompiler parameter list as the native ABI.

## Login preprocessing

The calls at `0x2a23bbe` and `0x2a23d5c` (also the `0x2a24080` path) populate the
stored login-password value and separate encryption-password bytes. Helpers
`0x2a7fc70`, `0x2a7fe30`, `0x2a81930`, `0x2a81950`, and `0x2a81970` establish:

```text
P = entered password bytes
M = email bytes transformed byte-by-byte through the native locale's case facet
    (lowercase email for the ordinary ASCII path)
L = standard_base64(PBKDF2_HMAC_SHA512(P, M || "ts3Login" || P, 10000, 48))
E = PBKDF2_HMAC_SHA512(P, M || "ts3Encryption" || P, 10000, 32)
LoginData.password = L
```

The password's case is preserved. The salt above is the concatenation itself;
it is not SHA-512 hashed first. Login preprocessing does not use the account
UUID. Native locale behavior for non-ASCII email and upstream UI trimming or
Unicode normalization have not been verified; do not silently invent those
transformations. The literal domains are public KDF domain separators, not
client authentication secrets.

`0x47594a0` matches Python PBKDF2-HMAC-SHA512 at 10,000 rounds for both output
lengths; see `native-kdf-kat-current.json`. This is execution of the primitive
with synthetic bytes, plus static caller evidence for the salts, output lengths,
base64 conversion, and request-field assignment. It is not an emulated complete
login UI. The synthetic login probe below independently exercised this ASCII
preprocessing recipe against the login handler.

## HTTP RPC framing and client authentication

The channel method at `0x46d9130` constructs:

```text
POST /authentication                         for LoginService
POST /synchronization                        for SynchronizationService
Host: clientapi.myteamspeak.com
Content-Type: application/ts3cloud

uint8(method_name_byte_length) || ASCII(short_method_name) || protobuf(request)
```

The native method-name length is less than 128. It uses `login`, not the
fully-qualified protobuf service name. There is no gRPC frame, outer protobuf
message, request-length prefix, or terminating NUL in this recovered framing.
The endpoint selects the service. The response body is the raw protobuf reply.
The channel at `0x46d9ac0` requires HTTP 200 and then parses the reply. Network
failure, non-200 HTTP status, serialization failure, and invalid protobuf are
transport errors; they are distinct from `LoginSession.error`.

**Observed 2026-10-22:** one POST using a nonexistent `.invalid` email, fixed
synthetic password, `skipSession=true`, sync version 1, and synthetic device
metadata returned HTTP 200 and the three bytes `28 ca 01`. Those decode as
`LoginSession.error` (field 5) = **202 / LOGIN_FAILED**. The request supplied no
client certificate, official-client secret, existing session, or real account.
The response misleadingly advertised `application/json`; its bytes are protobuf.
See `synthetic-login-probe.json` for the bounded receipt.

This establishes that this framing reaches the login handler without a required
client credential for a failed synthetic login. It does not establish that valid
credentials would succeed, or rule out additional checks on successful sign-in.
The earlier anonymous GET and unknown-method probes returned empty HTTP 400;
those were not evidence of a client-certificate requirement. The focused HTTP
setup follows the request through `0x46dd050`, `0x470cdc0`, `0x470daa0`, and
`0x4710b00`. Generic TLS/certificate strings in the linked libraries cannot prove
an authentication requirement. No official-client secret was extracted or used.

For the importer, use `origin=CLIENT` and only fields required by the observed
schema; do not copy an official-client fingerprint or renewal credential. A new
installation should use its own device identifier/name. Treat 208
`AUTH_REQUIRES_OTP` as an OTP prompt and resubmit only with the user's OTP; 209
`OTP_INVALID`, 211 `DEVICE_ID_MISSING`, 212 `RECOVERY_KEY_INVALID`, and 213
`NO_RECOVERY_KEYS` must remain distinct. Never interpret a transport success as
LOGIN_OK (200). Do not infer actual 2FA retry behavior from enum names: an
owner-authorized account test remains required. Renewal-token persistence is
outside the initial read-only importer.

## Master-key derivation and wrapping

`LoginSession.key` is handled by the account key path. The recovered current
key wrapper uses a leading one-byte version followed by:

```text
version[1] || GCM_tag[16] || nonce[12] || ciphertext
```

The wrap is AES-256-GCM with no AAD. For a 32-byte master key the complete blob
is 61 bytes. `0x2a80ff0` dispatches by version; `0x2a812b0` selects the wrapping
key; `0x2a82760` unwraps the GCM payload:

```text
version 1:
    salt = SHA512(account_uuid_bytes || "ts3PassPhrase" || L_bytes)
    W = PBKDF2_HMAC_SHA512(L_bytes, salt, 10000, 32)
version 2:
    W = E                                  # separate raw encryption-password bytes
master = AES_256_GCM_decrypt(W, nonce, ciphertext, tag, AAD=empty)
```

Version 1 uses the stored base64 login-password value, not the original plaintext
password. Preserve UUID bytes as received. Unsupported versions must fail
explicitly. A GCM authentication failure must never return usable key bytes.
`key_kat.py` compares each native version-specific derivation and native GCM
unwrap against an independent Python construction; both pass. Dispatch and
header handling are static evidence, not an executed constructor path.
`envelope_kat.py` additionally verifies rejection after changing a tag byte.

The legacy backup helper `0x2a80da0` base64-decodes a 32-byte recovery key and
unwraps `IV[16] || ciphertext` through the native CTR wrapper, then installs the
recovered key. `backup_kat.py` verifies this path using fixed synthetic bytes and
an explicit context. This is separate from the 16-character formatted recovery
code parser at `0x2a83d90` and its `ts3RecoveryKey` hashing helper `0x2a84190`.
Their existence does not make a formatted 2FA recovery code interchangeable with
a sync backup key. Legacy CTR wrapping lacks GCM authentication; validate
subsequent item digests before accepting recovered data.

The normal version 1 and version 2 password-derived paths are concrete evidence
against a universal claim that every new device needs a backup key. Whether a
particular account needs recovery depends on its available key material and
server response; do not force backup recovery before attempting supported
password-based unwrap.

## Sync-item envelope and protobuf mapping

The recovered sync-item decrypt helper `0x2a80410` handles:

```text
IV[16] || native_AES_256_CTR(master, plaintext) || SHA512(plaintext)[64]
```

The digest is outside the ciphertext. Require sufficient envelope length,
decrypt first, compare the entire digest, and only then parse or expose the
plaintext. It is an unkeyed digest; do not describe this format as AEAD. The
separate GCM envelope above wraps keys and must not be substituted for this
item envelope merely because the same binary links AES-GCM.

The wrapper `0x4758c90` calls the underlying CTR primitive one block at a time.
After each block the combined native update is:

```text
C = (big_endian(C) + 1) mod 2^128        # underlying CTR routine
C = (little_endian(C) + 1) mod 2^128     # wrapper
C = (big_endian(C) - 1) mod 2^128        # wrapper
```

Express each intermediate integer as 16 bytes before the next operation. Do not
replace this with a generic big-endian or little-endian CTR configuration.
`aes_kat.py` verifies 65 bytes (five blocks including a partial block) with
ordinary, low-byte-carry, and all-ones IVs against native execution. It also
checks the GCM primitive against independent AES-GCM output. The exact fixtures
and algorithms are synthetic and contain no user data.

`item_kat.py` builds `Item_Data` from the recovered descriptors, checks native
parser acceptance of `bookmark=16` and `identity=17`, decrypts a synthetic
bookmark envelope, and checks that changing the digest returns no plaintext.
The recovered item-consumption path is retained as `items-final.c` and
`2a7af30.asm`; these link the sync handling to the item representation. This
supports the schema mapping without a real account capture.

After verifying the envelope, parse `com.teamspeak.sync.proto.Item_Data`.
Bookmark fields are name, address, port, nickname, identity UUID reference,
server/channel passwords, and the other fields listed in MYTEAMSPEAK.md.
Identity fields are `unique_identity`, `id_name`, nickname, phonetic nickname,
and `is_default`. Resolve the bookmark's identity reference against imported
identity records. The synthetic identity contains a placeholder; it proves
protobuf mapping, **not** that `unique_identity` is a valid private identity or
that Relay's exported-identity parser accepts its encoding. Key-format and
voice-connection validation remain implementation acceptance tests. Never
silently replace a missing or invalid referenced identity with a newly generated
identity.

## Read-only implementation handoff

1. Generate the six recovered schemas and implement bounded request/reply
   parsing, login preprocessing, and the frame above. Disable sensitive request,
   response, crypto-buffer, and crash diagnostics. Keep TLS verification enabled.
2. Implement the crypto paths behind synthetic fixtures first. Preserve native
   CTR counter semantics, verify GCM tags/item digests, reject unsupported key
   versions, and keep partial plaintext out of previews.
3. Obtain login credentials and OTP only through local UI. Submit `login`, honor
   all returned authentication states, and use a successful session only for
   the read operation. Do not reuse the synthetic probe as a real-account test.
4. Call only `requestServerItems` with `Sync_Request_ItemClasses.session`,
   classes BOOKMARK=0 and IDENTITY=1, empty initial version knowledge, and the
   selected sync version. Do not send `synchronizeItems`, outgoing item blobs,
   deletion flags, reset/rekey requests, or account-change operations. Initial
   version/globalversion/detail semantics are schema-backed candidates and
   require an owner-authorized integration test before claiming completeness.
5. Unwrap the returned master key, decrypt/validate items, resolve identities,
   and offer explicit preview and selection before local import. Retain unknown
   fields for diagnosis by field number only, without logging their values.
6. Test valid login, required/invalid OTP, wrong password, unknown key version,
   bad tag/digest, missing identity, repeated import, and a bookmark connection
   with the owner. Verify no account or sync writes and no secrets in diagnostics.

No live session, successful account sign-in, synced-item download, identity key
round-trip, or APK account-flow test has been demonstrated. Those belong to
PHA-4344. This research changed no signing secret, stable APK, merge, or RAID
state. Existing unrelated native-library working-tree changes were excluded.
