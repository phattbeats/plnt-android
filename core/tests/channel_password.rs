//! PHA-4849: the channel password path, against a real TeamSpeak 3 server.
//!
//! This is the test that matters: everything else checks that the password
//! is *put into* the command, and this checks that the server *accepts* it.
//! A password-protected channel is the only thing that can tell a right
//! password from a wrong one, and a `cpw` that is not hashed the way
//! TeamSpeak expects looks exactly like a wrong password.
//!
//! Needs a server with a password-protected channel. Set:
//!
//!     PLNT_TS3_ADDR=host:9987
//!     PLNT_TS3_PROTECTED_CID=3
//!     PLNT_TS3_PROTECTED_PASSWORD=the-channel-password
//!
//! Without them the test prints a skip line and passes, so a plain
//! `cargo test` still works offline.
//!
//! To set one up:
//!
//!     docker run -d -p 9987:9987/udp -p 10011:10011/tcp \
//!         -e TS3SERVER_LICENSE=accept teamspeak:3.13
//!     # then, through the server query interface:
//!     #   login serveradmin <password from the container log>
//!     #   use sid=1
//!     #   channelcreate channel_name=Protected channel_password=<password>

use std::sync::{Arc, Mutex};
use std::time::{Duration, Instant};

use plnt_core::{Client, ConnEvent, EventSink, IdentityObj};

#[derive(Default)]
struct CapturingSink {
    events: Mutex<Vec<ConnEvent>>,
}

struct SinkHandle(Arc<CapturingSink>);

impl EventSink for SinkHandle {
    fn on_event(&self, ev: ConnEvent) {
        self.0.events.lock().unwrap().push(ev);
    }
}

impl CapturingSink {
    fn own_client_id(&self) -> Option<u64> {
        let events = self.events.lock().unwrap();
        events.iter().rev().find_map(|ev| match ev {
            ConnEvent::Connected(state) => Some(state.own_client_id),
            _ => None,
        })
    }

    /// The channel our own client is in, per the most recent roster
    /// snapshot. The roster is a snapshot, not a delta, so the last one
    /// wins.
    fn own_channel(&self) -> Option<u64> {        let events = self.events.lock().unwrap();
        let own = events.iter().rev().find_map(|ev| match ev {
            ConnEvent::Connected(state) => Some(state.own_client_id),
            _ => None,
        })?;
        let mut channel = None;
        for ev in events.iter() {
            if let ConnEvent::ClientList(clients) = ev {
                if let Some(me) = clients.iter().find(|c| c.id == own) {
                    channel = Some(me.channel_id);
                }
            }
        }
        channel
    }

    /// The server's answer to a privilege key: the `id` of the `error`
    /// command it tagged with `return_code=token`. id=0 means the key was
    /// accepted.
    fn privilege_key_answer(&self) -> Option<String> {
        let events = self.events.lock().unwrap();
        events.iter().rev().find_map(|ev| match ev {
            ConnEvent::RawCommand { name, args }
                if name == "error" && args.get("return_code").map(|v| v == "token").unwrap_or(false) =>
            {
                args.get("id").cloned()
            }
            _ => None,
        })
    }
}

fn server_addr() -> Option<(String, u16)> {
    let raw = std::env::var("PLNT_TS3_ADDR").ok()?;
    let (host, port) = raw.rsplit_once(':')?;
    Some((host.to_string(), port.parse().ok()?))
}

fn protected_channel() -> Option<u64> {
    std::env::var("PLNT_TS3_PROTECTED_CID")
        .ok()
        .and_then(|v| v.parse().ok())
}

fn protected_password() -> Option<String> {
    std::env::var("PLNT_TS3_PROTECTED_PASSWORD").ok()
}

/// The server container may still be starting; retry for up to 60 s.
fn connect(
    host: &str,
    port: u16,
    nick: &str,
    channel: Option<String>,
    channel_password: Option<String>,
) -> (Arc<Client>, Arc<CapturingSink>) {
    connect_with(host, port, nick, channel, channel_password, None)
}

