package com.plnt.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.HeadsetOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plnt.client.model.ChannelNode
import com.plnt.client.model.ClientRow
import com.plnt.client.model.ConnectionPhase
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneFaint
import com.plnt.client.ui.theme.BoneMuted
import com.plnt.client.ui.theme.DividerFaint
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.Mauve
import com.plnt.client.ui.theme.Oxblood
import com.plnt.client.ui.theme.RowTalking
import com.plnt.client.ui.theme.RowYouTalking
import com.plnt.client.ui.theme.Sage
import com.plnt.client.ui.theme.SurfaceDark
import com.plnt.client.ui.theme.SurfaceRaised

/** PHA-3076 screen 4: channel tree + per-client rows + the 160dp bottom control bar. */
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
) {
    // Collapse state is per-channel and sticky; channels with nothing in them
    // start collapsed (design §4), everything else starts open.
    val expandOverrides = remember { mutableStateMapOf<Long, Boolean>() }
    fun isExpanded(node: ChannelNode): Boolean =
        expandOverrides[node.id] ?: (node.clients.isNotEmpty() || node.children.isNotEmpty())

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark),
                title = {
                    Column {
                        Text(
                            serverName.ifBlank { "Connecting…" },
                            style = MaterialTheme.typography.titleLarge,
                            color = Bone,
                        )
                        Text(
                            phase.name.lowercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (phase == ConnectionPhase.RECONNECTING) Gold else BoneMuted,
                        )
                    }
                },
                actions = {
                    // Not in the mockup, but disconnect moved to the control bar
                    // pill and settings would otherwise be unreachable while
                    // connected. Implementer's call; flagged on PHA-3076.
                    IconButton(onClick = onOpenChat) {
                        Icon(Icons.Filled.Chat, contentDescription = "Chat", tint = Mauve)
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Mauve)
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(visibleRows(channelTree, ::isExpanded)) { row ->
                    when (row) {
                        is TreeRow.Channel -> {
                            ChannelRowView(
                                node = row.node,
                                depth = row.depth,
                                expanded = isExpanded(row.node),
                                onToggle = { expandOverrides[row.node.id] = !isExpanded(row.node) },
                                onJoin = { onJoinChannel(row.node.id) },
                            )
                            HorizontalDivider(thickness = 1.dp, color = if (row.isEmpty) DividerFaint else DividerLine)
                        }
                        is TreeRow.Client -> {
                            ClientRowView(row.client, row.depth)
                            HorizontalDivider(thickness = 1.dp, color = DividerLine)
                        }
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

@Composable
private fun ChannelRowView(
    node: ChannelNode,
    depth: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onJoin: () -> Unit,
) {
    val isEmpty = node.clients.isEmpty() && node.children.isEmpty()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable { onJoin() }
            .padding(start = (16 + 16 * depth).dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (expanded) "▾" else "▸",
            color = BoneMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable(enabled = !isEmpty) { onToggle() }
                .padding(end = 8.dp),
        )
        if (node.hasPassword) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = "Password protected",
                tint = BoneMuted,
                modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(node.name, style = MaterialTheme.typography.titleSmall, color = Bone)
        if (isEmpty) {
            Spacer(modifier = Modifier.width(8.dp))
            Text("(empty)", style = MaterialTheme.typography.labelSmall, color = BoneMuted)
        }
    }
}

@Composable
private fun ClientRowView(client: ClientRow, depth: Int) {
    val presence = client.presence
    val talkColor = when {
        presence.talking && client.isSelf -> Gold
        presence.talking -> Sage
        else -> null
    }
    val rowBackground = when {
        presence.talking && client.isSelf -> RowYouTalking
        presence.talking -> RowTalking
        else -> Bg
    }

    Box(modifier = Modifier.fillMaxWidth().height(52.dp).background(rowBackground)) {
        // 3dp left-edge bar, talk state only.
        talkColor?.let {
            Box(modifier = Modifier.width(3.dp).fillMaxHeight().background(it))
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = (16 + 16 * depth).dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(
                initial = client.name.firstOrNull()?.uppercase() ?: "?",
                ringColor = talkColor ?: BoneFaint,
                ringWidth = if (talkColor != null) 2.dp else 1.5.dp,
                dashed = presence.away,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    client.name + if (client.isSelf) " (you)" else "",
                    color = if (presence.away) BoneMuted else Bone,
                    fontStyle = if (presence.away) FontStyle.Italic else FontStyle.Normal,
                    fontWeight = if (presence.talking && client.isSelf) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium,
                )
                when {
                    presence.talking -> Text(
                        "talking",
                        color = talkColor ?: Sage,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    // Away has no colour of its own — italic + faint only, so it
                    // can never be confused with a talk state.
                    presence.away -> Text(
                        "away",
                        color = BoneFaint,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            // MIC and SND are independent — both show when both are true.
            if (presence.micMuted) StateTag("MIC")
            if (presence.outputMuted) {
                Spacer(modifier = Modifier.width(4.dp))
                StateTag("SND")
            }
        }
    }
}

@Composable
private fun Avatar(initial: String, ringColor: androidx.compose.ui.graphics.Color, ringWidth: androidx.compose.ui.unit.Dp, dashed: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .drawBehind {
                val stroke = ringWidth.toPx()
                drawCircle(
                    color = ringColor,
                    radius = size.minDimension / 2f - stroke / 2f,
                    style = Stroke(
                        width = stroke,
                        pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(6f, 6f)) else null,
                    ),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(initial, color = BoneMuted, style = MaterialTheme.typography.bodySmall)
    }
}

/** 30×14dp oxblood pill, bone 9sp — the MIC/SND tags from the spec table. */
@Composable
private fun StateTag(text: String) {
    Box(
        modifier = Modifier
            .size(width = 30.dp, height = 14.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Oxblood),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Bone, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * 160dp bottom control bar: 104dp PTT circle centred, 52dp mute/deafen circles
 * at ±110dp, disconnect pill below the PTT button (never beside it — the design
 * keeps hang-up out of the PTT thumb zone on purpose).
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
) {
    val haptics = LocalHapticFeedback.current
    Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(SurfaceDark)) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
                .size(104.dp)
                .clip(CircleShape)
                .background(if (transmitting) Gold else SurfaceRaised)
                .border(3.dp, Gold, CircleShape)
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
            Text(
                text = if (pttIsPushToTalk) "HOLD TO TALK" else "OPEN MIC",
                color = if (transmitting) Bg else Gold,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(72.dp),
            )
        }

        CircleToggle(
            modifier = Modifier.align(Alignment.TopCenter).offset(x = (-110).dp, y = 34.dp),
            checked = inputMuted,
            contentDescription = "Mute microphone",
            onClick = onToggleMute,
        ) { tint ->
            Icon(if (inputMuted) Icons.Filled.MicOff else Icons.Filled.Mic, contentDescription = null, tint = tint)
        }

        CircleToggle(
            modifier = Modifier.align(Alignment.TopCenter).offset(x = 110.dp, y = 34.dp),
            checked = outputDeafened,
            contentDescription = "Deafen",
            onClick = onToggleDeafen,
        ) { tint ->
            Icon(
                if (outputDeafened) Icons.Filled.HeadsetOff else Icons.Filled.Headset,
                contentDescription = null,
                tint = tint,
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
                .size(width = 80.dp, height = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, Oxblood, RoundedCornerShape(14.dp))
                .clickable { onDisconnect() },
            contentAlignment = Alignment.Center,
        ) {
            Text("DISCONNECT", color = Oxblood, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CircleToggle(
    modifier: Modifier,
    checked: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
    content: @Composable (androidx.compose.ui.graphics.Color) -> Unit,
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(if (checked) Oxblood else SurfaceDark)
            .border(1.5.dp, if (checked) Oxblood else BoneMuted, CircleShape)
            .clickable(onClickLabel = contentDescription) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content(if (checked) Bone else BoneMuted)
    }
}
