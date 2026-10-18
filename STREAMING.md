# Screen sharing over TS6 (PHA-3289)

Relay for Teamspeak (by PHATT TECH) can watch a TeamSpeak 6 screen share started from a desktop TS6 client,
and share the phone's own screen to desktop viewers (see "Sending" below).
Nothing here depends on any particular server: the signalling rides the same
TS3-style command channel the voice client already uses, and the media is a
direct WebRTC connection to the streamer's PC, exactly as between two desktop
clients.

## How it works

| Step | Direction | Wire |
| --- | --- | --- |
| Discover | app → server | `requeststreaminfo clid=<client>` for each client in our channel (also on `notifystreamstarted` pushes). A `notifystreaminfo` with `id`/`clid`/`name` means that client is streaming; a reply carrying only `return_code` means not. |
| Join | app → server | `joinstreamrequest id=<stream> clid=<streamer> msg=<nick> is_remove=0`. The streamer auto-approves. |
| Offer | server → app | `notifyrespondjoinstreamrequest decision=1 offer=<sdp>` — a plain libwebrtc offer (H264/VP8/VP9/AV1 video, stereo Opus audio when the share carries sound). |
| Answer | app → server | `streamsignaling id= clid= json={"cmd":"answer","args":{"answer":"<sdp>"}}` — sent immediately; the key is `answer`, not `sdp`. |
| ICE | both | `{"cmd":"iceCandidate","args":{"mLine":0,"mid":"0","sdp":"candidate:…"}}` in `streamsignaling` / `notifystreamsignaling`. |
| Realign | server → app | ~200 ms after our answer the streamer sends `{"cmd":"offer","args":{"offer":"<sdp>"}}`. It must be answered the same way or media never starts. |
| Leave | app → server | `joinstreamrequest … is_remove=1`. |

Four things were wrong in the first attempts and each one alone was enough to
leave the connection stuck in "checking": a trimmed answer (the streamer wants
its own codec and extension lists echoed back, which libwebrtc's `createAnswer`
does naturally), a slow answer, the unanswered realign offer, and a viewer that
would not negotiate H264 High. The Android implementation uses libwebrtc itself,
the same library the desktop client embeds, so the answer shape is right by
construction.

## Where the code is

- `core/src/lib.rs` — `Client::send_raw_command` and `ConnEvent::RawCommand`:
  the only core change. tsclientlib has no message types for the `stream*`
  verbs, so they go out as raw `OutCommand`s and the `notifystream*` family is
  lifted off the tsproto packet stream before tsclientlib drops it.
- `app/.../stream/StreamDirectory.kt` — who in our channel is streaming.
- `app/.../stream/StreamViewerSession.kt` — one viewing session: join,
  answer, trickle, realign, leave. Owned by `VoiceService` so it survives the
  Activity being recreated.
- `app/.../stream/StreamSenderSession.kt` — sharing this phone's screen:
  `setupstream`, one PeerConnection per viewer fed by a MediaProjection
  capture track, `stopstream`. Also owned by `VoiceService`; see "Sending"
  below.
- `app/.../ui/StreamViewerScreen.kt` — the `SurfaceViewRenderer` screen.

## Limits (v1)

- **Direct connection only.** The streamer offers host and server-reflexive
  candidates; there is no relay. A phone behind carrier-grade NAT that cannot
  reach the streamer directly fails with "could not reach the streamer". The
  planned fix is a user-supplied TURN server in Settings, never one we host.
- **One stream at a time** each way: one stream watched, one share sent.
  Sending is implemented (see "Sending" below), with the phone's own audio
  (see "Share audio").
- **Audio routing.** The share's audio plays through libwebrtc's own audio
  device module, alongside the voice `AudioEngine`. Both target the
  communication stream, so it works, but volume keys act on both together.
- Tested against TS6 server 6.0 beta with WebRTC transport off (stream commands
  over the text channel). A server that moves these commands onto its WebRTC
  data channel would need the TS6 command layer Relay does not have; the app
  reports "no stream found" there rather than failing silently.

## Sending (phone → desktop)

Proven against a live TS6 6.0 server and desktop viewer with the RAID sender
probe (PHA-3289). Implemented in `stream/StreamSenderSession.kt`.

