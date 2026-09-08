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

## Still unknown

The one gap between "we can read the schema" and "we can speak the protocol" is **framing**:
exactly how a `ClientCommandRequest` is carried inside the tsproto `Command` packet type, and
how a client declares `client_protocol_format` during the handshake. tsclientlib already
implements every hard part of the transport (crypto, resend, fragmentation), so this is a
matter of observing one real TS6 client session, not of reimplementing a stack.

Also unverified: whether the server enforces a client version/signature that would reject a
third-party TS6-format client.

## Caveats

- The TS6 server is **beta** software and this protocol is undocumented and unstable. Anything
  here can change between server builds. Re-run the extraction after upgrading the server.
- This is interoperability research against a server we run ourselves. The recovered schema is
  TeamSpeak's, so it is deliberately **not** committed to this public repository — regenerate it
  locally with `tools/ts6-proto/` from your own server binary.
