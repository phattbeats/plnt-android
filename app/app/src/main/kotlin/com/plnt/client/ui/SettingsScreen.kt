package com.plnt.client.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plnt.client.model.PttMode
import com.plnt.client.model.PttSource
import com.plnt.client.model.Settings

/** PHA-3076 screen 3: PTT mode, PTT source, identity export/import, about. */
@Composable
fun SettingsScreen(
    settings: Settings,
    identityExport: String?,
    onPttModeChange: (PttMode) -> Unit,
    onPttSourceChange: (PttSource) -> Unit,
    onImportIdentity: (String) -> Boolean,
    onBack: () -> Unit,
) {
    var importText by remember { mutableStateOf("") }
    var importResult by remember { mutableStateOf<Boolean?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text("Talk mode", style = MaterialTheme.typography.titleSmall)
            RadioRow("Push to talk", settings.pttMode == PttMode.PUSH_TO_TALK) { onPttModeChange(PttMode.PUSH_TO_TALK) }
            RadioRow("Open mic", settings.pttMode == PttMode.OPEN_MIC) { onPttModeChange(PttMode.OPEN_MIC) }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Push-to-talk button", style = MaterialTheme.typography.titleSmall)
            RadioRow("On-screen only", settings.pttSource == PttSource.TOUCH_ONLY) { onPttSourceChange(PttSource.TOUCH_ONLY) }
            RadioRow("Volume button", settings.pttSource == PttSource.VOLUME_BUTTON) { onPttSourceChange(PttSource.VOLUME_BUTTON) }
            RadioRow("Headset button", settings.pttSource == PttSource.HEADSET_BUTTON) { onPttSourceChange(PttSource.HEADSET_BUTTON) }
            Text(
                "Lock-screen / notification PTT ships with the foreground service (PHA-3078); " +
                    "the hardware button above only fires while the app is in the foreground.",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Identity", style = MaterialTheme.typography.titleSmall)
            Text(
                identityExport ?: "(none yet)",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp),
            )
            OutlinedTextField(
                value = importText,
                onValueChange = { importText = it; importResult = null },
                label = { Text("Paste identity to import") },
                modifier = Modifier.fillMaxWidth(),
            )
            Row {
                TextButton(onClick = { importResult = onImportIdentity(importText) }) { Text("Import") }
            }
            importResult?.let {
                Text(if (it) "Imported." else "Invalid identity.", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("About", style = MaterialTheme.typography.titleSmall)
            Text(
                "PLNT — voice-only TeamSpeak client. Connect, channel tree, who's talking, " +
                    "push-to-talk / open mic, mute/deafen. No chat, no streams, no file transfer.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