1. `setupstream name=<title> type=3 accessibility=1 mode=1 bitrate=4608 viewer_limit=0 audio=1`
   (`audio=0` when the share goes out picture-only; see "Share audio").
   The server **requires `accessibility`** (error 1542 without it) and echoes it
   back as `access` in `notifystreamstarted clid=<us> id=<sid>`.
2. A viewer clicking "watch" arrives as `notifyjoinstreamrequest clid=<viewer> id=<sid> is_remove=0`.
3. Reply `respondjoinstreamrequest id=<sid> clid=<viewer> decision=1 offer=<sdp> msg=`.
   One PeerConnection per viewer, all fed by one MediaProjection capture track.
4. The viewer answers via `notifystreamsignaling json={"cmd":"answer","args":{"answer":<sdp>}}`;
   ICE trickles both ways as `iceCandidate` messages.
5. `is_remove=1` / `notifystreamclientleft` drops a viewer; `stopstream id=<sid>` ends the share.

### What the desktop viewer cannot take (each one crashes TeamSpeak)

- **H264 Constrained Baseline (42e01f).** TS6 6.0 accepts it in its answer,
  has no decoder for it, and the whole client crashes. `TsOfferShape` only
  offers H264 **High** (`64xxxx`) and VP8, the formats TS streamers send.
- A non-libwebrtc sender (the aiortc test probe) trips an `RTC_CHECK` on the
  desktop's network thread about 2 s after connecting, on every codec. The app
  uses libwebrtc, the same engine as the desktop client.

The offer otherwise mirrors a real TS streamer: video stream id
`outgoing_video`, an inactive audio m-line after video, and no inline
candidates (they are trickled).

Android 14+ refuses MediaProjection unless the service is already foreground
with type `mediaProjection`; `VoiceService.startScreenShare` adds that type for
the duration of the share and drops it afterwards.

### Share audio (PHA-4176)

A share carries what the phone is playing, on by default (Settings → Screen
sharing → "Share phone audio"). `stream/ShareAudioCapture.kt` records it
with Android's playback capture (API 29+) on the share's own MediaProjection,
matching `USAGE_MEDIA`, `USAGE_GAME` and `USAGE_UNKNOWN`. libwebrtc's Java
audio module can only record a microphone, so the capture goes in through
its `AudioBufferCallback`: every 10 ms buffer the module records is
overwritten with playback, or zeroed, before encoding. Mic audio never
reaches the share, and the voice call's own playback
(`USAGE_VOICE_COMMUNICATION`) cannot be captured, so voices do not echo into
it. The audio m-line is then `sendonly` stereo Opus on stream id
`outgoing_video`, and `setupstream` says `audio=1`, which is what makes the
desktop viewer show its audio control.

Below API 29, without the mic permission, or with the switch off, the share
is picture-only and says `audio=0`. Apps that opt out of capture (most DRM
video) come through as silence.

Every step of a share and of watching one goes into the diagnostic trace as
`share: …` / `watch: …` lines.

### One share, and its quality (PHA-4176)

At most one share of ours is live. A `notifystreamstarted` naming us that no
session is waiting for (a share restarted before the server answered, or a
double tap) gets an immediate `stopstream`; the share button also ignores a
second tap while the system capture prompt is up.

Video settings live in Settings → Screen sharing: resolution (1080p / 720p /
480p), frame rate (60 / 30 / 15) and bitrate (8 / 4.5 / 2.5 / 1 Mbps), each
defaulting to Automatic. Automatic is 1080p, 60 fps, 8 Mbps on an unmetered
network and 720p, 30 fps, 2.5 Mbps on metered data
(`stream/StreamQuality.kt`). The bitrate goes to the server in `setupstream`
and, with the frame rate, is set as the encoder ceiling on every viewer's
video sender. Changes apply to the next share.

### Rotation (PHA-3289)

The share follows the phone. While sharing, `VoiceService` listens for
default-display changes; on a rotation it recomputes the frame size from the
full display size (status and navigation bars included, as MediaProjection
captures them) and the share's picked long edge, then calls
`StreamSenderSession.resize()`. That goes to
`ScreenCapturerAndroid.changeCaptureFormat`, which in webrtc-sdk 125 resizes
the virtual display in place: Android 14+ allows one virtual display per
projection, so it must not be recreated. There is no renegotiation; the
encoder carries the new resolution to every viewer.
