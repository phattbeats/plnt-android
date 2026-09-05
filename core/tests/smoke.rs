//! Host-side smoke for `core_version()`. Run with `cargo test -p plnt-core`.

#[test]
fn core_version_is_nonempty() {
    let v = plnt_core::core_version();
    assert!(!v.is_empty(), "core_version returned empty string: {:?}", v);
    assert!(v.starts_with("plnt-core"), "unexpected prefix: {:?}", v);
}
