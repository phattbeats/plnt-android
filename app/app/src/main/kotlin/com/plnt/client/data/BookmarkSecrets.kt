package com.plnt.client.data

import android.content.Context
import android.content.SharedPreferences
import com.plnt.client.model.Bookmark
import org.json.JSONArray
import org.json.JSONObject

/**
 * PHA-4856 (WS8): the parts of a [Bookmark] that are secrets — the server
 * password, the channel password and a privilege key. Until WS8 these sat in
 * the clear in [PlntDataStore]'s bookmark blob; they now live only in an
 * encrypted, backup-excluded store ([KeystoreSecretVault]), keyed by bookmark id.
 */
internal data class BookmarkSecrets(
    val serverPassword: String?,
    val channelPassword: String?,
    val privilegeKey: String?,
) {
    fun isEmpty(): Boolean = serverPassword == null && channelPassword == null && privilegeKey == null

    fun applyTo(b: Bookmark): Bookmark =
        b.copy(serverPassword = serverPassword, channelPassword = channelPassword, privilegeKey = privilegeKey)

    fun toJson(): String = JSONObject()
        .put("password", serverPassword ?: "")
        .put("channelpassword", channelPassword ?: "")
        .put("privilegekey", privilegeKey ?: "")
        .toString()

    companion object {
        fun of(b: Bookmark): BookmarkSecrets? =
            BookmarkSecrets(b.serverPassword, b.channelPassword, b.privilegeKey).takeUnless { it.isEmpty() }

        fun fromJson(raw: String): BookmarkSecrets? = runCatching {
            val o = JSONObject(raw)
            BookmarkSecrets(
                serverPassword = o.optString("password", "").ifEmpty { null },
                channelPassword = o.optString("channelpassword", "").ifEmpty { null },
                privilegeKey = o.optString("privilegekey", "").ifEmpty { null },
            )
        }.getOrNull()
    }
}

/**
 * The on-disk shape of the bookmark list in [PlntDataStore]. Pure, no Android
 * deps, so the migration can be tested on the host.
 */
internal object BookmarkCodec {
    private val SECRET_FIELDS = listOf("password", "channelpassword", "privilegekey")

    /** The list without its secrets — the only shape written from WS8 on. */
    fun encodePublic(bookmarks: List<Bookmark>): String {
        val arr = JSONArray()
        bookmarks.forEach { b ->
            arr.put(
                JSONObject()
                    .put("id", b.id)
                    .put("label", b.label)
                    .put("address", b.address)
                    .put("port", b.port)
                    .put("nickname", b.nickname)
                    .put("channel", b.channel ?: "")
                    .put("folder", b.folder ?: ""),
            )
        }
        return arr.toString()
    }

    /**
     * Reads either shape. A blob written before WS8 still carries its secrets
     * in the clear; they are returned on the bookmarks so the caller can move
     * them into the vault.
     */
    fun decode(raw: String): List<Bookmark> = runCatching {
        val arr = JSONArray(raw)
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            Bookmark(
                id = o.getString("id"),
                label = o.getString("label"),
                address = o.getString("address"),
                port = o.getInt("port"),
                nickname = o.getString("nickname"),
                serverPassword = o.optString("password", "").ifEmpty { null },
                // PHA-4849: optional, so an install written by an
                // older build reads back unchanged.
                channel = o.optString("channel", "").ifEmpty { null },
                channelPassword = o.optString("channelpassword", "").ifEmpty { null },
                privilegeKey = o.optString("privilegekey", "").ifEmpty { null },
                // Absent in stores written before account import existed.
                folder = o.optString("folder", "").ifEmpty { null },
            )
        }
    }.getOrDefault(emptyList())

    /** True while the blob still holds a secret field at all, even an empty one: it has not been rewritten yet. */
    fun hasLegacySecretFields(raw: String): Boolean = runCatching {
        val arr = JSONArray(raw)
        (0 until arr.length()).any { i -> SECRET_FIELDS.any { arr.getJSONObject(i).has(it) } }
    }.getOrDefault(false)
}

/** Where bookmark secrets are kept. Implementations throw when the store cannot be opened. */
internal interface SecretVault {
    fun readAll(): Map<String, BookmarkSecrets>

    /** Replaces every stored secret with [secrets]; false if the write did not reach disk. */
    fun replaceAll(secrets: Map<String, BookmarkSecrets>): Boolean
}

/**
 * Bookmarks with their secrets split off. The public half goes wherever
 * [writePublic] puts it; secrets only ever go to [vault].
 *
 * Fail closed: when the vault cannot be written, the bookmark is still saved
 * but its secrets are not — they are never written anywhere else instead, and
 * [save] says so, so the UI can tell the user they will be asked again.
 */
internal class BookmarkRepository(
    private val readPublic: suspend () -> String?,
    private val writePublic: suspend (String) -> Unit,
    private val vault: SecretVault,
) {
    suspend fun load(): List<Bookmark> {
        val raw = readPublic() ?: return emptyList()
        val stored = BookmarkCodec.decode(raw)
        val vaulted = runCatching { vault.readAll() }
        // A vault secret wins; a legacy plaintext one is only used where the vault has none.
        val merged = stored.map { b -> vaulted.getOrNull()?.get(b.id)?.applyTo(b) ?: b }
        // Migration. Only once the vault holds the secrets is the plaintext
        // copy dropped; if the vault can't be written (or read — a blind
        // replace would wipe what it holds), the old blob is left as it was
        // and the move is tried again on the next load.
        if (BookmarkCodec.hasLegacySecretFields(raw) && vaulted.isSuccess && writeVault(merged)) {
            writePublic(BookmarkCodec.encodePublic(merged))
        }
        return merged
    }

    /** Saves [bookmarks]; false when their secrets could not be stored securely and were dropped. */
    suspend fun save(bookmarks: List<Bookmark>): Boolean {
        val secretsStored = writeVault(bookmarks)
        writePublic(BookmarkCodec.encodePublic(bookmarks))
        return secretsStored
    }

    private fun writeVault(bookmarks: List<Bookmark>): Boolean {
        val secrets = bookmarks.mapNotNull { b -> BookmarkSecrets.of(b)?.let { b.id to it } }.toMap()
        return runCatching { vault.replaceAll(secrets) }.getOrDefault(false)
    }
}

/**
 * The real vault: EncryptedSharedPreferences under an Android Keystore master
 * key, in its own file so it can be backup-excluded on its own. An unreadable
 * file is reset by [openEncryptedPrefs] (the secrets are lost and asked for
 * again); a Keystore that can't produce a key at all makes every call throw,
 * which [BookmarkRepository] treats as "not stored" — never as "store in the clear".
 */
internal class KeystoreSecretVault(context: Context) : SecretVault {
    private val prefs: SharedPreferences by lazy { openEncryptedPrefs(context, PREFS_NAME, "plnt.bookmarks") }

    override fun readAll(): Map<String, BookmarkSecrets> =
        prefs.all.mapNotNull { (id, v) -> (v as? String)?.let(BookmarkSecrets::fromJson)?.let { id to it } }.toMap()

    override fun replaceAll(secrets: Map<String, BookmarkSecrets>): Boolean {
        val edit = prefs.edit().clear()
        for ((id, s) in secrets) edit.putString(id, s.toJson())
        return edit.commit()
    }

    companion object {
        /** Excluded in res/xml/backup_rules.xml and data_extraction_rules.xml. */
        const val PREFS_NAME = "plnt_bookmark_secrets"
    }
}
