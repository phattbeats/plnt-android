package com.plnt.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.plnt.client.core.CoreBridge
import com.plnt.client.model.AudioDeviceOption
import com.plnt.client.model.PttMode
import com.plnt.client.model.Settings
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneFaint
import com.plnt.client.ui.theme.BoneMuted
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.Sage
import com.plnt.client.ui.theme.SurfaceDark
import com.plnt.client.ui.theme.SurfaceHigh
import com.plnt.client.ui.theme.SurfaceRaised

/** Talk mode, PTT triggers, audio devices, background permission, identity, about. */
@Composable
fun SettingsScreen(
    settings: Settings,
    identityExport: String?,
    availableInputDevices: List<AudioDeviceOption>,
    availableOutputDevices: List<AudioDeviceOption>,
    batteryOptimizationExempt: Boolean,
    onRequestBatteryExemption: () -> Unit,
    onPttModeChange: (PttMode) -> Unit,
    onPttOnVolumeButtonChange: (Boolean) -> Unit,
    onPttOnHeadsetButtonChange: (Boolean) -> Unit,
    onInputDeviceChange: (String?) -> Unit,
    onOutputDeviceChange: (String?) -> Unit,
    onImportIdentity: (String) -> Boolean,
    onCreateIdentity: () -> Unit,
    onBack: () -> Unit,
) {
    var showExport by remember { mutableStateOf(false) }
    var showImport by remember { mutableStateOf(false) }
    var showCreate by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg),
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge, color = Bone) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Bone)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
        ) {
            Section("Talk mode", Icons.Outlined.RecordVoiceOver) {
                ChoiceRow("Push to talk", "Hold the button, or a hardware trigger, to speak", settings.pttMode == PttMode.PUSH_TO_TALK) { onPttModeChange(PttMode.PUSH_TO_TALK) }
                RowDivider()
                ChoiceRow("Open mic", "Always transmitting while unmuted", settings.pttMode == PttMode.OPEN_MIC) { onPttModeChange(PttMode.OPEN_MIC) }
            }

            Section("Push-to-talk triggers", Icons.Outlined.Mic) {
                // Two independent switches: both can be armed at once.
                SwitchRow("Volume button", "Only while PLNT is in the foreground", settings.pttOnVolumeButton, onPttOnVolumeButtonChange)
                RowDivider()
                SwitchRow("Headset button", "Works with the screen locked", settings.pttOnHeadsetButton, onPttOnHeadsetButtonChange)
                Footnote("The on-screen button is always live. From the lock screen, use the notification's Talk action.")
            }

            // Only devices the hardware actually reports right now are listed —
            // the lists refresh on entering this screen and, during a call, on
            // plug/unplug.
            Section("Microphone", Icons.Outlined.Mic) {
                DevicePicker(availableInputDevices, settings.preferredInputDeviceKey, onInputDeviceChange)
                Footnote("Automatic follows whatever Android routes to (Bluetooth, then wired, then the phone). Pick a device to keep using it even while another one is connected.")
            }

            Section("Speaker", Icons.Outlined.Headset) {
                DevicePicker(availableOutputDevices, settings.preferredOutputDeviceKey, onOutputDeviceChange)
            }

            Section("Background", Icons.Outlined.BatteryChargingFull) {
                if (batteryOptimizationExempt) {
                    InfoRow("Battery optimisation off", "Calls keep running with the screen off and the app in the background.", Sage)
                } else {
                    NavRow("Allow PLNT to run in the background", "Stops Android pausing the call to save battery", onClick = onRequestBatteryExemption)
                }
            }

            Section("Identity", Icons.Outlined.Key) {
                NavRow("Export identity", "Copy it to move this identity to another device", enabled = identityExport != null) { showExport = true }
                RowDivider()
                NavRow("Import identity", "Paste one exported elsewhere") { showImport = true }
                RowDivider()
                NavRow("Create new identity", "Servers will see you as a new user") { showCreate = true }
            }

            Section("About", Icons.Outlined.Info) {
                InfoRow(
                    "PLNT",
                    "A TeamSpeak client for phones: voice, chat, and watching screen shares.",
                    null,
                )
                // core_version() is the one string only the Rust core can produce,
                // so a broken JNI/UniFFI link is visible here without a debugger.
                Footnote(remember { CoreBridge.coreVersion() })
            }
        }
    }

    if (showExport && identityExport != null) {
        val clipboard = LocalClipboardManager.current
        SettingsSheet(title = "Export identity", onDismiss = { showExport = false }) {
            Text(
                "Your TeamSpeak identity. Anyone holding this can connect as you — treat it like a private key.",
                style = MaterialTheme.typography.bodyMedium,
                color = BoneMuted,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceRaised)
                    .border(1.dp, DividerLine, RoundedCornerShape(12.dp))
                    .padding(14.dp),
            ) {
                Text(identityExport, style = MaterialTheme.typography.bodySmall, color = Bone)
            }
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton("Copy", modifier = Modifier.fillMaxWidth(), onClick = {
                clipboard.setText(AnnotatedString(identityExport))
                showExport = false
            })
        }
    }

    if (showCreate) {
        SettingsSheet(title = "Create new identity", onDismiss = { showCreate = false }) {
            Text(
                "Generates a brand new TeamSpeak identity and discards the one on this device. " +
                    "Servers that recognise this device by its old identity (server groups, bans) will see a stranger. " +
                    "Export the current identity first if you want to keep it.",
                style = MaterialTheme.typography.bodyMedium,
                color = BoneMuted,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton("Cancel", onClick = { showCreate = false }, modifier = Modifier.weight(1f), color = BoneMuted)
                PrimaryButton("Create", modifier = Modifier.weight(1f), onClick = {
                    onCreateIdentity()
                    showCreate = false
                })
            }
        }
    }

    if (showImport) {
        var pasted by remember { mutableStateOf("") }
        var failed by remember { mutableStateOf(false) }
        SettingsSheet(title = "Import identity", onDismiss = { showImport = false }) {
            Text(
                "Paste an exported identity. This replaces the one on this device.",
                style = MaterialTheme.typography.bodyMedium,
                color = BoneMuted,
            )
            Spacer(modifier = Modifier.height(14.dp))
            BasicTextField(
                value = pasted,
                onValueChange = { pasted = it; failed = false },
                textStyle = MaterialTheme.typography.bodySmall.copy(color = Bone),
                cursorBrush = SolidColor(Gold),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceRaised)
                    .border(1.dp, DividerLine, RoundedCornerShape(12.dp))
                    .padding(14.dp),
            )
            if (failed) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "That isn't a valid identity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton("Import", enabled = pasted.isNotBlank(), modifier = Modifier.fillMaxWidth(), onClick = {
                if (onImportIdentity(pasted)) showImport = false else failed = true
            })
        }
    }
}

