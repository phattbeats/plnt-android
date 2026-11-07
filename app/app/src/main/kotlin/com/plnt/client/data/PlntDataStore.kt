package com.plnt.client.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.plnt.client.model.Bookmark
import com.plnt.client.model.InputRoute
import com.plnt.client.model.PttMode
import com.plnt.client.model.Settings
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

private val Context.plntDataStore by preferencesDataStore(name = "plnt_settings")

/**
 * Bookmarks + PTT settings, replacing PHA-3079's raw-SharedPreferences
 * stopgap with Jetpack DataStore (identity stays out of here — that one's
 * sensitive enough to earn its own encrypted store, see [IdentityStore]).
 * Bookmarks are kept as one JSON blob (same on-disk shape as before, so an
 * existing install's data reads back without a migration step); settings get
 * their own keys.
 */
class PlntDataStore(private val context: Context) {
    private object Keys {
        val BOOKMARKS = stringPreferencesKey("bookmarks_json")
        val PTT_MODE = stringPreferencesKey("ptt_mode")
        val PTT_VOLUME_BUTTON = booleanPreferencesKey("ptt_volume_button")
        val PTT_HEADSET_BUTTON = booleanPreferencesKey("ptt_headset_button")
        val INPUT_ROUTE = stringPreferencesKey("input_route")

        /**
         * Superseded: PTT trigger used to be one mutually-exclusive enum
         * (TOUCH_ONLY / VOLUME_BUTTON / HEADSET_BUTTON), which the design never
         * called for — volume and headset are independent switches. Still read
         * once so an existing install keeps whichever trigger it had.
         */
        val LEGACY_PTT_SOURCE = stringPreferencesKey("ptt_source")
    }

    suspend fun loadBookmarks(): List<Bookmark> {
        val raw = context.plntDataStore.data.first()[Keys.BOOKMARKS] ?: return emptyList()
        return runCatching {
            JSONArray(raw).let { arr ->
                (0 until arr.length()).map { i ->
                    val o = arr.getJSONObject(i)
                    Bookmark(
                        id = o.getString("id"),
                        label = o.getString("label"),
                        address = o.getString("address"),
                        port = o.getInt("port"),
                        nickname = o.getString("nickname"),
                        serverPassword = o.optString("password", "").ifEmpty { null },
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    suspend fun saveBookmarks(bookmarks: List<Bookmark>) {
        val arr = JSONArray()
        bookmarks.forEach { b ->
            arr.put(
                JSONObject()
                    .put("id", b.id)
                    .put("label", b.label)
                    .put("address", b.address)
                    .put("port", b.port)
                    .put("nickname", b.nickname)
                    .put("password", b.serverPassword ?: ""),
            )
        }
        context.plntDataStore.edit { it[Keys.BOOKMARKS] = arr.toString() }
    }

    suspend fun loadSettings(): Settings {
        val prefs = context.plntDataStore.data.first()
        val mode = prefs[Keys.PTT_MODE]?.let { runCatching { PttMode.valueOf(it) }.getOrNull() }
            ?: PttMode.PUSH_TO_TALK
        val legacy = prefs[Keys.LEGACY_PTT_SOURCE]
        val inputRoute = prefs[Keys.INPUT_ROUTE]?.let { runCatching { InputRoute.valueOf(it) }.getOrNull() }
            ?: InputRoute.AUTO
        return Settings(
            pttMode = mode,
            pttOnVolumeButton = prefs[Keys.PTT_VOLUME_BUTTON] ?: (legacy == "VOLUME_BUTTON"),
            pttOnHeadsetButton = prefs[Keys.PTT_HEADSET_BUTTON] ?: (legacy == "HEADSET_BUTTON"),
            preferredInputRoute = inputRoute,
        )
    }

    suspend fun saveSettings(settings: Settings) {
        context.plntDataStore.edit {
            it[Keys.PTT_MODE] = settings.pttMode.name
            it[Keys.PTT_VOLUME_BUTTON] = settings.pttOnVolumeButton
            it[Keys.PTT_HEADSET_BUTTON] = settings.pttOnHeadsetButton
            it[Keys.INPUT_ROUTE] = settings.preferredInputRoute.name
            it.remove(Keys.LEGACY_PTT_SOURCE)
        }
    }
}
