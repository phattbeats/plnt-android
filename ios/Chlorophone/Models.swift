import Foundation

// MARK: - Connection lifecycle

/// Where the app is in the connect lifecycle. Mirrors the Android app's
/// `ConnectionPhase` (see model/ServerConnectionStatus.kt) — the iOS port
/// keeps the same three ideas so the two apps read the same way.
enum ConnectionPhase: Equatable {
    case disconnected
    case connecting
    case connected
    /// A previously-good connection dropped and we are dialling again.
    case reconnecting
}

// MARK: - Saved servers

/// One saved TeamSpeak server. The Android app calls this a Bookmark; the
/// name is kept so both apps speak the same language.
struct Bookmark: Identifiable, Codable, Equatable {
    var id: String = UUID().uuidString
    var label: String
    var address: String
    var port: Int
    var nickname: String
    var serverPassword: String?

    init(
        id: String = UUID().uuidString,
        label: String,
        address: String,
        port: Int,
        nickname: String,
        serverPassword: String? = nil
    ) {
        self.id = id
        self.label = label
        self.address = address
        self.port = port
        self.nickname = nickname
        self.serverPassword = serverPassword
    }
}

// MARK: - Roster

/// Per-client state, as the design models it: independent axes, not one enum.
/// MIC and SND can show at once (mic muted *and* output muted) and "away" is a
/// font-style change that coexists with the colour vocabulary.
struct ClientPresence: Equatable {
    var talking: Bool = false
    var micMuted: Bool = false
    var outputMuted: Bool = false
    var away: Bool = false
}

/// One row under a channel.
struct ClientRow: Identifiable, Equatable {
    var clientId: UInt64
    var name: String
    var isSelf: Bool
    var presence: ClientPresence

    var id: UInt64 { clientId }
}

/// One channel and its subtree.
struct ChannelNode: Identifiable, Equatable {
    var id: UInt64
    var name: String
    var hasPassword: Bool
    var talkPower: Int64
    var clients: [ClientRow] = []
    var children: [ChannelNode] = []

    /// Rows the tree view flattens: the channel, then its clients, then its
    /// children. Depth is carried so the view can indent.
    struct Flat {
        var channel: ChannelNode
        var depth: Int
        var clients: [ClientRow]
    }

    var flattened: [Flat] {
        var out: [Flat] = []
        func walk(_ node: ChannelNode, _ depth: Int) {
            out.append(Flat(channel: node, depth: depth, clients: node.clients))
            for child in node.children {
                walk(child, depth + 1)
            }
        }
        walk(self, 0)
        return out
    }
}

// MARK: - Chat

/// One chat line, channel or private.
struct ChatMessage: Identifiable, Equatable {
    var id: String = UUID().uuidString
    var fromClientId: UInt64
    var fromName: String
    var isSelf: Bool
    var isDirect: Bool
    var text: String
}

// MARK: - Settings

/// Push-to-talk or open mic. Open mic means the mic stays open unless the
/// user mutes it; push-to-talk means it only opens while the button is held.
enum PttMode: String, Codable, CaseIterable, Identifiable {
    case pushToTalk
    case openMic

    var id: String { rawValue }

    var label: String {
        switch self {
        case .pushToTalk: return "Push to talk"
        case .openMic: return "Open mic"
        }
    }
}
