import Foundation
import Network
import SwiftUI

/// Holds the client, the core connection and every piece of state the UI
/// renders. The Android app splits this across `PlntViewModel` (state) and
/// `VoiceService` (the thing that owns the connection); iOS has no foreground
/// service, so the model owns both — the audio session's `audio` background
/// mode is what keeps the process alive while a call is up.
@MainActor
final class AppModel: ObservableObject {
    // MARK: - Published state

    @Published var phase: ConnectionPhase = .disconnected
    @Published var bookmarks: [Bookmark] = []
    @Published var serverName: String = ""
    @Published var ownClientId: UInt64?
    @Published var tree: [ChannelNode] = []
    @Published var inputMuted = false
    @Published var outputDeafened = false
    @Published var transmitting = false
    @Published var lastError: String?
    @Published var banner: String?
    @Published var chatMessages: [ChatMessage] = []
    @Published var pttMode: PttMode = .pushToTalk
    @Published var coreVersionString: String = "…"
    @Published var microphoneActive = true
    @Published var connectingBookmarkLabel: String = ""

    /// Not actor-isolated: the audio engine is touched from the Rust callback
    /// thread and from the capture queue, not just the main actor.
    nonisolated let audio = AudioEngine()
    private let holder = CoreHolder()

    // MARK: - Private state

    private var core: Client?
    private var sink: CoreEventSink?
    private var currentBookmark: Bookmark?
    private var lastChannelId: UInt64?
    private var channels: [Channel] = []
    private var clientsById: [UInt64: ClientInfo] = [:]
    private var talking: Set<UInt64> = []
    private var reconnectAttempts = 0
    private var reconnectWorkItem: DispatchWorkItem?
    private let pathMonitor = NWPathMonitor()
    private let pathQueue = DispatchQueue(label: "tech.phatt.chlorophone.network")
    private var networkAvailable = true

    init() {
        bookmarks = BookmarkStore.load()
        pttMode = SettingsStore.pttMode
        coreVersionString = coreVersion()
        audio.onCaptureFrame = { [holder] frame in
            holder.send(frame: frame)
        }
        audio.onError = { [weak self] stage in
            Task { @MainActor in
                self?.lastError = "Audio problem (\(stage)) — the call may be silent."
            }
        }
        startNetworkMonitor()
    }

    // MARK: - Bookmarks

    func save(bookmark: Bookmark) {
        if let index = bookmarks.firstIndex(where: { $0.id == bookmark.id }) {
            bookmarks[index] = bookmark
        } else {
            bookmarks.append(bookmark)
        }
        BookmarkStore.save(bookmarks)
    }

    func delete(bookmark: Bookmark) {
        bookmarks.removeAll { $0.id == bookmark.id }
        BookmarkStore.save(bookmarks)
    }

    // MARK: - Connection

    func connect(to bookmark: Bookmark) {
        guard phase == .disconnected else { return }
        currentBookmark = bookmark
        connectingBookmarkLabel = bookmark.label
        phase = .connecting
        lastError = nil
        banner = nil
        chatMessages.removeAll()

        AudioEngine.requestMicrophonePermission { [weak self] granted in
            Task { @MainActor in
                guard let self else { return }
                self.microphoneActive = granted
                if !granted {
                    self.lastError = "Microphone access is off. Enable it in Settings to talk."
                }
                self.startCall(bookmark)
            }
        }
    }

