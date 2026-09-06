//! PLNT core: Rust voice client + UniFFI surface to Kotlin.
//!
//! Voice-only TeamSpeak client. Transport is `tsclientlib` (pinned by commit) and the
//! audio codec is Opus via `audiopus`.
//!
//! Scope (voice only):
//! - Connect / disconnect.
//! - Channel tree discovery (cached from the server's first book-event burst).
//! - Join a channel by id.
//! - Mute / deafen the local client.
//! - Push PCM frames (48 kHz mono, 20 ms each) → Opus-encode and send.
//! - Receive PCM frames from any talker (Opus decode + jitter buffer) → emit
//!   `on_pcm_frame` at 20 ms granularity.
//! - Talk-status notifications (who is currently sending voice).
//! - Identity create / import / export as a portable string.

use std::collections::HashMap;
use std::sync::{Arc, Mutex};

// UniFFI 0.27 requires the scaffolding include macro at the crate root.
::uniffi::include_scaffolding!("plnt_core");

use audiopus::coder::Encoder;
use audiopus::{Application, Channels, SampleRate};
use base64::engine::general_purpose::STANDARD as BASE64_STANDARD;
use base64::Engine as _;
use futures::prelude::*;
use tsclientlib::audio::AudioHandler;
use tsclientlib::messages::c2s::{OutClientMoveMessage, OutClientMovePart};
use tsclientlib::prelude::M2BClientUpdateExt;
use tsclientlib::{ChannelId, OutCommandExt};
use tsclientlib::{Connection, DisconnectOptions, Identity, StreamItem};
use tsproto_packets::packets::{AudioData, CodecType, OutAudio};

mod udl_types;

pub use udl_types::{Channel, ClientInfo, ConnEvent, ConnectionState, DisconnectCause};

/// Build version string, surfaced to the Kotlin side for the settings screen
/// and bug reports.
#[uniffi::export]
pub fn core_version() -> String {
    env!("CARGO_PKG_VERSION").to_string()
}

// ---------------------------------------------------------------------------
// Errors
// ---------------------------------------------------------------------------

/// Error type surfaced through UniFFI.
#[derive(Debug, thiserror::Error, uniffi::Error)]
#[uniffi(flat_error)]
pub enum PlntError {
    #[error("not connected")]
    NotConnected,
    #[error("identity error: {0}")]
    Identity(String),
    #[error("connection error: {0}")]
    Connection(String),
    #[error("audio error: {0}")]
    Audio(String),
    #[error("invalid argument: {0}")]
    Invalid(String),
}

// ---------------------------------------------------------------------------
// Event sink — UniFFI callback interface
// ---------------------------------------------------------------------------

/// Sink that the core calls back into. Kotlin implements this and the native side
/// funnels `ConnEvent` values through it.
#[uniffi::export(callback_interface)]
pub trait EventSink: Send + Sync {
    fn on_event(&self, ev: ConnEvent);
}

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

/// 48 kHz × 20 ms = 960 samples per PCM frame.
const PCM_FRAME_SAMPLES: usize = 960;
/// Max bytes in a single Opus-encoded 20 ms mono packet.
const MAX_OPUS_PACKET_BYTES: usize = 1275;

// ---------------------------------------------------------------------------
// Client
// ---------------------------------------------------------------------------

#[derive(uniffi::Object)]
pub struct Client {
    /// Held in a `Mutex` so UniFFI's exported methods (which take `&self`) can
    /// mutate the slot. The slot is `Some` for the lifetime of the object.
    sink: Mutex<Option<Arc<dyn EventSink>>>,
    state: Arc<Mutex<ClientState>>,
    /// Tokio runtime that owns the connection task.
    runtime: tokio::runtime::Runtime,
}

struct ClientState {
    connection: Option<ConnectionHandle>,
    own_client_id: u16,
    server_name: String,
    /// Cached channel tree (ChannelId → record). Refreshed whenever we observe
    /// a new `BookEvents` batch from the server.
    channel_tree: HashMap<u64, Channel>,
}

#[derive(Clone)]
struct ConnectionHandle {
    cmd_tx: std::sync::mpsc::Sender<ControlCommand>,
}

enum ControlCommand {
    Disconnect,
    JoinChannel { channel_id: u64, password: Option<String> },
    SetInputMuted(bool),
    SetOutputMuted(bool),
    SendPcmFrame(Vec<f32>),
}

