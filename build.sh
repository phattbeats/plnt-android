#!/usr/bin/env bash
# build.sh — single-command toolchain for plnt-android
#
# Goal: on a clean checkout, `./build.sh` produces app-debug.apk with the
# Rust core (plnt-core) cross-compiled for aarch64-linux-android AND
# x86_64-linux-android, the .so files placed in jniLibs, the UniFFI
# Kotlin bindings generated, and a Gradle assembleDebug run.
#
# This script is the canonical entrypoint for the PHA-3074 deliverable.
# It is also the thing CI runs in a clean Ubuntu 24.04 container.

set -euo pipefail

# ---- config -----------------------------------------------------------------
NDK_VERSION="${NDK_VERSION:-27.0.12077973}"
ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-34}"
RUST_TARGETS="${RUST_TARGETS:-aarch64-linux-android x86_64-linux-android}"
TSCLIENTLIB_PIN="${TSCLIENTLIB_PIN:-ee3bc6f45a7137db7793ba5593a321df400d53e5}"

# Where to find the SDK / NDK / JDK.
# Honor $ANDROID_HOME / $ANDROID_SDK_ROOT / $JAVA_HOME if set, else default
# to the most common Linux locations.
ANDROID_HOME="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-/opt/android-sdk}}"
JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk-amd64}"

# ---- sanity checks ----------------------------------------------------------
need() { command -v "$1" >/dev/null || { echo "build.sh: missing dependency: $1" >&2; exit 1; }; }
need curl
need git
need tar
need unzip

# rustup + cargo (system rustc isn't enough — we need to add the Android targets).
if ! command -v rustup >/dev/null 2>&1; then
  echo "build.sh: installing rustup (no rustup on PATH)" >&2
  curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y --default-toolchain stable
  # shellcheck disable=SC1091
  source "$HOME/.cargo/env"
fi
need cargo
need rustc

# ---- Rust targets ------------------------------------------------------------
for tgt in $RUST_TARGETS; do
  if ! rustup target list --installed | grep -qx "$tgt"; then
    echo "build.sh: adding Rust target $tgt"
    rustup target add "$tgt"
  fi
done

# ---- cargo-ndk ---------------------------------------------------------------
# cargo-ndk is the cargo subcommand that ties the Android NDK linker into cargo.
# Install it pinned to a known-good version to keep builds reproducible.
CARGO_NDK_VERSION="${CARGO_NDK_VERSION:-4.1.0}"
if ! cargo install --list 2>/dev/null | grep -q "^cargo-ndk v${CARGO_NDK_VERSION}\b"; then
  echo "build.sh: installing cargo-ndk ${CARGO_NDK_VERSION}"
  cargo install --locked "cargo-ndk@${CARGO_NDK_VERSION}"
fi

# ---- Android SDK / NDK ------------------------------------------------------
if [[ ! -d "$ANDROID_HOME/cmdline-tools" ]]; then
  echo "build.sh: Android SDK not found at $ANDROID_HOME; install via cmdline-tools"
  # If ANDROID_HOME is missing entirely, install under $HOME for portability.
  if [[ ! -d "$ANDROID_HOME" ]]; then
    mkdir -p "$ANDROID_HOME"
  fi
  pushd "$ANDROID_HOME" >/dev/null
  curl -sSLo cmdline-tools.zip "https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_PLATFORM}_latest.zip"
  unzip -q cmdline-tools.zip
  mkdir -p cmdline-tools/latest
  mv cmdline-tools/bin cmdline-tools/lib cmdline-tools/NOTICE.txt cmdline-tools/source.properties cmdline-tools/latest/ 2>/dev/null || true
  rm cmdline-tools.zip
  popd >/dev/null
fi

export ANDROID_HOME
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

if [[ ! -d "$ANDROID_HOME/ndk/$NDK_VERSION" ]]; then
  echo "build.sh: installing NDK $NDK_VERSION"
  yes | sdkmanager --install "ndk;$NDK_VERSION"
fi

if [[ ! -d "$ANDROID_HOME/platforms/$ANDROID_PLATFORM" ]]; then
  echo "build.sh: installing platform $ANDROID_PLATFORM"
  yes | sdkmanager --install "platforms;$ANDROID_PLATFORM"
fi

if [[ ! -d "$ANDROID_HOME/build-tools/35.0.0" ]]; then
  echo "build.sh: installing build-tools 35.0.0"
  yes | sdkmanager --install "build-tools;35.0.0"
fi

# ---- Java / Gradle wrapper --------------------------------------------------
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

if [[ ! -x app/gradlew ]]; then
  echo "build.sh: bootstrapping gradle wrapper"
  pushd app >/dev/null
  gradle wrapper --gradle-version 8.10.2 --distribution-type bin || {
    echo "build.sh: gradle wrapper init failed — install gradle on PATH or run 'gradle wrapper' manually" >&2
    popd >/dev/null
    exit 1
  }
  popd >/dev/null
fi

# ---- Build the Rust core (cdylib) for both Android targets -----------------
echo "build.sh: cross-compiling plnt-core for: $RUST_TARGETS"
pushd core >/dev/null

# Generate the UniFFI Kotlin bindings once (host-side — no Android needed).
if ! command -v uniffi-bindgen >/dev/null 2>&1; then
  echo "build.sh: installing uniffi-bindgen-cli"
  cargo install --locked uniffi_bindgen-cli
fi
uniffi-bindgen generate src/plnt_core.udl --language kotlin --out-dir ../app/app/src/main/kotlin/com/plnt/client

# Cross-compile. cargo-ndk handles the linker glue; --target is per-arch.
for tgt in $RUST_TARGETS; do
  echo "build.sh: cargo-ndk build for $tgt"
  cargo ndk \
    --target "$tgt" \
    --platform "$ANDROID_PLATFORM" \
    --output-dir "../app/app/src/main/jniLibs/$tgt" \
    build --release
done

popd >/dev/null

# ---- Gradle assembleDebug ----------------------------------------------------
echo "build.sh: gradle assembleDebug"
pushd app >/dev/null
./gradlew --no-daemon assembleDebug
popd >/dev/null

# ---- Verify ------------------------------------------------------------------
APK="app/app/build/outputs/apk/debug/app-debug.apk"
if [[ -f "$APK" ]]; then
  echo "build.sh: success — APK at $APK ($(du -h "$APK" | cut -f1))"
else
  echo "build.sh: APK missing — check Gradle output above" >&2
  exit 1
fi
