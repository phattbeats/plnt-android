import Foundation
import Security

// MARK: - Keychain

/// Thin wrapper over the iOS Keychain.
///
/// The identity PEM is the client's persistent keypair: losing it means showing
/// up on the server as a stranger on every connect, and leaking it lets
/// someone else impersonate this client. Saved servers can carry a server
/// password, so those live here too. Both are `kSecClassGenericPassword`
/// items scoped to this app's service name.
enum Keychain {
    private static let service = "tech.phatt.chlorophone"

    @discardableResult
    static func set(_ data: Data, account: String) -> Bool {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account,
        ]
        let attributes: [String: Any] = [
            kSecValueData as String: data,
            // The identity has to survive a reboot; when this is set the
            // device is locked, so the item is readable without a user present.
            kSecAttrAccessible as String: kSecAttrAccessibleAfterFirstUnlock,
        ]
        let status = SecItemUpdate(query as CFDictionary, attributes as CFDictionary)
        if status == errSecSuccess { return true }
        if status == errSecItemNotFound {
            var insert = query
            insert.merge(attributes) { _, new in new }
            return SecItemAdd(insert as CFDictionary, nil) == errSecSuccess
        }
        return false
    }

    static func get(account: String) -> Data? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account,
            kSecReturnData as String: true,
            kSecMatchLimit as String: kSecMatchLimitOne,
        ]
        var out: CFTypeRef?
        guard SecItemCopyMatching(query as CFDictionary, &out) == errSecSuccess else { return nil }
        return out as? Data
    }

    @discardableResult
    static func delete(account: String) -> Bool {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account,
        ]
        let status = SecItemDelete(query as CFDictionary)
        return status == errSecSuccess || status == errSecItemNotFound
    }
}

// MARK: - Identity

/// The client's persistent TeamSpeak identity, in the standard export form
/// (`<counter>V<base64-key>`) that the official client also persists.
///
/// `loadOrCreate` mints one on first run; `peek` never mints, so a
/// reconnect after a crash cannot silently put the client back on the server
/// as a stranger.
enum IdentityStore {
    private static let account = "identity_pem"

    static func peek() -> String? {
        guard let data = Keychain.get(account: account) else { return nil }
        return String(data: data, encoding: .utf8)
    }

    static func loadOrCreate() -> String {
        if let existing = peek() { return existing }
        let created = createNew()
        return created
    }

    /// Validate and persist an imported identity. Returns false when the
    /// string is not a usable identity.
    static func importIdentity(_ pem: String) -> Bool {
        let trimmed = pem.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return false }
        guard (try? IdentityObj.import(s: trimmed)) != nil else { return false }
        return Keychain.set(Data(trimmed.utf8), account: account)
    }

    /// Discard the current identity and mint a fresh one.
    static func createNew() -> String {
        let created = IdentityObj.create().export()
        Keychain.set(Data(created.utf8), account: account)
        return created
    }
}

// MARK: - Saved servers

/// The list of saved servers. Keychain-backed because a bookmark can carry a
/// server password.
enum BookmarkStore {
    private static let account = "bookmarks"

    static func load() -> [Bookmark] {
        guard let data = Keychain.get(account: account),
              let decoded = try? JSONDecoder().decode([Bookmark].self, from: data)
        else { return [] }
        return decoded
    }

    static func save(_ bookmarks: [Bookmark]) {
        guard let data = try? JSONEncoder().encode(bookmarks) else { return }
        Keychain.set(data, account: account)
    }
}

// MARK: - Settings

/// Non-secret settings live in UserDefaults.
enum SettingsStore {
    private static let defaults = UserDefaults.standard
    private static let pttModeKey = "ptt_mode"

    static var pttMode: PttMode {
        get {
            guard let raw = defaults.string(forKey: pttModeKey),
                  let mode = PttMode(rawValue: raw)
            else { return .pushToTalk }
            return mode
        }
        set { defaults.set(newValue.rawValue, forKey: pttModeKey) }
    }
}
