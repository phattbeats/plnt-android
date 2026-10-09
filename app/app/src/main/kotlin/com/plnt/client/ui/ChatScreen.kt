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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plnt.client.core.ChatMessageTarget
import com.plnt.client.model.ChatMessage
import com.plnt.client.model.ClientRow
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneFaint
import com.plnt.client.ui.theme.BoneMuted
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.Mauve
import com.plnt.client.ui.theme.SurfaceDark
import com.plnt.client.ui.theme.SurfaceHigh
import com.plnt.client.ui.theme.SurfaceRaised

/**
 * Channel + private text chat. Bubbles: yours on the right in a gold tint,
 * everyone else on the left on a raised surface with the sender's avatar.
 * [peers] drives the "To" picker for private messages; pass the roster with
 * the local client already filtered out.
 */
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    peers: List<ClientRow>,
    onSend: (ChatMessageTarget, String) -> Unit,
    onBack: () -> Unit,
) {
    var draft by remember { mutableStateOf("") }
    var target by remember { mutableStateOf<ChatMessageTarget>(ChatMessageTarget.Channel) }
    var targetMenuOpen by remember { mutableStateOf(false) }

    val targetLabel = when (val t = target) {
        ChatMessageTarget.Channel -> "Channel"
        is ChatMessageTarget.Direct -> peers.find { it.clientId == t.clientId }?.name ?: "Client ${t.clientId}"
    }

    fun send() {
        if (draft.isNotBlank()) {
            onSend(target, draft.trim())
            draft = ""
        }
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg),
                title = {
                    Column {
                        Text("Chat", style = MaterialTheme.typography.titleLarge, color = Bone)
                        Text(
                            if (target is ChatMessageTarget.Channel) "Channel" else "Private · $targetLabel",
                            style = MaterialTheme.typography.labelMedium,
                            color = BoneMuted,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Bone)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.size(72.dp).clip(CircleShape).background(SurfaceRaised),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = Mauve, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Nothing here yet", style = MaterialTheme.typography.titleMedium, color = Bone)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Messages in this channel, and private ones, show up here.",
                        color = BoneMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    reverseLayout = true,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(messages.asReversed()) { msg -> ChatBubble(msg) }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .border(1.dp, DividerLine, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(SurfaceHigh)
                            .clickable { targetMenuOpen = true }
                            .padding(start = 12.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("To: $targetLabel", color = Mauve, style = MaterialTheme.typography.labelMedium)
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Choose recipient", tint = Mauve, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = targetMenuOpen, onDismissRequest = { targetMenuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("Channel", color = Bone) },
                            onClick = { target = ChatMessageTarget.Channel; targetMenuOpen = false },
                        )
                        peers.forEach { peer ->
                            DropdownMenuItem(
                                text = { Text(peer.name, color = Bone) },
                                onClick = { target = ChatMessageTarget.Direct(peer.clientId); targetMenuOpen = false },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(SurfaceRaised)
                            .border(1.dp, DividerLine, RoundedCornerShape(22.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (draft.isEmpty()) {
                            Text("Message…", color = BoneFaint, style = MaterialTheme.typography.bodyMedium)
                        }
                        BasicTextField(
                            value = draft,
                            onValueChange = { draft = it },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.merge(MaterialTheme.typography.bodyMedium).copy(color = Bone),
                            cursorBrush = SolidColor(Gold),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val canSend = draft.isNotBlank()
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (canSend) Gold else SurfaceHigh)
                            .clickable(enabled = canSend) { send() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (canSend) Bg else BoneFaint,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage) {
    val mine = msg.isSelf
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!mine) {
            Avatar(msg.fromName, size = 28.dp)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (mine) 18.dp else 4.dp,
                        bottomEnd = if (mine) 4.dp else 18.dp,
                    ),
                )
                .background(if (mine) Gold.copy(alpha = 0.18f) else SurfaceRaised)
                .padding(horizontal = 14.dp, vertical = 9.dp),
        ) {
            if (!mine || msg.isDirect) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!mine) {
                        Text(msg.fromName, color = Mauve, style = MaterialTheme.typography.labelMedium)
                    }
                    if (msg.isDirect) {
                        if (!mine) Spacer(modifier = Modifier.width(6.dp))
                        Text("private", color = BoneFaint, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
            Text(msg.text, color = Bone, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
