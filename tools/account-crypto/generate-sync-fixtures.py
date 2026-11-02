#!/usr/bin/env python3
"""
Regenerate the account sync fixtures without pycryptodome.

The committed fixtures were produced by tools/account-crypto/generate-android-fixtures.py,
which needs PyCryptodome for AES-GCM (login reply) and AES-ECB (item envelope). This
sandbox has no pip and no PyCryptodome, so the item envelope is rebuilt here with the
OpenSSL CLI for ECB block encryption plus the recovered native counter dance.

Nothing is taken on faith: --verify re-derives the *committed* syncReply and compares
it byte-for-byte before the folder variant is written. A mismatch aborts.

The login reply is not regenerated here (it needs AES-GCM); it is reused verbatim from
the committed fixture file.
"""
import argparse
import hashlib
import re
import subprocess
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parents[2]
FIXTURES = REPO / "app/app/src/test/kotlin/com/plnt/client/account/AccountFixtures.kt"


def varint(v: int) -> bytes:
    out = b""
    while v > 127:
        out += bytes([(v & 127) | 128])
        v >>= 7
    return out + bytes([v])


def bs(n: int, v) -> bytes:
    if isinstance(v, str):
        v = v.encode()
    return varint(n * 8 + 2) + varint(len(v)) + v


def num(n: int, v: int) -> bytes:
    return varint(n * 8) + varint(v)


def ecb(key: bytes, blocks: bytes) -> bytes:
    """AES-ECB over whole blocks, no padding — the OpenSSL CLI's -nopad mode."""
    result = subprocess.run(
        ["openssl", "enc", "-aes-%d-ecb" % (len(key) * 8), "-nopad", "-K", key.hex()],
        input=blocks, stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True,
    )
    return result.stdout


def native_ctr(key: bytes, iv: bytes, plain: bytes) -> bytes:
    """
    The recovered TS6 CTR variant, not textbook CTR.

    The keystream is AES-ECB of the counter block — the same thing CTR does,
    but with the native's counter advance. After each block the counter is
    advanced three times: big-endian +1, then little-endian +1, then
    big-endian -1, each on the full 128-bit value.
    """
    out = bytearray()
    c = iv
    for i in range(0, len(plain), 16):
        keystream = ecb(key, c)
        out += bytes(a ^ b for a, b in zip(plain[i:i + 16], keystream))
        # Literal byte-level mirror of the reference implementation: each step
        # reinterprets the whole 128-bit value in a different endianness.
        c = ((int.from_bytes(c, "big") + 1) % (1 << 128)).to_bytes(16, "big")
        c = ((int.from_bytes(c, "little") + 1) % (1 << 128)).to_bytes(16, "little")
        c = ((int.from_bytes(c, "big") - 1) % (1 << 128)).to_bytes(16, "big")
    return bytes(out)


MASTER = bytes(range(32))


def detail(uuid: str, plain: bytes) -> bytes:
    iv = bytes(range(16))
    encrypted = iv + native_ctr(MASTER, iv, plain) + hashlib.sha512(plain).digest()
    return bs(1, uuid) + bs(3, encrypted)


def identity_item() -> bytes:
    return bs(2, "identity-id") + num(6, 1) + bs(17, bs(1, "synthetic-private-key"))


def bookmark_item(folder: str | None) -> bytes:
    item = bs(2, "bookmark-id") + num(6, 0)
    if folder is not None:
        # Item_Data.parent (7) — the synced folder the server lives under.
        item += bs(7, folder)
    item += bs(16, bs(1, "Synthetic server") + bs(2, "voice.example.invalid") + num(3, 9987) +
               bs(4, "Synthetic nickname") + bs(6, "identity-id") + bs(7, "synthetic-server-password"))
    return item


def sync_reply(bookmark: bytes) -> bytes:
    groups = (bs(2, num(1, 0) + bs(3, detail("bookmark-id", bookmark))) +
              bs(2, num(1, 1) + bs(3, detail("identity-id", identity_item()))))
    return num(1, 301) + groups


def committed(name: str) -> str:
    text = FIXTURES.read_text()
    match = re.search(r'const val %s = "([0-9a-f]+)"' % name, text)
    if not match:
        raise SystemExit("fixture %s not found in %s" % (name, FIXTURES))
    return match.group(1)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--verify", action="store_true", help="prove the rebuild matches the committed fixture")
    parser.add_argument("--write", action="store_true", help="write the folder fixture into the Kotlin file")
    args = parser.parse_args()

    plain = bookmark_item(None)
    rebuilt = sync_reply(plain).hex()
    known = committed("syncReply")
    if rebuilt != known:
        print("MISMATCH: rebuilt syncReply does not match the committed fixture", file=sys.stderr)
        print("  rebuilt:  %s" % rebuilt[:120], file=sys.stderr)
        print("  committed:%s" % known[:120], file=sys.stderr)
        return 1
    print("verified: rebuilt syncReply matches the committed fixture byte-for-byte")

    folder_reply = sync_reply(bookmark_item("synthetic-folder-id")).hex()
    if not args.write:
        print("folderSyncReply = %s" % folder_reply)
        return 0

    text = FIXTURES.read_text()
    if "const val folderSyncReply" in text:
        text = re.sub(r'    const val folderSyncReply = "[0-9a-f]+"\n', "", text)
    text = text.replace(
        '    const val missingIdentityReply',
        '    const val folderSyncReply = "%s"\n    const val missingIdentityReply' % folder_reply,
    )
    FIXTURES.write_text(text)
    print("wrote folderSyncReply to %s" % FIXTURES)
    return 0


if __name__ == "__main__":
    sys.exit(main())
