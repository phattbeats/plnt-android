#!/usr/bin/env python3
"""PHA-4856 (WS8): audit a built APK's merged manifest and BuildConfig.

    tools/audit_apk.py <apk> --release   # the release build: nothing debuggable
    tools/audit_apk.py <apk>             # any build: the checks that hold for both

Reads the merged manifest with the SDK's apkanalyzer (so library-contributed
components are included, not just ours) and fails on:

  * android:debuggable / BuildConfig.DEBUG true      (--release only)
  * android:usesCleartextTraffic="true"
  * backup on without both backup rule files
  * an exported component other than MainActivity with no permission guarding it
  * a dangerous permission not on the reviewed list below

Prints what it checked, so the CI log is the audit record.
"""
import os
import subprocess
import sys
import xml.etree.ElementTree as ET

A = "{http://schemas.android.com/apk/res/android}"

# Reviewed in WS8_SECURITY_REVIEW.md. A new one fails the audit until it is reviewed.
ALLOWED_PERMISSIONS = {
    "android.permission.RECORD_AUDIO",
    "android.permission.MODIFY_AUDIO_SETTINGS",
    "android.permission.BLUETOOTH",
    "android.permission.BLUETOOTH_CONNECT",
    "android.permission.FOREGROUND_SERVICE",
    "android.permission.FOREGROUND_SERVICE_MICROPHONE",
    "android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK",
    "android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION",
    "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS",
    "android.permission.INTERNET",
    "android.permission.POST_NOTIFICATIONS",
    "android.permission.ACCESS_NETWORK_STATE",
    "android.permission.WAKE_LOCK",
    "android.permission.ACCESS_WIFI_STATE",
    # androidx.core adds this signature-level permission for its own
    # unexported receivers on API 33+.
    "com.plnt.client.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
}
EXPORTED_OK = {"com.plnt.client.MainActivity"}


def apkanalyzer(*args):
    home = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
    exe = os.path.join(home, "cmdline-tools", "latest", "bin", "apkanalyzer") if home else "apkanalyzer"
    return subprocess.run([exe, *args], check=True, capture_output=True, text=True).stdout


def main():
    if len(sys.argv) < 2:
        sys.exit(__doc__)
    apk = sys.argv[1]
    release = "--release" in sys.argv[2:]
    problems = []

    manifest = ET.fromstring(apkanalyzer("manifest", "print", apk))
    app = manifest.find("application")
    pkg = manifest.get("package")

    debuggable = app.get(A + "debuggable", "false") == "true"
    print(f"debuggable: {debuggable}")
    if release and debuggable:
        problems.append("release APK is debuggable")

    cleartext = app.get(A + "usesCleartextTraffic")
    print(f"usesCleartextTraffic: {cleartext} (absent = platform default: off on API 28+)")
    if cleartext == "true":
        problems.append("usesCleartextTraffic=true")

    allow_backup = app.get(A + "allowBackup", "true") == "true"
    rules = app.get(A + "fullBackupContent"), app.get(A + "dataExtractionRules")
    print(f"allowBackup: {allow_backup}, fullBackupContent: {rules[0]}, dataExtractionRules: {rules[1]}")
    if allow_backup and not all(rules):
        problems.append("backup is on but a backup rules file is missing")

    for kind in ("activity", "activity-alias", "service", "receiver", "provider"):
        for c in app.findall(kind):
            name = c.get(A + "name")
            if name.startswith("."):
                name = pkg + name
            exported = c.get(A + "exported") == "true"
            guard = c.get(A + "permission")
            if not exported:
                continue
            print(f"exported {kind}: {name} permission={guard}")
            # Compose ui-tooling's preview activity ships in debug builds only.
            if not release and name == "androidx.compose.ui.tooling.PreviewActivity":
                continue
            if name not in EXPORTED_OK and not guard:
                problems.append(f"exported {kind} {name} has no permission")

    perms = {p.get(A + "name") for p in manifest.findall("uses-permission")}
    for p in sorted(perms):
        print(f"uses-permission: {p}")
    unreviewed = sorted(perms - ALLOWED_PERMISSIONS)
    if unreviewed:
        problems.append("unreviewed permissions: " + ", ".join(unreviewed))

    if release:
        # BuildConfig.DEBUG gates nothing security-relevant since WS8, but a
        # release build that says DEBUG=true is a misconfigured build.
        code = apkanalyzer("dex", "code", "--class", f"{pkg}.BuildConfig", apk)
        debug_true = "DEBUG:Z = true" in code
        print(f"BuildConfig.DEBUG true: {debug_true}")
        if debug_true:
            problems.append("release BuildConfig.DEBUG is true")

    if problems:
        print("\nAUDIT FAILED:\n  " + "\n  ".join(problems))
        sys.exit(1)
    print("\naudit passed")


if __name__ == "__main__":
    main()
