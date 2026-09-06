package com.plnt.client

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plnt.client.core.CoreEvent
import com.plnt.client.data.IdentityStore
import com.plnt.client.data.PlntDataStore
import com.plnt.client.model.AppState
import com.plnt.client.model.Bookmark
import com.plnt.client.model.ChannelNode
import com.plnt.client.model.ClientPresence
import com.plnt.client.model.ClientRow
import com.plnt.client.model.ConnectionPhase
import com.plnt.client.model.PttMode
import com.plnt.client.model.Screen
import com.plnt.client.service.VoiceService
import com.plnt.client.service.VoiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Owns UI state as a single [StateFlow] so Compose renders every core event on
 * the next frame (collectAsStateWithLifecycle), no polling.
 *
 * PHA-3078: the [com.plnt.client.core.CoreClient] this app talks to lives in
 * [VoiceService], not here — this ViewModel binds to it and forwards UI
 * intents (connect/disconnect/joinChannel/ptt*) rather than owning a client
 * of its own. PHA-3079's version of this file created its own `CoreClient`
 * directly, which meant `VoiceService`'s `AudioEngine` was never in the loop
 * and no captured audio ever reached plnt-core; routing everything through
 * the bound service both fixes that and is what lets the connection survive
 * this ViewModel being cleared (app backgrounded hard enough to drop the
 * Activity) as long as the foreground service is still alive.
 *
 * Identity moved to [IdentityStore] (EncryptedSharedPreferences); bookmarks
 * and PTT settings to [PlntDataStore] (Preferences DataStore).
 */
class PlntViewModel(app: Application) : AndroidViewModel(app) {
    private val identityStore = IdentityStore(app)
    private val dataStore = PlntDataStore(app)

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    private var voiceService: VoiceService? = null
    private val pendingActions = mutableListOf<(VoiceService) -> Unit>()

