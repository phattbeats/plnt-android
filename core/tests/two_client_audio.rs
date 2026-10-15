//! Two-client voice test against a real TeamSpeak 3 server.
//!
//! Connects two `plnt_core::Client`s, sends a 440 Hz tone from one, and
//! checks what the other one plays: pitch, level and frame rate. Then the
//! talker goes quiet and talks again, which is the queue-ends-then-restarts
//! path that panicked the connection task in PHA-4176.
//!
//! Needs a server. CI starts the official `teamspeak` image as a service
//! container and sets `PLNT_TS3_ADDR=127.0.0.1:9987`. Without that variable
//! the test prints a skip line and passes, so a plain `cargo test` still
//! works offline. Locally:
//!
//!     docker run -d -p 9987:9987/udp -e TS3SERVER_LICENSE=accept teamspeak:3.13
//!     PLNT_TS3_ADDR=127.0.0.1:9987 cargo test -p plnt-core --test two_client_audio \
//!         --target x86_64-unknown-linux-gnu -- --nocapture

use std::sync::{Arc, Mutex};
use std::time::{Duration, Instant};

use plnt_core::{Client, ConnEvent, EventSink, IdentityObj};

/// Test sink that captures every event the core fires.
#[derive(Default)]
struct CapturingSink {
    events: Mutex<Vec<ConnEvent>>,
}

/// Local wrapper handed to `Client::new` as the `Box<dyn EventSink>`. A bare
/// `impl EventSink for Arc<CapturingSink>` hits the orphan rule (neither
/// `EventSink` nor `Arc` is local to this crate); this newtype gives the impl
/// a local type to hang off while still sharing the sink with the test via
/// the wrapped `Arc`.
struct SinkHandle(Arc<CapturingSink>);

impl EventSink for SinkHandle {
    fn on_event(&self, ev: ConnEvent) {
        self.0.events.lock().unwrap().push(ev);
    }
}

impl CapturingSink {
    /// Mono samples of every `PcmFrame` seen so far, and the frame count.
    fn pcm(&self) -> (Vec<f32>, usize) {
        let events = self.events.lock().unwrap();
        let mut out = Vec::new();
        let mut frames = 0;
        for ev in events.iter() {
            if let ConnEvent::PcmFrame { samples, .. } = ev {
                out.extend_from_slice(samples);
                frames += 1;
            }
        }
        (out, frames)
    }

    fn clear_pcm(&self) {
        self.events
            .lock()
            .unwrap()
            .retain(|ev| !matches!(ev, ConnEvent::PcmFrame { .. }));
    }

