package com.plnt.client.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.plnt.client.audio.AudioEngine
import com.plnt.client.audio.CORE_FRAME_SAMPLES
import com.plnt.client.core.CoreBridge
import com.plnt.client.core.CoreClient
import com.plnt.client.core.CoreEvent

private const val TAG = "plnt.voice"
private const val NOTIFICATION_CHANNEL_ID = "plnt-voice"
private const val NOTIFICATION_ID = 1

/**
 * Foreground service that owns the single [AudioEngine] and [CoreClient] for
 * the lifetime of one call. This is the "service the AudioEngine class
 * belongs to" per PHA-3077 — mic capture keeps a live foreground process so
 * Android doesn't kill it mid-call, and it survives the Activity going to
 * the background.
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

    private var client: CoreClient? = null
    private var audioEngine: AudioEngine? = null

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        return START_STICKY
    }

    /**
     * Connect to a server and start the audio engine. `onEvent` is the
     * app/UI-level callback (channel tree, talk status, etc — see
     * [CoreEvent]); PCM frames never reach it, they're intercepted here and
     * routed straight to [AudioEngine.onPlaybackFrame].
     */
    fun connect(
        address: String,
        port: Int,
        nickname: String,
        identityPem: String,
        password: String?,
        onEvent: (CoreEvent) -> Unit,
    ) {
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
            onEvent = onEvent,
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
        try {
            c.connect(address, port, nickname, identityPem, password)
        } catch (t: Throwable) {
            Log.e(TAG, "connect() failed", t)
            engine.shutdown()
            audioEngine = null
            client = null
            throw t
        }
    }

    /** Push-to-talk gate — capture keeps running, only forwarding toggles. See AudioEngine.setSending. */
    fun setPushToTalk(pressed: Boolean) {
        audioEngine?.setSending(pressed)
    }

    /** Open-mic mode: always forward captured frames. */
    fun setOpenMic(enabled: Boolean) {
        audioEngine?.setSending(enabled)
    }

    fun disconnect() {
        try {
            client?.disconnect()
            client?.close()
        } catch (t: Throwable) {
            Log.w(TAG, "disconnect() failed", t)
        }
        audioEngine?.shutdown()
        audioEngine = null
        client = null
    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }

    private fun startForeground() {
        val notification: Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("PLNT")
            .setContentText("Voice connected")
            .setSmallIcon(android.R.drawable.stat_sys_speakerphone)
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
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
}
