# PHA-3078 — Foreground service, notification actions, reconnect, identity + bookmark storage

Goal: the connection survives screen-off, app switching, and a wifi↔LTE handoff, and the user can
mute/talk/disconnect from the persistent notification.

## What changed

Before this ticket, `VoiceService` (`app/app/src/main/kotlin/com/plnt/client/service/VoiceService.kt`,
landed in PHA-3077) already existed but was never actually wired into the app: `PlntViewModel`
(PHA-3079) created its own `CoreClient` directly via `CoreBridge.newClient()` and never touched the
service or its `AudioEngine`. Concretely, that meant no captured microphone audio ever reached
plnt-core — the "voice" app didn't transmit voice. This ticket makes `VoiceService` the single owner
of the connection and rewires `PlntViewModel` to bind to it instead.

- **`VoiceService`** now:
  - Owns `CoreClient` + `AudioEngine` for the life of a call (same as PHA-3077, now actually used).
  - Exposes a `Binder` API (`connect`/`disconnect`/`joinChannel`/`setInputMuted`/`setOutputMuted`/
    `setPushToTalk`/`setPttMode`/`setEventListener`) that `PlntViewModel` calls instead of touching
    `CoreClient` itself.
  - Registers a `ConnectivityManager.NetworkCallback` (`registerDefaultNetworkCallback`). On a
    default-network change while connected (`onAvailable` with a network different from the one we
    connected on) or a network loss (`onLost`), it tears the `CoreClient` down and reconnects with
    the same identity + bookmark, then rejoins the last channel the client's own `ClientMoved` event
    reported (tracked in `lastChannelId`). Retries back off exponentially from 1 s, doubling, capped
    at 30 s, and give up after 10 attempts (surfaces a real `Disconnected` instead of retrying
    forever against, e.g., a server that's actively rejecting the identity).
  - Emits a synthetic `CoreEvent.Reconnecting` (added to `CoreBridge`'s sealed `CoreEvent`, not a
    real uniffi event) while an automatic reconnect is in flight, so the UI shows "reconnecting"
    instead of bouncing back to the bookmarks screen the way a real `Disconnected` would.
  - Holds a partial `PowerManager.WakeLock` ("plnt:voice") from the moment a connection succeeds
    until `disconnect()`/give-up, so a doze-mode CPU suspend doesn't stall the reconnect logic.
  - Adds three notification actions — Mute/Unmute, Talk/Stop talking, Disconnect — as
    `PendingIntent.getService` calls back into the same service's `onStartCommand`, matching the
    ticket's "mute/talk/disconnect from the notification" ask. "Talk" is a toggle (a notification tap
    can't do press-and-hold) that drives the same `AudioEngine.setSending` gate PTT uses.
  - Runs a `MediaSessionCompat` and reacts to `KEYCODE_HEADSETHOOK` / `KEYCODE_MEDIA_PLAY_PAUSE` as a
    press-and-hold PTT source, active for the life of the call — this is the piece that works with
    the screen off / app backgrounded, unlike `MainActivity.onKeyDown`'s volume/headset PTT handling
    (PHA-3079), which only fires while that Activity has input focus.

- **`IdentityStore`** (`app/app/src/main/kotlin/com/plnt/client/data/IdentityStore.kt`, new): the
  identity PEM moved from PHA-3079's plain `SharedPreferences` into Jetpack Security's
  `EncryptedSharedPreferences` (AES256-GCM values, AES256-SIV keys, master key in the Android
  Keystore).

- **`PlntDataStore`** (`app/app/src/main/kotlin/com/plnt/client/data/PlntDataStore.kt`, new):
  bookmarks + PTT settings moved from the same plain-`SharedPreferences` stopgap into Jetpack
  Preferences DataStore. Bookmarks keep the same on-disk JSON shape so there's no migration step.

- **`PlntViewModel`** (rewritten): binds to `VoiceService` (`bindService` + `ContextCompat.
  startForegroundService` on `connect()`) instead of owning a `CoreClient`. Every public method kept
  its exact signature (`connect(Bookmark)`, `disconnect()`, `joinChannel(Long)`, `setMuted(Boolean)`,
  etc.) so none of the PHA-3076 Compose screens needed to change. `onCleared()` now deliberately does
  **not** disconnect — that's the entire point of this ticket, the call has to outlive the ViewModel.

- **Manifest**: added `ACCESS_NETWORK_STATE` (network callback) and `WAKE_LOCK` permissions.

- **Gradle**: added `androidx.security:security-crypto`, `androidx.datastore:datastore-preferences`,
  `androidx.media:media` (`libs.versions.toml` + `app/app/build.gradle.kts`).

## What's NOT covered here

- Volume-key PTT while the screen is off / app backgrounded is **not** solved by this ticket.
  Android delivers hardware volume-key presses to the system volume UI when no app has input focus;
  there is no supported hook to intercept them in that state short of an accessibility service, which
  is out of scope. `MediaSessionCompat` only reliably captures the single-button headset hook / a
  Bluetooth headset's play-pause button, which this ticket does wire up. This matches the ticket's
  literal ask ("media-button ... push-to-talk hook") but is a real gap against "volume-key PTT with
  the screen off" if that was assumed to work everywhere — flagging it explicitly rather than
  reporting it as done.
- A first-connect failure (bad address/port/credentials, before ever reaching `Connected`) does not
  auto-retry — it surfaces immediately as `Disconnected` so the user sees the real error instead of
  watching the app retry a config problem for 30+ seconds. Auto-reconnect only kicks in after the
  client has connected successfully at least once.

## Verification status

Same constraint as every prior PHA-307x ticket in this repo (see `AUDIO_ENGINE.md`,
`VERIFICATION.md`): this was written in a sandbox with **no Android SDK, no NDK, no Rust/cargo
toolchain, and no emulator**. Concretely:

| Check | Status |
|---|---|
| Kotlin compiles | **Not verified here** — no `gradlew`/Android SDK in this sandbox. CI (`.github/workflows/build.yml`) runs `./build.sh` (which includes `assembleDebug`) on every push to `main`; that is this repo's actual compile gate, per README. |
| Emulator round-trip: connect → toggle airplane mode → reconnect to same channel within 10 s, with a logcat excerpt | **Not run** — requires an Android emulator/device, which this sandbox and the CI runner (`ubuntu-24.04`, build-only, no `android-emulator` step) both lack. Added as `## 6. Network handoff / reconnect` in `MANUAL_TEST_CHECKLIST.md` for whoever runs the physical/emulator round — that file already tracks the PHA-3077 audio device round for the same reason. |
| Notification actions (mute/talk/disconnect) post correctly and toggle the right service state | **Not run** — same blocker; procedure is in the checklist addition. |
| EncryptedSharedPreferences / DataStore read back what they wrote | **Not run** — no JVM/Robolectric harness in this sandbox either; reviewed by reading the Jetpack Security / DataStore API contracts, not exercised. |

## Reconnect / network-handoff test procedure (not executed in this sandbox)

1. Connect to a local TeamSpeak 3.13.8 test server, join a non-default channel.
2. Confirm the notification shows the connected state and the three actions.
3. Toggle airplane mode on, wait 3–5 s, toggle it off (or switch wifi → mobile data in Settings).
4. Expect: notification content flips to "Reconnecting…" within ~1 s of the network drop; once the
   network is back, the client reconnects and rejoins the same channel within 10 s (per the ticket's
   own acceptance bar); the channel tree / roster UI does not bounce to the bookmarks screen at any
   point during this.
5. Capture `adb logcat -s plnt.voice` across the whole window — expect `connect() failed` /
   `default network changed` / `default network lost` lines bracketing the reconnect, and no crash.
6. Tap "Disconnect" from the notification mid-reconnect-backoff; expect the retry loop to stop
   immediately (no further `connect()` attempts in logcat) and the notification to clear.

## Telling an OS service kill apart from a user disconnect (PHA-3283)

`onDestroy()` used to call the same `disconnect()` a Disconnect tap does, so an Android-initiated
service teardown — low memory, an OEM background/battery policy, doze/standby — reached the UI as a
user-initiated disconnect carrying the core's generic `client.disconnect` reason. Every teardown now
goes through `VoiceService.shutdown(cause, reason)`, and the cause is supplied by the caller that
knows it: `USER` for the Disconnect action and the in-app button, `SYSTEM_KILL` for `onDestroy()`,
`CONNECTION_LOST` / `RECONNECT_FAILED` / `ERROR` for the failure paths.

To confirm a suspected kill on a device, capture both buffers across the repro window:

```
adb logcat -b system -b main | grep -E 'plnt\.lifecycle|plnt\.voice|ActivityManager: Killing|lowmemorykiller'
```

`plnt.lifecycle` carries one line per service lifecycle callback — `onCreate`, `onStartCommand`,
`onTrimMemory`, `onTaskRemoved`, `onLowMemory`, `onDestroy` — each stamped `t+<n>s` since the last
successful connect, so "it timed out after a while" becomes an exact idle duration. Two signatures
worth knowing:

- `onTrimMemory ... COMPLETE` followed by `onDestroy` (and an `ActivityManager: Killing` line in the
  system buffer) is a memory reclaim.
- `onStartCommand ... null intent — START_STICKY restart after a kill` is Android restarting the
  service after it died. Nothing reconnects there: `connectionParams` lived only in the dead
  instance, so the restarted service comes up idle. Persisting it is deliberately left to the
  follow-up that decides between hardening for survival and auto-reconnecting.
