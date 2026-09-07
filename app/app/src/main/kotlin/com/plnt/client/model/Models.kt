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
 * One selectable entry in the Settings audio input/output pickers — a single
 * `AudioDeviceInfo` the hardware currently reports (PHA-3282).
 *
 * [key] is `"<AudioDeviceInfo.type>:<address>"`. The platform's own
 * `AudioDeviceInfo.id` is deliberately not used: it is only documented as
 * unique among one `getDevices()` call, so it churns when a headset drops out
 * and comes back, whereas type+address is what a user means by "my headset"
 * and survives a reconnect.
 *
 * This supersedes PHA-3132's `InputRoute` enum, which could only name a device
 * *category* (phone mic / wired / Bluetooth / USB) and had no output half at
 * all. Selecting a device here still drives the same communication-device
 * routing that enum drove, so the "keep using the phone mic while a Bluetooth
 * headset stays paired" case it existed for still works — just per-device.
 */
data class AudioDeviceOption(val key: String, val label: String)

data class Settings(
    val pttMode: PttMode = PttMode.PUSH_TO_TALK,
    // PHA-3076 §5: two independent switches, not a radio group — volume button
    // AND headset button can both arm PTT, and the on-screen button is always
    // live regardless of either.
    val pttOnVolumeButton: Boolean = false,
    val pttOnHeadsetButton: Boolean = false,
    // null = Automatic, the default. Nothing calls setPreferredDevice() and
    // BluetoothScoRouter's priority chain decides the route on its own, exactly
    // as it did before PHA-3282 (so PHA-3077/PHA-3080's SCO behaviour is intact).
    val preferredInputDeviceKey: String? = null,
    val preferredOutputDeviceKey: String? = null,
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
    /** Input devices the hardware offers right now — refreshed on plug/unplug and on entering Settings. */
    val availableInputDevices: List<AudioDeviceOption> = emptyList(),
    /** Output devices the hardware offers right now. Same refresh rules as [availableInputDevices]. */
    val availableOutputDevices: List<AudioDeviceOption> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    /**
     * False while the call is connected but the OS has not granted the
     * microphone — see `VoiceState.microphoneActive` (PHA-3290). Renders as a
     * muted own row, because that is what everyone else hears.
     */
    val microphoneActive: Boolean = true,
    /** Whether PLNT is exempt from battery optimisation — drives the Settings row (PHA-3290 item 6). */
    val batteryOptimizationExempt: Boolean = false,
    /**
     * Non-null when the Activity should launch the battery-optimisation
     * dialog. A nonce rather than a Boolean so a second request after the user
     * dismissed the first still re-fires the `LaunchedEffect` that consumes it.
     */
    val batteryPromptRequest: String? = null,
)
