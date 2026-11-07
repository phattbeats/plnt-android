# PHA-4851 — Voice reliability evidence

## Delivered source changes

- Playback now retains short and zero-sample results from AudioTrack WRITE_NON_BLOCKING, drains older samples before newer frames, and limits stale queued speech to 200 ms. A negative AudioTrack error clears the queue and is surfaced once through the existing audio-error path.
- Voice activation is an opt-in talk mode. It uses a deterministic RMS energy gate with a 240 ms hangover, so capture and Bluetooth routing stay alive while word endings are not clipped. Push-to-talk and open mic retain their existing behavior.
- The foreground service now owns Android communication audio focus. Any focus loss pauses send and output, preserves the session and route, updates the notification, and resumes only on focus gain. User mute/deafen still wins after focus returns.
- The Android framework AcousticEchoCanceler and NoiseSuppressor remain opportunistic effects on the VOICE_COMMUNICATION input. No third-party NS model, binary, or PocketTS source was added.

## Noise-suppression license audit

The implementation uses only android.media.audiofx.NoiseSuppressor and AcousticEchoCanceler, supplied by the Android platform and queried with isAvailable before use. Chlorophone does not bundle, redistribute, link, or download an external noise-suppression implementation. Therefore this change introduces no new third-party audio-DSP license into the app distribution. Device/OEM DSP behavior remains opaque and must be assessed by device testing; an unavailable or defective platform effect is treated as a documented device limitation, not silently replaced with copied GPL code.

## Benchmark plan — Rust vs Kotlin mixer

Do not select a mixer location by intuition. The current Rust core already owns each talker's jitter queue and emits one mixed 20 ms mono frame, so Kotlin has no per-user PCM identity to scale. Per-user volume therefore requires a deliberate core-surface change before a fair comparison.

Run the same 20 ms, 48 kHz mono workload for 1, 2, 4, and 8 simultaneous talkers on each target device. Capture median, p95, and maximum callback time, allocation count, XRUN/AudioTrack back-pressure events, and end-to-end output latency. Compare:

1. Rust mix: retain the current native mix after applying candidate per-user gains in Rust.
2. Kotlin mix: expose jitter-buffered per-user frames from Rust and mix bounded FloatArrays immediately before AudioTrack writes.

Report the device, Android version, route, battery mode, sample count, payload/loss settings, and raw log artifact with every result. A winner may differ by device; neither path is preselected.

## Loss and jitter procedure

Use a real TS test server and two sender clients. For every route in the matrix below, run each profile for at least five minutes while recording logcat filtered to plnt.audio and plnt.voice plus server-side packet counters:

| Profile | Direction | Expected evidence |
| --- | --- | --- |
| Baseline | both | zero AudioTrack errors; continuous speech |
| 1% random loss | receive and send | jitter buffer masks isolated losses; no reconnect |
| 5% random loss | receive and send | intelligible output; loss metrics recorded |
| 50 ms / 100 ms jitter | receive and send | no growing playback queue or stale replay |
| 30–40 s UDP blackhole | both | TemporaryDisconnect then Resumed; banner clears |
| Wi-Fi to LTE | both | network callback reconnects to same channel |
| Full radio loss over 10 min | both | Waiting for network state; no retry storm; reconnect on available |

Use a controlled router, host network emulator, or documented server-side impairment. Do not call a test passed from a synthetic local callback alone.

## Device matrix

No device result has been fabricated. Fill one row per physical run and attach logcat/screenshot evidence to the issue.

| Device / Android | Route | Background / screen-off | Focus interruption | Loss / jitter | Result |
| --- | --- | --- | --- | --- | --- |
| Pixel-class API 35 | speaker | pending | pending | pending | not run — no device in executor |
| Samsung-class API 34 | Bluetooth SCO | pending | pending | pending | not run — no device in executor |
| API 26–30 device | Bluetooth SCO legacy handshake | pending | pending | pending | not run — no device in executor |
| USB/wired headset | wired | pending | pending | pending | not run — no device in executor |

## Known limits and follow-up boundaries

- Whisper targets are not implemented: the current core has only ordinary channel voice send.
- Per-user volume is not implementable at the Kotlin layer yet: the core emits a mixed frame with client id 0, not individual frames.
- A floating PTT overlay is not implemented; the existing connected-screen PTT and lock-screen notification action remain the supported controls.
- Android volume-key PTT remains foreground-only. The media/headset trigger works under screen lock where Android delivers it.
- Partial-write and VAD unit tests are present in source but were not run here because this executor has no Java, Gradle wrapper, Android SDK/NDK, adb, emulator, Rust, or cargo.
- The current task must not be marked device-verified until the matrix, the benchmark, and loss/jitter artifacts are attached.

## Local verification once a toolchain is available

    ./build.sh
    cd app && ./gradlew testDebugUnitTest
    adb install -r app/app/build/outputs/apk/debug/app-debug.apk
    adb logcat -c
    # Run the matching matrix row and collect logs.
    adb logcat -d -s plnt.audio plnt.voice > voice-reliability-logcat.txt
