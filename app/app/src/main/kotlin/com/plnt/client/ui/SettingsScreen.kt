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
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.plnt.client.model.AudioDeviceOption
import com.plnt.client.model.PttMode
import com.plnt.client.model.Settings
import com.plnt.client.stream.StreamQualityPolicy
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.OnBrand
import com.plnt.client.ui.theme.TextNormal
import com.plnt.client.ui.theme.TextFaint
import com.plnt.client.ui.theme.TextMuted
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.Brand
import com.plnt.client.ui.theme.Speaking
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
    onShareAudioChange: (Boolean) -> Unit,
    onStreamQualityChange: (resolution: Int, fps: Int, bitrateKbps: Int) -> Unit,
    onImportIdentity: (String) -> Boolean,
    onImportIdentityIni: (String) -> String?,
    onDescribeIdentityIni: (String) -> String?,
    onCreateIdentity: () -> Unit,
    onOpenAbout: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var showExport by remember { mutableStateOf(false) }
    var showImport by remember { mutableStateOf(false) }
    var showCreate by remember { mutableStateOf(false) }
    var qualityPicker by remember { mutableStateOf<QualityKnob?>(null) }
    var importPreview by remember { mutableStateOf<String?>(null) }
    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        // Storage Access Framework hands back a content URI, not a path — read
        // the stream ourselves and hand the text down (PHA-4116).
        if (uri == null) return@rememberLauncherForActivityResult
        val text = runCatching {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
        }.getOrNull()
        if (text != null) importPreview = text
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg),
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge, color = TextNormal) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextNormal)
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
                SwitchRow("Volume button", "Only while TeamSpeak is open", settings.pttOnVolumeButton, onPttOnVolumeButtonChange)
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

            Section("Screen sharing", Icons.Outlined.Headset) {
                SwitchRow("Share phone audio", "Viewers hear what the phone plays", settings.shareAudio, onShareAudioChange)
                QualityKnob.entries.forEach { knob ->
                    RowDivider()
                    NavRow(knob.title, knob.label(knob.read(settings))) { qualityPicker = knob }
                }
                Footnote(
                    "Automatic shares at full quality (1080p, 60 fps, 8 Mbps) on Wi-Fi and at 720p, 30 fps, 2.5 Mbps on mobile data. " +
                        "Changes apply to the next share. Your voice still goes through the call. " +
                        "Apps that block recording, like most paid video apps, share silence.",
                )
            }

            Section("Background", Icons.Outlined.BatteryChargingFull) {
                if (batteryOptimizationExempt) {
                    InfoRow("Battery optimisation off", "Calls keep running with the screen off and the app in the background.", Speaking)
                } else {
                    NavRow("Allow TeamSpeak to run in the background", "Stops Android pausing the call to save battery", onClick = onRequestBatteryExemption)
                }
            }

            Section("Identity", Icons.Outlined.Key) {
                NavRow("Export identity", "Copy it to move this identity to another device", enabled = identityExport != null) { showExport = true }
                RowDivider()
                NavRow("Import identity", "From a file the official client exported, or paste one") { showImport = true }
                RowDivider()
                NavRow("Create new identity", "Servers will see you as a new user") { showCreate = true }
            }

            Section("About", Icons.Outlined.Info) {
                NavRow("About this app", "Version, credits and licences", onClick = onOpenAbout)
            }
        }
    }

    if (showExport && identityExport != null) {
        val clipboard = LocalClipboardManager.current
        SettingsSheet(title = "Export identity", onDismiss = { showExport = false }) {
            Text(
                "Your TeamSpeak identity. Anyone holding this can connect as you — treat it like a private key.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
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
                Text(identityExport, style = MaterialTheme.typography.bodySmall, color = TextNormal)
            }
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton("Copy", modifier = Modifier.fillMaxWidth(), onClick = {
                clipboard.setText(AnnotatedString(identityExport))
                showExport = false
            })
        }
    }

    qualityPicker?.let { knob ->
        SettingsSheet(title = knob.title, onDismiss = { qualityPicker = null }) {
            val current = knob.read(settings)
            (listOf(StreamQualityPolicy.AUTO) + knob.options).forEachIndexed { i, value ->
                if (i > 0) RowDivider()
                ChoiceRow(knob.label(value), null, selected = value == current) {
                    val next = knob.write(settings, value)
                    onStreamQualityChange(next.streamResolution, next.streamFps, next.streamBitrateKbps)
                    qualityPicker = null
                }
            }
        }
    }

    if (showCreate) {
        SettingsSheet(title = "Create new identity", onDismiss = { showCreate = false }) {
            Text(
                "Generates a brand new TeamSpeak identity and discards the one on this device. " +
                    "Servers that recognise this device by its old identity (server groups, bans) will see a stranger. " +
                    "Export the current identity first if you want to keep it.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton("Cancel", onClick = { showCreate = false }, modifier = Modifier.weight(1f), color = TextMuted)
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
                "Pick the `.ini` the official client exported, or paste an identity string. " +
                    "This replaces the identity on this device.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.height(14.dp))
            SecondaryButton(
                text = "Choose identity file…",
                onClick = {
                    // `.ini` and `text/*` so Android's picker surfaces the export
                    // on Android too, not just a desktop-authored `.ini`.
                    filePicker.launch(arrayOf("text/plain", "application/octet-stream", "*/*"))
                },
                modifier = Modifier.fillMaxWidth(),
            )
            if (importPreview != null) {
                val described = onDescribeIdentityIni(importPreview!!)
                Spacer(modifier = Modifier.height(12.dp))
                if (described != null) {
                    Text(
                        "Found an identity in this file — $described",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PrimaryButton("Import from file", modifier = Modifier.fillMaxWidth(), onClick = {
                        if (onImportIdentityIni(importPreview!!) != null) {
                            importPreview = null
                            showImport = false
                        }
                    })
                } else {
                    Text(
                        "That file doesn't contain a TeamSpeak identity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Or paste an identity string",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.height(8.dp))
            BasicTextField(
                value = pasted,
                onValueChange = { pasted = it; failed = false },
                textStyle = MaterialTheme.typography.bodySmall.copy(color = TextNormal),
                cursorBrush = SolidColor(Brand),
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
            Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title.uppercase(), color = TextMuted, style = MaterialTheme.typography.labelSmall)
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
        color = TextFaint,
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
            Text(label, style = MaterialTheme.typography.bodyLarge, color = TextNormal)
            if (detail != null) Text(detail, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (selected) Brand else SurfaceHigh)
                .border(1.dp, if (selected) Brand else DividerLine, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Icon(Icons.Filled.Check, contentDescription = null, tint = OnBrand, modifier = Modifier.size(14.dp))
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
            Text(label, style = MaterialTheme.typography.bodyLarge, color = TextNormal)
            if (detail != null) Text(detail, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OnBrand,
                checkedTrackColor = Brand,
                uncheckedThumbColor = TextMuted,
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
            Text(label, style = MaterialTheme.typography.bodyLarge, color = if (enabled) TextNormal else TextFaint)
            if (detail != null) Text(detail, style = MaterialTheme.typography.bodySmall, color = if (enabled) TextMuted else TextFaint)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextFaint)
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
            Text(label, style = MaterialTheme.typography.bodyLarge, color = TextNormal)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = TextMuted)
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
            Text(title, style = MaterialTheme.typography.titleLarge, color = TextNormal)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

/** PHA-4176: the three screen-share video settings, each 0 (Automatic) or a value from StreamQualityPolicy. */
private enum class QualityKnob(val title: String, val options: List<Int>) {
    RESOLUTION("Video resolution", StreamQualityPolicy.RESOLUTIONS),
    FRAME_RATE("Frame rate", StreamQualityPolicy.FRAME_RATES),
    BITRATE("Video bitrate", StreamQualityPolicy.BITRATES_KBPS);

    fun read(s: Settings): Int = when (this) {
        RESOLUTION -> s.streamResolution
        FRAME_RATE -> s.streamFps
        BITRATE -> s.streamBitrateKbps
    }

    fun write(s: Settings, value: Int): Settings = when (this) {
        RESOLUTION -> s.copy(streamResolution = value)
        FRAME_RATE -> s.copy(streamFps = value)
        BITRATE -> s.copy(streamBitrateKbps = value)
    }

    fun label(value: Int): String = when {
        value == StreamQualityPolicy.AUTO -> "Automatic"
        this == RESOLUTION -> StreamQualityPolicy.resolutionLabel(value)
        this == FRAME_RATE -> "$value fps"
        else -> StreamQualityPolicy.bitrateLabel(value)
    }
}
