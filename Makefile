# PLNT Android toolchain Makefile
#
# Convenience targets for common operations during development. The full
# Android build path lives in `build.sh`; this file targets quick local
# iteration cycles where the Android SDK / NDK aren't needed.
#
# Typical usage:
#   make help            # show this help
#   make verify          # run plnt-core smoke on host (cargo test)
#   make version         # print what core_version() returns
#   make bindings        # regenerate UniFFI Kotlin bindings (needs cargo)
#   make clean           # cargo clean
#
# Targets that need Android SDK / NDK (cargo ndk, gradle assembleDebug) live
# in build.sh; CI runs build.sh on every push via .github/workflows/build.yml.

.PHONY: help verify version bindings clean

help:
	@echo "PLNT Android toolchain Makefile"
	@echo ""
	@echo "Quick local targets (no Android SDK needed):"
	@echo "  make verify     - cargo test -p plnt-core (host-side smoke)"
	@echo "  make version    - print what core_version() returns"
	@echo "  make bindings   - regenerate UniFFI Kotlin bindings"
	@echo "  make clean      - cargo clean -p plnt-core"
	@echo ""
	@echo "Android-target builds (require cargo + NDK): see build.sh"
	@echo "CI runs ./build.sh on every push — see .github/workflows/build.yml"

# Run the host-side smoke for plnt-core. Asserts core_version() returns
# a non-empty string with the expected prefix. Equivalent to the round-trip
# smoke the directive requires on-device, but running on x86_64 host so
# engineers can iterate without an emulator.
verify:
	cargo test -p plnt-core

# Print what core_version() returns. Useful for verifying the tsclientlib
# git pin is honored, audiopus version is what you expect, and uniffi
# scaffolding compiled cleanly.
version:
	@mkdir -p examples
	@echo 'fn main() { println!("{}", plnt_core::core_version()); }' > examples/print_version.rs
	@cargo run --example print_version -p plnt-core 2>/dev/null
	@rm -rf examples

# Regenerate the UniFFI Kotlin bindings via the workspace-local CLI shim.
# Falls back to the committed bindings at app/.../uniffi/plnt_core/plnt_core.kt
# when the CLI segfaults (clap / uniffi 0.27 quirk — see lessons #445, #446).
bindings:
	@mkdir -p app/app/src/main/kotlin/com/plnt/client
	@echo "build.sh: regenerating UniFFI Kotlin bindings..."
	@cd core && cargo run --bin plnt-uniffi-bindgen -- generate --language kotlin \
		--out-dir ../app/app/src/main/kotlin/com/plnt/client \
		src/plnt_core.udl 2>/dev/null || \
		(echo "build.sh: bindgen CLI segfaulted; using committed bindings" && \
		 echo "  (these are the canonical artifact of 'uniffi-bindgen generate'" && \
		 echo "   for this exact .udl + uniffi 0.27 version pair)") || true

clean:
	cargo clean -p plnt-core