    private func startCall(_ bookmark: Bookmark) {
        do {
            try audio.start()
        } catch {
            lastError = "Audio could not start: \(error.localizedDescription)"
        }

        let sink = CoreEventSink { [weak self] ev in
            if case let .pcmFrame(_, samples) = ev {
                // Playback is latency-critical: hand the frame straight to the
                // audio engine instead of bouncing through the main actor.
                self?.audio.enqueuePlayback(samples)
                return
            }
            Task { @MainActor in
                self?.handle(ev)
            }
        }
        self.sink = sink

        let client = Client(sink: sink)
        core = client
        holder.set(client)

        let identity = IdentityStore.loadOrCreate()
        let address = bookmark.address
        let port = UInt16(clamping: bookmark.port)
        let nickname = bookmark.nickname
        let password = bookmark.serverPassword

        // `connect` blocks until the server has sent its first BookEvents
        // batch, so it cannot run on the main thread.
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            do {
                try client.connect(
                    address: address,
                    port: port,
                    nickname: nickname,
                    identityPem: identity,
                    password: password
                )
                Task { @MainActor in
                    self?.phase = .connected
                    self?.reconnectAttempts = 0
                }
            } catch {
                Task { @MainActor in
                    self?.failConnect(error)
                }
            }
        }
    }

    private func failConnect(_ error: Error) {
        phase = .disconnected
        lastError = describe(error)
        teardownConnection()
    }

    func disconnect() {
        reconnectWorkItem?.cancel()
        reconnectWorkItem = nil
        currentBookmark = nil
        lastChannelId = nil
        if let core {
            try? core.disconnect()
        }
        teardownConnection()
        phase = .disconnected
    }

    private func teardownConnection() {
        holder.set(nil)
        core = nil
        sink = nil
        audio.setSending(false)
        transmitting = false
        audio.stop()
        channels.removeAll()
        clientsById.removeAll()
        talking.removeAll()
        ownClientId = nil
        serverName = ""
        tree = []
        inputMuted = false
        outputDeafened = false
    }

    // MARK: - In-call actions

    func joinChannel(_ channelId: UInt64) {
        lastChannelId = channelId
        do {
            try core?.joinChannel(channelId: channelId, password: nil)
        } catch {
            lastError = describe(error)
        }
    }

    func setMuted(_ muted: Bool) {
        inputMuted = muted
        do {
            try core?.setInputMuted(muted: muted)
        } catch {
            lastError = describe(error)
        }
    }

    func setDeafened(_ deafened: Bool) {
        outputDeafened = deafened
        do {
            try core?.setOutputMuted(muted: deafened)
        } catch {
            lastError = describe(error)
        }
    }

    /// Push-to-talk press/release. In open-mic mode the mic stays open, so the
    /// only thing this does is toggle the mute state.
    func setTransmitting(_ value: Bool) {
        guard pttMode == .pushToTalk else { return }
        transmitting = value
        audio.setSending(value)
        if value {
            setMuted(false)
        } else {
            setMuted(true)
        }
    }

    func setPttMode(_ mode: PttMode) {
        pttMode = mode
        SettingsStore.pttMode = mode
        if mode == .openMic {
            transmitting = true
            audio.setSending(true)
            setMuted(false)
        } else {
            transmitting = false
            audio.setSending(false)
            setMuted(true)
        }
    }

    func sendChat(text: String, to clientId: UInt64?) {
        let trimmed = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        let target: ChatTarget = clientId.map { .client(clientId: $0) } ?? .channel
        do {
            try core?.sendTextMessage(target: target, text: trimmed)
            let selfId = ownClientId ?? 0
            chatMessages.append(ChatMessage(
                fromClientId: selfId,
                fromName: currentBookmark?.nickname ?? "me",
                isSelf: true,
                isDirect: clientId != nil,
                text: trimmed
            ))
        } catch {
            lastError = describe(error)
        }
    }

    // MARK: - Core events

    private func handle(_ ev: ConnEvent) {
        switch ev {
        case let .connected(state):
            ownClientId = state.ownClientId
            serverName = state.serverName
            phase = .connected
            banner = nil
            recomputeTree()

        case let .disconnected(cause, reason):
            if cause == .requested {
                // Our own teardown echoing back — nothing to report.
                return
            }
            phase = .disconnected
            banner = nil
            lastError = "Connection lost: \(reason)"
            teardownConnection()
            scheduleReconnect()

        case let .channelTree(list):
            channels = list
            recomputeTree()

        case let .clientList(list):
            clientsById = Dictionary(uniqueKeysWithValues: list.map { ($0.id, $0) })
            recomputeTree()

        case let .clientMoved(clientId, channelId):
            clientsById[clientId]?.channelId = channelId
            recomputeTree()

        case let .talkStatus(clientId, isTalking):
            if isTalking {
                talking.insert(clientId)
            } else {
                talking.remove(clientId)
            }
            recomputeTree()

        case let .textMessage(target, fromClientId, fromName, text):
            let isDirect: Bool
            if case .client = target { isDirect = true } else { isDirect = false }
            chatMessages.append(ChatMessage(
                fromClientId: fromClientId,
                fromName: fromName,
                isSelf: fromClientId == ownClientId,
                isDirect: isDirect,
                text: text
            ))

        case let .error(message):
            lastError = message

        case let .temporaryDisconnect(reason):
            banner = "Reconnecting… (\(reason))"

        case let .resumed(state):
            // tsclientlib's internal reconnect can hand back a different
            // client id, so the roster re-syncs from this event rather than
            // trusting the id cached at connect time.
            ownClientId = state.ownClientId
            serverName = state.serverName
            banner = nil
            recomputeTree()

        case .rawCommand:
            // TS6 screen-share signalling. The iOS client is voice-only for
            // now, so these are dropped on the floor.
            break

        case .pcmFrame:
            break
        }
    }

    // MARK: - Tree

    private func recomputeTree() {
        var byParent: [UInt64: [Channel]] = [:]
        var roots: [Channel] = []
        let known = Set(channels.map { $0.id })
        for channel in channels {
            if let parent = channel.parentId, known.contains(parent) {
                byParent[parent, default: []].append(channel)
            } else {
                roots.append(channel)
            }
        }

        func build(_ channel: Channel) -> ChannelNode {
            let rows: [ClientRow] = clientsById.values
                .filter { $0.channelId == channel.id }
                .sorted { $0.name.localizedCaseInsensitiveCompare($1.name) == .orderedAscending }
                .map { info in
                    ClientRow(
                        clientId: info.id,
                        name: info.name,
                        isSelf: info.id == ownClientId,
                        presence: ClientPresence(
                            talking: talking.contains(info.id),
                            micMuted: info.inputMuted,
                            outputMuted: info.outputMuted,
                            away: info.away
                        )
                    )
                }
            return ChannelNode(
                id: channel.id,
                name: channel.name,
                hasPassword: channel.hasPassword,
                talkPower: channel.talkPower,
                clients: rows,
                children: (byParent[channel.id] ?? []).map { build($0) }
            )
        }

        tree = roots
            .sorted { $0.name.localizedCaseInsensitiveCompare($1.name) == .orderedAscending }
            .map { build($0) }
    }

    // MARK: - Reconnect

    private func startNetworkMonitor() {
        pathMonitor.pathUpdateHandler = { [weak self] path in
            let available = path.status == .satisfied
            Task { @MainActor in
                guard let self else { return }
                let wasDown = !self.networkAvailable
                self.networkAvailable = available
                if available && wasDown {
                    // The network came back: dial again if we were on a call.
                    if self.phase == .disconnected, self.currentBookmark != nil {
                        self.scheduleReconnect()
                    }
                }
            }
        }
        pathMonitor.start(queue: pathQueue)
    }

    private func scheduleReconnect() {
        guard let bookmark = currentBookmark else { return }
        reconnectAttempts += 1
        if reconnectAttempts > 6 { return }
        let delay = min(pow(2.0, Double(reconnectAttempts - 1)), 30.0)
        banner = "Reconnecting in \(Int(delay))s…"
        let work = DispatchWorkItem { [weak self] in
            Task { @MainActor in
                guard let self else { return }
                self.banner = "Reconnecting…"
                self.phase = .reconnecting
                self.startCall(bookmark)
            }
        }
        reconnectWorkItem = work
        DispatchQueue.main.asyncAfter(deadline: .now() + delay, execute: work)
    }

    // MARK: - Helpers

    private func describe(_ error: Error) -> String {
        if let plnt = error as? PlntError {
            switch plnt {
            case .NotConnected: return "Not connected."
            case let .Identity(message): return "Identity error: \(message)"
            case let .Connection(message): return "Connection error: \(message)"
            case let .Audio(message): return "Audio error: \(message)"
            case let .Invalid(message): return "Invalid input: \(message)"
            }
        }
        return error.localizedDescription
    }
}

// MARK: - Core holder

/// Holds the UniFFI client for threads that are not the main actor: the
/// capture queue hands PCM frames to the core from there, and the core calls
/// back on a Rust-owned thread.
final class CoreHolder: @unchecked Sendable {
    private let lock = NSLock()
    private var client: Client?

    func set(_ client: Client?) {
        lock.lock()
        self.client = client
        lock.unlock()
    }

    func send(frame: [Float]) {
        lock.lock()
        let client = self.client
        lock.unlock()
        try? client?.sendPcmFrame(frame: frame)
    }
}

// MARK: - Event sink

/// The UniFFI callback interface. The core calls `onEvent` on a Rust-owned
/// thread; anything that touches UI state is hopped to the main actor by the
/// handler. Nothing thrown here may escape — a throw becomes a panic on the
/// Rust side and silently kills the connection task.
final class CoreEventSink: EventSink {
    private let handler: (ConnEvent) -> Void

    init(handler: @escaping (ConnEvent) -> Void) {
        self.handler = handler
    }

    func onEvent(ev: ConnEvent) {
        handler(ev)
    }
}
