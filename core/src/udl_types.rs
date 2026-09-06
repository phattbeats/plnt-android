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

/// All events the core emits to the sink. Flat enum over the variants the issue
/// specifies.
#[derive(Clone, Debug, Serialize, Deserialize, uniffi::Enum)]
pub enum ConnEvent {
    Connected(ConnectionState),
    Disconnected { reason: String },
    ChannelTree(Vec<Channel>),
    /// Full roster snapshot, not a delta — emitted once on connect and again
    /// whenever the visible client set or its properties change.
    ClientList(Vec<ClientInfo>),
    ClientMoved { client_id: u64, channel_id: u64 },
    TalkStatus { client_id: u64, talking: bool },
    /// 960 mono f32 samples at 48 kHz (20 ms) — jitter-buffered mixed mono.
    PcmFrame { client_id: u64, samples: Vec<f32> },
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
