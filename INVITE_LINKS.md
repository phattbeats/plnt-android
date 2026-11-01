# One-tap invite links (PHA-4108, extended by PHA-4849)

A phone-only user should be able to join a TeamSpeak server from a single link
or QR code, without typing an address. PLNT both **opens** and **produces** the
standard TeamSpeak server link, so a link shared from a desktop client works
here and one shared from PLNT works on the desktop.

## The links

TeamSpeak has two link shapes, and PLNT reads both.

### The server link — TeamSpeak's own, unchanged since TS3

```
ts3server://HOST?port=9987&password=***&channel=Movies/Lobby&channelpassword=..&addbookmark=Label
```

- Schemes accepted: `ts3server://` (canonical), plus `tsserver://` and
  `teamspeak://` aliases some sites emit, and a `teamspeak://connect?address=host:port`
  shape. See `ServerLink.SCHEMES`.
- All parameters are optional except the host. Missing `port` defaults to 9987.
- Parsing is pure JVM (`model/ServerLink.kt`, no `android.net.Uri`) so it is
  unit-tested on the host (`ServerLinkTest`).
- A produced link **never carries a nickname** — everyone picks their own.

### TeamSpeak's own link host — `tmspk.gg`

The official TeamSpeak app's "Create Invite" produces `https://tmspk.gg/<code>`
links. `tmspk.gg` has two shapes, and PLNT reads both:

| Link | What it is |
|---|---|
| `tmspk.gg/s=host?port=9988&nickname=Lee` | The same as `teamspeak://host?…` — a server link. |
| `tmspk.gg/<code>` | An **invite**: a code that TeamSpeak's invite service resolves to a server (and sometimes a channel). |

- An invite code is looked up at TeamSpeak's own invite service
  (`invites.teamspeak.com/query/?id=…`), the same one the desktop client
  asks. The answer names the server, its name, and — when the invite is to a
  channel — the channel.
- An invite expires (30 days by default). After that the service no longer
  knows it and PLNT says so rather than failing quietly.
- An invite's address may carry no port. Then PLNT leaves the port off, so
  the server's own DNS entry (`_ts3._udp.` SRV, or TSDNS) says which host and
  port to use — forcing 9987 there breaks any server whose SRV record names a
  different port.
- `https://tmspk.gg/…` opens Chlorophone: Android offers it in the "Open
  with" chooser. The domain is not ours, so there is no
  `assetlinks.json` to verify against and no `autoVerify` — the link never
  routes here silently. A pasted `tmspk.gg/…` link works either way.

## Privilege keys

A link may carry a privilege key: `token=` (or `privilegekey=`) — rights the
server's admin handed out, in TeamSpeak's own `P4U:` form.

- The key is spent **once**, on the connection it comes with, through
  TeamSpeak's `privilegekeyuse` command. The server's answer (an `error`
  command tagged `return_code=token`; id 0 means it was accepted) reaches the
  app, so a refused key is visible rather than silent.
- A key is **not** saved with a bookmark. It is good for one use, and a
  bookmark that kept it would spend it again on the next reconnect and be
  quietly refused.
- A **produced** link never carries a key. A key grants the holder the
  issuer's rights, and a link gets shared around a room; that is not a
  decision `toUri()` can make for the user.

## Channels and their passwords

- `channel=` is a path of channel names, rooted at the top level:
  `Movies/Lobby`. `cid=` is a channel id.
- `channelpassword=` is that channel's password.
- The password reaches TeamSpeak in the form the server expects —
  `base64(sha1(password))` — and never in the clear. The plain password is
  not logged either.
- A channel named by **id** cannot be asked for at connect time: the
  server's `client_default_channel` only understands names, and an id there
  is ignored, leaving the client in the default channel with nothing to show
  for it. So an id is joined once the channel tree has arrived, with
  `clientmove`, which takes the id directly. `model/ChannelPath.kt` does
  that resolution, and `PlntViewModel.joinPendingChannel()` drives it.
- Tapping a protected channel in the channel tree asks for the password.
  A move without one is refused by the server, and the user stays in the
  channel they were already in with nothing to show for the tap.

## Opening one (receiver)

`AndroidManifest.xml` registers a `VIEW`/`BROWSABLE` intent filter for the three
schemes and for `tmspk.gg`. A tap routes to `MainActivity`, which hands the URI to
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

- **https App Links:** these are custom-scheme links, not verified https links,
  so there is no web fallback page for someone without PLNT installed. A short
  landing page that offers the APK + the link is a possible follow-up.
- **Bookmark secrets are still in the clear.** A bookmark's server password,
  channel password and privilege key live in one JSON blob in Preferences
  DataStore. PHA-4856 (WS8) moves that blob into an encrypted store; this
  change did not pre-empt it.
