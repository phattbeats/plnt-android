package com.plnt.client.model

import org.junit.Assert.*
import org.junit.Test

class VoiceConnectionLifecycleTest {
    @Test fun queuedOldConnectedCannotMarkNewBookmarkConnected() {
        val gate = ConnectionGeneration()
        val oldClient = Any()
        val oldGeneration = gate.current()
        var ui = ServerConnectionStatus().start("old")
        val queuedConnected = {
            if (gate.accepts(oldGeneration, oldClient, oldClient)) ui = ui.connected()
        }
        gate.invalidate()
        ui = ui.start("new")
        queuedConnected()
        assertEquals("new", ui.pendingBookmarkId)
        assertNull(ui.activeBookmarkId)
        assertFalse(ui.isConnected("new"))
    }

    @Test fun queuedOldErrorCannotShutdownNewAttempt() {
        val gate = ConnectionGeneration()
        val oldClient = Any()
        val oldGeneration = gate.current()
        var status = ServerConnectionStatus().start("old")
        val queuedError = {
            if (gate.accepts(oldGeneration, oldClient, oldClient)) status = status.disconnected()
        }
        gate.invalidate()
        status = status.start("new")
        queuedError()
        assertEquals(ConnectionPhase.CONNECTING, status.phase)
        assertEquals("new", status.pendingBookmarkId)
    }

    @Test fun identityMismatchRejectedEvenWithCurrentGeneration() {
        val gate = ConnectionGeneration()
        val currentClient = Any()
        assertFalse(gate.accepts(gate.current(), Any(), currentClient))
        assertFalse(gate.accepts(gate.current(), null, currentClient))
        assertTrue(gate.accepts(gate.current(), currentClient, currentClient))
    }

    @Test fun disconnectInvalidatesAlreadyQueuedCallbacksAndPcm() {
        val gate = ConnectionGeneration()
        val owner = Any()
        val token = gate.current()
        gate.invalidate()
        assertFalse(gate.accepts(token, owner, owner))
    }

    @Test fun rebindingReplaysAuthenticatedBookmarkNotNewVmPendingSelection() {
        val snapshot = VoiceConnectionSnapshot(
            generation = 12,
            status = ServerConnectionStatus().start("plant").connected(),
            ownClientId = 42,
            serverName = "The Plant",
        )
        val freshVm = AppState(connectionStatus = ServerConnectionStatus().start("wrong"))
        val replayed = freshVm.withVoiceConnectionSnapshot(snapshot, initialReplay = true)
        assertEquals("plant", replayed.connectionStatus.activeBookmarkId)
        assertNull(replayed.connectionStatus.pendingBookmarkId)
        assertEquals(42L, replayed.ownClientId)
        assertEquals("The Plant", replayed.serverName)
        assertEquals(Screen.Connected, replayed.screen)
        assertEquals(ServerTapAction.REOPEN, replayed.connectionStatus.tapAction("plant"))
    }

    @Test fun rebindingPendingConnectionDoesNotDuplicateNativeConnect() {
        val snapshot = VoiceConnectionSnapshot(status = ServerConnectionStatus().start("plant"))
        val replayed = AppState().withVoiceConnectionSnapshot(snapshot, initialReplay = true)
        assertEquals(ConnectionPhase.CONNECTING, replayed.phase)
        assertEquals("plant", replayed.connectionStatus.pendingBookmarkId)
        assertEquals(ServerTapAction.IGNORE, replayed.connectionStatus.tapAction("plant"))
        assertFalse(replayed.connectionStatus.isConnected("plant"))
    }

    @Test fun rebindingReconnectPreservesSelectionWithoutGreen() {
        val snapshot = VoiceConnectionSnapshot(status = ServerConnectionStatus().start("plant").connected().reconnecting())
        val replayed = AppState().withVoiceConnectionSnapshot(snapshot, initialReplay = true)
        assertEquals(ConnectionPhase.RECONNECTING, replayed.phase)
        assertEquals("plant", replayed.connectionStatus.pendingBookmarkId)
        assertFalse(replayed.connectionStatus.isConnected("plant"))
    }

    @Test fun failedConnectionErrorReplayedOnBookmarksWithoutStaleRoster() {
        val snapshot = VoiceConnectionSnapshot(lastError = "server rejected password")
        val oldUi = AppState(connectionStatus = ServerConnectionStatus().start("plant").connected(), ownClientId = 42,
            channelTree = listOf(ChannelNode(1, "Lobby", false, 0)))
        val replayed = oldUi.withVoiceConnectionSnapshot(snapshot, initialReplay = true)
        assertEquals(Screen.Bookmarks, replayed.screen)
        assertEquals("server rejected password", replayed.lastError)
        assertNull(replayed.ownClientId)
        assertTrue(replayed.channelTree.isEmpty())
        assertFalse(replayed.connectionStatus.isConnected("plant"))
    }

    @Test fun snapshotUpdatesDoNotCloseChatWhileConnectionIsUnchanged() {
        val status = ServerConnectionStatus().start("plant").connected()
        val ui = AppState(screen = Screen.Chat, connectionStatus = status)
        assertEquals(Screen.Chat, ui.withVoiceConnectionSnapshot(VoiceConnectionSnapshot(status = status)).screen)
    }

    @Test fun bindFalseNullBinderDiedAndLossCannotQueueOrLeaveGreen() {
        for (reason in listOf("bindService returned false", "null binder", "binding died", "service lost")) {
            val binding = VoiceServiceBindingStatus().failure(reason)
            assertFalse(binding.canQueue)
            assertTrue(binding.failed)
            val ui = AppState(connectionStatus = ServerConnectionStatus().start("plant").connected(), ownClientId = 42,
                channelTree = listOf(ChannelNode(1, "Lobby", false, 0))).withVoiceBindingFailure(reason)
            assertEquals(ConnectionPhase.DISCONNECTED, ui.phase)
            assertNull(ui.ownClientId)
            assertTrue(ui.channelTree.isEmpty())
            assertEquals(reason, ui.lastError)
            assertEquals(Screen.Bookmarks, ui.screen)
            assertFalse(ui.connectionStatus.isConnected("plant"))
        }
    }

    @Test fun initialBindingFailureCancelsConnectingState() {
        val ui = AppState(connectionStatus = ServerConnectionStatus().start("plant"))
            .withVoiceBindingFailure("bindService returned false")
        assertEquals(ConnectionPhase.DISCONNECTED, ui.phase)
        assertNull(ui.connectionStatus.pendingBookmarkId)
        assertFalse(ui.connectionStatus.busy)
    }

    @Test fun aNewSuccessfulBindingClearsFailureAndStopsQueuing() {
        val binding = VoiceServiceBindingStatus().failure("binding died").bound()
        assertEquals(VoiceBindingPhase.BOUND, binding.phase)
        assertFalse(binding.canQueue)
        assertFalse(binding.failed)
        assertNull(binding.error)
    }

    @Test fun oldBindingDispatchRejectedAfterRebindOrVmClear() {
        val gate = ConnectionGeneration()
        val previousBinding = gate.current()
        gate.invalidate()
        assertFalse(gate.accepts(previousBinding))
        assertTrue(gate.accepts(gate.current()))
    }
}
