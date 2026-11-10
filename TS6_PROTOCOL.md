# TeamSpeak 6 protocol — what we know (PHA-3272)

PLNT is built on [tsclientlib](https://github.com/ReSpeak/tsclientlib), a TeamSpeak **3**
protocol implementation. This document records what we established about the **TeamSpeak 6**
server protocol, because TS6 is where chat, screen sharing and camera sharing actually live.

Everything below was derived from a TS6 server we operate
(`teamspeaksystems/teamspeak6-server:latest`, build dated 2025-07-28) using the tooling in
`tools/ts6-proto/`. Nothing here comes from TeamSpeak documentation, because none exists.

## Headline findings

1. **A TS3 client can connect to a TS6 server today.** tsclientlib completes the full
   handshake (RSA puzzle → ECDH → EAX transport) against the TS6 server on `9987/udp`,
   receives `InitServer` / `ClientEnterView`, and successfully decodes live Opus voice from
   other clients on that server. The legacy tsproto transport is intact in TS6.

2. **TS6 adds a protobuf command layer on top of that same transport.** The server binary
   embeds a complete set of protobuf descriptors defining a `ClientCommandRequest` /
   `ClientCommandResponse` pair — a request/response envelope with a `oneof` covering ~130
   commands (channels, clients, permissions, bans, tokens, file transfer, chat, streaming).
   Legacy TS3 clients get the old text-command format; the property `client_protocol_format`
   appears in the legacy command vocabulary and is the apparent negotiation switch.

3. **Screen sharing is server-signalled, not opaque P2P.** The media path is P2P
   (SRTP + OpenH264 — the binary links libsrtp and Opus), but *all signalling is relayed by
   the server* through commands and events we can now read and write. This is the finding
   that changes the answer for PLNT: an independent client can participate.

4. **Text chat is a first-class TS6 command.** `SendTextMessageRequest`, plus
   composing/typing indicators and message deletion.

## The streaming surface

Commands (fields 70–77 of `ClientCommandRequest`):

| Command | Purpose |
| --- | --- |
| `SetupStreamRequest` | Start a stream: `stream_type`, `stream_name`, `max_width`, `max_height`, `max_framerate`, `codec`, free-form `properties` map. Returns a `stream_id`. |
| `UpdateStreamRequest` | Change name/resolution/framerate mid-stream. |
| `StopStreamRequest` | End the stream. |
| `RequestStreamInfoRequest` | Fetch `StreamInfo` (streamer, type, codec, `viewer_count`, `started_at`). |
| `JoinStreamRequestRequest` | Ask to view a stream; returns `request_id` + `approved`. |
| `RespondJoinStreamRequestRequest` | Streamer approves/denies a viewer. |
| `StreamSignalingRequest` | **The WebRTC signalling relay**: `stream_id`, `target_client_id`, `signaling_type`, `signaling_data`. |
| `RemoveClientFromStreamRequest` | Kick a viewer. |

Events pushed by the server: `StreamStartedEvent`, `StreamStoppedEvent`, `StreamUpdatedEvent`,
`StreamInfoEvent`, `StreamAttendeesEvent`, `StreamClientJoinedEvent`, `StreamClientLeftEvent`,
`JoinStreamRequestEvent`, `RespondJoinStreamRequestEvent` (carries an SDP `offer` string), and
`StreamSignalingEvent` (carries a `json` payload — the ICE/SDP exchange).

Enums:

- `StreamType`: `UNSPECIFIED`, `NONE`, `CAMERA`, `SCREEN`, `WINDOW`, `EXISTING_SESSION`, `VOICE`
- `StreamMode`: `UNSPECIFIED`, `NONE`, `P2P`, `SFU` — the SFU mode is defined in the schema but
  TeamSpeak has stated their SFU server is still unreleased, so `P2P` is the mode to target.
- `StreamAccessibility`: `UNSPECIFIED`, `NONE`, `PUBLIC`, `CONTACTS_ONLY`, `PRIVATE`
- `StreamLeaveReason`: `UNSPECIFIED`, `NONE`, `LEFT`, `DENIED`, `FAILED`, `KICKED`, `BANNED`

Because signalling is plain SDP/ICE relayed as strings, the viewer and streamer sides are
ordinary WebRTC peers. On Android both directions are well-trodden: `MediaProjection` →
H.264 encoder → WebRTC for sending, and a `SurfaceViewRenderer` for receiving.

## Framing — answered, and it invalidates a premise above

Headline finding 2 says TS6 "adds a protobuf command layer on top of that same transport".
That is wrong. `TS6_FRAMING.md` (PHA-3287) works the framing out of the same binary:

- TS6 commands ride a **WebRTC data channel** (DTLS → SCTP), not tsproto. The format is picked
  by the SCTP **PPID**: 51 (WebRTC String) = legacy text command, 53 (WebRTC Binary) = a
  serialized `ClientCommandRequest` — the whole message, no length prefix and no command-id
  header.
- `client_protocol_format=proto` is a real property on the legacy text `clientinit` and it does
  latch the session to protobuf — but only for a session that has a WebRTC transport.
  `webrtc::Full_Session` is the only class in the binary that implements the switch, so sending
  it over tsproto on `9987/udp` is parsed and then ignored.
- `ClientInitRequest` has no `client_version_sign` field and the server does not synthesize
  one, so a TS6-format client is not asked for a version signature at init.

The practical consequence for PLNT: tsclientlib is not a stepping stone to the TS6 command
layer. Reaching it means an ICE/DTLS/SCTP data-channel client. See `TS6_FRAMING.md`.

## Still unknown

None of the framing work has been observed on the wire — it is all read out of the server
binary. The WebRTC server is itself an opt-in server option (`--enable-webrtc-server`) that our
test server does not currently enable, so the TS6-native path is untested against a real
client.

## Caveats

- The TS6 server is **beta** software and this protocol is undocumented and unstable. Anything
  here can change between server builds. Re-run the extraction after upgrading the server.
- This is interoperability research against a server we run ourselves. The recovered schema is
  TeamSpeak's, so it is deliberately **not** committed to this public repository — regenerate it
  locally with `tools/ts6-proto/` from your own server binary.
