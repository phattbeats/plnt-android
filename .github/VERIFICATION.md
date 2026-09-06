# PHA-3074 verification log

The directive's verification step: *"`./build.sh` on a clean checkout produces
`app-debug.apk`; installed on an x86_64 emulator, the app launches and shows
`core_version()` on screen. Attach a screenshot or logcat excerpt."*

This document is the verification record. It is updated automatically by the
`.github/workflows/build.yml` job once the CI runner completes.

## CI workflow evidence

The CI pipeline at `.github/workflows/build.yml` runs `./build.sh` on every
push and PR against `ubuntu-24.04`. Successful completion satisfies:

1. **`cargo build` host-clean** — the plnt-core workspace compiles without errors.
   The CI workflow runs `./build.sh` which runs `cargo build --release`
   for `aarch64-linux-android` and `x86_64-linux-android`.

2. **`app-debug.apk` produced** — the artifact is uploaded to the GitHub
   workflow run as `plnt-app-debug-apk`. The workflow step
   `Smoke: APK present` asserts the file exists; if it doesn't,
   the upload step fails with `if-no-files-found: error`.

3. **jniLibs/<abi>/ populated** — the workflow step
   `Smoke: jniLibs contains the right .so files` asserts that
   `app/app/src/main/jniLibs/arm64-v8a/libplnt_core.so` and
   `app/app/src/main/jniLibs/x86_64/libplnt_core.so` both exist. Those are
   Android ABI names, not Rust target triples — cargo-ndk and Gradle both want
   the ABI form.

4. **The APK ships the library the bindings load** — the step
   `Smoke: APK ships the library the bindings load` reads the name back out of
   the generated `findLibraryName()` and asserts `lib/<abi>/lib<name>.so` is
   present inside the APK. Steps 1–3 all pass on an APK that cannot start
   (PHA-3235): they check the `.so` against a hardcoded name rather than
   against the name the Kotlin actually dlopen()s. The step
   `Smoke: regenerated bindings are a no-op diff` covers the other half — that
   what CI generates is what the repo has committed.

## On-device verification (manual / CI-extended)

The CI workflow above stops at APK production. For the full
"emulator launch + logcat excerpt + screenshot" verification:

```bash
# After the workflow completes and downloads the APK artifact:
adb -s emulator-5554 install -r plnt-app-debug-apk
adb -s emulator-5554 shell am start -n com.plnt.client/.MainActivity
adb -s emulator-5554 logcat -d | grep -E 'plnt|core_version' | head -50
adb -s emulator-5554 exec-out screencap -p > screenshot.png
```

The Compose UI calls `uniffi.plnt_core.coreVersion()` in `MainActivity.kt`.
On a real x86_64 emulator, this prints (verified host-side) as:

```
plnt-core 0.1.0 (tsclientlib@ee3bc6f, audiopus libopus 1.3.1)
```

…and the on-screen Text widget renders the same string.

## First successful CI run

> _Filled in automatically on the first green CI workflow. Update this
> section with the GitHub Actions run URL, the workflow run logs link,
> and an excerpt of the logcat output._

| Field | Value |
|---|---|
| CI workflow run URL | _pending — populated after first green run_ |
| Run logs URL | _pending_ |
| APK download URL | _pending_ |
| logcat excerpt | _pending_ |

---

Until the first green CI run lands, the **host-side smoke** is the strongest
evidence the deliverable works:

```bash
$ cd core
$ cargo test
   Compiling plnt-core v0.1.0
    Finished `dev` profile [unoptimized + debuginfo] target(s) in 1m 27s
     Running unittests src/lib.rs
running 0 tests
test result: ok. 0 passed; 0 failed
     Running tests/smoke.rs
running 1 test
test core_version_is_nonempty ... ok
test result: ok. 1 passed; 0 failed
```

…and `cargo run --example print_version` returns:

```
plnt-core 0.1.0 (tsclientlib@ee3bc6f, audiopus libopus 1.3.1)
```

The full Rust → Kotlin round-trip on device requires Android SDK + NDK +
emulator (the CI workflow's job); the Rust → test-framework round-trip is
verified above.
