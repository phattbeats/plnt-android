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
import com.plnt.client.data.IdentityStore
import com.plnt.client.data.SessionStore
import com.plnt.client.model.AudioDeviceOption
import com.plnt.client.model.Bookmark
import com.plnt.client.model.PttMode
import com.plnt.client.model.ConnectionGeneration
import com.plnt.client.model.ConnectionPhase
import com.plnt.client.model.ServerConnectionStatus
import com.plnt.client.model.VoiceConnectionSnapshot
import com.plnt.client.stream.StreamInfo
import com.plnt.client.stream.StreamViewState
import com.plnt.client.stream.StreamViewerSession
import com.plnt.client.stream.StreamSendState
import com.plnt.client.stream.StreamSenderSession
import org.webrtc.EglBase
import org.webrtc.VideoSink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "plnt.voice"
// Separate tag so a repro capture can be filtered down to just the service's
// lifecycle callbacks (#3283) without the rest of the voice-path chatter.
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
// There is deliberately no MAX_RECONNECT_ATTEMPTS. It used to be 10, which with
// the 1→2→4→8→16→30s backoff ended a live session after roughly three minutes
// offline — a ceiling the user never asked for and could only recover from by
// noticing and reconnecting by hand. Only a Disconnect action ends a session
// now (#3290 item 4); see scheduleReconnect.

/**
 * Mic/output/transmit state as the service holds it. The notification actions
 * and the media-button PTT change these behind the UI's back, so the UI has to
 * be told rather than assume its own optimistic value still holds (#3079:
 * talk state renders from events, never from polling).
 */
data class VoiceState(
    val inputMuted: Boolean,
    val outputMuted: Boolean,
    val transmitting: Boolean,
    val pttMode: PttMode,
    val preferredInputDeviceKey: String?,
    val preferredOutputDeviceKey: String?,
    val availableInputDevices: List<AudioDeviceOption>,
    val availableOutputDevices: List<AudioDeviceOption>,
    /**
     * False while the call is running output-only because the OS refused this
     * app the microphone — a background `microphone` foreground-service start
     * on API 34+, or an `AudioRecord` that would not open. The user can hear
     * everyone and nobody can hear them, so this has to be visible rather than
     * inferred from silence (#3290 items 7 and 8).
     */
    val microphoneActive: Boolean,
)

