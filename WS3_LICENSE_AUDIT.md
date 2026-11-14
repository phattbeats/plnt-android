# WS3 (PHA-4868) licence audit — voice-quality components

| Component | Licence | Verdict |
|---|---|---|
| Android `NoiseSuppressor` / `AcousticEchoCanceler` (platform) | AOSP, Apache-2.0 | Already in use; OK |
| In-repo `VoiceActivityGate` (energy VAD + hangover) | Own code | OK, no dependency |
| RNNoise (xiph) | BSD-3-Clause | OK to bundle (needs NDK build + notice) |
| WebRTC audio processing (NS/VAD) | BSD-3-Clause | OK; heavy native build |
| Silero VAD | MIT | OK; needs ONNX runtime (MIT) |
| speexdsp | BSD-3-Clause | OK |

No GPL/AGPL candidates were selected. Not verified: exact upstream licence files
(audited from knowledge, not fetched) — confirm before bundling any native lib.

## Status
- Done: licence audit, energy VAD + unit test, `AudioEngine.setVadEnabled`.
- Not done: UI/settings toggle for VAD, per-user volume, RNNoise integration,
  whisper targets, floating PTT. Gradle/Android build not run in this sandbox.