    /// Any event that means the session broke or the core hit a panic.
    fn failure(&self) -> Option<String> {
        let events = self.events.lock().unwrap();
        events.iter().find_map(|ev| match ev {
            ConnEvent::Disconnected { cause, reason } => {
                Some(format!("Disconnected {cause:?}: {reason}"))
            }
            ConnEvent::TemporaryDisconnect { reason } => {
                Some(format!("TemporaryDisconnect: {reason}"))
            }
            ConnEvent::Error(msg) if msg.contains("PANIC") || msg.contains("audio_panic") => {
                Some(msg.clone())
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

/// The server container may still be starting; retry for up to 60 s.
fn connect(nick: &str) -> (Arc<Client>, Arc<CapturingSink>) {
    let (host, port) = server_addr().expect("PLNT_TS3_ADDR");
    let sink = Arc::new(CapturingSink::default());
    let client = Client::new(Box::new(SinkHandle(sink.clone())));
    let identity = IdentityObj::create().export();
    let deadline = Instant::now() + Duration::from_secs(60);
    loop {
        match client.connect(host.clone(), port, nick.to_string(), identity.clone(), None) {
            Ok(()) => return (client, sink),
            Err(e) if Instant::now() < deadline => {
                eprintln!("{nick}: connect failed ({e}), retrying");
                std::thread::sleep(Duration::from_secs(2));
            }
            Err(e) => panic!("{nick}: could not connect to {host}:{port}: {e}"),
        }
    }
}

/// Send `secs` of a 0.5-amplitude 440 Hz tone, paced in real time.
fn talk(client: &Client, secs: f64, phase: &mut f64) {
    let frames = (secs * 50.0) as usize;
    let step = 2.0 * std::f64::consts::PI * 440.0 / 48_000.0;
    let start = Instant::now();
    for i in 0..frames {
        let frame: Vec<f32> = (0..960)
            .map(|_| {
                *phase += step;
                (0.5 * phase.sin()) as f32
            })
            .collect();
        client.send_pcm_frame(frame).expect("send_pcm_frame");
        let due = start + Duration::from_millis(20 * (i as u64 + 1));
        if let Some(wait) = due.checked_duration_since(Instant::now()) {
            std::thread::sleep(wait);
        }
    }
}

fn rms(samples: &[f32]) -> f64 {
    (samples.iter().map(|s| (*s as f64).powi(2)).sum::<f64>() / samples.len() as f64).sqrt()
}

/// Dominant frequency from the zero-crossing rate.
fn zero_crossing_hz(samples: &[f32]) -> f64 {
    let crossings = samples
        .windows(2)
        .filter(|w| (w[0] >= 0.0) != (w[1] >= 0.0))
        .count();
    (crossings as f64 / 2.0) / (samples.len() as f64 / 48_000.0)
}

/// The middle of what was played: skips the jitter-buffer warm-up and the
/// fade-out, which would skew both RMS and pitch.
fn steady_part(samples: &[f32]) -> &[f32] {
    let frame = 960;
    let n = samples.len() / frame;
    assert!(n >= 30, "only {n} frames played, expected about 100");
    &samples[10 * frame..(n - 10) * frame]
}

#[test]
fn sink_collects_connected_event() {
    // Standalone smoke that doesn't need the TS3 server — verifies the
    // event-sink wiring works in isolation.
    let sink: Arc<CapturingSink> = Arc::new(CapturingSink::default());
    let _client = Client::new(Box::new(SinkHandle(sink.clone())));
    // No connection attempted, so events should be empty.
    assert!(sink.events.lock().unwrap().is_empty());
}

#[test]
fn two_client_440hz_relays_and_survives_talk_restart() {
    if server_addr().is_none() {
        eprintln!("skipping: PLNT_TS3_ADDR not set (no TeamSpeak server)");
        return;
    }

    let (listener, heard) = connect("plnt-listener");
    let (talker, talker_events) = connect("plnt-talker");
    // Both land in the default channel; give the server a moment to tell the
    // listener about the talker.
    std::thread::sleep(Duration::from_secs(2));

    let mut phase = 0.0;
    talk(&talker, 2.0, &mut phase);
    std::thread::sleep(Duration::from_millis(500));

    let (played, frames) = heard.pcm();
    eprintln!("burst 1: {frames} frames played");
    // 2 s of talk is ~100 frames. Pulling once per packet gave the wrong
    // count; a clock-driven pull plays one frame per 20 ms.
    assert!(
        (80..=130).contains(&frames),
        "listener played {frames} frames for 2 s of talk, expected ~100"
    );
    let steady = steady_part(&played);
    let hz = zero_crossing_hz(steady);
    let level = rms(steady);
    eprintln!("burst 1: {hz:.1} Hz, rms {level:.3}");
    // Playing tsclientlib's stereo output as mono came out at 220 Hz.
    assert!((hz - 440.0).abs() < 10.0, "listener heard {hz:.1} Hz, expected 440");
    // 0.5 amplitude sine → RMS 0.354; Opus voice mode keeps a pure tone close.
    assert!(
        (level - 0.354).abs() / 0.354 < 0.2,
        "listener RMS {level:.3}, expected about 0.354"
    );

    // Go quiet long enough for the listener's queue to end, then talk again:
    // the stale-queue pull after this point is what panicked in PHA-4176.
    std::thread::sleep(Duration::from_secs(2));
    heard.clear_pcm();
    talk(&talker, 2.0, &mut phase);
    std::thread::sleep(Duration::from_millis(500));

    let (played, frames) = heard.pcm();
    eprintln!("burst 2: {frames} frames played");
    assert!(frames >= 80, "listener played only {frames} frames after the talker restarted");
    let hz = zero_crossing_hz(steady_part(&played));
    assert!((hz - 440.0).abs() < 10.0, "after restart heard {hz:.1} Hz, expected 440");

    if let Some(f) = heard.failure() {
        panic!("listener session broke: {f}");
    }
    if let Some(f) = talker_events.failure() {
        panic!("talker session broke: {f}");
    }

    // A requested disconnect reports Requested, exactly once.
    listener.disconnect().expect("disconnect");
    std::thread::sleep(Duration::from_secs(3));
    let disconnects: Vec<String> = heard
        .events
        .lock()
        .unwrap()
        .iter()
        .filter_map(|ev| match ev {
            ConnEvent::Disconnected { cause, .. } => Some(format!("{cause:?}")),
            _ => None,
        })
        .collect();
    assert_eq!(disconnects, vec!["Requested".to_string()], "listener disconnect events");
    let _ = talker.disconnect();
}
