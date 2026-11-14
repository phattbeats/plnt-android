# TeamSpeak 6 command framing (PHA-3287)

`TS6_PROTOCOL.md` recovered the TS6 protobuf **schema** from the server binary and left one
gap: how a serialized `ClientCommandRequest` is actually carried on the wire, and how a client
declares that it speaks the protobuf format.

This document answers both, and corrects a premise we had been carrying since PHA-3272.

Derived by static analysis of `teamspeaksystems/teamspeak6-server:latest` (6.0.0-beta11+,
build dated 2026-06) with the tooling in `tools/ts6-proto/`. Addresses below are file virtual
addresses in that specific build and will move between builds; the structure will not.

## Headline: TS6 commands do not ride on tsproto at all

We assumed the protobuf command layer sat inside the tsproto `Command` packet type on
`9987/udp`. It does not.

The TS6 command channel is a **WebRTC data channel** — DTLS → SCTP → data channel — and the
command format is selected by the **SCTP payload protocol identifier (PPID)** of each user
message. The legacy tsproto `Command`/`CommandLow` packet path still exists, untouched, for
TS3 clients, but the protobuf format is not reachable from it (see "Why tsclientlib cannot
reach it" below).

## The receive side

`webrtc::Full_Session::on_sctp_message(ppid, stream_id, data, len)` (`0xbd8720`) is a bare
three-way switch on the PPID:

| PPID | RFC 8831 name | What the server does |
| --- | --- | --- |
| **53** (`0x35`) | WebRTC Binary | tail-calls the **protobuf** command handler with `(data, len)` verbatim |
| **51** (`0x33`) | WebRTC String | copies into a `std::string` and calls the **legacy text** command handler |
| **50** (`0x32`) | DCEP | `DATA_CHANNEL_OPEN` (type 3) → replies `DATA_CHANNEL_ACK` (type 2) |
| anything else | — | dropped |

So the framing is: **the entire SCTP user message is the serialized `ClientCommandRequest`.**
No length prefix. No command-id header. No envelope. The message boundary is the SCTP message
boundary, and the discriminator is out of band in the PPID.

The protobuf handler (`webrtc::Full_Session`, `0xbf6b90`) then does, in order:

1. `session->protocol_format = PROTOBUF` — receiving one protobuf command latches the session.
2. `ClientCommandRequest::ParseFromArray(data, len)` over the whole message.
3. Rejects `request.command_case() == 0` (no oneof member set).
4. Looks the oneof case up in a table and checks the command's **allowed connection states**.
   This is the custom field option `50000` visible on every oneof member in the descriptor
   (`B\x05\x82\xb5\x18\x01\x01` → extension 50000, packed repeated, `[1]`; `client_init` and
   most read commands carry `[1,2,3]`). Commands not permitted in the current state get an
   error response rather than a dispatch.
5. **Converts the request into a legacy TS3 text command** and feeds it to the existing text
   command handler. TS6 is a protobuf *front end* bolted onto the TS3 command core, not a
   reimplementation.

Errors are returned as a serialized `ClientCommandResponse` carrying `request_id` and an
`ErrorResponse`, sent back as PPID 53.

## The send side

Outbound commands and events queue a byte buffer on the session and are flushed by
`0xbd9cb0`, which picks the wrapper by the session's format flag:

- text → `0xbd8a20`, PPID **51**
- protobuf → `0xbd8a00`, PPID **53**

Both funnel into a single `usrsctp_sendv` call site (`0xbd7ed0`, the only one in the binary),
which writes the PPID in network byte order onto stream `0` for command traffic. Nothing is
prepended in either direction — the payload the notification builder produced is the payload
on the wire.

The branch itself is in the notification builder (`0xbdcf70`): for each recipient it tests
`session->protocol_format == 2` and either serializes the legacy text command or converts it
through a message factory into the matching protobuf event.

## `client_protocol_format`

The property is real and its value is the literal string `proto`.

It is read in the **legacy text `clientinit` handler** (`0xb57700`): the handler walks the
parsed key/value list, compares each key to `client_protocol_format`, compares the value to
`proto`, and on a match calls `transport::Session::set_protocol_format(1)`, which sets the
session field to `2` (= protobuf). Only then does it send `initserver`. Everything after that
point is protobuf in both directions.

The reverse route exists too. If the client's *first* message is already a protobuf
`ClientInitRequest` (PPID 53, oneof field 13), `0xbf6b90` synthesizes the legacy text command:

```
clientinit client_nickname=… client_version=… client_platform=…
           client_input_hardware=… client_output_hardware=…
           client_protocol_format=proto [client_server_password=…]
```

and runs it through the same legacy handler — which then sees `client_protocol_format=proto`
and latches the session. Both entry points converge on the same code. The server even logs
`proto clientinit params: …` and `proto clientinit result: error=…` for this path, which makes
a live TS6 client handshake observable in the server log without any packet capture.

The accessors, on the `com::teamspeak::server::transport::Session` interface:

| vtable offset | signature | body |
| --- | --- | --- |
| `+0x38` | `bool is_protobuf() const` | `return fmt == 2` |
| `+0x40` | `void set_protocol_format(int v)` | `if (v == 1) fmt = 2` |

## Client version signature: not required

`ClientInitRequest` has exactly six fields — `client_nickname`, `client_version` (a plain
string), `client_platform`, `client_input_hardware`, `client_output_hardware`,
`client_server_password`. There is **no `client_version_sign` field**, and the text command the
server synthesizes from it does not contain one either. A TS6-format client is therefore not
asked for a version signature at init. (`virtualserver_min_client_version` still exists as a
separate policy knob, and the legacy TS3 signature check still applies on the legacy path.)

## Why tsclientlib cannot reach this

`webrtc::Full_Session` is the **only** class in the binary that implements
`com::teamspeak::server::transport::Session`, and it is the only class that implements
`set_protocol_format` / `is_protobuf`. In the legacy `clientinit` handler the session pointer
is null-checked before the format is applied, so a connection without a WebRTC session parses
`client_protocol_format=proto` and then silently ignores it.

Consequences for PLNT:

- Sending `client_protocol_format=proto` from tsclientlib on `9987/udp` will not switch
  anything. It is not a cheap upgrade path.
- Speaking TS6 means implementing **ICE/STUN → DTLS → SCTP → data channel**, then putting
  protobuf on PPID 53. tsclientlib's tsproto transport is not a stepping stone to it; it is a
  parallel transport that the same server also happens to speak.
- The server exposes no additional listening socket for this. Only `9987/udp`, `30033/tcp`
  (file transfer) and `10080/tcp` (web query) are bound, and the WebRTC stack is fed from a
  `boost::asio::ip::udp` endpoint handler, so WebRTC is demultiplexed off the same `9987/udp`
  socket (RFC 7983 style: STUN / DTLS / tsproto by leading byte).
- WebRTC is gated by a server option — `--enable-webrtc-server`, env `TSSERVER_WEBRTC_ENABLED`,
  config `protocols.webrtc.enabled` — plus the `virtualserver_webrtc_certificate` /
  `virtualserver_webrtc_private_key` server properties. Our container sets none of these, so
  before any live TS6-client test the option has to be turned on and the server restarted.

## Status of this document

Everything above is read out of the server binary's code and descriptor tables; none of it has
been observed on the wire yet. The cheapest confirmation is no longer a tsproto capture — it is
to enable the WebRTC server, connect the official TS6 desktop client, and read the
`proto clientinit params:` line out of the server log. A packet capture would only show
STUN/DTLS.

## Reproducing

```bash
docker cp teamspeak6-server:/opt/tsserver/tsserver ./tsserver
python3 tools/ts6-proto/extract_descriptors.py tsserver   # schema (PHA-3272)
python3 tools/ts6-proto/binutils/find.py tsserver client_protocol_format   # xrefs
python3 tools/ts6-proto/binutils/disas.py tsserver 0xbd8720               # PPID switch
```

See `tools/ts6-proto/README.md`. The recovered schema is TeamSpeak's and is deliberately not
committed here.
