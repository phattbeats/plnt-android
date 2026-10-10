package com.plnt.client.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.HeadsetOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plnt.client.model.ChannelNode
import com.plnt.client.model.ClientRow
import com.plnt.client.model.ConnectionPhase
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.OnBrand
import com.plnt.client.ui.theme.Warning
import com.plnt.client.ui.theme.TextNormal
import com.plnt.client.ui.theme.TextFaint
import com.plnt.client.ui.theme.TextMuted
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.Brand
import com.plnt.client.ui.theme.Accent
import com.plnt.client.ui.theme.Danger
import com.plnt.client.ui.theme.RowTalking
import com.plnt.client.ui.theme.RowYouTalking
import com.plnt.client.ui.theme.Speaking
import com.plnt.client.ui.theme.SurfaceDark
import com.plnt.client.ui.theme.SurfaceHigh
import com.plnt.client.ui.theme.SurfaceRaised

/** Channel tree with per-client rows and the bottom talk controls. */
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
    onOpenSettings: () -> Unit,
    onOpenChat: () -> Unit,
    /** PHA-3289: tap a streaming client's row to watch. */
    onWatchStream: (com.plnt.client.stream.StreamInfo) -> Unit = {},
    /** PHA-3289 send side: our own share and its start/stop. */
    shareState: com.plnt.client.stream.StreamSendState = com.plnt.client.stream.StreamSendState(),
    onShareScreen: () -> Unit = {},
    onStopShare: () -> Unit = {},
) {
    // Collapse state is per-channel and sticky; channels with nothing in them
    // start collapsed, everything else starts open.
    val expandOverrides = remember { mutableStateMapOf<Long, Boolean>() }
    fun isExpanded(node: ChannelNode): Boolean =
        expandOverrides[node.id] ?: (node.clients.isNotEmpty() || node.children.isNotEmpty())

    val statusColor = when (phase) {
        ConnectionPhase.CONNECTED -> Speaking
        ConnectionPhase.RECONNECTING, ConnectionPhase.CONNECTING -> Warning
        ConnectionPhase.ERROR -> Danger
        ConnectionPhase.DISCONNECTED -> TextFaint
    }
    val statusText = when (phase) {
        ConnectionPhase.CONNECTED -> "Connected"
        ConnectionPhase.CONNECTING -> "Connecting…"
        ConnectionPhase.RECONNECTING -> "Reconnecting…"
        ConnectionPhase.ERROR -> "Error"
        ConnectionPhase.DISCONNECTED -> "Disconnected"
    }

    val selfChannel = remember(channelTree) { findSelfChannel(channelTree) }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TeamSpeakMark(size = 36.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                serverName.ifBlank { "Connecting…" },
                                style = MaterialTheme.typography.titleLarge,
                                color = TextNormal,
                                maxLines = 1,
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusDot(statusColor)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(statusText, style = MaterialTheme.typography.labelMedium, color = statusColor)
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = TextMuted)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            lastError?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Danger.copy(alpha = 0.18f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(it, color = Color(0xFFE8A3A3), style = MaterialTheme.typography.bodySmall)
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            ) {
                items(visibleRows(channelTree, ::isExpanded)) { row ->
                    when (row) {
                        is TreeRow.Channel -> ChannelRowView(
                            node = row.node,
                            depth = row.depth,
                            expanded = isExpanded(row.node),
                            current = row.node.id == selfChannel?.id,
                            onToggle = { expandOverrides[row.node.id] = !isExpanded(row.node) },
                            onJoin = { onJoinChannel(row.node.id) },
                        )
                        is TreeRow.Client -> ClientRowView(row.client, row.depth, onWatchStream)
                    }
                }
            }

            ControlBar(
                phase = phase,
                channelName = selfChannel?.name,
                serverName = serverName,
                onOpenChat = onOpenChat,
                inputMuted = inputMuted,
                outputDeafened = outputDeafened,
                transmitting = transmitting,
                pttIsPushToTalk = pttIsPushToTalk,
                onToggleMute = onToggleMute,
                onToggleDeafen = onToggleDeafen,
                onPttPress = onPttPress,
                onPttRelease = onPttRelease,
                onDisconnect = onDisconnect,
                shareState = shareState,
                onShareScreen = onShareScreen,
                onStopShare = onStopShare,
            )
        }
    }
}

private sealed class TreeRow {
    abstract val depth: Int

    data class Channel(val node: ChannelNode, override val depth: Int, val isEmpty: Boolean) : TreeRow()
    data class Client(val client: ClientRow, override val depth: Int) : TreeRow()
}

