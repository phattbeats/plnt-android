//! Host-side round trip for the identity surface. Run with `cargo test -p plnt-core`.
//!
//! PHA-3238: `export()` wrote serde JSON while `import()` and `Client::connect()`
//! parsed with `Identity::new_from_str`, which only accepts the TS3
//! `"<counter>V<base64>"` string or a bare base64 key — so a freshly created
//! identity came back as `Identity(IdentityCrypto(KeyDecodeError))` on the first
//! connect. These tests pin the round trip shut.

use plnt_core::IdentityObj;

#[test]
fn created_identity_round_trips_through_import() {
    let created = IdentityObj::create();
    let exported = created.export();

    let reimported = IdentityObj::import(exported.clone())
        .unwrap_or_else(|e| panic!("import rejected our own export {exported:?}: {e}"));

    assert_eq!(
        exported,
        reimported.export(),
        "export -> import -> export is not stable"
    );
    // The counter travels in the string, so the hash-cash level survives.
    assert_eq!(created.level(), reimported.level());
}

#[test]
fn export_uses_the_ts3_counter_v_key_format() {
    let exported = IdentityObj::create().export();
    let (counter, key) = exported
        .split_once('V')
        .unwrap_or_else(|| panic!("no 'V' separator in export: {exported:?}"));

    counter
        .parse::<u64>()
        .unwrap_or_else(|e| panic!("counter half {counter:?} is not a u64: {e}"));
    assert!(!key.is_empty(), "key half is empty: {exported:?}");
}

/// Identities persisted by a build that shipped the old JSON `export()` must
/// still load, otherwise upgrading the app silently resets the user's identity.
#[test]
fn legacy_json_export_still_imports() {
    let created = IdentityObj::create();
    let exported = created.export();
    let (counter, key) = exported.split_once('V').expect("ts3 form");
    let counter: u64 = counter.parse().expect("counter");

    // The shape tsproto's `Serialize for Identity` produced, which is what the
    // pre-fix `export()` handed to Kotlin and Kotlin persisted.
    let legacy = serde_json::json!({
        "key": key,
        "counter": counter,
        "max_counter": counter,
    })
    .to_string();

    let reimported = IdentityObj::import(legacy.clone())
        .unwrap_or_else(|e| panic!("legacy JSON identity rejected {legacy:?}: {e}"));
    assert_eq!(exported, reimported.export());
}

#[test]
fn empty_identity_is_a_clear_error_not_a_key_decode_error() {
    // Not `expect_err` — the Ok side is `Arc<IdentityObj>`, which is not `Debug`.
    let err = match IdentityObj::import(String::new()) {
        Ok(_) => panic!("empty string must not import"),
        Err(e) => e,
    };
    let msg = err.to_string();
    assert!(msg.contains("empty"), "unhelpful error for empty identity: {msg}");
}
