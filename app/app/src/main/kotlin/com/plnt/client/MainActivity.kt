package com.plnt.client

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plnt.client.model.Screen
import com.plnt.client.ui.BookmarksScreen
import com.plnt.client.ui.ConnectedScreen
import com.plnt.client.ui.SettingsScreen
import com.plnt.client.ui.theme.PlntTheme

class MainActivity : ComponentActivity() {
    private val viewModel: PlntViewModel by viewModels()

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { /* no-op: connect() surfaces a core error if mic capture ends up denied */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRuntimePermissions()

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()

            PlntTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (state.screen) {
                        Screen.Bookmarks -> BookmarksScreen(
                            bookmarks = state.bookmarks,
                            sessionConnected = state.sessionConnected,
                            onConnect = viewModel::connect,
                            onSave = viewModel::addOrUpdateBookmark,
                            onDelete = viewModel::deleteBookmark,
                            newId = viewModel::newBookmarkId,
                            onOpenSettings = { viewModel.navigate(Screen.Settings) },
                        )
                        Screen.Connected -> ConnectedScreen(
                            phase = state.phase,
                            serverName = state.serverName,
                            channelTree = state.channelTree,
                            inputMuted = state.inputMuted,
                            outputDeafened = state.outputDeafened,
                            transmitting = state.transmitting,
                            pttIsPushToTalk = state.settings.pttMode == com.plnt.client.model.PttMode.PUSH_TO_TALK,
                            lastError = state.lastError,
                            onJoinChannel = viewModel::joinChannel,
                            onToggleMute = { viewModel.setMuted(!state.inputMuted) },
                            onToggleDeafen = { viewModel.setDeafened(!state.outputDeafened) },
                            onPttPress = viewModel::pttPress,
                            onPttRelease = viewModel::pttRelease,
                            onDisconnect = viewModel::disconnect,
                            onOpenSettings = { viewModel.navigate(Screen.Settings) },
                        )
                        Screen.Settings -> SettingsScreen(
                            settings = state.settings,
                            identityExport = state.identityExport,
                            availableInputRoutes = state.availableInputRoutes,
                            onPttModeChange = viewModel::setPttMode,
                            onPttOnVolumeButtonChange = viewModel::setPttOnVolumeButton,
                            onPttOnHeadsetButtonChange = viewModel::setPttOnHeadsetButton,
                            onInputRouteChange = viewModel::setPreferredInputRoute,
                            onImportIdentity = viewModel::importIdentity,
                            onCreateIdentity = viewModel::createNewIdentity,
                            onBack = {
                                viewModel.navigate(
                                    if (state.phase == com.plnt.client.model.ConnectionPhase.CONNECTED) {
                                        Screen.Connected
                                    } else {
                                        Screen.Bookmarks
                                    },
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    private fun requestRuntimePermissions() {
        val needed = buildList {
            add(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) add(Manifest.permission.BLUETOOTH_CONNECT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) add(Manifest.permission.POST_NOTIFICATIONS)
        }.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (needed.isNotEmpty()) requestPermissions.launch(needed.toTypedArray())
    }

    /**
     * In-foreground hardware PTT (Settings > "Volume button" / "Headset
     * button"). The headset/media button is *also* handled by VoiceService's
     * MediaSession, which is what keeps it working with the screen locked;
     * this path only covers the foreground case and the volume keys, which no
     * media session delivers.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (!isArmedPttKey(keyCode)) return super.onKeyDown(keyCode, event)
        viewModel.pttPress()
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (!isArmedPttKey(keyCode)) return super.onKeyUp(keyCode, event)
        viewModel.pttRelease()
        return true
    }

    /** Both triggers are independent switches, so either (or both) can be armed. */
    private fun isArmedPttKey(keyCode: Int): Boolean {
        val state = viewModel.state.value
        if (state.screen != Screen.Connected) return false
        val volumeArmed = state.settings.pttOnVolumeButton &&
            (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        val headsetArmed = state.settings.pttOnHeadsetButton && keyCode == KeyEvent.KEYCODE_HEADSETHOOK
        return volumeArmed || headsetArmed
    }
}