#[uniffi::export]
impl Client {
    /// Build a new, unconnected client. The `sink` receives all events.
    #[uniffi::constructor]
    pub fn new(sink: Box<dyn EventSink>) -> Arc<Self> {
        let runtime = tokio::runtime::Builder::new_multi_thread()
            .enable_all()
            .worker_threads(2)
            .build()
            .expect("plnt-core: tokio runtime build");
        let sink_arc: Arc<dyn EventSink> = Arc::from(sink);
        Arc::new(Self {
            sink: Mutex::new(Some(sink_arc)),
            state: Arc::new(Mutex::new(ClientState {
                connection: None,
                own_client_id: 0,
                server_name: String::new(),
                channel_tree: HashMap::new(),
            })),
            runtime,
        })
    }

    /// Connect to a TeamSpeak 3 server and wait until the first `BookEvents` batch
    /// has been received (the server has accepted us and sent the initial state).
    pub fn connect(
        &self,
        address: String,
        port: u16,
        nickname: String,
        identity_pem: String,
        password: Option<String>,
    ) -> Result<(), PlntError> {
        let id = parse_identity(&identity_pem)?;

        let address_str = format!("{address}:{port}");
        let mut opts = Connection::build(address_str)
            .identity(id)
            .name(nickname);
        if let Some(pwd) = password.as_deref() {
            opts = opts.password(pwd.to_string());
        }

        let mut con = opts
            .connect()
            .map_err(|e| PlntError::Connection(format!("{e}")))?;

        // Drain the first BookEvents batch synchronously so we have a channel tree
        // available before we hand the connection off to the background task.
        let first_book = self.runtime.block_on(async {
            con.events()
                .try_filter(|e| future::ready(matches!(e, StreamItem::BookEvents(_))))
                .next()
                .await
        });
        match first_book {
            Some(Ok(_)) => {}
            Some(Err(e)) => {
                return Err(PlntError::Connection(format!(
                    "first book events failed: {e}"
                )));
            }
            None => {
                return Err(PlntError::Connection(
                    "stream ended before first book events".into(),
                ));
            }
        }

        let state_snapshot = con
            .get_state()
            .map_err(|e| PlntError::Connection(format!("get_state: {e}")))?;
        let own_client_id = state_snapshot.own_client.0;
        let server_name = state_snapshot.server.name.clone();
        let channel_tree = snapshot_channel_tree(&state_snapshot);
        let clients = snapshot_clients(&state_snapshot);

        let (cmd_tx, cmd_rx) = std::sync::mpsc::channel::<ControlCommand>();

        let sink_for_task = self
            .sink
            .lock()
            .unwrap()
            .as_ref()
            .cloned()
            .expect("sink installed at construction");
        let state_for_task = Arc::clone(&self.state);
        let initial_channel_tree = channel_tree.clone();
        let sink_for_connected = Arc::clone(&sink_for_task);

        // Publish state before spawning the loop: the loop's first act is to
        // check `connection.is_some()` and bail if it isn't set yet.
        {
            let mut guard = self.state.lock().unwrap();
            guard.own_client_id = own_client_id;
            guard.server_name = server_name.clone();
            guard.channel_tree = channel_tree.clone();
            guard.connection = Some(ConnectionHandle { cmd_tx });
        }

        // `connect()` above consumed the server's first BookEvents batch itself,
        // so the loop never sees it and — on a quiet server — never emits a
        // tree at all. That is why the connected screen came up empty. Replay
        // the snapshot here, and seed the loop's signatures so it does not
        // immediately emit the identical pair again.
        let initial_tree_signature = tree_signature(&channel_tree);
        let initial_client_signature = clients_signature(&clients);

        sink_for_connected.on_event(ConnEvent::Connected(ConnectionState {
            own_client_id: own_client_id.into(),
            server_name,
        }));
        sink_for_connected.on_event(ConnEvent::ChannelTree(
            channel_tree.values().cloned().collect(),
        ));
        sink_for_connected.on_event(ConnEvent::ClientList(clients));

        self.runtime.spawn(async move {
            run_connection_loop(
                con,
                cmd_rx,
                sink_for_task,
                state_for_task,
                initial_channel_tree,
                initial_tree_signature,
                initial_client_signature,
            )
            .await;
        });

        Ok(())
    }

