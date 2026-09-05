package com.plnt.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.HeadsetOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plnt.client.model.ChannelNode
import com.plnt.client.model.ClientRow
import com.plnt.client.model.ConnectionPhase
import com.plnt.client.model.PresenceKind
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.Oxblood
import com.plnt.client.ui.theme.Sage

/** PHA-3076 screen 2: channel tree + per-client rows + PTT + mute/deafen + disconnect. */
@Composable
fun ConnectedScreen(
    phase: ConnectionPhase,
    serverName: String,
    channelTree: List<ChannelNode>,
    inputMuted: Boolean,
    outputDeafened: Boolean,
    transmitting: Boolean,
    pttIsPushToTalk: Boolean,
    lastError: String?,
    onJoinChannel: (Long) -> Unit,
    onToggleMute: () -> Unit,
    onToggleDeafen: () -> Unit,
    onPttPress: () -> Unit,
    onPttRelease: () -> Unit,
    onDisconnect: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(serverName.ifBlank { "Connecting…" }, style = MaterialTheme.typography.titleMedium)
                        Text(phase.name.lowercase(), style = MaterialTheme.typography.labelSmall)
                    }
                },
                actions = {
                    IconButton(onClick = onDisconnect) {
                        Icon(Icons.Filled.CallEnd, contentDescription = "Disconnect", tint = Oxblood)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            lastError?.let {
                Text(
                    it,
                    color = Oxblood,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(flatten(channelTree)) { (node, depth) ->
                    ChannelRow(node, depth, onJoinChannel)
                    node.clients.forEach { c -> ClientRowView(c, depth + 1) }
                }
            }

            ControlBar(
                inputMuted = inputMuted,
                outputDeafened = outputDeafened,
                transmitting = transmitting,
                pttIsPushToTalk = pttIsPushToTalk,
                onToggleMute = onToggleMute,
                onToggleDeafen = onToggleDeafen,
                onPttPress = onPttPress,
                onPttRelease = onPttRelease,
            )
        }
    }
}

private fun flatten(nodes: List<ChannelNode>, depth: Int = 0): List<Pair<ChannelNode, Int>> =
    nodes.flatMap { n -> listOf(n to depth) + flatten(n.children, depth + 1) }

@Composable
private fun ChannelRow(node: ChannelNode, depth: Int, onJoin: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onJoin(node.id) }
            .padding(start = (16 * depth).dp, top = 10.dp, bottom = 10.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (node.hasPassword) {
            Icon(Icons.Filled.Lock, contentDescription = "Password protected", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.padding(2.dp))
        }
        Text(node.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ClientRowView(client: ClientRow, depth: Int) {
    val (color, tag) = when (client.presence) {
        PresenceKind.TALKING -> Sage to null
        PresenceKind.YOU_TALKING -> Gold to null
        PresenceKind.MUTED -> Oxblood to "MIC"
        PresenceKind.DEAFENED -> Oxblood to "SND"
        PresenceKind.AWAY -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) to null
        PresenceKind.IDLE -> MaterialTheme.colorScheme.onSurface to null
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (16 * depth).dp, top = 4.dp, bottom = 4.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            client.name + if (client.isSelf) " (you)" else "",
            color = color,
            fontStyle = if (client.presence == PresenceKind.AWAY) FontStyle.Italic else FontStyle.Normal,
            style = MaterialTheme.typography.bodyMedium,
        )
        tag?.let {
            Spacer(modifier = Modifier.padding(4.dp))
            Text(it, color = Oxblood, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ControlBar(
    inputMuted: Boolean,
    outputDeafened: Boolean,
    transmitting: Boolean,
    pttIsPushToTalk: Boolean,
    onToggleMute: () -> Unit,
    onToggleDeafen: () -> Unit,
    onPttPress: () -> Unit,
    onPttRelease: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconToggleButton(checked = inputMuted, onCheckedChange = { onToggleMute() }) {
            Icon(
                if (inputMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                contentDescription = "Mute",
                tint = if (inputMuted) Oxblood else MaterialTheme.colorScheme.onSurface,
            )
        }

        // Large, thumb-reachable PTT button (PHA-3076: one-handed use, readable in a car mount).
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(if (transmitting) Gold else MaterialTheme.colorScheme.surface)
                .pointerInput(pttIsPushToTalk) {
                    if (pttIsPushToTalk) {
                        detectTapGestures(
                            onPress = {
                                onPttPress()
                                tryAwaitRelease()
                                onPttRelease()
                            },
                        )
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Mic,
                contentDescription = "Push to talk",
                modifier = Modifier.size(40.dp),
                tint = if (transmitting) Color.Black else MaterialTheme.colorScheme.onSurface,
            )
        }

        IconToggleButton(checked = outputDeafened, onCheckedChange = { onToggleDeafen() }) {
            Icon(
                if (outputDeafened) Icons.Filled.HeadsetOff else Icons.Filled.Headset,
                contentDescription = "Deafen",
                tint = if (outputDeafened) Oxblood else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
