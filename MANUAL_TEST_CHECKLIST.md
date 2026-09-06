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

## 6. Network handoff / reconnect (PHA-3078)

Run against a local TeamSpeak 3.13.8 test server so a drop is easy to force.
Watch `adb logcat -s plnt.voice`.

- [ ] Connect, join a non-default channel. Toggle airplane mode on, wait
      3–5 s, toggle it back off. Confirm the notification content flips to
      "Reconnecting…" within ~1 s of the drop, the client reconnects and is
      back in the *same* channel within 10 s of the network returning, and
      the UI never bounces to the bookmarks screen during the whole window.
- [ ] Same test, but switch wifi → mobile data (or vice versa) instead of
      airplane mode — this is the actual wifi↔LTE handoff case, not just a
      full network loss.
- [ ] Background the app (press Home) before triggering the network change.
      Confirm the reconnect still happens and the notification still
      updates even with the Activity gone (this is what proves the
      connection lives in `VoiceService`, not `PlntViewModel`).
- [ ] From the notification: tap Mute, confirm the action label flips to
      "Unmute" and the other client sees you go silent. Tap Talk, confirm
      the other client hears you without touching the app. Tap Disconnect,
      confirm the notification clears and, if this is fired mid-reconnect
      backoff, no further `connect()` attempts appear in logcat afterward.
- [ ] Force a permanent failure (wrong server password) on a fresh connect.
      Confirm it surfaces as an error/returns to bookmarks immediately —
      it must NOT enter the reconnect-retry loop (only network-triggered
      drops after a successful connect should retry).
- [ ] Headset-button PTT: with a wired or BT headset connected and the app
      backgrounded/screen off, hold the headset button. Confirm the other
      client hears you only while held. (Volume-key PTT with the screen off
      is a known gap, not expected to work — see `FOREGROUND_SERVICE.md`.)
- [ ] Transient disconnect (PHA-3277): blackhole the server's voice UDP port
      (not airplane mode — this must stay short enough that tsclientlib
      resumes the session itself rather than the app's own reconnect loop
      taking over) for ~30–40 s, past the ~26 s resend-timeout but well
      under this checklist's other full-drop tests. Confirm the red "temp
      disconnect" banner appears, then clears on its own within a few
      seconds of restoring the network — it must not still be showing 30 s+
      later. Confirm the own row keeps its "(you)" label the whole time,
      including after the banner clears.

## 7. UI screenshot matrix (PHA-3079, emulator)

This section is the acceptance evidence for PHA-3079: one screenshot per
screen/state, taken on the emulator against a local TeamSpeak 3.13.8 server
with two bot clients (the `voicespike` example from the spike is the "other
person"). Unlike sections 1–5, none of this needs real audio hardware.

Setup:

```bash
# 1. local test server + two bots (from the spike write-up in PHA-3072)
cargo run --release -p tsclientlib --example voicespike -- --server 127.0.0.1 --nick jess
# 2. emulator + APK (build.sh output, or the CI artifact)
emulator -avd <avd> -no-snapshot -netdelay none -netspeed full &
adb install -r app/app/build/outputs/apk/debug/app-debug.apk
# 3. capture
adb exec-out screencap -p > shots/<name>.png
```

Capture each of these; the name in brackets maps to the mockup in PHA-3076's
**PLNT UI Design** document:

- [ ] `bookmarks-empty` [§1] — no servers, ghost icon + hint, `+` in the app bar.
- [ ] `bookmarks-list` [§2] — two or more rows, 72dp each, trailing dot sage on
      the one connected this session and `#4a4840` on the others.
- [ ] `bookmarks-swipe-edit` [§2] — mid-swipe right, mauve EDIT strip showing.
- [ ] `bookmarks-swipe-del` [§2] — mid-swipe left, oxblood DEL strip showing.
- [ ] `sheet-add` [§3] — bottom sheet, "Add server", SAVE at 40% (address empty).
- [ ] `sheet-edit-password` [§3] — "Edit server", password masked, then a second
      shot with the eye toggled on.
- [ ] `connected-tree` [§4] — channel tree with a collapsed channel (`▸`), an
      expanded one (`▾`) and an "(empty)" channel.
- [ ] `connected-other-talking` [§4] — bot talking: sage bar + ring + caption,
      row background lifted to `#141813`.
- [ ] `connected-you-talking` [§4] — PTT held: gold bar/ring/caption, bold name,
      row background `#1a1710`, PTT circle filled gold.
- [ ] `connected-muted` [§4] — mic muted: MIC pill; then deafened as well so
      both MIC and SND show on the same row (this is the case the old
      single-value presence enum could not render).
- [ ] `settings` [§5] — mauve section headers, gold radio, both PTT switches on
      at once.
- [ ] `settings-identity` [§5] — export sheet and import sheet.
- [ ] `notification` [§6] — shade, connected and muted variants.
- [ ] `lockscreen` [§7] — notification on the lock screen; then a shot taken
      while holding the notification's Talk action with the bot confirming
      audio.

Two states in the design's table **cannot** be produced from live data yet and
should be reported as not-captured rather than faked: a peer showing MIC/SND
(plnt-core reports no peer mute state) and the away treatment (no idle time in
the event stream). Both are implemented in Compose and flagged on PHA-3076.

Also confirm while capturing:

- [ ] Talk state appears/disappears in step with the bot's audio, with no
      visible lag — it is driven by `ConnEvent`, so a delay means something is
      polling.
- [ ] Toggling mute from the notification updates the on-screen mute circle
      and your own row's MIC pill (service → UI state listener).

## Report format

For each numbered section above, report pass/fail + a one-line note per
failed checkbox, plus a `logcat -d | grep -E 'plnt.audio|plnt.voice'`
excerpt covering at least one full device-switch event.