    // channelId -> talk-power-flat list of client ids we've observed there.
    private val roster = HashMap<Long, MutableSet<Long>>()
    private val talking = HashMap<Long, Boolean>()
    private val channelsById = HashMap<Long, com.plnt.client.core.CoreChannel>()
    // clientId -> last roster snapshot entry, for nicknames and peer mute state.
    private val clientsById = HashMap<Long, com.plnt.client.core.CoreClientInfo>()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val svc = (binder as VoiceService.LocalBinder).service()
            voiceService = svc
            svc.setEventListener { ev -> viewModelScope.launch { onCoreEvent(ev) } }
            // Mute/PTT can be changed from the notification, the lock screen or a
            // headset button while this UI isn't even on screen — take the
            // service's word for it rather than trusting our own last write.
            svc.setStateListener { st -> viewModelScope.launch { onVoiceState(st) } }
            val queued = pendingActions.toList()
            pendingActions.clear()
            queued.forEach { it(svc) }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            voiceService = null
        }
    }

    init {
        app.bindService(Intent(app, VoiceService::class.java), connection, Context.BIND_AUTO_CREATE)
        viewModelScope.launch {
            val settings = dataStore.loadSettings()
            _state.update {
                it.copy(
                    bookmarks = dataStore.loadBookmarks(),
                    settings = settings,
                    identityExport = identityStore.loadOrCreate(),
                )
            }
            // The service starts on its own default (push-to-talk); hand it the
            // persisted mode or an open-mic install stays silent until the user
            // toggles the setting again.
            runOnService {
                it.setPttMode(settings.pttMode)
                it.setHeadsetTriggerArmed(settings.pttOnHeadsetButton)
            }
        }
    }

    private fun runOnService(action: (VoiceService) -> Unit) {
        voiceService?.let(action) ?: pendingActions.add(action)
    }

    fun navigate(screen: Screen) = _state.update { it.copy(screen = screen) }

    fun addOrUpdateBookmark(bookmark: Bookmark) {
        val next = _state.value.bookmarks.filterNot { it.id == bookmark.id } + bookmark
        _state.update { it.copy(bookmarks = next) }
        viewModelScope.launch { dataStore.saveBookmarks(next) }
    }

    fun deleteBookmark(id: String) {
        val next = _state.value.bookmarks.filterNot { it.id == id }
        _state.update { it.copy(bookmarks = next) }
        viewModelScope.launch { dataStore.saveBookmarks(next) }
    }

    fun newBookmarkId(): String = UUID.randomUUID().toString()

    fun connect(bookmark: Bookmark) {
        roster.clear()
        talking.clear()
        channelsById.clear()
        clientsById.clear()
        _state.update {
            it.copy(
                phase = ConnectionPhase.CONNECTING,
                lastError = null,
                channelTree = emptyList(),
                sessionConnected = it.sessionConnected + bookmark.id,
            )
        }
        val identity = _state.value.identityExport ?: identityStore.loadOrCreate()
        val app = getApplication<Application>()
        ContextCompat.startForegroundService(app, Intent(app, VoiceService::class.java))
        runOnService { it.connect(bookmark, identity) }
    }

    fun disconnect() {
        runOnService { it.disconnect() }
        _state.update {
            it.copy(
                phase = ConnectionPhase.DISCONNECTED,
                channelTree = emptyList(),
                screen = Screen.Bookmarks,
                ownClientId = null,
            )
        }
    }

    fun joinChannel(channelId: Long) {
        runOnService { it.joinChannel(channelId, null) }
    }

    fun setMuted(muted: Boolean) {
        _state.update { it.copy(inputMuted = muted) }
        runOnService { it.setInputMuted(muted) }
    }

    fun setDeafened(deafened: Boolean) {
        _state.update { it.copy(outputDeafened = deafened, inputMuted = if (deafened) true else it.inputMuted) }
        runOnService { it.setOutputMuted(deafened) }
        if (deafened) runOnService { it.setInputMuted(true) }
    }

    /** Press-and-hold PTT. Gates *sending*, not capture — mirrors PHA-3077's AudioEngine contract. */
    fun pttPress() {
        if (_state.value.settings.pttMode != PttMode.PUSH_TO_TALK) return
        _state.update { it.copy(transmitting = true) }
        runOnService { it.setPushToTalk(true) }
    }

    fun pttRelease() {
        if (_state.value.settings.pttMode != PttMode.PUSH_TO_TALK) return
        _state.update { it.copy(transmitting = false) }
        runOnService { it.setPushToTalk(false) }
    }

    fun setPttMode(mode: PttMode) {
        _state.update { it.copy(settings = it.settings.copy(pttMode = mode)) }
        viewModelScope.launch { dataStore.saveSettings(_state.value.settings) }
        runOnService { it.setPttMode(mode) }
        _state.update { it.copy(transmitting = mode == PttMode.OPEN_MIC) }
    }

    fun setPttOnVolumeButton(enabled: Boolean) {
        _state.update { it.copy(settings = it.settings.copy(pttOnVolumeButton = enabled)) }
        viewModelScope.launch { dataStore.saveSettings(_state.value.settings) }
    }

    fun setPttOnHeadsetButton(enabled: Boolean) {
        _state.update { it.copy(settings = it.settings.copy(pttOnHeadsetButton = enabled)) }
        viewModelScope.launch { dataStore.saveSettings(_state.value.settings) }
        runOnService { it.setHeadsetTriggerArmed(enabled) }
    }

    fun importIdentity(pem: String): Boolean {
        if (!identityStore.import(pem)) return false
        _state.update { it.copy(identityExport = pem) }
        return true
    }

    /** Service is the source of truth for mic/output/transmit — mirror it verbatim. */
    private fun onVoiceState(st: VoiceState) {
        _state.update {
            it.copy(
                inputMuted = st.inputMuted,
                outputDeafened = st.outputMuted,
                transmitting = st.transmitting,
            )
        }
        // Own row's MIC/SND tags and talk ring come out of the same state.
        rebuildTree()
    }

    private fun onCoreEvent(ev: CoreEvent) {
        when (ev) {
            is CoreEvent.Connected -> {
                roster.clear()
                talking.clear()
                channelsById.clear()
                clientsById.clear()
                _state.update {
                    it.copy(
                        phase = ConnectionPhase.CONNECTED,
                        ownClientId = ev.ownClientId,
                        serverName = ev.serverName,
                        screen = Screen.Connected,
                    )
                }
            }
            is CoreEvent.Reconnecting -> _state.update {
                it.copy(phase = ConnectionPhase.RECONNECTING, lastError = null)
            }
            is CoreEvent.Disconnected -> _state.update {
                it.copy(phase = ConnectionPhase.DISCONNECTED, lastError = ev.reason, screen = Screen.Bookmarks)
            }
            is CoreEvent.Error -> _state.update { it.copy(lastError = ev.message) }
            is CoreEvent.TemporaryDisconnect -> _state.update {
                it.copy(lastError = "temp disconnect: ${ev.reason}")
            }
            is CoreEvent.Resumed -> {
                _state.update { it.copy(lastError = null, ownClientId = ev.ownClientId, serverName = ev.serverName) }
                // Roster rows already reflect the resumed session (ClientList
                // ticks kept flowing all along) — only isSelf needs redoing
                // now that ownClientId has moved.
                rebuildTree()
            }
            is CoreEvent.ChannelTree -> {
                // Snapshot, not a delta — same as ClientList below. Merging it
                // left deleted channels on screen forever, so a server that had
                // dropped a temporary channel and made a permanent one with the
                // same name rendered both.
                channelsById.clear()
                ev.channels.forEach { channelsById[it.id] = it }
                rebuildTree()
            }
            is CoreEvent.ClientList -> {
                // Whole-roster snapshot: rebuild both maps rather than merging,
                // so a client that left actually disappears from the tree.
                clientsById.clear()
                roster.clear()
                ev.clients.forEach { c ->
                    clientsById[c.id] = c
                    roster.getOrPut(c.channelId) { mutableSetOf() }.add(c.id)
                }
                rebuildTree()
            }
            is CoreEvent.ClientMoved -> {
                roster.values.forEach { it.remove(ev.clientId) }
                roster.getOrPut(ev.channelId) { mutableSetOf() }.add(ev.clientId)
                rebuildTree()
            }
            is CoreEvent.TalkStatus -> {
                talking[ev.clientId] = ev.talking
                rebuildTree()
            }
        }
    }

    private fun rebuildTree() {
        val ownId = _state.value.ownClientId
        val self = _state.value
        fun rowFor(clientId: Long): ClientRow {
            val isSelf = clientId == ownId
            val info = clientsById[clientId]
            // Independent axes, per PHA-3076's state table: mic-muted and
            // output-muted can both be true, and either can coexist with
            // talking. For our own row the local toggles win — they apply the
            // instant they are tapped, before the server echoes them back.
            val presence = ClientPresence(
                talking = if (isSelf) self.transmitting && !self.inputMuted else talking[clientId] == true,
                micMuted = if (isSelf) self.inputMuted else info?.inputMuted == true,
                outputMuted = if (isSelf) self.outputDeafened else info?.outputMuted == true,
                away = info?.away == true,
            )
            return ClientRow(
                clientId = clientId,
                // Fall back to the id only for a client we learned about from a
                // bare ClientMoved before its roster snapshot landed.
                name = info?.name ?: "Client $clientId",
                isSelf = isSelf,
                presence = presence,
            )
        }

        val byParent = channelsById.values.groupBy { it.parentId }
        fun build(parentId: Long?): List<ChannelNode> =
            byParent[parentId].orEmpty().sortedBy { it.name }.map { ch ->
                ChannelNode(
                    id = ch.id,
                    name = ch.name,
                    hasPassword = ch.hasPassword,
                    talkPower = ch.talkPower,
                    clients = roster[ch.id].orEmpty().sorted().map(::rowFor),
                    children = build(ch.id),
                )
            }

        _state.update { it.copy(channelTree = build(null)) }
    }

    override fun onCleared() {
        // Deliberately does NOT disconnect — the whole point of PHA-3078 is
        // that the call outlives this ViewModel. Only drop the binding.
        runCatching { getApplication<Application>().unbindService(connection) }
        super.onCleared()
    }
}
