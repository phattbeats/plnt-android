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
use std::borrow::Cow;

use tsclientlib::audio::AudioHandler;
use tsclientlib::messages::c2s::{
    OutClientMoveMessage, OutClientMovePart, OutPrivilegeKeyUseMessage, OutPrivilegeKeyUsePart,
};
use tsclientlib::prelude::{M2BClientMoveExt, M2BClientUpdateExt};
use tsclientlib::{ChannelId, OutCommandExt};
use tsclientlib::{Connection, DisconnectOptions, Identity, StreamItem};
use tsproto::connection::Event as RawEvent;
use tsproto_packets::packets::{AudioData, CodecType, Direction, Flags, OutAudio, OutCommand, PacketType};

#[cfg(target_os = "android")]
mod android_ctx;
mod identity_file;
mod udl_types;

pub use identity_file::import_from_ini;
pub use udl_types::{Channel, ChatTarget, ClientInfo, ConnEvent, ConnectionState, DisconnectCause};

/// Build version string, surfaced to the Kotlin side for the settings screen
/// and bug reports. Self-describing (`"plnt-core 0.1.0"`, not bare `"0.1.0"`)
/// because Settings -> About renders it unlabeled, directly under the app
/// description (#3132).
#[uniffi::export]
pub fn core_version() -> String {
    format!("plnt-core {}", env!("CARGO_PKG_VERSION"))
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

/// PHA-4176: last panic message + location. Android sends a native panic's
/// stderr nowhere, so the hook parks it here for the connection task to report.
static LAST_PANIC: Mutex<Option<String>> = Mutex::new(None);

fn install_panic_hook() {
    static ONCE: std::sync::Once = std::sync::Once::new();
    ONCE.call_once(|| {
        let prev = std::panic::take_hook();
        std::panic::set_hook(Box::new(move |info| {
            if let Ok(mut g) = LAST_PANIC.lock() {
                *g = Some(info.to_string());
            }
            prev(info);
        }));
    });
}

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

/// 48 kHz × 20 ms = 960 samples per PCM frame.
const PCM_FRAME_SAMPLES: usize = 960;
/// tsclientlib's `AudioQueue` decodes to interleaved stereo, so one 20 ms pull
/// is twice the mono frame; we downmix before handing it to Kotlin.
const STEREO_FRAME_SAMPLES: usize = PCM_FRAME_SAMPLES * 2;
/// Playback pull cadence: one 20 ms frame per tick, independent of when
/// packets arrive.
const AUDIO_TICK: std::time::Duration = std::time::Duration::from_millis(20);
/// Longest the loop parks on the event stream before servicing commands and
/// the playback tick again.
const LOOP_WAIT: std::time::Duration = std::time::Duration::from_millis(5);
/// Bounded command queue: about 5 s of 20 ms PCM frames. When full, audio is
/// dropped instead of piling up behind a stall and bursting out afterwards.
const CMD_QUEUE_CAP: usize = 256;
/// Most commands handled per loop pass, so a backlog can't starve the
/// connection's own stream (pongs/acks).
const CMD_DRAIN_MAX: usize = 16;

/// A poisoned lock means some thread panicked mid-update; the state is still
/// usable, and turning every later FFI call into a panic is worse.
fn lock<T>(m: &Mutex<T>) -> std::sync::MutexGuard<'_, T> {
    m.lock().unwrap_or_else(|e| e.into_inner())
}
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
    /// Bumped on every connect. A loop only owns `connection` while the
    /// handle's generation matches its own, so an old loop finishing late
    /// can't clear a newer connection's handle.
    next_generation: u64,
}

#[derive(Clone)]
struct ConnectionHandle {
    cmd_tx: std::sync::mpsc::SyncSender<ControlCommand>,
    generation: u64,
}

/// True while `state.connection` is still the handle of connection `generation`.
fn owns_connection(state: &ClientState, generation: u64) -> bool {
    state.connection.as_ref().map(|h| h.generation) == Some(generation)
}

