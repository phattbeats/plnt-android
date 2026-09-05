# PHA-3077 — manual device test checklist

Run on a real Android device (minSdk 26+) against a real TeamSpeak server,
with a second client (desktop TS client, or another PLNT device) in the same
channel to talk to. None of this is exercisable on the emulator (no
Bluetooth, no real audio hardware).

For each row: perform the action, then confirm the two callouts (what you
should hear, what logcat should show). Check `adb logcat -s plnt.audio
plnt.voice` while testing.

## 1. Wired headset (3.5 mm or USB-C)

- [ ] Connect with the wired headset already plugged in. Speak — the other
      client hears you; press their PTT / have them speak — you hear them in
      the headset, not the phone speaker.
- [ ] Logcat shows `AudioEngine started` and no `rehome` line during setup
      (the device was already active before `start()`).
- [ ] Unplug mid-call. Logcat shows `rehoming audio streams: device removed:
      ...(type=3 or 4)` (WIRED_HEADSET/HEADPHONES) and audio continues on
      the phone speaker within ~1 s, no crash.
- [ ] Re-plug mid-call. Logcat shows another `rehoming` line; audio moves
      back to the headset.

## 2. Bluetooth earbuds / headset

- [ ] Pair and connect the BT device *before* opening the app. Connect a
      call. Confirm audio routes to the BT device, not the speaker.
- [ ] API 26–30 devices: logcat shows `SCO_AUDIO_STATE_UPDATED: 1`
      (`SCO_AUDIO_STATE_CONNECTED`) before `AudioEngine started`.
      API 31+ devices: logcat shows `setCommunicationDevice(<BT SCO
      type>) -> true`.
- [ ] Press PTT repeatedly (5+ times over 30s). Confirm **no** audible
      reconnect click/delay between presses — this is the "capture never
      stops" requirement; if you hear the classic BT-SCO reconnect chirp
      each press, `setSending()` is being bypassed somewhere and the capture
      stream is being torn down instead.
- [ ] Disconnect the BT device mid-call (power it off). Logcat shows
      `rehoming audio streams: device removed:
      ...(type=7)` (BLUETOOTH_SCO) or type 8 (A2DP); audio falls back to the
      phone speaker/earpiece within ~2 s (the SCO connect timeout).
- [ ] Reconnect the BT device mid-call. Audio moves back to it.

## 3. Phone speaker (no accessory)

- [ ] Connect a call with nothing plugged in / paired. Confirm audio comes
      out of the phone's main speaker (not earpiece) — `MODE_IN_COMMUNICATION`
      + no external device should default here; if it's coming out the
      earpiece instead, note it (may need an explicit speakerphone-on
      toggle in a follow-up ticket — out of PHA-3077's scope, which is
      routing to *whatever Android currently has active*).
- [ ] AEC/NS effective: with the other client playing continuous audio (e.g.
      music) in the same room, confirm they don't hear their own audio
      looped back through your mic (that's the echo-cancellation working).

## 4. Switching mid-call

- [ ] Start on phone speaker, connect a BT headset mid-call → audio should
      move to BT automatically (Android's device-added path).
- [ ] With BT connected, plug in a wired headset → wired should generally
      win priority per Android's own routing; confirm no double-audio (both
      routes active) and no crash.
- [ ] Unplug the wired headset with BT still connected → falls back to BT
      (not speaker) since BT is still present. If it falls back to the
      speaker instead, note it — that's a `rehome()` device-priority
      question for a follow-up (current implementation always re-picks
      "any BT SCO > wired > USB > earpiece" fresh, so a still-connected BT
      device should win here).

## 5. Crash / longevity smoke

- [ ] 5+ minute call with at least one PTT press per 30s. No crash, no
      `AudioRecord`/`AudioTrack` error logs (`ERROR_INVALID_OPERATION`,
      `ERROR_DEAD_OBJECT`).
- [ ] Backgrounding the app mid-call (press Home) keeps the call running —
      the notification stays posted (`VoiceService` foreground) and audio
      keeps flowing both ways.

## Report format

For each numbered section above, report pass/fail + a one-line note per
failed checkbox, plus a `logcat -d | grep -E 'plnt.audio|plnt.voice'`
excerpt covering at least one full device-switch event.
