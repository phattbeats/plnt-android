package com.plnt.client.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentCallbacks2
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.ConnectivityManager
import android.net.Network
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import com.plnt.client.audio.AudioEngine
import com.plnt.client.audio.CORE_FRAME_SAMPLES
import com.plnt.client.core.ChatMessageTarget
import com.plnt.client.core.CoreBridge
import com.plnt.client.core.CoreClient
import com.plnt.client.core.CoreEvent
import com.plnt.client.core.DisconnectCause
import com.plnt.client.model.Bookmark
import com.plnt.client.model.InputRoute
import com.plnt.client.model.PttMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "plnt.voice"
// Separate tag so a repro capture can be filtered down to just the service's
// lifecycle callbacks (PHA-3283) without the rest of the voice-path chatter.
private const val LIFECYCLE_TAG = "plnt.lifecycle"
// Bumped from "plnt-voice": that channel was created IMPORTANCE_LOW, which
// makes the notification "Silent", and Android keeps silent notifications off
// the lock screen entirely — so the Talk action the design requires from the
// lock screen was unreachable. A channel's importance cannot be raised after
// it is created, so the fix needs a new id.
private const val NOTIFICATION_CHANNEL_ID = "plnt-voice-call"
private const val NOTIFICATION_ID = 1
private const val INITIAL_BACKOFF_MS = 1_000L
private const val MAX_BACKOFF_MS = 30_000L
private const val MAX_RECONNECT_ATTEMPTS = 10

/**
 * Mic/output/transmit state as the service holds it. The notification actions
 * and the media-button PTT change these behind the UI's back, so the UI has to
 * be told rather than assume its own optimistic value still holds (PHA-3079:
 * talk state renders from events, never from polling).
 */
data class VoiceState(
    val inputMuted: Boolean,
    val outputMuted: Boolean,
    val transmitting: Boolean,
    val pttMode: PttMode,
    val preferredInputRoute: InputRoute,
    val availableInputRoutes: Set<InputRoute>,
)

/**
 * Foreground service that owns the single [AudioEngine] + [CoreClient] for
 * the lifetime of a call (PHA-3077/PHA-3078). This is the piece PHA-3079's
 * `PlntViewModel` originally bypassed — that ViewModel talked to a
 * `CoreClient` it created itself and never touched this service or its
 * `AudioEngine`, so no captured audio ever actually reached plnt-core. This
 * ticket makes the service the one true owner: `PlntViewModel` binds to it
 * and forwards UI intents, so the connection (and the mic) survives the
 * Activity/ViewModel going away, screen-off, and — the point of this
 * ticket — a wifi↔LTE handoff, which tsclientlib does not survive on its
 * own (a torn-down `CoreClient` has to be recreated against the new route).
 *
 * Goes through [CoreBridge] rather than `uniffi.plnt_core.*` directly, per
 * that file's own convention: it's the one seam that isolates generated
 * binding quirks from the rest of the app.
 */
class VoiceService : Service() {
    inner class LocalBinder : Binder() {
        fun service(): VoiceService = this@VoiceService
    }

    private val binder = LocalBinder()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var client: CoreClient? = null
    private var audioEngine: AudioEngine? = null
    private var eventListener: ((CoreEvent) -> Unit)? = null
    private var stateListener: ((VoiceState) -> Unit)? = null

    private data class ConnectionParams(val bookmark: Bookmark, val identityPem: String)
    private var connectionParams: ConnectionParams? = null

    /**
     * Why the connection was torn down, or null while one is live or
     * reconnecting. Replaces the old `userInitiatedDisconnect` boolean, which
     * [onDestroy] also set — so an OS-initiated service kill reached the UI
     * labelled as a user disconnect and diagnosis had nothing to go on
     * (PHA-3283). Set in exactly one place, [shutdown], by the caller that
     * actually knows the cause.
     */
    private var disconnectCause: DisconnectCause? = null
    private var hasConnectedOnce = false
    private var reconnecting = false
    private var reconnectAttempts = 0
    private var reconnectBackoffMs = INITIAL_BACKOFF_MS
    private var reconnectJob: Job? = null

    private var ownClientId: Long? = null
    private var lastChannelId: Long? = null

    /** `SystemClock.elapsedRealtime()` of the last successful connect, for [logLifecycle]. */
    private var connectedAtElapsedMs: Long? = null

