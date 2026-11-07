package com.plnt.client.account

import android.content.Context
import com.plnt.client.core.CoreBridge
import com.plnt.client.data.openEncryptedPrefs
import com.plnt.client.model.ACCOUNT_IMPORT_ID_PREFIX
import com.plnt.client.model.Bookmark
import com.plnt.client.model.isAccountImport
import org.json.JSONObject
import java.util.UUID

/** The device id this install is bound to in a myTeamSpeak account. */
internal const val KEY_DEVICE_ID = "myts_device"

/**
 * The preference keys a sign-out removes: every imported identity and server
 * password, plus the device id the account was bound to. The device's own
 * identity PEM is not on this list and is never touched.
 */
internal fun accountKeysToClear(allKeys: Set<String>): Set<String> =
    allKeys.filter { it.startsWith(ACCOUNT_IMPORT_ID_PREFIX) || it == KEY_DEVICE_ID }.toSet()

/** Uses the already backup-excluded identity preferences. No account session/password is stored. */
class AccountStore(context: Context) {
    private val prefs by lazy { openEncryptedPrefs(context, "plnt_identity", "plnt.identity") }

    fun deviceId(): String = prefs.getString(KEY_DEVICE_ID, null) ?: UUID.randomUUID().toString().also {
        check(prefs.edit().putString(KEY_DEVICE_ID, it).commit())
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
        if (!bookmark.isAccountImport()) return bookmark to checkNotNull(localIdentity())
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

    /**
     * Removes everything this install imported from a myTeamSpeak account: the
     * imported identities and server passwords, and the device id the account
     * was bound to.
     *
     * This is a local erase only. The build has no account-write operation —
     * the read-only client can log in and download, but it cannot cancel a
     * session or revoke a device on the server — so the myTeamSpeak side of a
     * sign-out has to be done by the account owner in the official client. The
     * UI says so rather than implying the remote session ended.
     */
    fun clearImported(): Int {
        val keys = accountKeysToClear(prefs.all.keys)
        if (keys.isEmpty()) return 0
        val edit = prefs.edit()
        for (key in keys) edit.remove(key)
        check(edit.commit())
        return keys.size
    }
}
