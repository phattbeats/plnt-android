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

/// All events the core emits to the sink. Flat enum over the variants the issue
/// specifies.
#[derive(Clone, Debug, Serialize, Deserialize, uniffi::Enum)]
pub enum ConnEvent {
    Connected(ConnectionState),
    Disconnected { reason: String },
    ChannelTree(Vec<Channel>),
    ClientMoved { client_id: u64, channel_id: u64 },
    TalkStatus { client_id: u64, talking: bool },
    /// 960 mono f32 samples at 48 kHz (20 ms) — jitter-buffered mixed mono.
    PcmFrame { client_id: u64, samples: Vec<f32> },
    Error(String),
}
