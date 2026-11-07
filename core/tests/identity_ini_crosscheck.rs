//! Cross-implementation check for the `.ini` identity importer (PHA-4116).
//!
//! The unit tests inside `identity_file.rs` use tsclientlib's `tsaudiobot`
//! fixture — the same library that consumes the parsed value is also where the
//! test data came from. That proves self-consistency, not that we read what
//! TeamSpeak actually writes.
//!
//! `fixtures/c-generated.ini` was produced by landave/TSIdentityTool, an
//! independent C/libtomcrypt implementation of the format. Round-tripping it
//! checks our `.ini` handling against a second implementation, and against real
//! export shape — it carries `id=` (not `name=`) and a trailing
//! `phonetic_nickname=` with an empty value, neither of which the hand-written
//! fixtures exercised.
//!
//! Committed rather than generated at test time: the generator needs a C
//! toolchain we do not want in the build, and a fixed file pins the exact bytes
//! that were cross-checked.

use plnt_core::import_from_ini;

#[test]
fn parses_an_ini_written_by_an_independent_implementation() {
    let ini = include_str!("fixtures/c-generated.ini");
    let imported = import_from_ini(ini).expect("should parse a TSIdentityTool-written export");
    assert_eq!(imported.nickname.as_deref(), Some("pha4116-crosscheck"));
}

#[test]
fn round_trips_the_c_generated_identity_through_export_and_import() {
    let ini = include_str!("fixtures/c-generated.ini");
    let imported = import_from_ini(ini).unwrap();

    // The exported string is what gets persisted and later handed to
    // `Client::connect`, so it must survive our own import unchanged.
    let exported = imported.export();
    let again = plnt_core::IdentityObj::import(exported.clone())
        .unwrap_or_else(|e| panic!("import rejected our own export: {e}"));
    assert_eq!(again.export(), exported, "export -> import -> export is not stable");
}

#[test]
fn recovers_the_security_level_the_c_implementation_reported() {
    // TSIdentityTool printed "Current security level: 8 (with counter=52)" for
    // this file. The counter rides inside the value and the level is derived
    // from it — so if we mis-parsed the counter or the key, the level would
    // not come out. This is the strongest single assertion in the file.
    let ini = include_str!("fixtures/c-generated.ini");
    let imported = import_from_ini(ini).unwrap();
    assert_eq!(
        imported.level(),
        8,
        "counter/key parse lost the hash-cash level the C impl reported"
    );
}

#[test]
fn tolerates_unknown_and_empty_valued_keys() {
    // `phonetic_nickname=` is present and empty. Treating any empty value as a
    // hard error would reject an entirely ordinary export.
    let ini = include_str!("fixtures/c-generated.ini");
    assert!(import_from_ini(ini).is_ok());
}

#[test]
fn describe_is_safe_to_show_on_a_real_export() {
    let ini = include_str!("fixtures/c-generated.ini");
    let imported = import_from_ini(ini).unwrap();
    let described = imported.describe();

    assert!(described.contains("pha4116-crosscheck"));

    // The obfuscated key blob must never appear in anything the app displays
    // or logs. Check both the full export and the raw key half.
    let key_half = imported
        .export()
        .split_once('V')
        .map(|(_, k)| k.to_string())
        .unwrap_or_default();
    assert!(!key_half.is_empty(), "export carried no key half");
    assert!(!described.contains(&key_half));
    assert!(!described.contains(&imported.export()));
}