#[derive(Debug)]
enum ControlCommand {
    Disconnect,
    JoinChannel { channel_id: u64, password: Option<String> },
    /// PHA-4849: spend a privilege key (a token the server's admin issued)
    /// on the live connection. Used once — the server's answer is an `error`
    /// command carrying `return_code=token`.
    UsePrivilegeKey(String),
    SetInputMuted(bool),
    SetOutputMuted(bool),
    SendPcmFrame(Vec<f32>),
    SendTextMessage { target: ChatTarget, text: String },
    /// PHA-3289: a pre-escaped `name k=v k=v` command line.
    SendRawCommand(String),
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
                next_generation: 0,
            })),
            runtime,
        })
    }

    /// Connect to a TeamSpeak 3 server and wait until the first `BookEvents` batch
    /// has been received (the server has accepted us and sent the initial state).
    ///
    /// PHA-4849: [channel] is the channel an invite asked us to land in — a
    /// `/`-separated path of names (`Movies/Lobby`) or a numeric channel id
    /// (`42`). [channel_password] is that channel's password, and
    /// [privilege_key] is a token from the server's admin, spent once right
    /// after the session is up.
    ///
    /// The channel password is hashed by tsclientlib before it reaches the
    /// wire (`base64(sha1(password))`, TeamSpeak's own encoding) — the plain
    /// text never leaves the device, and neither password is ever logged.
    #[allow(clippy::too_many_arguments)]
    pub fn connect(
        &self,
        address: String,
        port: u16,
        nickname: String,
        identity_pem: String,
        password: Option<String>,
        channel: Option<String>,
        channel_password: Option<String>,
        privilege_key: Option<String>,
    ) -> Result<(), PlntError> {
        let id = parse_identity(&identity_pem)?;
        // PHA-4849: a privilege key is checked before the connection is made,
        // so a malformed one fails here instead of after a successful login.
        let privilege_key = privilege_key.as_deref().map(validate_privilege_key).transpose()?;

        // PHA-4849: port 0 means "no port was given". The address then goes
        // to the resolver as it is, so the server's own DNS entry decides:
        // the `_ts3._udp.` SRV record, or TSDNS. Forcing 9987 there breaks
        // any server whose SRV record names a different port — and a
        // nickname, whose address and port come from myTeamSpeak.
        let address_str = if port == 0 {
            address.clone()
        } else {
            format!("{address}:{port}")
        };
        let mut opts = Connection::build(address_str)
            .identity(id)
            .name(nickname);
        if let Some(pwd) = password.as_deref().filter(|p| !p.is_empty()) {
            opts = opts.password(pwd.to_string());
        }
        // PHA-4849: an invite names a channel to land in. `default_channel`
        // takes a path of channel names or `/<id>`; the server ignores it if
        // the channel does not exist or the password is wrong, and leaves us
        // in the default channel.
        if let Some(path) = channel.as_deref().and_then(normalize_channel_path) {
            opts = opts.channel(path);
        }
        if let Some(pwd) = channel_password.as_deref().filter(|p| !p.is_empty()) {
            opts = opts.channel_password(pwd.to_string());
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

        let (cmd_tx, cmd_rx) = std::sync::mpsc::sync_channel::<ControlCommand>(CMD_QUEUE_CAP);

        let sink_for_task = lock(&self.sink)
            .as_ref()
            .cloned()
            .expect("sink installed at construction");

        // PHA-4849: spend the privilege key. The server answers with an
        // `error` command carrying `return_code=token` (id=0 means the key
        // was accepted) — that reaches the app through the raw listener as a
        // `RawCommand`, so the user learns whether it worked.
        if let Some(key) = privilege_key.as_deref() {
            let cmd = build_privilege_key_use(key);
            if let Err(e) = cmd.send(&mut con) {
                sink_for_task.on_event(ConnEvent::Error(format!("privilegekeyuse: {e}")));
            }
        }
        install_raw_listener(&mut con, Arc::clone(&sink_for_task));
        let state_for_task = Arc::clone(&self.state);
        let initial_channel_tree = channel_tree.clone();
        let sink_for_connected = Arc::clone(&sink_for_task);

        // Publish state before spawning the loop: the loop's first act is to
        // check `connection.is_some()` and bail if it isn't set yet.
        // A second connect() replaces the handle; the previous loop sees it no
        // longer owns `connection` and shuts its socket down on its own.
        let generation = {
            let mut guard = lock(&self.state);
            guard.next_generation = guard.next_generation.wrapping_add(1);
            let generation = guard.next_generation;
            guard.own_client_id = own_client_id;
            guard.server_name = server_name.clone();
            guard.channel_tree = channel_tree.clone();
            guard.connection = Some(ConnectionHandle { cmd_tx, generation });
            generation
        };

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

        // PHA-4176: tokio swallows a panic in a spawned task, so a panicking
        // loop just stopped — no pongs, no `Disconnected`, server drops us on
        // ping timeout while the UI still says connected. Catch it and say so.
        install_panic_hook();
        let panic_sink = Arc::clone(&sink_for_task);
        let panic_state = Arc::clone(&self.state);
        self.runtime.spawn(async move {
            let res = std::panic::AssertUnwindSafe(run_connection_loop(
                con,
                cmd_rx,
                sink_for_task,
                state_for_task,
                initial_channel_tree,
                initial_tree_signature,
                initial_client_signature,
                generation,
            ))
            .catch_unwind()
            .await;
            if let Err(payload) = res {
                let msg = LAST_PANIC
                    .lock()
                    .ok()
                    .and_then(|mut g| g.take())
                    .or_else(|| payload.downcast_ref::<&str>().map(|s| s.to_string()))
                    .or_else(|| payload.downcast_ref::<String>().cloned())
                    .unwrap_or_else(|| "unknown panic".into());
                {
                    let mut g = lock(&panic_state);
                    if owns_connection(&g, generation) {
                        g.connection = None;
                    }
                }
                let _ = std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| {
                    panic_sink.on_event(ConnEvent::Error(format!("diag PANIC {msg}")));
                    panic_sink.on_event(ConnEvent::Disconnected {
                        cause: DisconnectCause::ConnectionLost,
                        reason: "core panic".into(),
                    });
                }));
            }
        });

        Ok(())
    }

    /// Disconnect cleanly. Drops the command channel; the background task exits.
    pub fn disconnect(&self) -> Result<(), PlntError> {
        let cmd_tx = {
            let mut guard = lock(&self.state);
            let handle = guard
                .connection
                .take()
                .ok_or_else(|| PlntError::NotConnected)?;
            handle.cmd_tx
        };
        // If the queue is full the Disconnect is lost, but dropping `cmd_tx`
        // here closes the channel and the loop treats that as a disconnect too.
        let _ = cmd_tx.try_send(ControlCommand::Disconnect);
        Ok(())
    }

    /// Move the own client to a channel by id. The channel's current name is
    /// resolved from the cached tree.
    ///
    /// PHA-4849: [password] is the channel's password, for a channel that has
    /// one. It used to be dropped on the floor here (`_password` and
    /// `password: None`), so a password-protected channel could never be
    /// entered — the request went out without a password and the server
    /// refused the move. The password is hashed before it reaches the wire
    /// (see [build_client_move]) and is never logged.
    pub fn join_channel(
        &self,
        channel_id: u64,
        password: Option<String>,
    ) -> Result<(), PlntError> {
        // Validate the id exists before sending the command.
        {
            let guard = lock(&self.state);
            if !guard.channel_tree.contains_key(&channel_id) {
                return Err(PlntError::Invalid(format!(
                    "unknown channel id {channel_id}"
                )));
            }
        }
        // An empty password is no password: TeamSpeak reads `cpw` as absent
        // rather than as an empty credential.
        self.send_cmd(Self::join_command(channel_id, password))
    }

    /// PHA-4849: spend a privilege key — a token the server's admin issued —
    /// on the live connection. A key works once; the server's answer is an
    /// `error` command carrying `return_code=token` (id=0 means it was
    /// accepted), which reaches the app as a `RawCommand`.
    pub fn use_privilege_key(&self, token: String) -> Result<(), PlntError> {
        let token = validate_privilege_key(&token)?;
        self.send_cmd(ControlCommand::UsePrivilegeKey(token))
    }

    /// Send a text message to the current channel or to a specific client (a
    /// private message). Server-wide chat is out of scope for v1 (#3281).
    pub fn send_text_message(&self, target: ChatTarget, text: String) -> Result<(), PlntError> {
        if text.trim().is_empty() {
            return Err(PlntError::Invalid("message text is empty".into()));
        }
        self.send_cmd(ControlCommand::SendTextMessage { target, text })
    }

    /// PHA-3289: send a command tsclientlib has no message type for — the TS6
    /// screen-share signalling verbs (`requeststreaminfo`, `joinstreamrequest`,
    /// `streamsignaling`, ...). `args` values are escaped here; pass them raw.
    /// Replies come back as [`ConnEvent::RawCommand`]; include a `return_code`
    /// argument to correlate the server's `error` reply with the request.
    pub fn send_raw_command(
        &self,
        name: String,
        args: HashMap<String, String>,
    ) -> Result<(), PlntError> {
        let name = name.trim();
        if name.is_empty() || !name.chars().all(|c| c.is_ascii_alphanumeric() || c == '_') {
            return Err(PlntError::Invalid(format!("bad command name {name:?}")));
        }
        let mut line = String::from(name);
        let mut keys: Vec<&String> = args.keys().collect();
        keys.sort();
        for k in keys {
            if k.is_empty() || k.contains(|c: char| c == ' ' || c == '=' || c == '|') {
                return Err(PlntError::Invalid(format!("bad argument key {k:?}")));
            }
            line.push(' ');
            line.push_str(k);
            line.push('=');
            line.push_str(&ts_escape(&args[k]));
        }
        // tsproto's command packet cap; the TS6 streamer's offer answer is
        // ~4-5 KB so this leaves headroom while refusing runaway payloads.
        if line.len() > 8000 {
            return Err(PlntError::Invalid(format!("command is {} bytes, limit 8000", line.len())));
        }
        self.send_cmd(ControlCommand::SendRawCommand(line))
    }

    pub fn set_input_muted(&self, muted: bool) -> Result<(), PlntError> {
        self.send_cmd(ControlCommand::SetInputMuted(muted))
    }

    pub fn set_output_muted(&self, muted: bool) -> Result<(), PlntError> {
        self.send_cmd(ControlCommand::SetOutputMuted(muted))
    }

    /// Encode a 960-sample mono f32 frame (20 ms @ 48 kHz) as Opus and send it.
    pub fn send_pcm_frame(&self, frame: Vec<f32>) -> Result<(), PlntError> {
        if frame.len() != PCM_FRAME_SAMPLES {
            return Err(PlntError::Invalid(format!(
                "expected {PCM_FRAME_SAMPLES} samples, got {}",
                frame.len()
            )));
        }
        self.send_cmd(ControlCommand::SendPcmFrame(frame))
    }
}

