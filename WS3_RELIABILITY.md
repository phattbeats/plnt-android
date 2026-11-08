# WS3 (PHA-4869) — background recovery, Rust vs Kotlin benchmark, device matrix, limits

Status: **protocol and documented limits only. No on-device measurements exist yet.**
This sandbox has no Android device, JDK, Gradle or cargo, so nothing below was run.
Recovery behaviour is taken from `VoiceService.kt` and `FOREGROUND_SERVICE.md`;
every row in the results tables is empty on purpose.

## 0. What the WS3 branch changes in the app (PHA-4851)

- **Partial writes** (`PlaybackWriter`): `AudioTrack.write(WRITE_NON_BLOCKING)` acknowledges
  samples, not frames. Only the accepted prefix leaves the queue, newer frames never overtake a
  tail, and the queue is capped at 200 ms so an unhealthy device cannot make remote speech stale.
  A negative result gets up to three reopen attempts, then output is disabled and reported once
  through the existing audio-error path. `PlaybackWriterTest` covers these cases on the JVM.
- **Voice activation** (`VoiceActivityGate`, `PttMode.VOICE_ACTIVATION`): a third talk mode next
  to push-to-talk and open mic. Energy gate with hysteresis (open 0.015 RMS, close 0.010) and a
  300 ms hangover so word endings are kept. Capture and the Bluetooth route stay open exactly as
  with PTT; only forwarding is gated. The gate state drives the connected-screen label and the
  notification. Thresholds are a starting point: microphone gain and the platform noise suppressor
  differ per device, so the matrix below tunes them.
- **Call interruption / audio focus** (`AudioFocusController`): focus is requested with
  `USAGE_VOICE_COMMUNICATION` for the life of the call. A transient loss (phone call, navigation
  prompt) pauses send and playback while leaving capture open so SCO does not drop; regain rehomes
  the route and resumes. Ducking is ignored on purpose. Permanent loss pauses and re-asks for focus.
  The notification reads "paused by another call" meanwhile.
- **Loss / jitter / simultaneous talkers** (`VoiceLinkHarnessTest`): a JVM harness that drives
  `PlaybackWriter` with configurable loss, jitter and talker count and counts late drops. It is the
  repeatable half of the evidence; the on-network half is section 1b.
- **Noise suppression**: only the platform `NoiseSuppressor` / `AcousticEchoCanceler`, queried with
  `isAvailable` first. No third-party DSP is bundled; see `WS3_LICENSE_AUDIT.md`.

Not implemented, and why: **whisper targets** (the core has only ordinary channel voice send),
**per-user volume** (the core emits one mixed frame with client id 0, so Kotlin has no per-user PCM
to scale; needs a core-surface change first, see section 2), **floating PTT overlay** (the
connected-screen PTT and the lock-screen notification action remain the controls).

### 1b. Loss and jitter on a real link

Use a real TeamSpeak test server and two sender clients. For every route in the matrix, run each
profile for at least five minutes with logcat filtered to `plnt.audio` and `plnt.voice` plus the
server-side packet counters. Do not call a profile passed from a synthetic local callback alone.

| Profile | Direction | Expected evidence |
| --- | --- | --- |
| Baseline | both | zero AudioTrack errors; continuous speech |
| 1% random loss | receive and send | jitter buffer masks isolated losses; no reconnect |
| 5% random loss | receive and send | intelligible output; loss metrics recorded |
| 50 ms / 100 ms jitter | receive and send | no growing playback queue or stale replay |
| 30–40 s UDP blackhole | both | TemporaryDisconnect then Resumed; banner clears |
| Wi-Fi to LTE | both | network callback reconnects to the same channel |
| Full radio loss, 10 min | both | "Waiting for network"; no retry storm; reconnect on available |

## 1. Recovery scenarios (expected behaviour, to be verified on device)

