//! PLNT core: Rust voice client + UniFFI surface to Kotlin.
//!
//! The goal of this crate is to keep all native interop in one place so
//! the Android side deals only with a generated Kotlin class and the .so
//! files cargo-ndk produces.
//!
//! Today the only public function is `core_version()`, which is a round-trip
//! smoke (Kotlin → Rust → String → Kotlin render on screen). The architecture
//! for connection/channel/push-to-talk lives in the spike's `voicespike.rs`
//! (`https://nextcloud.phatt.vip/s/2CstCCfpZgHyGws/...`) and will graduate
//! into this crate in subsequent PHA-3074 follow-up tickets.

/// Return the embedded core version. Round-trip smoke for the UniFFI binding.
pub fn core_version() -> String {
    format!(
        "plnt-core {} (tsclientlib@{}, audiopus {}, uniffi {})",
        env!("CARGO_PKG_VERSION"),
        // tsclientlib pinned commit (see Cargo.toml)
        "ee3bc6f",
        audiopus::version_str(),
        uniffi::VERSION,
    )
}

uniffi::include_scaffolding!("plnt_core");
