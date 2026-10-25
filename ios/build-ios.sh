#!/usr/bin/env bash
# build-ios.sh — cross-compile plnt-core for iOS and generate the Swift bindings.
#
# macOS only (it needs Xcode's SDKs). CI runs it on a macOS runner; a human
# with a Mac runs it the same way.
#
# What it produces:
#
#   build/lib/iphoneos/libplnt_core.a         device slice (arm64)
#   build/lib/iphonesimulator/libplnt_core.a   simulator slice (arm64/x86_64)
#   PlntCoreFFI/plnt_core.swift                UniFFI Swift bindings (committed)
#   PlntCoreFFI/plnt_coreFFI.h                 the C header the bindings import
#   PlntCoreFFI/plnt_coreFFI.modulemap         the module map that makes
#                                              `import plnt_coreFFI` work
#
# Why Opus gets its own step: audiopus_sys builds the vendored libopus with
# CMake, and the cmake crate offers no way to pass a toolchain file — so it
# would compile a *host* (macOS) library and then try to link it into an iOS
# binary. We build libopus ourselves with an explicit iOS toolchain and point
# audiopus_sys at the result with OPUS_LIB_DIR.
#
# The Rust core is built as a static archive (crate-type "staticlib") and
# linked into the app binary. A dylib would have to be embedded and signed
# alongside the app, which is more moving parts than a sideloaded build needs.

set -euo pipefail

IOS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$IOS_DIR/.." && pwd)"
BUILD_DIR="$IOS_DIR/build"
BINDINGS_DIR="$IOS_DIR/PlntCoreFFI"

DEVICE_TARGET="${DEVICE_TARGET:-aarch64-apple-ios}"
SIM_TARGET="${SIM_TARGET:-aarch64-apple-ios-sim}"
DEPLOYMENT_TARGET="${IPHONEOS_DEPLOYMENT_TARGET:-16.0}"
# PLATFORM_NAME values, matching what Xcode expands $(PLATFORM_NAME) to.
DEVICE_PLATFORM="iphoneos"
SIM_PLATFORM="iphonesimulator"

need() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "build-ios.sh: missing dependency: $1" >&2
    exit 1
  }
}
need cargo
need rustc
need cmake
need xcrun
need git

# ---- rustup targets --------------------------------------------------------
if ! command -v rustup >/dev/null 2>&1; then
  echo "build-ios.sh: installing rustup (no rustup on PATH)" >&2
  curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y --default-toolchain stable
  # shellcheck disable=SC1091
  source "$HOME/.cargo/env"
fi
for target in "$DEVICE_TARGET" "$SIM_TARGET"; do
  if ! rustup target list --installed | grep -qx "$target"; then
    echo "build-ios.sh: adding Rust target $target"
    rustup target add "$target"
  fi
done

# ---- libopus ---------------------------------------------------------------
# The vendored Opus source ships inside the audiopus_sys crate, so its
# version is pinned by Cargo.lock rather than by a URL here.
OPUS_CRATE_DIR="$(find "${CARGO_HOME:-$HOME/.cargo}/registry/src" -maxdepth 2 -type d -name 'audiopus_sys-*' 2>/dev/null | head -n1)"
if [[ -z "$OPUS_CRATE_DIR" ]]; then
  echo "build-ios.sh: audiopus_sys source not in the cargo registry — run 'cargo fetch' first" >&2
  exit 1
fi
OPUS_SRC="$OPUS_CRATE_DIR/opus"

