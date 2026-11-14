//! PHA-4849 — connection correctness, end to end through the wire format.
//!
//! The bug this pins shut: `join_channel` took a password, threw it away
//! (`_password`, `password: None`), and the command that did go out put the
//! *plain* password into `cpw` instead of TeamSpeak's
//! `base64(sha1(password))`. So a password-protected channel could not be
//! entered no matter what the user typed.
//!
//! These tests assert the packet, not the intention: what actually goes on
//! the wire, and that the plain password never does.

use base64::engine::general_purpose::STANDARD as BASE64_STANDARD;
use base64::Engine as _;
use sha1::{Digest, Sha1};

/// TeamSpeak's own encoding of a password on the wire.
fn ts_password_hash(password: &str) -> String {
    BASE64_STANDARD.encode(Sha1::digest(password.as_bytes()))
}

/// TeamSpeak escapes these inside a command's values, so a base64 value that
/// happens to contain a `/` reaches the wire as `\/`.
fn ts_escape(s: &str) -> String {
    let mut o = String::with_capacity(s.len());
    for c in s.chars() {
        match c {
            '\\' => o.push_str("\\\\"),
            '/' => o.push_str("\\/"),
            ' ' => o.push_str("\\s"),
            '|' => o.push_str("\\p"),
            '\n' => o.push_str("\\n"),
            '\r' => o.push_str("\\r"),
            '\t' => o.push_str("\\t"),
            c => o.push(c),
        }
    }
    o
}

/// The command's payload as text.
fn payload(cmd: &tsproto_packets::packets::OutCommand) -> String {
    String::from_utf8_lossy(cmd.0.content()).to_string()
}

#[test]
fn the_channel_password_reaches_the_command_as_team_speaks_it() {
    let password = "hunter2";
    let cmd = plnt_core::build_client_move(tsclientlib::ClientId(42), 7, Some(password));
    let text = payload(&cmd);

    assert!(
        text.contains("clientmove"),
        "not a clientmove command: {text:?}"
    );
    assert!(
        text.contains(&format!("cpw={}", ts_escape(&ts_password_hash(password)))),
        "the hashed channel password is missing from the command: {text:?}"
    );
    assert!(
        text.contains("cid=7"),
        "the channel id is missing from the command: {text:?}"
    );
}

#[test]
fn the_plain_password_never_reaches_the_wire() {
    let password = "s3cr3t-passphrase";
    let cmd = plnt_core::build_client_move(tsclientlib::ClientId(42), 7, Some(password));
    let text = payload(&cmd);

    assert!(
        !text.contains(password),
        "the plain password is on the wire: {text:?}"
    );
}

/// A channel without a password must not carry an empty `cpw` — the server
/// reads an empty `cpw` as "a password was supplied, and it was wrong".
#[test]
fn a_channel_without_a_password_carries_no_cpw() {
    for none in [None, Some("")] {
        let cmd = plnt_core::build_client_move(tsclientlib::ClientId(42), 7, none);
        let text = payload(&cmd);
        assert!(
            !text.contains("cpw="),
            "an empty password produced a cpw argument: {text:?}"
        );
    }
}

/// The known-answer test: a fixed password must hash to a fixed value, so a
/// change in the encoding is caught here rather than as "invalid password"
/// against a real server. The expected value comes from an independent
/// implementation (sha1 + base64, computed outside this crate).
#[test]
fn the_password_encoding_has_a_known_answer() {
    assert_eq!(ts_password_hash("secret"), "5en6G6MezRroT3XKqkdPOmY/BfQ=");
    // The same value tsclientlib produces, cross-checked against the
    // independent implementation above.
    let cmd = plnt_core::build_client_move(tsclientlib::ClientId(1), 1, Some("secret"));
    assert!(payload(&cmd).contains("cpw=5en6G6MezRroT3XKqkdPOmY\\/BfQ="));
}

#[test]
fn the_privilege_key_is_sent_as_a_privilegekeyuse() {
    let cmd = plnt_core::build_privilege_key_use("P4U:abcdef");
    let text = payload(&cmd);

    assert!(text.contains("privilegekeyuse"), "wrong command: {text:?}");
    assert!(text.contains("token=P4U:abcdef"), "the token is missing: {text:?}");
    // The server tags its answer with this, so the reply can be matched.
    assert!(
        text.contains("return_code=token"),
        "the command does not ask for a tagged answer: {text:?}"
    );
}

#[test]
fn a_malformed_privilege_key_is_refused_before_it_is_sent() {
    for bad in ["", "   ", "has space", "has\ttab", "has\nnewline"] {
        assert!(
            plnt_core::validate_privilege_key(bad).is_err(),
            "a malformed privilege key was accepted: {bad:?}"
        );
    }
    assert_eq!(
        plnt_core::validate_privilege_key("  P4U:ok  ").unwrap(),
        "P4U:ok",
        "a good privilege key was refused"
    );
}

#[test]
fn a_channel_path_from_a_link_is_normalised() {
    // A path of channel names.
    assert_eq!(
        plnt_core::normalize_channel_path("Movies/Lobby").as_deref(),
        Some("Movies/Lobby")
    );
    // Leading and trailing slashes are noise.
    assert_eq!(
        plnt_core::normalize_channel_path("/Movies/Lobby/").as_deref(),
        Some("Movies/Lobby")
    );
    // A bare number is a channel id, not a name. The server's
    // `client_default_channel` only understands names, so sending it an id
    // would leave the client in the default channel with no explanation —
    // an id is used with `clientmove`, which takes it directly.
    assert_eq!(plnt_core::normalize_channel_path("42"), None);
    assert_eq!(plnt_core::normalize_channel_path("/42"), None);
    // Nothing to join.
    assert_eq!(plnt_core::normalize_channel_path("   "), None);
    assert_eq!(plnt_core::normalize_channel_path("/"), None);
}

#[test]
fn the_identity_security_level_can_be_raised() {
    let identity = plnt_core::IdentityObj::create().export();
    let before = plnt_core::identity_level(identity.clone()).expect("level");
    assert!(before >= 8, "a fresh identity is level {before}, expected at least 8");

    // 12 is a level a phone can reach in well under a second.
    let raised = plnt_core::upgrade_identity_level(identity.clone(), 12).expect("upgrade");
    let after = plnt_core::identity_level(raised.clone()).expect("level");
    assert!(after >= 12, "level is {after}, expected at least 12");

    // The raised identity must still be a usable identity: the key did not
    // change, only the counter.
    let reimported = plnt_core::IdentityObj::import(raised.clone()).expect("import");
    assert_eq!(reimported.export(), raised);
    assert!(reimported.level() >= 12);
}

#[test]
fn raising_to_a_level_already_reached_keeps_the_identity() {
    let identity = plnt_core::IdentityObj::create().export();
    let level = plnt_core::identity_level(identity.clone()).expect("level");
    let again = plnt_core::upgrade_identity_level(identity.clone(), level).expect("upgrade");
    assert_eq!(again, identity, "a no-op upgrade changed the identity");
}

#[test]
fn an_impossible_security_level_target_is_refused() {
    let identity = plnt_core::IdentityObj::create().export();
    assert!(plnt_core::upgrade_identity_level(identity.clone(), 0).is_err());
    assert!(plnt_core::upgrade_identity_level(identity, 200).is_err());
}
