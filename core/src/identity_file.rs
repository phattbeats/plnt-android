//! Importing a user's own TeamSpeak identity from an exported `.ini` file.
//!
//! # Why this exists
//!
//! PHA-4116. The original framing was that getting an identity onto a device
//! required recovering TeamSpeak's myTeamSpeak account-sync protocol, which
//! would have meant reverse-engineering their native client and impersonating
//! the official app. That was the wrong question.
//!
//! A TeamSpeak identity is not an account object. It is a local ECC P-256 key
//! pair in a file. Nothing about it is stored on myTeamSpeak, no account is
//! needed to create or use one, and a game server never sees the account login
//! — it only sees the public key. The account is a *backup* facility for
//! identities that already live on your disk.
//!
//! So the import path is offline and file-based: the user exports an identity
//! `.ini` from a machine where they have the official client, we read it, and
//! hand the key to the parser that already backs `IdentityObj::import`.
//!
//! # What this module deliberately does not do
//!
//! - It never contacts `clientapi.myteamspeak.com` or any myTeamSpeak service.
//! - It never writes, uploads, or logs a private key. [`ImportedIdentity::describe`]
//!   returns only public metadata.
//! - It never creates, rotates, or upgrades an identity. Import is read-only.

use std::fmt;
use std::sync::Arc;

use crate::{IdentityObj, PlntError};

/// The `.ini` key holding the identity blob.
const IDENTITY_KEY: &str = "identity";

/// A parsed identity file: the key, plus whatever descriptive metadata the
/// `.ini` carried alongside it.
#[derive(Clone)]
pub struct ImportedIdentity {
    /// Ready to hand to the client. Persist with [`ImportedIdentity::export`].
    pub identity: Arc<IdentityObj>,
    /// The `name` field from the `.ini`, if present. A human label — never used
    /// for authentication.
    pub name: Option<String>,
    /// The `nickname` field from the `.ini`, if present. Advisory only: the
    /// nickname actually used on a server is chosen at connect time.
    pub nickname: Option<String>,
}

impl ImportedIdentity {
    /// Re-export in the portable `"<counter>V<key>"` form the rest of the core
    /// already speaks. This is what gets persisted.
    pub fn export(&self) -> String { self.identity.export() }

    /// Current security level (hash-cash counter depth).
    pub fn level(&self) -> u8 { self.identity.level() }

    /// Public-only summary for logs and the import UI. Deliberately has no way
    /// to stringify the key, so a careless `{}` cannot leak it into a log line.
    pub fn describe(&self) -> String {
        format!(
            "level={} name={} nickname={}",
            self.level(),
            self.name.as_deref().unwrap_or("-"),
            self.nickname.as_deref().unwrap_or("-"),
        )
    }
}

#[derive(Debug)]
pub enum ImportError {
    Io(std::io::Error),
    /// The file contained no `identity=` key at all.
    NoIdentityKey,
    /// More than one `identity=` key. We cannot guess which one the user
    /// meant, and picking one silently could import the wrong identity.
    MultipleIdentityKeys(usize),
    /// The `identity=` key had an empty value.
    EmptyIdentityValue,
    /// The value was present but did not parse as a TeamSpeak key.
    MalformedIdentity(String),
}

impl fmt::Display for ImportError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            ImportError::Io(e) => write!(f, "reading identity file: {e}"),
            ImportError::NoIdentityKey => write!(
                f,
                "no `{IDENTITY_KEY}=` key found — is this a TeamSpeak identity export?"
            ),
            ImportError::MultipleIdentityKeys(n) => write!(
                f,
                "found {n} `{IDENTITY_KEY}=` keys — refusing to guess which identity to import"
            ),
            ImportError::EmptyIdentityValue => {
                write!(f, "`{IDENTITY_KEY}=` was present but empty")
            }
            ImportError::MalformedIdentity(reason) => {
                write!(f, "identity value is not a valid TeamSpeak key: {reason}")
            }
        }
    }
}

impl std::error::Error for ImportError {}

impl From<std::io::Error> for ImportError {
    fn from(e: std::io::Error) -> Self {
        ImportError::Io(e)
    }
}

impl From<ImportError> for PlntError {
    fn from(e: ImportError) -> Self {
        PlntError::Identity(e.to_string())
    }
}

/// Import an identity from the text of a `.ini` file.
///
/// Handles the shapes TeamSpeak's exporter produces across client versions:
/// bare `key=value`, quoted values, CRLF line endings, leading whitespace,
/// comment lines, and `key = value` spacing. The value itself is passed through
/// to `IdentityObj::import` verbatim apart from surrounding quotes and
/// whitespace, because that function owns the accepted identity forms.
///
/// The caller reads the file — on Android that is a SAF content URI, not a
/// filesystem path, which is why this takes text rather than a `Path`.
pub fn import_from_ini(text: &str) -> Result<ImportedIdentity, ImportError> {
    let mut identity_values: Vec<&str> = Vec::new();
    let mut name = None;
    let mut nickname = None;

    for line in text.lines() {
        let line = line.trim();
        if line.is_empty() || line.starts_with(';') || line.starts_with('#') {
            continue;
        }
        let Some((key, value)) = line.split_once('=') else {
            continue;
        };
        let value = unquote(value.trim());
        match key.trim() {
            IDENTITY_KEY => identity_values.push(value),
            // `name` is what the official client writes; `id` is what
            // third-party writers (TSIdentityTool, for one) use for the same
            // slot. Accept either so a file from any of them describes itself.
            // Neither is used for authentication — display only.
            "name" | "id" => name = Some(value.to_string()),
            "nickname" => nickname = Some(value.to_string()),
            _ => {}
        }
    }

    match identity_values.len() {
        0 => return Err(ImportError::NoIdentityKey),
        1 => {}
        n => return Err(ImportError::MultipleIdentityKeys(n)),
    }

    let raw = identity_values[0];
    if raw.is_empty() {
        return Err(ImportError::EmptyIdentityValue);
    }

    let identity = IdentityObj::import(raw.to_string())
        .map_err(|e| ImportError::MalformedIdentity(e.to_string()))?;

    Ok(ImportedIdentity { identity, name, nickname })
}

