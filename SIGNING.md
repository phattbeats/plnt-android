# Signing

This app is sideloaded, not distributed through Google Play, so "release
signing" here means one long-lived key that every future build must reuse.

## The rule that matters

**There is exactly one keystore. It is not in this repository, and it must
never be in this repository.**

Android identifies an app by its signing key. Change the key and every
existing install becomes a *different* app: users must uninstall before they
can upgrade, and they lose their stored identity, bookmarks and settings. A
lost keystore is not an inconvenience — it is a forced reinstall for everyone
who has the app.

## Where the keystore lives

Generated once on 2026-10-18 for PHA-3081.

| | |
|---|---|
| File | `plnt-release.p12` |
| Backup location | Nextcloud → `PLNT/signing/plnt-release.p12` |
| Password location | Nextcloud → `PLNT/signing/plnt-release-credentials.txt` |
| Type | PKCS12, alias `plnt` |
| Key | RSA 4096, `SHA256withRSA` |
| Valid | 2026-10-18 → 2056-10-10 (30 years) |
| Certificate SHA-256 | `2D:EF:B9:96:0F:C5:3A:83:11:6A:00:04:2F:A3:BB:77:BA:8C:3A:4E:06:CF:5A:8C:9B:3A:4B:D9:75:43:AD:F1` |

That Nextcloud folder is **private and has no public share link**. The APK
under `PLNT/android/` does have one; the signing folder deliberately does not.

**Known gap:** the password was meant to live in the Paperclip secrets store.
That store refuses my credentials (`403 Board access required` on both the
agent key and the board bearer on file), so the password sits beside the
keystore in the same private Nextcloud folder instead. That is a stopgap, not
a design. Once a working board key exists, move the password into the secrets
store and leave the keystore where it is. Keystore and password in one
folder is one compromised account away from one leaked credential.

## Verifying you have the right keystore

```bash
keytool -list -v -keystore plnt-release.p12 -storetype PKCS12
```

The certificate SHA-256 fingerprint above is the check. If it does not match,
stop — you are about to sign with the wrong key.

## Using it for a build

Passwords go in `~/.gradle/gradle.properties`, never in the repo and never on
a command line that lands in CI logs.

```properties
PLNT_STORE_FILE=/absolute/path/to/plnt-release.p12
PLNT_STORE_PASSWORD=...
PLNT_KEY_ALIAS=plnt
PLNT_KEY_PASSWORD=...
```

Then in `app/app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile     = file(providers.gradleProperty("PLNT_STORE_FILE").get())
            storePassword = providers.gradleProperty("PLNT_STORE_PASSWORD").get()
            keyAlias      = providers.gradleProperty("PLNT_KEY_ALIAS").get()
            keyPassword   = providers.gradleProperty("PLNT_KEY_PASSWORD").get()
        }
    }
}
```

`build.sh` currently produces `app-debug.apk` and does not sign with this key.
Wiring the release path is a separate change — see the `versionCode` note in
`CHANGELOG.md`, which is still 1.

## CI

A GitHub Actions runner has no copy of the keystore. Options, in order of
preference:

1. Build signed APKs on a machine that holds the keystore, and publish the
   APK as a release asset. This is what the project does today.
2. Put the keystore in the GitHub Actions secret store. That is a second copy
   of a 30-year credential on a third-party service, and it means a leak is
   one Actions-log mishap away. Prefer (1).

## Guard rails in this repo

`.gitignore` excludes `*.p12`, `*.jks`, `*.keystore`, `keystore.properties`
and `plnt-release-credentials.txt`.

That is a backstop, not permission — if a keystore is ever staged, the commit
is the incident, not the `.gitignore`. Verify with:

```bash
cp /path/to/plnt-release.p12 . && git status --porcelain   # must show nothing
```

The credentials file is listed explicitly rather than by a pattern like
`credentials.txt`, because a pattern that broad would silently hide a
legitimately-named file the day someone adds one.