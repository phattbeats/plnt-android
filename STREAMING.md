# Screen-share viewing over TS6 (PHA-3289)

PLNT can watch a TeamSpeak 6 screen share started from a desktop TS6 client.
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
- `app/.../ui/StreamViewerScreen.kt` — the `SurfaceViewRenderer` screen.

## Limits (v1)

- **Direct connection only.** The streamer offers host and server-reflexive
  candidates; there is no relay. A phone behind carrier-grade NAT that cannot
  reach the streamer directly fails with "could not reach the streamer". The
  planned fix is a user-supplied TURN server in Settings, never one we host.
- **Viewing only.** Sharing the phone screen (`MediaProjection` → encoder →
  `setupstream`) is not implemented; the `setupstream` parameters have not been
  recovered yet.
- **One stream at a time.**
- **Audio routing.** The share's audio plays through libwebrtc's own audio
  device module, alongside the voice `AudioEngine`. Both target the
  communication stream, so it works, but volume keys act on both together.
- Tested against TS6 server 6.0 beta with WebRTC transport off (stream commands
  over the text channel). A server that moves these commands onto its WebRTC
  data channel would need the TS6 command layer PLNT does not have; the app
  reports "no stream found" there rather than failing silently.

## Sending (phone → desktop)

Proven against a live TS6 6.0 server and desktop viewer with the RAID sender
probe (PHA-3289). Implemented in `stream/StreamSenderSession.kt`.

1. `setupstream name=<title> type=3 accessibility=1 mode=1 bitrate=4608 viewer_limit=0 audio=0`.
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
