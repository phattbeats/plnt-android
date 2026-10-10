package com.plnt.client.model

import org.junit.Assert.*
import org.junit.Test

class ServerConnectionStatusTest {
    @Test fun attemptIsPendingNeverGreen() {
        val s = ServerConnectionStatus().start("plant")
        assertEquals(ConnectionPhase.CONNECTING, s.phase)
        assertEquals("plant", s.pendingBookmarkId)
        assertNull(s.activeBookmarkId)
        assertTrue(s.isPending("plant"))
        assertFalse(s.isConnected("plant"))
    }

    @Test fun duplicateAndOtherServerTapsIgnoredWhilePending() {
        val s = ServerConnectionStatus().start("plant")
        assertEquals(ServerTapAction.IGNORE, s.tapAction("plant"))
        assertEquals(ServerTapAction.IGNORE, s.tapAction("other"))
    }

    @Test fun authenticatedEventAloneMakesSelectedServerGreen() {
        val s = ServerConnectionStatus().start("plant").connected()
        assertEquals(ConnectionPhase.CONNECTED, s.phase)
        assertEquals("plant", s.activeBookmarkId)
        assertNull(s.pendingBookmarkId)
        assertTrue(s.isConnected("plant"))
        assertFalse(s.isConnected("other"))
    }

    @Test fun failedAttemptClearsPendingAndAllowsRetry() {
        val s = ServerConnectionStatus().start("plant").disconnected()
        assertEquals(ConnectionPhase.DISCONNECTED, s.phase)
        assertNull(s.pendingBookmarkId)
        assertNull(s.activeBookmarkId)
        assertFalse(s.isConnected("plant"))
        assertEquals(ServerTapAction.CONNECT, s.tapAction("plant"))
    }

    @Test fun disconnectClearsPreviouslyAuthenticatedBadge() {
        val s = ServerConnectionStatus().start("plant").connected().disconnected()
        assertFalse(s.isConnected("plant"))
        assertNull(s.activeBookmarkId)
    }

    @Test fun activeCardReopensInsteadOfConnectingAgain() {
        val s = ServerConnectionStatus().start("plant").connected()
        assertEquals(ServerTapAction.REOPEN, s.tapAction("plant"))
        assertEquals(ServerTapAction.CONNECT, s.tapAction("other"))
    }

    @Test fun switchingServerClearsOldBadgeBeforeNewAuthentication() {
        val s = ServerConnectionStatus().start("plant").connected().start("other")
        assertFalse(s.isConnected("plant"))
        assertFalse(s.isConnected("other"))
        assertTrue(s.isPending("other"))
        assertTrue(s.connected().isConnected("other"))
    }

    @Test fun reconnectIsNotGreenAndResumeRestoresOwnership() {
        val s = ServerConnectionStatus().start("plant").connected().reconnecting()
        assertEquals(ConnectionPhase.RECONNECTING, s.phase)
        assertFalse(s.isConnected("plant"))
        assertTrue(s.isPending("plant"))
        assertEquals(ServerTapAction.IGNORE, s.tapAction("plant"))
        assertTrue(s.connected().isConnected("plant"))
    }

    @Test fun unattributedRestoredSessionNeverInventsBookmarkOwnership() {
        val s = ServerConnectionStatus().connected()
        assertEquals(ConnectionPhase.CONNECTED, s.phase)
        assertFalse(s.isConnected("plant"))
        assertNull(s.activeBookmarkId)
    }
}
