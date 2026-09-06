package com.plnt.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plnt.client.core.ChatMessageTarget
import com.plnt.client.model.ChatMessage
import com.plnt.client.model.ClientRow
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneFaint
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.Mauve
import com.plnt.client.ui.theme.SurfaceDark
import com.plnt.client.ui.theme.SurfaceRaised

/**
 * Channel + private text chat (PHA-3281). No design spec covers chat yet —
 * parent PHA-3076 predates it — so this stays close to [ConnectedScreen]'s
 * visual language (same palette, same top-bar shape) rather than inventing a
 * new one. [peers] drives the "To:" picker for private messages; pass the
 * roster with the local client already filtered out.
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

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark),
                title = { Text("Chat", style = MaterialTheme.typography.titleLarge, color = Bone) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Mauve)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                reverseLayout = true,
            ) {
                items(messages.asReversed()) { msg -> ChatMessageRow(msg) }
            }

            HorizontalDivider(thickness = 1.dp, color = DividerLine)

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box {
                    Row(
                        modifier = Modifier
                            .height(36.dp)
                            .border(1.dp, DividerLine, RoundedCornerShape(18.dp))
                            .clickable { targetMenuOpen = true }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("To: $targetLabel", color = Mauve, style = MaterialTheme.typography.labelSmall)
                    }
                    DropdownMenu(expanded = targetMenuOpen, onDismissRequest = { targetMenuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("Channel") },
                            onClick = { target = ChatMessageTarget.Channel; targetMenuOpen = false },
                        )
                        peers.forEach { peer ->
                            DropdownMenuItem(
                                text = { Text(peer.name) },
                                onClick = { target = ChatMessageTarget.Direct(peer.clientId); targetMenuOpen = false },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .border(1.dp, DividerLine, RoundedCornerShape(18.dp))
                        .background(SurfaceRaised, RoundedCornerShape(18.dp))
                        .padding(horizontal = 12.dp),
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
                IconButton(
                    onClick = {
                        if (draft.isNotBlank()) {
                            onSend(target, draft)
                            draft = ""
                        }
                    },
                ) {
                    Icon(Icons.Filled.Send, contentDescription = "Send", tint = Gold)
                }
            }
        }
    }
}

@Composable
private fun ChatMessageRow(msg: ChatMessage) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                msg.fromName + if (msg.isSelf) " (you)" else "",
                color = if (msg.isSelf) Gold else Bone,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodySmall,
            )
            if (msg.isDirect) {
                Spacer(modifier = Modifier.width(6.dp))
                Text("DM", color = Mauve, style = MaterialTheme.typography.labelSmall)
            }
        }
        Text(msg.text, color = Bone, style = MaterialTheme.typography.bodyMedium)
    }
}
