package com.plnt.client.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.plnt.client.core.CoreBridge

/**
 * The identity PEM is the client's persistent keypair — losing it means
 * showing up on the server as a stranger on every connect, and leaking it
 * lets someone else impersonate this client. PHA-3079's ViewModel kept it in
 * plain SharedPreferences as a stopgap; this is PHA-3078's real store —
 * Jetpack Security's EncryptedSharedPreferences (AES256-GCM values,
 * AES256-SIV keys, master key held in the Android Keystore).
 */
class IdentityStore(context: Context) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "plnt_identity",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    fun loadOrCreate(): String {
        peek()?.let { return it }
        val created = CoreBridge.createIdentity()
        prefs.edit().putString(KEY_IDENTITY_PEM, created).apply()
        return created
    }

    /**
     * The stored PEM, or null if this install has never had one. Unlike
     * [loadOrCreate] this never generates one, which is what a headless
     * restart-reconnect needs (PHA-3290): silently minting a fresh identity
     * there would put the client back on the server as a stranger — new server
     * groups, new permissions — with nobody watching to notice.
     */
    fun peek(): String? = prefs.getString(KEY_IDENTITY_PEM, null)

    fun import(pem: String): Boolean {
        if (!CoreBridge.importIdentity(pem)) return false
        prefs.edit().putString(KEY_IDENTITY_PEM, pem).apply()
        return true
    }

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