@Composable
private fun Section(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)) {
            Icon(icon, contentDescription = null, tint = BoneMuted, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title.uppercase(), color = BoneMuted, style = MaterialTheme.typography.labelSmall)
        }
        PlntCard { content() }
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(thickness = 1.dp, color = DividerLine, modifier = Modifier.padding(start = 16.dp))
}

@Composable
private fun Footnote(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = BoneFaint,
        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 4.dp, bottom = 12.dp),
    )
}

/**
 * "Automatic" plus one row per present device. "Automatic" is a UI-only row
 * backed by a null key and stays selected by default. A device pinned earlier
 * that is no longer connected still renders, so the selection is visible (and
 * clearable) rather than silently showing as Automatic.
 */
@Composable
private fun DevicePicker(
    devices: List<AudioDeviceOption>,
    selectedKey: String?,
    onSelect: (String?) -> Unit,
) {
    ChoiceRow("Automatic", null, selectedKey == null) { onSelect(null) }
    devices.forEach { device ->
        RowDivider()
        ChoiceRow(device.label, null, selectedKey == device.key) { onSelect(device.key) }
    }
    if (selectedKey != null && devices.none { it.key == selectedKey }) {
        RowDivider()
        ChoiceRow("Selected device", "Not connected right now", selected = true) { onSelect(null) }
    }
}

@Composable
private fun ChoiceRow(label: String, detail: String?, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = Bone)
            if (detail != null) Text(detail, style = MaterialTheme.typography.bodySmall, color = BoneMuted)
        }
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (selected) Gold else SurfaceHigh)
                .border(1.dp, if (selected) Gold else DividerLine, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Icon(Icons.Filled.Check, contentDescription = null, tint = Bg, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun SwitchRow(label: String, detail: String?, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }.padding(start = 16.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = Bone)
            if (detail != null) Text(detail, style = MaterialTheme.typography.bodySmall, color = BoneMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Bg,
                checkedTrackColor = Gold,
                uncheckedThumbColor = BoneMuted,
                uncheckedTrackColor = SurfaceHigh,
                uncheckedBorderColor = DividerLine,
            ),
        )
    }
}

@Composable
private fun NavRow(label: String, detail: String?, enabled: Boolean = true, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(enabled = enabled) { onClick() }.padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = if (enabled) Bone else BoneFaint)
            if (detail != null) Text(detail, style = MaterialTheme.typography.bodySmall, color = if (enabled) BoneMuted else BoneFaint)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = BoneFaint)
    }
}

@Composable
private fun InfoRow(label: String, detail: String, dot: androidx.compose.ui.graphics.Color?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (dot != null) {
            StatusDot(dot)
            Spacer(modifier = Modifier.width(10.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = Bone)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = BoneMuted)
        }
    }
}

@Composable
private fun SettingsSheet(title: String, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { SheetDragHandle() },
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = Bone)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
