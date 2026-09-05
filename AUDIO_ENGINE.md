# PHA-3077 — Android audio engine

`AudioEngine` (`app/app/src/main/kotlin/com/plnt/client/audio/AudioEngine.kt`)
owns the two device audio streams for one call: mic capture and call
playback, both hitting `plnt-core`'s fixed 20 ms / 960-sample / 48 kHz mono
frame shape (`core/src/lib.rs`'s `PCM_FRAME_SAMPLES`, `Client.sendPcmFrame`,
`ConnEvent.PcmFrame` — landed in PHA-3075). `VoiceService`
(`app/app/src/main/kotlin/com/plnt/client/service/VoiceService.kt`) is the
foreground service that owns one `AudioEngine` + one `CoreClient` for the
life of a call, per the PHA-3077 "expose a simple `AudioEngine` class the
service owns" requirement. It goes through `CoreBridge`/`CoreClient`
(`app/app/src/main/kotlin/com/plnt/client/core/CoreBridge.kt` — PHA-3079's
seam over the generated `uniffi.plnt_core.*` symbols) rather than touching
`Client`/`EventSink` directly, per that file's own documented convention.
`CoreBridge.newClient()` gained a second `onPcmFrame` callback for this
ticket — the UI-facing `CoreEvent` still never carries raw samples, PCM
frames are intercepted in the sink before `translate()` and handed straight
to `AudioEngine.onPlaybackFrame`.

## What's implemented

- **Capture**: `AudioRecord` with `MediaRecorder.AudioSource.VOICE_COMMUNICATION`.
  Opens at the device's native rate (tries 48/44.1/16/8 kHz in that order —
  whichever `AudioRecord` actually initializes at) and resamples
  (linear-interpolation, `Resampler`) to 48 kHz mono, then buffers into exact
  960-sample frames (`FloatFrameAccumulator`) before handing them to the
  caller's `onCaptureFrame` lambda.
- **Playback**: `AudioTrack` with `AudioAttributes.USAGE_VOICE_COMMUNICATION`
  / `CONTENT_TYPE_SPEECH`, `ENCODING_PCM_FLOAT`, fixed 48 kHz mono — the same
  rate `ConnEvent.PcmFrame` samples already arrive at, so no resampling is
  needed on the way out.
- **Mode**: `AudioManager.MODE_IN_COMMUNICATION` for the lifetime of the call.
- **AEC/NS**: `AcousticEchoCanceler`/`NoiseSuppressor` requested against the
  capture stream's `audioSessionId` when `Effect.isAvailable()` — logged (not
  fatal) when a device doesn't have them.
- **Bluetooth SCO routing** (`BluetoothScoRouter`):
  - API 31+: `AudioManager.setCommunicationDevice()`, preferring
    `TYPE_BLUETOOTH_SCO` > wired > USB > built-in earpiece.
  - API 26–30: classic `startBluetoothSco()` +
    `ACTION_SCO_AUDIO_STATE_UPDATED` handshake, with a bounded 2 s wait so a
    slow-to-connect headset doesn't hang call setup.
- **Mid-call device changes**: `AudioDeviceCallback` registered on
  `AudioManager`; add/remove of a Bluetooth/wired/USB device or the built-in
  mic/speaker triggers `rehome()`, which re-picks the SCO/communication
  device and reopens both streams (`AudioRecord`/`AudioTrack` don't hot-swap
  routes — they have to be recreated against the new one).
- **PTT gates sending, not capture**: `AudioEngine.setSending(Boolean)` only
  toggles whether captured frames are forwarded; the capture thread and
  `AudioRecord` never stop, so there's no SCO reconnect between PTT presses.

## What's NOT implemented here (explicitly out of scope for PHA-3077)

- UI wiring for PTT/open-mic/mute/deafen (PHA-3078/3079's screens call
  `VoiceService.setPushToTalk`/`setOpenMic`).
- Mixing more than one simultaneous remote talker's `PcmFrame` stream in
  software — each `PcmFrame` is written straight to `AudioTrack` as it
  arrives; `AudioTrack` itself sums overlapping writes at the HAL, so this is
  correct for 2+ talkers as long as they're frame-aligned-ish, but no
  explicit jitter/mix buffer was added on the Kotlin side (plnt-core's
  `AudioHandler` already does the real jitter buffering per docs in
  `core/src/lib.rs`).

