package com.plnt.client.audio

import android.media.AudioManager
import org.junit.Assert.assertEquals
import org.junit.Test

class AudioFocusPolicyTest {
    @Test fun transientLossPausesOnce() {
        assertEquals(FocusAction.PAUSE, focusActionFor(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, false))
        assertEquals(FocusAction.NONE, focusActionFor(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, true))
    }

    @Test fun permanentLossPausesAndReacquires() {
        assertEquals(FocusAction.PAUSE_AND_REACQUIRE, focusActionFor(AudioManager.AUDIOFOCUS_LOSS, false))
    }

    @Test fun gainResumesOnlyWhenPaused() {
        assertEquals(FocusAction.RESUME, focusActionFor(AudioManager.AUDIOFOCUS_GAIN, true))
        assertEquals(FocusAction.NONE, focusActionFor(AudioManager.AUDIOFOCUS_GAIN, false))
    }

    @Test fun duckIsIgnored() {
        assertEquals(FocusAction.NONE, focusActionFor(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK, false))
    }
}