/// As [connect], and able to spend a privilege key on the way in.
fn connect_with(
    host: &str,
    port: u16,
    nick: &str,
    channel: Option<String>,
    channel_password: Option<String>,
    privilege_key: Option<String>,
) -> (Arc<Client>, Arc<CapturingSink>) {
    let sink = Arc::new(CapturingSink::default());
    let client = Client::new(Box::new(SinkHandle(sink.clone())));
    let identity = IdentityObj::create().export();
    let deadline = Instant::now() + Duration::from_secs(60);
    loop {
        let result = client.connect(
            host.to_string(),
            port,
            nick.to_string(),
            identity.clone(),
            None,
            channel.clone(),
            channel_password.clone(),
            privilege_key.clone(),
        );
        match result {
            Ok(()) => return (client, sink),
            Err(e) if Instant::now() < deadline => {
                eprintln!("{nick}: connect failed ({e}), retrying");
                std::thread::sleep(Duration::from_secs(2));
            }
            Err(e) => panic!("{nick}: could not connect to {host}:{port}: {e}"),
        }
    }
}

/// The channel to connect into by name, as the invite gives it. Defaults to
/// the channel's id in TeamSpeak's `/<id>` form.
fn protected_channel_spec(cid: u64) -> String {
    std::env::var("PLNT_TS3_PROTECTED_CHANNEL").unwrap_or_else(|_| format!("/{cid}"))
}

/// Wait until the roster knows which channel we are in, or give up.
///
/// The first roster snapshot after a connection can predate our own entry —
/// the server sends the client list in a later book-event batch — so the
/// roster is waited for rather than read once.
fn wait_for_roster(sink: &CapturingSink, timeout: Duration) -> Option<u64> {
    let deadline = Instant::now() + timeout;
    loop {
        if let Some(channel) = sink.own_channel() {
            return Some(channel);
        }
        if Instant::now() >= deadline {
            return None;
        }
        std::thread::sleep(Duration::from_millis(200));
    }
}

/// Wait until the roster says we are in `cid`, or give up.
fn wait_for_channel(sink: &CapturingSink, cid: u64, timeout: Duration) -> Option<u64> {
    let deadline = Instant::now() + timeout;
    loop {
        if let Some(channel) = sink.own_channel() {
            if channel == cid {
                return Some(channel);
            }
        }
        if Instant::now() >= deadline {
            return sink.own_channel();
        }
        std::thread::sleep(Duration::from_millis(200));
    }
}

/// A privilege key is spent once, on the connection it comes with, and the
/// server says whether it took.
#[test]
fn a_privilege_key_is_accepted_by_the_server() {
    let (host, port) = match server_addr() {
        Some(v) => v,
        None => {
            eprintln!("skipping: PLNT_TS3_ADDR not set (no TeamSpeak server)");
            return;
        }
    };
    let key = match std::env::var("PLNT_TS3_PRIVILEGE_KEY") {
        Ok(v) if !v.is_empty() => v,
        _ => {
            eprintln!("skipping: PLNT_TS3_PRIVILEGE_KEY not set (no privilege key)");
            return;
        }
    };

    let (_client, sink) = connect_with(&host, port, "plnt-privkey-test", None, None, Some(key));

    let deadline = Instant::now() + Duration::from_secs(20);
    let mut answer = None;
    while Instant::now() < deadline {
        if let Some(id) = sink.privilege_key_answer() {
            answer = Some(id);
            break;
        }
        std::thread::sleep(Duration::from_millis(200));
    }
    let answer = answer.expect("the server never answered the privilege key");
    assert_eq!(answer, "0", "the server refused the privilege key (error id {answer})");
}