impl Client {
    /// The command a channel join turns into.
    ///
    /// Split out of [`Client::join_channel`] so the trip from the API
    /// boundary to the wire can be asserted in a test: PHA-4849's bug was
    /// exactly this value being dropped between the two.
    fn join_command(channel_id: u64, password: Option<String>) -> ControlCommand {
        // An empty password is no password: TeamSpeak reads `cpw` as absent
        // rather than as an empty credential. A password that is *not* empty
        // is passed through byte for byte — TeamSpeak passwords may
        // legitimately begin or end with a space, and trimming one would
        // turn a right answer into "invalid password".
        let password = password.filter(|p| !p.is_empty());
        ControlCommand::JoinChannel { channel_id, password }
    }

    /// Queue a command for the connection loop without ever blocking the
    /// caller (often Kotlin's main or audio thread). A full queue means the
    /// loop is behind: PCM is dropped silently, anything else is an error.
    fn send_cmd(&self, cmd: ControlCommand) -> Result<(), PlntError> {
        let cmd_tx = {
            let guard = lock(&self.state);
            match guard.connection.as_ref() {
                Some(h) => h.cmd_tx.clone(),
                None => return Err(PlntError::NotConnected),
            }
        };
        let is_pcm = matches!(cmd, ControlCommand::SendPcmFrame(_));
        match cmd_tx.try_send(cmd) {
            Ok(()) => Ok(()),
            Err(std::sync::mpsc::TrySendError::Full(_)) if is_pcm => Ok(()),
            Err(std::sync::mpsc::TrySendError::Full(_)) => {
                Err(PlntError::Connection("command queue full".into()))
            }
            Err(std::sync::mpsc::TrySendError::Disconnected(_)) => Err(PlntError::NotConnected),
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
        export_identity(&self.inner)
    }

    /// Current security level (8 by default).
    pub fn level(&self) -> u8 {
        self.inner.level()
    }
}

/// PHA-4849: the identity's security level — the hash-cash level TeamSpeak
/// checks. Level 8 is what a fresh identity gets; servers ask for more.
#[uniffi::export]
pub fn identity_level(identity: String) -> Result<u8, PlntError> {
    Ok(parse_identity(&identity)?.level())
}

/// PHA-4849: raise the identity's security level to at least `target`.
///
/// TeamSpeak's security level is a hash-cash property of the identity: the
/// level is the number of leading zero bits in `sha1(public key + counter)`.
/// Raising it means finding a counter whose hash has more leading zeros —
/// pure CPU work, no server involved, and the identity's key does not change.
///
/// This is slow by design: level 16 takes a moment, level 20 takes minutes,
/// level 24 takes hours. The caller runs it off the UI thread and can stop by
/// simply not asking for a higher level again.
///
/// Returns the new export string — the counter moved, so the export changed
/// and the caller has to persist it or the work is lost.
#[uniffi::export]
pub fn upgrade_identity_level(identity: String, target: u8) -> Result<String, PlntError> {
    if target == 0 || target > 30 {
        return Err(PlntError::Invalid(format!(
            "security level target {target} is out of range (1..=30)"
        )));
    }
    let mut id = parse_identity(&identity)?;
    if id.level() < target {
        id.upgrade_level(target);
    }
    Ok(export_identity(&id))
}

/// The portable export form of an identity: `"<counter>V<base64-key>"`.
fn export_identity(id: &Identity) -> String {
    format!(
        "{}V{}",
        id.counter(),
        BASE64_STANDARD.encode(id.key().to_short()),
    )
}

/// Import from a TeamSpeak identity `.ini` file's contents (PHA-4116).
///
/// This is the path for a user who exports an identity from the official client
/// on another machine. It is entirely offline — no myTeamSpeak account, no
/// network, no official-client secret.
///
/// Takes the file's *text*, not a path: on Android the user picks the file
/// through the Storage Access Framework, so the caller reads the content URI and
/// hands us the string.
///
/// Returns the portable identity string to persist — the same form
/// [`IdentityObj::export`] writes and `Client::connect` accepts.
#[uniffi::export]
pub fn import_identity_ini(contents: String) -> Result<String, PlntError> {
    Ok(identity_file::import_from_ini(&contents)?.export())
}

/// Inspect a `.ini` without importing it — for the import UI to show what the
/// file actually holds (nickname, security level) before the user commits.
///
/// Returns a human-readable summary of public metadata only. It never includes
/// key material, so it is safe to log.
#[uniffi::export]
pub fn describe_identity_ini(contents: String) -> Result<String, PlntError> {
    Ok(identity_file::import_from_ini(&contents)?.describe())
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

/// Parse an identity string. The one parser behind both
/// [`IdentityObj::import`] and [`Client::connect`] — they used to differ, and a
/// value [`IdentityObj::export`] produced was not accepted by either (#3238).
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

/// The TS3 `clientmove` command that moves a client into a channel.
///
/// The channel password is **hashed** before it goes on the wire: TeamSpeak
/// expects `cpw = base64(sha1(password))`, never the password itself. A
/// client that sends the plain text is refused with "invalid password" even
/// when the password is right — which is what this code did before PHA-4849
/// (it put the raw password straight into `cpw`).
///
/// Pure on purpose — no connection, no side effects — so a test can assert
/// what actually goes on the wire without a server.
pub fn build_client_move(
    client_id: tsclientlib::ClientId,
    channel_id: u64,
    password: Option<&str>,
) -> OutCommand {
    let mut part = OutClientMovePart {
        client_id,
        channel_id: ChannelId(channel_id),
        channel_password: None,
    };
    if let Some(pwd) = password.filter(|p| !p.is_empty()) {
        // tsclientlib's own encoder: base64(sha1(password)).
        part = part.set_password(pwd);
    }
    OutClientMoveMessage::new(&mut std::iter::once(part))
}

/// The TS3 `privilegekeyuse` command.
///
/// `return_code=token` asks the server to tag its answer with
/// `return_code=token`, so the reply — an `error` command, where id=0 means
/// the key was accepted — can be told apart from other traffic.
pub fn build_privilege_key_use(token: &str) -> OutCommand {
    let mut cmd = OutPrivilegeKeyUseMessage::new(&mut std::iter::once(OutPrivilegeKeyUsePart {
        token: Cow::Borrowed(token),
    }));
    cmd.write_arg("return_code", &"token");
    cmd
}

/// Normalise a channel path from a link into the form TeamSpeak's
/// `client_default_channel` takes: a `/`-separated path of channel *names*
/// (`Movies/Lobby`).
///
/// A bare number is a channel *id*, and it is deliberately not turned into a
/// path: the server's `client_default_channel` only understands names, and
/// silently sending it an id leaves the client in the default channel with no
/// explanation. An id is used with `clientmove` instead — see
/// [`Client::join_channel`] — which takes the id directly. The app resolves
/// an id to a channel after the channel tree arrives and joins it that way.
///
/// Pure on purpose — no connection, no side effects — so a test can assert
/// what actually goes on the wire without a server.
pub fn normalize_channel_path(path: &str) -> Option<String> {
    let trimmed = path.trim().trim_start_matches('/').trim();
    if trimmed.is_empty() {
        return None;
    }
    if trimmed.bytes().all(|b| b.is_ascii_digit()) {
        // A channel id, not a name.
        return None;
    }
    let parts: Vec<&str> = trimmed
        .split('/')
        .map(|s| s.trim())
        .filter(|s| !s.is_empty())
        .collect();
    if parts.is_empty() {
        return None;
    }
    Some(parts.join("/"))
}

/// Check a privilege key before it goes on the wire.
///
/// The key is an opaque token the server's admin issued. We only check that
/// it is safe to put in a command — never what it says — and it is never
/// logged.
pub fn validate_privilege_key(key: &str) -> Result<String, PlntError> {
    let key = key.trim();
    if key.is_empty() {
        return Err(PlntError::Invalid("privilege key is empty".into()));
    }
    if key.len() > 4096 {
        return Err(PlntError::Invalid("privilege key is too long".into()));
    }
    if key.chars().any(|c| c.is_control() || c.is_whitespace()) {
        return Err(PlntError::Invalid(
            "privilege key contains whitespace or control characters".into(),
        ));
    }
    Ok(key.to_string())
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
    generation: u64,
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
    // Set on `DisconnectedTemporarily`, cleared on the `BookEvents` batch that
    // follows tsclientlib's internal reconnect — that batch is the earliest
    // point `con.get_state()` reflects the resumed session's (possibly new)
    // own_client id.
    let mut awaiting_resume = false;
    let mut reinstall_raw_listener = false;
    // Disconnect command or closed command channel: leave the outer loop.
    let mut exit_requested = false;
    // Playback is pulled on a 20 ms clock, the way tsclientlib's own
    // `fill_buffer` expects, not once per incoming packet: pulling per packet
    // drained queues at the wrong rate and kept finished queues alive, and a
    // finished queue panics on its next pull (PHA-4176).
    let mut next_audio_tick = std::time::Instant::now() + AUDIO_TICK;
    let mut mix_buf = vec![0f32; STEREO_FRAME_SAMPLES];
    // Rate limit for repeated core errors (send_audio while the link is down
    // would otherwise be 50 events a second).
    let mut last_error_report: Option<std::time::Instant> = None;
    let mut suppressed_errors: u64 = 0;
    let mut diag_audio_skipped: u64 = 0;
    // PHA-4176 diagnostics: reported through `ConnEvent::Error("diag ...")` so
    // no binding change is needed. Gap = time between two passes of this loop;
    // pongs only go out while it spins, so a multi-second gap is the stall.
    let diag_start = std::time::Instant::now();
    let mut diag_last_iter = diag_start;
    let mut diag_last_report = diag_start;
    let mut diag_max_gap_ms: u128 = 0;
    let mut diag_iters: u64 = 0;
    let mut diag_audio_ok: u64 = 0;
    let mut diag_audio_err: u64 = 0;
    let mut diag_audio_max_ms: u128 = 0;

    loop {
        {
            let now = std::time::Instant::now();
            let gap = now.duration_since(diag_last_iter).as_millis();
            diag_last_iter = now;
            diag_iters += 1;
            diag_max_gap_ms = diag_max_gap_ms.max(gap);
            if gap >= 500 {
                sink.on_event(ConnEvent::Error(format!(
                    "diag loop_gap_ms={gap} t={}s", now.duration_since(diag_start).as_secs()
                )));
            }
            if now.duration_since(diag_last_report).as_secs() >= 5 {
                diag_last_report = now;
                sink.on_event(ConnEvent::Error(format!(
                    "diag t={}s iters={diag_iters} max_gap_ms={diag_max_gap_ms} audio_ok={diag_audio_ok} audio_err={diag_audio_err} audio_max_ms={diag_audio_max_ms}",
                    now.duration_since(diag_start).as_secs()
                )));
                diag_max_gap_ms = 0;
            }
        }
        if reinstall_raw_listener {
            reinstall_raw_listener = false;
            install_raw_listener(&mut con, Arc::clone(&sink));
        }
        // Drain control commands first — `try_recv`, never `recv`. Wrapping the
        // blocking `recv()` in a `poll_fn` made this task park on a std channel
        // inside an async poll: the connection's own stream then never got
        // serviced, the server saw no acks, and it dropped us on resend timeout
        // about 25 s in while the UI still said "connected".
        for _ in 0..CMD_DRAIN_MAX {
            let cmd = match cmd_rx.try_recv() {
                Ok(v) => v,
                Err(std::sync::mpsc::TryRecvError::Empty) => break,
                Err(std::sync::mpsc::TryRecvError::Disconnected) => {
                    exit_requested = true;
                    break;
                }
            };
            match cmd {
                ControlCommand::Disconnect => {
                    exit_requested = true;
                    break;
                }
                ControlCommand::JoinChannel { channel_id, password } => {
                    let own_client = con.get_state().ok().map(|s| s.own_client);
                    if let Some(own) = own_client {
                        let cmd = build_client_move(own, channel_id, password.as_deref());
                        let _ = cmd.send(&mut con);
                    }
                }
                ControlCommand::UsePrivilegeKey(token) => {
                    let cmd = build_privilege_key_use(&token);
                    if let Err(e) = cmd.send(&mut con) {
                        sink.on_event(ConnEvent::Error(format!("privilegekeyuse: {e}")));
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
                ControlCommand::SendTextMessage { target, text } => {
                    if let Ok(state) = con.get_state() {
                        let msg_target = match target {
                            ChatTarget::Channel => tsclientlib::MessageTarget::Channel,
                            ChatTarget::Client { client_id } => {
                                tsclientlib::MessageTarget::Client(tsclientlib::ClientId(
                                    client_id as u16,
                                ))
                            }
                        };
                        let cmd = state.send_message(msg_target, &text);
                        let _ = cmd.send(&mut con);
                    }
                }
                ControlCommand::SendRawCommand(line) => {
                    let cmd = OutCommand::new(Direction::C2S, Flags::empty(), PacketType::Command, &line);
                    if let Err(e) = cmd.send(&mut con) {
                        sink.on_event(ConnEvent::Error(format!("send_raw_command: {e}")));
                    }
                }
                ControlCommand::SendPcmFrame(frame) => {
                    // Mid-reconnect there is no session to send to; every frame
                    // would fail. Drop them quietly and count them instead.
                    if awaiting_resume {
                        diag_audio_skipped += 1;
                        continue;
                    }
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
                            let t0 = std::time::Instant::now();
                            let res = con.send_audio(pkt);
                            diag_audio_max_ms = diag_audio_max_ms.max(t0.elapsed().as_millis());
                            if let Err(e) = res {
                                diag_audio_err += 1;
                                report_error(
                                    &*sink,
                                    &mut last_error_report,
                                    &mut suppressed_errors,
                                    format!("send_audio: {e}"),
                                );
                            } else {
                                diag_audio_ok += 1;
                            }
                        }
                        Err(e) => {
                            sink.on_event(ConnEvent::Error(format!("opus encode: {e:?}")));
                        }
                    }
                }
            }
        }
        if exit_requested || !owns_connection(&lock(&shared_state), generation) {
            break;
        }
        // Playback tick: one mixed 20 ms frame of everyone talking.
        let now = std::time::Instant::now();
        if now >= next_audio_tick {
            next_audio_tick += AUDIO_TICK;
            if now > next_audio_tick + AUDIO_TICK * 5 {
                // Fell well behind (stall, doze): resync instead of bursting.
                next_audio_tick = now + AUDIO_TICK;
            }
            if !audio.get_queues().is_empty() {
                pull_playback_frame(&mut audio, &mut mix_buf, &mut known_talkers, &*sink);
            }
        }
        // Wait for one event, but never longer than LOOP_WAIT so commands and
        // the playback tick keep moving. This parks the task properly instead
        // of spinning a core at 100% the way `now_or_never` + `yield_now` did.
        // `next()` is cancel-safe: a timeout drops no item.
        // Keep the two layers of Option apart: `Ok(None)` is "the stream
        // ended", `Err(_)` is just "nothing ready yet" — flattening them made a
        // connection the server had torn down look exactly like an idle one.
        let wait = LOOP_WAIT.min(next_audio_tick.saturating_duration_since(now));
        let ev = {
            use futures::StreamExt;
            tokio::time::timeout(wait, con.events().next()).await
        };
        let ev = match ev {
            Ok(Some(e)) => e,
            Ok(None) => {
                stream_ended = true;
                break;
            }
            Err(_) => continue,
        };
        match ev {
            Ok(StreamItem::Audio(audio_pkt)) => {
                let from_raw: u16 = match audio_pkt.data().data() {
                    AudioData::S2C { from, .. } => *from,
                    _ => continue,
                };
                let from_id = tsclientlib::ClientId(from_raw);
                // Only queue here; frames are pulled on the playback tick.
                if let Ok(Some(_id)) = audio.handle_packet(from_id, audio_pkt) {
                    if known_talkers.insert(from_id.0) {
                        sink.on_event(ConnEvent::TalkStatus { client_id: from_id.0 as u64, talking: true });
                    }
                }
            }
            Ok(StreamItem::BookEvents(events)) => {
                if let Ok(state) = con.get_state() {
                    if awaiting_resume {
                        awaiting_resume = false;
                        let resumed_own_client_id = state.own_client.0;
                        {
                            let mut guard = lock(&shared_state);
                            guard.own_client_id = resumed_own_client_id;
                        }
                        sink.on_event(ConnEvent::Resumed(ConnectionState {
                            own_client_id: resumed_own_client_id.into(),
                            server_name: state.server.name.clone(),
                        }));
                        // The resume rebuilt the tsproto client; the raw
                        // packet listener went with the old one. Re-hook it
                        // once `state` (which borrows `con`) is gone.
                        reinstall_raw_listener = true;
                    }
                    channel_tree = snapshot_channel_tree(&state);
                    {
                        let mut guard = lock(&shared_state);
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
                        // Server-wide chat and pokes are out of scope for v1
                        // (#3281) — only surface channel and private (client
                        // -to-client) messages.
                        if let tsclientlib::events::Event::Message { target, invoker, message } = &ev {
                            let chat_target = match target {
                                tsclientlib::MessageTarget::Channel => Some(ChatTarget::Channel),
                                tsclientlib::MessageTarget::Client(id) => {
                                    Some(ChatTarget::Client { client_id: id.0 as u64 })
                                }
                                tsclientlib::MessageTarget::Server
                                | tsclientlib::MessageTarget::Poke(_) => None,
                            };
                            if let Some(chat_target) = chat_target {
                                sink.on_event(ConnEvent::TextMessage {
                                    target: chat_target,
                                    from_client_id: invoker.id.0 as u64,
                                    from_name: invoker.name.clone(),
                                    text: message.clone(),
                                });
                            }
                        }
                    }
                }
            }
            Ok(StreamItem::NetworkStatsUpdated) => {}
            Ok(StreamItem::DisconnectedTemporarily(reason)) => {
                awaiting_resume = true;
                sink.on_event(ConnEvent::TemporaryDisconnect { reason: format!("{reason:?}") });
            }
            Ok(_) => {}
            Err(e) => {
                // While tsclientlib is reconnecting, an error means the
                // reconnect itself failed for good, and polling the finished
                // connect future again would panic. Treat it as a lost link.
                if awaiting_resume || con.get_state().is_err() {
                    sink.on_event(ConnEvent::Error(format!("reconnect failed: {e}")));
                    stream_ended = true;
                    break;
                }
                report_error(
                    &*sink,
                    &mut last_error_report,
                    &mut suppressed_errors,
                    format!("event error: {e}"),
                );
            }
        }
    }

    if !stream_ended {
        // `disconnect` only queues the packet; it goes out while the stream is
        // polled. Give it a moment so the server sees a clean leave rather
        // than timing us out.
        if con.disconnect(DisconnectOptions::new()).is_ok() {
            use futures::StreamExt;
            let _ = tokio::time::timeout(
                std::time::Duration::from_secs(2),
                con.events().for_each(|_| future::ready(())),
            )
            .await;
        }
    }
    // The cause is the load-bearing half here; the reason string is only for
    // logs and bug reports. `Requested` says the loop exited because the app
    // called `Client::disconnect()` — it does NOT say the *user* did, which is
    // the conflation #3283 is about. Only the Android service layer knows
    // whether that request came from a Disconnect tap or from `onDestroy()`
    // after Android reclaimed the service.
    let (cause, reason) = if stream_ended {
        (DisconnectCause::ConnectionLost, "connection lost")
    } else {
        (DisconnectCause::Requested, "client.disconnect")
    };
    if diag_audio_skipped > 0 {
        sink.on_event(ConnEvent::Error(format!("diag audio_skipped_while_reconnecting={diag_audio_skipped}")));
    }
    for cid in known_talkers.drain() {
        sink.on_event(ConnEvent::TalkStatus { client_id: cid as u64, talking: false });
    }
    // A newer connect() may already own the slot; leave it alone then, and
    // don't report a disconnect for a session the app has already replaced
    // (unless it was asked to disconnect or the link died, which the app
    // still needs to hear about).
    let still_owner = {
        let mut guard = lock(&shared_state);
        let owner = owns_connection(&guard, generation);
        if owner {
            guard.connection = None;
        }
        owner
    };
    if still_owner || stream_ended || exit_requested {
        sink.on_event(ConnEvent::Disconnected { cause, reason: reason.into() });
    }
}

/// Pull one 20 ms frame from every talker's queue, mix, downmix to mono and
/// hand it to Kotlin. Finished queues are removed by `fill_buffer_with_proc`
/// itself. A panic inside tsclientlib's queue code is contained: all queues
/// are reset (they refill from the next packets) instead of the connection
/// task dying.
fn pull_playback_frame(
    audio: &mut AudioHandler,
    mix_buf: &mut [f32],
    known_talkers: &mut std::collections::HashSet<u16>,
    sink: &dyn EventSink,
) {
    mix_buf.iter_mut().for_each(|s| *s = 0.0);
    let pulled = std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| {
        let mut any = false;
        let ended = audio.fill_buffer_with_proc(mix_buf, |_, pcm| any |= !pcm.is_empty());
        (any, ended)
    }));
    let ended: Vec<u16> = match pulled {
        Ok((any, ended)) => {
            if any {
                let mono: Vec<f32> = mix_buf
                    .chunks_exact(2)
                    .map(|lr| ((lr[0] + lr[1]) * 0.5).clamp(-1.0, 1.0))
                    .collect();
                sink.on_event(ConnEvent::PcmFrame { client_id: 0, samples: mono });
            }
            ended.into_iter().map(|id| id.0).collect()
        }
        Err(_) => {
            let msg = LAST_PANIC.lock().ok().and_then(|mut g| g.take());
            sink.on_event(ConnEvent::Error(format!(
                "diag audio_panic (queues reset) {}",
                msg.unwrap_or_default()
            )));
            audio.reset();
            known_talkers.iter().copied().collect()
        }
    };
    for cid in ended {
        if known_talkers.remove(&cid) {
            sink.on_event(ConnEvent::TalkStatus { client_id: cid as u64, talking: false });
        }
    }
}

