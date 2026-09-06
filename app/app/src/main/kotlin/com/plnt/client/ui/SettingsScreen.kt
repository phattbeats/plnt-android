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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plnt.client.core.CoreBridge
import com.plnt.client.model.PttMode
import com.plnt.client.model.Settings
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneFaint
import com.plnt.client.ui.theme.BoneMuted
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.SurfaceDark
import com.plnt.client.ui.theme.SurfaceRaised

/** PHA-3076 screen 5: PTT mode, PTT trigger sources, identity export/import, about. */
@Composable
fun SettingsScreen(
    settings: Settings,
    identityExport: String?,
    onPttModeChange: (PttMode) -> Unit,
    onPttOnVolumeButtonChange: (Boolean) -> Unit,
    onPttOnHeadsetButtonChange: (Boolean) -> Unit,
    onImportIdentity: (String) -> Boolean,
    onBack: () -> Unit,
) {
    var showExport by remember { mutableStateOf(false) }
    var showImport by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark),
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge, color = Bone) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Bone)
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            SectionHeader("Talk mode")
            RadioRow("Push to talk", settings.pttMode == PttMode.PUSH_TO_TALK) { onPttModeChange(PttMode.PUSH_TO_TALK) }
            RadioRow("Open mic", settings.pttMode == PttMode.OPEN_MIC) { onPttModeChange(PttMode.OPEN_MIC) }

            SectionGap()

            SectionHeader("Push-to-talk trigger")
            // Two independent switches: both can be armed at once (design §5).
            SwitchRow("Volume button", settings.pttOnVolumeButton, onPttOnVolumeButtonChange)
            SwitchRow("Headset button", settings.pttOnHeadsetButton, onPttOnHeadsetButtonChange)
            Text(
                "The on-screen PTT button is always available regardless of these. " +
                    "The headset button keeps working with the screen locked — the voice " +
                    "service holds the media session. The volume button only fires while " +
                    "PLNT is in the foreground; use the notification's Talk action from the " +
                    "lock screen.",
                style = MaterialTheme.typography.labelSmall,
                color = BoneFaint,
                modifier = Modifier.padding(top = 8.dp),
            )

            SectionGap()

            SectionHeader("Identity")
            ChevronRow("Export identity", enabled = identityExport != null) { showExport = true }
            ChevronRow("Import identity") { showImport = true }

            SectionGap()

            SectionHeader("About")
            Text(
                "PLNT — voice-only TeamSpeak client. Connect, channel tree, who's talking, " +
                    "push-to-talk / open mic, mute/deafen. No chat, no streams, no file transfer.",
                style = MaterialTheme.typography.bodySmall,
                color = BoneMuted,
            )
            // PHA-3074's acceptance criterion is that core_version() renders on screen: it is
            // the one place the UI shows a string that only the Rust core can produce, so a
            // broken JNI/UniFFI link is visible without a debugger. CoreBridge.coreVersion()
            // catches its own failures and returns "unavailable: ...", so this never crashes
            // Settings. remember{} keeps it to one FFI call per composition, not per frame.
            Text(
                remember { CoreBridge.coreVersion() },
                style = MaterialTheme.typography.labelSmall,
                color = BoneFaint,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }

    if (showExport && identityExport != null) {
        val clipboard = LocalClipboardManager.current
        SettingsSheet(title = "Export identity", onDismiss = { showExport = false }) {
            Text(
                "Your TeamSpeak identity. Anyone holding this can connect as you — " +
                    "treat it like a private key.",
                style = MaterialTheme.typography.bodySmall,
                color = BoneFaint,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, DividerLine, RoundedCornerShape(8.dp))
                    .background(SurfaceRaised, RoundedCornerShape(8.dp))
                    .padding(12.dp),
            ) {
                Text(identityExport, style = MaterialTheme.typography.bodySmall, color = Bone)
            }
            Spacer(modifier = Modifier.height(16.dp))
            SheetAction("COPY") {
                clipboard.setText(AnnotatedString(identityExport))
                showExport = false
            }
        }
    }

    if (showImport) {
        var pasted by remember { mutableStateOf("") }
        var failed by remember { mutableStateOf(false) }
        SettingsSheet(title = "Import identity", onDismiss = { showImport = false }) {
            Text(
                "Paste an exported identity. This replaces the one on this device.",
                style = MaterialTheme.typography.bodySmall,
                color = BoneFaint,
            )
            Spacer(modifier = Modifier.height(12.dp))
            BasicTextField(
                value = pasted,
                onValueChange = { pasted = it; failed = false },
                textStyle = MaterialTheme.typography.bodySmall.copy(color = Bone),
                cursorBrush = SolidColor(Gold),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .border(1.5.dp, DividerLine, RoundedCornerShape(8.dp))
                    .background(SurfaceRaised, RoundedCornerShape(8.dp))
                    .padding(12.dp),
            )
            if (failed) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "That isn't a valid identity.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            SheetAction("IMPORT", enabled = pasted.isNotBlank()) {
                if (onImportIdentity(pasted)) showImport = false else failed = true
            }
        }
    }
}

@Composable
private fun SectionGap() {
    Spacer(modifier = Modifier.height(20.dp))
    HorizontalDivider(thickness = 1.dp, color = DividerLine)
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp).clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Gold, unselectedColor = BoneMuted),
        )
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Bone)
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp).clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Bone)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Gold,
                checkedTrackColor = Gold.copy(alpha = 0.35f),
                uncheckedThumbColor = BoneMuted,
                uncheckedTrackColor = SurfaceRaised,
            ),
        )
    }
}

/** Plain text row with the trailing "›" the design asks Compose to add. */
@Composable
private fun ChevronRow(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp).clickable(enabled = enabled) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) Bone else BoneFaint,
        )
        Text("›", style = MaterialTheme.typography.bodyLarge, color = BoneMuted)
    }
}

@Composable
private fun SettingsSheet(title: String, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = { SheetDragHandle() },
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Bone)
            Spacer(modifier = Modifier.height(16.dp))
            content()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SheetAction(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = enabled) { onClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(
                label,
                color = if (enabled) Gold else Gold.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
