package com.plnt.client.model

enum class ConnectionPhase { DISCONNECTED, CONNECTING, CONNECTED, RECONNECTING, ERROR }

enum class ServerTapAction { CONNECT, REOPEN, IGNORE }

/** Bookmark ownership follows native events, never the user's attempted connect. */
data class ServerConnectionStatus(
    val phase: ConnectionPhase = ConnectionPhase.DISCONNECTED,
    val pendingBookmarkId: String? = null,
    val activeBookmarkId: String? = null,
) {
    val busy: Boolean get() = phase == ConnectionPhase.CONNECTING || phase == ConnectionPhase.RECONNECTING

    fun tapAction(bookmarkId: String): ServerTapAction = when {
        busy -> ServerTapAction.IGNORE
        phase == ConnectionPhase.CONNECTED && activeBookmarkId == bookmarkId -> ServerTapAction.REOPEN
        else -> ServerTapAction.CONNECT
    }

    fun start(bookmarkId: String) = ServerConnectionStatus(ConnectionPhase.CONNECTING, bookmarkId)

    /** Called only after the core has accepted the server's initial state. */
    fun connected() = ServerConnectionStatus(
        phase = ConnectionPhase.CONNECTED,
        activeBookmarkId = pendingBookmarkId ?: activeBookmarkId,
    )

    fun reconnecting() = ServerConnectionStatus(
        phase = ConnectionPhase.RECONNECTING,
        pendingBookmarkId = pendingBookmarkId ?: activeBookmarkId,
    )

    fun disconnected() = ServerConnectionStatus()

    fun isConnected(bookmarkId: String) = phase == ConnectionPhase.CONNECTED && activeBookmarkId == bookmarkId
    fun isPending(bookmarkId: String) = busy && pendingBookmarkId == bookmarkId
}