/// Forward a core error, at most one per second; the rest are counted and the
/// count rides along on the next one that gets through.
fn report_error(
    sink: &dyn EventSink,
    last: &mut Option<std::time::Instant>,
    suppressed: &mut u64,
    msg: String,
) {
    let now = std::time::Instant::now();
    if last.map_or(true, |t| now.duration_since(t).as_secs() >= 1) {
        *last = Some(now);
        if *suppressed > 0 {
            sink.on_event(ConnEvent::Error(format!("{msg} (+{} similar suppressed)", *suppressed)));
            *suppressed = 0;
        } else {
            sink.on_event(ConnEvent::Error(msg));
        }
    } else {
        *suppressed += 1;
    }
}

// ---------------------------------------------------------------------------
// PHA-3289: raw TS6 command passthrough
// ---------------------------------------------------------------------------

/// Hook the tsproto packet stream so the TS6-only `notifystream*` family —
/// which tsclientlib's typed parser drops as unknown — reaches the app as
/// [`ConnEvent::RawCommand`]. Must be re-run after tsclientlib's internal
/// reconnect, which replaces the tsproto client.
fn install_raw_listener(con: &mut Connection, sink: Arc<dyn EventSink>) {
    let raw = match con.get_tsproto_client_mut() {
        Ok(r) => r,
        Err(e) => {
            sink.on_event(ConnEvent::Error(format!("raw listener: {e}")));
            return;
        }
    };
    raw.event_listeners.push(Box::new(move |ev: &RawEvent| {
        if let RawEvent::ReceivePacket(p) = ev {
            if p.header().packet_type().is_command() {
                let text = String::from_utf8_lossy(p.content());
                if let Some(ev) = raw_command_event(&text) {
                    sink.on_event(ev);
                }
            }
        }
    }));
}

