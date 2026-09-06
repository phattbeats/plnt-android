package com.plnt.client.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
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
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import com.plnt.client.audio.AudioEngine
import com.plnt.client.audio.CORE_FRAME_SAMPLES
import com.plnt.client.core.CoreBridge
import com.plnt.client.core.CoreClient
import com.plnt.client.core.CoreEvent
import com.plnt.client.model.Bookmark
import com.plnt.client.model.PttMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "plnt.voice"
private const val NOTIFICATION_CHANNEL_ID = "plnt-voice"
private const val NOTIFICATION_ID = 1
private const val INITIAL_BACKOFF_MS = 1_000L
private const val MAX_BACKOFF_MS = 30_000L
private const val MAX_RECONNECT_ATTEMPTS = 10

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

    private data class ConnectionParams(val bookmark: Bookmark, val identityPem: String)
    private var connectionParams: ConnectionParams? = null
    private var userInitiatedDisconnect = false
    private var hasConnectedOnce = false
    private var reconnecting = false
    private var reconnectAttempts = 0
    private var reconnectBackoffMs = INITIAL_BACKOFF_MS
    private var reconnectJob: Job? = null

    private var ownClientId: Long? = null
    private var lastChannelId: Long? = null

    private var pttMode: PttMode = PttMode.PUSH_TO_TALK
    private var inputMuted = false
    private var outputMuted = false
    private var transmitting = false

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
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "PlntVoice").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                    val event = mediaButtonIntent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                        ?: return false
                    val isPttKey = event.keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                        event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    if (!isPttKey || pttMode != PttMode.PUSH_TO_TALK) return false
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
        when (intent?.action) {
            ACTION_TOGGLE_MUTE -> setInputMuted(!inputMuted)
            ACTION_TOGGLE_TALK -> setPushToTalk(!transmitting)
            ACTION_DISCONNECT -> disconnect()
        }
        return START_STICKY
    }

    /** Connect (or reconnect) using a bookmark + identity. `onEvent` observes app-level events. */
    fun connect(bookmark: Bookmark, identityPem: String) {
        userInitiatedDisconnect = false
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

    fun joinChannel(channelId: Long, password: String?) {
        lastChannelId = channelId
        runCatching { client?.joinChannel(channelId, password) }
    }

    fun setInputMuted(muted: Boolean) {
        inputMuted = muted
        runCatching { client?.setInputMuted(muted) }
        applySendingGate()
        updateNotification()
    }

    fun setOutputMuted(muted: Boolean) {
        outputMuted = muted
        runCatching { client?.setOutputMuted(muted) }
    }

    /** Press-and-hold PTT / media-button PTT gate. Capture keeps running, only forwarding toggles. */
    fun setPushToTalk(pressed: Boolean) {
        transmitting = pressed
        applySendingGate()
        updateNotification()
    }

    fun setPttMode(mode: PttMode) {
        pttMode = mode
        transmitting = mode == PttMode.OPEN_MIC
        applySendingGate()
    }

    private fun applySendingGate() {
        val engine = audioEngine ?: return
        engine.setSending(!inputMuted && transmitting)
    }

    fun disconnect() {
        userInitiatedDisconnect = true
        reconnectJob?.cancel()
        reconnecting = false
        connectionParams = null
        hasConnectedOnce = false
        lastChannelId = null
        ownClientId = null
        teardownClientOnly()
        releaseWakeLock()
        mediaSession.isActive = false
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        runCatching { connectivityManager.unregisterNetworkCallback(networkCallback) }
        reconnectJob?.cancel()
        disconnect()
        mediaSession.release()
        super.onDestroy()
    }

    // ---- connection lifecycle ----------------------------------------

    private fun doConnect() {
        val params = connectionParams ?: return
        teardownClientOnly()

        lateinit var engine: AudioEngine
        engine = AudioEngine(applicationContext, onCaptureFrame = { frame ->
            // frame is 960 samples @ 48 kHz mono, exactly what sendPcmFrame expects.
            try {
                client?.sendPcmFrame(frame)
            } catch (t: Throwable) {
                Log.w(TAG, "sendPcmFrame failed", t)
            }
        })
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
                        connectionParams = null
                        eventListener?.invoke(CoreEvent.Disconnected(t.message ?: t.javaClass.simpleName))
                        stopForeground(STOP_FOREGROUND_REMOVE)
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
            is CoreEvent.Disconnected -> {
                if (hasConnectedOnce && connectionParams != null && !userInitiatedDisconnect) {
                    scheduleReconnect(reconnectBackoffMs)
                } else if (!userInitiatedDisconnect) {
                    // First-connect failure delivered async instead of thrown from
                    // doConnect() — clean up the same way disconnect() would.
                    teardownClientOnly()
                    releaseWakeLock()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    eventListener?.invoke(ev)
                }
                // else: a stray event from a client that disconnect() already tore
                // down — swallow it so it can't stomp on a since-started new connect.
            }
            else -> eventListener?.invoke(ev)
        }
    }

    private fun scheduleReconnect(delayMs: Long) {
        if (connectionParams == null || userInitiatedDisconnect) return
        reconnectAttempts += 1
        if (reconnectAttempts > MAX_RECONNECT_ATTEMPTS) {
            Log.w(TAG, "giving up after $reconnectAttempts reconnect attempts")
            connectionParams = null
            reconnecting = false
            teardownClientOnly()
            releaseWakeLock()
            mediaSession.isActive = false
            eventListener?.invoke(CoreEvent.Disconnected("reconnect failed after $MAX_RECONNECT_ATTEMPTS attempts"))
            stopForeground(STOP_FOREGROUND_REMOVE)
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
            NotificationManager.IMPORTANCE_LOW,
        )
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val ACTION_TOGGLE_MUTE = "com.plnt.client.action.TOGGLE_MUTE"
        const val ACTION_TOGGLE_TALK = "com.plnt.client.action.TOGGLE_TALK"
        const val ACTION_DISCONNECT = "com.plnt.client.action.DISCONNECT"
    }
}
