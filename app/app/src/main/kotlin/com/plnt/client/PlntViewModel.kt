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
import com.plnt.client.model.ClientRow
import com.plnt.client.model.ConnectionPhase
import com.plnt.client.model.PresenceKind
import com.plnt.client.model.PttMode
import com.plnt.client.model.PttSource
import com.plnt.client.model.Screen
import com.plnt.client.service.VoiceService
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

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val svc = (binder as VoiceService.LocalBinder).service()
            voiceService = svc
            svc.setEventListener { ev -> viewModelScope.launch { onCoreEvent(ev) } }
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
            _state.update {
                it.copy(
                    bookmarks = dataStore.loadBookmarks(),
                    settings = dataStore.loadSettings(),
                    identityExport = identityStore.loadOrCreate(),
                )
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
        _state.update { it.copy(phase = ConnectionPhase.CONNECTING, lastError = null, channelTree = emptyList()) }
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

    fun setPttSource(source: PttSource) {
        _state.update { it.copy(settings = it.settings.copy(pttSource = source)) }
        viewModelScope.launch { dataStore.saveSettings(_state.value.settings) }
    }

    fun importIdentity(pem: String): Boolean {
        if (!identityStore.import(pem)) return false
        _state.update { it.copy(identityExport = pem) }
        return true
    }

    private fun onCoreEvent(ev: CoreEvent) {
        when (ev) {
            is CoreEvent.Connected -> {
                roster.clear()
                talking.clear()
                channelsById.clear()
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
            is CoreEvent.ChannelTree -> {
                ev.channels.forEach { channelsById[it.id] = it }
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
        fun rowFor(clientId: Long): ClientRow {
            val isSelf = clientId == ownId
            val presence = when {
                isSelf && _state.value.inputMuted -> PresenceKind.MUTED
                isSelf && _state.value.outputDeafened -> PresenceKind.DEAFENED
                isSelf && _state.value.transmitting -> PresenceKind.YOU_TALKING
                talking[clientId] == true -> PresenceKind.TALKING
                else -> PresenceKind.IDLE
            }
            // No nickname/initial-roster event exists yet on plnt-core's EventSink
            // (only client_id is carried by ClientMoved/TalkStatus) — see the
            // "known limitation" note filed against PHA-3075/PHA-3076.
            return ClientRow(clientId = clientId, name = "Client $clientId", isSelf = isSelf, presence = presence)
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