/// Decide whether a raw inbound command is one the app wants, and parse it.
/// Only the first `|`-separated part is surfaced; every stream notification
/// seen in the wild is single-part.
fn raw_command_event(text: &str) -> Option<ConnEvent> {
    let name = text.split(|c| c == ' ' || c == '|').next().unwrap_or("");
    let wanted = name.starts_with("notifystream")
        || name == "notifyjoinstreamrequest"
        || name == "notifyrespondjoinstreamrequest"
        || name == "notifyremovedfromstream"
        || (name == "error" && text.contains(" return_code="));
    if !wanted {
        return None;
    }
    let (name, args) = parse_command(text);
    Some(ConnEvent::RawCommand { name, args })
}

/// Split `name k=v k=v` (first part only) into its name and unescaped args.
fn parse_command(raw: &str) -> (String, HashMap<String, String>) {
    let first_part = raw.split('|').next().unwrap_or("");
    let mut name = String::new();
    let mut args = HashMap::new();
    for (i, tok) in first_part.split(' ').enumerate() {
        if i == 0 && !tok.contains('=') {
            name = tok.to_string();
            continue;
        }
        if tok.is_empty() {
            continue;
        }
        let (k, v) = tok.split_once('=').unwrap_or((tok, ""));
        args.insert(k.to_string(), ts_unescape(v));
    }
    (name, args)
}

