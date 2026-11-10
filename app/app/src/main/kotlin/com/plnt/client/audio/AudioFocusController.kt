package com.plnt.client.audio

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log

/** What the engine should do in response to an audio-focus change. */
internal enum class FocusAction { NONE, PAUSE, PAUSE_AND_REACQUIRE, RESUME }

/**
 * Pure mapping from an [AudioManager] focus-change code to an action, kept
 * free of Android objects so it is unit-testable.
 *
 * A phone call (or another app's alarm / navigation prompt) takes focus with a
 * transient loss; we go quiet for its duration and resume on regain.
 * `CAN_DUCK` is ignored: ducking a voice call would make the other party
 * inaudible, and the OS lowers us itself when it matters.
 * A permanent loss never gets a regain callback, so the caller must ask again.
 */
internal fun focusActionFor(change: Int, currentlyPaused: Boolean): FocusAction = when (change) {
    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (currentlyPaused) FocusAction.NONE else FocusAction.PAUSE
    AudioManager.AUDIOFOCUS_LOSS -> FocusAction.PAUSE_AND_REACQUIRE
    AudioManager.AUDIOFOCUS_GAIN -> if (currentlyPaused) FocusAction.RESUME else FocusAction.NONE
    else -> FocusAction.NONE
}

/**
 * Holds voice-communication audio focus for the life of a call and reports
 * interruptions (phone call, other app) via [onPause] / [onResume].
 */
internal class AudioFocusController(
    private val audioManager: AudioManager,
    private val onPause: (reason: String) -> Unit,
    private val onResume: () -> Unit,
    private val handler: Handler = Handler(Looper.getMainLooper()),
) {
    @Volatile private var paused = false
    @Volatile private var held = false
    private var request: AudioFocusRequest? = null

    private val listener = AudioManager.OnAudioFocusChangeListener { change ->
        when (focusActionFor(change, paused)) {
            FocusAction.PAUSE -> { paused = true; onPause("focus change $change") }
            FocusAction.PAUSE_AND_REACQUIRE -> {
                paused = true
                onPause("focus lost")
                // No regain callback follows a permanent loss; ask again shortly.
                handler.postDelayed({ if (held) acquire() }, REACQUIRE_DELAY_MS)
            }
            FocusAction.RESUME -> { paused = false; onResume() }
            FocusAction.NONE -> Unit
        }
    }

    fun acquire(): Boolean {
        val req = request ?: AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
            )
            .setOnAudioFocusChangeListener(listener, handler)
            .setAcceptsDelayedFocusGain(true)
            .build()
            .also { request = it }
        held = true
        val result = audioManager.requestAudioFocus(req)
        Log.i(TAG, "audio focus request result=$result")
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED && paused) {
            paused = false
            onResume()
        }
        return result != AudioManager.AUDIOFOCUS_REQUEST_FAILED
    }

    fun release() {
        held = false
        handler.removeCallbacksAndMessages(null)
        request?.let { audioManager.abandonAudioFocusRequest(it) }
        request = null
        paused = false
    }

    private companion object {
        const val TAG = "AudioFocus"
        const val REACQUIRE_DELAY_MS = 3_000L
    }
}
