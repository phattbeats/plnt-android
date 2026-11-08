#!/usr/bin/env bash
# harness.sh — drive chlorophone-android-harness on PHATT-RAID (PHA-5006).
#
# Runs on the RAID host. Agents call it over SSH, e.g.
#   ssh root@10.0.0.100 /mnt/cache/appdata/chlorophone-android-harness/harness.sh ci origin/main
#
#   up            build the image (if needed) and (re)start the container
#   ci [REF]      checkout REF (default origin/main), ./build.sh, then the host suites
#   build [REF]   checkout REF and ./build.sh only
#   test          Gradle testDebugUnitTest + cargo test -p plnt-core (current checkout)
#   gpu-check     nvidia-smi + NVIDIA EGL vendor libs inside the container
#   adb ARGS...   run adb against the harness's server
#   shell         interactive bash in /work/plnt-android
set -euo pipefail

NAME=chlorophone-android-harness
ROOT=${HARNESS_ROOT:-/mnt/cache/appdata/$NAME}
REPO_URL=${REPO_URL:-https://github.com/phattbeats/plnt-android.git}
LAN_IP=${LAN_IP:-10.0.0.100}
# TS3 test server for core/tests/two_client_audio.rs.
PLNT_TS3_ADDR=${PLNT_TS3_ADDR:-$LAN_IP:9987}
SRC=$ROOT/work/plnt-android
# Image context: this script's own directory (installed copy of tools/harness),
# so `up` never depends on which ref the work tree has checked out.
HERE=$(cd "$(dirname "$(readlink -f "$0")")" && pwd)

in_c() { docker exec -i -w /work/plnt-android -e PLNT_TS3_ADDR="$PLNT_TS3_ADDR" "$NAME" bash -lc "$*"; }

ensure_src() {
  mkdir -p "$ROOT/work"
  [[ -d $SRC/.git ]] || git clone -q "$REPO_URL" "$SRC"
}

checkout() {
  local ref=${1:-origin/main}
  ensure_src
  git -C "$SRC" fetch -q origin '+refs/heads/*:refs/remotes/origin/*' '+refs/pull/*/head:refs/remotes/origin/pr/*'
  git -C "$SRC" checkout -q --force --detach "$ref"
  git -C "$SRC" submodule update -q --init --recursive
  echo "harness: at $(git -C "$SRC" rev-parse --short HEAD) ($ref)"
}

up() {
  ensure_src
  mkdir -p "$ROOT"/cache/{gradle,cargo-registry,cargo-git}
  docker build -q -t "$NAME:latest" "$HERE"
  docker rm -f "$NAME" >/dev/null 2>&1 || true
  # adb is published on the LAN address only, never 0.0.0.0.
  docker run -d --name "$NAME" --restart unless-stopped --runtime nvidia \
    -e NVIDIA_VISIBLE_DEVICES=all -e NVIDIA_DRIVER_CAPABILITIES=all \
    -p "$LAN_IP:5037:5037" \
    -v "$ROOT/work:/work" \
    -v "$ROOT/cache/gradle:/cache/gradle" \
    -v "$ROOT/cache/cargo-registry:/opt/cargo/registry" \
    -v "$ROOT/cache/cargo-git:/opt/cargo/git" \
    "$NAME:latest" >/dev/null
  echo "harness: $NAME up; adb at $LAN_IP:5037"
}

build() { checkout "${1:-origin/main}"; in_c ./build.sh; }

test_suites() {
  in_c 'cd app && ./gradlew --no-daemon testDebugUnitTest'
  in_c 'cargo test -p plnt-core --target x86_64-unknown-linux-gnu -- --nocapture'
}

gpu_check() {
  in_c 'nvidia-smi --query-gpu=name,driver_version,memory.used,memory.total --format=csv
        echo "--- EGL vendor ICDs"; ls /usr/share/glvnd/egl_vendor.d/ 2>/dev/null
        echo "--- NVIDIA EGL libs"; ls /usr/lib/x86_64-linux-gnu/ | grep -E "^libEGL_nvidia|^libGLX_nvidia|^libnvidia-eglcore" || { echo "missing NVIDIA EGL libs" >&2; exit 1; }'
}

cmd=${1:-help}; shift || true
case $cmd in
  up) up ;;
  ci) build "${1:-origin/main}"; test_suites ;;
  build) build "${1:-origin/main}" ;;
  test) test_suites ;;
  gpu-check) gpu_check ;;
  adb) docker exec -i "$NAME" adb "$@" ;;
  shell) docker exec -it -w /work/plnt-android "$NAME" bash ;;
  *) sed -n '2,14p' "$0"; exit 2 ;;
esac