/// The headline: the password gets us into a protected channel, and the
/// absence of one does not.
#[test]
fn a_password_protected_channel_needs_its_password() {    let (host, port) = match server_addr() {
        Some(v) => v,
        None => {
            eprintln!("skipping: PLNT_TS3_ADDR not set (no TeamSpeak server)");
            return;
        }
    };
    let cid = match protected_channel() {
        Some(v) => v,
        None => {
            eprintln!("skipping: PLNT_TS3_PROTECTED_CID not set (no protected channel)");
            return;
        }
    };
    let password = match protected_password() {
        Some(v) => v,
        None => {
            eprintln!("skipping: PLNT_TS3_PROTECTED_PASSWORD not set");
            return;
        }
    };

    let (client, sink) = connect(&host, port, "plnt-pw-test", None, None);
    let start = wait_for_roster(&sink, Duration::from_secs(10))
        .expect("our channel after connecting");
    assert_ne!(start, cid, "the test starts in the protected channel");

    // Without the password the move is refused and the server leaves us
    // where we were.
    client.join_channel(cid, None).expect("join without a password");
    std::thread::sleep(Duration::from_secs(2));
    assert_ne!(
        sink.own_channel(),
        Some(cid),
        "a password-protected channel was entered without its password"
    );

    // With the password we get in. This is the assertion the whole of
    // PHA-4849 exists for.
    client
        .join_channel(cid, Some(password.clone()))
        .expect("join with the password");
    let landed = wait_for_channel(&sink, cid, Duration::from_secs(15));
    assert_eq!(
        landed,
        Some(cid),
        "the channel password did not get us into the protected channel (we are in {:?})",
        sink.own_channel()
    );

    let _ = client.disconnect();
}

/// The invited-channel path: connecting with a channel and its password
/// lands us there, without a second move.
#[test]
fn an_invited_channel_is_joined_at_connect_time() {
    let (host, port) = match server_addr() {
        Some(v) => v,
        None => {
            eprintln!("skipping: PLNT_TS3_ADDR not set (no TeamSpeak server)");
            return;
        }
    };
    let cid = match protected_channel() {
        Some(v) => v,
        None => {
            eprintln!("skipping: PLNT_TS3_PROTECTED_CID not set (no protected channel)");
            return;
        }
    };
    let password = match protected_password() {
        Some(v) => v,
        None => {
            eprintln!("skipping: PLNT_TS3_PROTECTED_PASSWORD not set");
            return;
        }
    };

    // The channel is named the way an invite names it.
    let (client, sink) = connect(
        &host,
        port,
        "plnt-invite-test",
        Some(protected_channel_spec(cid)),
        Some(password.clone()),
    );
    let landed = wait_for_channel(&sink, cid, Duration::from_secs(15));
    assert_eq!(
        landed,
        Some(cid),
        "connecting into an invited channel did not land there (we are in {:?})",
        sink.own_channel()
    );

    let _ = client.disconnect();
}

/// An invited channel whose password is wrong leaves us in the default
/// channel — the server does not say no out loud, it just does not move us.
/// That is exactly the case a user cannot explain, so it is worth pinning.
#[test]
fn an_invited_channel_with_the_wrong_password_does_not_move_us() {
    let (host, port) = match server_addr() {
        Some(v) => v,
        None => {
            eprintln!("skipping: PLNT_TS3_ADDR not set (no TeamSpeak server)");
            return;
        }
    };
    let cid = match protected_channel() {
        Some(v) => v,
        None => {
            eprintln!("skipping: PLNT_TS3_PROTECTED_CID not set (no protected channel)");
            return;
        }
    };

    let (_client, sink) = connect(
        &host,
        port,
        "plnt-wrongpw-test",
        Some(protected_channel_spec(cid)),
        Some("definitely-not-the-password".to_string()),
    );
    std::thread::sleep(Duration::from_secs(3));
    assert_ne!(
        sink.own_channel(),
        Some(cid),
        "a wrong password still got us into the protected channel"
    );
}
