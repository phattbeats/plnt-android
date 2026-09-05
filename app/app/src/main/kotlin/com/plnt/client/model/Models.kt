package com.plnt.client.model

/** A saved server (PHA-3076 screen 1: bookmarks list with add/edit). */
data class Bookmark(
    val id: String,
    val label: String,
    val address: String,
    val port: Int,
    val nickname: String,
    val serverPassword: String? = null,
)

enum class PresenceKind { IDLE, TALKING, YOU_TALKING, MUTED, DEAFENED, AWAY }

/**
 * One row under a channel. `name` is a placeholder until plnt-core exposes a
 * nickname / initial-roster event — see the "known limitation" note in
 * PHA-3079's report. Until then this renders as "Client <id>".
 */
data class ClientRow(
    val clientId: Long,
    val name: String,
    val isSelf: Boolean,
    val presence: PresenceKind,
)

data class ChannelNode(
    val id: Long,
    val name: String,
    val hasPassword: Boolean,
    val talkPower: Long,
    val clients: List<ClientRow> = emptyList(),
    val children: List<ChannelNode> = emptyList(),
)

enum class PttMode { PUSH_TO_TALK, OPEN_MIC }
enum class PttSource { TOUCH_ONLY, VOLUME_BUTTON, HEADSET_BUTTON }

data class Settings(
    val pttMode: PttMode = PttMode.PUSH_TO_TALK,
    val pttSource: PttSource = PttSource.TOUCH_ONLY,
)

sealed class Screen {
    data object Bookmarks : Screen()
    data object Connected : Screen()
    data object Settings : Screen()
}

enum class ConnectionPhase { DISCONNECTED, CONNECTING, CONNECTED, ERROR }

data class AppState(
    val screen: Screen = Screen.Bookmarks,
    val bookmarks: List<Bookmark> = emptyList(),
    val phase: ConnectionPhase = ConnectionPhase.DISCONNECTED,
    val serverName: String = "",
    val ownClientId: Long? = null,
    val channelTree: List<ChannelNode> = emptyList(),
    val inputMuted: Boolean = false,
    val outputDeafened: Boolean = false,
    val transmitting: Boolean = false,
    val lastError: String? = null,
    val settings: Settings = Settings(),
    val identityExport: String? = null,
)