## Verification status

This ticket was executed in a sandbox with **no Android SDK, no NDK, no
Rust/cargo toolchain, and no emulator** (same constraint noted in
`VERIFICATION.md` for PHA-3074/3075 — CI is this repo's verification path,
not the agent sandbox). Concretely:

| Check | Status |
|---|---|
| Kotlin compiles | **Not verified here** — no `gradlew`/Android SDK in this environment. Needs a CI run or a local `./build.sh`. |
| UniFFI bindings regenerated for `Client`/`EventSink`/`ConnEvent` | **Not verified here** — the committed bindings at `app/app/src/main/kotlin/com/plnt/client/uniffi/plnt_core/plnt_core.kt` still only cover PHA-3074's `core_version()` smoke function; PHA-3075 added `Client`/`EventSink`/`ConnEvent`/`IdentityObj` via `#[uniffi::export]` proc macros but (per that ticket's own notes) never got a toolchain to regenerate the Kotlin side either. **This blocks `VoiceService.kt` from compiling until `build.sh`/CI regenerates bindings.** Flagging this as a build blocker, not a PHA-3077-specific gap. |
| Emulator loopback (frames flow both ways through plnt-core to a local test server) | **Not run.** See "Emulator loopback test" below for the procedure to run once a toolchain is available — it was written but not executed in this sandbox. |
| Device round (wired headset / BT earbuds / phone speaker / mid-call switching) | **Not run** — needs physical hardware. See `MANUAL_TEST_CHECKLIST.md`. |
| Rust-side frame contract (960 samples / 48 kHz mono, `PcmFrame` shape) | Verified by reading `core/src/lib.rs` (PHA-3075) — no Rust changes were needed for this ticket, `Client.sendPcmFrame`/`ConnEvent.PcmFrame` already match the 20 ms frame shape this ticket asked `AudioEngine` to plumb into. |

## Emulator loopback test (procedure — not executed in this sandbox)

Goal: prove a captured 20 ms frame goes `AudioEngine` → `Client.sendPcmFrame`
→ `tsclientlib` → network → a local test server → back → `ConnEvent.PcmFrame`
→ `AudioEngine.onPlaybackFrame` → `AudioTrack`, on the x86_64 emulator (no
Bluetooth on emulator — this only proves the capture/encode/network/
decode/playback chain, not BT routing).

1. Bring up a local TeamSpeak-protocol-compatible test server reachable from
   the emulator at `10.0.2.2:<port>` (the emulator's alias for the host
   loopback). The spike already proved `tsclientlib` talks to a real
   TeamSpeak 3.13.8 server — reuse that server, or the `voicespike.rs`
   two-client harness pattern from the spike
   (<https://nextcloud.phatt.vip/s/2CstCCfpZgHyGws/download>) as the "second
   client" that echoes/receives what the app sends.
2. `./build.sh` then `adb install -r app-debug.apk`.
3. Launch the app, connect to `10.0.2.2:<port>` with a test identity.
4. Grant `RECORD_AUDIO` when prompted; the emulator's virtual microphone
   (silence, or a host mic passthrough if configured in AVD Manager) is
   enough to prove frames flow — content doesn't matter, only that
   `sendPcmFrame` fires and `PcmFrame` events come back.
5. Evidence to capture:
   - `adb logcat -d | grep -E 'plnt.audio|plnt.voice'` showing
     `AudioEngine started`, and no repeated `sendPcmFrame failed` /
     `PcmFrame from client ... expected 960` warnings.
   - The second client (or a packet capture on the test server) showing
     inbound Opus packets from the emulator's client and receiving frames
     back — mirrors the "4 s of Opus voice between two clients with zero
     loss" evidence bar the PHA-3073 spike already established.
6. Report the logcat excerpt + second-client evidence in the issue exactly
   like `VERIFICATION.md`'s CI evidence section, once a toolchain/CI run is
   available to actually build and install the APK.

## Manual device checklist

See `MANUAL_TEST_CHECKLIST.md` — wired headset, Bluetooth earbuds, phone
speaker, and switching mid-call, none of which the emulator can exercise.