build_opus() {
  local target="$1" sdk="$2" arch="$3" prefix="$4"
  echo "build-ios.sh: building libopus for $target ($arch)"
  cmake -S "$OPUS_SRC" -B "$BUILD_DIR/opus-$target" \
    -DCMAKE_SYSTEM_NAME=iOS \
    -DCMAKE_OSX_SYSROOT="$sdk" \
    -DCMAKE_OSX_ARCHITECTURES="$arch" \
    -DCMAKE_OSX_DEPLOYMENT_TARGET="$DEPLOYMENT_TARGET" \
    -DCMAKE_INSTALL_PREFIX="$prefix" \
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_SHARED_LIBS=OFF \
    -DOPUS_BUILD_PROGRAMS=OFF \
    -DOPUS_BUILD_TESTING=OFF \
    -DOPUS_INSTALL_PKG_CONFIG_MODULE=OFF \
    > "$BUILD_DIR/opus-$target-configure.log" 2>&1
  cmake --build "$BUILD_DIR/opus-$target" --target install -j "$(sysctl -n hw.ncpu)" \
    >> "$BUILD_DIR/opus-$target-configure.log" 2>&1
}

DEVICE_SDK="$(xcrun --sdk iphoneos --show-sdk-path)"
SIM_SDK="$(xcrun --sdk iphonesimulator --show-sdk-path)"

mkdir -p "$BUILD_DIR"
build_opus "$DEVICE_TARGET" "$DEVICE_SDK" "arm64" "$BUILD_DIR/opus-$DEVICE_TARGET"
build_opus "$SIM_TARGET" "$SIM_SDK" "arm64" "$BUILD_DIR/opus-$SIM_TARGET"

# ---- Rust core -------------------------------------------------------------
# OPUS_NO_PKG stops audiopus_sys from finding a *host* libopus through
# pkg-config; OPUS_LIB_DIR points it at the iOS build we just made.
export OPUS_NO_PKG=1
export LIBOPUS_STATIC=1

build_core() {
  local target="$1" platform="$2" opus_prefix="$3"
  echo "build-ios.sh: cross-compiling plnt-core for $target"
  export OPUS_LIB_DIR="$opus_prefix/lib"
  # SDKROOT is what rustc and clang use to find the Apple SDK for the
  # framework links the core carries (SystemConfiguration, Security).
  export SDKROOT="$DEVICE_SDK"
  if [[ "$target" == *sim* ]]; then
    export SDKROOT="$SIM_SDK"
  fi
  pushd "$REPO_ROOT/core" >/dev/null
  cargo build --release -p plnt-core --target "$target"
  popd >/dev/null
  mkdir -p "$BUILD_DIR/lib/$platform"
  cp "$REPO_ROOT/target/$target/release/libplnt_core.a" "$BUILD_DIR/lib/$platform/libplnt_core.a"
}

build_core "$DEVICE_TARGET" "$DEVICE_PLATFORM" "$BUILD_DIR/opus-$DEVICE_TARGET"
build_core "$SIM_TARGET" "$SIM_PLATFORM" "$BUILD_DIR/opus-$SIM_TARGET"

# ---- Swift bindings --------------------------------------------------------
# Library mode: the metadata uniffi embeds in the compiled cdylib is the
# source of truth for the Swift API. The device dylib carries the same
# metadata as the simulator one, so one read is enough.
echo "build-ios.sh: generating Swift bindings (library mode)"
pushd "$REPO_ROOT/core" >/dev/null
cargo run --release --manifest-path "$REPO_ROOT/Cargo.toml" \
  --target "$DEVICE_TARGET" --bin plnt-uniffi-bindgen -- generate \
  --language swift \
  --out-dir "$BINDINGS_DIR" \
  --library "$REPO_ROOT/target/$DEVICE_TARGET/release/libplnt_core.dylib"
popd >/dev/null

for file in plnt_core.swift plnt_coreFFI.h plnt_coreFFI.modulemap; do
  [[ -f "$BINDINGS_DIR/$file" ]] || {
    echo "build-ios.sh: ERROR — bindgen did not produce $file" >&2
    exit 1
  }
done

echo "build-ios.sh: done"
echo "  device:    $BUILD_DIR/lib/$DEVICE_PLATFORM/libplnt_core.a"
echo "  simulator: $BUILD_DIR/lib/$SIM_PLATFORM/libplnt_core.a"
echo "  bindings:  $BINDINGS_DIR"