fn ts_unescape(v: &str) -> String {
    let mut o = String::with_capacity(v.len());
    let mut it = v.chars();
    while let Some(c) = it.next() {
        if c != '\\' {
            o.push(c);
            continue;
        }
        match it.next() {
            Some('s') => o.push(' '),
            Some('p') => o.push('|'),
            Some('/') => o.push('/'),
            Some('n') => o.push('\n'),
            Some('r') => o.push('\r'),
            Some('t') => o.push('\t'),
            Some('v') => o.push('\x0b'),
            Some('f') => o.push('\x0c'),
            Some('a') => o.push('\x07'),
            Some('b') => o.push('\x08'),
            Some(x) => o.push(x),
            None => {}
        }
    }
    o
}

fn ts_escape(v: &str) -> String {
    let mut o = String::with_capacity(v.len() + 8);
    for c in v.chars() {
        match c {
            '\\' => o.push_str("\\\\"),
            '/' => o.push_str("\\/"),
            ' ' => o.push_str("\\s"),
            '|' => o.push_str("\\p"),
            '\n' => o.push_str("\\n"),
            '\r' => o.push_str("\\r"),
            '\t' => o.push_str("\\t"),
            '\x0b' => o.push_str("\\v"),
            '\x0c' => o.push_str("\\f"),
            '\x07' => o.push_str("\\a"),
            '\x08' => o.push_str("\\b"),
            c => o.push(c),
        }
    }
    o
}

