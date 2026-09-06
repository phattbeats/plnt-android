//! UniFFI-exposed types shared between the `Client` and `EventSink`. Kept in a
//! small module so the .udl stays trivial and the records/enums live next to
//! their constructors.

use serde::{Deserialize, Serialize};

/// A single channel in the TeamSpeak tree. Projects the bookkeeping `Channel`
/// into the minimum the app needs.
#[derive(Clone, Debug, Serialize, Deserialize, uniffi::Record)]
pub struct Channel {
    pub id: u64,
    pub name: String,
    pub parent_id: Option<u64>,
    pub has_password: bool,
    /// None means unlimited (or inherited; app shouldn't distinguish for v1).
    pub max_clients: Option<u32>,
    pub talk_power: i64,
    pub codec_latency_factor: u8,
    pub codec_is_opus_music: bool,
}

/// Connection-state snapshot, emitted on `Connected`.
#[derive(Clone, Debug, Serialize, Deserialize, uniffi::Record)]
pub struct ConnectionState {
    pub own_client_id: u64,
    pub server_name: String,
}

/// One client visible on the server. `ClientMoved` only ever carried an id, so
/// the app had no nicknames and no way to learn about anyone who was already
/// connected — this record is the roster the channel tree hangs clients off.
#[derive(Clone, Debug, Serialize, Deserialize, uniffi::Record)]
pub struct ClientInfo {
    pub id: u64,
    pub name: String,
    pub channel_id: u64,
    /// Peer's own mic mute, as the server reports it (the MIC pill).
    pub input_muted: bool,
    /// Peer's own speaker mute / deafen, as the server reports it (the SND pill).
    pub output_muted: bool,
    pub away: bool,
}

/// Why a connection ended, as far as the core can tell the cases apart.
///
/// PHA-3283: `Disconnected` used to carry only a reason string, and the
/// app-requested exit hardcoded `"client.disconnect"`. Downstream that was
/// indistinguishable from the user hanging up, which is what blocked diagnosis
/// of the PHA-3238 drop. The core deliberately does not model *why the app
/// asked* — it cannot know — so the Android layer widens this into its own
/// `DisconnectCause` (see `CoreBridge.kt`) with the causes only it can see,
/// such as Android reclaiming the foreground service.
#[derive(Clone, Copy, Debug, PartialEq, Eq, Serialize, Deserialize, uniffi::Enum)]
pub enum DisconnectCause {
    /// [`crate::Client::disconnect`] was called: the connection loop exited
    /// because this app asked it to. Says nothing about who or what inside
    /// the app asked.
    Requested,
    /// The event stream ended under us — server shutdown/kick, or the
    /// transport died. Nobody on this side asked for it.
    ConnectionLost,
}

/// Where a text message came from / should be sent to. Server-wide chat and
/// pokes exist in the protocol but are out of scope for v1 (PHA-3281) —
/// channel chat and client-to-client private messages only.
#[derive(Clone, Debug, Serialize, Deserialize, uniffi::Enum)]
pub enum ChatTarget {
    Channel,
    Client { client_id: u64 },
}

/// All events the core emits to the sink. Flat enum over the variants the issue
/// specifies.
#[derive(Clone, Debug, Serialize, Deserialize, uniffi::Enum)]
pub enum ConnEvent {
    Connected(ConnectionState),
    Disconnected { cause: DisconnectCause, reason: String },
    ChannelTree(Vec<Channel>),
    /// Full roster snapshot, not a delta — emitted once on connect and again
    /// whenever the visible client set or its properties change.
    ClientList(Vec<ClientInfo>),
    ClientMoved { client_id: u64, channel_id: u64 },
    TalkStatus { client_id: u64, talking: bool },
    /// 960 mono f32 samples at 48 kHz (20 ms) — jitter-buffered mixed mono.
    PcmFrame { client_id: u64, samples: Vec<f32> },
    /// Inbound channel or private text message.
    TextMessage {
        target: ChatTarget,
        from_client_id: u64,
        from_name: String,
        text: String,
    },
    Error(String),
    /// tsclientlib is resending unacked packets and hasn't heard back yet. It
    /// resolves this internally (reconnect + resume) without ever tearing the
    /// `Client` down, so unlike `Disconnected` no `Connected` follows — the UI
    /// must clear whatever this sets itself, once `Resumed` arrives.
    TemporaryDisconnect { reason: String },
    /// Emitted once, on the first `BookEvents` batch after a `TemporaryDisconnect`
    /// resolves. tsclientlib's internal reconnect rebuilds its session state from
    /// scratch, which can hand back a different `own_client_id` than before the
    /// blip even though nothing about the app's session looks different from the
    /// outside (PHA-3277) — carrying the fresh id here is what lets the roster's
    /// "(you)" row re-sync instead of silently going stale.
    Resumed(ConnectionState),
}
