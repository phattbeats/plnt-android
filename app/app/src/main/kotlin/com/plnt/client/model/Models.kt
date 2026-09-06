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

/**
 * Per-client state as the design models it: independent axes, not one enum.
 * PHA-3076's state table has MIC and SND as two separate tags that can show at
 * once (mic muted *and* output muted), and "away" is a font-style change that
 * coexists with the colour vocabulary rather than replacing it — a single-value
 * enum can't express either, which is what the first cut of this file got wrong.
 *
 * Known core gap: plnt-core's event stream carries neither peer mute state nor
 * idle time, so for anyone but yourself [micMuted]/[outputMuted]/[away] are
 * always false today. Filed against PHA-3075/PHA-3076; the UI renders them
 * correctly the moment core starts emitting them.
 */
data class ClientPresence(
    val talking: Boolean = false,
    val micMuted: Boolean = false,
    val outputMuted: Boolean = false,
    val away: Boolean = false,
)

/**
 * One row under a channel. `name` is a placeholder until plnt-core exposes a
 * nickname / initial-roster event — see the "known limitation" note in
 * PHA-3079's report. Until then this renders as "Client <id>".
 */
data class ClientRow(
    val clientId: Long,
    val name: String,
    val isSelf: Boolean,
    val presence: ClientPresence,
)

data class ChannelNode(
    val id: Long,
    val name: String,
    val hasPassword: Boolean,
    val talkPower: Long,
    val clients: List<ClientRow> = emptyList(),
    val children: List<ChannelNode> = emptyList(),
)

/**
 * One chat line, channel or private. `isDirect` mirrors whether the message
 * was sent/received as a private (client-to-client) message rather than
 * channel chat — server-wide chat is out of scope for v1 (PHA-3281).
 */
data class ChatMessage(
    val id: String,
    val fromClientId: Long,
    val fromName: String,
    val isSelf: Boolean,
    val isDirect: Boolean,
    val text: String,
)

enum class PttMode { PUSH_TO_TALK, OPEN_MIC }

/**
 * User-facing mic/output route preference. AUTO keeps [com.plnt.client.audio.AudioEngine]'s
 * existing priority chain (Bluetooth SCO > wired > USB > built-in); the rest force that
 * chain to skip straight to the named device type, so a user can e.g. keep talking on the
 * phone's own mic while a paired Bluetooth headset stays connected for media.
 */
enum class InputRoute { AUTO, BUILTIN_MIC, WIRED_HEADSET, BLUETOOTH, USB_HEADSET }

data class Settings(
    val pttMode: PttMode = PttMode.PUSH_TO_TALK,
    // PHA-3076 §5: two independent switches, not a radio group — volume button
    // AND headset button can both arm PTT, and the on-screen button is always
    // live regardless of either.
    val pttOnVolumeButton: Boolean = false,
    val pttOnHeadsetButton: Boolean = false,
    val preferredInputRoute: InputRoute = InputRoute.AUTO,
)

sealed class Screen {
    data object Bookmarks : Screen()
    data object Connected : Screen()
    data object Settings : Screen()
    data object Chat : Screen()
}

enum class ConnectionPhase { DISCONNECTED, CONNECTING, CONNECTED, RECONNECTING, ERROR }

data class AppState(
    val screen: Screen = Screen.Bookmarks,
    val bookmarks: List<Bookmark> = emptyList(),
    /** Bookmark ids connected to during this app session — drives the sage row dot. */
    val sessionConnected: Set<String> = emptySet(),
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
    /** Which [InputRoute]s the current hardware actually offers right now (updates as devices plug/unplug). */
    val availableInputRoutes: Set<InputRoute> = setOf(InputRoute.AUTO, InputRoute.BUILTIN_MIC),
    val chatMessages: List<ChatMessage> = emptyList(),
)