#[cfg(test)]
mod raw_command_tests {
    use super::*;

    #[test]
    fn escape_round_trips() {
        let s = "a b|c/d\\e\nf";
        assert_eq!(ts_unescape(&ts_escape(s)), s);
    }
    #[test]
    fn parses_stream_notification() {
        let (name, args) = parse_command(
            "notifystreaminfo return_code=60007 clid=11 id=0bc7 name=Default\\s-\\sFile\\sExplorer type=3 audio=0",
        );
        assert_eq!(name, "notifystreaminfo");
        assert_eq!(args["name"], "Default - File Explorer");
        assert_eq!(args["clid"], "11");
    }

    #[test]
    fn filters_to_stream_family() {
        assert!(raw_command_event("notifystreamstarted id=x clid=1").is_some());
        assert!(raw_command_event("notifyrespondjoinstreamrequest clid=1 id=x decision=1 offer=v=0").is_some());
        assert!(raw_command_event("error id=0 msg=ok return_code=60001").is_some());
        assert!(raw_command_event("error id=0 msg=ok").is_none());
        assert!(raw_command_event("notifytextmessage targetmode=2 msg=hi").is_none());
        assert!(raw_command_event("notifyclientmoved clid=1 ctid=2").is_none());
    }
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

// ---------------------------------------------------------------------------
// PHA-4849 — connection correctness
// ---------------------------------------------------------------------------

#[cfg(test)]
mod connection_correctness_tests {
    use super::*;

