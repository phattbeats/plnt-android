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
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneFaint
import com.plnt.client.ui.theme.BoneMuted
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.Mauve
import com.plnt.client.ui.theme.Oxblood
import com.plnt.client.ui.theme.RowTalking
import com.plnt.client.ui.theme.RowYouTalking
import com.plnt.client.ui.theme.Sage
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
        ConnectionPhase.CONNECTED -> Sage
        ConnectionPhase.RECONNECTING, ConnectionPhase.CONNECTING -> Gold
        ConnectionPhase.ERROR -> Oxblood
        ConnectionPhase.DISCONNECTED -> BoneFaint
    }
    val statusText = when (phase) {
        ConnectionPhase.CONNECTED -> "Connected"
        ConnectionPhase.CONNECTING -> "Connecting…"
        ConnectionPhase.RECONNECTING -> "Reconnecting…"
        ConnectionPhase.ERROR -> "Error"
        ConnectionPhase.DISCONNECTED -> "Disconnected"
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg),
                title = {
                    Column {
                        Text(
                            serverName.ifBlank { "Connecting…" },
                            style = MaterialTheme.typography.titleLarge,
                            color = Bone,
                            maxLines = 1,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusDot(statusColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(statusText, style = MaterialTheme.typography.labelMedium, color = statusColor)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenChat) {
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Chat", tint = BoneMuted)
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = BoneMuted)
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
                        .background(Oxblood.copy(alpha = 0.18f))
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
                            onToggle = { expandOverrides[row.node.id] = !isExpanded(row.node) },
                            onJoin = { onJoinChannel(row.node.id) },
                        )
                        is TreeRow.Client -> ClientRowView(row.client, row.depth, onWatchStream)
                    }
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

@Composable
private fun ChannelRowView(
    node: ChannelNode,
    depth: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onJoin: () -> Unit,
) {
    val isEmpty = node.clients.isEmpty() && node.children.isEmpty()
    val rotation by animateFloatAsState(if (expanded) 0f else -90f, label = "chevron")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (depth == 0) 8.dp else 0.dp)
            .height(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onJoin() }
            .padding(start = (4 + 18 * depth).dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(enabled = !isEmpty) { onToggle() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = if (isEmpty) BoneFaint else BoneMuted,
                modifier = Modifier.size(20.dp).rotate(rotation),
            )
        }
        Icon(
            Icons.Filled.Tag,
            contentDescription = null,
            tint = if (isEmpty) BoneFaint else Mauve,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            node.name,
            style = MaterialTheme.typography.titleSmall,
            color = if (isEmpty) BoneMuted else Bone,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        if (node.hasPassword) {
            Icon(Icons.Filled.Lock, contentDescription = "Password protected", tint = BoneFaint, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        val count = countClients(node)
        if (count > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(SurfaceHigh)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(count.toString(), color = BoneMuted, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun ClientRowView(
    client: ClientRow,
    depth: Int,
    onWatchStream: (com.plnt.client.stream.StreamInfo) -> Unit,
) {
    val presence = client.presence
    val stream = client.streaming
    val talking = presence.talking
    val ringColor = when {
        talking && client.isSelf -> Gold
        talking -> Sage
        else -> Color.Transparent
    }
    val rowBackground by animateColorAsState(
        when {
            talking && client.isSelf -> RowYouTalking
            talking -> RowTalking
            else -> Color.Transparent
        },
        label = "rowBg",
    )
    // A soft pulse on the ring while someone speaks — the old flat 3dp bar
    // read as a rendering glitch rather than a state.
    val pulse = rememberInfiniteTransition(label = "pulse")
    val ringAlpha by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "ringAlpha",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(rowBackground)
            .then(if (stream != null) Modifier.clickable { onWatchStream(stream) } else Modifier)
            .padding(start = (8 + 18 * depth).dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (talking) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .border(2.dp, ringColor.copy(alpha = ringAlpha), CircleShape),
                )
            }
            Avatar(client.name, size = 36.dp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    client.name,
                    color = if (presence.away) BoneMuted else Bone,
                    fontStyle = if (presence.away) FontStyle.Italic else FontStyle.Normal,
                    fontWeight = if (talking) FontWeight.SemiBold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                )
                if (client.isSelf) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("you", color = BoneFaint, style = MaterialTheme.typography.labelSmall)
                }
            }
            val subtitle = when {
                stream != null -> "Sharing ${stream.name} · tap to watch"
                talking -> "Speaking"
                presence.away -> "Away"
                else -> null
            }
            if (subtitle != null) {
                Text(
                    subtitle,
                    color = when {
                        stream != null -> Gold
                        talking -> ringColor
                        else -> BoneFaint
                    },
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (stream != null) Pill("LIVE", Gold, Icons.Filled.Videocam)
            if (presence.micMuted) Pill("MIC", Oxblood, Icons.Filled.MicOff)
            if (presence.outputMuted) Pill("SND", Oxblood, Icons.Filled.VolumeOff)
        }
    }
}

/**
 * Bottom talk controls: mute / push-to-talk / deafen on one row, disconnect
 * beneath. Hang-up stays out of the PTT thumb zone on purpose.
 */
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
    onDisconnect: () -> Unit,
    shareState: com.plnt.client.stream.StreamSendState,
    onShareScreen: () -> Unit,
    onStopShare: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val pttFill by animateColorAsState(if (transmitting) Gold else SurfaceRaised, label = "ptt")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(SurfaceDark)
            .border(1.dp, DividerLine, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(horizontal = 24.dp)
            .padding(top = 18.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            RoundToggle(
                checked = inputMuted,
                label = if (inputMuted) "Muted" else "Mute",
                icon = { tint -> Icon(if (inputMuted) Icons.Filled.MicOff else Icons.Filled.Mic, contentDescription = "Mute microphone", tint = tint) },
                onClick = onToggleMute,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(pttFill)
                        .border(2.dp, Gold, CircleShape)
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
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Mic,
                        contentDescription = if (pttIsPushToTalk) "Hold to talk" else "Open mic",
                        tint = if (transmitting) Bg else Gold,
                        modifier = Modifier.size(36.dp),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    when {
                        !pttIsPushToTalk && transmitting -> "Open mic · live"
                        !pttIsPushToTalk -> "Open mic"
                        transmitting -> "Talking"
                        else -> "Hold to talk"
                    },
                    color = if (transmitting) Gold else BoneMuted,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            RoundToggle(
                checked = outputDeafened,
                label = if (outputDeafened) "Deafened" else "Deafen",
                icon = { tint -> Icon(if (outputDeafened) Icons.Filled.HeadsetOff else Icons.Filled.Headset, contentDescription = "Deafen", tint = tint) },
                onClick = onToggleDeafen,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        val sharing = shareState.phase == com.plnt.client.stream.StreamSendPhase.STARTING ||
            shareState.phase == com.plnt.client.stream.StreamSendPhase.LIVE
        SecondaryButton(
            when {
                shareState.phase == com.plnt.client.stream.StreamSendPhase.STARTING -> "Starting share…"
                sharing && shareState.viewers > 0 -> "Stop sharing · ${shareState.viewers} watching"
                sharing -> "Stop sharing · live"
                else -> "Share screen"
            },
            onClick = if (sharing) onStopShare else onShareScreen,
            color = Gold,
            icon = if (sharing) Icons.Filled.StopScreenShare else Icons.Filled.ScreenShare,
            modifier = Modifier.fillMaxWidth(),
        )
        if (shareState.phase == com.plnt.client.stream.StreamSendPhase.FAILED && shareState.detail != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(shareState.detail, color = Oxblood, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.height(10.dp))
        SecondaryButton(
            "Disconnect",
            onClick = onDisconnect,
            color = Oxblood,
            icon = Icons.Filled.CallEnd,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RoundToggle(
    checked: Boolean,
    label: String,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit,
) {
    val fill by animateColorAsState(if (checked) Oxblood else SurfaceHigh, label = "toggle")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(fill)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            icon(if (checked) Bone else BoneMuted)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = if (checked) Bone else BoneFaint, style = MaterialTheme.typography.labelMedium)
    }
}
