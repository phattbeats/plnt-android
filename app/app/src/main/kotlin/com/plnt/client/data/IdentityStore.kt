package com.plnt.client.data

import android.content.Context
import android.content.SharedPreferences
import com.plnt.client.core.CoreBridge

/**
 * The identity PEM is the client's persistent keypair — losing it means
 * showing up on the server as a stranger on every connect, and leaking it
 * lets someone else impersonate this client. #3079's ViewModel kept it in
 * plain SharedPreferences as a stopgap; this is #3078's real store —
 * Jetpack Security's EncryptedSharedPreferences (AES256-GCM values,
 * AES256-SIV keys, master key held in the Android Keystore).
 */
class IdentityStore(context: Context) {
    // An unreadable file is deleted and recreated rather than crashing the
    // app; the identity is lost then and a new one is minted on next use.
    private val prefs: SharedPreferences by lazy { openEncryptedPrefs(context, "plnt_identity", "plnt.identity") }

    fun loadOrCreate(): String {
        peek()?.let { return it }
        val created = CoreBridge.createIdentity()
        prefs.edit().putString(KEY_IDENTITY_PEM, created).apply()
        return created
    }

    /**
     * The stored PEM, or null if this install has never had one. Unlike
     * [loadOrCreate] this never generates one, which is what a headless
     * restart-reconnect needs (#3290): silently minting a fresh identity
     * there would put the client back on the server as a stranger — new server
     * groups, new permissions — with nobody watching to notice.
     */
    fun peek(): String? = prefs.getString(KEY_IDENTITY_PEM, null)

    fun import(pem: String): Boolean {
        if (!CoreBridge.importIdentity(pem)) return false
        val previous = peek()
        if (prefs.edit().putString(KEY_IDENTITY_PEM, pem).commit()) return true
        // commit() also updates the in-memory preferences when a disk write fails.
        prefs.edit().putString(KEY_IDENTITY_PEM, previous).commit()
        return false
    }

    /**
     * Import from an exported TeamSpeak identity `.ini` file (PHA-4116).
     *
     * This is the path a user actually has: the official client writes a `.ini`,
     * not the bare identity string. Returns the persisted identity string, or
     * null if the file isn't a readable identity export — in which case the
     * stored identity is left untouched.
     */
    fun importIni(contents: String): String? {
        val pem = CoreBridge.importIdentityIni(contents) ?: return null
        if (!import(pem)) return null
        return pem
    }

    /**
     * What a `.ini` holds, without importing it — so the UI can show the user
     * what they are about to replace their identity with. Public metadata
     * only (name, nickname, level); safe to display and log.
     */
    fun describeIni(contents: String): String? = CoreBridge.describeIdentityIni(contents)

    /** Discards the current identity and persists a freshly generated one. */
    fun createNew(): String {
        val created = CoreBridge.createIdentity()
        prefs.edit().putString(KEY_IDENTITY_PEM, created).apply()
        return created
    }

    private companion object {
        const val KEY_IDENTITY_PEM = "identity_pem"
    }
}