    /// The password must survive the trip from the API boundary to the
    /// command the connection loop turns into a packet. PHA-4849's bug was
    /// this value being dropped right here.
    #[test]
    fn the_join_command_keeps_the_password() {
        match Client::join_command(7, Some("hunter2".into())) {
            ControlCommand::JoinChannel { channel_id, password } => {
                assert_eq!(channel_id, 7);
                assert_eq!(password.as_deref(), Some("hunter2"));
            }
            other => panic!("expected a join command, got {other:?}"),
        }
    }

    /// An empty password is no password at all: the packet must not carry a
    /// `cpw` for it.
    #[test]
    fn an_empty_password_is_no_password() {
        match Client::join_command(7, Some(String::new())) {
            ControlCommand::JoinChannel { password, .. } => {
                assert!(password.is_none(), "empty password became {password:?}");
            }
            other => panic!("expected a join command, got {other:?}"),
        }
        match Client::join_command(7, None) {
            ControlCommand::JoinChannel { password, .. } => assert!(password.is_none()),
            other => panic!("expected a join command, got {other:?}"),
        }
    }

    /// A password is passed through byte for byte. TeamSpeak passwords may
    /// legitimately begin or end with a space, and trimming one would turn a
    /// right answer into "invalid password".
    #[test]
    fn a_password_is_never_altered() {
        for exact in ["  secret  ", " secret", "secret ", "s e c r e t", "1234"] {
            match Client::join_command(7, Some(exact.into())) {
                ControlCommand::JoinChannel { password, .. } => {
                    assert_eq!(password.as_deref(), Some(exact));
                }
                other => panic!("expected a join command, got {other:?}"),
            }
        }
    }
}
