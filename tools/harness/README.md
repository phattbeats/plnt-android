# chlorophone-android-harness (PHATT-RAID)

A build and test runner for plnt-android that runs on PHATT-RAID (PHA-5006). The container has the same toolchain CI uses:

- JDK 17
- Android SDK: cmdline-tools 11076708, platform-tools, platforms 34 and 35, build-tools 34.0.0 and 35.0.0, NDK 27.0.12077973
- Rust stable with `aarch64-linux-android` and `x86_64-linux-android`, plus cargo-ndk 4.1.0
- libopus-dev
- Gradle 8.10.2

It runs with `--runtime nvidia` and also hosts an adb server.

## One command for agents

```bash
ssh -i /paperclip/.ssh/phatt_raid_key -o IdentitiesOnly=yes root@10.0.0.100 \
  /mnt/cache/appdata/chlorophone-android-harness/harness.sh ci origin/main
```

`ci REF` checks out REF, runs `./build.sh` (which produces the debug APK), then runs `testDebugUnitTest` and `cargo test -p plnt-core` on the host target. REF can be `origin/<branch>` or `origin/pr/<n>`. If a step fails, the command exits non-zero.

The APK ends up at `/mnt/cache/appdata/chlorophone-android-harness/work/plnt-android/app/app/build/outputs/apk/debug/app-debug.apk`.

Other subcommands: `build REF`, `test`, `gpu-check`, `adb ARGS...`, `shell`, `up`.

## Layout on RAID

Everything lives under `/mnt/cache/appdata/chlorophone-android-harness/`, on the cache pool rather than the array:

| Path | What |
|---|---|
| `harness.sh`, `Dockerfile`, `entrypoint.sh`, `10_nvidia.json` | Installed copy of this directory. `harness.sh up` builds the image from here. |
| `work/plnt-android` | Work tree, including `target/`, so incremental builds are fast. |
| `cache/gradle`, `cache/cargo-registry`, `cache/cargo-git` | Persistent caches. |
| `backups/` | TS3 volume snapshot and `docker inspect` output from before the 30033 republish. |

To install or update: copy `tools/harness/*` into that directory, then run `harness.sh up`.

Gradle note: the repo doesn't commit a Gradle wrapper. GitHub release downloads (where the Gradle dist is hosted) return 503/504 from RAID. Because of that, `harness.sh` writes an untracked `app/gradlew` shim that runs the image's `/opt/gradle` (8.10.2), and build.sh then skips its network bootstrap. If the repo ever commits a real wrapper, the harness uses that instead.

## adb

The adb server listens on **`10.0.0.100:5037`**, published on the LAN address only, never `0.0.0.0` or the tailnet. From the paperclip container or any LAN host:

```bash
ADB_SERVER_SOCKET=tcp:10.0.0.100:5037 adb devices
# or, without a local adb:
ssh root@10.0.0.100 /mnt/cache/appdata/chlorophone-android-harness/harness.sh adb devices
```

### Attaching a physical phone

**Wi-Fi adb (same LAN, Android 11+):**

1. On the phone, open Developer options → Wireless debugging → Pair device with pairing code.
2. Run `harness.sh adb pair <phone-ip>:<pair-port> <code>`.
3. Run `harness.sh adb connect <phone-ip>:<connect-port>`. The connect port is shown on the Wireless debugging screen and is different from the pairing port.

**Over Tailscale (phone off-LAN):** the phone joins the tailnet. Then run `harness.sh adb connect <phone-tailnet-ip>:<port>` using the Wireless debugging port. If you used `adb tcpip 5555` while the phone was on USB, use port 5555. RAID routes to tailnet addresses, so the container can reach the phone, but the adb server itself stays LAN-only.

Pairings are lost when the container is recreated. Re-pair after `harness.sh up`.

## GPU (for the emulator, PHA-5008)

The `--runtime nvidia` and `NVIDIA_DRIVER_CAPABILITIES=all` settings inject `nvidia-smi` and `libEGL_nvidia`. The image adds the glvnd vendor file `10_nvidia.json`, which the runtime doesn't provide, so EGL resolves to NVIDIA instead of Mesa. `harness.sh gpu-check` checks all of this.

The emulator itself also needs `/dev/kvm`, which RAID won't have until SVM is enabled in the BIOS (PHA-5007).

## TS3 test server

`chlorophone-ts3-test` (teamspeak:3.13) runs on RAID and publishes:

- 9987/udp (voice)
- 10011/tcp (query)
- 9988/tcp
- 30033/tcp (file transfer)

`harness.sh` sets the host suite's `PLNT_TS3_ADDR` to the TS3 container's bridge IP, because UDP from the harness to RAID's own published 9987 doesn't hairpin. Clients outside Docker use `10.0.0.100:9987`.