/// Strip a matched pair of surrounding quotes, if present.
///
/// TeamSpeak's exporter quotes some values and not others depending on client
/// version; the identity value is stored quoted in the common case.
fn unquote(value: &str) -> &str {
    let bytes = value.as_bytes();
    if bytes.len() >= 2 && bytes[0] == b'"' && bytes[bytes.len() - 1] == b'"' {
        &value[1..value.len() - 1]
    } else {
        value
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    // A real TeamSpeak-format value, quoted as the official exporter writes it:
    // `COUNTER || 'V' || base64(obfuscate(base64(ASN.1 private key)))`. This is
    // the `tsaudiobot` fixture from tsclientlib's own test suite.
    const SAMPLE: &str = "MCkDAgbAAgEgAiBhPImh+bO1xMGOrcplwN3G74bhE9XATm+DxVo3aNtBqg==";

    fn wrap(value: &str) -> String {
        format!("[Identity]\nname=a@b.c\nnickname=Friend\nidentity=\"{value}\"\n")
    }

    #[test]
    fn parses_quoted_value_with_metadata() {
        let imported = import_from_ini(&wrap(SAMPLE)).expect("should parse");
        assert_eq!(imported.name.as_deref(), Some("a@b.c"));
        assert_eq!(imported.nickname.as_deref(), Some("Friend"));
        assert!(!imported.export().is_empty());
    }

    #[test]
    fn parses_bare_value() {
        let text = format!("[Identity]\nidentity={SAMPLE}\n");
        let imported = import_from_ini(&text).expect("should parse");
        assert!(!imported.export().is_empty());
    }

    #[test]
    fn tolerates_crlf_spacing_and_comments() {
        let text = format!(
            "; exported by TeamSpeak\r\n[Identity]\r\n  identity = \"{SAMPLE}\"  \r\n"
        );
        assert!(import_from_ini(&text).is_ok());
    }

    #[test]
    fn rejects_missing_key() {
        assert!(matches!(
            import_from_ini("[Identity]\nnickname=Friend\n"),
            Err(ImportError::NoIdentityKey)
        ));
    }

    #[test]
    fn rejects_empty_value() {
        assert!(matches!(
            import_from_ini("[Identity]\nidentity=\n"),
            Err(ImportError::EmptyIdentityValue)
        ));
    }

    #[test]
    fn refuses_to_guess_between_multiple_identities() {
        let text = format!("[Identity]\nidentity={SAMPLE}\nidentity={SAMPLE}\n");
        assert!(matches!(
            import_from_ini(&text),
            Err(ImportError::MultipleIdentityKeys(2))
        ));
    }

    #[test]
    fn rejects_malformed_value() {
        assert!(matches!(
            import_from_ini("[Identity]\nidentity=not-a-real-key\n"),
            Err(ImportError::MalformedIdentity(_))
        ));
    }

    /// The official client writes `name=`; third-party writers use `id=` for
    /// the same slot. Both appear in the wild, so both must describe the file.
    #[test]
    fn accepts_either_name_or_id_for_the_label() {
        let with_name = import_from_ini(&format!("[Identity]\nname=alice\nidentity={SAMPLE}\n"));
        assert_eq!(with_name.unwrap().name.as_deref(), Some("alice"));

        let with_id = import_from_ini(&format!("[Identity]\nid=alice\nidentity={SAMPLE}\n"));
        assert_eq!(with_id.unwrap().name.as_deref(), Some("alice"));
    }

    /// The import path must not be able to leak key material into a log line.
    #[test]
    fn describe_leaks_no_key_material() {
        let imported = import_from_ini(&wrap(SAMPLE)).unwrap();
        let described = imported.describe();
        assert!(described.contains("Friend"));
        assert!(!described.contains(SAMPLE));
        assert!(!described.contains(&imported.export()));
    }

    /// An imported identity must be usable by the same client that owns it —
    /// this is the whole point of the module, so pin it directly.
    #[test]
    fn imported_identity_is_usable_as_an_identity() {
        let imported = import_from_ini(&wrap(SAMPLE)).expect("should parse");
        // Re-importing our own export must round-trip, which is exactly what
        // `Client::connect` does with the persisted string.
        let again = IdentityObj::import(imported.export()).expect("re-import");
        assert_eq!(again.export(), imported.export());
        assert_eq!(again.level(), imported.level());
    }
}