    private var pttMode: PttMode = PttMode.PUSH_TO_TALK
    private var headsetTriggerArmed = false
    private var inputMuted = false
    private var outputMuted = false
    private var transmitting = false
    private var preferredInputRoute: InputRoute = InputRoute.AUTO
    private var availableInputRoutes: Set<InputRoute> = setOf(InputRoute.AUTO, InputRoute.BUILTIN_MIC)

    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var mediaSession: MediaSessionCompat

    private val connectivityManager by lazy { getSystemService(ConnectivityManager::class.java) }
    private var currentNetwork: Network? = null
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val previous = currentNetwork
            currentNetwork = network
            if (previous != null && previous != network && hasConnectedOnce && connectionParams != null) {
                Log.i(TAG, "default network changed ($previous -> $network) — reconnecting")
                scheduleReconnect(0L)
            }
        }

        override fun onLost(network: Network) {
            if (network == currentNetwork) {
                currentNetwork = null
                if (hasConnectedOnce && connectionParams != null) {
                    Log.i(TAG, "default network lost — will reconnect when one is available")
                    scheduleReconnect(reconnectBackoffMs)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        logLifecycle("onCreate", "")
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "PlntVoice").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                    val event = mediaButtonIntent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                        ?: return false
                    val isPttKey = event.keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                        event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    // Settings > "Headset button" gates this: with the switch off
                    // the media button must fall through untouched.
                    if (!isPttKey || !headsetTriggerArmed || pttMode != PttMode.PUSH_TO_TALK) return false
                    when (event.action) {
                        KeyEvent.ACTION_DOWN -> setPushToTalk(true)
                        KeyEvent.ACTION_UP -> setPushToTalk(false)
                        else -> return false
                    }
                    return true
                }
            })
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        if (intent == null) {
            // START_STICKY handed us a null Intent, which only happens when
            // Android killed this service and restarted it. `connectionParams`
            // died with the old instance, so there is nothing here to reconnect
            // to — the user is simply left disconnected (PHA-3283 item 3).
            logLifecycle("onStartCommand", "null intent — START_STICKY restart after a kill, flags=$flags")
        } else {
            logLifecycle("onStartCommand", "action=${intent.action} flags=$flags")
        }
        when (intent?.action) {
            ACTION_TOGGLE_MUTE -> setInputMuted(!inputMuted)
            ACTION_TOGGLE_TALK -> setPushToTalk(!transmitting)
            ACTION_DISCONNECT -> disconnect()
        }
        return START_STICKY
    }

    /** Connect (or reconnect) using a bookmark + identity. `onEvent` observes app-level events. */
    fun connect(bookmark: Bookmark, identityPem: String) {
        disconnectCause = null
        hasConnectedOnce = false
        reconnecting = false
        reconnectAttempts = 0
        reconnectBackoffMs = INITIAL_BACKOFF_MS
        lastChannelId = null
        ownClientId = null
        connectionParams = ConnectionParams(bookmark, identityPem)
        doConnect()
    }

    fun setEventListener(listener: ((CoreEvent) -> Unit)?) {
        eventListener = listener
    }

    /**
     * Observe mute/transmit state. Fires immediately with the current value so a
     * UI that binds mid-call (or rebinds after its Activity was recreated) shows
     * what the service is actually doing, including changes made from the
     * notification actions or a headset button while the app was backgrounded.
     */
    fun setStateListener(listener: ((VoiceState) -> Unit)?) {
        stateListener = listener
        listener?.invoke(currentState())
    }

    fun currentState(): VoiceState =
        VoiceState(inputMuted, outputMuted, transmitting, pttMode, preferredInputRoute, availableInputRoutes)

    /** PHA-3132 follow-up: user-facing mic route override, independent of PTT mode. */
    fun setPreferredInputRoute(route: InputRoute) {
        preferredInputRoute = route
        audioEngine?.setPreferredInputRoute(route)
        emitState()
    }

    private fun emitState() {
        stateListener?.invoke(currentState())
    }

    fun joinChannel(channelId: Long, password: String?) {
        lastChannelId = channelId
        runCatching { client?.joinChannel(channelId, password) }
    }

    fun setInputMuted(muted: Boolean) {
        inputMuted = muted
        runCatching { client?.setInputMuted(muted) }
        applySendingGate()
        updateNotification()
        emitState()
    }

    fun setOutputMuted(muted: Boolean) {
        outputMuted = muted
        runCatching { client?.setOutputMuted(muted) }
        emitState()
    }

    fun sendTextMessage(target: ChatMessageTarget, text: String) {
        runCatching { client?.sendTextMessage(target, text) }
    }

    /** Press-and-hold PTT / media-button PTT gate. Capture keeps running, only forwarding toggles. */
    fun setPushToTalk(pressed: Boolean) {
        transmitting = pressed
        applySendingGate()
        updateNotification()
        emitState()
    }

    /**
     * Settings > "Headset button". Lives here rather than in the Activity
     * because the media session that receives the button is owned by this
     * service — that's what makes it work with the screen locked.
     */
    fun setHeadsetTriggerArmed(armed: Boolean) {
        headsetTriggerArmed = armed
    }

    fun setPttMode(mode: PttMode) {
        pttMode = mode
        transmitting = mode == PttMode.OPEN_MIC
        applySendingGate()
        emitState()
    }

    private fun applySendingGate() {
        val engine = audioEngine ?: return
        engine.setSending(!inputMuted && transmitting)
    }

    /**
     * The user hung up — the notification's Disconnect action or the in-app
     * button, and nothing else. [onDestroy] deliberately does not route
     * through here; see the note there.
     */
    fun disconnect() {
        shutdown(DisconnectCause.USER, "disconnected by user")
    }

    /**
     * The single teardown path. Every caller supplies the cause it actually
     * knows, which is the whole point of PHA-3283: before this, [onDestroy]
     * and a Disconnect tap both went through one `disconnect()` that stamped
     * `userInitiatedDisconnect = true`, so an OS kill and a user hang-up were
     * indistinguishable by the time they reached the UI.
     *
     * `notify = false` is for teardowns nobody is waiting to hear about (the
     * service being stopped while idle), so the UI does not get a spurious
     * "disconnected" for a connection that never existed.
     */
    private fun shutdown(cause: DisconnectCause, reason: String, notify: Boolean = true) {
        Log.i(TAG, "shutdown: cause=$cause reason=$reason")
        disconnectCause = cause
        reconnectJob?.cancel()
        reconnecting = false
        connectionParams = null
        hasConnectedOnce = false
        lastChannelId = null
        ownClientId = null
        connectedAtElapsedMs = null
        teardownClientOnly()
        releaseWakeLock()
        mediaSession.isActive = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        transmitting = false
        emitState()
        if (notify) eventListener?.invoke(CoreEvent.Disconnected(cause, reason))
    }

    override fun onDestroy() {
        // Not `disconnect()`. onDestroy() fires whenever Android reclaims the
        // service — low memory, an OEM background/battery policy, doze or a
        // standby bucket restriction — none of which the user did. Routing it
        // through the user path is what made the PHA-3238 drop surface as a
        // bare "client.disconnect" with no hint it was not the user's own
        // doing (PHA-3283).
        val wasConnected = connectionParams != null || client != null
        logLifecycle("onDestroy", "wasConnected=$wasConnected")
        runCatching { connectivityManager.unregisterNetworkCallback(networkCallback) }
        shutdown(
            DisconnectCause.SYSTEM_KILL,
            "Android stopped the voice service",
            notify = wasConnected,
        )
        mediaSession.release()
        super.onDestroy()
    }

    // ---- lifecycle instrumentation (PHA-3283 item 1) ----------------------

    /**
     * The service was killed because its task was swiped out of Recents. A
     * distinct cause from an OS reclaim, and the only one of these the user
     * did on purpose — worth telling apart in a repro log.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        logLifecycle("onTaskRemoved", "task swiped from Recents")
        super.onTaskRemoved(rootIntent)
    }

    override fun onTrimMemory(level: Int) {
        logLifecycle("onTrimMemory", "level=$level (${trimLevelName(level)})")
        super.onTrimMemory(level)
    }

    override fun onLowMemory() {
        logLifecycle("onLowMemory", "")
        super.onLowMemory()
    }

    /**
     * One greppable line per service lifecycle callback, stamped with
     * time-since-connect. PHA-3283 item 1 asks for the service kill to be
     * *confirmed* rather than inferred; this is that evidence:
     *
     * ```
     * adb logcat -b system -b main | grep -E 'plnt\.lifecycle|ActivityManager: Killing|lowmemorykiller'
     * ```
     *
     * lines up the app's own view of the teardown with the system's reason for
     * it, and `t+<n>s` answers "after how long idle" directly.
     *
     * Deliberately not debug-only and deliberately `Log.w` (survives release
     * log levels): the drop being chased only reproduces on a real device over
     * tens of minutes, so it has to be visible in whatever build the reporter
     * happens to be running.
     */
    private fun logLifecycle(callback: String, detail: String) {
        val since = connectedAtElapsedMs?.let { "t+${(SystemClock.elapsedRealtime() - it) / 1000}s" } ?: "t+-"
        Log.w(LIFECYCLE_TAG, "$callback $since connected=${client != null} reconnecting=$reconnecting $detail")
    }

    private fun trimLevelName(level: Int): String = when (level) {
        ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> "COMPLETE — next process to be killed"
        ComponentCallbacks2.TRIM_MEMORY_MODERATE -> "MODERATE"
        ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> "BACKGROUND"
        ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> "UI_HIDDEN"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> "RUNNING_CRITICAL"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> "RUNNING_LOW"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> "RUNNING_MODERATE"
        else -> "unknown"
    }

    // ---- connection lifecycle ----------------------------------------

    private fun doConnect() {
        val params = connectionParams ?: return
        teardownClientOnly()

        lateinit var engine: AudioEngine
        engine = AudioEngine(
            applicationContext,
            onCaptureFrame = { frame ->
                // frame is 960 samples @ 48 kHz mono, exactly what sendPcmFrame expects.
                try {
                    client?.sendPcmFrame(frame)
                } catch (t: Throwable) {
                    Log.w(TAG, "sendPcmFrame failed", t)
                }
            },
            onInputRoutesChanged = { routes -> mainHandler.post { availableInputRoutes = routes; emitState() } },
        )
        engine.setPreferredInputRoute(preferredInputRoute)
        audioEngine = engine

        val c = CoreBridge.newClient(
            onEvent = { ev -> mainHandler.post { handleEvent(ev) } },
            onPcmFrame = { clientId, samples ->
                if (samples.size == CORE_FRAME_SAMPLES) {
                    engine.onPlaybackFrame(samples)
                } else {
                    Log.w(TAG, "PcmFrame from client $clientId: ${samples.size} samples, expected $CORE_FRAME_SAMPLES")
                }
            },
        )
        client = c
        engine.start()
        applySendingGate()

        serviceScope.launch(Dispatchers.IO) {
            try {
                c.connect(
                    params.bookmark.address,
                    params.bookmark.port,
                    params.bookmark.nickname,
                    params.identityPem,
                    params.bookmark.serverPassword,
                )
            } catch (t: Throwable) {
                Log.e(TAG, "connect() failed", t)
                withContext(Dispatchers.Main) {
                    if (client === c) {
                        engine.shutdown()
                        audioEngine = null
                        client = null
                    }
                    if (hasConnectedOnce) {
                        scheduleReconnect(reconnectBackoffMs)
                    } else {
                        shutdown(DisconnectCause.ERROR, t.message ?: t.javaClass.simpleName)
                    }
                }
            }
        }
    }

    private fun handleEvent(ev: CoreEvent) {
        when (ev) {
            is CoreEvent.Connected -> {
                ownClientId = ev.ownClientId
                hasConnectedOnce = true
                connectedAtElapsedMs = SystemClock.elapsedRealtime()
                reconnecting = false
                reconnectAttempts = 0
                reconnectBackoffMs = INITIAL_BACKOFF_MS
                lastChannelId?.let { chan -> runCatching { client?.joinChannel(chan, null) } }
                acquireWakeLock()
                mediaSession.isActive = true
                mediaSession.setPlaybackState(
                    PlaybackStateCompat.Builder()
                        .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1f)
                        .build(),
                )
                updateNotification()
                eventListener?.invoke(ev)
            }
            is CoreEvent.ClientMoved -> {
                if (ev.clientId == ownClientId) lastChannelId = ev.channelId
                eventListener?.invoke(ev)
            }
            is CoreEvent.Resumed -> {
                // tsclientlib's internal reconnect can hand back a different
                // own_client_id than before the blip (PHA-3277) — re-sync the
                // copy `ClientMoved` above compares against, same as Connected.
                ownClientId = ev.ownClientId
                eventListener?.invoke(ev)
            }
            is CoreEvent.Disconnected -> {
                // APP_REQUESTED means our own shutdown()/teardownClientOnly()
                // asked the core to stop, so the service already knows the real
                // cause and has acted on it — only an unrequested drop is news.
                // This used to be inferred from `userInitiatedDisconnect`, which
                // missed the reconnect case: doConnect() tears the *old* client
                // down first, and that echo arriving after the new attempt had
                // started re-armed the backoff a second time.
                if (ev.cause == DisconnectCause.APP_REQUESTED) return
                // A drop that raced a teardown we have already reported.
                if (disconnectCause != null) return
                if (hasConnectedOnce && connectionParams != null) {
                    scheduleReconnect(reconnectBackoffMs)
                } else {
                    // First-connect failure delivered async instead of thrown
                    // from doConnect() — clean up the same way, but do not call
                    // it a user disconnect.
                    shutdown(ev.cause, ev.reason)
                }
            }
            else -> eventListener?.invoke(ev)
        }
    }

    private fun scheduleReconnect(delayMs: Long) {
        if (connectionParams == null || disconnectCause != null) return
        reconnectAttempts += 1
        if (reconnectAttempts > MAX_RECONNECT_ATTEMPTS) {
            Log.w(TAG, "giving up after $reconnectAttempts reconnect attempts")
            shutdown(
                DisconnectCause.RECONNECT_FAILED,
                "reconnect failed after $MAX_RECONNECT_ATTEMPTS attempts",
            )
            return
        }
        reconnecting = true
        updateNotification()
        eventListener?.invoke(CoreEvent.Reconnecting)
        reconnectJob?.cancel()
        val thisDelay = delayMs
        reconnectBackoffMs = (reconnectBackoffMs * 2).coerceAtMost(MAX_BACKOFF_MS)
        reconnectJob = serviceScope.launch {
            if (thisDelay > 0) delay(thisDelay)
            doConnect()
        }
    }

    private fun teardownClientOnly() {
        runCatching { client?.disconnect() }
        runCatching { client?.close() }
        audioEngine?.shutdown()
        audioEngine = null
        client = null
    }

    // ---- wake lock ------------------------------------------------------

    private fun acquireWakeLock() {
        if (wakeLock?.isHeld == true) return
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "plnt:voice").apply {
            setReferenceCounted(false)
            acquire()
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
    }

    // ---- notification -----------------------------------------------------

    private fun startForeground() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val contentText = when {
            reconnecting -> "Reconnecting…"
            connectionParams != null -> connectionParams?.bookmark?.label?.ifBlank { "Voice connected" } ?: "Voice connected"
            else -> "Idle"
        }
        val muteAction = NotificationCompat.Action(
            if (inputMuted) android.R.drawable.ic_lock_silent_mode else android.R.drawable.ic_lock_silent_mode_off,
            if (inputMuted) "Unmute" else "Mute",
            servicePendingIntent(ACTION_TOGGLE_MUTE, 1),
        )
        val talkAction = NotificationCompat.Action(
            android.R.drawable.ic_btn_speak_now,
            if (transmitting) "Stop talking" else "Talk",
            servicePendingIntent(ACTION_TOGGLE_TALK, 2),
        )
        val disconnectAction = NotificationCompat.Action(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Disconnect",
            servicePendingIntent(ACTION_DISCONNECT, 3),
        )
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("PLNT")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_sys_speakerphone)
            .setOngoing(connectionParams != null)
            // CATEGORY_CALL + VISIBILITY_PUBLIC put the row (and its actions)
            // on the lock screen with its content intact; setSilent stops the
            // now-DEFAULT-importance channel from buzzing on every mute or
            // talk-state update, which fires several times a second under PTT.
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSilent(true)
            .addAction(muteAction)
            .addAction(talkAction)
            .addAction(disconnectAction)
            .build()
    }

    private fun servicePendingIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, VoiceService::class.java).setAction(action)
        return PendingIntent.getService(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Voice",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            setSound(null, null)
            enableVibration(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannel(channel)
        // Drop the old LOW channel so a user upgrading in place doesn't keep
        // an orphaned "Voice" entry in the app's notification settings.
        manager.deleteNotificationChannel("plnt-voice")
    }

    companion object {
        const val ACTION_TOGGLE_MUTE = "com.plnt.client.action.TOGGLE_MUTE"
        const val ACTION_TOGGLE_TALK = "com.plnt.client.action.TOGGLE_TALK"
        const val ACTION_DISCONNECT = "com.plnt.client.action.DISCONNECT"
    }
}
