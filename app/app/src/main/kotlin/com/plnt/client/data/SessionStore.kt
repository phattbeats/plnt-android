package com.plnt.client.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.plnt.client.model.Bookmark
import org.json.JSONObject

private const val TAG = "plnt.session"

/**
 * The live session as a restart needs to see it: which server, and which
 * channel we were in.
 */
data class PersistedSession(val bookmark: Bookmark, val lastChannelId: Long?)

/**
 * Survives the process. Android killing
 * [com.plnt.client.service.VoiceService] takes its in-memory
 * `ConnectionParams` with it, so the `START_STICKY` restart that follows used
 * to come up with nothing to reconnect to and simply left the user
 * disconnected until they opened the app (PHA-3283's note, PHA-3290 item 2).
 * This is the state that makes that restart able to redial on its own — and a
 * plain crash-restart recoverable for free.
 *
 * Encrypted for the same reason [IdentityStore] is: a [Bookmark] can carry a
 * server password. The identity PEM deliberately is *not* copied in here —
 * it already lives in [IdentityStore] and one canonical copy of a private key
 * is enough.
 *
 * Writes go through `commit()`, not `apply()`. The whole point of this file is
 * to be readable after an abrupt kill, and `apply()`'s disk write is
 * asynchronous — exactly the window an LMK `SIGKILL` lands in. Callers write
 * off the main thread.
 */
class SessionStore(context: Context) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "plnt_session",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    /** Records (or re-records) the session a restart should reconnect to. */
    fun save(bookmark: Bookmark, lastChannelId: Long?) {
        val json = JSONObject()
            .put("id", bookmark.id)
            .put("label", bookmark.label)
            .put("address", bookmark.address)
            .put("port", bookmark.port)
            .put("nickname", bookmark.nickname)
            .put("password", bookmark.serverPassword ?: "")
        val editor = prefs.edit().putString(KEY_BOOKMARK, json.toString())
        if (lastChannelId != null) {
            editor.putLong(KEY_LAST_CHANNEL, lastChannelId)
        } else {
            editor.remove(KEY_LAST_CHANNEL)
        }
        editor.commit()
    }

    /**
     * The session to restore, or null when there is none — which is the normal
     * state after a user disconnect, and the signal a restarted service uses to
     * decide it has nothing to do.
     */
    fun load(): PersistedSession? {
        val raw = prefs.getString(KEY_BOOKMARK, null) ?: return null
        return runCatching {
            val o = JSONObject(raw)
            PersistedSession(
                bookmark = Bookmark(
                    id = o.getString("id"),
                    label = o.getString("label"),
                    address = o.getString("address"),
                    port = o.getInt("port"),
                    nickname = o.getString("nickname"),
                    serverPassword = o.optString("password", "").ifEmpty { null },
                ),
                lastChannelId = if (prefs.contains(KEY_LAST_CHANNEL)) prefs.getLong(KEY_LAST_CHANNEL, 0L) else null,
            )
        }.onFailure { Log.w(TAG, "unreadable persisted session — ignoring", it) }.getOrNull()
    }

    /**
     * The session is over by the user's own decision. Called from
     * `VoiceService.shutdown()` and nowhere near `onDestroy()`: a kill must
     * leave this intact, which is the entire point.
     */
    fun clear() {
        prefs.edit().clear().commit()
    }

    private companion object {
        const val KEY_BOOKMARK = "session_bookmark"
        const val KEY_LAST_CHANNEL = "session_last_channel"
    }
}
