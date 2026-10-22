package com.plnt.client.account

import android.content.Context
import com.plnt.client.core.CoreBridge
import com.plnt.client.data.openEncryptedPrefs
import com.plnt.client.model.Bookmark
import org.json.JSONObject
import java.util.UUID

/** Uses the already backup-excluded identity preferences. No account session/password is stored. */
class AccountStore(context: Context) {
    private val prefs by lazy { openEncryptedPrefs(context, "plnt_identity", "plnt.identity") }

    fun deviceId(): String = prefs.getString("myts_device", null) ?: UUID.randomUUID().toString().also {
        check(prefs.edit().putString("myts_device", it).commit())
    }

    fun save(entries: List<AccountEntry>) {
        require(entries.isNotEmpty() && entries.map { it.bookmark.id }.distinct().size == entries.size)
        require(entries.all { it.bookmark.id.startsWith("myts:") && CoreBridge.importIdentity(it.identity) })
        val edit = prefs.edit()
        for (entry in entries) edit.putString(entry.bookmark.id, JSONObject()
            .put("identity", entry.identity).put("password", entry.password ?: "").toString())
        check(edit.commit())
    }

    /** Missing imported keys fail closed, including after restore to a different device. */
    fun resolve(bookmark: Bookmark, localIdentity: () -> String?): Pair<Bookmark, String> {
        if (!bookmark.id.startsWith("myts:")) return bookmark to checkNotNull(localIdentity())
        return try {
            val raw = checkNotNull(prefs.getString(bookmark.id, null))
            val data = JSONObject(raw)
            val key = data.getString("identity")
            check(CoreBridge.importIdentity(key))
            bookmark.copy(serverPassword = bookmark.serverPassword ?: data.optString("password").ifEmpty { null }) to key
        } catch (e: Exception) {
            // JSON/provider failures can include raw values; connection errors are displayed/logged.
            throw IllegalStateException("Imported identity unavailable. Sign in and import this server again.")
        }
    }
}