    /// Disconnect cleanly. Drops the command channel; the background task exits.
    pub fn disconnect(&self) -> Result<(), PlntError> {
        let cmd_tx = {
            let mut guard = self.state.lock().unwrap();
            let handle = guard
                .connection
                .take()
                .ok_or_else(|| PlntError::NotConnected)?;
            handle.cmd_tx
        };
        let _ = cmd_tx.send(ControlCommand::Disconnect);
        Ok(())
    }

    /// Move the own client to a channel by id. The channel's current name is
    /// resolved from the cached tree.
    pub fn join_channel(
        &self,
        channel_id: u64,
        _password: Option<String>,
    ) -> Result<(), PlntError> {
        // Validate the id exists before sending the command.
        {
            let guard = self.state.lock().unwrap();
            if !guard.channel_tree.contains_key(&channel_id) {
                return Err(PlntError::Invalid(format!(
                    "unknown channel id {channel_id}"
                )));
            }
        }
        self.cmd_tx()?
            .send(ControlCommand::JoinChannel { channel_id, password: None })
            .map_err(|_| PlntError::NotConnected)
    }

    pub fn set_input_muted(&self, muted: bool) -> Result<(), PlntError> {
        self.cmd_tx()?
            .send(ControlCommand::SetInputMuted(muted))
            .map_err(|_| PlntError::NotConnected)
    }

    pub fn set_output_muted(&self, muted: bool) -> Result<(), PlntError> {
        self.cmd_tx()?
            .send(ControlCommand::SetOutputMuted(muted))
            .map_err(|_| PlntError::NotConnected)
    }

    /// Encode a 960-sample mono f32 frame (20 ms @ 48 kHz) as Opus and send it.
    pub fn send_pcm_frame(&self, frame: Vec<f32>) -> Result<(), PlntError> {
        if frame.len() != PCM_FRAME_SAMPLES {
            return Err(PlntError::Invalid(format!(
                "expected {PCM_FRAME_SAMPLES} samples, got {}",
                frame.len()
            )));
        }
        self.cmd_tx()?
            .send(ControlCommand::SendPcmFrame(frame))
            .map_err(|_| PlntError::NotConnected)
    }
}

impl Client {
    fn cmd_tx(&self) -> Result<std::sync::mpsc::Sender<ControlCommand>, PlntError> {
        let guard = self.state.lock().unwrap();
        match guard.connection.as_ref() {
            Some(h) => Ok(h.cmd_tx.clone()),
            None => Err(PlntError::NotConnected),
        }
    }
}

// ---------------------------------------------------------------------------
// Identity
// ---------------------------------------------------------------------------

/// Portable, persistent TeamSpeak identity. The export string is the standard
/// TS3 identity format `"<counter>V<base64-key>"` — the same shape the official
/// client persists.
#[derive(uniffi::Object)]
pub struct IdentityObj {
    inner: Identity,
}

#[uniffi::export]
impl IdentityObj {
    /// Create a new identity with the default level (8).
    #[uniffi::constructor]
    pub fn create() -> Arc<Self> {
        Arc::new(Self { inner: Identity::create() })
    }

    /// Import a previously exported identity.
    #[uniffi::constructor]
    pub fn import(s: String) -> Result<Arc<Self>, PlntError> {
        Ok(Arc::new(Self { inner: parse_identity(&s)? }))
    }

    /// Export as a portable string. Round-trips through [`parse_identity`] —
    /// and therefore through `import` and `Client::connect`, which both use it.
    ///
    /// The format is the standard TS3 identity string `"<counter>V<base64-key>"`,
    /// the same shape the official client persists, so the counter (and with it
    /// the hash-cash level) survives the round trip. The key half is
    /// `base64(key.to_short())`, which is what tsproto's own `Serialize` impl
    /// writes and what `EccKeyPrivP256::import_str` reads back.
    pub fn export(&self) -> String {
        format!(
            "{}V{}",
            self.inner.counter(),
            BASE64_STANDARD.encode(self.inner.key().to_short()),
        )
    }