/**
 * Foreground service that owns the single [AudioEngine] + [CoreClient] for
 * the lifetime of a call (#3077/#3078). This is the piece #3079's
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
    private val clientGeneration = ConnectionGeneration()
    private var connectionStatus = ServerConnectionStatus()
    private var connectionServerName = ""
    private var connectionError: String? = null
    private var cachedChannels: List<com.plnt.client.core.CoreChannel> = emptyList()
    private var cachedClients: List<com.plnt.client.core.CoreClientInfo> = emptyList()
    private var uiOwner: Any? = null
    private var connectionListener: ((VoiceConnectionSnapshot) -> Unit)? = null
    private var eventListener: ((CoreEvent) -> Unit)? = null
    private var stateListener: ((VoiceState) -> Unit)? = null

    // PHA-3289: the one screen-share viewer. Lives here, not in the ViewModel,
    // so a rotation or the Activity being recreated does not drop the stream.
    private val eglBase: EglBase by lazy { EglBase.create() }
    private var streamSession: StreamViewerSession? = null
    private var streamSink: VideoSink? = null
    private var streamStateListener: ((StreamViewState) -> Unit)? = null

    // PHA-3289 send side: our own screen share. Same lifetime rules as the viewer.
    private var sendSession: StreamSenderSession? = null
    private var sendStateListener: ((StreamSendState) -> Unit)? = null

    private data class ConnectionParams(val bookmark: Bookmark, val identityPem: String)
    private var connectionParams: ConnectionParams? = null

    /**
     * Why the connection was torn down, or null while one is live or
     * reconnecting. Replaces the old `userInitiatedDisconnect` boolean, which
     * [onDestroy] also set — so an OS-initiated service kill reached the UI
     * labelled as a user disconnect and diagnosis had nothing to go on
     * (#3283). Set in exactly one place, [shutdown], by the caller that
     * actually knows the cause.
     */
    private var disconnectCause: DisconnectCause? = null
    private var hasConnectedOnce = false
    private var reconnecting = false
    private var reconnectAttempts = 0
    private var reconnectBackoffMs = INITIAL_BACKOFF_MS
    private var reconnectJob: Job? = null

    /**
     * The retry loop is parked until a network shows up, rather than burning
     * attempts against a radio that is down (#3290 item 5). Left by
     * [ConnectivityManager.NetworkCallback.onAvailable], not by a timer.
     */
    private var awaitingNetwork = false

    /**
     * Whether the foreground service we currently hold includes the
     * `microphone` type. False means the OS refused it — see [startForeground].
     */
    private var micForegroundActive = false

    private val sessionStore by lazy { SessionStore(applicationContext) }
    private val identityStore by lazy { IdentityStore(applicationContext) }

    private var ownClientId: Long? = null
    private var lastChannelId: Long? = null

    /** `SystemClock.elapsedRealtime()` of the last successful connect, for [logLifecycle]. */
    private var connectedAtElapsedMs: Long? = null

    private var pttMode: PttMode = PttMode.PUSH_TO_TALK
    private var headsetTriggerArmed = false
    private var inputMuted = false
    private var outputMuted = false
    private var transmitting = false
    // #3282. Held here rather than only on the AudioEngine because the engine is
    // released on a real teardown and while parked waiting for a network — the pin
    // has to survive that without the ViewModel having to re-push it.
    private var preferredInputDeviceKey: String? = null
    private var preferredOutputDeviceKey: String? = null
    private var availableInputDevices: List<AudioDeviceOption> = emptyList()
    private var availableOutputDevices: List<AudioDeviceOption> = emptyList()

    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var mediaSession: MediaSessionCompat

    private val connectivityManager by lazy { getSystemService(ConnectivityManager::class.java) }
    private var currentNetwork: Network? = null
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val previous = currentNetwork
            currentNetwork = network
            if (connectionParams == null || !hasConnectedOnce) return
            if (awaitingNetwork) {
                // This — not a timer — is what drives a reconnect after a loss
                // (#3290 item 5). The backoff resets because the thing that
                // was failing has just changed.
                Log.i(TAG, "network available again ($network) — reconnecting")
                awaitingNetwork = false
                reconnectAttempts = 0
                reconnectBackoffMs = INITIAL_BACKOFF_MS
                scheduleReconnect(0L)
            } else if (previous != null && previous != network) {
                Log.i(TAG, "default network changed ($previous -> $network) — reconnecting")
                scheduleReconnect(0L)
            }
        }

        override fun onLost(network: Network) {
            if (network != currentNetwork) return
            currentNetwork = null
            if (connectionParams == null || !hasConnectedOnce) return
            // Deliberately does not schedule a retry. Every attempt made with
            // the radio down is guaranteed to fail, and under the old finite
            // budget those doomed attempts were what actually ended the
            // session — the give-up fired while the phone was still in the
            // tunnel. Park until onAvailable.
            Log.i(TAG, "default network lost — holding reconnect until one is available")
            enterAwaitingNetwork()
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
        // Logged *before* startForeground(), not after. A refused foreground
        // start on API 34+ takes the process with it, so the old ordering lost
        // this line entirely — and a capture with no `onStartCommand` in it
        // reads exactly like "the service was never restarted", which is the
        // wrong conclusion to hand #3286.
        if (intent == null) {
            logLifecycle("onStartCommand", "null intent — START_STICKY restart after a kill, flags=$flags")
        } else {
            logLifecycle("onStartCommand", "action=${intent.action} flags=$flags")
        }
        startForeground(withMicrophone = true)
        // A start command that came from the notification's own actions is one
        // of the documented exemptions from the API 34 while-in-use rule, so
        // this is a real chance to get the mic back after a degraded restart.
        syncCaptureWithForegroundType()
        when (intent?.action) {
            ACTION_TOGGLE_MUTE -> setInputMuted(!inputMuted)
            ACTION_TOGGLE_TALK -> setPushToTalk(!transmitting)
            ACTION_DISCONNECT -> disconnect()
        }
        if (intent == null) restorePersistedSession()
        return START_STICKY
    }

    /** Connect (or reconnect) using a bookmark + identity. `onEvent` observes app-level events. */
    fun connect(bookmark: Bookmark, identityPem: String) {
        // Establish the foreground state before anything reads it. The UI is
        // already bound when it calls this, so the binder call can land ahead
        // of the `startForegroundService` Intent that triggers onStartCommand —
        // and [ensureAudioEngine] decides whether to open the mic from exactly
        // the flag that sets. Without this a perfectly ordinary foreground
        // connect could come up output-only.
        startForeground(withMicrophone = true)
        clientGeneration.invalidate()
        reconnectJob?.cancel()
        reconnectJob = null
        disconnectCause = null
        hasConnectedOnce = false
        reconnecting = false
        awaitingNetwork = false
        reconnectAttempts = 0
        reconnectBackoffMs = INITIAL_BACKOFF_MS
        lastChannelId = null
        ownClientId = null
        connectionParams = ConnectionParams(bookmark, identityPem)
        connectionStatus = connectionStatus.start(bookmark.id)
        connectionError = null
        cachedChannels = emptyList()
        cachedClients = emptyList()
        publishConnectionSnapshot()
        persistSession()
        doConnect()
    }

    /**
     * The UI came to the foreground. Two things only the foreground makes
     * possible: re-requesting the `microphone` foreground-service type the OS
     * may have refused during a background restart, and, if that works,
     * reopening capture on the engine that has been running output-only.
     */
    fun onAppForegrounded() {
        if (connectionParams == null || micForegroundActive) return
        startForeground(withMicrophone = true)
        syncCaptureWithForegroundType()
    }

    fun currentConnectionSnapshot() = VoiceConnectionSnapshot(
        generation = clientGeneration.current(),
        status = connectionStatus,
        ownClientId = ownClientId,
        serverName = connectionServerName,
        lastError = connectionError,
    )

    fun setConnectionListener(owner: Any, listener: ((VoiceConnectionSnapshot) -> Unit)?) {
        uiOwner = owner
        connectionListener = listener
        listener?.invoke(currentConnectionSnapshot())
    }

    /** A newly bound UI must see the live roster, not start a second client. */
    fun setEventListener(listener: ((CoreEvent) -> Unit)?) {
        eventListener = listener
        if (connectionStatus.phase == ConnectionPhase.CONNECTED || connectionStatus.phase == ConnectionPhase.RECONNECTING) {
            listener?.invoke(CoreEvent.ChannelTree(cachedChannels))
            listener?.invoke(CoreEvent.ClientList(cachedClients))
        }
    }

    fun clearUiListeners(owner: Any) {
        if (uiOwner !== owner) return
        uiOwner = null
        connectionListener = null
        eventListener = null
        stateListener = null
        streamStateListener = null
        sendStateListener = null
    }

    private fun publishConnectionSnapshot() {
        connectionListener?.invoke(currentConnectionSnapshot())
    }

    private fun emitEvent(ev: CoreEvent) {
        when (ev) {
            is CoreEvent.Connected -> {
                connectionStatus = connectionStatus.connected()
                connectionServerName = ev.serverName
                connectionError = null
            }
            is CoreEvent.Resumed -> {
                connectionStatus = connectionStatus.connected()
                connectionServerName = ev.serverName
                connectionError = null
            }
            is CoreEvent.Reconnecting, is CoreEvent.TemporaryDisconnect -> {
                connectionStatus = connectionStatus.reconnecting()
            }
            is CoreEvent.Disconnected -> {
                connectionStatus = connectionStatus.disconnected()
                connectionError = if (ev.cause == DisconnectCause.USER || ev.cause == DisconnectCause.APP_REQUESTED) null else ev.reason
                cachedChannels = emptyList()
                cachedClients = emptyList()
            }
            is CoreEvent.Error -> connectionError = ev.message
            is CoreEvent.ChannelTree -> cachedChannels = ev.channels
            is CoreEvent.ClientList -> cachedClients = ev.clients
            is CoreEvent.ClientMoved -> cachedClients = cachedClients.map {
                if (it.id == ev.clientId) it.copy(channelId = ev.channelId) else it
            }
            else -> Unit
        }
        publishConnectionSnapshot()
        eventListener?.invoke(ev)
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

    fun currentState(): VoiceState = VoiceState(
        inputMuted = inputMuted,
        outputMuted = outputMuted,
        transmitting = transmitting,
        pttMode = pttMode,
        preferredInputDeviceKey = preferredInputDeviceKey,
        preferredOutputDeviceKey = preferredOutputDeviceKey,
        availableInputDevices = availableInputDevices,
        availableOutputDevices = availableOutputDevices,
        // No engine yet (idle, or parked waiting for a network) is not a mic
        // failure — only a running engine that could not get one is.
        microphoneActive = audioEngine?.isCaptureActive() ?: true,
    )

    /** #3282: user-facing mic device override, independent of PTT mode. Null = Automatic. */
    fun setPreferredInputDevice(key: String?) {
        preferredInputDeviceKey = key
        audioEngine?.setPreferredInputDevice(key)
        emitState()
    }

    /** Output half of [setPreferredInputDevice]. */
    fun setPreferredOutputDevice(key: String?) {
        preferredOutputDeviceKey = key
        audioEngine?.setPreferredOutputDevice(key)
        emitState()
    }

    private fun emitState() {
        stateListener?.invoke(currentState())
    }

    fun joinChannel(channelId: Long, password: String?) {
        lastChannelId = channelId
        persistSession()
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

    // ---- PHA-3289: screen-share viewing --------------------------------

    /** Raw TS6 command passthrough — the stream directory's `requeststreaminfo` polls use this. */
    fun sendRawCommand(name: String, args: Map<String, String>) {
        runCatching { client?.sendRawCommand(name, args) }
    }

    fun setStreamStateListener(listener: ((StreamViewState) -> Unit)?) {
        streamStateListener = listener
        listener?.invoke(streamSession?.current ?: StreamViewState())
    }

    /** Shared GL context for the viewer's `SurfaceViewRenderer`. */
    fun eglBaseContext(): EglBase.Context = eglBase.eglBaseContext

    /** Start watching [stream]; any stream already being watched is left first. */
    fun watchStream(stream: StreamInfo) {
        stopWatching()
        val nick = connectionParams?.bookmark?.nickname ?: "PLNT"
        val session = StreamViewerSession(
            context = applicationContext,
            eglBase = eglBase,
            ownNickname = nick,
            sendRaw = { name, args -> sendRawCommand(name, args) },
            onState = { st -> mainHandler.post { streamStateListener?.invoke(st) } },
        )
        streamSession = session
        streamSink?.let(session::attach)
        session.start(stream)
    }

    fun stopWatching() {
        val s = streamSession ?: return
        streamSession = null
        runCatching { s.stop() }
        streamStateListener?.invoke(StreamViewState())
    }

    fun setSendStateListener(listener: ((StreamSendState) -> Unit)?) {
        sendStateListener = listener
        listener?.invoke(sendSession?.current ?: StreamSendState())
    }

    /**
     * Share this phone's screen into the current channel. [permissionData] is
     * the MediaProjection grant from the system "Start recording or casting?"
     * prompt; it must be used once, right away.
     *
     * Android 14+ refuses MediaProjection unless the service is already in
     * the foreground with type `mediaProjection`, so the type is added before
     * capture starts and dropped again in [stopScreenShare].
     */
    fun startScreenShare(permissionData: Intent, title: String) {
        stopScreenShare()
        if (client == null) return
        promoteForProjection(true)
        val dm = resources.displayMetrics
        // Long edge capped at 1280: TS desktop streams default to 720p and a
        // phone's native resolution would just cost bitrate.
        val scale = minOf(1f, 1280f / maxOf(dm.widthPixels, dm.heightPixels))
        val w = ((dm.widthPixels * scale).toInt() / 2) * 2
        val h = ((dm.heightPixels * scale).toInt() / 2) * 2
        val session = StreamSenderSession(
            context = applicationContext,
            eglBase = eglBase,
            permissionData = permissionData,
            ownClientId = { ownClientId },
            sendRaw = { name, args -> sendRawCommand(name, args) },
            onState = { st ->
                mainHandler.post {
                    sendStateListener?.invoke(st)
                    if (st.phase == com.plnt.client.stream.StreamSendPhase.ENDED ||
                        st.phase == com.plnt.client.stream.StreamSendPhase.FAILED
                    ) {
                        if (sendSession != null && sendSession?.current?.phase == st.phase) {
                            sendSession = null
                            promoteForProjection(false)
                        }
                    }
                }
            },
        )
        sendSession = session
        session.start(title, w, h)
    }

    fun stopScreenShare() {
        val s = sendSession ?: return
        sendSession = null
        runCatching { s.stop() }
        promoteForProjection(false)
        sendStateListener?.invoke(StreamSendState())
    }

    /** Adds or drops the `mediaProjection` foreground type on top of the voice types. */
    private fun promoteForProjection(on: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        var types = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        if (micForegroundActive) types = types or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
        if (on) types = types or ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
        runCatching { startForeground(NOTIFICATION_ID, buildNotification(), types) }
            .onFailure { Log.w(TAG, "foreground type update (projection=$on) refused", it) }
    }

    /** The viewer screen's renderer; null when it goes away. Survives across sessions. */
    fun attachStreamSink(sink: VideoSink?) {
        streamSink = sink
        streamSession?.attach(sink)
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

    /** Initial setup failed before the asynchronous native connect was launched. */
    fun failConnectionStart(reason: String) {
        shutdown(DisconnectCause.ERROR, reason)
    }

    /**
     * Teardown that ends the *session*: the user hung up, or a first connect
     * failed in a way retrying cannot fix. Every caller supplies the cause it
     * actually knows, which is the point of #3283 — before that, [onDestroy]
     * and a Disconnect tap both went through one `disconnect()` that stamped
     * `userInitiatedDisconnect = true`, so an OS kill and a user hang-up were
     * indistinguishable by the time they reached the UI.
     *
     * Process teardown is deliberately *not* this: it goes through
     * [teardownForProcessDeath], which leaves the persisted session alone.
     * Clearing it here is what makes the difference meaningful — a session that
     * ends on purpose must not be redialled by the next restart.
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
        awaitingNetwork = false
        connectionParams = null
        hasConnectedOnce = false
        lastChannelId = null
        ownClientId = null
        connectedAtElapsedMs = null
        // The session is over on purpose, so the restart snapshot goes with it.
        // Without this the next OS restart of this service would cheerfully
        // redial a call the user hung up.
        clearPersistedSession()
        teardownClientOnly()
        releaseAudioEngine()
        releaseWakeLock()
        mediaSession.isActive = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        transmitting = false
        emitState()
        if (notify) emitEvent(CoreEvent.Disconnected(cause, reason))
    }

    /**
     * Android is destroying this service instance. Releases everything the
     * process holds — and nothing else.
     *
     * The distinction from [shutdown] is the whole of #3290 item 1.
     * #3283 already established that this is not a user disconnect and
     * labelled it [DisconnectCause.SYSTEM_KILL], but it still ran the user
     * teardown, which nulls `connectionParams` and (now) would wipe the
     * persisted session — erasing precisely the state the `START_STICKY`
     * restart needs. A kill must leave the snapshot on disk so the restarted
     * instance can reconnect on its own, with no user action.
     */
    private fun teardownForProcessDeath() {
        reconnectJob?.cancel()
        reconnecting = false
        awaitingNetwork = false
        teardownClientOnly()
        releaseAudioEngine()
        releaseWakeLock()
        mediaSession.isActive = false
    }

    override fun onDestroy() {
        val wasConnected = connectionParams != null || client != null
        logLifecycle("onDestroy", "wasConnected=$wasConnected sessionSnapshotKept=$wasConnected")
        runCatching { connectivityManager.unregisterNetworkCallback(networkCallback) }
        teardownForProcessDeath()
        if (wasConnected) {
            // Still reported, and still as SYSTEM_KILL (#3283) — a UI that
            // outlives this service instance needs to know the call dropped.
            // What changed is what happens next: the snapshot survives, so the
            // sticky restart redials without the user touching anything.
            emitEvent(
                CoreEvent.Disconnected(DisconnectCause.SYSTEM_KILL, "Android stopped the voice service"),
            )
        }
        mediaSession.release()
        super.onDestroy()
    }

    // ---- session persistence (#3290 item 2) ----------------------------

    /**
     * Snapshot the live session so a restart can find it. Called on connect and
     * on every channel change — the two things that make the snapshot wrong.
     */
    private fun persistSession() {
        val params = connectionParams ?: return
        val channel = lastChannelId
        serviceScope.launch(Dispatchers.IO) {
            runCatching { sessionStore.save(params.bookmark, channel) }
                .onFailure { Log.w(TAG, "could not persist session", it) }
        }
    }

    private fun clearPersistedSession() {
        serviceScope.launch(Dispatchers.IO) {
            runCatching { sessionStore.clear() }
                .onFailure { Log.w(TAG, "could not clear persisted session", it) }
        }
    }

    /**
     * The headless half of #3290: a `START_STICKY` restart arrives with a
     * null Intent, no Activity, no bound ViewModel and no user in front of the
     * phone. Everything needed to redial comes off disk — the bookmark and
     * channel from [SessionStore], the identity from [IdentityStore] — and the
     * reconnect runs through the same [doConnect] a foreground connect uses.
     */
    private fun restorePersistedSession() {
        if (connectionParams != null) {
            logLifecycle("restore", "session already live — nothing to restore")
            return
        }
        val restoreGeneration = clientGeneration.current()
        serviceScope.launch {
            val restored = withContext(Dispatchers.IO) {
                val session = runCatching { sessionStore.load() }.getOrNull()
                // peek(), not loadOrCreate(): an install whose identity is gone
                // must not silently reconnect as a brand-new stranger.
                val pem = session?.let { runCatching { identityStore.peek() }.getOrNull() }
                if (session != null && pem != null) session to pem else null
            }
            if (!clientGeneration.accepts(restoreGeneration) || connectionParams != null) return@launch
            if (restored == null) {
                logLifecycle("restore", "no persisted session — nothing to reconnect to")
                // Don't sit in the foreground holding a call notification for a
                // call that isn't happening. stopSelf() only clears the
                // *started* state; a bound UI keeps the instance alive.
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return@launch
            }
            val (session, pem) = restored
            logLifecycle(
                "restore",
                "headless reconnect to ${session.bookmark.address}:${session.bookmark.port} " +
                    "channel=${session.lastChannelId} mic=$micForegroundActive",
            )
            disconnectCause = null
            // This session had already connected before the kill, so a failure
            // now is not a first-connect failure and must retry rather than
            // surface as a config error and end the session. That carve-out
            // exists for a bookmark the user just typed wrong, which by
            // construction this cannot be.
            hasConnectedOnce = true
            reconnecting = false
            awaitingNetwork = false
            reconnectAttempts = 0
            reconnectBackoffMs = INITIAL_BACKOFF_MS
            lastChannelId = session.lastChannelId
            ownClientId = null
            connectionParams = ConnectionParams(session.bookmark, pem)
            connectionStatus = connectionStatus.start(session.bookmark.id).reconnecting()
            connectionError = null
            publishConnectionSnapshot()
            updateNotification()
            doConnect()
        }
    }

    // ---- lifecycle instrumentation (#3283 item 1) ----------------------

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
     * time-since-connect. #3283 item 1 asks for the service kill to be
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
        ensureAudioEngine()

        val generation = clientGeneration.current()
        var callbackClient: CoreClient? = null
        val c = try { CoreBridge.newClient(
            onEvent = { ev -> mainHandler.post {
                if (clientGeneration.accepts(generation, callbackClient, client)) handleEvent(ev)
            } },
            onPcmFrame = { clientId, samples -> mainHandler.post {
                if (!clientGeneration.accepts(generation, callbackClient, client)) return@post
                if (samples.size == CORE_FRAME_SAMPLES) {
                    // The field, not a captured local: the engine now outlives
                    // any one client, so a captured reference could outlive the
                    // engine it points at instead.
                    audioEngine?.onPlaybackFrame(samples)
                } else {
                    Log.w(TAG, "PcmFrame from client $clientId: ${samples.size} samples, expected $CORE_FRAME_SAMPLES")
                }
            } },
        ) } catch (t: Throwable) {
            if (!clientGeneration.accepts(generation)) return
            Log.e(TAG, "native client setup failed", t)
            if (hasConnectedOnce) scheduleReconnect(reconnectBackoffMs)
            else shutdown(DisconnectCause.ERROR, t.message ?: t.javaClass.simpleName)
            return
        }
        callbackClient = c
        client = c
        publishConnectionSnapshot()
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
                withContext(Dispatchers.Main) {
                    // A failed old attempt must never shut down a newer session.
                    if (!clientGeneration.accepts(generation, c, client)) return@withContext
                    Log.e(TAG, "connect() failed", t)
                    clientGeneration.invalidate()
                    runCatching { c.close() }
                    client = null
                    if (hasConnectedOnce) {
                        scheduleReconnect(reconnectBackoffMs)
                    } else {
                        shutdown(DisconnectCause.ERROR, t.message ?: t.javaClass.simpleName)
                    }
                }
            }
        }
    }

    /**
     * One [AudioEngine] per session, not one per connect attempt (#3290
     * item 8). Rebuilding it on every reconnect meant a fresh
     * `AudioRecord`/`AudioTrack` acquisition — and on Bluetooth a fresh SCO
     * handshake — stacked on top of the network reconnect, every cycle.
     *
     * It is released on a real teardown and when a reconnect goes long (see
     * [enterAwaitingNetwork]): now that the retry loop never gives up, holding
     * an open mic through an unbounded offline wait would keep the OS recording
     * indicator lit and the radio-off battery drain running for nothing.
     */
    private fun ensureAudioEngine(): AudioEngine {
        audioEngine?.let { existing ->
            if (existing.isRunning()) return existing
            existing.shutdown()
        }
        val engine = AudioEngine(
            applicationContext,
            onCaptureFrame = { frame ->
                // frame is 960 samples @ 48 kHz mono, exactly what sendPcmFrame expects.
                try {
                    client?.sendPcmFrame(frame)
                } catch (t: Throwable) {
                    Log.w(TAG, "sendPcmFrame failed", t)
                }
            },
            onAudioDevicesChanged = { inputs, outputs ->
                mainHandler.post {
                    availableInputDevices = inputs
                    availableOutputDevices = outputs
                    emitState()
                }
            },
            onAudioError = { stage, error -> mainHandler.post { reportAudioFailure(stage, error) } },
        )
        engine.setPreferredInputDevice(preferredInputDeviceKey)
        engine.setPreferredOutputDevice(preferredOutputDeviceKey)
        audioEngine = engine
        // Opening capture without the microphone foreground-service type would
        // get us silence at best; the call runs output-only until the mic can
        // be re-requested from the foreground (see [onAppForegrounded]).
        engine.start(withCapture = micForegroundActive)
        return engine
    }

    private fun releaseAudioEngine() {
        audioEngine?.shutdown()
        audioEngine = null
    }

    /**
     * Bring capture into line with the foreground type we actually hold. A
     * `microphone` start refused in the background leaves the call
     * output-only; this is where it upgrades once one is granted.
     */
    private fun syncCaptureWithForegroundType() {
        val engine = audioEngine ?: return
        if (!micForegroundActive || engine.isCaptureActive()) return
        if (engine.enableCapture()) {
            Log.i(TAG, "microphone acquired — upgrading call from output-only")
            logLifecycle("micUpgrade", "capture reopened after a degraded start")
            applySendingGate()
            updateNotification()
            emitState()
        }
    }

    /**
     * An audio stream could not be acquired. Surfaced rather than swallowed:
     * the failure mode this replaces is a call that reads as connected in every
     * UI element while carrying no audio in one direction.
     */
    private fun reportAudioFailure(stage: String, error: Throwable) {
        logLifecycle("audioFailure", "$stage: $error")
        val what = if (stage == "capture") "Microphone" else "Playback"
        emitEvent(
            CoreEvent.Error("$what unavailable: ${error.message ?: error.javaClass.simpleName}"),
        )
        updateNotification()
        emitState()
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
                emitEvent(ev)
            }
            is CoreEvent.ClientMoved -> {
                if (ev.clientId == ownClientId) {
                    lastChannelId = ev.channelId
                    // Re-snapshot: a restart has to come back to the channel the
                    // user is actually in, not the one they joined from.
                    persistSession()
                }
                emitEvent(ev)
            }
            is CoreEvent.Resumed -> {
                // tsclientlib's internal reconnect can hand back a different
                // own_client_id than before the blip (#3277) — re-sync the
                // copy `ClientMoved` above compares against, same as Connected.
                ownClientId = ev.ownClientId
                emitEvent(ev)
            }
            is CoreEvent.RawCommand -> {
                streamSession?.onRaw(ev)
                sendSession?.onRaw(ev)
                emitEvent(ev)
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
            else -> emitEvent(ev)
        }
    }

    /**
     * Back off and try again — indefinitely. There is no attempt ceiling: a
     * session ends when the user ends it, and nothing else (#3290 item 4).
     * The old 10-attempt budget, spent through a capped 1→30 s backoff, ended a
     * live call after roughly three minutes offline; a subway ride or a dead
     * router was enough, and recovery needed the user to notice and reconnect
     * by hand.
     *
     * The first-connect carve-out is unchanged and lives in [doConnect] /
     * [handleEvent]: a bookmark that has never connected still fails loudly and
     * immediately instead of retrying a typo forever.
     */
    private fun scheduleReconnect(delayMs: Long) {
        if (connectionParams == null || disconnectCause != null) return
        if (!hasUsableNetwork()) {
            // Retries are driven by the radio coming back, not by a timer.
            enterAwaitingNetwork()
            return
        }
        clientGeneration.invalidate()
        awaitingNetwork = false
        reconnectAttempts += 1
        reconnecting = true
        Log.i(TAG, "reconnect attempt $reconnectAttempts in ${delayMs}ms")
        updateNotification()
        emitEvent(CoreEvent.Reconnecting)
        reconnectJob?.cancel()
        val thisDelay = delayMs
        reconnectBackoffMs = (reconnectBackoffMs * 2).coerceAtMost(MAX_BACKOFF_MS)
        val scheduledGeneration = clientGeneration.current()
        reconnectJob = serviceScope.launch {
            if (thisDelay > 0) delay(thisDelay)
            if (clientGeneration.accepts(scheduledGeneration) && connectionParams != null) doConnect()
        }
    }

    /**
     * Park the session until a network exists. Not a terminal state — the
     * connection is still the user's, the notification still says so, and
     * [ConnectivityManager.NetworkCallback.onAvailable] resumes it.
     *
     * The wake lock and the audio engine both go, because this wait has no
     * bound: holding a partial wake lock and an open mic through an overnight
     * dead zone would flatten the battery, and neither is needed to receive the
     * connectivity callback that ends the wait.
     */
    private fun enterAwaitingNetwork() {
        val wasAwaiting = awaitingNetwork
        awaitingNetwork = true
        reconnecting = true
        reconnectJob?.cancel()
        teardownClientOnly()
        releaseAudioEngine()
        releaseWakeLock()
        updateNotification()
        emitState()
        if (!wasAwaiting) emitEvent(CoreEvent.Reconnecting)
    }

    /**
     * `currentNetwork` is only populated once `onAvailable` has fired, so fall
     * back to asking directly — a service restarted into an already-connected
     * device must not mistake "no callback yet" for "no network".
     */
    private fun hasUsableNetwork(): Boolean =
        currentNetwork != null || runCatching { connectivityManager.activeNetwork }.getOrNull() != null

    /** Tears down the core client only — the [AudioEngine] deliberately outlives it, see [ensureAudioEngine]. */
    private fun teardownClientOnly() {
        // Invalidate before disconnect/close, including already queued callbacks.
        clientGeneration.invalidate()
        // A stream cannot outlive the connection its signalling rides on.
        stopWatching()
        stopScreenShare()
        runCatching { client?.disconnect() }
        runCatching { client?.close() }
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

    /**
     * Enter the foreground, degrading rather than dying if the OS says no.
     *
     * `microphone` is a *while-in-use* foreground-service type. From Android 14
     * (API 34, and this app targets 35) an app that is in the background when
     * it creates one does not hold the while-in-use grant, and the system
     * throws `SecurityException` from `startForeground()` — which is exactly
     * the position a `START_STICKY` restart is in. Unguarded, as this was, the
     * refusal kills the restarted process outright and the headless reconnect
     * can never happen.
     *
     * So: ask for `microphone|mediaPlayback`, and on refusal fall back to
     * `mediaPlayback` alone, which carries no runtime prerequisite. That
     * downgrade is the documented pattern (declare both types, call
     * `startForeground()` with a subset, call it again later with more) and it
     * turns a fatal refusal into a call that is connected and audible but not
     * transmitting — upgraded to full duplex by [syncCaptureWithForegroundType]
     * the next time a start command arrives from the foreground or from the
     * notification's own actions, both of which are exempt from the rule.
     *
     * Note the battery-optimisation exemption of item 6 does *not* cover this:
     * it exempts the Android 12 background-*start* restriction, and the
     * while-in-use exemption list is a separate, narrower one that does not
     * include it. It is still worth having for the other reason — it is what
     * keeps a doze-restricted process able to reach the network at all — but
     * this fallback, not that exemption, is what makes a mic refusal survivable.
     *
     * @return whether the microphone type was granted.
     */
    private fun startForeground(withMicrophone: Boolean): Boolean {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // No service types to refuse before Q.
            runCatching { startForeground(NOTIFICATION_ID, notification) }
                .onFailure { Log.e(TAG, "startForeground failed", it) }
            micForegroundActive = true
            return true
        }
        if (withMicrophone) {
            val withMic = runCatching {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
                )
            }
            if (withMic.isSuccess) {
                micForegroundActive = true
                return true
            }
            Log.w(TAG, "microphone foreground type refused — degrading to output-only", withMic.exceptionOrNull())
            logLifecycle("startForeground", "microphone type refused: ${withMic.exceptionOrNull()}")
        }
        micForegroundActive = false
        runCatching {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        }.onFailure {
            // Nothing left to fall back to. Logged rather than rethrown: a
            // service that is merely not in the foreground can still hold the
            // connection for a while, and the alternative is a crash that takes
            // the reconnect with it.
            Log.e(TAG, "foreground start refused outright", it)
            logLifecycle("startForeground", "refused outright: $it")
        }
        return false
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val label = connectionParams?.bookmark?.label?.ifBlank { "Voice connected" } ?: "Voice connected"
        val contentText = when {
            // Distinct from "Reconnecting…": nothing is being attempted, and
            // saying so is honest about why it may sit here for a while.
            awaitingNetwork -> "Waiting for network…"
            reconnecting -> "Reconnecting…"
            connectionParams == null -> "Idle"
            // The user is connected but cannot be heard — the one call state
            // that looks fine and isn't.
            audioEngine?.isCaptureActive() == false -> "$label — no microphone"
            else -> label
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
