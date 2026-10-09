# One-tap invite links (PHA-4108)

A phone-only user should be able to join a TeamSpeak server from a single link
or QR code, without typing an address. PLNT both **opens** and **produces** the
standard TeamSpeak server link, so a link shared from a desktop client works
here and one shared from PLNT works on the desktop.

## The link

The format is TeamSpeak's own, unchanged since TS3:

```
ts3server://HOST?port=9987&password=..&channel=Movies/Lobby&channelpassword=..&addbookmark=Label
```

- Schemes accepted: `ts3server://` (canonical), plus `tsserver://` and
  `teamspeak://` aliases some sites emit, and a `teamspeak://connect?address=host:port`
  shape. See `ServerLink.SCHEMES`.
- All parameters are optional except the host. Missing `port` defaults to 9987.
- Parsing is pure JVM (`model/ServerLink.kt`, no `android.net.Uri`) so it is
  unit-tested on the host (`ServerLinkTest`).
- A produced link **never carries a nickname** — everyone picks their own.

## Opening one (receiver)

`AndroidManifest.xml` registers a `VIEW`/`BROWSABLE` intent filter for the three
schemes. A tap routes to `MainActivity`, which hands the URI to
`PlntViewModel.handleServerLink()`. That raises `InviteSheet` — showing where the
link leads and a "Save to my servers" toggle — **before any network call**. A
tapped link never silently dials out. `ServerLink.find()` tolerates a whole
pasted message, so "join me: ts3server://…" works too.

If the server is already saved, PLNT reuses that bookmark (keeping its id,
label and nickname) instead of creating a duplicate.

## Sharing one (sender)

The bookmark edit sheet has **Share invite link**, which opens `ShareInviteSheet`:
a scannable QR (zxing) plus the link text and the system share chooser. Hand the
QR to someone in the room or send the link over any chat app.

## Not yet / follow-ups

- **Channel auto-join:** the `channel=` path is parsed and shown, but PLNT does
  not yet move the user into it after connecting (core joins by channel id; a
  name/path → id match on the first channel tree is the follow-up).
- **https App Links:** these are custom-scheme links, not verified https links,
  so there is no web fallback page for someone without PLNT installed. A short
  landing page that offers the APK + the link is a possible follow-up.