    /// Current security level (8 by default).
    pub fn level(&self) -> u8 {
        self.inner.level()
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

/// Parse an identity string. The one parser behind both
/// [`IdentityObj::import`] and [`Client::connect`] — they used to differ, and a
/// value [`IdentityObj::export`] produced was not accepted by either (PHA-3238).
///
/// Accepts, in order:
/// - the TS3 identity string `"<counter>V<base64-key>"` that `export()` writes,
///   and bare base64/tomcrypt keys — both via tsproto's `new_from_str`;
/// - the JSON object `{"key":…,"counter":…,"max_counter":…}` that `export()`
///   wrote before this fix, so an identity already persisted by an installed
///   build keeps working (and re-exports in the new form on next save).
fn parse_identity(s: &str) -> Result<Identity, PlntError> {
    let s = s.trim();
    if s.is_empty() {
        return Err(PlntError::Identity(
            "identity is empty — create or import one before connecting".into(),
        ));
    }
    if s.starts_with('{') {
        return serde_json::from_str::<Identity>(s)
            .map_err(|e| PlntError::Identity(format!("malformed identity JSON: {e}")));
    }
    Identity::new_from_str(s).map_err(|e| PlntError::Identity(format!("{e:?}")))
}

/// Snapshot the channel tree into a `HashMap<id, Channel>`. ts-bookkeeping's
/// `Connection` book view is the source of truth — we project the minimal
/// record the app needs.
fn snapshot_channel_tree(
    state: &tsclientlib::data::Connection,
) -> HashMap<u64, Channel> {
    state
        .channels
        .iter()
        .map(|(id, ch)| {
            (
                id.0,
                Channel {
                    id: id.0,
                    name: ch.name.clone(),
                    parent_id: if ch.parent.0 == 0 { None } else { Some(ch.parent.0) },
                    has_password: ch.has_password.unwrap_or(false),
                    max_clients: match ch.max_clients.as_ref() {
                        Some(tsclientlib::MaxClients::Limited(n)) => Some((*n) as u32),
                        _ => None,
                    },
                    talk_power: ch.needed_talk_power.unwrap_or(0) as i64,
                    codec_latency_factor: ch.codec_latency_factor.unwrap_or(0) as u8,
                    codec_is_opus_music: matches!(ch.codec, tsclientlib::Codec::OpusMusic),
                },
            )
        })
        .collect()
}

/// Snapshot every client the server has told us about, with the properties the
/// roster rows render. Sorted by id so the signature below is stable.
fn snapshot_clients(state: &tsclientlib::data::Connection) -> Vec<ClientInfo> {
    let mut clients: Vec<ClientInfo> = state
        .clients
        .iter()
        .map(|(id, c)| ClientInfo {
            id: id.0 as u64,
            name: c.name.clone(),
            channel_id: c.channel.0,
            input_muted: c.input_muted,
            output_muted: c.output_muted,
            away: c.away_message.is_some(),
        })
        .collect();
    clients.sort_unstable_by_key(|c| c.id);
    clients
}

fn clients_signature(clients: &[ClientInfo]) -> u64 {
    use std::hash::{Hash, Hasher};
    let mut hasher = std::collections::hash_map::DefaultHasher::new();
    for c in clients {
        c.id.hash(&mut hasher);
        c.name.hash(&mut hasher);
        c.channel_id.hash(&mut hasher);
        c.input_muted.hash(&mut hasher);
        c.output_muted.hash(&mut hasher);
        c.away.hash(&mut hasher);
    }
    hasher.finish()
}

// ---------------------------------------------------------------------------
// Background connection task
// ---------------------------------------------------------------------------

#[allow(clippy::too_many_arguments)]
async fn run_connection_loop(
    mut con: Connection,
    cmd_rx: std::sync::mpsc::Receiver<ControlCommand>,
    sink: Arc<dyn EventSink>,
    shared_state: Arc<Mutex<ClientState>>,
    initial_channel_tree: HashMap<u64, Channel>,
    initial_tree_signature: u64,
    initial_client_signature: u64,
) {
    let mut encoder: Option<Encoder> = None;
    let mut out_packet_buf: Vec<u8> = vec![0u8; MAX_OPUS_PACKET_BYTES];
    let mut audio: AudioHandler = AudioHandler::default();
    let mut channel_tree = initial_channel_tree;
    let mut last_tree_signature: Option<u64> = Some(initial_tree_signature);
    let mut last_client_signature: Option<u64> = Some(initial_client_signature);
    // Track which queues currently exist so we can emit TalkStatus false on remove.
    let mut known_talkers: std::collections::HashSet<u16> = std::collections::HashSet::new();
    // Distinguishes "we tore this down" from "the server did".
    let mut stream_ended = false;

    loop {
        // Drain control commands first — `try_recv`, never `recv`. Wrapping the
        // blocking `recv()` in a `poll_fn` made this task park on a std channel
        // inside an async poll: the connection's own stream then never got
        // serviced, the server saw no acks, and it dropped us on resend timeout
        // about 25 s in while the UI still said "connected".
        loop {
            let cmd = match cmd_rx.try_recv() {
                Ok(v) => v,
                Err(std::sync::mpsc::TryRecvError::Empty) => break,
                Err(std::sync::mpsc::TryRecvError::Disconnected) => break,
            };
            match cmd {
                ControlCommand::Disconnect => break,
                ControlCommand::JoinChannel { channel_id, password } => {
                    let own_client = con.get_state().ok().map(|s| s.own_client);
                    if let Some(own) = own_client {
                        let pwd = password.unwrap_or_default();
                        let mut parts = std::iter::once(OutClientMovePart {
                            client_id: own,
                            channel_id: ChannelId(channel_id),
                            channel_password: if pwd.is_empty() { None } else { Some(pwd.into()) },
                        });
                        let cmd = OutClientMoveMessage::new(&mut parts);
                        let _ = cmd.send(&mut con);
                    }
                }
                ControlCommand::SetInputMuted(muted) => {
                    if let Ok(state) = con.get_state() {
                        let _ = state.client_update().set_input_muted(muted).send(&mut con);
                    }
                }
                ControlCommand::SetOutputMuted(muted) => {
                    if let Ok(state) = con.get_state() {
                        let _ = state.client_update().set_output_muted(muted).send(&mut con);
                    }
                }
                ControlCommand::SendPcmFrame(frame) => {
                    if encoder.is_none() {
                        match Encoder::new(SampleRate::Hz48000, Channels::Mono, Application::Voip) {
                            Ok(e) => encoder = Some(e),
                            Err(e) => {
                                sink.on_event(ConnEvent::Error(format!("opus encoder init: {e:?}")));
                                continue;
                            }
                        }
                    }
                    let enc = encoder.as_mut().unwrap();
                    match enc.encode_float(&frame, &mut out_packet_buf) {
                        Ok(len) => {
                            let pkt = OutAudio::new(&AudioData::C2S {
                                id: 0,
                                codec: CodecType::OpusVoice,
                                data: &out_packet_buf[..len],
                            });
                            if let Err(e) = con.send_audio(pkt) {
                                sink.on_event(ConnEvent::Error(format!("send_audio: {e}")));
                            }
                        }
                        Err(e) => {
                            sink.on_event(ConnEvent::Error(format!("opus encode: {e:?}")));
                        }
                    }
                }
            }
        }
        if shared_state.lock().unwrap().connection.is_none() {
            break;
        }
        // Pull one event (or none if none ready, then yield). Keep the two
        // layers of Option apart: `None` is "nothing ready yet", `Some(None)`
        // is "the stream ended" — flattening them together meant a connection
        // the server had already torn down looked exactly like an idle one, so
        // the loop spun forever and the UI never left "connected".
        let ev = {
            use futures::StreamExt;
            con.events().next().now_or_never()
        };
        let ev = match ev {
            Some(Some(e)) => e,
            Some(None) => {
                stream_ended = true;
                break;
            }
            None => {
                tokio::task::yield_now().await;
                continue;
            }
        };
        match ev {
            Ok(StreamItem::Audio(audio_pkt)) => {
                let from_raw: u16 = match audio_pkt.data().data() {
                    AudioData::S2C { from, .. } => *from,
                    _ => continue,
                };
                let from_id = tsclientlib::ClientId(from_raw);
                if let Ok(Some(_id)) = audio.handle_packet(from_id, audio_pkt) {
                    if known_talkers.insert(from_id.0) {
                        sink.on_event(ConnEvent::TalkStatus { client_id: from_id.0 as u64, talking: true });
                    }
                }
                // Pull decoded 20 ms frames out of every queue.
                let queue_ids: Vec<u16> = audio.get_queues().keys().map(|k| k.0).collect();
                let mut output = vec![0f32; PCM_FRAME_SAMPLES];
                for cid in queue_ids {
                    let id = tsclientlib::ClientId(cid);
                    let ended = {
                        let q = audio.get_mut_queues().get_mut(&id).unwrap();
                        match q.get_next_data(PCM_FRAME_SAMPLES) {
                            Ok((samples, is_end)) => {
                                let n = samples.len().min(PCM_FRAME_SAMPLES);
                                if n > 0 {
                                    output[..n].copy_from_slice(&samples[..n]);
                                    sink.on_event(ConnEvent::PcmFrame {
                                        client_id: cid as u64,
                                        samples: output[..n].to_vec(),
                                    });
                                }
                                is_end
                            }
                            Err(_) => continue,
                        }
                    };
                    if ended && known_talkers.remove(&cid) {
                        sink.on_event(ConnEvent::TalkStatus { client_id: cid as u64, talking: false });
                    }
                }
            }
            Ok(StreamItem::BookEvents(events)) => {
                if let Ok(state) = con.get_state() {
                    channel_tree = snapshot_channel_tree(&state);
                    {
                        let mut guard = shared_state.lock().unwrap();
                        guard.channel_tree = channel_tree.clone();
                    }
                    let signature = tree_signature(&channel_tree);
                    if last_tree_signature != Some(signature) {
                        let channels: Vec<Channel> = channel_tree.values().cloned().collect();
                        sink.on_event(ConnEvent::ChannelTree(channels));
                        last_tree_signature = Some(signature);
                    }
                    // Roster snapshot covers joins, parts, moves, renames and
                    // peer mute changes in one event — `ClientMoved` below only
                    // ever fires for someone who moves while we are watching.
                    let clients = snapshot_clients(&state);
                    let client_signature = clients_signature(&clients);
                    if last_client_signature != Some(client_signature) {
                        sink.on_event(ConnEvent::ClientList(clients));
                        last_client_signature = Some(client_signature);
                    }
                    for ev in events {
                        if let tsclientlib::events::Event::PropertyChanged {
                            id: tsclientlib::events::PropertyId::ClientChannel(client_id),
                            ..
                        } = &ev
                        {
                            if let Some(client) = state.clients.get(client_id) {
                                sink.on_event(ConnEvent::ClientMoved {
                                    client_id: client_id.0 as u64,
                                    channel_id: client.channel.0,
                                });
                            }
                        }
                    }
                }
            }
            Ok(StreamItem::NetworkStatsUpdated) => {}
            Ok(StreamItem::DisconnectedTemporarily(reason)) => {
                sink.on_event(ConnEvent::Error(format!("temp disconnect: {reason:?}")));
            }
            Ok(_) => {}
            Err(e) => {
                sink.on_event(ConnEvent::Error(format!("event error: {e}")));
            }
        }
    }

    let _ = con.disconnect(DisconnectOptions::new());
    // The cause is the load-bearing half here; the reason string is only for
    // logs and bug reports. `Requested` says the loop exited because the app
    // called `Client::disconnect()` — it does NOT say the *user* did, which is
    // the conflation PHA-3283 is about. Only the Android service layer knows
    // whether that request came from a Disconnect tap or from `onDestroy()`
    // after Android reclaimed the service.
    let (cause, reason) = if stream_ended {
        (DisconnectCause::ConnectionLost, "connection lost")
    } else {
        (DisconnectCause::Requested, "client.disconnect")
    };
    sink.on_event(ConnEvent::Disconnected { cause, reason: reason.into() });
    let mut guard = shared_state.lock().unwrap();
    guard.connection = None;
}

/// Cheap signature used to decide whether to re-emit the channel tree.
fn tree_signature(tree: &HashMap<u64, Channel>) -> u64 {
    use std::hash::{Hash, Hasher};
    let mut hasher = std::collections::hash_map::DefaultHasher::new();
    let mut keys: Vec<u64> = tree.keys().copied().collect();
    keys.sort_unstable();
    for k in keys {
        k.hash(&mut hasher);
        if let Some(c) = tree.get(&k) {
            c.name.hash(&mut hasher);
        }
    }
    hasher.finish()
}
