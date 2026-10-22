# Offline native crypto verification

These scripts emulate selected routines from one public TS6 beta5 Linux ELF.
They are research fixtures, not production account-handling code. Every key,
password, UUID, identity placeholder, and output is synthetic. They never start
the client, initialize its RNG, open accounts, or make network requests. The
source SHA-256 is enforced before emulation. Unsupported imports fail closed.

Install the Python packages `unicorn`, `pyelftools`, `pycryptodome`, and
`protobuf` in an isolated environment. The verification run used 2.1.4, 0.33,
4.1.0, and 7.36.2 respectively. Supply the public executable from the official
archive linked in [MYTEAMSPEAK.md](../../MYTEAMSPEAK.md):

```sh
export TS6_BINARY=/absolute/path/to/TeamSpeak
python tools/account-crypto/native_kat.py 10000
python tools/account-crypto/aes_kat.py
python tools/account-crypto/envelope_kat.py
python tools/account-crypto/key_kat.py
python tools/account-crypto/backup_kat.py
python tools/account-crypto/item_kat.py
```

The KDF check can take longer than the other checks. Run it at 10000 rounds to
verify the recovered production count; its default of 1 is only a quick harness
smoke check. Each assertion fails the process on mismatch. `aes_kat.py` prints
separate JSON objects for CTR and GCM checks. No script accepts real credentials.

`evidence/` contains saved synthetic results and bounded static traces. Capstone
files use original ELF virtual addresses; Ghidra files have a +0x100000 image
base. `manifest.json` binds the evidence files by SHA-256 to the target identity.
Native calls and independent reference computations are both visible in the
scripts. The full client UI, locale initialization, RNG-backed key constructor,
HTTP client, and live accounts are outside emulation coverage.

`synthetic-login-probe.json` is a separate network observation using only a
nonexistent `.invalid` account. It is not part of the offline suite. See the
[protocol report](../../docs/MYTEAMSPEAK-PROTOCOL.md) for its exact limits and
implementation handoff.