/** Flattens the tree into render order, honouring collapse state. */
private fun visibleRows(
    nodes: List<ChannelNode>,
    expanded: (ChannelNode) -> Boolean,
    depth: Int = 0,
): List<TreeRow> = nodes.flatMap { node ->
    val isEmpty = node.clients.isEmpty() && node.children.isEmpty()
    buildList {
        add(TreeRow.Channel(node, depth, isEmpty))
        if (expanded(node)) {
            node.clients.forEach { add(TreeRow.Client(it, depth + 1)) }
            addAll(visibleRows(node.children, expanded, depth + 1))
        }
    }
}

private fun countClients(node: ChannelNode): Int = node.clients.size + node.children.sumOf(::countClients)

/** The channel we're sitting in — Discord highlights it and names it in the voice panel. */
private fun findSelfChannel(nodes: List<ChannelNode>): ChannelNode? {
    for (node in nodes) {
        if (node.clients.any { it.isSelf }) return node
        findSelfChannel(node.children)?.let { return it }
    }
    return null
}

/**
 * Discord-style voice channel row: speaker glyph, name, and a lifted
 * background on the channel you're in. Tap joins; the chevron folds.
 */
@Composable
private fun ChannelRowView(
    node: ChannelNode,
    depth: Int,
    expanded: Boolean,
    current: Boolean,
    onToggle: () -> Unit,
    onJoin: () -> Unit,
) {
    val isEmpty = node.clients.isEmpty() && node.children.isEmpty()
    val rotation by animateFloatAsState(if (expanded) 0f else -90f, label = "chevron")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (depth == 0) 6.dp else 0.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (current) SurfaceHigh else Color.Transparent)
            .clickable { onJoin() }
            .padding(start = (2 + 16 * depth).dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(enabled = !isEmpty) { onToggle() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = if (isEmpty) Color.Transparent else TextFaint,
                modifier = Modifier.size(18.dp).rotate(rotation),
            )
        }
        Icon(
            Icons.Filled.VolumeUp,
            contentDescription = null,
            tint = if (current) TextNormal else TextFaint,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            node.name,
            style = MaterialTheme.typography.titleSmall,
            color = when {
                current -> TextNormal
                isEmpty -> TextFaint
                else -> TextMuted
            },
            fontWeight = if (current) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        if (node.hasPassword) {
            Icon(Icons.Filled.Lock, contentDescription = "Password protected", tint = TextFaint, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        val count = countClients(node)
        if (count > 0 && !expanded) {
            Text(count.toString(), color = TextFaint, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/**
 * Discord-style voice member: small avatar that gets a solid green ring while
 * speaking, name, then muted/deafened glyphs and a red LIVE badge for a
 * screen share (tap the row to watch).
 */
@Composable
private fun ClientRowView(
    client: ClientRow,
    depth: Int,
    onWatchStream: (com.plnt.client.stream.StreamInfo) -> Unit,
) {
    val presence = client.presence
    val stream = client.streaming
    val talking = presence.talking
    val ring by animateColorAsState(if (talking) Speaking else Color.Transparent, label = "ring")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .then(if (stream != null) Modifier.clickable { onWatchStream(stream) } else Modifier)
            .padding(start = (30 + 16 * depth).dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(30.dp).border(2.dp, ring, CircleShape).padding(3.dp),
            contentAlignment = Alignment.Center,
        ) {
            Avatar(client.name, size = 24.dp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            client.name,
            color = when {
                talking -> TextNormal
                presence.away -> TextFaint
                else -> TextMuted
            },
            fontStyle = if (presence.away) FontStyle.Italic else FontStyle.Normal,
            fontWeight = if (talking || client.isSelf) FontWeight.SemiBold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            modifier = Modifier.weight(1f, fill = false),
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            if (stream != null) LiveBadge()
            if (presence.micMuted) Icon(Icons.Filled.MicOff, contentDescription = "Microphone muted", tint = TextFaint, modifier = Modifier.size(16.dp))
            if (presence.outputMuted) Icon(Icons.Filled.HeadsetOff, contentDescription = "Sound muted", tint = TextFaint, modifier = Modifier.size(16.dp))
        }
    }
}

/** Discord's red LIVE badge. */
@Composable
private fun LiveBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Danger)
            .padding(horizontal = 5.dp, vertical = 1.dp),
    ) {
        Text("LIVE", color = OnBrand, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

/**
 * Discord-style voice panel: "Voice Connected" + where, a wide push-to-talk
 * bar, then one row of round controls. Hang-up is the far-right red button,
 * away from the PTT bar so a thumb can't hit it by accident.
 */
@Composable
private fun ControlBar(
    phase: ConnectionPhase,
    channelName: String?,
    serverName: String,
    onOpenChat: () -> Unit,
    inputMuted: Boolean,
    outputDeafened: Boolean,
    transmitting: Boolean,
    pttIsPushToTalk: Boolean,
    onToggleMute: () -> Unit,
    onToggleDeafen: () -> Unit,
    onPttPress: () -> Unit,
    onPttRelease: () -> Unit,
    onDisconnect: () -> Unit,
    shareState: com.plnt.client.stream.StreamSendState,
    onShareScreen: () -> Unit,
    onStopShare: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val sharing = shareState.phase == com.plnt.client.stream.StreamSendPhase.STARTING ||
        shareState.phase == com.plnt.client.stream.StreamSendPhase.LIVE
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(SurfaceDark)
            .padding(horizontal = 16.dp)
            .padding(top = 14.dp, bottom = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val connected = phase == ConnectionPhase.CONNECTED
            Icon(
                Icons.Filled.Circle,
                contentDescription = null,
                tint = if (connected) Speaking else Warning,
                modifier = Modifier.size(10.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (connected) "Voice Connected" else "Connecting…",
                    color = if (connected) Speaking else Warning,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    listOfNotNull(channelName, serverName.ifBlank { null }).joinToString(" / "),
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                )
            }
            if (sharing) {
                Text(
                    when {
                        shareState.phase == com.plnt.client.stream.StreamSendPhase.STARTING -> "Starting share…"
                        shareState.viewers > 0 -> "Live · ${shareState.viewers} watching"
                        else -> "Live"
                    },
                    color = Danger,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        if (shareState.phase == com.plnt.client.stream.StreamSendPhase.FAILED && shareState.detail != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(shareState.detail, color = Danger, style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(14.dp))
        val pttFill by animateColorAsState(
            when {
                transmitting -> Speaking
                pttIsPushToTalk -> Brand
                else -> SurfaceHigh
            },
            label = "ptt",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(pttFill)
                .pointerInput(pttIsPushToTalk) {
                    if (pttIsPushToTalk) {
                        detectTapGestures(
                            onPress = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onPttPress()
                                tryAwaitRelease()
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onPttRelease()
                            },
                        )
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Filled.Mic,
                contentDescription = if (pttIsPushToTalk) "Hold to talk" else "Open mic",
                tint = if (pttIsPushToTalk || transmitting) OnBrand else TextMuted,
                modifier = Modifier.size(26.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                when {
                    !pttIsPushToTalk && transmitting -> "Open mic · live"
                    !pttIsPushToTalk -> "Open mic"
                    transmitting -> "Talking"
                    else -> "Hold to talk"
                },
                color = if (pttIsPushToTalk || transmitting) OnBrand else TextMuted,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PanelButton(
                icon = if (inputMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                description = if (inputMuted) "Unmute microphone" else "Mute microphone",
                active = inputMuted,
                onClick = onToggleMute,
            )
            PanelButton(
                icon = if (outputDeafened) Icons.Filled.HeadsetOff else Icons.Filled.Headset,
                description = if (outputDeafened) "Undeafen" else "Deafen",
                active = outputDeafened,
                onClick = onToggleDeafen,
            )
            PanelButton(
                icon = if (sharing) Icons.Filled.StopScreenShare else Icons.Filled.ScreenShare,
                description = if (sharing) "Stop sharing" else "Share screen",
                active = false,
                fill = if (sharing) Brand else SurfaceHigh,
                onClick = if (sharing) onStopShare else onShareScreen,
            )
            PanelButton(
                icon = Icons.Outlined.ChatBubbleOutline,
                description = "Chat",
                active = false,
                onClick = onOpenChat,
            )
            PanelButton(
                icon = Icons.Filled.CallEnd,
                description = "Disconnect",
                active = false,
                fill = Danger,
                onClick = onDisconnect,
            )
        }
    }
}

/**
 * Round panel control. Like Discord, a toggled-on mute/deafen flips to a
 * light button with a red glyph so "you're muted" reads from across a room.
 */
@Composable
private fun PanelButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    active: Boolean,
    onClick: () -> Unit,
    fill: Color = SurfaceHigh,
) {
    val bg by animateColorAsState(if (active) TextNormal else fill, label = "panelButton")
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = description, tint = if (active) Danger else TextNormal, modifier = Modifier.size(24.dp))
    }
}
