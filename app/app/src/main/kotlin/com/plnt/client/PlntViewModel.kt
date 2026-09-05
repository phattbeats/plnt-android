package com.plnt.client

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plnt.client.core.CoreBridge
import com.plnt.client.core.CoreClient
import com.plnt.client.core.CoreEvent
import com.plnt.client.model.AppState
import com.plnt.client.model.Bookmark
import com.plnt.client.model.ChannelNode
import com.plnt.client.model.ClientRow
import com.plnt.client.model.ConnectionPhase
import com.plnt.client.model.PresenceKind
import com.plnt.client.model.PttMode
import com.plnt.client.model.PttSource
import com.plnt.client.model.Screen
import com.plnt.client.model.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Owns UI state as a single [StateFlow] so Compose renders every core event on
 * the next frame (collectAsStateWithLifecycle), no polling.
 *
 * Bookmarks/identity are kept in plain SharedPreferences here as a stopgap.
 * PHA-3078 owns the real EncryptedSharedPreferences (identity) + DataStore
 * (bookmarks/settings) and the ForegroundService that should end up owning
 * the [CoreClient] instead of this ViewModel so the connection survives
 * process death / screen-off. Swapping that in is a follow-up once PHA-3078
 * lands; this ViewModel's public surface (connect/disconnect/joinChannel/ptt*)
 * is written so it can delegate to a service binding instead of a local
 * client without changing any Compose call site.
 */
class PlntViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("plnt_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    private var client: CoreClient? = null

    // channelId -> talk-power-flat list of client ids we've observed there.
    private val roster = HashMap<Long, MutableSet<Long>>()
    private val talking = HashMap<Long, Boolean>()
    private val channelsById = HashMap<Long, com.plnt.client.core.CoreChannel>()

    init {
        _state.update { it.copy(bookmarks = loadBookmarks(), identityExport = loadOrCreateIdentity()) }
    }

    private fun loadOrCreateIdentity(): String {
        prefs.getString("identity_pem", null)?.let { return it }
        val created = CoreBridge.createIdentity()
        prefs.edit().putString("identity_pem", created).apply()
        return created
    }

    private fun loadBookmarks(): List<Bookmark> {
        val raw = prefs.getString("bookmarks_json", null) ?: return emptyList()
        return runCatching {
            org.json.JSONArray(raw).let { arr ->
                (0 until arr.length()).map { i ->
                    val o = arr.getJSONObject(i)
                    Bookmark(
                        id = o.getString("id"),
                        label = o.getString("label"),
                        address = o.getString("address"),
                        port = o.getInt("port"),
                        nickname = o.getString("nickname"),
                        serverPassword = o.optString("password", "").ifEmpty { null },
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun saveBookmarks(bookmarks: List<Bookmark>) {
        val arr = org.json.JSONArray()
        bookmarks.forEach { b ->
            arr.put(
                org.json.JSONObject()
                    .put("id", b.id)
                    .put("label", b.label)
                    .put("address", b.address)
                    .put("port", b.port)
                    .put("nickname", b.nickname)
                    .put("password", b.serverPassword ?: "")
            )
        }
        prefs.edit().putString("bookmarks_json", arr.toString()).apply()
    }

    fun navigate(screen: Screen) = _state.update { it.copy(screen = screen) }

    fun addOrUpdateBookmark(bookmark: Bookmark) {
        val next = _state.value.bookmarks.filterNot { it.id == bookmark.id } + bookmark
        _state.update { it.copy(bookmarks = next) }
        saveBookmarks(next)
    }

    fun deleteBookmark(id: String) {
        val next = _state.value.bookmarks.filterNot { it.id == id }
        _state.update { it.copy(bookmarks = next) }
        saveBookmarks(next)
    }

    fun newBookmarkId(): String = UUID.randomUUID().toString()

    fun connect(bookmark: Bookmark) {
        disconnect()
        roster.clear()
        talking.clear()
        channelsById.clear()
        _state.update { it.copy(phase = ConnectionPhase.CONNECTING, lastError = null) }
        val identity = _state.value.identityExport ?: loadOrCreateIdentity()
        val c = CoreBridge.newClient { ev -> viewModelScope.launch { onCoreEvent(ev) } }
        client = c
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                c.connect(bookmark.address, bookmark.port, bookmark.nickname, identity, bookmark.serverPassword)
            } catch (e: Throwable) {
                _state.update {
                    it.copy(phase = ConnectionPhase.ERROR, lastError = e.message ?: e.javaClass.simpleName)
                }
            }
        }
    }

    fun disconnect() {
        client?.let {
            runCatching { it.disconnect() }
            runCatching { it.close() }
        }
        client = null
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
        runCatching { client?.joinChannel(channelId, null) }
    }

    fun setMuted(muted: Boolean) {
        _state.update { it.copy(inputMuted = muted) }
        runCatching { client?.setInputMuted(muted) }
    }

    fun setDeafened(deafened: Boolean) {
        _state.update { it.copy(outputDeafened = deafened, inputMuted = if (deafened) true else it.inputMuted) }
        runCatching { client?.setOutputMuted(deafened) }
    }

    /** Press-and-hold PTT. Gates *sending*, not capture — mirrors PHA-3077's AudioEngine contract. */
    fun pttPress() {
        if (_state.value.settings.pttMode != PttMode.PUSH_TO_TALK) return
        _state.update { it.copy(transmitting = true) }
        runCatching { client?.setInputMuted(false) }
    }

    fun pttRelease() {
        if (_state.value.settings.pttMode != PttMode.PUSH_TO_TALK) return
        _state.update { it.copy(transmitting = false) }
        runCatching { client?.setInputMuted(_state.value.inputMuted) }
    }

    fun setPttMode(mode: PttMode) {
        _state.update { it.copy(settings = it.settings.copy(pttMode = mode)) }
        if (mode == PttMode.OPEN_MIC) {
            _state.update { it.copy(transmitting = true) }
            runCatching { client?.setInputMuted(_state.value.inputMuted) }
        }
    }

    fun setPttSource(source: PttSource) {
        _state.update { it.copy(settings = it.settings.copy(pttSource = source)) }
    }

    fun importIdentity(pem: String): Boolean {
        if (!CoreBridge.importIdentity(pem)) return false
        prefs.edit().putString("identity_pem", pem).apply()
        _state.update { it.copy(identityExport = pem) }
        return true
    }

    private fun onCoreEvent(ev: CoreEvent) {
        when (ev) {
            is CoreEvent.Connected -> _state.update {
                it.copy(
                    phase = ConnectionPhase.CONNECTED,
                    ownClientId = ev.ownClientId,
                    serverName = ev.serverName,
                    screen = Screen.Connected,
                )
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
        disconnect()
        super.onCleared()
    }
}