| Scenario | Mechanism in code | Pass criterion |
|---|---|---|
| Screen off, 30 min | Foreground service + `plnt:voice` partial wakelock | Voice received/sent throughout; no reconnect |
| Doze (`adb shell dumpsys deviceidle force-idle`) | Wakelock, foreground service | Connection survives or reconnects < 30 s |
| Wi-Fi to LTE handoff | `registerDefaultNetworkCallback` onAvailable/onLost, backoff 1 s to 30 s | Audio resumes in the same channel < 15 s |
| Airplane mode 60 s on/off | onLost, then exponential retry | Reconnects and rejoins last channel |
| Process kill (`adb shell am kill`, swipe from Recents) | `START_STICKY`, `restorePersistedSession()` | Session restored without user action |
| OEM battery killer (Xiaomi, Samsung, Huawei) | Not controllable | Record outcome; list the exemption steps users need |
| Phone call / other audio app | Audio focus hold (PHA-4866) | Pauses, then recovers after the call |
| Bluetooth headset connect/disconnect | `AudioDeviceCallback` to `rehome()` | Audio re-routes < 3 s, no crash |

Run each scenario three times per device and record time-to-audio-resume
(from the `logLifecycle` / `Diag` output).

## 2. Rust vs Kotlin benchmark protocol

Question: should the 20 ms frame path (resample, accumulate, Opus encode/decode,
jitter buffer) stay in `plnt-core` (Rust) or have parts moved to Kotlin?
Today encode/decode/jitter are Rust (`core/src/lib.rs`); resample/accumulate are
Kotlin (`AudioEngine`: `Resampler`, `FloatFrameAccumulator`).

Measure, per device, over a 10 min two-talker call with screen off:
1. CPU % of the app process (`adb shell top -H -p <pid>`, 1 s samples).
2. Per-frame time for resample+accumulate (Kotlin) vs Opus encode and decode (Rust),
   via `System.nanoTime()` / `Instant::now()` around each call, reported as p50/p95/p99.
3. Battery: `adb shell dumpsys batterystats --reset`, then the mAh estimate after 30 min.
4. Underruns/glitches: `AudioTrack.getUnderrunCount()` and jitter-buffer drop counters.
5. Variant B for comparison: port `Resampler` to Rust (exposed over UniFFI) and re-run 1-4.
   Keep per-frame FFI crossings to one; a crossing per 20 ms is expected to be cheap, but measure it.

Decision rule: only move code across the boundary if p99 frame time or battery
differs by > 10 % on at least 3 of the devices in the matrix.

| Device | Variant | CPU % | p50/p95/p99 ms | mAh/30 min | Underruns |
|---|---|---|---|---|---|
| | A (current) | | | | |
| | B (Rust resampler) | | | | |

## 3. Device matrix (to fill)

| Device | Android | OEM skin | Screen-off | Handoff | Kill/restore | BT headset | Notes |
|---|---|---|---|---|---|---|---|
| Pixel (latest) | | stock | | | | | |
| Pixel (API 26-28) | | stock | | | | | |
| Samsung mid-range | | One UI | | | | | battery optimisation |
| Xiaomi / Redmi | | MIUI/HyperOS | | | | | autostart + battery saver |
| Low-end (<=3 GB RAM) | | any | | | | | |

## 4. Documented limits (known now, from code and platform behaviour)

- Volume-key PTT does not work with the screen off or the app backgrounded; only the headset hook and
  Bluetooth play/pause do (`FOREGROUND_SERVICE.md`).
- A first-connect failure does not auto-retry; reconnect starts only after a successful connect.
- Reconnect backoff caps at 30 s; worst-case recovery after long connectivity loss is about 30 s plus handshake.
- Aggressive OEM task killers can stop the service despite the foreground service and `START_STICKY`;
  users may have to exempt the app from battery optimisation. Needs per-OEM confirmation on hardware.
- Linear-interpolation resampling is lower quality than a polyphase resampler (relevant only on devices
  whose mic does not open at 48 kHz).
- Android 14+ requires the microphone foreground-service type; starting it from the background is restricted.
  Verify on an API 34+ device.

## 5. Remaining work

All measurements in sections 1-3 need physical devices and an Android toolchain, so someone with that
hardware has to run them. The tables above are the capture format.
